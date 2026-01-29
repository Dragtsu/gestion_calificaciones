# ✅ PORCENTAJE Y CALIFICACIÓN ALMACENADOS EN BASE DE DATOS

## 📋 Resumen

Se ha modificado el sistema para que las columnas "Porcentaje examen" y "Calificación examen" se almacenen en la base de datos dentro de la entidad `AlumnoExamen`. Ahora estos valores se guardan y persisten junto con los aciertos de cada alumno.

---

## 🎯 Cambios Implementados

### 1. Modelo de Dominio - AlumnoExamen.java

**Campos agregados:**
```java
private Double porcentaje;  // Porcentaje obtenido (0-100)
private Double calificacion;  // Calificación sobre 10 (0-10)
```

**Estructura completa:**
```java
public class AlumnoExamen {
    private Long id;
    private Long alumnoId;
    private Long examenId;
    private Integer aciertos;      // Aciertos obtenidos (0-99)
    private Double porcentaje;     // Porcentaje obtenido (0-100) [NUEVO]
    private Double calificacion;   // Calificación sobre 10 (0-10) [NUEVO]
    
    // Campos opcionales
    private String nombreAlumno;
    private Integer numeroLista;
}
```

### 2. Entidad JPA - AlumnoExamenEntity.java

**Columnas agregadas en la tabla `alumno_examen`:**
```java
@Column
private Double porcentaje;

@Column
private Double calificacion;
```

### 3. Repositorio - AlumnoExamenRepositoryAdapter.java

**Métodos de mapeo actualizados:**

#### toEntity (Dominio → BD)
```java
private AlumnoExamenEntity toEntity(AlumnoExamen alumnoExamen) {
    return AlumnoExamenEntity.builder()
            .id(alumnoExamen.getId())
            .alumnoId(alumnoExamen.getAlumnoId())
            .examenId(alumnoExamen.getExamenId())
            .aciertos(alumnoExamen.getAciertos())
            .porcentaje(alumnoExamen.getPorcentaje())        // NUEVO
            .calificacion(alumnoExamen.getCalificacion())    // NUEVO
            .build();
}
```

#### toDomain (BD → Dominio)
```java
private AlumnoExamen toDomain(AlumnoExamenEntity entity) {
    return AlumnoExamen.builder()
            .id(entity.getId())
            .alumnoId(entity.getAlumnoId())
            .examenId(entity.getExamenId())
            .aciertos(entity.getAciertos())
            .porcentaje(entity.getPorcentaje())              // NUEVO
            .calificacion(entity.getCalificacion())          // NUEVO
            .build();
}
```

### 4. HomeController - Lógica de Guardado Actualizada

**Cálculo y almacenamiento:**
```java
for (Alumno alumno : tblAlumnos.getItems()) {
    String aciertoStr = aciertosPorAlumno.getOrDefault(alumno.getId(), "0");
    int aciertos = Integer.parseInt(aciertoStr);

    // Calcular porcentaje y calificación
    double porcentaje = (aciertos * 100.0) / totalAciertosExamen;
    double calificacion = (porcentaje * 10.0) / 100.0;

    // Buscar si ya existe un AlumnoExamen
    Optional<AlumnoExamen> alumnoExamenExistente = 
        alumnoExamenService.obtenerAlumnoExamenPorAlumnoYExamen(
            alumno.getId(), examen.getId()
        );

    if (alumnoExamenExistente.isPresent()) {
        // Actualizar
        AlumnoExamen alumnoExamen = alumnoExamenExistente.get();
        alumnoExamen.setAciertos(aciertos);
        alumnoExamen.setPorcentaje(porcentaje);          // NUEVO
        alumnoExamen.setCalificacion(calificacion);      // NUEVO
        alumnoExamenService.actualizarAlumnoExamen(alumnoExamen);
    } else {
        // Crear nuevo
        AlumnoExamen alumnoExamen = AlumnoExamen.builder()
            .alumnoId(alumno.getId())
            .examenId(examen.getId())
            .aciertos(aciertos)
            .porcentaje(porcentaje)                      // NUEVO
            .calificacion(calificacion)                  // NUEVO
            .build();
        alumnoExamenService.crearAlumnoExamen(alumnoExamen);
    }
}
```

---

## 🗄️ Estructura de la Base de Datos

### Tabla: `alumno_examen` (Actualizada)

