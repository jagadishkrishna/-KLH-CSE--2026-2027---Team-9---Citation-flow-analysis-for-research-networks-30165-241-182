@echo off
title CitationFlow - Citation Flow Analysis for Research Networks
echo ===================================================================
echo   CitationFlow: Citation Flow Analysis for Research Networks
echo   Academic DSA Project - Directed Graph & Algorithms
echo ===================================================================

echo [*] Checking Java version...
java -version
if %errorlevel% neq 0 (
    echo [!] Java 17+ is required. Please install Java and add it to PATH.
    pause
    exit /b 1
)

echo [*] Compiling Java source files to bin/...
if not exist "bin" mkdir bin
javac -d bin src/model/*.java src/dsa/*.java src/data/*.java src/service/*.java src/server/*.java src/test/*.java src/Main.java

if %errorlevel% neq 0 (
    echo [!] Compilation failed! Please check error output above.
    pause
    exit /b 1
)

echo [+] Compilation successful!
echo [*] Running automated DSA test suite...
java -cp bin test.TestRunner

if %errorlevel% neq 0 (
    echo [!] Tests failed!
    pause
    exit /b 1
)

echo.
echo [*] Starting CitationFlow HTTP Server and Web Dashboard on port 8080...
start http://localhost:8080/index.html
java -cp bin Main 8080

pause
