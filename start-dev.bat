@echo off
setlocal
cd /d "%~dp0"

if "%BACKEND_PORT%"=="" set "BACKEND_PORT=8080"
if "%ADMIN_PORT%"=="" set "ADMIN_PORT=8081"
if "%PET_APP_PORT%"=="" set "PET_APP_PORT=8088"
if "%RUOYI_PET_ADOPT_DB_USER%"=="" set "RUOYI_PET_ADOPT_DB_USER=root"
if "%RUOYI_PET_ADOPT_DB_PASSWORD%"=="" set "RUOYI_PET_ADOPT_DB_PASSWORD=root"
if "%RUOYI_PET_ADOPT_BOOTSTRAP_ADMIN_PASSWORD%"=="" set "RUOYI_PET_ADOPT_BOOTSTRAP_ADMIN_PASSWORD=admin123"

if not exist ".dev-logs" mkdir ".dev-logs"

where java >nul 2>nul || (
  echo Missing command: java
  pause
  exit /b 1
)

where mvn >nul 2>nul || (
  echo Missing command: mvn
  pause
  exit /b 1
)

where npm >nul 2>nul || (
  echo Missing command: npm
  pause
  exit /b 1
)

where mysqladmin >nul 2>nul
if not errorlevel 1 (
  mysqladmin -h127.0.0.1 -P3306 -u%RUOYI_PET_ADOPT_DB_USER% -p%RUOYI_PET_ADOPT_DB_PASSWORD% ping >nul 2>nul
  if errorlevel 1 (
    echo Local MySQL is not reachable on 127.0.0.1:3306.
    echo Please start MySQL and confirm RUOYI_PET_ADOPT_DB_PASSWORD. Default is root.
    pause
    exit /b 1
  )
) else (
  echo mysqladmin not found. Skipping MySQL pre-check.
)

where redis-cli >nul 2>nul
if not errorlevel 1 (
  redis-cli -h 127.0.0.1 -p 6379 -n 6 ping >nul 2>nul
  if errorlevel 1 (
    echo Local Redis is not reachable on 127.0.0.1:6379 database 6.
    echo Please start Redis before starting local development.
    pause
    exit /b 1
  )
) else (
  echo redis-cli not found. Skipping Redis pre-check.
)

echo Starting backend, admin UI and pet app...
(
  echo @echo off
  echo cd /d "%~dp0"
  echo set "RUOYI_PET_ADOPT_DB_USER=%RUOYI_PET_ADOPT_DB_USER%"
  echo set "RUOYI_PET_ADOPT_DB_PASSWORD=%RUOYI_PET_ADOPT_DB_PASSWORD%"
  echo set "RUOYI_PET_ADOPT_BOOTSTRAP_ADMIN_PASSWORD=%RUOYI_PET_ADOPT_BOOTSTRAP_ADMIN_PASSWORD%"
  echo mvn -pl ruoyi-admin -am package -DskipTests
  echo if errorlevel 1 pause ^& exit /b 1
  echo java -jar ruoyi-admin\target\ruoyi-admin.jar --spring.profiles.active=dev
) > ".dev-logs\run-backend.bat"

(
  echo @echo off
  echo cd /d "%~dp0ruoyi-ui"
  echo npm run dev
) > ".dev-logs\run-admin-ui.bat"

(
  echo @echo off
  echo cd /d "%~dp0pet-app"
  echo npm run dev
) > ".dev-logs\run-pet-app.bat"

start "RuoYi Pet Adopt Backend" cmd /k ""%~dp0.dev-logs\run-backend.bat""
start "RuoYi Pet Adopt Admin UI" cmd /k ""%~dp0.dev-logs\run-admin-ui.bat""
start "RuoYi Pet Adopt App" cmd /k ""%~dp0.dev-logs\run-pet-app.bat""

echo.
echo Services are starting:
echo Backend: http://localhost:%BACKEND_PORT%
echo Admin:   http://localhost:%ADMIN_PORT%
echo Client:  http://localhost:%PET_APP_PORT%
echo.
echo Use stop-dev.bat to stop processes listening on these local dev ports.

timeout /t 8 /nobreak >nul
start "" "http://localhost:%ADMIN_PORT%"
start "" "http://localhost:%PET_APP_PORT%"
pause
