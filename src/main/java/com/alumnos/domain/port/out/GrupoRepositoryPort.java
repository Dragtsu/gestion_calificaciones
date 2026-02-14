package com.alumnos.domain.port.out;

import com.alumnos.domain.model.Grupo;

import java.util.List;
import java.util.Optional;

public interface GrupoRepositoryPort {
    Grupo save(Grupo grupo);
    Optional<Grupo> findById(Long id);
    List<Grupo> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
