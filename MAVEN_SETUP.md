# 🔧 Configuración de Maven para el Proyecto

Esta guía te ayudará a configurar Maven correctamente para ejecutar el proyecto de Alumnos Desktop Application.

## 📥 Descarga e Instalación de Maven

### Windows

#### Opción 1: Descarga Manual

1. **Descargar Maven**
   - Ve a [Maven Download](https://maven.apache.org/download.cgi)
   - Descarga el archivo `apache-maven-X.X.X-bin.zip`

2. **Extraer Maven**
   - Extrae el archivo en `C:\Program Files\Apache\maven`
   - La estructura debería ser: `C:\Program Files\Apache\maven\bin\mvn.cmd`

3. **Configurar Variables de Entorno**
   
   **Método 1: Interfaz Gráfica**
   - Abrir "Panel de Control" → "Sistema" → "Configuración avanzada del sistema"
   - Clic en "Variables de entorno"
   - En "Variables del sistema", crear:
     - Variable: `MAVEN_HOME`
     - Valor: `C:\Program Files\Apache\maven`
   - Editar la variable `Path` y agregar:
     - `%MAVEN_HOME%\bin`
   - Clic en "Aceptar" en todas las ventanas

   **Método 2: PowerShell (Ejecutar como Administrador)**
   ```powershell
   # Configurar MAVEN_HOME
   [Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\Program Files\Apache\maven", "Machine")
   
   # Agregar al PATH
   $path = [Environment]::GetEnvironmentVariable("Path", "Machine")
   [Environment]::SetEnvironmentVariable("Path", "$path;C:\Program Files\Apache\maven\bin", "Machine")
   ```

4. **Verificar la Instalación**
   - Cerrar y abrir una nueva terminal PowerShell
   - Ejecutar:
   ```powershell
   mvn -version
   ```
   - Deberías ver la versión de Maven instalada

#### Opción 2: Usar Chocolatey (Recomendado)

Si tienes [Chocolatey](https://chocolatey.org/) instalado:

```powershell
# Ejecutar como Administrador
choco install maven
```

### Linux (Ubuntu/Debian)

```bash
# Actualizar repositorios
sudo apt update

# Instalar Maven
sudo apt install maven

# Verificar instalación
mvn -version
```

### macOS

#### Usando Homebrew (Recomendado)

```bash
# Instalar Maven
brew install maven

# Verificar instalación
mvn -version
```

#### Descarga Manual

```bash
# Descargar Maven
cd /opt
sudo curl -O https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz

# Extraer
sudo tar -xvzf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 maven

# Configurar PATH en ~/.zshrc o ~/.bash_profile
echo 'export MAVEN_HOME=/opt/maven' >> ~/.zshrc
echo 'export PATH=$MAVEN_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc

# Verificar
mvn -version
```

## 🚀 Compilar el Proyecto

Una vez Maven esté instalado, navega al directorio del proyecto:

```bash
# Windows (PowerShell)
cd D:\Desarrollos\alumnos

# Linux/Mac
cd /ruta/al/proyecto/alumnos
```

### Compilar sin ejecutar tests

```bash
mvn clean install -DskipTests
```

### Compilar y ejecutar tests

```bash
mvn clean install
```

### Ver las dependencias del proyecto

```bash
mvn dependency:tree
```

## 🎯 Ejecutar la Aplicación

### Opción 1: Con Maven (Recomendado)

```bash
mvn javafx:run
```

### Opción 2: Desde IntelliJ IDEA

1. Abrir el proyecto en IntelliJ IDEA
2. Esperar a que Maven descargue todas las dependencias
3. Click derecho en `AlumnosApplication.java`
4. Seleccionar "Run 'AlumnosApplication.main()'"

### Opción 3: Generar JAR ejecutable

```bash
# Generar JAR
mvn clean package -DskipTests

# Ejecutar el JAR
java -jar target/alumnos-1.0-SNAPSHOT.jar
```

## ⚠️ Solución de Problemas Comunes

### Maven no reconocido en la terminal

**Problema:** `mvn : The term 'mvn' is not recognized...`

**Solución:**
1. Verificar que Maven esté instalado correctamente
2. Verificar que `MAVEN_HOME` esté configurado
3. Verificar que `%MAVEN_HOME%\bin` esté en el PATH
4. **Reiniciar la terminal o el sistema** después de cambiar variables de entorno

### Error: JAVA_HOME no está configurado

**Problema:** `Error: JAVA_HOME is not defined correctly`

**Solución (Windows):**
```powershell
# Configurar JAVA_HOME (ajustar la ruta según tu instalación)
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.X-hotspot", "Machine")
```

### Error al descargar dependencias

**Problema:** Error de conexión al descargar dependencias

**Solución:**
1. Verificar conexión a internet
2. Limpiar el repositorio local de Maven:
   ```bash
   # Windows
   Remove-Item -Recurse -Force $env:USERPROFILE\.m2\repository
   
   # Linux/Mac
   rm -rf ~/.m2/repository
   ```
3. Volver a ejecutar `mvn clean install`

### JavaFX: Error Graphics Device initialization failed

**Problema:** Error al iniciar la interfaz gráfica

**Solución:**
1. Verificar que Java 17+ esté instalado
2. Ejecutar con: `mvn clean javafx:run`
3. Si persiste, actualizar drivers gráficos

## 📚 Comandos Maven Útiles

```bash
# Limpiar compilación anterior
mvn clean

# Compilar el proyecto
mvn compile

# Ejecutar tests
mvn test

# Empaquetar en JAR
mvn package

# Instalar en repositorio local
mvn install

# Ver dependencias
mvn dependency:tree

# Actualizar dependencias
mvn versions:display-dependency-updates

# Saltar tests
mvn install -DskipTests

# Modo offline (usar cache local)
mvn install -o
```

## 🔍 Verificar Configuración

Ejecuta estos comandos para verificar que todo esté correctamente configurado:

```bash
# Versión de Java
java -version

# Versión de Maven
mvn -version

# Variable JAVA_HOME
echo $env:JAVA_HOME  # Windows PowerShell
echo $JAVA_HOME      # Linux/Mac

# Variable MAVEN_HOME
echo $env:MAVEN_HOME # Windows PowerShell
echo $MAVEN_HOME     # Linux/Mac
```

## 📞 Necesitas Ayuda?

Si después de seguir esta guía aún tienes problemas:

1. Revisa los logs de error
2. Verifica las versiones de Java y Maven
3. Asegúrate de tener permisos de administrador al configurar variables
4. Reinicia tu sistema después de configurar variables de entorno
5. Consulta la documentación oficial de [Maven](https://maven.apache.org/guides/)

---

**Nota:** Este proyecto requiere Java 17 o superior y Maven 3.6+
