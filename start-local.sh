#!/bin/bash

echo "=========================================="
echo "Event Management Platform - Local Setup"
echo "=========================================="

echo ""
echo "Building all projects with Maven..."
echo "This may take a few minutes on first run..."

if ! mvn clean install -DskipTests; then
    echo ""
    echo "ERROR: Maven build failed!"
    echo "Please check the error messages above and fix any issues."
    echo ""
    exit 1
fi

echo ""
echo "Maven build completed successfully!"
echo ""

echo ""
echo "Starting Eureka Server..."
gnome-terminal --title="Eureka Server" -- bash -c "cd eureka-server && mvn spring-boot:run -Dspring-boot.run.profiles=dev; exec bash" &

echo ""
echo "Waiting for Eureka Server to start..."
sleep 10

echo ""
echo "Starting User Service..."
gnome-terminal --title="User Service" -- bash -c "cd user-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev; exec bash" &

echo ""
echo "Waiting for User Service to start..."
sleep 10

echo ""
echo "Starting Event Service..."
gnome-terminal --title="Event Service" -- bash -c "cd event-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev; exec bash" &

echo ""
echo "Waiting for Event Service to start..."
sleep 10

echo ""
echo "Starting Ticketing Service..."
gnome-terminal --title="Ticketing Service" -- bash -c "cd ticketing-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev; exec bash" &

echo ""
echo "Waiting for Ticketing Service to start..."
sleep 10

echo ""
echo "Starting Notification Service..."
gnome-terminal --title="Notification Service" -- bash -c "cd notification-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev; exec bash" &

echo ""
echo "Waiting for Notification Service to start..."
sleep 10

echo ""
echo "Starting API Gateway..."
gnome-terminal --title="API Gateway" -- bash -c "cd api-gateway && mvn spring-boot:run -Dspring-boot.run.profiles=dev; exec bash" &

echo ""
echo "=========================================="
echo "All services are starting up!"
echo "=========================================="
echo ""
echo "Service URLs:"
echo "- Eureka Server: http://localhost:8761"
echo "- API Gateway: http://localhost:8080"
echo "- User Service: http://localhost:8081"
echo "- Event Service: http://localhost:8082"
echo "- Ticketing Service: http://localhost:8083"
echo "- Notification Service: http://localhost:8084"
echo ""
echo "Press any key to exit..."
read -n 1
