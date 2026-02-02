-- ============================================================================
-- Script para Limpiar Base de Datos para Producción
-- ============================================================================
-- Este script elimina todos los datos de prueba manteniendo la estructura
-- de las tablas y la configuración inicial del sistema.
--
-- IMPORTANTE: Ejecutar este script antes de desplegar en producción
-- ============================================================================

-- Deshabilitar restricciones de clave foránea temporalmente
PRAGMA foreign_keys = OFF;

-- ============================================================================
-- PASO 1: Eliminar datos de las tablas de calificaciones y exámenes
-- ============================================================================

-- Eliminar calificaciones de concentrado (datos derivados de calificaciones)
DELETE FROM calificacion_concentrado;

-- Eliminar relaciones alumno-examen
DELETE FROM alumno_examen;

-- Eliminar exámenes
DELETE FROM examenes;

-- Eliminar calificaciones individuales
DELETE FROM calificaciones;

-- ============================================================================
-- PASO 2: Eliminar datos de agregados y criterios
-- ============================================================================

-- Eliminar agregados (componentes de evaluación de los criterios)
DELETE FROM agregados;

-- Eliminar criterios de evaluación
DELETE FROM criterios;

-- ============================================================================
-- PASO 3: Eliminar relaciones grupo-materia
-- ============================================================================

-- Eliminar asignaciones de materias a grupos
DELETE FROM grupo_materia;

-- ============================================================================
-- PASO 4: Eliminar datos de alumnos
-- ============================================================================

-- Eliminar alumnos
DELETE FROM alumnos;

-- ============================================================================
-- PASO 5: Eliminar datos de grupos y materias
-- ============================================================================

-- Eliminar grupos
DELETE FROM grupos;

-- Eliminar materias
DELETE FROM materias;

-- ============================================================================
-- PASO 6: Restablecer configuración inicial
-- ============================================================================

-- Limpiar tabla de configuración
DELETE FROM configuracion;

-- Insertar configuración por defecto
INSERT INTO configuracion (id, nombre_maestro)
VALUES (1, 'Sin configurar');

-- ============================================================================
-- PASO 7: Reiniciar secuencias de autoincremento (SQLite)
-- ============================================================================

-- Reiniciar secuencias de todas las tablas
DELETE FROM sqlite_sequence WHERE name = 'alumnos';
DELETE FROM sqlite_sequence WHERE name = 'grupos';
DELETE FROM sqlite_sequence WHERE name = 'materias';
DELETE FROM sqlite_sequence WHERE name = 'grupo_materia';
DELETE FROM sqlite_sequence WHERE name = 'criterios';
DELETE FROM sqlite_sequence WHERE name = 'agregados';
DELETE FROM sqlite_sequence WHERE name = 'calificaciones';
DELETE FROM sqlite_sequence WHERE name = 'calificacion_concentrado';
DELETE FROM sqlite_sequence WHERE name = 'examenes';
DELETE FROM sqlite_sequence WHERE name = 'alumno_examen';
DELETE FROM sqlite_sequence WHERE name = 'configuracion';

-- ============================================================================
-- PASO 8: Reactivar restricciones de clave foránea
-- ============================================================================

PRAGMA foreign_keys = ON;

-- ============================================================================
-- PASO 9: Verificar integridad de la base de datos
-- ============================================================================

-- Ejecutar verificación de integridad
PRAGMA integrity_check;

-- ============================================================================
-- PASO 10: Optimizar base de datos
-- ============================================================================

-- Compactar base de datos eliminando espacio no utilizado
VACUUM;

-- ============================================================================
-- Script completado exitosamente
-- ============================================================================
-- La base de datos está ahora limpia y lista para producción.
--
-- Próximos pasos:
-- 1. Configurar el nombre del maestro en el sistema
-- 2. Crear grupos necesarios
-- 3. Crear materias
-- 4. Asignar materias a grupos
-- 5. Registrar alumnos
-- 6. Definir criterios de evaluación
-- 7. Definir agregados por criterio
-- ============================================================================
