; Black and Pink Inventory Management - NSIS Installer Script
; NSIS Script for creating Windows installer
; Version: 1.0.0

;--------------------------------
; Includes
!include "MUI2.nsh"
!include "FileAssociation.nsh"
!include "x64.nsh"

;--------------------------------
; General

; Application Info
!define APP_NAME "Black and Pink Inventory Management"
!define APP_VERSION "1.0.0"
!define APP_PUBLISHER "Black and Pink"
!define APP_URL "https://blackandpink.org"
!define APP_EXECUTABLE "BlackAndPink.exe"
!define APP_GUID "{18159995-D967-4CD2-8885-77BFA97CFA9F}"

; Installer Info
Name "${APP_NAME}"
OutFile "dist\BlackAndPink-Setup-${APP_VERSION}.exe"
Unicode True

; Default installation directory
InstallDir "$PROGRAMFILES64\${APP_NAME}"
InstallDirRegKey HKLM "Software\${APP_NAME}" "InstallDir"

; Request administrator privileges
RequestExecutionLevel admin

; Compressor
SetCompressor /SOLID lzma

;--------------------------------
; Interface Settings

!define MUI_ABORTWARNING
!define MUI_ICON "src\jvmMain\resources\icon.ico"
!define MUI_UNICON "src\jvmMain\resources\icon.ico"

; Optional: Use custom header and welcome images if they exist
; !define MUI_HEADERIMAGE
; !define MUI_HEADERIMAGE_RIGHT
; !define MUI_HEADERIMAGE_BITMAP "installer-header.bmp" ; 150x57 pixels
; !define MUI_WELCOMEFINISHPAGE_BITMAP "installer-welcome.bmp" ; 164x314 pixels

; Welcome page
!define MUI_WELCOMEPAGE_TITLE "Welcome to ${APP_NAME} Setup"
!define MUI_WELCOMEPAGE_TEXT "This wizard will guide you through the installation of ${APP_NAME}.$\r$\n$\r$\nA modern inventory management system designed for Black and Pink organizations.$\r$\n$\r$\nClick Next to continue."

; License page
!define MUI_LICENSEPAGE_TEXT_TOP "Please review the license terms before installing ${APP_NAME}."
!define MUI_LICENSEPAGE_TEXT_BOTTOM "If you accept the terms of the agreement, click I Agree to continue. You must accept the agreement to install ${APP_NAME}."

; Directory page
!define MUI_DIRECTORYPAGE_TEXT_TOP "Setup will install ${APP_NAME} in the following folder. To install in a different folder, click Browse and select another folder. Click Next to continue."

; Start Menu page
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKLM"
!define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\${APP_NAME}"
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"

; Finish page
!define MUI_FINISHPAGE_RUN "$INSTDIR\${APP_EXECUTABLE}"
!define MUI_FINISHPAGE_RUN_TEXT "Launch ${APP_NAME}"

;--------------------------------
; Pages

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "LICENSE.txt"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
; Languages

!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "Spanish"

;--------------------------------
; Version Information

VIProductVersion "${APP_VERSION}.0"
VIAddVersionKey /LANG=${LANG_ENGLISH} "ProductName" "${APP_NAME}"
VIAddVersionKey /LANG=${LANG_ENGLISH} "Comments" "Modern inventory management system"
VIAddVersionKey /LANG=${LANG_ENGLISH} "CompanyName" "${APP_PUBLISHER}"
VIAddVersionKey /LANG=${LANG_ENGLISH} "LegalCopyright" "© 2025 ${APP_PUBLISHER}"
VIAddVersionKey /LANG=${LANG_ENGLISH} "FileDescription" "${APP_NAME} Installer"
VIAddVersionKey /LANG=${LANG_ENGLISH} "FileVersion" "${APP_VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} "ProductVersion" "${APP_VERSION}"

;--------------------------------
; Installer Sections

