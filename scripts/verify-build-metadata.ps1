Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

<#
verify wrapper - delegate the verify command to the shared metadata script.
#>
& (Join-Path $PSScriptRoot 'build-metadata.ps1') 'verify'
