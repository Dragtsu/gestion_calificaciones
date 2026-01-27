# ✅ Configuración Completa del Proyecto Maven

## 📋 Resumen de Configuración

El proyecto **Sistema de Gestión de Alumnos** ha sido completamente configurado como proyecto Maven con las siguientes características:

### 🎯 Tecnologías Configuradas

- ✅ **Java 17** - JDK requerido
- ✅ **Maven** - Gestión de dependencias y construcción
- ✅ **Spring Boot 3.2.1** - Framework de aplicación
- ✅ **JavaFX 21.0.1** - Interfaz gráfica de usuario
- ✅ **SQLite** - Base de datos embebida
- ✅ **Spring Data JPA** - Persistencia de datos
- ✅ **Lombok** - Reducción de código boilerplate
- ✅ **Hibernate Community Dialects** - Soporte para SQLite

## 📂 Archivos Configurados

### 1. pom.xml ✅
El archivo Maven principal con todas las dependencias:

```xml
- Spring Boot Starter
- Spring Data JPA
- SQLite JDBC Driver
- Hibernate SQLite Dialect
- JavaFX Controls & FXML
- Lombok
- Spring Boot Validation
- Spring Boot Test
```

**Plugins configurados:**
- `spring-boot-maven-plugin` - Para empaquetar la aplicación
- `javafx-maven-plugin` - Para ejecutar JavaFX

### 2. application.properties ✅
Configuración de la aplicación:

```properties
- Nombre de la aplicación
- URL de SQLite (jdbc:sqlite:alumnos.db)
- Configuración JPA/Hibernate
- Logging configurado
```

### 3. Estructura de Arquitectura Limpia ✅

```
src/main/java/com/alumnos/
├── domain/                    ✅ Capa de Dominio
│   ├── model/
│   │   └── Alumno.java       ✅ Entidad de dominio
│   └── port/
│       ├── in/
│       │   └── AlumnoServicePort.java        ✅ Puerto de entrada
│       └── out/
│           └── AlumnoRepositoryPort.java     ✅ Puerto de salida
│
├── application/              ✅ Capa de Aplicación
│   └── service/
│       └── AlumnoService.java               ✅ Implementación de casos de uso
│
├── infrastructure/           ✅ Capa de Infraestructura
│   ├── adapter/
│   │   ├── in/ui/           ✅ Adaptador de entrada (JavaFX)
│   │   │   ├── JavaFXApplication.java
│   │   │   ├── FxmlView.java
│   │   │   └── controller/
│   │   │       └── HomeController.java
│   │   └── out/persistence/ ✅ Adaptador de salida (Base de datos)
│   │       ├── entity/
│   │       │   └── AlumnoEntity.java
│   │       └── repository/
│   │           ├── AlumnoJpaRepository.java
│   │           └── AlumnoRepositoryAdapter.java
│   └── config/              ✅ Configuración
│       ├── ApplicationConfig.java
│       ├── StageManager.java
│       └── DataInitializer.java
│
└── AlumnosApplication.java  ✅ Clase principal
```

### 4. Recursos ✅

```
src/main/resources/
├── fxml/
│   └── home.fxml            ✅ Interfaz de usuario principal
├── css/
│   └── styles.css           ✅ Estilos de la aplicación
├── images/                  ✅ Directorio para imágenes
└── application.properties   ✅ Configuración
```

### 5. Scripts de Ejecución ✅

- `run.ps1` - Script PowerShell con menú interactivo
- `compile.bat` - Script batch para compilación rápida

## 📚 Documentación Creada

- ✅ `README.md` - Documentación principal actualizada
- ✅ `MAVEN_SETUP.md` - **Guía completa de instalación de Maven**
- ✅ `INDEX.md` - Índice actualizado con referencia a Maven
- ✅ `START_HERE.md` - Guía de inicio actualizada
- ✅ `ARCHITECTURE.md` - Arquitectura limpia documentada
- ✅ `INSTALLATION.md` - Guía de instalación
- ✅ `QUICK_START.md` - Inicio rápido
- ✅ `COMMANDS.md` - Comandos Maven y desarrollo
- ✅ `PROJECT_SUMMARY.md` - Resumen del proyecto

## 🚀 Cómo Usar el Proyecto

### Paso 1: Instalar Requisitos

Si no tienes Maven instalado:

