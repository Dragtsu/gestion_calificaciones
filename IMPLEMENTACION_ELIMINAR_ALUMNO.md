# Implementación de Eliminación de Alumnos con Verificación de Dependencias

## Resumen
Se ha implementado la funcionalidad de eliminar alumnos en el formulario de estudiantes, incluyendo verificación de dependencias antes de permitir la eliminación.

## Cambios Realizados

### 1. Servicio de Alumnos (`AlumnoService.java`)

#### Inyección de Dependencias
Se agregaron las siguientes dependencias al servicio para verificar las relaciones:
- `CalificacionServicePort` - Para verificar calificaciones regulares
- `CalificacionConcentradoServicePort` - Para verificar calificaciones del concentrado
- `AlumnoExamenServicePort` - Para verificar exámenes asociados

#### Método `eliminarAlumno(Long id)`
Se modificó para incluir validaciones antes de eliminar:

```java
@Override
@Transactional
public void eliminarAlumno(Long id) {
    // Verificar si el alumno tiene calificaciones asociadas
    if (!calificacionService.obtenerCalificacionesPorAlumno(id).isEmpty()) {
        throw new IllegalStateException("No se puede eliminar el alumno porque tiene calificaciones registradas");
    }
    
    // Verificar si el alumno tiene calificaciones en el concentrado
    if (!calificacionConcentradoService.obtenerCalificacionesPorAlumno(id).isEmpty()) {
        throw new IllegalStateException("No se puede eliminar el alumno porque tiene calificaciones en el concentrado");
    }
    
    // Verificar si el alumno tiene exámenes asociados
    if (!alumnoExamenService.obtenerAlumnoExamenPorAlumno(id).isEmpty()) {
        throw new IllegalStateException("No se puede eliminar el alumno porque tiene exámenes registrados");
    }
    
    // Si pasa todas las validaciones, elimina el alumno
    Optional<Alumno> alumno = alumnoRepositoryPort.findById(id);
    Long grupoId = alumno.map(Alumno::getGrupoId).orElse(null);
    
    alumnoRepositoryPort.deleteById(id);
    
    // Recalcular números de lista para el grupo
    if (grupoId != null) {
        recalcularNumerosLista(grupoId);
    }
}
```

### 2. Controlador de Estudiantes (`EstudiantesController.java`)

#### Columna de Acciones en la Tabla
Se agregó una nueva columna "Acciones" con un botón "Eliminar" en cada fila:

```java
TableColumn<Alumno, Void> colAcciones = new TableColumn<>("Acciones");
colAcciones.setCellFactory(param -> new javafx.scene.control.TableCell<Alumno, Void>() {
    private final Button btnEliminar = new Button("Eliminar");
    
    {
        btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
        btnEliminar.setOnAction(event -> {
            Alumno alumno = getTableView().getItems().get(getIndex());
            eliminarAlumno(alumno);
        });
    }
    
    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(btnEliminar);
        }
    }
});
```

#### Método `eliminarAlumno(Alumno alumno)`
Nuevo método que maneja la eliminación con confirmación del usuario:

```java
private void eliminarAlumno(Alumno alumno) {
    if (alumno == null) {
        mostrarError("Por favor seleccione un alumno");
        return;
    }
    
    // Mostrar diálogo de confirmación
    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText("¿Está seguro de eliminar este alumno?");
    confirmacion.setContentText(alumno.getNombre() + " " + 
                               alumno.getApellidoPaterno() + " " + 
                               alumno.getApellidoMaterno());
    
    confirmacion.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            try {
                alumnoService.eliminarAlumno(alumno.getId());
                mostrarExito("Alumno eliminado correctamente");
                
                // Recargar la tabla
                if (tablaAlumnos != null) {
                    cargarDatos(tablaAlumnos);
                }
            } catch (IllegalStateException e) {
                // Error de validación de dependencias
                mostrarError(e.getMessage());
            } catch (Exception e) {
                manejarExcepcion("eliminar alumno", e);
            }
        }
    });
}
```

## Funcionalidades Implementadas

### ✅ Validación de Dependencias
Antes de eliminar un alumno, el sistema verifica:
1. **Calificaciones regulares**: Si tiene calificaciones en el módulo de calificaciones
2. **Calificaciones del concentrado**: Si tiene calificaciones en el concentrado
3. **Exámenes**: Si tiene exámenes asociados

### ✅ Confirmación del Usuario
Se muestra un diálogo de confirmación con:
- Título: "Confirmar eliminación"
- Mensaje personalizado con el nombre completo del alumno
- Botones OK/Cancelar

### ✅ Mensajes de Error Claros
Si el alumno tiene dependencias, se muestra un mensaje específico indicando:
- "No se puede eliminar el alumno porque tiene calificaciones registradas"
- "No se puede eliminar el alumno porque tiene calificaciones en el concentrado"
- "No se puede eliminar el alumno porque tiene exámenes registrados"

### ✅ Recalculación Automática
Después de eliminar un alumno exitosamente:
- Se recalculan los números de lista del grupo al que pertenecía
- Se actualiza automáticamente la tabla de alumnos

### ✅ Diseño Visual
- Botón rojo (#f44336) para indicar acción destructiva
- Estilo consistente con el resto de la aplicación
- Botón en cada fila de la tabla para fácil acceso

## Flujo de Uso

1. El usuario ve la lista de alumnos en la tabla
2. Hace clic en el botón "Eliminar" de la fila del alumno deseado
3. Se muestra un diálogo de confirmación
4. Si confirma:
   - El sistema verifica que no haya dependencias
   - Si hay dependencias, muestra un mensaje de error específico
   - Si no hay dependencias, elimina el alumno
   - Muestra mensaje de éxito
   - Actualiza automáticamente la tabla
5. Si cancela, no se realiza ninguna acción

## Archivos Modificados

1. `src/main/java/com/alumnos/application/service/AlumnoService.java`
   - Agregadas dependencias para verificación
   - Modificado método `eliminarAlumno()` con validaciones

2. `src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/EstudiantesController.java`
   - Agregada columna de acciones en la tabla
   - Agregado método `eliminarAlumno()`
   - Manejo de confirmación y errores

## Ventajas de la Implementación

- ✅ **Integridad de datos**: Previene la eliminación de datos que tienen relaciones
- ✅ **Experiencia de usuario**: Confirmación clara antes de acciones destructivas
- ✅ **Mensajes claros**: El usuario entiende por qué no puede eliminar un registro
- ✅ **Transaccional**: Usa `@Transactional` para garantizar consistencia
- ✅ **Mantenible**: Código limpio y bien estructurado
- ✅ **Consistente**: Sigue los patrones ya establecidos en la aplicación

## Notas Técnicas

- Se usa `IllegalStateException` para errores de validación de negocio
- El método es transaccional para garantizar atomicidad
- Se recalculan automáticamente los números de lista después de eliminar
- El botón de eliminar está disponible en cada fila sin necesidad de selección previa
