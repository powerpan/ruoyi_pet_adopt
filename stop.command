#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

if docker compose version >/dev/null 2>&1; then
  compose() { docker compose "$@"; }
elif command -v docker-compose >/dev/null 2>&1; then
  compose() { docker-compose "$@"; }
else
  echo "Docker Compose is not available."
  read -r -p "Press Enter to exit..."
  exit 1
fi

if [ ! -f deploy/.env ]; then
  cp deploy/.env.example deploy/.env
fi

compose --env-file deploy/.env -f deploy/docker-compose.yml down

echo "RuoYi Pet Adopt has stopped. Database and upload volumes are preserved."
read -r -p "Press Enter to close this window..."
