#!/usr/bin/env bash
set -e

cd "$(dirname "$0")" || exit 1

ROOT_DIR=$(pwd)
LOG_DIR="${ROOT_DIR}/.dev-logs"
BACKEND_SESSION="ruoyi-pet-adopt-backend"
ADMIN_SESSION="ruoyi-pet-adopt-admin-ui"
APP_SESSION="ruoyi-pet-adopt-app"

BACKEND_PORT="${BACKEND_PORT:-8080}"
ADMIN_PORT="${ADMIN_PORT:-8081}"
PET_APP_PORT="${PET_APP_PORT:-8088}"
RUOYI_PET_ADOPT_DB_USER="${RUOYI_PET_ADOPT_DB_USER:-root}"
RUOYI_PET_ADOPT_DB_PASSWORD="${RUOYI_PET_ADOPT_DB_PASSWORD:-root}"
RUOYI_PET_ADOPT_BOOTSTRAP_ADMIN_PASSWORD="${RUOYI_PET_ADOPT_BOOTSTRAP_ADMIN_PASSWORD:-admin123}"

mkdir -p "$LOG_DIR"

pause() {
  echo
  read -r -p "Press Enter to close this window..."
}

need_command() {
  local name="$1"
  if ! command -v "$name" >/dev/null 2>&1; then
    echo "Missing command: ${name}"
    echo "Please install ${name} before starting local development."
    pause
    exit 1
  fi
}

screen_exists() {
  local name="$1"
  screen -ls 2>/dev/null | grep -E "[0-9]+\\.${name}[[:space:]]" >/dev/null 2>&1
}

port_listening() {
  local port="$1"
  lsof -nP -iTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1
}

wait_for_port() {
  local name="$1"
  local port="$2"
  local i

  echo "Waiting for ${name} on port ${port}..."
  for i in $(seq 1 90); do
    if port_listening "$port"; then
      echo "${name} is listening on ${port}."
      return 0
    fi
    sleep 2
  done

  echo "${name} did not become ready in time. Check logs in .dev-logs/."
  return 1
}

check_local_services() {
  if command -v mysqladmin >/dev/null 2>&1; then
    if ! mysqladmin -h127.0.0.1 -P3306 -u"${RUOYI_PET_ADOPT_DB_USER}" -p"${RUOYI_PET_ADOPT_DB_PASSWORD}" ping >/dev/null 2>&1; then
      echo "Local MySQL is not reachable on 127.0.0.1:3306 with user ${RUOYI_PET_ADOPT_DB_USER}."
      echo "Please start MySQL and confirm RUOYI_PET_ADOPT_DB_PASSWORD. Default is root."
      pause
      exit 1
    fi
  else
    echo "mysqladmin not found. Skipping MySQL pre-check."
  fi

  if command -v redis-cli >/dev/null 2>&1; then
    if ! redis-cli -h 127.0.0.1 -p 6379 -n 6 ping >/dev/null 2>&1; then
      echo "Local Redis is not reachable on 127.0.0.1:6379 database 6."
      echo "Please start Redis before starting local development."
      pause
      exit 1
    fi
  else
    echo "redis-cli not found. Skipping Redis pre-check."
  fi
}

start_backend() {
  if port_listening "$BACKEND_PORT"; then
    echo "Backend port ${BACKEND_PORT} is already in use. Skipping backend start."
    return
  fi

  if screen_exists "$BACKEND_SESSION"; then
    echo "Screen session ${BACKEND_SESSION} already exists. Skipping backend start."
    return
  fi

  echo "Starting backend in screen session ${BACKEND_SESSION}..."
  screen -dmS "$BACKEND_SESSION" /bin/zsh -lc "
    cd '$ROOT_DIR' &&
    export RUOYI_PET_ADOPT_DB_USER='$RUOYI_PET_ADOPT_DB_USER' &&
    export RUOYI_PET_ADOPT_DB_PASSWORD='$RUOYI_PET_ADOPT_DB_PASSWORD' &&
    export RUOYI_PET_ADOPT_BOOTSTRAP_ADMIN_PASSWORD='$RUOYI_PET_ADOPT_BOOTSTRAP_ADMIN_PASSWORD' &&
    exec > '$LOG_DIR/backend.log' 2>&1 &&
    echo '[backend] building and starting...' &&
    mvn -pl ruoyi-admin -am package -DskipTests &&
    exec java -jar ruoyi-admin/target/ruoyi-admin.jar --spring.profiles.active=dev
  "
}

start_admin_ui() {
  if port_listening "$ADMIN_PORT"; then
    echo "Admin UI port ${ADMIN_PORT} is already in use. Skipping admin UI start."
    return
  fi

  if screen_exists "$ADMIN_SESSION"; then
    echo "Screen session ${ADMIN_SESSION} already exists. Skipping admin UI start."
    return
  fi

  echo "Starting admin UI in screen session ${ADMIN_SESSION}..."
  screen -dmS "$ADMIN_SESSION" /bin/zsh -lc "
    cd '$ROOT_DIR/ruoyi-ui' &&
    exec > '$LOG_DIR/admin-ui.log' 2>&1 &&
    echo '[admin-ui] starting...' &&
    exec npm run dev
  "
}

start_pet_app() {
  if port_listening "$PET_APP_PORT"; then
    echo "Pet app port ${PET_APP_PORT} is already in use. Skipping pet app start."
    return
  fi

  if screen_exists "$APP_SESSION"; then
    echo "Screen session ${APP_SESSION} already exists. Skipping pet app start."
    return
  fi

  echo "Starting pet app in screen session ${APP_SESSION}..."
  screen -dmS "$APP_SESSION" /bin/zsh -lc "
    cd '$ROOT_DIR/pet-app' &&
    exec > '$LOG_DIR/pet-app.log' 2>&1 &&
    echo '[pet-app] starting...' &&
    exec npm run dev
  "
}

need_command java
need_command mvn
need_command npm
need_command screen
check_local_services

start_backend
start_admin_ui
start_pet_app

echo
wait_for_port "Backend" "$BACKEND_PORT" || true
wait_for_port "Admin UI" "$ADMIN_PORT" || true
wait_for_port "Pet app" "$PET_APP_PORT" || true

echo
echo "Local development services:"
echo "  Backend:  http://localhost:${BACKEND_PORT}"
echo "  Admin:    http://localhost:${ADMIN_PORT}"
echo "  Client:   http://localhost:${PET_APP_PORT}"
echo
echo "Logs:"
echo "  ${LOG_DIR}/backend.log"
echo "  ${LOG_DIR}/admin-ui.log"
echo "  ${LOG_DIR}/pet-app.log"
echo
echo "Stop with: ./stop-dev.command"

if command -v open >/dev/null 2>&1; then
  open "http://localhost:${ADMIN_PORT}" >/dev/null 2>&1 || true
  open "http://localhost:${PET_APP_PORT}" >/dev/null 2>&1 || true
fi

pause
