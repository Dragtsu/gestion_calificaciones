-- ========================================
-- SCRIPT DE OPTIMIZACIÓN DE BASE DE DATOS
-- Índices para mejorar rendimiento de consultas
-- ========================================

-- IMPORTANTE: Ejecutar este script una sola vez
-- Este script agrega índices para acelerar las consultas más frecuentes

-- ========================================
-- TABLA: alumnos
-- ========================================

-- Índice para búsquedas por grupo (muy frecuente)
CREATE INDEX IF NOT EXISTS idx_alumno_grupo
ON alumnos(grupo_id);

-- Índice para búsquedas por nombre completo (usado en filtros)
CREATE INDEX IF NOT EXISTS idx_alumno_nombre
ON alumnos(nombre, apellido_paterno, apellido_materno);

-- Índice para ordenamiento por número de lista
CREATE INDEX IF NOT EXISTS idx_alumno_numero_lista
ON alumnos(grupo_id, numero_lista);

-- ========================================
-- TABLA: criterios
-- ========================================

-- Índice para búsquedas por materia (muy frecuente)
CREATE INDEX IF NOT EXISTS idx_criterio_materia
ON criterios(materia_id);

-- Índice para búsquedas por parcial (usado en filtros)
CREATE INDEX IF NOT EXISTS idx_criterio_parcial
ON criterios(parcial);

-- Índice compuesto para búsquedas por materia y parcial
CREATE INDEX IF NOT EXISTS idx_criterio_materia_parcial
ON criterios(materia_id, parcial);

-- Índice para ordenamiento
CREATE INDEX IF NOT EXISTS idx_criterio_orden
ON criterios(materia_id, parcial, orden);

-- ========================================
-- TABLA: agregados
-- ========================================

-- Índice para búsquedas por criterio (muy frecuente)
CREATE INDEX IF NOT EXISTS idx_agregado_criterio
ON agregados(criterio_id);

-- Índice para ordenamiento
CREATE INDEX IF NOT EXISTS idx_agregado_orden
ON agregados(criterio_id, orden);

-- ========================================
-- TABLA: calificaciones
-- ========================================

-- Índice para búsquedas por alumno (muy frecuente)
CREATE INDEX IF NOT EXISTS idx_calificacion_alumno
ON calificaciones(alumno_id);

-- Índice para búsquedas por agregado (frecuente)
CREATE INDEX IF NOT EXISTS idx_calificacion_agregado
ON calificaciones(agregado_id);

-- Índice compuesto para evitar duplicados y acelerar búsquedas
CREATE UNIQUE INDEX IF NOT EXISTS idx_calificacion_alumno_agregado
ON calificaciones(alumno_id, agregado_id);

-- ========================================
-- TABLA: calificaciones_concentrado
-- ========================================

-- Índice para búsquedas por alumno
CREATE INDEX IF NOT EXISTS idx_calificacion_conc_alumno
ON calificaciones_concentrado(alumno_id);

-- Índice para búsquedas por grupo (usado en reportes)
CREATE INDEX IF NOT EXISTS idx_calificacion_conc_grupo
ON calificaciones_concentrado(grupo_id);

-- Índice para búsquedas por materia
CREATE INDEX IF NOT EXISTS idx_calificacion_conc_materia
ON calificaciones_concentrado(materia_id);

-- Índice para búsquedas por parcial
CREATE INDEX IF NOT EXISTS idx_calificacion_conc_parcial
ON calificaciones_concentrado(parcial);

-- Índice compuesto para reportes (grupo + materia + parcial)
CREATE INDEX IF NOT EXISTS idx_calificacion_conc_reporte
ON calificaciones_concentrado(grupo_id, materia_id, parcial);

-- ========================================
-- TABLA: examenes
-- ========================================

-- Índice para búsquedas por grupo (frecuente)
CREATE INDEX IF NOT EXISTS idx_examen_grupo
ON examenes(grupo_id);

-- Índice para búsquedas por materia (frecuente)
CREATE INDEX IF NOT EXISTS idx_examen_materia
ON examenes(materia_id);

-- Índice para búsquedas por parcial
CREATE INDEX IF NOT EXISTS idx_examen_parcial
ON examenes(parcial);

-- Índice compuesto para evitar duplicados
CREATE UNIQUE INDEX IF NOT EXISTS idx_examen_grupo_materia_parcial
ON examenes(grupo_id, materia_id, parcial);

-- ========================================
-- TABLA: alumno_examenes
-- ========================================

-- Índice para búsquedas por examen
CREATE INDEX IF NOT EXISTS idx_alumno_examen_examen
ON alumno_examenes(examen_id);

-- Índice para búsquedas por alumno
CREATE INDEX IF NOT EXISTS idx_alumno_examen_alumno
ON alumno_examenes(alumno_id);

-- Índice compuesto para evitar duplicados
CREATE UNIQUE INDEX IF NOT EXISTS idx_alumno_examen_unico
ON alumno_examenes(alumno_id, examen_id);

-- ========================================
-- TABLA: grupos_materias
-- ========================================

-- Índice para búsquedas por grupo
CREATE INDEX IF NOT EXISTS idx_grupo_materia_grupo
ON grupos_materias(grupo_id);

-- Índice para búsquedas por materia
CREATE INDEX IF NOT EXISTS idx_grupo_materia_materia
ON grupos_materias(materia_id);

-- Índice compuesto para evitar duplicados
CREATE UNIQUE INDEX IF NOT EXISTS idx_grupo_materia_unico
ON grupos_materias(grupo_id, materia_id);

-- ========================================
-- VERIFICACIÓN DE ÍNDICES
-- ========================================

-- Consulta para ver todos los índices creados
SELECT
    name as indice,
    tbl_name as tabla,
    sql
FROM
    sqlite_master
WHERE
    type = 'index'
    AND name LIKE 'idx_%'
ORDER BY
    tbl_name, name;

-- ========================================
-- ESTADÍSTICAS Y ANÁLISIS
-- ========================================

-- Habilitar análisis de consultas (útil para debugging)
-- PRAGMA query_only = ON;

-- Mostrar plan de ejecución de una consulta (ejemplo)
-- EXPLAIN QUERY PLAN
-- SELECT * FROM alumnos WHERE grupo_id = 1 ORDER BY numero_lista;

-- ========================================
-- NOTAS DE OPTIMIZACIÓN
-- ========================================

-- 1. Los índices mejoran las consultas SELECT, pero ralentizan INSERT/UPDATE/DELETE
-- 2. SQLite automáticamente usa índices cuando son beneficiosos
-- 3. Los índices UNIQUE además previenen datos duplicados
-- 4. Ejecutar ANALYZE periódicamente para actualizar estadísticas:
--    ANALYZE;

-- 5. Para ver el tamaño de la base de datos:
--    PRAGMA page_count;
--    PRAGMA page_size;

-- ========================================
-- FIN DEL SCRIPT
-- ========================================
