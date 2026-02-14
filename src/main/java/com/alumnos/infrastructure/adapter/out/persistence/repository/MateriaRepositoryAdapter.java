package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.out.MateriaRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.MateriaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MateriaRepositoryAdapter implements MateriaRepositoryPort {

    private final MateriaJpaRepository materiaJpaRepository;

    public MateriaRepositoryAdapter(MateriaJpaRepository materiaJpaRepository) {
        this.materiaJpaRepository = materiaJpaRepository;
    }

    @Override
    public Materia save(Materia materia) {
        MateriaEntity entity = toEntity(materia);
        MateriaEntity savedEntity = materiaJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Materia> findById(Long id) {
        return materiaJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Materia> findAll() {
        return materiaJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        materiaJpaRepository.deleteById(id);
    }

    @Override
    public List<Materia> findByNombreContaining(String nombre) {
        return materiaJpaRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        // Verifica si existe una materia con el código dado (usando el ID)
        try {
            Long id = Long.parseLong(codigo);
            return materiaJpaRepository.existsById(id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Mappers
    private MateriaEntity toEntity(Materia materia) {
        return MateriaEntity.builder()
                .id(materia.getId())
                .nombre(materia.getNombre())
                .build();
    }

    private Materia toDomain(MateriaEntity entity) {
        return Materia.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }
}
