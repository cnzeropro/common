@echo off
setlocal EnableExtensions DisableDelayedExpansion

set "USAGE=Usage: sync.bat [gradle^|maven] [--verbose^|-v]"
set "FORWARD_VERBOSE="
set "TARGET="
set "COMMAND=sync"
for %%I in ("%~dp0internal\manage.bat") do set "MANAGE_SCRIPT=%%~fI"

:parse_args
if "%~1"=="" goto args_ok
if /I "%~1"=="gradle" (
	if defined TARGET goto usage
	set "TARGET=gradle"
	shift
	goto parse_args
)
if /I "%~1"=="maven" (
	if defined TARGET goto usage
	set "TARGET=maven"
	shift
	goto parse_args
)
if /I "%~1"=="--verbose" (
	if defined FORWARD_VERBOSE goto usage
	set "FORWARD_VERBOSE=--verbose"
	shift
	goto parse_args
)
if /I "%~1"=="-v" (
	if defined FORWARD_VERBOSE goto usage
	set "FORWARD_VERBOSE=--verbose"
	shift
	goto parse_args
)
goto usage

:usage
>&2 echo %USAGE%
exit /b 1

:args_ok
if /I "%TARGET%"=="gradle" set "COMMAND=sync-gradle"
if /I "%TARGET%"=="maven" set "COMMAND=sync-maven"

if defined FORWARD_VERBOSE (
	>&2 echo [manage-metadata] wrapper dispatch %COMMAND%
	call "%MANAGE_SCRIPT%" %COMMAND% %FORWARD_VERBOSE%
) else (
	call "%MANAGE_SCRIPT%" %COMMAND%
)
exit /b %ERRORLEVEL%
