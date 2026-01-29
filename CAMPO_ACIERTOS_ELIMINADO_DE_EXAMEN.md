# ✅ CAMPO ACIERTOS ELIMINADO DE ENTIDAD EXAMEN - MOVIDO A ALUMNO_EXAMEN

## 📋 Resumen

Se ha eliminado el campo `aciertos` de la entidad `Examen`, ya que este campo se ha movido a la nueva entidad `AlumnoExamen`. Ahora un examen representa el examen en sí (grupo, materia, parcial, total de aciertos), mientras que `AlumnoExamen` representa los aciertos individuales de cada alumno en ese examen.

---

## 🔄 Arquitectura Actualizada

### Antes
```
Examen
- id
- alumnoId          ❌ (eliminado)
- grupoId
- materiaId
- parcial
- aciertos          ❌ (eliminado)
- totalAciertos
```

### Ahora
```
Examen (uno por grupo/materia/parcial)
- id
- grupoId
- materiaId
- parcial
- totalAciertos

AlumnoExamen (muchos por examen, uno por alumno)
- id
- alumnoId
- examenId
- aciertos
```

---

## 📝 Archivos Modificados

### 1. Modelo de Dominio - Examen.java
**Ruta**: `src/main/java/com/alumnos/domain/model/Examen.java`

**Cambios realizados**:
- ❌ Eliminado campo `alumnoId`
- ❌ Eliminado campo `aciertos`
- ❌ Eliminado campo opcional `nombreAlumno`
- ✅ Mantenido campo `totalAciertos`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Examen {
    private Long id;
    private Long grupoId;
    private Long materiaId;
    private Integer parcial;  // 1, 2 o 3
    private Integer totalAciertos;  // Total de aciertos del examen (máximo de aciertos posibles)

    // Campos opcionales para mostrar información
    private String nombreMateria;
}
```

### 2. Entidad JPA - ExamenEntity.java
**Ruta**: `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/entity/ExamenEntity.java`

**Cambios realizados**:
- ❌ Eliminado campo `alumnoId`
- ❌ Eliminado campo `aciertos`
- ✅ Actualizado `uniqueConstraint` de `(alumno_id, grupo_id, materia_id, parcial)` a `(grupo_id, materia_id, parcial)`

```java
@Entity
@Table(name = "examenes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"grupo_id", "materia_id", "parcial"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grupo_id", nullable = false)
    private Long grupoId;

    @Column(name = "materia_id", nullable = false)
    private Long materiaId;

    @Column(nullable = false)
    private Integer parcial;

    @Column(name = "total_aciertos")
    private Integer totalAciertos;
}
```

### 3. Repositorio JPA - ExamenJpaRepository.java
**Ruta**: `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/repository/ExamenJpaRepository.java`

**Cambios realizados**:
- ❌ Eliminado método `findByAlumnoId(Long alumnoId)`
- ❌ Eliminado método `findByAlumnoIdAndGrupoIdAndMateriaIdAndParcial(...)`
- ✅ Mantenido método `findByGrupoIdAndMateriaIdAndParcial(...)` que ahora devuelve `Optional<ExamenEntity>`

```java
@Repository
public interface ExamenJpaRepository extends JpaRepository<ExamenEntity, Long> {
    Optional<ExamenEntity> findByGrupoIdAndMateriaIdAndParcial(Long grupoId, Long materiaId, Integer parcial);
}
```

### 4. Puerto de Salida - ExamenRepositoryPort.java
**Ruta**: `src/main/java/com/alumnos/domain/port/out/ExamenRepositoryPort.java`

**Cambios realizados**:
- ❌ Eliminado método `findByAlumnoId(Long alumnoId)`
- ❌ Eliminado método `findByAlumnoIdAndGrupoIdAndMateriaIdAndParcial(...)`
- ✅ Mantenido método `findByGrupoIdAndMateriaIdAndParcial(...)` que devuelve `Optional<Examen>`

```java
public interface ExamenRepositoryPort {
    Examen save(Examen examen);
    Optional<Examen> findById(Long id);
    List<Examen> findAll();
    void deleteById(Long id);
    Optional<Examen> findByGrupoIdAndMateriaIdAndParcial(Long grupoId, Long materiaId, Integer parcial);
}
```

### 5. Adaptador del Repositorio - ExamenRepositoryAdapter.java
**Ruta**: `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/repository/ExamenRepositoryAdapter.java`

**Cambios realizados**:
- ❌ Eliminado método `findByAlumnoId(Long alumnoId)`
- ❌ Eliminado método `findByAlumnoIdAndGrupoIdAndMateriaIdAndParcial(...)`
- ✅ Actualizado método `toEntity()` - eliminado `alumnoId` y `aciertos`
- ✅ Actualizado método `toDomain()` - eliminado `alumnoId` y `aciertos`

```java
private ExamenEntity toEntity(Examen examen) {
    return ExamenEntity.builder()
            .id(examen.getId())
            .grupoId(examen.getGrupoId())
            .materiaId(examen.getMateriaId())
            .parcial(examen.getParcial())
            .totalAciertos(examen.getTotalAciertos())
            .build();
}

