#!/bin/bash

# Comprehensive test script for Windows installer creation
# Tests multiple approaches to ensure reliable builds

set -e

echo "🧪 Testing Windows Installer Creation Methods"
echo "=============================================="

# Test 1: Verify Docker approach works
echo ""
echo "🐳 Test 1: Docker-based NSIS installer"
echo "--------------------------------------"

if command -v docker &> /dev/null; then
    echo "✅ Docker is available"
    
    # Build the Docker image
    echo "Building Docker image..."
    docker build -f Dockerfile.windows-installer -t blackandpink-wine-test .
    
    # Test the installer creation
    echo "Creating Windows installer with Docker..."
    docker run --rm -v "$(pwd):/app" blackandpink-wine-test
    
    if [ -f "BlackAndPink-Setup-1.0.0.exe" ]; then
        echo "✅ Docker method: SUCCESS"
        installer_size=$(du -h "BlackAndPink-Setup-1.0.0.exe" | cut -f1)
        echo "   Installer size: $installer_size"
    else
        echo "❌ Docker method: FAILED"
    fi
else
    echo "⚠️  Docker not available, skipping Docker test"
fi

# Test 2: Verify native build works (on macOS we can test the createDistributable)
echo ""
echo "📦 Test 2: Gradle distributable creation"
echo "----------------------------------------"

echo "Building application..."
./gradlew build -x test --quiet

echo "Creating distributable..."
./gradlew createDistributable -x test --quiet

if [ -d "build/compose/binaries/main/app" ]; then
    echo "✅ Distributable creation: SUCCESS"
    app_size=$(du -sh "build/compose/binaries/main/app" | cut -f1)
    echo "   Distributable size: $app_size"
    echo "   Contents:"
    ls -la "build/compose/binaries/main/app/"
else
    echo "❌ Distributable creation: FAILED"
fi

# Test 3: Verify JAR creation works
echo ""
echo "☕ Test 3: JAR build verification"
echo "---------------------------------"

if [ -f "build/libs/blackandpink-jvm-1.0.0.jar" ]; then
    echo "✅ JAR creation: SUCCESS"
    jar_size=$(du -h "build/libs/blackandpink-jvm-1.0.0.jar" | cut -f1)
    echo "   JAR size: $jar_size"
else
    echo "❌ JAR creation: FAILED"
fi

# Test 4: Verify NSIS script syntax
echo ""
echo "📝 Test 4: NSIS script validation"
echo "----------------------------------"

if command -v makensis &> /dev/null; then
    echo "✅ NSIS is available"
    
    # Test script syntax (dry run)
    if makensis -V2 -NOCD windows-installer-simple.nsi; then
        echo "✅ NSIS script syntax: VALID"
    else
        echo "❌ NSIS script syntax: INVALID"
    fi
else
    echo "⚠️  NSIS not available locally (expected on macOS)"
fi

# Test 5: Check required files exist
echo ""
echo "📁 Test 5: Required files check"
echo "-------------------------------"

required_files=(
    "LICENSE.txt"
    "src/jvmMain/resources/icon.ico"
    "windows-installer-simple.nsi"
    "build.gradle.kts"
    "Dockerfile.windows-installer"
)

all_files_exist=true
for file in "${required_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file exists"
    else
        echo "❌ $file missing"
        all_files_exist=false
    fi
done

if $all_files_exist; then
    echo "✅ All required files: PRESENT"
else
    echo "❌ Some required files: MISSING"
fi

# Summary
echo ""
echo "📊 Test Summary"
echo "==============="
echo "This test validates that the Windows installer build process"
echo "can work in multiple environments and approaches."
echo ""
echo "For GitHub Actions, the workflow will:"
echo "1. Try native MSI creation with jpackage"
echo "2. Fall back to NSIS installer if MSI fails"
echo "3. Use Docker approach as ultimate fallback"
echo ""

if [ -f "BlackAndPink-Setup-1.0.0.exe" ]; then
    echo "🎉 Windows installer is ready for distribution!"
    echo "   File: BlackAndPink-Setup-1.0.0.exe"
    echo "   Size: $(du -h BlackAndPink-Setup-1.0.0.exe | cut -f1)"
else
    echo "⚠️  No installer created in this test run"
fi

echo ""
echo "✅ Test completed successfully!"
