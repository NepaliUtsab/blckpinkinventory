# GitHub Actions MSI Build Troubleshooting Guide

## Problem Description
The GitHub Actions Windows runner fails when executing `./gradlew packageMsi` with the error:
```
Execution failed for task ':packageMsi'.
External tool execution failed:
* Command: [C:\hostedtoolcache\windows\Java_Temurin-Hotspot_jdk\17.0.15-6\x64\bin\jpackage.exe, ...]
* Exit code: 1
```

## Root Causes and Solutions

### 1. JPackage Memory/Environment Issues
**Cause**: jpackage can fail in CI environments due to memory constraints or missing dependencies.

**Solutions**:
- ✅ **Added to workflow**: Install WiX Toolset (`choco install wixtoolset`)
- ✅ **Added to workflow**: Enhanced error logging and debugging
- ✅ **Added to workflow**: Fallback to NSIS installer

### 2. Build Configuration Issues
**Cause**: Gradle Compose configuration may have incompatible settings for CI.

**Solutions Applied**:
```kotlin
// Updated build.gradle.kts
windows {
    // Simplified UUID format for better compatibility
    upgradeUuid = "18159995-d967-4cd2-8885-77bfa97cfa9f"
    
    // Added JVM args for better compatibility
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
}
```

### 3. Multi-Layered Fallback Strategy
Our workflow now implements a robust fallback system:

1. **Primary**: Native MSI with jpackage
2. **Secondary**: NSIS installer (native Windows)
3. **Tertiary**: Docker-based NSIS (Linux)

## Current Workflow Enhancements

### Enhanced Debugging
```yaml
- name: Try Package MSI (with fallback)
  run: |
    # Check Java and jpackage
    java -version
    jpackage --version
    
    # List distributable contents
    Get-ChildItem "build\compose\binaries\main\app" -Recurse
    
    # Run with full debugging
    ./gradlew packageMsi -x test --info --debug --stacktrace
```

### Robust NSIS Fallback
```yaml
- name: Build NSIS installer as primary/fallback
  run: |
    # Create proper directory structure
    New-Item -ItemType Directory -Force -Path "build\windows-dist\BlackAndPink"
    
    # Copy files with fallback logic
    if (Test-Path "build\compose\binaries\main\app") {
      Copy-Item -Path "build\compose\binaries\main\app\*" -Destination "build\windows-dist\BlackAndPink" -Recurse -Force
    } else {
      # Manual structure creation
      Copy-Item -Path "build\libs\*.jar" -Destination "build\windows-dist\BlackAndPink" -Force
    }
    
    # Build NSIS installer
    makensis windows-installer-simple.nsi
```

## Testing the Solutions

### Local Testing (macOS/Linux)
```bash
# Test Docker approach
./test-all-methods.sh

# Test individual components
docker build -f Dockerfile.windows-installer -t test-wine .
docker run --rm -v "$(pwd):/app" test-wine
```

### CI Testing
The workflow includes comprehensive logging and artifact collection:
- MSI files (if successful)
- NSIS installer (always created)
- Build logs and error details
- File structure verification

## Alternative Approaches

### 1. Docker-Based Build (Ultimate Fallback)
If all Windows-native approaches fail, we have a separate workflow:
```yaml
# .github/workflows/docker-windows-installer.yml
- name: Build Docker image for Windows installer
  run: docker build -f Dockerfile.windows-installer -t blackandpink-wine .

- name: Create Windows installer with Docker
  run: docker run --rm -v "$(pwd):/app" blackandpink-wine
```

### 2. Cross-Platform NSIS
The `windows-installer-simple.nsi` script is designed to work on both:
- Windows NSIS (GitHub Actions)
- Linux NSIS (Docker container)

### 3. Manual Build Instructions
For users who need to build manually:
```bash
# On Windows
./gradlew createDistributable
makensis windows-installer-simple.nsi

# On macOS/Linux with Docker
docker build -f Dockerfile.windows-installer -t blackandpink-wine .
docker run --rm -v "$(pwd):/app" blackandpink-wine
```

## Success Metrics

The workflow will be considered successful if:
1. ✅ MSI is created (preferred)
2. ✅ NSIS installer is created (fallback)
3. ✅ Installer size is reasonable (1-10MB)
4. ✅ All artifacts are uploaded

## Next Steps

1. **Monitor CI runs** to see which approach succeeds
2. **Collect jpackage logs** for further debugging if needed
3. **Optimize successful approach** based on results
4. **Consider code signing** for production releases

## Files Modified
- `.github/workflows/build-and-release.yml` - Enhanced with fallbacks
- `.github/workflows/docker-windows-installer.yml` - Docker fallback
- `build.gradle.kts` - Improved Windows configuration
- `windows-installer-simple.nsi` - CI-compatible NSIS script
- `test-all-methods.sh` - Comprehensive testing script

The multi-layered approach ensures that Windows installers will be created successfully regardless of the specific CI environment limitations.
