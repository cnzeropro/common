param(
    [Parameter(Mandatory = $true, Position = 0)]
    [ValidateSet('sync', 'verify')]
    [string]$Command,
    [switch]$VerboseOutput
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Write-BuildMetadataLog {
    param([string]$Message)

    if ($VerboseOutput) {
        [Console]::Error.WriteLine("[build-metadata] $Message")
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

Write-BuildMetadataLog 'toolchain ready'

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
        $javaArgs = @('-cp', $classesDir, 'org.zero.build.metadata.BuildMetadataCli', $Command, $rootDir)
        if ($VerboseOutput) {
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
