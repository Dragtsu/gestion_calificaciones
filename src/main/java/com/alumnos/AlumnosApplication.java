package com.alumnos;

import com.alumnos.infrastructure.adapter.in.ui.JavaFXApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlumnosApplication {

    public static void main(String[] args) {
        Application.launch(JavaFXApplication.class, args);
    }
}
