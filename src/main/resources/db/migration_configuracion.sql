-- Crear tabla de configuración para SQLite
CREATE TABLE IF NOT EXISTS configuracion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_maestro VARCHAR(200) NOT NULL
);

-- Insertar registro por defecto si no existe
INSERT OR IGNORE INTO configuracion (id, nombre_maestro)
VALUES (1, 'Sin configurar');
