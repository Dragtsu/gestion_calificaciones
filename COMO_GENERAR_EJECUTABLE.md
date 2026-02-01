# Generar Ejecutable usando IntelliJ IDEA

## 🚀 Método 1: Usando IntelliJ IDEA (Sin Maven en PATH)

### Paso 1: Abrir la ventana de Maven en IntelliJ
1. Abre el proyecto en IntelliJ IDEA
2. Ve al menú **View** → **Tool Windows** → **Maven**
3. Se abrirá el panel de Maven en el lado derecho

### Paso 2: Limpiar el proyecto
1. En el panel de Maven, expande **alumnos**
2. Expande **Lifecycle**
3. Haz doble clic en **clean**

### Paso 3: Generar el ejecutable
1. En el panel de Maven, dentro de **Lifecycle**
2. Haz doble clic en **package**
3. Espera a que termine la compilación

### Paso 4: Ubicar el ejecutable
El archivo JAR se generará en:
```
target\Alumnos-1.0-SNAPSHOT.jar
```

---

## 🚀 Método 2: Usando el Terminal de IntelliJ

### Paso 1: Abrir Terminal en IntelliJ
1. Ve a **View** → **Tool Windows** → **Terminal**
2. O presiona **Alt + F12**

### Paso 2: Ejecutar comando Maven
En el terminal de IntelliJ, ejecuta:
```bash
mvn clean package -DskipTests
```

---

## 🚀 Método 3: Crear Configuración de Run

### Paso 1: Crear nueva configuración
1. Ve a **Run** → **Edit Configurations...**
2. Haz clic en el **+** (Agregar nueva configuración)
3. Selecciona **Maven**

### Paso 2: Configurar
- **Name:** Build Production JAR
- **Command line:** clean package -DskipTests
- **Working directory:** Deja el directorio del proyecto

### Paso 3: Ejecutar
1. Selecciona la configuración "Build Production JAR"
2. Haz clic en **Run** (triángulo verde)

---

## ▶️ Ejecutar el JAR Generado

Una vez generado el JAR, puedes ejecutarlo de varias formas:

### Desde IntelliJ Terminal:
```bash
java -jar target\Alumnos-1.0-SNAPSHOT.jar
```

### Desde PowerShell:
```powershell
.\start-production.ps1
```

### Desde Símbolo del sistema:
```batch
start-production.bat
```

### Doble clic (Windows):
1. Navega a la carpeta `target`
2. Haz doble clic en `Alumnos-1.0-SNAPSHOT.jar`
   *(Nota: Esto solo funciona si Java está asociado con archivos .jar)*

---

## 📦 Empaquetar para Distribución

### Crear carpeta de distribución:
1. Crea una carpeta llamada `Alumnos-Distribucion`
2. Copia los siguientes archivos:
   ```
   Alumnos-Distribucion/
   ├── Alumnos-1.0-SNAPSHOT.jar (desde target/)
   ├── alumnos.db
   ├── start-production.bat
   └── plantillas/
       └── concentrado_calificaciones.docx
   ```

### Comprimir para enviar:
1. Selecciona la carpeta `Alumnos-Distribucion`
2. Clic derecho → **Enviar a** → **Carpeta comprimida**
3. El ZIP generado puede enviarse a otros usuarios

---

## 🔍 Verificar el Ejecutable

### Verificar tamaño:
El JAR debería tener entre 70-100 MB (incluye todas las dependencias)

### Probar ejecución:
```bash
java -jar target\Alumnos-1.0-SNAPSHOT.jar
```

Si la aplicación se abre correctamente, ¡el ejecutable está listo! ✅

---

## ❗ Notas Importantes

### Requisitos para usuarios finales:
- Java 17 o superior instalado
- Descargar desde: https://adoptium.net/

### Si el JAR no ejecuta con doble clic:
1. Verifica que Java esté instalado: `java -version`
2. Asocia archivos .jar con Java:
   - Clic derecho en el .jar → **Abrir con** → **Java(TM) Platform SE binary**
   - Marca "Usar siempre esta aplicación"

### Archivos necesarios en el mismo directorio:
- `alumnos.db` (base de datos)
- `plantillas/` (carpeta con plantillas Word)

---

## 🐛 Solución de Problemas

### Error: "no main manifest attribute"
El `pom.xml` ya está configurado correctamente con Spring Boot Maven Plugin.
Si ves este error, verifica que la compilación haya sido exitosa.

### Error: "Could not find or load main class"
Asegúrate de ejecutar desde el directorio raíz del proyecto.

### Error de JavaFX
Verifica que estés usando Java con soporte para JavaFX o una versión específica como Liberica JDK Full.

---

## 📞 Ayuda Adicional

Si encuentras problemas durante la compilación:
1. Revisa la consola de Maven en IntelliJ
2. Verifica que todas las dependencias se descarguen correctamente
3. Intenta: **File** → **Invalidate Caches** → **Invalidate and Restart**
