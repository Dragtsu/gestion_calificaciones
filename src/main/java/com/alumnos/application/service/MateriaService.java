package com.alumnos.application.service;

import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.MateriaServicePort;
import com.alumnos.domain.port.in.GrupoMateriaServicePort;
import com.alumnos.domain.port.out.MateriaRepositoryPort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaService implements MateriaServicePort {

    private final MateriaRepositoryPort materiaRepositoryPort;
    private final GrupoMateriaServicePort grupoMateriaService;

    public MateriaService(MateriaRepositoryPort materiaRepositoryPort,
                          GrupoMateriaServicePort grupoMateriaService) {
        this.materiaRepositoryPort = materiaRepositoryPort;
        this.grupoMateriaService = grupoMateriaService;
    }

    @Override
    @Transactional
    @CacheEvict(value = "materias", allEntries = true) // 🗑️ Limpia el caché al crear
    public Materia crearMateria(Materia materia) {
        // El id se genera automáticamente por la base de datos
        return materiaRepositoryPort.save(materia);
    }

    @Override
    @Cacheable(value = "materias", key = "#id") // 💾 Guarda en caché por ID
    @Transactional(readOnly = true)
    public Optional<Materia> obtenerMateriaPorId(Long id) {
        return materiaRepositoryPort.findById(id);
    }

    @Override
    @Cacheable("materias") // 💾 Guarda la lista completa en caché
    @Transactional(readOnly = true)
    public List<Materia> obtenerTodasLasMaterias() {
        return materiaRepositoryPort.findAll();
    }

    @Override
    @Transactional
    @CacheEvict(value = "materias", allEntries = true) // 🗑️ Limpia el caché al actualizar
    public Materia actualizarMateria(Materia materia) {
        if (materia.getId() == null) {
            throw new IllegalArgumentException("El ID de la materia no puede ser nulo");
        }
        return materiaRepositoryPort.save(materia);
    }

    @Override
    @Transactional
    @CacheEvict(value = "materias", allEntries = true) // 🗑️ Limpia el caché al eliminar
    public void eliminarMateria(Long id) {
        // Verificar si la materia tiene asignaciones a grupos
        if (!grupoMateriaService.obtenerGruposPorMateria(id).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar la materia porque está asignada a uno o más grupos");
        }

        materiaRepositoryPort.deleteById(id);
    }

    @Override
    public List<Materia> buscarPorNombre(String nombre) {
        // Agregar comodines % al inicio y final para búsqueda parcial
        String nombreConComodines = "%" + nombre + "%";
        return materiaRepositoryPort.findByNombreContaining(nombreConComodines);
    }
}
