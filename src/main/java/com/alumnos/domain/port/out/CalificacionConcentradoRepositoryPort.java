package com.alumnos.domain.port.out;

import com.alumnos.domain.model.CalificacionConcentrado;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para el repositorio de CalificacionConcentrado.
 * Define los contratos para la persistencia de calificaciones del concentrado.
 */
public interface CalificacionConcentradoRepositoryPort {

    /**
     * Guarda o actualiza una calificación del concentrado.
     */
    CalificacionConcentrado save(CalificacionConcentrado calificacionConcentrado);

    /**
     * Busca una calificación por su ID.
     */
    Optional<CalificacionConcentrado> findById(Long id);

    /**
     * Obtiene todas las calificaciones del concentrado.
     */
    List<CalificacionConcentrado> findAll();

    /**
     * Elimina una calificación por su ID.
     */
    void deleteById(Long id);

    /**
     * Obtiene todas las calificaciones de un alumno específico.
     */
    List<CalificacionConcentrado> findByAlumnoId(Long alumnoId);

    /**
     * Obtiene todas las calificaciones de un agregado específico.
     */
    List<CalificacionConcentrado> findByAgregadoId(Long agregadoId);

    /**
     * Obtiene todas las calificaciones de un grupo específico.
     */
    List<CalificacionConcentrado> findByGrupoId(Long grupoId);

    /**
     * Obtiene todas las calificaciones de una materia específica.
     */
    List<CalificacionConcentrado> findByMateriaId(Long materiaId);

    /**
     * Obtiene todas las calificaciones de un parcial específico.
     */
    List<CalificacionConcentrado> findByParcial(Integer parcial);

    /**
     * Obtiene la calificación específica de un alumno en un agregado,
     * considerando el grupo, materia y parcial.
     */
    Optional<CalificacionConcentrado> findByAlumnoIdAndAgregadoIdAndGrupoIdAndMateriaIdAndParcial(
        Long alumnoId, Long agregadoId, Long grupoId, Long materiaId, Integer parcial);

    /**
     * Obtiene todas las calificaciones por grupo, materia y parcial.
     */
    List<CalificacionConcentrado> findByGrupoIdAndMateriaIdAndParcial(
        Long grupoId, Long materiaId, Integer parcial);
}
