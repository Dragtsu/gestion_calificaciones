# Sistema de Gestión de Alumnos
Aplicación de escritorio para gestión de alumnos desarrollada con JavaFX, Spring Boot y SQLite.
## Requisitos para Desarrollo
- Java JDK 22 o superior
- Maven 3.8+
- Git
- IDE recomendado: IntelliJ IDEA
## Importar el Proyecto
### 1. Clonar el Repositorio
```
git clone https://github.com/Dragtsu/gestion_calificaciones.git
cd alumnos
```
### 2. Importar en IntelliJ IDEA
1. Abrir IntelliJ IDEA
2. File → Open
3. Seleccionar la carpeta del proyecto
4. IntelliJ detectará automáticamente el proyecto Maven
5. Esperar a que Maven descargue las dependencias
### 3. Configurar el JDK
1. File → Project Structure → Project
2. SDK: Seleccionar Java 22
3. Language Level: 22
## Compilar y Ejecutar
### Desde Terminal
```bash
# Compilar el proyecto
mvn clean package
# Ejecutar la aplicación
java -jar target/Alumnos-1.0-SNAPSHOT.jar
```
### Desde IntelliJ IDEA
- Ejecutar: Clic derecho en AlumnosApplication.java → Run
- Debug: Clic derecho en AlumnosApplication.java → Debug
## Estructura del Proyecto
Arquitectura Hexagonal (Ports & Adapters):
- **domain/**: Entidades y lógica de negocio
  - **port/in/**: Interfaces de casos de uso
  - **port/out/**: Interfaces de repositorios
- **application/**: Implementación de casos de uso
- **infrastructure/**: Adaptadores e implementaciones
  - **adapter/in/ui/**: Controladores JavaFX
  - **adapter/out/persistence/**: Repositorios JPA
## Tecnologías
- JavaFX 21.0.1
- Spring Boot 3.2.1
- Hibernate 6.4.1
- SQLite 3.44.1
- Apache POI 5.2.5
- Maven
## Base de Datos
- Motor: SQLite
- Archivo: alumnos.db (se crea automáticamente)
- ORM: Hibernate/JPA
## Notas
- La base de datos (*.db) NO está en el repositorio
- La carpeta target/ NO está en el repositorio
- Las plantillas Word SÍ están en el repositorio
- Todos los recursos (FXML, CSS, iconos) SÍ están en el repositorio
---
Última actualización: Febrero 2026
