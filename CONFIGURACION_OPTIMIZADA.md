# ⚙️ Configuraciones Recomendadas de Optimización

## application.properties - Configuración Optimizada

```properties
# ========================================
# CONFIGURACIÓN DE BASE DE DATOS
# ========================================

spring.datasource.url=jdbc:sqlite:alumnos.db
spring.datasource.driver-class-name=org.sqlite.JDBC

# ========================================
# POOL DE CONEXIONES HIKARI (OPTIMIZADO)
# ========================================

# Número máximo de conexiones en el pool
spring.datasource.hikari.maximum-pool-size=10

# Número mínimo de conexiones inactivas
spring.datasource.hikari.minimum-idle=5

# Tiempo máximo de espera para obtener una conexión (ms)
spring.datasource.hikari.connection-timeout=20000

# Tiempo máximo que una conexión puede estar inactiva (ms)
spring.datasource.hikari.idle-timeout=300000

# Tiempo máximo de vida de una conexión (ms)
spring.datasource.hikari.max-lifetime=1200000

# Validar conexiones antes de usarlas
spring.datasource.hikari.connection-test-query=SELECT 1

# ========================================
# JPA / HIBERNATE
# ========================================

# Dialecto de SQLite
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect

# No actualizar automáticamente el esquema (ya está creado)
spring.jpa.hibernate.ddl-auto=none

# Mostrar SQL en desarrollo (desactivar en producción)
spring.jpa.show-sql=false

# Formatear SQL para mejor legibilidad (solo desarrollo)
spring.jpa.properties.hibernate.format_sql=false

# ========================================
# OPTIMIZACIONES DE HIBERNATE
# ========================================

# Habilitar batch processing para INSERT/UPDATE masivos
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Caché de segundo nivel (opcional - usar con precaución en SQLite)
# spring.jpa.properties.hibernate.cache.use_second_level_cache=true
# spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory

# ========================================
# LOGGING
# ========================================

# Nivel de logging general
logging.level.root=INFO

# Logging de la aplicación
logging.level.com.alumnos=DEBUG

# Logging de Spring (reducir ruido)
logging.level.org.springframework=WARN

# Logging de Hibernate (solo errores en producción)
logging.level.org.hibernate=ERROR

# Logging de SQL (desactivar en producción)
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Formato de logging
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Archivo de logging
logging.file.name=logs/alumnos.log
logging.file.max-size=10MB
logging.file.max-history=30

# ========================================
# JAVAFX
# ========================================

# No hay configuraciones específicas necesarias

# ========================================
# SPRING BOOT
# ========================================

# Nombre de la aplicación
spring.application.name=Alumnos

# Banner de inicio
spring.main.banner-mode=off

# ========================================
# CACHÉ (ACTIVAR DESPUÉS DE CONFIGURAR)
# ========================================

# Tipo de caché
spring.cache.type=simple

# Nombres de cachés
spring.cache.cache-names=materias,grupos,criterios,agregados,alumnos

# ========================================
# PERFORMANCE TUNING
# ========================================

# Deshabilitar DevTools en producción
spring.devtools.restart.enabled=false

# Lazy initialization (reducir tiempo de inicio)
spring.main.lazy-initialization=false

# ========================================
# CONFIGURACIÓN ESPECÍFICA DE PRODUCCIÓN
# ========================================

# Crear archivo application-prod.properties con:
# spring.jpa.show-sql=false
# logging.level.com.alumnos=INFO
# logging.level.org.hibernate.SQL=ERROR
# spring.devtools.restart.enabled=false
```

## application-prod.properties (Producción)

```properties
# ========================================
# CONFIGURACIÓN DE PRODUCCIÓN
# ========================================

# Desactivar SQL logging
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Logging mínimo
logging.level.com.alumnos=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=ERROR
logging.level.org.hibernate.SQL=ERROR

# Desactivar DevTools
spring.devtools.restart.enabled=false

# Pool de conexiones optimizado para producción
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2

# Archivo de log de producción
logging.file.name=logs/alumnos-prod.log
```

## JVM Arguments Recomendados

### Para Desarrollo:
```bash
-Xms256m 
-Xmx512m 
-XX:+UseG1GC 
-XX:MaxGCPauseMillis=200
```

### Para Producción:
```bash
-Xms512m 
-Xmx1024m 
-XX:+UseG1GC 
-XX:MaxGCPauseMillis=100 
-XX:+HeapDumpOnOutOfMemoryError 
-XX:HeapDumpPath=logs/heap-dump.hprof
```

## Configuración de SQLite Recomendada

```sql
-- Ejecutar al inicio de la aplicación para optimizar SQLite
PRAGMA journal_mode = WAL;           -- Write-Ahead Logging para mejor concurrencia
PRAGMA synchronous = NORMAL;         -- Balance entre seguridad y velocidad
PRAGMA cache_size = -64000;          -- 64MB de caché (negativo = KB)
PRAGMA temp_store = MEMORY;          -- Usar memoria para tablas temporales
PRAGMA mmap_size = 30000000000;      -- Memory-mapped I/O (30GB)
PRAGMA page_size = 4096;             -- Tamaño de página óptimo
```

## Monitoreo de Rendimiento

### Métricas a Monitorear:
1. **Tiempo de respuesta de UI** (< 100ms ideal)
2. **Consultas SQL** (< 50ms por query)
3. **Uso de memoria** (< 512MB en producción)
4. **Cache hit ratio** (> 80% ideal)
5. **Tiempo de inicio** (< 5 segundos)

### Herramientas Recomendadas:
- **VisualVM** - Monitoreo de JVM
- **JProfiler** - Profiling avanzado
- **SQLite Browser** - Análisis de base de datos
- **Spring Boot Actuator** (opcional) - Métricas de aplicación

## Checklist de Optimización

- [ ] Aplicar índices a la base de datos (ejecutar optimizacion_indices.sql)
- [ ] Configurar pool de conexiones Hikari
- [ ] Activar configuración de caché (descomentar @EnableCaching)
- [ ] Aplicar @Transactional(readOnly = true) en métodos de consulta
- [ ] Configurar logging apropiadamente
- [ ] Eliminar archivos de respaldo no usados
- [ ] Configurar JVM arguments
- [ ] Ejecutar PRAGMA de optimización SQLite
- [ ] Probar en ambiente de producción
- [ ] Medir métricas de rendimiento

## Notas Importantes

1. **No activar todo a la vez**: Implementar optimizaciones gradualmente
2. **Medir antes y después**: Usar métricas para validar mejoras
3. **Backup**: Siempre hacer backup de la base de datos antes de aplicar cambios
4. **Testing**: Probar exhaustivamente después de cada optimización
5. **Monitoreo**: Establecer monitoreo continuo en producción

## Contacto

Para dudas o problemas con las optimizaciones, consultar:
- INFORME_OPTIMIZACION.md - Análisis detallado
- Logs en carpeta `logs/` - Información de errores
- GitHub Issues - Reportar problemas

---

**Última actualización:** 2026-02-03
