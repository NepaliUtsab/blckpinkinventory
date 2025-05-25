# Docker-based Windows Installer Creation Guide

This guide shows how to create Windows installers for the Black and Pink Inventory Management application using Docker on macOS, without needing a Windows machine.

## Overview

We provide three Docker-based approaches:

1. **Wine Approach** (Recommended): Uses Wine to run Windows tools in a Linux container
2. **Native Windows**: Uses actual Windows containers (requires Windows Docker mode)
3. **Development**: Just builds the application for manual installer creation

## Quick Start

### Option 1: Automated Script (Recommended)

Use the provided script that handles everything automatically:

```bash
# Build installer using Wine (recommended for macOS)
./build-windows-installer.sh --wine

# Clean build first
./build-windows-installer.sh --clean --wine

# Build using native Windows containers
./build-windows-installer.sh --native

# Development build only
./build-windows-installer.sh --dev
```

### Option 2: Manual Docker Commands

```bash
# Using Wine approach
docker-compose --profile wine build
docker-compose --profile wine up

# Using native Windows approach (requires Windows containers)
docker-compose --profile native build  
docker-compose --profile native up

# Development build only
docker-compose --profile dev up
```

## Requirements

- **Docker Desktop**: Installed and running
- **4GB+ RAM**: For building the application and running Windows tools
- **10GB+ Disk Space**: For Docker images and build artifacts

## Detailed Setup

### 1. Wine Approach (Linux + Wine)

This is the recommended approach for macOS users as it doesn't require Windows containers.

**How it works:**
- Uses Ubuntu 22.04 base image
- Installs Wine to run Windows executables
- Installs NSIS and Inno Setup in Wine
- Builds both NSIS and Inno Setup installers

**Advantages:**
- Works on any platform that supports Docker
- No need for Windows containers
- Builds both MSI and EXE installers

**Disadvantages:**
- Slower initial setup (downloads Windows tools)
- Wine compatibility layers can sometimes have issues

### 2. Native Windows Approach

Uses actual Windows Server Core containers with native Windows tools.

**How it works:**
- Uses Windows Server Core 2022 as base
- Installs native Windows versions of Java, NSIS, and Inno Setup
- Builds installers using native Windows executables

**Advantages:**
- Uses actual Windows tools (no compatibility issues)
- Faster execution once set up
- More reliable for complex installer scripts

**Disadvantages:**
- Requires Docker Desktop with Windows containers enabled
- Only works on Windows hosts or Windows Docker mode
- Larger image sizes

**To enable Windows containers:**
1. Right-click Docker Desktop in system tray
2. Select "Switch to Windows containers..."
3. Wait for Docker to restart

### 3. Development Build

Just builds the Kotlin/Compose application without creating installers.

**Use case:**
- Quick testing of application builds
- Creating application bundles for manual installer creation
- CI/CD pipelines that handle installer creation separately

## Build Process

### What happens during build:

1. **Application Build**: 
   - Compiles Kotlin/Compose application
   - Creates native runtime bundle
   - Packages all dependencies

2. **Installer Creation**:
   - NSIS installer: `dist/BlackAndPink-Setup-1.0.0.exe`
   - Inno Setup installer: `dist/BlackAndPink-Setup-1.0.0.exe`
   - MSI installer: `build/compose/binaries/main/msi/BlackAndPink-1.0.0.msi`

3. **Output**:
   - Professional Windows installers ready for distribution
   - Automatic uninstaller creation
   - Registry entries and file associations
   - Start Menu and Desktop shortcuts

## Troubleshooting

### Common Issues:

#### Docker not running
```bash
Error: Cannot connect to the Docker daemon
Solution: Start Docker Desktop and wait for it to fully initialize
```

#### Wine installation fails
```bash
Error: Wine package installation failed
Solution: The container will retry automatically, or run with --clean flag
```

#### Windows containers not available
```bash
Error: Windows containers are not available
Solution: Switch Docker Desktop to Windows containers mode
```

#### Build artifacts missing
```bash
Error: Application binaries not found
Solution: Make sure the Gradle build completed successfully
```

### Performance Optimization:

#### Speed up builds:
1. **Use Docker build cache**: Don't clean unless necessary
2. **Allocate more resources**: Docker Desktop → Settings → Resources
3. **Use SSD storage**: Ensure Docker is using SSD storage
4. **Close other applications**: Free up RAM and CPU

#### Reduce image size:
1. **Multi-stage builds**: Already implemented in our Dockerfiles
2. **Clean package caches**: Automatically done in build process
3. **Use alpine images**: Consider for development builds

## File Structure

After successful build:

```
project/
├── dist/
│   └── BlackAndPink-Setup-1.0.0.exe    # Windows installer
├── build/
│   └── compose/
│       └── binaries/
│           └── main/
│               ├── app/
│               │   └── BlackAndPink/    # Application bundle
│               └── msi/
│                   └── BlackAndPink-1.0.0.msi
├── Dockerfile.windows-installer         # Wine-based build
├── Dockerfile.windows-native           # Windows-native build
├── docker-compose.yml                  # Container orchestration
└── build-windows-installer.sh          # Automated build script
```

## Integration with CI/CD

### GitHub Actions Integration:

You can use this Docker approach in GitHub Actions:

```yaml
- name: Build Windows Installer via Docker
  run: |
    docker-compose --profile wine build
    docker-compose --profile wine up

- name: Upload Installer
  uses: actions/upload-artifact@v4
  with:
    name: windows-installer-docker
    path: dist/*.exe
```

### Local Development Workflow:

1. **Make code changes**
2. **Test locally**: `./gradlew run`
3. **Build installer**: `./build-windows-installer.sh --wine`
4. **Test installer** on Windows VM or machine
5. **Commit and push** when ready

## Comparison with Other Approaches

| Approach | Platform Requirement | Setup Time | Reliability | Build Speed |
|----------|---------------------|------------|-------------|-------------|
| **Docker Wine** | Any (macOS, Linux, Windows) | Medium | Good | Medium |
| **Docker Native** | Windows Docker | Low | Excellent | Fast |
| **GitHub Actions** | None (cloud) | Low | Excellent | Fast |
| **Windows VM** | Windows VM | High | Excellent | Fast |
| **Manual Transfer** | Access to Windows | Medium | Good | Slow |

## Next Steps

1. **Try the Wine approach**: `./build-windows-installer.sh --wine`
2. **Test the installer**: On a Windows machine or VM
3. **Integrate with CI/CD**: Add to GitHub Actions if needed
4. **Code signing**: Consider adding for production releases
5. **Distribution**: Upload to GitHub Releases or your platform

The Docker approach gives you the flexibility to build Windows installers entirely from macOS, making your development workflow much more efficient!
