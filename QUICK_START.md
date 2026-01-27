# Guía Rápida de Inicio

## 🚀 Inicio Rápido

### Opción 1: Usando el script de PowerShell (Recomendado para Windows)

```powershell
# Ejecutar el script
.\run.ps1
```

### Opción 2: Usando Maven directamente

```bash
# 1. Compilar el proyecto
mvn clean install

# 2. Ejecutar la aplicación
mvn javafx:run
```

### Opción 3: Desde IntelliJ IDEA

1. Abrir el proyecto en IntelliJ IDEA
2. Esperar a que Maven sincronice las dependencias
3. Buscar la clase `AlumnosApplication.java`
4. Click derecho → Run 'AlumnosApplication.main()'

## 📋 Verificación de Requisitos

Antes de ejecutar, asegúrate de tener:

- ✅ Java 17 o superior instalado
- ✅ Maven 3.6 o superior instalado
- ✅ Variables de entorno JAVA_HOME y MAVEN_HOME configuradas

### Verificar Java:
```bash
java -version
```

### Verificar Maven:
```bash
mvn -version
```

## 🐛 Solución de Problemas

### Error: "JAVA_HOME no está configurado"
```bash
# Windows
setx JAVA_HOME "C:\Program Files\Java\jdk-17"

# Linux/Mac
export JAVA_HOME=/path/to/java
```

### Error: "Maven no encontrado"
- Descargar Maven desde: https://maven.apache.org/download.cgi
- Agregar Maven al PATH del sistema

### Error: "No se puede cargar el archivo FXML"
- Verificar que los archivos FXML están en `src/main/resources/fxml/`
- Limpiar y recompilar: `mvn clean install`

## 📊 Primera Ejecución

Al ejecutar por primera vez:
1. Se creará automáticamente la base de datos SQLite (`alumnos.db`)
2. Se insertarán 3 alumnos de ejemplo
3. Se abrirá la ventana principal de la aplicación

## 🎯 Próximos Pasos

Una vez que la aplicación esté ejecutándose:

1. **Explorar la interfaz** - Familiarízate con el formulario y la tabla
2. **Agregar un alumno** - Prueba el formulario de registro
3. **Buscar alumnos** - Usa la función de búsqueda
4. **Ver el código** - Explora la arquitectura limpia del proyecto

## 📚 Recursos Adicionales

- [Documentación completa](README.md)
- [Arquitectura del proyecto](README.md#-arquitectura-del-proyecto)
- [Tecnologías utilizadas](README.md#-tecnologías-utilizadas)
