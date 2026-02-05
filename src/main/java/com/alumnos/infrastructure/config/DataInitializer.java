package com.alumnos.infrastructure.config;

import com.alumnos.domain.model.Alumno;
import com.alumnos.domain.port.in.AlumnoServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);

    // DESHABILITADO PARA PRODUCCIÓN
    // @Bean
    public CommandLineRunner initData(AlumnoServicePort alumnoService) {
        return args -> {
            // Inicialización deshabilitada en producción
            /*
            try {
                // Verificar si ya hay datos
                if (alumnoService.obtenerTodosLosAlumnos().isEmpty()) {
                    // Crear algunos alumnos de ejemplo
                    // Código comentado para producción
                }
            } catch (Exception e) {
                LOG.error("Error al inicializar datos de ejemplo", e);
            }
            */
        };
    }
}
