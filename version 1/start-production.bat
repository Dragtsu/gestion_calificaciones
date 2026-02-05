@echo off
REM Script para ejecutar la aplicación en producción
REM Sistema de Gestión de Alumnos

echo ========================================
echo   Sistema de Gestión de Alumnos v1.0
echo ========================================
echo.

set JAR_FILE=target\Alumnos-1.0-SNAPSHOT.jar

REM Verificar que existe el JAR
if not exist "%JAR_FILE%" (
    echo [ERROR] No se encontró el archivo ejecutable
    echo.
    echo Por favor, ejecuta primero:
    echo   build-production.ps1
    echo.
    pause
    exit /b 1
)

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no está instalado
    echo Por favor instale Java 17+ desde: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

echo Iniciando aplicación...
echo.

REM Ejecutar la aplicación
java -jar "%JAR_FILE%"

if errorlevel 1 (
    echo.
    echo [ERROR] La aplicación terminó con errores
    pause
)
