# ✅ ENTRADA DE MENÚ "EXÁMENES" AGREGADA

## 📋 Resumen

Se ha agregado una nueva entrada de menú llamada **"Exámenes"** dentro del menú padre **"Concentrado"**. Esta vista permite desplegar una tabla con el listado de alumnos utilizando tres filtros obligatorios: Grupo, Materia y Parcial.

---

## 🎯 Funcionalidad Implementada

### Menú
- **Entrada de menú**: "Exámenes" 
- **Ubicación**: Dentro del submenú "Concentrado"
- **Ícono**: 📝

### Filtros Obligatorios
1. **Grupo**: ComboBox que lista todos los grupos disponibles
2. **Materia**: ComboBox que se habilita al seleccionar un grupo y lista las materias asignadas
3. **Parcial**: ComboBox con opciones 1, 2, 3

### Tabla de Alumnos
Muestra las siguientes columnas:
- ID
- Matrícula
- Nombre
- Apellido Paterno
- Apellido Materno

Los alumnos se ordenan alfabéticamente por nombre completo.

---

## 📝 Archivos Modificados

### 1. `home.fxml`

**Cambio realizado**:
- Agregado el botón "Exámenes" dentro del submenú de "Concentrado"

```xml
<!-- Submenú de Concentrado -->
<VBox fx:id="submenuConcentrado" visible="false" managed="false" spacing="0" style="-fx-background-color: #1a252a;">
    <!-- Concentrado de calificaciones -->
    <Button fx:id="btnMenuConcentradoCalificaciones" text="Concentrado de calificaciones" onAction="#handleMenuConcentrado"
            styleClass="menu-item" maxWidth="Infinity"
            style="-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 15 30 15 60; -fx-font-size: 14px; -fx-cursor: hand;">
        <graphic>
            <Label text="📊" style="-fx-font-size: 16px; -fx-text-fill: white;"/>
        </graphic>
    </Button>

    <!-- Exámenes -->
    <Button fx:id="btnMenuExamenes" text="Exámenes" onAction="#handleMenuExamenes"
            styleClass="menu-item" maxWidth="Infinity"
            style="-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 15 30 15 60; -fx-font-size: 14px; -fx-cursor: hand;">
        <graphic>
            <Label text="📝" style="-fx-font-size: 16px; -fx-text-fill: white;"/>
        </graphic>
    </Button>
</VBox>
```

---

### 2. `HomeController.java`

#### Cambios realizados:

##### A. Campo agregado
```java
private VBox vistaExamenes;
```

##### B. Método handler agregado
```java
@FXML
private void handleMenuExamenes() {
    lblTitulo.setText("Exámenes - Sistema de Gestión");
    mostrarVista("examenes");
    toggleMenu();
}
```

##### C. Actualización del método `mostrarVista()`
- Agregada validación de `vistaExamenes`
- Agregada ocultación de `vistaExamenes`
- Agregado caso "examenes" en el switch

```java
case "examenes":
    vistaExamenes.setVisible(true);
    vistaExamenes.toFront();
    break;
```

##### D. Actualización del método `crearTodasLasVistas()`
- Agregada creación de `vistaExamenes`
- Agregado al contenedor de vistas
- Agregado en las validaciones y manejo de errores

```java
// Crear vista de exámenes
vistaExamenes = crearVistaExamenesCompleta();
if (vistaExamenes != null) {
    vistaExamenes.setVisible(false); // Inicialmente oculta
} else {
    LOG.error("Error: vistaExamenes es null");
}
```

##### E. Método nuevo: `crearVistaExamenesCompleta()`
Este método crea la interfaz completa de la vista de exámenes con:
- **Header**: Título "Exámenes"
- **Panel de filtros**: 
  - ComboBox Grupo (obligatorio)
  - ComboBox Materia (obligatorio, se habilita al seleccionar grupo)
  - ComboBox Parcial (obligatorio, valores: 1, 2, 3)
  - Botón "Generar Tabla"
- **Panel de tabla**: 
  - TableView con columnas: ID, Matrícula, Nombre, Apellido Paterno, Apellido Materno
  - ScrollPane para navegación
