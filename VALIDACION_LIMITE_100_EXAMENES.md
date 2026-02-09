# ✅ Validación: Límite de 100 Puntos en Formulario de Exámenes

## 📋 Descripción
Se implementó la misma validación en el formulario de "Exámenes" para garantizar que la suma de:
- **Puntuación máxima de todos los criterios** de una materia y parcial
- **Total de puntos del examen** que se está guardando

NO supere los **100 puntos**.

## 🎯 Objetivo
Evitar que se asignen más de 100 puntos totales para evaluar a los alumnos en una materia y parcial específicos, validando desde el formulario de exámenes.

## 🔧 Implementación

### Archivo Modificado
`src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/ExamenesController.java`

### Cambios Realizados

#### 1. Agregación de Imports y Servicio de Criterios
```java
import com.alumnos.domain.model.Criterio;
import com.alumnos.domain.port.in.CriterioServicePort;
```

```java
private final CriterioServicePort criterioService;

public ExamenesController(ExamenServicePort examenService,
                         GrupoServicePort grupoService,
                         MateriaServicePort materiaService,
                         CriterioServicePort criterioService) {
    this.examenService = examenService;
    this.grupoService = grupoService;
    this.materiaService = materiaService;
    this.criterioService = criterioService;
}
```

#### 2. Método de Validación: `validarLimitePuntos()`

Método que verifica que la suma no exceda 100 puntos:

```java
private boolean validarLimitePuntos(Long materiaId, Integer parcial, Integer totalPuntosExamen) {
    // 1. Obtener todos los criterios de la materia y parcial
    List<Criterio> criteriosExistentes = criterioService.obtenerCriteriosPorMateria(materiaId)
        .stream()
        .filter(c -> c.getParcial().equals(parcial))
        .toList();

    // 2. Sumar la puntuación de criterios
    double totalCriterios = criteriosExistentes.stream()
        .mapToDouble(c -> c.getPuntuacionMaxima() != null ? c.getPuntuacionMaxima() : 0.0)
        .sum();

    // 3. Calcular suma total
    double sumaTotal = totalCriterios + totalPuntosExamen;

    // 4. Validar límite de 100
    if (sumaTotal > 100) {
        // Mostrar mensaje detallado
        return false;
    }

    return true;
}
```

#### 3. Integración en `guardarExamen()`

Se agregó la llamada a la validación después de validar duplicados:

```java
private void guardarExamen(ComboBox<Grupo> cmbGrupo, ComboBox<Materia> cmbMateria,
                          ComboBox<Integer> cmbParcial, TextField txtTotalPuntos,
                          DatePicker dpFechaAplicacion) {
    try {
        if (!validarFormulario(cmbGrupo, cmbMateria, cmbParcial, txtTotalPuntos)) return;

        // Verificar que no exista un examen duplicado
        Long grupoId = cmbGrupo.getValue().getId();
        Long materiaId = cmbMateria.getValue().getId();
        Integer parcial = cmbParcial.getValue();

        Optional<Examen> examenExistente = examenService.obtenerExamenPorGrupoMateriaParcial(
            grupoId, materiaId, parcial);

        if (examenExistente.isPresent()) {
            mostrarError("Ya existe un examen registrado para este grupo, materia y parcial");
            return;
        }

        // ⚠️ VALIDAR QUE LA SUMA NO EXCEDA 100 PUNTOS
        Integer totalPuntosExamen = Integer.parseInt(txtTotalPuntos.getText());
        if (!validarLimitePuntos(materiaId, parcial, totalPuntosExamen)) {
            return; // No continuar si la validación falla
        }

        // ... resto del código de guardado
    }
}
```

## 📊 Mensaje de Validación

Cuando se excede el límite, se muestra un mensaje detallado:

```
⚠️ SE SOBREPASA EL MÁXIMO DE PUNTOS PERMITIDOS

Desglose:
• Suma de criterios existentes: XX.X puntos
• Total puntos del examen: XX.X puntos
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
• TOTAL: XXX.X puntos

⚠️ El máximo permitido es 100 puntos.
Sobrepasa por: XX.X puntos

Por favor, ajuste el total de puntos del examen.
```

**Nota:** Los valores se muestran con 1 decimal (ejemplo: 50.0, 30.5, 25.0)

## 🎯 Casos de Uso

