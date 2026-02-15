package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.model.GrupoMateria;
import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.GrupoMateriaServicePort;
import com.alumnos.domain.port.in.GrupoServicePort;
import com.alumnos.domain.port.in.MateriaServicePort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controlador para la gestión de asignaciones de materias a grupos
 * Responsabilidad: Manejar la vista y operaciones CRUD de asignaciones grupo-materia
 */
@Component
public class AsignacionesController extends BaseController {

    private final GrupoMateriaServicePort grupoMateriaService;
    private final GrupoServicePort grupoService;
    private final MateriaServicePort materiaService;
    private TableView<GrupoMateria> tablaAsignaciones; // 📋 Referencia a la tabla
    private ComboBox<Grupo> cmbGrupo; // 📋 Referencia al ComboBox de grupos
    private ComboBox<Materia> cmbMateria; // 📋 Referencia al ComboBox de materias

    public AsignacionesController(GrupoMateriaServicePort grupoMateriaService,
                                  GrupoServicePort grupoService,
                                  MateriaServicePort materiaService) {
        this.grupoMateriaService = grupoMateriaService;
        this.grupoService = grupoService;
        this.materiaService = materiaService;
    }

    public VBox crearVista() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        vista.setMaxHeight(Double.MAX_VALUE);
        vista.setMaxWidth(Double.MAX_VALUE);

        VBox formulario = crearFormulario();
        VBox tabla = crearTabla();
        javafx.scene.layout.VBox.setVgrow(tabla, javafx.scene.layout.Priority.ALWAYS);

