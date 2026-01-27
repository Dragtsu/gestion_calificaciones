# Actualización: Orden de Criterios por Materia y Cuatrimestre

## Cambios Realizados

Se ha actualizado la lógica del número consecutivo de orden en los criterios de evaluación para que tome en cuenta tanto la **materia** como el **cuatrimestre**.

### Archivos Modificados

#### 1. CriterioService.java
**Ubicación**: `src/main/java/com/alumnos/application/service/CriterioService.java`

**Cambios**:

- **Método `calcularOrden()`**: 
  - Ahora filtra criterios por materia Y cuatrimestre
  - El orden se calcula dentro de cada combinación materia-cuatrimestre
  
- **Método `recalcularOrdenes()`**:
  - Agrupa criterios por cuatrimestre dentro de cada materia
  - Recalcula el orden secuencialmente para cada grupo materia-cuatrimestre
  - Mantiene el orden existente cuando es posible

- **Validaciones**:
  - Agregada validación obligatoria del campo cuatrimestre en `crearCriterio()`
  - Agregada validación obligatoria del campo cuatrimestre en `actualizarCriterio()`

- **Imports agregados**:
  - `java.util.Map`
  - `java.util.stream.Collectors`

#### 2. HomeController.java
**Ubicación**: `src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/HomeController.java`

**Cambios**:

- **Botón "Guardar Orden"**:
  - Ahora valida que todos los criterios visibles sean de la misma materia Y cuatrimestre
  - Mensaje de error actualizado para indicar la restricción de cuatrimestre
  - Mensaje de éxito incluye el número de cuatrimestre

### Comportamiento Actualizado

#### Antes:
- El orden era secuencial por materia únicamente (1, 2, 3, 4, ...)
- Todos los criterios de una materia tenían orden consecutivo sin importar el cuatrimestre

#### Ahora:
- El orden es secuencial por materia Y cuatrimestre
- Cada combinación materia-cuatrimestre tiene su propia secuencia de orden (1, 2, 3, ...)

#### Ejemplo:

**Materia: Matemáticas**
- Cuatrimestre 1:
  - Criterio A - Orden: 1
  - Criterio B - Orden: 2
  - Criterio C - Orden: 3
  
- Cuatrimestre 2:
  - Criterio D - Orden: 1
  - Criterio E - Orden: 2
  
- Cuatrimestre 3:
  - Criterio F - Orden: 1

### Funcionalidades Actualizadas

1. **Crear Criterio**:
   - El orden se calcula automáticamente basándose en materia y cuatrimestre
   - Es el último orden + 1 dentro de ese grupo

2. **Actualizar Criterio**:
   - Si cambia de materia o cuatrimestre, se recalcula el orden
   - Los órdenes de ambos grupos (origen y destino) se reorganizan

3. **Eliminar Criterio**:
   - Los órdenes del grupo materia-cuatrimestre se reorganizan automáticamente

4. **Guardar Orden Manual**:
   - Solo se puede guardar el orden si todos los criterios visibles son de la misma materia Y cuatrimestre
   - Validación en la interfaz para prevenir errores

### Validaciones Implementadas

✅ El campo cuatrimestre es obligatorio al crear  
✅ El campo cuatrimestre es obligatorio al actualizar  
✅ El orden se calcula considerando materia + cuatrimestre  
✅ No se puede guardar orden manual si hay criterios de diferentes cuatrimestres visibles  
✅ Los órdenes se recalculan automáticamente al crear/actualizar/eliminar  

### Impacto en la Base de Datos

- La columna `orden` en la tabla `criterios` ahora representa el orden dentro de cada grupo materia-cuatrimestre
- Los registros existentes se reorganizarán automáticamente al iniciar la aplicación
- No se requiere migración manual de datos

### Recomendaciones de Uso

1. **Al filtrar criterios**: Se recomienda filtrar por materia para ver todos los cuatrimestres
2. **Al ordenar manualmente**: Asegurarse de que solo se muestren criterios del mismo cuatrimestre
3. **Al crear criterios**: El sistema asignará automáticamente el siguiente orden disponible

### Estado

✅ **COMPLETADO** - Lógica actualizada correctamente  
✅ **VALIDADO** - Sin errores de compilación  
✅ **LISTO** - Para probar en la aplicación
