package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.Criterio;
import com.alumnos.domain.port.out.CriterioRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.CriterioEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CriterioRepositoryAdapter implements CriterioRepositoryPort {

    private final CriterioJpaRepository criterioJpaRepository;

    public CriterioRepositoryAdapter(CriterioJpaRepository criterioJpaRepository) {
        this.criterioJpaRepository = criterioJpaRepository;
    }

    @Override
    public Criterio save(Criterio criterio) {
        CriterioEntity entity = toEntity(criterio);
        CriterioEntity savedEntity = criterioJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Criterio> findById(Long id) {
        return criterioJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Criterio> findAll() {
        return criterioJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        criterioJpaRepository.deleteById(id);
    }

    @Override
    public List<Criterio> findByMateriaId(Long materiaId) {
        return criterioJpaRepository.findByMateriaId(materiaId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // Mappers
    private CriterioEntity toEntity(Criterio criterio) {
        return CriterioEntity.builder()
                .id(criterio.getId())
                .nombre(criterio.getNombre())
                .tipoEvaluacion(criterio.getTipoEvaluacion())
                .puntuacionMaxima(criterio.getPuntuacionMaxima())
                .materiaId(criterio.getMateriaId())
                .orden(criterio.getOrden())
                .parcial(criterio.getParcial())
                .build();
    }

    private Criterio toDomain(CriterioEntity entity) {
        return Criterio.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .tipoEvaluacion(entity.getTipoEvaluacion())
                .puntuacionMaxima(entity.getPuntuacionMaxima())
                .materiaId(entity.getMateriaId())
                .orden(entity.getOrden())
                .parcial(entity.getParcial())
                .build();
    }
}
