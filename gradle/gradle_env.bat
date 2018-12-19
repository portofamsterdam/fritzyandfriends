::
:: env.bat - Initializes environment parameters for development.
::
@echo off

set	GRADLE_DIR=%~dp0%
set TOOL_DIR=GRADLE_DIR
call :c_set_path PROJ_DIR "%~dp0%..\"
call :c_set_path TOOL_DIR "%~dp0%..\..\..\tools\"

set PROJ_PATH=%PROJ_DIR:~0,-1%
set TOOL_PATH=%TOOL_DIR:~0,-1%

set JAVA_HOME=%TOOL_DIR%java\jdk-8u40-windows-x64
set MYSQL_HOME=%TOOL_DIR%mysql\mysql-8.0.12-winx64

:: NOTE path update should also work when path contains '&'
set PATH=%JAVA_HOME%\bin;%MYSQL_HOME%\bin;%PATH:"&"="&&"%
exit /B 0

:c_set_path
set %1=%~f2
goto :eof
