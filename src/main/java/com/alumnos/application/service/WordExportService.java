package com.alumnos.application.service;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Servicio para exportar datos a documentos Word usando Apache POI
 * Permite trabajar con plantillas y generar documentos dinámicamente
 */
@Service
public class WordExportService {

    /**
     * Crea un documento Word simple desde cero
     *
     * @param outputPath Ruta donde se guardará el documento
     * @param titulo Título del documento
     * @param contenido Contenido del documento
     * @throws IOException Si hay error al escribir el archivo
     */
    public void crearDocumentoSimple(Path outputPath, String titulo, String contenido) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(outputPath.toFile())) {

            // Crear título
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(titulo);
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // Agregar contenido
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText(contenido);

            document.write(out);
        }
    }

    /**
     * Crea un documento con una tabla
     *
     * @param outputPath Ruta donde se guardará el documento
     * @param titulo Título del documento
     * @param encabezados Encabezados de la tabla
     * @param datos Datos de la tabla (cada sublista es una fila)
     * @throws IOException Si hay error al escribir el archivo
     */
    public void crearDocumentoConTabla(Path outputPath, String titulo,
                                       List<String> encabezados,
                                       List<List<String>> datos) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(outputPath.toFile())) {

            // Crear título
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(titulo);
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.addBreak();

            // Crear tabla
            XWPFTable table = document.createTable(datos.size() + 1, encabezados.size());
            table.setWidth("100%");

            // Configurar encabezados
            XWPFTableRow headerRow = table.getRow(0);
            for (int i = 0; i < encabezados.size(); i++) {
                XWPFTableCell cell = headerRow.getCell(i);
                cell.setColor("4472C4"); // Color azul para encabezados
                XWPFParagraph paragraph = cell.getParagraphs().get(0);
                XWPFRun run = paragraph.createRun();
                run.setText(encabezados.get(i));
                run.setBold(true);
                run.setColor("FFFFFF"); // Texto blanco
            }

            // Llenar datos
            for (int i = 0; i < datos.size(); i++) {
                XWPFTableRow row = table.getRow(i + 1);
                List<String> rowData = datos.get(i);
                for (int j = 0; j < rowData.size() && j < encabezados.size(); j++) {
                    XWPFTableCell cell = row.getCell(j);
                    cell.setText(rowData.get(j));
                }
            }

            document.write(out);
        }
    }

    /**
     * Usa una plantilla Word existente y reemplaza marcadores con valores
     * Los marcadores en la plantilla deben tener el formato: ${nombreVariable}
     *
     * @param templatePath Ruta de la plantilla
     * @param outputPath Ruta donde se guardará el documento generado
     * @param variables Mapa con las variables a reemplazar (clave: nombre sin ${}, valor: texto)
     * @throws IOException Si hay error al leer o escribir el archivo
     */
    public void generarDesdeTemplateConMarcadores(Path templatePath, Path outputPath,
                                                  Map<String, String> variables) throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath.toFile());
             XWPFDocument document = new XWPFDocument(fis);
             FileOutputStream out = new FileOutputStream(outputPath.toFile())) {

            // Reemplazar en párrafos
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                reemplazarEnParagraph(paragraph, variables);
            }

            // Reemplazar en tablas
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            reemplazarEnParagraph(paragraph, variables);
                        }
                    }
                }
            }

            document.write(out);
        }
    }

    /**
     * Usa una plantilla y llena una tabla identificada por un marcador
     *
     * @param templatePath Ruta de la plantilla
     * @param outputPath Ruta donde se guardará el documento
     * @param variables Variables simples a reemplazar
     * @param tableMarcador Marcador que identifica la tabla (ej: "TABLA_ALUMNOS")
     * @param encabezados Encabezados de la tabla
     * @param datos Datos para llenar la tabla
     * @throws IOException Si hay error al leer o escribir el archivo
     */
    public void generarDesdeTemplateConTabla(Path templatePath, Path outputPath,
                                            Map<String, String> variables,
                                            String tableMarcador,
                                            List<String> encabezados,
                                            List<List<String>> datos) throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath.toFile());
             XWPFDocument document = new XWPFDocument(fis);
             FileOutputStream out = new FileOutputStream(outputPath.toFile())) {

            // Reemplazar variables simples en párrafos
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                reemplazarEnParagraph(paragraph, variables);
            }

            // Buscar y llenar tabla con el marcador
            boolean tablaEncontrada = false;
            for (XWPFTable table : document.getTables()) {
                // Verificar si la tabla contiene el marcador
                if (contieneTexto(table, tableMarcador)) {
                    tablaEncontrada = true;
                    llenarTabla(table, encabezados, datos);
                    break;
                }
            }

            // Si no se encontró la tabla con el marcador, crearla
            if (!tablaEncontrada) {
                crearTablaEnDocumento(document, encabezados, datos);
            }

            document.write(out);
        }
    }

    /**
     * Reemplaza marcadores en un párrafo
     */
    private void reemplazarEnParagraph(XWPFParagraph paragraph, Map<String, String> variables) {
        String text = paragraph.getText();
        if (text != null && !text.isEmpty()) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String marcador = "${" + entry.getKey() + "}";
                if (text.contains(marcador)) {
                    text = text.replace(marcador, entry.getValue());
                }
            }

            // Limpiar runs existentes y crear uno nuevo con el texto reemplazado
            if (!text.equals(paragraph.getText())) {
                // Guardar formato del primer run
                XWPFRun firstRun = paragraph.getRuns().isEmpty() ? null : paragraph.getRuns().get(0);
                boolean isBold = firstRun != null && firstRun.isBold();
                int fontSize = firstRun != null ? firstRun.getFontSize() : 11;
                String fontFamily = firstRun != null ? firstRun.getFontFamily() : null;

                // Eliminar todos los runs
                for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }

                // Crear nuevo run con texto reemplazado
                XWPFRun newRun = paragraph.createRun();
                newRun.setText(text);
                if (isBold) newRun.setBold(true);
                if (fontSize > 0) newRun.setFontSize(fontSize);
                if (fontFamily != null) newRun.setFontFamily(fontFamily);
            }
        }
    }

    /**
     * Verifica si una tabla contiene un texto específico
     */
    private boolean contieneTexto(XWPFTable table, String texto) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                if (cell.getText().contains(texto)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Llena una tabla existente con datos
     */
    private void llenarTabla(XWPFTable table, List<String> encabezados, List<List<String>> datos) {
        // Limpiar filas existentes excepto la primera (encabezados)
        int rowCount = table.getNumberOfRows();
        for (int i = rowCount - 1; i > 0; i--) {
            table.removeRow(i);
        }

        // Configurar encabezados
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < encabezados.size(); i++) {
            XWPFTableCell cell;
            if (i < headerRow.getTableCells().size()) {
                cell = headerRow.getCell(i);
            } else {
                cell = headerRow.createCell();
            }
            cell.setColor("4472C4");
            cell.setText(encabezados.get(i));
        }

        // Agregar datos
        for (List<String> rowData : datos) {
            XWPFTableRow row = table.createRow();
            for (int i = 0; i < rowData.size() && i < encabezados.size(); i++) {
                XWPFTableCell cell = row.getCell(i);
                if (cell != null) {
                    cell.setText(rowData.get(i));
                }
            }
        }
    }

    /**
     * Crea una nueva tabla en el documento
     */
    private void crearTablaEnDocumento(XWPFDocument document, List<String> encabezados,
                                      List<List<String>> datos) {
        XWPFTable table = document.createTable(datos.size() + 1, encabezados.size());

        // Configurar encabezados
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < encabezados.size(); i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            cell.setColor("4472C4");
            cell.setText(encabezados.get(i));
        }

        // Llenar datos
        for (int i = 0; i < datos.size(); i++) {
            XWPFTableRow row = table.getRow(i + 1);
            List<String> rowData = datos.get(i);
            for (int j = 0; j < rowData.size() && j < encabezados.size(); j++) {
                row.getCell(j).setText(rowData.get(j));
            }
        }
    }

    /**
     * Ejemplo de uso: Exportar lista de alumnos
     * Este método puede servir como referencia para implementar otras exportaciones
     */
    public void exportarListaAlumnos(Path outputPath, String titulo,
                                     List<Map<String, String>> alumnos) throws IOException {
        List<String> encabezados = List.of("Matrícula", "Nombre", "Apellidos", "Grupo");
        List<List<String>> datos = alumnos.stream()
            .map(alumno -> List.of(
                alumno.getOrDefault("matricula", ""),
                alumno.getOrDefault("nombre", ""),
                alumno.getOrDefault("apellidos", ""),
                alumno.getOrDefault("grupo", "")
            ))
            .toList();

        crearDocumentoConTabla(outputPath, titulo, encabezados, datos);
    }
}
