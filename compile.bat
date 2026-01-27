@echo off
REM Script de compilacion para Windows
REM Sistema de Gestion de Alumnos

echo =====================================
echo   Sistema de Gestion de Alumnos
echo   Script de Compilacion
echo =====================================
echo.

REM Verificar Java
echo [1/2] Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no encontrado
    echo Por favor instala Java 17 o superior desde: https://adoptium.net/
    echo Consulta MAVEN_SETUP.md para mas informacion
    pause
    exit /b 1
)
echo [OK] Java encontrado
echo.

REM Verificar Maven
echo [2/2] Verificando Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven no encontrado
    echo.
    echo Maven no esta instalado o no esta en el PATH del sistema
    echo.
    echo Soluciones:
    echo   1. Instala Maven siguiendo MAVEN_SETUP.md
    echo   2. Descarga desde: https://maven.apache.org/download.cgi
    echo   3. O usa Chocolatey: choco install maven
    echo.
    pause
    exit /b 1
)
echo [OK] Maven encontrado
echo.

echo =====================================
echo   Compilando proyecto...
echo =====================================
echo.
echo Esto puede tomar algunos minutos la primera vez...
echo.

REM Compilar el proyecto
mvn clean install -DskipTests

if errorlevel 1 (
    echo.
    echo [ERROR] La compilacion fallo
    echo Revisa los mensajes de error arriba
    pause
    exit /b 1
)

echo.
echo =====================================
echo   Compilacion exitosa!
echo =====================================
echo.
echo El proyecto ha sido compilado correctamente.
echo.
echo Opciones para ejecutar:
echo   1. mvn javafx:run
echo   2. .\run.ps1
echo   3. Ejecutar desde IntelliJ IDEA
echo.
pause
