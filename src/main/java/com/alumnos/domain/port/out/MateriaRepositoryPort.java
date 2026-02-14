package com.alumnos.domain.port.out;

import com.alumnos.domain.model.Materia;

import java.util.List;
import java.util.Optional;

public interface MateriaRepositoryPort {
    Materia save(Materia materia);
    Optional<Materia> findById(Long id);
    List<Materia> findAll();
    void deleteById(Long id);
    List<Materia> findByNombreContaining(String nombre);
    boolean existsByCodigo(String codigo);
}
