param(
    [Parameter(Mandatory = $false)]
    [string]$WatchIpPort = "",

    [Parameter(Mandatory = $false)]
    [string]$PairingIpPort = "",

    [Parameter(Mandatory = $false)]
    [string]$PairingCode = "",

    [Parameter(Mandatory = $false)]
    [string]$ApkPath = ""
)

$ErrorActionPreference = "Stop"
$RepoRoot = Split-Path -Parent $PSScriptRoot
$ToolsDir = Join-Path $RepoRoot ".tools"
$PlatformToolsDir = Join-Path $ToolsDir "platform-tools"
$LocalAdb = Join-Path $PlatformToolsDir "adb.exe"

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  5D Ab Workout - Galaxy Watch8 Classic Wireless Deploy" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan

# 1. Ensure ADB is available (auto-download official Android platform-tools if missing)
$AdbCmd = Get-Command adb -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source
if (-not $AdbCmd) {
    if (-not (Test-Path $LocalAdb)) {
        Write-Host "[1/4] Downloading official Android SDK Platform-Tools (ADB)..." -ForegroundColor Yellow
        New-Item -ItemType Directory -Force -Path $ToolsDir | Out-Null
        $ZipPath = Join-Path $ToolsDir "platform-tools-latest-windows.zip"
        Invoke-WebRequest -Uri "https://dl.google.com/android/repository/platform-tools-latest-windows.zip" -OutFile $ZipPath
        Expand-Archive -Path $ZipPath -DestinationPath $ToolsDir -Force
        Remove-Item $ZipPath -Force
    }
    $AdbCmd = $LocalAdb
}
Write-Host "Using ADB: $AdbCmd" -ForegroundColor Green

# 2. Locate APK
if (-not $ApkPath) {
    $DefaultDebugApk = Join-Path $RepoRoot "app\build\outputs\apk\debug\app-debug.apk"
    $DefaultReleaseApk = Join-Path $RepoRoot "app\build\outputs\apk\release\app-release.apk"
    if (Test-Path $DefaultReleaseApk) {
        $ApkPath = $DefaultReleaseApk
    } elseif (Test-Path $DefaultDebugApk) {
        $ApkPath = $DefaultDebugApk
    }
}

if (-not $ApkPath -or -not (Test-Path $ApkPath)) {
    Write-Host "No built APK found yet at app\build\outputs\apk\." -ForegroundColor Yellow
    Write-Host "Build the project in Android Studio or via GitHub Actions, or pass -ApkPath <path-to-apk>." -ForegroundColor Yellow
    exit 1
}

# 3. Wireless Debugging Pairing (if PairingIpPort provided)
if ($PairingIpPort -and $PairingCode) {
    Write-Host "[2/4] Pairing with Galaxy Watch8 Classic at $PairingIpPort..." -ForegroundColor Cyan
    & $AdbCmd pair $PairingIpPort $PairingCode
}

# 4. Connect to Watch IP:Port
if ($WatchIpPort) {
    Write-Host "[3/4] Connecting to Galaxy Watch8 Classic at $WatchIpPort..." -ForegroundColor Cyan
    & $AdbCmd connect $WatchIpPort
}

# 5. Wait for Watch & Keep Screen Awake During Install
Write-Host "[4/4] Waiting for Galaxy Watch8 Classic to wake up on Wi-Fi..." -ForegroundColor Cyan
& $AdbCmd wait-for-device

$keepAwakeJob = Start-Job -ScriptBlock {
    param($adbPath)
    for ($i = 0; $i -lt 25; $i++) {
        & $adbPath shell input keyevent 224 2>$null
        & $adbPath shell input touchscreen swipe 200 200 200 205 50 2>$null
        Start-Sleep -Seconds 2
    }
} -ArgumentList $AdbCmd

Write-Host "Installing 5D Ab ($ApkPath) onto Galaxy Watch8 Classic..." -ForegroundColor Cyan
& $AdbCmd install -r $ApkPath

Write-Host "Granting Heart Rate & Activity sensor permissions..." -ForegroundColor Cyan
& $AdbCmd shell pm grant com.carlosmarins.fivedab android.permission.BODY_SENSORS 2>$null
& $AdbCmd shell pm grant com.carlosmarins.fivedab android.permission.ACTIVITY_RECOGNITION 2>$null
& $AdbCmd shell pm grant com.carlosmarins.fivedab android.permission.POST_NOTIFICATIONS 2>$null

Write-Host "Launching 5D Ab on Galaxy Watch8 Classic..." -ForegroundColor Cyan
& $AdbCmd shell am start -n com.carlosmarins.fivedab/com.carlosmarins.fivedab.MainActivity

Stop-Job $keepAwakeJob -ErrorAction SilentlyContinue
Remove-Job $keepAwakeJob -Force -ErrorAction SilentlyContinue

Write-Host "`n✓ 5D Ab is installed and running on your Galaxy Watch8 Classic!" -ForegroundColor Green
