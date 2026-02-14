package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.Agregado;
import com.alumnos.domain.port.out.AgregadoRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.AgregadoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AgregadoRepositoryAdapter implements AgregadoRepositoryPort {

    private final AgregadoJpaRepository agregadoJpaRepository;

    public AgregadoRepositoryAdapter(AgregadoJpaRepository agregadoJpaRepository) {
        this.agregadoJpaRepository = agregadoJpaRepository;
    }

    @Override
    public Agregado save(Agregado agregado) {
        AgregadoEntity entity = toEntity(agregado);
        AgregadoEntity savedEntity = agregadoJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Agregado> findById(Long id) {
        return agregadoJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Agregado> findAll() {
        return agregadoJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        agregadoJpaRepository.deleteById(id);
    }

    @Override
    public List<Agregado> findByCriterioId(Long criterioId) {
        return agregadoJpaRepository.findByCriterioId(criterioId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // Mappers
    private AgregadoEntity toEntity(Agregado agregado) {
        return AgregadoEntity.builder()
                .id(agregado.getId())
                .nombre(agregado.getNombre())
                .descripcion(agregado.getDescripcion())
                .criterioId(agregado.getCriterioId())
                .orden(agregado.getOrden())
                .build();
    }

    private Agregado toDomain(AgregadoEntity entity) {
        return Agregado.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .criterioId(entity.getCriterioId())
                .orden(entity.getOrden())
                .build();
    }
}
