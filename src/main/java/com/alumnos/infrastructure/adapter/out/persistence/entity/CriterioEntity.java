package com.alumnos.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "criterios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriterioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "tipo_evaluacion", nullable = false)
    private String tipoEvaluacion;  // "Check" o "Puntuacion"

    @Column(name = "puntuacion_maxima")
    private Double puntuacionMaxima;

    @Column(name = "materia_id", nullable = false)
    private Long materiaId;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "parcial")
    private Integer parcial;
}
