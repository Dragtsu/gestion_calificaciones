package com.alumnos.domain.port.in;

import com.alumnos.domain.model.Configuracion;

import java.util.Optional;

public interface ConfiguracionServicePort {
    Configuracion guardarConfiguracion(Configuracion configuracion);
    Optional<Configuracion> obtenerConfiguracion();
}