1. **Lee MAVEN_SETUP.md** - Guía paso a paso
2. Instala Java 17+ desde [Adoptium](https://adoptium.net/)
3. Instala Maven desde [Maven.apache.org](https://maven.apache.org/download.cgi)
4. Configura variables de entorno (MAVEN_HOME, PATH)
5. Reinicia tu terminal/sistema

### Paso 2: Verificar Instalación

```bash
java -version   # Debe mostrar 17 o superior
mvn -version    # Debe mostrar 3.6 o superior
```

### Paso 3: Compilar el Proyecto

```bash
# Navegar al directorio del proyecto
cd D:\Desarrollos\alumnos

# Opción 1: Compilar con Maven
mvn clean install -DskipTests

# Opción 2: Usar script de Windows
.\compile.bat

# Opción 3: Usar script PowerShell
.\run.ps1
```

### Paso 4: Ejecutar la Aplicación

```bash
# Opción 1: Con Maven
mvn javafx:run

# Opción 2: Con script PowerShell (incluye menú)
.\run.ps1

# Opción 3: Desde IntelliJ IDEA
# - Abrir el proyecto
# - Ejecutar AlumnosApplication.java
```

## 🎨 Funcionalidades Implementadas

### Frontend (JavaFX)
- ✅ Pantalla de inicio (home.fxml)
- ✅ Formulario de registro de alumnos
- ✅ Tabla con listado de alumnos
- ✅ Búsqueda por nombre
- ✅ Validación de campos
- ✅ Estadísticas en tiempo real
- ✅ Estilos CSS modernos
- ✅ Diseño responsive

### Backend (Spring Boot)
- ✅ Integración Spring Boot + JavaFX
- ✅ Inyección de dependencias
- ✅ Servicios transaccionales
- ✅ Repositorios JPA
- ✅ Inicializador de datos de ejemplo
- ✅ Validación de reglas de negocio

### Base de Datos (SQLite)
- ✅ Base de datos embebida (alumnos.db)
- ✅ Tabla de alumnos con todos los campos
- ✅ Generación automática de esquema
- ✅ Datos de ejemplo pre-cargados
- ✅ Constraints (email único, matrícula única)

## 📊 Comandos Maven Disponibles

```bash
# Limpiar compilación anterior
mvn clean

# Compilar código
mvn compile

# Ejecutar tests
mvn test

# Compilar y empaquetar
mvn package

# Instalar en repositorio local
mvn install

# Ejecutar aplicación JavaFX
mvn javafx:run

# Compilar sin tests
mvn clean install -DskipTests

# Ver árbol de dependencias
mvn dependency:tree

# Actualizar dependencias
mvn versions:display-dependency-updates
```

## 🎯 Próximos Pasos

### Para Desarrolladores

1. **Familiarízate con la arquitectura**
   - Lee `ARCHITECTURE.md`
   - Entiende las capas: Domain → Application → Infrastructure

2. **Explora el código**
   - Comienza por `AlumnosApplication.java`
   - Revisa `HomeController.java` para la UI
   - Examina `AlumnoService.java` para la lógica

3. **Prueba la aplicación**
   - Ejecuta con `mvn javafx:run`
   - Registra nuevos alumnos
   - Prueba la búsqueda
   - Observa la base de datos `alumnos.db`

### Para Extender el Proyecto

- Agregar más campos a Alumno
- Crear nuevas pantallas (CRUD completo)
- Implementar reportes
- Agregar exportación a PDF/Excel
- Implementar autenticación
- Agregar más validaciones

## 🐛 Solución de Problemas

### Maven no reconocido
**Error:** `mvn : The term 'mvn' is not recognized`

**Solución:** Consulta `MAVEN_SETUP.md` sección completa de instalación

### Java no encontrado
**Error:** `JAVA_HOME is not defined`

**Solución:**
```powershell
# Windows PowerShell como Administrador
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.X-hotspot", "Machine")
```

### Error al descargar dependencias
**Solución:**
```bash
# Limpiar repositorio Maven
Remove-Item -Recurse -Force $env:USERPROFILE\.m2\repository
mvn clean install
```

### JavaFX no se ejecuta
**Solución:**
- Verificar Java 17+ instalado
- Ejecutar con `mvn javafx:run` (no `mvn spring-boot:run`)
- Verificar que las dependencias de JavaFX estén descargadas

## 📝 Notas Adicionales

### Base de Datos
- La base de datos `alumnos.db` se crea automáticamente en la raíz del proyecto
- Incluye 3 alumnos de ejemplo al iniciar por primera vez
- Para reiniciar, simplemente elimina el archivo `alumnos.db`

### Desarrollo
- El proyecto usa Lombok - asegúrate de tener el plugin instalado en tu IDE
- Hot reload no está habilitado - reinicia la aplicación tras cambios
- Los logs se muestran en la consola (nivel DEBUG para com.alumnos)

### Producción
- Para generar JAR: `mvn clean package`
- El JAR se genera en `target/alumnos-1.0-SNAPSHOT.jar`
- Ejecutar con: `java -jar target/alumnos-1.0-SNAPSHOT.jar`

## 🎓 Conclusión

El proyecto está **100% configurado y listo para usar**. Solo necesitas:

1. ✅ Instalar Java 17+
2. ✅ Instalar Maven
3. ✅ Ejecutar `mvn clean install`
4. ✅ Ejecutar `mvn javafx:run`

**¡Disfruta desarrollando!** 🚀

---

**Documentación adicional:**
- [MAVEN_SETUP.md](MAVEN_SETUP.md) - Instalación de Maven
- [README.md](README.md) - Documentación completa
- [ARCHITECTURE.md](ARCHITECTURE.md) - Arquitectura del proyecto
- [COMMANDS.md](COMMANDS.md) - Referencia de comandos