- **Lógica**:
  - Carga materias dinámicamente según el grupo seleccionado
  - Valida que todos los filtros estén seleccionados antes de generar la tabla
  - Obtiene alumnos del grupo y los ordena alfabéticamente
  - Muestra alertas de validación y errores

---

## 🏗️ Estructura del Menú Resultante

```
📋 Concentrado (Menú padre)
  ├── 📊 Concentrado de calificaciones
  └── 📝 Exámenes (NUEVO)
```

---

## 🔄 Flujo de Uso

1. Usuario hace clic en "Concentrado" → Se despliega el submenú
2. Usuario hace clic en "Exámenes" → Se muestra la vista de exámenes
3. Usuario selecciona un **Grupo** → Se habilita el ComboBox de materias
4. Usuario selecciona una **Materia**
5. Usuario selecciona un **Parcial**
6. Usuario hace clic en **"Generar Tabla"** → Se carga la lista de alumnos del grupo seleccionado

---

## 📊 Arquitectura

```
┌─────────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN                │
│                                             │
│  HomeController                             │
│  - handleMenuExamenes()                     │
│  - crearVistaExamenesCompleta()             │
│  - mostrarVista("examenes")                 │
└──────────────────┬──────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────┐
│         CAPA DE APLICACIÓN                  │
│                                             │
│  AlumnoService                              │
│  - obtenerAlumnosPorGrupo()                 │
│                                             │
│  GrupoService                               │
│  - obtenerTodosLosGrupos()                  │
│                                             │
│  GrupoMateriaService                        │
│  - obtenerMateriasPorGrupo()                │
└──────────────────┬──────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────┐
│         CAPA DE DOMINIO                     │
│                                             │
│  Entidades:                                 │
│  - Alumno                                   │
│  - Grupo                                    │
│  - Materia                                  │
└─────────────────────────────────────────────┘
```

---

## ✅ Estado de Implementación

| Componente | Estado | Descripción |
|------------|--------|-------------|
| Entrada de menú FXML | ✅ Completo | Botón "Exámenes" en submenú Concentrado |
| Handler `handleMenuExamenes()` | ✅ Completo | Maneja la navegación a la vista |
| Vista `vistaExamenes` | ✅ Completo | Declarada e inicializada |
| Método `crearVistaExamenesCompleta()` | ✅ Completo | Crea la interfaz completa |
| Integración en `mostrarVista()` | ✅ Completo | Caso "examenes" agregado |
| Filtros obligatorios | ✅ Completo | Grupo, Materia, Parcial |
| Tabla de alumnos | ✅ Completo | 5 columnas con datos de alumno |
| Validaciones | ✅ Completo | Alertas y manejo de errores |
| Carga dinámica de materias | ✅ Completo | Según grupo seleccionado |
| Ordenamiento alfabético | ✅ Completo | Por nombre completo |

---

## 🚀 Próximos Pasos (Opcionales)

1. **Agregar columnas adicionales** a la tabla según necesidades:
   - Calificaciones por parcial
   - Promedio
   - Estado (Aprobado/Reprobado)

2. **Agregar funcionalidad de exportación**:
   - Exportar a Excel
   - Exportar a PDF

3. **Agregar filtros adicionales**:
   - Búsqueda por nombre/matrícula
   - Filtro por estado

4. **Persistencia de selección**:
   - Recordar última selección de filtros

---

## 📅 Fecha de Implementación

**Fecha**: 28 de enero de 2026  
**Estado**: ✅ Completado exitosamente

---

## 👨‍💻 Notas Técnicas

- La vista utiliza el mismo patrón de diseño que otras vistas del sistema
- Se reutilizan los servicios existentes (`AlumnoService`, `GrupoService`, `GrupoMateriaService`)
- El código sigue las convenciones de arquitectura limpia del proyecto
- Se incluye logging para depuración y seguimiento
- Manejo robusto de errores con try-catch y alertas al usuario

---

## ⚠️ Consideraciones

- Los tres filtros (Grupo, Materia, Parcial) son **obligatorios**
- La tabla no se genera hasta que los tres filtros estén seleccionados
- Las materias disponibles dependen del grupo seleccionado
- Los alumnos se muestran en orden alfabético por nombre completo

---

**FIN DEL DOCUMENTO**
