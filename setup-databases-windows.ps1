# Database Setup for Windows (PowerShell)
# Run with: powershell -ExecutionPolicy Bypass -File setup-databases-windows.ps1

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Database Setup for Windows (PowerShell)" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "Checking for PostgreSQL installation..." -ForegroundColor Yellow

# Try to find PostgreSQL installation
$pgPaths = @(
    "C:\Program Files\PostgreSQL\19\bin\psql.exe",
    "C:\Program Files\PostgreSQL\18\bin\psql.exe",
    "C:\Program Files\PostgreSQL\17\bin\psql.exe",
    "C:\Program Files\PostgreSQL\16\bin\psql.exe",
    "C:\Program Files\PostgreSQL\15\bin\psql.exe",
    "C:\Program Files\PostgreSQL\14\bin\psql.exe",
    "C:\Program Files\PostgreSQL\13\bin\psql.exe",
    "C:\Program Files\PostgreSQL\12\bin\psql.exe",
    "D:\Program Files\PostgreSQL\19\bin\psql.exe",
    "D:\Program Files\PostgreSQL\18\bin\psql.exe",
    "D:\Program Files\PostgreSQL\17\bin\psql.exe",
    "D:\Program Files\PostgreSQL\16\bin\psql.exe",
    "D:\Program Files\PostgreSQL\15\bin\psql.exe",
    "D:\Program Files\PostgreSQL\14\bin\psql.exe",
    "D:\Program Files\PostgreSQL\13\bin\psql.exe",
    "D:\Program Files\PostgreSQL\12\bin\psql.exe"
)

$pgPath = $null
foreach ($path in $pgPaths) {
    if (Test-Path $path) {
        $pgPath = $path
        break
    }
}

if (-not $pgPath) {
    Write-Host "ERROR: PostgreSQL not found in standard locations!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install PostgreSQL from: https://www.postgresql.org/download/windows/" -ForegroundColor Yellow
    Write-Host "Or add PostgreSQL bin directory to your PATH environment variable." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Common PostgreSQL installation paths:" -ForegroundColor Yellow
    Write-Host "- C:\Program Files\PostgreSQL\19\bin\" -ForegroundColor Gray
    Write-Host "- C:\Program Files\PostgreSQL\18\bin\" -ForegroundColor Gray
    Write-Host "- C:\Program Files\PostgreSQL\17\bin\" -ForegroundColor Gray
    Write-Host "- C:\Program Files\PostgreSQL\16\bin\" -ForegroundColor Gray
    Write-Host "- C:\Program Files\PostgreSQL\15\bin\" -ForegroundColor Gray
    Write-Host "- C:\Program Files\PostgreSQL\14\bin\" -ForegroundColor Gray
    Write-Host "- C:\Program Files\PostgreSQL\13\bin\" -ForegroundColor Gray
    Write-Host "- C:\Program Files\PostgreSQL\12\bin\" -ForegroundColor Gray
    Write-Host "- D:\Program Files\PostgreSQL\19\bin\" -ForegroundColor Gray
    Write-Host "- D:\Program Files\PostgreSQL\18\bin\" -ForegroundColor Gray
    Write-Host "- D:\Program Files\PostgreSQL\17\bin\" -ForegroundColor Gray
    Write-Host "- D:\Program Files\PostgreSQL\16\bin\" -ForegroundColor Gray
    Write-Host "- D:\Program Files\PostgreSQL\15\bin\" -ForegroundColor Gray
    Write-Host "- D:\Program Files\PostgreSQL\14\bin\" -ForegroundColor Gray
    Write-Host "- D:\Program Files\PostgreSQL\13\bin\" -ForegroundColor Gray
    Write-Host "- D:\Program Files\PostgreSQL\12\bin\" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Alternative: Use Chocolatey to install PostgreSQL:" -ForegroundColor Yellow
    Write-Host "choco install postgresql" -ForegroundColor Gray
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Found PostgreSQL at: $pgPath" -ForegroundColor Green

Write-Host ""
Write-Host "Testing PostgreSQL connection..." -ForegroundColor Yellow

# Test connection
try {
    $result = & $pgPath -U postgres -c "SELECT version();" 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Connection failed"
    }
    Write-Host "PostgreSQL connection successful!" -ForegroundColor Green
} catch {
    Write-Host "WARNING: Cannot connect to PostgreSQL with default settings!" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "This might be because:" -ForegroundColor Yellow
    Write-Host "1. PostgreSQL service is not running" -ForegroundColor Gray
    Write-Host "2. User 'postgres' doesn't exist or has different password" -ForegroundColor Gray
    Write-Host "3. PostgreSQL is configured differently" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Attempting to continue with database setup anyway..." -ForegroundColor Yellow
    Write-Host "If this fails, please check your PostgreSQL configuration." -ForegroundColor Yellow
    Write-Host ""
}

Write-Host ""
Write-Host "Setting up databases..." -ForegroundColor Yellow
Write-Host "This will create: user_db, event_db, ticketing_db" -ForegroundColor Yellow
Write-Host ""

# Check if setup script exists
if (-not (Test-Path "local-database-setup.sql")) {
    Write-Host "ERROR: local-database-setup.sql not found!" -ForegroundColor Red
    Write-Host "Please make sure you're running this script from the project root directory." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Run the database setup script
Write-Host "Running database setup script..." -ForegroundColor Yellow
try {
    & $pgPath -U postgres -f local-database-setup.sql
    if ($LASTEXITCODE -ne 0) {
        throw "Database setup failed"
    }
    
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "Database setup completed successfully!" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Created databases:" -ForegroundColor Yellow
    Write-Host "- user_db (User management)" -ForegroundColor Gray
    Write-Host "- event_db (Event management)" -ForegroundColor Gray
    Write-Host "- ticketing_db (Ticketing system)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Default users created:" -ForegroundColor Yellow
    Write-Host "- mehdi-jv (ADMIN)" -ForegroundColor Gray
    Write-Host "- rasoul-nb (EVENT_MANAGER)" -ForegroundColor Gray
    Write-Host "- mahdi-mst (USER)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "You can now start the services using:" -ForegroundColor Yellow
    Write-Host "- start-local.bat (Windows)" -ForegroundColor Gray
    Write-Host "- ./start-local.sh (Linux/Mac)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host ""
    Write-Host "ERROR: Database setup failed!" -ForegroundColor Red
    Write-Host ""
    Write-Host "This could be because:" -ForegroundColor Yellow
    Write-Host "1. PostgreSQL is not running" -ForegroundColor Gray
    Write-Host "2. User 'postgres' doesn't exist or has wrong password" -ForegroundColor Gray
    Write-Host "3. Permission issues" -ForegroundColor Gray
    Write-Host "4. Database already exists (this is usually OK)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Please check the error messages above for details." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "You can try:" -ForegroundColor Yellow
    Write-Host "1. Start PostgreSQL service: net start postgresql" -ForegroundColor Gray
    Write-Host "2. Check if postgres user exists and has correct password" -ForegroundColor Gray
    Write-Host "3. Run the script again (some errors are non-critical)" -ForegroundColor Gray
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Read-Host "Press Enter to exit"
