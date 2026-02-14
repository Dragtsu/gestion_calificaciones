package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.infrastructure.adapter.out.persistence.entity.CriterioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CriterioJpaRepository extends JpaRepository<CriterioEntity, Long> {
    List<CriterioEntity> findByMateriaId(Long materiaId);
}
