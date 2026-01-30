# Configuración y Uso de Apache POI para Exportar a Word

## 📋 Descripción

Este documento explica cómo usar Apache POI para exportar datos del sistema a documentos de Microsoft Word (.docx). Apache POI es una biblioteca Java que permite crear, modificar y leer archivos de Microsoft Office.

## 🔧 Configuración Realizada

### Dependencias Agregadas en pom.xml

Se han agregado las siguientes dependencias para trabajar con documentos Word:

```xml
<!-- Apache POI for Word documents -->
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
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml-schemas</artifactId>
    <version>5.2.5</version>
</dependency>
<dependency>
    <groupId>org.apache.xmlbeans</groupId>
    <artifactId>xmlbeans</artifactId>
    <version>5.1.1</version>
</dependency>
```

### ¿Qué incluye cada dependencia?

- **poi**: Librería base de Apache POI
- **poi-ooxml**: Soporte para formatos Office Open XML (.docx, .xlsx, .pptx)
- **poi-ooxml-schemas**: Esquemas XML necesarios para OOXML
- **xmlbeans**: Procesamiento de XML para los esquemas de Office

## 📦 Servicio WordExportService

Se ha creado el servicio `WordExportService` ubicado en:
```
src/main/java/com/alumnos/application/service/WordExportService.java
```

### Funcionalidades del Servicio

#### 1. Crear Documento Simple

Crea un documento Word básico con título y contenido:

```java
@Autowired
private WordExportService wordExportService;

public void ejemploDocumentoSimple() throws IOException {
    Path outputPath = Paths.get("documento_simple.docx");
    String titulo = "Reporte de Alumnos";
    String contenido = "Este es el contenido del documento...";
    
    wordExportService.crearDocumentoSimple(outputPath, titulo, contenido);
}
```

#### 2. Crear Documento con Tabla

Genera un documento con una tabla estructurada:

```java
public void ejemploDocumentoConTabla() throws IOException {
    Path outputPath = Paths.get("lista_alumnos.docx");
    String titulo = "Lista de Alumnos - Grupo 001";
    
    List<String> encabezados = List.of("Matrícula", "Nombre", "Apellidos", "Grupo");
    
    List<List<String>> datos = List.of(
        List.of("2024001", "Juan", "Pérez García", "001"),
        List.of("2024002", "María", "López Rodríguez", "001"),
        List.of("2024003", "Carlos", "Sánchez Martínez", "001")
    );
    
    wordExportService.crearDocumentoConTabla(outputPath, titulo, encabezados, datos);
}
```

#### 3. Usar Plantilla con Marcadores

Reemplaza variables en una plantilla existente. Los marcadores deben usar el formato `${nombreVariable}`:

**Ejemplo de plantilla (plantilla.docx):**
```
REPORTE DE CALIFICACIONES

Materia: ${nombreMateria}
Grupo: ${numeroGrupo}
Docente: ${nombreDocente}
Fecha: ${fecha}
```

**Código para usar la plantilla:**
```java
public void ejemploConPlantilla() throws IOException {
    Path templatePath = Paths.get("plantillas/plantilla.docx");
    Path outputPath = Paths.get("reporte_generado.docx");
    
    Map<String, String> variables = Map.of(
        "nombreMateria", "Programación Orientada a Objetos",
        "numeroGrupo", "001",
        "nombreDocente", "Dr. Juan Pérez",
        "fecha", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    );
    
    wordExportService.generarDesdeTemplateConMarcadores(templatePath, outputPath, variables);
}
```

#### 4. Plantilla con Tabla Dinámica

Usa una plantilla y llena una tabla con datos dinámicos:

```java
public void ejemploPlantillaConTabla() throws IOException {
    Path templatePath = Paths.get("plantillas/reporte_con_tabla.docx");
    Path outputPath = Paths.get("reporte_calificaciones.docx");
    
    // Variables simples
    Map<String, String> variables = Map.of(
        "nombreMateria", "Programación Java",
        "periodo", "Enero-Junio 2024"
    );
    
    // Datos de la tabla
    List<String> encabezados = List.of("Matrícula", "Nombre", "Parcial 1", "Parcial 2", "Final");
    
    List<List<String>> datos = List.of(
        List.of("2024001", "Juan Pérez", "8.5", "9.0", "8.8"),
        List.of("2024002", "María López", "9.5", "9.5", "9.5"),
        List.of("2024003", "Carlos Sánchez", "7.5", "8.0", "7.8")
    );
    
    wordExportService.generarDesdeTemplateConTabla(
        templatePath, 
        outputPath, 
        variables, 
        "TABLA_CALIFICACIONES",  // Marcador en la plantilla
        encabezados, 
        datos
    );
}
```

