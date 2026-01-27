# Script para Compilar y Ejecutar el Proyecto
# Sistema de Gestión de Alumnos - Módulo Grupos

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "  Sistema de Gestión de Alumnos" -ForegroundColor Cyan
Write-Host "  Compilación y Ejecución" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "IMPORTANTE: Nuevas funcionalidades agregadas:" -ForegroundColor Yellow
Write-Host "  ✓ Entidad Grupo creada" -ForegroundColor Green
Write-Host "  ✓ Número de grupo formateado a 3 dígitos (001-999)" -ForegroundColor Green
Write-Host "  ✓ Interfaz de gestión de grupos en el menú" -ForegroundColor Green
Write-Host "  ✓ CRUD completo para grupos" -ForegroundColor Green
Write-Host ""

# Verificar si IntelliJ IDEA puede compilar
Write-Host "Opciones para compilar y ejecutar:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Usar IntelliJ IDEA (Recomendado)" -ForegroundColor White
Write-Host "   - Abrir el proyecto en IntelliJ IDEA" -ForegroundColor Gray
Write-Host "   - Build > Build Project (Ctrl+F9)" -ForegroundColor Gray
Write-Host "   - Run > Run 'AlumnosApplication' (Shift+F10)" -ForegroundColor Gray
Write-Host ""

Write-Host "2. Usar Maven (si está instalado)" -ForegroundColor White
Write-Host "   - mvn clean compile" -ForegroundColor Gray
Write-Host "   - mvn spring-boot:run" -ForegroundColor Gray
Write-Host ""

Write-Host "3. Instalar Maven" -ForegroundColor White
Write-Host "   - Ejecutar: .\install-maven.ps1" -ForegroundColor Gray
Write-Host "   - O seguir las instrucciones en MAVEN_SETUP.md" -ForegroundColor Gray
Write-Host ""

# Verificar si Maven está disponible
Write-Host "Verificando Maven..." -ForegroundColor Cyan
$mavenPath = Get-Command mvn -ErrorAction SilentlyContinue

if ($mavenPath) {
    Write-Host "✓ Maven encontrado en: $($mavenPath.Source)" -ForegroundColor Green
    Write-Host ""

    $respuesta = Read-Host "¿Desea compilar con Maven ahora? (S/N)"
    if ($respuesta -eq "S" -or $respuesta -eq "s") {
        Write-Host ""
        Write-Host "Compilando proyecto..." -ForegroundColor Yellow
        & mvn clean compile

        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "✓ Compilación exitosa!" -ForegroundColor Green
            Write-Host ""

            $ejecutar = Read-Host "¿Desea ejecutar la aplicación? (S/N)"
            if ($ejecutar -eq "S" -or $ejecutar -eq "s") {
                Write-Host ""
                Write-Host "Iniciando aplicación..." -ForegroundColor Yellow
                Write-Host ""
                & mvn spring-boot:run
            }
        } else {
            Write-Host ""
            Write-Host "✗ Error en la compilación" -ForegroundColor Red
            Write-Host "Por favor revise los errores anteriores" -ForegroundColor Yellow
        }
    }
} else {
    Write-Host "✗ Maven no encontrado" -ForegroundColor Red
    Write-Host ""
    Write-Host "Para usar este script necesita Maven instalado." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Alternativas:" -ForegroundColor Cyan
    Write-Host "1. Usar IntelliJ IDEA para compilar y ejecutar (más fácil)" -ForegroundColor White
    Write-Host "2. Instalar Maven ejecutando: .\install-maven.ps1" -ForegroundColor White
    Write-Host ""

    $instalar = Read-Host "¿Desea intentar instalar Maven con Chocolatey? (S/N)"
    if ($instalar -eq "S" -or $instalar -eq "s") {
        Write-Host ""
        Write-Host "Intentando instalar Maven con Chocolatey..." -ForegroundColor Yellow

        # Verificar Chocolatey
        $choco = Get-Command choco -ErrorAction SilentlyContinue
        if ($choco) {
            choco install maven -y
            Write-Host ""
            Write-Host "Maven instalado. Por favor cierre y abra una nueva terminal." -ForegroundColor Green
        } else {
            Write-Host ""
            Write-Host "Chocolatey no está instalado." -ForegroundColor Red
            Write-Host "Instale Chocolatey desde: https://chocolatey.org/install" -ForegroundColor Yellow
            Write-Host "O siga las instrucciones en MAVEN_SETUP.md" -ForegroundColor Yellow
        }
    }
}

Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "Documentación disponible:" -ForegroundColor Yellow
Write-Host "  - GRUPO_ENTITY_README.md (Detalles del módulo Grupos)" -ForegroundColor Gray
Write-Host "  - README.md (Información general)" -ForegroundColor Gray
Write-Host "  - MAVEN_SETUP.md (Instalación de Maven)" -ForegroundColor Gray
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

pause
