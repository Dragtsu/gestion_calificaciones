# Campo Cuatrimestre Agregado a Criterios

## Cambios Realizados

Se ha agregado exitosamente el campo **Cuatrimestre** a la entidad **Criterio** con las siguientes modificaciones:

### 1. Entidad de Dominio (Criterio.java)
- ✅ Agregado campo `private Integer cuatrimestre` 
- ✅ Campo permite valores del 1 al 4

### 2. Entidad JPA (CriterioEntity.java)
- ✅ Agregado campo `@Column(name = "cuatrimestre")`
- ✅ La base de datos se actualizará automáticamente al iniciar la aplicación

### 3. Repositorio (CriterioRepositoryAdapter.java)
- ✅ Actualizado mapper `toEntity()` para incluir cuatrimestre
- ✅ Actualizado mapper `toDomain()` para incluir cuatrimestre

### 4. Formulario de Criterios (HomeController.java)
- ✅ Agregado ComboBox para seleccionar cuatrimestre (1, 2, 3, 4)
- ✅ Validación agregada: el cuatrimestre es obligatorio
- ✅ Campo se guarda correctamente al crear/actualizar criterio
- ✅ Campo se carga correctamente al editar un criterio
- ✅ Campo se limpia correctamente al limpiar el formulario

### 5. Tabla de Criterios
- ✅ Agregada columna "Cuatrim." para mostrar el cuatrimestre
- ✅ Columna centrada y con ancho de 80px

## Cómo Probar

1. **Compilar el proyecto en IntelliJ IDEA**:
   - Abrir el proyecto en IntelliJ IDEA
   - Build > Build Project (Ctrl+F9)
   - Run > Run 'AlumnosApplication' (Shift+F10)

2. **O usar Maven** (si está configurado):
   ```powershell
   .\start-app.ps1
   ```

3. **Navegar a Criterios de Evaluación**:
   - En el menú lateral, ir a "Criterios de Evaluación" > "Criterios"
   - Crear un nuevo criterio
   - Observar el nuevo campo "Cuatrimestre" con opciones del 1 al 4
   - Seleccionar un cuatrimestre (obligatorio)
   - Guardar el criterio
   - Verificar que el cuatrimestre aparece en la tabla

4. **Verificar funcionalidad completa**:
   - ✓ Crear criterio con cuatrimestre
   - ✓ Editar criterio y cambiar cuatrimestre
   - ✓ Verificar que el cuatrimestre se muestra en la tabla
   - ✓ Verificar validación: no permite guardar sin cuatrimestre

## Base de Datos

La columna `cuatrimestre` se agregará automáticamente a la tabla `criterios` gracias a la configuración:
```properties
spring.jpa.hibernate.ddl-auto=update
```

No es necesario ejecutar scripts SQL manualmente.

## Estructura del Campo

- **Tipo**: Integer
- **Valores permitidos**: 1, 2, 3, 4
- **Obligatorio**: Sí
- **Posición en formulario**: Después del campo "Materia"
- **Columna en tabla**: "Cuatrim." (centrada, 80px de ancho)

## Estado

✅ **COMPLETADO** - Todos los cambios han sido implementados y están listos para ser probados.

No se generó resumen adicional según lo solicitado.
