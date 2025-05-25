#!/bin/bash

# Test script for Docker Windows installer setup
# This script validates that Docker and the build environment are working correctly

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo_info() {
    echo -e "${BLUE}[INFO] $1${NC}"
}

echo_success() {
    echo -e "${GREEN}[SUCCESS] $1${NC}"
}

echo_warning() {
    echo -e "${YELLOW}[WARNING] $1${NC}"
}

echo_error() {
    echo -e "${RED}[ERROR] $1${NC}"
}

print_header() {
    echo "=================================================================="
    echo "  Black and Pink Inventory Management"
    echo "  Docker Environment Test"
    echo "=================================================================="
    echo ""
}

test_docker() {
    echo_info "Testing Docker installation..."
    
    if ! command -v docker &> /dev/null; then
        echo_error "Docker is not installed or not in PATH"
        return 1
    fi
    
    if ! docker info &> /dev/null; then
        echo_error "Docker daemon is not running"
        return 1
    fi
    
    echo_success "Docker is installed and running"
    echo_info "Docker version: $(docker --version)"
    
    # Test Docker Compose
    if command -v docker-compose &> /dev/null; then
        echo_info "Docker Compose version: $(docker-compose --version)"
    else
        echo_warning "Docker Compose not found as separate command (using docker compose)"
    fi
    
    return 0
}

test_project_files() {
    echo_info "Testing project files..."
    
    local required_files=(
        "build.gradle.kts"
        "windows-installer.nsi"
        "windows-installer.iss"
        "Dockerfile.windows-installer"
        "docker-compose.yml"
        "build-windows-installer.sh"
    )
    
    local missing_files=()
    
    for file in "${required_files[@]}"; do
        if [ -f "$file" ]; then
            echo_success "Found: $file"
        else
            echo_error "Missing: $file"
            missing_files+=("$file")
        fi
    done
    
    if [ ${#missing_files[@]} -gt 0 ]; then
        echo_error "Missing required files: ${missing_files[*]}"
        return 1
    fi
    
    echo_success "All required project files found"
    return 0
}

test_docker_images() {
    echo_info "Testing Docker image build (dry run)..."
    
    # Test if the Wine Dockerfile can be parsed
    if docker build -f Dockerfile.windows-installer --target ubuntu -t blackandpink-test . &> /dev/null; then
        echo_success "Wine Dockerfile syntax is valid"
        docker rmi blackandpink-test &> /dev/null || true
    else
        echo_error "Wine Dockerfile has syntax errors"
        return 1
    fi
    
    return 0
}

test_gradle() {
    echo_info "Testing Gradle wrapper..."
    
    if [ -f "./gradlew" ]; then
        chmod +x ./gradlew
        echo_success "Gradle wrapper found and made executable"
        
        # Test if Gradle can parse the build file
        if ./gradlew tasks --dry-run &> /dev/null; then
            echo_success "Gradle build file is valid"
        else
            echo_warning "Gradle build file may have issues"
        fi
    else
        echo_error "Gradle wrapper not found"
        return 1
    fi
    
    return 0
}

test_scripts() {
    echo_info "Testing build scripts..."
    
    if [ -x "./build-windows-installer.sh" ]; then
        echo_success "build-windows-installer.sh is executable"
    else
        echo_warning "build-windows-installer.sh is not executable (fixing...)"
        chmod +x ./build-windows-installer.sh
        echo_success "Fixed executable permissions"
    fi
    
    # Test script help function
    if ./build-windows-installer.sh --help &> /dev/null; then
        echo_success "Build script help function works"
    else
        echo_warning "Build script help function may have issues"
    fi
    
    return 0
}

test_resources() {
    echo_info "Testing application resources..."
    
    if [ -d "src/jvmMain/resources" ]; then
        echo_success "Resources directory found"
        
        if [ -f "src/jvmMain/resources/icon.ico" ]; then
            echo_success "Application icon found"
        else
            echo_warning "Application icon not found (installer may have issues)"
        fi
    else
        echo_warning "Resources directory not found"
    fi
    
    return 0
}

run_quick_build_test() {
    echo_info "Running quick build test..."
    
    # Test basic Gradle build
    echo_info "Testing Gradle build (clean + dependencies only)..."
    if ./gradlew clean dependencies --dry-run &> /dev/null; then
        echo_success "Gradle dependencies can be resolved"
    else
        echo_warning "Gradle dependencies may have issues"
    fi
    
    return 0
}

show_system_info() {
    echo ""
    echo "=================================================================="
    echo "  System Information"
    echo "=================================================================="
    
    echo_info "Operating System: $(uname -s)"
    echo_info "Architecture: $(uname -m)"
    echo_info "Available RAM: $(free -h 2>/dev/null | grep '^Mem:' | awk '{print $2}' || echo 'Unknown')"
    echo_info "Available Disk Space: $(df -h . | tail -1 | awk '{print $4}')"
    
    if command -v docker &> /dev/null; then
        echo_info "Docker Info:"
        docker system df 2>/dev/null || echo "  Docker system info unavailable"
    fi
}

main() {
    print_header
    
    local failed_tests=0
    
    test_docker || ((failed_tests++))
    echo ""
    
    test_project_files || ((failed_tests++))
    echo ""
    
    test_gradle || ((failed_tests++))
    echo ""
    
    test_scripts || ((failed_tests++))
    echo ""
    
    test_resources || ((failed_tests++))
    echo ""
    
    test_docker_images || ((failed_tests++))
    echo ""
    
    run_quick_build_test || ((failed_tests++))
    echo ""
    
    show_system_info
    echo ""
    
    echo "=================================================================="
    echo "  Test Results"
    echo "=================================================================="
    
    if [ $failed_tests -eq 0 ]; then
        echo_success "All tests passed! Your environment is ready for Docker builds."
        echo ""
        echo_info "Next steps:"
        echo "  1. Run: ./build-windows-installer.sh --wine"
        echo "  2. Or use: make installer"
        echo "  3. Wait for the build to complete"
        echo "  4. Find your installer in the dist/ directory"
    else
        echo_error "$failed_tests tests failed. Please fix the issues above."
        echo ""
        echo_info "Common solutions:"
        echo "  - Install Docker Desktop and ensure it's running"
        echo "  - Make sure you're in the correct project directory"
        echo "  - Check that all required files are present"
        echo "  - Verify your internet connection for downloading dependencies"
    fi
    
    echo ""
}

# Run the main function
main "$@"
