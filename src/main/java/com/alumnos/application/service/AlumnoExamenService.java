package com.alumnos.application.service;

import com.alumnos.domain.model.AlumnoExamen;
import com.alumnos.domain.port.in.AlumnoExamenServicePort;
import com.alumnos.domain.port.out.AlumnoExamenRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AlumnoExamenService implements AlumnoExamenServicePort {

    private final AlumnoExamenRepositoryPort alumnoExamenRepositoryPort;

    public AlumnoExamenService(AlumnoExamenRepositoryPort alumnoExamenRepositoryPort) {
        this.alumnoExamenRepositoryPort = alumnoExamenRepositoryPort;
    }

    @Override
    public AlumnoExamen crearAlumnoExamen(AlumnoExamen alumnoExamen) {
        return alumnoExamenRepositoryPort.save(alumnoExamen);
    }

    @Override
    public Optional<AlumnoExamen> obtenerAlumnoExamenPorId(Long id) {
        return alumnoExamenRepositoryPort.findById(id);
    }

    @Override
    public List<AlumnoExamen> obtenerTodosLosAlumnoExamen() {
        return alumnoExamenRepositoryPort.findAll();
    }

    @Override
    public AlumnoExamen actualizarAlumnoExamen(AlumnoExamen alumnoExamen) {
        return alumnoExamenRepositoryPort.save(alumnoExamen);
    }

    @Override
    public void eliminarAlumnoExamen(Long id) {
        alumnoExamenRepositoryPort.deleteById(id);
    }

    @Override
    public List<AlumnoExamen> obtenerAlumnoExamenPorAlumno(Long alumnoId) {
        return alumnoExamenRepositoryPort.findByAlumnoId(alumnoId);
    }

    @Override
    public List<AlumnoExamen> obtenerAlumnoExamenPorExamen(Long examenId) {
        return alumnoExamenRepositoryPort.findByExamenId(examenId);
    }

    @Override
    public Optional<AlumnoExamen> obtenerAlumnoExamenPorAlumnoYExamen(Long alumnoId, Long examenId) {
        return alumnoExamenRepositoryPort.findByAlumnoIdAndExamenId(alumnoId, examenId);
    }
}
