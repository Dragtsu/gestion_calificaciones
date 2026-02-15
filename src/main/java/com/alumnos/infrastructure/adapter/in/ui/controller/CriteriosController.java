package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Criterio;
import com.alumnos.domain.model.Examen;
import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.CriterioServicePort;
import com.alumnos.domain.port.in.ExamenServicePort;
import com.alumnos.domain.port.in.MateriaServicePort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de criterios de evaluación
 * Responsabilidad: Manejar la vista y operaciones CRUD de criterios
 */
@Component
public class CriteriosController extends BaseController {

    private final CriterioServicePort criterioService;
    private final MateriaServicePort materiaService;
    private final ExamenServicePort examenService;
    private TableView<Criterio> tablaCriterios; // 📋 Referencia a la tabla
    private ComboBox<Materia> cmbFiltroMateria; // 📋 Referencia al filtro de materia
    private ComboBox<Integer> cmbFiltroParcial; // 📋 Referencia al filtro de parcial
    private AgregadosController agregadosController; // 📋 Referencia para notificar cambios

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
    private Long materiaIdOriginal = null; // Materia original del criterio en edición
    private Integer parcialOriginal = null; // Parcial original del criterio en edición

    public CriteriosController(CriterioServicePort criterioService,
                              MateriaServicePort materiaService,
                              ExamenServicePort examenService) {
        this.criterioService = criterioService;
        this.materiaService = materiaService;
        this.examenService = examenService;
    }

    /**
     * Establece la referencia al controlador de Agregados para notificar cambios
     */
    public void setAgregadosController(AgregadosController agregadosController) {
        this.agregadosController = agregadosController;
    }

    public VBox crearVista() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        vista.setMaxHeight(Double.MAX_VALUE);
        vista.setMaxWidth(Double.MAX_VALUE);

        // Crear componentes en el orden correcto
        VBox formulario = crearFormulario();
        VBox filtrosYTabla = crearFiltrosYTabla();
        javafx.scene.layout.VBox.setVgrow(filtrosYTabla, javafx.scene.layout.Priority.ALWAYS);

        vista.getChildren().addAll(formulario, filtrosYTabla);

        // Aplicar filtros iniciales después de crear todos los componentes
        javafx.application.Platform.runLater(() -> {
            if (cmbFiltroMateria.getValue() != null || cmbFiltroParcial.getValue() != null) {
                aplicarFiltros(cmbFiltroMateria, cmbFiltroParcial);
            }
        });

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

