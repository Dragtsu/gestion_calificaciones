package com.alumnos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // Campos opcionales para mostrar información
    private String nombreMateria;
}
