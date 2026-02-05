# ✅ Corrección: Columna "Total Portafolio" Vacía en Excel

## 🐛 Problema Identificado

La columna "Total Portafolio" se exportaba **vacía** en el archivo Excel generado, aunque en la tabla de la interfaz se mostraba correctamente.

## 🔍 Causa del Problema

El valor de "Total Portafolio" se calculaba dinámicamente en el `cellValueFactory` de JavaFX para mostrarlo en la tabla, pero **nunca se guardaba** en el Map de datos (`fila`). Al exportar a Excel, el código buscaba `item.get("totalPortafolio")` pero ese valor no existía en el Map.

### Código Problemático:
```java
// El valor se calculaba pero NO se guardaba
colPortafolio.setCellValueFactory(cellData -> {
    Map<String, Object> fila = cellData.getValue();
    double totalPortafolio = 0.0;
    
    // ... cálculos ...
    
    return new SimpleStringProperty(String.format("%.2f", totalPortafolio));
    // ❌ totalPortafolio nunca se guarda en el Map
});
```

## ✅ Solución Implementada

Se modificó la lógica de carga de datos para **calcular y guardar** los valores de:
1. **Acumulado por criterio** (para cada criterio)
2. **Total Portafolio** (suma de todos los acumulados)

### Cambios Realizados:

#### 1. Guardar Nombre del Criterio
Se agregó el nombre del criterio a la información guardada:

```java
Map<String, Object> criterioInfo = new HashMap<>();
criterioInfo.put("esCheck", esCheck);
criterioInfo.put("puntuacionMaxima", criterio.getPuntuacionMaxima());
criterioInfo.put("agregadoIds", agregados.stream().map(Agregado::getId).collect(Collectors.toList()));
criterioInfo.put("nombreCriterio", criterio.getNombre()); // ✅ NUEVO
criteriosInfo.add(criterioInfo);
```

#### 2. Calcular y Guardar Acumulados por Criterio
Se modificó la sección de carga de datos para calcular y guardar cada acumulado:

```java
// Calcular acumulados por criterio y guardarlos en la fila
int criterioIndex = 0;
for (Map<String, Object> criterioInfo : criteriosInfo) {
    @SuppressWarnings("unchecked")
    List<Long> agregadoIds = (List<Long>) criterioInfo.get("agregadoIds");
    boolean esCheck = (Boolean) criterioInfo.get("esCheck");
    Double puntuacionMaxima = (Double) criterioInfo.get("puntuacionMaxima");

    double acumuladoCriterio = 0.0;
    for (Long agregadoId : agregadoIds) {
        Object valor = fila.get("agregado_" + agregadoId);
        if (esCheck) {
            if (valor instanceof Boolean && (Boolean) valor) {
                acumuladoCriterio += puntuacionMaxima / agregadoIds.size();
            }
        } else {
            if (valor instanceof String && !((String) valor).isEmpty()) {
                try {
                    acumuladoCriterio += Double.parseDouble((String) valor);
                } catch (NumberFormatException e) {
                    // Ignorar
                }
            }
        }
    }
    
    // ✅ Guardar el acumulado del criterio
    fila.put("acumulado_criterio_" + criterioIndex, acumuladoCriterio);
    totalPortafolio += acumuladoCriterio;
    criterioIndex++;
}
```

#### 3. Guardar Total Portafolio en el Map
Se agregó la línea para guardar el total calculado:

```java
double puntosParcial = totalPortafolio + puntosExamen;
double calificacionParcial = (puntosParcial * 10.0) / 100.0;

// Guardar los valores calculados en la fila
fila.put("totalPortafolio", totalPortafolio);        // ✅ NUEVO
fila.put("puntosParcial", puntosParcial);
fila.put("calificacionParcial", calificacionParcial);
```

#### 4. Actualizar Lógica de Exportación a Excel
Se mejoró la lógica para usar los valores guardados correctamente:

