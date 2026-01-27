# Sistema de Gestión de Alumnos

Aplicación de escritorio para gestión de alumnos desarrollada con **JavaFX**, **Spring Boot**, **SQLite** y **Maven**, siguiendo principios de **Arquitectura Limpia**.

## 🚀 Tecnologías Utilizadas

- **Java 17**
- **JavaFX 21.0.1** - Framework para interfaz de usuario
- **Spring Boot 3.2.1** - Framework de aplicación
- **Spring Data JPA** - Persistencia de datos
- **SQLite** - Base de datos embebida
- **Maven** - Gestión de dependencias
- **Lombok** - Reducción de código boilerplate

## 📁 Arquitectura del Proyecto

El proyecto sigue los principios de **Arquitectura Limpia (Clean Architecture)**, organizándose en las siguientes capas:

```
src/main/java/com/alumnos/
│
├── domain/                          # Capa de Dominio (Reglas de Negocio)
│   ├── model/                       # Entidades de dominio
│   │   └── Alumno.java
│   └── port/                        # Interfaces de puertos
│       ├── in/                      # Puertos de entrada (casos de uso)
│       │   └── AlumnoServicePort.java
│       └── out/                     # Puertos de salida (repositorios)
│           └── AlumnoRepositoryPort.java
│
├── application/                     # Capa de Aplicación (Casos de Uso)
│   ├── service/                     # Servicios de aplicación
│   │   └── AlumnoService.java
│   └── usecase/                     # Casos de uso específicos
│
├── infrastructure/                  # Capa de Infraestructura (Adaptadores)
│   ├── adapter/
│   │   ├── in/                     # Adaptadores de entrada
│   │   │   └── ui/                 # Interfaz de usuario JavaFX
│   │   │       ├── controller/
│   │   │       │   └── HomeController.java
│   │   │       ├── JavaFXApplication.java
│   │   │       └── FxmlView.java
│   │   └── out/                    # Adaptadores de salida
│   │       └── persistence/        # Persistencia de datos
│   │           ├── entity/
│   │           │   └── AlumnoEntity.java
│   │           └── repository/
│   │               ├── AlumnoJpaRepository.java
│   │               └── AlumnoRepositoryAdapter.java
│   └── config/                     # Configuración
│       └── StageManager.java
│
└── AlumnosApplication.java         # Clase principal

src/main/resources/
├── fxml/                           # Archivos FXML de interfaz
│   └── home.fxml
├── css/                            # Hojas de estilo
│   └── styles.css
├── images/                         # Recursos de imagen
└── application.properties          # Configuración de la aplicación
```

## 🏗️ Principios de Arquitectura Limpia

### 1. **Capa de Dominio (Domain)**
- Contiene la lógica de negocio pura
- Independiente de frameworks y tecnologías
- Define las entidades y reglas de negocio

### 2. **Capa de Aplicación (Application)**
- Contiene los casos de uso de la aplicación
- Orquesta el flujo de datos entre capas
- Implementa las interfaces de puertos de entrada

### 3. **Capa de Infraestructura (Infrastructure)**
- Implementa los detalles técnicos
- Adaptadores para UI, base de datos, etc.
- Implementa las interfaces de puertos de salida

## 🔧 Configuración del Proyecto

### Requisitos Previos

- **Java 17 o superior** - [Descargar OpenJDK](https://adoptium.net/)
- **Maven 3.6 o superior** - [Descargar Maven](https://maven.apache.org/download.cgi)
- **IDE compatible** (IntelliJ IDEA recomendado)

### 📥 Instalación de Requisitos

#### Instalar Java JDK 17+
1. Descargar desde [Adoptium (Eclipse Temurin)](https://adoptium.net/)
2. Ejecutar el instalador y seguir el asistente
3. Verificar la instalación abriendo una terminal:
   ```bash
   java -version
   ```

#### Instalar Maven
1. Descargar Maven desde [Maven Download](https://maven.apache.org/download.cgi)
2. Extraer el archivo ZIP:
   - **Windows**: `C:\Program Files\Apache\maven`
   - **Linux/Mac**: `/opt/maven`
3. Configurar variables de entorno:
   
   **Windows (PowerShell como Administrador):**
   ```powershell
   # Agregar al PATH
   [Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\Program Files\Apache\maven", "Machine")
   $path = [Environment]::GetEnvironmentVariable("Path", "Machine")
   [Environment]::SetEnvironmentVariable("Path", "$path;C:\Program Files\Apache\maven\bin", "Machine")
   ```
   
   **Linux/Mac:**
   ```bash
   # Agregar al ~/.bashrc o ~/.zshrc
   export MAVEN_HOME=/opt/maven
   export PATH=$MAVEN_HOME/bin:$PATH
   ```

4. Reiniciar la terminal y verificar:
   ```bash
   mvn -version
   ```

### Base de Datos

El proyecto utiliza SQLite como base de datos embebida. La base de datos se crea automáticamente al ejecutar la aplicación por primera vez en el archivo `alumnos.db` en la raíz del proyecto.

### Configuración (application.properties)

```properties
# Base de datos SQLite
spring.datasource.url=jdbc:sqlite:alumnos.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 🚀 Ejecutar la Aplicación

### Desde la línea de comandos:

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn javafx:run
```

### Desde IntelliJ IDEA:

1. Importar el proyecto como proyecto Maven
2. Esperar a que Maven descargue las dependencias
3. Ejecutar la clase principal `AlumnosApplication.java`

## 📋 Funcionalidades

- ✅ Registro de nuevos alumnos
- ✅ Listado de todos los alumnos
- ✅ Búsqueda de alumnos por nombre
- ✅ Validación de datos
- ✅ Interfaz gráfica moderna con JavaFX
- ✅ Persistencia con SQLite
- ✅ Arquitectura limpia y mantenible

## 🎨 Características de la Interfaz

- Formulario intuitivo para registro de alumnos
- Tabla con todos los alumnos registrados
- Búsqueda en tiempo real
- Diseño responsive y moderno
- Estadísticas de alumnos registrados

## 🧪 Testing

```bash
# Ejecutar tests
mvn test
```

## 📦 Generar JAR ejecutable

```bash
# Crear JAR con todas las dependencias
mvn clean package

# El JAR se generará en: target/alumnos-1.0-SNAPSHOT.jar
```

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

## 👨‍💻 Autor

Sistema de Gestión de Alumnos - 2026

## 📞 Soporte

Para soporte, por favor abre un issue en el repositorio del proyecto.