| Columna | Tipo | Restricciones | Descripción |
|---------|------|---------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Identificador único |
| alumno_id | BIGINT | NOT NULL | ID del alumno |
| examen_id | BIGINT | NOT NULL | ID del examen |
| aciertos | INTEGER | NOT NULL | Aciertos obtenidos (0-99) |
| **porcentaje** | **DOUBLE** | **NULL** | **Porcentaje calculado (0-100)** |
| **calificacion** | **DOUBLE** | **NULL** | **Calificación sobre 10 (0-10)** |

**Constraint Único**: `(alumno_id, examen_id)`

---

## 📊 Ejemplo de Datos Almacenados

### Escenario: Examen con 50 aciertos totales

| id | alumno_id | examen_id | aciertos | **porcentaje** | **calificacion** |
|----|-----------|-----------|----------|--------------|----------------|
| 1 | 101 | 5 | 45 | **90.00** | **9.0** |
| 2 | 102 | 5 | 42 | **84.00** | **8.4** |
| 3 | 103 | 5 | 50 | **100.00** | **10.0** |
| 4 | 104 | 5 | 38 | **76.00** | **7.6** |

---

## ✅ Ventajas de Almacenar en BD

### Antes (Solo en Vista)
- ❌ Los valores se calculaban solo al momento de mostrar
- ❌ No se podían consultar directamente desde la BD
- ❌ No se podían usar en reportes sin recalcular
- ❌ Pérdida de rendimiento al recalcular constantemente

### Ahora (Almacenado en BD)
- ✅ **Persistencia**: Los valores se guardan permanentemente
- ✅ **Consultas directas**: Se pueden consultar sin recalcular
- ✅ **Reportes**: Fácil generación de reportes y estadísticas
- ✅ **Rendimiento**: No necesita recalcular en cada vista
- ✅ **Histórico**: Se mantiene el registro exacto de calificaciones
- ✅ **Integridad**: Los valores quedan fijos aunque cambien las reglas

---

## 🔄 Flujo de Guardado

```
Usuario ingresa aciertos
         ↓
Presiona "Guardar Exámenes"
         ↓
Sistema valida datos
         ↓
Crea/Actualiza Examen (con totalAciertos)
         ↓
Por cada alumno:
    ├─ Obtiene aciertos ingresados
    ├─ Calcula porcentaje
    ├─ Calcula calificación
    ├─ Busca AlumnoExamen existente
    └─ Si existe:
          └─ Actualiza: aciertos, porcentaje, calificacion
       Si no existe:
          └─ Crea nuevo con: aciertos, porcentaje, calificacion
         ↓
Guarda en base de datos
         ↓
✓ Datos persistidos
```

---

## 🧪 Casos de Prueba

### Caso 1: Crear Nuevo Registro
**Datos:**
- Alumno ID: 101
- Examen ID: 5
- Total aciertos: 50
- Aciertos alumno: 45

**Guardado en BD:**
```sql
INSERT INTO alumno_examen (alumno_id, examen_id, aciertos, porcentaje, calificacion)
VALUES (101, 5, 45, 90.00, 9.0);
```

### Caso 2: Actualizar Registro Existente
**Datos previos en BD:**
- aciertos: 40
- porcentaje: 80.00
- calificacion: 8.0

**Nuevos datos:**
- aciertos: 45
- porcentaje: 90.00
- calificacion: 9.0

**Actualización en BD:**
```sql
UPDATE alumno_examen 
SET aciertos = 45, porcentaje = 90.00, calificacion = 9.0
WHERE alumno_id = 101 AND examen_id = 5;
```

### Caso 3: Calificación Perfecta
**Datos:**
- Total aciertos: 60
- Aciertos alumno: 60

**Guardado:**
- aciertos: 60
- porcentaje: 100.00
- calificacion: 10.0

### Caso 4: Calificación con Decimales
**Datos:**
- Total aciertos: 60
- Aciertos alumno: 55

**Guardado:**
- aciertos: 55
- porcentaje: 91.67
- calificacion: 9.17 (Java almacena el valor exacto)

---

## 📝 Precisión de Decimales

### Porcentaje
- **Tipo**: `Double`
- **Precisión**: Java mantiene la precisión completa
- **Ejemplo**: 91.66666666666667
- **Display**: Se formatea a 2 decimales en la vista (91.67)

