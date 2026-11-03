# =============================================================================
# 停止旧实例 -> 部署 WAR -> 启动 Tomcat 9 -> 等待并验证首页
# =============================================================================
param(
    [switch]$NoStart
)
$ErrorActionPreference = "Stop"

$ProjectRoot = $PSScriptRoot
$TomcatHome = "D:\apache-tomcat-9.0.122-windows-x64\apache-tomcat-9.0.122"
$JdkHome = "D:\java"
$WarPath = Join-Path $ProjectRoot "dist\ClassScoreManager.war"
$TargetWar = Join-Path $TomcatHome "webapps\classscore.war"
$TargetDir = Join-Path $TomcatHome "webapps\classscore"
$AppUrl = "http://localhost:8080/classscore/"

if (-not (Test-Path $WarPath)) {
    throw "找不到 $WarPath，请先执行 .\build.ps1"
}

$env:JAVA_HOME = $JdkHome
$env:CATALINA_HOME = $TomcatHome
$env:CATALINA_BASE = $TomcatHome
$env:CATALINA_OPTS = "-Dfile.encoding=UTF-8"
$env:CLASSSCORE_HOME = $ProjectRoot

Write-Host "[1/4] 停止已有 Tomcat 9 实例..." -ForegroundColor Yellow
if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) {
    $shutdownScript = Join-Path $TomcatHome "bin\shutdown.bat"
    Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/c", "`"$shutdownScript`"" `
        -WindowStyle Hidden -Wait
    for ($i = 0; $i -lt 20; $i++) {
        Start-Sleep -Milliseconds 500
        if (-not (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue)) { break }
    }
}

Write-Host "[2/4] 清理旧部署并复制 WAR..." -ForegroundColor Yellow
if (Test-Path $TargetDir) { Remove-Item $TargetDir -Recurse -Force }
if (Test-Path $TargetWar) { Remove-Item $TargetWar -Force }
Copy-Item $WarPath $TargetWar -Force

if ($NoStart) {
    Write-Host "WAR 已复制，未启动 Tomcat（-NoStart）。" -ForegroundColor Cyan
    exit 0
}

Write-Host "[3/4] 启动 Tomcat 9..." -ForegroundColor Yellow
Start-Process -FilePath (Join-Path $TomcatHome "bin\startup.bat") `
    -WorkingDirectory (Join-Path $TomcatHome "bin") -WindowStyle Hidden
Start-Sleep -Seconds 3

Write-Host "[4/4] 等待应用就绪..." -ForegroundColor Yellow
$ready = $false
for ($i = 0; $i -lt 60; $i++) {
    try {
        $response = Invoke-WebRequest -Uri $AppUrl -UseBasicParsing -TimeoutSec 5
        if ($response.StatusCode -eq 200) { $ready = $true; break }
    } catch { }
    Start-Sleep -Seconds 1
}

if (-not $ready) {
    Write-Host "应用未能正常启动，请查看 Tomcat 日志：" -ForegroundColor Red
    Write-Host (Join-Path $TomcatHome "logs\catalina.*.log")
    Get-ChildItem (Join-Path $TomcatHome "logs") -Filter "catalina.*.log" |
        Sort-Object LastWriteTime -Descending | Select-Object -First 1 |
        ForEach-Object { Get-Content $_.FullName -Tail 60 }
    exit 1
}

Write-Host ""
Write-Host "  DEPLOY SUCCESS" -ForegroundColor Green
Write-Host "  访问地址 : $AppUrl" -ForegroundColor Cyan
Write-Host "  教师账号 : teacher / 123456"
Write-Host "  学生账号 : 20240101 / 123456"
Write-Host ""
