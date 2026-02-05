# ✅ Restauración del Botón "Generar archivo" - Exportación a Word

## 📋 Resumen de Cambios

Se ha restaurado la funcionalidad del botón **"Generar archivo"** en el formulario de **Concentrado de Calificaciones**, que permite exportar los datos a un documento Word usando una plantilla prediseñada.

---

## 🎯 Cambios Implementados

### 1. **Imports Adicionales** (ConcentradoController.java)
Se agregaron los siguientes imports necesarios:
- `javafx.scene.layout.HBox`
- `javafx.stage.FileChooser`
- `javafx.geometry.Pos`
- `org.apache.poi.xwpf.usermodel.*`
- `java.awt.Desktop`
- `java.io.*`
- `java.nio.file.*`
- `java.time.*`

### 2. **Nuevas Dependencias Inyectadas**
Se agregaron al constructor de `ConcentradoController`:
- `ConfiguracionServicePort configuracionService` - Para obtener el nombre del maestro
- `GrupoMateriaServicePort grupoMateriaService` - Para obtener las materias asignadas a grupos

### 3. **Nuevo Botón en la Interfaz**
Se agregó el botón **"📄 Generar archivo"** junto al botón "💾 Guardar Cambios":
- **Color**: Naranja (#FF9800)
- **Ubicación**: Panel de botones en la parte inferior de la tabla
- **Acción**: Exporta los datos de la tabla a un archivo Word

### 4. **Métodos Implementados**

#### `generarArchivoConcentrado(...)`
Método principal que genera el archivo Word:
- Valida que haya datos en la tabla
- Obtiene información adicional (fecha de aplicación, nombre del maestro, semestre)
- Abre FileChooser para que el usuario seleccione dónde guardar
- Lee la plantilla `plantillas/concentrado_calificaciones.docx`
- Reemplaza variables en la plantilla
- Llena la tabla con datos de alumnos
- Calcula automáticamente: portafolio, puntos de examen, calificación parcial
- Ofrece opción de abrir el archivo generado

#### Métodos Auxiliares:
- `escribirSoloTexto(...)` - Escribe texto sin alterar formato de la plantilla
- `escribirTextoConFuenteReducida(...)` - Escribe con fuente 2 puntos más pequeña
- `convertirCalificacionALetra(...)` - Convierte calificación numérica a letras
- `convertirEnteroALetra(...)` - Convierte números 0-10 a letras
- `convertirDigitoALetra(...)` - Convierte dígitos 0-9 a letras
- `obtenerSemestreDesdeGrupoId(...)` - Extrae el semestre del ID del grupo
- `reemplazarEtiquetasEnParrafo(...)` - Reemplaza variables `${...}` en párrafos

---

## 🚀 Cómo Usar la Funcionalidad

### Paso 1: Seleccionar Datos
1. En el formulario **Concentrado de Calificaciones**
2. Seleccionar:
   - **Grupo** (ej. 601)
   - **Materia** (ej. Matemáticas)
   - **Parcial** (1, 2 o 3)
3. Presionar el botón **"Buscar"** para cargar los datos

### Paso 2: Generar Archivo
1. Presionar el botón **"📄 Generar archivo"** (botón naranja)
2. Se abrirá un diálogo para seleccionar dónde guardar el archivo
3. El sistema sugiere un nombre: `concentrado_[grupo]_[materia]_parcial[X]_[fecha].docx`
4. Seleccionar ubicación y presionar **"Guardar"**

### Paso 3: Resultado
- Se mostrará un mensaje de éxito
- Opción de **"Abrir archivo"** para ver el documento inmediatamente
- O presionar **"Cerrar"** y abrir el archivo manualmente después

---

## 📄 Plantilla de Word

### Ubicación
`plantillas/concentrado_calificaciones.docx`

### Variables que Reemplaza
La plantilla debe contener las siguientes variables (formato `${variable}`):
- `${materia}` → Nombre de la materia (ej. "Matemáticas")
- `${fecha_aplicacion}` → Fecha de aplicación del examen (ej. "15/01/2026")
- `${nombre_maestro}` → Nombre del maestro configurado
- `${parcial}` → Número de parcial (1, 2 o 3)
- `${SEMESTRE}` → Nombre del semestre (PRIMER, SEGUNDO, etc.)

### Estructura de la Tabla
La plantilla debe contener una tabla con al menos **9 columnas**:

| # | Columna | Descripción | Formato |
|---|---------|-------------|---------|
| 1 | Número de lista | Número del alumno | Entero |
| 2 | Nombre completo | Nombre y apellidos | Texto |
| 3 | (Columna extra) | Se llena con "0" | Entero |
| 4 | Total criterios | Número total de criterios | Entero |
| 5 | Portafolio | Suma de puntos de criterios | 2 dígitos (ej. "95") |
| 6 | Calificación Examen | Calificación sobre 10 | 1 decimal (ej. "8.5") |
| 7 | Puntos Examen | Aciertos del examen | Entero |
| 8 | Calificación Parcial | Calificación final del parcial | 1 decimal (ej. "9.2") |
| 9 | Calificación en Letra | Calificación en texto | Texto (ej. "Nueve punto dos") |

### Comportamiento Dinámico
- Si hay **más alumnos** que filas en la plantilla, se insertan automáticamente filas adicionales
- El formato de las filas insertadas **se copia** de la última fila de la plantilla
- Los estilos, bordes, fuentes y colores **se preservan**

---

## 🧮 Cálculos Automáticos

### Portafolio
Suma de puntos obtenidos en todos los criterios del parcial:

**Para criterios tipo "Check":**
```
puntos = (checkboxes_marcados / total_checkboxes) * puntuacion_maxima
```

**Para criterios tipo "Puntuación":**
```
puntos = suma_de_valores_capturados
```

### Calificación del Examen
```
calificacion_examen = (aciertos / total_puntos_examen) * 10
```

### Calificación Parcial
```
puntos_parcial = portafolio + calificacion_examen
calificacion_parcial = (puntos_parcial * 10) / 100
```

### Conversión a Letra
- `9.3` → `"Nueve punto tres"`
- `8.5` → `"Ocho punto cinco"`
- `10.0` → `"Diez punto cero"`

---

## 🎨 Diseño de la Interfaz

### Panel de Botones
Ahora tiene **2 botones** alineados a la derecha:

```
┌─────────────────────────────────────────────────┐
│ Tabla de Calificaciones                         │
├─────────────────────────────────────────────────┤
│                                                 │
│         [Datos de la tabla...]                  │
│                                                 │
├─────────────────────────────────────────────────┤
│           📄 Generar archivo  💾 Guardar Cambios│
└─────────────────────────────────────────────────┘
```

### Estilos de Botones
- **Generar archivo**: Naranja (#FF9800), texto blanco, emoji 📄
- **Guardar Cambios**: Verde (#4CAF50), texto blanco, emoji 💾

---

## 🔧 Detalles Técnicos

### Tecnologías Utilizadas
- **Apache POI 5.2.5** - Manipulación de archivos Word (.docx)
- **JavaFX FileChooser** - Diálogo de guardar archivo
- **Desktop API** - Apertura automática del archivo generado

### Manejo de Errores
✅ **Validación de datos**: Verifica que la tabla tenga datos antes de exportar  
✅ **Plantilla no encontrada**: Muestra error si no existe la plantilla  
✅ **Usuario cancela**: Maneja correctamente cuando el usuario cancela el diálogo  
✅ **Errores de escritura**: Captura y reporta errores de I/O  
✅ **Logs detallados**: Registra todo el proceso para depuración

### Logs Generados
```
INFO: Total de criterios para materia X parcial Y: Z
INFO: Tabla encontrada con N filas
INFO: Total de alumnos a exportar: M
INFO: Insertando X filas adicionales en la tabla
INFO: Alumno 1 en fila 6: numero='1', nombre='Juan Pérez'
INFO: Reemplazada etiqueta ${materia} con: Matemáticas
INFO: Datos escritos para M alumnos
```

---

## 📦 Dependencias Necesarias

### En pom.xml (Ya incluidas ✅)
```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.5</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
<dependency>
    <groupId>org.apache.xmlbeans</groupId>
    <artifactId>xmlbeans</artifactId>
    <version>5.1.1</version>
</dependency>
```

---

## ✅ Verificación de la Implementación

### Archivos Modificados
- ✅ `ConcentradoController.java` - Implementación completa
- ✅ Imports adicionales agregados
- ✅ Nuevas dependencias inyectadas
- ✅ Botón agregado a la interfaz
- ✅ Métodos implementados

### Archivos Existentes Requeridos
- ✅ `plantillas/concentrado_calificaciones.docx` - Plantilla existe
- ✅ Apache POI en pom.xml - Dependencias existen
- ✅ `ConfiguracionServicePort` - Servicio existe
- ✅ `GrupoMateriaServicePort` - Servicio existe

### Estado del Código
- ✅ **Sin errores de compilación**
- ⚠️ Solo warnings (advertencias menores de estilo)
- ✅ **Listo para usar**

---

## 🎯 Próximos Pasos

1. **Compilar el proyecto**: `mvn clean compile`
2. **Ejecutar la aplicación**: Usar el script `run.ps1` o ejecutar desde IDE
3. **Probar la funcionalidad**:
   - Ir a **Concentrado de Calificaciones**
   - Seleccionar Grupo, Materia y Parcial
   - Presionar **"Buscar"**
   - Presionar **"📄 Generar archivo"**
   - Verificar que el archivo Word se genera correctamente

---

## 🐛 Resolución de Problemas

### Error: "No se encontró la plantilla"
**Causa**: La plantilla no está en la ruta correcta  
**Solución**: Verificar que existe `plantillas/concentrado_calificaciones.docx` en la raíz del proyecto

### Error: "La plantilla no contiene ninguna tabla"
**Causa**: La plantilla no tiene tablas o está corrupta  
**Solución**: Abrir la plantilla en Word y verificar que contiene una tabla

### El archivo se genera pero está vacío
**Causa**: No hay datos en la tabla  
**Solución**: Asegurarse de presionar "Buscar" antes de generar el archivo

### Los valores están incorrectos
**Causa**: Los nombres de las columnas no coinciden  
**Solución**: Verificar que los índices de columna (COL_NUMERO_LISTA, COL_NOMBRE_COMPLETO, etc.) coincidan con la plantilla

### No se puede abrir el archivo automáticamente
**Causa**: Desktop API no soportado o no hay aplicación asociada  
**Solución**: Abrir el archivo manualmente desde la ubicación donde se guardó

---

## 📊 Comparación con Exportación a Excel

| Característica | Word (Plantilla) | Excel (Actual) |
|----------------|------------------|----------------|
| **Formato** | ✅ Mantiene diseño prediseñado | ⚠️ Genera desde cero |
| **Personalización** | ✅ Alta (editar plantilla) | ⚠️ Media (código) |
| **Documentos formales** | ✅ Ideal | ❌ No recomendado |
| **Análisis de datos** | ❌ Limitado | ✅ Excelente |
| **Complejidad** | ⚠️ Media-Alta | ✅ Media |
| **Velocidad** | ✅ Rápida | ✅ Rápida |

### ¿Cuándo usar cada uno?
- **Word**: Para documentos formales, reportes oficiales, impresión
- **Excel**: Para análisis, gráficos, procesamiento de datos

---

## 🎉 Conclusión

La funcionalidad de **exportación a Word** ha sido restaurada exitosamente en el formulario de Concentrado de Calificaciones. Los usuarios ahora pueden:

✅ Generar documentos Word profesionales con un solo clic  
✅ Usar plantillas personalizadas  
✅ Mantener el formato institucional  
✅ Abrir los documentos automáticamente  
✅ Tener todos los cálculos realizados automáticamente  

---

**Fecha de restauración**: 04/02/2026  
**Versión**: 1.0-SNAPSHOT  
**Estado**: ✅ Completado y listo para usar
