package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Alumno;
import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.port.in.AlumnoServicePort;
import com.alumnos.domain.port.in.GrupoServicePort;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controlador para la gestión de estudiantes (alumnos)
 * Responsabilidad: Manejar la vista y operaciones CRUD de estudiantes
 */
@Component
public class EstudiantesController extends BaseController {

    private final AlumnoServicePort alumnoService;
    private final GrupoServicePort grupoService;
    private TableView<Alumno> tablaAlumnos; // 📋 Referencia a la tabla

    // 📝 Campos del formulario
    private TextField txtNombre;
    private TextField txtApellidoP;
    private TextField txtApellidoM;
    private ComboBox<Grupo> cmbGrupo;
    private Label lblFormTitle;
    private Button btnGuardar;
    private Long alumnoIdEnEdicion = null; // ID del alumno en edición (null = crear nuevo)

    public EstudiantesController(AlumnoServicePort alumnoService, GrupoServicePort grupoService) {
        this.alumnoService = alumnoService;
        this.grupoService = grupoService;
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

        lblFormTitle = new Label("Registrar Nuevo Alumno");
        lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
        gridForm.setHgap(15);
        gridForm.setVgap(10);

        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        txtNombre = new TextField();
        txtNombre.setPromptText("Ingrese el nombre");

        Label lblApellidoP = new Label("Apellido Paterno:");
        lblApellidoP.setStyle("-fx-font-weight: bold;");
        txtApellidoP = new TextField();
        txtApellidoP.setPromptText("Ingrese el apellido paterno");

        Label lblApellidoM = new Label("Apellido Materno:");
        lblApellidoM.setStyle("-fx-font-weight: bold;");
        txtApellidoM = new TextField();
        txtApellidoM.setPromptText("Ingrese el apellido materno");

        Label lblGrupo = new Label("Grupo:");
        lblGrupo.setStyle("-fx-font-weight: bold;");
        cmbGrupo = new ComboBox<>();
        cmbGrupo.setPromptText("Seleccione un grupo");
        cargarGrupos(cmbGrupo);
        
        gridForm.add(lblNombre, 0, 0);
        gridForm.add(txtNombre, 1, 0);
        gridForm.add(lblApellidoP, 0, 1);
        gridForm.add(txtApellidoP, 1, 1);
        gridForm.add(lblApellidoM, 0, 2);
        gridForm.add(txtApellidoM, 1, 2);
        gridForm.add(lblGrupo, 0, 3);
        gridForm.add(cmbGrupo, 1, 3);

        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0;");

        btnGuardar = new Button("Guardar");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> guardarAlumno());

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> limpiarFormulario());

        buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);

        formulario.getChildren().addAll(lblFormTitle, new javafx.scene.control.Separator(), gridForm, buttonBox);
        return formulario;
    }

    private VBox crearTabla() {
        VBox contenedor = new VBox(10);
        contenedor.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblTableTitle = new Label("Lista de Alumnos");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        tablaAlumnos = new TableView<>(); // 📋 Guardar referencia

        TableColumn<Alumno, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        
        TableColumn<Alumno, String> colApellidoP = new TableColumn<>("Apellido Paterno");
        colApellidoP.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getApellidoPaterno()));

        TableColumn<Alumno, String> colApellidoM = new TableColumn<>("Apellido Materno");
        colApellidoM.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getApellidoMaterno()));

        // Columna de acciones con botones editar y eliminar
        TableColumn<Alumno, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new javafx.scene.control.TableCell<Alumno, Void>() {
            private final javafx.scene.layout.HBox botonesBox = new javafx.scene.layout.HBox(5);
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEditar.setOnAction(event -> {
                    Alumno alumno = getTableView().getItems().get(getIndex());
                    cargarAlumnoEnFormulario(alumno);
                });

                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEliminar.setOnAction(event -> {
                    Alumno alumno = getTableView().getItems().get(getIndex());
                    eliminarAlumno(alumno);
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

        tablaAlumnos.getColumns().addAll(colNombre, colApellidoP, colApellidoM, colAcciones);
        cargarDatos(tablaAlumnos);

        contenedor.getChildren().addAll(lblTableTitle, tablaAlumnos);
        return contenedor;
    }

    private void guardarAlumno() {
        try {
            if (!validarFormulario()) return;

            Alumno alumno = Alumno.builder()
                .id(alumnoIdEnEdicion)  // Si es null = crear, si tiene valor = actualizar
                .nombre(txtNombre.getText().trim())
                .apellidoPaterno(txtApellidoP.getText().trim())
                .apellidoMaterno(txtApellidoM.getText().trim())
                .grupoId(cmbGrupo.getValue().getId())
                .build();
            
            if (alumnoIdEnEdicion == null) {
                // Crear nuevo alumno
                alumnoService.crearAlumno(alumno);
                mostrarExito("Alumno creado correctamente");
            } else {
                // Actualizar alumno existente
                alumnoService.actualizarAlumno(alumno);
                mostrarExito("Alumno actualizado correctamente");
            }

            limpiarFormulario();

            // ⚡ RECARGAR LA TABLA después de guardar
            if (tablaAlumnos != null) {
                cargarDatos(tablaAlumnos);
            }
        } catch (Exception e) {
            manejarExcepcion("guardar alumno", e);
        }
    }

    private boolean validarFormulario() {
        return validarCampoNoVacio(txtNombre.getText(), "Nombre") &&
               validarCampoNoVacio(txtApellidoP.getText(), "Apellido Paterno") &&
               validarCampoNoVacio(txtApellidoM.getText(), "Apellido Materno") &&
               cmbGrupo.getValue() != null;
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellidoP.clear();
        txtApellidoM.clear();
        cmbGrupo.setValue(null);
        alumnoIdEnEdicion = null;
        lblFormTitle.setText("Registrar Nuevo Alumno");
        btnGuardar.setText("Guardar");
    }

    private void cargarGrupos(ComboBox<Grupo> combo) {
        try {
            List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
            combo.setItems(FXCollections.observableArrayList(grupos));
        } catch (Exception e) {
            manejarExcepcion("cargar grupos", e);
        }
    }

    private void cargarDatos(TableView<Alumno> tabla) {
        try {
            List<Alumno> alumnos = alumnoService.obtenerTodosLosAlumnos();
            tabla.setItems(FXCollections.observableArrayList(alumnos));
        } catch (Exception e) {
            manejarExcepcion("cargar alumnos", e);
        }
    }

    private void eliminarAlumno(Alumno alumno) {
        if (alumno == null) {
            mostrarError("Por favor seleccione un alumno");
            return;
        }

        // Mostrar confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este alumno?");
        confirmacion.setContentText(alumno.getNombre() + " " +
                                   alumno.getApellidoPaterno() + " " +
                                   alumno.getApellidoMaterno());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    alumnoService.eliminarAlumno(alumno.getId());
                    mostrarExito("Alumno eliminado correctamente");

                    // Recargar la tabla
                    if (tablaAlumnos != null) {
                        cargarDatos(tablaAlumnos);
                    }
                } catch (IllegalStateException e) {
                    // Error de validación de dependencias
                    mostrarError(e.getMessage());
                } catch (Exception e) {
                    manejarExcepcion("eliminar alumno", e);
                }
            }
        });
    }

    private void cargarAlumnoEnFormulario(Alumno alumno) {
        if (alumno == null) {
            mostrarError("Alumno no válido");
            return;
        }

        // Cargar datos en el formulario
        alumnoIdEnEdicion = alumno.getId();
        txtNombre.setText(alumno.getNombre());
        txtApellidoP.setText(alumno.getApellidoPaterno());
        txtApellidoM.setText(alumno.getApellidoMaterno());

        // Buscar y seleccionar el grupo
        if (alumno.getGrupoId() != null) {
            cmbGrupo.getItems().stream()
                .filter(g -> g.getId().equals(alumno.getGrupoId()))
                .findFirst()
                .ifPresent(cmbGrupo::setValue);
        }

        // Cambiar el título y texto del botón
        lblFormTitle.setText("Editar Alumno");
        btnGuardar.setText("Actualizar");

        // Hacer scroll hacia arriba para mostrar el formulario (opcional)
        txtNombre.requestFocus();
    }
}
