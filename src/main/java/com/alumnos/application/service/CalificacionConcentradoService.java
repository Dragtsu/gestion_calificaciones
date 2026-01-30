package com.alumnos.application.service;

import com.alumnos.domain.model.CalificacionConcentrado;
import com.alumnos.domain.port.in.CalificacionConcentradoServicePort;
import com.alumnos.domain.port.out.CalificacionConcentradoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para CalificacionConcentrado.
 * Implementa la lógica de negocio y validaciones para las calificaciones del concentrado.
 */
@Service
@Transactional
public class CalificacionConcentradoService implements CalificacionConcentradoServicePort {

    private final CalificacionConcentradoRepositoryPort repositoryPort;

    public CalificacionConcentradoService(CalificacionConcentradoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public CalificacionConcentrado crearCalificacion(CalificacionConcentrado calificacion) {
        // Validaciones
        validarCalificacion(calificacion);

        // Verificar si ya existe una calificación con los mismos filtros
        Optional<CalificacionConcentrado> existente = repositoryPort
                .findByAlumnoIdAndAgregadoIdAndGrupoIdAndMateriaIdAndParcial(
                        calificacion.getAlumnoId(),
                        calificacion.getAgregadoId(),
                        calificacion.getGrupoId(),
                        calificacion.getMateriaId(),
                        calificacion.getParcial()
                );

        if (existente.isPresent()) {
            // Actualizar la calificación existente
            CalificacionConcentrado calificacionExistente = existente.get();
            calificacionExistente.setPuntuacion(calificacion.getPuntuacion());
            calificacionExistente.setTipoEvaluacion(calificacion.getTipoEvaluacion());
            return repositoryPort.save(calificacionExistente);
        }

        // Crear nueva calificación
        return repositoryPort.save(calificacion);
    }

    @Override
    public Optional<CalificacionConcentrado> obtenerCalificacionPorId(Long id) {
        return repositoryPort.findById(id);
    }

    @Override
    public List<CalificacionConcentrado> obtenerTodasLasCalificaciones() {
        return repositoryPort.findAll();
    }

    @Override
    public CalificacionConcentrado actualizarCalificacion(CalificacionConcentrado calificacion) {
        if (calificacion.getId() == null) {
            throw new IllegalArgumentException("El ID de la calificación es requerido para actualizar");
        }

        Optional<CalificacionConcentrado> existente = repositoryPort.findById(calificacion.getId());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("La calificación con ID " + calificacion.getId() + " no existe");
        }

        validarCalificacion(calificacion);

        return repositoryPort.save(calificacion);
    }

    @Override
    public void eliminarCalificacion(Long id) {
        repositoryPort.deleteById(id);
    }

    @Override
    public List<CalificacionConcentrado> obtenerCalificacionesPorAlumno(Long alumnoId) {
        return repositoryPort.findByAlumnoId(alumnoId);
    }

    @Override
    public List<CalificacionConcentrado> obtenerCalificacionesPorAgregado(Long agregadoId) {
        return repositoryPort.findByAgregadoId(agregadoId);
    }

    @Override
    public List<CalificacionConcentrado> obtenerCalificacionesPorGrupo(Long grupoId) {
        return repositoryPort.findByGrupoId(grupoId);
    }

    @Override
    public List<CalificacionConcentrado> obtenerCalificacionesPorMateria(Long materiaId) {
        return repositoryPort.findByMateriaId(materiaId);
    }

    @Override
    public List<CalificacionConcentrado> obtenerCalificacionesPorParcial(Integer parcial) {
        return repositoryPort.findByParcial(parcial);
    }

    @Override
    public Optional<CalificacionConcentrado> obtenerCalificacionPorAlumnoYAgregadoYFiltros(
            Long alumnoId, Long agregadoId, Long grupoId, Long materiaId, Integer parcial) {
        return repositoryPort.findByAlumnoIdAndAgregadoIdAndGrupoIdAndMateriaIdAndParcial(
                alumnoId, agregadoId, grupoId, materiaId, parcial);
    }

    @Override
    public List<CalificacionConcentrado> obtenerCalificacionesPorGrupoMateriaYParcial(
            Long grupoId, Long materiaId, Integer parcial) {
        return repositoryPort.findByGrupoIdAndMateriaIdAndParcial(grupoId, materiaId, parcial);
    }

    /**
     * Valida los campos de una calificación.
     */
    private void validarCalificacion(CalificacionConcentrado calificacion) {
        if (calificacion.getAlumnoId() == null) {
            throw new IllegalArgumentException("El alumno es requerido");
        }
        if (calificacion.getAgregadoId() == null) {
            throw new IllegalArgumentException("El agregado es requerido");
        }
        if (calificacion.getCriterioId() == null) {
            throw new IllegalArgumentException("El criterio es requerido");
        }
        if (calificacion.getGrupoId() == null) {
            throw new IllegalArgumentException("El grupo es requerido");
        }
        if (calificacion.getMateriaId() == null) {
            throw new IllegalArgumentException("La materia es requerida");
        }
        if (calificacion.getParcial() == null) {
            throw new IllegalArgumentException("El parcial es requerido");
        }
        if (calificacion.getParcial() < 1 || calificacion.getParcial() > 3) {
            throw new IllegalArgumentException("El parcial debe ser 1, 2 o 3");
        }
        if (calificacion.getPuntuacion() == null) {
            throw new IllegalArgumentException("La puntuación es requerida");
        }
        if (calificacion.getPuntuacion() < 0) {
            throw new IllegalArgumentException("La puntuación no puede ser negativa");
        }

        // Validaciones específicas por tipo de evaluación
        if ("Check".equalsIgnoreCase(calificacion.getTipoEvaluacion())) {
            // Para tipo Check, la puntuación debe ser 0.0 o 1.0
            if (calificacion.getPuntuacion() != 0.0 && calificacion.getPuntuacion() != 1.0) {
                throw new IllegalArgumentException("Para tipo Check, la puntuación debe ser 0.0 o 1.0");
            }
        }
    }
}
