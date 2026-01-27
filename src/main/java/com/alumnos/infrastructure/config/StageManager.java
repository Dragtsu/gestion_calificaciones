package com.alumnos.infrastructure.config;

import com.alumnos.infrastructure.adapter.in.ui.FxmlView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageManager {

    private static final Logger LOG = LoggerFactory.getLogger(StageManager.class);

    private final ApplicationContext springContext;
    private Stage primaryStage;

    @Autowired
    public StageManager(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void switchScene(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        show(viewRootNodeHierarchy, view.getTitle());
    }

    private void show(final Parent rootNode, String title) {
        Scene scene = prepareScene(rootNode);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);

        // Configurar ventana sin decoraciones (sin barra de título)
        primaryStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

        // Maximizar la ventana
        primaryStage.setMaximized(true);

        try {
            primaryStage.show();
        } catch (Exception exception) {
            logAndExit("Unable to show scene for title: " + title, exception);
        }
    }

    private Scene prepareScene(Parent rootNode) {
        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(rootNode);
        }
        scene.setRoot(rootNode);
        return scene;
    }

    private Parent loadViewNodeHierarchy(String fxmlFilePath) {
        Parent rootNode = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            loader.setControllerFactory(springContext::getBean);
            rootNode = loader.load();
        } catch (IOException exception) {
            logAndExit("Unable to load FXML view: " + fxmlFilePath, exception);
        }
        return rootNode;
    }

    private void logAndExit(String errorMsg, Exception exception) {
        LOG.error(errorMsg, exception);
        Platform.exit();
    }
}
