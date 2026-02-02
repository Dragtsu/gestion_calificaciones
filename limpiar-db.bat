@echo off
REM ============================================================================
REM Script BAT para Limpiar la Base de Datos
REM ============================================================================

setlocal

echo ============================================================================
echo   Script de Limpieza de Base de Datos - Alumnos
echo ============================================================================
echo.

REM Verificar argumentos
set TIPO=%1
if "%TIPO%"=="" set TIPO=produccion

echo Tipo de limpieza: %TIPO%
echo.

REM Ejecutar script de PowerShell
powershell -ExecutionPolicy Bypass -File "limpiar-db.ps1" -Tipo %TIPO%

if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: La limpieza de la base de datos falló
    pause
    exit /b 1
)

echo.
echo Limpieza completada exitosamente
pause
