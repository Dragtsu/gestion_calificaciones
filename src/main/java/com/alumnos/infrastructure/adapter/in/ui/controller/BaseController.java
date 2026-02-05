package com.alumnos.infrastructure.adapter.in.ui.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Controlador base con funcionalidades comunes para todos los controladores
 */
public abstract class BaseController {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    protected boolean confirmarAccion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    protected void mostrarError(String mensaje) {
        mostrarAlerta("Error", mensaje, Alert.AlertType.ERROR);
    }

    protected void mostrarExito(String mensaje) {
        mostrarAlerta("Éxito", mensaje, Alert.AlertType.INFORMATION);
    }

    protected void mostrarInformacion(String mensaje) {
        mostrarAlerta("Información", mensaje, Alert.AlertType.INFORMATION);
    }

    protected void mostrarAdvertencia(String mensaje) {
        mostrarAlerta("Advertencia", mensaje, Alert.AlertType.WARNING);
    }

    protected boolean validarCampoNoVacio(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            mostrarError("El campo '" + nombreCampo + "' es obligatorio");
            return false;
        }
        return true;
    }

    protected void manejarExcepcion(String operacion, Exception e) {
        LOG.error("Error en {}", operacion, e);
        mostrarError("Error al " + operacion + ": " + e.getMessage());
    }
}