private Examen toDomain(ExamenEntity entity) {
    return Examen.builder()
            .id(entity.getId())
            .grupoId(entity.getGrupoId())
            .materiaId(entity.getMateriaId())
            .parcial(entity.getParcial())
            .totalAciertos(entity.getTotalAciertos())
            .build();
}
```

### 6. Puerto de Entrada - ExamenServicePort.java
**Ruta**: `src/main/java/com/alumnos/domain/port/in/ExamenServicePort.java`

**Cambios realizados**:
- ❌ Eliminado método `obtenerExamenesPorAlumno(Long alumnoId)`
- ❌ Eliminado método `obtenerExamenPorAlumnoGrupoMateriaParcial(...)`
- ❌ Eliminado método `obtenerExamenesPorGrupoMateriaParcial(...)` que devolvía List
- ✅ Mantenido método `obtenerExamenPorGrupoMateriaParcial(...)` que devuelve `Optional<Examen>`

```java
public interface ExamenServicePort {
    Examen crearExamen(Examen examen);
    Optional<Examen> obtenerExamenPorId(Long id);
    List<Examen> obtenerTodosLosExamenes();
    Examen actualizarExamen(Examen examen);
    void eliminarExamen(Long id);
    Optional<Examen> obtenerExamenPorGrupoMateriaParcial(Long grupoId, Long materiaId, Integer parcial);
}
```

### 7. Servicio de Aplicación - ExamenService.java
**Ruta**: `src/main/java/com/alumnos/application/service/ExamenService.java`

**Cambios realizados**:
- ❌ Eliminado método `obtenerExamenesPorAlumno(Long alumnoId)`
- ❌ Eliminado método `obtenerExamenPorAlumnoGrupoMateriaParcial(...)`
- ❌ Eliminado método `obtenerExamenesPorGrupoMateriaParcial(...)` que devolvía List
- ✅ Mantenido método `obtenerExamenPorGrupoMateriaParcial(...)` que devuelve `Optional<Examen>`

```java
@Override
public Optional<Examen> obtenerExamenPorGrupoMateriaParcial(Long grupoId, Long materiaId, Integer parcial) {
    return examenRepositoryPort.findByGrupoIdAndMateriaIdAndParcial(grupoId, materiaId, parcial);
}
```

### 8. HomeController.java
**Ruta**: `src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/HomeController.java`

**Cambios realizados**:

#### A. Imports Agregados
```java
import com.alumnos.domain.model.AlumnoExamen;
import com.alumnos.domain.port.in.AlumnoExamenServicePort;
```

#### B. Servicio Inyectado
```java
private final AlumnoExamenServicePort alumnoExamenService;

