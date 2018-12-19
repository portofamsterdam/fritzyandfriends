@ECHO off
cls
:: clean eclipse files and re-creates them based on the gradle configuration
call gradlew.bat --stacktrace cleaneclipse eclipse %*
