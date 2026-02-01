# Sistema de Gestión de Alumnos

Aplicación de escritorio para gestionar calificaciones de alumnos, construida con JavaFX y Spring Boot.

## 🚀 Inicio Rápido

### Para Desarrollo
```bash
.\run.ps1
```

### Para Producción

#### 1. Generar ejecutable
**Opción A - Script automático:**
```batch
build-production.bat
```

**Opción B - Usando IntelliJ IDEA:**
Ver guía completa en: [COMO_GENERAR_EJECUTABLE.md](COMO_GENERAR_EJECUTABLE.md)

#### 2. Ejecutar aplicación
```batch
start-production.bat
```

O directamente:
```bash
java -jar target\Alumnos-1.0-SNAPSHOT.jar
```

## 📋 Requisitos

- **Java 17+** - [Descargar Adoptium JDK](https://adoptium.net/)
- **Maven** (para compilar) - [Descargar Maven](https://maven.apache.org/download.cgi)

## 📖 Documentación

- [Guía de Producción](PRODUCCION.md) - Información detallada sobre distribución
- [Cómo Generar Ejecutable](COMO_GENERAR_EJECUTABLE.md) - Guía paso a paso con IntelliJ

## 🏗️ Estructura del Proyecto

```
alumnos/
├── src/main/java/          # Código fuente
├── src/main/resources/     # Recursos (FXML, CSS, SQL)
├── plantillas/             # Plantillas de Word
├── alumnos.db             # Base de datos SQLite
├── pom.xml                # Configuración Maven
├── run.ps1                # Script de desarrollo
├── build-production.bat   # Generar ejecutable
└── start-production.bat   # Ejecutar aplicación
```

## 🔧 Scripts Disponibles

| Script | Descripción |
|--------|-------------|
| `run.ps1` | Ejecutar en modo desarrollo |
| `build-production.bat` | Generar JAR ejecutable |
| `build-production.ps1` | Generar JAR ejecutable (PowerShell) |
| `start-production.bat` | Ejecutar aplicación empaquetada |
| `start-production.ps1` | Ejecutar aplicación empaquetada (PowerShell) |
| `limpiar-duplicados-examenes.ps1` | Mantenimiento de BD |

## 🎯 Características

- ✅ Gestión de alumnos, grupos y materias
- ✅ Registro de calificaciones y exámenes
- ✅ Generación de concentrados en Word
- ✅ Cálculo automático de promedios
- ✅ Base de datos SQLite integrada
- ✅ Interfaz gráfica con JavaFX

## 📦 Distribución

Para distribuir la aplicación a otros equipos:

1. Ejecuta `build-production.bat`
2. Crea una carpeta con:
   - `Alumnos-1.0-SNAPSHOT.jar` (desde `target/`)
   - `alumnos.db`
   - `plantillas/`
   - `start-production.bat`
3. Comprime y distribuye

## ⚙️ Configuración

La aplicación usa SQLite como base de datos local. No requiere configuración adicional.

La base de datos se crea automáticamente en el primer uso.

## 🐛 Solución de Problemas

**Error: "Java no está instalado"**
- Instala Java 17+ desde https://adoptium.net/
- Verifica: `java -version`

**Error: "Maven no encontrado"**
- Usa IntelliJ IDEA para compilar (ver COMO_GENERAR_EJECUTABLE.md)
- O instala Maven desde https://maven.apache.org/

**La aplicación no inicia**
- Verifica que `alumnos.db` esté en el mismo directorio
- Verifica que la carpeta `plantillas/` exista

## 📝 Licencia

Copyright © 2024 - Sistema de Gestión de Alumnos

## 👥 Autor

Desarrollado para gestión académica.