### Calificación
- **Tipo**: `Double`
- **Precisión**: Java mantiene la precisión completa
- **Ejemplo**: 9.166666666666666
- **Display**: Se formatea a 1 decimal en la vista (9.2)

---

## 🔍 Consultas SQL Útiles

### Ver calificaciones de un examen
```sql
SELECT 
    ae.alumno_id,
    ae.aciertos,
    ae.porcentaje,
    ae.calificacion
FROM alumno_examen ae
WHERE ae.examen_id = 5
ORDER BY ae.calificacion DESC;
```

### Promedio de calificaciones por examen
```sql
SELECT 
    examen_id,
    AVG(calificacion) as promedio,
    MAX(calificacion) as maxima,
    MIN(calificacion) as minima,
    COUNT(*) as total_alumnos
FROM alumno_examen
GROUP BY examen_id;
```

### Alumnos con calificación mayor a 8.0
```sql
SELECT 
    alumno_id,
    examen_id,
    calificacion
FROM alumno_examen
WHERE calificacion >= 8.0
ORDER BY calificacion DESC;
```

### Distribución de calificaciones
```sql
SELECT 
    CASE 
        WHEN calificacion >= 9.0 THEN 'Excelente (9-10)'
        WHEN calificacion >= 8.0 THEN 'Muy Bueno (8-8.9)'
        WHEN calificacion >= 7.0 THEN 'Bueno (7-7.9)'
        WHEN calificacion >= 6.0 THEN 'Suficiente (6-6.9)'
        ELSE 'Insuficiente (<6)'
    END as rango,
    COUNT(*) as cantidad
FROM alumno_examen
WHERE examen_id = 5
GROUP BY rango
ORDER BY MIN(calificacion) DESC;
```

---

## 🎯 Beneficios para Reportes

### Antes
```java
// Tenía que recalcular cada vez
for (AlumnoExamen ae : alumnos) {
    double porcentaje = (ae.getAciertos() * 100.0) / totalAciertos;
    double calificacion = porcentaje / 10.0;
    // usar valores calculados...
}
```

### Ahora
```java
// Obtiene directamente los valores guardados
for (AlumnoExamen ae : alumnos) {
    double porcentaje = ae.getPorcentaje();      // Ya guardado
    double calificacion = ae.getCalificacion();  // Ya guardado
    // usar valores directos...
}
```

---

## 🚀 Estado de Implementación

| Componente | Estado | Descripción |
|------------|--------|-------------|
| Campos en AlumnoExamen | ✅ Completo | porcentaje y calificacion agregados |
| Columnas en BD | ✅ Completo | tabla alumno_examen actualizada |
| Mapeo en repositorio | ✅ Completo | toEntity y toDomain actualizados |
| Cálculo en guardado | ✅ Completo | Se calcula al guardar exámenes |
| Persistencia | ✅ Completo | Valores se guardan en BD |
| Actualización | ✅ Completo | Valores se actualizan correctamente |

---

## 📌 Notas Importantes

1. **Valores Calculados al Guardar**: Los porcentajes y calificaciones se calculan automáticamente cuando se guardan los exámenes

2. **Precisión en BD**: Se almacenan como `Double` con precisión completa

3. **Formato en Vista**: Se formatean al mostrar (2 decimales para porcentaje, 1 para calificación)

4. **Compatibilidad**: Los valores antiguos sin porcentaje/calificación aparecerán como `NULL` en la BD

5. **Recalculo**: Si se actualiza el total de aciertos del examen, se pueden recalcular guardando de nuevo

6. **Integridad Referencial**: Los valores quedan ligados al registro específico de AlumnoExamen

---

## 🔄 Migración de Datos Existentes

Si ya existen registros en `alumno_examen` sin porcentaje/calificación, se pueden recalcular con:

```sql
-- Script para recalcular valores existentes
UPDATE alumno_examen ae
JOIN examenes e ON ae.examen_id = e.id
SET 
    ae.porcentaje = (ae.aciertos * 100.0) / e.total_aciertos,
    ae.calificacion = ((ae.aciertos * 100.0) / e.total_aciertos) / 10.0
WHERE ae.porcentaje IS NULL OR ae.calificacion IS NULL;
```

---

**Fecha de implementación**: 2026-01-29  
**Versión**: 2.0  
**Estado**: ✅ Completado  
**Impacto**: Alto - Cambio en estructura de BD
