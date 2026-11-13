@echo off
title Expense & Budget Tracker - Terminator
echo ========================================================
echo Stopping all Expense Tracker services (ports 8761, 8080-8083, 5173)...
echo ========================================================

for %%P in (8761 8080 8081 8082 8083 5173) do (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr /R /C:":%%P "') do (
        echo Killing process on port %%P (PID %%a)...
        taskkill /F /PID %%a 2>nul
    )
)

echo.
echo All services have been stopped.
pause

