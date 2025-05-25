#!/bin/bash
# Docker-based Windows Installer Builder for Black and Pink Inventory Management
# Creates Windows installer using Docker + Wine + NSIS
# Compatible with macOS, Linux, and Windows (via WSL)

set -e

# Configuration
DOCKER_IMAGE="blackandpink-wine-test"
APP_NAME="Black and Pink Inventory Management"
APP_VERSION="1.0.0"
INSTALLER_NAME="BlackAndPink-Setup-${APP_VERSION}.exe"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is available
check_docker() {
    print_status "Checking Docker availability..."
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed or not in PATH"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker daemon is not running"
        exit 1
    fi
    
    print_success "Docker is available"
}

# Function to check if Docker image exists
check_docker_image() {
    print_status "Checking Docker image: $DOCKER_IMAGE"
    if ! docker image inspect $DOCKER_IMAGE &> /dev/null; then
        print_warning "Docker image $DOCKER_IMAGE not found"
        print_status "Building Docker image..."
        
        if [ ! -f "Dockerfile.windows-installer" ]; then
            print_error "Dockerfile.windows-installer not found"
            exit 1
        fi
        
        docker build -f Dockerfile.windows-installer -t $DOCKER_IMAGE .
        print_success "Docker image built successfully"
    else
        print_success "Docker image $DOCKER_IMAGE found"
    fi
}

# Function to clean previous builds
clean_build() {
    print_status "Cleaning previous builds..."
    rm -f $INSTALLER_NAME
    rm -rf build/windows-dist/
    print_success "Cleaned previous builds"
}

# Function to build the application
build_application() {
    print_status "Building application JAR files..."
    docker run --rm -v "$(pwd)":/app -w /app $DOCKER_IMAGE /bin/bash -c "
        echo 'Building JAR files with Gradle...'
        ./gradlew distJar --no-daemon --console=plain
        echo 'JAR build completed'
    "
    print_success "Application built successfully"
}

