-- Script de migración para actualizar la tabla examenes
-- Eliminación del campo aciertos y alumno_id

-- PASO 1: Crear tabla temporal para los exámenes únicos (uno por grupo/materia/parcial)
CREATE TABLE IF NOT EXISTS examenes_temp (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    grupo_id INTEGER NOT NULL,
    materia_id INTEGER NOT NULL,
    parcial INTEGER NOT NULL,
    total_aciertos INTEGER,
    UNIQUE(grupo_id, materia_id, parcial)
);

-- PASO 2: Insertar registros únicos en la tabla temporal
-- Toma el primer registro de cada grupo/materia/parcial
INSERT INTO examenes_temp (grupo_id, materia_id, parcial, total_aciertos)
SELECT DISTINCT
    grupo_id,
    materia_id,
    parcial,
    total_aciertos
FROM examenes
GROUP BY grupo_id, materia_id, parcial;

-- PASO 3: Migrar los aciertos individuales a la tabla alumno_examen
-- Primero verificar que existe la tabla alumno_examen
CREATE TABLE IF NOT EXISTS alumno_examen (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    alumno_id INTEGER NOT NULL,
    examen_id INTEGER NOT NULL,
    aciertos INTEGER NOT NULL,
    UNIQUE(alumno_id, examen_id)
);

-- PASO 4: Migrar los datos de aciertos
-- Este paso requiere que los IDs de examenes_temp coincidan
-- Por simplicidad, primero verificamos si hay datos en examenes
-- y luego los migramos si corresponde

-- Nota: Este paso debe ejecutarse solo si hay datos que migrar
-- y después de asegurar que los IDs de examenes coinciden

-- PASO 5: Respaldar la tabla original
ALTER TABLE examenes RENAME TO examenes_backup;

-- PASO 6: Renombrar la tabla temporal a examenes
ALTER TABLE examenes_temp RENAME TO examenes;

-- NOTA: Los datos de alumno_examen deben migrarse manualmente
-- porque necesitamos el nuevo examen_id de la tabla examenes
-- Esto se puede hacer con un script más complejo o manualmente

-- VERIFICACIÓN: Contar registros
SELECT 'Examenes nuevos' as tabla, COUNT(*) as cantidad FROM examenes
UNION ALL
SELECT 'Examenes backup' as tabla, COUNT(*) as cantidad FROM examenes_backup;
