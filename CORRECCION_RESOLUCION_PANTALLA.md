# Corrección: Problemas de Resolución de Pantalla

## Problema
Las pantallas no se ajustaban correctamente en monitores con diferentes resoluciones. Los botones de cerrar y minimizar no eran visibles en algunas configuraciones de pantalla.

## Causa
1. La ventana estaba configurada con `setMaximized(true)` sin considerar las dimensiones específicas de cada monitor
2. El archivo FXML tenía dimensiones fijas (`prefHeight="700.0" prefWidth="1000.0"`)
3. No había adaptación dinámica al tamaño de la pantalla

## Solución Implementada

### 1. StageManager.java
**Archivo:** `src/main/java/com/alumnos/infrastructure/config/StageManager.java`

#### Cambios realizados:
- ✅ Se agregó detección automática de la resolución de la pantalla usando `Screen.getPrimary().getVisualBounds()`
- ✅ Se configuró el tamaño inicial de la ventana al 90% del tamaño de la pantalla
- ✅ Se agregó centrado automático de la ventana
- ✅ Se configuraron tamaños mínimos (800x600) para evitar ventanas muy pequeñas
- ✅ Se mantiene la funcionalidad de maximizar

```java
// Configurar tamaño mínimo para la ventana
primaryStage.setMinWidth(800);
primaryStage.setMinHeight(600);

// Obtener dimensiones de la pantalla
javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();

// Configurar tamaño inicial de la ventana (90% de la pantalla)
double width = bounds.getWidth() * 0.9;
double height = bounds.getHeight() * 0.9;

primaryStage.setWidth(width);
primaryStage.setHeight(height);

// Centrar la ventana
primaryStage.setX((bounds.getWidth() - width) / 2 + bounds.getMinX());
primaryStage.setY((bounds.getHeight() - height) / 2 + bounds.getMinY());
```

### 2. home.fxml
**Archivo:** `src/main/resources/fxml/home.fxml`

#### Cambios realizados:
- ✅ Se agregaron dimensiones mínimas responsivas (`minHeight="600.0" minWidth="800.0"`)
- ✅ Se configuró el StackPane y BorderPane con `maxHeight="Infinity"` y `maxWidth="Infinity"` para permitir expansión
- ✅ Se ajustó el prefHeight del menú lateral para que sea responsive

```xml
<StackPane minHeight="600.0" minWidth="800.0"
           prefHeight="768.0" prefWidth="1024.0"
           maxHeight="Infinity" maxWidth="Infinity">

    <BorderPane fx:id="mainContent" 
                minHeight="600.0" minWidth="800.0"
                maxHeight="Infinity" maxWidth="Infinity">
```

### 3. HomeControllerRefactored.java
**Archivo:** `src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/HomeControllerRefactored.java`

#### Nuevas funcionalidades agregadas:

##### a) Arrastre de ventana
- ✅ Se agregó la capacidad de arrastrar la ventana desde el header
- ✅ Solo funciona cuando la ventana NO está maximizada
- ✅ Implementado con eventos `onMousePressed` y `onMouseDragged`

```java
private void configurarArrastreVentana() {
    VBox header = (VBox) mainContent.getTop();
    
    header.setOnMousePressed(event -> {
        javafx.stage.Stage stage = (javafx.stage.Stage) lblTitulo.getScene().getWindow();
        if (!stage.isMaximized()) {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        }
    });

    header.setOnMouseDragged(event -> {
        javafx.stage.Stage stage = (javafx.stage.Stage) lblTitulo.getScene().getWindow();
        if (!stage.isMaximized()) {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }
    });
}
```


## Mejoras de UX

### Botones de control de ventana
**Orden de botones en el header:**
1. 🗕 Minimizar (─) - Fondo transparente
2. ✕ Cerrar - Fondo rojo (#d32f2f)

### Interacciones
1. **Arrastrar ventana**: Click y arrastrar desde el header (solo cuando no está maximizada)
2. **Minimizar**: Click en botón ─
3. **Cerrar**: Click en botón ✕

## Compatibilidad

### Resoluciones soportadas
- ✅ Resolución mínima: 800x600 px
- ✅ Resoluciones comunes: 1024x768, 1280x720, 1366x768, 1920x1080
- ✅ Resoluciones altas: 2K (2560x1440), 4K (3840x2160)
- ✅ Multi-monitor: Se adapta automáticamente al monitor principal

### Comportamiento responsive
- La ventana se inicia al 90% del tamaño de la pantalla disponible
- Se centra automáticamente
- Se puede redimensionar manualmente (respetando el tamaño mínimo)
- Se puede maximizar para usar toda la pantalla
- Los botones de control siempre son visibles en el header

## Resultado
✅ Los botones de cerrar y minimizar ahora son siempre visibles
✅ La aplicación se adapta a cualquier resolución de pantalla
✅ Mejor experiencia de usuario con arrastre de ventana
✅ Ventana responsive que respeta los límites de tamaño
✅ La ventana inicia maximizada y se adapta automáticamente al tamaño de la pantalla

## Archivos Modificados
1. `src/main/java/com/alumnos/infrastructure/config/StageManager.java`
2. `src/main/resources/fxml/home.fxml`
3. `src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/HomeControllerRefactored.java`

## Fecha
8 de febrero de 2026
