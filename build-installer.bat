@echo off
REM Build script for Windows installer using NSIS
REM Run this script on a Windows machine with NSIS installed

echo ==================================================
echo Black and Pink Inventory Management
echo Windows Installer Build Script
echo ==================================================

echo.
echo Checking for NSIS installation...
where makensis >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: NSIS is not installed or not in PATH
    echo Please install NSIS from: https://nsis.sourceforge.io/Download
    echo Make sure to add NSIS to your system PATH
    pause
    exit /b 1
)

echo NSIS found!
echo.

echo Checking for required files...

if not exist "windows-installer.nsi" (
    echo ERROR: windows-installer.nsi not found
    echo Make sure you're running this script from the project root directory
    pause
    exit /b 1
)

if not exist "LICENSE.txt" (
    echo ERROR: LICENSE.txt not found
    pause
    exit /b 1
)

if not exist "README.md" (
    echo ERROR: README.md not found
    pause
    exit /b 1
)

if not exist "src\jvmMain\resources\icon.ico" (
    echo ERROR: icon.ico not found at src\jvmMain\resources\icon.ico
    pause
    exit /b 1
)

if not exist "build\compose\binaries\main\app\BlackAndPink" (
    echo ERROR: Application binaries not found
    echo Please run: ./gradlew createDistributable first
    pause
    exit /b 1
)

echo All required files found!
echo.

echo Creating dist directory...
if not exist "dist" mkdir dist

echo.
echo Compiling NSIS installer...
echo Command: makensis windows-installer.nsi

makensis windows-installer.nsi

if %ERRORLEVEL% equ 0 (
    echo.
    echo ==================================================
    echo SUCCESS: Installer created successfully!
    echo Location: dist\BlackAndPink-Setup-1.0.0.exe
    echo ==================================================
    echo.
    echo You can now distribute this installer to end users.
    echo.
) else (
    echo.
    echo ==================================================
    echo ERROR: Failed to create installer
    echo Check the output above for error details
    echo ==================================================
    echo.
)

pause
