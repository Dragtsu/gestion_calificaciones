# ✅ FORMULARIO CONCENTRADO - MEJORAS IMPLEMENTADAS

## 📋 Resumen de Cambios

Se han implementado las siguientes mejoras al formulario de concentrado de calificaciones:

### ✨ Mejoras Implementadas

1. **Columnas No Redimensionables**
   - Todas las columnas ahora tienen tamaño fijo
   - No se pueden redimensionar manualmente
   - Ancho consistente y predecible

2. **CheckBox para Criterios Tipo "Check"**
   - Si el tipo de evaluación es "Check", se muestra un checkbox
   - El valor guardado es 1.0 (marcado) o 0.0 (desmarcado)
   - Centrado y fácil de usar

3. **TextField con Validación para Criterios Tipo "Puntuacion"**
   - Input de texto para ingresar puntuación
   - Validación automática: máximo 2 dígitos enteros
   - Permite valores decimales (ej: 9.5, 10)
   - No permite valores mayores a 99
   - Centrado en la celda

---

## 📁 Archivo Modificado

### `HomeController.java`

**Ubicación**: 
```
src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/HomeController.java
```

---

## 🔧 Cambios Detallados

### 1. Política de Redimensionamiento de Columnas

**Línea ~3275**

**Antes**:
```java
tblCalificaciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
```

**Después**:
```java
tblCalificaciones.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
```

**Motivo**: Cambiar a `UNCONSTRAINED_RESIZE_POLICY` permite tener control total sobre el tamaño de cada columna.

---

### 2. Columnas Fijas (No Redimensionables)

**Líneas ~3365-3387**

**Cambios en Columna #**:
```java
TableColumn<java.util.Map<String, Object>, Integer> colNumero = new TableColumn<>("#");
colNumero.setPrefWidth(50);
colNumero.setMinWidth(50);      // ← Nuevo
colNumero.setMaxWidth(50);      // ← Nuevo
colNumero.setResizable(false);  // ← Nuevo
```

**Cambios en Columna Nombre Completo**:
```java
TableColumn<java.util.Map<String, Object>, String> colNombre = new TableColumn<>("Nombre Completo");
colNombre.setPrefWidth(250);
colNombre.setMinWidth(250);     // ← Nuevo
colNombre.setMaxWidth(250);     // ← Nuevo
colNombre.setResizable(false);  // ← Nuevo
```

---

### 3. Columnas Dinámicas por Tipo de Criterio

**Líneas ~3402-3540**

#### A. Para Criterios Tipo "Check"

Se crea una columna con `CheckBox`:

```java
boolean esCheck = "Check".equalsIgnoreCase(criterio.getTipoEvaluacion());

if (esCheck) {
    TableColumn<java.util.Map<String, Object>, Boolean> colAgregadoCheck = new TableColumn<>(agregado.getNombre());
    colAgregadoCheck.setPrefWidth(100);
    colAgregadoCheck.setMinWidth(100);
    colAgregadoCheck.setMaxWidth(100);
    colAgregadoCheck.setResizable(false);
    colAgregadoCheck.setEditable(true);
    
    // Cell Value Factory: Convierte el valor almacenado a Boolean
    colAgregadoCheck.setCellValueFactory(cellData -> {
        Object valor = cellData.getValue().get("agregado_" + agregado.getId());
        boolean checked = false;
        if (valor != null) {
            if (valor instanceof Boolean) {
                checked = (Boolean) valor;
            } else if (valor instanceof String) {
                String strValor = (String) valor;
                checked = "true".equalsIgnoreCase(strValor) || "1".equals(strValor);
            } else if (valor instanceof Number) {
                checked = ((Number) valor).doubleValue() > 0;
            }
        }
        return new javafx.beans.property.SimpleBooleanProperty(checked);
    });
    
    // Cell Factory: Crea el CheckBox
    colAgregadoCheck.setCellFactory(col -> new TableCell<>() {
        private final CheckBox checkBox = new CheckBox();
        
        {
            checkBox.setStyle("-fx-alignment: CENTER;");
            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    java.util.Map<String, Object> fila = getTableRow().getItem();
                    fila.put("agregado_" + agregado.getId(), newVal);
                }
            });
        }
        
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                checkBox.setSelected(item != null && item);
                setGraphic(checkBox);
                setStyle("-fx-alignment: CENTER;");
            }
        }
    });
    
    colCriterio.getColumns().add(colAgregadoCheck);
}
```

**Características**:
- ✅ CheckBox centrado
- ✅ Estado se guarda automáticamente al cambiar
- ✅ Maneja conversión de diferentes tipos de valores
- ✅ Columna no redimensionable

---

#### B. Para Criterios Tipo "Puntuacion"

Se crea una columna con `TextField` validado:

