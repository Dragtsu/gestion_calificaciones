package com.alumnos.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA para almacenar calificaciones del concentrado.
 * Esta entidad guarda los valores capturados en el formulario de concentrado de calificaciones
 * junto con los IDs de los filtros aplicados (grupo, materia, parcial).
 *
 * Para criterios de tipo "Check":
 *   - puntuacion = 1.0 cuando el checkbox está marcado
 *   - puntuacion = 0.0 cuando el checkbox está desmarcado
 *
 * Para criterios de tipo "Puntuacion":
 *   - puntuacion = valor numérico capturado
 */
@Entity
@Table(name = "calificacion_concentrado",
    uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "agregado_id", "grupo_id", "materia_id", "parcial"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionConcentradoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_id", nullable = false)
    private Long alumnoId;

    @Column(name = "agregado_id", nullable = false)
    private Long agregadoId;

    @Column(name = "criterio_id", nullable = false)
    private Long criterioId;

    @Column(name = "grupo_id", nullable = false)
    private Long grupoId;

    @Column(name = "materia_id", nullable = false)
    private Long materiaId;

    @Column(nullable = false)
    private Integer parcial;  // 1, 2 o 3

    @Column(nullable = false)
    private Double puntuacion;  // 1.0 = checked, 0.0 = unchecked (para Check)
                                // Valor numérico (para Puntuacion)

    @Column(name = "puntos_parcial")
    private Double puntosParcial;  // Portafolio + Puntos Examen

    @Column(name = "calificacion_parcial")
    private Double calificacionParcial;  // (Puntos Parcial * 10) / 100

    @Column(name = "tipo_evaluacion", length = 20)
    private String tipoEvaluacion;  // "Check" o "Puntuacion"
}
