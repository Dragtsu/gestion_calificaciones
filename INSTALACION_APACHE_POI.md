# Instalación Rápida de Apache POI

## ✅ Resumen de Cambios Realizados

Se han agregado las siguientes dependencias al archivo `pom.xml`:

1. **Apache POI Core** (v5.2.5)
2. **Apache POI OOXML** (v5.2.5) 
3. **Apache POI OOXML Schemas** (v5.2.5)
4. **XML Beans** (v5.1.1)

## 🚀 Pasos para Cargar las Dependencias

### Opción 1: IntelliJ IDEA (Recomendado)

1. **Abrir el proyecto en IntelliJ IDEA**

2. **Recargar el proyecto Maven:**
   - Haz clic en el ícono de Maven en la esquina superior derecha (aparecerá un aviso)
   - O presiona `Ctrl + Shift + O`
   - O ve a **File → Reload All Maven Projects**
   - O abre la ventana Maven (View → Tool Windows → Maven) y haz clic en el ícono de "Reload"

3. **Esperar la descarga:**
   - IntelliJ descargará automáticamente las dependencias
   - Verás el progreso en la barra inferior
   - Puede tardar 1-2 minutos la primera vez

4. **Verificar:**
   - Los errores en `WordExportService.java` desaparecerán
   - Las clases de Apache POI estarán disponibles

### Opción 2: Maven desde Terminal

Si tienes Maven instalado:

```powershell
cd D:\Desarrollos\alumnos
mvn clean install
```

Si no tienes Maven instalado:

```powershell
.\install-maven.ps1
```

Luego ejecutar el comando mvn.

### Opción 3: Compilar el Proyecto

```powershell
.\compile-and-run.ps1
```

Este script detectará Maven y descargará las dependencias.

## 📦 Archivos Creados

### 1. Servicio Principal
**Ubicación:** `src/main/java/com/alumnos/application/service/WordExportService.java`

Proporciona métodos para:
- Crear documentos simples
- Crear documentos con tablas
- Usar plantillas con marcadores
- Generar documentos desde plantillas con tablas dinámicas

### 2. Documentación
**Ubicación:** `APACHE_POI_WORD_EXPORT.md`

Documentación completa con:
- Explicación de las dependencias
- Ejemplos de uso
- Casos de uso para el sistema
- Integración con JavaFX
- Referencias y mejores prácticas

## ✨ Próximos Pasos

### 1. Verificar la Instalación

Después de recargar Maven, verificar que los imports funcionen:

```java
import org.apache.poi.xwpf.usermodel.*;
```

### 2. Crear la Carpeta de Plantillas

```powershell
mkdir plantillas
```

### 3. Probar el Servicio

Crear un controlador de prueba:

```java
@RestController
public class TestWordController {
    
    @Autowired
    private WordExportService wordExportService;
    
    @GetMapping("/test-word")
    public String testWord() throws IOException {
        Path outputPath = Paths.get("test.docx");
        wordExportService.crearDocumentoSimple(
            outputPath,
            "Prueba",
            "Este es un documento de prueba"
        );
        return "Documento creado: " + outputPath.toAbsolutePath();
    }
}
```

### 4. Implementar Exportaciones

Según tus necesidades, implementar:
- Exportar lista de alumnos
- Exportar concentrado de calificaciones
- Exportar reporte de exámenes
- Exportar boletas individuales

## 🔍 Verificación de Dependencias

Para verificar que las dependencias se descargaron correctamente:

### En IntelliJ:

1. Abrir **File → Project Structure → Libraries**
2. Buscar las librerías de Apache POI
3. Deberías ver: poi, poi-ooxml, poi-ooxml-schemas, xmlbeans

### En Maven:

```powershell
mvn dependency:tree | Select-String "poi"
```

Deberías ver algo como:

```
[INFO] +- org.apache.poi:poi:jar:5.2.5:compile
[INFO] +- org.apache.poi:poi-ooxml:jar:5.2.5:compile
[INFO] +- org.apache.poi:poi-ooxml-schemas:jar:5.2.5:compile
```

## ⚠️ Solución de Problemas

### Error: "Cannot resolve symbol 'poi'"

**Causa:** Las dependencias no se han descargado aún.

**Solución:**
1. Recargar proyecto Maven en IntelliJ
2. O ejecutar `mvn clean install` desde terminal
3. Esperar a que termine la descarga

### Error: "Failed to download dependency"

**Causa:** Problema de conexión o repositorio Maven.

**Solución:**
1. Verificar conexión a Internet
2. Intentar de nuevo después de unos minutos
3. Limpiar cache de Maven: `mvn dependency:purge-local-repository`

### Los imports siguen en rojo después de recargar

**Solución:**
1. **File → Invalidate Caches → Invalidate and Restart**
2. Esperar a que IntelliJ reindexe el proyecto

### Maven no está instalado

**Solución:**
```powershell
.\install-maven.ps1
```

O instalar manualmente desde: https://maven.apache.org/download.cgi

## 📚 Recursos Adicionales

- [Documentación Apache POI](https://poi.apache.org/)
- [Ejemplos de XWPF](https://poi.apache.org/components/document/quick-guide-xwpf.html)
- [Guía completa](./APACHE_POI_WORD_EXPORT.md)

## 💡 Consejos

1. **Usar IntelliJ IDEA**: Es la forma más fácil de gestionar dependencias
2. **Plantillas**: Crear plantillas en Word facilita mucho el diseño
3. **Testing**: Probar con documentos pequeños primero
4. **Performance**: Para grandes volúmenes, procesar en background

---

**Fecha:** 2026-01-29  
**Versión Apache POI:** 5.2.5  
**Estado:** ✅ Configurado - Pendiente carga de dependencias
