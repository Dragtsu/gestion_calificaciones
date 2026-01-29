-- Script simple para limpiar duplicados en la tabla examenes
-- Este script elimina registros duplicados dejando solo uno por grupo/materia/parcial

-- OPCIÓN 1: Eliminar todos los registros duplicados excepto el primero
DELETE FROM examenes
WHERE id NOT IN (
    SELECT MIN(id)
    FROM examenes
    GROUP BY grupo_id, materia_id, parcial
);

-- Verificar que no hay duplicados
SELECT
    grupo_id,
    materia_id,
    parcial,
    COUNT(*) as cantidad
FROM examenes
GROUP BY grupo_id, materia_id, parcial
HAVING COUNT(*) > 1;

-- Si el resultado está vacío, ya no hay duplicados
