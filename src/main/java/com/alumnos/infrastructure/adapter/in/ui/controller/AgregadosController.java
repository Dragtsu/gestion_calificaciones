package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Agregado;
import com.alumnos.domain.model.Criterio;
import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.AgregadoServicePort;
import com.alumnos.domain.port.in.CalificacionConcentradoServicePort;
import com.alumnos.domain.port.in.CriterioServicePort;
import com.alumnos.domain.port.in.MateriaServicePort;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión de agregados (componentes de criterios)
 * Responsabilidad: Manejar la vista y operaciones CRUD de agregados
 */
@Component
public class AgregadosController extends BaseController {

    private final AgregadoServicePort agregadoService;
    private final CriterioServicePort criterioService;
    private final MateriaServicePort materiaService;
    private final CalificacionConcentradoServicePort calificacionConcentradoService;
    private TableView<Agregado> tablaAgregados; // 📋 Referencia a la tabla

    // 📋 Referencias a los componentes del formulario
    private Label lblFormTitle; // 📋 Referencia al título del formulario
    private ComboBox<Materia> cmbFormMateria;
    private ComboBox<Integer> cmbFormParcial;
    private ComboBox<Criterio> cmbFormCriterio;
    private TextField txtFormNombre; // 📋 Referencia al campo nombre del formulario
    private TextField txtFormDescripcion; // 📋 Referencia al campo descripcion del formulario
    private Button btnCancelarEdicion; // 📋 Referencia al botón Cancelar Edición
    private List<Criterio> todosCriteriosFormulario; // 📋 Lista de criterios para el formulario
    private Long agregadoIdEnEdicion = null; // 📋 ID del agregado en edición (null si es nuevo)
    private Long criterioIdOriginal = null; // 📋 Criterio original del agregado en edición
    private Integer ordenOriginal = null; // 📋 Orden original del agregado en edición

    // 📋 Referencias a los filtros de tabla
    private ComboBox<Materia> cmbFiltroMateria;
    private ComboBox<Integer> cmbFiltroParcial;
    private ComboBox<Criterio> cmbFiltroCriterio;

    public AgregadosController(AgregadoServicePort agregadoService, CriterioServicePort criterioService,
                               MateriaServicePort materiaService, CalificacionConcentradoServicePort calificacionConcentradoService) {
        this.agregadoService = agregadoService;
        this.criterioService = criterioService;
        this.materiaService = materiaService;
        this.calificacionConcentradoService = calificacionConcentradoService;
    }