```java
else {
    TableColumn<java.util.Map<String, Object>, String> colAgregadoPuntos = new TableColumn<>(agregado.getNombre());
    colAgregadoPuntos.setPrefWidth(100);
    colAgregadoPuntos.setMinWidth(100);
    colAgregadoPuntos.setMaxWidth(100);
    colAgregadoPuntos.setResizable(false);
    colAgregadoPuntos.setEditable(true);
    
    colAgregadoPuntos.setCellValueFactory(cellData -> {
        Object valor = cellData.getValue().get("agregado_" + agregado.getId());
        return new javafx.beans.property.SimpleStringProperty(valor != null ? valor.toString() : "");
    });
    
    colAgregadoPuntos.setCellFactory(col -> new TableCell<>() {
        private final TextField textField = new TextField();
        
        {
            textField.setStyle("-fx-alignment: CENTER; -fx-pref-width: 90px;");
            textField.setMaxWidth(90);
            
            // Validar que solo sean números de máximo 2 dígitos
            textField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    // Solo permitir números y punto decimal
                    if (!newVal.matches("\\d{0,2}(\\.\\d{0,2})?")) {
                        textField.setText(oldVal);
                        return;
                    }
                    // Validar que no exceda 99
                    try {
                        double valor = Double.parseDouble(newVal);
                        if (valor > 99) {
                            textField.setText(oldVal);
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar si no es un número válido aún
                    }
                }
            });
            
            // Guardar al perder el foco
            textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal && getTableRow() != null && getTableRow().getItem() != null) {
                    java.util.Map<String, Object> fila = getTableRow().getItem();
                    String valor = textField.getText();
                    fila.put("agregado_" + agregado.getId(), valor);
                }
            });
            
            // Guardar al presionar Enter
            textField.setOnAction(event -> {
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    java.util.Map<String, Object> fila = getTableRow().getItem();
                    String valor = textField.getText();
                    fila.put("agregado_" + agregado.getId(), valor);
                }
            });
        }
        
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                textField.setText(item != null ? item : "");
                setGraphic(textField);
                setStyle("-fx-alignment: CENTER;");
            }
        }
    });
    
    colCriterio.getColumns().add(colAgregadoPuntos);
}
```

**Validaciones Implementadas**:
- ✅ Solo permite números (0-9)
- ✅ Permite punto decimal
- ✅ Máximo 2 dígitos enteros (0-99)
- ✅ Máximo 2 decimales (ej: 9.99)
- ✅ No permite valores mayores a 99
- ✅ Revierte a valor anterior si la entrada es inválida

**Características**:
- ✅ TextField centrado
- ✅ Validación en tiempo real
- ✅ Guarda al perder foco o presionar Enter
- ✅ Columna no redimensionable

---

### 4. Método Guardar Calificaciones Mejorado

**Líneas ~3577-3628**

**Cambios**:
- Maneja valores booleanos de checkboxes
- Maneja valores string de textfields
- Maneja valores numéricos directos
- Convierte checkboxes marcados a 1.0 y desmarcados a 0.0

```java
private void guardarCalificaciones(TableView<java.util.Map<String, Object>> tabla) {
    try {
        for (java.util.Map<String, Object> fila : tabla.getItems()) {
            Long alumnoId = (Long) fila.get("alumnoId");
            
            for (String clave : fila.keySet()) {
                if (clave.startsWith("agregado_")) {
                    Object valor = fila.get(clave);
                    if (valor != null) {
                        try {
                            Long agregadoId = Long.parseLong(clave.replace("agregado_", ""));
                            Double puntuacion = null;
                            
                            // Manejar diferentes tipos de valores
                            if (valor instanceof Boolean) {
                                // Para checkboxes: true = 1.0, false = 0.0
                                puntuacion = ((Boolean) valor) ? 1.0 : 0.0;
                            } else if (valor instanceof String) {
                                String valorStr = ((String) valor).trim();
                                if (!valorStr.isEmpty()) {
                                    if ("true".equalsIgnoreCase(valorStr)) {
                                        puntuacion = 1.0;
                                    } else if ("false".equalsIgnoreCase(valorStr)) {
                                        puntuacion = 0.0;
                                    } else {
                                        puntuacion = Double.parseDouble(valorStr);
                                    }
                                }
                            } else if (valor instanceof Number) {
                                puntuacion = ((Number) valor).doubleValue();
                            }
                            
                            if (puntuacion != null) {
                                Calificacion calificacion = Calificacion.builder()
                                        .alumnoId(alumnoId)
                                        .agregadoId(agregadoId)
                                        .puntuacion(puntuacion)
                                        .build();
                                
                                calificacionService.crearCalificacion(calificacion);
                            }
                        } catch (NumberFormatException e) {
                            LOG.warn("Valor inválido para calificación: " + valor);
                        }
                    }
                }
            }
        }
    } catch (Exception e) {
        LOG.error("Error al guardar calificaciones", e);
        mostrarAlerta("Error", "Error al guardar las calificaciones: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}
```

