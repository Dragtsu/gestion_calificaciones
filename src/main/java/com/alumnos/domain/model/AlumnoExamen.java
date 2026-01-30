package com.alumnos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoExamen {
    private Long id;
    private Long alumnoId;
    private Long examenId;
    private Integer puntosExamen;  // Puntos de examen obtenidos por el alumno (0-99)
    private Double porcentaje;  // Porcentaje obtenido (0-100)
    private Double calificacion;  // Calificación sobre 10 (0-10)

    // Campos opcionales para mostrar información
    private String nombreAlumno;
    private Integer numeroLista;
}
