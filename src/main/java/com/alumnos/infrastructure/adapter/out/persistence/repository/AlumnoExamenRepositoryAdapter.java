package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.AlumnoExamen;
import com.alumnos.domain.port.out.AlumnoExamenRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.AlumnoExamenEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AlumnoExamenRepositoryAdapter implements AlumnoExamenRepositoryPort {

    private final AlumnoExamenJpaRepository alumnoExamenJpaRepository;

    public AlumnoExamenRepositoryAdapter(AlumnoExamenJpaRepository alumnoExamenJpaRepository) {
        this.alumnoExamenJpaRepository = alumnoExamenJpaRepository;
    }

    @Override
    public AlumnoExamen save(AlumnoExamen alumnoExamen) {
        AlumnoExamenEntity entity = toEntity(alumnoExamen);
        AlumnoExamenEntity savedEntity = alumnoExamenJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<AlumnoExamen> findById(Long id) {
        return alumnoExamenJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<AlumnoExamen> findAll() {
        return alumnoExamenJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        alumnoExamenJpaRepository.deleteById(id);
    }

    @Override
    public List<AlumnoExamen> findByAlumnoId(Long alumnoId) {
        return alumnoExamenJpaRepository.findByAlumnoId(alumnoId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlumnoExamen> findByExamenId(Long examenId) {
        return alumnoExamenJpaRepository.findByExamenId(examenId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AlumnoExamen> findByAlumnoIdAndExamenId(Long alumnoId, Long examenId) {
        return alumnoExamenJpaRepository.findByAlumnoIdAndExamenId(alumnoId, examenId)
                .map(this::toDomain);
    }

    private AlumnoExamenEntity toEntity(AlumnoExamen alumnoExamen) {
        return AlumnoExamenEntity.builder()
                .id(alumnoExamen.getId())
                .alumnoId(alumnoExamen.getAlumnoId())
                .examenId(alumnoExamen.getExamenId())
                .puntosExamen(alumnoExamen.getPuntosExamen())
                .porcentaje(alumnoExamen.getPorcentaje())
                .calificacion(alumnoExamen.getCalificacion())
                .build();
    }

    private AlumnoExamen toDomain(AlumnoExamenEntity entity) {
        return AlumnoExamen.builder()
                .id(entity.getId())
                .alumnoId(entity.getAlumnoId())
                .examenId(entity.getExamenId())
                .puntosExamen(entity.getPuntosExamen())
                .porcentaje(entity.getPorcentaje())
                .calificacion(entity.getCalificacion())
                .build();
    }
}
