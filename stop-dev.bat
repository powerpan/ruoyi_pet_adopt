@echo off
setlocal

echo Stopping local development processes on ports 8080, 8081 and 8088...

for %%P in (8080 8081 8088) do (
  for /f "tokens=5" %%A in ('netstat -ano ^| findstr ":%%P " ^| findstr "LISTENING"') do (
    echo Killing PID %%A on port %%P
    taskkill /F /PID %%A >nul 2>nul
  )
)

echo Done.
pause
