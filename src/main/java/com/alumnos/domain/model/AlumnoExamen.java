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
    private Integer aciertos;  // Aciertos obtenidos por el alumno en este examen (0-99)

    // Campos opcionales para mostrar información
    private String nombreAlumno;
    private Integer numeroLista;
}
