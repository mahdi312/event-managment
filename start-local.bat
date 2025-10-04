@echo off
echo ==========================================
echo Event Management Platform - Local Setup
echo ==========================================

echo.
echo Building all projects with Maven...
echo This may take a few minutes on first run...
mvn clean install -DskipTests

if errorlevel 1 (
    echo.
    echo ERROR: Maven build failed!
    echo Please check the error messages above and fix any issues.
    echo.
    pause
    exit /b 1
)

echo.
echo Maven build completed successfully!
echo.

echo.
echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd /d %~dp0eureka-server && mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo.
echo Waiting for Eureka Server to start...
timeout /t 10 /nobreak > nul

echo.
echo Starting User Service...
start "User Service" cmd /k "cd /d %~dp0user-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo.
echo Waiting for User Service to start...
timeout /t 10 /nobreak > nul

echo.
echo Starting Event Service...
start "Event Service" cmd /k "cd /d %~dp0event-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo.
echo Waiting for Event Service to start...
timeout /t 10 /nobreak > nul

echo.
echo Starting Ticketing Service...
start "Ticketing Service" cmd /k "cd /d %~dp0ticketing-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo.
echo Waiting for Ticketing Service to start...
timeout /t 10 /nobreak > nul

echo.
echo Starting Notification Service...
start "Notification Service" cmd /k "cd /d %~dp0notification-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo.
echo Waiting for Notification Service to start...
timeout /t 10 /nobreak > nul

echo.
echo Starting API Gateway...
start "API Gateway" cmd /k "cd /d %~dp0api-gateway && mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo.
echo ==========================================
echo All services are starting up!
echo ==========================================
echo.
echo Service URLs:
echo - Eureka Server: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - User Service: http://localhost:8081
echo - Event Service: http://localhost:8082
echo - Ticketing Service: http://localhost:8083
echo - Notification Service: http://localhost:8084
echo.
echo Press any key to exit...
pause > nul
