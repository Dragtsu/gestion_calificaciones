package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.infrastructure.adapter.out.persistence.entity.AgregadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgregadoJpaRepository extends JpaRepository<AgregadoEntity, Long> {
    List<AgregadoEntity> findByCriterioId(Long criterioId);
}
