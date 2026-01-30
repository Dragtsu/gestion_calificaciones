# ✅ COLUMNAS DE EXAMEN AGREGADAS AL CONCENTRADO DE CALIFICACIONES

## 📋 Cambios Realizados

Se han agregado **tres columnas adicionales** al formulario de "Concentrado de Calificaciones" que muestran información del examen de cada alumno:
1. **Aciertos** - Número de aciertos obtenidos por el alumno
2. **% Examen** - Porcentaje de aciertos (0-100%)
3. **Calif. Examen** - Calificación sobre 10 (0-10)

---

## 🎯 Origen de los Datos

### Entidad AlumnoExamen
Los datos provienen de la entidad `AlumnoExamen` que contiene:
```java
public class AlumnoExamen {
    private Long id;
    private Long alumnoId;
    private Long examenId;
    private Integer aciertos;      // Aciertos obtenidos (0-99)
    private Double porcentaje;     // Porcentaje obtenido (0-100)
    private Double calificacion;   // Calificación sobre 10 (0-10)
}
```

### Entidad Examen
Los datos se vinculan con el examen que corresponde a:
```java
public class Examen {
    private Long id;
    private Long grupoId;
    private Long materiaId;
    private Integer parcial;  // 1, 2 o 3
    private Integer totalAciertos;
}
```

### Relación de Datos
```
AlumnoExamen ──> Alumno (por alumnoId)
             └─> Examen (por examenId)
                     └─> Grupo, Materia, Parcial
```

---

## 🔧 Implementación Técnica

### 1. Búsqueda del Examen

**Ubicación:** Método `generarTablaCalificaciones()`

Antes de crear las columnas, se busca el examen correspondiente:
```java
Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaYParcial(
    grupo.getId(), materia.getId(), parcial);
```

**Condición:** Solo se agregan las columnas **si existe un examen** para ese grupo, materia y parcial.

---

### 2. Creación de Columnas

#### Columna "Aciertos"
```java
TableColumn<java.util.Map<String, Object>, String> colAciertos = new TableColumn<>("Aciertos");
colAciertos.setPrefWidth(100);
colAciertos.setMinWidth(100);
colAciertos.setMaxWidth(100);
colAciertos.setResizable(false);
colAciertos.setStyle("-fx-alignment: CENTER;");
```

**CellValueFactory:**
```java
colAciertos.setCellValueFactory(cellData -> {
    Object valor = cellData.getValue().get("aciertosExamen");
    return new SimpleStringProperty(
        valor != null ? String.valueOf(valor) : "-"
    );
});
```

**Estilo:**
- Color de fondo: `#fff3e0` (naranja claro)
- Fuente en negrita
- Alineación centrada

#### Columna "% Examen"
```java
TableColumn<java.util.Map<String, Object>, String> colPorcentajeExamen = new TableColumn<>("% Examen");
colPorcentajeExamen.setPrefWidth(100);
```

**CellValueFactory:**
```java
colPorcentajeExamen.setCellValueFactory(cellData -> {
    Object valor = cellData.getValue().get("porcentajeExamen");
    return new SimpleStringProperty(
        valor != null ? String.format("%.1f%%", (Double) valor) : "-"
    );
});
```

**Formato:** Un decimal con símbolo de porcentaje (ej: "85.5%")

#### Columna "Calif. Examen"
```java
TableColumn<java.util.Map<String, Object>, String> colCalificacionExamen = new TableColumn<>("Calif. Examen");
colCalificacionExamen.setPrefWidth(120);
```

**CellValueFactory:**
```java
colCalificacionExamen.setCellValueFactory(cellData -> {
    Object valor = cellData.getValue().get("calificacionExamen");
    return new SimpleStringProperty(
        valor != null ? String.format("%.2f", (Double) valor) : "-"
    );
});
```

**Formato:** Dos decimales (ej: "8.75")

**Estilo especial:**
- Tamaño de fuente: 14px (más grande)
- Color de fondo: `#fff3e0`
- Fuente en negrita

---

### 3. Carga de Datos por Alumno

**Ubicación:** Dentro del loop que crea filas de alumnos

```java
// Obtener el examen si existe
Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaYParcial(
    grupo.getId(), materia.getId(), parcial);

for (Alumno alumno : alumnos) {
    // ...crear fila...
    
    // Cargar datos de examen si existe
    if (examenOpt.isPresent()) {
        Examen examen = examenOpt.get();
        Optional<AlumnoExamen> alumnoExamenOpt = alumnoExamenService
            .obtenerAlumnoExamenPorAlumnoYExamen(alumno.getId(), examen.getId());
        
        if (alumnoExamenOpt.isPresent()) {
            AlumnoExamen alumnoExamen = alumnoExamenOpt.get();
            fila.put("aciertosExamen", alumnoExamen.getAciertos());
            fila.put("porcentajeExamen", alumnoExamen.getPorcentaje());
            fila.put("calificacionExamen", alumnoExamen.getCalificacion());
        } else {
            // Sin datos de examen para este alumno
            fila.put("aciertosExamen", null);
            fila.put("porcentajeExamen", null);
            fila.put("calificacionExamen", null);
        }
    }
}
```

