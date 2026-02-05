package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Examen;
import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.ExamenServicePort;
import com.alumnos.domain.port.in.GrupoServicePort;
import com.alumnos.domain.port.in.MateriaServicePort;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de exámenes
 * Responsabilidad: Manejar la vista y operaciones CRUD de exámenes
 */
@Component
public class ExamenesController extends BaseController {

    private final ExamenServicePort examenService;
    private final GrupoServicePort grupoService;
    private final MateriaServicePort materiaService;
    private TableView<Examen> tablaExamenes; // 📋 Referencia a la tabla

    public ExamenesController(ExamenServicePort examenService,
                             GrupoServicePort grupoService,
                             MateriaServicePort materiaService) {
        this.examenService = examenService;
        this.grupoService = grupoService;
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
                                                   txtTotalPuntos, dpFechaAplicacion));

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> {
            cmbGrupo.setValue(null);
            cmbMateria.setValue(null);
            cmbParcial.setValue(null);
            txtTotalPuntos.clear();
            dpFechaAplicacion.setValue(null);
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
                data.getValue().getFechaAplicacion().toString() : "N/A"));

        TableColumn<Examen, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<Examen, Void>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEliminar.setOnAction(e -> {
                    Examen examen = getTableView().getItems().get(getIndex());
                    eliminarExamen(examen, tablaExamenes);
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

        tablaExamenes.getColumns().addAll(colGrupo, colMateria, colParcial, colTotalPuntos, colFechaAplicacion, colAcciones);
        cargarDatos(tablaExamenes);

        contenedor.getChildren().addAll(lblTableTitle, tablaExamenes);
        return contenedor;
    }

    private void guardarExamen(ComboBox<Grupo> cmbGrupo, ComboBox<Materia> cmbMateria,
                              ComboBox<Integer> cmbParcial, TextField txtTotalPuntos,
                              DatePicker dpFechaAplicacion) {
        try {
            if (!validarFormulario(cmbGrupo, cmbMateria, cmbParcial, txtTotalPuntos)) return;

            // ⚠️ Verificar que no exista un examen duplicado
            Long grupoId = cmbGrupo.getValue().getId();
            Long materiaId = cmbMateria.getValue().getId();
            Integer parcial = cmbParcial.getValue();

            Optional<Examen> examenExistente = examenService.obtenerExamenPorGrupoMateriaParcial(
                grupoId, materiaId, parcial);

            if (examenExistente.isPresent()) {
                mostrarError("Ya existe un examen registrado para este grupo, materia y parcial");
                return;
            }

            Examen examen = Examen.builder()
                .grupoId(grupoId)
                .materiaId(materiaId)
                .parcial(parcial)
                .totalPuntosExamen(Integer.parseInt(txtTotalPuntos.getText()))
                .fechaAplicacion(dpFechaAplicacion.getValue())
                .build();

            examenService.crearExamen(examen);
            mostrarExito("Examen guardado correctamente");
            limpiarFormulario(cmbGrupo, cmbMateria, cmbParcial, txtTotalPuntos, dpFechaAplicacion);

            // ⚡ RECARGAR LA TABLA después de guardar
            if (tablaExamenes != null) {
                cargarDatos(tablaExamenes);
            }
        } catch (NumberFormatException e) {
            mostrarError("El total de puntos debe ser un valor numérico");
        } catch (Exception e) {
            manejarExcepcion("guardar examen", e);
        }
    }

    private void eliminarExamen(Examen examen, TableView<Examen> tabla) {
        try {
            if (confirmarAccion("Confirmar eliminación", "¿Está seguro de eliminar este examen?")) {
                examenService.eliminarExamen(examen.getId());
                mostrarExito("Examen eliminado correctamente");
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
        } catch (Exception e) {
            manejarExcepcion("cargar exámenes", e);
        }
    }
}
