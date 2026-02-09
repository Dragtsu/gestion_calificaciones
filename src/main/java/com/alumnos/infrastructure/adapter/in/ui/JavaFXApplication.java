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
        // Mostrar splash screen en el hilo de JavaFX
        Platform.runLater(() -> {
            try {
                showSplashScreen();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al mostrar la pantalla de carga", e);
            }
        });

        // Inicializar Spring Boot (proceso lento)
        springContext = new SpringApplicationBuilder(AlumnosApplication.class)
                .headless(false)
                .run();
        stageManager = springContext.getBean(StageManager.class);
    }

    private void showSplashScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/splash.fxml"));
        Parent root = loader.load();

        splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);

        // Añadir icono al splash screen
        try {
            splashStage.getIcons().add(
                new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/icon/gemicasco.ico")
                )
            );
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo cargar el icono del splash", e);
        }

        splashStage.setScene(new Scene(root));
        splashStage.centerOnScreen();
        splashStage.show();
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
        if (splashStage != null) {
            splashStage.close();
        }
        springContext.close();
        Platform.exit();
    }
}