        vista.getChildren().addAll(formulario, tabla);
        return vista;
    }

    private VBox crearFormulario() {
        VBox formulario = new VBox(10);
        formulario.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblFormTitle = new Label("Asignar Materia a Grupo");
        lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
        gridForm.setHgap(15);
        gridForm.setVgap(10);

        Label lblGrupo = new Label("Grupo:");
        lblGrupo.setStyle("-fx-font-weight: bold;");
        cmbGrupo = new ComboBox<>();
        cmbGrupo.setPromptText("Seleccione un grupo");
        cargarGrupos(cmbGrupo);

        Label lblMateria = new Label("Materia:");
        lblMateria.setStyle("-fx-font-weight: bold;");
        cmbMateria = new ComboBox<>();
        cmbMateria.setPromptText("Seleccione una materia");
        cargarMaterias(cmbMateria);

        gridForm.add(lblGrupo, 0, 0);
        gridForm.add(cmbGrupo, 1, 0);
        gridForm.add(lblMateria, 0, 1);
        gridForm.add(cmbMateria, 1, 1);

        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0;");

        Button btnGuardar = new Button("Asignar");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> guardarAsignacion(cmbGrupo, cmbMateria));

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> limpiarFormulario(cmbGrupo, cmbMateria));

        buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);

        formulario.getChildren().addAll(lblFormTitle, new javafx.scene.control.Separator(), gridForm, buttonBox);
        return formulario;
    }

    private VBox crearTabla() {
        VBox contenedor = new VBox(10);
        contenedor.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        contenedor.setMaxHeight(Double.MAX_VALUE);
        contenedor.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.VBox.setVgrow(contenedor, javafx.scene.layout.Priority.ALWAYS);

        Label lblTableTitle = new Label("Lista de Asignaciones");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        tablaAsignaciones = new TableView<>(); // 📋 Guardar referencia
        tablaAsignaciones.setMaxHeight(Double.MAX_VALUE);
        tablaAsignaciones.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.VBox.setVgrow(tablaAsignaciones, javafx.scene.layout.Priority.ALWAYS);

        TableColumn<GrupoMateria, String> colGrupo = new TableColumn<>("Grupo");
        colGrupo.setCellValueFactory(data -> {
            GrupoMateria gm = data.getValue();
            if (gm.getGrupoId() != null) {
                return grupoService.obtenerGrupoPorId(gm.getGrupoId())
                    .map(g -> new javafx.beans.property.SimpleStringProperty(String.valueOf(g.getId())))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        TableColumn<GrupoMateria, String> colMateria = new TableColumn<>("Materia");
        colMateria.setCellValueFactory(data -> {
            GrupoMateria gm = data.getValue();
            if (gm.getMateriaId() != null) {
                return materiaService.obtenerMateriaPorId(gm.getMateriaId())
                    .map(m -> new javafx.beans.property.SimpleStringProperty(m.getNombre()))
                    .orElse(new javafx.beans.property.SimpleStringProperty("N/A"));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        TableColumn<GrupoMateria, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<GrupoMateria, Void>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEliminar.setOnAction(e -> {
                    GrupoMateria asignacion = getTableView().getItems().get(getIndex());
                    eliminarAsignacion(asignacion, tablaAsignaciones);
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

        tablaAsignaciones.getColumns().addAll(colGrupo, colMateria, colAcciones);
        cargarDatos(tablaAsignaciones);

        contenedor.getChildren().addAll(lblTableTitle, tablaAsignaciones);
        return contenedor;
    }

    private void guardarAsignacion(ComboBox<Grupo> cmbGrupo, ComboBox<Materia> cmbMateria) {
        try {
            if (!validarFormulario(cmbGrupo, cmbMateria)) return;

            GrupoMateria asignacion = GrupoMateria.builder()
                .grupoId(cmbGrupo.getValue().getId())
                .materiaId(cmbMateria.getValue().getId())
                .build();

            grupoMateriaService.asignarMateriaAGrupo(asignacion);
            mostrarExito("Asignación guardada correctamente");
            limpiarFormulario(cmbGrupo, cmbMateria);

            // ⚡ RECARGAR LA TABLA después de guardar
            if (tablaAsignaciones != null) {
                cargarDatos(tablaAsignaciones);
            }
        } catch (Exception e) {
            manejarExcepcion("guardar asignación", e);
        }
    }

    private void eliminarAsignacion(GrupoMateria asignacion, TableView<GrupoMateria> tabla) {
        try {
            if (confirmarAccion("Confirmar eliminación", "¿Está seguro de eliminar esta asignación?")) {
                grupoMateriaService.eliminarAsignacion(asignacion.getId());
                mostrarExito("Asignación eliminada correctamente");
                cargarDatos(tabla);
            }
        } catch (Exception e) {
            manejarExcepcion("eliminar asignación", e);
        }
    }

    private boolean validarFormulario(ComboBox<Grupo> cmbGrupo, ComboBox<Materia> cmbMateria) {
        if (cmbGrupo.getValue() == null) {
            mostrarError("Debe seleccionar un grupo");
            return false;
        }
        if (cmbMateria.getValue() == null) {
            mostrarError("Debe seleccionar una materia");
            return false;
        }
        return true;
    }

    private void limpiarFormulario(ComboBox<Grupo> cmbGrupo, ComboBox<Materia> cmbMateria) {
        cmbGrupo.setValue(null);
        cmbMateria.setValue(null);
    }

    private void cargarGrupos(ComboBox<Grupo> combo) {
        try {
            List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
            combo.setItems(FXCollections.observableArrayList(grupos));
        } catch (Exception e) {
            manejarExcepcion("cargar grupos", e);
        }
    }

    /**
     * Método público para recargar la lista de grupos (llamado desde GruposController)
     */
    public void refrescarListaGrupos() {
        if (cmbGrupo != null) {
            cargarGrupos(cmbGrupo);
        }
    }

    /**
     * Método público para recargar la lista de materias (llamado desde MateriasController)
     */
    public void refrescarListaMaterias() {
        if (cmbMateria != null) {
            cargarMaterias(cmbMateria);
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

    private void cargarDatos(TableView<GrupoMateria> tabla) {
        try {
            List<GrupoMateria> asignaciones = grupoMateriaService.obtenerTodasLasAsignaciones();
            tabla.setItems(FXCollections.observableArrayList(asignaciones));
            tabla.refresh(); // 🔄 Forzar refresco de la tabla para que se rendericen los botones

            // 📏 Ajustar columnas al contenido (incluyendo botones)
            Platform.runLater(() -> ajustarColumnasAlContenido(tabla));
        } catch (Exception e) {
            manejarExcepcion("cargar asignaciones", e);
        }
    }

    private void ajustarColumnasAlContenido(TableView<GrupoMateria> tabla) {
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        for (TableColumn<GrupoMateria, ?> columna : tabla.getColumns()) {
            if ("Acciones".equals(columna.getText())) {
                columna.setPrefWidth(120);
                columna.setMinWidth(120);
                columna.setMaxWidth(120);
                continue;
            }

            double anchoMaximo = calcularAnchoColumna(columna);
            columna.setPrefWidth(anchoMaximo);
        }
    }

    private double calcularAnchoColumna(TableColumn<GrupoMateria, ?> columna) {
        javafx.scene.text.Text textoHeader = new javafx.scene.text.Text(columna.getText());
        double anchoMaximo = textoHeader.getLayoutBounds().getWidth() + 40;

        int filasARevisar = Math.min(tablaAsignaciones.getItems().size(), 50);

        for (int i = 0; i < filasARevisar; i++) {
            GrupoMateria asignacion = tablaAsignaciones.getItems().get(i);
            String valorCelda = obtenerValorCelda(columna, asignacion);

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

    private String obtenerValorCelda(TableColumn<GrupoMateria, ?> columna, GrupoMateria asignacion) {
        String nombreColumna = columna.getText();

        switch (nombreColumna) {
            case "Grupo":
                if (asignacion.getGrupoId() != null) {
                    try {
                        return grupoService.obtenerGrupoPorId(asignacion.getGrupoId())
                            .map(g -> String.valueOf(g.getId()))
                            .orElse("N/A");
                    } catch (Exception e) {
                        return "N/A";
                    }
                }
                return "N/A";
            case "Materia":
                if (asignacion.getMateriaId() != null) {
                    try {
                        return materiaService.obtenerMateriaPorId(asignacion.getMateriaId())
                            .map(Materia::getNombre)
                            .orElse("N/A");
                    } catch (Exception e) {
                        return "N/A";
                    }
                }
                return "N/A";
            default:
                return "";
        }
    }
}