---

## 📊 Estructura Visual de la Tabla

### Antes:
```
| # | Nombre | [Criterios...] | Portafolio |
```

### Después:
```
| # | Nombre | [Criterios...] | Portafolio | Aciertos | % Examen | Calif. Examen |
```

### Ejemplo Visual:
```
┌────┬──────────────────┬────────────┬────────────┬──────────┬──────────┬──────────────┐
│ #  │ Nombre Completo  │ Portafolio │ Aciertos   │ % Examen │ Calif. E.│
├────┼──────────────────┼────────────┼────────────┼──────────┼──────────┤
│ 1  │ García Ana       │   85.50    │     42     │  87.5%   │   8.75   │
│ 2  │ López Juan       │   78.00    │     38     │  79.2%   │   7.92   │
│ 3  │ Pérez María      │   92.30    │     45     │  93.8%   │   9.38   │
└────┴──────────────────┴────────────┴────────────┴──────────┴──────────┘
```

---

## 🎨 Características de las Columnas

| Columna | Ancho | Formato | Color Fondo | Alineación |
|---------|-------|---------|-------------|------------|
| **Aciertos** | 100px | Entero | #fff3e0 (naranja) | Centro |
| **% Examen** | 100px | "%.1f%%" | #fff3e0 (naranja) | Centro |
| **Calif. Examen** | 120px | "%.2f" | #fff3e0 (naranja) | Centro |

