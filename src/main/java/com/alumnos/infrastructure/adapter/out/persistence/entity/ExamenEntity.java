package com.alumnos.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "examenes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"grupo_id", "materia_id", "parcial"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grupo_id", nullable = false)
    private Long grupoId;

    @Column(name = "materia_id", nullable = false)
    private Long materiaId;

    @Column(nullable = false)
    private Integer parcial;


    @Column(name = "total_aciertos")
    private Integer totalPuntosExamen;

    @Column(name = "fecha_aplicacion")
    private LocalDate fechaAplicacion;
}