# Function to create Windows distributable
create_windows_dist() {
    print_status "Creating Windows distributable structure..."
    docker run --rm -v "$(pwd)":/app -w /app $DOCKER_IMAGE /bin/bash -c "
        # Create Windows distribution directory
        mkdir -p build/windows-dist/BlackAndPink/
        
        # Copy JAR files
        cp build/libs/*.jar build/windows-dist/BlackAndPink/
        
        # Create Windows batch launcher
        cat > build/windows-dist/BlackAndPink/BlackAndPink.bat << 'EOF'
@echo off
setlocal enabledelayedexpansion

echo Starting Black and Pink Inventory Management...

REM Get the directory where this script is located
set \"SCRIPT_DIR=%~dp0\"

REM Find Java installation
set \"JAVA_CMD=java\"
if defined JAVA_HOME (
    set \"JAVA_CMD=%JAVA_HOME%\\bin\\java\"
)

REM Check if Java is available
\"%JAVA_CMD%\" -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java Runtime Environment not found.
    echo Please install Java 17 or later and try again.
    pause
    exit /b 1
)

REM Launch the application
echo Launching application...
cd /d \"%SCRIPT_DIR%\"
\"%JAVA_CMD%\" -jar blackandpink-jvm-1.0.0.jar

REM Keep window open if there's an error
if errorlevel 1 (
    echo Application exited with an error.
    pause
)
EOF

        # Create basic icon placeholder
        echo 'icon placeholder' > build/windows-dist/BlackAndPink/icon.ico
        
        echo 'Windows distributable created successfully'
        ls -la build/windows-dist/BlackAndPink/
    "
    print_success "Windows distributable created"
}

# Function to build NSIS installer
build_nsis_installer() {
    print_status "Building NSIS installer..."
    docker run --rm -v "$(pwd)":/app -w /app $DOCKER_IMAGE /bin/bash -c "
        echo 'Building installer with native Linux NSIS...'
        makensis -V2 windows-installer-simple.nsi
        
        if [ -f 'BlackAndPink-Setup-1.0.0.exe' ]; then
            echo 'Installer created successfully:'
            ls -lh BlackAndPink-Setup-1.0.0.exe
        else
            echo 'ERROR: Installer creation failed'
            exit 1
        fi
    "
    
    if [ -f "$INSTALLER_NAME" ]; then
        print_success "Windows installer created: $INSTALLER_NAME"
        print_status "Installer size: $(du -h $INSTALLER_NAME | cut -f1)"
    else
        print_error "Installer creation failed"
        exit 1
    fi
}

# Function to verify installer
verify_installer() {
    print_status "Verifying installer..."
    
    if [ ! -f "$INSTALLER_NAME" ]; then
        print_error "Installer file not found: $INSTALLER_NAME"
        exit 1
    fi
    
    # Check file size
    size=$(stat -f%z "$INSTALLER_NAME" 2>/dev/null || stat -c%s "$INSTALLER_NAME" 2>/dev/null)
    if [ "$size" -lt 1000000 ]; then  # Less than 1MB
        print_warning "Installer size seems small: $(du -h $INSTALLER_NAME | cut -f1)"
    fi
    
    # Check if it's a PE executable (starts with MZ)
    if command -v xxd &> /dev/null; then
        header=$(xxd -l 2 -p "$INSTALLER_NAME")
        if [ "$header" = "4d5a" ]; then
            print_success "Installer is a valid PE executable"
        else
            print_warning "Installer may not be a valid PE executable"
        fi
    fi
    
    print_success "Installer verification completed"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  -c, --clean    Clean previous builds before building"
    echo "  -b, --build    Build application only (no installer)"
    echo "  -i, --installer Build installer only (assumes app is already built)"
    echo "  -v, --verify   Verify the installer after creation"
    echo ""
    echo "Examples:"
    echo "  $0              # Build everything (app + installer)"
    echo "  $0 --clean     # Clean and build everything"
    echo "  $0 --build     # Build application JAR only"
    echo "  $0 --installer # Build installer only"
}

# Parse command line arguments
CLEAN_BUILD=false
BUILD_ONLY=false
INSTALLER_ONLY=false
VERIFY_INSTALLER=true

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_usage
            exit 0
            ;;
        -c|--clean)
            CLEAN_BUILD=true
            shift
            ;;
        -b|--build)
            BUILD_ONLY=true
            shift
            ;;
        -i|--installer)
            INSTALLER_ONLY=true
            shift
            ;;
        -v|--verify)
            VERIFY_INSTALLER=true
            shift
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Main execution
print_status "Starting Docker-based Windows installer build for $APP_NAME"
print_status "Target installer: $INSTALLER_NAME"

# Check prerequisites
check_docker
check_docker_image

# Clean if requested
if [ "$CLEAN_BUILD" = true ]; then
    clean_build
fi

# Build application if not installer-only
if [ "$INSTALLER_ONLY" = false ]; then
    build_application
    create_windows_dist
fi

# Build installer if not build-only
if [ "$BUILD_ONLY" = false ]; then
    build_nsis_installer
    
    if [ "$VERIFY_INSTALLER" = true ]; then
        verify_installer
    fi
fi

# Final summary
print_success "Build process completed!"

if [ "$BUILD_ONLY" = false ] && [ -f "$INSTALLER_NAME" ]; then
    echo ""
    print_status "Generated installer details:"
    echo "  File: $INSTALLER_NAME"
    echo "  Size: $(du -h $INSTALLER_NAME | cut -f1)"
    echo "  Type: Windows PE executable"
    echo ""
    print_status "To test the installer:"
    echo "  1. Copy $INSTALLER_NAME to a Windows machine"
    echo "  2. Run the installer as Administrator"
    echo "  3. The application will be installed to Program Files"
    echo "  4. Launch from Start Menu or Desktop shortcut"
fi
