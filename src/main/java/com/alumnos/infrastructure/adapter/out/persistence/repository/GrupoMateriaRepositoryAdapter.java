package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.GrupoMateria;
import com.alumnos.domain.port.out.GrupoMateriaRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.GrupoMateriaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GrupoMateriaRepositoryAdapter implements GrupoMateriaRepositoryPort {

    private final GrupoMateriaJpaRepository grupoMateriaJpaRepository;

    public GrupoMateriaRepositoryAdapter(GrupoMateriaJpaRepository grupoMateriaJpaRepository) {
        this.grupoMateriaJpaRepository = grupoMateriaJpaRepository;
    }

    @Override
    public GrupoMateria save(GrupoMateria grupoMateria) {
        GrupoMateriaEntity entity = toEntity(grupoMateria);
        GrupoMateriaEntity savedEntity = grupoMateriaJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<GrupoMateria> findById(Long id) {
        return grupoMateriaJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<GrupoMateria> findAll() {
        return grupoMateriaJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        grupoMateriaJpaRepository.deleteById(id);
    }

    @Override
    public List<GrupoMateria> findByGrupoId(Long grupoId) {
        return grupoMateriaJpaRepository.findByGrupoId(grupoId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GrupoMateria> findByMateriaId(Long materiaId) {
        return grupoMateriaJpaRepository.findByMateriaId(materiaId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByGrupoIdAndMateriaId(Long grupoId, Long materiaId) {
        return grupoMateriaJpaRepository.existsByGrupoIdAndMateriaId(grupoId, materiaId);
    }

    // Mappers
    private GrupoMateriaEntity toEntity(GrupoMateria grupoMateria) {
        return GrupoMateriaEntity.builder()
                .id(grupoMateria.getId())
                .grupoId(grupoMateria.getGrupoId())
                .materiaId(grupoMateria.getMateriaId())
                .build();
    }

    private GrupoMateria toDomain(GrupoMateriaEntity entity) {
        return GrupoMateria.builder()
                .id(entity.getId())
                .grupoId(entity.getGrupoId())
                .materiaId(entity.getMateriaId())
                .build();
    }
}
