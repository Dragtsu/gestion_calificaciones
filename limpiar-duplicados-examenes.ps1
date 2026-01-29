# Script para limpiar duplicados en la tabla examenes
# PowerShell Script

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Limpieza de Duplicados - Tabla Examenes" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$dbPath = "alumnos.db"
$backupPath = "alumnos_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').db"

# Verificar que existe la base de datos
if (-not (Test-Path $dbPath)) {
    Write-Host "❌ Error: No se encontró la base de datos '$dbPath'" -ForegroundColor Red
    Write-Host "   Asegúrate de estar en el directorio correcto: D:\Desarrollos\alumnos" -ForegroundColor Yellow
    exit 1
}

# Hacer respaldo
Write-Host "📦 Creando respaldo..." -ForegroundColor Yellow
try {
    Copy-Item $dbPath $backupPath
    Write-Host "✅ Respaldo creado: $backupPath" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "❌ Error al crear respaldo: $_" -ForegroundColor Red
    exit 1
}

# Verificar si hay duplicados
Write-Host "🔍 Verificando duplicados..." -ForegroundColor Yellow
$checkDuplicates = @"
SELECT
    grupo_id,
    materia_id,
    parcial,
    COUNT(*) as cantidad
FROM examenes
GROUP BY grupo_id, materia_id, parcial
HAVING COUNT(*) > 1;
"@

$duplicates = sqlite3 $dbPath $checkDuplicates
if ([string]::IsNullOrWhiteSpace($duplicates)) {
    Write-Host "✅ No se encontraron duplicados en la tabla examenes" -ForegroundColor Green
    Write-Host ""
    Write-Host "ℹ️  La base de datos ya está limpia." -ForegroundColor Cyan
    exit 0
}

Write-Host "⚠️  Se encontraron registros duplicados:" -ForegroundColor Yellow
Write-Host $duplicates
Write-Host ""

# Preguntar confirmación
Write-Host "⚠️  ¿Deseas eliminar los duplicados? (S/N)" -ForegroundColor Yellow
$confirmacion = Read-Host
if ($confirmacion -ne "S" -and $confirmacion -ne "s") {
    Write-Host "❌ Operación cancelada" -ForegroundColor Red
    exit 0
}

# Ejecutar limpieza
Write-Host ""
Write-Host "🧹 Eliminando duplicados..." -ForegroundColor Yellow
$cleanQuery = @"
DELETE FROM examenes
WHERE id NOT IN (
    SELECT MIN(id)
    FROM examenes
    GROUP BY grupo_id, materia_id, parcial
);
"@

try {
    sqlite3 $dbPath $cleanQuery
    Write-Host "✅ Duplicados eliminados exitosamente" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "❌ Error al eliminar duplicados: $_" -ForegroundColor Red
    Write-Host "   Restaura el respaldo: Copy-Item $backupPath $dbPath -Force" -ForegroundColor Yellow
    exit 1
}

# Verificar resultado
Write-Host "🔍 Verificando resultado..." -ForegroundColor Yellow
$afterClean = sqlite3 $dbPath $checkDuplicates
if ([string]::IsNullOrWhiteSpace($afterClean)) {
    Write-Host "✅ Limpieza completada. No hay duplicados." -ForegroundColor Green
} else {
    Write-Host "⚠️  Aún hay duplicados:" -ForegroundColor Yellow
    Write-Host $afterClean
}

# Mostrar estadísticas
Write-Host ""
Write-Host "📊 Estadísticas:" -ForegroundColor Cyan
$stats = sqlite3 $dbPath "SELECT COUNT(*) as total FROM examenes;"
Write-Host "   Total de exámenes: $stats" -ForegroundColor White

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Proceso completado" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "ℹ️  Respaldo guardado en: $backupPath" -ForegroundColor Cyan
Write-Host "ℹ️  Si algo salió mal, restaura con:" -ForegroundColor Cyan
Write-Host "   Copy-Item $backupPath $dbPath -Force" -ForegroundColor Yellow
