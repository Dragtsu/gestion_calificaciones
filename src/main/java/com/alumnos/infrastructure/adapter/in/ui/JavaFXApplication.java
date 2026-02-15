package com.alumnos.infrastructure.adapter.in.ui;

import com.alumnos.AlumnosApplication;
import com.alumnos.domain.port.in.ConfiguracionServicePort;
import com.alumnos.infrastructure.config.StageManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaFXApplication extends Application {

    private static final Logger LOGGER = Logger.getLogger(JavaFXApplication.class.getName());
    private ConfigurableApplicationContext springContext;
    private StageManager stageManager;
    private Stage splashStage;

    @Override
    public void init() {
        // Mostrar splash screen en el hilo de JavaFX mientras se carga Spring
        Platform.runLater(this::showSplash);

        // Inicializar Spring Boot (proceso lento)
        springContext = new SpringApplicationBuilder(AlumnosApplication.class)
                .headless(false)
                .run();
        stageManager = springContext.getBean(StageManager.class);
    }

    private void showSplash() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/splash.fxml"));
            Parent splashRoot = loader.load();

            splashStage = new Stage();
            splashStage.initStyle(StageStyle.UNDECORATED);
            splashStage.setScene(new Scene(splashRoot, 600, 350));
            splashStage.setAlwaysOnTop(true);
            splashStage.centerOnScreen();

            // Añadir icono al splash
            try {
                splashStage.getIcons().add(
                    new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/icon/gemicasco.ico")
                    )
                );
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se pudo cargar el icono del splash", e);
            }

            splashStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al mostrar splash screen", e);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Cerrar splash screen
        if (splashStage != null) {
            splashStage.close();
        }

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

