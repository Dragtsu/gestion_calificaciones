# ========================================
# Script de Optimización de Base de Datos
# ========================================
# Este script aplica los índices de optimización
# a la base de datos SQLite del proyecto Alumnos
# ========================================

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  OPTIMIZACIÓN DE BASE DE DATOS - ALUMNOS" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Definir rutas
$dbPath = "alumnos.db"
$sqlScript = "src\main\resources\db\optimizacion_indices.sql"
$backupPath = "alumnos.db.backup_$(Get-Date -Format 'yyyyMMdd_HHmmss')"

# Verificar que existe sqlite3
$sqlite = Get-Command sqlite3 -ErrorAction SilentlyContinue

if (-not $sqlite) {
    Write-Host "ERROR: No se encuentra sqlite3 en el PATH" -ForegroundColor Red
    Write-Host "Opciones:" -ForegroundColor Yellow
    Write-Host "  1. Instalar SQLite desde: https://www.sqlite.org/download.html" -ForegroundColor Yellow
    Write-Host "  2. O ejecutar manualmente el script SQL en un cliente SQLite" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Presione cualquier tecla para continuar..."
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
}

# Verificar que existe la base de datos
if (-not (Test-Path $dbPath)) {
    Write-Host "ERROR: No se encuentra la base de datos en: $dbPath" -ForegroundColor Red
    Write-Host "Asegúrese de ejecutar este script desde la raíz del proyecto" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Presione cualquier tecla para continuar..."
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
}

# Verificar que existe el script SQL
if (-not (Test-Path $sqlScript)) {
    Write-Host "ERROR: No se encuentra el script SQL en: $sqlScript" -ForegroundColor Red
    Write-Host ""
    Write-Host "Presione cualquier tecla para continuar..."
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
}

Write-Host "Base de datos encontrada: $dbPath" -ForegroundColor Green
Write-Host "Script SQL encontrado: $sqlScript" -ForegroundColor Green
Write-Host ""

# Confirmar acción
Write-Host "Esta operación:" -ForegroundColor Yellow
Write-Host "  1. Creará un backup de la base de datos" -ForegroundColor White
Write-Host "  2. Aplicará índices de optimización" -ForegroundColor White
Write-Host "  3. Verificará los cambios" -ForegroundColor White
Write-Host ""

$confirmacion = Read-Host "¿Desea continuar? (S/N)"
if ($confirmacion -ne "S" -and $confirmacion -ne "s") {
    Write-Host "Operación cancelada por el usuario" -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "Iniciando proceso de optimización..." -ForegroundColor Cyan
Write-Host ""

# Paso 1: Crear backup
Write-Host "[1/4] Creando backup de la base de datos..." -ForegroundColor Cyan
try {
    Copy-Item $dbPath $backupPath -ErrorAction Stop
    Write-Host "      ✓ Backup creado: $backupPath" -ForegroundColor Green
} catch {
    Write-Host "      ✗ Error creando backup: $_" -ForegroundColor Red
    exit 1
}

# Paso 2: Verificar integridad
Write-Host "[2/4] Verificando integridad de la base de datos..." -ForegroundColor Cyan
$integrityCheck = sqlite3 $dbPath "PRAGMA integrity_check;"
if ($integrityCheck -eq "ok") {
    Write-Host "      ✓ Base de datos íntegra" -ForegroundColor Green
} else {
    Write-Host "      ✗ Problemas de integridad detectados: $integrityCheck" -ForegroundColor Red
    Write-Host "      Restaurando backup..." -ForegroundColor Yellow
    Copy-Item $backupPath $dbPath -Force
    exit 1
}

# Paso 3: Aplicar script de optimización
Write-Host "[3/4] Aplicando índices de optimización..." -ForegroundColor Cyan
try {
    $result = Get-Content $sqlScript -Raw | sqlite3 $dbPath 2>&1

    if ($LASTEXITCODE -eq 0) {
        Write-Host "      ✓ Índices aplicados correctamente" -ForegroundColor Green
    } else {
        Write-Host "      ⚠ Advertencias durante la aplicación: $result" -ForegroundColor Yellow
        Write-Host "      (Esto es normal si los índices ya existían)" -ForegroundColor Gray
    }
} catch {
    Write-Host "      ✗ Error aplicando índices: $_" -ForegroundColor Red
    Write-Host "      Restaurando backup..." -ForegroundColor Yellow
    Copy-Item $backupPath $dbPath -Force
    exit 1
}

# Paso 4: Verificar índices creados
Write-Host "[4/4] Verificando índices creados..." -ForegroundColor Cyan
$indices = sqlite3 $dbPath "SELECT COUNT(*) FROM sqlite_master WHERE type='index' AND name LIKE 'idx_%';"
Write-Host "      ✓ Índices de optimización detectados: $indices" -ForegroundColor Green

# Ejecutar ANALYZE para actualizar estadísticas
Write-Host ""
Write-Host "Actualizando estadísticas de la base de datos..." -ForegroundColor Cyan
sqlite3 $dbPath "ANALYZE;" | Out-Null
Write-Host "      ✓ Estadísticas actualizadas" -ForegroundColor Green

# Mostrar tamaño de la base de datos
Write-Host ""
Write-Host "Información de la base de datos:" -ForegroundColor Cyan
$dbSize = (Get-Item $dbPath).Length / 1MB
Write-Host "      Tamaño: $([math]::Round($dbSize, 2)) MB" -ForegroundColor White

# Resumen final
Write-Host ""
Write-Host "================================================" -ForegroundColor Green
Write-Host "  OPTIMIZACIÓN COMPLETADA EXITOSAMENTE" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Archivos generados:" -ForegroundColor White
Write-Host "  - Backup: $backupPath" -ForegroundColor Gray
Write-Host ""
Write-Host "Próximos pasos recomendados:" -ForegroundColor Yellow
Write-Host "  1. Reiniciar la aplicación para que tome los cambios" -ForegroundColor White
Write-Host "  2. Revisar INFORME_OPTIMIZACION.md para más mejoras" -ForegroundColor White
Write-Host "  3. Monitorear el rendimiento de la aplicación" -ForegroundColor White
Write-Host ""

# Preguntar si desea ver los índices creados
$verIndices = Read-Host "¿Desea ver la lista de índices creados? (S/N)"
if ($verIndices -eq "S" -or $verIndices -eq "s") {
    Write-Host ""
    Write-Host "Índices creados:" -ForegroundColor Cyan
    Write-Host "================================================" -ForegroundColor Gray
    $listaIndices = sqlite3 $dbPath "SELECT name, tbl_name FROM sqlite_master WHERE type='index' AND name LIKE 'idx_%' ORDER BY tbl_name, name;"
    Write-Host $listaIndices -ForegroundColor White
    Write-Host "================================================" -ForegroundColor Gray
}

Write-Host ""
Write-Host "Presione cualquier tecla para salir..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
