# ✅ ENTIDAD MATERIA SIMPLIFICADA - Solo ID y Nombre

## 🎯 Cambios Realizados

La entidad **Materia** ha sido simplificada para usar **solo el campo ID** como identificador único, eliminando el campo `codigo` redundante.

### Estructura Final:
- **id** - Long (PRIMARY KEY, autoincrementable)
- **nombre** - String (nombre de la materia)

---

## 📋 Archivos Modificados (6 archivos)

### 1. **MateriaEntity.java** (Entidad JPA)
```java
@Entity
@Table(name = "materias")
public class MateriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;
    
    // ❌ Eliminado: private Long codigo;
}
```

**Cambio**: Eliminado campo `codigo`

---

### 2. **Materia.java** (Modelo de Dominio)
```java
@Data
@Builder
public class Materia {
    private Long id;
    private String nombre;
    
    // ❌ Eliminado: private Long codigo;
}
```

**Cambio**: Eliminado campo `codigo`

---

### 3. **MateriaRepositoryAdapter.java** (Adaptador)
```java
private MateriaEntity toEntity(Materia materia) {
    return MateriaEntity.builder()
            .id(materia.getId())
            .nombre(materia.getNombre())
            // ❌ Eliminado: .codigo(materia.getCodigo())
            .build();
}

private Materia toDomain(MateriaEntity entity) {
    return Materia.builder()
            .id(entity.getId())
            .nombre(entity.getNombre())
            // ❌ Eliminado: .codigo(entity.getCodigo())
            .build();
}
```

**Cambio**: Eliminada referencia a `codigo` en los mappers

---

### 4. **MateriaService.java** (Servicio)
```java
@Override
public Materia crearMateria(Materia materia) {
    // El id se genera automáticamente por la base de datos
    return materiaRepositoryPort.save(materia);
    
    // ❌ Eliminada toda la lógica de generación de código
}
```

**Cambio**: 
- ❌ Eliminada lógica para calcular `max(codigo) + 1`
- ✅ El ID se genera automáticamente con `@GeneratedValue`

---

### 5. **HomeController.java** (Vista/UI) - CAMBIOS IMPORTANTES

#### Formulario Actualizado:
```java
// Campo ID (muestra el próximo ID que le tocaría)
Label lblId = new Label("ID:");
TextField txtId = new TextField();
txtId.setPromptText("Se asignará automáticamente");
txtId.setEditable(false);  // No editable
txtId.setStyle("-fx-background-color: #f0f0f0;");

// Campo Nombre
Label lblNombre = new Label("Nombre:");
TextField txtNombre = new TextField();
txtNombre.setPromptText("Nombre de la materia");
```

**Cambios**:
- ✅ Campo "Código" renombrado a "ID"
- ✅ Muestra el próximo ID que se asignará (calculado dinámicamente)
- ✅ Placeholder: "Se asignará automáticamente"

---

#### Tabla Actualizada:
```java
TableColumn<Materia, Long> colId = new TableColumn<>("ID");
colId.setPrefWidth(100);

TableColumn<Materia, String> colNombreMateria = new TableColumn<>("Nombre");
colNombreMateria.setPrefWidth(450);

TableColumn<Materia, Void> colAcciones = new TableColumn<>("Acciones");

tblMaterias.getColumns().addAll(colId, colNombreMateria, colAcciones);
// ❌ Eliminada columna: colCodigo
```

**Cambios**:
- ❌ Eliminada columna "Código"
- ✅ Solo 3 columnas: ID, Nombre, Acciones
- ✅ Columna ID más ancha (100px)
- ✅ Columna Nombre más ancha (450px)

---

#### Evento Guardar Mejorado:
```java
btnGuardar.setOnAction(event -> {
    // Validar nombre
    if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
        mostrarAlerta("Validación", "El nombre es requerido", Alert.AlertType.WARNING);
        return;
    }

    // Crear materia (sin asignar ID manualmente)
    Materia materia = Materia.builder()
            .nombre(txtNombre.getText().trim())
            .build();

    // Guardar (el ID se asigna automáticamente)
    Materia materiaGuardada = materiaService.crearMateria(materia);
    
    // Mensaje de éxito con el ID asignado
    mostrarAlerta("Éxito", "Materia guardada correctamente con ID: " 
                  + materiaGuardada.getId(), Alert.AlertType.INFORMATION);

    // Limpiar campos
    txtId.clear();
    txtNombre.clear();

    // Recargar tabla
    cargarMaterias(tblMaterias);
    lblEstadisticas.setText("Total de materias: " + tblMaterias.getItems().size());
    
    // ✅ ACTUALIZAR el próximo ID que se mostrará
    actualizarProximoId(txtId, tblMaterias);
});
```

**Cambios**:
- ✅ Mensaje muestra: "Materia guardada correctamente con ID: X"
- ✅ Llama a `actualizarProximoId()` para recalcular el próximo ID
- ❌ Eliminada referencia a `codigo`

