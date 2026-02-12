package com.alumnos.infrastructure.adapter.in.ui.controller;

import com.alumnos.domain.model.Alumno;
import com.alumnos.domain.model.Grupo;
import com.alumnos.domain.port.in.AlumnoServicePort;
import com.alumnos.domain.port.in.GrupoServicePort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // 🔍 Filtro de grupo
    private ComboBox<Grupo> cmbFiltroGrupo;
    private List<Alumno> todosLosAlumnos = new ArrayList<>(); // Cache de todos los alumnos

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

        Button btnImportar = new Button("Importar");
        btnImportar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;");
        btnImportar.setOnAction(e -> mostrarDialogoImportacion());

        buttonBox.getChildren().addAll(btnGuardar, btnLimpiar, btnImportar);

        formulario.getChildren().addAll(lblFormTitle, new javafx.scene.control.Separator(), gridForm, buttonBox);
        return formulario;
    }

    private VBox crearTabla() {
        VBox contenedor = new VBox(10);
        contenedor.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Filtro de grupo
        javafx.scene.layout.HBox headerBox = new javafx.scene.layout.HBox(15);
        headerBox.setStyle("-fx-alignment: center-left; -fx-padding: 0 0 10 0;");

        Label lblFiltro = new Label("Filtrar por grupo:");
        lblFiltro.setStyle("-fx-font-weight: bold;");

        cmbFiltroGrupo = new ComboBox<>();
        cmbFiltroGrupo.setPromptText("Seleccione un grupo");
        cmbFiltroGrupo.setPrefWidth(200);

        // Cargar grupos en el filtro
        cargarGruposEnFiltro();

        // Listener para filtrar cuando cambie el valor
        cmbFiltroGrupo.setOnAction(e -> aplicarFiltroGrupo());

        headerBox.getChildren().addAll(lblFiltro, cmbFiltroGrupo);

        tablaAlumnos = new TableView<>(); // 📋 Guardar referencia

        // 📝 Columna Número de Lista (NO EDITABLE) - Primera columna
        TableColumn<Alumno, Integer> colNumeroLista = new TableColumn<>("N° Lista");
        colNumeroLista.setCellValueFactory(data ->
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getNumeroLista()));
        colNumeroLista.setPrefWidth(80);
        colNumeroLista.setStyle("-fx-alignment: CENTER;");
        colNumeroLista.setEditable(false);
        colNumeroLista.setSortable(false);

        TableColumn<Alumno, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        
        TableColumn<Alumno, String> colApellidoP = new TableColumn<>("Apellido Paterno");
        colApellidoP.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getApellidoPaterno()));

        TableColumn<Alumno, String> colApellidoM = new TableColumn<>("Apellido Materno");
        colApellidoM.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getApellidoMaterno()));

        // 🆕 Columna de Grupo
        TableColumn<Alumno, String> colGrupo = new TableColumn<>("Grupo");
        colGrupo.setCellValueFactory(data -> {
            Long grupoId = data.getValue().getGrupoId();
            if (grupoId != null) {
                try {
                    Optional<Grupo> grupo = grupoService.obtenerGrupoPorId(grupoId);
                    return new javafx.beans.property.SimpleStringProperty(
                        grupo.map(Grupo::getNombre).orElse("Sin grupo")
                    );
                } catch (Exception e) {
                    return new javafx.beans.property.SimpleStringProperty("Error");
                }
            }
            return new javafx.beans.property.SimpleStringProperty("Sin grupo");
        });

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

        tablaAlumnos.getColumns().addAll(colNumeroLista, colNombre, colApellidoP, colApellidoM, colGrupo, colAcciones);
        cargarDatos(tablaAlumnos);

        contenedor.getChildren().addAll(headerBox, tablaAlumnos);
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

    /**
     * Método público para recargar la lista de grupos (llamado desde GruposController)
     */
    public void refrescarListaGrupos() {
        if (cmbGrupo != null) {
            cargarGrupos(cmbGrupo);
        }
    }

    private void cargarDatos(TableView<Alumno> tabla) {
        try {
            todosLosAlumnos = alumnoService.obtenerTodosLosAlumnos();

            // Ordenar por número de lista
            List<Alumno> alumnosOrdenados = todosLosAlumnos.stream()
                .sorted((a1, a2) -> {
                    if (a1.getNumeroLista() == null && a2.getNumeroLista() == null) return 0;
                    if (a1.getNumeroLista() == null) return 1;
                    if (a2.getNumeroLista() == null) return -1;
                    return a1.getNumeroLista().compareTo(a2.getNumeroLista());
                })
                .toList();

            tabla.setItems(FXCollections.observableArrayList(alumnosOrdenados));
            tabla.refresh(); // 🔄 Forzar refresco de la tabla para que se rendericen los botones

            // 📏 Ajustar columnas al contenido (incluyendo botones)
            Platform.runLater(() -> ajustarColumnasAlContenido(tabla));

            // Si hay un filtro seleccionado, aplicarlo
            if (cmbFiltroGrupo != null && cmbFiltroGrupo.getValue() != null) {
                aplicarFiltroGrupo();
            }
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

    /**
     * Carga los grupos en el ComboBox de filtro
     */
    private void cargarGruposEnFiltro() {
        try {
            List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();

            cmbFiltroGrupo.setItems(FXCollections.observableArrayList(grupos));

            // Seleccionar el primer elemento por defecto si hay grupos
            if (!grupos.isEmpty()) {
                cmbFiltroGrupo.setValue(grupos.get(0));
                // Aplicar el filtro automáticamente
                aplicarFiltroGrupo();
            }
        } catch (Exception e) {
            manejarExcepcion("cargar grupos en filtro", e);
        }
    }

    /**
     * Aplica el filtro de grupo a la tabla de alumnos
     */
    private void aplicarFiltroGrupo() {
        if (cmbFiltroGrupo == null || tablaAlumnos == null) {
            return;
        }

        Grupo grupoSeleccionado = cmbFiltroGrupo.getValue();

        if (grupoSeleccionado == null) {
            // Si no hay grupo seleccionado, mostrar todos ordenados por número de lista
            List<Alumno> alumnosOrdenados = todosLosAlumnos.stream()
                .sorted((a1, a2) -> {
                    if (a1.getNumeroLista() == null && a2.getNumeroLista() == null) return 0;
                    if (a1.getNumeroLista() == null) return 1;
                    if (a2.getNumeroLista() == null) return -1;
                    return a1.getNumeroLista().compareTo(a2.getNumeroLista());
                })
                .toList();
            tablaAlumnos.setItems(FXCollections.observableArrayList(alumnosOrdenados));
        } else {
            // Filtrar por grupo seleccionado y ordenar por número de lista
            List<Alumno> alumnosFiltrados = todosLosAlumnos.stream()
                .filter(alumno -> grupoSeleccionado.getId().equals(alumno.getGrupoId()))
                .sorted((a1, a2) -> {
                    if (a1.getNumeroLista() == null && a2.getNumeroLista() == null) return 0;
                    if (a1.getNumeroLista() == null) return 1;
                    if (a2.getNumeroLista() == null) return -1;
                    return a1.getNumeroLista().compareTo(a2.getNumeroLista());
                })
                .toList();
            tablaAlumnos.setItems(FXCollections.observableArrayList(alumnosFiltrados));
        }

        tablaAlumnos.refresh();
    }

    /**
     * Muestra el diálogo de importación de alumnos desde Excel
     */
    private void mostrarDialogoImportacion() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Importar Alumnos desde Excel");
        dialog.setHeaderText("Seleccione el archivo Excel y el formato de los nombres");

        // Crear contenido del diálogo
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");

        // Label de instrucciones
        Label lblInstrucciones = new Label("El archivo Excel debe contener los nombres completos de los alumnos en la primera columna.");
        lblInstrucciones.setWrapText(true);
        lblInstrucciones.setStyle("-fx-font-size: 12px;");

        // Selector de archivo
        javafx.scene.layout.HBox fileBox = new javafx.scene.layout.HBox(10);
        TextField txtArchivo = new TextField();
        txtArchivo.setPromptText("Seleccione un archivo Excel...");
        txtArchivo.setEditable(false);
        txtArchivo.setPrefWidth(300);

        Button btnSeleccionar = new Button("Seleccionar");
        btnSeleccionar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        fileBox.getChildren().addAll(txtArchivo, btnSeleccionar);

        // Radio buttons para formato
        Label lblFormato = new Label("Formato de nombres:");
        lblFormato.setStyle("-fx-font-weight: bold;");

        ToggleGroup formatoGroup = new ToggleGroup();

        RadioButton rbNombreApellidos = new RadioButton("Nombre(s) - Apellidos");
        rbNombreApellidos.setToggleGroup(formatoGroup);
        rbNombreApellidos.setSelected(true);

        RadioButton rbApellidosNombre = new RadioButton("Apellidos - Nombre(s)");
        rbApellidosNombre.setToggleGroup(formatoGroup);

        VBox formatoBox = new VBox(8);
        formatoBox.getChildren().addAll(lblFormato, rbNombreApellidos, rbApellidosNombre);

        // Selector de grupo
        Label lblGrupoImport = new Label("Grupo destino:");
        lblGrupoImport.setStyle("-fx-font-weight: bold;");
        ComboBox<Grupo> cmbGrupoImport = new ComboBox<>();
        cmbGrupoImport.setPromptText("Seleccione un grupo");
        cargarGrupos(cmbGrupoImport);

        // Variable para guardar el archivo seleccionado
        final File[] archivoSeleccionado = {null};

        btnSeleccionar.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo Excel");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx", "*.xls")
            );

            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                archivoSeleccionado[0] = file;
                txtArchivo.setText(file.getName());
            }
        });

        content.getChildren().addAll(
            lblInstrucciones,
            fileBox,
            new Separator(),
            formatoBox,
            new Separator(),
            lblGrupoImport,
            cmbGrupoImport
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Acción del botón OK
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (archivoSeleccionado[0] == null) {
                    mostrarError("Por favor seleccione un archivo Excel");
                    return;
                }
                if (cmbGrupoImport.getValue() == null) {
                    mostrarError("Por favor seleccione un grupo");
                    return;
                }

                boolean esFormatoNombreApellidos = rbNombreApellidos.isSelected();
                importarAlumnosDesdeExcel(archivoSeleccionado[0], esFormatoNombreApellidos, cmbGrupoImport.getValue());
            }
        });
    }

    /**
     * Importa alumnos desde un archivo Excel
     * @param archivo Archivo Excel a importar
     * @param esFormatoNombreApellidos true si el formato es Nombre(s)-Apellidos, false si es Apellidos-Nombre(s)
     * @param grupo Grupo al que se asignarán los alumnos
     */
    private void importarAlumnosDesdeExcel(File archivo, boolean esFormatoNombreApellidos, Grupo grupo) {
        try {
            FileInputStream fis = new FileInputStream(archivo);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0); // Leer la primera hoja

            List<Alumno> alumnosImportados = new ArrayList<>();
            int errores = 0;
            int filasProcesadas = 0;

            for (Row row : sheet) {
                // Saltar la primera fila si es encabezado (opcional)
                if (row.getRowNum() == 0) {
                    org.apache.poi.ss.usermodel.Cell firstCell = row.getCell(0);
                    if (firstCell != null && firstCell.getCellType() == CellType.STRING) {
                        String valor = firstCell.getStringCellValue().trim().toLowerCase();
                        if (valor.contains("nombre") || valor.contains("alumno")) {
                            continue; // Es encabezado, saltar
                        }
                    }
                }

                org.apache.poi.ss.usermodel.Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String nombreCompleto = cell.getStringCellValue().trim();

                    if (!nombreCompleto.isEmpty()) {
                        filasProcesadas++;
                        try {
                            Alumno alumno = parsearNombreCompleto(nombreCompleto, esFormatoNombreApellidos, grupo.getId());
                            if (alumno != null) {
                                alumnosImportados.add(alumno);
                            } else {
                                errores++;
                            }
                        } catch (Exception e) {
                            errores++;
                        }
                    }
                }
            }

            workbook.close();
            fis.close();

            // Guardar alumnos importados
            int guardados = 0;
            for (Alumno alumno : alumnosImportados) {
                try {
                    alumnoService.crearAlumno(alumno);
                    guardados++;
                } catch (Exception e) {
                    errores++;
                }
            }

            // Recargar tabla
            if (tablaAlumnos != null) {
                cargarDatos(tablaAlumnos);
            }

            // Mostrar resultado
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Importación completada");
            alerta.setHeaderText("Resultado de la importación");
            alerta.setContentText(
                "Filas procesadas: " + filasProcesadas + "\n" +
                "Alumnos importados: " + guardados + "\n" +
                "Errores: " + errores
            );
            alerta.showAndWait();

        } catch (Exception e) {
            manejarExcepcion("importar alumnos desde Excel", e);
        }
    }

    /**
     * Parsea un nombre completo y lo divide en nombre, apellido paterno y apellido materno
     * @param nombreCompleto Nombre completo del alumno
     * @param esFormatoNombreApellidos true si el formato es Nombre(s)-Apellidos, false si es Apellidos-Nombre(s)
     * @param grupoId ID del grupo
     * @return Objeto Alumno con los datos parseados
     */
    private Alumno parsearNombreCompleto(String nombreCompleto, boolean esFormatoNombreApellidos, Long grupoId) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            return null;
        }

        nombreCompleto = nombreCompleto.trim();
        String[] partes = nombreCompleto.split("\\s+");

        if (partes.length < 3) {
            // No hay suficientes partes (mínimo: 1 nombre + 2 apellidos)
            return null;
        }

        String nombre;
        String apellidoPaterno;
        String apellidoMaterno;

        if (esFormatoNombreApellidos) {
            // Formato: Nombre(s) - Apellidos
            // Los últimos 2 elementos son los apellidos, el resto es el nombre
            apellidoMaterno = capitalizarPrimeraLetra(partes[partes.length - 1]);
            apellidoPaterno = capitalizarPrimeraLetra(partes[partes.length - 2]);

            // El nombre es todo lo que queda antes de los apellidos
            StringBuilder nombreBuilder = new StringBuilder();
            for (int i = 0; i < partes.length - 2; i++) {
                if (i > 0) nombreBuilder.append(" ");
                nombreBuilder.append(capitalizarPrimeraLetra(partes[i]));
            }
            nombre = nombreBuilder.toString();

        } else {
            // Formato: Apellidos - Nombre(s)
            // Los primeros 2 elementos son los apellidos, el resto es el nombre
            apellidoPaterno = capitalizarPrimeraLetra(partes[0]);
            apellidoMaterno = capitalizarPrimeraLetra(partes[1]);

            // El nombre es todo lo que queda después de los apellidos
            StringBuilder nombreBuilder = new StringBuilder();
            for (int i = 2; i < partes.length; i++) {
                if (i > 2) nombreBuilder.append(" ");
                nombreBuilder.append(capitalizarPrimeraLetra(partes[i]));
            }
            nombre = nombreBuilder.toString();
        }

        return Alumno.builder()
            .nombre(nombre)
            .apellidoPaterno(apellidoPaterno)
            .apellidoMaterno(apellidoMaterno)
            .grupoId(grupoId)
            .build();
    }

    /**
     * Capitaliza la primera letra de cada palabra
     */
    private String capitalizarPrimeraLetra(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        texto = texto.toLowerCase();
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    /**
     * Ajusta el ancho de las columnas al contenido automáticamente
     * Incluye el ajuste de columnas con botones (Acciones)
     */
    private void ajustarColumnasAlContenido(TableView<Alumno> tabla) {
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        for (TableColumn<Alumno, ?> columna : tabla.getColumns()) {
            // Si es la columna de acciones, establecer un ancho fijo apropiado para los botones
            if ("Acciones".equals(columna.getText())) {
                columna.setPrefWidth(180); // Ancho suficiente para "Editar" + "Eliminar"
                columna.setMinWidth(180);
                columna.setMaxWidth(180);
                continue;
            }

            // Para las demás columnas, calcular el ancho basado en el contenido
            double anchoMaximo = calcularAnchoColumna(columna);
            columna.setPrefWidth(anchoMaximo);
        }
    }

    /**
     * Calcula el ancho óptimo para una columna basado en su contenido
     */
    private double calcularAnchoColumna(TableColumn<Alumno, ?> columna) {
        // Ancho mínimo basado en el header
        javafx.scene.text.Text textoHeader = new javafx.scene.text.Text(columna.getText());
        double anchoMaximo = textoHeader.getLayoutBounds().getWidth() + 40; // +40 para padding

        // Calcular ancho basado en el contenido de las celdas (máximo 50 filas para rendimiento)
        int filasARevisar = Math.min(tablaAlumnos.getItems().size(), 50);

        for (int i = 0; i < filasARevisar; i++) {
            Alumno alumno = tablaAlumnos.getItems().get(i);
            String valorCelda = obtenerValorCelda(columna, alumno);

            if (valorCelda != null && !valorCelda.isEmpty()) {
                javafx.scene.text.Text texto = new javafx.scene.text.Text(valorCelda);
                double ancho = texto.getLayoutBounds().getWidth() + 40; // +40 para padding
                if (ancho > anchoMaximo) {
                    anchoMaximo = ancho;
                }
            }
        }

        return anchoMaximo;
    }

    /**
     * Obtiene el valor de una celda para calcular su ancho
     */
    private String obtenerValorCelda(TableColumn<Alumno, ?> columna, Alumno alumno) {
        String nombreColumna = columna.getText();

        switch (nombreColumna) {
            case "N° Lista":
                return alumno.getNumeroLista() != null ? alumno.getNumeroLista().toString() : "";
            case "Nombre":
                return alumno.getNombre() != null ? alumno.getNombre() : "";
            case "Apellido Paterno":
                return alumno.getApellidoPaterno() != null ? alumno.getApellidoPaterno() : "";
            case "Apellido Materno":
                return alumno.getApellidoMaterno() != null ? alumno.getApellidoMaterno() : "";
            case "Grupo":
                if (alumno.getGrupoId() != null) {
                    try {
                        Optional<Grupo> grupo = grupoService.obtenerGrupoPorId(alumno.getGrupoId());
                        return grupo.map(Grupo::getNombre).orElse("Sin grupo");
                    } catch (Exception e) {
                        return "Error";
                    }
                }
                return "Sin grupo";
            default:
                return "";
        }
    }
}
