package com.alumnos.domain.port.out;

import com.alumnos.domain.model.GrupoMateria;

import java.util.List;
import java.util.Optional;

public interface GrupoMateriaRepositoryPort {
    GrupoMateria save(GrupoMateria grupoMateria);
    Optional<GrupoMateria> findById(Long id);
    List<GrupoMateria> findAll();
    void deleteById(Long id);
    List<GrupoMateria> findByGrupoId(Long grupoId);
    List<GrupoMateria> findByMateriaId(Long materiaId);
    boolean existsByGrupoIdAndMateriaId(Long grupoId, Long materiaId);
}
