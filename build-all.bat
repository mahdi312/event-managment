@echo off
echo ==========================================
echo Building Event Management Platform
echo ==========================================

echo.
echo Building all projects with Maven...
echo This may take a few minutes on first run...
echo.

mvn clean install -DskipTests

if errorlevel 1 (
    echo.
    echo ERROR: Maven build failed!
    echo Please check the error messages above and fix any issues.
    echo.
    echo Common issues and solutions:
    echo 1. Java version mismatch - ensure you have Java 17 or higher
    echo 2. Maven not found - ensure Maven is installed and in PATH
    echo 3. Network issues - check your internet connection for dependencies
    echo 4. Port conflicts - ensure no services are using the required ports
    echo.
    pause
    exit /b 1
)

echo.
echo ==========================================
echo Build completed successfully!
echo ==========================================
echo.
echo All microservices have been built and are ready to run.
echo.
echo You can now start the services using:
echo - start-local.bat (Windows)
echo - ./start-local.sh (Linux/Mac)
echo.
echo Or start individual services manually:
echo - cd eureka-server && mvn spring-boot:run
echo - cd user-service && mvn spring-boot:run
echo - cd event-service && mvn spring-boot:run
echo - cd ticketing-service && mvn spring-boot:run
echo - cd notification-service && mvn spring-boot:run
echo - cd api-gateway && mvn spring-boot:run
echo.
pause