```java
// Datos
int rowIndex = 5;
int criterioAcumuladoIndex = 0; // ✅ Índice para rastrear columnas de acumulado

for (Map<String, Object> item : tabla.getItems()) {
    Row row = sheet.createRow(rowIndex++);
    colIndex = 0;
    criterioAcumuladoIndex = 0; // Reiniciar para cada fila

    for (TableColumn<Map<String, Object>, ?> column : tabla.getColumns()) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(colIndex);
        String columnName = column.getText();
        Object value = null;

        if ("#".equals(columnName)) {
            value = item.get("numero");
        } else if ("Nombre Completo".equals(columnName)) {
            value = item.get("nombreCompleto");
        } else if (columnName.startsWith("Acum ")) {
            // ✅ Usar el índice para obtener el valor guardado
            value = item.get("acumulado_criterio_" + criterioAcumuladoIndex);
            criterioAcumuladoIndex++;
        } else if ("Total Portafolio".equals(columnName)) {
            // ✅ Ahora el valor existe en el Map
            value = item.get("totalPortafolio");
        }
        // ... resto de columnas ...
    }
}
```

## 📊 Valores Guardados en el Map

Ahora cada fila del informe contiene los siguientes valores calculados:

| Clave en el Map | Valor | Uso |
|----------------|-------|-----|
| `numero` | Número de lista | Columna # |
| `nombreCompleto` | Nombre del alumno | Columna Nombre Completo |
| `agregado_[ID]` | Valor del agregado | Columnas de agregados |
| `acumulado_criterio_0` | Acumulado del 1er criterio | Columna "Acum Criterio1" |
| `acumulado_criterio_1` | Acumulado del 2do criterio | Columna "Acum Criterio2" |
| `acumulado_criterio_N` | Acumulado del N-ésimo criterio | Columna "Acum CriterioN" |
| `totalPortafolio` ✅ | Suma de todos los acumulados | Columna "Total Portafolio" |
| `aciertosExamen` | Puntos del examen | Columna "Puntos Examen" |
| `porcentajeExamen` | Porcentaje del examen | Columna "% Examen" |
| `calificacionExamen` | Calificación del examen | Columna "Calif. Examen" |
| `puntosParcial` | Total portafolio + examen | Columna "Puntos Parcial" |
| `calificacionParcial` | Calificación sobre 10 | Columna "Calificación Parcial" |

## ✅ Resultado

### Antes:
```
Excel:
┌────┬──────────┬─────┬────────────────────┐
│ #  │ Nombre   │ ... │ Total Portafolio   │
├────┼──────────┼─────┼────────────────────┤
│ 1  │ Alumno A │ ... │ [VACÍO] ❌         │
│ 2  │ Alumno B │ ... │ [VACÍO] ❌         │
└────┴──────────┴─────┴────────────────────┘
```

### Ahora:
```
Excel:
┌────┬──────────┬─────┬────────────────────┐
│ #  │ Nombre   │ ... │ Total Portafolio   │
├────┼──────────┼─────┼────────────────────┤
│ 1  │ Alumno A │ ... │ 45.50 ✅           │
│ 2  │ Alumno B │ ... │ 38.75 ✅           │
└────┴──────────┴─────┴────────────────────┘
```

## 🎯 Beneficios Adicionales

Esta corrección también beneficia a:

1. **Columnas de Acumulado por Criterio**: Ahora también se exportan correctamente
2. **Performance**: Los valores se calculan una sola vez (al cargar datos) en lugar de calcularse cada vez que se accede a la celda
3. **Consistencia**: Los mismos valores calculados se usan tanto en la interfaz como en la exportación

## 🧪 Verificación

Para verificar que la corrección funciona:

1. Generar un informe con el botón "Buscar"
2. Verificar que la columna "Total Portafolio" muestra valores en la tabla
3. Exportar a Excel
4. Abrir el archivo Excel
5. ✅ Verificar que la columna "Total Portafolio" tiene valores numéricos

## 📝 Archivos Modificados

**Archivo:** `InformeConcentradoController.java`

**Líneas modificadas:**
- Línea ~337: Agregar `nombreCriterio` a criterioInfo
- Líneas ~630-660: Calcular y guardar acumulados por criterio
- Línea ~668: Guardar `totalPortafolio` en el Map
- Líneas ~820-860: Mejorar lógica de exportación para usar valores guardados

**Total de cambios:** ~50 líneas

## ✅ Estado

- ✅ **Problema corregido**
- ✅ **Sin errores de compilación**
- ✅ **Columna "Total Portafolio" se exporta correctamente**
- ✅ **Columnas de "Acumulado" también corregidas**
- ✅ **Performance mejorado**

---

**Fecha de Corrección:** 4 de febrero de 2026  
**Problema:** Columna "Total Portafolio" vacía en Excel  
**Solución:** Guardar valores calculados en el Map de datos  
**Estado:** ✅ Resuelto completamente
