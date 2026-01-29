package com.alumnos.application.service;

import com.alumnos.domain.model.Examen;
import com.alumnos.domain.port.in.ExamenServicePort;
import com.alumnos.domain.port.out.ExamenRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExamenService implements ExamenServicePort {

    private final ExamenRepositoryPort examenRepositoryPort;

    public ExamenService(ExamenRepositoryPort examenRepositoryPort) {
        this.examenRepositoryPort = examenRepositoryPort;
    }

    @Override
    public Examen crearExamen(Examen examen) {
        return examenRepositoryPort.save(examen);
    }

    @Override
    public Optional<Examen> obtenerExamenPorId(Long id) {
        return examenRepositoryPort.findById(id);
    }

    @Override
    public List<Examen> obtenerTodosLosExamenes() {
        return examenRepositoryPort.findAll();
    }

    @Override
    public Examen actualizarExamen(Examen examen) {
        return examenRepositoryPort.save(examen);
    }

    @Override
    public void eliminarExamen(Long id) {
        examenRepositoryPort.deleteById(id);
    }

    @Override
    public List<Examen> obtenerExamenesPorAlumno(Long alumnoId) {
        return examenRepositoryPort.findByAlumnoId(alumnoId);
    }

    @Override
    public List<Examen> obtenerExamenesPorGrupoMateriaParcial(Long grupoId, Long materiaId, Integer parcial) {
        return examenRepositoryPort.findByGrupoIdAndMateriaIdAndParcial(grupoId, materiaId, parcial);
    }

    @Override
    public Optional<Examen> obtenerExamenPorAlumnoGrupoMateriaParcial(Long alumnoId, Long grupoId, Long materiaId, Integer parcial) {
        return examenRepositoryPort.findByAlumnoIdAndGrupoIdAndMateriaIdAndParcial(alumnoId, grupoId, materiaId, parcial);
    }
}
