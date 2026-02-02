# Scripts de Limpieza de Base de Datos

Este directorio contiene scripts SQL para limpiar la base de datos de la aplicación de alumnos.

## Scripts Disponibles

### 1. `limpiar_base_datos_produccion.sql`
**Limpieza conservando la estructura**

Este script elimina todos los datos de las tablas pero mantiene la estructura completa de la base de datos.

**Características:**
- ✓ Elimina todos los datos de todas las tablas
- ✓ Mantiene la estructura de tablas intacta
- ✓ Reinicia los contadores de autoincremento
- ✓ Restablece la configuración inicial
- ✓ Verifica la integridad de la base de datos
- ✓ Optimiza la base de datos con VACUUM

**Cuándo usar:**
- Antes de desplegar en producción
- Para limpiar datos de prueba
- Cuando necesitas empezar con datos frescos pero mantener la estructura

**Uso:**
```bash
# Desde la raíz del proyecto
sqlite3 alumnos.db < src/main/resources/db/limpiar_base_datos_produccion.sql
```

### 2. `limpiar_completo.sql`
**Limpieza total y recreación**

Este script elimina completamente todas las tablas y las recrea desde cero.

**Características:**
- ✓ Elimina todas las tablas existentes (DROP)
- ✓ Recrea toda la estructura desde cero
- ✓ Inserta configuración inicial
- ✓ Verifica integridad y optimiza

**Cuándo usar:**
- Cuando necesitas recrear la base de datos desde cero
- Si hay problemas de corrupción en la estructura
- Para aplicar cambios estructurales importantes

**Uso:**
```bash
# Desde la raíz del proyecto
sqlite3 alumnos.db < src/main/resources/db/limpiar_completo.sql
```

### 3. `limpiar_duplicados_examenes.sql`
Script específico para eliminar exámenes duplicados.

## Scripts de PowerShell y BAT

### `limpiar-db.ps1`
Script de PowerShell con funcionalidades avanzadas.

**Características:**
- ✓ Crea respaldo automático antes de limpiar
- ✓ Múltiples tipos de limpieza
- ✓ Confirmación antes de ejecutar
- ✓ Restauración automática en caso de error
- ✓ Estadísticas post-limpieza

**Uso:**
```powershell
# Limpieza de producción (por defecto)
.\limpiar-db.ps1

# Limpieza completa
.\limpiar-db.ps1 -Tipo completo

# Especificar base de datos
.\limpiar-db.ps1 -DatabasePath "ruta/a/base.db"
```

**Parámetros:**
- `-Tipo`: Tipo de limpieza (`produccion`, `completo`, `test`)
- `-DatabasePath`: Ruta a la base de datos (por defecto: `alumnos.db`)

### `limpiar-db.bat`
Script BAT para ejecutar desde Windows.

**Uso:**
```batch
REM Limpieza de producción
limpiar-db.bat

REM Limpieza completa
limpiar-db.bat completo
```

## Proceso de Limpieza Paso a Paso

### Limpieza de Producción

1. **Deshabilitar restricciones de clave foránea**
   ```sql
   PRAGMA foreign_keys = OFF;
   ```

2. **Eliminar datos en orden (respetando dependencias):**
   - Calificaciones de concentrado
   - Relaciones alumno-examen
   - Exámenes
   - Calificaciones individuales
   - Agregados
   - Criterios
   - Relaciones grupo-materia
   - Alumnos
   - Grupos
   - Materias
   - Configuración

3. **Reiniciar secuencias de autoincremento**
   ```sql
   DELETE FROM sqlite_sequence WHERE name = 'tabla';
   ```

4. **Restaurar configuración inicial**
   ```sql
   INSERT INTO configuracion (id, nombre_maestro)
   VALUES (1, 'Sin configurar');
   ```

5. **Reactivar restricciones**
   ```sql
   PRAGMA foreign_keys = ON;
   ```

6. **Verificar integridad**
   ```sql
   PRAGMA integrity_check;
   ```

7. **Optimizar**
   ```sql
   VACUUM;
   ```

## Recomendaciones de Seguridad

### ⚠️ IMPORTANTE: Siempre crear respaldo

Antes de ejecutar cualquier script de limpieza:

```bash
# Windows PowerShell
Copy-Item alumnos.db alumnos_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').db

# Comando directo
copy alumnos.db alumnos_backup.db
```

### Verificación post-limpieza

Después de limpiar, verificar que la base de datos está correcta:

```sql
-- Verificar integridad
PRAGMA integrity_check;

-- Contar registros en tablas principales
SELECT 'alumnos' as tabla, COUNT(*) as registros FROM alumnos
UNION ALL
SELECT 'grupos', COUNT(*) FROM grupos
UNION ALL
SELECT 'materias', COUNT(*) FROM materias;
```

## Flujo de Trabajo Recomendado

### Para Desarrollo → Producción

1. **Crear respaldo de producción actual** (si existe)
   ```bash
   copy alumnos.db alumnos_produccion_backup.db
   ```

2. **Ejecutar script de limpieza**
   ```bash
   .\limpiar-db.ps1 -Tipo produccion
   ```

3. **Verificar que la aplicación inicia correctamente**

4. **Configurar datos iniciales:**
   - Nombre del maestro
   - Grupos escolares
   - Materias
   - Asignar materias a grupos

### Para Desarrollo/Testing

1. **Usar el script de PowerShell**
   ```bash
   .\limpiar-db.ps1 -Tipo test -DatabasePath "test.db"
   ```

2. **O ejecutar directamente el SQL**
   ```bash
   sqlite3 test.db < src/main/resources/db/limpiar_base_datos_produccion.sql
   ```

## Solución de Problemas

### Error: "FOREIGN KEY constraint failed"

Si aparece este error, asegúrate de que:
1. Las restricciones de clave foránea están deshabilitadas al inicio
2. Las tablas se eliminan en el orden correcto (de dependientes a principales)

**Solución:**
```sql
PRAGMA foreign_keys = OFF;
-- Ejecutar las operaciones
PRAGMA foreign_keys = ON;
```

### Error: "database is locked"

La base de datos está siendo usada por otra aplicación.

**Solución:**
1. Cerrar la aplicación de alumnos
2. Cerrar cualquier cliente SQLite que tenga abierta la base de datos
3. Intentar nuevamente

### Error al ejecutar PowerShell

**Error:** "no se pueden cargar archivos de script"

**Solución:**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

## Integración con la Aplicación

Los scripts están ubicados en `src/main/resources/db/` para que puedan ser:

1. **Ejecutados manualmente** desde línea de comandos
2. **Incluidos en el JAR** de la aplicación
3. **Ejecutados programáticamente** desde Java si es necesario

## Mantenimiento

### Actualizar scripts al cambiar la estructura

Si se agregan o modifican tablas, actualizar:
1. ✓ Orden de eliminación de datos en `limpiar_base_datos_produccion.sql`
2. ✓ Definiciones de tablas en `limpiar_completo.sql`
3. ✓ Lista de secuencias a reiniciar
4. ✓ Lista de tablas en el script de PowerShell (para estadísticas)

### Versionamiento

Considera mantener versiones de los scripts si realizas cambios importantes:
- `limpiar_base_datos_produccion_v1.sql`
- `limpiar_base_datos_produccion_v2.sql`

## Contacto y Soporte

Si encuentras problemas con los scripts de limpieza:
1. Verifica que tienes un respaldo de la base de datos
2. Revisa los mensajes de error en detalle
3. Consulta la sección de solución de problemas
4. Verifica la estructura de tablas actual vs. la esperada

---

**Última actualización:** 2026-02-02
**Versión de scripts:** 1.0
