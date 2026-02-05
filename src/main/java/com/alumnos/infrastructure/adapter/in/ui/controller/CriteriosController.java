package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Criterio;
import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.CriterioServicePort;
import com.alumnos.domain.port.in.MateriaServicePort;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controlador para la gestión de criterios de evaluación
 * Responsabilidad: Manejar la vista y operaciones CRUD de criterios
 */
@Component
public class CriteriosController extends BaseController {

    private final CriterioServicePort criterioService;
    private final MateriaServicePort materiaService;
    private TableView<Criterio> tablaCriterios; // 📋 Referencia a la tabla
    private ComboBox<Materia> cmbFiltroMateria; // 📋 Referencia al filtro de materia
    private ComboBox<Integer> cmbFiltroParcial; // 📋 Referencia al filtro de parcial

    // Referencias a campos del formulario para edición
    private TextField txtNombre;
    private ComboBox<String> cmbTipoEvaluacion;
    private TextField txtPuntuacionMaxima;
    private ComboBox<Materia> cmbMateria;
    private ComboBox<Integer> cmbParcial;
    private Label lblOrden;
    private TextField txtOrden;
    private Button btnGuardar;
    private Long criterioIdEnEdicion = null; // ID del criterio en edición

    public CriteriosController(CriterioServicePort criterioService, MateriaServicePort materiaService) {
        this.criterioService = criterioService;
        this.materiaService = materiaService;
    }

