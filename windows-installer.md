# Windows Installer Guide for Black and Pink Inventory Management

## Overview
This guide provides multiple approaches to create a Windows installer for the Black and Pink Inventory Management application.

## Option 1: Native MSI (Requires Windows)
The preferred method is to build the MSI installer on a Windows machine:

```bash
# On Windows machine with JDK 17+ installed
./gradlew packageMsi
```

This will create: `build/compose/binaries/main/msi/BlackAndPink-1.0.0.msi`

## Option 2: Portable Executable (Cross-platform)
Create a portable Windows executable using the existing app bundle:

```bash
# Build the application
./gradlew createDistributable

# The distributable will be created at:
# build/compose/binaries/main/app/BlackAndPink/
```

## Option 3: InnoSetup Script (Recommended for Production)
Use the provided InnoSetup script to create a professional Windows installer.

### Requirements:
- Inno Setup 6.0 or later
- The compiled application bundle

### Steps:
1. Build the application: `./gradlew createDistributable`
2. Copy the `BlackAndPink` folder to a Windows machine
3. Install Inno Setup on Windows
4. Compile the `windows-installer.iss` script
5. This will create `BlackAndPink-Setup-1.0.0.exe`

## Option 4: NSIS Script (Alternative)
Alternative installer using NSIS (Nullsoft Scriptable Install System).

### Requirements:
- NSIS 3.0 or later installed on Windows
- The compiled application bundle

### Automated Build (Recommended):
1. Transfer project files to Windows machine
2. Run the automated build script:
   ```cmd
   # Using Command Prompt
   build-installer.bat
   
   # OR using PowerShell
   .\build-installer.ps1
   ```

### Manual Steps:
1. Build the application: `./gradlew createDistributable`
2. Install NSIS on Windows machine
3. Run: `makensis windows-installer.nsi`
4. This creates `dist\BlackAndPink-Setup-1.0.0.exe`

## Installer Features

### InnoSetup Installer Features:
- ✅ Professional Windows installer
- ✅ Desktop shortcut creation
- ✅ Start Menu integration
- ✅ Proper uninstaller
- ✅ Registry entries
- ✅ File associations
- ✅ System requirements check
- ✅ Modern UI with custom branding

### NSIS Installer Features:
- ✅ Lightweight installer
- ✅ Custom branding
- ✅ Multiple language support
- ✅ Silent installation options
- ✅ Plugins support

## Application Details
- **Name**: Black and Pink Inventory Management
- **Version**: 1.0.0
- **Publisher**: Black and Pink
- **Java Runtime**: Bundled JRE 17
- **Size**: ~100MB (including JRE)

## Deployment Instructions

### For End Users:
1. Download the installer
2. Run as Administrator (recommended)
3. Follow the installation wizard
4. Launch from Desktop shortcut or Start Menu

### For System Administrators:
Silent installation options are available:
```cmd
BlackAndPink-Setup-1.0.0.exe /SILENT
BlackAndPink-Setup-1.0.0.exe /VERYSILENT /NORESTART
```

## Troubleshooting

### Common Issues:
1. **Java Not Found**: The installer includes a bundled JRE, so this shouldn't occur
2. **Permission Denied**: Run installer as Administrator
3. **Antivirus Warning**: The application is unsigned - consider code signing for production

### System Requirements:
- Windows 10 or later (64-bit)
- 4GB RAM minimum
- 200MB free disk space
- Administrator rights for installation

## Code Signing (Production)
For production deployment, consider code signing the installer:
1. Obtain a code signing certificate
2. Sign the installer using SignTool
3. This will eliminate security warnings

```cmd
signtool sign /f certificate.p12 /p password /t http://timestamp.digicert.com BlackAndPink-Setup-1.0.0.exe
```
