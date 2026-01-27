package com.alumnos.domain.port.in;

import com.alumnos.domain.model.Agregado;

import java.util.List;
import java.util.Optional;

public interface AgregadoServicePort {
    Agregado crearAgregado(Agregado agregado);
    Optional<Agregado> obtenerAgregadoPorId(Long id);
    List<Agregado> obtenerTodosLosAgregados();
    Agregado actualizarAgregado(Agregado agregado);
    void eliminarAgregado(Long id);
    List<Agregado> obtenerAgregadosPorCriterio(Long criterioId);
    void actualizarOrdenAgregado(Long agregadoId, Integer nuevoOrden);
}
