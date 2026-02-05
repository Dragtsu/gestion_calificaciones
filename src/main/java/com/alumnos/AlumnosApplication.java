package com.alumnos;

import com.alumnos.infrastructure.adapter.in.ui.JavaFXApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching  // ⚡ Activa el sistema de caché para mejorar rendimiento
public class AlumnosApplication {

    static {
        // Configurar UTF-8 para todo el sistema
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        System.setProperty("javafx.platform.charset", "UTF-8");
    }

    public static void main(String[] args) {
        Application.launch(JavaFXApplication.class, args);
    }
}
