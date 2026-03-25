param(
    [switch]$VerboseOutput
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

if ($VerboseOutput) {
    [Console]::Error.WriteLine('[build-metadata] wrapper dispatch sync')
}

if ($VerboseOutput) {
    & (Join-Path $PSScriptRoot 'build-metadata.ps1') -Command sync -VerboseOutput
} else {
    & (Join-Path $PSScriptRoot 'build-metadata.ps1') -Command sync
}
exit $LASTEXITCODE