### Caso 1: Crear Examen que Excede el Límite
1. Usuario llena el formulario de examen
2. Usuario ingresa total de puntos de 40
3. Sistema valida:
   - Criterios existentes: 70 puntos
   - Examen: 40 puntos
   - **TOTAL: 110 puntos** ❌
4. Sistema muestra mensaje de advertencia
5. **El examen NO se guarda**

### Caso 2: Crear Examen Dentro del Límite
1. Usuario llena el formulario de examen
2. Usuario ingresa total de puntos de 25
3. Sistema valida:
   - Criterios existentes: 70 puntos
   - Examen: 25 puntos
   - **TOTAL: 95 puntos** ✅
4. Sistema permite guardar el examen

### Caso 3: Sin Criterios Registrados
1. Usuario crea examen con 100 puntos
2. Sistema valida:
   - Criterios existentes: 0 puntos
   - Examen: 100 puntos
   - **TOTAL: 100 puntos** ✅
3. Sistema permite guardar

## 📐 Fórmula de Validación

```
Total = Suma(Criterios Existentes) + Total Puntos Examen

Si Total > 100:
    ❌ No permitir guardar
    Mostrar mensaje con desglose
Sino:
    ✅ Permitir guardar
```

## 🔍 Filtros Aplicados

La validación considera:
- ✅ **Materia**: Solo criterios de la misma materia
- ✅ **Parcial**: Solo criterios del mismo parcial (1, 2 o 3)

## ✨ Ventajas

1. **Validación Dual**: Funciona tanto en formulario de Criterios como de Exámenes
2. **Consistencia**: Mismo mensaje y formato en ambos formularios
3. **Prevención**: Evita configuraciones inválidas desde ambos puntos
4. **Transparencia**: Mensaje detallado muestra exactamente qué está mal

## 🧪 Ejemplos Numéricos

### Ejemplo 1: Excede el Límite (Desde Exámenes)
```
Criterios de "Matemáticas - Parcial 1":
- Tareas: 30 puntos
- Participación: 20 puntos
- Proyecto: 35 puntos
Total Criterios: 85 puntos

Usuario intenta crear examen con:
• Total puntos: 20 puntos

CÁLCULO:
85 + 20 = 105 puntos ❌

RESULTADO: No permite guardar, muestra mensaje
```

### Ejemplo 2: Dentro del Límite (Desde Exámenes)
```
Criterios de "Matemáticas - Parcial 1":
- Tareas: 30 puntos
- Participación: 20 puntos
Total Criterios: 50 puntos

Usuario intenta crear examen con:
• Total puntos: 40 puntos

CÁLCULO:
50 + 40 = 90 puntos ✅

RESULTADO: Permite guardar
```

### Ejemplo 3: Límite Exacto
```
Criterios de "Historia - Parcial 2":
- Ensayos: 50 puntos
- Participación: 30 puntos
Total Criterios: 80 puntos

Usuario intenta crear examen con:
• Total puntos: 20 puntos

CÁLCULO:
80 + 20 = 100 puntos ✅

RESULTADO: Permite guardar (justo en el límite)
```

## 🔄 Diferencias con Validación en Criterios

| Aspecto | Criterios | Exámenes |
|---------|-----------|----------|
| **Campo validado** | Puntuación Máxima | Total Puntos Examen |
| **Excluye en edición** | Criterio editado | No aplica (no hay edición) |
| **Mensaje** | "ajuste la puntuación máxima del criterio" | "ajuste el total de puntos del examen" |
| **Ubicación** | CriteriosController | ExamenesController |

## 📝 Notas Técnicas

- La validación se ejecuta **antes de guardar** en la base de datos
- Si la validación falla, el formulario permanece con los datos ingresados
- El usuario puede ajustar el total de puntos y volver a intentar
- No se permite editar exámenes, solo crear y eliminar
- Los criterios sin puntuación (null o 0) no afectan el cálculo

## ✅ Resultado Final

- ✅ No se permite guardar exámenes que excedan los 100 puntos totales
- ✅ Mensaje claro y detallado sobre el error
- ✅ Desglose completo de la suma
- ✅ Considera materia y parcial específicos
- ✅ Implementación independiente del formulario de Criterios
- ✅ Formato consistente: 1 decimal, sin emojis en desglose
