@echo off
echo ==========================================
echo Database Setup for Windows
echo ==========================================

echo.
echo Checking for PostgreSQL installation...

REM Try to find PostgreSQL installation
set PG_PATH=""
if exist "C:\Program Files\PostgreSQL\15\bin\psql.exe" (
    set PG_PATH="C:\Program Files\PostgreSQL\15\bin\psql.exe"
) else if exist "C:\Program Files\PostgreSQL\14\bin\psql.exe" (
    set PG_PATH="C:\Program Files\PostgreSQL\14\bin\psql.exe"
) else if exist "C:\Program Files\PostgreSQL\13\bin\psql.exe" (
    set PG_PATH="C:\Program Files\PostgreSQL\13\bin\psql.exe"
) else if exist "C:\Program Files\PostgreSQL\12\bin\psql.exe" (
    set PG_PATH="C:\Program Files\PostgreSQL\12\bin\psql.exe"
) else if exist "C:\Program Files\PostgreSQL\19\bin\psql.exe" (
    set PG_PATH="C:\Program Files\PostgreSQL\19\bin\psql.exe"
) else if exist "C:\Program Files\PostgreSQL\18\bin\psql.exe" (
    set PG_PATH="C:\Program Files\PostgreSQL\18\bin\psql.exe"
) else if exist "C:\Program Files\PostgreSQL\17\bin\psql.exe" (
    set PG_PATH="C:\Program Files\PostgreSQL\17\bin\psql.exe"
) else if exist "C:\Program Files\PostgreSQL\16\bin\psql.exe" (
    set PG_PATH="C:\Program Files\PostgreSQL\16\bin\psql.exe" 
) else if exist "D:\Program Files\PostgreSQL\19\bin\psql.exe" (
    set PG_PATH="D:\Program Files\PostgreSQL\19\bin\psql.exe"
) else if exist "D:\Program Files\PostgreSQL\18\bin\psql.exe" (
    set PG_PATH="D:\Program Files\PostgreSQL\18\bin\psql.exe"
) else if exist "D:\Program Files\PostgreSQL\17\bin\psql.exe" (
    set PG_PATH="D:\Program Files\PostgreSQL\17\bin\psql.exe"
) else if exist "D:\Program Files\PostgreSQL\16\bin\psql.exe" (
    set PG_PATH="D:\Program Files\PostgreSQL\16\bin\psql.exe"
) else if exist "D:\Program Files\PostgreSQL\15\bin\psql.exe" (
    set PG_PATH="D:\Program Files\PostgreSQL\15\bin\psql.exe"
) else if exist "D:\Program Files\PostgreSQL\14\bin\psql.exe" (
    set PG_PATH="D:\Program Files\PostgreSQL\14\bin\psql.exe"
) else if exist "D:\Program Files\PostgreSQL\13\bin\psql.exe" (
    set PG_PATH="D:\Program Files\PostgreSQL\13\bin\psql.exe"
) else if exist "D:\Program Files\PostgreSQL\12\bin\psql.exe" (
    set PG_PATH="D:\Program Files\PostgreSQL\12\bin\psql.exe"
) else (
    echo ERROR: PostgreSQL not found in standard locations!
    echo.
    echo Please install PostgreSQL from: https://www.postgresql.org/download/windows/
    echo Or add PostgreSQL bin directory to your PATH environment variable.
    echo.
    echo Common PostgreSQL installation paths:
    echo - C:\Program Files\PostgreSQL\19\bin\
    echo - C:\Program Files\PostgreSQL\18\bin\
    echo - C:\Program Files\PostgreSQL\17\bin\
    echo - C:\Program Files\PostgreSQL\16\bin\
    echo - C:\Program Files\PostgreSQL\15\bin\
    echo - C:\Program Files\PostgreSQL\14\bin\
    echo - C:\Program Files\PostgreSQL\13\bin\
    echo - C:\Program Files\PostgreSQL\12\bin\
    echo - D:\Program Files\PostgreSQL\19\bin\
    echo - D:\Program Files\PostgreSQL\18\bin\
    echo - D:\Program Files\PostgreSQL\17\bin\
    echo - D:\Program Files\PostgreSQL\16\bin\
    echo - D:\Program Files\PostgreSQL\15\bin\
    echo - D:\Program Files\PostgreSQL\14\bin\
    echo - D:\Program Files\PostgreSQL\13\bin\
    echo - D:\Program Files\PostgreSQL\12\bin\
    echo.
    pause
    exit /b 1
)

echo Found PostgreSQL at: %PG_PATH%

echo.
echo Testing PostgreSQL connection...
%PG_PATH% -U postgres -c "SELECT version();" > nul 2>&1
if errorlevel 1 (
    echo WARNING: Cannot connect to PostgreSQL with default settings!
    echo.
    echo This might be because:
    echo 1. PostgreSQL service is not running
    echo 2. User 'postgres' doesn't exist or has different password
    echo 3. PostgreSQL is configured differently
    echo.
    echo Attempting to continue with database setup anyway...
    echo If this fails, please check your PostgreSQL configuration.
    echo.
) else (
    echo PostgreSQL connection successful!
)

echo.
echo Setting up databases...
echo This will create: user_db, event_db, ticketing_db
echo.

echo Running database setup script...
%PG_PATH% -U postgres -f local-database-setup.sql

if errorlevel 1 (
    echo.
    echo ERROR: Database setup failed!
    echo.
    echo This could be because:
    echo 1. PostgreSQL is not running
    echo 2. User 'postgres' doesn't exist or has wrong password
    echo 3. Permission issues
    echo 4. Database already exists (this is usually OK)
    echo.
    echo Please check the error messages above for details.
    echo.
    echo You can try:
    echo 1. Start PostgreSQL service: net start postgresql
    echo 2. Check if postgres user exists and has correct password
    echo 3. Run the script again (some errors are non-critical)
    echo.
    pause
    exit /b 1
)

echo.
echo ==========================================
echo Database setup completed successfully!
echo ==========================================
echo.
echo Created databases:
echo - user_db (User management)
echo - event_db (Event management)  
echo - ticketing_db (Ticketing system)
echo.
echo Default users created:
echo - mehdi-jv (ADMIN)
echo - rasoul-nb (EVENT_MANAGER)
echo - mahdi-mst (USER)
echo.
echo You can now start the services using:
echo - start-local.bat (Windows)
echo - ./start-local.sh (Linux/Mac)
echo.
pause
