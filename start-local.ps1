# Event Management Platform - Local Setup (PowerShell)
# Run with: powershell -ExecutionPolicy Bypass -File start-local.ps1

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Event Management Platform - Local Setup" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "Building all projects with Maven..." -ForegroundColor Yellow
Write-Host "This may take a few minutes on first run..." -ForegroundColor Yellow

if (!(Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Maven not found!" -ForegroundColor Red
    Write-Host "Please install Maven and add it to your PATH." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

try {
    mvn clean install -DskipTests
    if ($LASTEXITCODE -ne 0) {
        throw "Maven build failed"
    }
    Write-Host ""
    Write-Host "Maven build completed successfully!" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host ""
    Write-Host "ERROR: Maven build failed!" -ForegroundColor Red
    Write-Host "Please check the error messages above and fix any issues." -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "Starting Eureka Server..." -ForegroundColor Yellow
Start-Process -FilePath "cmd" -ArgumentList "/k", "cd /d `"$PWD\eureka-server`" && mvn spring-boot:run -Dspring-boot.run.profiles=dev" -WindowStyle Normal

Write-Host ""
Write-Host "Waiting for Eureka Server to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "Starting User Service..." -ForegroundColor Yellow
Start-Process -FilePath "cmd" -ArgumentList "/k", "cd /d `"$PWD\user-service`" && mvn spring-boot:run -Dspring-boot.run.profiles=dev" -WindowStyle Normal

Write-Host ""
Write-Host "Waiting for User Service to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "Starting Event Service..." -ForegroundColor Yellow
Start-Process -FilePath "cmd" -ArgumentList "/k", "cd /d `"$PWD\event-service`" && mvn spring-boot:run -Dspring-boot.run.profiles=dev" -WindowStyle Normal

Write-Host ""
Write-Host "Waiting for Event Service to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "Starting Ticketing Service..." -ForegroundColor Yellow
Start-Process -FilePath "cmd" -ArgumentList "/k", "cd /d `"$PWD\ticketing-service`" && mvn spring-boot:run -Dspring-boot.run.profiles=dev" -WindowStyle Normal

Write-Host ""
Write-Host "Waiting for Ticketing Service to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "Starting Notification Service..." -ForegroundColor Yellow
Start-Process -FilePath "cmd" -ArgumentList "/k", "cd /d `"$PWD\notification-service`" && mvn spring-boot:run -Dspring-boot.run.profiles=dev" -WindowStyle Normal

Write-Host ""
Write-Host "Waiting for Notification Service to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "Starting API Gateway..." -ForegroundColor Yellow
Start-Process -FilePath "cmd" -ArgumentList "/k", "cd /d `"$PWD\api-gateway`" && mvn spring-boot:run -Dspring-boot.run.profiles=dev" -WindowStyle Normal

Write-Host ""
Write-Host "==========================================" -ForegroundColor Green
Write-Host "All services are starting up!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Service URLs:" -ForegroundColor Yellow
Write-Host "- Eureka Server: http://localhost:8761" -ForegroundColor Gray
Write-Host "- API Gateway: http://localhost:8080" -ForegroundColor Gray
Write-Host "- User Service: http://localhost:8081" -ForegroundColor Gray
Write-Host "- Event Service: http://localhost:8082" -ForegroundColor Gray
Write-Host "- Ticketing Service: http://localhost:8083" -ForegroundColor Gray
Write-Host "- Notification Service: http://localhost:8084" -ForegroundColor Gray
Write-Host ""
Read-Host "Press Enter to exit"
