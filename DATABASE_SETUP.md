# Database Setup Guide

This guide provides multiple ways to set up the databases for the Event Management Platform, handling cases where `psql` command might not be available.

## Method 1: Automated Scripts (Recommended)

### Windows
```bash
# Run the automated setup script
setup-databases-windows.bat
```

### Linux/Mac
```bash
# Make script executable and run
chmod +x setup-databases-linux.sh
./setup-databases-linux.sh
```

## Method 2: Manual PostgreSQL Installation

### Windows

#### Option A: Official Installer
1. Download PostgreSQL from: https://www.postgresql.org/download/windows/
2. Run the installer
3. Remember the password you set for the `postgres` user
4. Add PostgreSQL to PATH during installation

#### Option B: Using Chocolatey
```powershell
# Install Chocolatey (if not already installed)
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePoint]::SecurityProtocol = [System.Net.ServicePoint]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install PostgreSQL
choco install postgresql
```

#### Option C: Using Scoop
```powershell
# Install Scoop (if not already installed)
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
irm get.scoop.sh | iex

# Install PostgreSQL
scoop install postgresql
```

### Linux

#### Ubuntu/Debian
```bash
# Update package list
sudo apt update

# Install PostgreSQL
sudo apt install postgresql postgresql-contrib

# Start and enable PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Set postgres user password
sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'password';"
```

#### CentOS/RHEL/Fedora
```bash
# Install PostgreSQL
sudo yum install postgresql postgresql-server
# or for newer versions:
sudo dnf install postgresql postgresql-server

# Initialize database
sudo postgresql-setup --initdb

# Start and enable PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Set postgres user password
sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'password';"
```

#### Arch Linux
```bash
# Install PostgreSQL
sudo pacman -S postgresql

# Initialize database
sudo -u postgres initdb -D /var/lib/postgres/data

# Start and enable PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### macOS

#### Option A: Using Homebrew
```bash
# Install Homebrew (if not already installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install PostgreSQL
brew install postgresql

# Start PostgreSQL
brew services start postgresql

# Set postgres user password
psql postgres -c "ALTER USER postgres PASSWORD 'password';"
```

#### Option B: Using MacPorts
```bash
# Install PostgreSQL
sudo port install postgresql15

# Start PostgreSQL
sudo port load postgresql15

# Set postgres user password
psql postgres -c "ALTER USER postgres PASSWORD 'password';"
```

## Method 3: Using Docker (Alternative)

If you prefer to use Docker for just the databases:

```bash
# Create a docker-compose file for databases only
cat > docker-compose-db-only.yml << EOF
version: '3.8'
services:
  user-db:
    image: postgres:15-alpine
    container_name: user-db
    environment:
      POSTGRES_DB: user_db
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "5431:5432"
    volumes:
      - ./user-service/src/main/resources/init-db.sql:/docker-entrypoint-initdb.d/init-db.sql

  event-db:
    image: postgres:15-alpine
    container_name: event-db
    environment:
      POSTGRES_DB: event_db
      POSTGRES_USER: eventuser
      POSTGRES_PASSWORD: eventpassword
    ports:
      - "5432:5432"
    volumes:
      - ./event-service/src/main/resources/init-db.sql:/docker-entrypoint-initdb.d/init-db.sql

  ticketing-db:
    image: postgres:15-alpine
    container_name: ticketing-db
    environment:
      POSTGRES_DB: ticketing_db
      POSTGRES_USER: ticketuser
      POSTGRES_PASSWORD: ticketpassword
    ports:
      - "5433:5432"
    volumes:
      - ./ticketing-service/src/main/resources/init-db.sql:/docker-entrypoint-initdb.d/init-db.sql
EOF

# Start only the databases
docker-compose -f docker-compose-db-only.yml up -d

# Check if databases are running
docker-compose -f docker-compose-db-only.yml ps
```

## Method 4: Using pgAdmin (GUI Method)

1. **Install pgAdmin**: https://www.pgadmin.org/download/
2. **Connect to PostgreSQL** using pgAdmin
3. **Create databases manually**:
   - Right-click "Databases" → "Create" → "Database"
   - Create: `user_db`, `event_db`, `ticketing_db`
4. **Run SQL scripts**:
   - Right-click each database → "Query Tool"
   - Copy and paste the respective SQL from `local-database-setup.sql`

## Method 5: Using DBeaver (Alternative GUI)

1. **Install DBeaver**: https://dbeaver.io/download/
2. **Connect to PostgreSQL**
3. **Create databases** and run the SQL scripts

## Troubleshooting

### Common Issues

#### 1. "psql: command not found"
**Solution**: Add PostgreSQL bin directory to PATH
- **Windows**: Add `C:\Program Files\PostgreSQL\15\bin` to PATH
- **Linux/Mac**: Add `/usr/bin` or `/usr/local/bin` to PATH

#### 2. "FATAL: password authentication failed"
**Solution**: Reset postgres user password
```bash
# Linux/Mac
sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'password';"

# Windows (as Administrator)
psql -U postgres -c "ALTER USER postgres PASSWORD 'password';"
```

#### 3. "FATAL: database does not exist"
**Solution**: Create the database first
```bash
psql -U postgres -c "CREATE DATABASE user_db;"
psql -U postgres -c "CREATE DATABASE event_db;"
psql -U postgres -c "CREATE DATABASE ticketing_db;"
```

#### 4. "FATAL: role 'postgres' does not exist"
**Solution**: Create postgres user
```bash
# Linux/Mac
sudo -u postgres createuser --interactive

# Windows
createuser -U postgres
```

### Verification Commands

After setup, verify everything works:

```bash
# Check PostgreSQL version
psql --version

# Check if databases exist
psql -U postgres -c "\l"

# Check tables in user_db
psql -U postgres -d user_db -c "\dt"

# Check if default users exist
psql -U postgres -d user_db -c "SELECT username, email FROM users;"
```

## Next Steps

After successful database setup:

1. **Start the services** using the provided scripts:
   - Windows: `start-local.bat`
   - Linux/Mac: `./start-local.sh`

2. **Verify services** are running:
   - Eureka Server: http://localhost:8761
   - API Gateway: http://localhost:8080
   - User Service: http://localhost:8081
   - Event Service: http://localhost:8082
   - Ticketing Service: http://localhost:8083
   - Notification Service: http://localhost:8084

3. **Test the APIs** using the provided Postman collection or Swagger UI
