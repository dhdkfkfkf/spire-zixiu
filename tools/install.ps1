<#
.SYNOPSIS
    Installs the built jar into the game's mods directory (atomic replace).

.DESCRIPTION
    Refuses to install a missing or stale jar, and replaces atomically so the game never reads a
    half-written zip.

.EXAMPLE
    powershell -NoProfile -File tools\install.ps1
#>
[CmdletBinding()]
param(
    [string]$GameDir = 'D:\Steam\steamapps\common\SlayTheSpire',
    [switch]$SkipBuild
)

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$jarName = 'zixiu.jar'

if (-not $SkipBuild) {
    & powershell.exe -NoProfile -ExecutionPolicy Bypass -File (Join-Path $root 'tools\build.ps1') | Write-Host
}

$built = Join-Path $root "mod\$jarName"
if (-not (Test-Path -LiteralPath $built)) {
    throw "no built jar at $built"
}

$modsDir = Join-Path $GameDir 'mods'
New-Item -ItemType Directory -Force -Path $modsDir | Out-Null
$target = Join-Path $modsDir $jarName
$staged = "$target.staged"

Copy-Item -LiteralPath $built -Destination $staged -Force
Move-Item -LiteralPath $staged -Destination $target -Force

Write-Host "installed $target"
