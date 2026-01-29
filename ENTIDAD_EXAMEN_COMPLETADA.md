# ✅ ENTIDAD EXAMEN COMPLETADA CON PERSISTENCIA

## 📋 Resumen

Se ha creado la entidad **Examen** para persistir los aciertos de los alumnos en exámenes, con todos los vínculos necesarios (Grupo, Materia, Parcial, Alumno) y funcionalidad completa de guardar/editar.

---

## 🗂️ Archivos Creados (7 archivos)

### 1. Modelo de Dominio
**`Examen.java`**
```
src/main/java/com/alumnos/domain/model/Examen.java
```
- Modelo que representa un examen
- Campos: id, alumnoId, grupoId, materiaId, parcial, aciertos
- Usa Lombok para reducir código boilerplate

### 2. Puerto de Entrada (Service Port)
**`ExamenServicePort.java`**
```
src/main/java/com/alumnos/domain/port/in/ExamenServicePort.java
```
- Define contratos para el servicio de exámenes
- Métodos CRUD + consultas especiales

### 3. Puerto de Salida (Repository Port)
**`ExamenRepositoryPort.java`**
```
src/main/java/com/alumnos/domain/port/out/ExamenRepositoryPort.java
```
- Define contratos para el repositorio de exámenes
- Métodos de persistencia y consultas

### 4. Servicio de Aplicación
**`ExamenService.java`**
```
src/main/java/com/alumnos/application/service/ExamenService.java
```
- Implementa la lógica de negocio
- Servicio transaccional con Spring

### 5. Entidad JPA
**`ExamenEntity.java`**
```
src/main/java/com/alumnos/infrastructure/adapter/out/persistence/entity/ExamenEntity.java
```
- Entidad JPA para persistencia en SQLite
- Tabla: `examenes`
- Constraint único: (alumno_id, grupo_id, materia_id, parcial)

### 6. Repositorio JPA
**`ExamenJpaRepository.java`**
```
src/main/java/com/alumnos/infrastructure/adapter/out/persistence/repository/ExamenJpaRepository.java
```
- Interfaz Spring Data JPA
- Métodos de consulta personalizados

### 7. Adaptador del Repositorio
**`ExamenRepositoryAdapter.java`**
```
src/main/java/com/alumnos/infrastructure/adapter/out/persistence/repository/ExamenRepositoryAdapter.java
```
- Implementa el puerto de salida
- Convierte entre Entity y Domain Model

---

## 🔄 Modificaciones Realizadas

### HomeController.java

#### 1. Imports Agregados
```java
import com.alumnos.domain.model.Examen;
import com.alumnos.domain.port.in.ExamenServicePort;
```

#### 2. Servicio Inyectado
```java
private final ExamenServicePort examenService;

public HomeController(..., ExamenServicePort examenService) {
    // ...
    this.examenService = examenService;
}
```

#### 3. Vista de Exámenes Mejorada
- **HashMap para almacenar valores**: `aciertosPorAlumno`
- **Carga automática de valores guardados** al generar la tabla
- **Valores por defecto en 0** para alumnos sin exámenes guardados
- **Edición en tiempo real** con validación de 2 dígitos

#### 4. Botón Guardar Exámenes
- Guarda o actualiza todos los exámenes de la tabla
- Distingue entre crear nuevos y actualizar existentes
- Muestra resumen con cantidad de nuevos y actualizados
- Validaciones completas

---

## 🎯 Funcionalidades Implementadas

### ✅ Cargar Valores Guardados
Al hacer clic en "Generar Tabla":
1. Obtiene los alumnos del grupo seleccionado
2. Consulta los exámenes guardados para ese grupo/materia/parcial
3. Carga los valores de aciertos en el HashMap
4. Muestra "0" para alumnos sin valores guardados

### ✅ Edición de Aciertos
- Campo de texto editable en cada fila
- Validación: solo acepta números de 0-99 (máximo 2 dígitos)
- Los cambios se guardan en el HashMap automáticamente

### ✅ Guardar/Actualizar Exámenes
Botón "Guardar Exámenes":
- Verifica que la tabla esté generada
- Para cada alumno en la tabla:
  - Busca si ya existe un examen guardado
  - Si existe: actualiza el valor de aciertos
  - Si no existe: crea un nuevo registro
- Muestra alerta con resumen de operaciones

---

## 🗄️ Estructura de la Base de Datos

### Tabla: `examenes`

| Columna      | Tipo    | Restricciones           |
|--------------|---------|-------------------------|
| id           | BIGINT  | PRIMARY KEY, AUTO_INCREMENT |
| alumno_id    | BIGINT  | NOT NULL                |
| grupo_id     | BIGINT  | NOT NULL                |
| materia_id   | BIGINT  | NOT NULL                |
| parcial      | INTEGER | NOT NULL                |
| aciertos     | INTEGER | NOT NULL                |

**Constraint Único**: `(alumno_id, grupo_id, materia_id, parcial)`
- Garantiza que solo haya un examen por alumno/grupo/materia/parcial

---

## 🔗 Vínculos Implementados

### Examen → Alumno
```java
private Long alumnoId;
```

### Examen → Grupo
```java
private Long grupoId;
```

### Examen → Materia
```java
private Long materiaId;
```

### Examen → Parcial
```java
private Integer parcial;  // 1, 2 o 3
```

---

