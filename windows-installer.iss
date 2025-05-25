; Black and Pink Inventory Management - Windows Installer Script
; Inno Setup Script for creating professional Windows installer
; Version: 1.0.0

#define MyAppName "Black and Pink Inventory Management"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "Black and Pink"
#define MyAppURL "https://blackandpink.org"
#define MyAppExeName "BlackAndPink.exe"
#define MyAppDescription "Modern inventory management system for Black and Pink"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
AppId={{18159995-D967-4CD2-8885-77BFA97CFA9F}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
LicenseFile=LICENSE.txt
InfoBeforeFile=README.md
OutputDir=dist
OutputBaseFilename=BlackAndPink-Setup-{#MyAppVersion}
SetupIconFile=src\jvmMain\resources\icon.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern
PrivilegesRequired=admin
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked; OnlyBelowVersion: 6.1
Name: "startmenu"; Description: "Create Start Menu entry"; GroupDescription: "{cm:AdditionalIcons}"; Flags: checkedonce

[Files]
; Main application files
Source: "build\compose\binaries\main\app\BlackAndPink\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; Documentation
Source: "README.md"; DestDir: "{app}"; Flags: ignoreversion
Source: "LICENSE.txt"; DestDir: "{app}"; Flags: ignoreversion; AfterInstall: CreateLicenseFile
; Icon
Source: "src\jvmMain\resources\icon.ico"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
; Desktop shortcut
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon; IconFilename: "{app}\icon.ico"; Comment: "{#MyAppDescription}"
; Start Menu shortcuts
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\icon.ico"; Comment: "{#MyAppDescription}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"; IconFilename: "{app}\icon.ico"
; Quick Launch (older Windows versions)
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: quicklaunchicon; IconFilename: "{app}\icon.ico"

[Registry]
; File associations for inventory files
Root: HKCR; Subkey: ".bpinv"; ValueType: string; ValueName: ""; ValueData: "BlackAndPinkInventory"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "BlackAndPinkInventory"; ValueType: string; ValueName: ""; ValueData: "Black and Pink Inventory File"; Flags: uninsdeletekey
Root: HKCR; Subkey: "BlackAndPinkInventory\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\icon.ico"
Root: HKCR; Subkey: "BlackAndPinkInventory\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""

; Add to Windows Apps & Features
Root: HKLM; Subkey: "Software\Microsoft\Windows\CurrentVersion\Uninstall\{#MyAppName}"; ValueType: string; ValueName: "DisplayName"; ValueData: "{#MyAppName}"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Microsoft\Windows\CurrentVersion\Uninstall\{#MyAppName}"; ValueType: string; ValueName: "DisplayVersion"; ValueData: "{#MyAppVersion}"
Root: HKLM; Subkey: "Software\Microsoft\Windows\CurrentVersion\Uninstall\{#MyAppName}"; ValueType: string; ValueName: "Publisher"; ValueData: "{#MyAppPublisher}"
Root: HKLM; Subkey: "Software\Microsoft\Windows\CurrentVersion\Uninstall\{#MyAppName}"; ValueType: string; ValueName: "DisplayIcon"; ValueData: "{app}\icon.ico"

[Run]
; Option to launch the application after installation
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#MyAppName}}"; Flags: nowait postinstall skipifsilent shellexec

[UninstallDelete]
; Clean up any created data files (optional - ask user first)
Type: filesandordirs; Name: "{userappdata}\BlackAndPink"

[Code]
procedure CreateLicenseFile();
var
  LicenseContent: String;
begin
  LicenseContent := 'MIT License' + #13#10 + #13#10 +
    'Copyright (c) 2025 Black and Pink' + #13#10 + #13#10 +
    'Permission is hereby granted, free of charge, to any person obtaining a copy' + #13#10 +
    'of this software and associated documentation files (the "Software"), to deal' + #13#10 +
    'in the Software without restriction, including without limitation the rights' + #13#10 +
    'to use, copy, modify, merge, publish, distribute, sublicense, and/or sell' + #13#10 +
    'copies of the Software, and to permit persons to whom the Software is' + #13#10 +
    'furnished to do so, subject to the following conditions:' + #13#10 + #13#10 +
    'The above copyright notice and this permission notice shall be included in all' + #13#10 +
    'copies or substantial portions of the Software.' + #13#10 + #13#10 +
    'THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR' + #13#10 +
    'IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,' + #13#10 +
    'FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE' + #13#10 +
    'AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER' + #13#10 +
    'LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,' + #13#10 +
    'OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE' + #13#10 +
    'SOFTWARE.';
  
  if not FileExists(ExpandConstant('{app}\LICENSE.txt')) then
    SaveStringToFile(ExpandConstant('{app}\LICENSE.txt'), LicenseContent, False);
end;

function InitializeSetup(): Boolean;
begin
  Result := True;
  
  // Check Windows version
  if not IsWin64 then
  begin
    MsgBox('This application requires a 64-bit version of Windows.', mbError, MB_OK);
    Result := False;
    Exit;
  end;
  
  // Check available disk space (200MB minimum)
  if GetSpaceOnDisk(ExtractFileDrive(ExpandConstant('{tmp}')), False, True, True) < 200 * 1024 * 1024 then
  begin
    MsgBox('Insufficient disk space. At least 200MB is required.', mbError, MB_OK);
    Result := False;
    Exit;
  end;
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  case CurStep of
    ssInstall:
      begin
        // Custom installation logic here
      end;
    ssPostInstall:
      begin
        // Post-installation tasks
        // Create application data directory
        CreateDir(ExpandConstant('{userappdata}\BlackAndPink'));
      end;
  end;
end;

function ShouldSkipPage(PageID: Integer): Boolean;
begin
  Result := False;
  // Skip license page if running silently
  if (PageID = wpLicense) and (WizardSilent()) then
    Result := True;
end;