    private VBox crearFiltrosYTabla() {
        VBox contenedor = new VBox(15);
        contenedor.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        contenedor.setMaxHeight(Double.MAX_VALUE);
        contenedor.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.VBox.setVgrow(contenedor, javafx.scene.layout.Priority.ALWAYS);;

        // Sección de filtros
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

        // Inicializar con el primer valor si hay valores disponibles
        if (!cmbFiltroMateria.getItems().isEmpty()) {
            cmbFiltroMateria.getSelectionModel().selectFirst();
        }
        if (!cmbFiltroParcial.getItems().isEmpty()) {
            cmbFiltroParcial.getSelectionModel().selectFirst();
        }

        // Agregar listeners para aplicar filtros automáticamente al cambiar selección
        cmbFiltroMateria.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarFiltros(cmbFiltroMateria, cmbFiltroParcial);
        });

        cmbFiltroParcial.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarFiltros(cmbFiltroMateria, cmbFiltroParcial);
        });

        filterBox.getChildren().addAll(lblMateria, cmbFiltroMateria, lblParcial, cmbFiltroParcial);

        // Separador visual entre filtros y tabla
        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();

        // Tabla de criterios
        tablaCriterios = new TableView<>(); // 📋 Guardar referencia
        tablaCriterios.setMaxHeight(Double.MAX_VALUE);
        tablaCriterios.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.VBox.setVgrow(tablaCriterios, javafx.scene.layout.Priority.ALWAYS);

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

        contenedor.getChildren().addAll(filterBox, separator, tablaCriterios, lblInfo, btnGuardarOrden);
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

            // ⚠️ VALIDAR QUE LA SUMA NO EXCEDA 100 PUNTOS
            Long materiaId = cmbMateria.getValue().getId();
            Integer parcial = cmbParcial.getValue();

            if (!validarLimitePuntos(materiaId, parcial, puntuacion)) {
                return; // No continuar si la validación falla
            }

            Integer orden = null;

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
                Long materiaIdActual = cmbMateria.getValue().getId();
                Integer parcialActual = cmbParcial.getValue();

                // Verificar si cambió la materia o el parcial
                boolean cambioMateriaParcial = !materiaIdActual.equals(materiaIdOriginal) ||
                                               !parcialActual.equals(parcialOriginal);

                if (!cambioMateriaParcial) {
                    // Si no cambió materia ni parcial, mantener el orden actual del campo
                    if (!txtOrden.getText().trim().isEmpty()) {
                        try {
                            orden = Integer.parseInt(txtOrden.getText().trim());
                        } catch (NumberFormatException e) {
                            mostrarError("El orden debe ser un número válido");
                            return;
                        }
                    }
                }
                // Si cambió materia o parcial, orden queda null y será reasignado por el servicio

                Criterio criterio = Criterio.builder()
                    .id(criterioIdEnEdicion)
                    .nombre(txtNombre.getText().trim())
                    .tipoEvaluacion(cmbTipoEvaluacion.getValue())
                    .puntuacionMaxima(puntuacion)
                    .materiaId(materiaIdActual)
                    .parcial(parcialActual)
                    .orden(orden)
                    .build();

                criterioService.actualizarCriterio(criterio);

                if (cambioMateriaParcial) {
                    mostrarExito("Criterio actualizado. Se asignó un nuevo orden porque cambió la materia o el parcial.");
                } else {
                    mostrarExito("Criterio actualizado correctamente.");
                }
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

            // 🔔 Notificar al controlador de Agregados para que actualice su lista de criterios
            if (agregadosController != null) {
                agregadosController.refrescarListaCriterios();
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

    /**
     * Valida que la suma de puntuación máxima de criterios y total de puntos del examen
     * no supere 100 puntos para una materia y parcial específicos
     *
     * @param materiaId ID de la materia
     * @param parcial Número de parcial
     * @param puntuacionNueva Puntuación del criterio que se está guardando
     * @return true si la validación es exitosa, false si se excede el límite
     */
    private boolean validarLimitePuntos(Long materiaId, Integer parcial, Double puntuacionNueva) {
        try {
            // Si no hay puntuación, no validar (criterios tipo Check sin puntuación)
            if (puntuacionNueva == null || puntuacionNueva == 0) {
                return true;
            }

            // Obtener todos los criterios de la materia y parcial (excluyendo el que se está editando)
            List<Criterio> criteriosExistentes = criterioService.obtenerCriteriosPorMateria(materiaId)
                .stream()
                .filter(c -> c.getParcial().equals(parcial))
                .filter(c -> criterioIdEnEdicion == null || !c.getId().equals(criterioIdEnEdicion))
                .toList();

            // Sumar la puntuación máxima de todos los criterios existentes
            double totalCriterios = criteriosExistentes.stream()
                .mapToDouble(c -> c.getPuntuacionMaxima() != null ? c.getPuntuacionMaxima() : 0.0)
                .sum();

            // Obtener el examen de la materia y parcial
            Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
                null, materiaId, parcial); // null en grupoId porque no necesitamos filtrar por grupo

            // Si no encuentra por grupoId null, buscar en todos los exámenes
            if (examenOpt.isEmpty()) {
                List<Examen> todosExamenes = examenService.obtenerTodosLosExamenes();
                examenOpt = todosExamenes.stream()
                    .filter(e -> e.getMateriaId().equals(materiaId) && e.getParcial().equals(parcial))
                    .findFirst();
            }

            double totalExamen = 0.0;
            if (examenOpt.isPresent()) {
                totalExamen = examenOpt.get().getTotalPuntosExamen() != null ?
                    examenOpt.get().getTotalPuntosExamen() : 0.0;
            }

            // Calcular el total sumando criterios existentes + nueva puntuación + examen
            double sumaTotal = totalCriterios + puntuacionNueva + totalExamen;

            // Validar que no exceda 100
            if (sumaTotal > 100) {
                String mensaje = String.format(
                    "⚠️ SE SOBREPASA EL MÁXIMO DE PUNTOS PERMITIDOS\n\n" +
                    "Desglose:\n" +
                    "• Suma de criterios existentes: %.1f puntos\n" +
                    "• Puntuación de este criterio: %.1f puntos\n" +
                    "• Total puntos del examen: %.1f puntos\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "• TOTAL: %.1f puntos\n\n" +
                    "⚠️ El máximo permitido es 100 puntos.\n" +
                    "Sobrepasa por: %.1f puntos\n\n" +
                    "Por favor, ajuste la puntuación máxima del criterio.",
                    totalCriterios,
                    puntuacionNueva,
                    totalExamen,
                    sumaTotal,
                    sumaTotal - 100
                );

                mostrarAdvertencia(mensaje);
                return false;
            }

            return true;

        } catch (Exception e) {
            LOG.error("Error al validar límite de puntos: {}", e.getMessage());
            mostrarError("Error al validar el límite de puntos: " + e.getMessage());
            return false;
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
        materiaIdOriginal = null;
        parcialOriginal = null;
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

        // Guardar valores originales de materia y parcial
        materiaIdOriginal = criterio.getMateriaId();
        parcialOriginal = criterio.getParcial();

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

    /**
     * Método público para refrescar la lista de materias (llamado desde MateriasController)
     */
    public void refrescarListaMaterias() {
        if (cmbMateria != null) {
            cargarMaterias(cmbMateria);
        }
        if (cmbFiltroMateria != null) {
            cargarMaterias(cmbFiltroMateria);
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
            tabla.refresh(); // 🔄 Forzar refresco de la tabla para que se rendericen los botones

            // 📏 Ajustar columnas al contenido (incluyendo botones)
            Platform.runLater(() -> ajustarColumnasAlContenido(tabla));
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
                tablaCriterios.refresh(); // 🔄 Forzar refresco de la tabla para que se rendericen los botones
            }
        } catch (Exception e) {
            manejarExcepcion("aplicar filtros", e);
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

    private void ajustarColumnasAlContenido(TableView<Criterio> tabla) {
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        for (TableColumn<Criterio, ?> columna : tabla.getColumns()) {
            if ("Acciones".equals(columna.getText()) || "Ordenar".equals(columna.getText())) {
                columna.setPrefWidth(180);
                columna.setMinWidth(180);
                columna.setMaxWidth(180);
                continue;
            }

            double anchoMaximo = calcularAnchoColumna(columna);
            columna.setPrefWidth(anchoMaximo);
        }
    }

    private double calcularAnchoColumna(TableColumn<Criterio, ?> columna) {
        javafx.scene.text.Text textoHeader = new javafx.scene.text.Text(columna.getText());
        double anchoMaximo = textoHeader.getLayoutBounds().getWidth() + 40;

        int filasARevisar = Math.min(tablaCriterios.getItems().size(), 50);

        for (int i = 0; i < filasARevisar; i++) {
            Criterio criterio = tablaCriterios.getItems().get(i);
            String valorCelda = obtenerValorCelda(columna, criterio);

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

    private String obtenerValorCelda(TableColumn<Criterio, ?> columna, Criterio criterio) {
        String nombreColumna = columna.getText();

        switch (nombreColumna) {
            case "Orden":
                return criterio.getOrden() != null ? String.valueOf(criterio.getOrden()) : "";
            case "Nombre":
                return criterio.getNombre() != null ? criterio.getNombre() : "";
            case "Tipo":
                return criterio.getTipoEvaluacion() != null ? criterio.getTipoEvaluacion() : "";
            case "Puntuación Máx":
                return criterio.getPuntuacionMaxima() != null ? String.valueOf(criterio.getPuntuacionMaxima()) : "N/A";
            case "Materia":
                if (criterio.getMateriaId() != null) {
                    try {
                        Optional<Materia> materia = materiaService.obtenerMateriaPorId(criterio.getMateriaId());
                        return materia.map(Materia::getNombre).orElse("N/A");
                    } catch (Exception e) {
                        return "N/A";
                    }
                }
                return "N/A";
            case "Parcial":
                return criterio.getParcial() != null ? String.valueOf(criterio.getParcial()) : "N/A";
            default:
                return "";
        }
    }
}


