package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.*;
import com.alumnos.domain.port.in.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.geometry.Pos;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión del concentrado de calificaciones
 * Responsabilidad: Manejar la vista y operaciones CRUD del concentrado de calificaciones
 */
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
    private final ConfiguracionServicePort configuracionService;
    private final GrupoMateriaServicePort grupoMateriaService;

    private BorderPane mainContent;

    // Variables para almacenar la última selección y detectar cambios
    private Long ultimoGrupoId;
    private Long ultimaMateriaId;
    private Integer ultimoParcial;

    public ConcentradoController(CalificacionConcentradoServicePort calificacionConcentradoService,
                                 AlumnoServicePort alumnoService,
                                 AgregadoServicePort agregadoService,
                                 CriterioServicePort criterioService,
                                 GrupoServicePort grupoService,
                                 MateriaServicePort materiaService,
                                 ExamenServicePort examenService,
                                 AlumnoExamenServicePort alumnoExamenService,
                                 ConfiguracionServicePort configuracionService,
                                 GrupoMateriaServicePort grupoMateriaService) {
        this.calificacionConcentradoService = calificacionConcentradoService;
        this.alumnoService = alumnoService;
        this.agregadoService = agregadoService;
        this.criterioService = criterioService;
        this.grupoService = grupoService;
        this.materiaService = materiaService;
        this.examenService = examenService;
        this.alumnoExamenService = alumnoExamenService;
        this.configuracionService = configuracionService;
        this.grupoMateriaService = grupoMateriaService;
    }

    public void setMainContent(BorderPane mainContent) {
        this.mainContent = mainContent;
    }

    public VBox crearVista() {
        VBox vista = new VBox(10);
        vista.getChildren().addAll(
            crearFormulario(),
            crearFiltros(),
            crearTabla()
        );
        return vista;
    }

    public VBox crearVistaConcentrado() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        vista.setMaxHeight(Double.MAX_VALUE);
        vista.setMaxWidth(Double.MAX_VALUE);

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
            cargarGrupos(cmbGrupo);
            grupoContainer.getChildren().addAll(lblGrupo, cmbGrupo);

            // ComboBox para seleccionar materia
            VBox materiaContainer = new VBox(5);
            Label lblMateria = new Label("Materia: *");
            lblMateria.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            ComboBox<Materia> cmbMateria = new ComboBox<>();
            cmbMateria.setPrefWidth(250);
            cmbMateria.setPromptText("Seleccionar...");
            cmbMateria.setDisable(true);
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

            // Botón Buscar
            VBox buscarContainer = new VBox(5);
            Label lblEspacio = new Label(" ");
            Button btnBuscar = new Button("🔍 Buscar");
            btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand; -fx-font-weight: bold;");
            buscarContainer.getChildren().addAll(lblEspacio, btnBuscar);

            filtrosBox.getChildren().addAll(grupoContainer, materiaContainer, parcialContainer, buscarContainer);

            // Evento: Al seleccionar grupo, cargar materias
            cmbGrupo.setOnAction(e -> {
                if (cmbGrupo.getValue() != null) {
                    cargarMateriasPorGrupo(cmbMateria, cmbGrupo.getValue());
                    cmbMateria.setDisable(false);
                } else {
                    cmbMateria.getItems().clear();
                    cmbMateria.setDisable(true);
                }
            });

            filtrosPanel.getChildren().addAll(lblFiltros, filtrosBox);

            // Panel para la tabla
            VBox tablaPanel = new VBox(15);
            tablaPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            tablaPanel.setMaxHeight(Double.MAX_VALUE);
            tablaPanel.setMaxWidth(Double.MAX_VALUE);
            javafx.scene.layout.VBox.setVgrow(tablaPanel, javafx.scene.layout.Priority.ALWAYS);

            Label lblTabla = new Label("Tabla de Calificaciones");
            lblTabla.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

            TableView<Map<String, Object>> tblCalificaciones = new TableView<>();
            tblCalificaciones.setEditable(true);
            tblCalificaciones.setMaxHeight(Double.MAX_VALUE);
            tblCalificaciones.setMaxWidth(Double.MAX_VALUE);
            javafx.scene.layout.VBox.setVgrow(tblCalificaciones, javafx.scene.layout.Priority.ALWAYS);

            // Botón para generar tabla
            btnBuscar.setOnAction(e -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAdvertencia("Debe seleccionar Grupo, Materia y Parcial");
                    return;
                }
                // Registrar la selección actual antes de generar la tabla
                ultimoGrupoId = cmbGrupo.getValue().getId();
                ultimaMateriaId = cmbMateria.getValue().getId();
                ultimoParcial = cmbParcial.getValue();

                generarTablaCalificaciones(tblCalificaciones, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
            });

            // Panel de botones
            HBox botonesBox = new HBox(10);
            botonesBox.setAlignment(Pos.CENTER_RIGHT);

            // Botón para generar archivo Word
            Button btnGenerarArchivo = new Button("📄 Generar archivo");
            btnGenerarArchivo.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand; -fx-font-weight: bold;");
            btnGenerarArchivo.setOnAction(e -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAdvertencia("Debe seleccionar Grupo, Materia y Parcial");
                    return;
                }
                generarArchivoConcentrado(tblCalificaciones, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
            });

            // Botón para guardar cambios
            Button btnGuardarCambios = new Button("💾 Guardar Cambios");
            btnGuardarCambios.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand; -fx-font-weight: bold;");
            btnGuardarCambios.setOnAction(e -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAdvertencia("Debe seleccionar Grupo, Materia y Parcial");
                    return;
                }
                guardarCalificacionesDesdeTabla(tblCalificaciones, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
            });

            botonesBox.getChildren().addAll(btnGenerarArchivo, btnGuardarCambios);

            tablaPanel.getChildren().addAll(lblTabla, tblCalificaciones, botonesBox);

            vista.getChildren().addAll(lblTitulo, filtrosPanel, tablaPanel);

            // 🔄 Agregar listener de foco para recargar la tabla cuando se regresa a esta vista
            vista.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    LOG.debug("Vista 'Concentrado de Calificaciones' obtuvo el foco - verificando si hay cambios...");

                    // Si hay una selección válida, verificar si ha cambiado y recargar si es necesario
                    if (cmbGrupo.getValue() != null && cmbMateria.getValue() != null && cmbParcial.getValue() != null) {
                        Long grupoActual = cmbGrupo.getValue().getId();
                        Long materiaActual = cmbMateria.getValue().getId();
                        Integer parcialActual = cmbParcial.getValue();

                        // Comparar con la última selección
                        boolean huboChangios = !grupoActual.equals(ultimoGrupoId) ||
                                            !materiaActual.equals(ultimaMateriaId) ||
                                            !parcialActual.equals(ultimoParcial);

                        if (huboChangios) {
                            LOG.info("Se detectó cambio en la selección - recargando tabla...");
                            LOG.debug("Anterior: Grupo={}, Materia={}, Parcial={}", ultimoGrupoId, ultimaMateriaId, ultimoParcial);
                            LOG.debug("Actual: Grupo={}, Materia={}, Parcial={}", grupoActual, materiaActual, parcialActual);

                            generarTablaCalificaciones(tblCalificaciones, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());

                            // Actualizar la última selección
                            ultimoGrupoId = grupoActual;
                            ultimaMateriaId = materiaActual;
                            ultimoParcial = parcialActual;
                        } else {
                            LOG.debug("Sin cambios en la selección - no se recarga la tabla");
                        }
                    }
                }
            });

        } catch (Exception e) {
            manejarExcepcion("crear vista de concentrado de calificaciones", e);
        }

        return vista;
    }

    private void cargarMateriasPorGrupo(ComboBox<Materia> combo, Grupo grupo) {
        try {
            LOG.info("Cargando materias para grupo: {}", grupo.getId());

            // Obtener asignaciones del grupo y luego las materias
            List<Materia> materias = materiaService.obtenerTodasLasMaterias().stream()
                .filter(m -> {
                    // Aquí podrías filtrar por las materias asignadas al grupo si tienes esa relación
                    return true; // Por ahora mostrar todas
                })
                .collect(Collectors.toList());

            LOG.info("Total de materias disponibles: {}", materias.size());
            materias.forEach(m -> LOG.debug("  - {}: {}", m.getId(), m.getNombre()));

            combo.setItems(FXCollections.observableArrayList(materias));
        } catch (Exception e) {
            LOG.error("Error al cargar materias: ", e);
            manejarExcepcion("cargar materias por grupo", e);
        }
    }

    private void generarTablaCalificaciones(TableView<Map<String, Object>> tabla, Grupo grupo, Materia materia, Integer parcial) {
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
                .collect(Collectors.toList());

            if (alumnos.isEmpty()) {
                mostrarInformacion("No hay alumnos en este grupo");
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
                .collect(Collectors.toList());

            if (criterios.isEmpty()) {
                mostrarInformacion("No hay criterios para esta materia y parcial");
                return;
            }

            // Columna #
            TableColumn<Map<String, Object>, Integer> colNumero = new TableColumn<>("#");
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
            TableColumn<Map<String, Object>, String> colNombre = new TableColumn<>("Nombre Completo");
            colNombre.setPrefWidth(250);
            colNombre.setMinWidth(250);
            colNombre.setMaxWidth(250);
            colNombre.setResizable(false);
            colNombre.setCellValueFactory(cellData -> {
                String nombre = (String) cellData.getValue().get("nombreCompleto");
                return new javafx.beans.property.SimpleStringProperty(nombre);
            });
            tabla.getColumns().add(colNombre);

            // Lista para recopilar información de todos los agregados de todos los criterios
            List<Map<String, Object>> criteriosInfo = new java.util.ArrayList<>();

            // Obtener total de puntos del examen si existe (para el recálculo)
            Optional<Examen> examenOptTemp = examenService.obtenerExamenPorGrupoMateriaParcial(
                grupo.getId(), materia.getId(), parcial);
            final Integer totalPuntosExamenFinal = examenOptTemp.map(Examen::getTotalPuntosExamen).orElse(null);

            // Crear columnas dinámicamente por criterio
            for (Criterio criterio : criterios) {
                // Obtener agregados del criterio
                List<Agregado> agregados = agregadoService.obtenerAgregadosPorCriterio(criterio.getId()).stream()
                    .sorted((a1, a2) -> {
                        if (a1.getOrden() == null && a2.getOrden() == null) return 0;
                        if (a1.getOrden() == null) return 1;
                        if (a2.getOrden() == null) return -1;
                        return Integer.compare(a1.getOrden(), a2.getOrden());
                    })
                    .collect(Collectors.toList());

                if (!agregados.isEmpty()) {
                    // Crear columna padre para el criterio
                    TableColumn<Map<String, Object>, String> colCriterio = new TableColumn<>(
                        criterio.getNombre() + " (" + criterio.getPuntuacionMaxima() + " pts)"
                    );
                    colCriterio.setResizable(false);

                    final Double puntuacionMaximaCriterio = criterio.getPuntuacionMaxima();
                    boolean esCheck = "Check".equalsIgnoreCase(criterio.getTipoEvaluacion());

                    // Crear columnas hijas para cada agregado
                    for (Agregado agregado : agregados) {
                        if (esCheck) {
                            // Columna con CheckBox para tipo Check
                            TableColumn<Map<String, Object>, Boolean> colAgregadoCheck = new TableColumn<>();
                            colAgregadoCheck.setGraphic(crearHeaderConBotonDescripcion(agregado));
                            colAgregadoCheck.setPrefWidth(130);
                            colAgregadoCheck.setMinWidth(130);
                            colAgregadoCheck.setMaxWidth(130);
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

                            colAgregadoCheck.setCellFactory(col -> new TableCell<Map<String, Object>, Boolean>() {
                                private final CheckBox checkBox = new CheckBox();
                                private boolean isUpdating = false;

                                {
                                    checkBox.setStyle("-fx-alignment: CENTER;");
                                    checkBox.setOnAction(event -> {
                                        if (!isUpdating && getTableRow() != null && getTableRow().getItem() != null) {
                                            Map<String, Object> fila = getTableRow().getItem();
                                            fila.put("agregado_" + agregado.getId(), checkBox.isSelected());

                                            // ⚡ Recalcular puntosParcial y calificacionParcial
                                            recalcularPuntosParcial(fila, totalPuntosExamenFinal, criteriosInfo);

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
                            // Columna con TextField para tipo Puntuacion
                            TableColumn<Map<String, Object>, String> colAgregadoPuntos = new TableColumn<>();
                            colAgregadoPuntos.setGraphic(crearHeaderConBotonDescripcion(agregado));
                            colAgregadoPuntos.setPrefWidth(130);
                            colAgregadoPuntos.setMinWidth(130);
                            colAgregadoPuntos.setMaxWidth(130);
                            colAgregadoPuntos.setResizable(false);
                            colAgregadoPuntos.setEditable(true);

                            colAgregadoPuntos.setCellValueFactory(cellData -> {
                                Object valor = cellData.getValue().get("agregado_" + agregado.getId());
                                return new javafx.beans.property.SimpleStringProperty(valor != null ? valor.toString() : "");
                            });

                            colAgregadoPuntos.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
                                private final TextField textField = new TextField();

                                {
                                    textField.setStyle("-fx-alignment: CENTER; -fx-pref-width: 90px;");
                                    textField.setMaxWidth(90);

                                    textField.textProperty().addListener((obs, oldVal, newVal) -> {
                                        if (newVal != null && !newVal.isEmpty()) {
                                            if (!newVal.matches("\\d{0,2}(\\.\\d{0,2})?")) {
                                                textField.setText(oldVal);
                                                return;
                                            }
                                            try {
                                                double valor = Double.parseDouble(newVal);
                                                if (valor > 99) {
                                                    textField.setText(oldVal);
                                                }
                                            } catch (NumberFormatException e) {
                                                // Ignorar
                                            }
                                        }
                                    });

                                    textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                                        if (!newVal && getTableRow() != null && getTableRow().getItem() != null) {
                                            String valorTexto = textField.getText();
                                            Map<String, Object> fila = getTableRow().getItem();

                                            // Validar que la suma de agregados no supere el máximo del criterio
                                            if (!valorTexto.isEmpty() && !validarSumaAgregadosEnTabla(
                                                    fila, agregado.getId(), valorTexto, criterio, agregados)) {
                                                // Restaurar el valor anterior
                                                Object valorAnterior = fila.get("agregado_" + agregado.getId());
                                                textField.setText(valorAnterior != null ? valorAnterior.toString() : "");
                                                return;
                                            }

                                            fila.put("agregado_" + agregado.getId(), valorTexto);

                                            // ⚡ Recalcular puntosParcial y calificacionParcial
                                            recalcularPuntosParcial(fila, totalPuntosExamenFinal, criteriosInfo);

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

                    // Agregar columna Acumulado
                    TableColumn<Map<String, Object>, String> colAcumulado = new TableColumn<>("Acumulado");
                    colAcumulado.setPrefWidth(120);
                    colAcumulado.setMinWidth(120);
                    colAcumulado.setMaxWidth(120);
                    colAcumulado.setResizable(false);

                    final List<Long> agregadoIds = agregados.stream().map(Agregado::getId).collect(Collectors.toList());
                    final boolean esCheckFinal = esCheck;

                    colAcumulado.setCellValueFactory(cellData -> {
                        Map<String, Object> fila = cellData.getValue();
                        double puntosObtenidos = 0.0;

                        for (Long agregadoId : agregadoIds) {
                            Object valor = fila.get("agregado_" + agregadoId);

                            if (esCheckFinal) {
                                if (valor instanceof Boolean && (Boolean) valor) {
                                    puntosObtenidos += puntuacionMaximaCriterio / agregadoIds.size();
                                }
                            } else {
                                if (valor instanceof String && !((String) valor).isEmpty()) {
                                    try {
                                        puntosObtenidos += Double.parseDouble((String) valor);
                                    } catch (NumberFormatException e) {
                                        // Ignorar
                                    }
                                }
                            }
                        }

                        return new javafx.beans.property.SimpleStringProperty(
                            String.format("%.1f / %.1f", puntosObtenidos, puntuacionMaximaCriterio)
                        );
                    });

                    colAcumulado.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
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

                    // Guardar información del criterio
                    Map<String, Object> criterioInfo = new HashMap<>();
                    criterioInfo.put("criterioId", criterio.getId());
                    criterioInfo.put("agregadoIds", agregadoIds);
                    criterioInfo.put("esCheck", esCheck);
                    criterioInfo.put("puntuacionMaxima", criterio.getPuntuacionMaxima());
                    criteriosInfo.add(criterioInfo);
                }
            }

            // Agregar columna Portafolio
            if (!criteriosInfo.isEmpty()) {
                TableColumn<Map<String, Object>, String> colPortafolio = new TableColumn<>("Portafolio");
                colPortafolio.setPrefWidth(120);
                colPortafolio.setMinWidth(120);
                colPortafolio.setMaxWidth(120);
                colPortafolio.setResizable(false);

                colPortafolio.setCellValueFactory(cellData -> {
                    Map<String, Object> fila = cellData.getValue();
                    double totalPortafolio = 0.0;

                    for (Map<String, Object> criterioInfo : criteriosInfo) {
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
                                }
                            } else {
                                if (valor instanceof String && !((String) valor).isEmpty()) {
                                    try {
                                        puntosObtenidosCriterio += Double.parseDouble((String) valor);
                                    } catch (NumberFormatException e) {
                                        // Ignorar
                                    }
                                }
                            }
                        }

                        totalPortafolio += puntosObtenidosCriterio;
                    }

                    return new javafx.beans.property.SimpleStringProperty(String.format("%.1f", totalPortafolio));
                });

                colPortafolio.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
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

            // Agregar columnas de Examen si existe
            Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
                grupo.getId(), materia.getId(), parcial);

            if (examenOpt.isPresent()) {
                Examen examen = examenOpt.get();
                final Integer totalPuntosExamen = examen.getTotalPuntosExamen();

                // Columna Puntos Examen (EDITABLE)
                String headerPuntosExamen = totalPuntosExamen != null
                    ? "Puntos Examen (" + totalPuntosExamen + " pts)"
                    : "Puntos Examen";
                TableColumn<Map<String, Object>, String> colPuntosExamen = new TableColumn<>(headerPuntosExamen);
                colPuntosExamen.setPrefWidth(100);
                colPuntosExamen.setMinWidth(100);
                colPuntosExamen.setMaxWidth(100);
                colPuntosExamen.setResizable(false);
                colPuntosExamen.setEditable(true);

                colPuntosExamen.setCellValueFactory(cellData -> {
                    Object valor = cellData.getValue().get("aciertosExamen");
                    return new javafx.beans.property.SimpleStringProperty(
                        valor != null ? String.valueOf(valor) : ""
                    );
                });

                colPuntosExamen.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
                    private final TextField textField = new TextField();

                    {
                        textField.setStyle("-fx-alignment: CENTER; -fx-pref-width: 90px;");
                        textField.setMaxWidth(90);

                        // Validar que solo sean números enteros
                        textField.textProperty().addListener((obs, oldVal, newVal) -> {
                            if (newVal != null && !newVal.isEmpty()) {
                                // Solo permitir números enteros
                                if (!newVal.matches("\\d{0,3}")) {
                                    textField.setText(oldVal);
                                    return;
                                }
                                // Validar que no exceda el total de puntos del examen
                                if (totalPuntosExamen != null) {
                                    try {
                                        int valor = Integer.parseInt(newVal);
                                        if (valor > totalPuntosExamen) {
                                            textField.setText(oldVal);
                                            return;
                                        }
                                    } catch (NumberFormatException e) {
                                        // Ignorar si no es un número válido aún
                                    }
                                }
                            }
                        });

                        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                            if (!newVal && getTableRow() != null && getTableRow().getItem() != null) {
                                // Al perder el foco, guardar el valor
                                String valorTexto = textField.getText();
                                java.util.Map<String, Object> fila = getTableRow().getItem();

                                if (valorTexto != null && !valorTexto.isEmpty()) {
                                    try {
                                        int puntosExamen = Integer.parseInt(valorTexto);
                                        fila.put("aciertosExamen", puntosExamen);
                                    } catch (NumberFormatException e) {
                                        fila.put("aciertosExamen", null);
                                    }
                                } else {
                                    fila.put("aciertosExamen", null);
                                }

                                // ⚡ Recalcular puntosParcial y calificacionParcial
                                recalcularPuntosParcial(fila, totalPuntosExamen, criteriosInfo);

                                // Refrescar la tabla para actualizar porcentaje y calificación
                                tabla.refresh();
                            }
                        });

                        textField.setOnAction(event -> {
                            // Al presionar Enter, mover el foco para guardar el valor
                            if (getTableRow() != null && getTableRow().getItem() != null) {
                                tabla.requestFocus();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            textField.setText(item != null && !item.isEmpty() ? item : "");
                            setGraphic(textField);
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                });

                tabla.getColumns().add(colPuntosExamen);

                // Columna % Examen (calculado dinámicamente)
                TableColumn<Map<String, Object>, String> colPorcentajeExamen = new TableColumn<>("% Examen");
                colPorcentajeExamen.setPrefWidth(100);
                colPorcentajeExamen.setMinWidth(100);
                colPorcentajeExamen.setMaxWidth(100);
                colPorcentajeExamen.setResizable(false);

                colPorcentajeExamen.setCellValueFactory(cellData -> {
                    java.util.Map<String, Object> fila = cellData.getValue();
                    Object puntosExamenObj = fila.get("aciertosExamen");

                    if (puntosExamenObj != null && totalPuntosExamen != null && totalPuntosExamen > 0) {
                        try {
                            int puntosExamen = 0;
                            if (puntosExamenObj instanceof Number) {
                                puntosExamen = ((Number) puntosExamenObj).intValue();
                            } else if (puntosExamenObj instanceof String && !((String) puntosExamenObj).isEmpty()) {
                                puntosExamen = Integer.parseInt((String) puntosExamenObj);
                            }
                            double porcentaje = (puntosExamen * 100.0) / totalPuntosExamen;
                            return new javafx.beans.property.SimpleStringProperty(String.format("%.1f%%", porcentaje));
                        } catch (NumberFormatException e) {
                            return new javafx.beans.property.SimpleStringProperty("-");
                        }
                    }
                    return new javafx.beans.property.SimpleStringProperty("-");
                });

                tabla.getColumns().add(colPorcentajeExamen);

                // Columna Calificación Examen (calculada dinámicamente)
                TableColumn<Map<String, Object>, String> colCalificacionExamen = new TableColumn<>("Calif. Examen");
                colCalificacionExamen.setPrefWidth(120);
                colCalificacionExamen.setMinWidth(120);
                colCalificacionExamen.setMaxWidth(120);
                colCalificacionExamen.setResizable(false);

                colCalificacionExamen.setCellValueFactory(cellData -> {
                    java.util.Map<String, Object> fila = cellData.getValue();
                    Object puntosExamenObj = fila.get("aciertosExamen");

                    if (puntosExamenObj != null && totalPuntosExamen != null && totalPuntosExamen > 0) {
                        try {
                            int puntosExamen = 0;
                            if (puntosExamenObj instanceof Number) {
                                puntosExamen = ((Number) puntosExamenObj).intValue();
                            } else if (puntosExamenObj instanceof String && !((String) puntosExamenObj).isEmpty()) {
                                puntosExamen = Integer.parseInt((String) puntosExamenObj);
                            }
                            double porcentaje = (puntosExamen * 100.0) / totalPuntosExamen;
                            double calificacion = (porcentaje * 10.0) / 100.0;
                            return new javafx.beans.property.SimpleStringProperty(String.format("%.1f", calificacion));
                        } catch (NumberFormatException e) {
                            return new javafx.beans.property.SimpleStringProperty("-");
                        }
                    }
                    return new javafx.beans.property.SimpleStringProperty("-");
                });

                tabla.getColumns().add(colCalificacionExamen);
            }

            // Columna Puntos Parcial
            TableColumn<Map<String, Object>, String> colPuntosParcial = new TableColumn<>("Puntos Parcial");
            colPuntosParcial.setPrefWidth(120);
            colPuntosParcial.setMinWidth(120);
            colPuntosParcial.setMaxWidth(120);
            colPuntosParcial.setResizable(false);

            colPuntosParcial.setCellValueFactory(cellData -> {
                Object valor = cellData.getValue().get("puntosParcial");
                return new javafx.beans.property.SimpleStringProperty(
                    valor != null ? String.format("%.1f", (Double) valor) : "0.0"
                );
            });

            tabla.getColumns().add(colPuntosParcial);

            // Columna Calificación Parcial
            TableColumn<Map<String, Object>, String> colCalificacionParcial = new TableColumn<>("Calificación Parcial");
            colCalificacionParcial.setPrefWidth(150);
            colCalificacionParcial.setMinWidth(150);
            colCalificacionParcial.setMaxWidth(150);
            colCalificacionParcial.setResizable(false);

            colCalificacionParcial.setCellValueFactory(cellData -> {
                Object valor = cellData.getValue().get("calificacionParcial");
                return new javafx.beans.property.SimpleStringProperty(
                    valor != null ? String.format("%.1f", (Double) valor) : "0.0"
                );
            });

            colCalificacionParcial.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #c8e6c9; -fx-font-size: 14px;");
                    }
                }
            });

            tabla.getColumns().add(colCalificacionParcial);

            // Cargar datos
            ObservableList<Map<String, Object>> datos = FXCollections.observableArrayList();
            int numeroFila = 1;

            for (Alumno alumno : alumnos) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("numero", numeroFila++);
                fila.put("alumnoId", alumno.getId());
                fila.put("nombreCompleto", alumno.getApellidoPaterno() + " " + alumno.getApellidoMaterno() + " " + alumno.getNombre());

                // Cargar calificaciones existentes
                for (Map<String, Object> criterioInfo : criteriosInfo) {
                    @SuppressWarnings("unchecked")
                    List<Long> agregadoIds = (List<Long>) criterioInfo.get("agregadoIds");

                    for (Long agregadoId : agregadoIds) {
                        // Buscar calificación existente
                        List<CalificacionConcentrado> calificaciones = calificacionConcentradoService.obtenerTodasLasCalificaciones();
                        Optional<CalificacionConcentrado> calOpt = calificaciones.stream()
                            .filter(c -> c.getAlumnoId().equals(alumno.getId())
                                && c.getAgregadoId().equals(agregadoId)
                                && c.getParcial().equals(parcial))
                            .findFirst();

                        if (calOpt.isPresent()) {
                            CalificacionConcentrado cal = calOpt.get();
                            boolean esCheck = (Boolean) criterioInfo.get("esCheck");
                            if (esCheck) {
                                fila.put("agregado_" + agregadoId, cal.getPuntuacion() > 0);
                            } else {
                                fila.put("agregado_" + agregadoId, String.valueOf(cal.getPuntuacion()));
                            }
                        } else {
                            fila.put("agregado_" + agregadoId, "");
                        }
                    }
                }

                // Cargar datos de examen si existe
                if (examenOpt.isPresent()) {
                    Examen examen = examenOpt.get();
                    List<AlumnoExamen> alumnoExamenes = alumnoExamenService.obtenerTodosLosAlumnoExamen();
                    Optional<AlumnoExamen> alumnoExamenOpt = alumnoExamenes.stream()
                        .filter(ae -> ae.getAlumnoId().equals(alumno.getId()) && ae.getExamenId().equals(examen.getId()))
                        .findFirst();

                    if (alumnoExamenOpt.isPresent()) {
                        AlumnoExamen alumnoExamen = alumnoExamenOpt.get();
                        fila.put("aciertosExamen", alumnoExamen.getPuntosExamen());
                        fila.put("porcentajeExamen", alumnoExamen.getPorcentaje());
                        fila.put("calificacionExamen", alumnoExamen.getCalificacion());
                    }
                }

                // Calcular puntos parcial y calificación parcial
                double totalPortafolio = 0.0;
                for (Map<String, Object> criterioInfo : criteriosInfo) {
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
                            }
                        } else {
                            if (valor instanceof String && !((String) valor).isEmpty()) {
                                try {
                                    puntosObtenidosCriterio += Double.parseDouble((String) valor);
                                } catch (NumberFormatException e) {
                                    // Ignorar
                                }
                            }
                        }
                    }

                    totalPortafolio += puntosObtenidosCriterio;
                }

                double puntosExamen = 0.0;
                Object aciertosExamenObj = fila.get("aciertosExamen");
                if (aciertosExamenObj != null) {
                    try {
                        if (aciertosExamenObj instanceof Number) {
                            puntosExamen = ((Number) aciertosExamenObj).doubleValue();
                        } else if (aciertosExamenObj instanceof String && !((String) aciertosExamenObj).isEmpty()) {
                            puntosExamen = Double.parseDouble((String) aciertosExamenObj);
                        } else if (aciertosExamenObj instanceof Integer) {
                            puntosExamen = ((Integer) aciertosExamenObj).doubleValue();
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar, dejar en 0.0
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
            manejarExcepcion("generar tabla de calificaciones", e);
        }
    }

    private void guardarCalificacionesDesdeTabla(TableView<Map<String, Object>> tabla, Grupo grupo, Materia materia, Integer parcial) {
        try {
            ObservableList<Map<String, Object>> datos = tabla.getItems();

            for (Map<String, Object> fila : datos) {
                Long alumnoId = (Long) fila.get("alumnoId");

                // Obtener criterios
                List<Criterio> criterios = criterioService.obtenerCriteriosPorMateria(materia.getId()).stream()
                    .filter(c -> parcial.equals(c.getParcial()))
                    .collect(Collectors.toList());

                for (Criterio criterio : criterios) {
                    List<Agregado> agregados = agregadoService.obtenerAgregadosPorCriterio(criterio.getId());
                    boolean esCheck = "Check".equalsIgnoreCase(criterio.getTipoEvaluacion());

                    for (Agregado agregado : agregados) {
                        Object valor = fila.get("agregado_" + agregado.getId());
                        double puntuacion = 0.0;

                        if (esCheck) {
                            if (valor instanceof Boolean && (Boolean) valor) {
                                puntuacion = criterio.getPuntuacionMaxima() / agregados.size();
                            }
                        } else {
                            if (valor instanceof String && !((String) valor).isEmpty()) {
                                try {
                                    puntuacion = Double.parseDouble((String) valor);
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            }
                        }

                        // Buscar si ya existe la calificación
                        List<CalificacionConcentrado> calificaciones = calificacionConcentradoService.obtenerTodasLasCalificaciones();
                        Optional<CalificacionConcentrado> calOpt = calificaciones.stream()
                            .filter(c -> c.getAlumnoId().equals(alumnoId)
                                && c.getAgregadoId().equals(agregado.getId())
                                && c.getParcial().equals(parcial))
                            .findFirst();

                        if (calOpt.isPresent()) {
                            // Actualizar
                            CalificacionConcentrado cal = calOpt.get();
                            cal.setPuntuacion(puntuacion);
                            calificacionConcentradoService.actualizarCalificacion(cal);
                        } else {
                            // Crear nueva
                            CalificacionConcentrado nuevaCal = CalificacionConcentrado.builder()
                                .alumnoId(alumnoId)
                                .agregadoId(agregado.getId())
                                .criterioId(criterio.getId())
                                .grupoId(grupo.getId())
                                .materiaId(materia.getId())
                                .parcial(parcial)
                                .puntuacion(puntuacion)
                                .build();
                            calificacionConcentradoService.crearCalificacion(nuevaCal);
                        }
                    }
                }

                // Guardar puntos del examen si existe
                Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
                    grupo.getId(), materia.getId(), parcial);

                if (examenOpt.isPresent()) {
                    Examen examen = examenOpt.get();
                    Object aciertosExamenObj = fila.get("aciertosExamen");

                    if (aciertosExamenObj != null) {
                        Integer puntosExamen = null;

                        // Convertir el valor a Integer
                        if (aciertosExamenObj instanceof Number) {
                            puntosExamen = ((Number) aciertosExamenObj).intValue();
                        } else if (aciertosExamenObj instanceof String && !((String) aciertosExamenObj).isEmpty()) {
                            try {
                                puntosExamen = Integer.parseInt((String) aciertosExamenObj);
                            } catch (NumberFormatException e) {
                                LOG.warn("Valor inválido para puntos examen: {}", aciertosExamenObj);
                            }
                        }

                        if (puntosExamen != null && examen.getTotalPuntosExamen() != null && examen.getTotalPuntosExamen() > 0) {
                            // Calcular porcentaje y calificación
                            double porcentaje = (puntosExamen * 100.0) / examen.getTotalPuntosExamen();
                            double calificacion = (porcentaje * 10.0) / 100.0;

                            // Buscar si ya existe un AlumnoExamen
                            Optional<AlumnoExamen> alumnoExamenOpt = alumnoExamenService
                                .obtenerAlumnoExamenPorAlumnoYExamen(alumnoId, examen.getId());

                            if (alumnoExamenOpt.isPresent()) {
                                // Actualizar
                                AlumnoExamen alumnoExamen = alumnoExamenOpt.get();
                                alumnoExamen.setPuntosExamen(puntosExamen);
                                alumnoExamen.setPorcentaje(porcentaje);
                                alumnoExamen.setCalificacion(calificacion);
                                alumnoExamenService.actualizarAlumnoExamen(alumnoExamen);
                                LOG.info("Actualizado AlumnoExamen: alumnoId={}, examenId={}, puntos={}",
                                    alumnoId, examen.getId(), puntosExamen);
                            } else {
                                // Crear nuevo
                                AlumnoExamen nuevoAlumnoExamen = AlumnoExamen.builder()
                                    .alumnoId(alumnoId)
                                    .examenId(examen.getId())
                                    .puntosExamen(puntosExamen)
                                    .porcentaje(porcentaje)
                                    .calificacion(calificacion)
                                    .build();
                                alumnoExamenService.crearAlumnoExamen(nuevoAlumnoExamen);
                                LOG.info("Creado AlumnoExamen: alumnoId={}, examenId={}, puntos={}",
                                    alumnoId, examen.getId(), puntosExamen);
                            }
                        }
                    }
                }
            }

            mostrarExito("Calificaciones guardadas correctamente");

        } catch (Exception e) {
            manejarExcepcion("guardar calificaciones", e);
        }
    }


    private VBox crearFormulario() {
        VBox formulario = new VBox(10);

        ComboBox<Alumno> cmbAlumno = new ComboBox<>();
        cmbAlumno.setPromptText("Seleccione un alumno");
        cargarAlumnos(cmbAlumno);

        ComboBox<Agregado> cmbAgregado = new ComboBox<>();
        cmbAgregado.setPromptText("Seleccione un agregado");
        cargarAgregados(cmbAgregado);

        ComboBox<Criterio> cmbCriterio = new ComboBox<>();
        cmbCriterio.setPromptText("Seleccione un criterio");
        cargarCriterios(cmbCriterio);

        ComboBox<Grupo> cmbGrupo = new ComboBox<>();
        cmbGrupo.setPromptText("Seleccione un grupo");
        cargarGrupos(cmbGrupo);

        ComboBox<Materia> cmbMateria = new ComboBox<>();
        cmbMateria.setPromptText("Seleccione una materia");
        cargarMaterias(cmbMateria);

        ComboBox<Integer> cmbParcial = new ComboBox<>();
        cmbParcial.setPromptText("Parcial");
        cmbParcial.setItems(FXCollections.observableArrayList(1, 2, 3));

        TextField txtPuntuacion = new TextField();
        txtPuntuacion.setPromptText("Puntuación");

        Button btnGuardar = new Button("Guardar");
        btnGuardar.setOnAction(e -> guardarCalificacion(cmbAlumno, cmbAgregado, cmbCriterio,
                                                         cmbGrupo, cmbMateria, cmbParcial, txtPuntuacion));

        formulario.getChildren().addAll(
            new Label("Alumno:"), cmbAlumno,
            new Label("Agregado:"), cmbAgregado,
            new Label("Criterio:"), cmbCriterio,
            new Label("Grupo:"), cmbGrupo,
            new Label("Materia:"), cmbMateria,
            new Label("Parcial:"), cmbParcial,
            new Label("Puntuación:"), txtPuntuacion,
            btnGuardar
        );
        return formulario;
    }

    private VBox crearFiltros() {
        VBox filtros = new VBox(10);

        ComboBox<Grupo> cmbFiltroGrupo = new ComboBox<>();
        cmbFiltroGrupo.setPromptText("Filtrar por grupo");
        cargarGrupos(cmbFiltroGrupo);

        ComboBox<Materia> cmbFiltroMateria = new ComboBox<>();
        cmbFiltroMateria.setPromptText("Filtrar por materia");
        cargarMaterias(cmbFiltroMateria);

        ComboBox<Integer> cmbFiltroParcial = new ComboBox<>();
        cmbFiltroParcial.setPromptText("Filtrar por parcial");
        cmbFiltroParcial.setItems(FXCollections.observableArrayList(1, 2, 3));

        Button btnFiltrar = new Button("Filtrar");
        Button btnLimpiar = new Button("Limpiar filtros");

        filtros.getChildren().addAll(
            new Label("Filtros:"),
            cmbFiltroGrupo,
            cmbFiltroMateria,
            cmbFiltroParcial,
            btnFiltrar,
            btnLimpiar
        );

        return filtros;
    }

    private TableView<CalificacionConcentrado> crearTabla() {
        TableView<CalificacionConcentrado> tabla = new TableView<>();
        tabla.setEditable(false);

        // Columna Alumno
        TableColumn<CalificacionConcentrado, String> colAlumno = new TableColumn<>("Alumno");
        colAlumno.setCellValueFactory(data -> {
            CalificacionConcentrado cal = data.getValue();
            if (cal.getAlumnoId() != null) {
                return alumnoService.obtenerAlumnoPorId(cal.getAlumnoId())
                    .map(a -> new javafx.beans.property.SimpleStringProperty(
                        a.getNombre() + " " + a.getApellidoPaterno()))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        // Columna Agregado
        TableColumn<CalificacionConcentrado, String> colAgregado = new TableColumn<>("Agregado");
        colAgregado.setCellValueFactory(data -> {
            CalificacionConcentrado cal = data.getValue();
            if (cal.getAgregadoId() != null) {
                return agregadoService.obtenerAgregadoPorId(cal.getAgregadoId())
                    .map(a -> new javafx.beans.property.SimpleStringProperty(a.getNombre()))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        // Columna Criterio
        TableColumn<CalificacionConcentrado, String> colCriterio = new TableColumn<>("Criterio");
        colCriterio.setCellValueFactory(data -> {
            CalificacionConcentrado cal = data.getValue();
            if (cal.getCriterioId() != null) {
                return criterioService.obtenerCriterioPorId(cal.getCriterioId())
                    .map(c -> new javafx.beans.property.SimpleStringProperty(c.getNombre()))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        // Columna Grupo
        TableColumn<CalificacionConcentrado, String> colGrupo = new TableColumn<>("Grupo");
        colGrupo.setCellValueFactory(data -> {
            CalificacionConcentrado cal = data.getValue();
            if (cal.getGrupoId() != null) {
                return grupoService.obtenerGrupoPorId(cal.getGrupoId())
                    .map(g -> new javafx.beans.property.SimpleStringProperty(String.valueOf(g.getId())))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        // Columna Materia
        TableColumn<CalificacionConcentrado, String> colMateria = new TableColumn<>("Materia");
        colMateria.setCellValueFactory(data -> {
            CalificacionConcentrado cal = data.getValue();
            if (cal.getMateriaId() != null) {
                return materiaService.obtenerMateriaPorId(cal.getMateriaId())
                    .map(m -> new javafx.beans.property.SimpleStringProperty(m.getNombre()))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        // Columna Parcial
        TableColumn<CalificacionConcentrado, String> colParcial = new TableColumn<>("Parcial");
        colParcial.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getParcial() != null ?
                String.valueOf(data.getValue().getParcial()) : "N/A"));

        // Columna Puntuación
        TableColumn<CalificacionConcentrado, String> colPuntuacion = new TableColumn<>("Puntuación");
        colPuntuacion.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getPuntuacion() != null ?
                String.valueOf(data.getValue().getPuntuacion()) : "N/A"));

        // Columna Acciones
        TableColumn<CalificacionConcentrado, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(col -> new TableCell<CalificacionConcentrado, Void>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                btnEliminar.setOnAction(e -> {
                    CalificacionConcentrado calificacion = getTableView().getItems().get(getIndex());
                    eliminarCalificacion(calificacion, getTableView());
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

        tabla.getColumns().addAll(colAlumno, colAgregado, colCriterio, colGrupo,
                                   colMateria, colParcial, colPuntuacion, colAcciones);

        // Cargar datos
        cargarDatos(tabla);

        return tabla;
    }


    private void guardarCalificacion(ComboBox<Alumno> cmbAlumno, ComboBox<Agregado> cmbAgregado,
                                     ComboBox<Criterio> cmbCriterio, ComboBox<Grupo> cmbGrupo,
                                     ComboBox<Materia> cmbMateria, ComboBox<Integer> cmbParcial,
                                     TextField txtPuntuacion) {
        try {
            if (!validarFormulario(cmbAlumno, cmbAgregado, cmbCriterio, cmbGrupo,
                                  cmbMateria, cmbParcial, txtPuntuacion)) return;

            CalificacionConcentrado calificacion = CalificacionConcentrado.builder()
                .alumnoId(cmbAlumno.getValue().getId())
                .agregadoId(cmbAgregado.getValue().getId())
                .criterioId(cmbCriterio.getValue().getId())
                .grupoId(cmbGrupo.getValue().getId())
                .materiaId(cmbMateria.getValue().getId())
                .parcial(cmbParcial.getValue())
                .puntuacion(Double.parseDouble(txtPuntuacion.getText()))
                .build();

            calificacionConcentradoService.crearCalificacion(calificacion);
            mostrarExito("Calificación guardada correctamente");
            limpiarFormulario(cmbAlumno, cmbAgregado, cmbCriterio, cmbGrupo, cmbMateria, cmbParcial, txtPuntuacion);
        } catch (NumberFormatException e) {
            mostrarError("La puntuación debe ser un valor numérico");
        } catch (Exception e) {
            manejarExcepcion("guardar calificación", e);
        }
    }

    private void eliminarCalificacion(CalificacionConcentrado calificacion, TableView<CalificacionConcentrado> tabla) {
        try {
            if (confirmarAccion("Confirmar eliminación", "¿Está seguro de eliminar esta calificación?")) {
                calificacionConcentradoService.eliminarCalificacion(calificacion.getId());
                mostrarExito("Calificación eliminada correctamente");
                cargarDatos(tabla);
            }
        } catch (Exception e) {
            manejarExcepcion("eliminar calificación", e);
        }
    }

    private boolean validarFormulario(ComboBox<Alumno> cmbAlumno, ComboBox<Agregado> cmbAgregado,
                                     ComboBox<Criterio> cmbCriterio, ComboBox<Grupo> cmbGrupo,
                                     ComboBox<Materia> cmbMateria, ComboBox<Integer> cmbParcial,
                                     TextField txtPuntuacion) {
        if (cmbAlumno.getValue() == null) {
            mostrarError("Debe seleccionar un alumno");
            return false;
        }
        if (cmbAgregado.getValue() == null) {
            mostrarError("Debe seleccionar un agregado");
            return false;
        }
        if (cmbCriterio.getValue() == null) {
            mostrarError("Debe seleccionar un criterio");
            return false;
        }
        if (cmbGrupo.getValue() == null) {
            mostrarError("Debe seleccionar un grupo");
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
        if (!validarCampoNoVacio(txtPuntuacion.getText(), "Puntuación")) {
            return false;
        }

        // Validar que la suma de agregados no supere el valor del criterio
        return validarSumaAgregadosNoPasaCriterio(cmbAlumno, cmbAgregado, cmbCriterio,
                                                  cmbGrupo, cmbMateria, cmbParcial, txtPuntuacion);
    }

    /**
     * Valida que la suma de las puntuaciones de los agregados tipo "Puntuacion"
     * no supere el valor máximo del criterio
     */
    private boolean validarSumaAgregadosNoPasaCriterio(ComboBox<Alumno> cmbAlumno, ComboBox<Agregado> cmbAgregado,
                                                       ComboBox<Criterio> cmbCriterio, ComboBox<Grupo> cmbGrupo,
                                                       ComboBox<Materia> cmbMateria, ComboBox<Integer> cmbParcial,
                                                       TextField txtPuntuacion) {
        try {
            Criterio criterio = cmbCriterio.getValue();

            // Solo validar para criterios de tipo "Puntuacion"
            if (!"Puntuacion".equalsIgnoreCase(criterio.getTipoEvaluacion())) {
                return true;
            }

            // Obtener la puntuación máxima del criterio
            Double puntuacionMaximaCriterio = criterio.getPuntuacionMaxima();
            if (puntuacionMaximaCriterio == null) {
                mostrarError("El criterio no tiene una puntuación máxima definida");
                return false;
            }

            // Parsear la puntuación actual a ingresar
            double puntuacionActual;
            try {
                puntuacionActual = Double.parseDouble(txtPuntuacion.getText().trim());
            } catch (NumberFormatException e) {
                mostrarError("La puntuación debe ser un valor numérico válido");
                return false;
            }

            // Obtener todos los agregados del criterio
            List<Agregado> agregadosCriterio = agregadoService.obtenerTodosLosAgregados().stream()
                .filter(a -> a.getCriterioId() != null && a.getCriterioId().equals(criterio.getId()))
                .toList();

            // Obtener todas las calificaciones existentes del alumno para este grupo, materia y parcial
            List<CalificacionConcentrado> calificacionesExistentes =
                calificacionConcentradoService.obtenerCalificacionesPorGrupoMateriaYParcial(
                    cmbGrupo.getValue().getId(),
                    cmbMateria.getValue().getId(),
                    cmbParcial.getValue()
                ).stream()
                .filter(cal -> cal.getAlumnoId().equals(cmbAlumno.getValue().getId()))
                .toList();

            // Sumar las puntuaciones de los agregados de este criterio (excluyendo el agregado actual si ya existe)
            double sumaAgregadosExistentes = 0.0;
            Long agregadoActualId = cmbAgregado.getValue().getId();

            for (Agregado agregado : agregadosCriterio) {
                // Buscar si existe una calificación para este agregado
                Optional<CalificacionConcentrado> calExistente = calificacionesExistentes.stream()
                    .filter(cal -> cal.getAgregadoId().equals(agregado.getId()))
                    .findFirst();

                if (calExistente.isPresent()) {
                    // Si es el agregado actual que estamos editando, no lo sumamos aún
                    if (!agregado.getId().equals(agregadoActualId)) {
                        sumaAgregadosExistentes += calExistente.get().getPuntuacion();
                    }
                }
            }

            // Calcular la suma total incluyendo la nueva puntuación
            double sumaTotal = sumaAgregadosExistentes + puntuacionActual;

            // Validar que no supere la puntuación máxima del criterio
            if (sumaTotal > puntuacionMaximaCriterio) {
                mostrarError(String.format(
                    "La suma de los agregados (%.1f) supera la puntuación máxima del criterio (%.1f).\n" +
                    "Suma actual de otros agregados: %.1f\n" +
                    "Puntuación que intenta ingresar: %.1f\n" +
                    "Puntuación máxima disponible: %.1f",
                    sumaTotal, puntuacionMaximaCriterio, sumaAgregadosExistentes,
                    puntuacionActual, puntuacionMaximaCriterio - sumaAgregadosExistentes
                ));
                return false;
            }

            return true;

        } catch (Exception e) {
            manejarExcepcion("validar suma de agregados", e);
            return false;
        }
    }

    /**
     * Valida que la suma de los agregados de tipo "Puntuacion" no supere el máximo del criterio
     * cuando se edita directamente en la tabla
     */
    private boolean validarSumaAgregadosEnTabla(Map<String, Object> fila, Long agregadoIdActual,
                                                String nuevoPuntuacionStr, Criterio criterio,
                                                List<Agregado> agregadosCriterio) {
        try {
            // Solo validar para criterios de tipo "Puntuacion"
            if (!"Puntuacion".equalsIgnoreCase(criterio.getTipoEvaluacion())) {
                return true;
            }

            // Obtener la puntuación máxima del criterio
            Double puntuacionMaximaCriterio = criterio.getPuntuacionMaxima();
            if (puntuacionMaximaCriterio == null) {
                mostrarError("El criterio no tiene una puntuación máxima definida");
                return false;
            }

            // Parsear la nueva puntuación
            double nuevaPuntuacion;
            try {
                nuevaPuntuacion = Double.parseDouble(nuevoPuntuacionStr.trim());
            } catch (NumberFormatException e) {
                // Si no es válido, el listener de validación de formato ya lo manejó
                return true;
            }

            // Sumar las puntuaciones de todos los agregados del criterio
            double sumaTotal = 0.0;

            for (Agregado agregado : agregadosCriterio) {
                if (agregado.getId().equals(agregadoIdActual)) {
                    // Para el agregado actual, usar la nueva puntuación
                    sumaTotal += nuevaPuntuacion;
                } else {
                    // Para los demás agregados, usar el valor de la fila
                    Object valor = fila.get("agregado_" + agregado.getId());
                    if (valor instanceof String && !((String) valor).isEmpty()) {
                        try {
                            sumaTotal += Double.parseDouble((String) valor);
                        } catch (NumberFormatException e) {
                            // Ignorar valores inválidos
                        }
                    }
                }
            }

            // Validar que no supere la puntuación máxima del criterio
            if (sumaTotal > puntuacionMaximaCriterio) {
                // Calcular la suma de los otros agregados
                double sumaOtros = sumaTotal - nuevaPuntuacion;

                mostrarError(String.format(
                    "La suma de los agregados (%.1f) supera la puntuación máxima del criterio '%s' (%.1f).\n" +
                    "Suma actual de otros agregados: %.1f\n" +
                    "Puntuación que intenta ingresar: %.1f\n" +
                    "Puntuación máxima disponible: %.1f",
                    sumaTotal, criterio.getNombre(), puntuacionMaximaCriterio,
                    sumaOtros, nuevaPuntuacion, puntuacionMaximaCriterio - sumaOtros
                ));
                return false;
            }

            return true;

        } catch (Exception e) {
            manejarExcepcion("validar suma de agregados en tabla", e);
            return false;
        }
    }

    /**
     * Crea un header personalizado para las columnas de agregados con un botón para mostrar la descripción
     */
    private HBox crearHeaderConBotonDescripcion(Agregado agregado) {
        HBox header = new HBox(3);
        header.setAlignment(Pos.CENTER);

        Label lblNombre = new Label(agregado.getNombre());
        lblNombre.setStyle("-fx-font-size: 11px;");

        // Solo agregar botón si hay descripción
        if (agregado.getDescripcion() != null && !agregado.getDescripcion().trim().isEmpty()) {
            Button btnInfo = new Button("ⓘ");
            btnInfo.setStyle("-fx-font-size: 9px; -fx-padding: 1 4px; -fx-background-radius: 8; " +
                           "-fx-cursor: hand; -fx-min-width: 18px; -fx-max-width: 18px; " +
                           "-fx-min-height: 18px; -fx-max-height: 18px;");
            btnInfo.setTooltip(new Tooltip("Ver descripción"));

            btnInfo.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Descripción de " + agregado.getNombre());
                alert.setHeaderText(agregado.getNombre());
                alert.setContentText(agregado.getDescripcion());

                // Ajustar tamaño del diálogo
                alert.getDialogPane().setPrefWidth(400);
                alert.showAndWait();
            });

            header.getChildren().addAll(lblNombre, btnInfo);
        } else {
            header.getChildren().add(lblNombre);
        }

        return header;
    }

    private void limpiarFormulario(ComboBox<Alumno> cmbAlumno, ComboBox<Agregado> cmbAgregado,
                                   ComboBox<Criterio> cmbCriterio, ComboBox<Grupo> cmbGrupo,
                                   ComboBox<Materia> cmbMateria, ComboBox<Integer> cmbParcial,
                                   TextField txtPuntuacion) {
        cmbAlumno.setValue(null);
        cmbAgregado.setValue(null);
        cmbCriterio.setValue(null);
        cmbGrupo.setValue(null);
        cmbMateria.setValue(null);
        cmbParcial.setValue(null);
        txtPuntuacion.clear();
    }

    private void cargarAlumnos(ComboBox<Alumno> combo) {
        try {
            List<Alumno> alumnos = alumnoService.obtenerTodosLosAlumnos();
            combo.setItems(FXCollections.observableArrayList(alumnos));
        } catch (Exception e) {
            manejarExcepcion("cargar alumnos", e);
        }
    }

    private void cargarAgregados(ComboBox<Agregado> combo) {
        try {
            List<Agregado> agregados = agregadoService.obtenerTodosLosAgregados();
            combo.setItems(FXCollections.observableArrayList(agregados));
        } catch (Exception e) {
            manejarExcepcion("cargar agregados", e);
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

    private void cargarGrupos(ComboBox<Grupo> combo) {
        try {
            List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
            combo.setItems(FXCollections.observableArrayList(grupos));
            LOG.info("Grupos cargados exitosamente: {} grupos disponibles", grupos.size());
            // Configurar el callback para mostrar el ID del grupo
            combo.setCellFactory(param -> new javafx.scene.control.ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.valueOf(item.getId()));
                    }
                }
            });
            combo.setButtonCell(new javafx.scene.control.ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.valueOf(item.getId()));
                    }
                }
            });
        } catch (Exception e) {
            LOG.error("Error al cargar grupos: ", e);
            manejarExcepcion("cargar grupos", e);
        }
    }

    private void cargarMaterias(ComboBox<Materia> combo) {
        try {
            LOG.debug("Cargando materias...");
            List<Materia> materias = materiaService.obtenerTodasLasMaterias();

            if (materias == null || materias.isEmpty()) {
                LOG.warn("No se encontraron materias");
                return;
            }

            LOG.info("Total de materias: {}", materias.size());
            materias.forEach(m -> LOG.debug("  - {}: {}", m.getId(), m.getNombre()));

            combo.setItems(FXCollections.observableArrayList(materias));
        } catch (Exception e) {
            LOG.error("Error al cargar materias: ", e);
            manejarExcepcion("cargar materias", e);
        }
    }

    private void cargarDatos(TableView<CalificacionConcentrado> tabla) {
        try {
            List<CalificacionConcentrado> calificaciones = calificacionConcentradoService.obtenerTodasLasCalificaciones();
            tabla.setItems(FXCollections.observableArrayList(calificaciones));

            // 📏 Ajustar columnas al contenido (incluyendo botones)
            Platform.runLater(() -> ajustarColumnasAlContenido(tabla));
        } catch (Exception e) {
            manejarExcepcion("cargar calificaciones", e);
        }
    }


    /**
     * Recalcula los valores de puntosParcial y calificacionParcial cuando se modifican los puntos de examen
     *
     * @param fila Map que contiene los datos de la fila
     * @param totalPuntosExamen Total de puntos del examen
     * @param criteriosInfo Lista con información de los criterios
     */
    private void recalcularPuntosParcial(Map<String, Object> fila, Integer totalPuntosExamen, List<Map<String, Object>> criteriosInfo) {
        try {
            // Calcular total de portafolio (puntos de criterios/agregados)
            double totalPortafolio = 0.0;
            for (Map<String, Object> criterioInfo : criteriosInfo) {
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
                        }
                    } else {
                        if (valor instanceof String && !((String) valor).isEmpty()) {
                            try {
                                puntosObtenidosCriterio += Double.parseDouble((String) valor);
                            } catch (NumberFormatException e) {
                                // Ignorar valores inválidos
                            }
                        }
                    }
                }

                totalPortafolio += puntosObtenidosCriterio;
            }

            // Obtener puntos del examen directamente (aciertos, no la calificación)
            double puntosExamen = 0.0;
            Object aciertosExamenObj = fila.get("aciertosExamen");

            if (aciertosExamenObj != null) {
                try {
                    if (aciertosExamenObj instanceof Number) {
                        puntosExamen = ((Number) aciertosExamenObj).doubleValue();
                    } else if (aciertosExamenObj instanceof String && !((String) aciertosExamenObj).isEmpty()) {
                        puntosExamen = Double.parseDouble((String) aciertosExamenObj);
                    }
                } catch (NumberFormatException e) {
                    // Si hay error, dejar puntosExamen en 0.0
                }
            }

            // Calcular puntos parcial y calificación parcial
            // Puntos Parcial = Portafolio + Puntos Examen (aciertos directos)
            double puntosParcial = totalPortafolio + puntosExamen;
            double calificacionParcial = (puntosParcial * 10.0) / 100.0;

            // Actualizar la fila con los nuevos valores
            fila.put("puntosParcial", puntosParcial);
            fila.put("calificacionParcial", calificacionParcial);

        } catch (Exception e) {
            LOG.error("Error al recalcular puntos parcial: {}", e.getMessage());
        }
    }

    /**
     * Genera un archivo Word con el concentrado de calificaciones usando una plantilla
     */
    private void generarArchivoConcentrado(TableView<Map<String, Object>> tabla, Grupo grupo, Materia materia, Integer parcial) {
        try {
            // Validar que haya datos en la tabla
            if (tabla.getItems().isEmpty()) {
                mostrarAdvertencia("No hay datos para exportar");
                return;
            }

            // Obtener el examen para acceder a la fecha de aplicación
            Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
                grupo.getId(), materia.getId(), parcial
            );

            String fechaAplicacionStr = "N/A";
            if (examenOpt.isPresent() && examenOpt.get().getFechaAplicacion() != null) {
                fechaAplicacionStr = examenOpt.get().getFechaAplicacion()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            // Obtener el nombre del maestro de la configuración
            String nombreMaestro = "N/A";
            Optional<Configuracion> configuracionOpt = configuracionService.obtenerConfiguracion();
            if (configuracionOpt.isPresent() && configuracionOpt.get().getNombreMaestro() != null) {
                nombreMaestro = configuracionOpt.get().getNombreMaestro();
            }

            // Obtener el semestre desde el primer dígito del ID del grupo
            String semestre = obtenerSemestreDesdeGrupoId(grupo.getId());

            // Crear FileChooser para seleccionar dónde guardar
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

            // Mostrar diálogo para seleccionar ubicación
            File file = fileChooser.showSaveDialog(mainContent.getScene().getWindow());

            if (file == null) {
                // Usuario canceló la operación
                return;
            }

            // Ruta de la plantilla
            Path templatePath = Paths.get("plantillas/concentrado_calificaciones.docx");

            // Obtener criterios de evaluación para esta materia y parcial
            List<Criterio> criterios = new ArrayList<>();
            List<Map<String, Object>> criteriosInfo = new ArrayList<>();
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
                    .collect(Collectors.toList());
                totalCriterios = criterios.size();
                LOG.info("Total de criterios para materia {} parcial {}: {}", materia.getId(), parcial, totalCriterios);

                // Recopilar información de criterios y agregados para calcular portafolio
                for (Criterio criterio : criterios) {
                    List<Agregado> agregados = agregadoService.obtenerAgregadosPorCriterio(criterio.getId()).stream()
                        .sorted((a1, a2) -> {
                            if (a1.getOrden() == null && a2.getOrden() == null) return 0;
                            if (a1.getOrden() == null) return 1;
                            if (a2.getOrden() == null) return -1;
                            return Integer.compare(a1.getOrden(), a2.getOrden());
                        })
                        .collect(Collectors.toList());

                    if (!agregados.isEmpty()) {
                        Map<String, Object> criterioInfo = new HashMap<>();
                        criterioInfo.put("criterioId", criterio.getId());
                        criterioInfo.put("agregadoIds", agregados.stream().map(Agregado::getId).collect(Collectors.toList()));
                        criterioInfo.put("esCheck", "Check".equalsIgnoreCase(criterio.getTipoEvaluacion()));
                        criterioInfo.put("puntuacionMaxima", criterio.getPuntuacionMaxima());
                        criteriosInfo.add(criterioInfo);
                    }
                }
            } catch (Exception e) {
                LOG.error("Error al obtener criterios", e);
            }
            final int TOTAL_CRITERIOS = totalCriterios;
            final List<Map<String, Object>> CRITERIOS_INFO = criteriosInfo;
            final String NOMBRE_MAESTRO = nombreMaestro;
            final String PARCIAL = String.valueOf(parcial);
            final String SEMESTRE = semestre;

            // Usar la plantilla - escribir directamente en las filas
            try (FileInputStream fis = new FileInputStream(templatePath.toFile());
                 XWPFDocument document = new XWPFDocument(fis);
                 FileOutputStream out = new FileOutputStream(file)) {

                 // Reemplazar etiquetas en párrafos del documento
                 for (XWPFParagraph paragraph : document.getParagraphs()) {
                     reemplazarEtiquetasEnParrafo(paragraph, materia.getNombre(), fechaAplicacionStr, NOMBRE_MAESTRO, PARCIAL, SEMESTRE);
                 }

                 // Reemplazar etiquetas en tablas
                 for (XWPFTable table : document.getTables()) {
                     for (XWPFTableRow row : table.getRows()) {
                         for (XWPFTableCell cell : row.getTableCells()) {
                             for (XWPFParagraph paragraph : cell.getParagraphs()) {
                                 reemplazarEtiquetasEnParrafo(paragraph, materia.getNombre(), fechaAplicacionStr, NOMBRE_MAESTRO, PARCIAL, SEMESTRE);
                             }
                         }
                     }
                 }

                 if (!document.getTables().isEmpty()) {
                    XWPFTable table = document.getTables().get(0);
                    LOG.info("Tabla encontrada con {} filas", table.getNumberOfRows());

                    // Los datos se escriben a partir de la fila 6 (índice 5)
                    final int FILA_INICIO = 5; // Fila 6 (base 1)
                    final int COL_NUMERO_LISTA = 0; // Primera columna
                    final int COL_NOMBRE_COMPLETO = 1; // Segunda columna

                    // Obtener datos de alumnos
                    List<Map<String, Object>> alumnos = tabla.getItems();
                    LOG.info("Total de alumnos a exportar: {}", alumnos.size());

                    // Verificar si es necesario insertar filas adicionales
                    int filasNecesarias = FILA_INICIO + alumnos.size();
                    if (table.getNumberOfRows() < filasNecesarias) {
                        int filasAInsertar = filasNecesarias - table.getNumberOfRows();
                        LOG.info("Insertando {} filas adicionales en la tabla (tiene {}, necesita {})",
                            filasAInsertar, table.getNumberOfRows(), filasNecesarias);

                        // Obtener la última fila como referencia para copiar el formato
                        XWPFTableRow filaReferencia = table.getRow(table.getNumberOfRows() - 1);

                        for (int i = 0; i < filasAInsertar; i++) {
                            XWPFTableRow nuevaFila = table.createRow();

                            // Copiar el formato de la fila de referencia
                            for (int j = 0; j < filaReferencia.getTableCells().size() && j < nuevaFila.getTableCells().size(); j++) {
                                XWPFTableCell celdaReferencia = filaReferencia.getCell(j);
                                XWPFTableCell nuevaCelda = nuevaFila.getCell(j);

                                // Copiar propiedades de la celda (bordes, alineación, etc.)
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
                        Map<String, Object> fila = alumnos.get(alumnoIdx);
                        int filaIndex = FILA_INICIO + alumnoIdx;

                        // Obtener la fila existente (sin crear ni clonar)
                        XWPFTableRow filaActual = table.getRow(filaIndex);

                        // Obtener datos del alumno
                        Object numero = fila.get("numero");
                        Object nombreCompleto = fila.get("nombreCompleto");
                        String numeroStr = numero != null ? numero.toString() : String.valueOf(alumnoIdx + 1);
                        String nombreStr = nombreCompleto != null ? nombreCompleto.toString() : "";

                        LOG.info("Alumno {} en fila {}: numero='{}', nombre='{}'", alumnoIdx + 1, filaIndex + 1, numeroStr, nombreStr);

                        // Escribir SOLO los datos en las columnas correspondientes
                        if (!filaActual.getTableCells().isEmpty()) {
                            XWPFTableCell cell = filaActual.getCell(COL_NUMERO_LISTA);
                            escribirSoloTexto(cell, numeroStr);
                        }

                        if (COL_NOMBRE_COMPLETO < filaActual.getTableCells().size()) {
                            XWPFTableCell cell = filaActual.getCell(COL_NOMBRE_COMPLETO);
                            escribirSoloTexto(cell, nombreStr);
                        }

                        // Escribir "0" en la columna 3 (índice 2)
                        final int COL_TERCERA = 2;
                        if (COL_TERCERA < filaActual.getTableCells().size()) {
                            XWPFTableCell cell = filaActual.getCell(COL_TERCERA);
                            escribirSoloTexto(cell, "0");
                        }

                        // Escribir el total de criterios en la columna 4 (índice 3)
                        final int COL_TOTAL_CRITERIOS = 3;
                        if (COL_TOTAL_CRITERIOS < filaActual.getTableCells().size()) {
                            XWPFTableCell cell = filaActual.getCell(COL_TOTAL_CRITERIOS);
                            escribirSoloTexto(cell, String.valueOf(TOTAL_CRITERIOS));
                        }

                        // Calcular y escribir el Portafolio en la columna 5 (índice 4)
                        final int COL_PORTAFOLIO = 4;
                        if (COL_PORTAFOLIO < filaActual.getTableCells().size()) {
                            double totalPortafolio = 0.0;

                            // Sumar los puntos obtenidos de todos los criterios
                            for (Map<String, Object> criterioInfo : CRITERIOS_INFO) {
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
                                        // Para tipo Puntuación, sumar los valores numéricos
                                        if (valor instanceof Number) {
                                            puntosObtenidosCriterio += ((Number) valor).doubleValue();
                                        } else if (valor instanceof String && !((String) valor).isEmpty()) {
                                            try {
                                                puntosObtenidosCriterio += Double.parseDouble((String) valor);
                                            } catch (NumberFormatException e) {
                                                // Ignorar valores no numéricos
                                            }
                                        }
                                    }
                                }

                                totalPortafolio += puntosObtenidosCriterio;
                            }

                            XWPFTableCell cell = filaActual.getCell(COL_PORTAFOLIO);
                            // Formatear como entero de dos dígitos
                            escribirSoloTexto(cell, String.format("%02d", (int) Math.round(totalPortafolio)));
                        }

                        // Escribir Calificación Examen en columna 6 (índice 5) con 1 decimal
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
                            XWPFTableCell cell = filaActual.getCell(COL_CALIFICACION_EXAMEN);
                            escribirSoloTexto(cell, valorStr);
                        }

                        // Escribir Puntos Examen en columna 7 (índice 6) como entero
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
                            XWPFTableCell cell = filaActual.getCell(COL_PUNTOS_EXAMEN);
                            escribirSoloTexto(cell, valorStr);
                        }

                        // Escribir Calificación Parcial en columna 8 (índice 7) con 1 decimal
                        final int COL_CALIFICACION_PARCIAL = 7;
                        double calificacionParcialValor = 0.0;
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
                            XWPFTableCell cell = filaActual.getCell(COL_CALIFICACION_PARCIAL);
                            escribirSoloTexto(cell, valorStr);
                        }

                        // Escribir Calificación Parcial en letra en columna 9 (índice 8)
                        final int COL_CALIFICACION_LETRA = 8;
                        if (COL_CALIFICACION_LETRA < filaActual.getTableCells().size()) {
                            String valorLetra = convertirCalificacionALetra(calificacionParcialValor);
                            XWPFTableCell cell = filaActual.getCell(COL_CALIFICACION_LETRA);
                            escribirTextoConFuenteReducida(cell, valorLetra); // Fuente 2 puntos más pequeña
                        }
                    }

                    LOG.info("Datos escritos para {} alumnos", alumnos.size());
                } else {
                    LOG.error("No se encontraron tablas en el documento");
                    throw new IOException("La plantilla no contiene ninguna tabla");
                }

                document.write(out);
            } catch (java.io.FileNotFoundException e) {
                LOG.error("No se encontró la plantilla en: {}", templatePath);
                mostrarError("No se encontró la plantilla en: " + templatePath.toString());
                return;
            } catch (Exception e) {
                LOG.error("Error al procesar la plantilla", e);
                throw new IOException("Error al procesar la plantilla: " + e.getMessage(), e);
            }

            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportación Exitosa");
            alert.setHeaderText(null);
            alert.setContentText("El concentrado se ha exportado correctamente a:\n" + file.getAbsolutePath());

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
                        mostrarAdvertencia("No se pudo abrir el archivo automáticamente");
                    }
                }
            }

        } catch (IOException e) {
            LOG.error("Error al generar archivo Word", e);
            mostrarError("No se pudo generar el archivo: " + e.getMessage());
        } catch (Exception e) {
            LOG.error("Error inesperado al generar archivo", e);
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Escribe SOLO el texto en una celda sin modificar formato ni estructura
     * Este método es lo más minimalista posible para no alterar NADA de la plantilla
     */
    private void escribirSoloTexto(XWPFTableCell cell, String texto) {
        try {
            XWPFParagraph paragraph;

            // Si no hay párrafos, crear uno
            if (cell.getParagraphs().isEmpty()) {
                LOG.warn("Celda sin párrafos, creando uno nuevo");
                paragraph = cell.addParagraph();
            } else {
                paragraph = cell.getParagraphs().get(0);
            }

            XWPFRun run;

            // Si no hay runs, crear uno
            if (paragraph.getRuns().isEmpty()) {
                LOG.warn("Párrafo sin runs, creando uno nuevo");
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
     * Específico para columna de calificación en letra
     */
    private void escribirTextoConFuenteReducida(XWPFTableCell cell, String texto) {
        try {
            XWPFParagraph paragraph;

            // Si no hay párrafos, crear uno
            if (cell.getParagraphs().isEmpty()) {
                LOG.warn("Celda sin párrafos, creando uno nuevo");
                paragraph = cell.addParagraph();
            } else {
                paragraph = cell.getParagraphs().get(0);
            }

            XWPFRun run;

            // Si no hay runs, crear uno
            if (paragraph.getRuns().isEmpty()) {
                LOG.warn("Párrafo sin runs, creando uno nuevo");
                run = paragraph.createRun();
            } else {
                run = paragraph.getRuns().get(0);
            }

            // Obtener el tamaño de fuente actual y reducirlo en 2 puntos
            int tamanoActual = run.getFontSize();
            if (tamanoActual > 0) {
                run.setFontSize(tamanoActual - 2);
            } else {
                // Si no tiene tamaño definido, usar 9 puntos (11 - 2)
                run.setFontSize(9);
            }

            // SOLO reemplazar el texto del run
            run.setText(texto, 0);

        } catch (Exception e) {
            LOG.error("Error al escribir texto con fuente reducida en celda: {}", e.getMessage(), e);
        }
    }

    /**
     * Convierte una calificación numérica (0-10) a su equivalente en letra
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
     * Convierte un número entero (0-10) a letra
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
     * Convierte un dígito (0-9) a letra
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
     * Obtiene el nombre del semestre basado en el primer dígito del ID del grupo.
     * Ejemplos: 101 → PRIMER, 201 → SEGUNDO, 301 → TERCER, etc.
     *
     * @param grupoId ID del grupo
     * @return Nombre del semestre en mayúsculas (PRIMER, SEGUNDO, TERCER, CUARTO, QUINTO, SEXTO)
     */
    private String obtenerSemestreDesdeGrupoId(Long grupoId) {
        if (grupoId == null) {
            return "N/A";
        }

        // Convertir el ID a String y obtener el primer dígito
        String grupoIdStr = String.valueOf(grupoId);
        if (grupoIdStr.isEmpty()) {
            return "N/A";
        }

        // Obtener el primer dígito
        char primerDigito = grupoIdStr.charAt(0);
        int semestre = Character.getNumericValue(primerDigito);

        // Mapear el dígito al nombre del semestre
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
     * Reemplaza etiquetas en un párrafo de un documento Word.
     * Este método maneja correctamente los casos donde una etiqueta está dividida en múltiples runs.
     */
    private void reemplazarEtiquetasEnParrafo(XWPFParagraph paragraph, String nombreMateria, String fechaAplicacion, String nombreMaestro, String parcial, String semestre) {
        if (paragraph.getRuns() == null || paragraph.getRuns().isEmpty()) {
            return;
        }

        // Concatenar todo el texto del párrafo
        StringBuilder textoCompleto = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String texto = run.getText(0);
            if (texto != null) {
                textoCompleto.append(texto);
            }
        }

        String textoOriginal = textoCompleto.toString();

        // Verificar si hay etiquetas que reemplazar
        if (!textoOriginal.contains("${materia}") && !textoOriginal.contains("${fecha_aplicacion}")
            && !textoOriginal.contains("${nombre_maestro}") && !textoOriginal.contains("${parcial}")
            && !textoOriginal.contains("${SEMESTRE}")) {
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

        // Si hubo reemplazo, actualizar el párrafo
        if (huboReemplazo) {
            // Guardar el formato del primer run
            XWPFRun primerRun = paragraph.getRuns().get(0);
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
            XWPFRun nuevoRun = paragraph.createRun();
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

    private void ajustarColumnasAlContenido(TableView<CalificacionConcentrado> tabla) {
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        for (TableColumn<CalificacionConcentrado, ?> columna : tabla.getColumns()) {
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

    private double calcularAnchoColumna(TableColumn<CalificacionConcentrado, ?> columna) {
        javafx.scene.text.Text textoHeader = new javafx.scene.text.Text(columna.getText());
        double anchoMaximo = textoHeader.getLayoutBounds().getWidth() + 40;

        // Nota: Esta tabla puede estar vacía, así que solo calculamos por el header
        return anchoMaximo < 100 ? 100 : anchoMaximo;
    }
}
