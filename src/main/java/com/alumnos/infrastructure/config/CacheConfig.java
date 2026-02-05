package com.alumnos.infrastructure.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuración de caché para mejorar el rendimiento de la aplicación.
 *
 * Cachés configurados:
 * - materias: Lista completa de materias (raramente cambia)
 * - grupos: Lista completa de grupos (raramente cambia)
 * - criterios: Criterios de evaluación por materia/parcial
 *
 * NOTA: Descomentar @EnableCaching en AlumnosApplication para activar
 */
@Configuration
@EnableCaching
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