public HomeController(..., AlumnoExamenServicePort alumnoExamenService) {
    // ...
    this.alumnoExamenService = alumnoExamenService;
}
```

#### C. Lógica de Guardado Actualizada
Ahora se crea/actualiza un solo `Examen` por grupo/materia/parcial, y luego se crean/actualizan múltiples `AlumnoExamen` con los aciertos individuales de cada alumno.

```java
// 1. Crear o actualizar el examen (sin aciertos individuales)
Optional<Examen> examenExistente = examenService.obtenerExamenPorGrupoMateriaParcial(
    grupo.getId(), materia.getId(), parcial
);

Examen examen;
if (examenExistente.isPresent()) {
    examen = examenExistente.get();
    examen.setTotalAciertos(totalAciertosExamen);
    examen = examenService.actualizarExamen(examen);
} else {
    examen = Examen.builder()
        .grupoId(grupo.getId())
        .materiaId(materia.getId())
        .parcial(parcial)
        .totalAciertos(totalAciertosExamen)
        .build();
    examen = examenService.crearExamen(examen);
}

// 2. Guardar los aciertos de cada alumno en AlumnoExamen
for (Alumno alumno : tblAlumnos.getItems()) {
    String aciertoStr = aciertosPorAlumno.getOrDefault(alumno.getId(), "0");
    int aciertos = Integer.parseInt(aciertoStr);

    Optional<AlumnoExamen> alumnoExamenExistente = alumnoExamenService
        .obtenerAlumnoExamenPorAlumnoYExamen(alumno.getId(), examen.getId());

    if (alumnoExamenExistente.isPresent()) {
        AlumnoExamen ae = alumnoExamenExistente.get();
        ae.setAciertos(aciertos);
        alumnoExamenService.actualizarAlumnoExamen(ae);
    } else {
        AlumnoExamen ae = AlumnoExamen.builder()
            .alumnoId(alumno.getId())
            .examenId(examen.getId())
            .aciertos(aciertos)
            .build();
        alumnoExamenService.crearAlumnoExamen(ae);
    }
}
```

#### D. Lógica de Carga Actualizada
Ahora se busca el examen por grupo/materia/parcial y luego se cargan los aciertos de cada alumno desde `AlumnoExamen`.

```java
// Buscar si existe un examen para este grupo/materia/parcial
Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
    grupoSeleccionado.getId(), materiaSeleccionada.getId(), parcialSeleccionado
);

