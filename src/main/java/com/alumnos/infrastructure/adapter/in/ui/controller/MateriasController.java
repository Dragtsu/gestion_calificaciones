package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.MateriaServicePort;
import javafx.application.Platform;
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
    private AsignacionesController asignacionesController; // 🔗 Referencia para sincronización
    private CriteriosController criteriosController; // 🔗 Referencia para sincronización
    private AgregadosController agregadosController; // 🔗 Referencia para sincronización

    // 📝 Campos del formulario
    private TextField txtNombre;
    private Label lblFormTitle;
    private Button btnGuardar;
    private Long materiaIdEnEdicion = null; // ID de la materia en edición (null = crear nueva)

    public MateriasController(MateriaServicePort materiaService) {
        this.materiaService = materiaService;
    }

    /**
     * Configura la referencia al controlador de asignaciones para sincronización
     */
    public void setAsignacionesController(AsignacionesController asignacionesController) {
        this.asignacionesController = asignacionesController;
    }

    /**
     * Configura la referencia al controlador de criterios para sincronización
     */
    public void setCriteriosController(CriteriosController criteriosController) {
        this.criteriosController = criteriosController;
    }

    /**
     * Configura la referencia al controlador de agregados para sincronización
     */
    public void setAgregadosController(AgregadosController agregadosController) {
        this.agregadosController = agregadosController;
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
        contenedor.setMaxHeight(Double.MAX_VALUE);
        contenedor.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.VBox.setVgrow(contenedor, javafx.scene.layout.Priority.ALWAYS);

        Label lblTableTitle = new Label("Lista de Materias");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        tablaMaterias = new TableView<>(); // 📋 Guardar referencia
        tablaMaterias.setMaxHeight(Double.MAX_VALUE);
        tablaMaterias.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.VBox.setVgrow(tablaMaterias, javafx.scene.layout.Priority.ALWAYS);
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

            // 🔗 SINCRONIZAR con AsignacionesController
            if (asignacionesController != null) {
                asignacionesController.refrescarListaMaterias();
            }

            // 🔗 SINCRONIZAR con CriteriosController
            if (criteriosController != null) {
                criteriosController.refrescarListaMaterias();
            }

            // 🔗 SINCRONIZAR con AgregadosController
            if (agregadosController != null) {
                agregadosController.refrescarListaMaterias();
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
            tabla.refresh(); // 🔄 Forzar refresco de la tabla para que se rendericen los botones

            // 📏 Ajustar columnas al contenido (incluyendo botones)
            Platform.runLater(() -> ajustarColumnasAlContenido(tabla));
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

                    // 🔗 SINCRONIZAR con AsignacionesController
                    if (asignacionesController != null) {
                        asignacionesController.refrescarListaMaterias();
                    }

                    // 🔗 SINCRONIZAR con CriteriosController
                    if (criteriosController != null) {
                        criteriosController.refrescarListaMaterias();
                    }

                    // 🔗 SINCRONIZAR con AgregadosController
                    if (agregadosController != null) {
                        agregadosController.refrescarListaMaterias();
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

    /**
     * Ajusta el ancho de las columnas al contenido automáticamente
     */
    private void ajustarColumnasAlContenido(TableView<Materia> tabla) {
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        for (TableColumn<Materia, ?> columna : tabla.getColumns()) {
            if ("Acciones".equals(columna.getText())) {
                columna.setPrefWidth(180);
                columna.setMinWidth(180);
                columna.setMaxWidth(180);
                continue;
            }

            double anchoMaximo = calcularAnchoColumna(columna);
            columna.setPrefWidth(anchoMaximo);
        }
    }

    private double calcularAnchoColumna(TableColumn<Materia, ?> columna) {
        javafx.scene.text.Text textoHeader = new javafx.scene.text.Text(columna.getText());
        double anchoMaximo = textoHeader.getLayoutBounds().getWidth() + 40;

        int filasARevisar = Math.min(tablaMaterias.getItems().size(), 50);

        for (int i = 0; i < filasARevisar; i++) {
            Materia materia = tablaMaterias.getItems().get(i);
            String valorCelda = obtenerValorCelda(columna, materia);

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

    private String obtenerValorCelda(TableColumn<Materia, ?> columna, Materia materia) {
        if ("Materia".equals(columna.getText())) {
            return materia.getNombre() != null ? materia.getNombre() : "";
        }
        return "";
    }
}
