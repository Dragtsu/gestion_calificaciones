package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Alumno;
import com.alumnos.domain.model.Agregado;
import com.alumnos.domain.model.AlumnoExamen;
import com.alumnos.domain.model.Calificacion;
import com.alumnos.domain.model.Criterio;
import com.alumnos.domain.model.Examen;
import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.model.GrupoMateria;
import com.alumnos.domain.model.Materia;
import com.alumnos.domain.port.in.AgregadoServicePort;
import com.alumnos.domain.port.in.AlumnoExamenServicePort;
import com.alumnos.domain.port.in.AlumnoServicePort;
import com.alumnos.domain.port.in.CalificacionServicePort;
import com.alumnos.domain.port.in.CriterioServicePort;
import com.alumnos.domain.port.in.ExamenServicePort;
import com.alumnos.domain.port.in.GrupoMateriaServicePort;
import com.alumnos.domain.port.in.GrupoServicePort;
import com.alumnos.domain.port.in.MateriaServicePort;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    @FXML
    private VBox drawerMenu;

    @FXML
    private BorderPane mainContent;

    @FXML
    private javafx.scene.layout.StackPane contentContainer;

    @FXML
    private Region overlay;

    @FXML
    private Button btnMenu;

    @FXML
    private Button btnMenuEstudiantes;

    @FXML
    private Button btnMenuUsuarios;

    @FXML
    private Button btnMenuMatricula;

    @FXML
    private Button btnMenuGrupos;

    @FXML
    private Button btnMenuConfiguracion;

    @FXML
    private Button btnMenuAcercaDe;

    @FXML
    private VBox submenuCriterios;

    @FXML
    private VBox submenuConcentrado;

    // Componentes creados dinámicamente (no están en FXML)
    private TextField txtNombre;
    private TextField txtApellido;
    private TextField txtEmail;
    private TextField txtMatricula;
    private DatePicker dpFechaNacimiento;
    private ComboBox<Grupo> cmbGrupo;
    private TextField txtBuscar;
    private Button btnGuardar;
    private Button btnLimpiar;
    private Button btnBuscar;
    private TableView<Alumno> tblAlumnos;
    private Long alumnoIdEnEdicion = null;  // ID del alumno que se está editando
    private TableColumn<Alumno, Long> colId;
    private TableColumn<Alumno, String> colNombre;
    private TableColumn<Alumno, String> colApellidoPaterno;
    private TableColumn<Alumno, String> colApellidoMaterno;

    @FXML
    private Label lblTitulo;

    private Label lblEstadistica;

    // Capas de vistas
    private VBox vistaEstudiantes;
    private VBox vistaGrupos;
    private VBox vistaMaterias;
    private VBox vistaAsignaciones;
    private VBox vistaCriterios;
    private VBox vistaAgregados;
    private VBox vistaConcentrado;
    private VBox vistaExamenes;
    private VBox vistaUsuarios;
    private VBox vistaMatricula;

    private final AlumnoServicePort alumnoService;
    private final GrupoServicePort grupoService;
    private final MateriaServicePort materiaService;
    private final GrupoMateriaServicePort grupoMateriaService;
    private final CriterioServicePort criterioService;
    private final AgregadoServicePort agregadoService;
    private final CalificacionServicePort calificacionService;
    private final ExamenServicePort examenService;
    private final AlumnoExamenServicePort alumnoExamenService;
    private ObservableList<Alumno> alumnosList;
    private boolean menuAbierto = false;

    public HomeController(AlumnoServicePort alumnoService, GrupoServicePort grupoService,
                         MateriaServicePort materiaService, GrupoMateriaServicePort grupoMateriaService,
                         CriterioServicePort criterioService, AgregadoServicePort agregadoService,
                         CalificacionServicePort calificacionService, ExamenServicePort examenService,
                         AlumnoExamenServicePort alumnoExamenService) {
        this.alumnoService = alumnoService;
        this.grupoService = grupoService;
        this.materiaService = materiaService;
        this.grupoMateriaService = grupoMateriaService;
        this.criterioService = criterioService;
        this.agregadoService = agregadoService;
        this.calificacionService = calificacionService;
        this.examenService = examenService;
        this.alumnoExamenService = alumnoExamenService;
    }

    @FXML
    public void initialize() {
        // Verificar que no se haya inicializado previamente
        if (vistaEstudiantes != null || vistaGrupos != null) {
            LOG.warn("initialize() ya fue llamado previamente. Saltando creación de vistas.");
            return;
        }

        // Crear todas las vistas y agregarlas al contenedor como capas
        crearTodasLasVistas();

        // Mostrar solo la vista de estudiantes por defecto
        mostrarVista("estudiantes");
    }

    private void crearTodasLasVistas() {
        try {
            // Limpiar el contenedor antes de agregar vistas
            if (contentContainer != null) {
                contentContainer.getChildren().clear();
            }

            // Crear vista de estudiantes
            vistaEstudiantes = crearVistaEstudiantesCompleta();
            if (vistaEstudiantes != null) {
                vistaEstudiantes.setVisible(false); // Inicialmente oculta
            } else {
                LOG.error("Error: vistaEstudiantes es null");
            }

            // Crear vista de grupos
            vistaGrupos = crearVistaGruposCompleta();
            if (vistaGrupos != null) {
                vistaGrupos.setVisible(false); // Inicialmente oculta
            } else {
                LOG.error("Error: vistaGrupos es null");
            }

            // Crear vista de materias
            vistaMaterias = crearVistaMateriasCompleta();
            if (vistaMaterias != null) {
                vistaMaterias.setVisible(false); // Inicialmente oculta
            } else {
                LOG.error("Error: vistaMaterias es null");
            }

            // Crear vista de asignaciones
            vistaAsignaciones = crearVistaAsignacionesCompleta();
            if (vistaAsignaciones != null) {
                vistaAsignaciones.setVisible(false); // Inicialmente oculta
            } else {
                LOG.error("Error: vistaAsignaciones es null");
            }

            // Crear vista de criterios
            vistaCriterios = crearVistaCriteriosCompleta();
            if (vistaCriterios != null) {
                vistaCriterios.setVisible(false); // Inicialmente oculta
            } else {
                LOG.error("Error: vistaCriterios es null");
            }

            // Crear vista de agregados
            vistaAgregados = crearVistaAgregadosCompleta();
            if (vistaAgregados != null) {
                vistaAgregados.setVisible(false); // Inicialmente oculta
            } else {
                LOG.error("Error: vistaAgregados es null");
            }

            // Crear vista de concentrado de calificaciones
            vistaConcentrado = crearVistaConcentradoCompleta();
            if (vistaConcentrado != null) {
                vistaConcentrado.setVisible(false); // Inicialmente oculta
            } else {
                LOG.error("Error: vistaConcentrado es null");
            }

            // Crear vista de exámenes
            vistaExamenes = crearVistaExamenesCompleta();
            if (vistaExamenes != null) {
                vistaExamenes.setVisible(false); // Inicialmente oculta
            } else {
                LOG.error("Error: vistaExamenes es null");
            }

            // Agregar todas las vistas al contenedor
            if (vistaEstudiantes != null && vistaGrupos != null && vistaMaterias != null &&
                vistaAsignaciones != null && vistaCriterios != null && vistaAgregados != null &&
                vistaConcentrado != null && vistaExamenes != null) {
                contentContainer.getChildren().addAll(vistaEstudiantes, vistaGrupos, vistaMaterias,
                                                     vistaAsignaciones, vistaCriterios, vistaAgregados,
                                                     vistaConcentrado, vistaExamenes);
            } else {
                LOG.error("Error: No se pudieron crear todas las vistas");
                // Crear al menos una vista vacía para evitar errores
                if (vistaEstudiantes == null) {
                    vistaEstudiantes = new VBox();
                    vistaEstudiantes.setVisible(false);
                }
                if (vistaGrupos == null) {
                    vistaGrupos = new VBox();
                    vistaGrupos.setVisible(false);
                }
                if (vistaMaterias == null) {
                    vistaMaterias = new VBox();
                    vistaMaterias.setVisible(false);
                }
                if (vistaAsignaciones == null) {
                    vistaAsignaciones = new VBox();
                    vistaAsignaciones.setVisible(false);
                }
                if (vistaCriterios == null) {
                    vistaCriterios = new VBox();
                    vistaCriterios.setVisible(false);
                }
                if (vistaAgregados == null) {
                    vistaAgregados = new VBox();
                    vistaAgregados.setVisible(false);
                }
                if (vistaConcentrado == null) {
                    vistaConcentrado = new VBox();
                    vistaConcentrado.setVisible(false);
                }
                if (vistaExamenes == null) {
                    vistaExamenes = new VBox();
                    vistaExamenes.setVisible(false);
                }
                contentContainer.getChildren().addAll(vistaEstudiantes, vistaGrupos, vistaMaterias,
                                                     vistaAsignaciones, vistaCriterios, vistaAgregados,
                                                     vistaConcentrado, vistaExamenes);
            }
        } catch (Exception e) {
            LOG.error("Error al crear las vistas", e);
            e.printStackTrace();
            // Crear vistas vacías para evitar null pointer
            vistaEstudiantes = new VBox();
            vistaGrupos = new VBox();
            vistaMaterias = new VBox();
            vistaAsignaciones = new VBox();
            vistaCriterios = new VBox();
            vistaAgregados = new VBox();
            vistaConcentrado = new VBox();
            vistaExamenes = new VBox();
            vistaEstudiantes.setVisible(false);
            vistaGrupos.setVisible(false);
            vistaMaterias.setVisible(false);
            vistaAsignaciones.setVisible(false);
            vistaCriterios.setVisible(false);
            vistaAgregados.setVisible(false);
            vistaConcentrado.setVisible(false);
            vistaExamenes.setVisible(false);
            contentContainer.getChildren().addAll(vistaEstudiantes, vistaGrupos, vistaMaterias,
                                                 vistaAsignaciones, vistaCriterios, vistaAgregados,
                                                 vistaConcentrado, vistaExamenes);
        }
    }

    private void mostrarVista(String nombreVista) {
        // Validar que las vistas existen
        if (vistaEstudiantes == null || vistaGrupos == null || vistaMaterias == null ||
            vistaAsignaciones == null || vistaCriterios == null || vistaAgregados == null ||
            vistaConcentrado == null || vistaExamenes == null) {
            LOG.error("Error: Las vistas no están inicializadas correctamente");
            return;
        }

        // Ocultar todas las vistas
        vistaEstudiantes.setVisible(false);
        vistaGrupos.setVisible(false);
        vistaMaterias.setVisible(false);
        vistaAsignaciones.setVisible(false);
        vistaCriterios.setVisible(false);
        vistaAgregados.setVisible(false);
        vistaConcentrado.setVisible(false);
        vistaExamenes.setVisible(false);

        // Mostrar solo la vista seleccionada
        try {
            switch (nombreVista.toLowerCase()) {
                case "estudiantes":
                    vistaEstudiantes.setVisible(true);
                    vistaEstudiantes.toFront();
                    break;
                case "grupos":
                    vistaGrupos.setVisible(true);
                    vistaGrupos.toFront();
                    break;
                case "materias":
                    vistaMaterias.setVisible(true);
                    vistaMaterias.toFront();
                    break;
                case "asignaciones":
                    vistaAsignaciones.setVisible(true);
                    vistaAsignaciones.toFront();
                    break;
                case "criterios":
                    vistaCriterios.setVisible(true);
                    vistaCriterios.toFront();
                    break;
                case "agregados":
                    vistaAgregados.setVisible(true);
                    vistaAgregados.toFront();
                    break;
                case "concentrado":
                    vistaConcentrado.setVisible(true);
                    vistaConcentrado.toFront();
                    break;
                case "examenes":
                    vistaExamenes.setVisible(true);
                    vistaExamenes.toFront();
                    break;
                default:
                    LOG.warn("Vista no reconocida: " + nombreVista);
                    vistaEstudiantes.setVisible(true);
                    vistaEstudiantes.toFront();
                    break;
            }
        } catch (Exception e) {
            LOG.error("Error al mostrar vista: " + nombreVista, e);
        }
    }

    // Método para abrir/cerrar el menú drawer
    @FXML
    private void toggleMenu() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), drawerMenu);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), overlay);

        if (menuAbierto) {
            // Cerrar menú
            transition.setToX(-280);
            fadeTransition.setToValue(0);
            fadeTransition.setOnFinished(e -> overlay.setVisible(false));
            menuAbierto = false;
        } else {
            // Abrir menú
            overlay.setVisible(true);
            transition.setToX(0);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            menuAbierto = true;
        }

        transition.play();
        fadeTransition.play();
    }

    // Método para cerrar el menú al hacer clic en el overlay
    @FXML
    private void closeMenu() {
        if (menuAbierto) {
            toggleMenu();
        }
    }

    // Método para minimizar la ventana
    @FXML
    private void minimizeWindow() {
        javafx.stage.Stage stage = (javafx.stage.Stage) lblTitulo.getScene().getWindow();
        stage.setIconified(true);
    }

    // Método para cerrar la ventana
    @FXML
    private void closeWindow() {
        javafx.stage.Stage stage = (javafx.stage.Stage) lblTitulo.getScene().getWindow();
        stage.close();
        javafx.application.Platform.exit();
    }

    // Handlers para los items del menú
    @FXML
    private void handleMenuEstudiantes() {
        lblTitulo.setText("Estudiantes - Sistema de Gestión");
        mostrarVista("estudiantes");
        toggleMenu();
    }


    @FXML
    private void handleMenuGrupos() {
        lblTitulo.setText("Grupos - Sistema de Gestión");
        mostrarVista("grupos");
        toggleMenu();
    }

    @FXML
    private void handleMenuMaterias() {
        lblTitulo.setText("Materias - Sistema de Gestión");
        mostrarVista("materias");
        toggleMenu();
    }

    @FXML
    private void handleMenuAsignaciones() {
        lblTitulo.setText("Asignación de Materias - Sistema de Gestión");
        mostrarVista("asignaciones");
        toggleMenu();
    }

    @FXML
    private void handleMenuConfiguracion() {
        lblTitulo.setText("Configuración - Sistema de Gestión");
        mostrarAlerta("Navegación", "Módulo de Configuración seleccionado", Alert.AlertType.INFORMATION);
        toggleMenu();
    }

    @FXML
    private void handleMenuAcercaDe() {
        mostrarAlerta("Acerca de",
            "Sistema de Gestión de Alumnos v1.0\n\n" +
            "Desarrollado con:\n" +
            "- Spring Boot\n" +
            "- JavaFX\n" +
            "- SQLite\n" +
            "- Arquitectura Limpia",
            Alert.AlertType.INFORMATION);
        toggleMenu();
    }

    @FXML
    private void toggleSubmenuCriterios() {
        if (submenuCriterios != null) {
            boolean isVisible = submenuCriterios.isVisible();
            submenuCriterios.setVisible(!isVisible);
            submenuCriterios.setManaged(!isVisible);
        }
    }

    @FXML
    private void toggleSubmenuConcentrado() {
        if (submenuConcentrado != null) {
            boolean isVisible = submenuConcentrado.isVisible();
            submenuConcentrado.setVisible(!isVisible);
            submenuConcentrado.setManaged(!isVisible);
        }
    }

    @FXML
    private void handleMenuCriteriosLista() {
        lblTitulo.setText("Criterios de Evaluación - Sistema de Gestión");
        mostrarVista("criterios");
        toggleMenu();
    }

    @FXML
    private void handleMenuAgregados() {
        lblTitulo.setText("Agregados - Sistema de Gestión");
        mostrarVista("agregados");
        toggleMenu();
    }

    @FXML
    private void handleMenuConcentrado() {
        lblTitulo.setText("Concentrado de calificaciones - Sistema de Gestión");
        mostrarVista("concentrado");
        toggleMenu();
    }

    @FXML
    private void handleMenuExamenes() {
        lblTitulo.setText("Exámenes - Sistema de Gestión");
        mostrarVista("examenes");
        toggleMenu();
    }


    @FXML
    private void handleGuardar() {
        try {
            if (validarCampos()) {
                Alumno alumno = Alumno.builder()
                        .id(alumnoIdEnEdicion)  // Si es null, es una creación; si tiene valor, es actualización
                        .nombre(txtNombre.getText().trim())
                        .apellidoPaterno(txtApellido.getText().trim())
                        .apellidoMaterno(txtEmail.getText().trim())
                        .grupoId(cmbGrupo.getValue() != null ? cmbGrupo.getValue().getId() : null)
                        .build();

                if (alumnoIdEnEdicion == null) {
                    // Crear nuevo alumno
                    alumnoService.crearAlumno(alumno);
                    mostrarAlerta("Éxito", "Alumno creado correctamente", Alert.AlertType.INFORMATION);
                } else {
                    // Actualizar alumno existente
                    alumnoService.actualizarAlumno(alumno);
                    mostrarAlerta("Éxito", "Alumno actualizado correctamente", Alert.AlertType.INFORMATION);
                }

                limpiarFormulario();
                cargarAlumnos();
                if (lblEstadistica != null) {
                    actualizarEstadisticas();
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLimpiar() {
        limpiarFormulario();
    }

    @FXML
    private void handleBuscar() {
        if (alumnosList == null || txtBuscar == null) return;

        String textoBusqueda = txtBuscar.getText();
        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            cargarAlumnos();
        } else {
            alumnosList.clear();
            alumnosList.addAll(alumnoService.buscarPorNombre(textoBusqueda));
        }
    }

    private void cargarAlumnos() {
        if (alumnosList != null) {
            alumnosList.clear();
            alumnosList.addAll(alumnoService.obtenerTodosLosAlumnos());
        }
    }

    private void filtrarAlumnosPorGrupo(Long grupoId) {
        if (alumnosList != null) {
            alumnosList.clear();
            List<Alumno> alumnosDelGrupo = alumnoService.obtenerTodosLosAlumnos().stream()
                .filter(alumno -> alumno.getGrupoId() != null && alumno.getGrupoId().equals(grupoId))
                .sorted((a1, a2) -> {
                    // Ordenar por número de lista
                    if (a1.getNumeroLista() == null) return 1;
                    if (a2.getNumeroLista() == null) return -1;
                    return a1.getNumeroLista().compareTo(a2.getNumeroLista());
                })
                .collect(java.util.stream.Collectors.toList());
            alumnosList.addAll(alumnosDelGrupo);
            actualizarEstadisticas();
        }
    }

    private void cargarAlumnoEnFormulario(Alumno alumno) {
        alumnoIdEnEdicion = alumno.getId();  // Establecer ID para modo edición
        txtNombre.setText(alumno.getNombre());
        txtApellido.setText(alumno.getApellidoPaterno());
        txtEmail.setText(alumno.getApellidoMaterno());

        // Cargar grupo si existe
        if (alumno.getGrupoId() != null && cmbGrupo != null) {
            for (Grupo grupo : cmbGrupo.getItems()) {
                if (grupo.getId().equals(alumno.getGrupoId())) {
                    cmbGrupo.setValue(grupo);
                    break;
                }
            }
        } else if (cmbGrupo != null) {
            cmbGrupo.setValue(null);
        }

        // Cambiar texto del botón a "Actualizar"
        if (btnGuardar != null) {
            btnGuardar.setText("Actualizar");
        }
    }

    private void limpiarFormulario() {
        alumnoIdEnEdicion = null;  // Limpiar ID de edición
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();  // Se usa para apellido materno
        if (cmbGrupo != null) {
            cmbGrupo.setValue(null);
        }
        if (btnGuardar != null) {
            btnGuardar.setText("Guardar");
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El nombre es requerido", Alert.AlertType.WARNING);
            return false;
        }
        if (txtApellido.getText() == null || txtApellido.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El apellido paterno es requerido", Alert.AlertType.WARNING);
            return false;
        }
        // Validar que apellido paterno contenga solo una palabra
        if (txtApellido.getText().trim().contains(" ")) {
            mostrarAlerta("Validación", "El apellido paterno debe contener solo una palabra", Alert.AlertType.WARNING);
            return false;
        }
        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El apellido materno es requerido", Alert.AlertType.WARNING);
            return false;
        }
        // Validar que apellido materno contenga solo una palabra
        if (txtEmail.getText().trim().contains(" ")) {
            mostrarAlerta("Validación", "El apellido materno debe contener solo una palabra", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbGrupo == null || cmbGrupo.getValue() == null) {
            mostrarAlerta("Validación", "Debe seleccionar un grupo", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void actualizarEstadisticas() {
        int totalAlumnos = alumnosList.size();
        lblEstadistica.setText("Total de alumnos: " + totalAlumnos);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para crear la vista completa de estudiantes
    private VBox crearVistaEstudiantesCompleta() {
        try {
            // Crear layout principal para estudiantes
            VBox vista = new VBox(20);
            vista.setStyle("-fx-padding: 20;");

            // Formulario de Registro
            VBox formPanel = crearFormularioEstudiantes();

            // Panel de tabla
            VBox tablePanel = crearTablaEstudiantes();

            vista.getChildren().addAll(formPanel, tablePanel);

            return vista;
        } catch (Exception e) {
            LOG.error("Error al crear vista de estudiantes", e);
            e.printStackTrace();
            // Retornar una vista vacía en caso de error
            VBox vistaError = new VBox();
            Label lblError = new Label("Error al cargar la vista de estudiantes: " + e.getMessage());
            lblError.setStyle("-fx-text-fill: red; -fx-padding: 20;");
            vistaError.getChildren().add(lblError);
            return vistaError;
        }
    }

    private VBox crearFormularioEstudiantes() {
        VBox formPanel = new VBox(10);
        formPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblFormTitle = new Label("Registrar Nuevo Alumno");
        lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
        gridForm.setHgap(15);
        gridForm.setVgap(10);

        // Campos del formulario
        Label lblNombre = new Label("Nombre:");
        txtNombre = new TextField();
        txtNombre.setPromptText("Ingrese el nombre");

        Label lblApellidoPaterno = new Label("Apellido Paterno:");
        txtApellido = new TextField();
        txtApellido.setPromptText("Ingrese el apellido paterno");

        Label lblApellidoMaterno = new Label("Apellido Materno:");
        txtEmail = new TextField();
        txtEmail.setPromptText("Ingrese el apellido materno");

        Label lblGrupo = new Label("Grupo:");
        cmbGrupo = new ComboBox<>();
        cmbGrupo.setPromptText("Seleccione un grupo");
        cmbGrupo.setPrefWidth(300);

        // Cargar grupos en el ComboBox con manejo de errores
        try {
            if (grupoService != null) {
                List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
                LOG.info("Grupos cargados para ComboBox: {} grupos", grupos.size());
                cmbGrupo.setItems(FXCollections.observableArrayList(grupos));
            } else {
                LOG.error("grupoService es null - no se pueden cargar grupos en ComboBox");
                cmbGrupo.setItems(FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            LOG.error("Error al cargar grupos en ComboBox", e);
            cmbGrupo.setItems(FXCollections.observableArrayList());
        }

        // Configurar cómo se muestra el grupo en el ComboBox
        cmbGrupo.setCellFactory(param -> new ListCell<Grupo>() {
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
        cmbGrupo.setButtonCell(new ListCell<Grupo>() {
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

        gridForm.add(lblNombre, 0, 0);
        gridForm.add(txtNombre, 1, 0);
        gridForm.add(lblApellidoPaterno, 0, 1);
        gridForm.add(txtApellido, 1, 1);
        gridForm.add(lblApellidoMaterno, 0, 2);
        gridForm.add(txtEmail, 1, 2);
        gridForm.add(lblGrupo, 0, 3);
        gridForm.add(cmbGrupo, 1, 3);

        // Botones
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        btnGuardar = new Button("Guardar");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> handleGuardar());

        btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> handleLimpiar());

        buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);

        formPanel.getChildren().addAll(lblFormTitle, new Separator(), gridForm, buttonBox);
        return formPanel;
    }

    private VBox crearTablaEstudiantes() {
        VBox tablePanel = new VBox(10);
        tablePanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblTableTitle = new Label("Lista de Alumnos");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Búsqueda y filtros
        javafx.scene.layout.HBox searchBox = new javafx.scene.layout.HBox(10);
        searchBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar por nombre...");
        txtBuscar.setPrefWidth(250);

        btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 25; -fx-cursor: hand;");
        btnBuscar.setOnAction(e -> handleBuscar());

        // ComboBox para filtrar por grupo
        Label lblFiltroGrupo = new Label("Grupo:");
        lblFiltroGrupo.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        ComboBox<Grupo> cmbFiltroGrupo = new ComboBox<>();
        cmbFiltroGrupo.setPromptText("Todos");
        cmbFiltroGrupo.setPrefWidth(120);

        // Cargar grupos en el combo
        try {
            List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
            cmbFiltroGrupo.setItems(FXCollections.observableArrayList(grupos));
        } catch (Exception e) {
            LOG.error("Error al cargar grupos para filtro", e);
        }

        // Configurar cómo se muestran los grupos
        cmbFiltroGrupo.setCellFactory(param -> new ListCell<Grupo>() {
            @Override
            protected void updateItem(Grupo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.valueOf(item.getId()));
            }
        });
        cmbFiltroGrupo.setButtonCell(new ListCell<Grupo>() {
            @Override
            protected void updateItem(Grupo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Todos" : String.valueOf(item.getId()));
            }
        });

        // Evento para filtrar por grupo
        cmbFiltroGrupo.setOnAction(event -> {
            Grupo grupoSeleccionado = cmbFiltroGrupo.getValue();
            if (grupoSeleccionado != null) {
                filtrarAlumnosPorGrupo(grupoSeleccionado.getId());
            } else {
                cargarAlumnos();
            }
        });

        // Botón para limpiar filtro
        Button btnLimpiarFiltroGrupo = new Button("Limpiar");
        btnLimpiarFiltroGrupo.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-cursor: hand;");
        btnLimpiarFiltroGrupo.setOnAction(event -> {
            cmbFiltroGrupo.setValue(null);
            cargarAlumnos();
            actualizarEstadisticas();
        });

        searchBox.getChildren().addAll(txtBuscar, btnBuscar, lblFiltroGrupo, cmbFiltroGrupo, btnLimpiarFiltroGrupo);

        // Tabla
        tblAlumnos = new TableView<>();
        tblAlumnos.setPrefHeight(300);

        colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(200);

        colApellidoPaterno = new TableColumn<>("Apellido Paterno");
        colApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colApellidoPaterno.setPrefWidth(200);

        colApellidoMaterno = new TableColumn<>("Apellido Materno");
        colApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        colApellidoMaterno.setPrefWidth(200);

        // Columna Número de Lista
        TableColumn<Alumno, Integer> colNumeroLista = new TableColumn<>("N° Lista");
        colNumeroLista.setCellValueFactory(new PropertyValueFactory<>("numeroLista"));
        colNumeroLista.setPrefWidth(80);
        colNumeroLista.setStyle("-fx-alignment: CENTER;");

        // Columna Grupo - mostrar código del grupo basado en grupoId
        TableColumn<Alumno, String> colGrupo = new TableColumn<>("Grupo");
        colGrupo.setPrefWidth(150);
        colGrupo.setCellValueFactory(cellData -> {
            Alumno alumno = cellData.getValue();
            if (alumno.getGrupoId() != null) {
                try {
                    return new javafx.beans.property.SimpleStringProperty(
                        grupoService.obtenerGrupoPorId(alumno.getGrupoId())
                            .map(g -> String.valueOf(g.getId()))
                            .orElse("Sin grupo")
                    );
                } catch (Exception e) {
                    return new javafx.beans.property.SimpleStringProperty("Error");
                }
            }
            return new javafx.beans.property.SimpleStringProperty("Sin grupo");
        });
        // Columna de acciones
        TableColumn<Alumno, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(200);
        colAcciones.setCellFactory(col -> new TableCell<>() {
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
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmar eliminación");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText("¿Está seguro de eliminar al estudiante " + alumno.getNombre() + " " + alumno.getApellidoPaterno() + "?");

                    confirmacion.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                alumnoService.eliminarAlumno(alumno.getId());
                                cargarAlumnos();
                                actualizarEstadisticas();
                                mostrarAlerta("Éxito", "Estudiante eliminado correctamente", Alert.AlertType.INFORMATION);
                            } catch (Exception e) {
                                mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
                            }
                        }
                    });
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

        tblAlumnos.getColumns().addAll(colId, colNombre, colApellidoPaterno, colApellidoMaterno, colNumeroLista, colGrupo, colAcciones);

        alumnosList = FXCollections.observableArrayList();
        tblAlumnos.setItems(alumnosList);
        cargarAlumnos();

        // Doble clic para editar
        tblAlumnos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Alumno alumnoSeleccionado = tblAlumnos.getSelectionModel().getSelectedItem();
                if (alumnoSeleccionado != null) {
                    cargarAlumnoEnFormulario(alumnoSeleccionado);
                }
            }
        });

        lblEstadistica = new Label("Total de alumnos: " + alumnosList.size());
        lblEstadistica.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #666;");

        tablePanel.getChildren().addAll(lblTableTitle, new Separator(), searchBox, tblAlumnos, lblEstadistica);
        return tablePanel;
    }

    // Método para crear la vista completa de grupos
    private VBox crearVistaGruposCompleta() {
        try {
            // Crear layout principal para grupos
            VBox vista = new VBox(20);
            vista.setStyle("-fx-padding: 20;");


            // Panel de formulario
            VBox formPanel = new VBox(10);
            formPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

            Label lblFormTitle = new Label("Registrar Nuevo Grupo");
            lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
            gridForm.setHgap(15);
            gridForm.setVgap(10);

            // Campo ID (editable - código del grupo, entero)
            Label lblId = new Label("Código del Grupo:");
            TextField txtIdGrupo = new TextField();
            txtIdGrupo.setPromptText("Código del grupo (número entero)");
            txtIdGrupo.setPrefWidth(150);

            // Validación para solo números enteros
            txtIdGrupo.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    txtIdGrupo.setText(oldValue);
                }
            });

            gridForm.add(lblId, 0, 0);
            gridForm.add(txtIdGrupo, 1, 0);

        // Botones
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        Button btnGuardarGrupo = new Button("Guardar");
        btnGuardarGrupo.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");

        Button btnLimpiarGrupo = new Button("Limpiar");
        btnLimpiarGrupo.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");

        buttonBox.getChildren().addAll(btnGuardarGrupo, btnLimpiarGrupo);

        formPanel.getChildren().addAll(lblFormTitle, new Separator(), gridForm, buttonBox);

        // Panel de tabla
        VBox tablePanel = new VBox(10);
        tablePanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lblTableTitle = new Label("Lista de Grupos");
        lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Tabla de grupos
        TableView<Grupo> tblGrupos = new TableView<>();
        tblGrupos.setPrefHeight(300);

        TableColumn<Grupo, Long> colIdGrupo = new TableColumn<>("Código del Grupo");
        colIdGrupo.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdGrupo.setPrefWidth(500);

        // Columna de acciones
        TableColumn<Grupo, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(120);
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                btnEliminar.setOnAction(event -> {
                    Grupo grupo = getTableView().getItems().get(getIndex());
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmar eliminación");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText("¿Está seguro de eliminar el grupo " + grupo.getId() + "?");

                    confirmacion.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                grupoService.eliminarGrupo(grupo.getId());
                                cargarGrupos(tblGrupos);
                                mostrarAlerta("Éxito", "Grupo eliminado correctamente", Alert.AlertType.INFORMATION);
                            } catch (Exception e) {
                                mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
                            }
                        }
                    });
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

        tblGrupos.getColumns().addAll(colIdGrupo, colAcciones);

        // Estadísticas
        Label lblEstadisticaGrupos = new Label("Total de grupos: 0");
        lblEstadisticaGrupos.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #666;");

        tablePanel.getChildren().addAll(lblTableTitle, new Separator(), tblGrupos, lblEstadisticaGrupos);

        // Cargar grupos inicialmente
        try {
            cargarGrupos(tblGrupos);
            lblEstadisticaGrupos.setText("Total de grupos: " + tblGrupos.getItems().size());
        } catch (Exception e) {
            LOG.error("Error al cargar grupos inicialmente", e);
            lblEstadisticaGrupos.setText("Error al cargar grupos: " + e.getMessage());
        }

        // Eventos de botones
        btnGuardarGrupo.setOnAction(event -> {
            try {
                // Validaciones
                if (txtIdGrupo.getText() == null || txtIdGrupo.getText().trim().isEmpty()) {
                    mostrarAlerta("Validación", "El código del grupo es requerido", Alert.AlertType.WARNING);
                    return;
                }

                Long codigoGrupo = Long.parseLong(txtIdGrupo.getText().trim());

                Grupo grupo = Grupo.builder()
                        .id(codigoGrupo)
                        .build();

                Grupo grupoGuardado = grupoService.crearGrupo(grupo);
                mostrarAlerta("Éxito", "Grupo guardado correctamente con código: " + grupoGuardado.getId(), Alert.AlertType.INFORMATION);

                // Limpiar campos
                txtIdGrupo.clear();

                // Recargar tabla
                cargarGrupos(tblGrupos);
                lblEstadisticaGrupos.setText("Total de grupos: " + tblGrupos.getItems().size());

                // Recargar ComboBox de grupos en el formulario de estudiantes
                recargarGruposEnComboBox();

            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "El código debe ser un número entero válido", Alert.AlertType.ERROR);
            } catch (IllegalArgumentException e) {
                // Capturar el mensaje "El grupo ya existe"
                mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnLimpiarGrupo.setOnAction(event -> {
            txtIdGrupo.clear();
        });

            // Agregar todos los paneles al layout principal
            vista.getChildren().addAll(formPanel, tablePanel);

            // Retornar la vista completa
            return vista;

        } catch (Exception e) {
            LOG.error("Error al crear vista de grupos", e);
            e.printStackTrace();
            // Retornar una vista vacía en caso de error
            VBox vistaError = new VBox();
            Label lblError = new Label("Error al cargar la vista de grupos: " + e.getMessage());
            lblError.setStyle("-fx-text-fill: red; -fx-padding: 20;");
            vistaError.getChildren().add(lblError);
            return vistaError;
        }
    }

    private void cargarGrupos(TableView<Grupo> tabla) {
        try {
            if (grupoService == null) {
                LOG.error("grupoService es null - no se pueden cargar grupos");
                return;
            }
            if (tabla == null) {
                LOG.error("tabla es null - no se pueden cargar grupos");
                return;
            }

            ObservableList<Grupo> gruposList = FXCollections.observableArrayList(
                grupoService.obtenerTodosLosGrupos()
            );
            tabla.setItems(gruposList);
            LOG.info("Grupos cargados correctamente: {} grupos", gruposList.size());
        } catch (Exception e) {
            LOG.error("Error al cargar grupos en la tabla", e);
            // Mostrar tabla vacía en caso de error
            tabla.setItems(FXCollections.observableArrayList());
        }
    }

    // Método para crear la vista completa de materias
    private VBox crearVistaMateriasCompleta() {
        try {
            VBox vista = new VBox(20);
            vista.setStyle("-fx-padding: 20;");

            // Panel de formulario
            VBox formPanel = new VBox(10);
            formPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

            Label lblFormTitle = new Label("Registrar Nueva Materia");
            lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
            gridForm.setHgap(15);
            gridForm.setVgap(10);

            // Campo ID (solo lectura - muestra el próximo ID)
            Label lblId = new Label("ID:");
            TextField txtId = new TextField();
            txtId.setPromptText("Se asignará automáticamente");
            txtId.setPrefWidth(150);
            txtId.setEditable(false);  // No editable
            txtId.setStyle("-fx-background-color: #f0f0f0;");  // Gris claro para indicar que no es editable

            // Campo Nombre
            Label lblNombre = new Label("Nombre:");
            TextField txtNombre = new TextField();
            txtNombre.setPromptText("Nombre de la materia");
            txtNombre.setPrefWidth(300);

            gridForm.add(lblId, 0, 0);
            gridForm.add(txtId, 1, 0);
            gridForm.add(lblNombre, 0, 1);
            gridForm.add(txtNombre, 1, 1);

            // Botones
            javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
            Button btnGuardar = new Button("Guardar");
            btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");

            Button btnLimpiar = new Button("Limpiar");
            btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");

            buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);
            formPanel.getChildren().addAll(lblFormTitle, new Separator(), gridForm, buttonBox);

            // Panel de tabla
            VBox tablePanel = new VBox(10);
            tablePanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

            Label lblTableTitle = new Label("Lista de Materias");
            lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            // Búsqueda
            javafx.scene.layout.HBox searchBox = new javafx.scene.layout.HBox(10);
            TextField txtBuscar = new TextField();
            txtBuscar.setPromptText("Buscar por nombre...");
            txtBuscar.setPrefWidth(300);

            Button btnBuscar = new Button("Buscar");
            btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 25; -fx-cursor: hand;");
            btnBuscar.setOnAction(e -> handleBuscar());

            searchBox.getChildren().addAll(txtBuscar, btnBuscar);

            // Tabla
            TableView<Materia> tblMaterias = new TableView<>();
            tblMaterias.setPrefHeight(300);

            TableColumn<Materia, Long> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colId.setPrefWidth(100);

            TableColumn<Materia, String> colNombreMateria = new TableColumn<>("Nombre");
            colNombreMateria.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colNombreMateria.setPrefWidth(450);

            // Columna de acciones
            TableColumn<Materia, Void> colAcciones = new TableColumn<>("Acciones");
            colAcciones.setPrefWidth(120);
            colAcciones.setCellFactory(col -> new TableCell<>() {
                private final Button btnEliminar = new Button("Eliminar");

                {
                    btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                    btnEliminar.setOnAction(event -> {
                        Materia materia = getTableView().getItems().get(getIndex());
                        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmacion.setTitle("Confirmar eliminación");
                        confirmacion.setHeaderText(null);
                        confirmacion.setContentText("¿Está seguro de eliminar la materia " + materia.getId() + " - " + materia.getNombre() + "?");

                        confirmacion.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    materiaService.eliminarMateria(materia.getId());
                                    cargarMaterias(tblMaterias);
                                    mostrarAlerta("Éxito", "Materia eliminada correctamente", Alert.AlertType.INFORMATION);
                                } catch (Exception e) {
                                    mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
                                }
                            }
                        });
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

            tblMaterias.getColumns().addAll(colId, colNombreMateria, colAcciones);

            Label lblEstadisticas = new Label("Total de materias: 0");
            lblEstadisticas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #666;");

            tablePanel.getChildren().addAll(lblTableTitle, new Separator(), searchBox, tblMaterias, lblEstadisticas);

            // Cargar materias inicialmente y calcular próximo ID
            try {
                cargarMaterias(tblMaterias);
                lblEstadisticas.setText("Total de materias: " + tblMaterias.getItems().size());
                // Mostrar el próximo ID que le tocaría
                actualizarProximoId(txtId, tblMaterias);
            } catch (Exception e) {
                LOG.error("Error al cargar materias inicialmente", e);
                lblEstadisticas.setText("Error al cargar materias: " + e.getMessage());
            }

            // Eventos
            btnGuardar.setOnAction(event -> {
                try {
                    if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
                        mostrarAlerta("Validación", "El nombre es requerido", Alert.AlertType.WARNING);
                        return;
                    }

                    Materia materia = Materia.builder()
                            .nombre(txtNombre.getText().trim())
                            .build();

                    Materia materiaGuardada = materiaService.crearMateria(materia);
                    mostrarAlerta("Éxito", "Materia guardada correctamente con ID: " + materiaGuardada.getId(), Alert.AlertType.INFORMATION);

                    txtId.clear();
                    txtNombre.clear();

                    cargarMaterias(tblMaterias);
                    lblEstadisticas.setText("Total de materias: " + tblMaterias.getItems().size());
                    // Actualizar el próximo ID
                    actualizarProximoId(txtId, tblMaterias);
                } catch (Exception e) {
                    mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            btnLimpiar.setOnAction(event -> {
                txtId.clear();
                txtNombre.clear();
                // Recalcular el próximo ID
                actualizarProximoId(txtId, tblMaterias);
            });

            btnBuscar.setOnAction(event -> {
                String textoBusqueda = txtBuscar.getText();
                if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
                    cargarMaterias(tblMaterias);
                } else {
                    ObservableList<Materia> materiasFiltradas = FXCollections.observableArrayList(
                            materiaService.buscarPorNombre(textoBusqueda)
                    );
                    tblMaterias.setItems(materiasFiltradas);
                }
                lblEstadisticas.setText("Total de materias: " + tblMaterias.getItems().size());
            });

            tblMaterias.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Materia materiaSeleccionada = tblMaterias.getSelectionModel().getSelectedItem();
                    if (materiaSeleccionada != null) {
                        txtId.setText(String.valueOf(materiaSeleccionada.getId()));
                        txtNombre.setText(materiaSeleccionada.getNombre());
                    }
                }
            });

            vista.getChildren().addAll(formPanel, tablePanel);
            return vista;

        } catch (Exception e) {
            LOG.error("Error al crear vista de materias", e);
            e.printStackTrace();
            VBox vistaError = new VBox();
            Label lblError = new Label("Error al cargar la vista de materias: " + e.getMessage());
            lblError.setStyle("-fx-text-fill: red; -fx-padding: 20;");
            vistaError.getChildren().add(lblError);
            return vistaError;
        }
    }

    private void cargarMaterias(TableView<Materia> tabla) {
        try {
            if (materiaService == null) {
                LOG.error("materiaService es null - no se pueden cargar materias");
                return;
            }
            if (tabla == null) {
                LOG.error("tabla es null - no se pueden cargar materias");
                return;
            }

            ObservableList<Materia> materiasList = FXCollections.observableArrayList(
                    materiaService.obtenerTodasLasMaterias()
            );
            tabla.setItems(materiasList);
            LOG.info("Materias cargadas correctamente: {} materias", materiasList.size());
        } catch (Exception e) {
            LOG.error("Error al cargar materias en la tabla", e);
            tabla.setItems(FXCollections.observableArrayList());
        }
    }

    // Método auxiliar para calcular y mostrar el próximo ID
    private void actualizarProximoId(TextField txtId, TableView<Materia> tabla) {
        try {
            if (tabla.getItems().isEmpty()) {
                txtId.setText("1");
            } else {
                Long maxId = tabla.getItems().stream()
                        .map(Materia::getId)
                        .max(Long::compareTo)
                        .orElse(0L);
                txtId.setText(String.valueOf(maxId + 1));
            }
        } catch (Exception e) {
            LOG.error("Error al calcular próximo ID", e);
            txtId.setText("?");
        }
    }

    // Método para recargar los grupos en el ComboBox
    private void recargarGruposEnComboBox() {
        if (cmbGrupo != null && grupoService != null) {
            try {
                List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
                cmbGrupo.setItems(FXCollections.observableArrayList(grupos));
                LOG.info("ComboBox de grupos recargado: {} grupos", grupos.size());
            } catch (Exception e) {
                LOG.error("Error al recargar grupos en ComboBox", e);
            }
        }
    }

    // Método para crear la vista completa de asignaciones de materias a grupos
    private VBox crearVistaAsignacionesCompleta() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        try {

            // Panel de formulario
            VBox formPanel = new VBox(15);
            formPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

            Label lblFormTitle = new Label("Asignar Materia a Grupo");
            lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
            gridForm.setHgap(15);
            gridForm.setVgap(10);

            // ComboBox para seleccionar Grupo
            Label lblGrupo = new Label("Grupo:");
            ComboBox<Grupo> cmbGrupoAsignacion = new ComboBox<>();
            cmbGrupoAsignacion.setPromptText("Seleccione un grupo");
            cmbGrupoAsignacion.setPrefWidth(300);

            try {
                cmbGrupoAsignacion.setItems(FXCollections.observableArrayList(grupoService.obtenerTodosLosGrupos()));
            } catch (Exception e) {
                LOG.error("Error al cargar grupos", e);
            }

            cmbGrupoAsignacion.setCellFactory(param -> new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.valueOf(item.getId()));
                }
            });
            cmbGrupoAsignacion.setButtonCell(new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.valueOf(item.getId()));
                }
            });

            // ComboBox para seleccionar Materia
            Label lblMateria = new Label("Materia:");
            ComboBox<Materia> cmbMateriaAsignacion = new ComboBox<>();
            cmbMateriaAsignacion.setPromptText("Seleccione una materia");
            cmbMateriaAsignacion.setPrefWidth(300);

            try {
                cmbMateriaAsignacion.setItems(FXCollections.observableArrayList(materiaService.obtenerTodasLasMaterias()));
            } catch (Exception e) {
                LOG.error("Error al cargar materias", e);
            }

            cmbMateriaAsignacion.setCellFactory(param -> new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getId() + " - " + item.getNombre());
                }
            });
            cmbMateriaAsignacion.setButtonCell(new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getId() + " - " + item.getNombre());
                }
            });

            gridForm.add(lblGrupo, 0, 0);
            gridForm.add(cmbGrupoAsignacion, 1, 0);
            gridForm.add(lblMateria, 0, 1);
            gridForm.add(cmbMateriaAsignacion, 1, 1);

            // Botones
            javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
            Button btnAsignar = new Button("Asignar");
            btnAsignar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");

            Button btnLimpiar = new Button("Limpiar");
            btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");

            buttonBox.getChildren().addAll(btnAsignar, btnLimpiar);
            formPanel.getChildren().addAll(lblFormTitle, new Separator(), gridForm, buttonBox);

            // Panel de tabla
            VBox tablePanel = new VBox(10);
            tablePanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

            Label lblTableTitle = new Label("Lista de Asignaciones");
            lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            // Tabla
            TableView<GrupoMateria> tblAsignaciones = new TableView<>();
            tblAsignaciones.setPrefHeight(300);

            TableColumn<GrupoMateria, Long> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colId.setPrefWidth(60);

            TableColumn<GrupoMateria, String> colGrupo = new TableColumn<>("Grupo");
            colGrupo.setPrefWidth(250);
            colGrupo.setCellValueFactory(cellData -> {
                GrupoMateria asignacion = cellData.getValue();
                if (asignacion.getGrupoId() != null) {
                    try {
                        return new javafx.beans.property.SimpleStringProperty(
                            grupoService.obtenerGrupoPorId(asignacion.getGrupoId())
                                .map(g -> String.valueOf(g.getId()))
                                .orElse("Grupo no encontrado")
                        );
                    } catch (Exception e) {
                        return new javafx.beans.property.SimpleStringProperty("Error");
                    }
                }
                return new javafx.beans.property.SimpleStringProperty("Sin grupo");
            });

            TableColumn<GrupoMateria, String> colMateria = new TableColumn<>("Materia");
            colMateria.setPrefWidth(250);
            colMateria.setCellValueFactory(cellData -> {
                GrupoMateria asignacion = cellData.getValue();
                if (asignacion.getMateriaId() != null) {
                    try {
                        return new javafx.beans.property.SimpleStringProperty(
                            materiaService.obtenerMateriaPorId(asignacion.getMateriaId())
                                .map(m -> m.getId() + " - " + m.getNombre())
                                .orElse("Materia no encontrada")
                        );
                    } catch (Exception e) {
                        return new javafx.beans.property.SimpleStringProperty("Error");
                    }
                }
                return new javafx.beans.property.SimpleStringProperty("Sin materia");
            });

            // Columna de acciones
            TableColumn<GrupoMateria, Void> colAcciones = new TableColumn<>("Acciones");
            colAcciones.setPrefWidth(120);
            colAcciones.setCellFactory(col -> new TableCell<>() {
                private final Button btnEliminar = new Button("Eliminar");

                {
                    btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                    btnEliminar.setOnAction(event -> {
                        GrupoMateria asignacion = getTableView().getItems().get(getIndex());
                        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmacion.setTitle("Confirmar eliminación");
                        confirmacion.setHeaderText(null);
                        confirmacion.setContentText("¿Está seguro de eliminar esta asignación?");

                        confirmacion.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    grupoMateriaService.eliminarAsignacion(asignacion.getId());
                                    cargarAsignaciones(tblAsignaciones);
                                    mostrarAlerta("Éxito", "Asignación eliminada correctamente", Alert.AlertType.INFORMATION);
                                } catch (Exception e) {
                                    mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
                                }
                            }
                        });
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

            tblAsignaciones.getColumns().addAll(colId, colGrupo, colMateria, colAcciones);

            Label lblEstadisticas = new Label("Total de asignaciones: 0");
            lblEstadisticas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #666;");

            tablePanel.getChildren().addAll(lblTableTitle, new Separator(), tblAsignaciones, lblEstadisticas);

            // Cargar asignaciones inicialmente
            cargarAsignaciones(tblAsignaciones);
            lblEstadisticas.setText("Total de asignaciones: " + tblAsignaciones.getItems().size());

            // Eventos
            btnAsignar.setOnAction(event -> {
                try {
                    if (cmbGrupoAsignacion.getValue() == null) {
                        mostrarAlerta("Validación", "Debe seleccionar un grupo", Alert.AlertType.WARNING);
                        return;
                    }
                    if (cmbMateriaAsignacion.getValue() == null) {
                        mostrarAlerta("Validación", "Debe seleccionar una materia", Alert.AlertType.WARNING);
                        return;
                    }

                    GrupoMateria asignacion = GrupoMateria.builder()
                            .grupoId(cmbGrupoAsignacion.getValue().getId())
                            .materiaId(cmbMateriaAsignacion.getValue().getId())
                            .build();

                    grupoMateriaService.asignarMateriaAGrupo(asignacion);
                    mostrarAlerta("Éxito", "Materia asignada al grupo correctamente", Alert.AlertType.INFORMATION);

                    cmbGrupoAsignacion.setValue(null);
                    cmbMateriaAsignacion.setValue(null);

                    cargarAsignaciones(tblAsignaciones);
                    lblEstadisticas.setText("Total de asignaciones: " + tblAsignaciones.getItems().size());
                } catch (IllegalArgumentException e) {
                    mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
                } catch (Exception e) {
                    mostrarAlerta("Error", "Error al asignar: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            btnLimpiar.setOnAction(event -> {
                cmbGrupoAsignacion.setValue(null);
                cmbMateriaAsignacion.setValue(null);
            });

            vista.getChildren().addAll(formPanel, tablePanel);

        } catch (Exception e) {
            LOG.error("Error al crear vista de asignaciones", e);
        }

        return vista;
    }

    // Método para cargar asignaciones en la tabla
    private void cargarAsignaciones(TableView<GrupoMateria> tabla) {
        try {
            ObservableList<GrupoMateria> asignacionesList = FXCollections.observableArrayList(
                    grupoMateriaService.obtenerTodasLasAsignaciones()
            );
            tabla.setItems(asignacionesList);
            LOG.info("Asignaciones cargadas correctamente: {} asignaciones", asignacionesList.size());
        } catch (Exception e) {
            LOG.error("Error al cargar asignaciones en la tabla", e);
            tabla.setItems(FXCollections.observableArrayList());
        }
    }

    // Método para crear la vista completa de criterios de evaluación
    private VBox crearVistaCriteriosCompleta() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        try {

            // Panel de formulario
            VBox formPanel = new VBox(15);
            formPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

            Label lblFormTitle = new Label("Registrar Criterio");
            lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
            gridForm.setHgap(15);
            gridForm.setVgap(10);

            // COLUMNA 1: Nombre, Tipo de Evaluación, Puntuación Máxima

            // Campo Nombre
            Label lblNombre = new Label("Nombre:");
            TextField txtNombre = new TextField();
            txtNombre.setPromptText("Nombre del criterio");
            txtNombre.setPrefWidth(250);

            // ComboBox Tipo de Evaluación
            Label lblTipoEval = new Label("Tipo de Evaluación:");
            ComboBox<String> cmbTipoEval = new ComboBox<>();
            cmbTipoEval.getItems().addAll("Check", "Puntuacion");
            cmbTipoEval.setPromptText("Seleccione tipo");
            cmbTipoEval.setPrefWidth(250);

            // Campo Puntuación Máxima
            Label lblPuntMax = new Label("Puntuación Máxima:");
            TextField txtPuntMax = new TextField();
            txtPuntMax.setPromptText("Máx. 99");
            txtPuntMax.setPrefWidth(150);
            // Campo siempre habilitado/editable independientemente del tipo de evaluación

            // Validación para máximo 2 dígitos
            txtPuntMax.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.isEmpty()) {
                    // Solo permitir números y máximo 2 dígitos
                    if (!newValue.matches("\\d{0,2}")) {
                        txtPuntMax.setText(oldValue);
                    }
                }
            });

            // COLUMNA 2: Materia y Cuatrimestre

            // ComboBox Materia
            Label lblMateria = new Label("Materia:");
            ComboBox<Materia> cmbMateria = new ComboBox<>();
            cmbMateria.setPromptText("Seleccione una materia");
            cmbMateria.setPrefWidth(250);

            try {
                cmbMateria.setItems(FXCollections.observableArrayList(materiaService.obtenerTodasLasMaterias()));
            } catch (Exception e) {
                LOG.error("Error al cargar materias", e);
            }

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
                    setText(empty || item == null ? null : item.getNombre());
                }
            });

            // ComboBox Parcial
            Label lblParcial = new Label("Parcial:");
            ComboBox<Integer> cmbParcial = new ComboBox<>();
            cmbParcial.setPromptText("Seleccione parcial");
            cmbParcial.setPrefWidth(250);
            cmbParcial.setItems(FXCollections.observableArrayList(1, 2, 3));

            // El campo de puntuación máxima siempre está habilitado/editable

            // Distribuir en 2 columnas:
            // Columna 1 (0-1): Nombre, Tipo Eval, Punt Max
            // Columna 2 (2-3): Materia, Parcial
            gridForm.add(lblNombre, 0, 0);
            gridForm.add(txtNombre, 1, 0);
            gridForm.add(lblTipoEval, 0, 1);
            gridForm.add(cmbTipoEval, 1, 1);
            gridForm.add(lblPuntMax, 0, 2);
            gridForm.add(txtPuntMax, 1, 2);

            gridForm.add(lblMateria, 2, 0);
            gridForm.add(cmbMateria, 3, 0);
            gridForm.add(lblParcial, 2, 1);
            gridForm.add(cmbParcial, 3, 1);

            // Botones
            javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
            Button btnGuardar = new Button("Guardar");
            btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");

            Button btnLimpiar = new Button("Limpiar");
            btnLimpiar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");

            buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);
            formPanel.getChildren().addAll(lblFormTitle, new Separator(), gridForm, buttonBox);

            // Panel de tabla
            VBox tablePanel = new VBox(10);
            tablePanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

            Label lblTableTitle = new Label("Lista de Criterios");
            lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            // Panel de filtro y acciones
            javafx.scene.layout.HBox filterBox = new javafx.scene.layout.HBox(10);
            filterBox.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

            Label lblFiltrarPorMateria = new Label("Filtrar por Materia:");
            lblFiltrarPorMateria.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            ComboBox<Materia> cmbFiltroMateria = new ComboBox<>();
            cmbFiltroMateria.setPromptText("Todas las materias");
            cmbFiltroMateria.setPrefWidth(250);

            try {
                List<Materia> materias = materiaService.obtenerTodasLasMaterias();
                cmbFiltroMateria.setItems(FXCollections.observableArrayList(materias));
            } catch (Exception e) {
                LOG.error("Error al cargar materias para filtro", e);
            }

            cmbFiltroMateria.setCellFactory(param -> new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
            cmbFiltroMateria.setButtonCell(new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Todas las materias" : item.getNombre());
                }
            });

            Label lblFiltrarPorParcial = new Label("Filtrar por Parcial:");
            lblFiltrarPorParcial.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            ComboBox<Integer> cmbFiltroParcial = new ComboBox<>();
            cmbFiltroParcial.setPromptText("Todos los parciales");
            cmbFiltroParcial.setPrefWidth(250);
            cmbFiltroParcial.setItems(FXCollections.observableArrayList(1, 2, 3));

            Button btnLimpiarFiltro = new Button("Limpiar Filtro");
            btnLimpiarFiltro.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-cursor: hand;");

            Region spacer = new Region();
            javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Button btnGuardarOrden = new Button("Guardar Orden");
            btnGuardarOrden.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-cursor: hand;");

            filterBox.getChildren().addAll(lblFiltrarPorMateria, cmbFiltroMateria, lblFiltrarPorParcial, cmbFiltroParcial, btnLimpiarFiltro, spacer, btnGuardarOrden);

            // Tabla
            TableView<Criterio> tblCriterios = new TableView<>();
            tblCriterios.setPrefHeight(300);

            TableColumn<Criterio, Long> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colId.setPrefWidth(60);
            colId.setVisible(false); // Ocultar columna ID

            TableColumn<Criterio, String> colNombre = new TableColumn<>("Nombre");
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colNombre.setPrefWidth(200);

            TableColumn<Criterio, String> colTipo = new TableColumn<>("Tipo");
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoEvaluacion"));
            colTipo.setPrefWidth(100);

            TableColumn<Criterio, Double> colPuntMax = new TableColumn<>("Punt. Máx.");
            colPuntMax.setCellValueFactory(new PropertyValueFactory<>("puntuacionMaxima"));
            colPuntMax.setPrefWidth(100);
            // Formatear a dos dígitos
            colPuntMax.setCellFactory(column -> new TableCell<Criterio, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("%02d", item.intValue()));
                    }
                }
            });

            TableColumn<Criterio, Integer> colOrden = new TableColumn<>("Orden");
            colOrden.setCellValueFactory(new PropertyValueFactory<>("orden"));
            colOrden.setPrefWidth(80);
            colOrden.setStyle("-fx-alignment: CENTER;");

            TableColumn<Criterio, Integer> colParcial = new TableColumn<>("Parcial");
            colParcial.setCellValueFactory(new PropertyValueFactory<>("parcial"));
            colParcial.setPrefWidth(80);
            colParcial.setStyle("-fx-alignment: CENTER;");

            TableColumn<Criterio, String> colMateria = new TableColumn<>("Materia");
            colMateria.setPrefWidth(200);
            colMateria.setCellValueFactory(cellData -> {
                Criterio criterio = cellData.getValue();
                if (criterio.getMateriaId() != null) {
                    try {
                        return new javafx.beans.property.SimpleStringProperty(
                            materiaService.obtenerMateriaPorId(criterio.getMateriaId())
                                .map(Materia::getNombre)
                                .orElse("Materia no encontrada")
                        );
                    } catch (Exception e) {
                        return new javafx.beans.property.SimpleStringProperty("Error");
                    }
                }
                return new javafx.beans.property.SimpleStringProperty("Sin materia");
            });

            // Columna de ordenamiento
            TableColumn<Criterio, Void> colOrdenAcciones = new TableColumn<>("Mover");
            colOrdenAcciones.setPrefWidth(100);
            colOrdenAcciones.setCellFactory(col -> new TableCell<>() {
                private final Button btnSubir = new Button("↑");
                private final Button btnBajar = new Button("↓");

                {
                    btnSubir.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10; -fx-cursor: hand; -fx-font-weight: bold;");
                    btnBajar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10; -fx-cursor: hand; -fx-font-weight: bold;");

                    btnSubir.setOnAction(event -> {
                        int index = getIndex();
                        if (index > 0) {
                            ObservableList<Criterio> items = getTableView().getItems();
                            Criterio criterio = items.get(index);
                            items.remove(index);
                            items.add(index - 1, criterio);
                            getTableView().getSelectionModel().select(index - 1);
                            getTableView().refresh();
                        }
                    });

                    btnBajar.setOnAction(event -> {
                        int index = getIndex();
                        ObservableList<Criterio> items = getTableView().getItems();
                        if (index < items.size() - 1) {
                            Criterio criterio = items.get(index);
                            items.remove(index);
                            items.add(index + 1, criterio);
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

                    // Mostrar controles solo cuando ambos filtros (materia y parcial) están seleccionados
                    boolean filtrosCompletos = cmbFiltroMateria.getValue() != null && cmbFiltroParcial.getValue() != null;
                    if (!filtrosCompletos) {
                        setGraphic(null);
                        return;
                    }

                    ObservableList<Criterio> items = getTableView().getItems();
                    int totalItems = items.size();
                    if (totalItems <= 1) {
                        setGraphic(null);
                        return;
                    }

                    int index = getIndex();
                    javafx.scene.layout.HBox contenedor = new javafx.scene.layout.HBox(5);
                    contenedor.setPrefWidth(Double.MAX_VALUE);
                    contenedor.setFillHeight(true);
                    Region leftSpacer = new Region();
                    Region rightSpacer = new Region();
                    javafx.scene.layout.HBox.setHgrow(leftSpacer, javafx.scene.layout.Priority.ALWAYS);
                    javafx.scene.layout.HBox.setHgrow(rightSpacer, javafx.scene.layout.Priority.ALWAYS);

                    if (index == 0) {
                        // Primer registro: botón bajar alineado a la derecha
                        contenedor.getChildren().setAll(leftSpacer, btnBajar);
                    } else if (index == totalItems - 1) {
                        // Último registro: botón subir alineado a la izquierda
                        contenedor.getChildren().setAll(btnSubir, rightSpacer);
                    } else {
                        // Registros intermedios: ambos botones centrados
                        contenedor.setAlignment(javafx.geometry.Pos.CENTER);
                        contenedor.getChildren().setAll(btnSubir, btnBajar);
                    }

                    setGraphic(contenedor);
                }
            });

            // Columna de acciones
            TableColumn<Criterio, Void> colAcciones = new TableColumn<>("Acciones");
            colAcciones.setPrefWidth(180);
            colAcciones.setCellFactory(col -> new TableCell<>() {
                private final javafx.scene.layout.HBox botonesBox = new javafx.scene.layout.HBox(5);
                private final Button btnEditar = new Button("Editar");
                private final Button btnEliminar = new Button("Eliminar");

                {
                    btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                    btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");

                    btnEditar.setOnAction(event -> {
                        Criterio criterio = getTableView().getItems().get(getIndex());
                        txtNombre.setText(criterio.getNombre());
                        cmbTipoEval.setValue(criterio.getTipoEvaluacion());

                        // Llenar el campo de puntuación si existe
                        if (criterio.getPuntuacionMaxima() != null) {
                            txtPuntMax.setText(String.valueOf(criterio.getPuntuacionMaxima().intValue()));
                        } else {
                            txtPuntMax.clear();
                        }

                        // Buscar y seleccionar la materia
                        for (Materia m : cmbMateria.getItems()) {
                            if (m.getId().equals(criterio.getMateriaId())) {
                                cmbMateria.setValue(m);
                                break;
                            }
                        }

                        // Seleccionar el parcial
                        if (criterio.getParcial() != null) {
                            cmbParcial.setValue(criterio.getParcial());
                        }

                        btnGuardar.setText("Actualizar");
                        btnGuardar.setUserData(criterio.getId());
                    });

                    btnEliminar.setOnAction(event -> {
                        Criterio criterio = getTableView().getItems().get(getIndex());
                        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmacion.setTitle("Confirmar eliminación");
                        confirmacion.setHeaderText(null);
                        confirmacion.setContentText("¿Está seguro de eliminar este criterio?");

                        confirmacion.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    criterioService.eliminarCriterio(criterio.getId());
                                    cargarCriterios(tblCriterios);
                                    mostrarAlerta("Éxito", "Criterio eliminado correctamente", Alert.AlertType.INFORMATION);
                                } catch (Exception e) {
                                    mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
                                }
                            }
                        });
                    });

                    botonesBox.getChildren().addAll(btnEditar, btnEliminar);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : botonesBox);
                }
            });

            tblCriterios.getColumns().addAll(colId, colNombre, colTipo, colPuntMax, colOrden, colParcial, colMateria, colOrdenAcciones, colAcciones);

            Label lblEstadisticas = new Label("Total de criterios: 0");
            lblEstadisticas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #666;");

            tablePanel.getChildren().addAll(lblTableTitle, new Separator(), filterBox, tblCriterios, lblEstadisticas);

            // Cargar criterios inicialmente
            cargarCriterios(tblCriterios);
            lblEstadisticas.setText("Total de criterios: " + tblCriterios.getItems().size());

            // Evento: Filtrar por materia
            cmbFiltroMateria.setOnAction(event -> {
                aplicarFiltrosCriterios(tblCriterios, cmbFiltroMateria, cmbFiltroParcial);
                lblEstadisticas.setText("Total de criterios: " + tblCriterios.getItems().size());
            });

            // Evento: Filtrar por parcial
            cmbFiltroParcial.setOnAction(event -> {
                aplicarFiltrosCriterios(tblCriterios, cmbFiltroMateria, cmbFiltroParcial);
                lblEstadisticas.setText("Total de criterios: " + tblCriterios.getItems().size());
            });

            // Evento: Limpiar filtro
            btnLimpiarFiltro.setOnAction(event -> {
                cmbFiltroMateria.setValue(null);
                cmbFiltroParcial.setValue(null);
                cargarCriterios(tblCriterios);
                lblEstadisticas.setText("Total de criterios: " + tblCriterios.getItems().size());
            });

            // Evento: Guardar orden
            btnGuardarOrden.setOnAction(event -> {
                try {
                    // Obtener la lista actual de la tabla
                    ObservableList<Criterio> criteriosOrdenados = tblCriterios.getItems();

                    if (criteriosOrdenados.isEmpty()) {
                        mostrarAlerta("Información", "No hay criterios para ordenar", Alert.AlertType.INFORMATION);
                        return;
                    }

                    // Verificar que haya un filtro de materia activo
                    Materia materiaFiltro = cmbFiltroMateria.getValue();

                    if (materiaFiltro == null) {
                        mostrarAlerta("Advertencia", "Debe seleccionar una materia para guardar el orden", Alert.AlertType.WARNING);
                        return;
                    }

                    // Verificar que todos los criterios sean de la misma materia y parcial
                    Long materiaId = materiaFiltro.getId();
                    Integer primerParcial = criteriosOrdenados.isEmpty() ? null : criteriosOrdenados.get(0).getParcial();

                    for (Criterio criterio : criteriosOrdenados) {
                        if (!materiaId.equals(criterio.getMateriaId())) {
                            mostrarAlerta("Error", "Todos los criterios deben ser de la misma materia", Alert.AlertType.ERROR);
                            return;
                        }
                        if (!criterio.getParcial().equals(primerParcial)) {
                            mostrarAlerta("Error", "Todos los criterios deben ser del mismo parcial para guardar el orden", Alert.AlertType.ERROR);
                            return;
                        }
                    }

                    // Actualizar el orden en la base de datos según la posición actual en la tabla
                    int nuevoOrden = 1;
                    for (Criterio criterio : criteriosOrdenados) {
                        criterioService.actualizarOrdenCriterio(criterio.getId(), nuevoOrden++);
                    }

                    mostrarAlerta("Éxito", "El orden de los criterios de la materia '" + materiaFiltro.getNombre() + "' (Parcial " + primerParcial + ") se guardó correctamente", Alert.AlertType.INFORMATION);

                    // Recargar la tabla manteniendo el filtro
                    //filtrarCriteriosPorMateria(tblCriterios, materiaId);
                    aplicarFiltrosCriterios(tblCriterios, cmbFiltroMateria, cmbFiltroParcial);
                    lblEstadisticas.setText("Total de criterios: " + tblCriterios.getItems().size());

                } catch (Exception e) {
                    LOG.error("Error al guardar orden de criterios", e);
                    mostrarAlerta("Error", "Error al guardar el orden: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            // Eventos
            btnGuardar.setOnAction(event -> {
                try {
                    if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
                        mostrarAlerta("Validación", "El nombre es requerido", Alert.AlertType.WARNING);
                        return;
                    }
                    if (cmbTipoEval.getValue() == null) {
                        mostrarAlerta("Validación", "Debe seleccionar un tipo de evaluación", Alert.AlertType.WARNING);
                        return;
                    }
                    if (cmbMateria.getValue() == null) {
                        mostrarAlerta("Validación", "Debe seleccionar una materia", Alert.AlertType.WARNING);
                        return;
                    }
                    if (cmbParcial.getValue() == null) {
                        mostrarAlerta("Validación", "Debe seleccionar un parcial", Alert.AlertType.WARNING);
                        return;
                    }

                    // Validar que la puntuación máxima no esté vacía
                    if (txtPuntMax.getText() == null || txtPuntMax.getText().trim().isEmpty()) {
                        mostrarAlerta("Validación", "La puntuación máxima es requerida", Alert.AlertType.WARNING);
                        return;
                    }

                    // Procesar puntuación máxima - independientemente del tipo de evaluación
                    Double puntuacionMax = null;
                    try {
                        int valor = Integer.parseInt(txtPuntMax.getText().trim());
                        if (valor <= 0 || valor > 99) {
                            mostrarAlerta("Error", "La puntuación máxima debe ser entre 1 y 99", Alert.AlertType.ERROR);
                            return;
                        }
                        puntuacionMax = (double) valor;
                    } catch (NumberFormatException e) {
                        mostrarAlerta("Error", "La puntuación máxima debe ser un número válido", Alert.AlertType.ERROR);
                        return;
                    }

                    Criterio.CriterioBuilder builder = Criterio.builder()
                            .nombre(txtNombre.getText().trim())
                            .tipoEvaluacion(cmbTipoEval.getValue())
                            .puntuacionMaxima(puntuacionMax)
                            .materiaId(cmbMateria.getValue().getId())
                            .parcial(cmbParcial.getValue());

                    if ("Actualizar".equals(btnGuardar.getText()) && btnGuardar.getUserData() != null) {
                        // Actualizar
                        builder.id((Long) btnGuardar.getUserData());
                        Criterio criterio = builder.build();
                        criterioService.actualizarCriterio(criterio);
                        mostrarAlerta("Éxito", "Criterio actualizado correctamente", Alert.AlertType.INFORMATION);
                    } else {
                        // Crear
                        Criterio criterio = builder.build();
                        criterioService.crearCriterio(criterio);
                        mostrarAlerta("Éxito", "Criterio creado correctamente", Alert.AlertType.INFORMATION);
                    }

                    // Limpiar formulario
                    txtNombre.clear();
                    cmbTipoEval.setValue(null);
                    txtPuntMax.clear();
                    cmbMateria.setValue(null);
                    cmbParcial.setValue(null);
                    btnGuardar.setText("Guardar");
                    btnGuardar.setUserData(null);

                    cargarCriterios(tblCriterios);
                    lblEstadisticas.setText("Total de criterios: " + tblCriterios.getItems().size());

                } catch (IllegalArgumentException e) {
                    mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
                } catch (Exception e) {
                    mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            btnLimpiar.setOnAction(event -> {
                txtNombre.clear();
                cmbTipoEval.setValue(null);
                txtPuntMax.clear();
                cmbMateria.setValue(null);
                cmbParcial.setValue(null);
                btnGuardar.setText("Guardar");
                btnGuardar.setUserData(null);
            });

            vista.getChildren().addAll(formPanel, tablePanel);

        } catch (Exception e) {
            LOG.error("Error al crear vista de criterios", e);
        }

        return vista;
    }

    // Método para cargar criterios en la tabla
    private void cargarCriterios(TableView<Criterio> tabla) {
        try {
            ObservableList<Criterio> criteriosList = FXCollections.observableArrayList(
                    criterioService.obtenerTodosLosCriterios()
            );
            tabla.setItems(criteriosList);

            // Forzar el refresh de la tabla para que las columnas de botones se actualicen
            tabla.refresh();

            LOG.info("Criterios cargados correctamente: {} criterios", criteriosList.size());
        } catch (Exception e) {
            LOG.error("Error al cargar criterios en la tabla", e);
            tabla.setItems(FXCollections.observableArrayList());
        }
    }

    // Método para crear la vista completa de agregados
    private VBox crearVistaAgregadosCompleta() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        try {

            // Panel de formulario
            VBox formPanel = new VBox(15);
            formPanel.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-background-radius: 5;");

            Label lblFormTitle = new Label("Información del Agregado");
            lblFormTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

            javafx.scene.layout.GridPane gridForm = new javafx.scene.layout.GridPane();
            gridForm.setHgap(15);
            gridForm.setVgap(15);
            gridForm.setStyle("-fx-padding: 15;");

            // Campo Nombre
            Label lblNombre = new Label("Nombre:");
            TextField txtNombre = new TextField();
            txtNombre.setPromptText("Nombre del agregado");
            txtNombre.setPrefWidth(300);

            // ComboBox Grupo
            Label lblGrupo = new Label("Grupo:");
            ComboBox<Grupo> cmbGrupo = new ComboBox<>();
            cmbGrupo.setPromptText("Seleccione un grupo");
            cmbGrupo.setPrefWidth(300);

            // Cargar grupos
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
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.valueOf(item.getId()));
                    }
                }
            });
            cmbGrupo.setButtonCell(new ListCell<Grupo>() {
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

            // ComboBox Materia
            Label lblMateria = new Label("Materia:");
            ComboBox<Materia> cmbMateria = new ComboBox<>();
            cmbMateria.setPromptText("Seleccione primero un grupo");
            cmbMateria.setPrefWidth(300);
            cmbMateria.setDisable(true);

            // Listas para almacenar todas las asignaciones y materias
            final List<GrupoMateria> todasAsignaciones = new java.util.ArrayList<>();
            final List<Materia> todasMaterias = new java.util.ArrayList<>();

            try {
                todasAsignaciones.addAll(grupoMateriaService.obtenerTodasLasAsignaciones());
                todasMaterias.addAll(materiaService.obtenerTodasLasMaterias());
            } catch (Exception e) {
                LOG.error("Error al cargar asignaciones y materias", e);
            }

            cmbMateria.setCellFactory(param -> new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNombre());
                    }
                }
            });
            cmbMateria.setButtonCell(new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNombre());
                    }
                }
            });

            // ComboBox Parcial
            Label lblParcial = new Label("Parcial:");
            ComboBox<Integer> cmbParcial = new ComboBox<>();
            cmbParcial.setPromptText("Seleccione primero una materia");
            cmbParcial.setPrefWidth(300);
            cmbParcial.setDisable(true);
            cmbParcial.setItems(FXCollections.observableArrayList(1, 2, 3));

            // ComboBox Criterio de Evaluación
            Label lblCriterio = new Label("Criterio de Evaluación:");
            ComboBox<Criterio> cmbCriterio = new ComboBox<>();
            cmbCriterio.setPromptText("Seleccione primero un parcial");
            cmbCriterio.setPrefWidth(400);
            cmbCriterio.setDisable(true);

            // Lista completa de criterios para filtrar
            final List<Criterio> todosCriterios = new java.util.ArrayList<>();
            try {
                List<Criterio> criterios = criterioService.obtenerTodosLosCriterios();
                for (Criterio criterio : criterios) {
                    try {
                        Materia materia = materiaService.obtenerMateriaPorId(criterio.getMateriaId()).orElse(null);
                        if (materia != null) {
                            criterio.setNombreMateria(materia.getNombre());
                        }
                    } catch (Exception e) {
                        LOG.error("Error al cargar materia", e);
                    }
                }
                todosCriterios.addAll(criterios);
            } catch (Exception e) {
                LOG.error("Error al cargar criterios", e);
            }

            cmbCriterio.setCellFactory(param -> new ListCell<Criterio>() {
                @Override
                protected void updateItem(Criterio item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNombre() + " - " + (item.getNombreMateria() != null ? item.getNombreMateria() : "Sin materia") + " (Parcial " + item.getParcial() + ")");
                    }
                }
            });
            cmbCriterio.setButtonCell(new ListCell<Criterio>() {
                @Override
                protected void updateItem(Criterio item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNombre() + " - " + (item.getNombreMateria() != null ? item.getNombreMateria() : "Sin materia") + " (Parcial " + item.getParcial() + ")");
                    }
                }
            });

            // Evento cuando se selecciona un grupo
            cmbGrupo.setOnAction(event -> {
                Grupo grupoSeleccionado = cmbGrupo.getValue();
                if (grupoSeleccionado != null) {
                    // Filtrar materias asignadas a este grupo
                    List<Long> materiaIdsDelGrupo = todasAsignaciones.stream()
                            .filter(gm -> gm.getGrupoId().equals(grupoSeleccionado.getId()))
                            .map(GrupoMateria::getMateriaId)
                            .collect(java.util.stream.Collectors.toList());

                    List<Materia> materiasDelGrupo = todasMaterias.stream()
                            .filter(m -> materiaIdsDelGrupo.contains(m.getId()))
                            .collect(java.util.stream.Collectors.toList());

                    if (materiasDelGrupo.isEmpty()) {
                        cmbMateria.setItems(FXCollections.observableArrayList());
                        cmbMateria.setValue(null);
                        cmbMateria.setDisable(true);
                        cmbMateria.setPromptText("No hay materias asignadas al grupo");
                    } else {
                        cmbMateria.setItems(FXCollections.observableArrayList(materiasDelGrupo));
                        cmbMateria.setValue(null);
                        cmbMateria.setDisable(false);
                        cmbMateria.setPromptText("Seleccione una materia");
                    }
                } else {
                    cmbMateria.setItems(FXCollections.observableArrayList());
                    cmbMateria.setValue(null);
                    cmbMateria.setDisable(true);
                    cmbMateria.setPromptText("Seleccione primero un grupo");
                }

                // Resetear parcial y criterio
                cmbParcial.setValue(null);
                cmbParcial.setDisable(true);
                cmbParcial.setPromptText("Seleccione primero una materia");
                cmbCriterio.setItems(FXCollections.observableArrayList());
                cmbCriterio.setValue(null);
                cmbCriterio.setDisable(true);
                cmbCriterio.setPromptText("Seleccione primero un parcial");
            });

            // Evento cuando se selecciona una materia
            cmbMateria.setOnAction(event -> {
                Materia materiaSeleccionada = cmbMateria.getValue();
                if (materiaSeleccionada != null) {
                    // Habilitar el selector de parcial
                    cmbParcial.setValue(null);
                    cmbParcial.setDisable(false);
                    cmbParcial.setPromptText("Seleccione un parcial");
                } else {
                    cmbParcial.setValue(null);
                    cmbParcial.setDisable(true);
                    cmbParcial.setPromptText("Seleccione primero una materia");
                }

                // Resetear criterio
                cmbCriterio.setItems(FXCollections.observableArrayList());
                cmbCriterio.setValue(null);
                cmbCriterio.setDisable(true);
                cmbCriterio.setPromptText("Seleccione primero un parcial");
            });

            // Evento cuando se selecciona un parcial
            cmbParcial.setOnAction(event -> {
                Integer parcialSeleccionado = cmbParcial.getValue();
                Materia materiaSeleccionada = cmbMateria.getValue();

                if (parcialSeleccionado != null && materiaSeleccionada != null) {
                    // Filtrar criterios por materia y parcial
                    List<Criterio> criteriosFiltrados = todosCriterios.stream()
                            .filter(c -> c.getParcial() != null && c.getParcial().equals(parcialSeleccionado))
                            .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaSeleccionada.getId()))
                            .collect(java.util.stream.Collectors.toList());

                    if (criteriosFiltrados.isEmpty()) {
                        cmbCriterio.setItems(FXCollections.observableArrayList());
                        cmbCriterio.setValue(null);
                        cmbCriterio.setDisable(true);
                        cmbCriterio.setPromptText("No hay criterios para esta materia y parcial");
                    } else {
                        cmbCriterio.setItems(FXCollections.observableArrayList(criteriosFiltrados));
                        cmbCriterio.setValue(null);
                        cmbCriterio.setDisable(false);
                        cmbCriterio.setPromptText("Seleccione un criterio");
                    }
                } else {
                    cmbCriterio.setItems(FXCollections.observableArrayList());
                    cmbCriterio.setValue(null);
                    cmbCriterio.setDisable(true);
                    cmbCriterio.setPromptText("Seleccione primero un parcial");
                }
            });

            gridForm.add(lblNombre, 0, 0);
            gridForm.add(txtNombre, 1, 0);
            gridForm.add(lblGrupo, 0, 1);
            gridForm.add(cmbGrupo, 1, 1);
            gridForm.add(lblMateria, 0, 2);
            gridForm.add(cmbMateria, 1, 2);
            gridForm.add(lblParcial, 0, 3);
            gridForm.add(cmbParcial, 1, 3);
            gridForm.add(lblCriterio, 0, 4);
            gridForm.add(cmbCriterio, 1, 4);

            // Botones
            javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
            Button btnGuardar = new Button("Guardar");
            btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");

            Button btnLimpiar = new Button("Limpiar");
            btnLimpiar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");

            buttonBox.getChildren().addAll(btnGuardar, btnLimpiar);
            buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0;");

            formPanel.getChildren().addAll(lblFormTitle, new Separator(), gridForm, buttonBox);

            // Panel de tabla
            VBox tablePanel = new VBox(15);
            tablePanel.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-background-radius: 5;");

            Label lblTableTitle = new Label("Lista de Agregados");
            lblTableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Panel de filtro y acciones
            javafx.scene.layout.HBox filterBox = new javafx.scene.layout.HBox(10);
            filterBox.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

            Label lblFiltroGrupo = new Label("Grupo:");
            lblFiltroGrupo.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

            ComboBox<Grupo> cmbFiltroGrupoAgregados = new ComboBox<>();
            cmbFiltroGrupoAgregados.setPromptText("Todos");
            cmbFiltroGrupoAgregados.setPrefWidth(80);

            try {
                List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
                cmbFiltroGrupoAgregados.setItems(FXCollections.observableArrayList(grupos));
            } catch (Exception e) {
                LOG.error("Error al cargar grupos para filtro", e);
            }

            cmbFiltroGrupoAgregados.setCellFactory(param -> new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.valueOf(item.getId()));
                }
            });
            cmbFiltroGrupoAgregados.setButtonCell(new ListCell<Grupo>() {
                @Override
                protected void updateItem(Grupo item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Todos" : String.valueOf(item.getId()));
                }
            });

            Label lblFiltroMateria = new Label("Materia:");
            lblFiltroMateria.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

            ComboBox<Materia> cmbFiltroMateriaAgregados = new ComboBox<>();
            cmbFiltroMateriaAgregados.setPromptText("Seleccione grupo");
            cmbFiltroMateriaAgregados.setPrefWidth(150);
            cmbFiltroMateriaAgregados.setDisable(true);

            cmbFiltroMateriaAgregados.setCellFactory(param -> new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
            cmbFiltroMateriaAgregados.setButtonCell(new ListCell<Materia>() {
                @Override
                protected void updateItem(Materia item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Todas" : item.getNombre());
                }
            });

            Label lblFiltroParcial = new Label("Parcial:");
            lblFiltroParcial.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

            ComboBox<Integer> cmbFiltroParcialAgregados = new ComboBox<>();
            cmbFiltroParcialAgregados.setPromptText("Todos");
            cmbFiltroParcialAgregados.setPrefWidth(80);
            cmbFiltroParcialAgregados.setDisable(true);
            cmbFiltroParcialAgregados.setItems(FXCollections.observableArrayList(1, 2, 3));

            Label lblFiltroCriterio = new Label("Criterio:");
            lblFiltroCriterio.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

            ComboBox<Criterio> cmbFiltroCriterioAgregados = new ComboBox<>();
            cmbFiltroCriterioAgregados.setPromptText("Seleccione parcial");
            cmbFiltroCriterioAgregados.setPrefWidth(180);
            cmbFiltroCriterioAgregados.setDisable(true);

            cmbFiltroCriterioAgregados.setCellFactory(param -> new ListCell<Criterio>() {
                @Override
                protected void updateItem(Criterio item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        try {
                            Materia materia = materiaService.obtenerMateriaPorId(item.getMateriaId()).orElse(null);
                            String nombreMateria = materia != null ? materia.getNombre() : "";
                            setText(item.getNombre() + " - " + nombreMateria);
                        } catch (Exception e) {
                            setText(item.getNombre());
                        }
                    }
                }
            });
            cmbFiltroCriterioAgregados.setButtonCell(new ListCell<Criterio>() {
                @Override
                protected void updateItem(Criterio item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Todos");
                    } else {
                        try {
                            Materia materia = materiaService.obtenerMateriaPorId(item.getMateriaId()).orElse(null);
                            String nombreMateria = materia != null ? materia.getNombre() : "";
                            setText(item.getNombre() + " - " + nombreMateria);
                        } catch (Exception e) {
                            setText(item.getNombre());
                        }
                    }
                }
            });

            Button btnLimpiarFiltroAgregados = new Button("Limpiar");
            btnLimpiarFiltroAgregados.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 8 15; -fx-cursor: hand;");

            Region spacer = new Region();
            javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Button btnGuardarOrdenAgregados = new Button("Guardar Orden");
            btnGuardarOrdenAgregados.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 8 15; -fx-cursor: hand;");

            filterBox.getChildren().addAll(lblFiltroGrupo, cmbFiltroGrupoAgregados, lblFiltroMateria, cmbFiltroMateriaAgregados,
                                          lblFiltroParcial, cmbFiltroParcialAgregados, lblFiltroCriterio, cmbFiltroCriterioAgregados,
                                          btnLimpiarFiltroAgregados, spacer, btnGuardarOrdenAgregados);


            // Tabla
            TableView<Agregado> tblAgregados = new TableView<>();
            tblAgregados.setPrefHeight(300);

            TableColumn<Agregado, Long> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colId.setPrefWidth(60);

            TableColumn<Agregado, String> colNombre = new TableColumn<>("Nombre");
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colNombre.setPrefWidth(200);

            TableColumn<Agregado, String> colCriterio = new TableColumn<>("Criterio de Evaluación");
            colCriterio.setPrefWidth(250);
            colCriterio.setCellValueFactory(cellData -> {
                Agregado agregado = cellData.getValue();
                if (agregado.getCriterioId() != null) {
                    try {
                        Criterio criterio = criterioService.obtenerCriterioPorId(agregado.getCriterioId()).orElse(null);
                        if (criterio != null) {
                            return new javafx.beans.property.SimpleStringProperty(criterio.getNombre());
                        }
                    } catch (Exception e) {
                        LOG.error("Error al cargar criterio", e);
                    }
                }
                return new javafx.beans.property.SimpleStringProperty("Criterio no encontrado");
            });

            TableColumn<Agregado, String> colMateria = new TableColumn<>("Materia");
            colMateria.setPrefWidth(200);
            colMateria.setCellValueFactory(cellData -> {
                Agregado agregado = cellData.getValue();
                if (agregado.getCriterioId() != null) {
                    try {
                        Criterio criterio = criterioService.obtenerCriterioPorId(agregado.getCriterioId()).orElse(null);
                        if (criterio != null && criterio.getMateriaId() != null) {
                            Materia materia = materiaService.obtenerMateriaPorId(criterio.getMateriaId()).orElse(null);
                            if (materia != null) {
                                return new javafx.beans.property.SimpleStringProperty(materia.getNombre());
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("Error al cargar materia del criterio", e);
                    }
                }
                return new javafx.beans.property.SimpleStringProperty("Sin materia");
            });

            TableColumn<Agregado, Integer> colOrden = new TableColumn<>("Orden");
            colOrden.setCellValueFactory(new PropertyValueFactory<>("orden"));
            colOrden.setPrefWidth(80);
            colOrden.setStyle("-fx-alignment: CENTER;");

            // Columna de ordenamiento
            TableColumn<Agregado, Void> colOrdenAcciones = new TableColumn<>("Mover");
            colOrdenAcciones.setPrefWidth(100);
            colOrdenAcciones.setCellFactory(col -> new TableCell<>() {
                private final Button btnSubir = new Button("↑");
                private final Button btnBajar = new Button("↓");

                {
                    btnSubir.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10; -fx-cursor: hand; -fx-font-weight: bold;");
                    btnBajar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10; -fx-cursor: hand; -fx-font-weight: bold;");

                    btnSubir.setOnAction(event -> {
                        int index = getIndex();
                        if (index > 0) {
                            ObservableList<Agregado> items = getTableView().getItems();
                            Agregado agregado = items.get(index);
                            items.remove(index);
                            items.add(index - 1, agregado);
                            getTableView().getSelectionModel().select(index - 1);
                        }
                    });

                    btnBajar.setOnAction(event -> {
                        int index = getIndex();
                        ObservableList<Agregado> items = getTableView().getItems();
                        if (index < items.size() - 1) {
                            Agregado agregado = items.get(index);
                            items.remove(index);
                            items.add(index + 1, agregado);
                            getTableView().getSelectionModel().select(index + 1);
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

                    // Mostrar controles solo cuando ambos filtros (materia y cuatrimestre) están seleccionados
                    boolean filtrosCompletos = cmbFiltroMateriaAgregados.getValue() != null && cmbFiltroCriterioAgregados.getValue() != null;
                    if (!filtrosCompletos) {
                        setGraphic(null);
                        return;
                    }

                    ObservableList<Agregado> items = getTableView().getItems();
                    int totalItems = items.size();
                    if (totalItems <= 1) {
                        setGraphic(null);
                        return;
                    }

                    int index = getIndex();
                    javafx.scene.layout.HBox contenedor = new javafx.scene.layout.HBox(5);
                    contenedor.setPrefWidth(Double.MAX_VALUE);
                    contenedor.setFillHeight(true);
                    Region leftSpacer = new Region();
                    Region rightSpacer = new Region();
                    javafx.scene.layout.HBox.setHgrow(leftSpacer, javafx.scene.layout.Priority.ALWAYS);
                    javafx.scene.layout.HBox.setHgrow(rightSpacer, javafx.scene.layout.Priority.ALWAYS);

                    if (index == 0) {
                        // Primer registro: botón bajar alineado a la derecha
                        contenedor.getChildren().setAll(leftSpacer, btnBajar);
                    } else if (index == totalItems - 1) {
                        // Último registro: botón subir alineado a la izquierda
                        contenedor.getChildren().setAll(btnSubir, rightSpacer);
                    } else {
                        // Registros intermedios: ambos botones centrados
                        contenedor.setAlignment(javafx.geometry.Pos.CENTER);
                        contenedor.getChildren().setAll(btnSubir, btnBajar);
                    }

                    setGraphic(contenedor);
                }
            });

            TableColumn<Agregado, Void> colAcciones = new TableColumn<>("Acciones");
            colAcciones.setPrefWidth(150);
            colAcciones.setCellFactory(col -> new TableCell<>() {
                private final javafx.scene.layout.HBox botonesBox = new javafx.scene.layout.HBox(5);
                private final Button btnEditar = new Button("Editar");
                private final Button btnEliminar = new Button("Eliminar");

                {
                    btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");
                    btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15; -fx-cursor: hand;");

                    btnEditar.setOnAction(event -> {
                        Agregado agregado = getTableView().getItems().get(getIndex());
                        txtNombre.setText(agregado.getNombre());

                        // Encontrar el criterio completo para obtener su materia y parcial
                        try {
                            Criterio criterioSeleccionado = criterioService.obtenerCriterioPorId(agregado.getCriterioId()).orElse(null);
                            if (criterioSeleccionado != null) {
                                Long materiaId = criterioSeleccionado.getMateriaId();
                                Integer parcial = criterioSeleccionado.getParcial();

                                // Encontrar el grupo que tiene asignada esta materia
                                Grupo grupoEncontrado = null;
                                for (GrupoMateria asignacion : todasAsignaciones) {
                                    if (asignacion.getMateriaId().equals(materiaId)) {
                                        // Buscar el grupo
                                        for (Grupo g : cmbGrupo.getItems()) {
                                            if (g.getId().equals(asignacion.getGrupoId())) {
                                                grupoEncontrado = g;
                                                break;
                                            }
                                        }
                                        if (grupoEncontrado != null) break;
                                    }
                                }

                                if (grupoEncontrado != null) {
                                    // 1. Seleccionar el grupo
                                    cmbGrupo.setValue(grupoEncontrado);

                                    // 2. Buscar y seleccionar la materia en la lista filtrada
                                    for (Materia m : cmbMateria.getItems()) {
                                        if (m.getId().equals(materiaId)) {
                                            cmbMateria.setValue(m);
                                            break;
                                        }
                                    }

                                    // 3. Seleccionar el parcial
                                    if (parcial != null) {
                                        cmbParcial.setValue(parcial);

                                        // 4. Buscar y seleccionar el criterio en la lista filtrada
                                        for (Criterio c : cmbCriterio.getItems()) {
                                            if (c.getId().equals(agregado.getCriterioId())) {
                                                cmbCriterio.setValue(c);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOG.error("Error al cargar criterio para editar", e);
                        }

                        btnGuardar.setText("Actualizar");
                        btnGuardar.setUserData(agregado.getId());
                    });

                    btnEliminar.setOnAction(event -> {
                        Agregado agregado = getTableView().getItems().get(getIndex());
                        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmacion.setTitle("Confirmar eliminación");
                        confirmacion.setHeaderText(null);
                        confirmacion.setContentText("¿Está seguro de eliminar el agregado: " + agregado.getNombre() + "?");

                        confirmacion.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    agregadoService.eliminarAgregado(agregado.getId());
                                    cargarAgregados(tblAgregados);
                                    mostrarAlerta("Éxito", "Agregado eliminado correctamente", Alert.AlertType.INFORMATION);
                                } catch (Exception e) {
                                    LOG.error("Error al eliminar agregado", e);
                                    mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
                                }
                            }
                        });
                    });

                    botonesBox.getChildren().addAll(btnEditar, btnEliminar);
                    botonesBox.setStyle("-fx-alignment: center;");
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

            tblAgregados.getColumns().addAll(colId, colNombre, colCriterio, colMateria, colOrden, colOrdenAcciones, colAcciones);

            Label lblEstadisticas = new Label("Total de agregados: 0");
            lblEstadisticas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #666;");

            tablePanel.getChildren().addAll(lblTableTitle, new Separator(), filterBox, tblAgregados, lblEstadisticas);

            // Cargar datos iniciales
            cargarAgregados(tblAgregados);

            // Evento: Filtrar por grupo
            cmbFiltroGrupoAgregados.setOnAction(event -> {
                Grupo grupoSeleccionado = cmbFiltroGrupoAgregados.getValue();
                if (grupoSeleccionado != null) {
                    // Cargar materias asignadas al grupo
                    try {
                        List<Long> materiaIdsDelGrupo = todasAsignaciones.stream()
                                .filter(gm -> gm.getGrupoId().equals(grupoSeleccionado.getId()))
                                .map(GrupoMateria::getMateriaId)
                                .collect(java.util.stream.Collectors.toList());

                        List<Materia> materiasDelGrupo = todasMaterias.stream()
                                .filter(m -> materiaIdsDelGrupo.contains(m.getId()))
                                .collect(java.util.stream.Collectors.toList());

                        if (materiasDelGrupo.isEmpty()) {
                            cmbFiltroMateriaAgregados.setItems(FXCollections.observableArrayList());
                            cmbFiltroMateriaAgregados.setValue(null);
                            cmbFiltroMateriaAgregados.setDisable(true);
                            cmbFiltroMateriaAgregados.setPromptText("No hay materias para este grupo");
                        } else {
                            cmbFiltroMateriaAgregados.setItems(FXCollections.observableArrayList(materiasDelGrupo));
                            cmbFiltroMateriaAgregados.setValue(null);
                            cmbFiltroMateriaAgregados.setDisable(false);
                            cmbFiltroMateriaAgregados.setPromptText("Todas las materias");
                        }
                    } catch (Exception e) {
                        LOG.error("Error al cargar materias del grupo", e);
                    }
                } else {
                    cmbFiltroMateriaAgregados.setItems(FXCollections.observableArrayList());
                    cmbFiltroMateriaAgregados.setValue(null);
                    cmbFiltroMateriaAgregados.setDisable(true);
                    cmbFiltroMateriaAgregados.setPromptText("Seleccione primero un grupo");
                }

                // Resetear parcial y criterio
                cmbFiltroParcialAgregados.setValue(null);
                cmbFiltroParcialAgregados.setDisable(true);
                cmbFiltroParcialAgregados.setPromptText("Seleccione primero una materia");
                cmbFiltroCriterioAgregados.setItems(FXCollections.observableArrayList());
                cmbFiltroCriterioAgregados.setValue(null);
                cmbFiltroCriterioAgregados.setDisable(true);
                cmbFiltroCriterioAgregados.setPromptText("Seleccione primero un parcial");

                cargarAgregados(tblAgregados);
                lblEstadisticas.setText("Total de agregados: " + tblAgregados.getItems().size());
            });

            // Evento: Filtrar por materia
            cmbFiltroMateriaAgregados.setOnAction(event -> {
                Materia materiaSeleccionada = cmbFiltroMateriaAgregados.getValue();
                if (materiaSeleccionada != null) {
                    // Habilitar parcial
                    cmbFiltroParcialAgregados.setValue(null);
                    cmbFiltroParcialAgregados.setDisable(false);
                    cmbFiltroParcialAgregados.setPromptText("Todos los parciales");
                } else {
                    cmbFiltroParcialAgregados.setValue(null);
                    cmbFiltroParcialAgregados.setDisable(true);
                    cmbFiltroParcialAgregados.setPromptText("Seleccione primero una materia");
                }

                // Resetear criterio
                cmbFiltroCriterioAgregados.setItems(FXCollections.observableArrayList());
                cmbFiltroCriterioAgregados.setValue(null);
                cmbFiltroCriterioAgregados.setDisable(true);
                cmbFiltroCriterioAgregados.setPromptText("Seleccione primero un parcial");

                cargarAgregados(tblAgregados);
                lblEstadisticas.setText("Total de agregados: " + tblAgregados.getItems().size());
            });

            // Evento: Filtrar por parcial
            cmbFiltroParcialAgregados.setOnAction(event -> {
                Integer parcialSeleccionado = cmbFiltroParcialAgregados.getValue();
                Materia materiaSeleccionada = cmbFiltroMateriaAgregados.getValue();

                if (parcialSeleccionado != null && materiaSeleccionada != null) {
                    // Cargar criterios de la materia y parcial seleccionados
                    try {
                        List<Criterio> criteriosFiltrados = todosCriterios.stream()
                                .filter(c -> c.getMateriaId() != null && c.getMateriaId().equals(materiaSeleccionada.getId()))
                                .filter(c -> c.getParcial() != null && c.getParcial().equals(parcialSeleccionado))
                                .collect(java.util.stream.Collectors.toList());

                        if (criteriosFiltrados.isEmpty()) {
                            cmbFiltroCriterioAgregados.setItems(FXCollections.observableArrayList());
                            cmbFiltroCriterioAgregados.setValue(null);
                            cmbFiltroCriterioAgregados.setDisable(true);
                            cmbFiltroCriterioAgregados.setPromptText("No hay criterios para esta combinación");
                        } else {
                            cmbFiltroCriterioAgregados.setItems(FXCollections.observableArrayList(criteriosFiltrados));
                            cmbFiltroCriterioAgregados.setValue(null);
                            cmbFiltroCriterioAgregados.setDisable(false);
                            cmbFiltroCriterioAgregados.setPromptText("Todos los criterios");
                        }
                    } catch (Exception e) {
                        LOG.error("Error al cargar criterios", e);
                    }
                } else {
                    cmbFiltroCriterioAgregados.setItems(FXCollections.observableArrayList());
                    cmbFiltroCriterioAgregados.setValue(null);
                    cmbFiltroCriterioAgregados.setDisable(true);
                    cmbFiltroCriterioAgregados.setPromptText("Seleccione primero un parcial");
                }

                cargarAgregados(tblAgregados);
                lblEstadisticas.setText("Total de agregados: " + tblAgregados.getItems().size());
            });

            // Evento: Filtrar por criterio
            cmbFiltroCriterioAgregados.setOnAction(event -> {
                Criterio criterioSeleccionado = cmbFiltroCriterioAgregados.getValue();
                if (criterioSeleccionado != null) {
                    filtrarAgregadosPorCriterio(tblAgregados, criterioSeleccionado.getId());
                    lblEstadisticas.setText("Total de agregados: " + tblAgregados.getItems().size());
                } else {
                    cargarAgregados(tblAgregados);
                    lblEstadisticas.setText("Total de agregados: " + tblAgregados.getItems().size());
                }
            });

            // Evento: Limpiar filtro
            btnLimpiarFiltroAgregados.setOnAction(event -> {
                cmbFiltroGrupoAgregados.setValue(null);
                cmbFiltroMateriaAgregados.setValue(null);
                cmbFiltroMateriaAgregados.setDisable(true);
                cmbFiltroMateriaAgregados.setPromptText("Seleccione primero un grupo");
                cmbFiltroParcialAgregados.setValue(null);
                cmbFiltroParcialAgregados.setDisable(true);
                cmbFiltroParcialAgregados.setPromptText("Seleccione primero una materia");
                cmbFiltroCriterioAgregados.setValue(null);
                cmbFiltroCriterioAgregados.setItems(FXCollections.observableArrayList());
                cmbFiltroCriterioAgregados.setDisable(true);
                cmbFiltroCriterioAgregados.setPromptText("Seleccione primero un parcial");
                cargarAgregados(tblAgregados);
                lblEstadisticas.setText("Total de agregados: " + tblAgregados.getItems().size());
            });

            // Evento: Guardar orden
            btnGuardarOrdenAgregados.setOnAction(event -> {
                try {
                    // Obtener la lista actual de la tabla
                    ObservableList<Agregado> agregadosOrdenados = tblAgregados.getItems();

                    if (agregadosOrdenados.isEmpty()) {
                        mostrarAlerta("Información", "No hay agregados para ordenar", Alert.AlertType.INFORMATION);
                        return;
                    }

                    // Verificar que todos los agregados sean del mismo criterio
                    Long primerCriterioId = agregadosOrdenados.get(0).getCriterioId();
                    boolean todosMismoCriterio = agregadosOrdenados.stream()
                            .allMatch(a -> primerCriterioId.equals(a.getCriterioId()));

                    if (!todosMismoCriterio) {
                        mostrarAlerta("Advertencia",
                            "Los agregados visibles pertenecen a diferentes criterios.\n" +
                            "Para guardar el orden, asegúrese de que todos los agregados mostrados sean del mismo criterio.\n\n" +
                            "Sugerencia: Use el filtro por materia para ver agregados agrupados por criterio.",
                            Alert.AlertType.WARNING);
                        return;
                    }

                    // Obtener el nombre del criterio
                    Criterio criterio = criterioService.obtenerCriterioPorId(primerCriterioId).orElse(null);
                    String nombreCriterio = criterio != null ? criterio.getNombre() : "Criterio " + primerCriterioId;

                    // Actualizar el orden en la base de datos según la posición actual en la tabla
                    int nuevoOrden = 1;
                    for (Agregado agregado : agregadosOrdenados) {
                        agregadoService.actualizarOrdenAgregado(agregado.getId(), nuevoOrden++);
                    }

                    mostrarAlerta("Éxito", "El orden de los agregados del criterio '" + nombreCriterio + "' se guardó correctamente", Alert.AlertType.INFORMATION);

                    // Recargar la tabla manteniendo el filtro
                    /*Materia materiaFiltro = cmbFiltroMateriaAgregados.getValue();
                    if (materiaFiltro != null) {
                        filtrarAgregadosPorMateria(tblAgregados, materiaFiltro.getId());
                    } else {
                        cargarAgregados(tblAgregados);
                    }*/
                    Criterio criterioSeleccionado = cmbFiltroCriterioAgregados.getValue();
                    filtrarAgregadosPorCriterio(tblAgregados, criterioSeleccionado.getId());
                    lblEstadisticas.setText("Total de agregados: " + tblAgregados.getItems().size());

                } catch (Exception e) {
                    LOG.error("Error al guardar orden de agregados", e);
                    mostrarAlerta("Error", "Error al guardar el orden: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });


            // Eventos
            btnGuardar.setOnAction(event -> {
                try {
                    if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
                        mostrarAlerta("Validación", "El nombre es requerido", Alert.AlertType.WARNING);
                        return;
                    }
                    if (cmbCriterio.getValue() == null) {
                        mostrarAlerta("Validación", "Debe seleccionar un criterio de evaluación", Alert.AlertType.WARNING);
                        return;
                    }

                    Agregado.AgregadoBuilder builder = Agregado.builder()
                            .nombre(txtNombre.getText().trim())
                            .criterioId(cmbCriterio.getValue().getId());

                    if ("Actualizar".equals(btnGuardar.getText()) && btnGuardar.getUserData() != null) {
                        // Actualizar
                        builder.id((Long) btnGuardar.getUserData());
                        Agregado agregado = builder.build();
                        agregadoService.actualizarAgregado(agregado);
                        mostrarAlerta("Éxito", "Agregado actualizado correctamente", Alert.AlertType.INFORMATION);
                    } else {
                        // Crear
                        Agregado agregado = builder.build();
                        agregadoService.crearAgregado(agregado);
                        mostrarAlerta("Éxito", "Agregado creado correctamente", Alert.AlertType.INFORMATION);
                    }

                    // Limpiar formulario
                    txtNombre.clear();
                    cmbGrupo.setValue(null);
                    cmbMateria.setValue(null);
                    cmbMateria.setDisable(true);
                    cmbMateria.setPromptText("Seleccione primero un grupo");
                    cmbParcial.setValue(null);
                    cmbParcial.setDisable(true);
                    cmbParcial.setPromptText("Seleccione primero una materia");
                    cmbCriterio.setValue(null);
                    cmbCriterio.setDisable(true);
                    cmbCriterio.setPromptText("Seleccione primero un parcial");
                    btnGuardar.setText("Guardar");
                    btnGuardar.setUserData(null);

                    cargarAgregados(tblAgregados);
                    lblEstadisticas.setText("Total de agregados: " + tblAgregados.getItems().size());

                } catch (IllegalArgumentException e) {
                    mostrarAlerta("Validación", e.getMessage(), Alert.AlertType.WARNING);
                } catch (Exception e) {
                    LOG.error("Error al guardar agregado", e);
                    mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            btnLimpiar.setOnAction(event -> {
                txtNombre.clear();
                cmbGrupo.setValue(null);
                cmbMateria.setValue(null);
                cmbMateria.setDisable(true);
                cmbMateria.setPromptText("Seleccione primero un grupo");
                cmbParcial.setValue(null);
                cmbParcial.setDisable(true);
                cmbParcial.setPromptText("Seleccione primero una materia");
                cmbCriterio.setValue(null);
                cmbCriterio.setDisable(true);
                cmbCriterio.setPromptText("Seleccione primero un parcial");
                btnGuardar.setText("Guardar");
                btnGuardar.setUserData(null);
            });

            vista.getChildren().addAll(formPanel, tablePanel);

        } catch (Exception e) {
            LOG.error("Error al crear vista de agregados", e);
        }

        return vista;
    }

    // Método para crear la vista completa de Concentrado de calificaciones
    private VBox crearVistaConcentradoCompleta() {
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

            filtrosBox.getChildren().addAll(grupoContainer, materiaContainer, parcialContainer);

            // Botones
            javafx.scene.layout.HBox botonesBox = new javafx.scene.layout.HBox(10);
            Button btnGenerar = new Button("Generar Tabla");
            btnGenerar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");

            Button btnGuardar = new Button("Guardar Calificaciones");
            btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            btnGuardar.setDisable(true);

            botonesBox.getChildren().addAll(btnGenerar, btnGuardar);

            filtrosPanel.getChildren().addAll(lblFiltros, filtrosBox, botonesBox);

            // Panel de tabla
            VBox tablaPanel = new VBox(15);
            tablaPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPrefHeight(500);
            scrollPane.setStyle("-fx-background-color: transparent;");

            TableView<java.util.Map<String, Object>> tblCalificaciones = new TableView<>();
            tblCalificaciones.setEditable(true);
            tblCalificaciones.setPlaceholder(new Label("Seleccione Grupo, Materia y Parcial, luego presione 'Generar Tabla'"));
            tblCalificaciones.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            scrollPane.setContent(tblCalificaciones);
            tablaPanel.getChildren().add(scrollPane);

            // Lógica para cargar materias cuando se selecciona un grupo
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

            // Evento del botón generar
            btnGenerar.setOnAction(event -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAlerta("Validación", "Debe seleccionar Grupo, Materia y Parcial", Alert.AlertType.WARNING);
                    return;
                }

                generarTablaCalificaciones(tblCalificaciones, cmbGrupo.getValue(), cmbMateria.getValue(), cmbParcial.getValue());
                btnGuardar.setDisable(false);
            });

            // Evento del botón guardar
            btnGuardar.setOnAction(event -> {
                guardarCalificaciones(tblCalificaciones);
                mostrarAlerta("Éxito", "Calificaciones guardadas correctamente", Alert.AlertType.INFORMATION);
            });

            vista.getChildren().addAll(lblTitulo, filtrosPanel, tablaPanel);

        } catch (Exception e) {
            LOG.error("Error al crear vista de concentrado de calificaciones", e);
        }

        return vista;
    }

    // Método para generar la tabla de calificaciones
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
                mostrarAlerta("Información", "No hay alumnos en este grupo", Alert.AlertType.INFORMATION);
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
                mostrarAlerta("Información", "No hay criterios para esta materia y parcial", Alert.AlertType.INFORMATION);
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

            // Lista para recopilar información de todos los agregados de todos los criterios
            List<java.util.Map<String, Object>> criteriosInfo = new java.util.ArrayList<>();

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
                        .collect(java.util.stream.Collectors.toList());

                if (!agregados.isEmpty()) {
                    // Crear columna padre para el criterio
                    TableColumn<java.util.Map<String, Object>, String> colCriterio = new TableColumn<>(
                            criterio.getNombre() + " (" + criterio.getPuntuacionMaxima() + " pts)"
                    );
                    colCriterio.setResizable(false);

                    // Obtener IDs de todos los agregados del criterio para validación
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
                            // Columna con TextField para tipo Puntuacion (máximo 2 dígitos)
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

                                    // Validar que solo sean números de máximo 2 dígitos
                                    textField.textProperty().addListener((obs, oldVal, newVal) -> {
                                        if (newVal != null && !newVal.isEmpty()) {
                                            // Solo permitir números y punto decimal
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
                                                // Ignorar si no es un número válido aún
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
                                                        // Ignorar valores no numéricos
                                                    }
                                                }
                                            }

                                            // Validar que la suma no exceda el máximo
                                            if (sumaTotal > puntuacionMaximaCriterio) {
                                                mostrarAlerta("Advertencia",
                                                    "No puede exceder el máximo de puntos",
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
                                                        // Ignorar valores no numéricos
                                                    }
                                                }
                                            }

                                            // Validar que la suma no exceda el máximo
                                            if (sumaTotal > puntuacionMaximaCriterio) {
                                                mostrarAlerta("Advertencia",
                                                    "No puede exceder el máximo de puntos",
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
                                // Para tipo Puntuación, sumar los valores numéricos
                                if (valor instanceof Number) {
                                    puntosObtenidos += ((Number) valor).doubleValue();
                                } else if (valor instanceof String && !((String) valor).isEmpty()) {
                                    try {
                                        puntosObtenidos += Double.parseDouble((String) valor);
                                    } catch (NumberFormatException e) {
                                        // Ignorar valores no numéricos
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

                    tabla.getColumns().add(colCriterio);

                    // Guardar información del criterio para la columna Portafolio
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

            // Llenar datos
            ObservableList<java.util.Map<String, Object>> datos = FXCollections.observableArrayList();
            int numero = 1;
            for (Alumno alumno : alumnos) {
                java.util.Map<String, Object> fila = new java.util.HashMap<>();
                fila.put("numero", numero++);
                fila.put("alumnoId", alumno.getId());
                fila.put("nombreCompleto", alumno.getApellidoPaterno() + " " +
                        alumno.getApellidoMaterno() + " " + alumno.getNombre());

                // Cargar calificaciones existentes
                for (Criterio criterio : criterios) {
                    List<Agregado> agregados = agregadoService.obtenerAgregadosPorCriterio(criterio.getId());
                    for (Agregado agregado : agregados) {
                        Optional<Calificacion> calificacion = calificacionService
                                .obtenerCalificacionPorAlumnoYAgregado(alumno.getId(), agregado.getId());
                        fila.put("agregado_" + agregado.getId(),
                                calificacion.map(c -> String.valueOf(c.getPuntuacion())).orElse(""));
                    }
                }

                datos.add(fila);
            }

            tabla.setItems(datos);

        } catch (Exception e) {
            LOG.error("Error al generar tabla de calificaciones", e);
            mostrarAlerta("Error", "Error al generar la tabla: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Método para guardar las calificaciones
    private void guardarCalificaciones(TableView<java.util.Map<String, Object>> tabla) {
        try {
            for (java.util.Map<String, Object> fila : tabla.getItems()) {
                Long alumnoId = (Long) fila.get("alumnoId");

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
                                        // Intentar convertir a número
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
                                    Calificacion calificacion = Calificacion.builder()
                                            .alumnoId(alumnoId)
                                            .agregadoId(agregadoId)
                                            .puntuacion(puntuacion)
                                            .build();

                                    calificacionService.crearCalificacion(calificacion);
                                }
                            } catch (NumberFormatException e) {
                                LOG.warn("Valor inválido para calificación: " + valor);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error al guardar calificaciones", e);
            mostrarAlerta("Error", "Error al guardar las calificaciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Método para cargar agregados en la tabla
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


    // Método para filtrar criterios por materia
    private void filtrarCriteriosPorMateria(TableView<Criterio> tabla, Long materiaId) {
        try {
            List<Criterio> criteriosFiltrados = criterioService.obtenerCriteriosPorMateria(materiaId);

            // Ordenar por orden
            criteriosFiltrados.sort((c1, c2) -> {
                if (c1.getOrden() == null && c2.getOrden() == null) return 0;
                if (c1.getOrden() == null) return 1;
                if (c2.getOrden() == null) return -1;
                return Integer.compare(c1.getOrden(), c2.getOrden());
            });

            ObservableList<Criterio> criteriosList = FXCollections.observableArrayList(criteriosFiltrados);
            tabla.setItems(criteriosList);

            // Forzar el refresh de la tabla para que las columnas de botones se actualicen
            tabla.refresh();

            LOG.info("Criterios filtrados por materia {}: {} criterios", materiaId, criteriosList.size());
        } catch (Exception e) {
            LOG.error("Error al filtrar criterios por materia", e);
            tabla.setItems(FXCollections.observableArrayList());
        }
    }

    // Método para aplicar filtros combinados de materia y parcial
    private void aplicarFiltrosCriterios(TableView<Criterio> tabla, ComboBox<Materia> cmbMateria, ComboBox<Integer> cmbParcial) {
        try {
            List<Criterio> criteriosFiltrados = criterioService.obtenerTodosLosCriterios();

            // Filtrar por materia si está seleccionada
            Materia materiaSeleccionada = cmbMateria.getValue();
            if (materiaSeleccionada != null) {
                criteriosFiltrados = criteriosFiltrados.stream()
                        .filter(c -> materiaSeleccionada.getId().equals(c.getMateriaId()))
                        .collect(java.util.stream.Collectors.toList());
            }

            // Filtrar por parcial si está seleccionado
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

    // Método para filtrar agregados por materia
    private void filtrarAgregadosPorMateria(TableView<Agregado> tabla, Long materiaId) {
        try {
            List<Agregado> todosLosAgregados = agregadoService.obtenerTodosLosAgregados();
            List<Agregado> agregadosFiltrados = new java.util.ArrayList<>();

            for (Agregado agregado : todosLosAgregados) {
                if (agregado.getCriterioId() != null) {
                    Criterio criterio = criterioService.obtenerCriterioPorId(agregado.getCriterioId()).orElse(null);
                    if (criterio != null && materiaId.equals(criterio.getMateriaId())) {
                        agregadosFiltrados.add(agregado);
                    }
                }
            }

            // Ordenar primero por criterioId y luego por orden dentro de cada criterio
            agregadosFiltrados.sort((a1, a2) -> {
                // Primero ordenar por criterioId
                int criterioCompare = Long.compare(a1.getCriterioId(), a2.getCriterioId());
                if (criterioCompare != 0) return criterioCompare;

                // Dentro del mismo criterio, ordenar por orden
                if (a1.getOrden() == null && a2.getOrden() == null) return 0;
                if (a1.getOrden() == null) return 1;
                if (a2.getOrden() == null) return -1;
                return Integer.compare(a1.getOrden(), a2.getOrden());
            });

            ObservableList<Agregado> agregadosList = FXCollections.observableArrayList(agregadosFiltrados);
            tabla.setItems(agregadosList);

            // Forzar el refresh de la tabla para que las columnas de botones se actualicen
            tabla.refresh();

            LOG.info("Agregados filtrados por materia {}: {} agregados", materiaId, agregadosList.size());
        } catch (Exception e) {
            LOG.error("Error al filtrar agregados por materia", e);
            tabla.setItems(FXCollections.observableArrayList());
        }
    }

    // Método para filtrar agregados por criterio
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

    // Método para crear la vista completa de Exámenes
    private VBox crearVistaExamenesCompleta() {
        VBox vista = new VBox(20);
        vista.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        try {
            // Header
            Label lblTitulo = new Label("Exámenes");
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

            // Botón Buscar en la misma fila
            VBox buscarContainer = new VBox(5);
            Label lblEspacio = new Label(" "); // Espaciador para alinear con los otros labels
            lblEspacio.setStyle("-fx-font-size: 14px;");
            Button btnBuscar = new Button("Buscar");
            btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            btnBuscar.setOnMouseEntered(e -> btnBuscar.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;"));
            btnBuscar.setOnMouseExited(e -> btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;"));
            buscarContainer.getChildren().addAll(lblEspacio, btnBuscar);

            filtrosBox.getChildren().addAll(grupoContainer, materiaContainer, parcialContainer, buscarContainer);

            filtrosPanel.getChildren().addAll(lblFiltros, filtrosBox);

            // Panel de tabla
            VBox tablaPanel = new VBox(15);
            tablaPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            // Campo para Total de Aciertos del Examen
            javafx.scene.layout.HBox totalAciertosBox = new javafx.scene.layout.HBox(10);
            totalAciertosBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label lblTotalAciertos = new Label("Total de aciertos de examen:");
            lblTotalAciertos.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");

            TextField txtTotalAciertos = new TextField();
            txtTotalAciertos.setPromptText("00");
            txtTotalAciertos.setPrefWidth(60);
            txtTotalAciertos.setStyle("-fx-alignment: CENTER;");

            // Limitar a máximo 2 dígitos
            txtTotalAciertos.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.matches("\\d{0,2}")) {
                    txtTotalAciertos.setText(oldVal);
                } else {
                    // Refrescar la tabla cuando cambia el total de aciertos
                    if (tblAlumnos != null && tblAlumnos.getItems() != null) {
                        tblAlumnos.refresh();
                    }
                }
            });

            totalAciertosBox.getChildren().addAll(lblTotalAciertos, txtTotalAciertos);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPrefHeight(500);
            scrollPane.setStyle("-fx-background-color: transparent;");

            TableView<Alumno> tblAlumnos = new TableView<>();
            tblAlumnos.setPlaceholder(new Label("Seleccione Grupo, Materia y Parcial, luego presione 'Generar Tabla'"));
            tblAlumnos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Columna Número de Lista
            TableColumn<Alumno, Integer> colNumeroLista = new TableColumn<>("N° Lista");
            colNumeroLista.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("numeroLista"));
            colNumeroLista.setPrefWidth(80);
            colNumeroLista.setStyle("-fx-alignment: CENTER;");

            // Columna Nombre Completo
            TableColumn<Alumno, String> colNombreCompleto = new TableColumn<>("Nombre Completo");
            colNombreCompleto.setCellValueFactory(cellData -> {
                Alumno alumno = cellData.getValue();
                String nombreCompleto = alumno.getNombre() + " " +
                                       alumno.getApellidoPaterno() + " " +
                                       alumno.getApellidoMaterno();
                return new javafx.beans.property.SimpleStringProperty(nombreCompleto);
            });
            colNombreCompleto.setPrefWidth(300);

            // Columna Aciertos
            TableColumn<Alumno, String> colAciertos = new TableColumn<>("Aciertos");
            colAciertos.setPrefWidth(100);
            colAciertos.setStyle("-fx-alignment: CENTER;");

            // HashMap para almacenar los valores de aciertos por alumno
            java.util.Map<Long, String> aciertosPorAlumno = new java.util.HashMap<>();

            colAciertos.setCellValueFactory(cellData -> {
                Alumno alumno = cellData.getValue();
                String valor = aciertosPorAlumno.getOrDefault(alumno.getId(), "0");
                return new javafx.beans.property.SimpleStringProperty(valor);
            });
            colAciertos.setEditable(true);
            colAciertos.setCellFactory(col -> new TableCell<Alumno, String>() {
                private final TextField textField = new TextField();

                {
                    textField.setStyle("-fx-alignment: CENTER;");
                    textField.setPromptText("0-99");

                    // Limitar a máximo 2 dígitos
                    textField.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal != null && !newVal.matches("\\d{0,2}")) {
                            textField.setText(oldVal);
                        }
                    });

                    // Guardar el valor en el HashMap cuando cambia
                    textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal && getTableRow() != null && getTableRow().getItem() != null) {
                            Alumno alumno = getTableRow().getItem();
                            String valor = textField.getText();
                            if (valor == null || valor.trim().isEmpty()) {
                                valor = "0";
                            }
                            aciertosPorAlumno.put(alumno.getId(), valor);
                            // Refrescar la tabla para actualizar el porcentaje
                            tblAlumnos.refresh();
                        }
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                    } else {
                        textField.setText(item != null ? item : "0");
                        setGraphic(textField);
                    }
                }
            });

            // Columna Porcentaje Examen
            TableColumn<Alumno, String> colPorcentaje = new TableColumn<>("Porcentaje examen");
            colPorcentaje.setPrefWidth(120);
            colPorcentaje.setStyle("-fx-alignment: CENTER;");

            colPorcentaje.setCellValueFactory(cellData -> {
                Alumno alumno = cellData.getValue();
                String aciertoStr = aciertosPorAlumno.getOrDefault(alumno.getId(), "0");
                String totalAciertosStr = txtTotalAciertos.getText();

                try {
                    int aciertos = Integer.parseInt(aciertoStr);
                    int totalAciertos = totalAciertosStr != null && !totalAciertosStr.isEmpty()
                        ? Integer.parseInt(totalAciertosStr)
                        : 0;

                    if (totalAciertos > 0) {
                        double porcentaje = (aciertos * 100.0) / totalAciertos;
                        return new javafx.beans.property.SimpleStringProperty(
                            String.format("%.2f", porcentaje)
                        );
                    } else {
                        return new javafx.beans.property.SimpleStringProperty("N/A");
                    }
                } catch (NumberFormatException e) {
                    return new javafx.beans.property.SimpleStringProperty("0.00");
                }
            });

            tblAlumnos.setEditable(true);
            tblAlumnos.getColumns().addAll(colNumeroLista, colNombreCompleto, colAciertos, colPorcentaje);

            scrollPane.setContent(tblAlumnos);

            // Botón para guardar
            Button btnGuardarExamenes = new Button("Guardar Exámenes");
            btnGuardarExamenes.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;");
            btnGuardarExamenes.setOnMouseEntered(e -> btnGuardarExamenes.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;"));
            btnGuardarExamenes.setOnMouseExited(e -> btnGuardarExamenes.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-background-radius: 5;"));
            btnGuardarExamenes.setOnAction(e -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAlerta("Validación", "Debe generar la tabla primero", Alert.AlertType.WARNING);
                    return;
                }

                // Validar que se haya ingresado el total de aciertos
                String totalAciertosStr = txtTotalAciertos.getText();
                if (totalAciertosStr == null || totalAciertosStr.trim().isEmpty()) {
                    mostrarAlerta("Validación", "Debe ingresar el total de aciertos del examen", Alert.AlertType.WARNING);
                    return;
                }

                try {
                    int totalAciertosExamen = Integer.parseInt(totalAciertosStr);
                    Grupo grupo = cmbGrupo.getValue();
                    Materia materia = cmbMateria.getValue();
                    Integer parcial = cmbParcial.getValue();

                    // Buscar o crear el examen (sin aciertos individuales)
                    Optional<Examen> examenExistente = examenService.obtenerExamenPorGrupoMateriaParcial(
                        grupo.getId(), materia.getId(), parcial
                    );

                    Examen examen;
                    if (examenExistente.isPresent()) {
                        // Actualizar el examen existente
                        examen = examenExistente.get();
                        examen.setTotalAciertos(totalAciertosExamen);
                        examen = examenService.actualizarExamen(examen);
                    } else {
                        // Crear nuevo examen
                        examen = Examen.builder()
                            .grupoId(grupo.getId())
                            .materiaId(materia.getId())
                            .parcial(parcial)
                            .totalAciertos(totalAciertosExamen)
                            .build();
                        examen = examenService.crearExamen(examen);
                    }

                    // Ahora guardar los aciertos de cada alumno en AlumnoExamen
                    int guardados = 0;
                    int actualizados = 0;

                    for (Alumno alumno : tblAlumnos.getItems()) {
                        String aciertoStr = aciertosPorAlumno.getOrDefault(alumno.getId(), "0");
                        int aciertos = Integer.parseInt(aciertoStr);

                        // Buscar si ya existe un AlumnoExamen
                        Optional<AlumnoExamen> alumnoExamenExistente = alumnoExamenService.obtenerAlumnoExamenPorAlumnoYExamen(
                            alumno.getId(), examen.getId()
                        );

                        if (alumnoExamenExistente.isPresent()) {
                            // Actualizar
                            AlumnoExamen alumnoExamen = alumnoExamenExistente.get();
                            alumnoExamen.setAciertos(aciertos);
                            alumnoExamenService.actualizarAlumnoExamen(alumnoExamen);
                            actualizados++;
                        } else {
                            // Crear nuevo
                            AlumnoExamen alumnoExamen = AlumnoExamen.builder()
                                .alumnoId(alumno.getId())
                                .examenId(examen.getId())
                                .aciertos(aciertos)
                                .build();
                            alumnoExamenService.crearAlumnoExamen(alumnoExamen);
                            guardados++;
                        }
                    }

                    mostrarAlerta("Éxito",
                        "Exámenes guardados correctamente\n" +
                        "Nuevos: " + guardados + "\n" +
                        "Actualizados: " + actualizados,
                        Alert.AlertType.INFORMATION);

                    LOG.info("Exámenes guardados - Grupo: {}, Materia: {}, Parcial: {}, Nuevos: {}, Actualizados: {}",
                            grupo.getId(), materia.getNombre(), parcial, guardados, actualizados);

                } catch (Exception ex) {
                    LOG.error("Error al guardar exámenes", ex);
                    mostrarAlerta("Error", "No se pudieron guardar los exámenes: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });

            tablaPanel.getChildren().addAll(totalAciertosBox, scrollPane, btnGuardarExamenes);

            // Lógica para cargar materias cuando se selecciona un grupo
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
                        cmbMateria.setItems(FXCollections.observableArrayList());
                        cmbMateria.setDisable(true);
                    }
                } else {
                    cmbMateria.setItems(FXCollections.observableArrayList());
                    cmbMateria.setDisable(true);
                }
            });

            // Evento del botón buscar
            btnBuscar.setOnAction(event -> {
                if (cmbGrupo.getValue() == null || cmbMateria.getValue() == null || cmbParcial.getValue() == null) {
                    mostrarAlerta("Validación", "Debe seleccionar Grupo, Materia y Parcial", Alert.AlertType.WARNING);
                    return;
                }

                try {
                    // Obtener los alumnos del grupo seleccionado
                    Grupo grupoSeleccionado = cmbGrupo.getValue();
                    Materia materiaSeleccionada = cmbMateria.getValue();
                    Integer parcialSeleccionado = cmbParcial.getValue();

                    List<Alumno> alumnos = alumnoService.obtenerTodosLosAlumnos().stream()
                        .filter(alumno -> alumno.getGrupoId() != null && alumno.getGrupoId().equals(grupoSeleccionado.getId()))
                        .sorted((a1, a2) -> {
                            // Ordenar por número de lista
                            if (a1.getNumeroLista() == null && a2.getNumeroLista() == null) return 0;
                            if (a1.getNumeroLista() == null) return 1;
                            if (a2.getNumeroLista() == null) return -1;
                            return Integer.compare(a1.getNumeroLista(), a2.getNumeroLista());
                        })
                        .collect(java.util.stream.Collectors.toList());

                    // Limpiar el HashMap
                    aciertosPorAlumno.clear();

                    // Buscar si existe un examen para este grupo/materia/parcial
                    Optional<Examen> examenOpt = examenService.obtenerExamenPorGrupoMateriaParcial(
                        grupoSeleccionado.getId(), materiaSeleccionada.getId(), parcialSeleccionado
                    );

                    // Establecer el totalAciertos en el campo de texto
                    if (examenOpt.isPresent()) {
                        Examen examen = examenOpt.get();
                        if (examen.getTotalAciertos() != null) {
                            txtTotalAciertos.setText(String.valueOf(examen.getTotalAciertos()));
                        } else {
                            txtTotalAciertos.setText("");
                        }

                        // Cargar los aciertos de cada alumno desde AlumnoExamen
                        List<AlumnoExamen> alumnoExamenes = alumnoExamenService.obtenerAlumnoExamenPorExamen(examen.getId());
                        for (AlumnoExamen ae : alumnoExamenes) {
                            aciertosPorAlumno.put(ae.getAlumnoId(), String.valueOf(ae.getAciertos()));
                        }
                    } else {
                        txtTotalAciertos.setText("");
                    }

                    // Para los alumnos sin valores guardados, establecer "0"
                    for (Alumno alumno : alumnos) {
                        if (!aciertosPorAlumno.containsKey(alumno.getId())) {
                            aciertosPorAlumno.put(alumno.getId(), "0");
                        }
                    }

                    ObservableList<Alumno> alumnosList = FXCollections.observableArrayList(alumnos);
                    tblAlumnos.setItems(alumnosList);

                    // Forzar refresh de la tabla para que muestre los valores
                    tblAlumnos.refresh();

                    LOG.info("Tabla de exámenes generada - Grupo: {}, Materia: {}, Parcial: {}, Alumnos: {}",
                            grupoSeleccionado.getId(), materiaSeleccionada.getNombre(), parcialSeleccionado, alumnos.size());
                } catch (Exception e) {
                    LOG.error("Error al generar tabla de exámenes", e);
                    mostrarAlerta("Error", "No se pudo generar la tabla: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            vista.getChildren().addAll(lblTitulo, filtrosPanel, tablaPanel);

        } catch (Exception e) {
            LOG.error("Error al crear vista de exámenes", e);
        }

        return vista;
    }
}
