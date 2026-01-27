# Entidad Grupo - Implementación Completa

## Resumen
Se ha creado la entidad **Grupo** siguiendo la arquitectura limpia del proyecto (Domain-Driven Design). La entidad incluye:

- **numeroGrupo**: Campo numérico de 3 dígitos (001-999) formateado automáticamente
- **nombreGrupo**: Nombre descriptivo del grupo
- **activo**: Estado del grupo

## Archivos Creados

### 1. Capa de Dominio
- `src/main/java/com/alumnos/domain/model/Grupo.java`
  - Modelo de dominio con Lombok (Builder, Data, etc.)
  - Campos: id, numeroGrupo, nombreGrupo, activo

- `src/main/java/com/alumnos/domain/port/in/GrupoServicePort.java`
  - Interfaz de puerto de entrada (use cases)
  - Métodos: crearGrupo, obtenerGrupoPorId, obtenerTodosLosGrupos, actualizarGrupo, eliminarGrupo, buscarPorNombre, existeNumeroGrupo

- `src/main/java/com/alumnos/domain/port/out/GrupoRepositoryPort.java`
  - Interfaz de puerto de salida (repositorio)
  - Métodos: save, findById, findAll, deleteById, findByNombreGrupoContaining, existsByNumeroGrupo

### 2. Capa de Aplicación
- `src/main/java/com/alumnos/application/service/GrupoService.java`
  - Implementación de la lógica de negocio
  - Validaciones:
    - Número de grupo debe estar entre 001 y 999
    - No se permiten números de grupo duplicados
    - Nombre de grupo requerido

### 3. Capa de Infraestructura

#### Persistencia
- `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/entity/GrupoEntity.java`
  - Entidad JPA con anotaciones
  - Tabla: "grupos"
  - Campos con restricciones (unique, nullable)

- `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/repository/GrupoJpaRepository.java`
  - Interfaz JPA Repository
  - Métodos personalizados para búsqueda

- `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/repository/GrupoRepositoryAdapter.java`
  - Adaptador que conecta el dominio con la infraestructura
  - Mapeo entre Grupo (dominio) y GrupoEntity (JPA)

#### Controlador UI
- Modificado: `src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/HomeController.java`
  - Inyección de GrupoServicePort
  - Método `mostrarVentanaGrupos()` que crea una ventana modal completa para gestión de grupos
  - Método `cargarGrupos()` para cargar datos en la tabla

## Características de la Interfaz de Grupos

La ventana de gestión de grupos incluye:

### Formulario de Registro
- **Campo Número de Grupo**: 
  - Validación automática para solo números
  - Máximo 3 dígitos
  - Formateado a 3 dígitos con ceros a la izquierda (001, 002, etc.)
  
- **Campo Nombre de Grupo**:
  - Texto libre para el nombre descriptivo
  - Requerido

- **Botones**:
  - Guardar: Crea un nuevo grupo con validaciones
  - Limpiar: Limpia los campos del formulario

### Tabla de Grupos
- Columnas:
  - ID
  - Número (formateado a 3 dígitos)
  - Nombre del Grupo
  - Activo
  - Acciones (botón Eliminar)

- Funcionalidades:
  - Búsqueda por nombre
  - Doble clic para editar
  - Eliminación con confirmación
  - Contador de grupos totales

### Validaciones Implementadas
1. Número de grupo entre 001 y 999
2. No se permiten números duplicados
3. Nombre de grupo requerido
4. Confirmación antes de eliminar

## Integración con el Menú

El módulo de Grupos está integrado en el menú lateral tipo Android existente:
- Ícono: 👥
- Ubicación: Cuarto item del menú
- Al hacer clic, se abre una ventana modal de gestión completa

## Formato del Número de Grupo

El número de grupo se formatea automáticamente a 3 dígitos:
- Input: 1 → Display: 001
- Input: 25 → Display: 025
- Input: 999 → Display: 999

Esto se implementa mediante:
- Validación en el TextField (solo números, máximo 3 dígitos)
- Formateo en la celda de la tabla usando `String.format("%03d", item)`
- Validación en el servicio (1-999)

## Base de Datos

La tabla `grupos` se creará automáticamente con:
- id (PRIMARY KEY, AUTO_INCREMENT)
- numero_grupo (INTEGER, UNIQUE, NOT NULL)
- nombre_grupo (VARCHAR, NOT NULL)
- activo (BOOLEAN, NOT NULL)

## Cómo Ejecutar

### Opción 1: Con Script (Recomendado)
```powershell
.\run.ps1
```

### Opción 2: Con Maven (si está instalado)
```bash
mvn clean install
mvn spring-boot:run
```

### Opción 3: Desde IDE
1. Abrir el proyecto en IntelliJ IDEA
2. Ejecutar la clase `AlumnosApplication.java`
3. O usar el botón de Run

## Próximos Pasos

Si deseas compilar y ejecutar el proyecto:

1. **Instalar Maven** (si no está instalado):
   ```powershell
   # Con Chocolatey
   choco install maven
   
   # O seguir las instrucciones en MAVEN_SETUP.md
   ```

2. **Compilar el proyecto**:
   ```powershell
   cd D:\Desarrollos\alumnos
   mvn clean install
   ```

3. **Ejecutar la aplicación**:
   ```powershell
   .\run.ps1
   # O
   mvn spring-boot:run
   ```

## Arquitectura

El proyecto sigue los principios de Arquitectura Limpia (Clean Architecture):

```
┌─────────────────────────────────────────┐
│         Presentación (UI)               │
│  HomeController + JavaFX Views          │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      Capa de Aplicación                 │
│  GrupoService (Lógica de Negocio)       │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│         Dominio (Core)                  │
│  Grupo (Modelo)                         │
│  GrupoServicePort (Puerto In)           │
│  GrupoRepositoryPort (Puerto Out)       │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      Infraestructura                    │
│  GrupoEntity (JPA)                      │
│  GrupoJpaRepository                     │
│  GrupoRepositoryAdapter                 │
│  SQLite Database                        │
└─────────────────────────────────────────┘
```

## Notas Técnicas

- **Framework**: Spring Boot 3.2.1
- **Java**: Version 22
- **JavaFX**: Version 21.0.1
- **Base de Datos**: SQLite 3.44.1.0
- **ORM**: Hibernate con dialect para SQLite
- **Patrón**: Repository Pattern + Clean Architecture
- **Inyección de Dependencias**: Spring DI
- **UI**: JavaFX con FXML

## Testing

Para ejecutar los tests (cuando se implementen):
```bash
mvn test
```

---

**Fecha de creación**: 2026-01-26
**Desarrollado por**: Sistema de Gestión de Alumnos
