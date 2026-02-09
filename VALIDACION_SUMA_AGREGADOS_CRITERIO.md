# Validación: Suma de Agregados no debe superar el valor del Criterio

## Fecha: 2026-02-08

## Descripción del Cambio

Se implementó una **validación en el formulario de "Concentrado de Calificaciones"** que impide que la suma de las puntuaciones de los agregados de tipo "Puntuación" supere el valor máximo del criterio al cual pertenecen.

## Problema Resuelto

Anteriormente, el sistema permitía ingresar calificaciones para agregados sin validar que la suma total de todos los agregados de un criterio no excediera la puntuación máxima definida para ese criterio. Esto causaba inconsistencias en las calificaciones.

**Ejemplo del problema:**
- Criterio: "Tareas" con puntuación máxima de **10 puntos**
- Agregados del criterio:
  - Tarea 1: 5 puntos ✅
  - Tarea 2: 4 puntos ✅
  - Tarea 3: 3 puntos ❌ (Suma = 12 > 10) → **Ahora se rechaza**

## Implementación

### Archivo Modificado
- **ConcentradoController.java** (`src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/ConcentradoController.java`)

### Métodos Agregados/Modificados

#### 1. `validarFormulario()` - Modificado
Se agregó una llamada al nuevo método de validación:

```java
// Validar que la suma de agregados no supere el valor del criterio
return validarSumaAgregadosNoPasaCriterio(cmbAlumno, cmbAgregado, cmbCriterio, 
                                          cmbGrupo, cmbMateria, cmbParcial, txtPuntuacion);
```

#### 2. `validarSumaAgregadosNoPasaCriterio()` - Nuevo
Método que implementa la lógica de validación completa.

## Lógica de Validación

### 1. **Verificación del tipo de criterio**
   - Solo se valida para criterios de tipo **"Puntuacion"**
   - Los criterios de tipo **"Check"** no se validan (son booleanos)

### 2. **Obtención de datos**
   - Puntuación máxima del criterio
   - Puntuación que se intenta ingresar
   - Todos los agregados que pertenecen al criterio
   - Calificaciones existentes del alumno para ese criterio

### 3. **Cálculo de la suma**
   ```
   Suma Total = Σ(Puntuaciones de otros agregados) + Puntuación nueva
   ```
   - Se excluye la puntuación del agregado actual si ya existe (para permitir ediciones)

### 4. **Validación**
   ```
   Si Suma Total > Puntuación Máxima del Criterio
      → Rechazar y mostrar error detallado
   ```

## Mensaje de Error

Cuando se supera el límite, se muestra un mensaje informativo con:

```
La suma de los agregados (12.00) supera la puntuación máxima del criterio (10.00).
Suma actual de otros agregados: 9.00
Puntuación que intenta ingresar: 3.00
Puntuación máxima disponible: 1.00
```

Esto ayuda al usuario a entender:
- ✅ Cuál es el límite del criterio
- ✅ Cuántos puntos ya están asignados
- ✅ Cuántos puntos está intentando agregar
- ✅ Cuántos puntos están disponibles

## Casos de Uso Cubiertos

### ✅ Caso 1: Agregar nueva calificación
- Valida que al agregar una nueva puntuación, la suma total no supere el criterio

### ✅ Caso 2: Editar calificación existente
- Permite editar (excluye la puntuación actual del agregado)
- Valida la nueva suma con el valor modificado

### ✅ Caso 3: Múltiples alumnos
- La validación se aplica por alumno (cada alumno tiene su propia suma)

### ✅ Caso 4: Diferentes contextos
- La validación considera: Grupo, Materia y Parcial
- Las sumas son independientes por cada combinación

### ✅ Caso 5: Criterios tipo "Check"
- No se valida (son checkboxes, no suman)

## Flujo de Validación

```
Usuario ingresa puntuación
         ↓
¿Criterio es tipo "Puntuacion"? → NO → ✅ Permitir
         ↓ SÍ
Obtener puntuaciones existentes del alumno
         ↓
Calcular suma total
         ↓
¿Suma > Puntuación Máxima? → NO → ✅ Permitir
         ↓ SÍ
❌ Rechazar y mostrar error detallado
```

## Beneficios

1. **Integridad de datos**: Previene inconsistencias en las calificaciones
2. **Validación temprana**: Error antes de guardar en la BD
3. **Mensajes claros**: El usuario sabe exactamente qué está mal y cuánto puede ingresar
4. **Flexible**: Permite editar calificaciones existentes
5. **Contexto específico**: Valida por alumno, grupo, materia y parcial

## Comportamiento

### Antes del Cambio ❌
```
Usuario: Ingresa Tarea 3 = 3 puntos
Sistema: ✅ Guardado (Total = 12 puntos > 10 puntos) ← ERROR
```

### Después del Cambio ✅
```
Usuario: Ingresa Tarea 3 = 3 puntos
Sistema: ❌ Error: "La suma supera la puntuación máxima..."
Usuario: Corrige a Tarea 3 = 1 punto
Sistema: ✅ Guardado (Total = 10 puntos = 10 puntos) ← CORRECTO
```

## Consideraciones Técnicas

- **Performance**: La validación consulta la BD solo cuando es necesario (tipo "Puntuacion")
- **Transacciones**: No hay cambios en la lógica transaccional
- **Excepciones**: Captura y maneja errores apropiadamente
- **Null safety**: Valida valores nulos antes de operar

## Estado
✅ **Completado** - La validación está implementada y funcionando correctamente sin errores de compilación.

## Pruebas Sugeridas

1. ✅ Crear agregados para un criterio tipo "Puntuacion"
2. ✅ Intentar ingresar puntuaciones que sumen más del límite
3. ✅ Verificar que el mensaje de error sea claro
4. ✅ Editar una puntuación existente
5. ✅ Verificar que criterios tipo "Check" no se validen
6. ✅ Probar con diferentes alumnos, grupos, materias y parciales
