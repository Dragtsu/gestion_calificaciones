@echo off
REM Script para generar ejecutable de producción
REM Sistema de Gestión de Alumnos

echo ========================================
echo   Generando Ejecutable de Producción
echo ========================================
echo.

REM Buscar Maven en ubicaciones comunes
set MAVEN_CMD=

REM Verificar si mvn está en PATH
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    set MAVEN_CMD=mvn
    goto :maven_found
)

REM Buscar en instalaciones comunes de Maven
if exist "C:\Program Files\Apache\maven\bin\mvn.cmd" (
    set MAVEN_CMD="C:\Program Files\Apache\maven\bin\mvn.cmd"
    goto :maven_found
)

if exist "C:\apache-maven\bin\mvn.cmd" (
    set MAVEN_CMD="C:\apache-maven\bin\mvn.cmd"
    goto :maven_found
)

if exist "%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.7-bin\3k9n615lchs6mp84v355m633uo\apache-maven-3.9.7\bin\mvn.cmd" (
    set MAVEN_CMD="%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.7-bin\3k9n615lchs6mp84v355m633uo\apache-maven-3.9.7\bin\mvn.cmd"
    goto :maven_found
)

REM Maven no encontrado
echo [ERROR] Maven no está instalado o no se encuentra en las ubicaciones estándar
echo.
echo Por favor, usa uno de estos métodos:
echo.
echo 1. Instalar Maven globalmente:
echo    https://maven.apache.org/download.cgi
echo.
echo 2. Usar IntelliJ IDEA:
echo    - Abrir panel Maven (View -^> Tool Windows -^> Maven)
echo    - Doble clic en: Lifecycle -^> clean
echo    - Doble clic en: Lifecycle -^> package
echo.
echo 3. Ver guía completa en: COMO_GENERAR_EJECUTABLE.md
echo.
pause
exit /b 1

:maven_found
echo [OK] Maven encontrado
echo.

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no está instalado
    echo Por favor instale Java 17+ desde: https://adoptium.net/
    echo.
    pause
    exit /b 1
)
echo [OK] Java encontrado
echo.

echo Iniciando compilación...
echo.

REM Limpiar proyecto
echo [1/3] Limpiando proyecto...
%MAVEN_CMD% clean
if errorlevel 1 (
    echo.
    echo [ERROR] Falló la limpieza del proyecto
    pause
    exit /b 1
)

echo.
echo [2/3] Compilando y empaquetando...
%MAVEN_CMD% package -DskipTests
if errorlevel 1 (
    echo.
    echo [ERROR] Falló la compilación
    pause
    exit /b 1
)

echo.
echo [3/3] Verificando ejecutable...

set JAR_FILE=target\Alumnos-1.0-SNAPSHOT.jar
if exist "%JAR_FILE%" (
    echo.
    echo ========================================
    echo   Ejecutable generado exitosamente
    echo ========================================
    echo.
    echo Archivo: %JAR_FILE%
    for %%A in ("%JAR_FILE%") do echo Tamaño: %%~zA bytes
    echo.
    echo Para ejecutar la aplicación:
    echo   java -jar %JAR_FILE%
    echo.
    echo O usa el script:
    echo   start-production.bat
    echo.
) else (
    echo.
    echo [ERROR] No se encontró el archivo JAR
    pause
    exit /b 1
)

pause
