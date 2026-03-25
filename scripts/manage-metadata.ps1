Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$usage = 'Usage: manage-metadata.ps1 <sync|verify> [--verbose|-v|-VerboseOutput]'
$verbose = $false
$command = $null

function Write-Usage {
    [Console]::Error.WriteLine($usage)
}

if ($args.Count -lt 1 -or $args.Count -gt 2) {
    Write-Usage
    exit 1
}

$command = [string]$args[0]
if ($command -ne 'sync' -and $command -ne 'verify') {
    Write-Usage
    exit 1
}

if ($args.Count -eq 2) {
    switch ([string]$args[1]) {
        '--verbose' { $verbose = $true }
        '-v' { $verbose = $true }
        '-VerboseOutput' { $verbose = $true }
        default {
            Write-Usage
            exit 1
        }
    }
}

function Write-BuildMetadataLog {
    param([string]$Message)

    if ($verbose) {
        [Console]::Error.WriteLine("[manage-metadata] $Message")
    }
}

Write-BuildMetadataLog 'arguments parsed'

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$rootDir = (Resolve-Path (Join-Path $scriptDir '..')).Path
$sourceDir = Join-Path $scriptDir 'build-metadata\src\main\java'
$buildRootDir = Join-Path $rootDir 'build\build-metadata-cli'
$buildDir = Join-Path $buildRootDir ([string]$PID)
$classesDir = Join-Path $buildDir 'classes'
$sourceListFile = Join-Path $buildDir 'sources.txt'
Write-BuildMetadataLog "paths ready: root=$rootDir"

New-Item -ItemType Directory -Force $classesDir | Out-Null

$javac = Get-Command javac -ErrorAction SilentlyContinue
if (-not $javac) {
    throw 'Missing javac command. Please configure a JDK and ensure javac is on PATH.'
}

$java = Get-Command java -ErrorAction SilentlyContinue
if (-not $java) {
    throw 'Missing java command. Please configure a JDK and ensure java is on PATH.'
}

Write-BuildMetadataLog "toolchain ready: javac=$($javac.Source), java=$($java.Source)"

Get-ChildItem -Path $sourceDir -Recurse -Filter *.java |
    Sort-Object FullName |
    ForEach-Object { $_.FullName } |
    Set-Content -Path $sourceListFile -Encoding Ascii

$sourceCount = @(Get-Content -Path $sourceListFile).Count
Write-BuildMetadataLog "source list ready: $sourceCount file(s)"

$sourceListArg = "@$sourceListFile"
$exitCode = 0

try {
    Write-BuildMetadataLog 'javac start'
    & $javac.Source -encoding UTF-8 -d $classesDir $sourceListArg
    $exitCode = $LASTEXITCODE
    Write-BuildMetadataLog "javac done: exit=$exitCode"
    if ($exitCode -eq 0) {
        $javaArgs = @('-cp', $classesDir, 'org.zero.build.metadata.BuildMetadataCli', $command, $rootDir)
        if ($verbose) {
            $javaArgs += '--verbose'
        }

        Write-BuildMetadataLog 'java cli start'
        & $java.Source @javaArgs
        $exitCode = $LASTEXITCODE
        Write-BuildMetadataLog "java cli done: exit=$exitCode"
    }
} finally {
    Write-BuildMetadataLog 'cleanup start'
    if (Test-Path $buildDir) {
        Remove-Item -Path $buildDir -Recurse -Force
    }
    Write-BuildMetadataLog 'cleanup done'
}

exit $exitCode
