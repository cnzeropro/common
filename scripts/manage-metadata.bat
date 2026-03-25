@echo off
setlocal EnableExtensions DisableDelayedExpansion

set "USAGE=Usage: manage-metadata.bat ^<sync^|verify^> [--verbose^|-v]"
set "VERBOSE="

if /I "%~1"=="sync" (
	set "COMMAND=sync"
	goto parse_flag
)
if /I "%~1"=="verify" (
	set "COMMAND=verify"
	goto parse_flag
)
goto usage

:parse_flag
if "%~2"=="" goto args_ok
if /I "%~2"=="--verbose" (
	if not "%~3"=="" goto usage
	set "VERBOSE=1"
	goto args_ok
)
if /I "%~2"=="-v" (
	if not "%~3"=="" goto usage
	set "VERBOSE=1"
	goto args_ok
)
goto usage

:usage
>&2 echo %USAGE%
exit /b 1

:args_ok
call :log "arguments parsed"

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "ROOT_DIR=%%~fI"
set "SOURCE_DIR=%SCRIPT_DIR%build-metadata\src\main\java"
set "BUILD_ROOT_DIR=%ROOT_DIR%\build\build-metadata-cli"
set "RUN_TOKEN=%RANDOM%%RANDOM%"
set "BUILD_DIR=%BUILD_ROOT_DIR%\%RUN_TOKEN%"
set "CLASSES_DIR=%BUILD_DIR%\classes"
set "SOURCE_LIST_FILE=%BUILD_DIR%\sources.txt"
set "SOURCE_COUNT=0"
set "EXIT_CODE=0"
set "JAVAC_PATH="
set "JAVA_PATH="
call :log "paths ready: root=%ROOT_DIR%"

mkdir "%CLASSES_DIR%" >nul 2>&1
if errorlevel 1 (
	>&2 echo Failed to create build directory "%CLASSES_DIR%".
	set "EXIT_CODE=1"
	goto cleanup
)

for /f "delims=" %%I in ('where javac 2^>nul') do if not defined JAVAC_PATH set "JAVAC_PATH=%%I"
if not defined JAVAC_PATH (
	>&2 echo Missing javac command. Please configure a JDK and ensure javac is on PATH.
	set "EXIT_CODE=1"
	goto cleanup
)

for /f "delims=" %%I in ('where java 2^>nul') do if not defined JAVA_PATH set "JAVA_PATH=%%I"
if not defined JAVA_PATH (
	>&2 echo Missing java command. Please configure a JDK and ensure java is on PATH.
	set "EXIT_CODE=1"
	goto cleanup
)

call :log "toolchain ready: javac=%JAVAC_PATH%, java=%JAVA_PATH%"

>"%SOURCE_LIST_FILE%" (
	for /f "delims=" %%F in ('dir /b /s /a:-d /o:n "%SOURCE_DIR%\*.java"') do (
		set /a SOURCE_COUNT+=1
		echo %%~fF
	)
)
if errorlevel 1 (
	>&2 echo Failed to collect Java sources from "%SOURCE_DIR%".
	set "EXIT_CODE=1"
	goto cleanup
)

call :log "source list ready: %SOURCE_COUNT% file(s)"
call :log "javac start"
"%JAVAC_PATH%" -encoding UTF-8 -d "%CLASSES_DIR%" @"%SOURCE_LIST_FILE%"
set "EXIT_CODE=%ERRORLEVEL%"
call :log "javac done: exit=%EXIT_CODE%"
if not "%EXIT_CODE%"=="0" goto cleanup

call :log "java cli start"
if defined VERBOSE (
	"%JAVA_PATH%" -cp "%CLASSES_DIR%" org.zero.build.metadata.BuildMetadataCli %COMMAND% "%ROOT_DIR%" --verbose
) else (
	"%JAVA_PATH%" -cp "%CLASSES_DIR%" org.zero.build.metadata.BuildMetadataCli %COMMAND% "%ROOT_DIR%"
)
set "EXIT_CODE=%ERRORLEVEL%"
call :log "java cli done: exit=%EXIT_CODE%"

:cleanup
call :log "cleanup start"
if exist "%BUILD_DIR%" (
	rmdir /s /q "%BUILD_DIR%" >nul 2>&1
)
call :log "cleanup done"
exit /b %EXIT_CODE%

:log
if not defined VERBOSE exit /b 0
>&2 echo [manage-metadata] %~1
exit /b 0
