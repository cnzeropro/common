@echo off
setlocal

set "USAGE=Usage: verify-build-metadata.bat [--verbose|-v]"
set "FORWARD_VERBOSE="

if "%~1"=="" goto args_ok
if /I "%~1"=="--verbose" (
	if not "%~2"=="" goto usage
	set "FORWARD_VERBOSE=%~1"
	goto args_ok
)
if /I "%~1"=="-v" (
	if not "%~2"=="" goto usage
	set "FORWARD_VERBOSE=%~1"
	goto args_ok
)
goto usage

:usage
>&2 echo %USAGE%
exit /b 1

:args_ok
if defined FORWARD_VERBOSE (
	>&2 echo [build-metadata] wrapper dispatch verify
	call "%~dp0build-metadata.bat" verify %FORWARD_VERBOSE%
) else (
	call "%~dp0build-metadata.bat" verify
)
exit /b %ERRORLEVEL%
