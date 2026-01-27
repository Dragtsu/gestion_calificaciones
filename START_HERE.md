# 🎓 Sistema de Gestión de Alumnos

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.1-blue?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green?style=for-the-badge&logo=spring)
![Maven](https://img.shields.io/badge/Maven-3.6+-red?style=for-the-badge&logo=apache-maven)
![SQLite](https://img.shields.io/badge/SQLite-Database-blue?style=for-the-badge&logo=sqlite)

**Aplicación de escritorio profesional con Arquitectura Limpia**

[Inicio Rápido](#-inicio-rápido) • [Documentación](#-documentación) • [Características](#-características) • [Arquitectura](#-arquitectura)

</div>

---

## 📋 Índice

- [🚀 Inicio Rápido](#-inicio-rápido)
- [📚 Documentación](#-documentación)
- [✨ Características](#-características)
- [🏗️ Arquitectura](#️-arquitectura)
- [🛠️ Tecnologías](#️-tecnologías)
- [📸 Screenshots](#-screenshots)
- [🎯 Estructura del Proyecto](#-estructura-del-proyecto)

---

## 🚀 Inicio Rápido

### ⚠️ Prerrequisitos

**¿No tienes Maven instalado?** 👉 Lee [MAVEN_SETUP.md](MAVEN_SETUP.md) primero

Requisitos:
- ☕ Java JDK 17+ → [Descargar](https://adoptium.net/)
- 📦 Maven 3.6+ → [Descargar](https://maven.apache.org/download.cgi)

Verifica tu instalación:
```bash
java -version    # Debe mostrar Java 17 o superior
mvn -version     # Debe mostrar Maven 3.6 o superior
```

### Opción 1: Script de PowerShell (Recomendado)
```powershell
.\run.ps1
```

### Opción 2: Maven
```bash
mvn clean install -DskipTests
mvn javafx:run
```

### Opción 3: IntelliJ IDEA
1. Abre el proyecto
2. Espera a que Maven descargue las dependencias
3. Ejecuta `AlumnosApplication.java`

> 📖 **Problemas con Maven?** Consulta [MAVEN_SETUP.md](MAVEN_SETUP.md) para instrucciones detalladas

---

## 📚 Documentación

| Documento | Descripción | Acceso Rápido |
|-----------|-------------|---------------|
| **[INDEX.md](INDEX.md)** | 📑 Índice completo de documentación | **EMPIEZA AQUÍ** |
| **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** | 📊 Resumen del proyecto | Visión general |
| **[INSTALLATION.md](INSTALLATION.md)** | 🔧 Instalación de requisitos | Java & Maven |
| **[QUICK_START.md](QUICK_START.md)** | ⚡ Guía de inicio rápido | 5 minutos |
| **[ARCHITECTURE.md](ARCHITECTURE.md)** | 🏛️ Arquitectura del sistema | Diagramas |
| **[COMMANDS.md](COMMANDS.md)** | 💻 Comandos útiles | Referencia |

---

## ✨ Características

### 🎯 Funcionalidades Principales

- ✅ **Registro de Alumnos** - Formulario completo e intuitivo
- ✅ **Lista Dinámica** - Tabla con todos los alumnos registrados
- ✅ **Búsqueda Inteligente** - Por nombre en tiempo real
- ✅ **Validación de Datos** - Email único, campos obligatorios
- ✅ **Persistencia** - Base de datos SQLite embebida
- ✅ **Datos de Ejemplo** - 3 alumnos precargados al iniciar
- ✅ **Estadísticas** - Contador de alumnos en tiempo real

### 📋 Datos del Alumno

```
┌─────────────────────────────────┐
│  📝 Información del Alumno      │
├─────────────────────────────────┤
│  • ID (Auto-generado)           │
│  • Nombre                       │
│  • Apellido                     │
│  • Email (Único)                │
│  • Número de Matrícula (Único) │
│  • Fecha de Nacimiento          │
│  • Estado (Activo/Inactivo)    │
└─────────────────────────────────┘
```

---

## 🏗️ Arquitectura

### Clean Architecture (Hexagonal)

```
┌─────────────────────────────────────────┐
│         PRESENTATION LAYER              │
│         (JavaFX + FXML)                 │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│        APPLICATION LAYER                │
│        (Business Logic)                 │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│          DOMAIN LAYER                   │
│          (Core Business)                │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      INFRASTRUCTURE LAYER               │
│      (Database, Config)                 │
└─────────────────────────────────────────┘
```

> 📖 Ver [ARCHITECTURE.md](ARCHITECTURE.md) para diagramas detallados

### Principios Aplicados

- ✅ **SOLID Principles**
- ✅ **Dependency Inversion**
- ✅ **Separation of Concerns**
- ✅ **Ports & Adapters Pattern**
- ✅ **Repository Pattern**

---

## 🛠️ Tecnologías

### Backend
- **Spring Boot 3.2.1** - Framework de aplicación
- **Spring Data JPA** - Persistencia de datos
- **Hibernate** - ORM
- **SQLite** - Base de datos embebida

### Frontend
- **JavaFX 21.0.1** - Framework de UI
- **FXML** - Diseño declarativo de interfaces
- **CSS** - Estilos personalizados

### Build & Tools
- **Maven** - Gestión de dependencias
- **Lombok** - Reducción de boilerplate
- **Java 17** - Lenguaje de programación

---

## 📸 Screenshots

### Pantalla Principal

```
╔══════════════════════════════════════════════════════════╗
║  Sistema de Gestión de Alumnos                          ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  ┌─── Registrar Nuevo Alumno ────────────────────────┐  ║
║  │                                                    │  ║
║  │  Nombre:    [____________]  Apellido: [_________] │  ║
║  │  Email:     [____________]  Matrícula:[_________] │  ║
║  │  Fecha Nac: [____________]                        │  ║
║  │                                                    │  ║
║  │  [ Guardar ]  [ Limpiar ]                        │  ║
║  └────────────────────────────────────────────────────┘  ║
║                                                          ║
║  ┌─── Lista de Alumnos ──────────────────────────────┐  ║
║  │                                                    │  ║
║  │  Buscar: [__________] [ 🔍 Buscar ]              │  ║
║  │                                                    │  ║
║  │  ┌───┬─────────┬──────────┬─────────────────┐    │  ║
║  │  │ID │ Nombre  │ Apellido │ Email           │    │  ║
║  │  ├───┼─────────┼──────────┼─────────────────┤    │  ║
║  │  │ 1 │ Juan    │ Pérez    │ juan@email.com  │    │  ║
║  │  │ 2 │ María   │ López    │ maria@email.com │    │  ║
║  │  │ 3 │ Carlos  │ García   │ carlos@email.com│    │  ║
║  │  └───┴─────────┴──────────┴─────────────────┘    │  ║
║  │                                                    │  ║
║  │  Total de alumnos: 3                              │  ║
║  └────────────────────────────────────────────────────┘  ║
║                                                          ║
╠══════════════════════════════════════════════════════════╣
║  © 2026 Sistema de Gestión de Alumnos                   ║
╚══════════════════════════════════════════════════════════╝
```

---

## 🎯 Estructura del Proyecto

```
alumnos/
├── 📄 pom.xml                    # Configuración Maven
├── 📄 run.ps1                    # Script de ejecución
├── 📚 DOCUMENTACIÓN/
│   ├── INDEX.md                  # Índice
│   ├── PROJECT_SUMMARY.md        # Resumen
│   ├── ARCHITECTURE.md           # Arquitectura
│   ├── README.md                 # Este archivo
│   ├── INSTALLATION.md           # Instalación
│   ├── QUICK_START.md            # Inicio rápido
│   └── COMMANDS.md               # Comandos
│
└── 📂 src/main/
    ├── java/com/alumnos/
    │   ├── 🎯 domain/            # Capa de Dominio
    │   │   ├── model/
    │   │   └── port/
    │   ├── 🔧 application/       # Capa de Aplicación
    │   │   └── service/
    │   └── 🏗️ infrastructure/    # Capa de Infraestructura
    │       ├── adapter/
    │       └── config/
    │
    └── resources/
        ├── fxml/                 # Vistas JavaFX
        ├── css/                  # Estilos
        └── application.properties
```

---

## 🚦 Requisitos del Sistema

| Componente | Versión Requerida | Verificación |
|------------|-------------------|--------------|
| Java JDK   | 17 o superior    | `java -version` |
| Maven      | 3.6 o superior   | `mvn -version` |
| Memoria RAM| Mínimo 2GB       | - |
| Disco      | 500MB libres     | - |

---

## 📖 Guía de Lectura Rápida

### Para Nuevos Desarrolladores
1. ⭐ **[INDEX.md](INDEX.md)** - Empieza aquí
2. 🔧 **[INSTALLATION.md](INSTALLATION.md)** - Configura tu entorno
3. ⚡ **[QUICK_START.md](QUICK_START.md)** - Primera ejecución
4. 🏛️ **[ARCHITECTURE.md](ARCHITECTURE.md)** - Entiende el código

### Para Desarrolladores Experimentados
1. 🏛️ **[ARCHITECTURE.md](ARCHITECTURE.md)** - Visión arquitectural
2. 📊 **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Overview completo
3. 💻 **[COMMANDS.md](COMMANDS.md)** - Comandos útiles

---

## 💡 Ejemplos de Uso

### Crear un Alumno

```java
Alumno alumno = Alumno.builder()
    .nombre("Juan")
    .apellido("Pérez")
    .email("juan@ejemplo.com")
    .numeroMatricula("MAT-001")
    .fechaNacimiento(LocalDate.of(2000, 5, 15))
    .activo(true)
    .build();

alumnoService.crearAlumno(alumno);
```

### Buscar Alumnos

```java
// Obtener todos los alumnos
List<Alumno> todos = alumnoService.obtenerTodosLosAlumnos();

// Buscar por nombre
List<Alumno> resultados = alumnoService.buscarPorNombre("Juan");

// Obtener por ID
Optional<Alumno> alumno = alumnoService.obtenerAlumnoPorId(1L);
```

---

## 🔧 Comandos Frecuentes

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn javafx:run

# Tests
mvn test

# Package
mvn clean package

# Limpiar DB
Remove-Item alumnos.db
```

> 💡 Ver [COMMANDS.md](COMMANDS.md) para más comandos

---

## 🎓 Próximas Funcionalidades

- [ ] Actualización de alumnos
- [ ] Eliminación de alumnos
- [ ] Exportar a PDF/Excel
- [ ] Gráficos estadísticos
- [ ] Sistema de calificaciones
- [ ] Gestión de cursos
- [ ] Multi-idioma
- [ ] Tema oscuro/claro

---

## 🤝 Contribuir

Las contribuciones son bienvenidas:

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/NuevaCaracteristica`)
3. Commit cambios (`git commit -m 'Agregar nueva característica'`)
4. Push (`git push origin feature/NuevaCaracteristica`)
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto es open source y está disponible bajo la licencia MIT.

---

## 👨‍💻 Autor

Sistema de Gestión de Alumnos - 2026

---

## 🌟 Agradecimientos

- **Robert C. Martin** - Clean Architecture
- **Alistair Cockburn** - Hexagonal Architecture
- **Comunidad Spring** - Framework excepcional
- **Comunidad JavaFX** - UI moderna para Java

---

<div align="center">

**⭐ Si este proyecto te fue útil, considera darle una estrella ⭐**

[📖 Documentación](INDEX.md) • [🐛 Reportar Bug](issues) • [💡 Solicitar Feature](issues)

**Hecho con ❤️ y Clean Architecture**

</div>
