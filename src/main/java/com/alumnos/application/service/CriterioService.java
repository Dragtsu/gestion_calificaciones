package com.alumnos.application.service;

import com.alumnos.domain.model.Criterio;
import com.alumnos.domain.port.in.CriterioServicePort;
import com.alumnos.domain.port.out.CriterioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CriterioService implements CriterioServicePort {

    private final CriterioRepositoryPort criterioRepositoryPort;

    public CriterioService(CriterioRepositoryPort criterioRepositoryPort) {
        this.criterioRepositoryPort = criterioRepositoryPort;
    }

    @Override
    public Criterio crearCriterio(Criterio criterio) {
        // Validaciones
        if (criterio.getNombre() == null || criterio.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del criterio es requerido");
        }
        if (criterio.getTipoEvaluacion() == null || criterio.getTipoEvaluacion().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de evaluación es requerido");
        }
        if (criterio.getMateriaId() == null) {
            throw new IllegalArgumentException("La materia es requerida");
        }
        if (criterio.getParcial() == null) {
            throw new IllegalArgumentException("El cuatrimestre es requerido");
        }

        // Si es tipo "Puntuacion", validar puntuación máxima
        if ("Puntuacion".equals(criterio.getTipoEvaluacion())) {
            if (criterio.getPuntuacionMaxima() == null || criterio.getPuntuacionMaxima() <= 0) {
                throw new IllegalArgumentException("La puntuación máxima debe ser mayor a 0");
            }
        }

        // Calcular orden automáticamente
        calcularOrden(criterio);
        Criterio criterioGuardado = criterioRepositoryPort.save(criterio);

        // Recalcular órdenes para todos los criterios de la misma materia
        recalcularOrdenes(criterio.getMateriaId());

        return criterioGuardado;
    }

    @Override
    public Optional<Criterio> obtenerCriterioPorId(Long id) {
        return criterioRepositoryPort.findById(id);
    }

    @Override
    public List<Criterio> obtenerTodosLosCriterios() {
        return criterioRepositoryPort.findAll();
    }

    @Override
    public Criterio actualizarCriterio(Criterio criterio) {
        if (criterio.getId() == null) {
            throw new IllegalArgumentException("El ID del criterio no puede ser nulo");
        }

        // Aplicar las mismas validaciones que en crear
        if (criterio.getNombre() == null || criterio.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del criterio es requerido");
        }
        if (criterio.getTipoEvaluacion() == null || criterio.getTipoEvaluacion().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de evaluación es requerido");
        }
        if (criterio.getMateriaId() == null) {
            throw new IllegalArgumentException("La materia es requerida");
        }
        if (criterio.getParcial() == null) {
            throw new IllegalArgumentException("El parcial es requerido");
        }

        if ("Puntuacion".equals(criterio.getTipoEvaluacion())) {
            if (criterio.getPuntuacionMaxima() == null || criterio.getPuntuacionMaxima() <= 0) {
                throw new IllegalArgumentException("La puntuación máxima debe ser mayor a 0");
            }
        }

        // Obtener criterio anterior para verificar si cambió materia o parcial
        Criterio criterioAnterior = criterioRepositoryPort.findById(criterio.getId()).orElse(null);
        Long materiaAnterior = criterioAnterior != null ? criterioAnterior.getMateriaId() : null;

        // Calcular orden
        calcularOrden(criterio);
        Criterio criterioActualizado = criterioRepositoryPort.save(criterio);

        // Recalcular órdenes para la materia actual (esto recalculará todos los cuatrimestres de la materia)
        recalcularOrdenes(criterio.getMateriaId());

        // Si cambió de materia, recalcular también la materia anterior
        if (materiaAnterior != null && !materiaAnterior.equals(criterio.getMateriaId())) {
            recalcularOrdenes(materiaAnterior);
        }

        return criterioActualizado;
    }

    @Override
    public void eliminarCriterio(Long id) {
        // Obtener materia antes de eliminar
        Criterio criterio = criterioRepositoryPort.findById(id).orElse(null);
        Long materiaId = criterio != null ? criterio.getMateriaId() : null;

        criterioRepositoryPort.deleteById(id);

        // Recalcular órdenes para la materia
        if (materiaId != null) {
            recalcularOrdenes(materiaId);
        }
    }

    @Override
    public List<Criterio> obtenerCriteriosPorMateria(Long materiaId) {
        return criterioRepositoryPort.findByMateriaId(materiaId);
    }

    @Override
    public List<Criterio> obtenerCriteriosPorParcial(Integer parcial) {
        return criterioRepositoryPort.findAll().stream()
                .filter(criterio -> criterio.getParcial().equals(parcial))
                .collect(Collectors.toList());
    }

    /**
     * Calcula el orden para un criterio basado en su materia y parcial
     */
    private void calcularOrden(Criterio criterio) {
        if (criterio.getMateriaId() == null || criterio.getParcial() == null) {
            criterio.setOrden(null);
            return;
        }

        // Obtener todos los criterios de la misma materia y parcial
        List<Criterio> criteriosDeLaMateriaYParcial = criterioRepositoryPort.findAll().stream()
                .filter(c -> criterio.getMateriaId().equals(c.getMateriaId()))
                .filter(c -> criterio.getParcial().equals(c.getParcial()))
                .filter(c -> !c.getId().equals(criterio.getId())) // Excluir el criterio actual
                .toList();

        // El orden es la cantidad de criterios + 1
        criterio.setOrden(criteriosDeLaMateriaYParcial.size() + 1);
    }

    /**
     * Recalcula los órdenes para todos los criterios de una materia agrupados por parcial
     */
    private void recalcularOrdenes(Long materiaId) {
        if (materiaId == null) return;

        // Obtener todos los criterios de la materia
        List<Criterio> criteriosDeLaMateria = criterioRepositoryPort.findAll().stream()
                .filter(c -> materiaId.equals(c.getMateriaId()))
                .toList();

        // Agrupar por parcial y recalcular orden dentro de cada grupo
        Map<Integer, List<Criterio>> criteriosPorParcial = criteriosDeLaMateria.stream()
                .filter(c -> c.getParcial() != null)
                .collect(Collectors.groupingBy(Criterio::getParcial));

        // Para cada parcial, asignar órdenes secuenciales
        for (Map.Entry<Integer, List<Criterio>> entry : criteriosPorParcial.entrySet()) {
            List<Criterio> criteriosDelParcial = entry.getValue();
            criteriosDelParcial.sort((c1, c2) -> {
                // Ordenar primero por orden existente, luego por ID
                if (c1.getOrden() != null && c2.getOrden() != null) {
                    return Integer.compare(c1.getOrden(), c2.getOrden());
                }
                return Long.compare(c1.getId(), c2.getId());
            });

            // Asignar órdenes secuenciales dentro del parcial
            int orden = 1;
            for (Criterio criterio : criteriosDelParcial) {
                criterio.setOrden(orden++);
                criterioRepositoryPort.save(criterio);
            }
        }
    }

    @Override
    public void actualizarOrdenCriterio(Long criterioId, Integer nuevoOrden) {
        Criterio criterio = criterioRepositoryPort.findById(criterioId)
                .orElseThrow(() -> new IllegalArgumentException("Criterio no encontrado"));
        criterio.setOrden(nuevoOrden);
        criterioRepositoryPort.save(criterio);
    }
}
