package com.alumnos.domain.port.out;

import com.alumnos.domain.model.Configuracion;

import java.util.Optional;

public interface ConfiguracionRepositoryPort {
    Configuracion save(Configuracion configuracion);
    Optional<Configuracion> findById(Long id);
    Optional<Configuracion> findFirst();
}
