package com.alumnos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoMateria {
    private Long id;
    private Long grupoId;
    private Long materiaId;
    // Campos opcionales para mostrar información
    private String nombreGrupo;
    private String nombreMateria;
}
