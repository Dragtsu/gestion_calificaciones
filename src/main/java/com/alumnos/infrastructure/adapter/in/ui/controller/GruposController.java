package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.port.in.GrupoServicePort;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controlador para la gestión de grupos
 */
@Component
public class GruposController extends BaseController {

    private final GrupoServicePort grupoService;
    private TableView<Grupo> tablaGrupos; // 📋 Referencia a la tabla

    public GruposController(GrupoServicePort grupoService) {
        this.grupoService = grupoService;
    }

    public VBox crearVista() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        vista.getChildren().addAll(crearFormulario(), crearTabla());
        return vista;
    }

    private VBox crearFormulario() {
        VBox form = new VBox(10);
        form.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblFormTitle = new Label("Registrar Nuevo Grupo");
        lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
        gridForm.setHgap(15);
        gridForm.setVgap(10);

        Label lblId = new Label("ID Grupo:");
        lblId.setStyle("-fx-font-weight: bold;");
        TextField txtId = new TextField();
        txtId.setPromptText("Ingrese el ID del grupo");

        gridForm.add(lblId, 0, 0);
        gridForm.add(txtId, 1, 0);

        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0;");

        Button btnGuardar = new Button("Guardar");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> guardarGrupo(txtId));

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> txtId.clear());

        buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);

        form.getChildren().addAll(lblFormTitle, new javafx.scene.control.Separator(), gridForm, buttonBox);
        return form;
    }

    private VBox crearTabla() {
        VBox contenedor = new VBox(10);
        contenedor.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblTableTitle = new Label("Lista de Grupos");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        tablaGrupos = new TableView<>(); // 📋 Guardar referencia
        TableColumn<Grupo, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getId())));

        // Columna de acciones con botón eliminar
        TableColumn<Grupo, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new javafx.scene.control.TableCell<Grupo, Void>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEliminar.setOnAction(event -> {
                    Grupo grupo = getTableView().getItems().get(getIndex());
                    eliminarGrupo(grupo);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });

        tablaGrupos.getColumns().addAll(colId, colAcciones);
        cargarDatos(tablaGrupos);

        contenedor.getChildren().addAll(lblTableTitle, tablaGrupos);
        return contenedor;
    }

    private void guardarGrupo(TextField txtId) {
        try {
            if (!validarCampoNoVacio(txtId.getText(), "ID Grupo")) return;

            Grupo grupo = new Grupo();
            grupo.setId(Long.parseLong(txtId.getText()));
            grupoService.crearGrupo(grupo);

            mostrarExito("Grupo guardado");
            txtId.clear();

            // ⚡ RECARGAR LA TABLA después de guardar
            if (tablaGrupos != null) {
                cargarDatos(tablaGrupos);
            }
        } catch (NumberFormatException e) {
            mostrarError("El ID del grupo debe ser un número válido");
        } catch (Exception e) {
            manejarExcepcion("guardar grupo", e);
        }
    }

    private void cargarDatos(TableView<Grupo> tabla) {
        try {
            List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
            tabla.setItems(FXCollections.observableArrayList(grupos));
        } catch (Exception e) {
            manejarExcepcion("cargar grupos", e);
        }
    }

    private void eliminarGrupo(Grupo grupo) {
        if (grupo == null) {
            mostrarError("Por favor seleccione un grupo");
            return;
        }

        // Mostrar confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este grupo?");
        confirmacion.setContentText("Grupo: " + grupo.getId());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    grupoService.eliminarGrupo(grupo.getId());
                    mostrarExito("Grupo eliminado correctamente");

                    // Recargar la tabla
                    if (tablaGrupos != null) {
                        cargarDatos(tablaGrupos);
                    }
                } catch (IllegalStateException e) {
                    // Error de validación de dependencias
                    mostrarError(e.getMessage());
                } catch (Exception e) {
                    manejarExcepcion("eliminar grupo", e);
                }
            }
        });
    }
}
