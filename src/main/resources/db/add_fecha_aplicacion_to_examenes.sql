-- Agregar campo fecha_aplicacion a la tabla examenes
-- Fecha: 2026-01-31
-- Descripción: Agrega el campo fecha_aplicacion para almacenar la fecha en que se aplicó el examen

ALTER TABLE examenes ADD COLUMN fecha_aplicacion DATE;

-- Comentario sobre el campo
-- fecha_aplicacion: Fecha en que se aplicó el examen (formato: YYYY-MM-DD)
