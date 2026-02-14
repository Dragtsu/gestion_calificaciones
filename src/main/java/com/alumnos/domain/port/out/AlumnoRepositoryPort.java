package com.alumnos.domain.port.out;

import com.alumnos.domain.model.Alumno;

import java.util.List;
import java.util.Optional;

public interface AlumnoRepositoryPort {
    Alumno save(Alumno alumno);
    Optional<Alumno> findById(Long id);
    List<Alumno> findAll();
    void deleteById(Long id);
    List<Alumno> findByNombreContaining(String nombre);
}
