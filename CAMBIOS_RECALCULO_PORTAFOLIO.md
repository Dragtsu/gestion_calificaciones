# Actualización: Recálculo Automático de "Puntos Parcial" al Modificar Portafolio

## 📋 Descripción
Se ha actualizado el formulario de "Concentrado de Calificaciones" para que la columna **"Puntos Parcial"** se recalcule automáticamente cuando se modifican los valores de las columnas de **Portafolio** (criterios/agregados) o **Puntos Examen**.

## ✨ Funcionalidad Implementada

Ahora, cuando el usuario modifica cualquiera de estos valores:
- ✅ **Criterios tipo "Check"** (CheckBox)
- ✅ **Criterios tipo "Puntuación"** (TextField con valores numéricos)
- ✅ **Puntos Examen** (ya estaba implementado)

El sistema recalcula automáticamente:
1. **Total Portafolio**: Suma de todos los puntos de criterios/agregados
2. **Puntos Examen**: Los aciertos del examen (directamente, sin conversión)
3. **Puntos Parcial**: `Total Portafolio + Puntos Examen (aciertos)`
4. **Calificación Parcial**: `(Puntos Parcial * 10) / 100`

## 🔧 Cambios Realizados

### Archivo Modificado
`src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/ConcentradoController.java`

### Modificaciones Específicas

#### 1. Obtención del Total de Puntos del Examen (Líneas ~296-302)
```java
// Obtener total de puntos del examen si existe (para el recálculo)
Optional<Examen> examenOptTemp = examenService.obtenerExamenPorGrupoMateriaParcial(
    grupo.getId(), materia.getId(), parcial);
final Integer totalPuntosExamenFinal = examenOptTemp.map(Examen::getTotalPuntosExamen).orElse(null);
```

**Propósito**: Almacenar el total de puntos del examen en una variable `final` para poder usarla dentro de los listeners de las celdas editables.

#### 2. Listener de CheckBox (Líneas ~358-366)
**Antes:**
```java
checkBox.setOnAction(event -> {
    if (!isUpdating && getTableRow() != null && getTableRow().getItem() != null) {
        Map<String, Object> fila = getTableRow().getItem();
        fila.put("agregado_" + agregado.getId(), checkBox.isSelected());
        tabla.refresh();
    }
});
```

**Después:**
```java
checkBox.setOnAction(event -> {
    if (!isUpdating && getTableRow() != null && getTableRow().getItem() != null) {
        Map<String, Object> fila = getTableRow().getItem();
        fila.put("agregado_" + agregado.getId(), checkBox.isSelected());
        
        // ⚡ Recalcular puntosParcial y calificacionParcial
        recalcularPuntosParcial(fila, totalPuntosExamenFinal, criteriosInfo);
        
        tabla.refresh();
    }
});
```

#### 3. Listener de TextField (Líneas ~426-437)
**Antes:**
```java
textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
    if (!newVal && getTableRow() != null && getTableRow().getItem() != null) {
        String valorTexto = textField.getText();
        Map<String, Object> fila = getTableRow().getItem();
        fila.put("agregado_" + agregado.getId(), valorTexto);
        tabla.refresh();
    }
});
```

**Después:**
```java
textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
    if (!newVal && getTableRow() != null && getTableRow().getItem() != null) {
        String valorTexto = textField.getText();
        Map<String, Object> fila = getTableRow().getItem();
        fila.put("agregado_" + agregado.getId(), valorTexto);
        
        // ⚡ Recalcular puntosParcial y calificacionParcial
        recalcularPuntosParcial(fila, totalPuntosExamenFinal, criteriosInfo);
        
        tabla.refresh();
    }
});
```

#### 4. Corrección del Cálculo en Carga Inicial (Líneas ~877-895)
**Antes:**
```java
double puntosExamen = 0.0;
Object calificacionExamenObj = fila.get("calificacionExamen");
if (calificacionExamenObj != null && calificacionExamenObj instanceof Double) {
    puntosExamen = (Double) calificacionExamenObj;
}

double puntosParcial = totalPortafolio + puntosExamen;
```

