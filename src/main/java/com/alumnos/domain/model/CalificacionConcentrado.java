package com.alumnos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio que representa una calificación en el concentrado.
 * Almacena los valores capturados en el formulario de concentrado de calificaciones
 * incluyendo los IDs de los filtros aplicados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionConcentrado {

    private Long id;

    // IDs de los filtros
    private Long alumnoId;
    private Long agregadoId;
    private Long criterioId;
    private Long grupoId;
    private Long materiaId;
    private Integer parcial;  // 1, 2 o 3

    // Valor de la calificación
    private Double puntuacion;  // Para tipo "Check": 1.0 = checked, 0.0 = unchecked
                                // Para tipo "Puntuacion": valor numérico capturado

    // Valores calculados del parcial (agregados por columnas en el concentrado)
    private Double puntosParcial;      // Portafolio + Puntos Examen
    private Double calificacionParcial; // (Puntos Parcial * 10) / 100

    // Información del tipo de evaluación (opcional, para validaciones)
    private String tipoEvaluacion;  // "Check" o "Puntuacion"

    // Campos opcionales para mostrar información
    private String nombreAlumno;
    private String nombreAgregado;
    private String nombreCriterio;
    private String nombreMateria;
}
