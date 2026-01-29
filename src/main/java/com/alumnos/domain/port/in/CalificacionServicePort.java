package com.alumnos.domain.port.in;

import com.alumnos.domain.model.Calificacion;

import java.util.List;
import java.util.Optional;

public interface CalificacionServicePort {
    Calificacion crearCalificacion(Calificacion calificacion);
    Optional<Calificacion> obtenerCalificacionPorId(Long id);
    List<Calificacion> obtenerTodasLasCalificaciones();
    Calificacion actualizarCalificacion(Calificacion calificacion);
    void eliminarCalificacion(Long id);
    List<Calificacion> obtenerCalificacionesPorAlumno(Long alumnoId);
    List<Calificacion> obtenerCalificacionesPorAgregado(Long agregadoId);
    Optional<Calificacion> obtenerCalificacionPorAlumnoYAgregado(Long alumnoId, Long agregadoId);
}
