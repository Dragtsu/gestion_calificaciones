# Script simple para ejecutar la aplicación
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  Sistema de Gestión de Alumnos  " -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Agregar Maven al PATH
$env:PATH = "C:\Users\macie\.m2\wrapper\dists\apache-maven-3.9.7-bin\3k9n615lchs6mp84v355m633uo\apache-maven-3.9.7\bin;$env:PATH"

# Cambiar al directorio del proyecto
Set-Location "D:\Desarrollos\alumnos"

Write-Host "Compilando proyecto..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "Iniciando aplicación..." -ForegroundColor Green
    Write-Host ""
    mvn spring-boot:run
} else {
    Write-Host ""
    Write-Host "Error en la compilación" -ForegroundColor Red
}
