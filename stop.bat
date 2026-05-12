@echo off
setlocal
cd /d "%~dp0"

docker compose version >nul 2>nul
if errorlevel 1 (
  where docker-compose >nul 2>nul
  if errorlevel 1 (
    echo Docker Compose is not available.
    pause
    exit /b 1
  )
  set "COMPOSE=docker-compose"
) else (
  set "COMPOSE=docker compose"
)

if not exist "deploy\.env" (
  copy "deploy\.env.example" "deploy\.env" >nul
)

%COMPOSE% --env-file deploy\.env -f deploy\docker-compose.yml down
if errorlevel 1 (
  echo Failed to stop RuoYi Pet Adopt.
  pause
  exit /b 1
)

echo RuoYi Pet Adopt has stopped. Database and upload volumes are preserved.
pause
