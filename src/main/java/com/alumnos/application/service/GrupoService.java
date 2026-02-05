package com.alumnos.application.service;

import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.port.in.GrupoServicePort;
import com.alumnos.domain.port.in.AlumnoServicePort;
import com.alumnos.domain.port.in.GrupoMateriaServicePort;
import com.alumnos.domain.port.out.GrupoRepositoryPort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService implements GrupoServicePort {

    private final GrupoRepositoryPort grupoRepositoryPort;
    private final AlumnoServicePort alumnoService;
    private final GrupoMateriaServicePort grupoMateriaService;

    public GrupoService(GrupoRepositoryPort grupoRepositoryPort,
                        AlumnoServicePort alumnoService,
                        GrupoMateriaServicePort grupoMateriaService) {
        this.grupoRepositoryPort = grupoRepositoryPort;
        this.alumnoService = alumnoService;
        this.grupoMateriaService = grupoMateriaService;
    }

    @Override
    @Transactional
    @CacheEvict(value = "grupos", allEntries = true) // 🗑️ Limpia el caché al crear
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
    @Cacheable(value = "grupos", key = "#id") // 💾 Guarda en caché por ID
    @Transactional(readOnly = true)
    public Optional<Grupo> obtenerGrupoPorId(Long id) {
        return grupoRepositoryPort.findById(id);
    }

    @Override
    @Cacheable("grupos") // 💾 Guarda la lista completa en caché
    @Transactional(readOnly = true)
    public List<Grupo> obtenerTodosLosGrupos() {
        return grupoRepositoryPort.findAll();
    }

    @Override
    @Transactional
    @CacheEvict(value = "grupos", allEntries = true) // 🗑️ Limpia el caché al actualizar
    public Grupo actualizarGrupo(Grupo grupo) {
        if (grupo.getId() == null) {
            throw new IllegalArgumentException("El ID del grupo no puede ser nulo");
        }

        return grupoRepositoryPort.save(grupo);
    }

    @Override
    @Transactional
    @CacheEvict(value = "grupos", allEntries = true) // 🗑️ Limpia el caché al eliminar
    public void eliminarGrupo(Long id) {
        // Verificar si el grupo tiene alumnos asociados
        long cantidadAlumnos = alumnoService.obtenerTodosLosAlumnos().stream()
            .filter(alumno -> id.equals(alumno.getGrupoId()))
            .count();

        if (cantidadAlumnos > 0) {
            throw new IllegalStateException("No se puede eliminar el grupo porque tiene " + cantidadAlumnos + " alumno(s) asociado(s)");
        }

        // Verificar si el grupo tiene materias asignadas
        if (!grupoMateriaService.obtenerMateriasPorGrupo(id).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el grupo porque tiene materias asignadas");
        }

        grupoRepositoryPort.deleteById(id);
    }
}
