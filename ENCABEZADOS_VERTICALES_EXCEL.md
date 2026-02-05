# ✅ Encabezados Verticales y Auto-ajuste de Columnas en Excel

## 📋 Cambios Implementados

Se ha modificado la exportación a Excel del formulario "Informe de Concentrado" para que los encabezados de columna tengan orientación vertical y las columnas se ajusten automáticamente de forma inteligente.

## 🎯 Modificaciones Realizadas

### 1. Orientación Vertical de Encabezados

Se agregó rotación de 90 grados a los encabezados de columna:

```java
CellStyle headerStyle = workbook.createCellStyle();
headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
headerStyle.setBorderBottom(BorderStyle.THIN);
headerStyle.setBorderTop(BorderStyle.THIN);
headerStyle.setBorderRight(BorderStyle.THIN);
headerStyle.setBorderLeft(BorderStyle.THIN);
headerStyle.setAlignment(HorizontalAlignment.CENTER);
headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
headerStyle.setRotation((short) 90); // ✅ NUEVO - Orientación vertical (90 grados)
headerStyle.setWrapText(false);      // ✅ NUEVO - Evitar wrap de texto
org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
headerFont.setBold(true);
headerStyle.setFont(headerFont);
```

### 2. Altura de Fila de Encabezados

Se aumentó la altura de la fila de encabezados para acomodar el texto vertical:

```java
// Encabezados de columna (fila 4)
Row headerRow = sheet.createRow(4);
headerRow.setHeightInPoints(120); // ✅ NUEVO - Altura mayor para texto vertical (antes: altura por defecto ~15)
int colIndex = 0;
for (TableColumn<Map<String, Object>, ?> column : tabla.getColumns()) {
    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(colIndex++);
    cell.setCellValue(column.getText());
    cell.setCellStyle(headerStyle);
}
```

### 3. Auto-ajuste Inteligente de Columnas

Se implementó un sistema de auto-ajuste que considera el tipo de cada columna:

```java
// Ajustar ancho de columnas de forma inteligente
for (int i = 0; i < tabla.getColumns().size(); i++) {
    TableColumn<Map<String, Object>, ?> column = tabla.getColumns().get(i);
    String columnName = column.getText();
    
    // Ajustar ancho según el tipo de columna
    if ("#".equals(columnName)) {
        // Columna de número - ancho fijo pequeño
        sheet.setColumnWidth(i, 1500); // ~6 caracteres
        
    } else if ("Nombre Completo".equals(columnName)) {
        // Columna de nombre - ancho mayor
        sheet.autoSizeColumn(i);
        int currentWidth = sheet.getColumnWidth(i);
        sheet.setColumnWidth(i, Math.min(currentWidth + 500, 10000)); // Máximo ~40 caracteres
        
    } else if (columnName.startsWith("Acum ") || 
               "Total Portafolio".equals(columnName) || 
               "Puntos Examen".equals(columnName) ||
               "% Examen".equals(columnName) ||
               "Calif. Examen".equals(columnName) ||
               "Puntos Parcial".equals(columnName) ||
               "Calificación Parcial".equals(columnName)) {
        // Columnas de cálculos - ancho mediano
        sheet.setColumnWidth(i, 2500); // ~10 caracteres
        
    } else {
        // Columnas de agregados - ancho pequeño/mediano
        sheet.setColumnWidth(i, 2000); // ~8 caracteres
    }
}
```

## 📊 Anchos de Columna por Tipo

| Tipo de Columna | Ancho (unidades Excel) | Aprox. Caracteres | Uso |
|----------------|------------------------|-------------------|-----|
| `#` | 1,500 | ~6 | Número de lista |
| `Nombre Completo` | Auto + 500 (max 10,000) | Variable (~40 max) | Nombres de alumnos |
| Agregados | 2,000 | ~8 | Columnas de tareas, trabajos |
| Acumulados | 2,500 | ~10 | Acum por criterio |
| Total Portafolio | 2,500 | ~10 | Suma de portafolio |
| Puntos Examen | 2,500 | ~10 | Puntos del examen |
| % Examen | 2,500 | ~10 | Porcentaje |
| Calif. Examen | 2,500 | ~10 | Calificación examen |
| Puntos Parcial | 2,500 | ~10 | Total de puntos |
| Calificación Parcial | 2,500 | ~10 | Calificación final |

## 🎨 Comparación Visual

