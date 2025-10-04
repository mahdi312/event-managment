#!/bin/bash

echo "=========================================="
echo "Database Setup for Linux/Mac"
echo "=========================================="

echo ""
echo "Checking for PostgreSQL installation..."

# Check if psql is available
if ! command -v psql &> /dev/null; then
    echo "ERROR: psql command not found!"
    echo ""
    echo "Please install PostgreSQL:"
    echo ""
    echo "Ubuntu/Debian:"
    echo "  sudo apt update"
    echo "  sudo apt install postgresql postgresql-contrib"
    echo ""
    echo "CentOS/RHEL/Fedora:"
    echo "  sudo yum install postgresql postgresql-server"
    echo "  # or for newer versions:"
    echo "  sudo dnf install postgresql postgresql-server"
    echo ""
    echo "macOS (with Homebrew):"
    echo "  brew install postgresql"
    echo "  brew services start postgresql"
    echo ""
    echo "After installation, make sure PostgreSQL service is running:"
    echo "  sudo systemctl start postgresql"
    echo "  sudo systemctl enable postgresql"
    echo ""
    exit 1
fi

echo "Found psql command"

# Check PostgreSQL version
PG_VERSION=$(psql --version | grep -oE '[0-9]+\.[0-9]+' | head -1)
echo "PostgreSQL version: $PG_VERSION"

echo ""
echo "Testing PostgreSQL connection..."

# Test connection
if ! psql -U postgres -c "SELECT version();" > /dev/null 2>&1; then
    echo "WARNING: Cannot connect to PostgreSQL with default settings!"
    echo ""
    echo "This might be because:"
    echo "1. PostgreSQL service is not running"
    echo "2. User 'postgres' doesn't exist or has different password"
    echo "3. PostgreSQL is configured differently"
    echo ""
    echo "Attempting to continue with database setup anyway..."
    echo "If this fails, please check your PostgreSQL configuration."
    echo ""
    echo "To troubleshoot:"
    echo "1. Start PostgreSQL service:"
    echo "   sudo systemctl start postgresql"
    echo "   # or on macOS:"
    echo "   brew services start postgresql"
    echo ""
    echo "2. Create postgres user (if needed):"
    echo "   sudo -u postgres createuser --interactive"
    echo ""
    echo "3. Set postgres password:"
    echo "   sudo -u postgres psql -c \"ALTER USER postgres PASSWORD 'password';\""
    echo ""
else
    echo "PostgreSQL connection successful!"
fi

echo ""
echo "Setting up databases..."
echo "This will create: user_db, event_db, ticketing_db"
echo ""

# Run the database setup script
echo "Running database setup script..."
if psql -U postgres -f local-database-setup.sql; then
    echo ""
    echo "=========================================="
    echo "Database setup completed successfully!"
    echo "=========================================="
    echo ""
    echo "Created databases:"
    echo "- user_db (User management)"
    echo "- event_db (Event management)"
    echo "- ticketing_db (Ticketing system)"
    echo ""
    echo "Default users created:"
    echo "- mehdi-jv (ADMIN)"
    echo "- rasoul-nb (EVENT_MANAGER)"
    echo "- mahdi-mst (USER)"
    echo ""
    echo "You can now start the services using:"
    echo "- ./start-local.sh (Linux/Mac)"
    echo "- start-local.bat (Windows)"
    echo ""
else
    echo ""
    echo "ERROR: Database setup failed!"
    echo ""
    echo "This could be because:"
    echo "1. PostgreSQL is not running"
    echo "2. User 'postgres' doesn't exist or has wrong password"
    echo "3. Permission issues"
    echo "4. Database already exists (this is usually OK)"
    echo ""
    echo "Please check the error messages above for details."
    echo ""
    echo "You can try:"
    echo "1. Start PostgreSQL service: sudo systemctl start postgresql"
    echo "2. Check if postgres user exists and has correct password"
    echo "3. Run the script again (some errors are non-critical)"
    echo ""
    exit 1
fi
