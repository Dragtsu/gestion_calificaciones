# ============================================================================
# Script de PowerShell para Limpiar la Base de Datos
# ============================================================================
# Este script proporciona opciones para limpiar la base de datos de la
# aplicación de alumnos
# ============================================================================

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("produccion", "completo", "test")]
    [string]$Tipo = "produccion",

    [Parameter(Mandatory=$false)]
    [string]$DatabasePath = "alumnos.db"
)

# Colores para la salida
function Write-ColorOutput {
    param(
        [Parameter(Mandatory=$true)]
        [string]$Message,

        [Parameter(Mandatory=$false)]
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

# Banner
Write-ColorOutput "`n============================================================================" "Cyan"
Write-ColorOutput "  Script de Limpieza de Base de Datos - Alumnos" "Cyan"
Write-ColorOutput "============================================================================`n" "Cyan"

# Verificar que existe SQLite3
$sqliteCommand = Get-Command sqlite3 -ErrorAction SilentlyContinue
if (-not $sqliteCommand) {
    Write-ColorOutput "ERROR: No se encontró sqlite3 en el PATH" "Red"
    Write-ColorOutput "Por favor, instale SQLite3 o agregue su ubicación al PATH" "Yellow"
    Write-ColorOutput "Descarga: https://www.sqlite.org/download.html" "Yellow"
    exit 1
}

# Determinar qué script SQL usar
$scriptPath = switch ($Tipo) {
    "produccion" {
        "src\main\resources\db\limpiar_base_datos_produccion.sql"
    }
    "completo" {
        "src\main\resources\db\limpiar_completo.sql"
    }
    "test" {
        "src\main\resources\db\limpiar_base_datos_produccion.sql"
    }
}

# Verificar que existe el script SQL
if (-not (Test-Path $scriptPath)) {
    Write-ColorOutput "ERROR: No se encontró el script SQL: $scriptPath" "Red"
    exit 1
}

# Verificar que existe la base de datos
if (-not (Test-Path $DatabasePath)) {
    Write-ColorOutput "ADVERTENCIA: No se encontró la base de datos: $DatabasePath" "Yellow"
    $continuar = Read-Host "¿Desea crear una nueva base de datos? (s/n)"
    if ($continuar -ne "s" -and $continuar -ne "S") {
        Write-ColorOutput "Operación cancelada" "Yellow"
        exit 0
    }
}

# Crear respaldo antes de limpiar
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$backupPath = "alumnos_backup_$timestamp.db"

if (Test-Path $DatabasePath) {
    Write-ColorOutput "Creando respaldo de la base de datos..." "Yellow"
    Copy-Item $DatabasePath $backupPath
    Write-ColorOutput "Respaldo creado: $backupPath" "Green"
}

# Mostrar información
Write-ColorOutput "`nConfiguración:" "Cyan"
Write-ColorOutput "  - Tipo de limpieza: $Tipo" "White"
Write-ColorOutput "  - Base de datos: $DatabasePath" "White"
Write-ColorOutput "  - Script SQL: $scriptPath" "White"
Write-ColorOutput "  - Respaldo: $backupPath`n" "White"

# Confirmación
Write-ColorOutput "ADVERTENCIA: Esta operación eliminará datos de la base de datos" "Red"
$confirmacion = Read-Host "¿Está seguro de continuar? (escriba 'SI' para confirmar)"

if ($confirmacion -ne "SI") {
    Write-ColorOutput "`nOperación cancelada por el usuario" "Yellow"
    if (Test-Path $backupPath) {
        Remove-Item $backupPath
        Write-ColorOutput "Respaldo eliminado" "Gray"
    }
    exit 0
}

# Ejecutar el script SQL
Write-ColorOutput "`nEjecutando limpieza de base de datos..." "Yellow"

try {
    $content = Get-Content $scriptPath -Raw
    $content | sqlite3 $DatabasePath

    if ($LASTEXITCODE -eq 0) {
        Write-ColorOutput "`n✓ Base de datos limpiada exitosamente" "Green"

        # Mostrar estadísticas
        Write-ColorOutput "`nEstadísticas de la base de datos:" "Cyan"

        $tables = @(
            "alumnos",
            "grupos",
            "materias",
            "grupo_materia",
            "criterios",
            "agregados",
            "calificaciones",
            "examenes",
            "alumno_examen",
            "calificacion_concentrado"
        )

        foreach ($table in $tables) {
            $count = sqlite3 $DatabasePath "SELECT COUNT(*) FROM $table;"
            Write-ColorOutput "  - $table : $count registros" "White"
        }

        Write-ColorOutput "`nRespaldo guardado en: $backupPath" "Green"
        Write-ColorOutput "Si todo está correcto, puede eliminar el respaldo manualmente`n" "Gray"

    } else {
        throw "SQLite retornó código de error: $LASTEXITCODE"
    }

} catch {
    Write-ColorOutput "`n✗ Error al limpiar la base de datos" "Red"
    Write-ColorOutput "Detalles: $($_.Exception.Message)" "Red"

    # Restaurar desde el respaldo
    if (Test-Path $backupPath) {
        Write-ColorOutput "`nRestaurando desde el respaldo..." "Yellow"
        Copy-Item $backupPath $DatabasePath -Force
        Write-ColorOutput "Base de datos restaurada desde el respaldo" "Green"
    }

    exit 1
}

Write-ColorOutput "============================================================================" "Cyan"
Write-ColorOutput "  Proceso completado" "Cyan"
Write-ColorOutput "============================================================================`n" "Cyan"