**Características comunes:**
- ✅ No redimensionables
- ✅ Fuente en negrita
- ✅ Color de fondo naranja claro (#fff3e0)
- ✅ Muestran "-" cuando no hay datos

---

## 🔄 Flujo de Obtención de Datos

### Paso 1: Usuario selecciona filtros
```
Usuario selecciona:
- Grupo: 101
- Materia: Matemáticas  
- Parcial: 1
```

### Paso 2: Búsqueda del examen
```java
Optional<Examen> examen = examenService.obtenerExamenPorGrupoMateriaYParcial(
    grupoId: 101,
    materiaId: 5,
    parcial: 1
);
```

### Paso 3: Por cada alumno, obtener AlumnoExamen
```java
for (Alumno alumno : alumnos) {
    Optional<AlumnoExamen> ae = alumnoExamenService
        .obtenerAlumnoExamenPorAlumnoYExamen(
            alumnoId: alumno.getId(),
            examenId: examen.getId()
        );
    
    if (ae.isPresent()) {
        // Mostrar datos
    } else {
        // Mostrar "-"
    }
}
```

---

## ✅ Casos de Uso

### Caso 1: Examen Existe y Alumno lo Realizó
**Condición:**
- Existe `Examen` para grupo, materia, parcial
- Existe `AlumnoExamen` para alumno y examen

**Resultado:**
```
| Aciertos | % Examen | Calif. Examen |
|    42    |  87.5%   |     8.75      |
```

### Caso 2: Examen Existe pero Alumno NO lo Realizó
**Condición:**
- Existe `Examen` para grupo, materia, parcial
- NO existe `AlumnoExamen` para alumno y examen

**Resultado:**
```
| Aciertos | % Examen | Calif. Examen |
|    -     |    -     |       -       |
```

### Caso 3: NO Existe Examen
**Condición:**
- NO existe `Examen` para grupo, materia, parcial

**Resultado:**
```
Las columnas NO se muestran en la tabla
```

---

## 🎯 Ventajas de la Implementación

### 1. Condicional
✅ Las columnas **solo aparecen si existe un examen**
- No ocupa espacio innecesario
- Interfaz más limpia cuando no hay examen

### 2. Datos Vinculados
✅ Datos obtenidos directamente de la base de datos
- No se calculan en tiempo real
- Datos ya almacenados previamente

### 3. Visual Coherente
✅ Color naranja distintivo (#fff3e0)
- Diferenciación clara de las columnas de criterios
- Agrupación visual de datos de examen

### 4. Formato Apropiado
✅ Cada columna con su formato específico
- Aciertos: entero
- Porcentaje: 1 decimal + símbolo %
- Calificación: 2 decimales

### 5. Manejo de Nulos
✅ Muestra "-" cuando no hay datos
- No muestra "null" o valores vacíos
- Interfaz más profesional

---

## 🔍 Servicios Utilizados

### ExamenServicePort
```java
Optional<Examen> obtenerExamenPorGrupoMateriaYParcial(
    Long grupoId, 
    Long materiaId, 
    Integer parcial
);
```
**Uso:** Buscar el examen correspondiente a los filtros seleccionados

### AlumnoExamenServicePort
```java
Optional<AlumnoExamen> obtenerAlumnoExamenPorAlumnoYExamen(
    Long alumnoId, 
    Long examenId
);
```
**Uso:** Obtener los datos del examen de un alumno específico

---

## 📝 Resumen de Archivos Modificados

### HomeController.java
**Método modificado:** `generarTablaCalificaciones()`

**Cambios realizados:**

1. ✅ **Búsqueda de examen** (línea ~3837)
   - Obtiene examen por grupo, materia y parcial
   - Solo si existe

2. ✅ **Creación de 3 columnas** (línea ~3837-3937)
   - Columna "Aciertos"
   - Columna "% Examen"
   - Columna "Calif. Examen"
   - Con estilos y formatos específicos

3. ✅ **Carga de datos por alumno** (línea ~3939-3999)
   - Obtiene AlumnoExamen para cada alumno
   - Carga aciertos, porcentaje y calificación
   - Maneja casos sin datos

---

## 🗄️ Datos en la Base de Datos

### Tabla: examenes
```sql
CREATE TABLE examenes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    grupo_id INTEGER NOT NULL,
    materia_id INTEGER NOT NULL,
    parcial INTEGER NOT NULL,
    total_aciertos INTEGER,
    UNIQUE(grupo_id, materia_id, parcial)
);
```

### Tabla: alumno_examen
```sql
CREATE TABLE alumno_examen (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    alumno_id INTEGER NOT NULL,
    examen_id INTEGER NOT NULL,
    aciertos INTEGER NOT NULL,
    porcentaje REAL,
    calificacion REAL,
    UNIQUE(alumno_id, examen_id)
);
```

---

## ✅ Validaciones Implementadas

1. ✅ **Examen existe**
   - Se verifica antes de crear columnas
   - Si no existe, columnas no se muestran

2. ✅ **AlumnoExamen existe**
   - Se verifica por cada alumno
   - Si no existe, se muestra "-"

3. ✅ **Valores nulos**
   - Se manejan apropiadamente
   - Se muestra "-" en lugar de null

4. ✅ **Formato correcto**
   - Aciertos como entero
   - Porcentaje con 1 decimal
   - Calificación con 2 decimales

---

## 🎨 Comparación Visual

### Columnas de Criterios vs Columnas de Examen

| Aspecto | Criterios | Examen |
|---------|-----------|--------|
| **Color de fondo** | #e3f2fd (azul) | #fff3e0 (naranja) |
| **Posición** | Centro de la tabla | Final de la tabla |
| **Editables** | ✅ Sí | ❌ No |
| **Fuente** | Normal/Negrita | Negrita |
| **Agrupación** | Por criterio | Todas juntas |
| **Condicional** | Siempre visible | Solo si hay examen |

---

## 🚀 Próximos Pasos Opcionales

### Mejoras Sugeridas:
1. **Tooltip informativo**: Mostrar "Total de aciertos: 48" al pasar sobre Aciertos
2. **Columna Total Aciertos**: Agregar columna que muestre el total de aciertos del examen
3. **Color según calificación**: Colorear según el rango (rojo <6, amarillo 6-8, verde >8)
4. **Ordenamiento**: Permitir ordenar por cualquiera de las columnas de examen
5. **Filtro**: Agregar opción para ocultar/mostrar columnas de examen

---

## 📊 Estado Final

| Componente | Estado |
|-----------|--------|
| Columna "Aciertos" | ✅ AGREGADA |
| Columna "% Examen" | ✅ AGREGADA |
| Columna "Calif. Examen" | ✅ AGREGADA |
| Búsqueda de Examen | ✅ IMPLEMENTADA |
| Carga de AlumnoExamen | ✅ IMPLEMENTADA |
| Formato de datos | ✅ CORRECTO |
| Manejo de nulos | ✅ IMPLEMENTADO |
| Estilos visuales | ✅ APLICADOS |
| Posicionamiento | ✅ DESPUÉS DE PORTAFOLIO |
| Condicional (solo si hay examen) | ✅ IMPLEMENTADO |

---

## 🎯 Beneficios

1. **Información completa**: Ahora se ve el portafolio Y el examen en una sola pantalla
2. **Comparación fácil**: Se puede comparar el desempeño de portafolio vs examen
3. **Datos confiables**: Información obtenida directamente de la BD
4. **Visual distintivo**: Color naranja diferencia claramente del portafolio
5. **Profesional**: Formato adecuado y manejo de casos especiales

---

**Fecha de Implementación:** 2026-01-29  
**Módulo:** Concentrado de Calificaciones  
**Tipo de Cambio:** Agregado de Columnas  
**Estado:** ✅ COMPLETADO Y FUNCIONAL
