package com.alumnos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grupo {
    private Long id;  // Código del grupo

    @Override
    public String toString() {
        return id != null ? String.valueOf(id) : "Sin código";
    }

    /**
     * Método auxiliar para obtener el nombre del grupo (el ID)
     */
    public String getNombre() {
        return toString();
    }
}
