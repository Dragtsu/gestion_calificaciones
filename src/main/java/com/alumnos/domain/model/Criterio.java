package com.alumnos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Criterio {
    private Long id;
    private String nombre;
    private String tipoEvaluacion;  // "Check" o "Puntuacion"
    private Double puntuacionMaxima;
    private Long materiaId;
    private Integer orden;  // Orden dinámico basado en la materia
    private Integer cuatrimestre;  // Cuatrimestre del 1 al 4

    // Campos opcionales para mostrar información
    private String nombreMateria;
}
