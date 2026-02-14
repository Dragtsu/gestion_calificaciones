package com.alumnos.domain.port.out;

import com.alumnos.domain.model.AlumnoExamen;

import java.util.List;
import java.util.Optional;

public interface AlumnoExamenRepositoryPort {
    AlumnoExamen save(AlumnoExamen alumnoExamen);
    Optional<AlumnoExamen> findById(Long id);
    List<AlumnoExamen> findAll();
    void deleteById(Long id);
    List<AlumnoExamen> findByAlumnoId(Long alumnoId);
    List<AlumnoExamen> findByExamenId(Long examenId);
    Optional<AlumnoExamen> findByAlumnoIdAndExamenId(Long alumnoId, Long examenId);
}
