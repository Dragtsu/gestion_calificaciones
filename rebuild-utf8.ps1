# Script para limpiar y recompilar el proyecto con encoding UTF-8 correcto
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Limpiando y recompilando con UTF-8..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Configurar encoding UTF-8 para la sesión actual
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

# Limpiar y compilar
Write-Host "`nEjecutando: mvn clean compile..." -ForegroundColor Yellow
mvn clean compile -Dfile.encoding=UTF-8 -DskipTests

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "Compilación completada!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
