package com.alumnos.application.service;

import com.alumnos.domain.model.GrupoMateria;
import com.alumnos.domain.port.in.GrupoMateriaServicePort;
import com.alumnos.domain.port.out.GrupoMateriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GrupoMateriaService implements GrupoMateriaServicePort {

    private final GrupoMateriaRepositoryPort grupoMateriaRepositoryPort;

    public GrupoMateriaService(GrupoMateriaRepositoryPort grupoMateriaRepositoryPort) {
        this.grupoMateriaRepositoryPort = grupoMateriaRepositoryPort;
    }

    @Override
    public GrupoMateria asignarMateriaAGrupo(GrupoMateria grupoMateria) {
        // Validar que no exista ya la asignación
        if (grupoMateriaRepositoryPort.existsByGrupoIdAndMateriaId(
                grupoMateria.getGrupoId(), grupoMateria.getMateriaId())) {
            throw new IllegalArgumentException("Esta materia ya está asignada a este grupo");
        }
        return grupoMateriaRepositoryPort.save(grupoMateria);
    }

    @Override
    public Optional<GrupoMateria> obtenerAsignacionPorId(Long id) {
        return grupoMateriaRepositoryPort.findById(id);
    }

    @Override
    public List<GrupoMateria> obtenerTodasLasAsignaciones() {
        return grupoMateriaRepositoryPort.findAll();
    }

    @Override
    public void eliminarAsignacion(Long id) {
        grupoMateriaRepositoryPort.deleteById(id);
    }

    @Override
    public List<GrupoMateria> obtenerMateriasPorGrupo(Long grupoId) {
        return grupoMateriaRepositoryPort.findByGrupoId(grupoId);
    }

    @Override
    public List<GrupoMateria> obtenerGruposPorMateria(Long materiaId) {
        return grupoMateriaRepositoryPort.findByMateriaId(materiaId);
    }
}
