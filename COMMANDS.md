# 📋 Comandos Útiles - Proyecto Alumnos

## 🚀 Comandos de Maven

### Compilación y Construcción
```powershell
# Limpiar el proyecto (elimina /target)
mvn clean

# Compilar el código fuente
mvn compile

# Compilar código de prueba
mvn test-compile

# Limpiar y compilar
mvn clean compile

# Crear el JAR del proyecto
mvn package

# Instalar en repositorio local de Maven
mvn install

# Limpiar, compilar, probar y empaquetar
mvn clean install
```

### Ejecución
```powershell
# Ejecutar la aplicación JavaFX
mvn javafx:run

# Ejecutar con Spring Boot (alternativa)
mvn spring-boot:run

# Ejecutar con perfil específico
mvn javafx:run -Dspring.profiles.active=dev
```

### Testing
```powershell
# Ejecutar todos los tests
mvn test

# Ejecutar un test específico
mvn test -Dtest=AlumnoServiceTest

# Ejecutar tests con cobertura (si tienes JaCoCo configurado)
mvn clean test jacoco:report

# Saltar tests durante la construcción
mvn clean install -DskipTests
```

### Dependencias
```powershell
# Ver árbol de dependencias
mvn dependency:tree

# Descargar todas las dependencias
mvn dependency:resolve

# Buscar actualizaciones de dependencias
mvn versions:display-dependency-updates

# Copiar dependencias a una carpeta
mvn dependency:copy-dependencies
```

### Análisis y Calidad
```powershell
# Verificar el código
mvn verify

# Ejecutar análisis de código (si tienes plugins configurados)
mvn checkstyle:check

# Ver información del proyecto
mvn help:effective-pom
```

---

## 🎨 Trabajar con JavaFX

### Comandos JavaFX Maven Plugin
```powershell
# Ejecutar con debugging
mvn javafx:run -Djavafx.args="--debug"

# Ejecutar con JVM arguments personalizados
mvn javafx:run -Djavafx.jvmArgs="-Xmx512m"

# Ver información del plugin
mvn help:describe -Dplugin=org.openjfx:javafx-maven-plugin
```

---

## 💾 Gestión de Base de Datos SQLite

### Ver la Base de Datos
```powershell
# Abrir SQLite CLI (si está instalado)
sqlite3 alumnos.db

# Comandos SQLite comunes:
# .tables          - Ver todas las tablas
# .schema alumnos  - Ver estructura de la tabla alumnos
# SELECT * FROM alumnos; - Ver todos los registros
# .exit            - Salir
```

### Backup y Restauración
```powershell
# Hacer backup de la base de datos
Copy-Item alumnos.db -Destination alumnos_backup_$(Get-Date -Format 'yyyyMMdd').db

# Restaurar desde backup
Copy-Item alumnos_backup_20260124.db -Destination alumnos.db
```

### Limpiar Base de Datos
```powershell
# Eliminar la base de datos (se recreará al iniciar la app)
Remove-Item alumnos.db -ErrorAction SilentlyContinue
```

---

## 🔧 Comandos de IntelliJ IDEA

### Atajos de Teclado (Windows)
```
Ctrl + Shift + F10  - Ejecutar la clase actual
Shift + F10         - Ejecutar última configuración
Ctrl + F9           - Build del proyecto
Ctrl + Shift + F9   - Recompilar archivo actual
Ctrl + E            - Archivos recientes
Ctrl + Shift + A    - Find Action (buscar comandos)
Ctrl + N            - Buscar clase
Ctrl + Shift + N    - Buscar archivo
Alt + Enter         - Quick fix
Ctrl + Alt + L      - Reformatear código
```

### Maven en IntelliJ
```
Ctrl + Shift + O    - Organizar imports
Alt + Insert        - Generar código (getters, setters, etc.)
```

---

## 🐛 Debugging

### Ejecutar en Modo Debug
```powershell
# Maven con debug
mvnDebug javafx:run

# Conectar debugger en puerto 8000
# Luego en IntelliJ: Run → Attach to Process
```

### Ver Logs
```powershell
# Ver logs en tiempo real
Get-Content application.log -Wait

# Ver últimas 50 líneas
Get-Content application.log -Tail 50
```

---

## 📦 Crear Ejecutable

### Crear JAR Ejecutable
```powershell
# Crear JAR con dependencias
mvn clean package

# El archivo estará en: target/alumnos-1.0-SNAPSHOT.jar
```

### Ejecutar JAR
```powershell
# Ejecutar el JAR creado
java -jar target/alumnos-1.0-SNAPSHOT.jar
```

### Crear Ejecutable Nativo (Avanzado)
```powershell
# Con jpackage (Java 14+)
jpackage --input target --name Alumnos --main-jar alumnos-1.0-SNAPSHOT.jar --main-class com.alumnos.AlumnosApplication --type exe --win-console
```

---

## 🧹 Limpieza del Proyecto

