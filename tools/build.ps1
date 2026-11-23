<#
.SYNOPSIS
    Builds zixiu.jar: compiles against the game and mod-loader jars from the install directories.

.DESCRIPTION
    The game runs on Java 8, so the output must be Java 8 bytecode even if the compiler is newer.

.EXAMPLE
    powershell -NoProfile -File tools\build.ps1
#>
[CmdletBinding()]
param(
    [string]$Jdk = 'C:\Users\10797\.jdks\jdk8u512-b09',
    [string]$GameDir = 'D:\Steam\steamapps\common\SlayTheSpire',
    [string]$WorkshopDir = 'D:\Steam\steamapps\workshop\content\646570'
)

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$srcDir = Join-Path $root 'src\main\java'
$resDir = Join-Path $root 'src\main\resources'
$jarName = 'zixiu.jar'

$runStamp = "$PID-" + (Get-Date).ToUniversalTime().ToString('yyyyMMdd-HHmmss')
$runDir = Join-Path $root ("build\runs\build-" + $runStamp)
$classesDir = Join-Path $runDir 'classes'
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

$cp = @(
    (Join-Path $GameDir 'desktop-1.0.jar'),
    (Join-Path $WorkshopDir '1605060445\ModTheSpire.jar'),
    (Join-Path $WorkshopDir '1605833019\BaseMod.jar'),
    (Join-Path $WorkshopDir '1609158507\StSLib.jar')
) -join ';'

$sources = Get-ChildItem -Recurse -Path $srcDir -Filter *.java
& (Join-Path $Jdk 'bin\javac.exe') -encoding UTF-8 -source 1.8 -target 1.8 `
    -cp $cp -d $classesDir @($sources.FullName)
if ($LASTEXITCODE -ne 0) {
    throw "javac failed with exit code $LASTEXITCODE"
}

Copy-Item -Recurse -Force (Join-Path $resDir '*') $classesDir

$modDir = Join-Path $root 'mod'
New-Item -ItemType Directory -Force -Path $modDir | Out-Null
$jarPath = Join-Path $modDir $jarName
& (Join-Path $Jdk 'bin\jar.exe') cf $jarPath -C $classesDir .

Write-Host "built $jarPath"
