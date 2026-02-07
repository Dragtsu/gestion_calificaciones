package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.*;
import com.alumnos.domain.port.in.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para el Informe de Concentrado de Calificaciones
 * Responsabilidad: Manejar la vista de solo lectura del informe de concentrado
 */
@Component
public class InformeConcentradoController extends BaseController {

    private final AlumnoServicePort alumnoService;
    private final CriterioServicePort criterioService;
    private final AgregadoServicePort agregadoService;
    private final GrupoServicePort grupoService;
    private final MateriaServicePort materiaService;
    private final CalificacionConcentradoServicePort calificacionConcentradoService;
    private final ExamenServicePort examenService;
    private final AlumnoExamenServicePort alumnoExamenService;

    public InformeConcentradoController(AlumnoServicePort alumnoService,
                                       CriterioServicePort criterioService,
                                       AgregadoServicePort agregadoService,
                                       GrupoServicePort grupoService,
                                       MateriaServicePort materiaService,
                                       CalificacionConcentradoServicePort calificacionConcentradoService,
                                       ExamenServicePort examenService,
                                       AlumnoExamenServicePort alumnoExamenService) {
        this.alumnoService = alumnoService;
        this.criterioService = criterioService;
        this.agregadoService = agregadoService;
        this.grupoService = grupoService;
        this.materiaService = materiaService;
        this.calificacionConcentradoService = calificacionConcentradoService;
        this.examenService = examenService;
        this.alumnoExamenService = alumnoExamenService;
    }

    /**
     * Crea la vista del informe de concentrado con filtros y tabla de solo lectura
     */
    public VBox crearVista() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        try {
            // Header
            Label lblTitulo = new Label("Informe de Concentrado de Calificaciones");
            lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Panel de filtros
            VBox filtrosPanel = new VBox(15);
            filtrosPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            Label lblFiltros = new Label("Filtros (Obligatorios)");
            lblFiltros.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Fila de filtros
            HBox filtrosBox = new HBox(20);
            filtrosBox.setAlignment(Pos.CENTER_LEFT);

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
            cargarMaterias(cmbMateria);
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
            btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            buscarContainer.getChildren().addAll(lblEspacio, btnBuscar);

            filtrosBox.getChildren().addAll(grupoContainer, materiaContainer, parcialContainer, buscarContainer);
            filtrosPanel.getChildren().addAll(lblFiltros, filtrosBox);

            // Panel de tabla
            VBox tablaPanel = new VBox(15);
            tablaPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPrefHeight(500);
            scrollPane.setStyle("-fx-background-color: transparent;");

            TableView<Map<String, Object>> tblInforme = new TableView<>();
            tblInforme.setEditable(false);
            tblInforme.setPlaceholder(new Label("Seleccione Grupo, Materia y Parcial, luego presione 'Buscar'"));
            tblInforme.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            // ✅ Agregar estilo personalizado para resaltado uniforme de filas
            tblInforme.setStyle(
                "-fx-selection-bar: #E3F2FD; " +                    // Color de fondo de fila seleccionada (azul claro)
                "-fx-selection-bar-non-focused: #F5F5F5; " +        // Color cuando la tabla no tiene foco (gris claro)
                "-fx-background-color: white; " +
                "-fx-table-cell-border-color: transparent; " +
                "-fx-focus-color: transparent; " +                   // Sin borde de foco
                "-fx-faint-focus-color: transparent;"                // Sin borde de foco tenue
            );

            // ✅ Agregar CSS adicional para celdas seleccionadas
            String cellStyle =
                ".table-view:focused .table-row-cell:filled:selected, " +
                ".table-view .table-row-cell:filled:selected { " +
                "    -fx-background-color: #E3F2FD; " +              // Azul claro para toda la fila
                "    -fx-text-fill: black; " +                       // Texto negro
                "} " +
                ".table-view:focused .table-row-cell:filled:selected .table-cell, " +
                ".table-view .table-row-cell:filled:selected .table-cell { " +
                "    -fx-background-color: #E3F2FD; " +              // Azul claro para todas las celdas
                "    -fx-text-fill: black; " +                       // Texto negro
                "    -fx-border-color: transparent; " +
                "} " +
                ".table-view .table-row-cell:filled:hover { " +
                "    -fx-background-color: #F5F5F5; " +              // Gris muy claro al pasar el mouse
                "    -fx-text-fill: black; " +
                "}";

            // Aplicar el estilo CSS a la tabla
            tblInforme.setStyle(tblInforme.getStyle() + cellStyle);

            scrollPane.setContent(tblInforme);
            tablaPanel.getChildren().add(scrollPane);

            // Botón Exportar a Excel
            Button btnExportarExcel = new Button("📊 Exportar a Excel");
            btnExportarExcel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            btnExportarExcel.setDisable(true); // Deshabilitado hasta que se genere el informe

            HBox botonesBox = new HBox(10);
            botonesBox.setAlignment(Pos.CENTER_RIGHT);
            botonesBox.getChildren().add(btnExportarExcel);

            // Evento del botón Buscar
            btnBuscar.setOnAction(event -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAdvertencia("Debe seleccionar Grupo, Materia y Parcial");
                    return;
                }
                generarTablaInforme(tblInforme, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
                btnExportarExcel.setDisable(false); // Habilitar botón después de generar
            });