## 🎯 Casos de Uso en el Sistema

### 1. Exportar Lista de Alumnos de un Grupo

```java
@Service
public class AlumnoReportService {
    
    @Autowired
    private AlumnoService alumnoService;
    
    @Autowired
    private WordExportService wordExportService;
    
    public void exportarAlumnosDeGrupo(Long grupoId, Path outputPath) throws IOException {
        List<AlumnoDTO> alumnos = alumnoService.obtenerAlumnosPorGrupo(grupoId);
        
        List<String> encabezados = List.of("Matrícula", "Nombre Completo", "Edad", "Promedio");
        
        List<List<String>> datos = alumnos.stream()
            .map(a -> List.of(
                a.getMatricula(),
                a.getNombre() + " " + a.getApellidos(),
                String.valueOf(a.getEdad()),
                String.format("%.2f", a.getPromedio())
            ))
            .toList();
        
        wordExportService.crearDocumentoConTabla(
            outputPath, 
            "Lista de Alumnos", 
            encabezados, 
            datos
        );
    }
}
```

### 2. Exportar Concentrado de Calificaciones

```java
public void exportarConcentrado(Long grupoId, Long materiaId, Path outputPath) throws IOException {
    // Obtener datos
    GrupoDTO grupo = grupoService.obtenerPorId(grupoId);
    MateriaDTO materia = materiaService.obtenerPorId(materiaId);
    List<CalificacionConcentradoDTO> calificaciones = 
        calificacionService.obtenerConcentrado(grupoId, materiaId);
    
    // Variables para la plantilla
    Map<String, String> variables = Map.of(
        "nombreMateria", materia.getNombre(),
        "numeroGrupo", grupo.getNumeroGrupo(),
        "cuatrimestre", String.valueOf(materia.getCuatrimestre()),
        "fecha", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    );
    
    // Datos de la tabla
    List<String> encabezados = List.of(
        "Matrícula", "Nombre", "Parcial 1", "Parcial 2", "Parcial 3", 
        "Examen", "Promedio", "Calificación Final"
    );
    
    List<List<String>> datos = calificaciones.stream()
        .map(c -> List.of(
            c.getMatricula(),
            c.getNombreCompleto(),
            formatearCalificacion(c.getParcial1()),
            formatearCalificacion(c.getParcial2()),
            formatearCalificacion(c.getParcial3()),
            formatearCalificacion(c.getExamen()),
            formatearCalificacion(c.getPromedio()),
            formatearCalificacion(c.getCalificacionFinal())
        ))
        .toList();
    
    Path templatePath = Paths.get("plantillas/concentrado_calificaciones.docx");
    
    wordExportService.generarDesdeTemplateConTabla(
        templatePath,
        outputPath,
        variables,
        "TABLA_CALIFICACIONES",
        encabezados,
        datos
    );
}

private String formatearCalificacion(Double cal) {
    return cal != null ? String.format("%.2f", cal) : "N/A";
}
```

### 3. Exportar Reporte de Examen

```java
public void exportarReporteExamen(Long examenId, Path outputPath) throws IOException {
    ExamenDTO examen = examenService.obtenerPorId(examenId);
    List<AlumnoExamenDTO> resultados = alumnoExamenService.obtenerPorExamen(examenId);
    
    Map<String, String> variables = Map.of(
        "nombreExamen", examen.getNombre(),
        "fecha", examen.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        "totalAciertos", String.valueOf(examen.getTotalAciertos()),
        "porcentaje", String.format("%.0f%%", examen.getPorcentaje())
    );
    
    List<String> encabezados = List.of(
        "Matrícula", "Nombre", "Puntos", "Porcentaje", "Calificación"
    );
    
    List<List<String>> datos = resultados.stream()
        .map(r -> List.of(
            r.getMatricula(),
            r.getNombreAlumno(),
            String.valueOf(r.getPuntos()),
            String.format("%.2f%%", r.getPorcentajeObtenido()),
            String.format("%.2f", r.getCalificacion())
        ))
        .toList();
    
    Path templatePath = Paths.get("plantillas/reporte_examen.docx");
    
    wordExportService.generarDesdeTemplateConTabla(
        templatePath,
        outputPath,
        variables,
        "TABLA_RESULTADOS",
        encabezados,
        datos
    );
}
```