**Después:**
```java
double puntosExamen = 0.0;
Object aciertosExamenObj = fila.get("aciertosExamen");
if (aciertosExamenObj != null) {
    try {
        if (aciertosExamenObj instanceof Number) {
            puntosExamen = ((Number) aciertosExamenObj).doubleValue();
        } else if (aciertosExamenObj instanceof String && !((String) aciertosExamenObj).isEmpty()) {
            puntosExamen = Double.parseDouble((String) aciertosExamenObj);
        }
    } catch (NumberFormatException e) {
        // Ignorar, dejar en 0.0
    }
}

double puntosParcial = totalPortafolio + puntosExamen;
```

#### 5. Corrección del Método recalcularPuntosParcial (Líneas ~1420-1438)
**Antes:**
```java
// Calcular puntos del examen (calificación sobre 10)
double puntosExamen = 0.0;
Object aciertosExamenObj = fila.get("aciertosExamen");

if (aciertosExamenObj != null && totalPuntosExamen != null && totalPuntosExamen > 0) {
    try {
        int aciertosExamen = 0;
        if (aciertosExamenObj instanceof Number) {
            aciertosExamen = ((Number) aciertosExamenObj).intValue();
        } else if (aciertosExamenObj instanceof String && !((String) aciertosExamenObj).isEmpty()) {
            aciertosExamen = Integer.parseInt((String) aciertosExamenObj);
        }
        
        // Calcular porcentaje y convertir a calificación sobre 10
        double porcentaje = (aciertosExamen * 100.0) / totalPuntosExamen;
        puntosExamen = (porcentaje * 10.0) / 100.0;
    } catch (NumberFormatException e) {
        // Si hay error, dejar puntosExamen en 0.0
    }
}
```

**Después:**
```java
// Obtener puntos del examen directamente (aciertos, no la calificación)
double puntosExamen = 0.0;
Object aciertosExamenObj = fila.get("aciertosExamen");

if (aciertosExamenObj != null) {
    try {
        if (aciertosExamenObj instanceof Number) {
            puntosExamen = ((Number) aciertosExamenObj).doubleValue();
        } else if (aciertosExamenObj instanceof String && !((String) aciertosExamenObj).isEmpty()) {
            puntosExamen = Double.parseDouble((String) aciertosExamenObj);
        }
    } catch (NumberFormatException e) {
        // Si hay error, dejar puntosExamen en 0.0
    }
}
```

## 📊 Método Utilizado

El método `recalcularPuntosParcial()` realiza los siguientes cálculos:

### 1. Calcular Total de Portafolio
```java
double totalPortafolio = 0.0;
for (Map<String, Object> criterioInfo : criteriosInfo) {
    // Para cada criterio tipo "Check"
    if (esCheck && valor instanceof Boolean && (Boolean) valor) {
        puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
    }
    // Para cada criterio tipo "Puntuación"
    else if (!esCheck && valor instanceof String && !((String) valor).isEmpty()) {
        puntosObtenidosCriterio += Double.parseDouble((String) valor);
    }
    totalPortafolio += puntosObtenidosCriterio;
}
```

### 2. Obtener Puntos del Examen (Aciertos Directos)
```java
double puntosExamen = 0.0;
Object aciertosExamenObj = fila.get("aciertosExamen");

if (aciertosExamenObj != null) {
    // Obtener los aciertos directamente sin conversión
    puntosExamen = valor numérico de aciertos
}
```

### 3. Calcular Puntos Parcial y Calificación Parcial
```java
// Puntos Parcial = Portafolio + Puntos Examen (aciertos directos)
double puntosParcial = totalPortafolio + puntosExamen;
double calificacionParcial = (puntosParcial * 10.0) / 100.0;

// Actualizar la fila con los nuevos valores
fila.put("puntosParcial", puntosParcial);
fila.put("calificacionParcial", calificacionParcial);
```

## 📐 Fórmula Correcta

### ✅ Fórmula Correcta Implementada:
```
Puntos Parcial = Total Portafolio + Puntos Examen (aciertos)
Calificación Parcial = (Puntos Parcial * 10) / 100
```