## 🎨 Interfaz de Usuario

### Panel de Filtros
- **Grupo** (obligatorio)
- **Materia** (obligatorio, se carga según grupo)
- **Parcial** (obligatorio, opciones: 1, 2, 3)
- **Botón "Generar Tabla"**

### Tabla de Exámenes
| N° Lista | Nombre Completo | Aciertos |
|----------|-----------------|----------|
| 1        | Juan Pérez López| 85       |
| 2        | Ana García Ruiz | 92       |

- **N° Lista**: Número de lista del alumno
- **Nombre Completo**: Concatenación de nombre + apellidos
- **Aciertos**: Campo editable (0-99)

### Botón de Acción
- **"Guardar Exámenes"** (color verde)
  - Guarda/actualiza todos los valores de la tabla
  - Muestra resumen de operaciones realizadas

---

## 📊 Flujo de Trabajo

```
1. Usuario selecciona:
   - Grupo
   - Materia (según grupo)
   - Parcial

2. Hace clic en "Generar Tabla"
   ↓
   - Obtiene alumnos del grupo
   - Carga exámenes guardados
   - Muestra tabla con valores (0 por defecto)

3. Usuario edita aciertos
   ↓
   - Valores se guardan en HashMap
   - Validación: solo 0-99

4. Hace clic en "Guardar Exámenes"
   ↓
   - Crea/actualiza registros en BD
   - Muestra alerta con resumen
```

---

## 🏗️ Arquitectura Hexagonal

```
┌─────────────────────────────────────┐
│   CAPA DE PRESENTACIÓN              │
│   HomeController                    │
│   - crearVistaExamenesCompleta()    │
│   - HashMap aciertosPorAlumno       │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   CAPA DE APLICACIÓN                │
│   ExamenService                     │
│   - crearExamen()                   │
│   - actualizarExamen()              │
│   - obtenerExamenes...()            │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   CAPA DE DOMINIO                   │
│   Examen (Domain Model)             │
│   ExamenServicePort (Input Port)    │
│   ExamenRepositoryPort (Output Port)│
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   CAPA DE INFRAESTRUCTURA           │
│   ExamenEntity (JPA)                │
│   ExamenJpaRepository               │
│   ExamenRepositoryAdapter           │
└─────────────────────────────────────┘
```

---

## ✅ Validaciones Implementadas

### Al Generar Tabla
- ✅ Grupo seleccionado
- ✅ Materia seleccionada
- ✅ Parcial seleccionado

### Al Editar Aciertos
- ✅ Solo números (0-9)
- ✅ Máximo 2 dígitos (0-99)
- ✅ No permite caracteres especiales

### Al Guardar
- ✅ Tabla debe estar generada
- ✅ Valores válidos en HashMap
- ✅ Manejo de excepciones

---

## 🔐 Integridad de Datos

### Constraint Único
```sql
UNIQUE (alumno_id, grupo_id, materia_id, parcial)
```

### Comportamiento
- **Primera vez**: Crea nuevo registro
- **Subsecuentes**: Actualiza registro existente
- **No duplicados**: Garantizado por constraint

---

## 📝 Métodos del Servicio

### ExamenServicePort

```java
// CRUD Básico
Examen crearExamen(Examen examen);
Optional<Examen> obtenerExamenPorId(Long id);
List<Examen> obtenerTodosLosExamenes();
Examen actualizarExamen(Examen examen);
void eliminarExamen(Long id);

// Consultas Especiales
List<Examen> obtenerExamenesPorAlumno(Long alumnoId);
List<Examen> obtenerExamenesPorGrupoMateriaParcial(Long grupoId, Long materiaId, Integer parcial);
Optional<Examen> obtenerExamenPorAlumnoGrupoMateriaParcial(Long alumnoId, Long grupoId, Long materiaId, Integer parcial);
```

---

## 🚀 Estado de Implementación

| Componente | Estado | Descripción |
|------------|--------|-------------|
| Modelo de Dominio | ✅ Completo | Examen.java creado |
| Service Port | ✅ Completo | ExamenServicePort.java |
| Repository Port | ✅ Completo | ExamenRepositoryPort.java |
| Servicio | ✅ Completo | ExamenService.java |
| Entidad JPA | ✅ Completo | ExamenEntity.java |
| JPA Repository | ✅ Completo | ExamenJpaRepository.java |
| Repository Adapter | ✅ Completo | ExamenRepositoryAdapter.java |
| Integración UI | ✅ Completo | HomeController modificado |
| HashMap para valores | ✅ Completo | aciertosPorAlumno |
| Carga de valores | ✅ Completo | Al generar tabla |
| Edición en tabla | ✅ Completo | TextField editable |
| Botón Guardar | ✅ Completo | Crear/Actualizar |
| Validaciones | ✅ Completo | Todas implementadas |

---

## 📅 Próximos Pasos (Opcionales)

1. **Reportes**: Generar reportes de exámenes en PDF/Excel
2. **Estadísticas**: Promedios, mejores/peores resultados
3. **Histórico**: Ver exámenes anteriores del alumno
4. **Comparativas**: Comparar resultados entre grupos
5. **Exportación**: Exportar tabla a diferentes formatos

---

## 📅 Fecha de Implementación

**Fecha**: 28 de enero de 2026  
**Estado**: ✅ Completado exitosamente

---

**FIN DEL DOCUMENTO**
