# GitHub Setup and CI/CD Guide

## Quick Setup for GitHub Repository

### 1. Initialize Git Repository (if not already done)
```bash
cd /Users/utsab/Desktop/Projects/blackandpink
git init
git add .
git commit -m "Initial commit: Black and Pink Inventory Management"
```

### 2. Create GitHub Repository
1. Go to [GitHub.com](https://github.com)
2. Click "New repository"
3. Name: `blackandpink-inventory`
4. Description: `Modern inventory management system for Black and Pink organizations`
5. Choose Public or Private
6. **Don't** initialize with README (we already have one)
7. Click "Create repository"

### 3. Connect Local Repository to GitHub
```bash
# Replace YOUR_USERNAME with your GitHub username
git remote add origin https://github.com/YOUR_USERNAME/blackandpink-inventory.git
git branch -M main
git push -u origin main
```

## GitHub Actions Workflows

### Automatic Builds
Two workflows are configured:

#### 1. Development Build (`dev-build.yml`)
- **Triggers**: Push to `main` or `develop` branches (non-tagged)
- **Actions**: 
  - ~~Runs tests~~ (tests disabled for faster builds)
  - Builds Windows installer (MSI + NSIS)
  - Stores artifacts for 7 days
- **Artifacts**: Available in Actions tab → Build logs → Artifacts

#### 2. Release Build (`build-and-release.yml`)
- **Triggers**: Push tags starting with `v` (e.g., `v1.0.0`)
- **Actions**:
  - ~~Runs tests on Linux~~ (tests disabled for faster builds)
  - Builds packages for all platforms:
    - Windows: MSI + NSIS installer
    - macOS: DMG package
    - Linux: DEB + RPM packages
  - Creates GitHub Release with downloadable assets

## Creating a Release

### Method 1: Using Git Tags
```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0
```

### Method 2: Using GitHub Web Interface
1. Go to your repository on GitHub
2. Click "Releases" → "Create a new release"
3. Tag version: `v1.0.0`
4. Release title: `Black and Pink Inventory Management v1.0.0`
5. Click "Publish release"

## What Happens Automatically

### On Every Push to main/develop:
1. ~~✅ Tests run on Ubuntu~~ (tests disabled for faster builds)
2. ✅ Windows installer is built and stored as artifact
3. ✅ You can download the installer from GitHub Actions page

### On Tagged Release (v*):
1. ~~✅ Full cross-platform build~~ (tests disabled for faster builds)
2. ✅ GitHub Release is created automatically
3. ✅ All installers are attached to the release
4. ✅ Users can download directly from Releases page

## Downloading Installers

### Development Builds
1. Go to your repository → Actions tab
2. Click on latest "Development Build" workflow run
3. Scroll to "Artifacts" section
4. Download `windows-installers-dev-[commit-hash]`

### Release Builds
1. Go to your repository → Releases tab
2. Click on the latest release
3. Download from "Assets" section:
   - `BlackAndPink-Setup-1.0.0.msi` (Windows MSI)
   - `BlackAndPink-Setup-1.0.0.exe` (Windows NSIS)
   - `BlackAndPink-1.0.0.dmg` (macOS)
   - `BlackAndPink-1.0.0.deb` (Linux Debian/Ubuntu)
   - `BlackAndPink-1.0.0.rpm` (Linux RedHat/CentOS)

## Customizing the Workflows

### Update Version Numbers
Edit these files when releasing new versions:
- `build.gradle.kts` → `version = "1.0.0"`
- `windows-installer.nsi` → `APP_VERSION "1.0.0"`
- `windows-installer.iss` → `AppVersion=1.0.0`

### Adding Code Signing (Optional)
For production releases, consider adding code signing:

1. Add secrets to GitHub repository:
   - `WINDOWS_CERTIFICATE`: Base64 encoded .p12 certificate
   - `WINDOWS_CERTIFICATE_PASSWORD`: Certificate password

2. Update workflow to sign installers before upload

### Custom Build Triggers
You can modify `.github/workflows/*.yml` to:
- Build on different branches
- Skip builds with `[skip ci]` in commit message
- Add manual workflow triggers
- Schedule nightly builds

## Repository Structure
```
.github/
  workflows/
    build-and-release.yml    # Full release workflow
    dev-build.yml           # Development build workflow
build-installer.bat         # Windows build script
build-installer.ps1         # PowerShell build script  
windows-installer.nsi       # NSIS installer script
windows-installer.iss       # InnoSetup installer script
build.gradle.kts           # Gradle build configuration
```

## Troubleshooting

### Build Failures
- Check Actions tab for detailed logs
- Common issues: Java version, missing dependencies
- Windows builds may fail if NSIS installation fails

### Artifact Not Available
- Artifacts expire after retention period (7 days for dev, 30 days for releases)
- Use releases for permanent downloads

### Permission Issues
- Ensure repository has Actions enabled (Settings → Actions)
- Check if organization restrictions apply

## Next Steps

1. **Push to GitHub**: Follow setup steps above
2. **Test Build**: Push a commit and check Actions tab
3. **Create Release**: Tag v1.0.0 and verify release creation
4. **Share**: Send users to Releases page for downloads

Your Windows installer will now be automatically built and available for download every time you push code! 🚀
