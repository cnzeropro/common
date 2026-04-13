@echo off
setlocal EnableExtensions DisableDelayedExpansion

call :main %*
set "EXIT_CODE=%ERRORLEVEL%"
call :cleanup
exit /b %EXIT_CODE%

:main
set "USAGE=Usage: manage.bat ^<sync^|verify^|sync-gradle^|verify-gradle^|sync-maven^|verify-maven^> [--verbose^|-v]"
set "VERBOSE="

if /I "%~1"=="sync" (
	set "COMMAND=sync"
	goto parse_flag
)
if /I "%~1"=="verify" (
	set "COMMAND=verify"
	goto parse_flag
)
if /I "%~1"=="sync-gradle" (
	set "COMMAND=sync-gradle"
	goto parse_flag
)
if /I "%~1"=="verify-gradle" (
	set "COMMAND=verify-gradle"
	goto parse_flag
)
if /I "%~1"=="sync-maven" (
	set "COMMAND=sync-maven"
	goto parse_flag
)
if /I "%~1"=="verify-maven" (
	set "COMMAND=verify-maven"
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
for %%I in ("%SCRIPT_DIR%..\..\..") do set "ROOT_DIR=%%~fI"
set "SOURCE_DIR=%SCRIPT_DIR%cli\src\main\java"
set "BUILD_ROOT_DIR=%ROOT_DIR%\build\build-metadata-cli"
set "SOURCE_COUNT=0"
set "JAVAC_PATH="
set "JAVA_PATH="
call :log "paths ready: root=%ROOT_DIR%"

call :create_build_dir
if errorlevel 1 (
	>&2 echo Failed to create build directory under "%BUILD_ROOT_DIR%".
	exit /b 1
)
set "SOURCE_LIST_FILE=%BUILD_DIR%\sources.txt"

for /f "delims=" %%I in ('where javac 2^>nul') do if not defined JAVAC_PATH set "JAVAC_PATH=%%I"
if not defined JAVAC_PATH (
	>&2 echo Missing javac command. Please configure a JDK and ensure javac is on PATH.
	exit /b 1
)

for /f "delims=" %%I in ('where java 2^>nul') do if not defined JAVA_PATH set "JAVA_PATH=%%I"
if not defined JAVA_PATH (
	>&2 echo Missing java command. Please configure a JDK and ensure java is on PATH.
	exit /b 1
)

call :log "toolchain ready: javac=%JAVAC_PATH%, java=%JAVA_PATH%"

type nul > "%SOURCE_LIST_FILE%"
for /f "delims=" %%F in ('dir /b /s /a:-d /o:n "%SOURCE_DIR%\*.java"') do call :append_source "%%~fF"
if errorlevel 1 (
	>&2 echo Failed to collect Java sources from "%SOURCE_DIR%".
	exit /b 1
)
for /f %%I in ('find /v /c "" ^< "%SOURCE_LIST_FILE%"') do set "SOURCE_COUNT=%%I"

call :log "source list ready: %SOURCE_COUNT% relative file(s)"
call :log "javac start"
pushd "%ROOT_DIR%" >nul
"%JAVAC_PATH%" -encoding UTF-8 -d "%CLASSES_DIR%" @"%SOURCE_LIST_FILE%"
set "EXIT_CODE=%ERRORLEVEL%"
popd >nul
call :log "javac done: exit=%EXIT_CODE%"
if not "%EXIT_CODE%"=="0" exit /b %EXIT_CODE%

call :log "java cli start"
if defined VERBOSE (
	"%JAVA_PATH%" -cp "%CLASSES_DIR%" org.zero.build.metadata.BuildMetadataCli %COMMAND% "%ROOT_DIR%" --verbose
) else (
	"%JAVA_PATH%" -cp "%CLASSES_DIR%" org.zero.build.metadata.BuildMetadataCli %COMMAND% "%ROOT_DIR%"
)
set "EXIT_CODE=%ERRORLEVEL%"
call :log "java cli done: exit=%EXIT_CODE%"
exit /b %EXIT_CODE%

:cleanup
call :log "cleanup start"
if defined BUILD_DIR if exist "%BUILD_DIR%" (
	rmdir /s /q "%BUILD_DIR%" >nul 2>&1
)
call :log "cleanup done"
exit /b 0

:create_build_dir
setlocal EnableDelayedExpansion
if not exist "%BUILD_ROOT_DIR%" (
	mkdir "%BUILD_ROOT_DIR%" >nul 2>&1
)
for /l %%I in (1,1,32) do (
	set "RUN_TOKEN=!TIME!_%%I_!RANDOM!!RANDOM!"
	set "RUN_TOKEN=!RUN_TOKEN: =0!"
	set "RUN_TOKEN=!RUN_TOKEN::=_!"
	set "RUN_TOKEN=!RUN_TOKEN:.=_!"
	set "RUN_TOKEN=!RUN_TOKEN:,=_!"
	set "RUN_TOKEN=!RUN_TOKEN:/=_!"
	set "RUN_TOKEN=!RUN_TOKEN:\=_!"
	set "RUN_TOKEN=!RUN_TOKEN:-=_!"
	set "CANDIDATE_BUILD_DIR=%BUILD_ROOT_DIR%\!RUN_TOKEN!"
	mkdir "!CANDIDATE_BUILD_DIR!\classes" >nul 2>&1 && (
		for %%P in ("!CANDIDATE_BUILD_DIR!") do (
			endlocal
			set "BUILD_DIR=%%~fP"
			set "CLASSES_DIR=%%~fP\classes"
			exit /b 0
		)
	)
)
endlocal
exit /b 1

:log
if not defined VERBOSE exit /b 0
>&2 echo [manage-metadata] %~1
exit /b 0

:append_source
set "ABS_PATH=%~1"
setlocal EnableDelayedExpansion
set "REL_PATH=!ABS_PATH:%ROOT_DIR%\=!"
set "REL_PATH=!REL_PATH:\=/!"
>>"%SOURCE_LIST_FILE%" echo !REL_PATH!
endlocal
exit /b 0
