param(
    [switch]$VerboseOutput
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

if ($VerboseOutput) {
    [Console]::Error.WriteLine('[build-metadata] wrapper dispatch verify')
}

if ($VerboseOutput) {
    & (Join-Path $PSScriptRoot 'build-metadata.ps1') -Command verify -VerboseOutput
} else {
    & (Join-Path $PSScriptRoot 'build-metadata.ps1') -Command verify
}
exit $LASTEXITCODE
