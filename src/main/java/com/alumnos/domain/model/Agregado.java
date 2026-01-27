package com.alumnos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agregado {
    private Long id;
    private String nombre;
    private Long criterioId;
    private Integer orden;  // Orden dinámico basado en la materia

    // Campos opcionales para mostrar información
    private String nombreCriterio;
    private String nombreMateria;
}
