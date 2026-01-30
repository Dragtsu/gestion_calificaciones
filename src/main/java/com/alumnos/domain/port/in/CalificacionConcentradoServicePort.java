package com.alumnos.domain.port.in;

import com.alumnos.domain.model.CalificacionConcentrado;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para el servicio de CalificacionConcentrado.
 * Define los casos de uso disponibles para la gestión de calificaciones del concentrado.
 */
public interface CalificacionConcentradoServicePort {

    /**
     * Crea una nueva calificación del concentrado.
     * Si ya existe una calificación con los mismos filtros, la actualiza.
     */
    CalificacionConcentrado crearCalificacion(CalificacionConcentrado calificacion);

    /**
     * Obtiene una calificación por su ID.
     */
    Optional<CalificacionConcentrado> obtenerCalificacionPorId(Long id);

    /**
     * Obtiene todas las calificaciones del concentrado.
     */
    List<CalificacionConcentrado> obtenerTodasLasCalificaciones();

    /**
     * Actualiza una calificación existente.
     */
    CalificacionConcentrado actualizarCalificacion(CalificacionConcentrado calificacion);

    /**
     * Elimina una calificación por su ID.
     */
    void eliminarCalificacion(Long id);

    /**
     * Obtiene todas las calificaciones de un alumno.
     */
    List<CalificacionConcentrado> obtenerCalificacionesPorAlumno(Long alumnoId);

    /**
     * Obtiene todas las calificaciones de un agregado.
     */
    List<CalificacionConcentrado> obtenerCalificacionesPorAgregado(Long agregadoId);

    /**
     * Obtiene todas las calificaciones de un grupo.
     */
    List<CalificacionConcentrado> obtenerCalificacionesPorGrupo(Long grupoId);

    /**
     * Obtiene todas las calificaciones de una materia.
     */
    List<CalificacionConcentrado> obtenerCalificacionesPorMateria(Long materiaId);

    /**
     * Obtiene todas las calificaciones de un parcial.
     */
    List<CalificacionConcentrado> obtenerCalificacionesPorParcial(Integer parcial);

    /**
     * Obtiene la calificación específica de un alumno en un agregado,
     * considerando el grupo, materia y parcial.
     */
    Optional<CalificacionConcentrado> obtenerCalificacionPorAlumnoYAgregadoYFiltros(
        Long alumnoId, Long agregadoId, Long grupoId, Long materiaId, Integer parcial);

    /**
     * Obtiene todas las calificaciones por grupo, materia y parcial.
     */
    List<CalificacionConcentrado> obtenerCalificacionesPorGrupoMateriaYParcial(
        Long grupoId, Long materiaId, Integer parcial);
}
