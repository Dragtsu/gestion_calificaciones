# Script de instalación automática de Maven
# Ejecutar como Administrador

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Instalador de Apache Maven" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si ya está instalado
Write-Host "[1/5] Verificando instalación previa..." -ForegroundColor Yellow
try {
    $mvnCheck = mvn -version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Maven ya está instalado:" -ForegroundColor Green
        Write-Host $mvnCheck
        $response = Read-Host "¿Deseas reinstalar? (S/N)"
        if ($response -ne "S" -and $response -ne "s") {
            Write-Host "Instalación cancelada" -ForegroundColor Yellow
            exit 0
        }
    }
} catch {
    Write-Host "Maven no encontrado. Procediendo con la instalación..." -ForegroundColor Yellow
}

# Verificar Java
Write-Host ""
Write-Host "[2/5] Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | Out-String
    Write-Host "✓ Java encontrado: $($javaVersion.Trim())" -ForegroundColor Green
} catch {
    Write-Host "✗ Java no encontrado. Por favor instala Java 17+ primero" -ForegroundColor Red
    Write-Host "Descarga: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

# Configuración
$mavenVersion = "3.9.5"
$downloadUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
$zipFile = "$env:TEMP\apache-maven-$mavenVersion.zip"
$extractPath = "$env:TEMP\maven-temp"
$installPath = "C:\Program Files\Apache\maven"

# Descargar Maven
Write-Host ""
Write-Host "[3/5] Descargando Maven $mavenVersion..." -ForegroundColor Yellow
Write-Host "URL: $downloadUrl" -ForegroundColor Gray

try {
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri $downloadUrl -OutFile $zipFile -UseBasicParsing -TimeoutSec 300
    $ProgressPreference = 'Continue'

    if (Test-Path $zipFile) {
        $size = [math]::Round((Get-Item $zipFile).Length / 1MB, 2)
        Write-Host "✓ Descarga completada: $size MB" -ForegroundColor Green
    } else {
        throw "Archivo no descargado"
    }
} catch {
    Write-Host "✗ Error en la descarga: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Por favor, descarga Maven manualmente desde:" -ForegroundColor Yellow
    Write-Host "https://maven.apache.org/download.cgi" -ForegroundColor Cyan
    exit 1
}

# Extraer Maven
Write-Host ""
Write-Host "[4/5] Extrayendo Maven..." -ForegroundColor Yellow

try {
    if (Test-Path $extractPath) {
        Remove-Item -Path $extractPath -Recurse -Force
    }

    Expand-Archive -Path $zipFile -DestinationPath $extractPath -Force
    Write-Host "✓ Extracción completada" -ForegroundColor Green
} catch {
    Write-Host "✗ Error al extraer: $_" -ForegroundColor Red
    exit 1
}

# Instalar Maven
Write-Host ""
Write-Host "[5/5] Instalando Maven..." -ForegroundColor Yellow

try {
    # Crear directorio de instalación si no existe
    $installDir = Split-Path $installPath -Parent
    if (-not (Test-Path $installDir)) {
        New-Item -ItemType Directory -Path $installDir -Force | Out-Null
    }

    # Eliminar instalación anterior si existe
    if (Test-Path $installPath) {
        Write-Host "Eliminando instalación anterior..." -ForegroundColor Gray
        Remove-Item -Path $installPath -Recurse -Force
    }

    # Copiar Maven a la ubicación final
    $mavenFolder = Get-ChildItem -Path $extractPath -Filter "apache-maven-*" -Directory | Select-Object -First 1
    Move-Item -Path $mavenFolder.FullName -Destination $installPath -Force

    Write-Host "✓ Maven instalado en: $installPath" -ForegroundColor Green
} catch {
    Write-Host "✗ Error al instalar: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Puede que necesites ejecutar este script como Administrador" -ForegroundColor Yellow
    exit 1
}

# Configurar variables de entorno
Write-Host ""
Write-Host "Configurando variables de entorno..." -ForegroundColor Yellow

try {
    # Configurar MAVEN_HOME
    [Environment]::SetEnvironmentVariable("MAVEN_HOME", $installPath, "Machine")
    Write-Host "✓ MAVEN_HOME configurado" -ForegroundColor Green

    # Agregar al PATH
    $currentPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
    $mavenBin = "$installPath\bin"

    if ($currentPath -notlike "*$mavenBin*") {
        $newPath = "$currentPath;$mavenBin"
        [Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
        Write-Host "✓ Maven agregado al PATH" -ForegroundColor Green
    } else {
        Write-Host "✓ Maven ya está en el PATH" -ForegroundColor Green
    }

    # Actualizar PATH en la sesión actual
    $env:MAVEN_HOME = $installPath
    $env:Path = "$env:Path;$mavenBin"

} catch {
    Write-Host "✗ Error al configurar variables: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Necesitas ejecutar este script como Administrador" -ForegroundColor Yellow
    Write-Host "Click derecho en PowerShell → 'Ejecutar como Administrador'" -ForegroundColor Yellow
    exit 1
}

# Limpiar archivos temporales
Write-Host ""
Write-Host "Limpiando archivos temporales..." -ForegroundColor Gray
Remove-Item -Path $zipFile -Force -ErrorAction SilentlyContinue
Remove-Item -Path $extractPath -Recurse -Force -ErrorAction SilentlyContinue

# Verificar instalación
Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Verificando instalación..." -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

Start-Sleep -Seconds 2

try {
    $mvnVersion = & "$installPath\bin\mvn.cmd" -version 2>&1
    Write-Host "✓ ¡Maven instalado correctamente!" -ForegroundColor Green
    Write-Host ""
    Write-Host $mvnVersion
    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host "  ¡Instalación Exitosa!" -ForegroundColor Green
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "IMPORTANTE: Cierra y abre una nueva terminal para usar Maven" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Para verificar, ejecuta en una nueva terminal:" -ForegroundColor Cyan
    Write-Host "  mvn -version" -ForegroundColor White
    Write-Host ""
    Write-Host "Para compilar el proyecto:" -ForegroundColor Cyan
    Write-Host "  cd D:\Desarrollos\alumnos" -ForegroundColor White
    Write-Host "  mvn clean install -DskipTests" -ForegroundColor White
    Write-Host "  mvn javafx:run" -ForegroundColor White
    Write-Host ""

} catch {
    Write-Host "✗ Error al verificar Maven: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Maven está instalado pero necesitas:" -ForegroundColor Yellow
    Write-Host "1. Cerrar esta terminal" -ForegroundColor White
    Write-Host "2. Abrir una nueva terminal" -ForegroundColor White
    Write-Host "3. Ejecutar: mvn -version" -ForegroundColor White
}

Write-Host ""
Write-Host "Presiona cualquier tecla para continuar..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
