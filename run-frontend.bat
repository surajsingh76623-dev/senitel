@echo off
echo ==============================================================================
echo Starting SENTINEL Frontend (React 18 / Vite / Tailwind CSS)...
echo Web UI will be available at http://localhost:5173
echo ==============================================================================

set "PATH=C:\Program Files\nodejs;%PATH%"

cd /d "%~dp0frontend"
npm run dev
