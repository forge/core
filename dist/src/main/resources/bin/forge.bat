@REM ----------------------------------------------------------------------------
@REM Copyright 2012 Red Hat, Inc. and/or its affiliates.
@REM
@REM Licensed under the Eclipse Public License version 1.0, available at
@REM http://www.eclipse.org/legal/epl-v10.html
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Forge Startup script
@REM
@REM Required Environment vars:
@REM ------------------
@REM JAVA_HOME - location of a JRE home dir
@REM
@REM Optional Environment vars
@REM ------------------
@REM FORGE_HOME - location of Forge's installed home dir
@REM FORGE_OPTS - parameters passed to the Java VM when running Forge
@REM ----------------------------------------------------------------------------

@echo off

@REM set %USERHOME% to equivalent of $HOME
if not "%USERHOME%" == "" goto OkUserhome
set "USERHOME=%USERPROFILE%"

if not "%USERHOME%" == "" goto OkUserhome
set "USERHOME=%HOMEDRIVE%%HOMEPATH%"

:OkUserhome

@REM Execute a user defined script before this one
if exist "%USERHOME%\forgerc_pre.bat" call "%USERHOME%\forgerc_pre.bat"

set ERROR_CODE=0

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto chkJVersion

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = "%JAVA_HOME%"
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:chkJVersion
set PATH="%JAVA_HOME%\bin";%PATH%

for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
   set JAVAVER=%%g
)
for /f "delims=. tokens=1-3" %%v in ("%JAVAVER%") do (
   set JAVAVER_MINOR=%%w
)

if %JAVAVER_MINOR% geq 6 goto chkFHome

echo.
echo A Java 1.6 or higher JRE is required to run Forge. "%JAVA_HOME%\bin\java.exe" is version %JAVAVER%
echo.
goto error

:chkFHome
if not "%FORGE_HOME%"=="" goto valFHome

if "%OS%"=="Windows_NT" SET "FORGE_HOME=%~dp0.."
if "%OS%"=="WINNT" SET "FORGE_HOME=%~dp0.."
if not "%FORGE_HOME%"=="" goto valFHome

echo.
echo ERROR: FORGE_HOME not found in your environment.
echo Please set the FORGE_HOME variable in your environment to match the
echo location of the Forge installation
echo.
goto error

:valFHome

:stripFHome
if not "_%FORGE_HOME:~-1%"=="_\" goto checkFBat
set "FORGE_HOME=%FORGE_HOME:~0,-1%"
goto stripFHome

:checkFBat
if exist "%FORGE_HOME%\bin\forge.bat" goto init

echo.
echo ERROR: FORGE_HOME is set to an invalid directory.
echo FORGE_HOME = "%FORGE_HOME%"
echo Please set the FORGE_HOME variable in your environment to match the
echo location of the Forge installation
echo.
goto error
@REM ==== END VALIDATION ====

@REM Initializing the argument line and the plugin directory if any
:init
set FORGE_CMD_LINE_ARGS=
set FORGE_PLUGIN_DIR=
set FORGE_DEBUG_ARGS=
:initArgs
if %1a==a goto endInit
set FORGE_CMD_LINE_ARGS=%FORGE_CMD_LINE_ARGS% %1
if "%FORGE_PLUGIN_DIR%"=="-pluginDir" set FORGE_PLUGIN_DIR=%1
if "%1"=="-pluginDir" set FORGE_PLUGIN_DIR=%1
if "%1"=="--debug" set FORGE_DEBUG_ARGS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
shift
goto initArgs
@REM Reaching here means variables are defined and arguments have been captured
:endInit

SET FORGE_JAVA_EXE="%JAVA_HOME%\bin\java.exe"

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTCWJars

set JBOSS_MODULES="%FORGE_HOME%\jboss-modules.jar"
goto runForge

@REM Start Forge
:runForge
set FORGE_MAIN_CLASS=org.jboss.forge.shell.Bootstrap
%FORGE_JAVA_EXE% %FORGE_DEBUG_ARGS% %FORGE_OPTS% "-Dforge.home=%FORGE_HOME%" -Dforge.shell.colorEnabled=true -jar %JBOSS_MODULES% -modulepath "%FORGE_HOME%\modules;%USERHOME%\.forge\plugins;%FORGE_PLUGIN_DIR%" org.jboss.forge %FORGE_CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
IF EXIST "%FORGE_HOME%\.update\" (
	rmdir /S/Q "%FORGE_HOME%\modules"
	xcopy /S /Q /Y "%FORGE_HOME%\.update" "%FORGE_HOME%\" > NUL
	rmdir /S/Q "%FORGE_HOME%\.update"
	goto runForge
)
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
set ERROR_CODE=1


:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT
if "%OS%"=="WINNT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set FORGE_JAVA_EXE=
set FORGE_CMD_LINE_ARGS=
goto postExec

:endNT
@endlocal & set ERROR_CODE=%ERROR_CODE%

:postExec
if exist "%USERHOME%\forgerc_post.bat" call "%USERHOME%\forgerc_post.bat"

if "%FORGE_TERMINATE_CMD%" == "on" exit %ERROR_CODE%

cmd /C exit /B %ERROR_CODE%

