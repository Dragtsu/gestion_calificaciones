package com.alumnos.infrastructure.adapter.out.persistence.repository;

import com.alumnos.infrastructure.adapter.out.persistence.entity.MateriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaJpaRepository extends JpaRepository<MateriaEntity, Long> {
    // Buscar por nombre con LIKE (el parámetro ya incluye los comodines %)
    @Query("SELECT m FROM MateriaEntity m WHERE LOWER(m.nombre) LIKE LOWER(:nombre)")
    List<MateriaEntity> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
}
