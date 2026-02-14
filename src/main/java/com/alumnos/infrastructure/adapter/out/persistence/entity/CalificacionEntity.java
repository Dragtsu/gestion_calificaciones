package com.alumnos.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "calificaciones",
    uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "agregado_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_id", nullable = false)
    private Long alumnoId;

    @Column(name = "agregado_id", nullable = false)
    private Long agregadoId;

    @Column(nullable = false)
    private Double puntuacion;
}
