package com.alumnos.domain.port.out;

import com.alumnos.domain.model.Agregado;

import java.util.List;
import java.util.Optional;

public interface AgregadoRepositoryPort {
    Agregado save(Agregado agregado);
    Optional<Agregado> findById(Long id);
    List<Agregado> findAll();
    void deleteById(Long id);
    List<Agregado> findByCriterioId(Long criterioId);
}
