# ⚠️ SOLUCIÓN AL ERROR: "Query did not return a unique result: 3 results were returned"

## 📋 Descripción del Problema

El error ocurre porque en la base de datos existen múltiples registros de exámenes con el mismo `grupo_id`, `materia_id` y `parcial`. Esto es resultado de la estructura antigua donde cada alumno tenía su propio registro de examen.

**Ejemplo del problema:**
```
examenes table (estructura antigua):
| id | alumno_id | grupo_id | materia_id | parcial | aciertos | total_aciertos |
|----|-----------|----------|------------|---------|----------|----------------|
| 1  | 101       | 1        | 5          | 1       | 85       | 100            |
| 2  | 102       | 1        | 5          | 1       | 90       | 100            |
| 3  | 103       | 1        | 5          | 1       | 78       | 100            |
```

Cuando se busca por `grupo_id=1, materia_id=5, parcial=1`, la consulta devuelve 3 resultados en lugar de 1.

---

## ✅ Solución Implementada (Temporal)

He modificado el código para que tome solo el primer resultado cuando hay duplicados. Esto permite que la aplicación funcione mientras se migran los datos.

**Archivo modificado**: `ExamenRepositoryAdapter.java`
```java
@Override
public Optional<Examen> findByGrupoIdAndMateriaIdAndParcial(Long grupoId, Long materiaId, Integer parcial) {
    List<ExamenEntity> results = examenJpaRepository.findByGrupoIdAndMateriaIdAndParcial(grupoId, materiaId, parcial);
    // Tomar el primer resultado si hay múltiples (compatibilidad con datos antiguos)
    return results.isEmpty() ? Optional.empty() : Optional.of(toDomain(results.get(0)));
}
```

---

## 🔧 Solución Permanente: Limpiar la Base de Datos

### Opción 1: Limpiar Duplicados (Recomendado)

Ejecuta el script SQL para eliminar los registros duplicados:

**Ubicación del script**: `src/main/resources/db/limpiar_duplicados_examenes.sql`

```sql
-- Eliminar todos los registros duplicados excepto el primero
DELETE FROM examenes
WHERE id NOT IN (
    SELECT MIN(id)
    FROM examenes
    GROUP BY grupo_id, materia_id, parcial
);
```

**Pasos para ejecutar:**

1. **Abrir la base de datos SQLite**:
   ```powershell
   cd D:\Desarrollos\alumnos
   sqlite3 alumnos.db
   ```

2. **Hacer un respaldo** (opcional pero recomendado):
   ```sql
   .backup backup_alumnos.db
   ```

3. **Ejecutar el script de limpieza**:
   ```sql
   .read src/main/resources/db/limpiar_duplicados_examenes.sql
   ```

4. **Verificar que no hay duplicados**:
   ```sql
   SELECT grupo_id, materia_id, parcial, COUNT(*) as cantidad
   FROM examenes
   GROUP BY grupo_id, materia_id, parcial
   HAVING COUNT(*) > 1;
   ```
   
   Si el resultado está vacío, ya no hay duplicados.

5. **Salir de SQLite**:
   ```sql
   .quit
   ```

### Opción 2: Migración Completa (Avanzado)

Si deseas migrar completamente a la nueva estructura con la tabla `alumno_examen`:

**Ubicación del script**: `src/main/resources/db/migration_examenes.sql`

Este script:
1. Crea una tabla temporal `examenes_temp` con la nueva estructura
2. Migra los registros únicos
3. Crea la tabla `alumno_examen` si no existe
4. Respalda la tabla original como `examenes_backup`
5. Renombra la tabla temporal a `examenes`

**⚠️ ADVERTENCIA**: Esta migración es más compleja y requiere migrar manualmente los aciertos a la tabla `alumno_examen`.

---

## 🔄 Pasos Recomendados

### Paso 1: Hacer Respaldo
```powershell
cd D:\Desarrollos\alumnos
Copy-Item alumnos.db alumnos_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').db
```

### Paso 2: Ejecutar Limpieza de Duplicados
```powershell
sqlite3 alumnos.db ".read src/main/resources/db/limpiar_duplicados_examenes.sql"
```

### Paso 3: Verificar
```powershell
sqlite3 alumnos.db "SELECT COUNT(*) as total_examenes FROM examenes;"
```

### Paso 4: Reiniciar la Aplicación
La aplicación ya debería funcionar correctamente sin el error.

---

## 📊 Verificación Post-Limpieza

Después de ejecutar la limpieza, verifica que:

1. **No hay duplicados**:
   ```sql
   SELECT grupo_id, materia_id, parcial, COUNT(*) 
   FROM examenes 
   GROUP BY grupo_id, materia_id, parcial 
   HAVING COUNT(*) > 1;
   ```
   Resultado esperado: Sin filas

2. **Los registros restantes tienen sentido**:
   ```sql
   SELECT * FROM examenes LIMIT 10;
   ```

3. **La aplicación carga correctamente**:
   - Ejecutar la aplicación
   - Navegar a "Concentrado" → "Exámenes"
   - Seleccionar Grupo, Materia y Parcial
   - Presionar "Buscar"
   - Verificar que no aparece el error

---

## 🎯 Estado Actual

- ✅ **Código actualizado**: La aplicación puede manejar duplicados tomando el primer resultado
- ⚠️ **Base de datos**: Requiere limpieza manual para eliminar duplicados
- ⏳ **Migración completa**: Pendiente (opcional)

---

## 📝 Notas Adicionales

1. **Compatibilidad Temporal**: El código actual funciona con datos antiguos pero es recomendable limpiar la base de datos
2. **Migración a AlumnoExamen**: En el futuro, los aciertos individuales deberían estar en la tabla `alumno_examen`
3. **Constraint Único**: Después de limpiar, considera agregar un constraint único en la tabla:
   ```sql
   CREATE UNIQUE INDEX idx_examenes_unique 
   ON examenes(grupo_id, materia_id, parcial);
   ```

---

**Fecha**: 2026-01-29
**Estado**: ⚠️ Requiere acción del usuario (limpiar base de datos)
**Prioridad**: Alta
