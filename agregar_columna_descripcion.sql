-- Script para agregar la columna 'descripcion' a la tabla 'agregados'
-- Ejecutar este script en la base de datos alumnos.db

ALTER TABLE agregados ADD COLUMN descripcion TEXT;
