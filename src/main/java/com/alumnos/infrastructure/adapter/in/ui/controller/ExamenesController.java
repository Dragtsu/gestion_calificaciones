package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.AlumnoExamen;
import com.alumnos.domain.model.Criterio;
import com.alumnos.domain.model.Examen;
import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.AlumnoExamenServicePort;
import com.alumnos.domain.port.in.CriterioServicePort;
import com.alumnos.domain.port.in.ExamenServicePort;
import com.alumnos.domain.port.in.GrupoServicePort;
import com.alumnos.domain.port.in.MateriaServicePort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de exámenes
 * Responsabilidad: Manejar la vista y operaciones CRUD de exámenes
 */
@Component
public class ExamenesController extends BaseController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final ExamenServicePort examenService;
    private final GrupoServicePort grupoService;
    private final MateriaServicePort materiaService;
    private final CriterioServicePort criterioService;
    private final AlumnoExamenServicePort alumnoExamenService;
    private TableView<Examen> tablaExamenes; // 📋 Referencia a la tabla
    private Examen examenEnEdicion; // 📝 Para rastrear si estamos editando

    public ExamenesController(ExamenServicePort examenService,
                             GrupoServicePort grupoService,
                             MateriaServicePort materiaService,
                             CriterioServicePort criterioService,
                             AlumnoExamenServicePort alumnoExamenService) {
        this.examenService = examenService;
        this.grupoService = grupoService;
        this.materiaService = materiaService;
        this.criterioService = criterioService;
        this.alumnoExamenService = alumnoExamenService;
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

        Label lblFormTitle = new Label("Registrar Nuevo Examen");
        lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
        gridForm.setHgap(15);
        gridForm.setVgap(10);

        Label lblGrupo = new Label("Grupo:");
        lblGrupo.setStyle("-fx-font-weight: bold;");
        ComboBox<Grupo> cmbGrupo = new ComboBox<>();
        cmbGrupo.setPromptText("Seleccione un grupo");
        cargarGrupos(cmbGrupo);

        Label lblMateria = new Label("Materia:");
        lblMateria.setStyle("-fx-font-weight: bold;");
        ComboBox<Materia> cmbMateria = new ComboBox<>();
        cmbMateria.setPromptText("Seleccione una materia");
        cargarMaterias(cmbMateria);

        Label lblParcial = new Label("Parcial:");
        lblParcial.setStyle("-fx-font-weight: bold;");
        ComboBox<Integer> cmbParcial = new ComboBox<>();
        cmbParcial.setPromptText("Parcial");
        cmbParcial.setItems(FXCollections.observableArrayList(1, 2, 3));

        Label lblTotalPuntos = new Label("Total de Puntos:");
        lblTotalPuntos.setStyle("-fx-font-weight: bold;");
        TextField txtTotalPuntos = new TextField();
        txtTotalPuntos.setPromptText("Total de puntos del examen");

        Label lblFecha = new Label("Fecha de Aplicación:");
        lblFecha.setStyle("-fx-font-weight: bold;");
        DatePicker dpFechaAplicacion = new DatePicker();
        dpFechaAplicacion.setPromptText("Fecha de aplicación");

        gridForm.add(lblGrupo, 0, 0);
        gridForm.add(cmbGrupo, 1, 0);
        gridForm.add(lblMateria, 0, 1);
        gridForm.add(cmbMateria, 1, 1);
        gridForm.add(lblParcial, 0, 2);
        gridForm.add(cmbParcial, 1, 2);
        gridForm.add(lblTotalPuntos, 0, 3);
        gridForm.add(txtTotalPuntos, 1, 3);
        gridForm.add(lblFecha, 0, 4);
        gridForm.add(dpFechaAplicacion, 1, 4);

        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0;");

        Button btnGuardar = new Button("Guardar");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> guardarExamen(cmbGrupo, cmbMateria, cmbParcial,
                                                   txtTotalPuntos, dpFechaAplicacion, lblFormTitle));

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> {
            limpiarFormulario(cmbGrupo, cmbMateria, cmbParcial, txtTotalPuntos, dpFechaAplicacion);
            examenEnEdicion = null;
            lblFormTitle.setText("Registrar Nuevo Examen");
            // Habilitar campos
            cmbGrupo.setDisable(false);
            cmbMateria.setDisable(false);
            cmbParcial.setDisable(false);
        });

        buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);

        formulario.getChildren().addAll(lblFormTitle, new javafx.scene.control.Separator(), gridForm, buttonBox);
        return formulario;
    }

    private VBox crearTabla() {
        VBox contenedor = new VBox(10);
        contenedor.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblTableTitle = new Label("Lista de Exámenes");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        tablaExamenes = new TableView<>(); // 📋 Guardar referencia

        TableColumn<Examen, String> colGrupo = new TableColumn<>("Grupo");
        colGrupo.setCellValueFactory(data -> {
            Examen examen = data.getValue();
            if (examen.getGrupoId() != null) {
                return grupoService.obtenerGrupoPorId(examen.getGrupoId())
                    .map(g -> new javafx.beans.property.SimpleStringProperty(String.valueOf(g.getId())))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        TableColumn<Examen, String> colMateria = new TableColumn<>("Materia");
        colMateria.setCellValueFactory(data -> {
            Examen examen = data.getValue();
            if (examen.getMateriaId() != null) {
                return materiaService.obtenerMateriaPorId(examen.getMateriaId())
                    .map(m -> new javafx.beans.property.SimpleStringProperty(m.getNombre()))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        TableColumn<Examen, String> colParcial = new TableColumn<>("Parcial");
        colParcial.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getParcial() != null ?
                String.valueOf(data.getValue().getParcial()) : "N/A"));

        TableColumn<Examen, String> colTotalPuntos = new TableColumn<>("Total Puntos");
        colTotalPuntos.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTotalPuntosExamen() != null ?
                String.valueOf(data.getValue().getTotalPuntosExamen()) : "N/A"));

        TableColumn<Examen, String> colFechaAplicacion = new TableColumn<>("Fecha Aplicación");
        colFechaAplicacion.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFechaAplicacion() != null ?
                data.getValue().getFechaAplicacion().format(FORMATO_FECHA) : "N/A"));

        TableColumn<Examen, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<Examen, Void>() {
            private final javafx.scene.layout.HBox botonesBox = new javafx.scene.layout.HBox(5);
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEditar.setOnAction(e -> {
                    Examen examen = getTableView().getItems().get(getIndex());
                    cargarExamenParaEditar(examen);
                });

                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEliminar.setOnAction(e -> {
                    Examen examen = getTableView().getItems().get(getIndex());
                    eliminarExamen(examen, tablaExamenes);
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

        tablaExamenes.getColumns().addAll(colGrupo, colMateria, colParcial, colTotalPuntos, colFechaAplicacion, colAcciones);
        cargarDatos(tablaExamenes);

        contenedor.getChildren().addAll(lblTableTitle, tablaExamenes);
        return contenedor;
    }

    private void guardarExamen(ComboBox<Grupo> cmbGrupo, ComboBox<Materia> cmbMateria,
                              ComboBox<Integer> cmbParcial, TextField txtTotalPuntos,
                              DatePicker dpFechaAplicacion, Label lblFormTitle) {
        try {
            if (!validarFormulario(cmbGrupo, cmbMateria, cmbParcial, txtTotalPuntos)) return;

            Long grupoId = cmbGrupo.getValue().getId();
            Long materiaId = cmbMateria.getValue().getId();
            Integer parcial = cmbParcial.getValue();
            Integer totalPuntosExamen = Integer.parseInt(txtTotalPuntos.getText());

            // Modo edición
            if (examenEnEdicion != null) {
                // ⚠️ VALIDAR QUE LA SUMA NO EXCEDA 100 PUNTOS (considerando el examen actual)
                if (!validarLimitePuntosParaEdicion(materiaId, parcial, totalPuntosExamen, examenEnEdicion.getId())) {
                    return;
                }

                // Crear un nuevo objeto con todos los datos actualizados
                // Esto asegura que no haya problemas de referencia con el objeto en la tabla
                Examen examenActualizado = Examen.builder()
                    .id(examenEnEdicion.getId())
                    .grupoId(examenEnEdicion.getGrupoId())
                    .materiaId(examenEnEdicion.getMateriaId())
                    .parcial(examenEnEdicion.getParcial())
                    .totalPuntosExamen(totalPuntosExamen)
                    .fechaAplicacion(dpFechaAplicacion.getValue())
                    .build();

                examenService.actualizarExamen(examenActualizado);

                // ⚡ RECARGAR LA TABLA inmediatamente después de actualizar
                if (tablaExamenes != null) {
                    cargarDatos(tablaExamenes);
                    tablaExamenes.refresh(); // Forzar refresh visual
                }

                mostrarExito("Examen actualizado correctamente");
                examenEnEdicion = null;
                lblFormTitle.setText("Registrar Nuevo Examen");
                // Habilitar campos
                cmbGrupo.setDisable(false);
                cmbMateria.setDisable(false);
                cmbParcial.setDisable(false);
            } else {
                // Modo creación
                // ⚠️ Verificar que no exista un examen duplicado
                Optional<Examen> examenExistente = examenService.obtenerExamenPorGrupoMateriaParcial(
                    grupoId, materiaId, parcial);

                if (examenExistente.isPresent()) {
                    mostrarError("Ya existe un examen registrado para este grupo, materia y parcial");
                    return;
                }

                // ⚠️ VALIDAR QUE LA SUMA NO EXCEDA 100 PUNTOS
                if (!validarLimitePuntos(materiaId, parcial, totalPuntosExamen)) {
                    return;
                }

                Examen examen = Examen.builder()
                    .grupoId(grupoId)
                    .materiaId(materiaId)
                    .parcial(parcial)
                    .totalPuntosExamen(totalPuntosExamen)
                    .fechaAplicacion(dpFechaAplicacion.getValue())
                    .build();

                examenService.crearExamen(examen);
                mostrarExito("Examen guardado correctamente");

                // ⚡ RECARGAR LA TABLA después de guardar
                if (tablaExamenes != null) {
                    cargarDatos(tablaExamenes);
                }
            }

            limpiarFormulario(cmbGrupo, cmbMateria, cmbParcial, txtTotalPuntos, dpFechaAplicacion);
        } catch (NumberFormatException e) {
            mostrarError("El total de puntos debe ser un valor numérico");
        } catch (Exception e) {
            manejarExcepcion("guardar examen", e);
        }
    }

    private void eliminarExamen(Examen examen, TableView<Examen> tabla) {
        try {
            // Verificar si existen calificaciones de examen registradas
            List<AlumnoExamen> alumnosConExamen = alumnoExamenService.obtenerAlumnoExamenPorExamen(examen.getId());

            String mensaje;
            if (!alumnosConExamen.isEmpty()) {
                mensaje = String.format(
                    "⚠️ ADVERTENCIA: Este examen tiene %d alumno(s) con calificaciones registradas.\n\n" +
                    "Si elimina este examen, también se eliminarán todas las calificaciones " +
                    "registradas de los alumnos para este examen.\n\n" +
                    "¿Está seguro de que desea continuar?",
                    alumnosConExamen.size()
                );
            } else {
                mensaje = "¿Está seguro de eliminar este examen?";
            }

            if (confirmarAccion("Confirmar eliminación", mensaje)) {
                // Primero eliminar todos los registros de AlumnoExamen vinculados
                for (AlumnoExamen alumnoExamen : alumnosConExamen) {
                    alumnoExamenService.eliminarAlumnoExamen(alumnoExamen.getId());
                }

                // Luego eliminar el examen
                examenService.eliminarExamen(examen.getId());

                if (!alumnosConExamen.isEmpty()) {
                    mostrarExito(String.format(
                        "Examen y %d calificación(es) de alumno(s) eliminados correctamente",
                        alumnosConExamen.size()
                    ));
                } else {
                    mostrarExito("Examen eliminado correctamente");
                }

                cargarDatos(tabla);
            }
        } catch (Exception e) {
            manejarExcepcion("eliminar examen", e);
        }
    }

    private boolean validarFormulario(ComboBox<Grupo> cmbGrupo, ComboBox<Materia> cmbMateria,
                                     ComboBox<Integer> cmbParcial, TextField txtTotalPuntos) {
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
        return validarCampoNoVacio(txtTotalPuntos.getText(), "Total de puntos");
    }

    private void limpiarFormulario(ComboBox<Grupo> cmbGrupo, ComboBox<Materia> cmbMateria,
                                   ComboBox<Integer> cmbParcial, TextField txtTotalPuntos,
                                   DatePicker dpFechaAplicacion) {
        cmbGrupo.setValue(null);
        cmbMateria.setValue(null);
        cmbParcial.setValue(null);
        txtTotalPuntos.clear();
        dpFechaAplicacion.setValue(null);
    }

    private void cargarGrupos(ComboBox<Grupo> combo) {
        try {
            List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
            combo.setItems(FXCollections.observableArrayList(grupos));
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

    private void cargarDatos(TableView<Examen> tabla) {
        try {
            List<Examen> examenes = examenService.obtenerTodosLosExamenes();
            tabla.setItems(FXCollections.observableArrayList(examenes));

            // 📏 Ajustar columnas al contenido (incluyendo botones)
            Platform.runLater(() -> ajustarColumnasAlContenido(tabla));
        } catch (Exception e) {
            manejarExcepcion("cargar exámenes", e);
        }
    }

    /**
     * Valida que la suma de puntuación máxima de criterios y total de puntos del examen
     * no supere 100 puntos para una materia y parcial específicos
     *
     * @param materiaId ID de la materia
     * @param parcial Número de parcial
     * @param totalPuntosExamen Total de puntos del examen que se está guardando
     * @return true si la validación es exitosa, false si se excede el límite
     */
    private boolean validarLimitePuntos(Long materiaId, Integer parcial, Integer totalPuntosExamen) {
        try {
            // Si no hay puntos del examen, no validar
            if (totalPuntosExamen == null || totalPuntosExamen == 0) {
                return true;
            }

            // Obtener todos los criterios de la materia y parcial
            List<Criterio> criteriosExistentes = criterioService.obtenerCriteriosPorMateria(materiaId)
                .stream()
                .filter(c -> c.getParcial().equals(parcial))
                .toList();

            // Sumar la puntuación máxima de todos los criterios
            double totalCriterios = criteriosExistentes.stream()
                .mapToDouble(c -> c.getPuntuacionMaxima() != null ? c.getPuntuacionMaxima() : 0.0)
                .sum();

            // Calcular el total sumando criterios + examen
            double sumaTotal = totalCriterios + totalPuntosExamen;

            // Validar que no exceda 100
            if (sumaTotal > 100) {
                String mensaje = String.format(
                    "⚠️ SE SOBREPASA EL MÁXIMO DE PUNTOS PERMITIDOS\n\n" +
                    "Desglose:\n" +
                    "• Suma de criterios existentes: %.1f puntos\n" +
                    "• Total puntos del examen: %.1f puntos\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "• TOTAL: %.1f puntos\n\n" +
                    "⚠️ El máximo permitido es 100 puntos.\n" +
                    "Sobrepasa por: %.1f puntos\n\n" +
                    "Por favor, ajuste el total de puntos del examen.",
                    totalCriterios,
                    (double) totalPuntosExamen,
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

    /**
     * Carga un examen en el formulario para editarlo
     * Solo permite editar Total de Puntos y Fecha
     *
     * @param examen El examen a editar
     */
    private void cargarExamenParaEditar(Examen examen) {
        try {
            // Encontrar el formulario y sus controles
            VBox vista = (VBox) tablaExamenes.getParent().getParent();
            VBox formulario = (VBox) vista.getChildren().get(0);

            // Obtener el título
            Label lblFormTitle = (Label) formulario.getChildren().get(0);
            lblFormTitle.setText("Editar Examen");

            // Obtener el GridPane que contiene los controles
            javafx.scene.layout.GridPane gridForm = (javafx.scene.layout.GridPane) formulario.getChildren().get(2);

            // Obtener los controles del formulario
            @SuppressWarnings("unchecked")
            ComboBox<Grupo> cmbGrupo = (ComboBox<Grupo>) gridForm.getChildren().get(1);
            @SuppressWarnings("unchecked")
            ComboBox<Materia> cmbMateria = (ComboBox<Materia>) gridForm.getChildren().get(3);
            @SuppressWarnings("unchecked")
            ComboBox<Integer> cmbParcial = (ComboBox<Integer>) gridForm.getChildren().get(5);
            TextField txtTotalPuntos = (TextField) gridForm.getChildren().get(7);
            DatePicker dpFechaAplicacion = (DatePicker) gridForm.getChildren().get(9);

            // Cargar datos del examen
            grupoService.obtenerGrupoPorId(examen.getGrupoId()).ifPresent(cmbGrupo::setValue);
            materiaService.obtenerMateriaPorId(examen.getMateriaId()).ifPresent(cmbMateria::setValue);
            cmbParcial.setValue(examen.getParcial());
            txtTotalPuntos.setText(String.valueOf(examen.getTotalPuntosExamen()));
            dpFechaAplicacion.setValue(examen.getFechaAplicacion());

            // Deshabilitar campos no editables
            cmbGrupo.setDisable(true);
            cmbMateria.setDisable(true);
            cmbParcial.setDisable(true);

            // Guardar referencia del examen en edición
            examenEnEdicion = examen;


        } catch (Exception e) {
            manejarExcepcion("cargar examen para editar", e);
        }
    }

    /**
     * Valida que la suma de puntuación máxima de criterios y total de puntos del examen
     * no supere 100 puntos para una materia y parcial específicos
     * Esta versión excluye el examen que se está editando del cálculo
     *
     * @param materiaId ID de la materia
     * @param parcial Número de parcial
     * @param totalPuntosExamen Total de puntos del examen que se está guardando
     * @param examenIdActual ID del examen que se está editando
     * @return true si la validación es exitosa, false si se excede el límite
     */
    private boolean validarLimitePuntosParaEdicion(Long materiaId, Integer parcial,
                                                    Integer totalPuntosExamen, Long examenIdActual) {
        try {
            // Si no hay puntos del examen, no validar
            if (totalPuntosExamen == null || totalPuntosExamen == 0) {
                return true;
            }

            // Obtener todos los criterios de la materia y parcial
            List<Criterio> criteriosExistentes = criterioService.obtenerCriteriosPorMateria(materiaId)
                .stream()
                .filter(c -> c.getParcial().equals(parcial))
                .toList();

            // Sumar la puntuación máxima de todos los criterios
            double totalCriterios = criteriosExistentes.stream()
                .mapToDouble(c -> c.getPuntuacionMaxima() != null ? c.getPuntuacionMaxima() : 0.0)
                .sum();

            // Obtener otros exámenes de la misma materia y parcial (excluyendo el actual)
            List<Examen> otrosExamenes = examenService.obtenerTodosLosExamenes().stream()
                .filter(e -> e.getMateriaId().equals(materiaId)
                          && e.getParcial().equals(parcial)
                          && !e.getId().equals(examenIdActual))
                .toList();

            // Sumar puntos de otros exámenes
            double totalOtrosExamenes = otrosExamenes.stream()
                .mapToDouble(e -> e.getTotalPuntosExamen() != null ? e.getTotalPuntosExamen() : 0.0)
                .sum();

            // Calcular el total sumando criterios + otros exámenes + examen actual
            double sumaTotal = totalCriterios + totalOtrosExamenes + totalPuntosExamen;

            // Validar que no exceda 100
            if (sumaTotal > 100) {
                String mensaje = String.format(
                    "⚠️ SE SOBREPASA EL MÁXIMO DE PUNTOS PERMITIDOS\n\n" +
                    "Desglose:\n" +
                    "• Suma de criterios existentes: %.1f puntos\n" +
                    "• Suma de otros exámenes: %.1f puntos\n" +
                    "• Total puntos de este examen: %.1f puntos\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "• TOTAL: %.1f puntos\n\n" +
                    "⚠️ El máximo permitido es 100 puntos.\n" +
                    "Sobrepasa por: %.1f puntos\n\n" +
                    "Por favor, ajuste el total de puntos del examen.",
                    totalCriterios,
                    totalOtrosExamenes,
                    (double) totalPuntosExamen,
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

    private void ajustarColumnasAlContenido(TableView<Examen> tabla) {
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        for (TableColumn<Examen, ?> columna : tabla.getColumns()) {
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

    private double calcularAnchoColumna(TableColumn<Examen, ?> columna) {
        javafx.scene.text.Text textoHeader = new javafx.scene.text.Text(columna.getText());
        double anchoMaximo = textoHeader.getLayoutBounds().getWidth() + 40;

        int filasARevisar = Math.min(tablaExamenes.getItems().size(), 50);

        for (int i = 0; i < filasARevisar; i++) {
            Examen examen = tablaExamenes.getItems().get(i);
            String valorCelda = obtenerValorCelda(columna, examen);

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

    private String obtenerValorCelda(TableColumn<Examen, ?> columna, Examen examen) {
        String nombreColumna = columna.getText();

        switch (nombreColumna) {
            case "Grupo":
                if (examen.getGrupoId() != null) {
                    try {
                        Optional<Grupo> grupo = grupoService.obtenerGrupoPorId(examen.getGrupoId());
                        return grupo.map(g -> String.valueOf(g.getId())).orElse("N/A");
                    } catch (Exception e) {
                        return "N/A";
                    }
                }
                return "N/A";
            case "Materia":
                if (examen.getMateriaId() != null) {
                    try {
                        Optional<Materia> materia = materiaService.obtenerMateriaPorId(examen.getMateriaId());
                        return materia.map(Materia::getNombre).orElse("N/A");
                    } catch (Exception e) {
                        return "N/A";
                    }
                }
                return "N/A";
            case "Parcial":
                return examen.getParcial() != null ? String.valueOf(examen.getParcial()) : "N/A";
            case "Total Puntos":
                return examen.getTotalPuntosExamen() != null ? String.valueOf(examen.getTotalPuntosExamen()) : "N/A";
            case "Fecha Aplicación":
                return examen.getFechaAplicacion() != null ? examen.getFechaAplicacion().format(FORMATO_FECHA) : "N/A";
            default:
                return "";
        }
    }
}
