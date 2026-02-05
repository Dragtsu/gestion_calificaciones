package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.*;
import com.alumnos.domain.port.in.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

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

    private final CalificacionConcentradoServicePort calificacionConcentradoService;
    private final AlumnoServicePort alumnoService;
    private final AgregadoServicePort agregadoService;
    private final CriterioServicePort criterioService;
    private final GrupoServicePort grupoService;
    private final MateriaServicePort materiaService;
    private final ExamenServicePort examenService;
    private final AlumnoExamenServicePort alumnoExamenService;

    private BorderPane mainContent;

    public ConcentradoController(CalificacionConcentradoServicePort calificacionConcentradoService,
                                 AlumnoServicePort alumnoService,
                                 AgregadoServicePort agregadoService,
                                 CriterioServicePort criterioService,
                                 GrupoServicePort grupoService,
                                 MateriaServicePort materiaService,
                                 ExamenServicePort examenService,
                                 AlumnoExamenServicePort alumnoExamenService) {
        this.calificacionConcentradoService = calificacionConcentradoService;
        this.alumnoService = alumnoService;
        this.agregadoService = agregadoService;
        this.criterioService = criterioService;
        this.grupoService = grupoService;
        this.materiaService = materiaService;
        this.examenService = examenService;
        this.alumnoExamenService = alumnoExamenService;
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

            Label lblTabla = new Label("Tabla de Calificaciones");
            lblTabla.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

            TableView<Map<String, Object>> tblCalificaciones = new TableView<>();
            tblCalificaciones.setEditable(true);
            tblCalificaciones.setMinHeight(400);

            // Botón para generar tabla
            btnBuscar.setOnAction(e -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAdvertencia("Debe seleccionar Grupo, Materia y Parcial");
                    return;
                }
                generarTablaCalificaciones(tblCalificaciones, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
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

            tablaPanel.getChildren().addAll(lblTabla, tblCalificaciones, btnGuardarCambios);

            vista.getChildren().addAll(lblTitulo, filtrosPanel, tablaPanel);

        } catch (Exception e) {
            manejarExcepcion("crear vista de concentrado de calificaciones", e);
        }

        return vista;
    }

    private void cargarMateriasPorGrupo(ComboBox<Materia> combo, Grupo grupo) {
        try {
            // Obtener asignaciones del grupo y luego las materias
            List<Materia> materias = materiaService.obtenerTodasLasMaterias().stream()
                .filter(m -> {
                    // Aquí podrías filtrar por las materias asignadas al grupo si tienes esa relación
                    return true; // Por ahora mostrar todas
                })
                .collect(Collectors.toList());
            combo.setItems(FXCollections.observableArrayList(materias));
        } catch (Exception e) {
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
                            TableColumn<Map<String, Object>, Boolean> colAgregadoCheck = new TableColumn<>(agregado.getNombre());
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

                            colAgregadoCheck.setCellFactory(col -> new TableCell<Map<String, Object>, Boolean>() {
                                private final CheckBox checkBox = new CheckBox();
                                private boolean isUpdating = false;

                                {
                                    checkBox.setStyle("-fx-alignment: CENTER;");
                                    checkBox.setOnAction(event -> {
                                        if (!isUpdating && getTableRow() != null && getTableRow().getItem() != null) {
                                            Map<String, Object> fila = getTableRow().getItem();
                                            fila.put("agregado_" + agregado.getId(), checkBox.isSelected());
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
                            TableColumn<Map<String, Object>, String> colAgregadoPuntos = new TableColumn<>(agregado.getNombre());
                            colAgregadoPuntos.setPrefWidth(100);
                            colAgregadoPuntos.setMinWidth(100);
                            colAgregadoPuntos.setMaxWidth(100);
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
                                            fila.put("agregado_" + agregado.getId(), valorTexto);
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
                            String.format("%.2f / %.2f", puntosObtenidos, puntuacionMaximaCriterio)
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
                TableColumn<Map<String, Object>, String> colPuntosExamen = new TableColumn<>("Puntos Examen");
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
                            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", calificacion));
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
                    valor != null ? String.format("%.2f", (Double) valor) : "0.00"
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
                Object calificacionExamenObj = fila.get("calificacionExamen");
                if (calificacionExamenObj != null && calificacionExamenObj instanceof Double) {
                    puntosExamen = (Double) calificacionExamenObj;
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
        return validarCampoNoVacio(txtPuntuacion.getText(), "Puntuación");
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

    private void cargarDatos(TableView<CalificacionConcentrado> tabla) {
        try {
            List<CalificacionConcentrado> calificaciones = calificacionConcentradoService.obtenerTodasLasCalificaciones();
            tabla.setItems(FXCollections.observableArrayList(calificaciones));
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

            // Calcular puntos del examen (calificación sobre 10)
            double puntosExamen = 0.0;
            Object aciertosExamenObj = fila.get("aciertosExamen");

            if (aciertosExamenObj != null && totalPuntosExamen != null && totalPuntosExamen > 0) {
                try {
                    int aciertosExamen = 0;
                    if (aciertosExamenObj instanceof Number) {
                        aciertosExamen = ((Number) aciertosExamenObj).intValue();
                    } else if (aciertosExamenObj instanceof String && !((String) aciertosExamenObj).isEmpty()) {
                        aciertosExamen = Integer.parseInt((String) aciertosExamenObj);
                    }

                    // Calcular porcentaje y convertir a calificación sobre 10
                    double porcentaje = (aciertosExamen * 100.0) / totalPuntosExamen;
                    puntosExamen = (porcentaje * 10.0) / 100.0;
                } catch (NumberFormatException e) {
                    // Si hay error, dejar puntosExamen en 0.0
                }
            }

            // Calcular puntos parcial y calificación parcial
            double puntosParcial = totalPortafolio + puntosExamen;
            double calificacionParcial = (puntosParcial * 10.0) / 100.0;

            // Actualizar la fila con los nuevos valores
            fila.put("puntosParcial", puntosParcial);
            fila.put("calificacionParcial", calificacionParcial);

        } catch (Exception e) {
            LOG.error("Error al recalcular puntos parcial: {}", e.getMessage());
        }
    }
}
