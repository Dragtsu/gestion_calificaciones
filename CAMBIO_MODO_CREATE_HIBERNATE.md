# Cambio: Modo CREATE en Hibernate para Limpiar Base de Datos

## ✅ REVERTIDO - Vuelto a modo UPDATE

**Fecha de reversión:** 8 de febrero de 2026

### Estado Actual
La configuración ahora está en modo **`update`** (normal):
- ✅ Los datos se mantienen entre ejecuciones
- ✅ Solo se actualizan las estructuras de tablas si cambian las entidades
- ✅ No se borran datos

---

## Historial de Cambios

### Cambio Original (Revertido)
Se modificó la configuración de Hibernate de `update` a `create` para limpiar y recrear la base de datos.

## Archivos Modificados

### 1. application.properties
```ini
# ANTES:
spring.jpa.hibernate.ddl-auto=update

# AHORA:
spring.jpa.hibernate.ddl-auto=create
```

### 2. application-prod.properties
```ini
# ANTES:
spring.jpa.hibernate.ddl-auto=update

# AHORA:
spring.jpa.hibernate.ddl-auto=create
```

## ¿Qué significa este cambio?

### Modo `update` (ANTERIOR)
- Mantiene los datos existentes
- Solo actualiza la estructura de las tablas si hay cambios en las entidades
- **NO** elimina datos

### Modo `create` (ACTUAL)
- ⚠️ **ELIMINA todas las tablas existentes**
- ⚠️ **BORRA todos los datos**
- Recrea todas las tablas desde cero según las entidades definidas
- Útil para desarrollo o cuando necesitas resetear completamente la base de datos

## Comportamiento al Iniciar la Aplicación

Cuando ejecutes la aplicación ahora, Hibernate:

1. ✅ **Eliminará** el archivo `alumnos.db` (o eliminará todas las tablas si existe)
2. ✅ **Creará** todas las tablas nuevamente desde cero
3. ✅ **Ejecutará** el `DataInitializer` para insertar la configuración inicial

## ⚠️ IMPORTANTE - PÉRDIDA DE DATOS

Con este cambio:
- 🔴 **TODOS los datos actuales se perderán** cada vez que inicies la aplicación
- 🔴 **Alumnos, grupos, materias, calificaciones, etc.** serán eliminados
- 🔴 Solo permanecerá la configuración inicial que establece el `DataInitializer`

## ¿Cuándo usar cada modo?

| Modo | Uso Recomendado |
|------|-----------------|
| `create` | - Desarrollo inicial<br>- Limpieza completa de datos<br>- Testing<br>- **NO usar en producción** |
| `update` | - Producción<br>- Mantener datos existentes<br>- Actualizar estructura sin perder datos |
| `create-drop` | - Testing automático<br>- Elimina todo al cerrar la app |
| `validate` | - Producción estricta<br>- Solo valida que la estructura coincida |
| `none` | - Control manual completo<br>- Usar scripts SQL propios |

## Volver al Modo Normal (update)

Cuando termines de limpiar la base de datos, **DEBES** cambiar de vuelta a `update`:

### application.properties
```ini
spring.jpa.hibernate.ddl-auto=update
```

### application-prod.properties
```ini
spring.jpa.hibernate.ddl-auto=update
```

## Próximos Pasos

1. **Ejecutar la aplicación** - Esto limpiará la base de datos
2. **Verificar** que se crearon las tablas correctamente
3. **⚠️ IMPORTANTE:** Cambiar de vuelta a `update` para evitar perder datos en futuras ejecuciones

## Archivos Afectados
- `src/main/resources/application.properties`
- `src/main/resources/application-prod.properties`

## Fecha del Cambio
8 de febrero de 2026

---

## 🔴 RECORDATORIO IMPORTANTE

**Este cambio es temporal para limpieza. NO olvides volver a configurar `update` después de ejecutar la aplicación una vez, o perderás todos los datos cada vez que inicies la aplicación.**
