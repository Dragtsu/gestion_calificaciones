# Script para generar ejecutable de producción
# Sistema de Gestión de Alumnos

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Generando Ejecutable de Producción   " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar Maven
try {
    $mvnVersion = mvn -version 2>&1 | Select-String "Apache Maven" | Out-String
    Write-Host "✓ Maven encontrado: $($mvnVersion.Trim())" -ForegroundColor Green
} catch {
    Write-Host "✗ Maven no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "Por favor instale Maven desde: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# Verificar Java
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | Select-Object -First 1 | Out-String
    Write-Host "✓ Java encontrado: $($javaVersion.Trim())" -ForegroundColor Green
} catch {
    Write-Host "✗ Java no está instalado" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Iniciando compilación..." -ForegroundColor Yellow
Write-Host ""

# Configurar encoding UTF-8 para la compilación
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
Write-Host "✓ Encoding UTF-8 configurado" -ForegroundColor Green
Write-Host ""

# Limpiar y compilar
Write-Host "[1/3] Limpiando proyecto..." -ForegroundColor Cyan
mvn clean

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "✗ Error en la limpieza" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2/3] Compilando y empaquetando..." -ForegroundColor Cyan
mvn package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "✗ Error en la compilación" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[3/3] Verificando ejecutable..." -ForegroundColor Cyan

$jarFile = "target\Alumnos-1.0-SNAPSHOT.jar"
if (Test-Path $jarFile) {
    $size = (Get-Item $jarFile).Length / 1MB
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  ✓ Ejecutable generado exitosamente   " -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Archivo: $jarFile" -ForegroundColor White
    Write-Host "Tamaño: $([math]::Round($size, 2)) MB" -ForegroundColor White
    Write-Host ""
    Write-Host "Para ejecutar la aplicación:" -ForegroundColor Yellow
    Write-Host "  java -jar $jarFile" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "O usa el script:" -ForegroundColor Yellow
    Write-Host "  .\start-production.ps1" -ForegroundColor Cyan
    Write-Host "  O: .\start-production.bat" -ForegroundColor Cyan
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "✗ No se encontró el archivo JAR" -ForegroundColor Red
    exit 1
}
