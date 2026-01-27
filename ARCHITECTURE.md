# Arquitectura del Sistema - Diagrama de Capas

## 📐 Diagrama de Arquitectura Limpia

```
┌─────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                       │
│                     (JavaFX User Interface)                      │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  HomeController.java                                       │ │
│  │  - Maneja eventos de UI                                    │ │
│  │  - Interactúa con servicios                                │ │
│  └────────────────────────────────────────────────────────────┘ │
└───────────────────────────────┬─────────────────────────────────┘
                                │ depende de
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                        APPLICATION LAYER                         │
│                    (Business Logic / Use Cases)                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  AlumnoService.java                                        │ │
│  │  - Implementa lógica de negocio                            │ │
│  │  - Orquesta operaciones                                    │ │
│  │  - Valida reglas de negocio                                │ │
│  └────────────────────────────────────────────────────────────┘ │
└───────────────────────────────┬─────────────────────────────────┘
                                │ depende de
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                          DOMAIN LAYER                            │
│                  (Core Business / Entities)                      │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  Alumno.java (Domain Model)                                │ │
│  │  - Entidad de dominio pura                                 │ │
│  │  - Sin dependencias externas                               │ │
│  │  - Representa el modelo de negocio                         │ │
│  └────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  Ports (Interfaces)                                        │ │
│  │  - AlumnoServicePort (Input Port)                          │ │
│  │  - AlumnoRepositoryPort (Output Port)                      │ │
│  └────────────────────────────────────────────────────────────┘ │
└───────────────────────────────┬─────────────────────────────────┘
                                │ implementado por
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      INFRASTRUCTURE LAYER                        │
│                  (External Services / Database)                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  AlumnoRepositoryAdapter.java                              │ │
│  │  - Implementa AlumnoRepositoryPort                         │ │
│  │  - Traduce entre dominio y persistencia                    │ │
│  └────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  AlumnoJpaRepository.java                                  │ │
│  │  - Interfaz Spring Data JPA                                │ │
│  │  - Interactúa con SQLite                                   │ │
│  └────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  AlumnoEntity.java                                         │ │
│  │  - Entidad JPA                                             │ │
│  │  - Mapeada a tabla de base de datos                        │ │
│  └────────────────────────────────────────────────────────────┘ │
└───────────────────────────────┬─────────────────────────────────┘
                                │ persiste en
                                ▼
                        ┌─────────────────┐
                        │  SQLite Database │
                        │   alumnos.db     │
                        └─────────────────┘
```

## 🔄 Flujo de Datos

```
Usuario → JavaFX UI → Controller → Service → Repository Port
                                      ↓              ↓
                                  Domain Model  ← Adapter
                                                     ↓
                                                JPA Repository
                                                     ↓
                                                  SQLite DB
```

## 📊 Estructura de Paquetes Detallada

```
com.alumnos/
│
├── 🎯 DOMAIN (Núcleo del Sistema)
│   ├── model/
│   │   └── Alumno.java                    # Entidad de dominio
│   └── port/
│       ├── in/
│       │   └── AlumnoServicePort.java     # Contrato de servicios
│       └── out/
│           └── AlumnoRepositoryPort.java  # Contrato de repositorio
│
├── 🔧 APPLICATION (Casos de Uso)
│   ├── service/
│   │   └── AlumnoService.java             # Implementación de lógica
│   └── usecase/
│       └── [Casos de uso específicos]
│
├── 🏗️ INFRASTRUCTURE (Detalles Técnicos)
│   ├── adapter/
│   │   ├── in/
│   │   │   └── ui/
│   │   │       ├── JavaFXApplication.java # Punto de entrada JavaFX
│   │   │       ├── FxmlView.java         # Enum de vistas
│   │   │       └── controller/
│   │   │           └── HomeController.java # Controlador UI
│   │   └── out/
│   │       └── persistence/
│   │           ├── entity/
│   │           │   └── AlumnoEntity.java # Entidad JPA
│   │           └── repository/
│   │               ├── AlumnoJpaRepository.java      # Spring Data
│   │               └── AlumnoRepositoryAdapter.java  # Adapter pattern
│   └── config/
│       ├── ApplicationConfig.java         # Config Spring
│       ├── StageManager.java             # Gestor de escenas JavaFX
│       └── DataInitializer.java          # Datos iniciales
│
└── AlumnosApplication.java                # Main application class
```

## 🎨 Patrón de Diseño: Hexagonal Architecture (Ports & Adapters)

```
                    ┌─────────────────────┐
                    │                     │
           ┌────────│  DOMAIN LOGIC       │────────┐
           │        │  (Business Core)    │        │
           │        │                     │        │
           │        └─────────────────────┘        │
           │                                       │
    ┌──────▼─────┐                        ┌───────▼──────┐
    │            │                        │              │
    │ INPUT PORT │                        │ OUTPUT PORT  │
    │ (Use Cases)│                        │ (Repository) │
    │            │                        │              │
    └──────┬─────┘                        └───────┬──────┘
           │                                      │
           │                                      │
    ┌──────▼─────┐                        ┌───────▼──────┐
    │            │                        │              │
    │ UI ADAPTER │                        │ DB ADAPTER   │
    │ (JavaFX)   │                        │ (JPA/SQLite) │
    │            │                        │              │
    └────────────┘                        └──────────────┘
```

## 🔐 Principios SOLID Aplicados

### 1. **Single Responsibility Principle (SRP)**
- Cada clase tiene una única responsabilidad
- `HomeController`: Solo maneja la UI
- `AlumnoService`: Solo lógica de negocio
- `AlumnoRepositoryAdapter`: Solo persistencia

### 2. **Open/Closed Principle (OCP)**
- Abierto para extensión, cerrado para modificación
- Nuevas funcionalidades mediante nuevos adaptadores

### 3. **Liskov Substitution Principle (LSP)**
- Los adaptadores implementan las interfaces de puerto
- Pueden ser reemplazados sin afectar el dominio

### 4. **Interface Segregation Principle (ISP)**
- Interfaces específicas y segregadas
- `AlumnoServicePort` y `AlumnoRepositoryPort` son independientes

### 5. **Dependency Inversion Principle (DIP)**
- Las dependencias apuntan hacia abstracciones
- El dominio no depende de la infraestructura
- La infraestructura depende del dominio

## 🎯 Beneficios de esta Arquitectura

✅ **Testabilidad**: Fácil de hacer unit tests
✅ **Mantenibilidad**: Código organizado y limpio
✅ **Escalabilidad**: Fácil agregar nuevas funcionalidades
✅ **Independencia**: El dominio no depende de frameworks
✅ **Flexibilidad**: Fácil cambiar tecnologías (BD, UI, etc.)

## 🔄 Flujo de una Operación (Ejemplo: Guardar Alumno)

```
1. Usuario llena formulario y hace clic en "Guardar"
   ↓
2. HomeController.handleGuardar()
   ↓
3. Crea objeto Alumno (Domain Model)
   ↓
4. Llama a alumnoService.crearAlumno(alumno)
   ↓
5. AlumnoService valida reglas de negocio
   ↓
6. Llama a alumnoRepositoryPort.save(alumno)
   ↓
7. AlumnoRepositoryAdapter traduce Domain → Entity
   ↓
8. AlumnoJpaRepository.save(entity)
   ↓
9. Spring Data JPA persiste en SQLite
   ↓
10. Retorna el resultado al Controller
    ↓
11. Controller actualiza la UI
```

## 📚 Referencias

- Clean Architecture (Robert C. Martin)
- Hexagonal Architecture (Alistair Cockburn)
- Domain-Driven Design (Eric Evans)
- SOLID Principles
