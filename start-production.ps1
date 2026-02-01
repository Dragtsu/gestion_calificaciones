# Script para ejecutar la aplicación en producción
# Sistema de Gestión de Alumnos

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Sistema de Gestión de Alumnos v1.0   " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$jarFile = "target\Alumnos-1.0-SNAPSHOT.jar"

# Verificar que existe el JAR
if (-not (Test-Path $jarFile)) {
    Write-Host "✗ No se encontró el archivo ejecutable" -ForegroundColor Red
    Write-Host ""
    Write-Host "Por favor, ejecuta primero:" -ForegroundColor Yellow
    Write-Host "  .\build-production.ps1" -ForegroundColor Cyan
    Write-Host ""
    exit 1
}

# Verificar Java
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | Select-Object -First 1
    Write-Host "✓ Java encontrado" -ForegroundColor Green
} catch {
    Write-Host "✗ Java no está instalado" -ForegroundColor Red
    Write-Host "Por favor instale Java 17+ desde: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Iniciando aplicación..." -ForegroundColor Yellow
Write-Host ""

# Ejecutar la aplicación
java -jar $jarFile
