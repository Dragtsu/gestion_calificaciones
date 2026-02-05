package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.MateriaServicePort;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controlador para la gestión de materias
 */
@Component
public class MateriasController extends BaseController {

    private final MateriaServicePort materiaService;
    private TableView<Materia> tablaMaterias; // 📋 Referencia a la tabla

    // 📝 Campos del formulario
    private TextField txtNombre;
    private Label lblFormTitle;
    private Button btnGuardar;
    private Long materiaIdEnEdicion = null; // ID de la materia en edición (null = crear nueva)

    public MateriasController(MateriaServicePort materiaService) {
        this.materiaService = materiaService;
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

        lblFormTitle = new Label("Registrar Nueva Materia");
        lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
        gridForm.setHgap(15);
        gridForm.setVgap(10);

        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        txtNombre = new TextField();
        txtNombre.setPromptText("Ingrese el nombre de la materia");

        gridForm.add(lblNombre, 0, 0);
        gridForm.add(txtNombre, 1, 0);

        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0;");

        btnGuardar = new Button("Guardar");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> guardarMateria());

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> limpiarFormulario());

        buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);

        form.getChildren().addAll(lblFormTitle, new javafx.scene.control.Separator(), gridForm, buttonBox);
        return form;
    }

    private VBox crearTabla() {
        VBox contenedor = new VBox(10);
        contenedor.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblTableTitle = new Label("Lista de Materias");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        tablaMaterias = new TableView<>(); // 📋 Guardar referencia
        TableColumn<Materia, String> colNombre = new TableColumn<>("Materia");
        colNombre.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));

        // Columna de acciones con botones editar y eliminar
        TableColumn<Materia, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new javafx.scene.control.TableCell<Materia, Void>() {
            private final javafx.scene.layout.HBox botonesBox = new javafx.scene.layout.HBox(5);
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEditar.setOnAction(event -> {
                    Materia materia = getTableView().getItems().get(getIndex());
                    cargarMateriaEnFormulario(materia);
                });

                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEliminar.setOnAction(event -> {
                    Materia materia = getTableView().getItems().get(getIndex());
                    eliminarMateria(materia);
                });

                botonesBox.getChildren().addAll(btnEditar, btnEliminar);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(botonesBox);
                }
            }
        });

        tablaMaterias.getColumns().addAll(colNombre, colAcciones);
        cargarDatos(tablaMaterias);

        contenedor.getChildren().addAll(lblTableTitle, tablaMaterias);
        return contenedor;
    }

    private void guardarMateria() {
        try {
            if (!validarCampoNoVacio(txtNombre.getText(), "Nombre")) return;

            Materia materia = new Materia();
            materia.setId(materiaIdEnEdicion); // Si es null = crear, si tiene valor = actualizar
            materia.setNombre(txtNombre.getText().trim());

            if (materiaIdEnEdicion == null) {
                // Crear nueva materia
                materiaService.crearMateria(materia);
                mostrarExito("Materia creada correctamente");
            } else {
                // Actualizar materia existente
                materiaService.actualizarMateria(materia);
                mostrarExito("Materia actualizada correctamente");
            }

            limpiarFormulario();

            // ⚡ RECARGAR LA TABLA después de guardar
            if (tablaMaterias != null) {
                cargarDatos(tablaMaterias);
            }
        } catch (Exception e) {
            manejarExcepcion("guardar materia", e);
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        materiaIdEnEdicion = null;
        lblFormTitle.setText("Registrar Nueva Materia");
        btnGuardar.setText("Guardar");
    }

    private void cargarDatos(TableView<Materia> tabla) {
        try {
            List<Materia> materias = materiaService.obtenerTodasLasMaterias();
            tabla.setItems(FXCollections.observableArrayList(materias));
        } catch (Exception e) {
            manejarExcepcion("cargar materias", e);
        }
    }

    private void eliminarMateria(Materia materia) {
        if (materia == null) {
            mostrarError("Por favor seleccione una materia");
            return;
        }

        // Mostrar confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta materia?");
        confirmacion.setContentText("Materia: " + materia.getNombre());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    materiaService.eliminarMateria(materia.getId());
                    mostrarExito("Materia eliminada correctamente");

                    // Recargar la tabla
                    if (tablaMaterias != null) {
                        cargarDatos(tablaMaterias);
                    }
                } catch (IllegalStateException e) {
                    // Error de validación de dependencias
                    mostrarError(e.getMessage());
                } catch (Exception e) {
                    manejarExcepcion("eliminar materia", e);
                }
            }
        });
    }

    private void cargarMateriaEnFormulario(Materia materia) {
        if (materia == null) {
            mostrarError("Materia no válida");
            return;
        }

        // Cargar datos en el formulario
        materiaIdEnEdicion = materia.getId();
        txtNombre.setText(materia.getNombre());

        // Cambiar el título y texto del botón
        lblFormTitle.setText("Editar Materia");
        btnGuardar.setText("Actualizar");

        // Poner foco en el campo
        txtNombre.requestFocus();
    }
}
