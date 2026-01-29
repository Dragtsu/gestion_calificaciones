package com.alumnos.application.service;

import com.alumnos.domain.model.Calificacion;
import com.alumnos.domain.port.in.CalificacionServicePort;
import com.alumnos.domain.port.out.CalificacionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CalificacionService implements CalificacionServicePort {

    private final CalificacionRepositoryPort calificacionRepositoryPort;

    public CalificacionService(CalificacionRepositoryPort calificacionRepositoryPort) {
        this.calificacionRepositoryPort = calificacionRepositoryPort;
    }

    @Override
    public Calificacion crearCalificacion(Calificacion calificacion) {
        // Validaciones
        if (calificacion.getAlumnoId() == null) {
            throw new IllegalArgumentException("El alumno es requerido");
        }
        if (calificacion.getAgregadoId() == null) {
            throw new IllegalArgumentException("El agregado es requerido");
        }
        if (calificacion.getPuntuacion() == null) {
            throw new IllegalArgumentException("La puntuación es requerida");
        }
        if (calificacion.getPuntuacion() < 0) {
            throw new IllegalArgumentException("La puntuación no puede ser negativa");
        }

        // Verificar si ya existe una calificación para este alumno y agregado
        Optional<Calificacion> existente = calificacionRepositoryPort
                .findByAlumnoIdAndAgregadoId(calificacion.getAlumnoId(), calificacion.getAgregadoId());

        if (existente.isPresent()) {
            // Si existe, actualizar
            calificacion.setId(existente.get().getId());
            return calificacionRepositoryPort.save(calificacion);
        }

        return calificacionRepositoryPort.save(calificacion);
    }

    @Override
    public Optional<Calificacion> obtenerCalificacionPorId(Long id) {
        return calificacionRepositoryPort.findById(id);
    }

    @Override
    public List<Calificacion> obtenerTodasLasCalificaciones() {
        return calificacionRepositoryPort.findAll();
    }

    @Override
    public Calificacion actualizarCalificacion(Calificacion calificacion) {
        if (calificacion.getId() == null) {
            throw new IllegalArgumentException("El ID de la calificación es requerido para actualizar");
        }

        Optional<Calificacion> existente = calificacionRepositoryPort.findById(calificacion.getId());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("La calificación con ID " + calificacion.getId() + " no existe");
        }

        if (calificacion.getPuntuacion() != null && calificacion.getPuntuacion() < 0) {
            throw new IllegalArgumentException("La puntuación no puede ser negativa");
        }

        return calificacionRepositoryPort.save(calificacion);
    }

    @Override
    public void eliminarCalificacion(Long id) {
        calificacionRepositoryPort.deleteById(id);
    }

    @Override
    public List<Calificacion> obtenerCalificacionesPorAlumno(Long alumnoId) {
        return calificacionRepositoryPort.findByAlumnoId(alumnoId);
    }

    @Override
    public List<Calificacion> obtenerCalificacionesPorAgregado(Long agregadoId) {
        return calificacionRepositoryPort.findByAgregadoId(agregadoId);
    }

    @Override
    public Optional<Calificacion> obtenerCalificacionPorAlumnoYAgregado(Long alumnoId, Long agregadoId) {
        return calificacionRepositoryPort.findByAlumnoIdAndAgregadoId(alumnoId, agregadoId);
    }
}
