package com.alumnos.domain.port.out;

import com.alumnos.domain.model.Examen;

import java.util.List;
import java.util.Optional;

public interface ExamenRepositoryPort {
    Examen save(Examen examen);
    Optional<Examen> findById(Long id);
    List<Examen> findAll();
    void deleteById(Long id);
    Optional<Examen> findByGrupoIdAndMateriaIdAndParcial(Long grupoId, Long materiaId, Integer parcial);
}
