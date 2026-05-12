#!/usr/bin/env bash
set -e

cd "$(dirname "$0")" || exit 1

SESSIONS=(
  "ruoyi-pet-adopt-backend"
  "ruoyi-pet-adopt-admin-ui"
  "ruoyi-pet-adopt-app"
)

PORTS=(
  8080
  8081
  8088
)

stop_session() {
  local name="$1"
  if screen -ls 2>/dev/null | grep -E "[0-9]+\\.${name}[[:space:]]" >/dev/null 2>&1; then
    echo "Stopping screen session ${name}..."
    screen -S "$name" -X quit || true
  else
    echo "Screen session ${name} is not running."
  fi
}

stop_port() {
  local port="$1"
  local pids
  pids=$(lsof -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null || true)

  if [ -z "$pids" ]; then
    echo "Port ${port} is not listening."
    return
  fi

  echo "Stopping listener on port ${port}: ${pids}"
  kill $pids 2>/dev/null || true
}

for session in "${SESSIONS[@]}"; do
  stop_session "$session"
done

for port in "${PORTS[@]}"; do
  stop_port "$port"
done

echo
echo "Remaining listeners on 8080/8081/8088:"
lsof -nP -iTCP:8080 -iTCP:8081 -iTCP:8088 -sTCP:LISTEN || true
echo
echo "Local development services have been stopped if they were started by start-dev.command."
read -r -p "Press Enter to close this window..."
