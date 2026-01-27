# ⚡ INSTALAR MAVEN - GUÍA RÁPIDA

## 🎯 Instalación Automática (5 minutos)

### Paso 1: Abre PowerShell como Administrador

1. Presiona `Windows + X`
2. Selecciona **"Windows PowerShell (Administrador)"** o **"Terminal (Administrador)"**
3. Aparecerá un cuadro de permisos, haz click en "Sí"

### Paso 2: Ejecuta el Script de Instalación

```powershell
cd D:\Desarrollos\alumnos
.\install-maven.ps1
```

### Paso 3: Espera a que Termine

El script hará automáticamente:
- ⬇️ Descargar Maven 3.9.5 (~9 MB)
- 📂 Instalar en `C:\Program Files\Apache\maven`
- ⚙️ Configurar variables de entorno
- ✅ Verificar la instalación

### Paso 4: Reinicia la Terminal

1. **Cierra todas las terminales**
2. Abre una nueva terminal (normal, no requiere admin)
3. Verifica que funcione:

```bash
mvn -version
```

Deberías ver:
```
Apache Maven 3.9.5
Maven home: C:\Program Files\Apache\maven
Java version: 22.0.1
```

---

## 🚀 Compilar el Proyecto

Una vez Maven esté instalado:

```bash
# Compilar el proyecto
mvn clean install -DskipTests

# Ejecutar la aplicación
mvn javafx:run
```

O simplemente ejecuta:
```powershell
.\run.ps1
```

---

## 🛠️ Instalación Manual (Alternativa)

Si prefieres instalar manualmente o el script falla:

### 1. Descargar Maven

Ve a: https://maven.apache.org/download.cgi

Descarga: `apache-maven-3.9.X-bin.zip`

### 2. Extraer

- Extrae el archivo ZIP
- Mueve la carpeta a: `C:\Program Files\Apache\maven`

### 3. Configurar Variables (PowerShell como Admin)

```powershell
[Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\Program Files\Apache\maven", "Machine")
$path = [Environment]::GetEnvironmentVariable("Path", "Machine")
[Environment]::SetEnvironmentVariable("Path", "$path;C:\Program Files\Apache\maven\bin", "Machine")
```

### 4. Verificar

Cierra y abre nueva terminal:
```bash
mvn -version
```

---

## ❌ Problemas Comunes

### "El script no se ejecuta"

**Error:** `.\install-maven.ps1 no se puede cargar porque la ejecución de scripts está deshabilitada`

**Solución:**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
.\install-maven.ps1
```

### "mvn no se reconoce"

**Causa:** Variables de entorno no actualizadas o terminal antigua

**Solución:**
1. Cierra TODAS las terminales abiertas
2. Abre una nueva terminal
3. Ejecuta: `mvn -version`

### "Acceso denegado"

**Causa:** No tienes permisos de administrador

**Solución:**
- Ejecuta PowerShell como **Administrador** (paso 1 arriba)

---

## 📦 Instalación con Chocolatey (Opcional)

Si ya tienes Chocolatey:

```powershell
choco install maven
```

---

## ✅ Verificación Final

Después de instalar, en una **nueva terminal**:

```bash
# Verificar Maven
mvn -version

# Verificar Java
java -version

# Compilar proyecto
cd D:\Desarrollos\alumnos
mvn clean install -DskipTests

# Ejecutar aplicación
mvn javafx:run
```

---

## 📞 ¿Necesitas Más Ayuda?

- 📖 Lee `MAVEN_SETUP.md` para más detalles
- 🔍 Consulta `LEEME_PRIMERO.md` para información general del proyecto

---

## 🎯 Resumen en 4 Pasos

```
1️⃣ Abrir PowerShell como ADMINISTRADOR
2️⃣ Ejecutar: .\install-maven.ps1
3️⃣ Cerrar y abrir nueva terminal
4️⃣ Ejecutar: mvn -version
```

**¡Listo!** Ahora puedes compilar y ejecutar el proyecto 🎉

---

**Script creado:** `D:\Desarrollos\alumnos\install-maven.ps1`  
**Tu Java:** OpenJDK 22.0.1 ✅  
**Maven requerido:** 3.6+ ⏳
