package com.alumnos.domain.port.out;

import com.alumnos.domain.model.Criterio;

import java.util.List;
import java.util.Optional;

public interface CriterioRepositoryPort {
    Criterio save(Criterio criterio);
    Optional<Criterio> findById(Long id);
    List<Criterio> findAll();
    void deleteById(Long id);
    List<Criterio> findByMateriaId(Long materiaId);
}
