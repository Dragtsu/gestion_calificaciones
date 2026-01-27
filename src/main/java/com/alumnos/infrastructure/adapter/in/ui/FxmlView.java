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
    };

    public abstract String getTitle();
    public abstract String getFxmlFile();
}