    public VBox crearVista() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        vista.getChildren().addAll(
            crearFormulario(),
            crearFiltros(),
            crearTabla()
        );
        return vista;
    }

    private VBox crearFormulario() {
        VBox formulario = new VBox(10);
        formulario.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblFormTitle = new Label("Registrar Nuevo Criterio de Evaluación");
        lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
        gridForm.setHgap(20);  // Espacio horizontal entre columnas
        gridForm.setVgap(10);  // Espacio vertical entre filas

        // ========== COLUMNA 1 ==========
        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        txtNombre = new TextField(); // 📋 Guardar referencia
        txtNombre.setPromptText("Nombre del criterio");
        txtNombre.setPrefWidth(200);

        Label lblTipo = new Label("Tipo de Evaluación:");
        lblTipo.setStyle("-fx-font-weight: bold;");
        cmbTipoEvaluacion = new ComboBox<>(); // 📋 Guardar referencia
        cmbTipoEvaluacion.setPromptText("Tipo de evaluación");
        cmbTipoEvaluacion.setItems(FXCollections.observableArrayList("Check", "Puntuacion"));
        cmbTipoEvaluacion.setPrefWidth(200);

        Label lblPuntuacion = new Label("Puntuación Máxima:");
        lblPuntuacion.setStyle("-fx-font-weight: bold;");
        txtPuntuacionMaxima = new TextField(); // 📋 Guardar referencia
        txtPuntuacionMaxima.setPromptText("Puntuación máxima");
        txtPuntuacionMaxima.setPrefWidth(200);

        // ========== COLUMNA 2 ==========
        Label lblMateria = new Label("Materia:");
        lblMateria.setStyle("-fx-font-weight: bold;");
        cmbMateria = new ComboBox<>(); // 📋 Guardar referencia
        cmbMateria.setPromptText("Seleccione una materia");
        cargarMaterias(cmbMateria);
        cmbMateria.setPrefWidth(200);

        Label lblParcial = new Label("Parcial:");
        lblParcial.setStyle("-fx-font-weight: bold;");
        cmbParcial = new ComboBox<>(); // 📋 Guardar referencia
        cmbParcial.setPromptText("Parcial");
        cmbParcial.setItems(FXCollections.observableArrayList(1, 2, 3));
        cmbParcial.setPrefWidth(200);

        Label lblOrden = new Label("Orden:");
        lblOrden.setStyle("-fx-font-weight: bold;");
        this.lblOrden = lblOrden; // 📋 Guardar referencia
        txtOrden = new TextField(); // 📋 Guardar referencia
        txtOrden.setPromptText("Orden");
        txtOrden.setPrefWidth(200);
        // Ocultar el campo orden inicialmente (solo se mostrará al editar)
        lblOrden.setVisible(false);
        lblOrden.setManaged(false);
        txtOrden.setVisible(false);
        txtOrden.setManaged(false);

        // Agregar a GridPane en 2 columnas (3 filas cada una)
        // Columna 1 (izquierda): columnas 0-1
        gridForm.add(lblNombre, 0, 0);
        gridForm.add(txtNombre, 1, 0);
        gridForm.add(lblTipo, 0, 1);
        gridForm.add(cmbTipoEvaluacion, 1, 1);
        gridForm.add(lblPuntuacion, 0, 2);
        gridForm.add(txtPuntuacionMaxima, 1, 2);

        // Columna 2 (derecha): columnas 2-3
        gridForm.add(lblMateria, 2, 0);
        gridForm.add(cmbMateria, 3, 0);
        gridForm.add(lblParcial, 2, 1);
        gridForm.add(cmbParcial, 3, 1);
        gridForm.add(lblOrden, 2, 2);
        gridForm.add(txtOrden, 3, 2);

        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0;");

        btnGuardar = new Button("Guardar"); // 📋 Guardar referencia
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> guardarCriterio());

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> limpiarFormulario());

        buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);

        formulario.getChildren().addAll(lblFormTitle, new javafx.scene.control.Separator(), gridForm, buttonBox);
        return formulario;
    }

    private VBox crearFiltros() {
        VBox filtros = new VBox(10);
        filtros.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblFiltrosTitle = new Label("Filtros");
        lblFiltrosTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        javafx.scene.layout.HBox filterBox = new javafx.scene.layout.HBox(10);
        filterBox.setStyle("-fx-alignment: center-left;");

        Label lblMateria = new Label("Materia:");
        lblMateria.setStyle("-fx-font-weight: bold;");

        cmbFiltroMateria = new ComboBox<>(); // 📋 Guardar referencia
        cmbFiltroMateria.setPromptText("Seleccione una materia");
        cargarMaterias(cmbFiltroMateria);
        cmbFiltroMateria.setPrefWidth(200);

        Label lblParcial = new Label("Parcial:");
        lblParcial.setStyle("-fx-font-weight: bold;");

        cmbFiltroParcial = new ComboBox<>(); // 📋 Guardar referencia
        cmbFiltroParcial.setPromptText("Seleccione un parcial");
        cmbFiltroParcial.setItems(FXCollections.observableArrayList(1, 2, 3));
        cmbFiltroParcial.setPrefWidth(150);

        // Agregar listeners para refrescar tabla al cambiar filtros
        cmbFiltroMateria.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (tablaCriterios != null) {
                tablaCriterios.refresh();
            }
        });

        cmbFiltroParcial.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (tablaCriterios != null) {
                tablaCriterios.refresh();
            }
        });

        Button btnFiltrar = new Button("Filtrar");
        btnFiltrar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-cursor: hand;");
        btnFiltrar.setOnAction(e -> aplicarFiltros(cmbFiltroMateria, cmbFiltroParcial));

        Button btnLimpiarFiltros = new Button("Limpiar filtros");
        btnLimpiarFiltros.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-cursor: hand;");
        btnLimpiarFiltros.setOnAction(e -> limpiarFiltros(cmbFiltroMateria, cmbFiltroParcial));

        filterBox.getChildren().addAll(lblMateria, cmbFiltroMateria, lblParcial, cmbFiltroParcial, btnFiltrar, btnLimpiarFiltros);
        filtros.getChildren().addAll(lblFiltrosTitle, filterBox);

        return filtros;
    }

    private VBox crearTabla() {
        VBox contenedor = new VBox(10);
        contenedor.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblTableTitle = new Label("Lista de Criterios de Evaluación");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        tablaCriterios = new TableView<>(); // 📋 Guardar referencia

        TableColumn<Criterio, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));

        TableColumn<Criterio, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTipoEvaluacion()));

        TableColumn<Criterio, String> colPuntuacion = new TableColumn<>("Puntuación Máx");
        colPuntuacion.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getPuntuacionMaxima() != null ?
                String.valueOf(data.getValue().getPuntuacionMaxima()) : "N/A"));

        TableColumn<Criterio, String> colMateria = new TableColumn<>("Materia");
        colMateria.setCellValueFactory(data -> {
            Criterio criterio = data.getValue();
            if (criterio.getMateriaId() != null) {
                return materiaService.obtenerMateriaPorId(criterio.getMateriaId())
                    .map(m -> new javafx.beans.property.SimpleStringProperty(m.getNombre()))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        TableColumn<Criterio, String> colParcial = new TableColumn<>("Parcial");
        colParcial.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getParcial() != null ?
                String.valueOf(data.getValue().getParcial()) : "N/A"));

        TableColumn<Criterio, String> colOrden = new TableColumn<>("Orden");
        colOrden.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getOrden() != null ?
                String.valueOf(data.getValue().getOrden()) : "N/A"));

        // Columna de Ordenamiento (Subir/Bajar)
        TableColumn<Criterio, Void> colOrdenAcciones = new TableColumn<>("Ordenar");
        colOrdenAcciones.setPrefWidth(120);
        colOrdenAcciones.setCellFactory(param -> new TableCell<Criterio, Void>() {
            private final Button btnSubir = new Button("↑");
            private final Button btnBajar = new Button("↓");

            {
                btnSubir.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10; -fx-cursor: hand;");
                btnBajar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10; -fx-cursor: hand;");

                btnSubir.setOnAction(event -> {
                    int index = getIndex();
                    javafx.collections.ObservableList<Criterio> items = getTableView().getItems();
                    if (index > 0) {
                        Criterio criterio = items.get(index);
                        items.remove(index);
                        items.add(index - 1, criterio);
                        getTableView().getSelectionModel().select(index - 1);
                        getTableView().refresh();
                    }
                });

                btnBajar.setOnAction(event -> {
                    int index = getIndex();
                    javafx.collections.ObservableList<Criterio> items = getTableView().getItems();
                    if (index < items.size() - 1) {
                        Criterio criterio = items.get(index);
                        items.remove(index);
                        items.add(index + 1, criterio);
                        getTableView().getSelectionModel().select(index + 1);
                        getTableView().refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                // Mostrar controles solo cuando ambos filtros están seleccionados
                boolean filtrosCompletos = cmbFiltroMateria != null && cmbFiltroMateria.getValue() != null &&
                                          cmbFiltroParcial != null && cmbFiltroParcial.getValue() != null;

                if (!filtrosCompletos) {
                    setGraphic(null);
                    return;
                }

                javafx.collections.ObservableList<Criterio> items = getTableView().getItems();
                int totalItems = items.size();
                if (totalItems <= 1) {
                    setGraphic(null);
                    return;
                }

                int index = getIndex();
                javafx.scene.layout.HBox contenedor = new javafx.scene.layout.HBox(5);
                contenedor.setAlignment(javafx.geometry.Pos.CENTER);

                if (index == 0) {
                    // Primer registro: solo botón bajar
                    contenedor.getChildren().add(btnBajar);
                } else if (index == totalItems - 1) {
                    // Último registro: solo botón subir
                    contenedor.getChildren().add(btnSubir);
                } else {
                    // Registros intermedios: ambos botones
                    contenedor.getChildren().addAll(btnSubir, btnBajar);
                }

                setGraphic(contenedor);
            }
        });

        TableColumn<Criterio, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(180);
        colAcciones.setCellFactory(param -> new TableCell<Criterio, Void>() {
            private final javafx.scene.layout.HBox botonesBox = new javafx.scene.layout.HBox(5);
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");

                btnEditar.setOnAction(e -> {
                    Criterio criterio = getTableView().getItems().get(getIndex());
                    cargarCriterioEnFormulario(criterio);
                });

                btnEliminar.setOnAction(e -> {
                    Criterio criterio = getTableView().getItems().get(getIndex());
                    eliminarCriterio(criterio, tablaCriterios);
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

        tablaCriterios.getColumns().addAll(colNombre, colTipo, colPuntuacion, colMateria, colParcial, colOrden, colOrdenAcciones, colAcciones);
        cargarDatos(tablaCriterios);

        // Botón para guardar el orden
        Button btnGuardarOrden = new Button("💾 Guardar Orden");
        btnGuardarOrden.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");
        btnGuardarOrden.setOnAction(e -> guardarOrdenCriterios());

        Label lblInfo = new Label("💡 Selecciona Materia y Parcial en los filtros para poder ordenar los criterios usando ↑ ↓");
        lblInfo.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

        contenedor.getChildren().addAll(lblTableTitle, tablaCriterios, lblInfo, btnGuardarOrden);
        return contenedor;
    }

    private void guardarCriterio() {
        try {
            if (!validarFormulario()) return;

            // Procesar puntuación máxima
            Double puntuacion = null;
            if (!txtPuntuacionMaxima.getText().trim().isEmpty()) {
                try {
                    puntuacion = Double.parseDouble(txtPuntuacionMaxima.getText().trim());
                } catch (NumberFormatException e) {
                    mostrarError("La puntuación máxima debe ser un número válido");
                    return;
                }
            }

            Integer orden = null;
            // Solo usar el campo orden si estamos editando
            if (criterioIdEnEdicion != null && !txtOrden.getText().trim().isEmpty()) {
                try {
                    orden = Integer.parseInt(txtOrden.getText().trim());
                } catch (NumberFormatException e) {
                    mostrarError("El orden debe ser un número válido");
                    return;
                }
            }

            if (criterioIdEnEdicion == null) {
                // CREAR nuevo criterio - orden será asignado automáticamente por el servicio
                Criterio criterio = Criterio.builder()
                    .nombre(txtNombre.getText().trim())
                    .tipoEvaluacion(cmbTipoEvaluacion.getValue())
                    .puntuacionMaxima(puntuacion)
                    .materiaId(cmbMateria.getValue().getId())
                    .parcial(cmbParcial.getValue())
                    .build();

                criterioService.crearCriterio(criterio);
                mostrarExito("Criterio creado correctamente. El orden fue asignado automáticamente.");
            } else {
                // ACTUALIZAR criterio existente
                Criterio criterio = Criterio.builder()
                    .id(criterioIdEnEdicion)
                    .nombre(txtNombre.getText().trim())
                    .tipoEvaluacion(cmbTipoEvaluacion.getValue())
                    .puntuacionMaxima(puntuacion)
                    .materiaId(cmbMateria.getValue().getId())
                    .parcial(cmbParcial.getValue())
                    .orden(orden)
                    .build();

                criterioService.actualizarCriterio(criterio);
                mostrarExito("Criterio actualizado correctamente");
            }

            limpiarFormulario();

            // ⚡ RECARGAR LA TABLA después de guardar manteniendo los filtros
            if (tablaCriterios != null) {
                // Si hay filtros activos, aplicar filtros; si no, cargar todos
                if (cmbFiltroMateria.getValue() != null || cmbFiltroParcial.getValue() != null) {
                    aplicarFiltros(cmbFiltroMateria, cmbFiltroParcial);
                } else {
                    cargarDatos(tablaCriterios);
                }
            }
        } catch (Exception e) {
            manejarExcepcion("guardar criterio", e);
        }
    }

    private void eliminarCriterio(Criterio criterio, TableView<Criterio> tabla) {
        try {
            if (confirmarAccion("Confirmar eliminación", "¿Está seguro de eliminar este criterio?")) {
                criterioService.eliminarCriterio(criterio.getId());
                mostrarExito("Criterio eliminado correctamente");

                // Mantener los filtros después de eliminar
                if (cmbFiltroMateria.getValue() != null || cmbFiltroParcial.getValue() != null) {
                    aplicarFiltros(cmbFiltroMateria, cmbFiltroParcial);
                } else {
                    cargarDatos(tabla);
                }

                // ✅ Refrescar la tabla para actualizar los botones de orden
                tabla.refresh();
            }
        } catch (Exception e) {
            manejarExcepcion("eliminar criterio", e);
        }
    }

    private boolean validarFormulario() {
        if (!validarCampoNoVacio(txtNombre.getText(), "Nombre")) return false;

        if (cmbTipoEvaluacion.getValue() == null) {
            mostrarError("Debe seleccionar un tipo de evaluación");
            return false;
        }

        if (cmbTipoEvaluacion.getValue().equals("Puntuacion") && txtPuntuacionMaxima.getText().trim().isEmpty()) {
            mostrarError("La puntuación máxima es obligatoria para tipo 'Puntuacion'");
            return false;
        }

        if (cmbMateria.getValue() == null) {
            mostrarError("Debe seleccionar una materia");
            return false;
        }

        if (cmbParcial.getValue() == null) {
            mostrarError("Debe seleccionar un parcial");
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        criterioIdEnEdicion = null;
        txtNombre.clear();
        cmbTipoEvaluacion.setValue(null);
        txtPuntuacionMaxima.clear();
        cmbMateria.setValue(null);
        cmbParcial.setValue(null);
        txtOrden.clear();

        // Ocultar el campo orden (solo se muestra al editar)
        if (lblOrden != null) {
            lblOrden.setVisible(false);
            lblOrden.setManaged(false);
        }
        if (txtOrden != null) {
            txtOrden.setVisible(false);
            txtOrden.setManaged(false);
        }

        // Resetear el texto del botón
        if (btnGuardar != null) {
            btnGuardar.setText("Guardar");
            btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        }
    }

    private void cargarCriterioEnFormulario(Criterio criterio) {
        criterioIdEnEdicion = criterio.getId();
        txtNombre.setText(criterio.getNombre());
        cmbTipoEvaluacion.setValue(criterio.getTipoEvaluacion());

        if (criterio.getPuntuacionMaxima() != null) {
            txtPuntuacionMaxima.setText(String.valueOf(criterio.getPuntuacionMaxima()));
        } else {
            txtPuntuacionMaxima.clear();
        }

        // Cargar materia
        if (criterio.getMateriaId() != null) {
            materiaService.obtenerMateriaPorId(criterio.getMateriaId())
                .ifPresent(materia -> cmbMateria.setValue(materia));
        }

        cmbParcial.setValue(criterio.getParcial());

        // Mostrar y cargar el campo orden al editar
        if (lblOrden != null) {
            lblOrden.setVisible(true);
            lblOrden.setManaged(true);
        }
        if (txtOrden != null) {
            txtOrden.setVisible(true);
            txtOrden.setManaged(true);
            if (criterio.getOrden() != null) {
                txtOrden.setText(String.valueOf(criterio.getOrden()));
            } else {
                txtOrden.clear();
            }
        }

        // Cambiar el texto del botón a "Actualizar"
        if (btnGuardar != null) {
            btnGuardar.setText("Actualizar");
            btnGuardar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        }
    }

    private void cargarMaterias(ComboBox<Materia> combo) {
        try {
            List<Materia> materias = materiaService.obtenerTodasLasMaterias();
            combo.setItems(FXCollections.observableArrayList(materias));
        } catch (Exception e) {
            manejarExcepcion("cargar materias", e);
        }
    }

    private void cargarDatos(TableView<Criterio> tabla) {
        try {
            List<Criterio> criterios = criterioService.obtenerTodosLosCriterios();

            // Ordenar por campo "Orden" (null al final)
            criterios.sort((c1, c2) -> {
                if (c1.getOrden() == null && c2.getOrden() == null) return 0;
                if (c1.getOrden() == null) return 1;
                if (c2.getOrden() == null) return -1;
                return c1.getOrden().compareTo(c2.getOrden());
            });

            tabla.setItems(FXCollections.observableArrayList(criterios));
        } catch (Exception e) {
            manejarExcepcion("cargar criterios", e);
        }
    }

    private void aplicarFiltros(ComboBox<Materia> cmbMateria, ComboBox<Integer> cmbParcial) {
        try {
            List<Criterio> criterios = criterioService.obtenerTodosLosCriterios();

            // Aplicar filtro por materia si está seleccionado
            if (cmbMateria.getValue() != null) {
                Long materiaId = cmbMateria.getValue().getId();
                criterios = criterios.stream()
                    .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaId))
                    .collect(java.util.stream.Collectors.toList());
            }

            // Aplicar filtro por parcial si está seleccionado
            if (cmbParcial.getValue() != null) {
                Integer parcial = cmbParcial.getValue();
                criterios = criterios.stream()
                    .filter(c -> c.getParcial() != null && c.getParcial().equals(parcial))
                    .collect(java.util.stream.Collectors.toList());
            }

            // Ordenar por campo "Orden" (null al final)
            criterios.sort((c1, c2) -> {
                if (c1.getOrden() == null && c2.getOrden() == null) return 0;
                if (c1.getOrden() == null) return 1;
                if (c2.getOrden() == null) return -1;
                return c1.getOrden().compareTo(c2.getOrden());
            });

            // Actualizar tabla con resultados filtrados
            if (tablaCriterios != null) {
                tablaCriterios.setItems(FXCollections.observableArrayList(criterios));
            }
        } catch (Exception e) {
            manejarExcepcion("aplicar filtros", e);
        }
    }

    private void limpiarFiltros(ComboBox<Materia> cmbMateria, ComboBox<Integer> cmbParcial) {
        cmbMateria.setValue(null);
        cmbParcial.setValue(null);
        // Recargar todos los datos sin filtros
        if (tablaCriterios != null) {
            cargarDatos(tablaCriterios);
        }
    }

    private void guardarOrdenCriterios() {
        try {
            if (tablaCriterios == null || tablaCriterios.getItems().isEmpty()) {
                mostrarError("No hay criterios para ordenar");
                return;
            }

            // Verificar que haya filtros aplicados
            if (cmbFiltroMateria == null || cmbFiltroMateria.getValue() == null) {
                mostrarAdvertencia("Debe seleccionar una materia en los filtros antes de guardar el orden");
                return;
            }

            if (cmbFiltroParcial == null || cmbFiltroParcial.getValue() == null) {
                mostrarAdvertencia("Debe seleccionar un parcial en los filtros antes de guardar el orden");
                return;
            }

            List<Criterio> criteriosOrdenados = tablaCriterios.getItems();
            Materia materiaFiltro = cmbFiltroMateria.getValue();
            Integer parcialFiltro = cmbFiltroParcial.getValue();

            // Verificar que todos los criterios sean de la misma materia y parcial
            for (Criterio criterio : criteriosOrdenados) {
                if (!criterio.getMateriaId().equals(materiaFiltro.getId())) {
                    mostrarError("Todos los criterios deben ser de la misma materia");
                    return;
                }
                if (!criterio.getParcial().equals(parcialFiltro)) {
                    mostrarError("Todos los criterios deben ser del mismo parcial");
                    return;
                }
            }

            // Actualizar el orden en la base de datos según la posición actual en la tabla
            int nuevoOrden = 1;
            for (Criterio criterio : criteriosOrdenados) {
                criterioService.actualizarOrdenCriterio(criterio.getId(), nuevoOrden++);
            }

            mostrarExito("El orden de los criterios de la materia '" + materiaFiltro.getNombre() +
                        "' (Parcial " + parcialFiltro + ") se guardó correctamente");

            // Recargar la tabla manteniendo los filtros
            aplicarFiltros(cmbFiltroMateria, cmbFiltroParcial);

        } catch (Exception e) {
            manejarExcepcion("guardar orden de criterios", e);
        }
    }
}