            // Evento del botón Exportar
            btnExportarExcel.setOnAction(event -> {
                if (tblInforme.getItems().isEmpty()) {
                    mostrarAdvertencia("No hay datos para exportar. Genere el informe primero.");
                    return;
                }
                exportarAExcel(tblInforme, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
            });

            vista.getChildren().addAll(lblTitulo, filtrosPanel, tablaPanel, botonesBox);

        } catch (Exception e) {
            LOG.error("Error al crear vista de informe de concentrado", e);
        }

        return vista;
    }

    /**
     * Genera la tabla de informe de concentrado (solo lectura)
     */
    private void generarTablaInforme(TableView<Map<String, Object>> tabla, Grupo grupo, Materia materia, Integer parcial) {
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
            colNumero.setStyle("-fx-alignment: CENTER;");
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

            // Lista para recopilar información de todos los agregados
            List<Map<String, Object>> criteriosInfo = new ArrayList<>();

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

                if (agregados.isEmpty()) continue;

                boolean esCheck = "Check".equalsIgnoreCase(criterio.getTipoEvaluacion());

                // Crear una columna por agregado
                for (Agregado agregado : agregados) {
                    TableColumn<Map<String, Object>, String> colAgregado = new TableColumn<>(agregado.getNombre());
                    colAgregado.setPrefWidth(esCheck ? 80 : 100);
                    colAgregado.setMinWidth(esCheck ? 80 : 100);
                    colAgregado.setMaxWidth(esCheck ? 80 : 100);
                    colAgregado.setResizable(false);

                    if (esCheck) {
                        // Columna tipo Check - mostrar ✓ o ✗
                        colAgregado.setCellValueFactory(cellData -> {
                            Object valor = cellData.getValue().get("agregado_" + agregado.getId());
                            if (valor instanceof Boolean) {
                                return new javafx.beans.property.SimpleStringProperty((Boolean) valor ? "✓" : "✗");
                            }
                            return new javafx.beans.property.SimpleStringProperty("✗");
                        });

                        colAgregado.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                    setStyle("");
                                } else {
                                    setText(item);

                                    // Estilo base
                                    String baseStyle = "-fx-alignment: CENTER; -fx-font-size: 16px; -fx-font-weight: bold; ";

                                    // Determinar color según el valor
                                    if ("✓".equals(item)) {
                                        // Check verdadero - verde siempre
                                        setStyle(baseStyle + "-fx-text-fill: #00C853;");
                                    } else {
                                        // Check falso - rojo siempre
                                        setStyle(baseStyle + "-fx-text-fill: #D32F2F;");
                                    }

                                    // Si la fila está seleccionada, agregar fondo azul claro
                                    if (getTableRow() != null && getTableRow().isSelected()) {
                                        setStyle(getStyle() + " -fx-background-color: #E3F2FD;");
                                    }
                                }
                            }

                            @Override
                            public void updateSelected(boolean selected) {
                                super.updateSelected(selected);
                                // Refrescar el estilo cuando cambia la selección
                                updateItem(getItem(), isEmpty());
                            }
                        });
                    } else {
                        // Columna tipo Puntuación - mostrar valor o 0 en rojo
                        colAgregado.setCellValueFactory(cellData -> {
                            Object valor = cellData.getValue().get("agregado_" + agregado.getId());
                            if (valor instanceof String && !((String) valor).isEmpty()) {
                                return new javafx.beans.property.SimpleStringProperty((String) valor);
                            }
                            return new javafx.beans.property.SimpleStringProperty("0");
                        });

                        colAgregado.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setText(null);
                                    setStyle("");
                                } else {
                                    setText(item != null && !item.isEmpty() ? item : "0");

                                    // Estilo base
                                    String baseStyle = "-fx-alignment: CENTER; ";

                                    if ("0".equals(item) || item == null || item.isEmpty()) {
                                        // Cero o vacío - rojo y negrita siempre
                                        setStyle(baseStyle + "-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
                                    } else {
                                        // Valor normal - negro siempre
                                        setStyle(baseStyle + "-fx-text-fill: black;");
                                    }

                                    // Si la fila está seleccionada, agregar fondo azul claro
                                    if (getTableRow() != null && getTableRow().isSelected()) {
                                        setStyle(getStyle() + " -fx-background-color: #E3F2FD;");
                                    }
                                }
                            }

                            @Override
                            public void updateSelected(boolean selected) {
                                super.updateSelected(selected);
                                // Refrescar el estilo cuando cambia la selección
                                updateItem(getItem(), isEmpty());
                            }
                        });
                    }

                    tabla.getColumns().add(colAgregado);
                }

                // Guardar información del criterio
                Map<String, Object> criterioInfo = new HashMap<>();
                criterioInfo.put("esCheck", esCheck);
                criterioInfo.put("puntuacionMaxima", criterio.getPuntuacionMaxima());
                criterioInfo.put("agregadoIds", agregados.stream().map(Agregado::getId).collect(Collectors.toList()));
                criterioInfo.put("nombreCriterio", criterio.getNombre());
                criteriosInfo.add(criterioInfo);

                // Columna Acumulado por criterio
                TableColumn<Map<String, Object>, String> colAcumulado = new TableColumn<>("Acum " + criterio.getNombre());
                colAcumulado.setPrefWidth(100);
                colAcumulado.setMinWidth(100);
                colAcumulado.setMaxWidth(100);
                colAcumulado.setResizable(false);

                final boolean esCriterioCheck = esCheck;
                final Double puntuacionMaxima = criterio.getPuntuacionMaxima();
                final List<Long> agregadoIds = agregados.stream().map(Agregado::getId).collect(Collectors.toList());

                colAcumulado.setCellValueFactory(cellData -> {
                    Map<String, Object> fila = cellData.getValue();
                    double acumulado = 0.0;

                    for (Long agregadoId : agregadoIds) {
                        Object valor = fila.get("agregado_" + agregadoId);
                        if (esCriterioCheck) {
                            if (valor instanceof Boolean && (Boolean) valor) {
                                acumulado += puntuacionMaxima / agregadoIds.size();
                            }
                        } else {
                            if (valor instanceof String && !((String) valor).isEmpty()) {
                                try {
                                    acumulado += Double.parseDouble((String) valor);
                                } catch (NumberFormatException e) {
                                    // Ignorar
                                }
                            }
                        }
                    }

                    return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", acumulado));
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

                            // Estilo base con fondo azul claro
                            String baseStyle = "-fx-alignment: CENTER; -fx-font-weight: bold; ";

                            // Si la fila está seleccionada, usar fondo de selección más oscuro
                            if (getTableRow() != null && getTableRow().isSelected()) {
                                setStyle(baseStyle + "-fx-background-color: #BBDEFB; -fx-text-fill: black;");
                            } else {
                                // Fondo azul claro normal
                                setStyle(baseStyle + "-fx-background-color: #E3F2FD; -fx-text-fill: black;");
                            }
                        }
                    }

                    @Override
                    public void updateSelected(boolean selected) {
                        super.updateSelected(selected);
                        // Refrescar el estilo cuando cambia la selección
                        updateItem(getItem(), isEmpty());
                    }
                });

                tabla.getColumns().add(colAcumulado);
            }

            // Columna Total Portafolio
            TableColumn<Map<String, Object>, String> colPortafolio = new TableColumn<>("Total Portafolio");
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

                    for (Long agregadoId : agregadoIds) {
                        Object valor = fila.get("agregado_" + agregadoId);
                        if (esCheck) {
                            if (valor instanceof Boolean && (Boolean) valor) {
                                totalPortafolio += puntuacionMaxima / agregadoIds.size();
                            }
                        } else {
                            if (valor instanceof String && !((String) valor).isEmpty()) {
                                try {
                                    totalPortafolio += Double.parseDouble((String) valor);
                                } catch (NumberFormatException e) {
                                    // Ignorar
                                }
                            }
                        }
                    }
                }

                return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", totalPortafolio));
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

                        // Estilo base con fondo naranja claro
                        String baseStyle = "-fx-alignment: CENTER; -fx-font-weight: bold; -fx-font-size: 14px; ";

                        // Si la fila está seleccionada, usar fondo naranja más oscuro
                        if (getTableRow() != null && getTableRow().isSelected()) {
                            setStyle(baseStyle + "-fx-background-color: #FFE0B2; -fx-text-fill: black;");
                        } else {
                            // Fondo naranja claro normal
                            setStyle(baseStyle + "-fx-background-color: #FFF3E0; -fx-text-fill: black;");
                        }
                    }
                }

                @Override
                public void updateSelected(boolean selected) {
                    super.updateSelected(selected);
                    // Refrescar el estilo cuando cambia la selección
                    updateItem(getItem(), isEmpty());
                }
            });

            tabla.getColumns().add(colPortafolio);

            // Agregar columnas de Examen si existe
            Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
                grupo.getId(), materia.getId(), parcial);

            if (examenOpt.isPresent()) {
                Examen examen = examenOpt.get();
                final Integer totalPuntosExamen = examen.getTotalPuntosExamen();

                // Columna Puntos Examen
                String headerPuntosExamen = totalPuntosExamen != null
                    ? "Puntos Examen (" + totalPuntosExamen + " pts)"
                    : "Puntos Examen";
                TableColumn<Map<String, Object>, String> colPuntosExamen = new TableColumn<>(headerPuntosExamen);
                colPuntosExamen.setPrefWidth(100);
                colPuntosExamen.setMinWidth(100);
                colPuntosExamen.setMaxWidth(100);
                colPuntosExamen.setResizable(false);

                colPuntosExamen.setCellValueFactory(cellData -> {
                    Object valor = cellData.getValue().get("aciertosExamen");
                    if (valor != null) {
                        return new javafx.beans.property.SimpleStringProperty(String.valueOf(valor));
                    }
                    return new javafx.beans.property.SimpleStringProperty("0");
                });

                colPuntosExamen.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item != null && !item.isEmpty() ? item : "0");

                            // Estilo base
                            String baseStyle = "-fx-alignment: CENTER; ";

                            if ("0".equals(item) || item == null || item.isEmpty()) {
                                // Cero o vacío - rojo y negrita siempre
                                setStyle(baseStyle + "-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
                            } else {
                                // Valor normal - negro siempre
                                setStyle(baseStyle + "-fx-text-fill: black;");
                            }

                            // Si la fila está seleccionada, agregar fondo azul claro
                            if (getTableRow() != null && getTableRow().isSelected()) {
                                setStyle(getStyle() + " -fx-background-color: #E3F2FD;");
                            }
                        }
                    }

                    @Override
                    public void updateSelected(boolean selected) {
                        super.updateSelected(selected);
                        // Refrescar el estilo cuando cambia la selección
                        updateItem(getItem(), isEmpty());
                    }
                });

                tabla.getColumns().add(colPuntosExamen);

                // Columna % Examen
                TableColumn<Map<String, Object>, String> colPorcentajeExamen = new TableColumn<>("% Examen");
                colPorcentajeExamen.setPrefWidth(100);
                colPorcentajeExamen.setMinWidth(100);
                colPorcentajeExamen.setMaxWidth(100);
                colPorcentajeExamen.setResizable(false);

                colPorcentajeExamen.setCellValueFactory(cellData -> {
                    Object porcentaje = cellData.getValue().get("porcentajeExamen");
                    if (porcentaje != null) {
                        return new javafx.beans.property.SimpleStringProperty(String.format("%.1f%%", (Double) porcentaje));
                    }
                    return new javafx.beans.property.SimpleStringProperty("0.0%");
                });

                colPorcentajeExamen.setStyle("-fx-alignment: CENTER;");
                tabla.getColumns().add(colPorcentajeExamen);

                // Columna Calificación Examen
                TableColumn<Map<String, Object>, String> colCalificacionExamen = new TableColumn<>("Calif. Examen");
                colCalificacionExamen.setPrefWidth(120);
                colCalificacionExamen.setMinWidth(120);
                colCalificacionExamen.setMaxWidth(120);
                colCalificacionExamen.setResizable(false);

                colCalificacionExamen.setCellValueFactory(cellData -> {
                    Object calif = cellData.getValue().get("calificacionExamen");
                    if (calif != null) {
                        return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", (Double) calif));
                    }
                    return new javafx.beans.property.SimpleStringProperty("0.00");
                });

                colCalificacionExamen.setStyle("-fx-alignment: CENTER;");
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
                    valor != null ? String.format("%.2f", (Double) valor) : "0.00"
                );
            });

            colPuntosParcial.setStyle("-fx-alignment: CENTER;");
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
                    valor != null ? String.format("%.2f", (Double) valor) : "0.00"
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

                        // Estilo base con fondo verde claro
                        String baseStyle = "-fx-alignment: CENTER; -fx-font-weight: bold; -fx-font-size: 14px; ";

                        // Si la fila está seleccionada, usar fondo verde más oscuro
                        if (getTableRow() != null && getTableRow().isSelected()) {
                            setStyle(baseStyle + "-fx-background-color: #A5D6A7; -fx-text-fill: black;");
                        } else {
                            // Fondo verde claro normal
                            setStyle(baseStyle + "-fx-background-color: #C8E6C9; -fx-text-fill: black;");
                        }
                    }
                }

                @Override
                public void updateSelected(boolean selected) {
                    super.updateSelected(selected);
                    // Refrescar el estilo cuando cambia la selección
                    updateItem(getItem(), isEmpty());
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
                    Optional<AlumnoExamen> alumnoExamenOpt = alumnoExamenService
                        .obtenerAlumnoExamenPorAlumnoYExamen(alumno.getId(), examen.getId());

                    if (alumnoExamenOpt.isPresent()) {
                        AlumnoExamen alumnoExamen = alumnoExamenOpt.get();
                        fila.put("aciertosExamen", alumnoExamen.getPuntosExamen());
                        fila.put("porcentajeExamen", alumnoExamen.getPorcentaje());
                        fila.put("calificacionExamen", alumnoExamen.getCalificacion());
                    }
                }

                // Calcular puntos parcial y calificación parcial
                double totalPortafolio = 0.0;

                // Calcular acumulados por criterio y guardarlos en la fila
                int criterioIndex = 0;
                for (Map<String, Object> criterioInfo : criteriosInfo) {
                    @SuppressWarnings("unchecked")
                    List<Long> agregadoIds = (List<Long>) criterioInfo.get("agregadoIds");
                    boolean esCheck = (Boolean) criterioInfo.get("esCheck");
                    Double puntuacionMaxima = (Double) criterioInfo.get("puntuacionMaxima");

                    double acumuladoCriterio = 0.0;
                    for (Long agregadoId : agregadoIds) {
                        Object valor = fila.get("agregado_" + agregadoId);
                        if (esCheck) {
                            if (valor instanceof Boolean && (Boolean) valor) {
                                acumuladoCriterio += puntuacionMaxima / agregadoIds.size();
                            }
                        } else {
                            if (valor instanceof String && !((String) valor).isEmpty()) {
                                try {
                                    acumuladoCriterio += Double.parseDouble((String) valor);
                                } catch (NumberFormatException e) {
                                    // Ignorar
                                }
                            }
                        }
                    }

                    // Guardar el acumulado del criterio
                    fila.put("acumulado_criterio_" + criterioIndex, acumuladoCriterio);
                    totalPortafolio += acumuladoCriterio;
                    criterioIndex++;
                }

                double puntosExamen = 0.0;
                Object calificacionExamenObj = fila.get("calificacionExamen");
                if (calificacionExamenObj instanceof Double) {
                    puntosExamen = (Double) calificacionExamenObj;
                }

                double puntosParcial = totalPortafolio + puntosExamen;
                double calificacionParcial = (puntosParcial * 10.0) / 100.0;

                // Guardar los valores calculados en la fila
                fila.put("totalPortafolio", totalPortafolio);
                fila.put("puntosParcial", puntosParcial);
                fila.put("calificacionParcial", calificacionParcial);

                datos.add(fila);
            }

            tabla.setItems(datos);
            // Mensaje removido - no mostrar "X alumnos encontrados"

        } catch (Exception e) {
            manejarExcepcion("generar informe de concentrado", e);
        }
    }

    private void cargarGrupos(ComboBox<Grupo> combo) {
        try {
            List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
            combo.setItems(FXCollections.observableArrayList(grupos));
            combo.setCellFactory(param -> new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.valueOf(item.getId()));
                }
            });
            combo.setButtonCell(new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Seleccionar..." : String.valueOf(item.getId()));
                }
            });
        } catch (Exception e) {
            manejarExcepcion("cargar grupos", e);
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

    /**
     * Exporta los datos del informe a un archivo Excel
     */
    private void exportarAExcel(TableView<Map<String, Object>> tabla, Grupo grupo, Materia materia, Integer parcial) {
        try {
            // Crear selector de archivo
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Informe de Concentrado");

            // Generar nombre sugerido
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = String.format("Informe_Concentrado_Grupo%s_%s_P%d_%s.xlsx",
                grupo.getId(),
                materia.getNombre().replaceAll("[^a-zA-Z0-9]", "_"),
                parcial,
                timestamp);

            fileChooser.setInitialFileName(nombreArchivo);
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo Excel", "*.xlsx")
            );

            // Mostrar diálogo para guardar
            File file = fileChooser.showSaveDialog(tabla.getScene().getWindow());
            if (file == null) {
                return; // Usuario canceló
            }

            // Crear workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Informe Concentrado");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setRotation((short) 90); // ✅ Orientación vertical (90 grados)
            headerStyle.setWrapText(false);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle checkVerdaderoStyle = workbook.createCellStyle();
            checkVerdaderoStyle.cloneStyleFrom(dataStyle);
            org.apache.poi.ss.usermodel.Font checkVerdaderoFont = workbook.createFont();
            checkVerdaderoFont.setColor(IndexedColors.GREEN.getIndex());
            checkVerdaderoFont.setBold(true);
            checkVerdaderoStyle.setFont(checkVerdaderoFont);

            CellStyle checkFalsoStyle = workbook.createCellStyle();
            checkFalsoStyle.cloneStyleFrom(dataStyle);
            org.apache.poi.ss.usermodel.Font checkFalsoFont = workbook.createFont();
            checkFalsoFont.setColor(IndexedColors.RED.getIndex());
            checkFalsoFont.setBold(true);
            checkFalsoStyle.setFont(checkFalsoFont);

            CellStyle ceroRojoStyle = workbook.createCellStyle();
            ceroRojoStyle.cloneStyleFrom(dataStyle);
            org.apache.poi.ss.usermodel.Font ceroRojoFont = workbook.createFont();
            ceroRojoFont.setColor(IndexedColors.RED.getIndex());
            ceroRojoFont.setBold(true);
            ceroRojoStyle.setFont(ceroRojoFont);

            CellStyle calificacionFinalStyle = workbook.createCellStyle();
            calificacionFinalStyle.cloneStyleFrom(dataStyle);
            calificacionFinalStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            calificacionFinalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font calificacionFont = workbook.createFont();
            calificacionFont.setBold(true);
            calificacionFinalStyle.setFont(calificacionFont);

            // Fila de título
            Row titleRow = sheet.createRow(0);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("INFORME DE CONCENTRADO DE CALIFICACIONES");
            CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleCell.setCellStyle(titleStyle);

            // Información del informe
            Row infoRow1 = sheet.createRow(1);
            infoRow1.createCell(0).setCellValue("Grupo: " + grupo.getId());
            infoRow1.createCell(2).setCellValue("Materia: " + materia.getNombre());
            infoRow1.createCell(4).setCellValue("Parcial: " + parcial);

            Row infoRow2 = sheet.createRow(2);
            infoRow2.createCell(0).setCellValue("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            // Encabezados de columna (fila 4)
            Row headerRow = sheet.createRow(4);
            headerRow.setHeightInPoints(120); // ✅ Altura mayor para texto vertical
            int colIndex = 0;
            for (TableColumn<Map<String, Object>, ?> column : tabla.getColumns()) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(column.getText());
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowIndex = 5;
            int criterioAcumuladoIndex = 0; // Índice para rastrear columnas de acumulado

            for (Map<String, Object> item : tabla.getItems()) {
                Row row = sheet.createRow(rowIndex++);
                colIndex = 0;
                criterioAcumuladoIndex = 0; // Reiniciar para cada fila

                for (TableColumn<Map<String, Object>, ?> column : tabla.getColumns()) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(colIndex);
                    String columnName = column.getText();

                    // Obtener el valor de la celda
                    Object value = null;

                    // Mapeo de nombres de columna a claves del Map
                    if ("#".equals(columnName)) {
                        value = item.get("numero");
                    } else if ("Nombre Completo".equals(columnName)) {
                        value = item.get("nombreCompleto");
                    } else if (columnName.startsWith("Acum ")) {
                        // Es una columna de acumulado - usar el índice para obtener el valor guardado
                        value = item.get("acumulado_criterio_" + criterioAcumuladoIndex);
                        criterioAcumuladoIndex++;
                    } else if ("Total Portafolio".equals(columnName)) {
                        value = item.get("totalPortafolio");
                    } else if ("Puntos Examen".equals(columnName)) {
                        value = item.get("aciertosExamen");
                    } else if ("% Examen".equals(columnName)) {
                        value = item.get("porcentajeExamen");
                    } else if ("Calif. Examen".equals(columnName)) {
                        value = item.get("calificacionExamen");
                    } else if ("Puntos Parcial".equals(columnName)) {
                        value = item.get("puntosParcial");
                    } else if ("Calificación Parcial".equals(columnName)) {
                        value = item.get("calificacionParcial");
                        if (value != null) {
                            if (value instanceof Double) {
                                cell.setCellValue((Double) value);
                            } else {
                                cell.setCellValue(value.toString());
                            }
                            cell.setCellStyle(calificacionFinalStyle);
                            colIndex++;
                            continue;
                        }
                    } else {
                        // Es una columna de agregado
                        String key = buscarClaveAgregado(item, columnName);
                        if (key != null) {
                            value = item.get(key);
                        }
                    }

                    // Escribir el valor en la celda
                    if (value == null) {
                        cell.setCellValue("");
                        cell.setCellStyle(dataStyle);
                    } else if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                        cell.setCellStyle(dataStyle);
                    } else if (value instanceof Double) {
                        cell.setCellValue((Double) value);
                        cell.setCellStyle(dataStyle);
                    } else if (value instanceof Boolean) {
                        // Check: true = ✓, false = ✗
                        cell.setCellValue((Boolean) value ? "✓" : "✗");
                        cell.setCellStyle((Boolean) value ? checkVerdaderoStyle : checkFalsoStyle);
                    } else if (value instanceof String) {
                        String strValue = (String) value;
                        if (strValue.isEmpty() || "0".equals(strValue)) {
                            cell.setCellValue("0");
                            cell.setCellStyle(ceroRojoStyle);
                        } else {
                            try {
                                // Intentar parsear como número
                                double numValue = Double.parseDouble(strValue);
                                cell.setCellValue(numValue);
                                cell.setCellStyle(dataStyle);
                            } catch (NumberFormatException e) {
                                cell.setCellValue(strValue);
                                cell.setCellStyle(dataStyle);
                            }
                        }
                    } else {
                        cell.setCellValue(value.toString());
                        cell.setCellStyle(dataStyle);
                    }

                    colIndex++;
                }
            }

            // Ajustar ancho de columnas de forma inteligente
            for (int i = 0; i < tabla.getColumns().size(); i++) {
                TableColumn<Map<String, Object>, ?> column = tabla.getColumns().get(i);
                String columnName = column.getText();

                // Ajustar ancho según el tipo de columna
                if ("#".equals(columnName)) {
                    // Columna de número - ancho fijo pequeño
                    sheet.setColumnWidth(i, 1500); // ~6 caracteres
                } else if ("Nombre Completo".equals(columnName)) {
                    // Columna de nombre - ancho mayor
                    sheet.autoSizeColumn(i);
                    int currentWidth = sheet.getColumnWidth(i);
                    sheet.setColumnWidth(i, Math.min(currentWidth + 500, 10000)); // Máximo ~40 caracteres
                } else if (columnName.startsWith("Acum ") ||
                           "Total Portafolio".equals(columnName) ||
                           "Puntos Examen".equals(columnName) ||
                           "% Examen".equals(columnName) ||
                           "Calif. Examen".equals(columnName) ||
                           "Puntos Parcial".equals(columnName) ||
                           "Calificación Parcial".equals(columnName)) {
                    // Columnas de cálculos - ancho mediano
                    sheet.setColumnWidth(i, 2500); // ~10 caracteres
                } else {
                    // Columnas de agregados - ancho pequeño/mediano
                    sheet.setColumnWidth(i, 2000); // ~8 caracteres
                }
            }

            // Merge de la celda del título
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tabla.getColumns().size() - 1));

            // Guardar archivo
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }

            workbook.close();

            // Preguntar si desea abrir el archivo
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Exportación Exitosa");
            confirmacion.setHeaderText("Archivo Excel generado correctamente");
            confirmacion.setContentText("¿Desea abrir el archivo ahora?\n\n" + file.getAbsolutePath());

            ButtonType btnAbrir = new ButtonType("Abrir");
            ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmacion.getButtonTypes().setAll(btnAbrir, btnCerrar);

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == btnAbrir) {
                    try {
                        // Abrir el archivo con la aplicación predeterminada del sistema
                        if (java.awt.Desktop.isDesktopSupported()) {
                            java.awt.Desktop.getDesktop().open(file);
                        }
                    } catch (Exception ex) {
                        LOG.error("Error al abrir el archivo", ex);
                        mostrarError("No se pudo abrir el archivo automáticamente.\nUbicación: " + file.getAbsolutePath());
                    }
                }
            });

        } catch (Exception e) {
            LOG.error("Error al exportar a Excel", e);
            mostrarError("Error al exportar a Excel: " + e.getMessage());
        }
    }

    /**
     * Calcula el valor acumulado para una columna de Excel
     */
    private String calcularAcumuladoParaExcel(Map<String, Object> item, String columnName) {
        // Este método debería calcular el acumulado basándose en los datos
        // Por simplicidad, retornamos un valor calculado previamente si existe
        return ""; // Placeholder - los valores ya se calculan en la tabla
    }

    /**
     * Busca la clave del agregado en el Map basándose en el nombre de la columna
     */
    private String buscarClaveAgregado(Map<String, Object> item, String columnName) {
        // Buscar en las claves del map que comienzan con "agregado_"
        for (String key : item.keySet()) {
            if (key.startsWith("agregado_")) {
                // Aquí deberíamos tener una mejor forma de mapear
                // Por ahora retornamos la primera coincidencia
                return key;
            }
        }
        return null;
    }
}
