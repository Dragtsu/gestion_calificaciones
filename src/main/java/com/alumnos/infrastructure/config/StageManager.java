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

        // Añadir icono a la ventana principal
        try {
            primaryStage.getIcons().add(
                new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/icon/gemicasco.ico")
                )
            );
        } catch (Exception e) {
            LOG.warn("No se pudo cargar el icono de la aplicación", e);
        }

        // Configurar ventana sin decoraciones (sin barra de título)
        primaryStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

        // Configurar tamaño mínimo para la ventana
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Obtener dimensiones de la pantalla
        javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
        javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();

        // Configurar tamaño inicial de la ventana (95% de la pantalla)
        double width = bounds.getWidth() * 0.95;
        double height = bounds.getHeight() * 0.95;

        primaryStage.setWidth(width);
        primaryStage.setHeight(height);

        // Centrar la ventana
        primaryStage.setX((bounds.getWidth() - width) / 2 + bounds.getMinX());
        primaryStage.setY((bounds.getHeight() - height) / 2 + bounds.getMinY());

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

    public Stage showModal(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stage.setTitle(view.getTitle());

        // Añadir icono a la ventana modal
        try {
            stage.getIcons().add(
                new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/icon/gemicasco.ico")
                )
            );
        } catch (Exception e) {
            LOG.warn("No se pudo cargar el icono para la ventana modal", e);
        }

        stage.setScene(new Scene(viewRootNodeHierarchy));
        stage.centerOnScreen();
        return stage;
    }

    private void logAndExit(String errorMsg, Exception exception) {
        LOG.error(errorMsg, exception);
        Platform.exit();
    }
}
