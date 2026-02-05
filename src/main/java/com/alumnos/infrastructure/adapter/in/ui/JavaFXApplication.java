package com.alumnos.infrastructure.adapter.in.ui;

import com.alumnos.AlumnosApplication;
import com.alumnos.domain.port.in.ConfiguracionServicePort;
import com.alumnos.infrastructure.config.StageManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFXApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private StageManager stageManager;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(AlumnosApplication.class)
                .headless(false)
                .run();
        stageManager = springContext.getBean(StageManager.class);
    }

    @Override
    public void start(Stage primaryStage) {
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
