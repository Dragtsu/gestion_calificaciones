package com.alumnos.domain.port.in;

import com.alumnos.domain.model.Materia;

import java.util.List;
import java.util.Optional;

public interface MateriaServicePort {
    Materia crearMateria(Materia materia);
    Optional<Materia> obtenerMateriaPorId(Long id);
    List<Materia> obtenerTodasLasMaterias();
    Materia actualizarMateria(Materia materia);
    void eliminarMateria(Long id);
    List<Materia> buscarPorNombre(String nombre);
}
