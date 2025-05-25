#!/bin/bash
set -e

echo "=================================================="
echo "Black and Pink Windows Installer Builder (Docker)"
echo "=================================================="

# Check if project files exist
if [ ! -f "build.gradle.kts" ]; then
    echo "ERROR: This doesn't appear to be the Black and Pink project directory"
    echo "Make sure you've mounted the project directory to /workspace"
    exit 1
fi

echo "Building Kotlin/Compose application..."
chmod +x gradlew
./gradlew build -x test

echo "Creating distributable..."
./gradlew createDistributable -x test

echo "Verifying files..."
if [ ! -d "build/compose/binaries/main/app/BlackAndPink" ]; then
    echo "ERROR: Application binaries not found"
    exit 1
fi

echo "Creating output directory..."
mkdir -p dist

echo "Building NSIS installer..."
if [ -f "windows-installer.nsi" ]; then
    # Convert paths for Wine
    export WINEPATH="C:/Program Files (x86)/NSIS"
    xvfb-run -a wine "$HOME/.wine/drive_c/Program Files (x86)/NSIS/makensis.exe" windows-installer.nsi
    echo "✓ NSIS installer created: dist/BlackAndPink-Setup-1.0.0.exe"
else
    echo "⚠ windows-installer.nsi not found, skipping NSIS build"
fi

echo "Building Inno Setup installer..."
if [ -f "windows-installer.iss" ]; then
    # Convert to Windows-style path
    xvfb-run -a wine "$HOME/.wine/drive_c/Program Files (x86)/Inno Setup 6/ISCC.exe" windows-installer.iss
    echo "✓ Inno Setup installer created: dist/BlackAndPink-Setup-1.0.0.exe"
else
    echo "⚠ windows-installer.iss not found, skipping Inno Setup build"
fi

echo ""
echo "=================================================="
echo "✅ SUCCESS: Windows installers built successfully!"
echo "=================================================="

ls -la dist/
echo ""
echo "Installers are available in the ./dist/ directory"
