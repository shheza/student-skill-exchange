@echo off
setlocal enabledelayedexpansion
title SkillX Java Backend Server

echo =======================================================
echo   SkillX ? Student Skill Exchange System (Java Backend)
echo   College OOP Mini Project Server
echo =======================================================
echo.

rem 1. Check for local portable JDK in backend/jdk
set JAVAC_CMD=javac
set JAVA_CMD=java

for /d %%D in ("%~dp0jdk\jdk-*") do (
    if exist "%%D\bin\javac.exe" (
        set "JAVAC_CMD=%%D\bin\javac.exe"
        set "JAVA_CMD=%%D\bin\java.exe"
        goto :FOUND_JDK
    )
)

rem 2. Check JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        goto :FOUND_JDK
    )
)

:FOUND_JDK
echo Using Java Compiler: %JAVAC_CMD%
echo Using Java Runtime:  %JAVA_CMD%
echo.

rem 3. Ensure bin directory exists
if not exist "%~dp0bin" mkdir "%~dp0bin"

echo [1/2] Compiling Java OOP classes with SQLite Database...
"%JAVAC_CMD%" -encoding UTF-8 -cp "%~dp0lib\*" -d "%~dp0bin" "%~dp0src\models\*.java" "%~dp0src\database\*.java" "%~dp0src\service\*.java" "%~dp0src\server\*.java" "%~dp0src\Main.java"
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [OK] Compilation successful!
echo.
echo [2/2] Starting SkillX REST API Server on port 8080...
echo.
"%JAVA_CMD%" -cp "%~dp0lib\*;%~dp0bin" Main 8080
pause