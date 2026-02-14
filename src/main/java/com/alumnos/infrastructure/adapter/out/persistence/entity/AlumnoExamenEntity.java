package com.alumnos.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alumno_examen",
    uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "examen_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoExamenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_id", nullable = false)
    private Long alumnoId;

    @Column(name = "examen_id", nullable = false)
    private Long examenId;

    @Column(name = "aciertos", nullable = false)
    private Integer puntosExamen;

    @Column
    private Double porcentaje;

    @Column
    private Double calificacion;
}
