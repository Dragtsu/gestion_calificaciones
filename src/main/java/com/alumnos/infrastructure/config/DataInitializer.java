package com.alumnos.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);

    // Migración de base de datos: agregar columna descripcion a la tabla agregados
    @Bean
    public CommandLineRunner migrateDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // Verificar si la columna ya existe
                String checkColumn = "PRAGMA table_info(agregados)";
                Boolean columnExists = jdbcTemplate.query(checkColumn, rs -> {
                    while (rs.next()) {
                        if ("descripcion".equals(rs.getString("name"))) {
                            return true;
                        }
                    }
                    return false;
                });

                // Si no existe, agregar la columna
                if (Boolean.FALSE.equals(columnExists)) {
                    String sql = "ALTER TABLE agregados ADD COLUMN descripcion TEXT";
                    jdbcTemplate.execute(sql);
                    LOG.info("✅ Columna 'descripcion' agregada exitosamente a la tabla 'agregados'");
                } else {
                    LOG.info("ℹ️ La columna 'descripcion' ya existe en la tabla 'agregados'");
                }
            } catch (Exception e) {
                LOG.error("❌ Error al migrar la base de datos", e);
            }
        };
    }
}
