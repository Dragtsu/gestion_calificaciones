# Sistema de Gestión de Alumnos - Guía de Producción

## 🚀 Generar Ejecutable para Producción

### Requisitos Previos
- Java 17 o superior instalado
- Maven instalado y configurado

### Pasos para Generar el Ejecutable

#### Opción 1: Usar el script automatizado (Recomendado)
```powershell
.\build-production.ps1
```

#### Opción 2: Comando Maven directo
```bash
mvn clean package -DskipTests
```

El ejecutable se generará en:
```
target\Alumnos-1.0-SNAPSHOT.jar
```

## ▶️ Ejecutar la Aplicación

### Windows

#### PowerShell:
```powershell
.\start-production.ps1
```

#### Símbolo del sistema o doble clic:
```batch
start-production.bat
```

#### Comando directo:
```bash
java -jar target\Alumnos-1.0-SNAPSHOT.jar
```

## 📦 Distribuir la Aplicación

### Archivos necesarios para distribución:
1. `Alumnos-1.0-SNAPSHOT.jar` (del directorio target)
2. `alumnos.db` (base de datos)
3. `plantillas\` (directorio con plantillas de Word)
4. `start-production.bat` (opcional, para facilitar ejecución)

### Estructura recomendada para distribución:
```
Alumnos/
├── Alumnos-1.0-SNAPSHOT.jar
├── alumnos.db
├── start-production.bat
└── plantillas/
    └── concentrado_calificaciones.docx
```

## 🔧 Configuración de Producción

### Variables de entorno (opcional)
Si necesitas configurar el puerto o la ubicación de la base de datos:

```bash
# Configurar ubicación de la base de datos
set SPRING_DATASOURCE_URL=jdbc:sqlite:./alumnos.db

# Ejecutar
java -jar Alumnos-1.0-SNAPSHOT.jar
```

## ❓ Solución de Problemas

### Error: "Java no está instalado"
- Descarga e instala Java desde: https://adoptium.net/
- Verifica la instalación: `java -version`

### Error: "No se encontró el archivo ejecutable"
- Ejecuta primero: `.\build-production.ps1`
- O: `mvn clean package`

### Error: "No se puede encontrar la base de datos"
- Asegúrate de que `alumnos.db` esté en el mismo directorio que el JAR
- O en el directorio desde donde ejecutas el comando

### La interfaz JavaFX no se muestra
- Verifica que estés usando Java 17+ con JavaFX incluido
- O usa un JDK con JavaFX como Liberica JDK Full

## 📝 Notas Adicionales

### Tamaño del ejecutable
El JAR empaquetado incluye todas las dependencias necesarias (Spring Boot, JavaFX, Apache POI, etc.) y debería tener aproximadamente 70-100 MB.

### Rendimiento
La aplicación es de escritorio y se ejecuta localmente, sin necesidad de servidor web.

### Actualizaciones
Para actualizar la aplicación, simplemente reemplaza el archivo JAR con la nueva versión.

## 🔐 Seguridad

- La base de datos SQLite está en el sistema de archivos local
- No hay exposición de puertos de red
- Los datos permanecen en el equipo local

## 📞 Soporte

Para problemas o dudas sobre la generación del ejecutable, revisa los logs de Maven durante la compilación.
