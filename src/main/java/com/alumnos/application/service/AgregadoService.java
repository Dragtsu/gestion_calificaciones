package com.alumnos.application.service;

import com.alumnos.domain.model.Agregado;
import com.alumnos.domain.port.in.AgregadoServicePort;
import com.alumnos.domain.port.out.AgregadoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AgregadoService implements AgregadoServicePort {

    private final AgregadoRepositoryPort agregadoRepositoryPort;

    public AgregadoService(AgregadoRepositoryPort agregadoRepositoryPort) {
        this.agregadoRepositoryPort = agregadoRepositoryPort;
    }

    @Override
    public Agregado crearAgregado(Agregado agregado) {
        // Validaciones
        if (agregado.getNombre() == null || agregado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del agregado es requerido");
        }
        if (agregado.getCriterioId() == null) {
            throw new IllegalArgumentException("El criterio de evaluación es requerido");
        }

        // Calcular orden automáticamente basado en el criterio
        List<Agregado> agregadosDelCriterio = agregadoRepositoryPort.findByCriterioId(agregado.getCriterioId());
        int nuevoOrden = agregadosDelCriterio.size() + 1;
        agregado.setOrden(nuevoOrden);

        return agregadoRepositoryPort.save(agregado);
    }

    @Override
    public Optional<Agregado> obtenerAgregadoPorId(Long id) {
        return agregadoRepositoryPort.findById(id);
    }

    @Override
    public List<Agregado> obtenerTodosLosAgregados() {
        return agregadoRepositoryPort.findAll();
    }

    @Override
    public Agregado actualizarAgregado(Agregado agregado) {
        // Validaciones
        if (agregado.getNombre() == null || agregado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del agregado es requerido");
        }
        if (agregado.getCriterioId() == null) {
            throw new IllegalArgumentException("El criterio de evaluación es requerido");
        }

        // Verificar si cambió de criterio
        Agregado agregadoAnterior = agregadoRepositoryPort.findById(agregado.getId()).orElse(null);
        Long criterioIdAnterior = agregadoAnterior != null ? agregadoAnterior.getCriterioId() : null;
        boolean cambioDeCriterio = agregadoAnterior != null && !agregadoAnterior.getCriterioId().equals(agregado.getCriterioId());

        if (cambioDeCriterio) {
            // Cambió de criterio, asignar al final del nuevo criterio
            List<Agregado> agregadosDelNuevoCriterio = agregadoRepositoryPort.findByCriterioId(agregado.getCriterioId());
            int nuevoOrden = agregadosDelNuevoCriterio.size() + 1;
            agregado.setOrden(nuevoOrden);
        } else if (agregado.getOrden() == null) {
            // Si no tiene orden, asignarlo
            List<Agregado> agregadosDelCriterio = agregadoRepositoryPort.findByCriterioId(agregado.getCriterioId());
            int nuevoOrden = agregadosDelCriterio.size();
            agregado.setOrden(nuevoOrden > 0 ? nuevoOrden : 1);
        }
        // Si no cambió de criterio y tiene orden, mantener el orden actual

        Agregado agregadoActualizado = agregadoRepositoryPort.save(agregado);

        // Si cambió de criterio, recalcular órdenes del criterio anterior
        if (cambioDeCriterio && criterioIdAnterior != null) {
            recalcularOrdenesDelCriterio(criterioIdAnterior);
        }

        return agregadoActualizado;
    }

    @Override
    public void eliminarAgregado(Long id) {
        // Obtener el agregado antes de eliminarlo para saber su criterio
        Agregado agregado = agregadoRepositoryPort.findById(id).orElse(null);
        Long criterioId = agregado != null ? agregado.getCriterioId() : null;

        agregadoRepositoryPort.deleteById(id);

        // Recalcular órdenes del criterio
        if (criterioId != null) {
            recalcularOrdenesDelCriterio(criterioId);
        }
    }

    @Override
    public List<Agregado> obtenerAgregadosPorCriterio(Long criterioId) {
        return agregadoRepositoryPort.findByCriterioId(criterioId);
    }

    @Override
    public void actualizarOrdenAgregado(Long agregadoId, Integer nuevoOrden) {
        Agregado agregado = agregadoRepositoryPort.findById(agregadoId)
                .orElseThrow(() -> new IllegalArgumentException("Agregado no encontrado"));
        agregado.setOrden(nuevoOrden);
        agregadoRepositoryPort.save(agregado);
    }

    /**
     * Recalcula los órdenes para todos los agregados de un criterio
     */
    private void recalcularOrdenesDelCriterio(Long criterioId) {
        List<Agregado> agregadosDelCriterio = agregadoRepositoryPort.findByCriterioId(criterioId);

        // Ordenar por orden actual (o por ID si no tienen orden)
        agregadosDelCriterio.sort((a1, a2) -> {
            if (a1.getOrden() == null && a2.getOrden() == null) return Long.compare(a1.getId(), a2.getId());
            if (a1.getOrden() == null) return 1;
            if (a2.getOrden() == null) return -1;
            return Integer.compare(a1.getOrden(), a2.getOrden());
        });

        // Asignar órdenes secuenciales
        int orden = 1;
        for (Agregado agregado : agregadosDelCriterio) {
            agregado.setOrden(orden++);
            agregadoRepositoryPort.save(agregado);
        }
    }
}
