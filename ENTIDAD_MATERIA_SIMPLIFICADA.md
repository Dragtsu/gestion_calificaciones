# ✅ ENTIDAD MATERIA MODIFICADA - Solo Código y Nombre

## 🎯 Cambios Realizados

La entidad **Materia** ha sido simplificada para tener solo **dos campos**:

### Campos Finales:
1. **id** - Long (clave primaria, autoincrementable)
2. **codigo** - Long (autoincrementable, NO EDITABLE)
3. **nombre** - String (editable)

### Campos Eliminados:
- ❌ descripcion (String)
- ❌ creditos (Integer)
- ❌ activa (Boolean)

---

## 📋 Archivos Modificados

### 1. **Materia.java** (Modelo de Dominio)
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Materia {
    private Long id;
    private Long codigo;  // Código autoincrementable (no editable)
    private String nombre;
}
```

**Cambios**:
- ✅ Código cambiado de `String` a `Long`
- ✅ Eliminados: descripcion, creditos, activa

---

### 2. **MateriaEntity.java** (Entidad JPA)
```java
@Entity
@Table(name = "materias")
public class MateriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(nullable = false)
    private String nombre;
}
```

**Cambios**:
- ✅ Código es `Long` con `@GeneratedValue`
- ✅ Eliminados: descripcion, creditos, activa

---

### 3. **MateriaServicePort.java** (Puerto de Entrada)
```java
public interface MateriaServicePort {
    Materia crearMateria(Materia materia);
    Optional<Materia> obtenerMateriaPorId(Long id);
    List<Materia> obtenerTodasLasMaterias();
    Materia actualizarMateria(Materia materia);
    void eliminarMateria(Long id);
    List<Materia> buscarPorNombre(String nombre);
    // ❌ Eliminado: boolean existeCodigo(String codigo);
}
```

**Cambios**:
- ❌ Eliminado método `existeCodigo()` (código es autoincrementable)

---

### 4. **MateriaRepositoryPort.java** (Puerto de Salida)
```java
public interface MateriaRepositoryPort {
    Materia save(Materia materia);
    Optional<Materia> findById(Long id);
    List<Materia> findAll();
    void deleteById(Long id);
    List<Materia> findByNombreContaining(String nombre);
    // ❌ Eliminado: boolean existsByCodigo(String codigo);
}
```

**Cambios**:
- ❌ Eliminado método `existsByCodigo()`

---

### 5. **MateriaService.java** (Servicio)
```java
@Service
@Transactional
public class MateriaService implements MateriaServicePort {
    
    @Override
    public Materia crearMateria(Materia materia) {
        // El código es autoincrementable, no se asigna manualmente
        return materiaRepositoryPort.save(materia);
    }
    
    // ❌ Eliminado: validación de código duplicado
    // ❌ Eliminado: setActiva(true)
    // ❌ Eliminado: método existeCodigo()
}
```

**Cambios**:
- ❌ Eliminada validación de código único
- ❌ Eliminada asignación de `activa = true`
- ❌ Eliminado método `existeCodigo()`

---

### 6. **MateriaJpaRepository.java** (Repositorio JPA)
```java
@Repository
public interface MateriaJpaRepository extends JpaRepository<MateriaEntity, Long> {
    List<MateriaEntity> findByNombreContainingIgnoreCase(String nombre);
    // ❌ Eliminado: boolean existsByCodigo(String codigo);
}
```

**Cambios**:
- ❌ Eliminado método `existsByCodigo()`

---

### 7. **MateriaRepositoryAdapter.java** (Adaptador)
```java
@Component
public class MateriaRepositoryAdapter implements MateriaRepositoryPort {
    
    private MateriaEntity toEntity(Materia materia) {
        return MateriaEntity.builder()
                .id(materia.getId())
                .codigo(materia.getCodigo())
                .nombre(materia.getNombre())
                // ❌ Eliminados: descripcion, creditos, activa
                .build();
    }

    private Materia toDomain(MateriaEntity entity) {
        return Materia.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .nombre(entity.getNombre())
                // ❌ Eliminados: descripcion, creditos, activa
                .build();
    }
    
