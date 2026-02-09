package com.alumnos.infrastructure.config;

import org.springframework.cache.CacheManager;
// import org.springframework.cache.annotation.EnableCaching;  // ❌ DESACTIVADO
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuración de caché para mejorar el rendimiento de la aplicación.
 *
 * ❌ DESACTIVADO: En aplicaciones de escritorio JavaFX, el caché causa problemas
 * de sincronización entre vistas. Los cambios en una pantalla no se reflejan en otras
 * hasta que la aplicación se reinicia. Para apps de escritorio es mejor consultar
 * siempre los datos actuales de la base de datos.
 *
 * Cachés configurados (si se reactiva):
 * - materias: Lista completa de materias
 * - grupos: Lista completa de grupos
 * - criterios: Criterios de evaluación por materia/parcial
 * - agregados: Agregados de calificaciones
 * - alumnos: Lista de alumnos
 */
@Configuration
// @EnableCaching  // ❌ DESACTIVADO: Causa problemas de sincronización en apps de escritorio
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("materias"),
            new ConcurrentMapCache("grupos"),
            new ConcurrentMapCache("criterios"),
            new ConcurrentMapCache("agregados"),
            new ConcurrentMapCache("alumnos")
        ));
        return cacheManager;
    }
}