### ❌ Fórmula Incorrecta Anterior:
```
Puntos Parcial = Total Portafolio + Calificación Examen
```

**Diferencia clave**: Ahora se suman los **puntos del examen directamente** (aciertos), no la calificación del examen convertida a escala de 10.

## 🧪 Casos de Uso

### Ejemplo Numérico:
- **Portafolio**: 70 puntos
- **Puntos Examen**: 85 aciertos (de 100 totales)

**Cálculo:**
- Puntos Parcial = 70 + 85 = **155 puntos**
- Calificación Parcial = (155 * 10) / 100 = **15.5** ⚠️ (puede exceder 10)

### Caso 1: Usuario marca un CheckBox
1. Usuario marca/desmarca un criterio tipo "Check"
2. El sistema:
   - Actualiza el valor del agregado
   - Recalcula el total de portafolio
   - Recalcula puntos parcial = portafolio + aciertos examen
   - Actualiza la columna "Puntos Parcial" en la tabla

### Caso 2: Usuario ingresa puntos en un campo de puntuación
1. Usuario ingresa un valor numérico en un criterio tipo "Puntuación"
2. Usuario sale del campo (pierde el foco)
3. El sistema:
   - Actualiza el valor del agregado
   - Recalcula el total de portafolio
   - Recalcula puntos parcial = portafolio + aciertos examen
   - Actualiza la columna "Puntos Parcial" en la tabla

### Caso 3: Usuario modifica puntos del examen
1. Usuario ingresa/modifica los puntos del examen
2. El sistema:
   - Usa los aciertos directamente
   - Recalcula puntos parcial = portafolio + aciertos examen
   - Actualiza las columnas correspondientes

## ✅ Resultado

La columna **"Puntos Parcial"** ahora se actualiza automáticamente y en tiempo real cuando el usuario modifica:
- ✅ Cualquier CheckBox de criterios
- ✅ Cualquier TextField de puntuación
- ✅ Los puntos del examen

Y la fórmula correcta utiliza los **aciertos del examen directamente**, no la calificación convertida.

## 📝 Notas Técnicas

- El método `recalcularPuntosParcial()` se corrigió para usar `aciertosExamen` en lugar de calcular la calificación
- Se agregó la variable `totalPuntosExamenFinal` para poder acceder al total de puntos desde los listeners
- Se corrigió también el cálculo inicial en `generarTablaCalificaciones()` para usar `aciertosExamen`
- Los cambios son compatibles con el código existente
- No se requieren cambios en la base de datos ni en otros archivos

## 🔧 Cambios Realizados

### Archivo Modificado
`src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/ConcentradoController.java`

### Modificaciones Específicas

#### 1. Obtención del Total de Puntos del Examen (Líneas ~296-302)
```java
// Obtener total de puntos del examen si existe (para el recálculo)
Optional<Examen> examenOptTemp = examenService.obtenerExamenPorGrupoMateriaParcial(
    grupo.getId(), materia.getId(), parcial);
final Integer totalPuntosExamenFinal = examenOptTemp.map(Examen::getTotalPuntosExamen).orElse(null);
```

**Propósito**: Almacenar el total de puntos del examen en una variable `final` para poder usarla dentro de los listeners de las celdas editables.

#### 2. Listener de CheckBox (Líneas ~358-366)
**Antes:**
```java
checkBox.setOnAction(event -> {
    if (!isUpdating && getTableRow() != null && getTableRow().getItem() != null) {
        Map<String, Object> fila = getTableRow().getItem();
        fila.put("agregado_" + agregado.getId(), checkBox.isSelected());
        tabla.refresh();
    }
});
```

**Después:**
```java
checkBox.setOnAction(event -> {
    if (!isUpdating && getTableRow() != null && getTableRow().getItem() != null) {
        Map<String, Object> fila = getTableRow().getItem();
        fila.put("agregado_" + agregado.getId(), checkBox.isSelected());
        
        // ⚡ Recalcular puntosParcial y calificacionParcial
        recalcularPuntosParcial(fila, totalPuntosExamenFinal, criteriosInfo);
        
        tabla.refresh();
    }
});
```

