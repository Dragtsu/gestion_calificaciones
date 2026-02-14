package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.infrastructure.adapter.out.persistence.entity.GrupoMateriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoMateriaJpaRepository extends JpaRepository<GrupoMateriaEntity, Long> {
    List<GrupoMateriaEntity> findByGrupoId(Long grupoId);
    List<GrupoMateriaEntity> findByMateriaId(Long materiaId);
    boolean existsByGrupoIdAndMateriaId(Long grupoId, Long materiaId);
}
