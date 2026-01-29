package com.alumnos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {
    private Long id;
    private Long alumnoId;
    private Long agregadoId;
    private Double puntuacion;  // Puntuación obtenida

    // Campos opcionales para mostrar información
    private String nombreAlumno;
    private String nombreAgregado;
}