    // ❌ Eliminado: método existsByCodigo()
}
```

**Cambios**:
- ✅ Mappers actualizados para solo código y nombre
- ❌ Eliminado método `existsByCodigo()`

---

### 8. **HomeController.java** (Vista/UI)

#### Formulario Simplificado:
```java
// Campo Código (solo lectura - autoincrementable)
Label lblCodigo = new Label("Código:");
TextField txtCodigo = new TextField();
txtCodigo.setPromptText("Autoincrementable");
txtCodigo.setPrefWidth(150);
txtCodigo.setEditable(false);  // ✅ NO EDITABLE
txtCodigo.setStyle("-fx-background-color: #f0f0f0;");  // Gris claro

// Campo Nombre (editable)
Label lblNombre = new Label("Nombre:");
TextField txtNombre = new TextField();
txtNombre.setPromptText("Nombre de la materia");
txtNombre.setPrefWidth(300);

// ❌ Eliminados: txtDescripcion, txtCreditos
```

**Cambios en UI**:
- ✅ Campo Código: No editable, fondo gris, placeholder "Autoincrementable"
- ✅ Solo campo Nombre es editable
- ❌ Eliminados campos: Descripción, Créditos

---

#### Tabla Simplificada:
```java
TableColumn<Materia, Long> colId = new TableColumn<>("ID");
TableColumn<Materia, Long> colCodigo = new TableColumn<>("Código");  // ✅ Tipo Long
TableColumn<Materia, String> colNombreMateria = new TableColumn<>("Nombre");
TableColumn<Materia, Void> colAcciones = new TableColumn<>("Acciones");

