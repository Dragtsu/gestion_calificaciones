package com.alumnos.domain.port.in;

import com.alumnos.domain.model.GrupoMateria;

import java.util.List;
import java.util.Optional;

public interface GrupoMateriaServicePort {
    GrupoMateria asignarMateriaAGrupo(GrupoMateria grupoMateria);
    Optional<GrupoMateria> obtenerAsignacionPorId(Long id);
    List<GrupoMateria> obtenerTodasLasAsignaciones();
    void eliminarAsignacion(Long id);
    List<GrupoMateria> obtenerMateriasPorGrupo(Long grupoId);
    List<GrupoMateria> obtenerGruposPorMateria(Long materiaId);
}
