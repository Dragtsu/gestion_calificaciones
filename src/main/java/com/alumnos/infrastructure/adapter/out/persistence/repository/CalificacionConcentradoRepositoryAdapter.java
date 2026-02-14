package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.CalificacionConcentrado;
import com.alumnos.domain.port.out.CalificacionConcentradoRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.CalificacionConcentradoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador del repositorio de CalificacionConcentrado.
 * Convierte entre el modelo de dominio y la entidad JPA.
 */
@Component
public class CalificacionConcentradoRepositoryAdapter implements CalificacionConcentradoRepositoryPort {

    private final CalificacionConcentradoJpaRepository jpaRepository;

    public CalificacionConcentradoRepositoryAdapter(CalificacionConcentradoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CalificacionConcentrado save(CalificacionConcentrado calificacionConcentrado) {
        CalificacionConcentradoEntity entity = toEntity(calificacionConcentrado);
        CalificacionConcentradoEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<CalificacionConcentrado> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<CalificacionConcentrado> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<CalificacionConcentrado> findByAlumnoId(Long alumnoId) {
        return jpaRepository.findByAlumnoId(alumnoId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CalificacionConcentrado> findByAgregadoId(Long agregadoId) {
        return jpaRepository.findByAgregadoId(agregadoId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CalificacionConcentrado> findByGrupoId(Long grupoId) {
        return jpaRepository.findByGrupoId(grupoId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CalificacionConcentrado> findByMateriaId(Long materiaId) {
        return jpaRepository.findByMateriaId(materiaId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CalificacionConcentrado> findByParcial(Integer parcial) {
        return jpaRepository.findByParcial(parcial).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CalificacionConcentrado> findByAlumnoIdAndAgregadoIdAndGrupoIdAndMateriaIdAndParcial(
            Long alumnoId, Long agregadoId, Long grupoId, Long materiaId, Integer parcial) {
        return jpaRepository.findByAlumnoIdAndAgregadoIdAndGrupoIdAndMateriaIdAndParcial(
                alumnoId, agregadoId, grupoId, materiaId, parcial)
                .map(this::toDomain);
    }

    @Override
    public List<CalificacionConcentrado> findByGrupoIdAndMateriaIdAndParcial(
            Long grupoId, Long materiaId, Integer parcial) {
        return jpaRepository.findByGrupoIdAndMateriaIdAndParcial(grupoId, materiaId, parcial).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convierte del modelo de dominio a la entidad JPA.
     */
    private CalificacionConcentradoEntity toEntity(CalificacionConcentrado calificacion) {
        return CalificacionConcentradoEntity.builder()
                .id(calificacion.getId())
                .alumnoId(calificacion.getAlumnoId())
                .agregadoId(calificacion.getAgregadoId())
                .criterioId(calificacion.getCriterioId())
                .grupoId(calificacion.getGrupoId())
                .materiaId(calificacion.getMateriaId())
                .parcial(calificacion.getParcial())
                .puntuacion(calificacion.getPuntuacion())
                .puntosParcial(calificacion.getPuntosParcial())
                .calificacionParcial(calificacion.getCalificacionParcial())
                .tipoEvaluacion(calificacion.getTipoEvaluacion())
                .build();
    }

    /**
     * Convierte de la entidad JPA al modelo de dominio.
     */
    private CalificacionConcentrado toDomain(CalificacionConcentradoEntity entity) {
        return CalificacionConcentrado.builder()
                .id(entity.getId())
                .alumnoId(entity.getAlumnoId())
                .agregadoId(entity.getAgregadoId())
                .criterioId(entity.getCriterioId())
                .grupoId(entity.getGrupoId())
                .materiaId(entity.getMateriaId())
                .parcial(entity.getParcial())
                .puntuacion(entity.getPuntuacion())
                .puntosParcial(entity.getPuntosParcial())
                .calificacionParcial(entity.getCalificacionParcial())
                .tipoEvaluacion(entity.getTipoEvaluacion())
                .build();
    }
}