### Antes:
```
Excel (Encabezados Horizontales):
┌────┬─────────────────────┬──────────┬──────────┬───────────────┐
│ #  │ Nombre Completo     │ Tarea 1  │ Tarea 2  │ Total Port... │
├────┼─────────────────────┼──────────┼──────────┼───────────────┤
│ 1  │ García Pérez Ana    │ 10       │ 8.5      │ 45.50         │
│ 2  │ López Martínez Juan │ 9        │ 7.0      │ 38.75         │
└────┴─────────────────────┴──────────┴──────────┴───────────────┘

Problemas:
- Encabezados largos ocupan mucho espacio horizontal
- Difícil de leer cuando hay muchas columnas
- Anchos inconsistentes
```

### Ahora:
```
Excel (Encabezados Verticales):
┌───┬───────────────────┬─┬─┬───┐
│ # │ Nombre Completo   │T│T│T  │
│   │                   │a│a│o  │
│   │                   │r│r│t  │
│   │                   │e│e│a  │
│   │                   │a│a│l  │
│   │                   │ │ │   │
│   │                   │1│2│P..│
├───┼───────────────────┼─┼─┼───┤
│ 1 │ García Pérez Ana  │1│8│45 │
│   │                   │0│.│.5 │
│   │                   │ │5│0  │
├───┼───────────────────┼─┼─┼───┤
│ 2 │ López Martínez J. │9│7│38 │
│   │                   │ │.│.7 │
│   │                   │ │0│5  │
└───┴───────────────────┴─┴─┴───┘

Ventajas:
✅ Encabezados compactos (columnas más angostas)
✅ Se pueden ver más columnas en pantalla
✅ Mejor uso del espacio horizontal
✅ Fácil lectura con muchas columnas
✅ Anchos consistentes y optimizados
```

## 📐 Detalles Técnicos

### Rotación de Texto
- **Propiedad:** `CellStyle.setRotation(short rotation)`
- **Valor:** `90` (grados)
- **Rango válido:** -90 a 90 grados
- **Efecto:** Texto escrito verticalmente de abajo hacia arriba

### Altura de Fila
- **Propiedad:** `Row.setHeightInPoints(float height)`
- **Valor:** `120` puntos
- **Default:** ~15 puntos
- **Razón:** Acomodar texto vertical largo (ej: "Calificación Parcial")

### Unidades de Ancho de Columna
Excel usa unidades especiales para el ancho:
- **1 unidad** ≈ 1/256 del ancho de un carácter '0'
- **256 unidades** = 1 carácter
- **2560 unidades** = 10 caracteres

Conversión aproximada:
```
Caracteres × 256 = Unidades Excel
6 × 256 = 1,536 ≈ 1,500 (columna #)
8 × 256 = 2,048 ≈ 2,000 (agregados)
10 × 256 = 2,560 ≈ 2,500 (cálculos)
```

## 🎯 Ventajas de la Implementación

### 1. **Ahorro de Espacio Horizontal**
- Columnas más angostas = más columnas visibles
- Mejor para informes con muchos criterios/agregados
- Reduce necesidad de scroll horizontal

### 2. **Consistencia Visual**
- Anchos predefinidos por tipo de columna
- Aspecto más profesional y organizado
- Fácil de leer y comparar datos

### 3. **Optimización Inteligente**
- Columna de número: mínimo necesario
- Columna de nombre: se ajusta al contenido
- Columnas de datos: ancho fijo consistente

### 4. **Legibilidad**
- Texto vertical estándar en reportes
- Encabezados bien separados del contenido
- Bordes en todas las celdas para claridad

## 📊 Ejemplo Real

### Escenario: Materia con 5 criterios, 3 agregados cada uno

**Antes (horizontal):**
- 15 columnas de agregados × 15 caracteres promedio = 225 caracteres de ancho
- Requiere scroll horizontal extenso
- Difícil ver inicio y fin simultáneamente

**Ahora (vertical):**
- 15 columnas de agregados × 8 caracteres = 120 caracteres de ancho
- ~46% menos espacio horizontal
- Más fácil visualizar todo el informe

## ✅ Estado Final

- ✅ **Encabezados verticales (90°)** implementados
- ✅ **Altura de fila ajustada** (120 puntos)
- ✅ **Auto-ajuste inteligente** por tipo de columna
- ✅ **Sin errores de compilación**
- ✅ **Listo para usar**

## 🔧 Archivo Modificado

**Archivo:** `InformeConcentradoController.java`

**Método:** `exportarAExcel()`

**Líneas modificadas:**
- Línea ~751: Agregar `headerStyle.setRotation((short) 90)`
- Línea ~752: Agregar `headerStyle.setWrapText(false)`
- Línea ~823: Agregar `headerRow.setHeightInPoints(120)`
- Líneas ~928-955: Reemplazar auto-ajuste simple con lógica inteligente

**Total de cambios:** ~30 líneas

---

**Fecha de Implementación:** 4 de febrero de 2026  
**Características:** Encabezados verticales + Auto-ajuste inteligente  
**Estado:** ✅ Implementado y funcional
