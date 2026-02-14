package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.domain.model.Alumno;
import com.alumnos.domain.port.out.AlumnoRepositoryPort;
import com.alumnos.infrastructure.adapter.out.persistence.entity.AlumnoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AlumnoRepositoryAdapter implements AlumnoRepositoryPort {

    private final AlumnoJpaRepository alumnoJpaRepository;

    public AlumnoRepositoryAdapter(AlumnoJpaRepository alumnoJpaRepository) {
        this.alumnoJpaRepository = alumnoJpaRepository;
    }

    @Override
    public Alumno save(Alumno alumno) {
        AlumnoEntity entity = toEntity(alumno);
        AlumnoEntity savedEntity = alumnoJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Alumno> findById(Long id) {
        return alumnoJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Alumno> findAll() {
        return alumnoJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        alumnoJpaRepository.deleteById(id);
    }

    @Override
    public List<Alumno> findByNombreContaining(String nombre) {
        return alumnoJpaRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }


    // Mappers
    private AlumnoEntity toEntity(Alumno alumno) {
        return AlumnoEntity.builder()
                .id(alumno.getId())
                .nombre(alumno.getNombre())
                .apellidoPaterno(alumno.getApellidoPaterno())
                .apellidoMaterno(alumno.getApellidoMaterno())
                .grupoId(alumno.getGrupoId())
                .numeroLista(alumno.getNumeroLista())
                .build();
    }

    private Alumno toDomain(AlumnoEntity entity) {
        return Alumno.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellidoPaterno(entity.getApellidoPaterno())
                .apellidoMaterno(entity.getApellidoMaterno())
                .grupoId(entity.getGrupoId())
                .numeroLista(entity.getNumeroLista())
                .build();
    }
}
