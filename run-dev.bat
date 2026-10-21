@echo off
echo ==============================================================================
echo Launching SENTINEL Platform (Backend + Frontend)...
echo Backend:  http://localhost:8080/api (Swagger: http://localhost:8080/swagger-ui.html)
echo Frontend: http://localhost:5173
echo ==============================================================================

start "SENTINEL Backend" cmd /k "%~dp0run-backend.bat"
start "SENTINEL Frontend" cmd /k "%~dp0run-frontend.bat"

echo.
echo Both services have been launched in separate windows!
echo Once started, navigate your browser to: http://localhost:5173
echo.
