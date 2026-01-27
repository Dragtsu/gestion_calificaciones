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

    @Bean
    public CommandLineRunner initData(AlumnoServicePort alumnoService) {
        return args -> {
            LOG.info("Inicializando datos de ejemplo...");

            try {
                // Verificar si ya hay datos
                if (alumnoService.obtenerTodosLosAlumnos().isEmpty()) {
                    // Crear algunos alumnos de ejemplo
                    Alumno alumno1 = Alumno.builder()
                            .nombre("Juan")
                            .apellidoPaterno("Pérez")
                            .apellidoMaterno("García")
                            .build();

                    Alumno alumno2 = Alumno.builder()
                            .nombre("María")
                            .apellidoPaterno("López")
                            .apellidoMaterno("Martínez")
                            .build();

                    Alumno alumno3 = Alumno.builder()
                            .nombre("Carlos")
                            .apellidoPaterno("Rodríguez")
                            .apellidoMaterno("Sánchez")
                            .build();

                    alumnoService.crearAlumno(alumno1);
                    alumnoService.crearAlumno(alumno2);
                    alumnoService.crearAlumno(alumno3);

                    LOG.info("Datos de ejemplo creados exitosamente");
                } else {
                    LOG.info("La base de datos ya contiene datos");
                }
            } catch (Exception e) {
                LOG.error("Error al inicializar datos de ejemplo", e);
            }
        };
    }
}