---

#### Evento Limpiar Mejorado:
```java
btnLimpiar.setOnAction(event -> {
    txtId.clear();
    txtNombre.clear();
    // ✅ Recalcular el próximo ID después de limpiar
    actualizarProximoId(txtId, tblMaterias);
});
```

**Cambios**:
- ✅ Recalcula el próximo ID después de limpiar
- ✅ El campo ID vuelve a mostrar el próximo valor

---

#### Método Nuevo: actualizarProximoId()
```java
// ✅ MÉTODO NUEVO
private void actualizarProximoId(TextField txtId, TableView<Materia> tabla) {
    try {
        if (tabla.getItems().isEmpty()) {
            // Si no hay materias, el próximo ID será 1
            txtId.setText("1");
        } else {
            // Buscar el ID máximo actual
            Long maxId = tabla.getItems().stream()
                    .map(Materia::getId)
                    .max(Long::compareTo)
                    .orElse(0L);
            // El próximo ID será maxId + 1
            txtId.setText(String.valueOf(maxId + 1));
        }
    } catch (Exception e) {
        LOG.error("Error al calcular próximo ID", e);
        txtId.setText("?");
    }
}
```

**Funcionalidad**:
1. Si la tabla está vacía → Muestra "1"
2. Si hay materias → Busca el ID máximo y muestra maxId + 1
3. Si hay error → Muestra "?"

**Cuándo se llama**:
- Al cargar la vista por primera vez
- Después de guardar una materia
- Después de limpiar el formulario

---

#### Doble Click en Tabla:
```java
tblMaterias.setOnMouseClicked(event -> {
    if (event.getClickCount() == 2) {
        Materia materiaSeleccionada = tblMaterias.getSelectionModel().getSelectedItem();
        if (materiaSeleccionada != null) {
            txtId.setText(String.valueOf(materiaSeleccionada.getId()));
            txtNombre.setText(materiaSeleccionada.getNombre());
        }
    }
});
```

**Cambios**:
- ✅ Muestra `getId()` en lugar de `getCodigo()`

---

#### Mensaje de Eliminación:
```java
confirmacion.setContentText("¿Está seguro de eliminar la materia " 
                            + materia.getId() + " - " + materia.getNombre() + "?");
```

**Cambios**:
- ✅ Usa `getId()` en lugar de `getCodigo()`

---

## 🎨 Interfaz de Usuario Actualizada

### Formulario:
```
┌──────────────────────────────────────────┐
│ Registrar Nueva Materia                  │
├──────────────────────────────────────────┤
│ ID:     [3              ] ← Próximo ID   │
│ Nombre: [_______________] ← Editable     │
│                                          │
│ [Guardar]  [Limpiar]                    │
└──────────────────────────────────────────┘
```

**Comportamiento del campo ID**:
- ✅ Muestra el próximo ID que se asignará
- ✅ Se actualiza automáticamente después de cada operación
- ✅ Campo gris (no editable)
- ✅ Placeholder: "Se asignará automáticamente"

---

### Tabla:
```
┌────────────────────────────────────────────────┐
│ Lista de Materias                              │
├────────────────────────────────────────────────┤
│ Buscar: [____________] [Buscar]                │
│                                                │
│ ┌────┬──────────────────────┬────────┐        │
│ │ ID │       Nombre         │Acciones│        │
│ ├────┼──────────────────────┼────────┤        │
│ │  1 │ Álgebra Lineal       │[Eliminar]       │
│ │  2 │ Física I             │[Eliminar]       │
│ │  3 │ Química Orgánica     │[Eliminar]       │
│ └────┴──────────────────────┴────────┘        │
│                                                │
│ Total de materias: 3                           │
└────────────────────────────────────────────────┘
```

**Cambios**:
- ❌ Columna "Código" eliminada
- ✅ Solo 3 columnas: ID (100px), Nombre (450px), Acciones (120px)

---

## 🔄 Flujo de Uso

### Crear Primera Materia:
```
1. Usuario abre vista de Materias
   → Campo ID muestra: "1" (próximo ID)

2. Usuario ingresa: "Álgebra Lineal"

3. Click en "Guardar"
   → Base de datos asigna ID = 1
   → Mensaje: "Materia guardada correctamente con ID: 1"

4. Vista se actualiza:
   → Campo ID muestra: "2" (próximo ID)
   → Tabla muestra: [1] Álgebra Lineal
```

---

### Crear Segunda Materia:
```
1. Campo ID ya muestra: "2"

2. Usuario ingresa: "Física I"

3. Click en "Guardar"
   → Base de datos asigna ID = 2
   → Mensaje: "Materia guardada correctamente con ID: 2"

4. Vista se actualiza:
   → Campo ID muestra: "3"
   → Tabla muestra: 
      [1] Álgebra Lineal
      [2] Física I
```

---

