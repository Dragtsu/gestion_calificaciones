package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.port.out.GrupoRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.GrupoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GrupoRepositoryAdapter implements GrupoRepositoryPort {

    private final GrupoJpaRepository grupoJpaRepository;

    public GrupoRepositoryAdapter(GrupoJpaRepository grupoJpaRepository) {
        this.grupoJpaRepository = grupoJpaRepository;
    }

    @Override
    public Grupo save(Grupo grupo) {
        GrupoEntity entity = toEntity(grupo);
        GrupoEntity savedEntity = grupoJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Grupo> findById(Long id) {
        return grupoJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Grupo> findAll() {
        return grupoJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        grupoJpaRepository.deleteById(id);
    }


    @Override
    public boolean existsById(Long id) {
        return grupoJpaRepository.existsById(id);
    }

    // Mappers
    private GrupoEntity toEntity(Grupo grupo) {
        GrupoEntity entity = GrupoEntity.builder()
                .id(grupo.getId())
                .build();
        // Marcar como nueva entidad si no tiene ID previo o si es una nueva inserción
        entity.setNew(true);
        return entity;
    }

    private Grupo toDomain(GrupoEntity entity) {
        return Grupo.builder()
                .id(entity.getId())
                .build();
    }
}
