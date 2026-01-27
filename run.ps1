# Script para ejecutar la aplicación de Alumnos
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  Sistema de Gestión de Alumnos  " -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si existe Maven
try {
    $mvnVersion = mvn -version
    Write-Host "Maven encontrado:" -ForegroundColor Green
    Write-Host "$mvnVersion" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "ERROR: Maven no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "Por favor instale Maven desde: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# Preguntar si desea limpiar y compilar
$compile = Read-Host "¿Desea compilar el proyecto primero? (S/N)"
if ($compile -eq "S" -or $compile -eq "s") {
    Write-Host ""
    Write-Host "Compilando proyecto..." -ForegroundColor Yellow
    mvn clean install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: La compilación falló" -ForegroundColor Red
        exit 1
    }
    Write-Host "Compilación exitosa!" -ForegroundColor Green
    Write-Host ""
}

# Ejecutar la aplicación
Write-Host "Iniciando aplicación..." -ForegroundColor Yellow
Write-Host ""
mvn javafx:run
