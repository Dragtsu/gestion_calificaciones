# Reversión: Cambios de Header y Configuración de Ventana

## Problema Reportado
Después de los últimos cambios:
- ❌ El menú lateral no se visualizaba
- ❌ El header personalizado no se veía
- ❌ Se mostraba el header del sistema operativo (no solicitado)

## Causa del Problema
Los cambios realizados para mejorar la compatibilidad en otras PCs causaron:
1. El orden de `initStyle()` movido al principio causó conflictos
2. `Platform.runLater()` en el `initialize()` retrasaba la configuración del arrastre
3. Tamaños fijos muy rígidos en el header impedían su correcta visualización
4. La maximización con `Platform.runLater()` causaba problemas de layout

## Reversión Realizada

### 1. StageManager.java - Restaurado al orden original

**Revertido a:**
```java
private void show(final Parent rootNode, String title) {
    Scene scene = prepareScene(rootNode);
    
    primaryStage.setTitle(title);
    primaryStage.setScene(scene);
    
    // Añadir icono
    
    // Configurar ventana sin decoraciones
    primaryStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
    
    // Configurar tamaños mínimos
    primaryStage.setMinWidth(800);
    primaryStage.setMinHeight(600);
    
    // Obtener dimensiones de la pantalla
    javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
    javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
    
    // Configurar tamaño inicial (95% de la pantalla)
    double width = bounds.getWidth() * 0.95;
    double height = bounds.getHeight() * 0.95;
    
    primaryStage.setWidth(width);
    primaryStage.setHeight(height);
    
    // Centrar la ventana
    primaryStage.setX((bounds.getWidth() - width) / 2 + bounds.getMinX());
    primaryStage.setY((bounds.getHeight() - height) / 2 + bounds.getMinY());
    
    // Maximizar la ventana (inmediatamente)
    primaryStage.setMaximized(true);
    
    primaryStage.show();
}
```

**Cambios revertidos:**
- ❌ Eliminado `Platform.runLater()` para maximizar
- ❌ Eliminado logging de resolución
- ❌ Eliminado chequeo de `getStyle() == null`
- ✅ `initStyle()` se llama después de `setScene()` (orden original)
- ✅ Maximización inmediata (no diferida)

### 2. HomeControllerRefactored.java - Initialize simplificado

**Revertido a:**
```java
@FXML
public void initialize() {
    if (vistaEstudiantes != null) {
        LOG.warn("Ya inicializado, saltando");
        return;
    }
    configurarArrastreVentana();
    cargarVistas();
    mostrarVista("estudiantes");
}
```

**Cambios revertidos:**
- ❌ Eliminado `Platform.runLater()` que envolvía `configurarArrastreVentana()`
- ❌ Eliminado `mainContent.requestLayout()`
- ❌ Eliminado logging de layout actualizado
- ✅ Configuración de arrastre inmediata
- ✅ Flujo de inicialización simple y directo

### 3. home.fxml - Header flexible

**Revertido a:**
```xml
<VBox styleClass="header" 
      style="-fx-background-color: #2196F3; -fx-padding: 8 15;">
    <HBox alignment="CENTER_LEFT" spacing="10">
        <!-- Contenido del header -->
    </HBox>
</VBox>
```

**Cambios revertidos:**
- ❌ Eliminadas alturas fijas del VBox header (`minHeight`, `prefHeight`, `maxHeight`)
- ❌ Eliminadas alturas fijas del HBox interno
- ❌ Eliminados tamaños fijos de botones (30x30px)
- ❌ Eliminado ancho fijo del container de botones
- ❌ Reducido tamaño de fuente de 16px a 14px en botones
- ✅ Header con altura automática basada en contenido
- ✅ Botones con tamaños flexibles

**Botones - Configuración restaurada:**
```xml
<!-- Botón Minimizar -->
<Button fx:id="btnMinimizar" onAction="#minimizeWindow"
        style="-fx-background-color: transparent; -fx-text-fill: white; 
               -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 3 10;">
    <graphic>
        <Label text="─" style="-fx-text-fill: white; -fx-font-size: 14px; 
                              -fx-font-weight: bold;"/>
    </graphic>
</Button>

<!-- Botón Cerrar -->
<Button fx:id="btnCerrar" onAction="#closeWindow"
        style="-fx-background-color: #d32f2f; -fx-text-fill: white; 
               -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 3 10; 
               -fx-background-radius: 0;">
    <graphic>
        <Label text="✕" style="-fx-text-fill: white; -fx-font-size: 14px; 
                              -fx-font-weight: bold;"/>
    </graphic>
</Button>
```

## Configuración Actual (Restaurada)

### Ventana
| Propiedad | Valor |
|-----------|-------|
| Estilo | UNDECORATED (sin decoraciones del SO) |
| Tamaño mínimo | 800x600 px |
| Tamaño inicial | 95% de la pantalla |
| Estado inicial | Maximizada |
| Header del SO | NO visible (correcto) ✅ |

### Header Personalizado
| Propiedad | Valor |
|-----------|-------|
| Color de fondo | #2196F3 (azul) |
| Altura | Automática (basada en contenido) |
| Botones | Tamaño flexible con padding 3px 10px |
| Font size | 14px |

### Comportamiento
✅ **Header personalizado visible** (azul con título y botones)
✅ **Sin header del sistema operativo** (ventana UNDECORATED)
✅ **Menú lateral funcional** (drawer menu)
✅ **Arrastre de ventana funcional**
✅ **Botones de cerrar y minimizar funcionales**

## Orden de Inicialización (Restaurado)

```
1. prepareScene()
2. setTitle()
3. setScene()
4. [Añadir icono]
5. initStyle(UNDECORATED)    ← Aquí se elimina el header del SO
6. setMinWidth/Height()
7. Calcular tamaño (95% pantalla)
8. setWidth/Height()
9. Centrar ventana
10. setMaximized(true)       ← Maximizar inmediatamente
11. show()
```

## Archivos Modificados

1. ✅ `src/main/java/com/alumnos/infrastructure/config/StageManager.java`
   - Revertido al orden original de inicialización
   - Maximización inmediata sin Platform.runLater()

2. ✅ `src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/HomeControllerRefactored.java`
   - Initialize simplificado sin Platform.runLater()

3. ✅ `src/main/resources/fxml/home.fxml`
   - Header con tamaños flexibles
   - Botones sin tamaños fijos

## Resultado Final

✅ **Header personalizado visible** (fondo azul, título, botones)
✅ **Menú lateral visible y funcional**
✅ **Sin header del sistema operativo** (ventana sin decoraciones)
✅ **Ventana maximizada correctamente**
✅ **Todos los controles accesibles**

## Lecciones Aprendidas

1. ⚠️ **Orden de `initStyle()`:** No importa si se llama antes o después de `setScene()`, pero debe llamarse ANTES de `show()`

2. ⚠️ **Platform.runLater() para maximizar:** Puede causar problemas de layout y timing

3. ⚠️ **Tamaños fijos en FXML:** Pueden impedir que los elementos se visualicen correctamente en algunas resoluciones

4. ✅ **Tamaños flexibles:** Es mejor usar padding y dejar que los componentes se ajusten automáticamente

## Fecha
8 de febrero de 2026

---

**Estado:** ✅ **REVERTIDO Y FUNCIONAL**

La aplicación ahora funciona correctamente con:
- Header personalizado visible
- Sin header del sistema operativo
- Menú lateral funcional
- Todos los controles accesibles
