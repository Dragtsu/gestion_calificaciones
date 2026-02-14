package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.infrastructure.adapter.out.persistence.entity.ConfiguracionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionJpaRepository extends JpaRepository<ConfiguracionEntity, Long> {
    Optional<ConfiguracionEntity> findFirstByOrderByIdAsc();
}
