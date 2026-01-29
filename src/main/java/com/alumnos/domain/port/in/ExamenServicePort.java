package com.alumnos.domain.port.in;

import com.alumnos.domain.model.Examen;

import java.util.List;
import java.util.Optional;

public interface ExamenServicePort {
    Examen crearExamen(Examen examen);
    Optional<Examen> obtenerExamenPorId(Long id);
    List<Examen> obtenerTodosLosExamenes();
    Examen actualizarExamen(Examen examen);
    void eliminarExamen(Long id);
    Optional<Examen> obtenerExamenPorGrupoMateriaParcial(Long grupoId, Long materiaId, Integer parcial);
}
