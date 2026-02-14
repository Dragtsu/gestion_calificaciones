package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.Calificacion;
import com.alumnos.domain.port.out.CalificacionRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.CalificacionEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CalificacionRepositoryAdapter implements CalificacionRepositoryPort {

    private final CalificacionJpaRepository calificacionJpaRepository;

    public CalificacionRepositoryAdapter(CalificacionJpaRepository calificacionJpaRepository) {
        this.calificacionJpaRepository = calificacionJpaRepository;
    }

    @Override
    public Calificacion save(Calificacion calificacion) {
        CalificacionEntity entity = toEntity(calificacion);
        CalificacionEntity savedEntity = calificacionJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Calificacion> findById(Long id) {
        return calificacionJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Calificacion> findAll() {
        return calificacionJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        calificacionJpaRepository.deleteById(id);
    }

    @Override
    public List<Calificacion> findByAlumnoId(Long alumnoId) {
        return calificacionJpaRepository.findByAlumnoId(alumnoId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Calificacion> findByAgregadoId(Long agregadoId) {
        return calificacionJpaRepository.findByAgregadoId(agregadoId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Calificacion> findByAlumnoIdAndAgregadoId(Long alumnoId, Long agregadoId) {
        return calificacionJpaRepository.findByAlumnoIdAndAgregadoId(alumnoId, agregadoId)
                .map(this::toDomain);
    }

    private CalificacionEntity toEntity(Calificacion calificacion) {
        return CalificacionEntity.builder()
                .id(calificacion.getId())
                .alumnoId(calificacion.getAlumnoId())
                .agregadoId(calificacion.getAgregadoId())
                .puntuacion(calificacion.getPuntuacion())
                .build();
    }

    private Calificacion toDomain(CalificacionEntity entity) {
        return Calificacion.builder()
                .id(entity.getId())
                .alumnoId(entity.getAlumnoId())
                .agregadoId(entity.getAgregadoId())
                .puntuacion(entity.getPuntuacion())
                .build();
    }
}