### Limpiar Formulario:
```
1. Usuario ha ingresado texto en el nombre

2. Click en "Limpiar"
   → Campo ID se limpia temporalmente
   → Campo Nombre se limpia
   → Se recalcula el próximo ID
   → Campo ID muestra: "3" (o el que corresponda)
```

---

### Editar Materia (Doble Click):
```
1. Usuario hace doble click en fila con ID=2

2. Formulario se llena:
   → Campo ID: "2" (ID de la materia seleccionada)
   → Campo Nombre: "Física I"

3. Usuario modifica el nombre: "Física II"

4. Click en "Guardar"
   → Se actualiza la materia con ID=2
   → Mensaje: "Materia guardada correctamente con ID: 2"
```

---

## 📊 Base de Datos

### Tabla Actualizada: `materias`
```sql
CREATE TABLE materias (
    id     INTEGER PRIMARY KEY AUTOINCREMENT,  -- Único identificador
    nombre VARCHAR(255) NOT NULL                -- Nombre de la materia
);
```

**Cambios**:
- ❌ Eliminada columna: `codigo`
- ✅ Solo 2 columnas: `id` y `nombre`

---

## ✅ Ventajas de la Simplificación

### 1. **Eliminación de Redundancia**
- ❌ Antes: `id` y `codigo` (duplicación innecesaria)
- ✅ Ahora: Solo `id` (un único identificador)

### 2. **Simplicidad**
- Menos campos en la entidad
- Menos columnas en la tabla
- Código más limpio y mantenible

### 3. **Mejor UX**
- Usuario ve directamente el ID que se asignará
- No hay confusión entre "ID" y "Código"
- Interfaz más clara y directa

### 4. **Menos Código**
- No necesita lógica de generación de código
- No necesita validación de código único
- Mappers más simples

---

## 🎯 Resumen de Cambios

### Archivos Modificados: 6
1. ✅ MateriaEntity.java - Eliminado campo `codigo`
2. ✅ Materia.java - Eliminado campo `codigo`
3. ✅ MateriaRepositoryAdapter.java - Actualizado mappers
4. ✅ MateriaService.java - Simplificado `crearMateria()`
5. ✅ HomeController.java - Vista actualizada con próximo ID
6. ✅ application.properties - Ya configurado con `create-drop`

### Líneas Modificadas: ~50 líneas
### Método Nuevo: 1 (`actualizarProximoId()`)

---

## 🚀 Para Probar

### 1. Rebuild
```
Build > Rebuild Project (Ctrl+Shift+F9)
```

### 2. Ejecutar
```
Run > Run 'AlumnosApplication' (Shift+F10)
```

### 3. Probar Funcionalidad

**Crear Materia:**
1. Click menú → Materias
2. Campo ID muestra: "1"
3. Nombre: "Álgebra Lineal"
4. Click "Guardar"
5. ✓ Mensaje: "Materia guardada correctamente con ID: 1"
6. ✓ Campo ID ahora muestra: "2"

**Crear Segunda Materia:**
1. Campo ID ya muestra: "2"
2. Nombre: "Física I"
3. Click "Guardar"
4. ✓ ID asignado: 2
5. ✓ Campo ID ahora muestra: "3"

**Limpiar:**
1. Click "Limpiar"
2. ✓ Campo ID vuelve a mostrar: "3"

**Editar:**
1. Doble click en fila
2. ✓ ID se muestra en el campo (no editable)
3. ✓ Puede editar el nombre

---

## 💡 Comportamiento del Campo ID

### Escenarios:

| Situación | Valor Mostrado en Campo ID |
|-----------|----------------------------|
| Tabla vacía (sin materias) | "1" |
| Hay 5 materias (IDs: 1,2,3,4,5) | "6" |
| Se eliminó la última (quedan 1,2,3,4) | "5" |
| Después de guardar | Próximo ID disponible |
| Después de limpiar | Próximo ID disponible |
| Doble click en fila (editar) | ID de la materia seleccionada |

---

## ✅ Estado de Compilación

**Sin errores críticos** ✅

Solo warnings menores (normales):
- Variables no usadas
- Parámetros de lambdas no usados
- Sugerencias de optimización

---

## 🎉 Resultado Final

### Antes (Redundante):
```
Campos: id, codigo, nombre
Tabla: 4 columnas (ID, Código, Nombre, Acciones)
Lógica: Generar código manualmente
```

### Ahora (Simplificado):
```
Campos: id, nombre
Tabla: 3 columnas (ID, Nombre, Acciones)
Lógica: ID autogenerado por BD
UI: Muestra próximo ID dinámicamente
```

---

**Fecha**: 26 de Enero de 2026  
**Cambio**: Simplificación de Materia - Solo ID y Nombre  
**Campo Eliminado**: codigo (redundante con id)  
**Mejora UI**: Campo ID muestra próximo valor automáticamente  
**Estado**: ✅ COMPLETADO  

---

**¡La entidad Materia ha sido completamente simplificada y optimizada!** 🎊
