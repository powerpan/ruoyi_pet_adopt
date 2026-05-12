#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is not installed. Please install Docker Desktop first."
  read -r -p "Press Enter to exit..."
  exit 1
fi

if docker compose version >/dev/null 2>&1; then
  compose() { docker compose "$@"; }
elif command -v docker-compose >/dev/null 2>&1; then
  compose() { docker-compose "$@"; }
else
  echo "Docker Compose is not available. Please update Docker Desktop."
  read -r -p "Press Enter to exit..."
  exit 1
fi

if ! docker info >/dev/null 2>&1; then
  echo "Docker is not running. Please start Docker Desktop first."
  read -r -p "Press Enter to exit..."
  exit 1
fi

if [ ! -f deploy/.env ]; then
  cp deploy/.env.example deploy/.env
  echo "Created deploy/.env from deploy/.env.example."
  echo "You can edit deploy/.env to change ports and passwords."
fi

env_value() {
  local key="$1"
  local fallback="$2"
  local value
  value=$(grep -E "^${key}=" deploy/.env | tail -n 1 | cut -d= -f2- | tr -d '\r' || true)
  if [ -n "$value" ]; then
    echo "$value"
  else
    echo "$fallback"
  fi
}

ADMIN_PORT=$(env_value ADMIN_PORT 8081)
PET_APP_PORT=$(env_value PET_APP_PORT 8088)
BACKEND_PORT=$(env_value BACKEND_PORT 8080)

compose --env-file deploy/.env -f deploy/docker-compose.yml up -d --build

echo
echo "Waiting for backend to become ready..."
ready=0
for _ in $(seq 1 90); do
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

echo
echo "RuoYi Pet Adopt is starting."
echo "Admin:   http://localhost:${ADMIN_PORT}"
echo "Client:  http://localhost:${PET_APP_PORT}"
echo "Backend: http://localhost:${BACKEND_PORT}"
if [ "$ready" != "1" ]; then
  echo "Backend is still starting. If the page shows 502, refresh it after a moment."
fi
echo

if command -v open >/dev/null 2>&1; then
  open "http://localhost:${ADMIN_PORT}" >/dev/null 2>&1 || true
  open "http://localhost:${PET_APP_PORT}" >/dev/null 2>&1 || true
fi

read -r -p "Press Enter to close this window..."