#### 3. Listener de TextField (Líneas ~426-437)
**Antes:**
```java
textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
    if (!newVal && getTableRow() != null && getTableRow().getItem() != null) {
        String valorTexto = textField.getText();
        Map<String, Object> fila = getTableRow().getItem();
        fila.put("agregado_" + agregado.getId(), valorTexto);
        tabla.refresh();
    }
});
```

**Después:**
```java
textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
    if (!newVal && getTableRow() != null && getTableRow().getItem() != null) {
        String valorTexto = textField.getText();
        Map<String, Object> fila = getTableRow().getItem();
        fila.put("agregado_" + agregado.getId(), valorTexto);
        
        // ⚡ Recalcular puntosParcial y calificacionParcial
        recalcularPuntosParcial(fila, totalPuntosExamenFinal, criteriosInfo);
        
        tabla.refresh();
    }
});
```

## 📊 Método Utilizado

El método `recalcularPuntosParcial()` (ya existente en el código) realiza los siguientes cálculos:

### 1. Calcular Total de Portafolio
```java
double totalPortafolio = 0.0;
for (Map<String, Object> criterioInfo : criteriosInfo) {
    // Para cada criterio tipo "Check"
    if (esCheck && valor instanceof Boolean && (Boolean) valor) {
        puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
    }
    // Para cada criterio tipo "Puntuación"
    else if (!esCheck && valor instanceof String && !((String) valor).isEmpty()) {
        puntosObtenidosCriterio += Double.parseDouble((String) valor);
    }
    totalPortafolio += puntosObtenidosCriterio;
}
```

### 2. Calcular Puntos del Examen
```java
int aciertosExamen = // valor de la celda "aciertosExamen"
double porcentaje = (aciertosExamen * 100.0) / totalPuntosExamen;
double puntosExamen = (porcentaje * 10.0) / 100.0;
```

### 3. Calcular Puntos Parcial y Calificación Parcial
```java
double puntosParcial = totalPortafolio + puntosExamen;
double calificacionParcial = (puntosParcial * 10.0) / 100.0;

// Actualizar la fila con los nuevos valores
fila.put("puntosParcial", puntosParcial);
fila.put("calificacionParcial", calificacionParcial);
```

## 🧪 Casos de Uso

### Caso 1: Usuario marca un CheckBox
1. Usuario marca/desmarca un criterio tipo "Check"
2. El sistema:
   - Actualiza el valor del agregado
   - Recalcula el total de portafolio
   - Recalcula puntos parcial = portafolio + examen
   - Actualiza la columna "Puntos Parcial" en la tabla

### Caso 2: Usuario ingresa puntos en un campo de puntuación
1. Usuario ingresa un valor numérico en un criterio tipo "Puntuación"
2. Usuario sale del campo (pierde el foco)
3. El sistema:
   - Actualiza el valor del agregado
   - Recalcula el total de portafolio
   - Recalcula puntos parcial = portafolio + examen
   - Actualiza la columna "Puntos Parcial" en la tabla

### Caso 3: Usuario modifica puntos del examen
1. Usuario ingresa/modifica los puntos del examen
2. El sistema:
   - Calcula el porcentaje
   - Convierte a calificación sobre 10
   - Recalcula puntos parcial = portafolio + examen
   - Actualiza las columnas correspondientes

## ✅ Resultado

La columna **"Puntos Parcial"** ahora se actualiza automáticamente y en tiempo real cuando el usuario modifica:
- ✅ Cualquier CheckBox de criterios
- ✅ Cualquier TextField de puntuación
- ✅ Los puntos del examen

Esto mejora significativamente la experiencia del usuario, ya que no necesita guardar o recargar los datos para ver los valores actualizados.

## 📝 Notas Técnicas

- El método `recalcularPuntosParcial()` ya existía y se reutilizó
- Se agregó la variable `totalPuntosExamenFinal` para poder acceder al total de puntos desde los listeners
- Los cambios son compatibles con el código existente
- No se requieren cambios en la base de datos ni en otros archivos
