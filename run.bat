@echo off
REM Script to launch the Black and Pink Inventory Management Application
REM This script should be placed in the project root directory

echo =====================================================
echo   Black and Pink Inventory Management Application
echo =====================================================

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    exit /b 1
)

REM Check Java version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    echo Found Java version: %%g
)

REM Check if the MSI file exists
set MSI_FILE=%~dp0build\compose\binaries\main\msi\BlackAndPink-1.0.0.msi
if exist "%MSI_FILE%" (
    echo MSI installer found at: %MSI_FILE%
    echo For a better experience, install the application from the MSI.
    echo Or you can continue with the development version.
    set /p open_msi=Open MSI installer? (y/n): 
    if /I "%open_msi%" == "y" (
        start "" "%MSI_FILE%"
        exit /b 0
    )
)

REM Run the application
echo Starting Black and Pink Inventory Management...
cd /d "%~dp0" && gradlew.bat run
pause
