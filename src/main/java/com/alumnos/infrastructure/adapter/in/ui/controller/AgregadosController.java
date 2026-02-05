package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Agregado;
import com.alumnos.domain.model.Criterio;
import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.AgregadoServicePort;
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
    private TableView<Agregado> tablaAgregados; // 📋 Referencia a la tabla

    // 📋 Referencias a los filtros de tabla
    private ComboBox<Materia> cmbFiltroMateria;
    private ComboBox<Integer> cmbFiltroParcial;
    private ComboBox<Criterio> cmbFiltroCriterio;

    public AgregadosController(AgregadoServicePort agregadoService, CriterioServicePort criterioService, MateriaServicePort materiaService) {
        this.agregadoService = agregadoService;
        this.criterioService = criterioService;
        this.materiaService = materiaService;
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

        Label lblFormTitle = new Label("Registrar Nuevo Agregado");
        lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
        gridForm.setHgap(20);  // Espacio entre columnas
        gridForm.setVgap(10);  // Espacio entre filas

        // ========== COLUMNA 1 (izquierda) ==========
        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del agregado");
        txtNombre.setPrefWidth(250);

        // ========== COLUMNA 2 (derecha) ==========
        Label lblMateria = new Label("Materia:");
        lblMateria.setStyle("-fx-font-weight: bold;");
        ComboBox<Materia> cmbMateria = new ComboBox<>();
        cmbMateria.setPromptText("Seleccione una materia");
        cmbMateria.setPrefWidth(250);
        cargarMaterias(cmbMateria);

        Label lblParcial = new Label("Parcial:");
        lblParcial.setStyle("-fx-font-weight: bold;");
        ComboBox<Integer> cmbParcial = new ComboBox<>();
        cmbParcial.setPromptText("Seleccione un parcial");
        cmbParcial.setPrefWidth(250);
        cmbParcial.setDisable(true); // Deshabilitado hasta que se seleccione materia
        cmbParcial.setItems(FXCollections.observableArrayList(1, 2, 3));

        Label lblCriterio = new Label("Criterio:");
        lblCriterio.setStyle("-fx-font-weight: bold;");
        ComboBox<Criterio> cmbCriterio = new ComboBox<>();
        cmbCriterio.setPromptText("Seleccione materia y parcial primero");
        cmbCriterio.setPrefWidth(250);
        cmbCriterio.setDisable(true); // Deshabilitado hasta que se seleccione materia y parcial

        // Lista completa de criterios para filtrar
        final List<Criterio> todosCriterios = new java.util.ArrayList<>();
        try {
            todosCriterios.addAll(criterioService.obtenerTodosLosCriterios());
        } catch (Exception e) {
            manejarExcepcion("cargar criterios", e);
        }

        // Evento: Al seleccionar materia, habilitar parcial
        cmbMateria.setOnAction(event -> {
            if (cmbMateria.getValue() != null) {
                cmbParcial.setDisable(false);
                cmbParcial.setValue(null);
                cmbCriterio.setValue(null);
                cmbCriterio.setDisable(true);
                cmbCriterio.setPromptText("Seleccione un parcial primero");
            } else {
                cmbParcial.setDisable(true);
                cmbParcial.setValue(null);
                cmbCriterio.setValue(null);
                cmbCriterio.setDisable(true);
                cmbCriterio.setPromptText("Seleccione materia y parcial primero");
            }
        });

        // Evento: Al seleccionar parcial, filtrar y habilitar criterio
        cmbParcial.setOnAction(event -> {
            if (cmbParcial.getValue() != null && cmbMateria.getValue() != null) {
                // Filtrar criterios por materia y parcial
                Long materiaId = cmbMateria.getValue().getId();
                Integer parcial = cmbParcial.getValue();

                List<Criterio> criteriosFiltrados = todosCriterios.stream()
                    .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaId))
                    .filter(c -> c.getParcial() != null && c.getParcial().equals(parcial))
                    .collect(Collectors.toList());

                cmbCriterio.setItems(FXCollections.observableArrayList(criteriosFiltrados));
                cmbCriterio.setDisable(false);
                cmbCriterio.setValue(null);
                cmbCriterio.setPromptText(criteriosFiltrados.isEmpty() ?
                    "No hay criterios disponibles" : "Seleccione un criterio");
            } else {
                cmbCriterio.setValue(null);
                cmbCriterio.setDisable(true);
                cmbCriterio.setPromptText("Seleccione un parcial primero");
            }
        });

        // Agregar componentes al GridPane en 2 columnas
        // Columna 1 (izquierda): columnas 0-1
        gridForm.add(lblNombre, 0, 0);
        gridForm.add(txtNombre, 1, 0);

        // Columna 2 (derecha): columnas 2-3
        gridForm.add(lblMateria, 2, 0);
        gridForm.add(cmbMateria, 3, 0);
        gridForm.add(lblParcial, 2, 1);
        gridForm.add(cmbParcial, 3, 1);
        gridForm.add(lblCriterio, 2, 2);
        gridForm.add(cmbCriterio, 3, 2);

        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0;");

        Button btnGuardar = new Button("Guardar");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> guardarAgregado(txtNombre, cmbCriterio));

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> {
            txtNombre.clear();
            cmbMateria.setValue(null);
            cmbParcial.setValue(null);
            cmbParcial.setDisable(true);
            cmbCriterio.setValue(null);
            cmbCriterio.setDisable(true);
            cmbCriterio.setPromptText("Seleccione materia y parcial primero");
        });

        buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);

        formulario.getChildren().addAll(lblFormTitle, new javafx.scene.control.Separator(), gridForm, buttonBox);
        return formulario;
    }


    private VBox crearTabla() {
        VBox contenedor = new VBox(10);
        contenedor.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblTableTitle = new Label("Lista de Agregados");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // ========== FILTROS (sin título) ==========
        javafx.scene.layout.HBox filterBox = new javafx.scene.layout.HBox(10);
        filterBox.setStyle("-fx-alignment: center-left; -fx-padding: 10 0;");

        // ComboBox Materia
        Label lblFiltroMateria = new Label("Materia:");
        lblFiltroMateria.setStyle("-fx-font-weight: bold;");
        cmbFiltroMateria = new ComboBox<>();
        cmbFiltroMateria.setPromptText("Seleccione materia");
        cmbFiltroMateria.setPrefWidth(200);
        cargarMaterias(cmbFiltroMateria);

        // ComboBox Parcial
        Label lblFiltroParcial = new Label("Parcial:");
        lblFiltroParcial.setStyle("-fx-font-weight: bold;");
        cmbFiltroParcial = new ComboBox<>();
        cmbFiltroParcial.setPromptText("Seleccione parcial");
        cmbFiltroParcial.setPrefWidth(150);
        cmbFiltroParcial.setDisable(true);
        cmbFiltroParcial.setItems(FXCollections.observableArrayList(1, 2, 3));

        // ComboBox Criterio
        Label lblFiltroCriterio = new Label("Criterio:");
        lblFiltroCriterio.setStyle("-fx-font-weight: bold;");
        cmbFiltroCriterio = new ComboBox<>();
        cmbFiltroCriterio.setPromptText("Seleccione criterio");
        cmbFiltroCriterio.setPrefWidth(250);
        cmbFiltroCriterio.setDisable(true);

        // Lista completa de criterios para filtrar
        final List<Criterio> todosCriterios = new java.util.ArrayList<>();
        try {
            todosCriterios.addAll(criterioService.obtenerTodosLosCriterios());
        } catch (Exception e) {
            manejarExcepcion("cargar criterios", e);
        }

        // Evento: Al seleccionar materia, habilitar parcial
        cmbFiltroMateria.setOnAction(event -> {
            if (cmbFiltroMateria.getValue() != null) {
                cmbFiltroParcial.setDisable(false);
                cmbFiltroParcial.setValue(null);
                cmbFiltroCriterio.setValue(null);
                cmbFiltroCriterio.setDisable(true);
            } else {
                cmbFiltroParcial.setDisable(true);
                cmbFiltroParcial.setValue(null);
                cmbFiltroCriterio.setValue(null);
                cmbFiltroCriterio.setDisable(true);
            }
            // Refrescar tabla para actualizar botones de orden
            if (tablaAgregados != null) {
                tablaAgregados.refresh();
            }
        });

        // Evento: Al seleccionar parcial, filtrar y habilitar criterio
        cmbFiltroParcial.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && cmbFiltroMateria.getValue() != null) {
                Long materiaId = cmbFiltroMateria.getValue().getId();
                Integer parcial = newVal;

                List<Criterio> criteriosFiltrados = todosCriterios.stream()
                    .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaId))
                    .filter(c -> c.getParcial() != null && c.getParcial().equals(parcial))
                    .collect(Collectors.toList());

                cmbFiltroCriterio.setItems(FXCollections.observableArrayList(criteriosFiltrados));
                cmbFiltroCriterio.setDisable(false);
                cmbFiltroCriterio.setValue(null);
            } else {
                cmbFiltroCriterio.setValue(null);
                cmbFiltroCriterio.setDisable(true);
            }
            // Refrescar tabla para actualizar botones de orden
            if (tablaAgregados != null) {
                tablaAgregados.refresh();
            }
        });

        // Evento: Al seleccionar criterio, refrescar tabla
        cmbFiltroCriterio.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Refrescar tabla para actualizar botones de orden
            if (tablaAgregados != null) {
                tablaAgregados.refresh();
            }
        });

        Button btnFiltrar = new Button("Filtrar");
        btnFiltrar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-cursor: hand;");
        btnFiltrar.setOnAction(e -> aplicarFiltrosTabla(cmbFiltroMateria, cmbFiltroParcial, cmbFiltroCriterio));

        Button btnLimpiarFiltros = new Button("Limpiar filtros");
        btnLimpiarFiltros.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-cursor: hand;");
        btnLimpiarFiltros.setOnAction(e -> {
            cmbFiltroMateria.setValue(null);
            cmbFiltroParcial.setValue(null);
            cmbFiltroParcial.setDisable(true);
            cmbFiltroCriterio.setValue(null);
            cmbFiltroCriterio.setDisable(true);
            cargarDatos(tablaAgregados);
        });

        filterBox.getChildren().addAll(
            lblFiltroMateria, cmbFiltroMateria,
            lblFiltroParcial, cmbFiltroParcial,
            lblFiltroCriterio, cmbFiltroCriterio,
            btnFiltrar, btnLimpiarFiltros
        );

        tablaAgregados = new TableView<>(); // 📋 Guardar referencia

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

        // Columna Acciones
        TableColumn<Agregado, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(100);
        colAcciones.setCellFactory(param -> new TableCell<Agregado, Void>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
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
                    setGraphic(btnEliminar);
                }
            }
        });

        tablaAgregados.getColumns().addAll(colNombre, colCriterio, colMateria, colParcial, colOrden, colOrdenAcciones, colAcciones);
        cargarDatos(tablaAgregados);

        // Botón para guardar el orden
        Button btnGuardarOrden = new Button("💾 Guardar Orden");
        btnGuardarOrden.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");
        btnGuardarOrden.setOnAction(e -> guardarOrdenAgregados());

        Label lblInfo = new Label("💡 Selecciona Materia, Parcial y Criterio en los filtros para ordenar los agregados usando ↑ ↓");
        lblInfo.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

        contenedor.getChildren().addAll(lblTableTitle, filterBox, new javafx.scene.control.Separator(), tablaAgregados, lblInfo, btnGuardarOrden);
        return contenedor;
    }

    private void guardarAgregado(TextField txtNombre, ComboBox<Criterio> cmbCriterio) {
        try {
            if (!validarFormulario(txtNombre, cmbCriterio)) return;

            // El orden será asignado automáticamente por el servicio
            Agregado agregado = Agregado.builder()
                .nombre(txtNombre.getText())
                .criterioId(cmbCriterio.getValue().getId())
                .build();

            agregadoService.crearAgregado(agregado);
            mostrarExito("Agregado guardado correctamente. El orden fue asignado automáticamente.");
            limpiarFormulario(txtNombre, cmbCriterio);

            // ⚡ RECARGAR LA TABLA después de guardar manteniendo los filtros
            if (tablaAgregados != null) {
                // Si hay filtros activos, aplicar filtros; si no, cargar todos
                if (cmbFiltroMateria.getValue() != null || cmbFiltroParcial.getValue() != null || cmbFiltroCriterio.getValue() != null) {
                    aplicarFiltrosTabla(cmbFiltroMateria, cmbFiltroParcial, cmbFiltroCriterio);
                } else {
                    cargarDatos(tablaAgregados);
                }
            }
        } catch (Exception e) {
            manejarExcepcion("guardar agregado", e);
        }
    }

    private void eliminarAgregado(Agregado agregado, TableView<Agregado> tabla) {
        try {
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

    private void limpiarFormulario(TextField txtNombre, ComboBox<Criterio> cmbCriterio) {
        txtNombre.clear();
        cmbCriterio.setValue(null);
    }

    private void cargarCriterios(ComboBox<Criterio> combo) {
        try {
            List<Criterio> criterios = criterioService.obtenerTodosLosCriterios();
            combo.setItems(FXCollections.observableArrayList(criterios));
        } catch (Exception e) {
            manejarExcepcion("cargar criterios", e);
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
        } catch (Exception e) {
            manejarExcepcion("cargar agregados", e);
        }
    }

    private void aplicarFiltrosTabla(ComboBox<Materia> cmbMateria, ComboBox<Integer> cmbParcial, ComboBox<Criterio> cmbCriterio) {
        try {
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

            if (agregados.isEmpty()) {
                mostrarInformacion("No se encontraron agregados con los filtros seleccionados");
            }
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

        } catch (Exception e) {
            manejarExcepcion("guardar orden de agregados", e);
        }
    }
}
