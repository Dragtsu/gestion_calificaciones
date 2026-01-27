package com.alumnos.domain.port.in;

import com.alumnos.domain.model.Alumno;

import java.util.List;
import java.util.Optional;

public interface AlumnoServicePort {
    Alumno crearAlumno(Alumno alumno);
    Optional<Alumno> obtenerAlumnoPorId(Long id);
    List<Alumno> obtenerTodosLosAlumnos();
    Alumno actualizarAlumno(Alumno alumno);
    void eliminarAlumno(Long id);
    List<Alumno> buscarPorNombre(String nombre);
}
