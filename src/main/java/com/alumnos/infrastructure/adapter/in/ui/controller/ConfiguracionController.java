package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Configuracion;
import com.alumnos.domain.port.in.ConfiguracionServicePort;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

/**
 * Controlador para la gestión de configuración del sistema
 */
@Component
public class ConfiguracionController extends BaseController {

    private final ConfiguracionServicePort configuracionService;

    // 📝 Campo del formulario
    private TextField txtNombreMaestro;
    private Configuracion configuracionActual;

    public ConfiguracionController(ConfiguracionServicePort configuracionService) {
        this.configuracionService = configuracionService;
    }

    public VBox crearVista() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        vista.getChildren().add(crearFormulario());

        // Cargar configuración actual
        cargarConfiguracion();

        return vista;
    }

    private VBox crearFormulario() {
        VBox form = new VBox(15);
        form.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        form.setMaxWidth(600);
        form.setAlignment(Pos.TOP_CENTER);

        // Título
        Label lblFormTitle = new Label("Configuración del Sistema");
        lblFormTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Separador
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        // Grid del formulario
        GridPane gridForm = new GridPane();
        gridForm.setHgap(15);
        gridForm.setVgap(15);
        gridForm.setAlignment(Pos.CENTER);

        Label lblNombreMaestro = new Label("Nombre del Maestro:");
        lblNombreMaestro.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        txtNombreMaestro = new TextField();
        txtNombreMaestro.setPromptText("Ingrese el nombre del maestro");
        txtNombreMaestro.setPrefWidth(350);
        txtNombreMaestro.setStyle("-fx-font-size: 14px; -fx-padding: 8;");

        gridForm.add(lblNombreMaestro, 0, 0);
        gridForm.add(txtNombreMaestro, 1, 0);

        // Botones
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnGuardar = new Button("Guardar Configuración");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 35; -fx-cursor: hand; -fx-font-weight: bold;");
        btnGuardar.setOnAction(e -> guardarConfiguracion());

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 35; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> cargarConfiguracion());

        buttonBox.getChildren().addAll(btnGuardar, btnCancelar);

        // Información adicional
        Label lblInfo = new Label("ℹ️ El nombre del maestro aparecerá en los informes y documentos generados");
        lblInfo.setStyle("-fx-text-fill: #666; -fx-font-size: 12px; -fx-font-style: italic;");
        lblInfo.setWrapText(true);
        lblInfo.setMaxWidth(500);
        lblInfo.setAlignment(Pos.CENTER);
        VBox.setMargin(lblInfo, new Insets(10, 0, 0, 0));

        form.getChildren().addAll(lblFormTitle, separator, gridForm, buttonBox, lblInfo);
        return form;
    }

    private void cargarConfiguracion() {
        try {
            configuracionActual = configuracionService.obtenerConfiguracion()
                    .orElseGet(() -> Configuracion.builder()
                            .nombreMaestro("")
                            .build());

            txtNombreMaestro.setText(configuracionActual.getNombreMaestro() != null
                    ? configuracionActual.getNombreMaestro()
                    : "");
        } catch (Exception e) {
            mostrarError("Error al cargar configuración: " + e.getMessage());
        }
    }

    private void guardarConfiguracion() {
        try {
            String nombreMaestro = txtNombreMaestro.getText().trim();

            // Validación
            if (nombreMaestro.isEmpty()) {
                mostrarAlerta("Validación", "El nombre del maestro no puede estar vacío", Alert.AlertType.WARNING);
                txtNombreMaestro.requestFocus();
                return;
            }

            // Actualizar configuración
            if (configuracionActual == null) {
                configuracionActual = new Configuracion();
            }
            configuracionActual.setNombreMaestro(nombreMaestro);

            // Guardar
            configuracionService.guardarConfiguracion(configuracionActual);

            mostrarExito("Configuración guardada exitosamente");
        } catch (Exception e) {
            mostrarError("Error al guardar configuración: " + e.getMessage());
        }
    }
}
