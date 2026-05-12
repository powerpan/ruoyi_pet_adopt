#!/usr/bin/env bash
# 在项目根目录执行 Maven 单元测试。
# 用法：
#   ./scripts/run-tests.sh
#   ./scripts/run-tests.sh -pl pet-system
#   ./scripts/run-tests.sh -pl pet-system
#
# 环境变量：
#   VERBOSE=1       关闭 Maven -q，输出完整 [INFO] 与各插件日志
#   SHOW_JVM_WARN=1 不过滤测试子进程里的 OpenJDK/ByteBuddy 常见警告
#   JAVA_HOME、MAVEN_OPTS（脚本会为 mockito-inline 追加 -XX:+EnableDynamicAgentLoading）

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if [[ ! "${MAVEN_OPTS:-}" =~ EnableDynamicAgentLoading ]]; then
  export MAVEN_OPTS="${MAVEN_OPTS:+$MAVEN_OPTS }-XX:+EnableDynamicAgentLoading"
fi

MVN_CLI=( -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn )
if [[ "${VERBOSE:-0}" != "1" ]]; then
  MVN_CLI+=( -q )
fi

# 过滤 Surefire 子进程打到 stderr 的常见噪声（非错误）；需要可看：SHOW_JVM_WARN=1
filter_jvm_stderr() {
  while IFS= read -r line || [[ -n "${line}" ]]; do
    case "$line" in
      OpenJDK\ 64-Bit\ Server\ VM\ warning:*) ;;
      OpenJDK\ 64-Bit\ Server\ VM\ warning*) ;;
      WARNING:\ A\ Java\ agent\ has\ been\ loaded\ dynamically*) ;;
      WARNING:\ If\ a\ serviceability\ tool\ is\ in\ use*) ;;
      WARNING:\ If\ a\ serviceability\ tool\ is\ not\ in\ use*) ;;
      WARNING:\ Dynamic\ loading\ of\ agents\ will\ be\ disallowed*) ;;
      WARNING:\ A\ terminally\ deprecated\ method\ in\ sun.misc.Unsafe*) ;;
      WARNING:\ sun.misc.Unsafe*) ;;
      WARNING:\ Please\ consider\ reporting\ this\ to\ the\ maintainers*) ;;
      *) printf '%s\n' "$line" >&2 ;;
    esac
  done
}

# 从各模块 target/surefire-reports/TEST-*.xml 汇总用例数（Maven -q 时不显示类名列表，用此补全「有价值」信息）
# 参数：$1=项目根；其余为本次传给 mvn 的参数（用于文末提示）
summarize_surefire_reports() {
  local root=$1
  shift
  local user_args="$*"
  local tmp agg
  tmp=$(mktemp "${TMPDIR:-/tmp}/run-tests-sum.XXXXXX") || return 0
  agg="${tmp}.agg"

  local f d mod t fe e sk
  while IFS= read -r -d '' f; do
    [[ -f "$f" ]] || continue
    d=$(dirname "$f")
    d=$(dirname "$d")
    d=$(dirname "$d")
    mod=$(basename "$d")
    t=$(grep -m1 -oE 'tests="[0-9]+"' "$f" | grep -oE '[0-9]+' || echo 0)
    fe=$(grep -m1 -oE 'failures="[0-9]+"' "$f" | grep -oE '[0-9]+' || echo 0)
    e=$(grep -m1 -oE 'errors="[0-9]+"' "$f" | grep -oE '[0-9]+' || echo 0)
    sk=$(grep -m1 -oE 'skipped="[0-9]+"' "$f" | grep -oE '[0-9]+' || echo 0)
    echo "$mod $t $fe $e $sk" >> "$tmp"
  done < <(find "$root" -path '*/target/surefire-reports/TEST-*.xml' -print0 2>/dev/null | sort -z)

  echo ""
  echo "┌─────────────────────────────────────────────────────────────┐"
  echo "│  测试结果汇总（按模块聚合 Surefire TEST-*.xml）               │"
  echo "├─────────────────────────────────────────────────────────────┤"
  printf "│  %-14s %6s %6s %6s %6s                          │\n" "模块" "用例" "失败" "错误" "跳过"
  echo "├─────────────────────────────────────────────────────────────┤"

  if [[ ! -s "$tmp" ]]; then
    echo "│  （未找到 TEST-*.xml，可能未执行 test 或尚未编译测试）          │"
  else
    awk '{
      t[$1]+=$2; f[$1]+=$3; e[$1]+=$4; s[$1]+=$5
    }
    END {
      for (k in t) printf "%s %d %d %d %d\n", k, t[k], f[k], e[k], s[k]
    }' "$tmp" | sort > "$agg"
    local sum_tests=0 sum_fail=0 sum_err=0 sum_skip=0
    while read -r mod t fe e sk; do
      [[ -n "${mod:-}" ]] || continue
      printf "│  %-14s %6s %6s %6s %6s                          │\n" "$mod" "$t" "$fe" "$e" "$sk"
      sum_tests=$((sum_tests + t))
      sum_fail=$((sum_fail + fe))
      sum_err=$((sum_err + e))
      sum_skip=$((sum_skip + sk))
    done < "$agg"
    echo "├─────────────────────────────────────────────────────────────┤"
    printf "│  %-14s %6s %6s %6s %6s                          │\n" "合计" "$sum_tests" "$sum_fail" "$sum_err" "$sum_skip"
    echo "│  ※ 若只构建了部分模块，表中可能仍含其它模块「历史 target」数据 │"
  fi
  rm -f "$tmp" "$agg"
  echo "└─────────────────────────────────────────────────────────────┘"
  echo ""
  echo "  详测 stdout/stderr：各模块 target/surefire-reports/*-output.txt"
  if [[ -n "$user_args" ]]; then
    echo "  完整 Maven 日志：   cd \"$root\" && VERBOSE=1 ./scripts/run-tests.sh $user_args"
  else
    echo "  完整 Maven 日志：   cd \"$root\" && VERBOSE=1 ./scripts/run-tests.sh"
  fi
  echo ""
}

echo ""
echo "┌─────────────────────────────────────────────────────────────┐"
echo "│  单元测试（Maven test）                                       │"
echo "├─────────────────────────────────────────────────────────────┤"
echo "│  项目根：${ROOT}"
echo "│  详细度：VERBOSE=${VERBOSE:-0}（1=完整 Maven 日志）              "
echo "│  说明：  默认 -q 少刷屏；中间若出现 JVM/ByteBuddy 警告，        │"
echo "│          多为 Mockito 动态 agent，一般可忽略。                │"
echo "│          想原样看这些警告：SHOW_JVM_WARN=1                     │"
echo "└─────────────────────────────────────────────────────────────┘"
echo ""
echo "执行：mvn ${MVN_CLI[*]} test $*"
echo ""

set +e
if [[ "${SHOW_JVM_WARN:-0}" == "1" ]]; then
  mvn "${MVN_CLI[@]}" test "$@"
else
  mvn "${MVN_CLI[@]}" test "$@" 2> >(filter_jvm_stderr)
fi
code=$?
set -e

summarize_surefire_reports "$ROOT" "$@"

if [[ "$code" -eq 0 ]]; then
  echo "✓ 测试结论：BUILD SUCCESS（退出码 0）"
else
  echo "✗ 测试结论：BUILD FAILURE（退出码 $code）" >&2
  echo "  建议：VERBOSE=1 ./scripts/run-tests.sh $* 查看编译/依赖错误" >&2
fi
exit "$code"
