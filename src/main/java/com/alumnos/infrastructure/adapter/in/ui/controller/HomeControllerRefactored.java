package com.alumnos.infrastructure.adapter.in.ui.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * Controlador principal refactorizado - Solo coordina navegación entre vistas
 * Ya NO contiene lógica de negocio, formularios, tablas, etc.
 * Delega toda la responsabilidad a controladores especializados
 */
@Controller
public class HomeControllerRefactored {

    private static final Logger LOG = LoggerFactory.getLogger(HomeControllerRefactored.class);

    @FXML private VBox drawerMenu;
    @FXML private BorderPane mainContent;
    @FXML private StackPane contentContainer;
    @FXML private Region overlay;
    @FXML private Button btnMenu;
    @FXML private Label lblTitulo;
    @FXML private VBox submenuCriterios;
    @FXML private VBox submenuConcentrado;

    // Botones del menú
    @FXML private Button btnMinimizar;
    @FXML private Button btnCerrar;
    @FXML private Button btnMenuEstudiantes;
    @FXML private Button btnMenuGrupos;
    @FXML private Button btnMenuMaterias;
    @FXML private Button btnMenuAsignaciones;
    @FXML private Button btnMenuCriterios;
    @FXML private Button btnMenuCriteriosLista;
    @FXML private Button btnMenuAgregados;
    @FXML private Button btnMenuConcentrado;
    @FXML private Button btnMenuConcentradoCalificaciones;
    @FXML private Button btnMenuInformeConcentrado;
    @FXML private Button btnMenuExamenes;
    @FXML private Button btnMenuConfiguracion;

    // Controladores especializados (inyectados)
    private final EstudiantesController estudiantesController;
    private final GruposController gruposController;
    private final MateriasController materiasController;
    private final AsignacionesController asignacionesController;
    private final CriteriosController criteriosController;
    private final AgregadosController agregadosController;
    private final ConcentradoController concentradoController;
    private final InformeConcentradoController informeConcentradoController;
    private final ExamenesController examenesController;
    private final ConfiguracionController configuracionController;

    // Vistas cacheadas
    private VBox vistaEstudiantes;
    private VBox vistaGrupos;
    private VBox vistaMaterias;
    private VBox vistaAsignaciones;
    private VBox vistaCriterios;
    private VBox vistaAgregados;
    private VBox vistaConcentrado;
    private VBox vistaInformeConcentrado;
    private VBox vistaExamenes;
    private VBox vistaConfiguracion;

    private boolean menuAbierto = false;

    public HomeControllerRefactored(
            EstudiantesController estudiantesController,
            GruposController gruposController,
            MateriasController materiasController,
            AsignacionesController asignacionesController,
            CriteriosController criteriosController,
            AgregadosController agregadosController,
            ConcentradoController concentradoController,
            InformeConcentradoController informeConcentradoController,
            ExamenesController examenesController,
            ConfiguracionController configuracionController) {
        this.estudiantesController = estudiantesController;
        this.gruposController = gruposController;
        this.materiasController = materiasController;
        this.asignacionesController = asignacionesController;
        this.criteriosController = criteriosController;
        this.agregadosController = agregadosController;
        this.concentradoController = concentradoController;
        this.informeConcentradoController = informeConcentradoController;
        this.examenesController = examenesController;
        this.configuracionController = configuracionController;
    }

    @FXML
    public void initialize() {
        if (vistaEstudiantes != null) {
            LOG.warn("Ya inicializado, saltando");
            return;
        }
        cargarVistas();
        mostrarVista("estudiantes");
    }

