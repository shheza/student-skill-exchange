@echo off
setlocal enabledelayedexpansion
title SkillX - Student Skill Exchange System

echo =======================================================
echo   SkillX - Student Skill Exchange System
echo   College OOP Mini Project (Full-Stack Java + Web)
echo =======================================================
echo.

rem Check portable JDK or system Java
set JAVAC_CMD=javac
set JAVA_CMD=java

for /d %%D in ("%~dp0backend\jdk\jdk-*") do (
    if exist "%%D\bin\javac.exe" (
        set "JAVAC_CMD=%%D\bin\javac.exe"
        set "JAVA_CMD=%%D\bin\java.exe"
        goto :FOUND_JDK
    )
)

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        goto :FOUND_JDK
    )
)

:FOUND_JDK
echo Java Compiler: %JAVAC_CMD%
echo Java Runtime:  %JAVA_CMD%
echo.

rem Ensure bin directory
if not exist "%~dp0backend\bin" mkdir "%~dp0backend\bin"

echo [1/3] Compiling Java OOP Backend...
"%JAVAC_CMD%" -encoding UTF-8 -d "%~dp0backend\bin" "%~dp0backend\src\models\*.java" "%~dp0backend\src\service\*.java" "%~dp0backend\src\server\*.java" "%~dp0backend\src\Main.java"
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Backend compilation failed!
    pause
    exit /b %ERRORLEVEL%
)
echo [OK] Compilation successful!
echo.

echo [2/3] Launching SkillX Browser Application...
start "" cmd /c "timeout /t 2 /nobreak >nul & start http://localhost:8080"

echo [3/3] Starting SkillX REST Server on http://localhost:8080 ...
echo Press Ctrl+C anytime to stop the server.
echo.
"%JAVA_CMD%" -cp "%~dp0backend\bin" Main 8080
pause
