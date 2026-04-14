Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$RootDir = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$CommonDataTest = 'org.zero.common.data.enumeration.HttpStatusTest'
$CoreBaseTest = 'org.zero.common.core.util.java.EnumUtilTest'
$CoreBaseMultiReleaseTest = 'org.zero.common.core.util.java.lang.StackUtilMultiReleaseTest'

function Write-Step {
	param([string]$Message)
	[Console]::Error.WriteLine("[ci-verify] $Message")
}

function Show-EnvVar {
	param([string]$Name)
	$Value = [Environment]::GetEnvironmentVariable($Name)
	if ([string]::IsNullOrWhiteSpace($Value)) {
		[Console]::Error.WriteLine("{0}=<unset>" -f $Name)
		return
	}
	[Console]::Error.WriteLine(("{0}={1}" -f $Name, $Value))
}

function Invoke-Step {
	param(
		[string]$Description,
		[scriptblock]$Action
	)

	Write-Step $Description
	& $Action
	if ($LASTEXITCODE -ne 0) {
		throw "Step failed: $Description"
	}
}

$isWindowsShell = $env:OS -eq 'Windows_NT'

if ($isWindowsShell) {
	$GradleWrapper = Join-Path $RootDir 'gradlew.bat'
	$MavenWrapper = Join-Path $RootDir 'mvnw.cmd'
} else {
	$GradleWrapper = Join-Path $RootDir 'gradlew'
	$MavenWrapper = Join-Path $RootDir 'mvnw'
}

Set-Location $RootDir
Write-Step "repository root: $RootDir"
Show-EnvVar 'JAVA_HOME'
Show-EnvVar 'JDK11_HOME'
Show-EnvVar 'JDK17_HOME'
Show-EnvVar 'JDK21_HOME'
Show-EnvVar 'JDK25_HOME'

Invoke-Step 'java -version' { & java -version }
Invoke-Step 'javac -version' { & javac -version }
Invoke-Step 'gradle wrapper version' { & $GradleWrapper --version }
Invoke-Step 'maven wrapper version' { & $MavenWrapper -version }

Invoke-Step 'verify metadata projections' { & (Join-Path $RootDir 'scripts\metadata\verify.ps1') '--verbose' }

Invoke-Step 'run stable Gradle test slice' {
	& $GradleWrapper `
		'--no-daemon' `
		'-PexcludeProjects=common-test' `
		':common-data:test' `
		'--tests' $CommonDataTest `
		':core-base:test' `
		'--tests' $CoreBaseTest `
		':core-base:testJava11' `
		'--tests' $CoreBaseMultiReleaseTest `
		':core-base:testJava17' `
		':core-base:testJava21'
}

Invoke-Step 'run stable Maven test slice for common-data' {
	& $MavenWrapper `
		'-B' `
		'-ntp' `
		'-pl' 'common-data' `
		'-am' `
		'-Dtest=HttpStatusTest' `
		'test'
}

Invoke-Step 'run stable Maven verify slice for core-base multi-release tests' {
	& $MavenWrapper `
		'-B' `
		'-ntp' `
		'-pl' 'common-core/core-base' `
		'-am' `
		'-Dtest=EnumUtilTest' `
		'-Dsurefire.failIfNoSpecifiedTests=false' `
		'-Dit.test=StackUtilMultiReleaseTest' `
		'-Dfailsafe.failIfNoSpecifiedTests=false' `
		'verify'
}

Write-Step 'verification completed'