Section "Application Files" SecApp
  SectionIn RO
  
  ; Set output path
  SetOutPath "$INSTDIR"
  
  ; Copy application files
  File /r "build\compose\binaries\main\app\BlackAndPink\*.*"
  
  ; Copy documentation
  File "README.md"
  File "LICENSE.txt"
  
  ; Copy icon
  File "src\jvmMain\resources\icon.ico"
  
  ; Store installation folder
  WriteRegStr HKLM "Software\${APP_NAME}" "InstallDir" $INSTDIR
  
  ; Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  
  ; Add to Add/Remove Programs
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "DisplayName" "${APP_NAME}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "DisplayVersion" "${APP_VERSION}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "Publisher" "${APP_PUBLISHER}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "DisplayIcon" "$INSTDIR\icon.ico"
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "NoRepair" 1
  
  ; Estimate size
  ${GetSize} "$INSTDIR" "/S=0K" $0 $1 $2
  IntFmt $0 "0x%08X" $0
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "EstimatedSize" "$0"
  
SectionEnd

Section "Desktop Shortcut" SecDesktop
  CreateShortcut "$DESKTOP\${APP_NAME}.lnk" "$INSTDIR\${APP_EXECUTABLE}" "" "$INSTDIR\icon.ico"
SectionEnd

Section "Start Menu Shortcuts" SecStartMenu
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
  
  CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
  CreateShortcut "$SMPROGRAMS\$StartMenuFolder\${APP_NAME}.lnk" "$INSTDIR\${APP_EXECUTABLE}" "" "$INSTDIR\icon.ico"
  CreateShortcut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

Section "File Associations" SecFileAssoc
  ; Register .bpinv file extension
  ${registerExtension} "$INSTDIR\${APP_EXECUTABLE}" ".bpinv" "Black and Pink Inventory File"
SectionEnd

;--------------------------------
; Section Descriptions

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecApp} "Core application files (required)"
  !insertmacro MUI_DESCRIPTION_TEXT ${SecDesktop} "Create a desktop shortcut"
  !insertmacro MUI_DESCRIPTION_TEXT ${SecStartMenu} "Create Start Menu shortcuts"
  !insertmacro MUI_DESCRIPTION_TEXT ${SecFileAssoc} "Associate .bpinv files with the application"
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
; Installer Functions

Function .onInit
  ; Check if 64-bit Windows
  ${IfNot} ${RunningX64}
    MessageBox MB_OK|MB_ICONSTOP "This application requires a 64-bit version of Windows."
    Abort
  ${EndIf}
  
  ; Check if already installed
  ReadRegStr $R0 HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "UninstallString"
  StrCmp $R0 "" done
  
  MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION "${APP_NAME} is already installed. $\n$\nClick OK to remove the previous version or Cancel to cancel this upgrade." IDOK uninst
  Abort
  
  uninst:
    ClearErrors
    ExecWait '$R0 /S _?=$INSTDIR'
    
    IfErrors no_remove_uninstaller done
    no_remove_uninstaller:
  
  done:
FunctionEnd

Function LaunchApplication
  ExecShell "" "$INSTDIR\${APP_EXECUTABLE}"
FunctionEnd

;--------------------------------
; Uninstaller Section

Section "Uninstall"
  
  ; Remove files and folders
  Delete "$INSTDIR\${APP_EXECUTABLE}"
  Delete "$INSTDIR\*.jar"
  Delete "$INSTDIR\*.dll"
  Delete "$INSTDIR\README.md"
  Delete "$INSTDIR\LICENSE.txt"
  Delete "$INSTDIR\icon.ico"
  Delete "$INSTDIR\Uninstall.exe"
  
  ; Remove runtime directory
  RMDir /r "$INSTDIR\runtime"
  
  ; Remove application directory
  RMDir "$INSTDIR"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}"
  DeleteRegKey HKLM "Software\${APP_NAME}"
  
  ; Remove file associations
  ${unregisterExtension} ".bpinv" "Black and Pink Inventory File"
  
  ; Remove shortcuts
  Delete "$DESKTOP\${APP_NAME}.lnk"
  
  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
  Delete "$SMPROGRAMS\$StartMenuFolder\${APP_NAME}.lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  RMDir "$SMPROGRAMS\$StartMenuFolder"
  
  ; Optional: Remove user data (ask first)
  MessageBox MB_YESNO "Remove user data and settings?" IDNO +2
  RMDir /r "$APPDATA\BlackAndPink"
  
SectionEnd

;--------------------------------
; Uninstaller Functions

Function un.onInit
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to completely remove ${APP_NAME} and all of its components?" IDYES +2
  Abort
FunctionEnd
