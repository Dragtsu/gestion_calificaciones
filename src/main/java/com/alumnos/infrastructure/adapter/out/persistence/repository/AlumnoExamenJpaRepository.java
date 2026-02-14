package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.infrastructure.adapter.out.persistence.entity.AlumnoExamenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoExamenJpaRepository extends JpaRepository<AlumnoExamenEntity, Long> {
    List<AlumnoExamenEntity> findByAlumnoId(Long alumnoId);
    List<AlumnoExamenEntity> findByExamenId(Long examenId);
    Optional<AlumnoExamenEntity> findByAlumnoIdAndExamenId(Long alumnoId, Long examenId);
}
