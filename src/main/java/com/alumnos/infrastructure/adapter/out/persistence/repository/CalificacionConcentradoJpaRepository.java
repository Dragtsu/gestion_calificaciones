package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.infrastructure.adapter.out.persistence.entity.CalificacionConcentradoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para CalificacionConcentrado.
 * Proporciona métodos de consulta personalizados para el concentrado de calificaciones.
 */
@Repository
public interface CalificacionConcentradoJpaRepository extends JpaRepository<CalificacionConcentradoEntity, Long> {

    List<CalificacionConcentradoEntity> findByAlumnoId(Long alumnoId);

    List<CalificacionConcentradoEntity> findByAgregadoId(Long agregadoId);

    List<CalificacionConcentradoEntity> findByGrupoId(Long grupoId);

    List<CalificacionConcentradoEntity> findByMateriaId(Long materiaId);

    List<CalificacionConcentradoEntity> findByParcial(Integer parcial);

    Optional<CalificacionConcentradoEntity> findByAlumnoIdAndAgregadoIdAndGrupoIdAndMateriaIdAndParcial(
        Long alumnoId, Long agregadoId, Long grupoId, Long materiaId, Integer parcial);

    List<CalificacionConcentradoEntity> findByGrupoIdAndMateriaIdAndParcial(
        Long grupoId, Long materiaId, Integer parcial);
}