    /**
     * Carga TODAS las vistas delegando a los controladores especializados
     */
    private void cargarVistas() {
        try {
            contentContainer.getChildren().clear();

            // Pasar el mainContent a ConcentradoController para los diálogos de guardar archivo
            concentradoController.setMainContent(mainContent);

            // Cada controlador crea su propia vista
            vistaEstudiantes = estudiantesController.crearVista();
            vistaGrupos = gruposController.crearVista();
            vistaMaterias = materiasController.crearVista();
            vistaAsignaciones = asignacionesController.crearVista();
            vistaCriterios = criteriosController.crearVista();
            vistaAgregados = agregadosController.crearVista();
            vistaConcentrado = concentradoController.crearVistaConcentrado();
            vistaInformeConcentrado = informeConcentradoController.crearVista();
            vistaExamenes = examenesController.crearVista();
            vistaConfiguracion = configuracionController.crearVista();

            // Ocultar todas inicialmente
            vistaEstudiantes.setVisible(false);
            vistaGrupos.setVisible(false);
            vistaMaterias.setVisible(false);
            vistaAsignaciones.setVisible(false);
            vistaCriterios.setVisible(false);
            vistaAgregados.setVisible(false);
            vistaConcentrado.setVisible(false);
            vistaInformeConcentrado.setVisible(false);
            vistaExamenes.setVisible(false);
            vistaConfiguracion.setVisible(false);

            // Agregar al contenedor
            contentContainer.getChildren().addAll(
                vistaEstudiantes, vistaGrupos, vistaMaterias,
                vistaAsignaciones, vistaCriterios, vistaAgregados,
                vistaConcentrado, vistaInformeConcentrado, vistaExamenes,
                vistaConfiguracion
            );

            LOG.info("Vistas cargadas exitosamente");
        } catch (Exception e) {
            LOG.error("Error al cargar vistas", e);
            mostrarError("Error al cargar las vistas: " + e.getMessage());
        }
    }

    /**
     * Muestra solo la vista especificada, ocultando las demás
     */
    private void mostrarVista(String nombreVista) {
        try {
            // Ocultar todas
            vistaEstudiantes.setVisible(false);
            vistaGrupos.setVisible(false);
            vistaMaterias.setVisible(false);
            vistaAsignaciones.setVisible(false);
            vistaCriterios.setVisible(false);
            vistaAgregados.setVisible(false);
            vistaConcentrado.setVisible(false);
            vistaInformeConcentrado.setVisible(false);
            vistaExamenes.setVisible(false);
            vistaConfiguracion.setVisible(false);

            // Mostrar la solicitada
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
                case "informeconcentrado":
                    vistaInformeConcentrado.setVisible(true);
                    vistaInformeConcentrado.toFront();
                    break;
                case "examenes":
                    vistaExamenes.setVisible(true);
                    vistaExamenes.toFront();
                    break;
                case "configuracion":
                    vistaConfiguracion.setVisible(true);
                    vistaConfiguracion.toFront();
                    break;
                default:
                    LOG.warn("Vista no reconocida: {}", nombreVista);
                    vistaEstudiantes.setVisible(true);
                    vistaEstudiantes.toFront();
            }
        } catch (Exception e) {
            LOG.error("Error al mostrar vista: " + nombreVista, e);
        }
    }

    // ==================== MÉTODOS DE NAVEGACIÓN ====================

    @FXML
    private void toggleMenu() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), drawerMenu);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), overlay);

        if (menuAbierto) {
            transition.setToX(-280);
            fadeTransition.setToValue(0);
            fadeTransition.setOnFinished(e -> overlay.setVisible(false));
            menuAbierto = false;
        } else {
            overlay.setVisible(true);
            transition.setToX(0);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            menuAbierto = true;
        }

        transition.play();
        fadeTransition.play();
    }

    @FXML
    private void closeMenu() {
        if (menuAbierto) toggleMenu();
    }

    @FXML
    private void minimizeWindow() {
        ((javafx.stage.Stage) lblTitulo.getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void closeWindow() {
        ((javafx.stage.Stage) lblTitulo.getScene().getWindow()).close();
        javafx.application.Platform.exit();
    }

    @FXML
    private void handleMenuEstudiantes() {
        lblTitulo.setText("Alumnos - Sistema de Gestión");
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
    private void handleMenuInformeConcentrado() {
        lblTitulo.setText("Informe de concentrado - Sistema de Gestión");
        mostrarVista("informeconcentrado");
        toggleMenu();
    }

    @FXML
    private void handleMenuExamenes() {
        lblTitulo.setText("Exámenes - Sistema de Gestión");
        mostrarVista("examenes");
        toggleMenu();
    }

    @FXML
    private void handleMenuConfiguracion() {
        lblTitulo.setText("Configuración - Sistema de Gestión");
        mostrarVista("configuracion");
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

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
