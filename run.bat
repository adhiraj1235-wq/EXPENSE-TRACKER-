@echo off
title Expense & Budget Tracker - Launcher
echo ========================================================
echo Starting Expense & Budget Tracker Microservices + React
echo ========================================================
echo.

:: Detect Java executable
set JAVA_EXE="C:\Program Files\Java\jdk-25.0.4.1\bin\java.exe"
where java >nul 2>nul
if %ERRORLEVEL% equ 0 set JAVA_EXE=java

:: Detect npm executable
set NPM_CMD="C:\Program Files\nodejs\npm.cmd"
where npm >nul 2>nul
if %ERRORLEVEL% equ 0 set NPM_CMD=npm

echo [1/6] Starting Eureka Discovery Server (Port 8761)...
start "Discovery Server (8761)" %JAVA_EXE% -jar backend\discovery-server\target\discovery-server-1.0.0.jar
echo Waiting 8 seconds for Eureka registry to initialize...
timeout /t 8 /nobreak > nul

echo [2/6] Starting API Gateway (Port 8080)...
start "API Gateway (8080)" %JAVA_EXE% -jar backend\api-gateway\target\api-gateway-1.0.0.jar
echo Waiting 6 seconds for Gateway...
timeout /t 6 /nobreak > nul

echo [3/6] Starting User Service (Port 8081)...
start "User Service (8081)" %JAVA_EXE% -jar backend\user-service\target\user-service-1.0.0.jar

echo [4/6] Starting Transaction Service (Port 8082)...
start "Transaction Service (8082)" %JAVA_EXE% -jar backend\transaction-service\target\transaction-service-1.0.0.jar

echo [5/6] Starting Budget Analytics Service (Port 8083)...
start "Budget Analytics Service (8083)" %JAVA_EXE% -jar backend\budget-analytics-service\target\budget-analytics-service-1.0.0.jar
echo Waiting 8 seconds for services to register with Eureka...
timeout /t 8 /nobreak > nul

echo [6/6] Starting React Frontend (Port 5173)...
cd frontend
start "React Frontend (5173)" %NPM_CMD% run dev
cd ..

timeout /t 5 /nobreak > nul
echo.
echo ========================================================
echo All 6 services launched successfully!
echo.
echo - React Frontend: http://localhost:5173
echo - API Gateway:    http://localhost:8080
echo - Eureka Registry:http://localhost:8761
echo ========================================================
echo Opening browser...
start http://localhost:5173
pause
