package com.alumnos.domain.port.in;

import com.alumnos.domain.model.Grupo;

import java.util.List;
import java.util.Optional;

public interface GrupoServicePort {
    Grupo crearGrupo(Grupo grupo);
    Optional<Grupo> obtenerGrupoPorId(Long id);
    List<Grupo> obtenerTodosLosGrupos();
    Grupo actualizarGrupo(Grupo grupo);
    int eliminarGrupo(Long id);
    int eliminarGrupoCascada(Long id);
}
