param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('sync', 'verify')]
    [string]$Command
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

<#
path bootstrap - derive the repository root and the temporary compilation directories.
#>
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$rootDir = (Resolve-Path (Join-Path $scriptDir '..')).Path
$sourceDir = Join-Path $scriptDir 'build-metadata\src\main\java'
$buildRootDir = Join-Path $rootDir 'build\build-metadata-cli'
$buildDir = Join-Path $buildRootDir ([string]$PID)
$classesDir = Join-Path $buildDir 'classes'
$sourceListFile = Join-Path $buildDir 'sources.txt'

New-Item -ItemType Directory -Force $classesDir | Out-Null

<#
JDK command guard - compile and run the metadata CLI with the local java and javac commands.
#>
$javac = Get-Command javac -ErrorAction SilentlyContinue
if (-not $javac) {
    throw 'Missing javac command. Please configure a JDK and ensure javac is on PATH.'
}

$java = Get-Command java -ErrorAction SilentlyContinue
if (-not $java) {
    throw 'Missing java command. Please configure a JDK and ensure java is on PATH.'
}

<#
source list file - use an argument file to avoid command-line length and escaping issues.
#>
Get-ChildItem -Path $sourceDir -Recurse -Filter *.java |
    Sort-Object FullName |
    ForEach-Object { $_.FullName } |
    Set-Content -Path $sourceListFile -Encoding Ascii

$sourceListArg = "@$sourceListFile"
try {
    <#
    compile and run - compile the helper CLI and execute the requested subcommand.
    #>
    & $javac.Source -encoding UTF-8 -d $classesDir $sourceListArg
    & $java.Source -cp $classesDir org.zero.build.metadata.BuildMetadataCli $Command $rootDir
} finally {
    <#
    temp cleanup - remove the per-run temporary directory after the command finishes.
    #>
    if (Test-Path $buildDir) {
        Remove-Item -Path $buildDir -Recurse -Force
    }
}
