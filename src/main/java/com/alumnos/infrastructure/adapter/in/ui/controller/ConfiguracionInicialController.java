package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Configuracion;
import com.alumnos.domain.port.in.ConfiguracionServicePort;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

@Controller
public class ConfiguracionInicialController {

    private final ConfiguracionServicePort configuracionService;

    @FXML
    private TextField txtNombreMaestro;

    @FXML
    private Button btnGuardar;

    public ConfiguracionInicialController(ConfiguracionServicePort configuracionService) {
        this.configuracionService = configuracionService;
    }

    @FXML
    public void initialize() {
        // Configurar evento del botón
        btnGuardar.setOnAction(event -> handleGuardar());
    }

    private void handleGuardar() {
        String nombreMaestro = txtNombreMaestro.getText();

        if (nombreMaestro == null || nombreMaestro.trim().isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese el nombre del maestro", Alert.AlertType.ERROR);
            return;
        }

        // Guardar configuración
        Configuracion configuracion = Configuracion.builder()
                .nombreMaestro(nombreMaestro.trim())
                .build();

        configuracionService.guardarConfiguracion(configuracion);

        // Cerrar ventana
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
