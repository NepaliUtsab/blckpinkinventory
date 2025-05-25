# NSIS Installer Usage Guide

## Required Files for NSIS Installation

The NSIS script `windows-installer.nsi` requires these files to be present:

### 1. Application Files
- `build/compose/binaries/main/app/BlackAndPink/` - Your built application (created by `./gradlew createDistributable`)

### 2. Documentation
- ✅ `LICENSE.txt` - Already present
- ✅ `README.md` - Already present

### 3. Icons and Images
- ✅ `src/jvmMain/resources/icon.ico` - Application icon (already present)
- ⚠️ `installer-header.bmp` - Header image for installer (150x57 pixels) - OPTIONAL
- ⚠️ `installer-welcome.bmp` - Welcome page image (164x314 pixels) - OPTIONAL

**Note**: The bitmap files are optional. If they don't exist, NSIS will use default styling.

## Steps to Create Windows Installer

### Step 1: Install NSIS (Windows Only)
1. Download NSIS from: https://nsis.sourceforge.io/Download
2. Install NSIS on a Windows machine
3. Make sure NSIS is added to your PATH

### Step 2: Transfer Files to Windows
Since you're on macOS, you need to transfer these files to a Windows machine:
1. The entire project folder OR
2. Just the essential files:
   - `windows-installer.nsi`
   - `LICENSE.txt`
   - `README.md`
   - `src/jvmMain/resources/icon.ico`
   - `build/compose/binaries/main/app/BlackAndPink/` (entire folder)

### Step 3: Compile the Installer (on Windows)
Open Command Prompt or PowerShell on Windows and run:

```cmd
# Navigate to your project directory
cd C:\path\to\your\project

# Compile the NSIS script
makensis windows-installer.nsi
```

### Step 4: Result
This will create: `dist\BlackAndPink-Setup-1.0.0.exe`

## Alternative: Use InnoSetup Instead

InnoSetup might be easier to use and is more commonly used for professional installers.
Use the `windows-installer.iss` file instead:

1. Install Inno Setup from: https://jrsoftware.org/isinfo.php
2. Open `windows-installer.iss` in Inno Setup Compiler
3. Click "Build" or press F9
4. This creates `BlackAndPink-Setup-1.0.0.exe`

## Cross-Platform Alternative

If you want to avoid Windows completely, you can:
1. Use `./gradlew packageMsi` (but requires Windows)
2. Use Docker with Windows container
3. Use GitHub Actions with Windows runner
4. Use a Windows VM

## Testing the Installer

Once created, test the installer:
1. Run `BlackAndPink-Setup-1.0.0.exe` on a clean Windows machine
2. Verify it installs correctly
3. Check that shortcuts are created
4. Verify the application launches
5. Test the uninstaller
