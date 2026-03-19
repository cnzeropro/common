Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

<#
sync wrapper - delegate the sync command to the shared metadata script.
#>
& (Join-Path $PSScriptRoot 'build-metadata.ps1') 'sync'
