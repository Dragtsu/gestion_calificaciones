package com.alumnos.domain.port.out;

import com.alumnos.domain.model.Calificacion;

import java.util.List;
import java.util.Optional;

public interface CalificacionRepositoryPort {
    Calificacion save(Calificacion calificacion);
    Optional<Calificacion> findById(Long id);
    List<Calificacion> findAll();
    void deleteById(Long id);
    List<Calificacion> findByAlumnoId(Long alumnoId);
    List<Calificacion> findByAgregadoId(Long agregadoId);
    Optional<Calificacion> findByAlumnoIdAndAgregadoId(Long alumnoId, Long agregadoId);
}