### Limpiar Archivos Temporales
```powershell
# Eliminar carpeta target
Remove-Item -Recurse -Force target -ErrorAction SilentlyContinue

# Eliminar base de datos
Remove-Item alumnos.db -ErrorAction SilentlyContinue

# Eliminar archivos de IDE
Remove-Item -Recurse -Force .idea -ErrorAction SilentlyContinue
Remove-Item *.iml -ErrorAction SilentlyContinue
```

### Reset Completo del Proyecto
```powershell
# Limpiar todo
mvn clean
Remove-Item alumnos.db -ErrorAction SilentlyContinue

# Reconstruir
mvn install

# Ejecutar
mvn javafx:run
```

---

## 📊 Información del Proyecto

### Ver Información
```powershell
# Ver propiedades del proyecto
mvn help:effective-pom

# Ver información de plugins
mvn help:describe -Dplugin=spring-boot

# Ver versión de Java
java -version

# Ver versión de Maven
mvn -version
```

### Análisis de Código
```powershell
# Contar líneas de código
Get-ChildItem -Recurse -Include *.java | Get-Content | Measure-Object -Line

# Buscar TODO en el código
Get-ChildItem -Recurse -Include *.java | Select-String -Pattern "TODO"
```

---

## 🔍 Búsqueda y Navegación

### Buscar en el Código
```powershell
# Buscar texto en archivos Java
Get-ChildItem -Recurse -Include *.java | Select-String -Pattern "AlumnoService"

# Buscar en FXML
Get-ChildItem -Recurse -Include *.fxml | Select-String -Pattern "Button"
```

---

## 🌿 Git (Control de Versiones)

### Comandos Básicos
```powershell
# Inicializar repositorio
git init

# Ver estado
git status

# Agregar archivos
git add .
git add src/main/java/com/alumnos/domain/model/Alumno.java

# Commit
git commit -m "feat: Agregar funcionalidad de búsqueda"

# Ver historial
git log --oneline

# Ver diferencias
git diff
```

### Ramas
```powershell
# Crear rama
git branch feature/nueva-funcionalidad

# Cambiar de rama
git checkout feature/nueva-funcionalidad

# Crear y cambiar (shortcut)
git checkout -b feature/nueva-funcionalidad

# Listar ramas
git branch -a

# Fusionar rama
git checkout main
git merge feature/nueva-funcionalidad
```

---

## 📝 Logging y Debugging

### Cambiar Nivel de Log
```properties
# En application.properties
logging.level.root=DEBUG
logging.level.com.alumnos=TRACE
logging.level.org.springframework=INFO
```

### Ver Logs SQL
```properties
# Habilitar logs SQL
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

## 🎯 Workflow de Desarrollo Típico

### Iniciar Sesión de Desarrollo
```powershell
# 1. Navegar al proyecto
cd D:\Desarrollos\alumnos

# 2. Actualizar dependencias (si es necesario)
mvn clean install

# 3. Ejecutar en modo desarrollo
mvn javafx:run

# En otra terminal: ver logs
Get-Content application.log -Wait
```

### Agregar Nueva Funcionalidad
```powershell
# 1. Crear rama
git checkout -b feature/nueva-funcionalidad

# 2. Hacer cambios en el código...

# 3. Compilar y probar
mvn clean compile
mvn test

# 4. Ejecutar aplicación
mvn javafx:run

# 5. Si todo funciona, commit
git add .
git commit -m "feat: Agregar nueva funcionalidad"

# 6. Fusionar con main
git checkout main
git merge feature/nueva-funcionalidad
```

---

## 🆘 Solución de Problemas

### Problema: Maven no encuentra dependencias
```powershell
# Forzar actualización de dependencias
mvn clean install -U

# Limpiar caché de Maven
mvn dependency:purge-local-repository
```

### Problema: Cambios no se reflejan
```powershell
# Limpiar y reconstruir
mvn clean install

# En IntelliJ: File → Invalidate Caches and Restart
```

### Problema: Puerto en uso
```powershell
# Ver qué proceso usa el puerto (ejemplo: 8080)
netstat -ano | findstr :8080

# Matar proceso por PID
taskkill /PID <numero_pid> /F
```

---

## 📚 Scripts Personalizados

### Script de Desarrollo Completo
```powershell
# dev.ps1
Write-Host "Iniciando entorno de desarrollo..." -ForegroundColor Cyan
mvn clean compile
if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilación exitosa!" -ForegroundColor Green
    mvn javafx:run
} else {
    Write-Host "Error en la compilación" -ForegroundColor Red
}
```

### Script de Testing
```powershell
# test.ps1
Write-Host "Ejecutando tests..." -ForegroundColor Cyan
mvn clean test
Write-Host "Tests completados!" -ForegroundColor Green
```

---

**¡Guarda este archivo como referencia rápida durante el desarrollo!**

*Tip: Usa Ctrl+F para buscar comandos específicos en este archivo.*
