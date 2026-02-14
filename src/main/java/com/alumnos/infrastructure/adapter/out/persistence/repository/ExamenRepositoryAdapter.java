package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.Examen;
import com.alumnos.domain.port.out.ExamenRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.ExamenEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExamenRepositoryAdapter implements ExamenRepositoryPort {

    private final ExamenJpaRepository examenJpaRepository;

    public ExamenRepositoryAdapter(ExamenJpaRepository examenJpaRepository) {
        this.examenJpaRepository = examenJpaRepository;
    }

    @Override
    public Examen save(Examen examen) {
        ExamenEntity entity = toEntity(examen);
        ExamenEntity savedEntity = examenJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Examen> findById(Long id) {
        return examenJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Examen> findAll() {
        return examenJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        examenJpaRepository.deleteById(id);
    }


    @Override
    public Optional<Examen> findByGrupoIdAndMateriaIdAndParcial(Long grupoId, Long materiaId, Integer parcial) {
        List<ExamenEntity> results = examenJpaRepository.findByGrupoIdAndMateriaIdAndParcial(grupoId, materiaId, parcial);
        // Tomar el primer resultado si hay múltiples (compatibilidad con datos antiguos)
        return results.isEmpty() ? Optional.empty() : Optional.of(toDomain(results.get(0)));
    }

    private ExamenEntity toEntity(Examen examen) {
        return ExamenEntity.builder()
                .id(examen.getId())
                .grupoId(examen.getGrupoId())
                .materiaId(examen.getMateriaId())
                .parcial(examen.getParcial())
                .totalPuntosExamen(examen.getTotalPuntosExamen())
                .fechaAplicacion(examen.getFechaAplicacion())
                .build();
    }

    private Examen toDomain(ExamenEntity entity) {
        return Examen.builder()
                .id(entity.getId())
                .grupoId(entity.getGrupoId())
                .materiaId(entity.getMateriaId())
                .parcial(entity.getParcial())
                .totalPuntosExamen(entity.getTotalPuntosExamen())
                .fechaAplicacion(entity.getFechaAplicacion())
                .build();
    }
}