if (examenOpt.isPresent()) {
    Examen examen = examenOpt.get();
    
    // Cargar totalAciertos
    if (examen.getTotalAciertos() != null) {
        txtTotalAciertos.setText(String.valueOf(examen.getTotalAciertos()));
    }

    // Cargar los aciertos de cada alumno desde AlumnoExamen
    List<AlumnoExamen> alumnoExamenes = alumnoExamenService
        .obtenerAlumnoExamenPorExamen(examen.getId());
    
    for (AlumnoExamen ae : alumnoExamenes) {
        aciertosPorAlumno.put(ae.getAlumnoId(), String.valueOf(ae.getAciertos()));
    }
}
```

---

## 🗄️ Estructura de la Base de Datos Actualizada

### Tabla: `examenes` (Modificada)

| Columna        | Tipo    | Restricciones           | Cambio |
|----------------|---------|-------------------------|--------|
| id             | BIGINT  | PRIMARY KEY, AUTO_INCREMENT | Sin cambio |
| ~~alumno_id~~  | ~~BIGINT~~ | ~~NOT NULL~~ | ❌ **ELIMINADO** |
| grupo_id       | BIGINT  | NOT NULL                | Sin cambio |
| materia_id     | BIGINT  | NOT NULL                | Sin cambio |
| parcial        | INTEGER | NOT NULL                | Sin cambio |
| ~~aciertos~~   | ~~INTEGER~~ | ~~NOT NULL~~ | ❌ **ELIMINADO** |
| total_aciertos | INTEGER | NULL                    | Sin cambio |

**Constraint Único Actualizado**: `(grupo_id, materia_id, parcial)`
- Ahora un examen es único por grupo/materia/parcial (sin alumnoId)

### Tabla: `alumno_examen` (Nueva)

| Columna    | Tipo    | Restricciones           |
|------------|---------|-------------------------|
| id         | BIGINT  | PRIMARY KEY, AUTO_INCREMENT |
| alumno_id  | BIGINT  | NOT NULL                |
| examen_id  | BIGINT  | NOT NULL                |
| aciertos   | INTEGER | NOT NULL                |

**Constraint Único**: `(alumno_id, examen_id)`
- Un alumno tiene un solo registro de aciertos por examen

---

## 🔗 Relaciones Actualizadas

### Examen (1) → AlumnoExamen (N)
Un examen puede tener múltiples registros de AlumnoExamen (uno por cada alumno que realizó el examen).

### Alumno (1) → AlumnoExamen (N)
Un alumno puede tener múltiples registros de AlumnoExamen (uno por cada examen que ha realizado).

---

## 🏗️ Arquitectura Hexagonal

```
┌─────────────────────────────────────┐
│   CAPA DE PRESENTACIÓN              │
│   HomeController                    │
│   - Inyecta AlumnoExamenService     │
│   - Crea/actualiza Examen único     │
│   - Crea/actualiza AlumnoExamen     │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   CAPA DE APLICACIÓN                │
│   ExamenService (modificado)        │
│   - Sin métodos de alumno           │
│   AlumnoExamenService (nuevo)       │
│   - Maneja aciertos individuales    │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   CAPA DE DOMINIO                   │
│   Examen (modificado)                │
│   - Sin alumnoId ni aciertos        │
│   AlumnoExamen (nuevo)               │
│   - Con alumnoId, examenId, aciertos│
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   CAPA DE INFRAESTRUCTURA           │
│   ExamenEntity (modificado)         │
│   - Sin alumno_id ni aciertos       │
│   AlumnoExamenEntity (nuevo)        │
│   - Tabla alumno_examen             │
└─────────────────────────────────────┘
```

---

## ✅ Beneficios de la Nueva Arquitectura

1. **Normalización**: Un examen ya no se duplica por cada alumno
2. **Eficiencia**: Se reduce la redundancia de datos (grupo, materia, parcial, totalAciertos)
3. **Claridad**: Separación clara entre el examen (metadata) y los resultados individuales
4. **Escalabilidad**: Más fácil agregar nuevas propiedades al examen sin afectar a todos los alumnos
5. **Consistencia**: El totalAciertos se guarda una sola vez por examen

---

## 🚀 Estado de Implementación

| Componente | Estado | Descripción |
|------------|--------|-------------|
| Modelo Examen actualizado | ✅ Completo | Eliminados alumnoId y aciertos |
| ExamenEntity actualizada | ✅ Completo | Constraint único actualizado |
| ExamenJpaRepository actualizado | ✅ Completo | Métodos de alumno eliminados |
| ExamenRepositoryPort actualizado | ✅ Completo | Métodos de alumno eliminados |
| ExamenRepositoryAdapter actualizado | ✅ Completo | Mapeos actualizados |
| ExamenServicePort actualizado | ✅ Completo | Métodos de alumno eliminados |
| ExamenService actualizado | ✅ Completo | Métodos de alumno eliminados |
| HomeController actualizado | ✅ Completo | Usa AlumnoExamen para aciertos |
| Inyección de AlumnoExamenService | ✅ Completo | Servicio agregado al constructor |

---

## 📌 Notas Importantes

1. **Migración de Datos**: Los datos existentes en la tabla `examenes` necesitarán ser migrados a las nuevas tablas
2. **Constraint Único**: Ahora un examen es único por grupo/materia/parcial (sin alumnoId)
3. **AlumnoExamen**: Los aciertos individuales ahora se almacenan en la tabla `alumno_examen`
4. **Compatibilidad**: Esta es una refactorización que rompe la estructura anterior
5. **Ventajas**: Mejor normalización y separación de responsabilidades

---

**Fecha de implementación**: 2026-01-29
**Versión**: 2.0
**Estado**: ✅ Completado
