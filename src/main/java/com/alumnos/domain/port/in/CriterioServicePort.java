package com.alumnos.domain.port.in;

import com.alumnos.domain.model.Criterio;

import java.util.List;
import java.util.Optional;

public interface CriterioServicePort {
    Criterio crearCriterio(Criterio criterio);
    Optional<Criterio> obtenerCriterioPorId(Long id);
    List<Criterio> obtenerTodosLosCriterios();
    Criterio actualizarCriterio(Criterio criterio);
    void eliminarCriterio(Long id);
    List<Criterio> obtenerCriteriosPorMateria(Long materiaId);
    void actualizarOrdenCriterio(Long criterioId, Integer nuevoOrden);
    List<Criterio> obtenerCriteriosPorCuatrimestre(Integer cuatrimestre);
}
