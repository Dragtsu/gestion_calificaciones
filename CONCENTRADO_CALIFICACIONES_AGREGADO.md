# ✅ Nueva Entrada de Menú: Concentrado de Calificaciones

## 📋 Resumen
Se ha agregado una nueva entrada en el menú principal llamada "Concentrado de calificaciones" que permitirá generar un reporte consolidado de las calificaciones de los alumnos por grupo.

## 🔧 Cambios Realizados

### 1. Archivo FXML (`home.fxml`)
- ✅ Agregado botón de menú "Concentrado de calificaciones"
- ✅ Icono: 📋
- ✅ Ubicación: Entre "Criterios de Evaluación" y el separador antes de "Configuración"
- ✅ Handler: `handleMenuConcentrado`

### 2. HomeController.java

#### Variables Agregadas:
```java
private VBox vistaConcentrado;
```

#### Métodos Agregados:

**Handler del menú:**
```java
@FXML
private void handleMenuConcentrado() {
    lblTitulo.setText("Concentrado de calificaciones - Sistema de Gestión");
    mostrarVista("concentrado");
    toggleMenu();
}
```

**Creación de la vista:**
```java
private VBox crearVistaConcentradoCompleta() {
    // Vista con:
    // - Filtro por grupo (ComboBox)
    // - Botón "Generar Concentrado"
    // - Tabla para mostrar resultados
}
```

#### Métodos Actualizados:

**`crearTodasLasVistas()`:**
- ✅ Agregada creación de `vistaConcentrado`
- ✅ Agregada al contenedor de vistas
- ✅ Agregado manejo de errores para la nueva vista

**`mostrarVista()`:**
- ✅ Agregada validación de `vistaConcentrado`
- ✅ Agregado ocultamiento de `vistaConcentrado` al cambiar de vista
- ✅ Agregado caso "concentrado" en el switch

## 📊 Funcionalidad Actual

### Implementado:
- ✅ Entrada de menú funcionando
- ✅ Vista básica con interfaz de usuario
- ✅ Filtro por grupo
- ✅ Placeholder para la tabla de resultados

### Pendiente de Implementar:
- ⏳ Lógica para obtener alumnos del grupo seleccionado
- ⏳ Lógica para obtener materias del grupo
- ⏳ Lógica para obtener criterios y agregados por materia
- ⏳ Cálculo de calificaciones por criterio/agregado
- ⏳ Generación de columnas dinámicas en la tabla
- ⏳ Cálculo de promedios y totales
- ⏳ Opción de exportar a Excel/PDF

## 🎯 Próximos Pasos

1. **Definir estructura de datos** para el concentrado:
   - Modelo de datos que represente: Alumno + Materias + Criterios + Calificaciones

2. **Implementar lógica de generación**:
   - Obtener datos del grupo seleccionado
   - Calcular calificaciones
   - Generar columnas dinámicas

3. **Mejorar la interfaz**:
   - Agregar más filtros (por materia, por cuatrimestre)
   - Agregar opciones de exportación
   - Agregar estadísticas (promedios, máximos, mínimos)

4. **Agregar funcionalidades avanzadas**:
   - Selección de múltiples grupos
   - Comparación entre grupos
   - Gráficos de rendimiento

## 📝 Notas Técnicas

- La vista se carga dinámicamente al inicializar la aplicación
- El ComboBox de grupos se llena con todos los grupos existentes
- La tabla está configurada con un placeholder mientras no hay datos
- El botón "Generar" valida que se haya seleccionado un grupo antes de proceder

## 🔄 Commit
```
Se agrega nueva entrada de menú "Concentrado de calificaciones"
```

---
**Fecha de creación:** 2026-01-27
**Autor:** Sistema de Gestión de Alumnos
