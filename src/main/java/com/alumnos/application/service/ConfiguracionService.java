package com.alumnos.application.service;

import com.alumnos.domain.model.Configuracion;
import com.alumnos.domain.port.in.ConfiguracionServicePort;
import com.alumnos.domain.port.out.ConfiguracionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ConfiguracionService implements ConfiguracionServicePort {

    private final ConfiguracionRepositoryPort configuracionRepositoryPort;

    public ConfiguracionService(ConfiguracionRepositoryPort configuracionRepositoryPort) {
        this.configuracionRepositoryPort = configuracionRepositoryPort;
    }

    @Override
    public Configuracion guardarConfiguracion(Configuracion configuracion) {
        // Si no tiene ID, asignar 1 (solo habrá un registro)
        if (configuracion.getId() == null) {
            configuracion.setId(1L);
        }
        return configuracionRepositoryPort.save(configuracion);
    }

    @Override
    public Optional<Configuracion> obtenerConfiguracion() {
        return configuracionRepositoryPort.findFirst();
    }
}
