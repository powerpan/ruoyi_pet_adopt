@echo off
setlocal
cd /d "%~dp0"

where docker >nul 2>nul
if errorlevel 1 (
  echo Docker is not installed. Please install Docker Desktop first.
  pause
  exit /b 1
)

docker compose version >nul 2>nul
if errorlevel 1 (
  where docker-compose >nul 2>nul
  if errorlevel 1 (
    echo Docker Compose is not available. Please update Docker Desktop.
    pause
    exit /b 1
  )
  set "COMPOSE=docker-compose"
) else (
  set "COMPOSE=docker compose"
)

docker info >nul 2>nul
if errorlevel 1 (
  echo Docker is not running. Please start Docker Desktop first.
  pause
  exit /b 1
)

if not exist "deploy\.env" (
  copy "deploy\.env.example" "deploy\.env" >nul
  echo Created deploy\.env from deploy\.env.example.
  echo You can edit deploy\.env to change ports and passwords.
)

set "ADMIN_PORT=8081"
set "PET_APP_PORT=8088"
set "BACKEND_PORT=8080"
for /f "usebackq tokens=1,* delims==" %%A in ("deploy\.env") do (
  if "%%A"=="ADMIN_PORT" set "ADMIN_PORT=%%B"
  if "%%A"=="PET_APP_PORT" set "PET_APP_PORT=%%B"
  if "%%A"=="BACKEND_PORT" set "BACKEND_PORT=%%B"
)

%COMPOSE% --env-file deploy\.env -f deploy\docker-compose.yml up -d --build
if errorlevel 1 (
  echo Failed to start RuoYi Pet Adopt.
  pause
  exit /b 1
)

echo.
echo Waiting for backend to become ready...
set "READY=0"
for /L %%I in (1,1,90) do (
  curl -fsS "http://localhost:%BACKEND_PORT%/app/pet/topics?pageNum=1&pageSize=1" >nul 2>nul
  if not errorlevel 1 (
    set "READY=1"
    goto backend_ready
  )
  timeout /t 2 /nobreak >nul
)

:backend_ready
echo.
echo RuoYi Pet Adopt is starting.
echo Admin:   http://localhost:%ADMIN_PORT%
echo Client:  http://localhost:%PET_APP_PORT%
echo Backend: http://localhost:%BACKEND_PORT%
if not "%READY%"=="1" echo Backend is still starting. If the page shows 502, refresh it after a moment.
echo.

start "" "http://localhost:%ADMIN_PORT%"
start "" "http://localhost:%PET_APP_PORT%"
pause
