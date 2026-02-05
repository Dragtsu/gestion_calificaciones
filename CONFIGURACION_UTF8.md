# Configuración UTF-8 - Solución de Problemas de Acentos y Caracteres Especiales

## Problema Resuelto
Se han corregido los problemas de codificación de caracteres en el proyecto, incluyendo:
- ✓ Símbolo de "paloma" (✓) que aparecía como caracteres extraños
- ✓ Acentos en español (á, é, í, ó, ú, ñ)
- ✓ Caracteres especiales

## Cambios Aplicados

### 1. AlumnosApplication.java
Se agregó un bloque estático para configurar UTF-8 en toda la aplicación:

```java
static {
    System.setProperty("file.encoding", "UTF-8");
    System.setProperty("sun.jnu.encoding", "UTF-8");
    System.setProperty("javafx.platform.charset", "UTF-8");
}
```

### 2. pom.xml
Se agregaron las siguientes propiedades:

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>
```

Y en el plugin de compilación:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <encoding>UTF-8</encoding>
    </configuration>
</plugin>
```

### 3. .mvn/jvm.config
Se creó el archivo `.mvn/jvm.config` con:

```
-Dfile.encoding=UTF-8
-Dsun.jnu.encoding=UTF-8
```

### 4. ConcentradoController.java
Se corrigieron todos los caracteres mal codificados:
- `Γ£ô` → `✓` (símbolo de paloma/check)
- Acentos corregidos en comentarios

### 5. build-production.ps1
Se agregó configuración de UTF-8 en el script de compilación de producción.

## Cómo Compilar con UTF-8

### Opción 1: Script Automático
```powershell
.\rebuild-utf8.ps1
```

### Opción 2: Maven Manual
```bash
mvn clean compile -Dfile.encoding=UTF-8
```

### Opción 3: Ejecución Completa
```bash
mvn clean package -DskipTests -Dfile.encoding=UTF-8
```

## Verificación

Para verificar que los caracteres se muestran correctamente:

1. **Compilar el proyecto:**
   ```powershell
   .\rebuild-utf8.ps1
   ```

2. **Ejecutar la aplicación:**
   ```powershell
   .\run.ps1
   ```

3. **Verificar en la aplicación:**
   - Ir a "Informe de Concentrado"
   - Los checkboxes marcados deben mostrar: `✓`
   - Los acentos deben verse correctamente: `á é í ó ú ñ`

## Configuración del IDE (IntelliJ IDEA)

Si usas IntelliJ IDEA, asegúrate de:

1. **File > Settings > Editor > File Encodings**
   - Global Encoding: `UTF-8`
   - Project Encoding: `UTF-8`
   - Default encoding for properties files: `UTF-8`

2. **File > Settings > Build, Execution, Deployment > Compiler > Java Compiler**
   - Additional command line parameters: `-encoding UTF-8`

3. **Help > Edit Custom VM Options**
   Agregar:
   ```
   -Dfile.encoding=UTF-8
   -Dsun.jnu.encoding=UTF-8
   ```

## Configuración de Windows Terminal/PowerShell

Si ves caracteres extraños en la consola:

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001
```

## Problemas Comunes

### Problema: Los caracteres siguen viéndose mal
**Solución:**
1. Eliminar carpetas `target` y `.mvn` (excepto `jvm.config`)
2. Ejecutar: `mvn clean compile -Dfile.encoding=UTF-8`
3. Reiniciar el IDE

### Problema: El símbolo ✓ no se ve en la consola
**Solución:**
La consola de Windows puede no soportar todos los caracteres Unicode. Esto es normal y solo afecta a la consola, no a la aplicación JavaFX.

### Problema: Error al compilar con Maven
**Solución:**
```powershell
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
mvn clean compile
```

## Notas Importantes

- ✅ Todos los archivos `.java` deben estar guardados con codificación UTF-8
- ✅ El archivo `.mvn/jvm.config` debe estar presente
- ✅ La aplicación debe iniciarse con la configuración UTF-8
- ✅ No eliminar el bloque `static` en `AlumnosApplication.java`

## Referencias

- [Maven Encoding](https://maven.apache.org/general.html#encoding-warning)
- [Java UTF-8 Everywhere](https://utf8everywhere.org/)
- [JavaFX Character Encoding](https://openjfx.io/openjfx-docs/)
