package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.infrastructure.adapter.out.persistence.entity.ExamenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenJpaRepository extends JpaRepository<ExamenEntity, Long> {
    List<ExamenEntity> findByGrupoIdAndMateriaIdAndParcial(Long grupoId, Long materiaId, Integer parcial);
}
