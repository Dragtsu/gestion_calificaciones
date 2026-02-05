package com.alumnos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alumno {
    private Long id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Long grupoId;  // Clave foránea al grupo
    private Integer numeroLista;  // Número de lista dinámico basado en grupo y orden alfabético

    @Override
    public String toString() {
        return nombre + " " + apellidoPaterno +
               (apellidoMaterno != null ? " " + apellidoMaterno : "");
    }
}
