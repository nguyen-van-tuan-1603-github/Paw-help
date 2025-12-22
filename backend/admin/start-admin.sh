#!/bin/bash

echo "========================================"
echo "  PAWHELP ADMIN PANEL - HTTP SERVER"
echo "========================================"
echo ""
echo "Starting HTTP server on port 8000..."
echo ""
echo "Admin Panel will be available at:"
echo "  http://localhost:8000"
echo ""
echo "Press Ctrl+C to stop the server"
echo "========================================"
echo ""

cd "$(dirname "$0")"
python3 -m http.server 8000

