-- ============================================================================
-- Script para Limpiar Completamente la Base de Datos
-- ============================================================================
-- Este script elimina TODOS los datos incluyendo la estructura de tablas
-- Use con precaución - esto recreará la base de datos desde cero
--
-- ADVERTENCIA: Este script elimina TODO, incluyendo la estructura
-- ============================================================================

-- Deshabilitar restricciones de clave foránea
PRAGMA foreign_keys = OFF;

-- ============================================================================
-- ELIMINAR TODAS LAS TABLAS EXISTENTES
-- ============================================================================

DROP TABLE IF EXISTS calificacion_concentrado;
DROP TABLE IF EXISTS alumno_examen;
DROP TABLE IF EXISTS examenes;
DROP TABLE IF EXISTS calificaciones;
DROP TABLE IF EXISTS agregados;
DROP TABLE IF EXISTS criterios;
DROP TABLE IF EXISTS grupo_materia;
DROP TABLE IF EXISTS alumnos;
DROP TABLE IF EXISTS grupos;
DROP TABLE IF EXISTS materias;
DROP TABLE IF EXISTS configuracion;

-- ============================================================================
-- RECREAR ESTRUCTURA DE TABLAS
-- ============================================================================

-- Tabla de configuración
CREATE TABLE IF NOT EXISTS configuracion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_maestro TEXT NOT NULL
);

-- Tabla de materias
CREATE TABLE IF NOT EXISTS materias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT 1
);

-- Tabla de grupos
CREATE TABLE IF NOT EXISTS grupos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT 1
);

-- Tabla de alumnos
CREATE TABLE IF NOT EXISTS alumnos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    numero_lista INTEGER NOT NULL,
    apellido_paterno TEXT NOT NULL,
    apellido_materno TEXT NOT NULL,
    nombres TEXT NOT NULL,
    grupo_id INTEGER NOT NULL,
    activo BOOLEAN DEFAULT 1,
    FOREIGN KEY (grupo_id) REFERENCES grupos(id) ON DELETE CASCADE,
    UNIQUE(numero_lista, grupo_id)
);

-- Tabla de relación grupo-materia
CREATE TABLE IF NOT EXISTS grupo_materia (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    grupo_id INTEGER NOT NULL,
    materia_id INTEGER NOT NULL,
    FOREIGN KEY (grupo_id) REFERENCES grupos(id) ON DELETE CASCADE,
    FOREIGN KEY (materia_id) REFERENCES materias(id) ON DELETE CASCADE,
    UNIQUE(grupo_id, materia_id)
);

-- Tabla de criterios de evaluación
CREATE TABLE IF NOT EXISTS criterios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    porcentaje REAL NOT NULL,
    grupo_materia_id INTEGER NOT NULL,
    FOREIGN KEY (grupo_materia_id) REFERENCES grupo_materia(id) ON DELETE CASCADE
);

-- Tabla de agregados (componentes de criterios)
CREATE TABLE IF NOT EXISTS agregados (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    porcentaje REAL NOT NULL,
    criterio_id INTEGER NOT NULL,
    FOREIGN KEY (criterio_id) REFERENCES criterios(id) ON DELETE CASCADE
);

-- Tabla de calificaciones
CREATE TABLE IF NOT EXISTS calificaciones (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    alumno_id INTEGER NOT NULL,
    agregado_id INTEGER NOT NULL,
    calificacion REAL NOT NULL,
    FOREIGN KEY (alumno_id) REFERENCES alumnos(id) ON DELETE CASCADE,
    FOREIGN KEY (agregado_id) REFERENCES agregados(id) ON DELETE CASCADE,
    UNIQUE(alumno_id, agregado_id)
);

-- Tabla de concentrado de calificaciones
CREATE TABLE IF NOT EXISTS calificacion_concentrado (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    alumno_id INTEGER NOT NULL,
    criterio_id INTEGER NOT NULL,
    calificacion REAL NOT NULL,
    FOREIGN KEY (alumno_id) REFERENCES alumnos(id) ON DELETE CASCADE,
    FOREIGN KEY (criterio_id) REFERENCES criterios(id) ON DELETE CASCADE,
    UNIQUE(alumno_id, criterio_id)
);

-- Tabla de exámenes
CREATE TABLE IF NOT EXISTS examenes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    grupo_materia_id INTEGER NOT NULL,
    fecha_aplicacion TEXT,
    FOREIGN KEY (grupo_materia_id) REFERENCES grupo_materia(id) ON DELETE CASCADE
);

-- Tabla de relación alumno-examen
CREATE TABLE IF NOT EXISTS alumno_examen (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    alumno_id INTEGER NOT NULL,
    examen_id INTEGER NOT NULL,
    calificacion REAL NOT NULL,
    FOREIGN KEY (alumno_id) REFERENCES alumnos(id) ON DELETE CASCADE,
    FOREIGN KEY (examen_id) REFERENCES examenes(id) ON DELETE CASCADE,
    UNIQUE(alumno_id, examen_id)
);

-- ============================================================================
-- INSERTAR CONFIGURACIÓN INICIAL
-- ============================================================================

INSERT INTO configuracion (id, nombre_maestro)
VALUES (1, 'Sin configurar');

-- ============================================================================
-- REACTIVAR RESTRICCIONES
-- ============================================================================

PRAGMA foreign_keys = ON;

-- ============================================================================
-- VERIFICAR Y OPTIMIZAR
-- ============================================================================

PRAGMA integrity_check;
VACUUM;

-- ============================================================================
-- Script completado exitosamente
-- ============================================================================
