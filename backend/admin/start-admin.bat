@echo off
echo ========================================
echo   PAWHELP ADMIN PANEL - HTTP SERVER
echo ========================================
echo.
echo Starting HTTP server on port 8000...
echo.
echo Admin Panel will be available at:
echo   http://localhost:8000
echo.
echo Press Ctrl+C to stop the server
echo ========================================
echo.

cd /d %~dp0
python -m http.server 8000

