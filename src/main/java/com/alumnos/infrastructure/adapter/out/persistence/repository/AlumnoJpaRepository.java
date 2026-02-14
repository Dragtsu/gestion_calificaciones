package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.infrastructure.adapter.out.persistence.entity.AlumnoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumnoJpaRepository extends JpaRepository<AlumnoEntity, Long> {
    // Buscar en la concatenación de nombre, apellido paterno y apellido materno
    @Query("SELECT a FROM AlumnoEntity a WHERE CONCAT(a.nombre, ' ', a.apellidoPaterno, ' ', a.apellidoMaterno) LIKE :nombreCompleto")
    List<AlumnoEntity> findByNombreContainingIgnoreCase(@Param("nombreCompleto") String nombreCompleto);
}
