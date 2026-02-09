# ✅ Validación: Límite de 100 Puntos en Criterios de Evaluación

## 📋 Descripción
Se implementó una validación en el formulario de "Criterios de Evaluación" para garantizar que la suma de:
- **Puntuación máxima de todos los criterios** de una materia y parcial
- **Total de puntos del examen** de esa materia y parcial

NO supere los **100 puntos**.

## 🎯 Objetivo
Evitar que se asignen más de 100 puntos totales para evaluar a los alumnos en una materia y parcial específicos, manteniendo un sistema de calificación balanceado y consistente.

## 🔧 Implementación

### Archivo Modificado
`src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/CriteriosController.java`

### Cambios Realizados

#### 1. Agregación de Imports y Servicio de Exámenes
```java
import com.alumnos.domain.model.Examen;
import com.alumnos.domain.port.in.ExamenServicePort;
import java.util.Optional;
```

```java
private final ExamenServicePort examenService;

public CriteriosController(CriterioServicePort criterioService, 
                          MateriaServicePort materiaService,
                          ExamenServicePort examenService) {
    this.criterioService = criterioService;
    this.materiaService = materiaService;
    this.examenService = examenService;
}
```

#### 2. Método de Validación: `validarLimitePuntos()`

Método que verifica que la suma no exceda 100 puntos:

```java
private boolean validarLimitePuntos(Long materiaId, Integer parcial, Double puntuacionNueva) {
    // 1. Obtener todos los criterios existentes (excluyendo el que se está editando)
    List<Criterio> criteriosExistentes = criterioService.obtenerCriteriosPorMateria(materiaId)
        .stream()
        .filter(c -> c.getParcial().equals(parcial))
        .filter(c -> criterioIdEnEdicion == null || !c.getId().equals(criterioIdEnEdicion))
        .toList();

    // 2. Sumar la puntuación de criterios existentes
    double totalCriterios = criteriosExistentes.stream()
        .mapToDouble(c -> c.getPuntuacionMaxima() != null ? c.getPuntuacionMaxima() : 0.0)
        .sum();

    // 3. Obtener el examen y su total de puntos
    Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
        null, materiaId, parcial);
    
    double totalExamen = examenOpt.isPresent() && examenOpt.get().getTotalPuntosExamen() != null 
        ? examenOpt.get().getTotalPuntosExamen() 
        : 0.0;

    // 4. Calcular suma total
    double sumaTotal = totalCriterios + puntuacionNueva + totalExamen;

    // 5. Validar límite de 100
    if (sumaTotal > 100) {
        // Mostrar mensaje detallado
        return false;
    }

    return true;
}
```

#### 3. Integración en `guardarCriterio()`

Se agregó la llamada a la validación después de validar el formulario:

```java
private void guardarCriterio() {
    try {
        if (!validarFormulario()) return;

        // Procesar puntuación máxima
        Double puntuacion = null;
        if (!txtPuntuacionMaxima.getText().trim().isEmpty()) {
            try {
                puntuacion = Double.parseDouble(txtPuntuacionMaxima.getText().trim());
            } catch (NumberFormatException e) {
                mostrarError("La puntuación máxima debe ser un número válido");
                return;
            }
        }

        // ⚠️ VALIDAR QUE LA SUMA NO EXCEDA 100 PUNTOS
        Long materiaId = cmbMateria.getValue().getId();
        Integer parcial = cmbParcial.getValue();
        
        if (!validarLimitePuntos(materiaId, parcial, puntuacion)) {
            return; // No continuar si la validación falla
        }

        // ... resto del código de guardado
    }
}
```

## 📊 Mensaje de Validación

Cuando se excede el límite, se muestra un mensaje detallado con:

```
⚠️ SE SOBREPASA EL MÁXIMO DE PUNTOS PERMITIDOS

Desglose:
• Suma de criterios existentes: XX.X puntos
• Puntuación de este criterio: XX.X puntos
• Total puntos del examen: XX.X puntos
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
• TOTAL: XXX.X puntos

⚠️ El máximo permitido es 100 puntos.
Sobrepasa por: XX.X puntos

Por favor, ajuste la puntuación máxima del criterio.
```

**Nota:** Los valores se muestran con 1 decimal (ejemplo: 50.5, 30.0, 25.5)

## 🎯 Casos de Uso

### Caso 1: Crear Nuevo Criterio
1. Usuario llena el formulario de criterio
2. Usuario ingresa puntuación máxima de 30
3. Sistema valida:
   - Criterios existentes: 50 puntos
   - Examen: 25 puntos
   - Nuevo criterio: 30 puntos
   - **TOTAL: 105 puntos** ❌
4. Sistema muestra mensaje de advertencia
5. **El criterio NO se guarda**

### Caso 2: Editar Criterio Existente
1. Usuario edita un criterio con puntuación de 20
2. Usuario cambia la puntuación a 40
3. Sistema valida:
   - Criterios existentes (sin el editado): 30 puntos
   - Examen: 25 puntos
   - Criterio editado: 40 puntos
   - **TOTAL: 95 puntos** ✅
4. Sistema permite guardar el criterio

### Caso 3: Criterio sin Puntuación (Check)
1. Usuario crea criterio tipo "Check" sin puntuación
2. Sistema no valida (puntuación = 0 o null)
3. Sistema permite guardar sin restricción

## 📐 Fórmula de Validación

```
Total = Suma(Criterios Existentes) + Puntuación Nueva + Total Puntos Examen

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
- ✅ **Excluye el criterio en edición**: Si se está editando, no cuenta el valor anterior

## ✨ Ventajas

1. **Prevención de Errores**: Evita configuraciones inválidas desde el origen
2. **Transparencia**: Mensaje detallado muestra exactamente qué está mal
3. **Flexibilidad**: Permite ajustar la puntuación antes de guardar
4. **Integridad**: Garantiza que el sistema de calificación sea consistente

## 🧪 Ejemplos Numéricos

### Ejemplo 1: Excede el Límite
```
Criterios existentes:
- Tareas: 30 puntos
- Participación: 20 puntos
- Proyecto: 35 puntos
Total Criterios: 85 puntos

Examen: 20 puntos

Nuevo Criterio: 10 puntos

TOTAL: 85 + 20 + 10 = 115 puntos ❌
Sobrepasa por: 15.0 puntos
```

### Ejemplo 2: Dentro del Límite
```
Criterios existentes:
- Tareas: 25 puntos
- Participación: 15 puntos
- Proyecto: 30 puntos
Total Criterios: 70 puntos

Examen: 20 puntos

Nuevo Criterio: 10 puntos

TOTAL: 70 + 20 + 10 = 100 puntos ✅
```

### Ejemplo 3: Sin Examen
```
Criterios existentes:
- Tareas: 30 puntos
- Participación: 20 puntos
Total Criterios: 50 puntos

Examen: 0 puntos (no configurado)

Nuevo Criterio: 30 puntos

TOTAL: 50 + 0 + 30 = 80 puntos ✅
```

## 📝 Notas Técnicas

- La validación se ejecuta **antes de guardar** en la base de datos
- Si la validación falla, el formulario permanece con los datos ingresados
- El usuario puede ajustar la puntuación y volver a intentar
- La validación aplica tanto para crear como para editar criterios
- Los criterios tipo "Check" sin puntuación no afectan el cálculo

## ✅ Resultado Final

- ✅ No se permite guardar criterios que excedan los 100 puntos totales
- ✅ Mensaje claro y detallado sobre el error
- ✅ Desglose completo de la suma
- ✅ Considera materia y parcial específicos
- ✅ Funciona tanto para crear como editar criterios
