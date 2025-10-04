# Local Development Setup Guide

This guide explains how to run the Event Management Platform locally without Docker.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- Git

## Database Setup

### Option 1: Automated Setup (Recommended)

#### Windows
```bash
# Run the automated setup script
setup-databases-windows.bat

# Or using PowerShell
powershell -ExecutionPolicy Bypass -File setup-databases-windows.ps1
```

#### Linux/Mac
```bash
# Make script executable and run
chmod +x setup-databases-linux.sh
./setup-databases-linux.sh
```

### Option 2: Manual Setup

#### 1. Install and Start PostgreSQL

Make sure PostgreSQL is running on your local machine:
- Default port: 5432
- Default user: postgres
- Default password: password (or your configured password)

**Installation guides:**
- **Windows**: Download from https://www.postgresql.org/download/windows/
- **Ubuntu/Debian**: `sudo apt install postgresql postgresql-contrib`
- **CentOS/RHEL**: `sudo yum install postgresql postgresql-server`
- **macOS**: `brew install postgresql`

#### 2. Create Databases and Tables

Run the database setup script:

```bash
# Connect to PostgreSQL as superuser and run the setup script
psql -U postgres -f local-database-setup.sql
```

This will create:
- `user_db` - User management database
- `event_db` - Event management database  
- `ticketing_db` - Ticketing database

#### 3. Verify Database Setup

```bash
# Check if databases were created
psql -U postgres -c "\l"

# You should see: user_db, event_db, ticketing_db
```

### Option 3: Docker Database Only

If you prefer to use Docker just for databases:

```bash
# Create databases using Docker
docker-compose -f docker-compose-db-only.yml up -d
```

### Troubleshooting Database Setup

If you get "psql: command not found" error, see the comprehensive [DATABASE_SETUP.md](DATABASE_SETUP.md) guide for detailed installation instructions for your platform.

## Building the Project

### Option 1: Build and Start All Services (Recommended)

#### Windows
```bash
# Build all projects and start all services
start-local.bat
```

#### Linux/Mac
```bash
# Build all projects and start all services
./start-local.sh
```

### Option 2: Build Only (Without Starting Services)

#### Windows
```bash
# Build all projects only
build-all.bat
```

#### Linux/Mac
```bash
# Build all projects only
./build-all.sh
```

### Option 3: Manual Build and Start

```bash
# Build all projects first
mvn clean install -DskipTests

# Then start services individually (see below)
```

## Running the Services

### 1. Start Eureka Server (Service Discovery)

```bash
cd eureka-server
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Eureka will be available at: http://localhost:8761

### 2. Start User Service

```bash
cd user-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

User Service will be available at: http://localhost:8081

### 3. Start Event Service

```bash
cd event-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Event Service will be available at: http://localhost:8082

### 4. Start Ticketing Service

```bash
cd ticketing-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Ticketing Service will be available at: http://localhost:8083

### 5. Start Notification Service

```bash
cd notification-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Notification Service will be available at: http://localhost:8084

### 6. Start API Gateway

```bash
cd api-gateway
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

API Gateway will be available at: http://localhost:8080

## Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| Eureka Server | http://localhost:8761 | Service Discovery |
| API Gateway | http://localhost:8080 | Main Entry Point |
| User Service | http://localhost:8081 | User Management |
| Event Service | http://localhost:8082 | Event Management |
| Ticketing Service | http://localhost:8083 | Ticket Booking |
| Notification Service | http://localhost:8084 | Notifications |

## API Documentation

Each service has Swagger UI available:
- User Service: http://localhost:8081/swagger-ui.html
- Event Service: http://localhost:8082/swagger-ui.html
- Ticketing Service: http://localhost:8083/swagger-ui.html

## Default Users

The following users are created by default:

| Username | Email | Role | Password |
|----------|-------|------|----------|
| mehdi-jv | mehdi-jv@example.com | ADMIN | (hashed) |
| rasoul-nb | rasoul-nb@example.com | EVENT_MANAGER | (hashed) |
| mahdi-mst | mahdi-mst@example.com | USER | (hashed) |

## Troubleshooting

### Database Connection Issues

1. **Check PostgreSQL is running:**
   ```bash
   # Windows
   net start postgresql
   
   # Linux/Mac
   sudo systemctl start postgresql
   ```

2. **Verify database exists:**
   ```bash
   psql -U postgres -c "\l"
   ```

3. **Check connection:**
   ```bash
   psql -U postgres -d user_db -c "\dt"
   ```

### Service Discovery Issues

1. **Check Eureka is running:** http://localhost:8761
2. **Verify services are registered:** Check Eureka dashboard
3. **Check service logs** for connection errors

### Port Conflicts

If you get port conflicts, you can change ports in the respective `application-dev.yml` files:

```yaml
server:
  port: 8081  # Change this to an available port
```

## Development Tips

1. **Use IDE Run Configurations:** Set up run configurations in your IDE for easier development
2. **Hot Reload:** Use Spring Boot DevTools for automatic restarts
3. **Database Tools:** Use pgAdmin or DBeaver for database management
4. **API Testing:** Use Postman or curl for API testing

## Stopping Services

To stop all services:
1. Press `Ctrl+C` in each terminal running a service
2. Or use your IDE's stop button for each service

## Cleanup

To remove the databases:
```bash
psql -U postgres -c "DROP DATABASE IF EXISTS user_db;"
psql -U postgres -c "DROP DATABASE IF EXISTS event_db;"
psql -U postgres -c "DROP DATABASE IF EXISTS ticketing_db;"
```