tblMaterias.getColumns().addAll(colId, colCodigo, colNombreMateria, colAcciones);
// ❌ Eliminadas columnas: colDescripcion, colCreditos, colActiva
```

**Cambios en Tabla**:
- ✅ Columna Código ahora es `TableColumn<Materia, Long>`
- ✅ Solo 4 columnas: ID, Código, Nombre, Acciones
- ❌ Eliminadas: Descripción, Créditos, Activa

---

#### Evento Guardar Simplificado:
```java
btnGuardar.setOnAction(event -> {
    // Solo validar nombre
    if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
        mostrarAlerta("Validación", "El nombre es requerido", Alert.AlertType.WARNING);
        return;
    }

    Materia materia = Materia.builder()
            .nombre(txtNombre.getText().trim())
            // ❌ NO se asigna código (es autoincrementable)
            .build();

    Materia materiaGuardada = materiaService.crearMateria(materia);
    mostrarAlerta("Éxito", "Materia guardada correctamente con código: " 
                  + materiaGuardada.getCodigo(), Alert.AlertType.INFORMATION);
    
    // Limpiar solo 2 campos
    txtCodigo.clear();
    txtNombre.clear();
});
```

**Cambios en Guardar**:
- ❌ Eliminada validación de código
- ❌ Eliminada validación de créditos
- ✅ Solo se valida y guarda el nombre
- ✅ Código se genera automáticamente
- ✅ Mensaje muestra el código autogenerado

---

#### Evento Limpiar Simplificado:
```java
btnLimpiar.setOnAction(event -> {
    txtCodigo.clear();
    txtNombre.clear();
    // ❌ Eliminados: txtDescripcion.clear(), txtCreditos.clear()
});
```

---

#### Doble Click en Tabla:
```java
tblMaterias.setOnMouseClicked(event -> {
    if (event.getClickCount() == 2) {
        Materia materiaSeleccionada = tblMaterias.getSelectionModel().getSelectedItem();
        if (materiaSeleccionada != null) {
            txtCodigo.setText(String.valueOf(materiaSeleccionada.getCodigo()));  // ✅ Long a String
            txtNombre.setText(materiaSeleccionada.getNombre());
            // ❌ Eliminados: setTexts para descripcion y creditos
        }
    }
});
```

**Cambios**:
- ✅ Código se muestra como String (conversión de Long)
- ✅ Solo se cargan código (no editable) y nombre

---

## 🎨 Interfaz de Usuario Actualizada

### Formulario:
```
┌──────────────────────────────────────────┐
│ Registrar Nueva Materia                  │
├──────────────────────────────────────────┤
│ Código:  [Autoincrementable    ] ← GRIS │
│ Nombre:  [___________________] ← EDITABLE│
│                                          │
│ [Guardar]  [Limpiar]                    │
└──────────────────────────────────────────┘
```

### Tabla:
```
┌──────────────────────────────────────────────────┐
│ Lista de Materias                                │
├──────────────────────────────────────────────────┤
│ Buscar: [____________] [Buscar]                  │
│                                                  │
│ ┌──┬────────┬─────────────────┬────────┐        │
│ │ID│ Código │     Nombre      │Acciones│        │
│ ├──┼────────┼─────────────────┼────────┤        │
│ │1 │   1    │Álgebra Lineal   │[Eliminar]       │
│ │2 │   2    │Física I         │[Eliminar]       │
│ │3 │   3    │Química Orgánica │[Eliminar]       │
│ └──┴────────┴─────────────────┴────────┘        │
│                                                  │
│ Total de materias: 3                             │
└──────────────────────────────────────────────────┘
```

---

## 🔧 Funcionalidad Actualizada

### Crear Materia:
1. Usuario ingresa **solo el Nombre**
2. Click en "Guardar"
3. ✅ Sistema genera **código automáticamente**
4. ✅ Mensaje: "Materia guardada correctamente con código: 1"
5. ✅ Aparece en la tabla con código autogenerado

### Editar Materia:
1. Doble clic en una fila
2. ✅ Código se muestra (campo gris, no editable)
3. ✅ Nombre se carga para editar
4. Usuario modifica el nombre
5. Click en "Guardar"
6. ✅ Solo el nombre se actualiza (código permanece igual)

### Eliminar Materia:
1. Click en "Eliminar"
2. Confirmación: "¿Está seguro de eliminar la materia 1 - Álgebra Lineal?"
3. ✅ Se elimina de la base de datos

### Buscar Materia:
1. Escribir en campo de búsqueda
2. Click en "Buscar"
3. ✅ Filtra por nombre (funciona igual)

---

## 📊 Base de Datos

### Tabla Actualizada: `materias`
```sql
CREATE TABLE materias (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,  -- PK autoincrementable
    codigo  INTEGER UNIQUE NOT NULL,            -- Autoincrementable, único
    nombre  VARCHAR(255) NOT NULL               -- Nombre de la materia
);
```

**Cambios en BD**:
- ❌ Eliminadas columnas: `descripcion`, `creditos`, `activa`
- ✅ Código ahora es `INTEGER` (Long en Java)
- ✅ Código es autoincrementable

---

## ✅ Validaciones Actualizadas

| Campo | Validación Anterior | Validación Actual |
|-------|-------------------|-------------------|
| Código | Requerido, único (String) | ❌ NO validado (autoincrementable) |
| Nombre | Requerido | ✅ Requerido |
| Descripción | Opcional | ❌ Eliminado |
| Créditos | Requerido, solo números | ❌ Eliminado |
| Activa | Auto true | ❌ Eliminado |

---

## 🎯 Resumen de Cambios

### Archivos Modificados: 8
1. ✅ Materia.java
2. ✅ MateriaEntity.java
3. ✅ MateriaServicePort.java
4. ✅ MateriaRepositoryPort.java
5. ✅ MateriaService.java
6. ✅ MateriaJpaRepository.java
7. ✅ MateriaRepositoryAdapter.java
8. ✅ HomeController.java

### Líneas Eliminadas: ~150 líneas
### Líneas Modificadas: ~80 líneas

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
1. Click en menú → Materias
2. Dejar código vacío (campo gris)
3. Ingresar nombre: "Álgebra Lineal"
4. Click "Guardar"
5. ✓ Debe mostrar: "Materia guardada correctamente con código: 1"
6. ✓ Aparece en tabla con código = 1

**Crear Segunda Materia:**
1. Nombre: "Física I"
2. Click "Guardar"
3. ✓ Código = 2 (autoincrementado)

**Editar Materia:**
1. Doble clic en fila
2. ✓ Código aparece en gris (no editable)
3. Modificar nombre
4. Click "Guardar"
5. ✓ Solo nombre se actualiza

---

## 💡 Características Clave

### Código Autoincrementable:
- ✅ Se genera automáticamente
- ✅ Usuario NO puede editarlo
- ✅ Campo gris indica que no es editable
- ✅ Único y secuencial (1, 2, 3, ...)

### Simplicidad:
- ✅ Solo 2 campos esenciales
- ✅ Formulario más simple
- ✅ Menos validaciones
- ✅ Más rápido de usar

---

**Fecha**: 26 de Enero de 2026  
**Cambio**: Simplificación entidad Materia  
**Campos**: Solo código (autoincrementable) y nombre  
**Estado**: ✅ COMPLETADO  

---

**¡La entidad Materia ha sido simplificada exitosamente!** 🎊
