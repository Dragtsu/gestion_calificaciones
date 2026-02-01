package com.alumnos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Examen {
    private Long id;
    private Long grupoId;
    private Long materiaId;
    private Integer parcial;  // 1, 2 o 3
    private Integer totalPuntosExamen;  // Total de puntos del examen (máximo de puntos posibles)
    private LocalDate fechaAplicacion;  // Fecha de aplicación del examen

    // Campos opcionales para mostrar información
    private String nombreMateria;
}
