# Build script for Windows installer using NSIS
# Run this script on a Windows machine with NSIS installed

Write-Host "==================================================" -ForegroundColor Green
Write-Host "Black and Pink Inventory Management" -ForegroundColor Green
Write-Host "Windows Installer Build Script (PowerShell)" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green
Write-Host ""

# Check for NSIS installation
Write-Host "Checking for NSIS installation..." -ForegroundColor Yellow
try {
    $nsisPath = Get-Command makensis -ErrorAction Stop
    Write-Host "NSIS found at: $($nsisPath.Source)" -ForegroundColor Green
} catch {
    Write-Host "ERROR: NSIS is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install NSIS from: https://nsis.sourceforge.io/Download" -ForegroundColor Red
    Write-Host "Make sure to add NSIS to your system PATH" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""

# Check for required files
Write-Host "Checking for required files..." -ForegroundColor Yellow

$requiredFiles = @(
    "windows-installer.nsi",
    "LICENSE.txt", 
    "README.md",
    "src\jvmMain\resources\icon.ico"
)

$requiredDirs = @(
    "build\compose\binaries\main\app\BlackAndPink"
)

$allFilesExist = $true

foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "✓ $file" -ForegroundColor Green
    } else {
        Write-Host "✗ $file (MISSING)" -ForegroundColor Red
        $allFilesExist = $false
    }
}

foreach ($dir in $requiredDirs) {
    if (Test-Path $dir) {
        Write-Host "✓ $dir" -ForegroundColor Green
    } else {
        Write-Host "✗ $dir (MISSING)" -ForegroundColor Red
        Write-Host "  Please run: ./gradlew createDistributable first" -ForegroundColor Yellow
        $allFilesExist = $false
    }
}

if (-not $allFilesExist) {
    Write-Host ""
    Write-Host "ERROR: Some required files are missing" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "All required files found!" -ForegroundColor Green

# Create dist directory
Write-Host ""
Write-Host "Creating dist directory..." -ForegroundColor Yellow
if (-not (Test-Path "dist")) {
    New-Item -ItemType Directory -Path "dist" | Out-Null
    Write-Host "Created dist directory" -ForegroundColor Green
} else {
    Write-Host "dist directory already exists" -ForegroundColor Green
}

# Compile installer
Write-Host ""
Write-Host "Compiling NSIS installer..." -ForegroundColor Yellow
Write-Host "Command: makensis windows-installer.nsi" -ForegroundColor Cyan

try {
    $result = Start-Process -FilePath "makensis" -ArgumentList "windows-installer.nsi" -Wait -PassThru -NoNewWindow
    
    if ($result.ExitCode -eq 0) {
        Write-Host ""
        Write-Host "==================================================" -ForegroundColor Green
        Write-Host "SUCCESS: Installer created successfully!" -ForegroundColor Green
        Write-Host "Location: dist\BlackAndPink-Setup-1.0.0.exe" -ForegroundColor Green
        Write-Host "==================================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "You can now distribute this installer to end users." -ForegroundColor Green
        
        # Show file size
        if (Test-Path "dist\BlackAndPink-Setup-1.0.0.exe") {
            $fileSize = (Get-Item "dist\BlackAndPink-Setup-1.0.0.exe").Length
            $fileSizeMB = [Math]::Round($fileSize / 1MB, 2)
            Write-Host "Installer size: $fileSizeMB MB" -ForegroundColor Cyan
        }
        
    } else {
        Write-Host ""
        Write-Host "==================================================" -ForegroundColor Red
        Write-Host "ERROR: Failed to create installer" -ForegroundColor Red
        Write-Host "Exit code: $($result.ExitCode)" -ForegroundColor Red
        Write-Host "==================================================" -ForegroundColor Red
    }
} catch {
    Write-Host ""
    Write-Host "ERROR: Failed to run makensis command" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}

Write-Host ""
Read-Host "Press Enter to exit"
