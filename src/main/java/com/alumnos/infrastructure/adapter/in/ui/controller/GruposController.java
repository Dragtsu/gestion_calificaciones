package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.port.in.GrupoServicePort;
import javafx.application.Platform;
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
    private EstudiantesController estudiantesController; // 🔗 Para notificar cambios
    private AsignacionesController asignacionesController; // 🔗 Para notificar cambios

    public GruposController(GrupoServicePort grupoService) {
        this.grupoService = grupoService;
    }

    /**
     * Método para establecer la referencia al controlador de estudiantes
     * Permite notificar cuando se crea o elimina un grupo
     */
    public void setEstudiantesController(EstudiantesController estudiantesController) {
        this.estudiantesController = estudiantesController;
    }

    /**
     * Método para establecer la referencia al controlador de asignaciones
     * Permite notificar cuando se crea o elimina un grupo
     */
    public void setAsignacionesController(AsignacionesController asignacionesController) {
        this.asignacionesController = asignacionesController;
    }

    public VBox crearVista() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        vista.setMaxHeight(Double.MAX_VALUE);
        vista.setMaxWidth(Double.MAX_VALUE);

        VBox formulario = crearFormulario();
        VBox tabla = crearTabla();
        javafx.scene.layout.VBox.setVgrow(tabla, javafx.scene.layout.Priority.ALWAYS);

        vista.getChildren().addAll(formulario, tabla);
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
        contenedor.setMaxHeight(Double.MAX_VALUE);
        contenedor.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.VBox.setVgrow(contenedor, javafx.scene.layout.Priority.ALWAYS);;

        Label lblTableTitle = new Label("Lista de Grupos");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        tablaGrupos = new TableView<>(); // 📋 Guardar referencia
        tablaGrupos.setMaxHeight(Double.MAX_VALUE);
        tablaGrupos.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.VBox.setVgrow(tablaGrupos, javafx.scene.layout.Priority.ALWAYS);
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

            // 🔔 NOTIFICAR a otros controladores para actualizar sus ComboBox de grupos
            if (estudiantesController != null) {
                estudiantesController.refrescarListaGrupos();
            }
            if (asignacionesController != null) {
                asignacionesController.refrescarListaGrupos();
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
            tabla.refresh(); // 🔄 Forzar refresco de la tabla para que se rendericen los botones

            // 📏 Ajustar columnas al contenido (incluyendo botones)
            Platform.runLater(() -> ajustarColumnasAlContenido(tabla));
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

                    // 🔔 NOTIFICAR a otros controladores para actualizar sus ComboBox de grupos
                    if (estudiantesController != null) {
                        estudiantesController.refrescarListaGrupos();
                    }
                    if (asignacionesController != null) {
                        asignacionesController.refrescarListaGrupos();
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

    private void ajustarColumnasAlContenido(TableView<Grupo> tabla) {
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        for (TableColumn<Grupo, ?> columna : tabla.getColumns()) {
            if ("Acciones".equals(columna.getText())) {
                columna.setPrefWidth(120);
                columna.setMinWidth(120);
                columna.setMaxWidth(120);
                continue;
            }

            double anchoMaximo = calcularAnchoColumna(columna);
            columna.setPrefWidth(anchoMaximo);
        }
    }

    private double calcularAnchoColumna(TableColumn<Grupo, ?> columna) {
        javafx.scene.text.Text textoHeader = new javafx.scene.text.Text(columna.getText());
        double anchoMaximo = textoHeader.getLayoutBounds().getWidth() + 40;

        int filasARevisar = Math.min(tablaGrupos.getItems().size(), 50);

        for (int i = 0; i < filasARevisar; i++) {
            Grupo grupo = tablaGrupos.getItems().get(i);
            String valorCelda = obtenerValorCelda(columna, grupo);

            if (valorCelda != null && !valorCelda.isEmpty()) {
                javafx.scene.text.Text texto = new javafx.scene.text.Text(valorCelda);
                double ancho = texto.getLayoutBounds().getWidth() + 40;
                if (ancho > anchoMaximo) {
                    anchoMaximo = ancho;
                }
            }
        }

        return anchoMaximo;
    }

    private String obtenerValorCelda(TableColumn<Grupo, ?> columna, Grupo grupo) {
        if ("ID".equals(columna.getText())) {
            return String.valueOf(grupo.getId());
        }
        return "";
    }
}
