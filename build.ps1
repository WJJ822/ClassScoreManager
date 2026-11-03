# =============================================================================
# GradeFlow 构建脚本（无需 Maven）
# 编译 Java 源码 -> 生成 IntelliJ exploded artifact -> 打包 WAR
# =============================================================================
$ErrorActionPreference = "Stop"

$ProjectRoot = $PSScriptRoot
$JdkHome = if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\javac.exe"))) {
    $env:JAVA_HOME
} elseif (Test-Path "C:\Program Files\Microsoft\jdk-17.0.12.7-hotspot\bin\javac.exe") {
    "C:\Program Files\Microsoft\jdk-17.0.12.7-hotspot"
} else {
    "D:\java"
}
$TomcatHome = "D:\apache-tomcat-9.0.122-windows-x64\apache-tomcat-9.0.122"

$SourceDir = Join-Path $ProjectRoot "src"
$WebDir = Join-Path $ProjectRoot "web"
$OutDir = Join-Path $ProjectRoot "out\artifacts\ClassScoreManager_war_exploded"
$DistDir = Join-Path $ProjectRoot "dist"
$ClassesDir = Join-Path $OutDir "WEB-INF\classes"
$WarPath = Join-Path $DistDir "ClassScoreManager.war"

Write-Host ""
Write-Host "  GradeFlow 班级学生成绩管理系统" -ForegroundColor Cyan
Write-Host "  ------------------------------------------------" -ForegroundColor DarkGray
Write-Host "  JDK     : $JdkHome"
Write-Host "  Tomcat  : $TomcatHome"
Write-Host "  输出    : $OutDir"
Write-Host ""

if (-not (Test-Path (Join-Path $JdkHome "bin\javac.exe"))) {
    throw "找不到 javac：$JdkHome\bin\javac.exe"
}
if (-not (Test-Path (Join-Path $TomcatHome "lib\servlet-api.jar"))) {
    throw "找不到 Tomcat 9：$TomcatHome"
}
if (-not (Test-Path (Join-Path $WebDir "WEB-INF\lib\h2-2.2.224.jar"))) {
    throw "缺少 H2 驱动：web\WEB-INF\lib\h2-2.2.224.jar"
}

if (Test-Path $OutDir) { Remove-Item $OutDir -Recurse -Force }
if (Test-Path $DistDir) { Remove-Item $DistDir -Recurse -Force }
New-Item -ItemType Directory -Force -Path $ClassesDir | Out-Null
New-Item -ItemType Directory -Force -Path $DistDir | Out-Null

Write-Host "[1/4] 复制 Web 资源..." -ForegroundColor Yellow
Copy-Item (Join-Path $WebDir "*") $OutDir -Recurse -Force
New-Item -ItemType Directory -Force -Path $ClassesDir | Out-Null

Write-Host "[2/4] 编译 Java 源码..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem $SourceDir -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
$classPath = @(
    (Join-Path $TomcatHome "lib\servlet-api.jar"),
    (Join-Path $TomcatHome "lib\jsp-api.jar"),
    (Join-Path $WebDir "WEB-INF\lib\h2-2.2.224.jar"),
    (Join-Path $WebDir "WEB-INF\lib\jstl-1.2.jar")
) -join ";"

& (Join-Path $JdkHome "bin\javac.exe") --release 17 -encoding UTF-8 `
    -cp $classPath -d $ClassesDir $javaFiles
if ($LASTEXITCODE -ne 0) { throw "Java 编译失败" }

Write-Host "[3/4] 校验工件..." -ForegroundColor Yellow
$classCount = (Get-ChildItem $ClassesDir -Recurse -Filter "*.class").Count
$libCount = (Get-ChildItem (Join-Path $OutDir "WEB-INF\lib") -Filter "*.jar").Count
if ($classCount -lt 30) { throw "编译产物不足，仅 $classCount 个 class" }
if ($libCount -lt 2) { throw "WEB-INF/lib 依赖不完整" }

Write-Host "[4/4] 打包 WAR..." -ForegroundColor Yellow
Push-Location $OutDir
try {
    & (Join-Path $JdkHome "bin\jar.exe") --create --file $WarPath .
    if ($LASTEXITCODE -ne 0) { throw "WAR 打包失败" }
} finally {
    Pop-Location
}

Write-Host ""
Write-Host "  BUILD SUCCESS" -ForegroundColor Green
Write-Host "  class : $classCount"
Write-Host "  lib   : $libCount"
Write-Host "  war   : $WarPath" -ForegroundColor Cyan
Write-Host ""
