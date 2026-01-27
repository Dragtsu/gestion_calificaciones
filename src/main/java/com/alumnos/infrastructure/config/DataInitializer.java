package com.alumnos.infrastructure.config;

import com.alumnos.domain.model.Agregado;
import com.alumnos.domain.model.Alumno;
import com.alumnos.domain.model.Criterio;
import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.model.GrupoMateria;
import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.AgregadoServicePort;
import com.alumnos.domain.port.in.AlumnoServicePort;
import com.alumnos.domain.port.in.CriterioServicePort;
import com.alumnos.domain.port.in.GrupoMateriaServicePort;
import com.alumnos.domain.port.in.GrupoServicePort;
import com.alumnos.domain.port.in.MateriaServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(AlumnoServicePort alumnoService,
                                       GrupoServicePort grupoService,
                                       MateriaServicePort materiaService,
                                       GrupoMateriaServicePort grupoMateriaService,
                                       CriterioServicePort criterioService,
                                       AgregadoServicePort agregadoService) {
        return args -> {
            LOG.info("Inicializando datos de ejemplo...");

            try {
                // 1. CREAR GRUPOS DE EJEMPLO (si no existen)
                if (grupoService.obtenerTodosLosGrupos().isEmpty()) {
                    LOG.info("Creando grupos de ejemplo...");

                    Grupo grupo1 = Grupo.builder().id(101L).build();
                    Grupo grupo2 = Grupo.builder().id(102L).build();
                    Grupo grupo3 = Grupo.builder().id(201L).build();

                    grupoService.crearGrupo(grupo1);
                    grupoService.crearGrupo(grupo2);
                    grupoService.crearGrupo(grupo3);

                    LOG.info("Grupos de ejemplo creados: 101, 102, 201");
                }

                // 2. CREAR MATERIAS DE EJEMPLO (si no existen)
                if (materiaService.obtenerTodasLasMaterias().isEmpty()) {
                    LOG.info("Creando materias de ejemplo...");

                    Materia materia1 = Materia.builder().nombre("Matemáticas").build();
                    Materia materia2 = Materia.builder().nombre("Física").build();
                    Materia materia3 = Materia.builder().nombre("Programación").build();
                    Materia materia4 = Materia.builder().nombre("Base de Datos").build();

                    materiaService.crearMateria(materia1);
                    materiaService.crearMateria(materia2);
                    materiaService.crearMateria(materia3);
                    materiaService.crearMateria(materia4);

                    LOG.info("Materias de ejemplo creadas");
                }

                // 3. CREAR ALUMNOS DE EJEMPLO (si no existen)
                if (alumnoService.obtenerTodosLosAlumnos().isEmpty()) {
                    LOG.info("Creando alumnos de ejemplo...");

                    Alumno alumno1 = Alumno.builder()
                            .nombre("Juan")
                            .apellidoPaterno("Pérez")
                            .apellidoMaterno("García")
                            .grupoId(101L)
                            .build();

                    Alumno alumno2 = Alumno.builder()
                            .nombre("María")
                            .apellidoPaterno("López")
                            .apellidoMaterno("Martínez")
                            .grupoId(101L)
                            .build();

                    Alumno alumno3 = Alumno.builder()
                            .nombre("Carlos")
                            .apellidoPaterno("Rodríguez")
                            .apellidoMaterno("Sánchez")
                            .grupoId(102L)
                            .build();

                    alumnoService.crearAlumno(alumno1);
                    alumnoService.crearAlumno(alumno2);
                    alumnoService.crearAlumno(alumno3);

                    LOG.info("Alumnos de ejemplo creados");
                }

                // 4. CREAR ASIGNACIONES DE EJEMPLO (si no existen)
                if (grupoMateriaService.obtenerTodasLasAsignaciones().isEmpty()) {
                    LOG.info("Creando asignaciones de ejemplo...");

                    // Asignar materias al grupo 101
                    GrupoMateria asig1 = GrupoMateria.builder()
                            .grupoId(101L)
                            .materiaId(1L)  // Matemáticas
                            .build();

                    GrupoMateria asig2 = GrupoMateria.builder()
                            .grupoId(101L)
                            .materiaId(3L)  // Programación
                            .build();

                    // Asignar materias al grupo 102
                    GrupoMateria asig3 = GrupoMateria.builder()
                            .grupoId(102L)
                            .materiaId(2L)  // Física
                            .build();

                    GrupoMateria asig4 = GrupoMateria.builder()
                            .grupoId(102L)
                            .materiaId(4L)  // Base de Datos
                            .build();

                    grupoMateriaService.asignarMateriaAGrupo(asig1);
                    grupoMateriaService.asignarMateriaAGrupo(asig2);
                    grupoMateriaService.asignarMateriaAGrupo(asig3);
                    grupoMateriaService.asignarMateriaAGrupo(asig4);

                    LOG.info("Asignaciones de ejemplo creadas");
                }

                // 5. CREAR CRITERIOS DE EJEMPLO (si no existen)
                if (criterioService.obtenerTodosLosCriterios().isEmpty()) {
                    LOG.info("Creando criterios de ejemplo...");

                    Criterio criterio1 = Criterio.builder()
                            .nombre("Asistencia")
                            .tipoEvaluacion("Check")
                            .materiaId(1L)  // Matemáticas
                            .build();

                    Criterio criterio2 = Criterio.builder()
                            .nombre("Examen Final")
                            .tipoEvaluacion("Puntuacion")
                            .puntuacionMaxima(100.0)
                            .materiaId(1L)  // Matemáticas
                            .build();

                    Criterio criterio3 = Criterio.builder()
                            .nombre("Participación")
                            .tipoEvaluacion("Puntuacion")
                            .puntuacionMaxima(10.0)
                            .materiaId(3L)  // Programación
                            .build();

                    Criterio criterio4 = Criterio.builder()
                            .nombre("Proyecto Final")
                            .tipoEvaluacion("Puntuacion")
                            .puntuacionMaxima(50.0)
                            .materiaId(3L)  // Programación
                            .build();

                    criterioService.crearCriterio(criterio1);
                    criterioService.crearCriterio(criterio2);
                    criterioService.crearCriterio(criterio3);
                    criterioService.crearCriterio(criterio4);

                    LOG.info("Criterios de ejemplo creados");
                }

                // 6. INICIALIZAR ORDEN DE AGREGADOS (si no tienen orden)
                inicializarOrdenAgregados(agregadoService);

                LOG.info("✅ Inicialización de datos completada exitosamente");

            } catch (Exception e) {
                LOG.error("❌ Error al inicializar datos de ejemplo", e);
            }
        };
    }

    /**
     * Inicializa el campo orden para agregados existentes que no lo tienen
     */
    private void inicializarOrdenAgregados(AgregadoServicePort agregadoService) {
        try {
            List<Agregado> todosLosAgregados = agregadoService.obtenerTodosLosAgregados();

            // Agrupar por criterioId
            Map<Long, Integer> contadorPorCriterio = new HashMap<>();
            int actualizados = 0;

            for (Agregado agregado : todosLosAgregados) {
                if (agregado.getOrden() == null && agregado.getCriterioId() != null) {
                    Long criterioId = agregado.getCriterioId();
                    int orden = contadorPorCriterio.getOrDefault(criterioId, 0) + 1;
                    contadorPorCriterio.put(criterioId, orden);

                    // Actualizar el orden usando el servicio
                    agregadoService.actualizarOrdenAgregado(agregado.getId(), orden);
                    actualizados++;

                    LOG.debug("Orden inicializado para agregado ID {}: orden {}", agregado.getId(), orden);
                }
            }

            if (actualizados > 0) {
                LOG.info("Inicialización de orden de agregados: {} agregados actualizados", actualizados);
            }
        } catch (Exception e) {
            LOG.error("Error al inicializar orden de agregados", e);
        }
    }
}
