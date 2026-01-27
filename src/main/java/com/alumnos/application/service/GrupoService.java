package com.alumnos.application.service;

import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.port.in.GrupoServicePort;
import com.alumnos.domain.port.out.GrupoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GrupoService implements GrupoServicePort {

    private final GrupoRepositoryPort grupoRepositoryPort;

    public GrupoService(GrupoRepositoryPort grupoRepositoryPort) {
        this.grupoRepositoryPort = grupoRepositoryPort;
    }

    @Override
    public Grupo crearGrupo(Grupo grupo) {
        // Validar que el ID no sea nulo
        if (grupo.getId() == null) {
            throw new IllegalArgumentException("El código del grupo es requerido");
        }

        // Validar que el ID no exista
        if (grupoRepositoryPort.existsById(grupo.getId())) {
            throw new IllegalArgumentException("El grupo ya existe");
        }

        return grupoRepositoryPort.save(grupo);
    }

    @Override
    public Optional<Grupo> obtenerGrupoPorId(Long id) {
        return grupoRepositoryPort.findById(id);
    }

    @Override
    public List<Grupo> obtenerTodosLosGrupos() {
        return grupoRepositoryPort.findAll();
    }

    @Override
    public Grupo actualizarGrupo(Grupo grupo) {
        if (grupo.getId() == null) {
            throw new IllegalArgumentException("El ID del grupo no puede ser nulo");
        }

        return grupoRepositoryPort.save(grupo);
    }

    @Override
    public void eliminarGrupo(Long id) {
        grupoRepositoryPort.deleteById(id);
    }
}
