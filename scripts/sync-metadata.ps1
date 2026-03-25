Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$usage = 'Usage: sync-metadata.ps1 [--verbose|-v|-VerboseOutput]'
$verbose = $false

function Write-Usage {
    [Console]::Error.WriteLine($usage)
}

if ($args.Count -gt 1) {
    Write-Usage
    exit 1
}

if ($args.Count -eq 1) {
    switch ([string]$args[0]) {
        '--verbose' { $verbose = $true }
        '-v' { $verbose = $true }
        '-VerboseOutput' { $verbose = $true }
        default {
            Write-Usage
            exit 1
        }
    }
}

if ($verbose) {
    [Console]::Error.WriteLine('[manage-metadata] wrapper dispatch sync')
}

if ($verbose) {
    & (Join-Path $PSScriptRoot 'manage-metadata.ps1') sync '--verbose'
} else {
    & (Join-Path $PSScriptRoot 'manage-metadata.ps1') sync
}
exit $LASTEXITCODE