## 📁 Estructura de Plantillas Recomendada

Crear una carpeta `plantillas` en la raíz del proyecto:

```
alumnos/
├── plantillas/
│   ├── concentrado_calificaciones.docx
│   ├── reporte_examen.docx
│   ├── lista_alumnos.docx
│   └── reporte_general.docx
├── src/
└── pom.xml
```

## 🎨 Crear Plantillas en Word

### Pasos para crear una plantilla:

1. **Crear documento en Word**
2. **Insertar marcadores** usando el formato `${nombreVariable}`
3. **Para tablas dinámicas**: 
   - Crear una tabla con al menos una fila
   - En alguna celda, colocar el marcador (ej: `TABLA_CALIFICACIONES`)
4. **Guardar como .docx**

### Ejemplo de plantilla para concentrado:

```
CONCENTRADO DE CALIFICACIONES

Materia: ${nombreMateria}
Grupo: ${numeroGrupo}
Cuatrimestre: ${cuatrimestre}
Fecha: ${fecha}

TABLA_CALIFICACIONES
```

## 🔌 Integrar con JavaFX

Para integrar la exportación con la interfaz gráfica:

```java
@FXML
private void handleExportarConcentrado() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Guardar Concentrado");
    fileChooser.setInitialFileName("concentrado_" + 
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".docx");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Documentos Word", "*.docx")
    );
    
    File file = fileChooser.showSaveDialog(stage);
    if (file != null) {
        try {
            // Obtener IDs seleccionados
            Long grupoId = grupoComboBox.getValue().getId();
            Long materiaId = materiaComboBox.getValue().getId();
            
            // Exportar
            reportService.exportarConcentrado(grupoId, materiaId, file.toPath());
            
            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportación Exitosa");
            alert.setHeaderText(null);
            alert.setContentText("El concentrado se ha exportado correctamente.");
            alert.showAndWait();
            
            // Opción para abrir el archivo
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
            
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al exportar");
            alert.setContentText("No se pudo exportar el concentrado: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
```

## 🚀 Instalación de Dependencias

### Opción 1: Usando IntelliJ IDEA

1. Abrir el proyecto en IntelliJ IDEA
2. El IDE detectará los cambios en `pom.xml`
3. Click en el ícono de Maven que aparece en la esquina superior derecha
4. O usar: **Maven → Reload Project**

### Opción 2: Usando Maven desde terminal

Si Maven está instalado:

```powershell
cd D:\Desarrollos\alumnos
mvn clean install
```

Si Maven no está instalado, ejecutar el script:

```powershell
.\install-maven.ps1
```

## 📝 Notas Importantes

### Formatos de Archivo

- Apache POI trabaja con formato **.docx** (Office Open XML)
- **NO** soporta el formato antiguo **.doc**
- Asegurarse de que las plantillas estén en formato .docx

### Rendimiento

- Para documentos con muchos datos (>1000 filas), considerar:
  - Procesar en segundo plano (usar `Task` de JavaFX)
  - Mostrar barra de progreso
  - Limitar la cantidad de datos exportados

### Manejo de Errores

Siempre envolver las operaciones en try-catch:

```java
try {
    wordExportService.crearDocumentoConTabla(...);
} catch (IOException e) {
    logger.error("Error al exportar documento", e);
    // Mostrar mensaje al usuario
}
```

## 🔗 Referencias

- [Documentación Apache POI](https://poi.apache.org/components/document/)
- [Ejemplos XWPF](https://poi.apache.org/components/document/quick-guide-xwpf.html)
- [API JavaDoc](https://poi.apache.org/apidocs/5.2/)

## ✅ Testing

Para probar la funcionalidad, crear un test:

```java
@SpringBootTest
class WordExportServiceTest {
    
    @Autowired
    private WordExportService wordExportService;
    
    @Test
    void testCrearDocumentoSimple() throws IOException {
        Path outputPath = Paths.get("test_documento.docx");
        wordExportService.crearDocumentoSimple(
            outputPath, 
            "Título de Prueba", 
            "Contenido de prueba"
        );
        
        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);
        
        // Limpiar
        Files.deleteIfExists(outputPath);
    }
}
```

## 🎓 Próximos Pasos

1. Crear las plantillas de Word necesarias
2. Implementar los servicios de reporte específicos
3. Agregar botones de exportación en las interfaces
4. Probar con datos reales
5. Optimizar según necesidades

---

**Última actualización**: 2026-01-29  
**Versión Apache POI**: 5.2.5
