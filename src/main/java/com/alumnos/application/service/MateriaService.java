package com.alumnos.application.service;

import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.MateriaServicePort;
import com.alumnos.domain.port.out.MateriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MateriaService implements MateriaServicePort {

    private final MateriaRepositoryPort materiaRepositoryPort;

    public MateriaService(MateriaRepositoryPort materiaRepositoryPort) {
        this.materiaRepositoryPort = materiaRepositoryPort;
    }

    @Override
    public Materia crearMateria(Materia materia) {
        // El id se genera automáticamente por la base de datos
        return materiaRepositoryPort.save(materia);
    }

    @Override
    public Optional<Materia> obtenerMateriaPorId(Long id) {
        return materiaRepositoryPort.findById(id);
    }

    @Override
    public List<Materia> obtenerTodasLasMaterias() {
        return materiaRepositoryPort.findAll();
    }

    @Override
    public Materia actualizarMateria(Materia materia) {
        if (materia.getId() == null) {
            throw new IllegalArgumentException("El ID de la materia no puede ser nulo");
        }
        return materiaRepositoryPort.save(materia);
    }

    @Override
    public void eliminarMateria(Long id) {
        materiaRepositoryPort.deleteById(id);
    }

    @Override
    public List<Materia> buscarPorNombre(String nombre) {
        // Agregar comodines % al inicio y final para búsqueda parcial
        String nombreConComodines = "%" + nombre + "%";
        return materiaRepositoryPort.findByNombreContaining(nombreConComodines);
    }
}
