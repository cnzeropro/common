Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$usage = 'Usage: sync.ps1 [gradle|maven] [--verbose|-v|-VerboseOutput]'
$verbose = $false
$target = $null
$command = 'sync'

function Write-Usage {
    [Console]::Error.WriteLine($usage)
}

foreach ($argItem in $args) {
    switch ([string]$argItem) {
        'gradle' {
            if ($null -ne $target) {
                Write-Usage
                exit 1
            }
            $target = 'gradle'
        }
        'maven' {
            if ($null -ne $target) {
                Write-Usage
                exit 1
            }
            $target = 'maven'
        }
        '--verbose' {
            if ($verbose) {
                Write-Usage
                exit 1
            }
            $verbose = $true
        }
        '-v' {
            if ($verbose) {
                Write-Usage
                exit 1
            }
            $verbose = $true
        }
        '-VerboseOutput' {
            if ($verbose) {
                Write-Usage
                exit 1
            }
            $verbose = $true
        }
        default {
            Write-Usage
            exit 1
        }
    }
}

if ($target -eq 'gradle') {
    $command = 'sync-gradle'
} elseif ($target -eq 'maven') {
    $command = 'sync-maven'
}

if ($verbose) {
    [Console]::Error.WriteLine("[manage-metadata] wrapper dispatch $command")
    & (Join-Path $PSScriptRoot 'internal\manage.ps1') $command '--verbose'
} else {
    & (Join-Path $PSScriptRoot 'internal\manage.ps1') $command
}
exit $LASTEXITCODE
