#!/bin/bash

# Cross-platform Windows installer builder for macOS
# This script provides multiple approaches to build Windows installers

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo_info() {
    echo -e "${BLUE}INFO: $1${NC}"
}

echo_success() {
    echo -e "${GREEN}SUCCESS: $1${NC}"
}

echo_warning() {
    echo -e "${YELLOW}WARNING: $1${NC}"
}

echo_error() {
    echo -e "${RED}ERROR: $1${NC}"
}

print_header() {
    echo "=================================================================="
    echo "  Black and Pink Inventory Management"
    echo "  Cross-Platform Windows Installer Builder"
    echo "=================================================================="
    echo ""
}

print_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -w, --wine       Build using Wine (Linux container with Windows tools)"
    echo "  -n, --native     Build using native Windows container (requires Windows Docker)"
    echo "  -d, --dev        Build in development container"
    echo "  -c, --clean      Clean build artifacts first"
    echo "  -h, --help       Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 --wine       # Build installer using Wine (recommended for macOS)"
    echo "  $0 --native     # Build installer using native Windows container"
    echo "  $0 --clean      # Clean build and build using Wine"
    echo ""
}

check_requirements() {
    echo_info "Checking requirements..."
    
    # Check if Docker is installed
    if ! command -v docker &> /dev/null; then
        echo_error "Docker is not installed or not in PATH"
        echo "Please install Docker Desktop from: https://www.docker.com/products/docker-desktop"
        exit 1
    fi
    
    # Check if Docker is running
    if ! docker info &> /dev/null; then
        echo_error "Docker daemon is not running"
        echo "Please start Docker Desktop and try again"
        exit 1
    fi
    
    echo_success "Docker is available and running"
}

clean_artifacts() {
    echo_info "Cleaning build artifacts..."
    
    rm -rf build/
    rm -rf dist/
    rm -rf .gradle/caches/
    
    # Clean Docker build cache
    docker system prune -f &> /dev/null || true
    
    echo_success "Build artifacts cleaned"
}

build_with_wine() {
    echo_info "Building Windows installer using Wine..."
    echo_warning "This may take several minutes on first run (downloading dependencies)"
    
    # Create dist directory if it doesn't exist
    mkdir -p dist
    
    # Build the Docker image and run the container
    docker-compose --profile wine build
    docker-compose --profile wine up --force-recreate
    
    # Check if installers were created
    if [ -f "dist/BlackAndPink-Setup-1.0.0.exe" ]; then
        echo_success "Windows installer created successfully!"
        echo_info "Installer location: $(pwd)/dist/BlackAndPink-Setup-1.0.0.exe"
        echo_info "Installer size: $(du -h dist/BlackAndPink-Setup-1.0.0.exe | cut -f1)"
    else
        echo_error "Failed to create Windows installer"
        exit 1
    fi
}

build_with_native() {
    echo_info "Building Windows installer using native Windows container..."
    echo_warning "This requires Docker Desktop with Windows containers enabled"
    
    # Check if Windows containers are available
    if ! docker system info | grep -q "OSType: windows"; then
        echo_error "Windows containers are not available"
        echo "Switch Docker Desktop to Windows containers mode and try again"
        echo "Right-click Docker Desktop → Switch to Windows containers"
        exit 1
    fi
    
    # Create dist directory if it doesn't exist
    mkdir -p dist
    
    # Build and run the Windows container
    docker-compose --profile native build
    docker-compose --profile native up --force-recreate
    
    # Check if installers were created
    if [ -f "dist/BlackAndPink-Setup-1.0.0.exe" ]; then
        echo_success "Windows installer created successfully!"
        echo_info "Installer location: $(pwd)/dist/BlackAndPink-Setup-1.0.0.exe"
        echo_info "Installer size: $(du -h dist/BlackAndPink-Setup-1.0.0.exe | cut -f1)"
    else
        echo_error "Failed to create Windows installer"
        exit 1
    fi
}

build_dev() {
    echo_info "Building application in development container..."
    
    docker-compose --profile dev up --force-recreate
    
    echo_success "Development build completed"
    echo_info "Application bundle: build/compose/binaries/main/app/BlackAndPink/"
}

show_results() {
    echo ""
    echo "=================================================================="
    echo "  Build Results"
    echo "=================================================================="
    
    if [ -d "dist" ] && [ "$(ls -A dist)" ]; then
        echo_info "Generated installers:"
        ls -la dist/
    else
        echo_warning "No installers found in dist/ directory"
    fi
    
    if [ -d "build/compose/binaries/main/app/BlackAndPink" ]; then
        echo_info "Application bundle is available for manual installer creation"
        echo "  Location: build/compose/binaries/main/app/BlackAndPink/"
    fi
    
    echo ""
    echo_info "Next steps:"
    echo "  1. Test the installer on a Windows machine"
    echo "  2. Consider code signing for production distribution"
    echo "  3. Upload to GitHub Releases or your distribution platform"
    echo ""
}

# Main script logic
main() {
    print_header
    
    local build_method=""
    local clean_first=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -w|--wine)
                build_method="wine"
                shift
                ;;
            -n|--native)
                build_method="native"
                shift
                ;;
            -d|--dev)
                build_method="dev"
                shift
                ;;
            -c|--clean)
                clean_first=true
                shift
                ;;
            -h|--help)
                print_usage
                exit 0
                ;;
            *)
                echo_error "Unknown option: $1"
                print_usage
                exit 1
                ;;
        esac
    done
    
    # Default to wine if no method specified
    if [ -z "$build_method" ]; then
        echo_info "No build method specified, defaulting to Wine approach"
        build_method="wine"
    fi
    
    check_requirements
    
    if [ "$clean_first" = true ]; then
        clean_artifacts
    fi
    
    case $build_method in
        wine)
            build_with_wine
            ;;
        native)
            build_with_native
            ;;
        dev)
            build_dev
            ;;
    esac
    
    show_results
}

# Run main function with all arguments
main "$@"
