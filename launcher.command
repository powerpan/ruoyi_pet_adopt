#!/usr/bin/env bash

cd "$(dirname "$0")" || exit 1

APP_NAME="宠物领养平台"

dialog() {
  local title="$1"
  local message
  message=$(printf '%b' "$2")
  osascript - "$title" "$message" <<'OSA' >/dev/null 2>&1 || true
on run argv
  display dialog (item 2 of argv) buttons {"知道了"} default button "知道了" with title (item 1 of argv)
end run
OSA
}

choose_action() {
  osascript <<'OSA' 2>/dev/null
set choiceItems to {"启动服务", "停止服务", "查看状态", "打开管理端", "打开用户端", "打开配置文件", "退出"}
set selectedItem to choose from list choiceItems with title "宠物领养平台" with prompt "请选择要执行的操作：" OK button name "执行" cancel button name "退出"
if selectedItem is false then
  return "退出"
else
  return item 1 of selectedItem
end if
OSA
}

ensure_compose() {
  if ! command -v docker >/dev/null 2>&1; then
    dialog "$APP_NAME" "未检测到 Docker。请先安装并启动 Docker Desktop。"
    return 1
  fi

  if docker compose version >/dev/null 2>&1; then
    compose() { docker compose "$@"; }
  elif command -v docker-compose >/dev/null 2>&1; then
    compose() { docker-compose "$@"; }
  else
    dialog "$APP_NAME" "未检测到 Docker Compose。请更新 Docker Desktop。"
    return 1
  fi

  return 0
}

ensure_docker_running() {
  ensure_compose || return 1

  if ! docker info >/dev/null 2>&1; then
    dialog "$APP_NAME" "Docker Desktop 还没有运行。请先打开 Docker Desktop，等它启动完成后再回来点击启动。"
    return 1
  fi

  return 0
}

ensure_env() {
  if [ ! -f deploy/.env ]; then
    cp deploy/.env.example deploy/.env
    echo "Created deploy/.env from deploy/.env.example."
  fi
}

env_value() {
  local key="$1"
  local fallback="$2"
  local value
  value=$(grep -E "^${key}=" deploy/.env 2>/dev/null | tail -n 1 | cut -d= -f2- | tr -d '\r' || true)
  if [ -n "$value" ]; then
    echo "$value"
  else
    echo "$fallback"
  fi
}

read_ports() {
  ensure_env
  ADMIN_PORT=$(env_value ADMIN_PORT 8081)
  PET_APP_PORT=$(env_value PET_APP_PORT 8088)
  BACKEND_PORT=$(env_value BACKEND_PORT 8080)
}

wait_backend() {
  local ready=0
  local i

  echo
  echo "Waiting for backend to become ready..."
  for i in $(seq 1 90); do
    if command -v curl >/dev/null 2>&1; then
      if curl -fsS "http://localhost:${BACKEND_PORT}/app/pet/topics?pageNum=1&pageSize=1" >/dev/null 2>&1; then
        ready=1
        break
      fi
    else
      ready=1
      break
    fi
    sleep 2
  done

  if [ "$ready" = "1" ]; then
    return 0
  fi

  return 1
}

start_stack() {
  ensure_docker_running || return
  read_ports

  echo
  echo "Starting ${APP_NAME}..."
  if ! compose --env-file deploy/.env -f deploy/docker-compose.yml up -d --build; then
    dialog "$APP_NAME" "启动失败，请查看当前终端窗口里的错误日志。"
    return
  fi

  if wait_backend; then
    open "http://localhost:${ADMIN_PORT}" >/dev/null 2>&1 || true
    open "http://localhost:${PET_APP_PORT}" >/dev/null 2>&1 || true
    dialog "$APP_NAME" "服务已启动。\n\n管理端：http://localhost:${ADMIN_PORT}\n用户端：http://localhost:${PET_APP_PORT}\n后端接口：http://localhost:${BACKEND_PORT}"
  else
    dialog "$APP_NAME" "服务已提交启动，但后端还没完全就绪。\n\n如果页面显示 502，请稍等片刻后刷新。"
  fi
}

stop_stack() {
  ensure_compose || return
  ensure_env

  echo
  echo "Stopping ${APP_NAME}..."
  if ! compose --env-file deploy/.env -f deploy/docker-compose.yml down; then
    dialog "$APP_NAME" "停止失败，请查看当前终端窗口里的错误日志。"
    return
  fi

  dialog "$APP_NAME" "服务已停止。\n数据库和上传文件卷会保留。"
}

show_status() {
  ensure_compose || return
  ensure_env

  echo
  echo "Current service status:"
  compose --env-file deploy/.env -f deploy/docker-compose.yml ps
  dialog "$APP_NAME" "状态已输出到当前终端窗口。"
}

open_admin() {
  read_ports
  open "http://localhost:${ADMIN_PORT}" >/dev/null 2>&1 || true
}

open_client() {
  read_ports
  open "http://localhost:${PET_APP_PORT}" >/dev/null 2>&1 || true
}

open_env() {
  ensure_env
  open -e deploy/.env >/dev/null 2>&1 || open deploy/.env >/dev/null 2>&1 || true
}

while true; do
  action=$(choose_action)
  case "$action" in
    "启动服务") start_stack ;;
    "停止服务") stop_stack ;;
    "查看状态") show_status ;;
    "打开管理端") open_admin ;;
    "打开用户端") open_client ;;
    "打开配置文件") open_env ;;
    "退出" | "") exit 0 ;;
  esac
done
