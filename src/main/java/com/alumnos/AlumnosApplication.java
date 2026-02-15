package com.alumnos;

import com.alumnos.infrastructure.adapter.in.ui.JavaFXApplication;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cache.annotation.EnableCaching;  // ❌ DESACTIVADO: En apps de escritorio causa más problemas que beneficios

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

@SpringBootApplication
// @EnableCaching  // ❌ DESACTIVADO: Para apps de escritorio, los datos deben reflejarse inmediatamente
public class AlumnosApplication {

    private static final Logger LOG = LoggerFactory.getLogger(AlumnosApplication.class);

    static {
        // Configurar UTF-8 para todo el sistema
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        System.setProperty("javafx.platform.charset", "UTF-8");


        // Configurar el icono para la barra de tareas de Windows
        try {
            if (Taskbar.isTaskbarSupported()) {
                BufferedImage image = null;
                InputStream iconStream = null;

                // Intentar cargar PNG primero (más compatible con ImageIO)
                iconStream = AlumnosApplication.class.getResourceAsStream("/icon/gemicasco.png");
                if (iconStream != null) {
                    try {
                        image = ImageIO.read(iconStream);
                        iconStream.close();
                    } catch (Exception e) {
                        LOG.warn("No se pudo leer el PNG: {}", e.getMessage());
                    }
                }

                // Si PNG falla, intentar con ICO
                if (image == null) {
                    iconStream = AlumnosApplication.class.getResourceAsStream("/icon/gemicasco.ico");
                    if (iconStream != null) {
                        try {
                            image = ImageIO.read(iconStream);
                            iconStream.close();
                        } catch (Exception e) {
                            LOG.warn("No se pudo leer el ICO: {}", e.getMessage());
                        }
                    }
                }

                // Establecer el icono si se cargó exitosamente
                if (image != null) {
                    Taskbar.getTaskbar().setIconImage(image);
                    LOG.info("Icono de la barra de tareas configurado correctamente");
                } else {
                    LOG.warn("No se pudo cargar ningun formato de icono");
                }
            }
        } catch (Exception e) {
            LOG.error("No se pudo establecer el icono de la barra de tareas: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        Application.launch(JavaFXApplication.class, args);
    }
}
