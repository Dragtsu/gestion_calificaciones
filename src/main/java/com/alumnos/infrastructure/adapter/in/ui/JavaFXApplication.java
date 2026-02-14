package com.alumnos.infrastructure.adapter.in.ui;

import com.alumnos.AlumnosApplication;
import com.alumnos.domain.port.in.ConfiguracionServicePort;
import com.alumnos.infrastructure.config.StageManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaFXApplication extends Application {

    private static final Logger LOGGER = Logger.getLogger(JavaFXApplication.class.getName());
    private ConfigurableApplicationContext springContext;
    private StageManager stageManager;

    @Override
    public void init() {
        // Inicializar Spring Boot (proceso lento)
        springContext = new SpringApplicationBuilder(AlumnosApplication.class)
                .headless(false)
                .run();
        stageManager = springContext.getBean(StageManager.class);
    }

    @Override
    public void start(Stage primaryStage) {
        // Añadir icono a la aplicación
        try {
            primaryStage.getIcons().add(
                new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/icon/gemicasco.ico")
                )
            );
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo cargar el icono de la aplicación", e);
        }

        stageManager.setPrimaryStage(primaryStage);

        ConfiguracionServicePort configuracionService = springContext.getBean(ConfiguracionServicePort.class);

        if (configuracionService.obtenerConfiguracion().isEmpty()) {
            stageManager.showModal(FxmlView.CONFIGURACION_INICIAL).showAndWait();
        }

        stageManager.switchScene(FxmlView.HOME);
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}

