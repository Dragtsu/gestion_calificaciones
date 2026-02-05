package com.alumnos.infrastructure.adapter.in.ui.controller;
import com.alumnos.application.service.WordExportService;
import com.alumnos.domain.model.*;
import com.alumnos.domain.port.in.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.awt.Desktop;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Component
public class ConcentradoController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ConcentradoController.class);
    private final CalificacionConcentradoServicePort calificacionConcentradoService;
    private final AlumnoServicePort alumnoService;
    private final AgregadoServicePort agregadoService;
    private final CriterioServicePort criterioService;
    private final GrupoServicePort grupoService;
    private final MateriaServicePort materiaService;
    private final ExamenServicePort examenService;
    private final AlumnoExamenServicePort alumnoExamenService;
    private final WordExportService wordExportService;
    private final ConfiguracionServicePort configuracionService;
    private final GrupoMateriaServicePort grupoMateriaService;
    private BorderPane mainContent;
    public ConcentradoController(CalificacionConcentradoServicePort calificacionConcentradoService,
                                 AlumnoServicePort alumnoService, AgregadoServicePort agregadoService,
                                 CriterioServicePort criterioService, GrupoServicePort grupoService,
                                 MateriaServicePort materiaService, ExamenServicePort examenService,
                                 AlumnoExamenServicePort alumnoExamenService, WordExportService wordExportService,
                                 ConfiguracionServicePort configuracionService, GrupoMateriaServicePort grupoMateriaService) {
        this.calificacionConcentradoService = calificacionConcentradoService;
        this.alumnoService = alumnoService;
        this.agregadoService = agregadoService;
        this.criterioService = criterioService;
        this.grupoService = grupoService;
        this.materiaService = materiaService;
        this.examenService = examenService;
        this.alumnoExamenService = alumnoExamenService;
        this.wordExportService = wordExportService;
        this.configuracionService = configuracionService;
        this.grupoMateriaService = grupoMateriaService;
    }
    public void setMainContent(BorderPane mainContent) {
        this.mainContent = mainContent;
    }
    public VBox crearVistaConcentradoCompleta() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        try {
            // Header
            Label lblTitulo = new Label("Concentrado de Calificaciones");
            lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Panel de filtros
            VBox filtrosPanel = new VBox(15);
            filtrosPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            Label lblFiltros = new Label("Filtros (Obligatorios)");
            lblFiltros.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Fila de filtros
            javafx.scene.layout.HBox filtrosBox = new javafx.scene.layout.HBox(20);
            filtrosBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // ComboBox para seleccionar grupo
            VBox grupoContainer = new VBox(5);
            Label lblGrupo = new Label("Grupo: *");
            lblGrupo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            ComboBox<Grupo> cmbGrupo = new ComboBox<>();
            cmbGrupo.setPrefWidth(150);
            cmbGrupo.setPromptText("Seleccionar...");
            try {
                List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
                cmbGrupo.setItems(FXCollections.observableArrayList(grupos));
            } catch (Exception e) {
                LOG.error("Error al cargar grupos", e);
            }
            cmbGrupo.setCellFactory(param -> new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.valueOf(item.getId()));
                }
            });
            cmbGrupo.setButtonCell(new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Seleccionar..." : String.valueOf(item.getId()));
                }
            });
            grupoContainer.getChildren().addAll(lblGrupo, cmbGrupo);

            // ComboBox para seleccionar materia
            VBox materiaContainer = new VBox(5);
            Label lblMateria = new Label("Materia: *");
            lblMateria.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            ComboBox<Materia> cmbMateria = new ComboBox<>();
            cmbMateria.setPrefWidth(250);
            cmbMateria.setPromptText("Seleccionar...");
            cmbMateria.setDisable(true);
            cmbMateria.setCellFactory(param -> new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
            cmbMateria.setButtonCell(new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Seleccionar..." : item.getNombre());
                }
            });
            materiaContainer.getChildren().addAll(lblMateria, cmbMateria);

            // ComboBox para seleccionar parcial
            VBox parcialContainer = new VBox(5);
            Label lblParcial = new Label("Parcial: *");
            lblParcial.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            ComboBox<Integer> cmbParcial = new ComboBox<>();
            cmbParcial.setPrefWidth(120);
            cmbParcial.setPromptText("Seleccionar...");
            cmbParcial.setItems(FXCollections.observableArrayList(1, 2, 3));
            parcialContainer.getChildren().addAll(lblParcial, cmbParcial);

            // Bot├│n Buscar (antes "Generar Tabla") - en la misma fila que los inputs
            VBox buscarContainer = new VBox(5);
            Label lblEspacio = new Label(" "); // Espacio para alinear con los otros labels
            Button btnBuscar = new Button("Buscar");
            btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            buscarContainer.getChildren().addAll(lblEspacio, btnBuscar);

            filtrosBox.getChildren().addAll(grupoContainer, materiaContainer, parcialContainer, buscarContainer);

            filtrosPanel.getChildren().addAll(lblFiltros, filtrosBox);

            // Panel de tabla
            VBox tablaPanel = new VBox(15);
            tablaPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            // Botones sobre la tabla (Guardar y Generar archivo)
            javafx.scene.layout.HBox botonesTablaBox = new javafx.scene.layout.HBox(10);
            botonesTablaBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

            Button btnGuardar = new Button("Guardar");
            btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            btnGuardar.setDisable(true);

            Button btnGenerarArchivo = new Button("Generar archivo");
            btnGenerarArchivo.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            btnGenerarArchivo.setDisable(true);

            botonesTablaBox.getChildren().addAll(btnGenerarArchivo, btnGuardar);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPrefHeight(500);
            scrollPane.setStyle("-fx-background-color: transparent;");

            TableView<java.util.Map<String, Object>> tblCalificaciones = new TableView<>();
            tblCalificaciones.setEditable(true);
            tblCalificaciones.setPlaceholder(new Label("Seleccione Grupo, Materia y Parcial, luego presione 'Buscar'"));
            tblCalificaciones.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            scrollPane.setContent(tblCalificaciones);
            tablaPanel.getChildren().addAll(botonesTablaBox, scrollPane);

            // L├│gica para cargar materias cuando se selecciona un grupo
            cmbGrupo.setOnAction(event -> {
                Grupo grupoSeleccionado = cmbGrupo.getValue();
                if (grupoSeleccionado != null) {
                    try {
                        List<GrupoMateria> asignaciones = grupoMateriaService.obtenerMateriasPorGrupo(grupoSeleccionado.getId());
                        List<Materia> materias = new java.util.ArrayList<>();
                        for (GrupoMateria gm : asignaciones) {
                            materiaService.obtenerMateriaPorId(gm.getMateriaId()).ifPresent(materias::add);
                        }
                        cmbMateria.setItems(FXCollections.observableArrayList(materias));
                        cmbMateria.setDisable(false);
                    } catch (Exception e) {
                        LOG.error("Error al cargar materias del grupo", e);
                        mostrarAlerta("Error", "No se pudieron cargar las materias del grupo", Alert.AlertType.ERROR);
                    }
                } else {
                    cmbMateria.setItems(FXCollections.observableArrayList());
                    cmbMateria.setDisable(true);
                }
            });

            // Evento del bot├│n Buscar (antes "Generar Tabla")
            btnBuscar.setOnAction(event -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAlerta("Validaci├│n", "Debe seleccionar Grupo, Materia y Parcial", Alert.AlertType.WARNING);
                    return;
                }

                generarTablaCalificaciones(tblCalificaciones, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
                btnGuardar.setDisable(false);
                btnGenerarArchivo.setDisable(false);
            });

            // Evento del bot├│n Guardar (antes "Guardar Calificaciones")
            btnGuardar.setOnAction(event -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAlerta("Validaci├│n", "Debe seleccionar Grupo, Materia y Parcial", Alert.AlertType.WARNING);
                    return;
                }
                guardarCalificaciones(tblCalificaciones, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
                mostrarAlerta("├ëxito", "Calificaciones guardadas correctamente", Alert.AlertType.INFORMATION);
            });

            // Evento del bot├│n Generar archivo
            btnGenerarArchivo.setOnAction(event -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAlerta("Validaci├│n", "Debe seleccionar Grupo, Materia y Parcial", Alert.AlertType.WARNING);
                    return;
                }
                generarArchivoConcentrado(tblCalificaciones, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
            });

            vista.getChildren().addAll(lblTitulo, filtrosPanel, tablaPanel);

        } catch (Exception e) {
            LOG.error("Error al crear vista de concentrado de calificaciones", e);
        }

        return vista;
    }

    // M├⌐todo para crear la vista de Informe de Concentrado (Solo Lectura)
    public VBox crearVistaInformeConcentrado() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        try {
            // Header
            Label lblTitulo = new Label("Informe de Concentrado (Solo Lectura)");
            lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Panel de filtros
            VBox filtrosPanel = new VBox(15);
            filtrosPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            Label lblFiltros = new Label("Filtros (Obligatorios)");
            lblFiltros.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Fila de filtros
            javafx.scene.layout.HBox filtrosBox = new javafx.scene.layout.HBox(20);
            filtrosBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // ComboBox para seleccionar grupo
            VBox grupoContainer = new VBox(5);
            Label lblGrupo = new Label("Grupo: *");
            lblGrupo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            ComboBox<Grupo> cmbGrupo = new ComboBox<>();
            cmbGrupo.setPrefWidth(150);
            cmbGrupo.setPromptText("Seleccionar...");
            try {
                List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
                cmbGrupo.setItems(FXCollections.observableArrayList(grupos));
            } catch (Exception e) {
                LOG.error("Error al cargar grupos", e);
            }
            cmbGrupo.setCellFactory(param -> new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.valueOf(item.getId()));
                }
            });
            cmbGrupo.setButtonCell(new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Seleccionar..." : String.valueOf(item.getId()));
                }
            });
            grupoContainer.getChildren().addAll(lblGrupo, cmbGrupo);

            // ComboBox para seleccionar materia
            VBox materiaContainer = new VBox(5);
            Label lblMateria = new Label("Materia: *");
            lblMateria.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            ComboBox<Materia> cmbMateria = new ComboBox<>();
            cmbMateria.setPrefWidth(250);
            cmbMateria.setPromptText("Seleccionar...");
            cmbMateria.setDisable(true);
            cmbMateria.setCellFactory(param -> new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
            cmbMateria.setButtonCell(new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Seleccionar..." : item.getNombre());
                }
            });
            materiaContainer.getChildren().addAll(lblMateria, cmbMateria);

            // ComboBox para seleccionar parcial
            VBox parcialContainer = new VBox(5);
            Label lblParcial = new Label("Parcial: *");
            lblParcial.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            ComboBox<Integer> cmbParcial = new ComboBox<>();
            cmbParcial.setPrefWidth(120);
            cmbParcial.setPromptText("Seleccionar...");
            cmbParcial.setItems(FXCollections.observableArrayList(1, 2, 3));
            parcialContainer.getChildren().addAll(lblParcial, cmbParcial);

            // Bot├│n Buscar
            VBox buscarContainer = new VBox(5);
            Label lblEspacio = new Label(" "); // Espacio para alinear con los otros labels
            Button btnBuscar = new Button("Buscar");
            btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            buscarContainer.getChildren().addAll(lblEspacio, btnBuscar);

            // Bot├│n Limpiar
            VBox limpiarContainer = new VBox(5);
            Label lblEspacio2 = new Label(" "); // Espacio para alinear con los otros labels
            Button btnLimpiar = new Button("Limpiar");
            btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            limpiarContainer.getChildren().addAll(lblEspacio2, btnLimpiar);

            // Botón Exportar
            VBox exportarContainer = new VBox(5);
            Label lblEspacio3 = new Label(" ");
            Button btnExportar = new Button("Exportar");
            btnExportar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            btnExportar.setDisable(true);
            exportarContainer.getChildren().addAll(lblEspacio3, btnExportar);

            filtrosBox.getChildren().addAll(grupoContainer, materiaContainer, parcialContainer, buscarContainer, limpiarContainer, exportarContainer);

            // Segunda fila de filtros opcionales (Nombre, Apellidos)
            javafx.scene.layout.HBox filtrosOpcionalesBox = new javafx.scene.layout.HBox(20);
            filtrosOpcionalesBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // TextField para filtrar por Nombre
            VBox nombreContainer = new VBox(5);
            Label lblNombre = new Label("Nombre:");
            lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            TextField txtNombre = new TextField();
            txtNombre.setPrefWidth(200);
            txtNombre.setPromptText("Filtrar por nombre...");
            txtNombre.setDisable(true);
            nombreContainer.getChildren().addAll(lblNombre, txtNombre);

            // TextField para filtrar por Apellido Paterno
            VBox apellidoPaternoContainer = new VBox(5);
            Label lblApellidoPaterno = new Label("Apellido Paterno:");
            lblApellidoPaterno.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            TextField txtApellidoPaterno = new TextField();
            txtApellidoPaterno.setPrefWidth(200);
            txtApellidoPaterno.setPromptText("Filtrar por apellido paterno...");
            txtApellidoPaterno.setDisable(true);
            apellidoPaternoContainer.getChildren().addAll(lblApellidoPaterno, txtApellidoPaterno);

            // TextField para filtrar por Apellido Materno
            VBox apellidoMaternoContainer = new VBox(5);
            Label lblApellidoMaterno = new Label("Apellido Materno:");
            lblApellidoMaterno.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            TextField txtApellidoMaterno = new TextField();
            txtApellidoMaterno.setPrefWidth(200);
            txtApellidoMaterno.setPromptText("Filtrar por apellido materno...");
            txtApellidoMaterno.setDisable(true);
            apellidoMaternoContainer.getChildren().addAll(lblApellidoMaterno, txtApellidoMaterno);

            filtrosOpcionalesBox.getChildren().addAll(nombreContainer, apellidoPaternoContainer, apellidoMaternoContainer);

            filtrosPanel.getChildren().addAll(lblFiltros, filtrosBox, filtrosOpcionalesBox);

            // Panel de tabla
            VBox tablaPanel = new VBox(15);
            tablaPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPrefHeight(500);
            scrollPane.setStyle("-fx-background-color: transparent;");

            TableView<java.util.Map<String, Object>> tblInforme = new TableView<>();
            tblInforme.setEditable(false); // SOLO LECTURA
            tblInforme.setPlaceholder(new Label("Seleccione Grupo, Materia y Parcial, luego presione 'Buscar'"));
            tblInforme.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            scrollPane.setContent(tblInforme);
            tablaPanel.getChildren().addAll(scrollPane);

            // L├│gica para cargar materias cuando se selecciona un grupo
            cmbGrupo.setOnAction(event -> {
                Grupo grupoSeleccionado = cmbGrupo.getValue();
                if (grupoSeleccionado != null) {
                    try {
                        List<GrupoMateria> asignaciones = grupoMateriaService.obtenerMateriasPorGrupo(grupoSeleccionado.getId());
                        List<Materia> materias = new java.util.ArrayList<>();
                        for (GrupoMateria gm : asignaciones) {
                            materiaService.obtenerMateriaPorId(gm.getMateriaId()).ifPresent(materias::add);
                        }
                        cmbMateria.setItems(FXCollections.observableArrayList(materias));
                        cmbMateria.setDisable(false);
                    } catch (Exception e) {
                        LOG.error("Error al cargar materias del grupo", e);
                        mostrarAlerta("Error", "No se pudieron cargar las materias del grupo", Alert.AlertType.ERROR);
                    }
                } else {
                    cmbMateria.setItems(FXCollections.observableArrayList());
                    cmbMateria.setDisable(true);
                    txtNombre.setDisable(true);
                    txtApellidoPaterno.setDisable(true);
                    txtApellidoMaterno.setDisable(true);
                }
            });

            // Habilitar filtros opcionales cuando se seleccionan los filtros obligatorios
            cmbParcial.setOnAction(event -> {
                if (cmbGrupo.getValue() != null && cmbMateria.getValue() != null && cmbParcial.getValue() != null) {
                    txtNombre.setDisable(false);
                    txtApellidoPaterno.setDisable(false);
                    txtApellidoMaterno.setDisable(false);
                } else {
                    txtNombre.setDisable(true);
                    txtApellidoPaterno.setDisable(true);
                    txtApellidoMaterno.setDisable(true);
                }
            });

            cmbMateria.setOnAction(event -> {
                if (cmbGrupo.getValue() != null && cmbMateria.getValue() != null && cmbParcial.getValue() != null) {
                    txtNombre.setDisable(false);
                    txtApellidoPaterno.setDisable(false);
                    txtApellidoMaterno.setDisable(false);
                } else {
                    txtNombre.setDisable(true);
                    txtApellidoPaterno.setDisable(true);
                    txtApellidoMaterno.setDisable(true);
                }
            });

            // Evento del bot├│n Buscar
            btnBuscar.setOnAction(event -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAlerta("Validaci├│n", "Debe seleccionar Grupo, Materia y Parcial", Alert.AlertType.WARNING);
                    return;
                }

                // Obtener valores de filtros opcionales
                String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
                String apellidoPaterno = txtApellidoPaterno.getText() != null ? txtApellidoPaterno.getText().trim() : "";
                String apellidoMaterno = txtApellidoMaterno.getText() != null ? txtApellidoMaterno.getText().trim() : "";

                generarTablaInformeConcentrado(tblInforme, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue(),
                        nombre, apellidoPaterno, apellidoMaterno);

                // Habilitar botón exportar después de generar la tabla
                btnExportar.setDisable(false);
            });

            // Evento del botón Exportar
            btnExportar.setOnAction(event -> {
                if (tblInforme.getItems().isEmpty()) {
                    mostrarAlerta("Información", "No hay datos para exportar. Primero genere la tabla.", Alert.AlertType.INFORMATION);
                    return;
                }

                exportarInformeAExcel(tblInforme, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
            });

            // Evento del bot├│n Limpiar
            btnLimpiar.setOnAction(event -> {
                // Limpiar ComboBoxes
                cmbGrupo.setValue(null);
                cmbMateria.setValue(null);
                cmbMateria.setDisable(true);
                cmbParcial.setValue(null);

                // Limpiar TextFields
                txtNombre.clear();
                txtNombre.setDisable(true);
                txtApellidoPaterno.clear();
                txtApellidoPaterno.setDisable(true);
                txtApellidoMaterno.clear();
                txtApellidoMaterno.setDisable(true);

                // Limpiar tabla
                tblInforme.getColumns().clear();
                tblInforme.getItems().clear();

                // Deshabilitar botón exportar
                btnExportar.setDisable(true);
            });

            vista.getChildren().addAll(lblTitulo, filtrosPanel, tablaPanel);

        } catch (Exception e) {
            LOG.error("Error al crear vista de informe de concentrado", e);
        }

        return vista;
    }

    // M├⌐todo para generar la tabla de informe (solo lectura) - similar a generarTablaCalificaciones pero sin edici├│n
    private void generarTablaInformeConcentrado(TableView<java.util.Map<String, Object>> tabla, Grupo grupo, Materia materia, Integer parcial,
                                                String filtroNombre, String filtroApellidoPaterno, String filtroApellidoMaterno) {
        try {
            tabla.getColumns().clear();
            tabla.getItems().clear();

            // Obtener alumnos del grupo
            List<Alumno> alumnos = alumnoService.obtenerTodosLosAlumnos().stream()
                    .filter(a -> grupo.getId().equals(a.getGrupoId()))
                    .filter(a -> {
                        // Aplicar filtros opcionales
                        boolean coincideNombre = filtroNombre.isEmpty() ||
                                (a.getNombre() != null && a.getNombre().toLowerCase().contains(filtroNombre.toLowerCase()));
                        boolean coincideApellidoPaterno = filtroApellidoPaterno.isEmpty() ||
                                (a.getApellidoPaterno() != null && a.getApellidoPaterno().toLowerCase().contains(filtroApellidoPaterno.toLowerCase()));
                        boolean coincideApellidoMaterno = filtroApellidoMaterno.isEmpty() ||
                                (a.getApellidoMaterno() != null && a.getApellidoMaterno().toLowerCase().contains(filtroApellidoMaterno.toLowerCase()));
                        return coincideNombre && coincideApellidoPaterno && coincideApellidoMaterno;
                    })
                    .sorted((a1, a2) -> {
                        String nombre1 = a1.getApellidoPaterno() + " " + a1.getApellidoMaterno() + " " + a1.getNombre();
                        String nombre2 = a2.getApellidoPaterno() + " " + a2.getApellidoMaterno() + " " + a2.getNombre();
                        return nombre1.compareToIgnoreCase(nombre2);
                    })
                    .collect(java.util.stream.Collectors.toList());

            if (alumnos.isEmpty()) {
                mostrarAlerta("Informaci├│n", "No hay alumnos que coincidan con los filtros", Alert.AlertType.INFORMATION);
                return;
            }

            // Obtener criterios de la materia y parcial
            List<Criterio> criterios = criterioService.obtenerCriteriosPorMateria(materia.getId()).stream()
                    .filter(c -> parcial.equals(c.getParcial()))
                    .sorted((c1, c2) -> {
                        if (c1.getOrden() == null && c2.getOrden() == null) return 0;
                        if (c1.getOrden() == null) return 1;
                        if (c2.getOrden() == null) return -1;
                        return Integer.compare(c1.getOrden(), c2.getOrden());
                    })
                    .collect(java.util.stream.Collectors.toList());

            if (criterios.isEmpty()) {
                mostrarAlerta("Informaci├│n", "No hay criterios para esta materia y parcial", Alert.AlertType.INFORMATION);
                return;
            }

            // Obtener examen si existe (necesario para columnas y carga de datos)
            Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
                    grupo.getId(), materia.getId(), parcial);

            // Columna #
            TableColumn<java.util.Map<String, Object>, Integer> colNumero = new TableColumn<>("#");
            colNumero.setPrefWidth(50);
            colNumero.setMinWidth(50);
            colNumero.setMaxWidth(50);
            colNumero.setResizable(false);
            colNumero.setCellValueFactory(cellData -> {
                Integer numero = (Integer) cellData.getValue().get("numero");
                return new javafx.beans.property.SimpleObjectProperty<>(numero);
            });
            tabla.getColumns().add(colNumero);

            // Columna Nombre Completo
            TableColumn<java.util.Map<String, Object>, String> colNombre = new TableColumn<>("Nombre Completo");
            colNombre.setPrefWidth(250);
            colNombre.setMinWidth(250);
            colNombre.setMaxWidth(250);
            colNombre.setResizable(false);
            colNombre.setCellValueFactory(cellData -> {
                String nombre = (String) cellData.getValue().get("nombreCompleto");
                return new javafx.beans.property.SimpleStringProperty(nombre);
            });
            tabla.getColumns().add(colNombre);

            // Lista para recopilar informaci├│n de todos los agregados de todos los criterios
            List<java.util.Map<String, Object>> criteriosInfo = new java.util.ArrayList<>();

            // Crear columnas din├ímicamente por criterio (solo lectura)
            for (Criterio criterio : criterios) {
                // Obtener agregados del criterio
                List<Agregado> agregados = agregadoService.obtenerAgregadosPorCriterio(criterio.getId()).stream()
                        .sorted((a1, a2) -> {
                            if (a1.getOrden() == null && a2.getOrden() == null) return 0;
                            if (a1.getOrden() == null) return 1;
                            if (a2.getOrden() == null) return -1;
                            return Integer.compare(a1.getOrden(), a2.getOrden());
                        })
                        .collect(java.util.stream.Collectors.toList());

                if (!agregados.isEmpty()) {
                    // Guardar informaci├│n del criterio para la columna Portafolio
                    java.util.Map<String, Object> criterioInfo = new java.util.HashMap<>();
                    criterioInfo.put("criterioId", criterio.getId());
                    criterioInfo.put("agregadoIds", agregados.stream().map(Agregado::getId).collect(java.util.stream.Collectors.toList()));
                    criterioInfo.put("esCheck", "Check".equalsIgnoreCase(criterio.getTipoEvaluacion()));
                    criterioInfo.put("puntuacionMaxima", criterio.getPuntuacionMaxima());
                    criteriosInfo.add(criterioInfo);

                    // Crear columna padre para el criterio
                    TableColumn<java.util.Map<String, Object>, String> colCriterio = new TableColumn<>(
                            criterio.getNombre() + " (" + criterio.getPuntuacionMaxima() + " pts)"
                    );
                    colCriterio.setResizable(false);

                    final Double puntuacionMaximaCriterio = criterio.getPuntuacionMaxima();
                    boolean esCheck = "Check".equalsIgnoreCase(criterio.getTipoEvaluacion());

                    // Crear columnas hijas para cada agregado (solo lectura)
                    for (Agregado agregado : agregados) {
                        TableColumn<java.util.Map<String, Object>, String> colAgregado = new TableColumn<>(agregado.getNombre());
                        colAgregado.setPrefWidth(100);
                        colAgregado.setMinWidth(100);
                        colAgregado.setMaxWidth(100);
                        colAgregado.setResizable(false);

                        colAgregado.setCellValueFactory(cellData -> {
                            Object valor = cellData.getValue().get("agregado_" + agregado.getId());
                            String texto = "";

                            if (esCheck) {
                                // Para tipo Check, mostrar ✓ o X en rojo si está vacío
                                if (valor instanceof Boolean && (Boolean) valor) {
                                    texto = "✓";
                                } else if (valor instanceof String) {
                                    String strValor = (String) valor;
                                    if ("true".equalsIgnoreCase(strValor) || "1".equals(strValor)) {
                                        texto = "✓";
                                    } else {
                                        texto = "X"; // Vacío = X
                                    }
                                } else {
                                    texto = "X"; // Vacío = X
                                }
                            } else {
                                // Para tipo Puntuación, mostrar el valor numérico o 0 en rojo si está vacío
                                if (valor != null && !valor.toString().isEmpty()) {
                                    texto = valor.toString();
                                } else {
                                    texto = "0"; // Vac├¡o = 0
                                }
                            }

                            return new javafx.beans.property.SimpleStringProperty(texto);
                        });

                        final boolean esCheckFinal = esCheck;
                        colAgregado.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                    setStyle("");
                                } else {
                                    setText(item);
                                    // Si es Check y est├í vac├¡o (X), o es Puntuaci├│n y es 0, poner en rojo
                                    if ((esCheckFinal && "X".equals(item)) || (!esCheckFinal && "0".equals(item))) {
                                        setStyle("-fx-alignment: CENTER; -fx-text-fill: red; -fx-font-weight: bold;");
                                    } else {
                                        setStyle("-fx-alignment: CENTER;");
                                    }
                                }
                            }
                        });

                        colCriterio.getColumns().add(colAgregado);
                    }

                    // Agregar columna Acumulado
                    TableColumn<java.util.Map<String, Object>, String> colAcumulado = new TableColumn<>("Acumulado");
                    colAcumulado.setPrefWidth(120);
                    colAcumulado.setMinWidth(120);
                    colAcumulado.setMaxWidth(120);
                    colAcumulado.setResizable(false);

                    final List<Long> agregadoIds = agregados.stream()
                            .map(Agregado::getId)
                            .collect(java.util.stream.Collectors.toList());

                    colAcumulado.setCellValueFactory(cellData -> {
                        java.util.Map<String, Object> fila = cellData.getValue();
                        double puntosObtenidos = 0.0;

                        for (Long agregadoId : agregadoIds) {
                            Object valor = fila.get("agregado_" + agregadoId);

                            if (esCheck) {
                                if (valor instanceof Boolean && (Boolean) valor) {
                                    puntosObtenidos += puntuacionMaximaCriterio / agregadoIds.size();
                                } else if (valor instanceof String) {
                                    String strValor = (String) valor;
                                    if ("true".equalsIgnoreCase(strValor) || "1".equals(strValor)) {
                                        puntosObtenidos += puntuacionMaximaCriterio / agregadoIds.size();
                                    }
                                }
                            } else {
                                if (valor instanceof Number) {
                                    puntosObtenidos += ((Number) valor).doubleValue();
                                } else if (valor instanceof String && !((String) valor).isEmpty()) {
                                    try {
                                        puntosObtenidos += Double.parseDouble((String) valor);
                                    } catch (NumberFormatException e) {
                                        // Ignorar
                                    }
                                }
                            }
                        }

                        return new javafx.beans.property.SimpleStringProperty(
                            String.format("%.2f / %.2f", puntosObtenidos, puntuacionMaximaCriterio)
                        );
                    });

                    colAcumulado.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setStyle("");
                            } else {
                                setText(item);
                                setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #f0f0f0;");
                            }
                        }
                    });

                    colCriterio.getColumns().add(colAcumulado);
                    tabla.getColumns().add(colCriterio);
                }
            }


            // Agregar columna Portafolio (solo si hay criterios)
            if (!criteriosInfo.isEmpty()) {
                TableColumn<java.util.Map<String, Object>, String> colPortafolio = new TableColumn<>("Portafolio");
                colPortafolio.setPrefWidth(120);
                colPortafolio.setMinWidth(120);
                colPortafolio.setMaxWidth(120);
                colPortafolio.setResizable(false);

                colPortafolio.setCellValueFactory(cellData -> {
                    java.util.Map<String, Object> fila = cellData.getValue();
                    double puntosObtenidosTotal = 0.0;
                    double puntuacionMaximaTotal = 0.0;

                    // Sumar los puntos obtenidos de todos los criterios
                    for (java.util.Map<String, Object> criterioInfo : criteriosInfo) {
                        @SuppressWarnings("unchecked")
                        List<Long> agregadoIds = (List<Long>) criterioInfo.get("agregadoIds");
                        boolean esCheck = (boolean) criterioInfo.get("esCheck");
                        double puntuacionMaxima = (double) criterioInfo.get("puntuacionMaxima");

                        double puntosObtenidosCriterio = 0.0;

                        for (Long agregadoId : agregadoIds) {
                            Object valor = fila.get("agregado_" + agregadoId);

                            if (esCheck) {
                                if (valor instanceof Boolean && (Boolean) valor) {
                                    puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
                                } else if (valor instanceof String) {
                                    String strValor = (String) valor;
                                    if ("true".equalsIgnoreCase(strValor) || "1".equals(strValor)) {
                                        puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
                                    }
                                }
                            } else {
                                if (valor instanceof Number) {
                                    puntosObtenidosCriterio += ((Number) valor).doubleValue();
                                } else if (valor instanceof String && !((String) valor).isEmpty()) {
                                    try {
                                        puntosObtenidosCriterio += Double.parseDouble((String) valor);
                                    } catch (NumberFormatException e) {
                                        // Ignorar
                                    }
                                }
                            }
                        }

                        puntosObtenidosTotal += puntosObtenidosCriterio;
                        puntuacionMaximaTotal += puntuacionMaxima;
                    }

                    return new javafx.beans.property.SimpleStringProperty(
                        String.format("%.2f / %.2f", puntosObtenidosTotal, puntuacionMaximaTotal)
                    );
                });

                colPortafolio.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #e3f2fd;");
                        }
                    }
                });

                tabla.getColumns().add(colPortafolio);
            }

            // Agregar columnas de Examen (solo lectura)

            if (examenOpt.isPresent()) {
                // Columna Puntos Examen
                TableColumn<java.util.Map<String, Object>, String> colAciertos = new TableColumn<>("Puntos Examen");
                colAciertos.setPrefWidth(100);
                colAciertos.setMinWidth(100);
                colAciertos.setMaxWidth(100);
                colAciertos.setResizable(false);

                colAciertos.setCellValueFactory(cellData -> {
                    Object valor = cellData.getValue().get("aciertosExamen");
                    return new javafx.beans.property.SimpleStringProperty(
                        valor != null ? String.valueOf(valor) : "-"
                    );
                });

                colAciertos.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #fff3e0;");
                        }
                    }
                });

                tabla.getColumns().add(colAciertos);

                // Columna Porcentaje Examen
                TableColumn<java.util.Map<String, Object>, String> colPorcentajeExamen = new TableColumn<>("% Examen");
                colPorcentajeExamen.setPrefWidth(100);
                colPorcentajeExamen.setMinWidth(100);
                colPorcentajeExamen.setMaxWidth(100);
                colPorcentajeExamen.setResizable(false);

                colPorcentajeExamen.setCellValueFactory(cellData -> {
                    Object valor = cellData.getValue().get("porcentajeExamen");
                    return new javafx.beans.property.SimpleStringProperty(
                        valor != null ? String.format("%.1f%%", (Double) valor) : "-"
                    );
                });

                colPorcentajeExamen.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #fff3e0;");
                        }
                    }
                });

                tabla.getColumns().add(colPorcentajeExamen);

                // Columna Calificaci├│n Examen
                TableColumn<java.util.Map<String, Object>, String> colCalificacionExamen = new TableColumn<>("Calif. Examen");
                colCalificacionExamen.setPrefWidth(120);
                colCalificacionExamen.setMinWidth(120);
                colCalificacionExamen.setMaxWidth(120);
                colCalificacionExamen.setResizable(false);

                colCalificacionExamen.setCellValueFactory(cellData -> {
                    Object valor = cellData.getValue().get("calificacionExamen");
                    return new javafx.beans.property.SimpleStringProperty(
                        valor != null ? String.format("%.2f", (Double) valor) : "-"
                    );
                });

                colCalificacionExamen.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #fff3e0;");
                        }
                    }
                });

                tabla.getColumns().add(colCalificacionExamen);
            }

            // Columna Puntos Parcial
            TableColumn<java.util.Map<String, Object>, String> colPuntosParcial = new TableColumn<>("Puntos Parcial");
            colPuntosParcial.setPrefWidth(120);
            colPuntosParcial.setMinWidth(120);
            colPuntosParcial.setMaxWidth(120);
            colPuntosParcial.setResizable(false);

            colPuntosParcial.setCellValueFactory(cellData -> {
                Object valor = cellData.getValue().get("puntosParcial");
                return new javafx.beans.property.SimpleStringProperty(
                    valor != null ? String.format("%.2f", (Double) valor) : "-"
                );
            });

            colPuntosParcial.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #c8e6c9;");
                    }
                }
            });

            tabla.getColumns().add(colPuntosParcial);

            // Columna Calificación Parcial
            TableColumn<java.util.Map<String, Object>, String> colCalificacionParcial = new TableColumn<>("Calificación Parcial");
            colCalificacionParcial.setPrefWidth(150);
            colCalificacionParcial.setMinWidth(150);
            colCalificacionParcial.setMaxWidth(150);
            colCalificacionParcial.setResizable(false);

            colCalificacionParcial.setCellValueFactory(cellData -> {
                Object valor = cellData.getValue().get("calificacionParcial");
                return new javafx.beans.property.SimpleStringProperty(
                    valor != null ? String.format("%.2f", (Double) valor) : "-"
                );
            });

            colCalificacionParcial.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #e8f5e9; -fx-font-size: 16px;");
                    }
                }
            });

            tabla.getColumns().add(colCalificacionParcial);

            // Llenar datos
            ObservableList<java.util.Map<String, Object>> datos = FXCollections.observableArrayList();
            int numero = 1;


            for (Alumno alumno : alumnos) {
                java.util.Map<String, Object> fila = new java.util.HashMap<>();
                fila.put("numero", numero++);
                fila.put("alumnoId", alumno.getId());
                fila.put("nombreCompleto", alumno.getApellidoPaterno() + " " +
                        alumno.getApellidoMaterno() + " " + alumno.getNombre());

                // Cargar calificaciones existentes desde CalificacionConcentrado
                for (Criterio criterio : criterios) {
                    List<Agregado> agregados = agregadoService.obtenerAgregadosPorCriterio(criterio.getId());
                    boolean esCheck = "Check".equalsIgnoreCase(criterio.getTipoEvaluacion());

                    for (Agregado agregado : agregados) {
                        Optional<CalificacionConcentrado> calificacion = calificacionConcentradoService
                                .obtenerCalificacionPorAlumnoYAgregadoYFiltros(
                                        alumno.getId(),
                                        agregado.getId(),
                                        grupo.getId(),
                                        materia.getId(),
                                        parcial
                                );

                        if (calificacion.isPresent()) {
                            Double puntuacion = calificacion.get().getPuntuacion();
                            if (esCheck) {
                                // Para tipo Check, convertir a Boolean
                                fila.put("agregado_" + agregado.getId(), puntuacion != null && puntuacion > 0);
                            } else {
                                // Para tipo Puntuacion, mantener como String
                                fila.put("agregado_" + agregado.getId(), puntuacion != null ? String.valueOf(puntuacion) : "");
                            }
                        } else {
                            // Si no hay calificaci├│n, poner valor por defecto seg├║n el tipo
                            if (esCheck) {
                                fila.put("agregado_" + agregado.getId(), false);
                            } else {
                                fila.put("agregado_" + agregado.getId(), "");
                            }
                        }
                    }
                }

                // Cargar datos de examen si existe
                if (examenOpt.isPresent()) {
                    Examen examen = examenOpt.get();
                    Optional<AlumnoExamen> alumnoExamenOpt = alumnoExamenService
                            .obtenerAlumnoExamenPorAlumnoYExamen(alumno.getId(), examen.getId());

                    if (alumnoExamenOpt.isPresent()) {
                        AlumnoExamen alumnoExamen = alumnoExamenOpt.get();
                        fila.put("aciertosExamen", alumnoExamen.getPuntosExamen());
                        fila.put("porcentajeExamen", alumnoExamen.getPorcentaje());
                        fila.put("calificacionExamen", alumnoExamen.getCalificacion());
                    } else {
                        // Sin datos de examen
                        fila.put("aciertosExamen", null);
                        fila.put("porcentajeExamen", null);
                        fila.put("calificacionExamen", null);
                    }
                }

                // Calcular y agregar puntosParcial y calificacionParcial al Map
                double totalPortafolio = 0.0;
                for (java.util.Map<String, Object> criterioInfo : criteriosInfo) {
                    @SuppressWarnings("unchecked")
                    List<Long> agregadoIds = (List<Long>) criterioInfo.get("agregadoIds");
                    boolean esCheck = (Boolean) criterioInfo.get("esCheck");
                    Double puntuacionMaxima = (Double) criterioInfo.get("puntuacionMaxima");

                    double puntosObtenidosCriterio = 0.0;

                    for (Long agregadoId : agregadoIds) {
                        Object valor = fila.get("agregado_" + agregadoId);

                        if (esCheck) {
                            if (valor instanceof Boolean && (Boolean) valor) {
                                puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
                            } else if (valor instanceof String) {
                                String strValor = (String) valor;
                                if ("true".equalsIgnoreCase(strValor) || "1".equals(strValor)) {
                                    puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
                                }
                            }
                        } else {
                            if (valor instanceof Number) {
                                puntosObtenidosCriterio += ((Number) valor).doubleValue();
                            } else if (valor instanceof String && !((String) valor).isEmpty()) {
                                try {
                                    puntosObtenidosCriterio += Double.parseDouble((String) valor);
                                } catch (NumberFormatException e) {
                                    // Ignorar valores no num├⌐ricos
                                }
                            }
                        }
                    }

                    totalPortafolio += puntosObtenidosCriterio;
                }

                double puntosExamen = 0.0;
                Object aciertosExamen = fila.get("aciertosExamen");
                if (aciertosExamen != null) {
                    if (aciertosExamen instanceof Number) {
                        puntosExamen = ((Number) aciertosExamen).doubleValue();
                    } else if (aciertosExamen instanceof String) {
                        try {
                            puntosExamen = Double.parseDouble((String) aciertosExamen);
                        } catch (NumberFormatException e) {
                            // Ignorar valores no num├⌐ricos
                        }
                    }
                }

                double puntosParcial = totalPortafolio + puntosExamen;
                double calificacionParcial = (puntosParcial * 10.0) / 100.0;

                fila.put("puntosParcial", puntosParcial);
                fila.put("calificacionParcial", calificacionParcial);

                datos.add(fila);
            }

            tabla.setItems(datos);

        } catch (Exception e) {
            LOG.error("Error al generar tabla de informe de concentrado", e);
            mostrarAlerta("Error", "No se pudo generar la tabla: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // M├⌐todo para generar la tabla de calificaciones
    private void generarTablaCalificaciones(TableView<java.util.Map<String, Object>> tabla, Grupo grupo, Materia materia, Integer parcial) {
        try {
            tabla.getColumns().clear();
            tabla.getItems().clear();

            // Obtener alumnos del grupo
            List<Alumno> alumnos = alumnoService.obtenerTodosLosAlumnos().stream()
                    .filter(a -> grupo.getId().equals(a.getGrupoId()))
                    .sorted((a1, a2) -> {
                        String nombre1 = a1.getApellidoPaterno() + " " + a1.getApellidoMaterno() + " " + a1.getNombre();
                        String nombre2 = a2.getApellidoPaterno() + " " + a2.getApellidoMaterno() + " " + a2.getNombre();
                        return nombre1.compareToIgnoreCase(nombre2);
                    })
                    .collect(java.util.stream.Collectors.toList());

            if (alumnos.isEmpty()) {
                mostrarAlerta("Informaci├│n", "No hay alumnos en este grupo", Alert.AlertType.INFORMATION);
                return;
            }

            // Obtener criterios de la materia y parcial
            List<Criterio> criterios = criterioService.obtenerCriteriosPorMateria(materia.getId()).stream()
                    .filter(c -> parcial.equals(c.getParcial()))
                    .sorted((c1, c2) -> {
                        if (c1.getOrden() == null && c2.getOrden() == null) return 0;
                        if (c1.getOrden() == null) return 1;
                        if (c2.getOrden() == null) return -1;
                        return Integer.compare(c1.getOrden(), c2.getOrden());
                    })
                    .collect(java.util.stream.Collectors.toList());

            if (criterios.isEmpty()) {
                mostrarAlerta("Informaci├│n", "No hay criterios para esta materia y parcial", Alert.AlertType.INFORMATION);
                return;
            }

            // Columna #
            TableColumn<java.util.Map<String, Object>, Integer> colNumero = new TableColumn<>("#");
            colNumero.setPrefWidth(50);
            colNumero.setMinWidth(50);
            colNumero.setMaxWidth(50);
            colNumero.setResizable(false);
            colNumero.setCellValueFactory(cellData -> {
                Integer numero = (Integer) cellData.getValue().get("numero");
                return new javafx.beans.property.SimpleObjectProperty<>(numero);
            });
            tabla.getColumns().add(colNumero);

            // Columna Nombre Completo
            TableColumn<java.util.Map<String, Object>, String> colNombre = new TableColumn<>("Nombre Completo");
            colNombre.setPrefWidth(250);
            colNombre.setMinWidth(250);
            colNombre.setMaxWidth(250);
            colNombre.setResizable(false);
            colNombre.setCellValueFactory(cellData -> {
                String nombre = (String) cellData.getValue().get("nombreCompleto");
                return new javafx.beans.property.SimpleStringProperty(nombre);
            });
            tabla.getColumns().add(colNombre);

            // Lista para recopilar informaci├│n de todos los agregados de todos los criterios
            List<java.util.Map<String, Object>> criteriosInfo = new java.util.ArrayList<>();

            // Crear columnas din├ímicamente por criterio
            for (Criterio criterio : criterios) {
                // Obtener agregados del criterio
                List<Agregado> agregados = agregadoService.obtenerAgregadosPorCriterio(criterio.getId()).stream()
                        .sorted((a1, a2) -> {
                            if (a1.getOrden() == null && a2.getOrden() == null) return 0;
                            if (a1.getOrden() == null) return 1;
                            if (a2.getOrden() == null) return -1;
                            return Integer.compare(a1.getOrden(), a2.getOrden());
                        })
                        .collect(java.util.stream.Collectors.toList());

                if (!agregados.isEmpty()) {
                    // Crear columna padre para el criterio
                    TableColumn<java.util.Map<String, Object>, String> colCriterio = new TableColumn<>(
                            criterio.getNombre() + " (" + criterio.getPuntuacionMaxima() + " pts)"
                    );
                    colCriterio.setResizable(false);

                    // Obtener IDs de todos los agregados del criterio para validaci├│n
                    final List<Long> agregadoIdsDelCriterio = agregados.stream()
                            .map(Agregado::getId)
                            .collect(java.util.stream.Collectors.toList());
                    final Double puntuacionMaximaCriterio = criterio.getPuntuacionMaxima();

                    // Crear columnas hijas para cada agregado
                    for (Agregado agregado : agregados) {
                        boolean esCheck = "Check".equalsIgnoreCase(criterio.getTipoEvaluacion());

                        if (esCheck) {
                            // Columna con CheckBox para tipo Check
                            TableColumn<java.util.Map<String, Object>, Boolean> colAgregadoCheck = new TableColumn<>(agregado.getNombre());
                            colAgregadoCheck.setPrefWidth(100);
                            colAgregadoCheck.setMinWidth(100);
                            colAgregadoCheck.setMaxWidth(100);
                            colAgregadoCheck.setResizable(false);
                            colAgregadoCheck.setEditable(true);

                            colAgregadoCheck.setCellValueFactory(cellData -> {
                                Object valor = cellData.getValue().get("agregado_" + agregado.getId());
                                boolean checked = false;
                                if (valor != null) {
                                    if (valor instanceof Boolean) {
                                        checked = (Boolean) valor;
                                    } else if (valor instanceof String) {
                                        String strValor = (String) valor;
                                        checked = "true".equalsIgnoreCase(strValor) || "1".equals(strValor);
                                    } else if (valor instanceof Number) {
                                        checked = ((Number) valor).doubleValue() > 0;
                                    }
                                }
                                return new javafx.beans.property.SimpleBooleanProperty(checked);
                            });

                            colAgregadoCheck.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, Boolean>() {
                                private final CheckBox checkBox = new CheckBox();
                                private boolean isUpdating = false;

                                {
                                    checkBox.setStyle("-fx-alignment: CENTER;");
                                    checkBox.setOnAction(event -> {
                                        if (!isUpdating && getTableRow() != null && getTableRow().getItem() != null) {
                                            java.util.Map<String, Object> fila = getTableRow().getItem();
                                            fila.put("agregado_" + agregado.getId(), checkBox.isSelected());
                                            // Refrescar la tabla para actualizar el acumulado
                                            tabla.refresh();
                                        }
                                    });
                                }

                                @Override
                                protected void updateItem(Boolean item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null);
                                    } else {
                                        isUpdating = true;
                                        checkBox.setSelected(item != null && item);
                                        isUpdating = false;
                                        setGraphic(checkBox);
                                        setStyle("-fx-alignment: CENTER;");
                                    }
                                }
                            });

                            colCriterio.getColumns().add(colAgregadoCheck);

                        } else {
                            // Columna con TextField para tipo Puntuacion (m├íximo 2 d├¡gitos)
                            TableColumn<java.util.Map<String, Object>, String> colAgregadoPuntos = new TableColumn<>(agregado.getNombre());
                            colAgregadoPuntos.setPrefWidth(100);
                            colAgregadoPuntos.setMinWidth(100);
                            colAgregadoPuntos.setMaxWidth(100);
                            colAgregadoPuntos.setResizable(false);
                            colAgregadoPuntos.setEditable(true);

                            colAgregadoPuntos.setCellValueFactory(cellData -> {
                                Object valor = cellData.getValue().get("agregado_" + agregado.getId());
                                return new javafx.beans.property.SimpleStringProperty(valor != null ? valor.toString() : "");
                            });

                            colAgregadoPuntos.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                                private final TextField textField = new TextField();

                                {
                                    textField.setStyle("-fx-alignment: CENTER; -fx-pref-width: 90px;");
                                    textField.setMaxWidth(90);

                                    // Validar que solo sean n├║meros de m├íximo 2 d├¡gitos
                                    textField.textProperty().addListener((obs, oldVal, newVal) -> {
                                        if (newVal != null && !newVal.isEmpty()) {
                                            // Solo permitir n├║meros y punto decimal
                                            if (!newVal.matches("\\d{0,2}(\\.\\d{0,2})?")) {
                                                textField.setText(oldVal);
                                                return;
                                            }
                                            // Validar que no exceda 99
                                            try {
                                                double valor = Double.parseDouble(newVal);
                                                if (valor > 99) {
                                                    textField.setText(oldVal);
                                                    return;
                                                }
                                            } catch (NumberFormatException e) {
                                                // Ignorar si no es un n├║mero v├ílido a├║n
                                            }
                                        }
                                    });

                                    textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                                        if (!newVal && getTableRow() != null && getTableRow().getItem() != null) {
                                            // Al perder el foco, validar la sumatoria total
                                            String valorTexto = textField.getText();
                                            java.util.Map<String, Object> fila = getTableRow().getItem();

                                            // Guardar temporalmente el nuevo valor
                                            String valorAnterior = (String) fila.get("agregado_" + agregado.getId());
                                            fila.put("agregado_" + agregado.getId(), valorTexto);

                                            // Calcular la sumatoria de todos los agregados del criterio
                                            double sumaTotal = 0.0;
                                            for (Long agregadoId : agregadoIdsDelCriterio) {
                                                Object valor = fila.get("agregado_" + agregadoId);
                                                if (valor instanceof String && !((String) valor).isEmpty()) {
                                                    try {
                                                        sumaTotal += Double.parseDouble((String) valor);
                                                    } catch (NumberFormatException e) {
                                                        // Ignorar valores no num├⌐ricos
                                                    }
                                                }
                                            }

                                            // Validar que la suma no exceda el m├íximo
                                            if (sumaTotal > puntuacionMaximaCriterio) {
                                                mostrarAlerta("Advertencia",
                                                    "No puede exceder el m├íximo de puntos",
                                                    Alert.AlertType.WARNING);
                                                // Restaurar el valor anterior
                                                fila.put("agregado_" + agregado.getId(), valorAnterior != null ? valorAnterior : "");
                                                textField.setText(valorAnterior != null ? valorAnterior : "");
                                            }

                                            // Refrescar la tabla para actualizar el acumulado
                                            tabla.refresh();
                                        }
                                    });

                                    textField.setOnAction(event -> {
                                        if (getTableRow() != null && getTableRow().getItem() != null) {
                                            // Al presionar Enter, validar la sumatoria total
                                            String valorTexto = textField.getText();
                                            java.util.Map<String, Object> fila = getTableRow().getItem();

                                            // Guardar temporalmente el nuevo valor
                                            String valorAnterior = (String) fila.get("agregado_" + agregado.getId());
                                            fila.put("agregado_" + agregado.getId(), valorTexto);

                                            // Calcular la sumatoria de todos los agregados del criterio
                                            double sumaTotal = 0.0;
                                            for (Long agregadoId : agregadoIdsDelCriterio) {
                                                Object valor = fila.get("agregado_" + agregadoId);
                                                if (valor instanceof String && !((String) valor).isEmpty()) {
                                                    try {
                                                        sumaTotal += Double.parseDouble((String) valor);
                                                    } catch (NumberFormatException e) {
                                                        // Ignorar valores no num├⌐ricos
                                                    }
                                                }
                                            }

                                            // Validar que la suma no exceda el m├íximo
                                            if (sumaTotal > puntuacionMaximaCriterio) {
                                                mostrarAlerta("Advertencia",
                                                    "No puede exceder el m├íximo de puntos",
                                                    Alert.AlertType.WARNING);
                                                // Restaurar el valor anterior
                                                fila.put("agregado_" + agregado.getId(), valorAnterior != null ? valorAnterior : "");
                                                textField.setText(valorAnterior != null ? valorAnterior : "");
                                            }

                                            // Refrescar la tabla para actualizar el acumulado
                                            tabla.refresh();
                                        }
                                    });
                                }

                                @Override
                                protected void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null);
                                    } else {
                                        textField.setText(item != null ? item : "");
                                        setGraphic(textField);
                                        setStyle("-fx-alignment: CENTER;");
                                    }
                                }
                            });

                            colCriterio.getColumns().add(colAgregadoPuntos);
                        }
                    }

                    // Agregar columna Acumulado al final de cada grupo de agregados
                    TableColumn<java.util.Map<String, Object>, String> colAcumulado = new TableColumn<>("Acumulado");
                    colAcumulado.setPrefWidth(120);
                    colAcumulado.setMinWidth(120);
                    colAcumulado.setMaxWidth(120);
                    colAcumulado.setResizable(false);
                    colAcumulado.setStyle("-fx-alignment: CENTER;");

                    final Long criterioIdFinal = criterio.getId();
                    final Double puntuacionMaximaFinal = criterio.getPuntuacionMaxima();
                    final boolean esCheckFinal = "Check".equalsIgnoreCase(criterio.getTipoEvaluacion());
                    final List<Long> agregadoIds = agregados.stream()
                            .map(Agregado::getId)
                            .collect(java.util.stream.Collectors.toList());

                    colAcumulado.setCellValueFactory(cellData -> {
                        java.util.Map<String, Object> fila = cellData.getValue();
                        double puntosObtenidos = 0.0;

                        // Calcular puntos obtenidos sumando los valores de los agregados
                        for (Long agregadoId : agregadoIds) {
                            Object valor = fila.get("agregado_" + agregadoId);

                            if (esCheckFinal) {
                                // Para tipo Check, cada checkbox marcado suma una parte proporcional
                                if (valor instanceof Boolean && (Boolean) valor) {
                                    puntosObtenidos += puntuacionMaximaFinal / agregadoIds.size();
                                } else if (valor instanceof String) {
                                    String strValor = (String) valor;
                                    if ("true".equalsIgnoreCase(strValor) || "1".equals(strValor)) {
                                        puntosObtenidos += puntuacionMaximaFinal / agregadoIds.size();
                                    }
                                }
                            } else {
                                // Para tipo Puntuaci├│n, sumar los valores num├⌐ricos
                                if (valor instanceof Number) {
                                    puntosObtenidos += ((Number) valor).doubleValue();
                                } else if (valor instanceof String && !((String) valor).isEmpty()) {
                                    try {
                                        puntosObtenidos += Double.parseDouble((String) valor);
                                    } catch (NumberFormatException e) {
                                        // Ignorar valores no num├⌐ricos
                                    }
                                }
                            }
                        }

                        return new javafx.beans.property.SimpleStringProperty(
                            String.format("%.2f / %.2f", puntosObtenidos, puntuacionMaximaFinal)
                        );
                    });

                    colAcumulado.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setStyle("");
                            } else {
                                setText(item);
                                setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #f0f0f0;");
                            }
                        }
                    });

                    colCriterio.getColumns().add(colAcumulado);

                    // Guardar informaci├│n del criterio para la columna Portafolio
                    java.util.Map<String, Object> criterioInfo = new java.util.HashMap<>();
                    criterioInfo.put("criterioId", criterio.getId());
                    criterioInfo.put("agregadoIds", agregadoIdsDelCriterio);
                    criterioInfo.put("esCheck", "Check".equalsIgnoreCase(criterio.getTipoEvaluacion()));
                    criterioInfo.put("puntuacionMaxima", criterio.getPuntuacionMaxima());
                    criteriosInfo.add(criterioInfo);
                }
            }

            // Agregar columna Portafolio al final de todos los criterios
            if (!criteriosInfo.isEmpty()) {
                TableColumn<java.util.Map<String, Object>, String> colPortafolio = new TableColumn<>("Portafolio");
                colPortafolio.setPrefWidth(120);
                colPortafolio.setMinWidth(120);
                colPortafolio.setMaxWidth(120);
                colPortafolio.setResizable(false);
                colPortafolio.setStyle("-fx-alignment: CENTER;");

                colPortafolio.setCellValueFactory(cellData -> {
                    java.util.Map<String, Object> fila = cellData.getValue();
                    double totalPortafolio = 0.0;

                    // Sumar los puntos obtenidos de todos los criterios
                    for (java.util.Map<String, Object> criterioInfo : criteriosInfo) {
                        @SuppressWarnings("unchecked")
                        List<Long> agregadoIds = (List<Long>) criterioInfo.get("agregadoIds");
                        boolean esCheck = (Boolean) criterioInfo.get("esCheck");
                        Double puntuacionMaxima = (Double) criterioInfo.get("puntuacionMaxima");

                        double puntosObtenidosCriterio = 0.0;

                        // Calcular puntos obtenidos de este criterio
                        for (Long agregadoId : agregadoIds) {
                            Object valor = fila.get("agregado_" + agregadoId);

                            if (esCheck) {
                                // Para tipo Check, cada checkbox marcado suma una parte proporcional
                                if (valor instanceof Boolean && (Boolean) valor) {
                                    puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
                                } else if (valor instanceof String) {
                                    String strValor = (String) valor;
                                    if ("true".equalsIgnoreCase(strValor) || "1".equals(strValor)) {
                                        puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
                                    }
                                }
                            } else {
                                // Para tipo Puntuaci├│n, sumar los valores num├⌐ricos
                                if (valor instanceof Number) {
                                    puntosObtenidosCriterio += ((Number) valor).doubleValue();
                                } else if (valor instanceof String && !((String) valor).isEmpty()) {
                                    try {
                                        puntosObtenidosCriterio += Double.parseDouble((String) valor);
                                    } catch (NumberFormatException e) {
                                        // Ignorar valores no num├⌐ricos
                                    }
                                }
                            }
                        }

                        totalPortafolio += puntosObtenidosCriterio;
                    }

                    return new javafx.beans.property.SimpleStringProperty(
                        String.format("%.2f", totalPortafolio)
                    );
                });

                colPortafolio.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #e3f2fd; -fx-font-size: 14px;");
                        }
                    }
                });

                tabla.getColumns().add(colPortafolio);
            }

            // Agregar columnas de Examen (Puntos Examen, Porcentaje, Calificaci├│n)
            // Buscar el examen correspondiente al grupo, materia y parcial
            Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
                    grupo.getId(), materia.getId(), parcial);

            if (examenOpt.isPresent()) {
                Examen examen = examenOpt.get();

                // Columna Puntos Examen
                TableColumn<java.util.Map<String, Object>, String> colAciertos = new TableColumn<>("Puntos Examen");
                colAciertos.setPrefWidth(100);
                colAciertos.setMinWidth(100);
                colAciertos.setMaxWidth(100);
                colAciertos.setResizable(false);

                colAciertos.setCellValueFactory(cellData -> {
                    Object valor = cellData.getValue().get("aciertosExamen");
                    return new javafx.beans.property.SimpleStringProperty(
                        valor != null ? String.valueOf(valor) : "-"
                    );
                });

                colAciertos.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #fff3e0;");
                        }
                    }
                });

                tabla.getColumns().add(colAciertos);

                // Columna Porcentaje Examen
                TableColumn<java.util.Map<String, Object>, String> colPorcentajeExamen = new TableColumn<>("% Examen");
                colPorcentajeExamen.setPrefWidth(100);
                colPorcentajeExamen.setMinWidth(100);
                colPorcentajeExamen.setMaxWidth(100);
                colPorcentajeExamen.setResizable(false);

                colPorcentajeExamen.setCellValueFactory(cellData -> {
                    Object valor = cellData.getValue().get("porcentajeExamen");
                    return new javafx.beans.property.SimpleStringProperty(
                        valor != null ? String.format("%.1f%%", (Double) valor) : "-"
                    );
                });

                colPorcentajeExamen.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #fff3e0;");
                        }
                    }
                });

                tabla.getColumns().add(colPorcentajeExamen);

                // Columna Calificaci├│n Examen
                TableColumn<java.util.Map<String, Object>, String> colCalificacionExamen = new TableColumn<>("Calif. Examen");
                colCalificacionExamen.setPrefWidth(120);
                colCalificacionExamen.setMinWidth(120);
                colCalificacionExamen.setMaxWidth(120);
                colCalificacionExamen.setResizable(false);

                colCalificacionExamen.setCellValueFactory(cellData -> {
                    Object valor = cellData.getValue().get("calificacionExamen");
                    return new javafx.beans.property.SimpleStringProperty(
                        valor != null ? String.format("%.2f", (Double) valor) : "-"
                    );
                });

                colCalificacionExamen.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #fff3e0;");
                        }
                    }
                });

                tabla.getColumns().add(colCalificacionExamen);
            }

            // Columna Puntos Parcial
            TableColumn<java.util.Map<String, Object>, String> colPuntosParcial = new TableColumn<>("Puntos Parcial");
            colPuntosParcial.setPrefWidth(120);
            colPuntosParcial.setMinWidth(120);
            colPuntosParcial.setMaxWidth(120);
            colPuntosParcial.setResizable(false);

            colPuntosParcial.setCellValueFactory(cellData -> {
                Object valor = cellData.getValue().get("puntosParcial");
                return new javafx.beans.property.SimpleStringProperty(
                    valor != null ? String.format("%.2f", (Double) valor) : "-"
                );
            });

            colPuntosParcial.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #c8e6c9;");
                    }
                }
            });

            tabla.getColumns().add(colPuntosParcial);

            // Columna Calificación Parcial
            TableColumn<java.util.Map<String, Object>, String> colCalificacionParcial = new TableColumn<>("Calificación Parcial");
            colCalificacionParcial.setPrefWidth(150);
            colCalificacionParcial.setMinWidth(150);
            colCalificacionParcial.setMaxWidth(150);
            colCalificacionParcial.setResizable(false);

            colCalificacionParcial.setCellValueFactory(cellData -> {
                Object valor = cellData.getValue().get("calificacionParcial");
                return new javafx.beans.property.SimpleStringProperty(
                    valor != null ? String.format("%.2f", (Double) valor) : "-"
                );
            });

            colCalificacionParcial.setCellFactory(col -> new TableCell<java.util.Map<String, Object>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #e8f5e9; -fx-font-size: 16px;");
                    }
                }
            });

            tabla.getColumns().add(colCalificacionParcial);

            // Llenar datos
            ObservableList<java.util.Map<String, Object>> datos = FXCollections.observableArrayList();
            int numero = 1;


            for (Alumno alumno : alumnos) {
                java.util.Map<String, Object> fila = new java.util.HashMap<>();
                fila.put("numero", numero++);
                fila.put("alumnoId", alumno.getId());
                fila.put("nombreCompleto", alumno.getApellidoPaterno() + " " +
                        alumno.getApellidoMaterno() + " " + alumno.getNombre());

                // Cargar calificaciones existentes desde CalificacionConcentrado
                for (Criterio criterio : criterios) {
                    List<Agregado> agregados = agregadoService.obtenerAgregadosPorCriterio(criterio.getId());
                    boolean esCheck = "Check".equalsIgnoreCase(criterio.getTipoEvaluacion());

                    for (Agregado agregado : agregados) {
                        Optional<CalificacionConcentrado> calificacion = calificacionConcentradoService
                                .obtenerCalificacionPorAlumnoYAgregadoYFiltros(
                                        alumno.getId(),
                                        agregado.getId(),
                                        grupo.getId(),
                                        materia.getId(),
                                        parcial
                                );

                        if (calificacion.isPresent()) {
                            Double puntuacion = calificacion.get().getPuntuacion();
                            if (esCheck) {
                                // Para tipo Check, convertir a Boolean
                                fila.put("agregado_" + agregado.getId(), puntuacion != null && puntuacion > 0);
                            } else {
                                // Para tipo Puntuacion, mantener como String
                                fila.put("agregado_" + agregado.getId(), puntuacion != null ? String.valueOf(puntuacion) : "");
                            }
                        } else {
                            // Si no hay calificaci├│n, poner valor por defecto seg├║n el tipo
                            if (esCheck) {
                                fila.put("agregado_" + agregado.getId(), false);
                            } else {
                                fila.put("agregado_" + agregado.getId(), "");
                            }
                        }
                    }
                }

                // Cargar datos de examen si existe
                if (examenOpt.isPresent()) {
                    Examen examen = examenOpt.get();
                    Optional<AlumnoExamen> alumnoExamenOpt = alumnoExamenService
                            .obtenerAlumnoExamenPorAlumnoYExamen(alumno.getId(), examen.getId());

                    if (alumnoExamenOpt.isPresent()) {
                        AlumnoExamen alumnoExamen = alumnoExamenOpt.get();
                        fila.put("aciertosExamen", alumnoExamen.getPuntosExamen());
                        fila.put("porcentajeExamen", alumnoExamen.getPorcentaje());
                        fila.put("calificacionExamen", alumnoExamen.getCalificacion());
                    } else {
                        // Sin datos de examen
                        fila.put("aciertosExamen", null);
                        fila.put("porcentajeExamen", null);
                        fila.put("calificacionExamen", null);
                    }
                }

                // Calcular y agregar puntosParcial y calificacionParcial al Map
                double totalPortafolio = 0.0;
                for (java.util.Map<String, Object> criterioInfo : criteriosInfo) {
                    @SuppressWarnings("unchecked")
                    List<Long> agregadoIds = (List<Long>) criterioInfo.get("agregadoIds");
                    boolean esCheck = (Boolean) criterioInfo.get("esCheck");
                    Double puntuacionMaxima = (Double) criterioInfo.get("puntuacionMaxima");

                    double puntosObtenidosCriterio = 0.0;

                    for (Long agregadoId : agregadoIds) {
                        Object valor = fila.get("agregado_" + agregadoId);

                        if (esCheck) {
                            if (valor instanceof Boolean && (Boolean) valor) {
                                puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
                            } else if (valor instanceof String) {
                                String strValor = (String) valor;
                                if ("true".equalsIgnoreCase(strValor) || "1".equals(strValor)) {
                                    puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
                                }
                            }
                        } else {
                            if (valor instanceof Number) {
                                puntosObtenidosCriterio += ((Number) valor).doubleValue();
                            } else if (valor instanceof String && !((String) valor).isEmpty()) {
                                try {
                                    puntosObtenidosCriterio += Double.parseDouble((String) valor);
                                } catch (NumberFormatException e) {
                                    // Ignorar valores no num├⌐ricos
                                }
                            }
                        }
                    }

                    totalPortafolio += puntosObtenidosCriterio;
                }

                double puntosExamen = 0.0;
                Object aciertosExamen = fila.get("aciertosExamen");
                if (aciertosExamen != null) {
                    if (aciertosExamen instanceof Number) {
                        puntosExamen = ((Number) aciertosExamen).doubleValue();
                    } else if (aciertosExamen instanceof String) {
                        try {
                            puntosExamen = Double.parseDouble((String) aciertosExamen);
                        } catch (NumberFormatException e) {
                            // Ignorar valores no num├⌐ricos
                        }
                    }
                }

                double puntosParcial = totalPortafolio + puntosExamen;
                double calificacionParcial = (puntosParcial * 10.0) / 100.0;

                fila.put("puntosParcial", puntosParcial);
                fila.put("calificacionParcial", calificacionParcial);

                datos.add(fila);
            }

            tabla.setItems(datos);

        } catch (Exception e) {
            LOG.error("Error al generar tabla de calificaciones", e);
            mostrarAlerta("Error", "Error al generar la tabla: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // M├⌐todo para guardar las calificaciones
    private void guardarCalificaciones(TableView<java.util.Map<String, Object>> tabla, Grupo grupo, Materia materia, Integer parcial) {
        try {
            int totalGuardadas = 0;

            // Obtener criterios para calcular el portafolio
            List<Criterio> criterios = criterioService.obtenerCriteriosPorMateria(materia.getId()).stream()
                    .filter(c -> c.getParcial() != null && c.getParcial().equals(parcial))
                    .collect(java.util.stream.Collectors.toList());

            // Obtener el examen si existe
            Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
                    grupo.getId(), materia.getId(), parcial);

            for (java.util.Map<String, Object> fila : tabla.getItems()) {
                Long alumnoId = (Long) fila.get("alumnoId");

                // Calcular portafolio sumando todos los criterios del alumno
                double totalPortafolio = 0.0;
                for (Criterio criterio : criterios) {
                    List<Agregado> agregados = agregadoService.obtenerAgregadosPorCriterio(criterio.getId());
                    boolean esCheck = "Check".equalsIgnoreCase(criterio.getTipoEvaluacion());

                    double puntosObtenidosCriterio = 0.0;
                    for (Agregado agregado : agregados) {
                        Object valor = fila.get("agregado_" + agregado.getId());

                        if (esCheck) {
                            if (valor instanceof Boolean && (Boolean) valor) {
                                puntosObtenidosCriterio += criterio.getPuntuacionMaxima() / agregados.size();
                            } else if (valor instanceof String) {
                                String strValor = (String) valor;
                                if ("true".equalsIgnoreCase(strValor) || "1".equals(strValor)) {
                                    puntosObtenidosCriterio += criterio.getPuntuacionMaxima() / agregados.size();
                                }
                            }
                        } else {
                            if (valor instanceof Number) {
                                puntosObtenidosCriterio += ((Number) valor).doubleValue();
                            } else if (valor instanceof String && !((String) valor).isEmpty()) {
                                try {
                                    puntosObtenidosCriterio += Double.parseDouble((String) valor);
                                } catch (NumberFormatException e) {
                                    // Ignorar valores no num├⌐ricos
                                }
                            }
                        }
                    }
                    totalPortafolio += puntosObtenidosCriterio;
                }

                // Obtener puntos del examen (aciertos)
                double puntosExamen = 0.0;
                if (examenOpt.isPresent()) {
                    Optional<AlumnoExamen> alumnoExamenOpt = alumnoExamenService.obtenerAlumnoExamenPorAlumnoYExamen(
                            alumnoId, examenOpt.get().getId());
                    if (alumnoExamenOpt.isPresent()) {
                        Integer aciertos = alumnoExamenOpt.get().getPuntosExamen();
                        puntosExamen = aciertos != null ? aciertos.doubleValue() : 0.0;
                    }
                }

                // Calcular puntos parcial y calificaci├│n parcial
                double puntosParcial = totalPortafolio + puntosExamen;
                double calificacionParcial = (puntosParcial * 10.0) / 100.0;

                // Recorrer todas las claves que empiezan con "agregado_"
                for (String clave : fila.keySet()) {
                    if (clave.startsWith("agregado_")) {
                        Object valor = fila.get(clave);
                        if (valor != null) {
                            try {
                                Long agregadoId = Long.parseLong(clave.replace("agregado_", ""));
                                Double puntuacion = null;

                                // Manejar diferentes tipos de valores
                                if (valor instanceof Boolean) {
                                    // Para checkboxes: true = 1.0, false = 0.0
                                    puntuacion = ((Boolean) valor) ? 1.0 : 0.0;
                                } else if (valor instanceof String) {
                                    String valorStr = ((String) valor).trim();
                                    if (!valorStr.isEmpty()) {
                                        // Intentar convertir a n├║mero
                                        if ("true".equalsIgnoreCase(valorStr)) {
                                            puntuacion = 1.0;
                                        } else if ("false".equalsIgnoreCase(valorStr)) {
                                            puntuacion = 0.0;
                                        } else {
                                            puntuacion = Double.parseDouble(valorStr);
                                        }
                                    }
                                } else if (valor instanceof Number) {
                                    puntuacion = ((Number) valor).doubleValue();
                                }

                                if (puntuacion != null) {
                                    // Obtener el agregado para saber el criterioId
                                    Optional<Agregado> agregadoOpt = agregadoService.obtenerAgregadoPorId(agregadoId);
                                    if (agregadoOpt.isPresent()) {
                                        Agregado agregado = agregadoOpt.get();

                                        // Crear CalificacionConcentrado con todos los campos incluidos puntosParcial y calificacionParcial
                                        CalificacionConcentrado calificacion = CalificacionConcentrado.builder()
                                                .alumnoId(alumnoId)
                                                .agregadoId(agregadoId)
                                                .criterioId(agregado.getCriterioId())
                                                .grupoId(grupo.getId())
                                                .materiaId(materia.getId())
                                                .parcial(parcial)
                                                .puntuacion(puntuacion)
                                                .puntosParcial(puntosParcial)
                                                .calificacionParcial(calificacionParcial)
                                                .build();

                                        calificacionConcentradoService.crearCalificacion(calificacion);
                                        totalGuardadas++;
                                    }
                                }
                            } catch (NumberFormatException e) {
                                LOG.warn("Valor inv├ílido para calificaci├│n: " + valor);
                            }
                        }
                    }
                }
            }
            LOG.info("Total de calificaciones guardadas: " + totalGuardadas);
        } catch (Exception e) {
            LOG.error("Error al guardar calificaciones", e);
            mostrarAlerta("Error", "Error al guardar las calificaciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // M├⌐todo para cargar agregados en la tabla
    private void cargarAgregados(TableView<Agregado> tabla) {
        try {
            ObservableList<Agregado> agregadosList = FXCollections.observableArrayList(
                    agregadoService.obtenerTodosLosAgregados()
            );
            tabla.setItems(agregadosList);

            // Forzar el refresh de la tabla para que las columnas de botones se actualicen
            tabla.refresh();

            LOG.info("Agregados cargados correctamente: {} agregados", agregadosList.size());
        } catch (Exception e) {
            LOG.error("Error al cargar agregados en la tabla", e);
            tabla.setItems(FXCollections.observableArrayList());
        }
    }


    // M├⌐todo para aplicar filtros combinados de materia y parcial
    private void aplicarFiltrosCriterios(TableView<Criterio> tabla, ComboBox<Materia> cmbMateria, ComboBox<Integer> cmbParcial) {
        try {
            List<Criterio> criteriosFiltrados = criterioService.obtenerTodosLosCriterios();

            // Filtrar por materia si est├í seleccionada
            Materia materiaSeleccionada = cmbMateria.getValue();
            if (materiaSeleccionada != null) {
                criteriosFiltrados = criteriosFiltrados.stream()
                        .filter(c -> materiaSeleccionada.getId().equals(c.getMateriaId()))
                        .collect(java.util.stream.Collectors.toList());
            }

            // Filtrar por parcial si est├í seleccionado
            Integer parcialSeleccionado = cmbParcial.getValue();
            if (parcialSeleccionado != null) {
                criteriosFiltrados = criteriosFiltrados.stream()
                        .filter(c -> parcialSeleccionado.equals(c.getParcial()))
                        .collect(java.util.stream.Collectors.toList());
            }

            // Ordenar por parcial y orden
            criteriosFiltrados.sort((c1, c2) -> {
                // Primero por parcial
                if (c1.getParcial() != null && c2.getParcial() != null) {
                    int parcialComp = Integer.compare(c1.getParcial(), c2.getParcial());
                    if (parcialComp != 0) return parcialComp;
                }
                // Luego por orden
                if (c1.getOrden() == null && c2.getOrden() == null) return 0;
                if (c1.getOrden() == null) return 1;
                if (c2.getOrden() == null) return -1;
                return Integer.compare(c1.getOrden(), c2.getOrden());
            });

            ObservableList<Criterio> criteriosList = FXCollections.observableArrayList(criteriosFiltrados);
            tabla.setItems(criteriosList);

            // Forzar el refresh de la tabla
            tabla.refresh();

            LOG.info("Criterios filtrados: {} criterios", criteriosList.size());
        } catch (Exception e) {
            LOG.error("Error al aplicar filtros de criterios", e);
            tabla.setItems(FXCollections.observableArrayList());
        }
    }


    // M├⌐todo para filtrar agregados por criterio
    private void filtrarAgregadosPorCriterio(TableView<Agregado> tabla, Long criterioId) {
        try {
            List<Agregado> agregadosDelCriterio = agregadoService.obtenerAgregadosPorCriterio(criterioId);

            // Ordenar por orden
            agregadosDelCriterio.sort((a1, a2) -> {
                if (a1.getOrden() == null && a2.getOrden() == null) return 0;
                if (a1.getOrden() == null) return 1;
                if (a2.getOrden() == null) return -1;
                return Integer.compare(a1.getOrden(), a2.getOrden());
            });

            ObservableList<Agregado> agregadosList = FXCollections.observableArrayList(agregadosDelCriterio);
            tabla.setItems(agregadosList);

            // Forzar el refresh de la tabla para que las columnas de botones se actualicen
            tabla.refresh();

            LOG.info("Agregados filtrados por criterio {}: {} agregados", criterioId, agregadosList.size());
        } catch (Exception e) {
            LOG.error("Error al filtrar agregados por criterio", e);
            tabla.setItems(FXCollections.observableArrayList());
        }
    }

    // M├⌐todo para crear la vista completa de Ex├ímenes
    private VBox crearVistaExamenesCompleta() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        try {
            // Header
            Label lblTitulo = new Label("Ex├ímenes");
            lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Panel de filtros
            VBox filtrosPanel = new VBox(15);
            filtrosPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            Label lblFiltros = new Label("Filtros (Obligatorios)");
            lblFiltros.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Fila de filtros
            javafx.scene.layout.HBox filtrosBox = new javafx.scene.layout.HBox(20);
            filtrosBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // ComboBox para seleccionar grupo
            VBox grupoContainer = new VBox(5);
            Label lblGrupo = new Label("Grupo: *");
            lblGrupo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            ComboBox<Grupo> cmbGrupo = new ComboBox<>();
            cmbGrupo.setPrefWidth(150);
            cmbGrupo.setPromptText("Seleccionar...");
            try {
                List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
                cmbGrupo.setItems(FXCollections.observableArrayList(grupos));
            } catch (Exception e) {
                LOG.error("Error al cargar grupos", e);
            }
            cmbGrupo.setCellFactory(param -> new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.valueOf(item.getId()));
                }
            });
            cmbGrupo.setButtonCell(new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Seleccionar..." : String.valueOf(item.getId()));
                }
            });
            grupoContainer.getChildren().addAll(lblGrupo, cmbGrupo);

            // ComboBox para seleccionar materia
            VBox materiaContainer = new VBox(5);
            Label lblMateria = new Label("Materia: *");
            lblMateria.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            ComboBox<Materia> cmbMateria = new ComboBox<>();
            cmbMateria.setPrefWidth(250);
            cmbMateria.setPromptText("Seleccionar...");
            cmbMateria.setDisable(true);
            cmbMateria.setCellFactory(param -> new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
            cmbMateria.setButtonCell(new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Seleccionar..." : item.getNombre());
                }
            });
            materiaContainer.getChildren().addAll(lblMateria, cmbMateria);

            // ComboBox para seleccionar parcial
            VBox parcialContainer = new VBox(5);
            Label lblParcial = new Label("Parcial: *");
            lblParcial.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            ComboBox<Integer> cmbParcial = new ComboBox<>();
            cmbParcial.setPrefWidth(120);
            cmbParcial.setPromptText("Seleccionar...");
            cmbParcial.setItems(FXCollections.observableArrayList(1, 2, 3));
            parcialContainer.getChildren().addAll(lblParcial, cmbParcial);

            // Bot├│n Buscar en la misma fila
            VBox buscarContainer = new VBox(5);
            Label lblEspacio = new Label(" "); // Espaciador para alinear con los otros labels
            Button btnBuscar = new Button("Buscar");
            btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            btnBuscar.setOnMouseEntered(e -> btnBuscar.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;"));
            btnBuscar.setOnMouseExited(e -> btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;"));
            buscarContainer.getChildren().addAll(lblEspacio, btnBuscar);

            // Bot├│n Limpiar
            VBox limpiarContainer = new VBox(5);
            Label lblEspacio2 = new Label(" "); // Espacio para alinear con los otros labels
            Button btnLimpiar = new Button("Limpiar");
            btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            limpiarContainer.getChildren().addAll(lblEspacio2, btnLimpiar);

            // Botón Exportar
            VBox exportarContainer = new VBox(5);
            Label lblEspacio3 = new Label(" ");
            Button btnExportar = new Button("Exportar");
            btnExportar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            btnExportar.setDisable(true);
            exportarContainer.getChildren().addAll(lblEspacio3, btnExportar);

            filtrosBox.getChildren().addAll(grupoContainer, materiaContainer, parcialContainer, buscarContainer, limpiarContainer, exportarContainer);

            // Segunda fila de filtros opcionales (Nombre, Apellidos)
            javafx.scene.layout.HBox filtrosOpcionalesBox = new javafx.scene.layout.HBox(20);
            filtrosOpcionalesBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // TextField para filtrar por Nombre
            VBox nombreContainer = new VBox(5);
            Label lblNombre = new Label("Nombre:");
            lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            TextField txtNombre = new TextField();
            txtNombre.setPrefWidth(200);
            txtNombre.setPromptText("Filtrar por nombre...");
            txtNombre.setDisable(true);
            nombreContainer.getChildren().addAll(lblNombre, txtNombre);

            // TextField para filtrar por Apellido Paterno
            VBox apellidoPaternoContainer = new VBox(5);
            Label lblApellidoPaterno = new Label("Apellido Paterno:");
            lblApellidoPaterno.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            TextField txtApellidoPaterno = new TextField();
            txtApellidoPaterno.setPrefWidth(200);
            txtApellidoPaterno.setPromptText("Filtrar por apellido paterno...");
            txtApellidoPaterno.setDisable(true);
            apellidoPaternoContainer.getChildren().addAll(lblApellidoPaterno, txtApellidoPaterno);

            // TextField para filtrar por Apellido Materno
            VBox apellidoMaternoContainer = new VBox(5);
            Label lblApellidoMaterno = new Label("Apellido Materno:");
            lblApellidoMaterno.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            TextField txtApellidoMaterno = new TextField();
            txtApellidoMaterno.setPrefWidth(200);
            txtApellidoMaterno.setPromptText("Filtrar por apellido materno...");
            txtApellidoMaterno.setDisable(true);
            apellidoMaternoContainer.getChildren().addAll(lblApellidoMaterno, txtApellidoMaterno);

            filtrosOpcionalesBox.getChildren().addAll(nombreContainer, apellidoPaternoContainer, apellidoMaternoContainer);

            filtrosPanel.getChildren().addAll(lblFiltros, filtrosBox, filtrosOpcionalesBox);

            // Panel de tabla
            VBox tablaPanel = new VBox(15);
            tablaPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPrefHeight(500);
            scrollPane.setStyle("-fx-background-color: transparent;");

            TableView<java.util.Map<String, Object>> tblInforme = new TableView<>();
            tblInforme.setEditable(false); // SOLO LECTURA
            tblInforme.setPlaceholder(new Label("Seleccione Grupo, Materia y Parcial, luego presione 'Buscar'"));
            tblInforme.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            scrollPane.setContent(tblInforme);
            tablaPanel.getChildren().addAll(scrollPane);

            // L├│gica para cargar materias cuando se selecciona un grupo
            cmbGrupo.setOnAction(event -> {
                Grupo grupoSeleccionado = cmbGrupo.getValue();
                if (grupoSeleccionado != null) {
                    try {
                        List<GrupoMateria> asignaciones = grupoMateriaService.obtenerMateriasPorGrupo(grupoSeleccionado.getId());
                        List<Materia> materias = new java.util.ArrayList<>();
                        for (GrupoMateria gm : asignaciones) {
                            materiaService.obtenerMateriaPorId(gm.getMateriaId()).ifPresent(materias::add);
                        }
                        cmbMateria.setItems(FXCollections.observableArrayList(materias));
                        cmbMateria.setDisable(false);
                    } catch (Exception e) {
                        LOG.error("Error al cargar materias del grupo", e);
                        mostrarAlerta("Error", "No se pudieron cargar las materias del grupo", Alert.AlertType.ERROR);
                    }
                } else {
                    cmbMateria.setItems(FXCollections.observableArrayList());
                    cmbMateria.setDisable(true);
                    txtNombre.setDisable(true);
                    txtApellidoPaterno.setDisable(true);
                    txtApellidoMaterno.setDisable(true);
                }
            });

            // Habilitar filtros opcionales cuando se seleccionan los filtros obligatorios
            cmbParcial.setOnAction(event -> {
                if (cmbGrupo.getValue() != null && cmbMateria.getValue() != null && cmbParcial.getValue() != null) {
                    txtNombre.setDisable(false);
                    txtApellidoPaterno.setDisable(false);
                    txtApellidoMaterno.setDisable(false);
                } else {
                    txtNombre.setDisable(true);
                    txtApellidoPaterno.setDisable(true);
                    txtApellidoMaterno.setDisable(true);
                }
            });

            cmbMateria.setOnAction(event -> {
                if (cmbGrupo.getValue() != null && cmbMateria.getValue() != null && cmbParcial.getValue() != null) {
                    txtNombre.setDisable(false);
                    txtApellidoPaterno.setDisable(false);
                    txtApellidoMaterno.setDisable(false);
                } else {
                    txtNombre.setDisable(true);
                    txtApellidoPaterno.setDisable(true);
                    txtApellidoMaterno.setDisable(true);
                }
            });

            // Evento del bot├│n Buscar
            btnBuscar.setOnAction(event -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAlerta("Validaci├│n", "Debe seleccionar Grupo, Materia y Parcial", Alert.AlertType.WARNING);
                    return;
                }

                // Obtener valores de filtros opcionales
                String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
                String apellidoPaterno = txtApellidoPaterno.getText() != null ? txtApellidoPaterno.getText().trim() : "";
                String apellidoMaterno = txtApellidoMaterno.getText() != null ? txtApellidoMaterno.getText().trim() : "";

                generarTablaInformeConcentrado(tblInforme, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue(),
                        nombre, apellidoPaterno, apellidoMaterno);

                // Habilitar botón exportar después de generar la tabla
                btnExportar.setDisable(false);
            });

            // Evento del botón Exportar
            btnExportar.setOnAction(event -> {
                if (tblInforme.getItems().isEmpty()) {
                    mostrarAlerta("Información", "No hay datos para exportar. Primero genere la tabla.", Alert.AlertType.INFORMATION);
                    return;
                }

                exportarInformeAExcel(tblInforme, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
            });

            // Evento del bot├│n Limpiar
            btnLimpiar.setOnAction(event -> {
                // Limpiar ComboBoxes
                cmbGrupo.setValue(null);
                cmbMateria.setValue(null);
                cmbMateria.setDisable(true);
                cmbParcial.setValue(null);

                // Limpiar TextFields
                txtNombre.clear();
                txtNombre.setDisable(true);
                txtApellidoPaterno.clear();
                txtApellidoPaterno.setDisable(true);
                txtApellidoMaterno.clear();
                txtApellidoMaterno.setDisable(true);

                // Limpiar tabla
                tblInforme.getColumns().clear();
                tblInforme.getItems().clear();

                // Deshabilitar botón exportar
                btnExportar.setDisable(true);
            });

            vista.getChildren().addAll(lblTitulo, filtrosPanel, tablaPanel);

        } catch (Exception e) {
            LOG.error("Error al crear vista de informe de concentrado", e);
        }

        return vista;
    }

    // Método para generar archivo Word del concentrado de calificaciones
    private void generarArchivoConcentrado(TableView<java.util.Map<String, Object>> tabla, Grupo grupo, Materia materia, Integer parcial) {
        try {
            // Validar que haya datos en la tabla
            if (tabla.getItems().isEmpty()) {
                mostrarAlerta("Advertencia", "No hay datos para exportar", Alert.AlertType.WARNING);
                return;
            }

            // Obtener el examen para acceder a la fecha de aplicaci├│n
            Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
                grupo.getId(), materia.getId(), parcial
            );

            String fechaAplicacionStr = "N/A";
            if (examenOpt.isPresent() && examenOpt.get().getFechaAplicacion() != null) {
                fechaAplicacionStr = examenOpt.get().getFechaAplicacion()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            // Obtener el nombre del maestro de la configuraci├│n
            String nombreMaestro = "N/A";
            Optional<Configuracion> configuracionOpt = configuracionService.obtenerConfiguracion();
            if (configuracionOpt.isPresent() && configuracionOpt.get().getNombreMaestro() != null) {
                nombreMaestro = configuracionOpt.get().getNombreMaestro();
            }

            // Obtener el semestre desde el primer d├¡gito del ID del grupo
            String semestre = obtenerSemestreDesdeGrupoId(grupo.getId());

            // Crear FileChooser para seleccionar d├│nde guardar
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Concentrado de Calificaciones");

            // Nombre sugerido del archivo
            String nombreArchivo = String.format("concentrado_%s_%s_parcial%d_%s.docx",
                    grupo.getId(),
                    materia.getNombre().replaceAll("[^a-zA-Z0-9]", "_"),
                    parcial,
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            fileChooser.setInitialFileName(nombreArchivo);

            // Filtro para archivos Word
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Documentos Word", "*.docx")
            );

            // Mostrar di├ílogo para seleccionar ubicaci├│n
            File file = fileChooser.showSaveDialog(mainContent.getScene().getWindow());

            if (file == null) {
                // Usuario cancel├│ la operaci├│n
                return;
            }

            // Preparar variables para la plantilla
            Map<String, String> variables = new HashMap<>();
            variables.put("nombreMateria", materia.getNombre());
            variables.put("numeroGrupo", String.valueOf(grupo.getId()));
            variables.put("parcial", String.valueOf(parcial));
            variables.put("fecha", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            variables.put("fechaAplicacion", fechaAplicacionStr);

            // Ruta de la plantilla
            Path templatePath = Paths.get("plantillas/concentrado_calificaciones.docx");

            // Obtener criterios de evaluaci├│n para esta materia y parcial
            List<Criterio> criterios = new ArrayList<>();
            List<java.util.Map<String, Object>> criteriosInfo = new ArrayList<>();
            int totalCriterios = 0;

            try {
                criterios = criterioService.obtenerCriteriosPorMateria(materia.getId()).stream()
                    .filter(c -> parcial.equals(c.getParcial()))
                    .sorted((c1, c2) -> {
                        if (c1.getOrden() == null && c2.getOrden() == null) return 0;
                        if (c1.getOrden() == null) return 1;
                        if (c2.getOrden() == null) return -1;
                        return Integer.compare(c1.getOrden(), c2.getOrden());
                    })
                    .collect(java.util.stream.Collectors.toList());
                totalCriterios = criterios.size();
                LOG.info("Total de criterios para materia {} parcial {}: {}", materia.getId(), parcial, totalCriterios);

                // Recopilar informaci├│n de criterios y agregados para calcular portafolio
                for (Criterio criterio : criterios) {
                    List<Agregado> agregados = agregadoService.obtenerAgregadosPorCriterio(criterio.getId()).stream()
                        .sorted((a1, a2) -> {
                            if (a1.getOrden() == null && a2.getOrden() == null) return 0;
                            if (a1.getOrden() == null) return 1;
                            if (a2.getOrden() == null) return -1;
                            return Integer.compare(a1.getOrden(), a2.getOrden());
                        })
                        .collect(java.util.stream.Collectors.toList());

                    if (!agregados.isEmpty()) {
                        java.util.Map<String, Object> criterioInfo = new java.util.HashMap<>();
                        criterioInfo.put("criterioId", criterio.getId());
                        criterioInfo.put("agregadoIds", agregados.stream().map(Agregado::getId).collect(java.util.stream.Collectors.toList()));
                        criterioInfo.put("esCheck", "Check".equalsIgnoreCase(criterio.getTipoEvaluacion()));
                        criterioInfo.put("puntuacionMaxima", criterio.getPuntuacionMaxima());
                        criteriosInfo.add(criterioInfo);
                    }
                }
            } catch (Exception e) {
                LOG.error("Error al obtener criterios", e);
            }
            final int TOTAL_CRITERIOS = totalCriterios;
            final List<java.util.Map<String, Object>> CRITERIOS_INFO = criteriosInfo;
            final String NOMBRE_MAESTRO = nombreMaestro;
            final String PARCIAL = String.valueOf(parcial);
            final String SEMESTRE = semestre;

            // Usar la plantilla - escribir directamente en las filas
            try (java.io.FileInputStream fis = new java.io.FileInputStream(templatePath.toFile());
                 org.apache.poi.xwpf.usermodel.XWPFDocument document = new org.apache.poi.xwpf.usermodel.XWPFDocument(fis);
                 java.io.FileOutputStream out = new java.io.FileOutputStream(file)) {

                 // Reemplazar etiquetas en p├írrafos del documento
                 for (org.apache.poi.xwpf.usermodel.XWPFParagraph paragraph : document.getParagraphs()) {
                     reemplazarEtiquetasEnParrafo(paragraph, materia.getNombre(), fechaAplicacionStr, NOMBRE_MAESTRO, PARCIAL, SEMESTRE);
                 }

                 // Reemplazar etiquetas en tablas
                 for (org.apache.poi.xwpf.usermodel.XWPFTable table : document.getTables()) {
                     for (org.apache.poi.xwpf.usermodel.XWPFTableRow row : table.getRows()) {
                         for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : row.getTableCells()) {
                             for (org.apache.poi.xwpf.usermodel.XWPFParagraph paragraph : cell.getParagraphs()) {
                                 reemplazarEtiquetasEnParrafo(paragraph, materia.getNombre(), fechaAplicacionStr, NOMBRE_MAESTRO, PARCIAL, SEMESTRE);
                             }
                         }
                     }
                 }

                 if (!document.getTables().isEmpty()) {
                    org.apache.poi.xwpf.usermodel.XWPFTable table = document.getTables().get(0);
                    LOG.info("Tabla encontrada con {} filas", table.getNumberOfRows());

                    // Los datos se escriben a partir de la fila 6 (├¡ndice 5)
                    final int FILA_INICIO = 5; // Fila 6 (base 1)
                    final int COL_NUMERO_LISTA = 0; // Primera columna
                    final int COL_NOMBRE_COMPLETO = 1; // Segunda columna

                    // Obtener datos de alumnos
                    java.util.List<java.util.Map<String, Object>> alumnos = tabla.getItems();
                    LOG.info("Total de alumnos a exportar: {}", alumnos.size());

                    // Verificar si es necesario insertar filas adicionales
                    int filasNecesarias = FILA_INICIO + alumnos.size();
                    if (table.getNumberOfRows() < filasNecesarias) {
                        int filasAInsertar = filasNecesarias - table.getNumberOfRows();
                        LOG.info("Insertando {} filas adicionales en la tabla (tiene {}, necesita {})",
                            filasAInsertar, table.getNumberOfRows(), filasNecesarias);

                        // Obtener la ├║ltima fila como referencia para copiar el formato
                        org.apache.poi.xwpf.usermodel.XWPFTableRow filaReferencia = table.getRow(table.getNumberOfRows() - 1);

                        for (int i = 0; i < filasAInsertar; i++) {
                            org.apache.poi.xwpf.usermodel.XWPFTableRow nuevaFila = table.createRow();

                            // Copiar el formato de la fila de referencia
                            for (int j = 0; j < filaReferencia.getTableCells().size() && j < nuevaFila.getTableCells().size(); j++) {
                                org.apache.poi.xwpf.usermodel.XWPFTableCell celdaReferencia = filaReferencia.getCell(j);
                                org.apache.poi.xwpf.usermodel.XWPFTableCell nuevaCelda = nuevaFila.getCell(j);

                                // Copiar propiedades de la celda (bordes, alineaci├│n, etc.)
                                if (celdaReferencia.getCTTc().getTcPr() != null) {
                                    nuevaCelda.getCTTc().setTcPr(
                                        (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr)
                                        celdaReferencia.getCTTc().getTcPr().copy()
                                    );
                                }
                            }
                        }
                        LOG.info("Filas insertadas correctamente. Total de filas: {}", table.getNumberOfRows());
                    }

                    // SOLO ESCRIBIR DATOS en las filas existentes, sin modificar formato ni estructura
                    for (int alumnoIdx = 0; alumnoIdx < alumnos.size(); alumnoIdx++) {
                        java.util.Map<String, Object> fila = alumnos.get(alumnoIdx);
                        int filaIndex = FILA_INICIO + alumnoIdx;
                        double calificacionParcialValor = 0.0; // Variable para almacenar la calificación parcial

                        // Obtener la fila existente (sin crear ni clonar)
                        org.apache.poi.xwpf.usermodel.XWPFTableRow filaActual = table.getRow(filaIndex);

                        // Obtener datos del alumno
                        Object numero = fila.get("numero");
                        Object nombreCompleto = fila.get("nombreCompleto");
                        String numeroStr = numero != null ? numero.toString() : String.valueOf(alumnoIdx + 1);
                        String nombreStr = nombreCompleto != null ? nombreCompleto.toString() : "";

                        LOG.info("Alumno {} en fila {}: numero='{}', nombre='{}'", alumnoIdx + 1, filaIndex + 1, numeroStr, nombreStr);

                        // Escribir SOLO los datos en las columnas correspondientes
                        if (!filaActual.getTableCells().isEmpty()) {
                            org.apache.poi.xwpf.usermodel.XWPFTableCell cell = filaActual.getCell(COL_NUMERO_LISTA);
                            escribirSoloTexto(cell, numeroStr);
                        }

                        if (COL_NOMBRE_COMPLETO < filaActual.getTableCells().size()) {
                            org.apache.poi.xwpf.usermodel.XWPFTableCell cell = filaActual.getCell(COL_NOMBRE_COMPLETO);
                            escribirSoloTexto(cell, nombreStr);
                        }

                        // Escribir "0" en la columna 3 (├¡ndice 2)
                        final int COL_TERCERA = 2;
                        if (COL_TERCERA < filaActual.getTableCells().size()) {
                            org.apache.poi.xwpf.usermodel.XWPFTableCell cell = filaActual.getCell(COL_TERCERA);
                            escribirSoloTexto(cell, "0");
                        }

                        // Escribir el total de criterios en la columna 4 (├¡ndice 3)
                        final int COL_TOTAL_CRITERIOS = 3;
                        if (COL_TOTAL_CRITERIOS < filaActual.getTableCells().size()) {
                            org.apache.poi.xwpf.usermodel.XWPFTableCell cell = filaActual.getCell(COL_TOTAL_CRITERIOS);
                            escribirSoloTexto(cell, String.valueOf(TOTAL_CRITERIOS));
                        }

                        // Calcular y escribir el Portafolio en la columna 5 (├¡ndice 4)
                        final int COL_PORTAFOLIO = 4;
                        if (COL_PORTAFOLIO < filaActual.getTableCells().size()) {
                            double totalPortafolio = 0.0;

                            // Sumar los puntos obtenidos de todos los criterios
                            for (java.util.Map<String, Object> criterioInfo : CRITERIOS_INFO) {
                                @SuppressWarnings("unchecked")
                                List<Long> agregadoIds = (List<Long>) criterioInfo.get("agregadoIds");
                                boolean esCheck = (Boolean) criterioInfo.get("esCheck");
                                Double puntuacionMaxima = (Double) criterioInfo.get("puntuacionMaxima");

                                double puntosObtenidosCriterio = 0.0;

                                // Calcular puntos obtenidos de este criterio
                                for (Long agregadoId : agregadoIds) {
                                    Object valor = fila.get("agregado_" + agregadoId);

                                    if (esCheck) {
                                        // Para tipo Check, cada checkbox marcado suma una parte proporcional
                                        if (valor instanceof Boolean && (Boolean) valor) {
                                            puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
                                        } else if (valor instanceof String) {
                                            String strValor = (String) valor;
                                            if ("true".equalsIgnoreCase(strValor) || "1".equals(strValor)) {
                                                puntosObtenidosCriterio += puntuacionMaxima / agregadoIds.size();
                                            }
                                        }
                                    } else {
                                        // Para tipo Puntuaci├│n, sumar los valores num├⌐ricos
                                        if (valor instanceof Number) {
                                            puntosObtenidosCriterio += ((Number) valor).doubleValue();
                                        } else if (valor instanceof String && !((String) valor).isEmpty()) {
                                            try {
                                                puntosObtenidosCriterio += Double.parseDouble((String) valor);
                                            } catch (NumberFormatException e) {
                                                // Ignorar valores no num├⌐ricos
                                            }
                                        }
                                    }
                                }

                                totalPortafolio += puntosObtenidosCriterio;
                            }

                            org.apache.poi.xwpf.usermodel.XWPFTableCell cell = filaActual.getCell(COL_PORTAFOLIO);
                            // Formatear como entero de dos d├¡gitos
                            escribirSoloTexto(cell, String.format("%02d", (int) Math.round(totalPortafolio)));
                        }

                        // Escribir Calificaci├│n Examen en columna 6 (├¡ndice 5) con 1 decimal
                        final int COL_CALIFICACION_EXAMEN = 5;
                        if (COL_CALIFICACION_EXAMEN < filaActual.getTableCells().size()) {
                            Object calificacionExamen = fila.get("calificacionExamen");
                            String valorStr = "0.0";
                            if (calificacionExamen != null) {
                                if (calificacionExamen instanceof Number) {
                                    valorStr = String.format("%.1f", ((Number) calificacionExamen).doubleValue());
                                } else if (calificacionExamen instanceof String && !"-".equals(calificacionExamen)) {
                                    try {
                                        double valor = Double.parseDouble((String) calificacionExamen);
                                        valorStr = String.format("%.1f", valor);
                                    } catch (NumberFormatException e) {
                                        valorStr = "0.0";
                                    }
                                }
                            }
                            org.apache.poi.xwpf.usermodel.XWPFTableCell cell = filaActual.getCell(COL_CALIFICACION_EXAMEN);
                            escribirSoloTexto(cell, valorStr);
                        }

                        // Escribir Puntos Examen en columna 7 (├¡ndice 6) como entero
                        final int COL_PUNTOS_EXAMEN = 6;
                        if (COL_PUNTOS_EXAMEN < filaActual.getTableCells().size()) {
                            Object aciertosExamen = fila.get("aciertosExamen");
                            String valorStr = "0";
                            if (aciertosExamen != null) {
                                if (aciertosExamen instanceof Number) {
                                    valorStr = String.valueOf(((Number) aciertosExamen).intValue());
                                } else if (aciertosExamen instanceof String && !"-".equals(aciertosExamen)) {
                                    try {
                                        valorStr = String.valueOf(Integer.parseInt((String) aciertosExamen));
                                    } catch (NumberFormatException e) {
                                        valorStr = "0";
                                    }
                                }
                            }
                            org.apache.poi.xwpf.usermodel.XWPFTableCell cell = filaActual.getCell(COL_PUNTOS_EXAMEN);
                            escribirSoloTexto(cell, valorStr);
                        }

                        // Escribir Calificación Parcial en columna 8 (índice 7) con 1 decimal
                        final int COL_CALIFICACION_PARCIAL = 7;
                        if (COL_CALIFICACION_PARCIAL < filaActual.getTableCells().size()) {
                            Object calificacionParcial = fila.get("calificacionParcial");
                            String valorStr = "0.0";
                            if (calificacionParcial != null) {
                                if (calificacionParcial instanceof Number) {
                                    calificacionParcialValor = ((Number) calificacionParcial).doubleValue();
                                    valorStr = String.format("%.1f", calificacionParcialValor);
                                } else if (calificacionParcial instanceof String && !"-".equals(calificacionParcial)) {
                                    try {
                                        calificacionParcialValor = Double.parseDouble((String) calificacionParcial);
                                        valorStr = String.format("%.1f", calificacionParcialValor);
                                    } catch (NumberFormatException e) {
                                        valorStr = "0.0";
                                    }
                                }
                            }
                            org.apache.poi.xwpf.usermodel.XWPFTableCell cell = filaActual.getCell(COL_CALIFICACION_PARCIAL);
                            escribirSoloTexto(cell, valorStr);
                        }

                        // Escribir Calificación Parcial en letra en columna 9 (índice 8)
                        final int COL_CALIFICACION_LETRA = 8;
                        if (COL_CALIFICACION_LETRA < filaActual.getTableCells().size()) {
                            String valorLetra = convertirCalificacionALetra(calificacionParcialValor);
                            org.apache.poi.xwpf.usermodel.XWPFTableCell cell = filaActual.getCell(COL_CALIFICACION_LETRA);
                            escribirTextoConFuenteReducida(cell, valorLetra); // Fuente 2 puntos m├ís peque├▒a
                        }
                    }

                    LOG.info("Datos escritos para {} alumnos", alumnos.size());
                } else {
                    LOG.error("No se encontraron tablas en el documento");
                    throw new IOException("La plantilla no contiene ninguna tabla");
                }

                document.write(out);
            } catch (java.io.FileNotFoundException e) {
                LOG.error("No se encontr├│ la plantilla en: {}", templatePath);
                mostrarAlerta("Error", "No se encontr├│ la plantilla en: " + templatePath.toString(), Alert.AlertType.ERROR);
                return;
            } catch (Exception e) {
                LOG.error("Error al procesar la plantilla", e);
                throw new IOException("Error al procesar la plantilla: " + e.getMessage(), e);
            }

            // Mostrar mensaje de ├⌐xito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportaci├│n Exitosa");
            alert.setHeaderText(null);
            alert.setContentText("El concentrado se ha exportado correctamente a:\n" + file.getAbsolutePath());

            // Agregar bot├│n para abrir el archivo
            ButtonType btnAbrir = new ButtonType("Abrir archivo");
            ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(btnAbrir, btnCerrar);

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == btnAbrir) {
                // Intentar abrir el archivo con la aplicaci├│n predeterminada
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        LOG.error("Error al abrir archivo", e);
                        mostrarAlerta("Error", "No se pudo abrir el archivo autom├íticamente", Alert.AlertType.WARNING);
                    }
                }
            }

        } catch (IOException e) {
            LOG.error("Error al generar archivo Word", e);
            mostrarAlerta("Error", "No se pudo generar el archivo: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            LOG.error("Error inesperado al generar archivo", e);
            mostrarAlerta("Error", "Error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }



    /**
     * Escribe SOLO el texto en una celda sin modificar formato ni estructura
     * Este m├⌐todo es lo m├ís minimalista posible para no alterar NADA de la plantilla
     */
    private void escribirSoloTexto(org.apache.poi.xwpf.usermodel.XWPFTableCell cell, String texto) {
        try {
            org.apache.poi.xwpf.usermodel.XWPFParagraph paragraph;

            // Si no hay p├írrafos, crear uno
            if (cell.getParagraphs().isEmpty()) {
                LOG.warn("Celda sin p├írrafos, creando uno nuevo");
                paragraph = cell.addParagraph();
            } else {
                paragraph = cell.getParagraphs().get(0);
            }

            org.apache.poi.xwpf.usermodel.XWPFRun run;

            // Si no hay runs, crear uno
            if (paragraph.getRuns().isEmpty()) {
                LOG.warn("P├írrafo sin runs, creando uno nuevo");
                run = paragraph.createRun();
            } else {
                run = paragraph.getRuns().get(0);
            }

            // SOLO reemplazar el texto del run
            run.setText(texto, 0);

        } catch (Exception e) {
            LOG.error("Error al escribir texto en celda: {}", e.getMessage(), e);
        }
    }

    /**
     * Escribe texto en una celda con fuente reducida (2 puntos menos)
     * Espec├¡fico para columna de calificaci├│n en letra
     */
    private void escribirTextoConFuenteReducida(org.apache.poi.xwpf.usermodel.XWPFTableCell cell, String texto) {
        try {
            org.apache.poi.xwpf.usermodel.XWPFParagraph paragraph;

            // Si no hay p├írrafos, crear uno
            if (cell.getParagraphs().isEmpty()) {
                LOG.warn("Celda sin p├írrafos, creando uno nuevo");
                paragraph = cell.addParagraph();
            } else {
                paragraph = cell.getParagraphs().get(0);
            }

            org.apache.poi.xwpf.usermodel.XWPFRun run;

            // Si no hay runs, crear uno
            if (paragraph.getRuns().isEmpty()) {
                LOG.warn("P├írrafo sin runs, creando uno nuevo");
                run = paragraph.createRun();
            } else {
                run = paragraph.getRuns().get(0);
            }

            // Obtener el tama├▒o de fuente actual y reducirlo en 2 puntos
            int tamanoActual = run.getFontSize();
            if (tamanoActual > 0) {
                run.setFontSize(tamanoActual - 2);
            } else {
                // Si no tiene tama├▒o definido, usar 9 puntos (11 - 2)
                run.setFontSize(9);
            }

            // SOLO reemplazar el texto del run
            run.setText(texto, 0);

        } catch (Exception e) {
            LOG.error("Error al escribir texto con fuente reducida en celda: {}", e.getMessage(), e);
        }
    }

    /**
     * Formatea un valor con un n├║mero espec├¡fico de decimales
     */
    private String formatearValorConDecimal(Object valor, int decimales) {
        if (valor == null) {
            return "";
        }
        if (valor instanceof Boolean) {
            return (Boolean) valor ? "✓" : "";
        }
        if (valor instanceof Number) {
            String formato = String.format("%%.%df", decimales);
            return String.format(formato, ((Number) valor).doubleValue());
        }
        if (valor instanceof String) {
            String strValor = (String) valor;
            // Si es un número en string, formatearlo
            try {
                double numValor = Double.parseDouble(strValor);
                String formato = String.format("%%.%df", decimales);
                return String.format(formato, numValor);
            } catch (NumberFormatException e) {
                // Si no es número, devolver como está
                if ("true".equalsIgnoreCase(strValor) || "1".equals(strValor)) {
                    return "✓";
                } else if ("false".equalsIgnoreCase(strValor) || "0".equals(strValor)) {
                    return "";
                }
                return strValor;
            }
        }
        return valor.toString();
    }

    /**
     * Convierte una calificaci├│n num├⌐rica (0-10) a su equivalente en letra
     * Ejemplo: 9.3 = "Nueve punto tres", 9.0 = "Nueve punto cero", 8.5 = "Ocho punto cinco"
     */
    private String convertirCalificacionALetra(double calificacion) {
        // Redondear a 1 decimal
        double calRedondeada = Math.round(calificacion * 10.0) / 10.0;

        // Separar parte entera y decimal
        int parteEntera = (int) calRedondeada;
        int parteDecimal = (int) Math.round((calRedondeada - parteEntera) * 10);

        // Convertir parte entera a letra
        String parteEnteraTexto = convertirEnteroALetra(parteEntera);

        // Siempre agregar "punto" y el decimal en letra (incluso si es cero)
        String parteDecimalTexto = convertirDigitoALetra(parteDecimal);
        return parteEnteraTexto + " punto " + parteDecimalTexto;
    }

    /**
     * Convierte un n├║mero entero (0-10) a letra
     */
    private String convertirEnteroALetra(int numero) {
        switch (numero) {
            case 0: return "Cero";
            case 1: return "Uno";
            case 2: return "Dos";
            case 3: return "Tres";
            case 4: return "Cuatro";
            case 5: return "Cinco";
            case 6: return "Seis";
            case 7: return "Siete";
            case 8: return "Ocho";
            case 9: return "Nueve";
            case 10: return "Diez";
            default: return "N/A";
        }
    }

    /**
     * Convierte un d├¡gito (0-9) a letra
     */
    private String convertirDigitoALetra(int digito) {
        switch (digito) {
            case 0: return "cero";
            case 1: return "uno";
            case 2: return "dos";
            case 3: return "tres";
            case 4: return "cuatro";
            case 5: return "cinco";
            case 6: return "seis";
            case 7: return "siete";
            case 8: return "ocho";
            case 9: return "nueve";
            default: return "";
        }
    }

    /**
     * Obtiene el nombre del semestre basado en el primer d├¡gito del ID del grupo.
     * Ejemplos: 101 ΓåÆ PRIMER, 201 ΓåÆ SEGUNDO, 301 ΓåÆ TERCER, etc.
     *
     * @param grupoId ID del grupo
     * @return Nombre del semestre en may├║sculas (PRIMER, SEGUNDO, TERCER, CUARTO, QUINTO, SEXTO)
     */
    private String obtenerSemestreDesdeGrupoId(Long grupoId) {
        if (grupoId == null) {
            return "N/A";
        }

        // Convertir el ID a String y obtener el primer d├¡gito
        String grupoIdStr = String.valueOf(grupoId);
        if (grupoIdStr.isEmpty()) {
            return "N/A";
        }

        // Obtener el primer d├¡gito
        char primerDigito = grupoIdStr.charAt(0);
        int semestre = Character.getNumericValue(primerDigito);

        // Mapear el d├¡gito al nombre del semestre
        switch (semestre) {
            case 1:
                return "PRIMER";
            case 2:
                return "SEGUNDO";
            case 3:
                return "TERCER";
            case 4:
                return "CUARTO";
            case 5:
                return "QUINTO";
            case 6:
                return "SEXTO";
            default:
                return "N/A";
        }
    }

    /**
     * Reemplaza etiquetas en un p├írrafo de un documento Word.
     * Este m├⌐todo maneja correctamente los casos donde una etiqueta est├í dividida en m├║ltiples runs.
     */
    private void reemplazarEtiquetasEnParrafo(org.apache.poi.xwpf.usermodel.XWPFParagraph paragraph, String nombreMateria, String fechaAplicacion, String nombreMaestro, String parcial, String semestre) {
        if (paragraph.getRuns() == null || paragraph.getRuns().isEmpty()) {
            return;
        }

        // Concatenar todo el texto del p├írrafo
        StringBuilder textoCompleto = new StringBuilder();
        for (org.apache.poi.xwpf.usermodel.XWPFRun run : paragraph.getRuns()) {
            String texto = run.getText(0);
            if (texto != null) {
                textoCompleto.append(texto);
            }
        }

        String textoOriginal = textoCompleto.toString();

        // Verificar si hay etiquetas que reemplazar
        if (!textoOriginal.contains("${materia}") && !textoOriginal.contains("${fecha_aplicacion}") && !textoOriginal.contains("${nombre_maestro}") && !textoOriginal.contains("${parcial}") && !textoOriginal.contains("${SEMESTRE}")) {
            return;
        }

        // Realizar los reemplazos
        String textoReemplazado = textoOriginal;
        boolean huboReemplazo = false;

        if (textoReemplazado.contains("${materia}")) {
            textoReemplazado = textoReemplazado.replace("${materia}", nombreMateria);
            LOG.info("Reemplazada etiqueta ${{materia}} con: {}", nombreMateria);
            huboReemplazo = true;
        }

        if (textoReemplazado.contains("${fecha_aplicacion}")) {
            textoReemplazado = textoReemplazado.replace("${fecha_aplicacion}", fechaAplicacion);
            LOG.info("Reemplazada etiqueta ${{fecha_aplicacion}} con: {}", fechaAplicacion);
            huboReemplazo = true;
        }

        if (textoReemplazado.contains("${nombre_maestro}")) {
            textoReemplazado = textoReemplazado.replace("${nombre_maestro}", nombreMaestro);
            LOG.info("Reemplazada etiqueta ${{nombre_maestro}} con: {}", nombreMaestro);
            huboReemplazo = true;
        }

        if (textoReemplazado.contains("${parcial}")) {
            textoReemplazado = textoReemplazado.replace("${parcial}", parcial);
            LOG.info("Reemplazada etiqueta ${{parcial}} con: {}", parcial);
            huboReemplazo = true;
        }

        if (textoReemplazado.contains("${SEMESTRE}")) {
            textoReemplazado = textoReemplazado.replace("${SEMESTRE}", semestre);
            LOG.info("Reemplazada etiqueta ${{SEMESTRE}} con: {}", semestre);
            huboReemplazo = true;
        }

        // Si hubo reemplazo, actualizar el p├írrafo
        if (huboReemplazo) {
            // Guardar el formato del primer run
            org.apache.poi.xwpf.usermodel.XWPFRun primerRun = paragraph.getRuns().get(0);
            String fontFamily = primerRun.getFontFamily();
            int fontSize = primerRun.getFontSize();
            boolean bold = primerRun.isBold();
            boolean italic = primerRun.isItalic();

            // Eliminar todos los runs existentes
            int numRuns = paragraph.getRuns().size();
            for (int i = numRuns - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            // Crear un nuevo run con el texto reemplazado
            org.apache.poi.xwpf.usermodel.XWPFRun nuevoRun = paragraph.createRun();
            nuevoRun.setText(textoReemplazado);

            // Aplicar el formato guardado
            if (fontFamily != null) {
                nuevoRun.setFontFamily(fontFamily);
            }
            if (fontSize > 0) {
                nuevoRun.setFontSize(fontSize);
            }
            nuevoRun.setBold(bold);
            nuevoRun.setItalic(italic);
        }
    }

    /**
     * Capitaliza la primera letra de un texto y convierte el resto a min├║sculas.
     *
     * @param texto El texto a capitalizar
     * @return El texto con la primera letra en may├║scula y el resto en min├║sculas
     */
    private String capitalizarPrimeraLetra(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        // Convertir todo a minúsculas y capitalizar la primera letra
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    /**
     * Exporta el informe de concentrado a un archivo Excel
     */
    private void exportarInformeAExcel(TableView<java.util.Map<String, Object>> tabla, Grupo grupo, Materia materia, Integer parcial) {
        try {
            // Crear FileChooser para seleccionar dónde guardar
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportar Informe a Excel");

            // Nombre sugerido del archivo
            String nombreArchivo = String.format("informe_concentrado_%s_%s_parcial%d_%s.xlsx",
                    grupo.getId(),
                    materia.getNombre().replaceAll("[^a-zA-Z0-9]", "_"),
                    parcial,
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            fileChooser.setInitialFileName(nombreArchivo);

            // Filtro para archivos Excel
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx")
            );

            // Mostrar diálogo para seleccionar ubicación
            File file = fileChooser.showSaveDialog(mainContent != null ? mainContent.getScene().getWindow() : null);

            if (file == null) {
                // Usuario canceló la operación
                return;
            }

            // Crear workbook de Excel
            org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Informe Concentrado");

            // Estilos
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            org.apache.poi.ss.usermodel.CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            // Fila de título
            org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("INFORME DE CONCENTRADO DE CALIFICACIONES");
            org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            titleCell.setCellStyle(titleStyle);

            // Información del reporte
            int rowNum = 2;
            org.apache.poi.ss.usermodel.Row infoRow1 = sheet.createRow(rowNum++);
            infoRow1.createCell(0).setCellValue("Materia:");
            infoRow1.createCell(1).setCellValue(materia.getNombre());

            org.apache.poi.ss.usermodel.Row infoRow2 = sheet.createRow(rowNum++);
            infoRow2.createCell(0).setCellValue("Grupo:");
            infoRow2.createCell(1).setCellValue(String.valueOf(grupo.getId()));

            org.apache.poi.ss.usermodel.Row infoRow3 = sheet.createRow(rowNum++);
            infoRow3.createCell(0).setCellValue("Parcial:");
            infoRow3.createCell(1).setCellValue(String.valueOf(parcial));

            org.apache.poi.ss.usermodel.Row infoRow4 = sheet.createRow(rowNum++);
            infoRow4.createCell(0).setCellValue("Fecha:");
            infoRow4.createCell(1).setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            rowNum++; // Fila en blanco

            // Crear fila de encabezados
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(rowNum++);
            ObservableList<TableColumn<java.util.Map<String, Object>, ?>> columns = tabla.getColumns();

            int colNum = 0;
            for (TableColumn<java.util.Map<String, Object>, ?> column : columns) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(colNum++);
                cell.setCellValue(column.getText());
                cell.setCellStyle(headerStyle);

                // Si la columna tiene subcolumnas, agregarlas también
                if (!column.getColumns().isEmpty()) {
                    for (TableColumn<?, ?> subColumn : column.getColumns()) {
                        cell = headerRow.createCell(colNum++);
                        cell.setCellValue(column.getText() + " - " + subColumn.getText());
                        cell.setCellStyle(headerStyle);
                    }
                    colNum--; // Ajustar porque ya contamos las subcolumnas
                }
            }

            // Llenar datos
            for (java.util.Map<String, Object> rowData : tabla.getItems()) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);

                colNum = 0;
                for (TableColumn<java.util.Map<String, Object>, ?> column : columns) {
                    if (column.getColumns().isEmpty()) {
                        // Columna simple
                        org.apache.poi.ss.usermodel.Cell cell = row.createCell(colNum++);
                        Object value = column.getCellData(rowData);

                        if (value != null) {
                            if (value instanceof Number) {
                                cell.setCellValue(((Number) value).doubleValue());
                            } else {
                                cell.setCellValue(value.toString());
                            }
                        }
                        cell.setCellStyle(dataStyle);
                    } else {
                        // Columna con subcolumnas
                        for (TableColumn<?, ?> subColumn : column.getColumns()) {
                            org.apache.poi.ss.usermodel.Cell cell = row.createCell(colNum++);
                            @SuppressWarnings("unchecked")
                            TableColumn<java.util.Map<String, Object>, ?> typedSubColumn =
                                (TableColumn<java.util.Map<String, Object>, ?>) subColumn;
                            Object value = typedSubColumn.getCellData(rowData);

                            if (value != null) {
                                if (value instanceof Number) {
                                    cell.setCellValue(((Number) value).doubleValue());
                                } else {
                                    cell.setCellValue(value.toString());
                                }
                            }
                            cell.setCellStyle(dataStyle);
                        }
                    }
                }
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < colNum; i++) {
                sheet.autoSizeColumn(i);
            }

            // Guardar el archivo
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
            workbook.close();

            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportación Exitosa");
            alert.setHeaderText(null);
            alert.setContentText("El informe se ha exportado correctamente a:\n" + file.getAbsolutePath());

            // Agregar botón para abrir el archivo
            ButtonType btnAbrir = new ButtonType("Abrir archivo");
            ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(btnAbrir, btnCerrar);

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == btnAbrir) {
                // Intentar abrir el archivo con la aplicación predeterminada
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        LOG.error("Error al abrir archivo", e);
                        mostrarAlerta("Error", "No se pudo abrir el archivo automáticamente", Alert.AlertType.WARNING);
                    }
                }
            }

        } catch (Exception e) {
            LOG.error("Error al exportar a Excel", e);
            mostrarAlerta("Error", "No se pudo exportar a Excel: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
