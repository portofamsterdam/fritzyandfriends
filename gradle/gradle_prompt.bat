::
:: prompt.bat - Creates a new prompt initialized with environment parameters.
::
@echo on
setlocal

:: include/configure environment specifics from 'include' file
call "%~dp0%gradle_env.bat"
if errorlevel 1 ( echo Script %~n0 can't load project variables. & exit /B )

start "Eneco[gradle]" /I /D "%~dp0" "%~dp0gradle_env.bat"
