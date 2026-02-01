package com.alumnos.infrastructure.adapter.in.ui;

public enum FxmlView {
    HOME {
        @Override
        public String getTitle() {
            return "Sistema de Gestión de Alumnos - Inicio";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/home.fxml";
        }
    },
    CONFIGURACION_INICIAL {
        @Override
        public String getTitle() {
            return "Configuración Inicial";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/configuracion_inicial.fxml";
        }
    };

    public abstract String getTitle();
    public abstract String getFxmlFile();
}
