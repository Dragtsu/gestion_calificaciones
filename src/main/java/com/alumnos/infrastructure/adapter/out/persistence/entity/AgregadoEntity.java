package com.alumnos.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agregados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "criterio_id", nullable = false)
    private Long criterioId;

    @Column(name = "orden")
    private Integer orden;
}
