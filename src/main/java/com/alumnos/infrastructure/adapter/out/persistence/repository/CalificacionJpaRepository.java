package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.infrastructure.adapter.out.persistence.entity.CalificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionJpaRepository extends JpaRepository<CalificacionEntity, Long> {
    List<CalificacionEntity> findByAlumnoId(Long alumnoId);
    List<CalificacionEntity> findByAgregadoId(Long agregadoId);
    Optional<CalificacionEntity> findByAlumnoIdAndAgregadoId(Long alumnoId, Long agregadoId);
}
