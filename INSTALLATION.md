# Instalación de Requisitos - Sistema de Gestión de Alumnos

## ⚠️ IMPORTANTE: Requisitos Previos

Este proyecto requiere las siguientes herramientas instaladas en tu sistema:

## 1️⃣ Instalar Java JDK 17

### Descargar e Instalar:
1. Visita: https://www.oracle.com/java/technologies/downloads/#java17
2. Descarga **Java SE Development Kit 17** para Windows
3. Ejecuta el instalador y sigue las instrucciones
4. Ubicación típica de instalación: `C:\Program Files\Java\jdk-17`

### Configurar Variables de Entorno:

1. **Abrir Variables de Entorno:**
   - Presiona `Win + X` y selecciona "Sistema"
   - Click en "Configuración avanzada del sistema"
   - Click en "Variables de entorno"

2. **Crear JAVA_HOME:**
   - En "Variables del sistema", click en "Nueva"
   - Nombre: `JAVA_HOME`
   - Valor: `C:\Program Files\Java\jdk-17` (ajusta según tu instalación)

3. **Actualizar PATH:**
   - Busca la variable `Path` en "Variables del sistema"
   - Click en "Editar"
   - Agregar: `%JAVA_HOME%\bin`

4. **Verificar instalación:**
   ```powershell
   java -version
   ```
   Deberías ver: `java version "17.x.x"`

---

## 2️⃣ Instalar Apache Maven

### Descargar e Instalar:
1. Visita: https://maven.apache.org/download.cgi
2. Descarga **apache-maven-3.9.x-bin.zip** (Binary zip archive)
3. Extrae el archivo a: `C:\Program Files\Apache\maven`
   - Ruta final debe ser: `C:\Program Files\Apache\maven\bin`

### Configurar Variables de Entorno:

1. **Crear MAVEN_HOME:**
   - En "Variables del sistema", click en "Nueva"
   - Nombre: `MAVEN_HOME`
   - Valor: `C:\Program Files\Apache\maven`

2. **Actualizar PATH:**
   - Busca la variable `Path` en "Variables del sistema"
   - Click en "Editar"
   - Agregar: `%MAVEN_HOME%\bin`

3. **Verificar instalación:**
   ```powershell
   mvn -version
   ```
   Deberías ver información de Maven y Java

---

## 3️⃣ Configurar IntelliJ IDEA (Opcional pero Recomendado)

### Descargar e Instalar:
1. Visita: https://www.jetbrains.com/idea/download/
2. Descarga **IntelliJ IDEA Community Edition** (gratis)
3. Instala siguiendo las instrucciones

### Configurar el Proyecto:
1. Abre IntelliJ IDEA
2. Click en "Open" y selecciona la carpeta del proyecto
3. IntelliJ detectará automáticamente que es un proyecto Maven
4. Espera a que Maven descargue todas las dependencias
5. Verifica que el SDK esté configurado en Java 17:
   - File → Project Structure → Project → SDK

---

## 🚀 Verificación de Instalación Completa

Abre PowerShell y ejecuta estos comandos:

```powershell
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Navegar al proyecto
cd D:\Desarrollos\alumnos

# Compilar el proyecto
mvn clean compile

# Ejecutar el proyecto
mvn javafx:run
```

Si todos los comandos funcionan correctamente, ¡estás listo para usar la aplicación!

---

## 📋 Resumen de Ubicaciones

| Herramienta | Ubicación Recomendada | Variable de Entorno |
|------------|----------------------|---------------------|
| Java JDK   | `C:\Program Files\Java\jdk-17` | `JAVA_HOME` |
| Maven      | `C:\Program Files\Apache\maven` | `MAVEN_HOME` |
| Proyecto   | `D:\Desarrollos\alumnos` | - |

---

## 🆘 Solución de Problemas

### Problema: "java no se reconoce como comando"
**Solución:** 
- Verifica que JAVA_HOME esté configurado correctamente
- Verifica que `%JAVA_HOME%\bin` esté en el PATH
- Reinicia PowerShell después de configurar variables

### Problema: "mvn no se reconoce como comando"
**Solución:**
- Verifica que MAVEN_HOME esté configurado correctamente
- Verifica que `%MAVEN_HOME%\bin` esté en el PATH
- Reinicia PowerShell después de configurar variables

### Problema: "JAVA_HOME is not set"
**Solución:**
- Configura la variable JAVA_HOME como se indicó arriba
- Asegúrate de usar "Variables del sistema", no "Variables de usuario"

### Problema: Maven descarga dependencias muy lento
**Solución:**
- Es normal en la primera ejecución
- Maven descarga todas las librerías necesarias
- Puede tomar 5-15 minutos dependiendo de tu conexión

---

## 📞 Pasos Siguientes

Una vez instalado todo:

1. ✅ Verifica las instalaciones con los comandos de verificación
2. ✅ Compila el proyecto: `mvn clean install`
3. ✅ Ejecuta la aplicación: `mvn javafx:run`
4. ✅ Lee la [Guía Rápida](QUICK_START.md)
5. ✅ Explora el [README](README.md) para más información

---

## 🎓 Recursos Adicionales

- [Documentación de Java](https://docs.oracle.com/javase/17/)
- [Documentación de Maven](https://maven.apache.org/guides/)
- [JavaFX Documentation](https://openjfx.io/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
