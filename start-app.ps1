# Script para iniciar la aplicación de Alumnos
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  Sistema de Gestión de Alumnos  " -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Agregar Maven al PATH
$env:PATH = "C:\Users\macie\.m2\wrapper\dists\apache-maven-3.9.7-bin\3k9n615lchs6mp84v355m633uo\apache-maven-3.9.7\bin;$env:PATH"

# Cambiar al directorio del proyecto
Set-Location "D:\Desarrollos\alumnos"

Write-Host "Iniciando aplicación..." -ForegroundColor Yellow
Write-Host ""

# Ejecutar la aplicación
mvn spring-boot:run