---

## 🎯 Funcionamiento

### Flujo de Uso

1. **Seleccionar Filtros**:
   - Grupo
   - Materia
   - Parcial

2. **Generar Tabla**:
   - Se crean columnas según los criterios
   - Cada criterio tiene columnas hijas (agregados)
   - Las columnas se adaptan al tipo de evaluación

3. **Ingresar Calificaciones**:
   - **Para Check**: Marcar/desmarcar checkbox
   - **Para Puntuacion**: Escribir número (máx 2 dígitos)

4. **Guardar**:
   - Presionar botón "Guardar Calificaciones"
   - Los valores se persisten en la base de datos

---

## 📊 Ejemplo Visual

### Tabla Generada

| # | Nombre Completo | **Examen 1 (10 pts)** ||| **Tareas (20 pts)** |||
|---|----------------|----------|----------|---------|---------|---------|---------|
|   |                | Asistencia (☑) | Participación (☑) | Examen (📝) | Tarea 1 (📝) | Tarea 2 (📝) | Proyecto (📝) |
| 1 | García López Ana| ☑ | ☑ | 9.5 | 18 | 19 | 20 |
| 2 | Pérez Juan | ☑ | ☐ | 8.0 | 15 | 17 | 18 |

**Leyenda**:
- ☑/☐ = CheckBox (para tipo Check)
- 📝 = TextField con validación (para tipo Puntuacion)

---

## ✅ Validaciones

### CheckBox (Tipo Check)
- ✅ Solo dos estados: marcado (1.0) o desmarcado (0.0)
- ✅ Cambio instantáneo
- ✅ No requiere validación

### TextField (Tipo Puntuacion)
- ✅ Solo acepta números: 0-9
- ✅ Permite punto decimal: 9.5
- ✅ Máximo 2 dígitos enteros: 0-99
- ✅ Máximo 2 decimales: 0.00-99.99
- ✅ Rechaza valores > 99
- ✅ Rechaza letras y caracteres especiales

---

## 🎨 Características Visuales

### Columnas
- ✅ Ancho fijo: 100px para agregados
- ✅ No redimensionables
- ✅ Columna # : 50px
- ✅ Columna Nombre: 250px

### Celdas
- ✅ Contenido centrado
- ✅ CheckBox alineado al centro
- ✅ TextField alineado al centro
- ✅ Estilo limpio y profesional

---

## 🔄 Conversión de Valores

### Al Cargar Calificaciones Existentes

**Para CheckBox**:
```
Valor BD → Valor Checkbox
1.0      → Marcado
0.0      → Desmarcado
null     → Desmarcado
"true"   → Marcado
"false"  → Desmarcado
```

**Para TextField**:
```
Valor BD → Valor TextField
9.5      → "9.5"
10       → "10"
null     → ""
0        → "0"
```

### Al Guardar Calificaciones

**Desde CheckBox**:
```
Checkbox → Valor BD
Marcado  → 1.0
Desmarcado → 0.0
```

**Desde TextField**:
```
TextField → Valor BD
"9.5"    → 9.5
"10"     → 10.0
""       → No se guarda
"abc"    → No se guarda (warning en log)
```

---

## 📝 Notas Importantes

1. **Regex de Validación**: `\\d{0,2}(\\.\\d{0,2})?`
   - Permite: 0, 1, 9, 10, 99, 9.5, 10.0, 99.99
   - Rechaza: 100, 9.555, abc, -5

2. **Persistencia**:
   - Los checkboxes se guardan como 1.0 o 0.0
   - Los textfields se guardan como Double
   - Valores vacíos no se guardan

3. **Experiencia de Usuario**:
   - Validación en tiempo real (no espera a guardar)
   - Feedback inmediato al escribir valores inválidos
   - Columnas consistentes y predecibles

---

## ✅ Estado de Compilación

- ❌ **Antes**: 0 errores
- ✅ **Después**: 0 errores
- ⚠️ Warnings existentes: No afectan funcionalidad

---

## 🎉 Implementación Completada

Todas las mejoras solicitadas han sido implementadas:
- ✅ CheckBox para criterios tipo Check
- ✅ TextField con validación (máx 2 dígitos) para tipo Puntuacion
- ✅ Columnas no redimensionables
- ✅ Guardado correcto de ambos tipos de valores

---

**Fecha de Implementación**: 2026-01-27
**Archivo Modificado**: HomeController.java
**Líneas Modificadas**: ~280 líneas
**Funcionalidad**: Completamente operativa