    public VBox crearVista() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        vista.getChildren().addAll(
            crearFormulario(),
            crearTabla()
        );
        return vista;
    }

    private VBox crearFormulario() {
        VBox formulario = new VBox(10);
        formulario.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        lblFormTitle = new Label("Registrar Nuevo Agregado"); // 📋 Guardar referencia
        lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
        gridForm.setHgap(20);  // Espacio entre columnas
        gridForm.setVgap(10);  // Espacio entre filas

        // ========== COLUMNA 1 (izquierda) ==========
        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        txtFormNombre = new TextField(); // 📋 Guardar referencia
        txtFormNombre.setPromptText("Nombre del agregado");
        txtFormNombre.setPrefWidth(250);

        Label lblDescripcion = new Label("Descripción:");
        lblDescripcion.setStyle("-fx-font-weight: bold;");
        txtFormDescripcion = new TextField(); // 📋 Guardar referencia
        txtFormDescripcion.setPromptText("Descripción del agregado (opcional)");
        txtFormDescripcion.setPrefWidth(250);

        // ========== COLUMNA 2 (derecha) ==========
        Label lblMateria = new Label("Materia:");
        lblMateria.setStyle("-fx-font-weight: bold;");
        cmbFormMateria = new ComboBox<>();
        cmbFormMateria.setPromptText("Seleccione una materia");
        cmbFormMateria.setPrefWidth(250);
        cargarMaterias(cmbFormMateria);

        Label lblParcial = new Label("Parcial:");
        lblParcial.setStyle("-fx-font-weight: bold;");
        cmbFormParcial = new ComboBox<>(); // 📋 Guardar referencia
        cmbFormParcial.setPromptText("Seleccione un parcial");
        cmbFormParcial.setPrefWidth(250);
        cmbFormParcial.setDisable(true); // Deshabilitado hasta que se seleccione materia
        cmbFormParcial.setItems(FXCollections.observableArrayList(1, 2, 3));

        Label lblCriterio = new Label("Criterio:");
        lblCriterio.setStyle("-fx-font-weight: bold;");
        cmbFormCriterio = new ComboBox<>(); // 📋 Guardar referencia
        cmbFormCriterio.setPromptText("Seleccione materia y parcial primero");
        cmbFormCriterio.setPrefWidth(250);
        cmbFormCriterio.setDisable(true); // Deshabilitado hasta que se seleccione materia y parcial

        // Lista completa de criterios para filtrar
        todosCriteriosFormulario = new java.util.ArrayList<>(); // 📋 Guardar referencia
        try {
            todosCriteriosFormulario.addAll(criterioService.obtenerTodosLosCriterios());
        } catch (Exception e) {
            manejarExcepcion("cargar criterios", e);
        }

        // Evento: Al seleccionar materia, habilitar parcial
        cmbFormMateria.setOnAction(event -> {
            if (cmbFormMateria.getValue() != null) {
                cmbFormParcial.setDisable(false);
                cmbFormParcial.setValue(null);
                cmbFormCriterio.setValue(null);
                cmbFormCriterio.setDisable(true);
                cmbFormCriterio.setPromptText("Seleccione un parcial primero");
            } else {
                cmbFormParcial.setDisable(true);
                cmbFormParcial.setValue(null);
                cmbFormCriterio.setValue(null);
                cmbFormCriterio.setDisable(true);
                cmbFormCriterio.setPromptText("Seleccione materia y parcial primero");
            }
        });

        // Evento: Al seleccionar parcial, filtrar y habilitar criterio
        cmbFormParcial.setOnAction(event -> {
            if (cmbFormParcial.getValue() != null && cmbFormMateria.getValue() != null) {
                // Filtrar criterios por materia y parcial
                Long materiaId = cmbFormMateria.getValue().getId();
                Integer parcial = cmbFormParcial.getValue();

                List<Criterio> criteriosFiltrados = todosCriteriosFormulario.stream()
                    .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaId))
                    .filter(c -> c.getParcial() != null && c.getParcial().equals(parcial))
                    .collect(Collectors.toList());

                cmbFormCriterio.setItems(FXCollections.observableArrayList(criteriosFiltrados));
                cmbFormCriterio.setDisable(false);
                cmbFormCriterio.setValue(null);
                cmbFormCriterio.setPromptText(criteriosFiltrados.isEmpty() ?
                    "No hay criterios disponibles" : "Seleccione un criterio");
            } else {
                cmbFormCriterio.setValue(null);
                cmbFormCriterio.setDisable(true);
                cmbFormCriterio.setPromptText("Seleccione un parcial primero");
            }
        });

        // Agregar componentes al GridPane en 2 columnas
        // Columna 1 (izquierda): columnas 0-1
        gridForm.add(lblNombre, 0, 0);
        gridForm.add(txtFormNombre, 1, 0);
        gridForm.add(lblDescripcion, 0, 1);
        gridForm.add(txtFormDescripcion, 1, 1);

        // Columna 2 (derecha): columnas 2-3        // Columna 2 (derecha): columnas 2-3
        gridForm.add(lblMateria, 2, 0);
        gridForm.add(cmbFormMateria, 3, 0);
        gridForm.add(lblParcial, 2, 1);
        gridForm.add(cmbFormParcial, 3, 1);
        gridForm.add(lblCriterio, 2, 2);
        gridForm.add(cmbFormCriterio, 3, 2);

        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0;");

        Button btnGuardar = new Button("Guardar");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> guardarAgregado());

        btnCancelarEdicion = new Button("Cancelar Edición"); // 📋 Guardar referencia
        btnCancelarEdicion.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");
        btnCancelarEdicion.setVisible(false); // Oculto por defecto
        btnCancelarEdicion.setManaged(false); // No ocupa espacio cuando está oculto
        btnCancelarEdicion.setOnAction(e -> limpiarFormulario());

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> limpiarFormulario());

        buttonBox.getChildren().addAll(btnGuardar, btnCancelarEdicion, btnLimpiar);

        formulario.getChildren().addAll(lblFormTitle, new javafx.scene.control.Separator(), gridForm, buttonBox);
        return formulario;
    }


    private VBox crearTabla() {
        VBox contenedor = new VBox(10);
        contenedor.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");


        // ========== FILTROS (sin título) ==========
        javafx.scene.layout.HBox filterBox = new javafx.scene.layout.HBox(10);
        filterBox.setStyle("-fx-alignment: center-left; -fx-padding: 10 0;");

        // ComboBox Materia
        Label lblFiltroMateria = new Label("Materia:");
        lblFiltroMateria.setStyle("-fx-font-weight: bold;");
        cmbFiltroMateria = new ComboBox<>();
        cmbFiltroMateria.setPromptText("Todas las materias");
        cmbFiltroMateria.setPrefWidth(200);
        cargarMaterias(cmbFiltroMateria);

        // ComboBox Parcial
        Label lblFiltroParcial = new Label("Parcial:");
        lblFiltroParcial.setStyle("-fx-font-weight: bold;");
        cmbFiltroParcial = new ComboBox<>();
        cmbFiltroParcial.setPromptText("Todos los parciales");
        cmbFiltroParcial.setPrefWidth(150);
        cmbFiltroParcial.setItems(FXCollections.observableArrayList(1, 2, 3));

        // ComboBox Criterio
        Label lblFiltroCriterio = new Label("Criterio:");
        lblFiltroCriterio.setStyle("-fx-font-weight: bold;");
        cmbFiltroCriterio = new ComboBox<>();
        cmbFiltroCriterio.setPromptText("Todos los criterios");
        cmbFiltroCriterio.setPrefWidth(250);
        cmbFiltroCriterio.setDisable(true);

        // Lista completa de criterios para filtrar
        final List<Criterio> todosCriterios = new java.util.ArrayList<>();
        try {
            todosCriterios.addAll(criterioService.obtenerTodosLosCriterios());
        } catch (Exception e) {
            manejarExcepcion("cargar criterios", e);
        }

        // Evento: Al seleccionar materia, habilitar parcial y aplicar filtro automáticamente
        cmbFiltroMateria.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cmbFiltroParcial.setDisable(false);
                // Seleccionar el primer parcial por defecto si no hay ninguno seleccionado
                if (!cmbFiltroParcial.getItems().isEmpty() && cmbFiltroParcial.getValue() == null) {
                    cmbFiltroParcial.getSelectionModel().selectFirst();
                } else if (oldVal != null) {
                    // Si se cambió de materia, resetear parcial y criterio
                    cmbFiltroParcial.setValue(null);
                    cmbFiltroCriterio.setValue(null);
                    cmbFiltroCriterio.setDisable(true);
                    // Seleccionar el primer parcial
                    if (!cmbFiltroParcial.getItems().isEmpty()) {
                        cmbFiltroParcial.getSelectionModel().selectFirst();
                    }
                }
            } else {
                cmbFiltroParcial.setDisable(true);
                cmbFiltroParcial.setValue(null);
                cmbFiltroCriterio.setValue(null);
                cmbFiltroCriterio.setDisable(true);
            }
            // Aplicar filtros automáticamente
            aplicarFiltrosTabla(cmbFiltroMateria, cmbFiltroParcial, cmbFiltroCriterio);
        });

        // Evento: Al seleccionar parcial, filtrar y habilitar criterio automáticamente
        cmbFiltroParcial.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && cmbFiltroMateria.getValue() != null) {
                Long materiaId = cmbFiltroMateria.getValue().getId();
                Integer parcial = newVal;

                List<Criterio> criteriosFiltrados = todosCriterios.stream()
                    .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaId))
                    .filter(c -> c.getParcial() != null && c.getParcial().equals(parcial))
                    .collect(Collectors.toList());

                cmbFiltroCriterio.setItems(FXCollections.observableArrayList(criteriosFiltrados));
                cmbFiltroCriterio.setDisable(criteriosFiltrados.isEmpty());

                // 🎯 Seleccionar el primer criterio automáticamente si hay datos disponibles
                if (!criteriosFiltrados.isEmpty()) {
                    cmbFiltroCriterio.getSelectionModel().selectFirst();
                } else {
                    cmbFiltroCriterio.setValue(null);
                }
            } else {
                cmbFiltroCriterio.setValue(null);
                cmbFiltroCriterio.setDisable(true);
            }
            // Aplicar filtros automáticamente
            aplicarFiltrosTabla(cmbFiltroMateria, cmbFiltroParcial, cmbFiltroCriterio);
        });

        // Evento: Al seleccionar criterio, aplicar filtro automáticamente
        cmbFiltroCriterio.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Aplicar filtros automáticamente
            aplicarFiltrosTabla(cmbFiltroMateria, cmbFiltroParcial, cmbFiltroCriterio);
        });

        filterBox.getChildren().addAll(
            lblFiltroMateria, cmbFiltroMateria,
            lblFiltroParcial, cmbFiltroParcial,
            lblFiltroCriterio, cmbFiltroCriterio
        );

        tablaAgregados = new TableView<>(); // 📋 Guardar referencia (DEBE estar antes de selectFirst)

        // Columna Nombre
        TableColumn<Agregado, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colNombre.setPrefWidth(200);

        // Columna Criterio
        TableColumn<Agregado, String> colCriterio = new TableColumn<>("Criterio");
        colCriterio.setCellValueFactory(data -> {
            Agregado agregado = data.getValue();
            if (agregado.getCriterioId() != null) {
                return criterioService.obtenerCriterioPorId(agregado.getCriterioId())
                    .map(c -> new javafx.beans.property.SimpleStringProperty(c.getNombre()))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        colCriterio.setPrefWidth(180);

        // Columna Materia
        TableColumn<Agregado, String> colMateria = new TableColumn<>("Materia");
        colMateria.setCellValueFactory(data -> {
            Agregado agregado = data.getValue();
            if (agregado.getCriterioId() != null) {
                return criterioService.obtenerCriterioPorId(agregado.getCriterioId())
                    .flatMap(criterio -> materiaService.obtenerMateriaPorId(criterio.getMateriaId()))
                    .map(m -> new javafx.beans.property.SimpleStringProperty(m.getNombre()))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        colMateria.setPrefWidth(150);

        // Columna Parcial
        TableColumn<Agregado, String> colParcial = new TableColumn<>("Parcial");
        colParcial.setCellValueFactory(data -> {
            Agregado agregado = data.getValue();
            if (agregado.getCriterioId() != null) {
                return criterioService.obtenerCriterioPorId(agregado.getCriterioId())
                    .map(criterio -> new javafx.beans.property.SimpleStringProperty(
                        criterio.getParcial() != null ? String.valueOf(criterio.getParcial()) : "N/A"
                    ))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        colParcial.setPrefWidth(80);
        colParcial.setStyle("-fx-alignment: CENTER;");

        // Columna Orden
        TableColumn<Agregado, String> colOrden = new TableColumn<>("Orden");
        colOrden.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getOrden() != null ?
                String.valueOf(data.getValue().getOrden()) : "N/A"));
        colOrden.setPrefWidth(80);
        colOrden.setStyle("-fx-alignment: CENTER;");

        // Columna de Ordenamiento (Subir/Bajar)
        TableColumn<Agregado, Void> colOrdenAcciones = new TableColumn<>("Ordenar");
        colOrdenAcciones.setPrefWidth(120);
        colOrdenAcciones.setCellFactory(param -> new TableCell<Agregado, Void>() {
            private final Button btnSubir = new Button("↑");
            private final Button btnBajar = new Button("↓");

            {
                btnSubir.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10; -fx-cursor: hand;");
                btnBajar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10; -fx-cursor: hand;");

                btnSubir.setOnAction(event -> {
                    int index = getIndex();
                    javafx.collections.ObservableList<Agregado> items = getTableView().getItems();
                    if (index > 0) {
                        Agregado agregado = items.get(index);
                        items.remove(index);
                        items.add(index - 1, agregado);
                        getTableView().getSelectionModel().select(index - 1);
                        getTableView().refresh();
                    }
                });

                btnBajar.setOnAction(event -> {
                    int index = getIndex();
                    javafx.collections.ObservableList<Agregado> items = getTableView().getItems();
                    if (index < items.size() - 1) {
                        Agregado agregado = items.get(index);
                        items.remove(index);
                        items.add(index + 1, agregado);
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

                // 🔒 Verificar que los 3 filtros estén seleccionados
                boolean filtrosCompletos = cmbFiltroMateria != null && cmbFiltroMateria.getValue() != null &&
                                          cmbFiltroParcial != null && cmbFiltroParcial.getValue() != null &&
                                          cmbFiltroCriterio != null && cmbFiltroCriterio.getValue() != null;

                if (!filtrosCompletos) {
                    setGraphic(null);
                    return;
                }

                // Mostrar controles solo si hay más de 1 agregado
                javafx.collections.ObservableList<Agregado> items = getTableView().getItems();
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

        // Columna Descripción
        TableColumn<Agregado, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDescripcion() != null ? data.getValue().getDescripcion() : ""));
        colDescripcion.setPrefWidth(200);

        // Columna Acciones
        TableColumn<Agregado, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(180);
        colAcciones.setCellFactory(param -> new TableCell<Agregado, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final javafx.scene.layout.HBox contenedor = new javafx.scene.layout.HBox(5);

            {
                btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");

                contenedor.setAlignment(javafx.geometry.Pos.CENTER);
                contenedor.getChildren().addAll(btnEditar, btnEliminar);

                btnEditar.setOnAction(e -> {
                    Agregado agregado = getTableView().getItems().get(getIndex());
                    editarAgregado(agregado);
                });

                btnEliminar.setOnAction(e -> {
                    Agregado agregado = getTableView().getItems().get(getIndex());
                    eliminarAgregado(agregado, tablaAgregados);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(contenedor);
                }
            }
        });

        tablaAgregados.getColumns().addAll(colNombre, colCriterio, colMateria, colParcial, colOrden, colOrdenAcciones, colDescripcion, colAcciones);

        // NO cargar datos inicialmente, dejar que los filtros automáticos se encarguen
        // cargarDatos(tablaAgregados); // ❌ Eliminado - los filtros se encargarán de la carga inicial

        // Botón para guardar el orden
        Button btnGuardarOrden = new Button("💾 Guardar Orden");
        btnGuardarOrden.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");
        btnGuardarOrden.setOnAction(e -> guardarOrdenAgregados());

        Label lblInfo = new Label("💡 Selecciona Materia, Parcial y Criterio en los filtros para ordenar los agregados usando ↑ ↓");
        lblInfo.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

        contenedor.getChildren().addAll(filterBox, new javafx.scene.control.Separator(), tablaAgregados, lblInfo, btnGuardarOrden);

        // 🎯 Seleccionar el primer valor por defecto DESPUÉS de que TODO esté creado
        // Esto disparará automáticamente los eventos y aplicará los filtros
        if (!cmbFiltroMateria.getItems().isEmpty()) {
            cmbFiltroMateria.getSelectionModel().selectFirst();
        }

        return contenedor;
    }

    private void guardarAgregado() {
        try {
            if (!validarFormulario(txtFormNombre, cmbFormCriterio)) return;

            if (agregadoIdEnEdicion != null) {
                // MODO EDICIÓN
                Long criterioIdActual = cmbFormCriterio.getValue().getId();
                boolean cambioDeCriterio = !criterioIdActual.equals(criterioIdOriginal);

                Agregado agregado = Agregado.builder()
                    .id(agregadoIdEnEdicion)
                    .nombre(txtFormNombre.getText().trim())
                    .descripcion(txtFormDescripcion.getText().trim().isEmpty() ? null : txtFormDescripcion.getText().trim())
                    .criterioId(criterioIdActual)
                    .orden(cambioDeCriterio ? null : ordenOriginal) // Mantener orden si no cambió criterio, null si cambió
                    .build();

                agregadoService.actualizarAgregado(agregado);

                if (cambioDeCriterio) {
                    mostrarExito("Agregado actualizado correctamente. Se asignó al final del nuevo criterio.");
                } else {
                    mostrarExito("Agregado actualizado correctamente.");
                }
            } else {
                // MODO CREAR
                Agregado agregado = Agregado.builder()
                    .nombre(txtFormNombre.getText().trim())
                    .descripcion(txtFormDescripcion.getText().trim().isEmpty() ? null : txtFormDescripcion.getText().trim())
                    .criterioId(cmbFormCriterio.getValue().getId())
                    .build();

                agregadoService.crearAgregado(agregado);
                mostrarExito("Agregado guardado correctamente. El orden fue asignado automáticamente.");
            }

            // Limpiar formulario y ocultar botón Cancelar
            limpiarFormulario();

            // ⚡ RECARGAR LA TABLA después de guardar manteniendo los filtros
            if (tablaAgregados != null) {
                // Si hay filtros activos, aplicar filtros; si no, cargar todos
                if (cmbFiltroMateria.getValue() != null || cmbFiltroParcial.getValue() != null || cmbFiltroCriterio.getValue() != null) {
                    aplicarFiltrosTabla(cmbFiltroMateria, cmbFiltroParcial, cmbFiltroCriterio);
                } else {
                    cargarDatos(tablaAgregados);
                }

                // ✅ Refrescar la tabla para actualizar los botones de orden
                tablaAgregados.refresh();
            }
        } catch (Exception e) {
            manejarExcepcion("guardar agregado", e);
        }
    }

    private void editarAgregado(Agregado agregado) {
        try {
            // Verificar si el agregado está siendo usado en el concentrado de calificaciones
            List<com.alumnos.domain.model.CalificacionConcentrado> calificaciones =
                calificacionConcentradoService.obtenerCalificacionesPorAgregado(agregado.getId());

            if (!calificaciones.isEmpty()) {
                // El agregado está siendo usado, no se puede cambiar materia-parcial-criterio
                mostrarAdvertencia("Este agregado ya ha sido utilizado en el concentrado de calificaciones.\n" +
                    "Solo puede editar el nombre y la descripción.");

                // Cargar datos en el formulario pero deshabilitar los combos
                cargarAgregadoEnFormulario(agregado, true);
            } else {
                // El agregado no está siendo usado, se puede editar todo
                cargarAgregadoEnFormulario(agregado, false);
            }
        } catch (Exception e) {
            manejarExcepcion("editar agregado", e);
        }
    }

    private void cargarAgregadoEnFormulario(Agregado agregado, boolean bloquearCriterio) {
        try {
            // Guardar ID, criterio y orden original en edición
            agregadoIdEnEdicion = agregado.getId();
            criterioIdOriginal = agregado.getCriterioId();
            ordenOriginal = agregado.getOrden();

            // Cambiar título del formulario
            lblFormTitle.setText("Editar Agregado");
            lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FF9800;");

            // Mostrar el botón Cancelar Edición
            if (btnCancelarEdicion != null) {
                btnCancelarEdicion.setVisible(true);
                btnCancelarEdicion.setManaged(true);
            }

            // Cargar datos del agregado
            txtFormNombre.setText(agregado.getNombre());
            txtFormDescripcion.setText(agregado.getDescripcion() != null ? agregado.getDescripcion() : "");

            if (bloquearCriterio) {
                // Bloquear cambios en materia-parcial-criterio
                // Cargar el criterio actual pero deshabilitar los combos
                criterioService.obtenerCriterioPorId(agregado.getCriterioId()).ifPresent(criterio -> {
                    materiaService.obtenerMateriaPorId(criterio.getMateriaId()).ifPresent(materia -> {
                        cmbFormMateria.setValue(materia);
                        cmbFormMateria.setDisable(true);

                        cmbFormParcial.setValue(criterio.getParcial());
                        cmbFormParcial.setDisable(true);

                        // Filtrar y cargar criterios
                        List<Criterio> criteriosFiltrados = todosCriteriosFormulario.stream()
                            .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materia.getId()))
                            .filter(c -> c.getParcial() != null && c.getParcial().equals(criterio.getParcial()))
                            .collect(Collectors.toList());

                        cmbFormCriterio.setItems(FXCollections.observableArrayList(criteriosFiltrados));
                        cmbFormCriterio.setValue(criterio);
                        cmbFormCriterio.setDisable(true);
                    });
                });
            } else {
                // Permitir cambios en materia-parcial-criterio
                criterioService.obtenerCriterioPorId(agregado.getCriterioId()).ifPresent(criterio -> {
                    materiaService.obtenerMateriaPorId(criterio.getMateriaId()).ifPresent(materia -> {
                        cmbFormMateria.setValue(materia);
                        cmbFormMateria.setDisable(false);

                        cmbFormParcial.setValue(criterio.getParcial());
                        cmbFormParcial.setDisable(false);

                        // Filtrar y cargar criterios
                        List<Criterio> criteriosFiltrados = todosCriteriosFormulario.stream()
                            .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materia.getId()))
                            .filter(c -> c.getParcial() != null && c.getParcial().equals(criterio.getParcial()))
                            .collect(Collectors.toList());

                        cmbFormCriterio.setItems(FXCollections.observableArrayList(criteriosFiltrados));
                        cmbFormCriterio.setValue(criterio);
                        cmbFormCriterio.setDisable(false);
                    });
                });
            }

            // Hacer scroll al formulario
            txtFormNombre.requestFocus();

        } catch (Exception e) {
            manejarExcepcion("cargar agregado en formulario", e);
        }
    }

    private void eliminarAgregado(Agregado agregado, TableView<Agregado> tabla) {
        try {
            // Verificar si el agregado está siendo usado en el concentrado de calificaciones
            List<com.alumnos.domain.model.CalificacionConcentrado> calificaciones =
                calificacionConcentradoService.obtenerCalificacionesPorAgregado(agregado.getId());

            if (!calificaciones.isEmpty()) {
                mostrarError("No se puede eliminar este agregado porque ya ha sido utilizado en el concentrado de calificaciones.\n" +
                    "Total de calificaciones registradas: " + calificaciones.size());
                return;
            }

            if (confirmarAccion("Confirmar eliminación", "¿Está seguro de eliminar este agregado?")) {
                agregadoService.eliminarAgregado(agregado.getId());
                mostrarExito("Agregado eliminado correctamente");

                // Mantener los filtros después de eliminar
                if (cmbFiltroMateria.getValue() != null || cmbFiltroParcial.getValue() != null || cmbFiltroCriterio.getValue() != null) {
                    aplicarFiltrosTabla(cmbFiltroMateria, cmbFiltroParcial, cmbFiltroCriterio);
                } else {
                    cargarDatos(tabla);
                }

                // ✅ Refrescar la tabla para actualizar los botones de orden
                tabla.refresh();
            }
        } catch (Exception e) {
            manejarExcepcion("eliminar agregado", e);
        }
    }

    private boolean validarFormulario(TextField txtNombre, ComboBox<Criterio> cmbCriterio) {
        if (!validarCampoNoVacio(txtNombre.getText(), "Nombre")) return false;

        if (cmbCriterio.getValue() == null) {
            mostrarError("Debe seleccionar un criterio");
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        // Limpiar campos
        txtFormNombre.clear();
        txtFormDescripcion.clear();
        cmbFormMateria.setValue(null);
        cmbFormParcial.setValue(null);
        cmbFormParcial.setDisable(true);
        cmbFormCriterio.setValue(null);
        cmbFormCriterio.setDisable(true);
        cmbFormCriterio.setPromptText("Seleccione materia y parcial primero");

        // Habilitar combos si estaban deshabilitados
        cmbFormMateria.setDisable(false);
        cmbFormParcial.setDisable(true);
        cmbFormCriterio.setDisable(true);

        // Limpiar modo edición
        agregadoIdEnEdicion = null;
        criterioIdOriginal = null;
        ordenOriginal = null;

        // Restaurar título del formulario
        lblFormTitle.setText("Registrar Nuevo Agregado");
        lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Ocultar botón Cancelar Edición
        if (btnCancelarEdicion != null) {
            btnCancelarEdicion.setVisible(false);
            btnCancelarEdicion.setManaged(false);
        }
    }

    private void cargarCriterios(ComboBox<Criterio> combo) {
        try {
            List<Criterio> criterios = criterioService.obtenerTodosLosCriterios();
            combo.setItems(FXCollections.observableArrayList(criterios));
        } catch (Exception e) {
            manejarExcepcion("cargar criterios", e);
        }
    }

    /**
     * Método público para refrescar la lista de materias (llamado desde MateriasController)
     */
    public void refrescarListaMaterias() {
        if (cmbFormMateria != null) {
            cargarMaterias(cmbFormMateria);
        }
        if (cmbFiltroMateria != null) {
            cargarMaterias(cmbFiltroMateria);
        }
    }

    /**
     * Método público para refrescar la lista de criterios (llamado desde CriteriosController)
     */
    public void refrescarListaCriterios() {
        try {
            // Recargar la lista completa de criterios
            List<Criterio> nuevosCriterios = criterioService.obtenerTodosLosCriterios();

            // Actualizar la lista del formulario
            if (todosCriteriosFormulario != null) {
                todosCriteriosFormulario.clear();
                todosCriteriosFormulario.addAll(nuevosCriterios);

                // Si hay materia y parcial seleccionados en el formulario, actualizar el combo
                if (cmbFormMateria != null && cmbFormMateria.getValue() != null &&
                    cmbFormParcial != null && cmbFormParcial.getValue() != null) {

                    Long materiaId = cmbFormMateria.getValue().getId();
                    Integer parcial = cmbFormParcial.getValue();

                    List<Criterio> criteriosFiltrados = nuevosCriterios.stream()
                        .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaId))
                        .filter(c -> c.getParcial() != null && c.getParcial().equals(parcial))
                        .collect(Collectors.toList());

                    if (cmbFormCriterio != null) {
                        cmbFormCriterio.setItems(FXCollections.observableArrayList(criteriosFiltrados));
                    }
                }
            }

            // Actualizar el combo de filtros de tabla si hay filtros activos
            if (cmbFiltroCriterio != null && cmbFiltroParcial != null && cmbFiltroMateria != null) {
                if (cmbFiltroMateria.getValue() != null && cmbFiltroParcial.getValue() != null) {
                    Long materiaId = cmbFiltroMateria.getValue().getId();
                    Integer parcial = cmbFiltroParcial.getValue();

                    List<Criterio> criteriosFiltrados = nuevosCriterios.stream()
                        .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaId))
                        .filter(c -> c.getParcial() != null && c.getParcial().equals(parcial))
                        .collect(Collectors.toList());

                    cmbFiltroCriterio.setItems(FXCollections.observableArrayList(criteriosFiltrados));
                }
            }
        } catch (Exception e) {
            manejarExcepcion("refrescar criterios", e);
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

    private void cargarDatos(TableView<Agregado> tabla) {
        try {
            List<Agregado> agregados = agregadoService.obtenerTodosLosAgregados();
            tabla.setItems(FXCollections.observableArrayList(agregados));
            // ✅ Refrescar la tabla para actualizar los botones de orden
            tabla.refresh();
        } catch (Exception e) {
            manejarExcepcion("cargar agregados", e);
        }
    }

    private void aplicarFiltrosTabla(ComboBox<Materia> cmbMateria, ComboBox<Integer> cmbParcial, ComboBox<Criterio> cmbCriterio) {
        try {
            // ⚠️ Validar que la tabla esté inicializada antes de usarla
            if (tablaAgregados == null) {
                return; // La tabla aún no está creada, no hacer nada
            }

            List<Agregado> agregados = agregadoService.obtenerTodosLosAgregados();

            // Filtrar por criterio si está seleccionado
            if (cmbCriterio.getValue() != null) {
                Long criterioId = cmbCriterio.getValue().getId();
                agregados = agregados.stream()
                    .filter(a -> a.getCriterioId() != null && a.getCriterioId().equals(criterioId))
                    .sorted((a1, a2) -> {
                        if (a1.getOrden() == null && a2.getOrden() == null) return 0;
                        if (a1.getOrden() == null) return 1;
                        if (a2.getOrden() == null) return -1;
                        return Integer.compare(a1.getOrden(), a2.getOrden());
                    })
                    .collect(Collectors.toList());
            }
            // Si no hay criterio, filtrar por parcial
            else if (cmbParcial.getValue() != null && cmbMateria.getValue() != null) {
                Long materiaId = cmbMateria.getValue().getId();
                Integer parcial = cmbParcial.getValue();

                // Obtener todos los criterios de la materia y parcial
                List<Long> criteriosIds = criterioService.obtenerTodosLosCriterios().stream()
                    .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaId))
                    .filter(c -> c.getParcial() != null && c.getParcial().equals(parcial))
                    .map(Criterio::getId)
                    .collect(Collectors.toList());

                agregados = agregados.stream()
                    .filter(a -> a.getCriterioId() != null && criteriosIds.contains(a.getCriterioId()))
                    .collect(Collectors.toList());
            }
            // Si solo hay materia
            else if (cmbMateria.getValue() != null) {
                Long materiaId = cmbMateria.getValue().getId();

                // Obtener todos los criterios de la materia
                List<Long> criteriosIds = criterioService.obtenerTodosLosCriterios().stream()
                    .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaId))
                    .map(Criterio::getId)
                    .collect(Collectors.toList());

                agregados = agregados.stream()
                    .filter(a -> a.getCriterioId() != null && criteriosIds.contains(a.getCriterioId()))
                    .collect(Collectors.toList());
            }

            tablaAgregados.setItems(FXCollections.observableArrayList(agregados));

            // ✅ Refrescar la tabla para actualizar los botones de orden
            tablaAgregados.refresh();

            // No mostrar alerta si no hay datos - simplemente mostrar tabla vacía
        } catch (Exception e) {
            manejarExcepcion("aplicar filtros", e);
        }
    }

    private void guardarOrdenAgregados() {
        try {
            javafx.collections.ObservableList<Agregado> agregadosOrdenados = tablaAgregados.getItems();

            if (agregadosOrdenados.isEmpty()) {
                mostrarInformacion("No hay agregados para ordenar");
                return;
            }

            // Verificar que todos los agregados sean del mismo criterio
            Long primerCriterioId = agregadosOrdenados.get(0).getCriterioId();
            boolean todosMismoCriterio = agregadosOrdenados.stream()
                .allMatch(a -> a.getCriterioId() != null && a.getCriterioId().equals(primerCriterioId));

            if (!todosMismoCriterio) {
                mostrarAdvertencia("Para guardar el orden, todos los agregados deben ser del mismo criterio. Use el filtro de Criterio.");
                return;
            }

            // Asignar nuevos órdenes
            for (int i = 0; i < agregadosOrdenados.size(); i++) {
                Agregado agregado = agregadosOrdenados.get(i);
                agregado.setOrden(i + 1);
                agregadoService.actualizarAgregado(agregado);
            }

            mostrarExito("Orden guardado correctamente");

            // Mantener los filtros después de guardar el orden
            if (cmbFiltroMateria.getValue() != null || cmbFiltroParcial.getValue() != null || cmbFiltroCriterio.getValue() != null) {
                aplicarFiltrosTabla(cmbFiltroMateria, cmbFiltroParcial, cmbFiltroCriterio);
            } else {
                cargarDatos(tablaAgregados);
            }

            // ✅ Refrescar la tabla para actualizar los botones de orden
            tablaAgregados.refresh();

        } catch (Exception e) {
            manejarExcepcion("guardar orden de agregados", e);
        }
    }
}
