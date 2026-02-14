package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.Configuracion;
import com.alumnos.domain.port.out.ConfiguracionRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.ConfiguracionEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ConfiguracionRepositoryAdapter implements ConfiguracionRepositoryPort {

    private final ConfiguracionJpaRepository configuracionJpaRepository;

    public ConfiguracionRepositoryAdapter(ConfiguracionJpaRepository configuracionJpaRepository) {
        this.configuracionJpaRepository = configuracionJpaRepository;
    }

    @Override
    public Configuracion save(Configuracion configuracion) {
        ConfiguracionEntity entity = toEntity(configuracion);
        ConfiguracionEntity savedEntity = configuracionJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Configuracion> findById(Long id) {
        return configuracionJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Configuracion> findFirst() {
        return configuracionJpaRepository.findFirstByOrderByIdAsc()
                .map(this::toDomain);
    }

    // Mappers
    private ConfiguracionEntity toEntity(Configuracion configuracion) {
        return ConfiguracionEntity.builder()
                .id(configuracion.getId())
                .nombreMaestro(configuracion.getNombreMaestro())
                .build();
    }

    private Configuracion toDomain(ConfiguracionEntity entity) {
        return Configuracion.builder()
                .id(entity.getId())
                .nombreMaestro(entity.getNombreMaestro())
                .build();
    }
}
