package com.alumnos.application.service;

import com.alumnos.domain.model.Alumno;
import com.alumnos.domain.port.in.AlumnoServicePort;
import com.alumnos.domain.port.in.CalificacionServicePort;
import com.alumnos.domain.port.in.CalificacionConcentradoServicePort;
import com.alumnos.domain.port.in.AlumnoExamenServicePort;
import com.alumnos.domain.port.out.AlumnoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AlumnoService implements AlumnoServicePort {

    private final AlumnoRepositoryPort alumnoRepositoryPort;
    private final CalificacionServicePort calificacionService;
    private final CalificacionConcentradoServicePort calificacionConcentradoService;
    private final AlumnoExamenServicePort alumnoExamenService;

    public AlumnoService(AlumnoRepositoryPort alumnoRepositoryPort,
                         CalificacionServicePort calificacionService,
                         CalificacionConcentradoServicePort calificacionConcentradoService,
                         AlumnoExamenServicePort alumnoExamenService) {
        this.alumnoRepositoryPort = alumnoRepositoryPort;
        this.calificacionService = calificacionService;
        this.calificacionConcentradoService = calificacionConcentradoService;
        this.alumnoExamenService = alumnoExamenService;
    }

    @Override
    @Transactional
    public Alumno crearAlumno(Alumno alumno) {
        // Capitalizar la primera letra de los campos de texto
        normalizarNombres(alumno);

        // Calcular el número de lista antes de guardar
        calcularNumeroLista(alumno);
        Alumno alumnoGuardado = alumnoRepositoryPort.save(alumno);

        // Recalcular números de lista para todos los alumnos del mismo grupo
        if (alumno.getGrupoId() != null) {
            recalcularNumerosLista(alumno.getGrupoId());
        }

        return alumnoGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Alumno> obtenerAlumnoPorId(Long id) {
        return alumnoRepositoryPort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Alumno> obtenerTodosLosAlumnos() {
        return alumnoRepositoryPort.findAll();
    }

    @Override
    @Transactional
    public Alumno actualizarAlumno(Alumno alumno) {
        if (alumno.getId() == null) {
            throw new IllegalArgumentException("El ID del alumno no puede ser nulo");
        }

        // Capitalizar la primera letra de los campos de texto
        normalizarNombres(alumno);

        // Obtener el alumno anterior para verificar si cambió de grupo
        Optional<Alumno> alumnoAnterior = alumnoRepositoryPort.findById(alumno.getId());
        Long grupoAnterior = alumnoAnterior.map(Alumno::getGrupoId).orElse(null);

        // Calcular el número de lista antes de actualizar
        calcularNumeroLista(alumno);
        Alumno alumnoActualizado = alumnoRepositoryPort.save(alumno);

        // Recalcular números de lista para el grupo actual
        if (alumno.getGrupoId() != null) {
            recalcularNumerosLista(alumno.getGrupoId());
        }

        // Si cambió de grupo, recalcular también el grupo anterior
        if (grupoAnterior != null && !grupoAnterior.equals(alumno.getGrupoId())) {
            recalcularNumerosLista(grupoAnterior);
        }

        return alumnoActualizado;
    }

    @Override
    @Transactional
    public void eliminarAlumno(Long id) {
        // Verificar si el alumno tiene calificaciones asociadas
        if (!calificacionService.obtenerCalificacionesPorAlumno(id).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el alumno porque tiene calificaciones registradas");
        }

        // Verificar si el alumno tiene calificaciones en el concentrado
        if (!calificacionConcentradoService.obtenerCalificacionesPorAlumno(id).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el alumno porque tiene calificaciones en el concentrado");
        }

        // Verificar si el alumno tiene exámenes asociados
        if (!alumnoExamenService.obtenerAlumnoExamenPorAlumno(id).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el alumno porque tiene exámenes registrados");
        }

        // Obtener el grupo antes de eliminar para recalcular
        Optional<Alumno> alumno = alumnoRepositoryPort.findById(id);
        Long grupoId = alumno.map(Alumno::getGrupoId).orElse(null);

        alumnoRepositoryPort.deleteById(id);

        // Recalcular números de lista para el grupo
        if (grupoId != null) {
            recalcularNumerosLista(grupoId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Alumno> buscarPorNombre(String nombre) {
        // Agregar comodines % al inicio y final para búsqueda parcial
        String nombreConComodines = "%" + nombre + "%";
        return alumnoRepositoryPort.findByNombreContaining(nombreConComodines);
    }

    /**
     * Calcula el número de lista para un alumno basado en su grupo y orden alfabético
     */
    private void calcularNumeroLista(Alumno alumno) {
        if (alumno.getGrupoId() == null) {
            alumno.setNumeroLista(null);
            return;
        }

        // Obtener todos los alumnos del mismo grupo
        List<Alumno> alumnosDelGrupo = alumnoRepositoryPort.findAll().stream()
                .filter(a -> alumno.getGrupoId().equals(a.getGrupoId()))
                .filter(a -> !a.getId().equals(alumno.getId())) // Excluir el alumno actual
                .sorted((a1, a2) -> {
                    // Ordenar por: Apellido Paterno - Apellido Materno - Nombre
                    int comparacion = compararCadenas(a1.getApellidoPaterno(), a2.getApellidoPaterno());
                    if (comparacion != 0) return comparacion;

                    comparacion = compararCadenas(a1.getApellidoMaterno(), a2.getApellidoMaterno());
                    if (comparacion != 0) return comparacion;

                    return compararCadenas(a1.getNombre(), a2.getNombre());
                })
                .toList();

        // Buscar la posición del alumno actual en orden alfabético
        int posicion = 1;
        for (Alumno a : alumnosDelGrupo) {
            int comparacion = compararAlumnos(alumno, a);
            if (comparacion > 0) {
                posicion++;
            }
        }

        alumno.setNumeroLista(posicion);
    }

    /**
     * Recalcula los números de lista para todos los alumnos de un grupo
     */
    private void recalcularNumerosLista(Long grupoId) {
        if (grupoId == null) return;

        // Obtener todos los alumnos del grupo ordenados alfabéticamente
        List<Alumno> alumnosDelGrupo = alumnoRepositoryPort.findAll().stream()
                .filter(a -> grupoId.equals(a.getGrupoId()))
                .sorted((a1, a2) -> compararAlumnos(a1, a2))
                .toList();

        // Asignar números de lista secuenciales
        int numeroLista = 1;
        for (Alumno alumno : alumnosDelGrupo) {
            alumno.setNumeroLista(numeroLista++);
            alumnoRepositoryPort.save(alumno);
        }
    }

    /**
     * Compara dos alumnos por orden alfabético: Apellido Paterno - Apellido Materno - Nombre
     */
    private int compararAlumnos(Alumno a1, Alumno a2) {
        int comparacion = compararCadenas(a1.getApellidoPaterno(), a2.getApellidoPaterno());
        if (comparacion != 0) return comparacion;

        comparacion = compararCadenas(a1.getApellidoMaterno(), a2.getApellidoMaterno());
        if (comparacion != 0) return comparacion;

        return compararCadenas(a1.getNombre(), a2.getNombre());
    }

    /**
     * Compara dos cadenas de forma segura (maneja nulls)
     */
    private int compararCadenas(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;
        return s1.compareToIgnoreCase(s2);
    }

    /**
     * Normaliza los nombres del alumno capitalizando la primera letra
     */
    private void normalizarNombres(Alumno alumno) {
        if (alumno.getNombre() != null) {
            alumno.setNombre(capitalizarPrimeraLetra(alumno.getNombre()));
        }
        if (alumno.getApellidoPaterno() != null) {
            alumno.setApellidoPaterno(capitalizarPrimeraLetra(alumno.getApellidoPaterno()));
        }
        if (alumno.getApellidoMaterno() != null) {
            alumno.setApellidoMaterno(capitalizarPrimeraLetra(alumno.getApellidoMaterno()));
        }
    }

    /**
     * Capitaliza la primera letra de un texto y el resto en minúsculas
     * Preserva los acentos correctamente
     */
    private String capitalizarPrimeraLetra(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        // Convertir todo a minúsculas y capitalizar la primera letra
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}
