#!/usr/bin/env bash
# CitationFlow Launcher for Linux / macOS / WSL

echo "==================================================================="
echo "  CitationFlow: Citation Flow Analysis for Research Networks"
echo "  Academic DSA Project - Directed Graph & Algorithms"
echo "==================================================================="

mkdir -p bin
echo "[*] Compiling Java source files..."
javac -d bin src/model/*.java src/dsa/*.java src/data/*.java src/service/*.java src/server/*.java src/test/*.java src/Main.java

if [ $? -ne 0 ]; then
    echo "[-] Compilation failed."
    exit 1
fi

echo "[+] Compilation successful."
echo "[*] Running automated DSA test suite..."
java -cp bin test.TestRunner

if [ $? -ne 0 ]; then
    echo "[-] Test suite failed."
    exit 1
fi

echo ""
echo "[*] Starting CitationFlow HTTP Server on http://localhost:8080..."
java -cp bin Main 8080
