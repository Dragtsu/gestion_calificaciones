package com.alumnos.domain.port.in;

import com.alumnos.domain.model.AlumnoExamen;

import java.util.List;
import java.util.Optional;

public interface AlumnoExamenServicePort {
    AlumnoExamen crearAlumnoExamen(AlumnoExamen alumnoExamen);
    Optional<AlumnoExamen> obtenerAlumnoExamenPorId(Long id);
    List<AlumnoExamen> obtenerTodosLosAlumnoExamen();
    AlumnoExamen actualizarAlumnoExamen(AlumnoExamen alumnoExamen);
    void eliminarAlumnoExamen(Long id);
    List<AlumnoExamen> obtenerAlumnoExamenPorAlumno(Long alumnoId);
    List<AlumnoExamen> obtenerAlumnoExamenPorExamen(Long examenId);
    Optional<AlumnoExamen> obtenerAlumnoExamenPorAlumnoYExamen(Long alumnoId, Long examenId);
}
