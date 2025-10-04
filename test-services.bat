@echo off
echo ==========================================
echo Testing Individual Services
echo ==========================================

echo.
echo Testing if Maven is available...
mvn --version
if errorlevel 1 (
    echo ERROR: Maven not found!
    pause
    exit /b 1
)

echo.
echo Testing if we can start Eureka Server...
echo This will run for 30 seconds then stop...
cd eureka-server
timeout /t 2 /nobreak > nul
mvn spring-boot:run -Dspring-boot.run.profiles=dev &
timeout /t 30 /nobreak > nul
taskkill /f /im java.exe > nul 2>&1
cd ..

echo.
echo Testing if we can start User Service...
echo This will run for 30 seconds then stop...
cd user-service
timeout /t 2 /nobreak > nul
mvn spring-boot:run -Dspring-boot.run.profiles=dev &
timeout /t 30 /nobreak > nul
taskkill /f /im java.exe > nul 2>&1
cd ..

echo.
echo ==========================================
echo Test completed!
echo ==========================================
echo.
echo If you saw Spring Boot startup messages above, the services can start.
echo If you saw errors, please check:
echo 1. Java is installed and in PATH
echo 2. Maven is installed and in PATH
echo 3. Database is running (for user-service)
echo 4. No port conflicts
echo.
pause
