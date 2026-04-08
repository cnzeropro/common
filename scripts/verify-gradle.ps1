Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$usage = 'Usage: verify-gradle.ps1 [--verbose|-v|-VerboseOutput]'
$verbose = $false

function Write-Usage { [Console]::Error.WriteLine($usage) }

if ($args.Count -gt 1) { Write-Usage; exit 1 }
if ($args.Count -eq 1) {
	switch ([string]$args[0]) {
		'--verbose' { $verbose = $true }
		'-v' { $verbose = $true }
		'-VerboseOutput' { $verbose = $true }
		default { Write-Usage; exit 1 }
	}
}

if ($verbose) { [Console]::Error.WriteLine('[manage-metadata] wrapper dispatch verify-gradle') }
if ($verbose) { & (Join-Path $PSScriptRoot 'manage-metadata.ps1') verify-gradle '--verbose' } else { & (Join-Path $PSScriptRoot 'manage-metadata.ps1') verify-gradle }
exit $LASTEXITCODE
