====================================
  Sistema de Gestion de Alumnos
====================================

Version: 1.0
Fecha: 05/02/2026

REQUISITOS:
-----------
- Windows 10 o superior
- Java 17 o superior instalado

INSTALACION DE JAVA (si no lo tienes):
---------------------------------------
1. Ve a: https://adoptium.net/
2. Descarga "Temurin 17 LTS" para Windows
3. Ejecuta el instalador
4. Reinicia el equipo

COMO EJECUTAR:
--------------
Opcion 1: Doble clic en "Alumnos.jar"
Opcion 2: Ejecutar "Iniciar-Alumnos.bat"
Opcion 3: Ejecutar "Iniciar-Alumnos.ps1"

PRIMERA VEZ:
------------
Al iniciar por primera vez:
1. Se abrira una ventana de configuracion inicial
2. Completa los datos solicitados (nombre de la escuela, etc.)
3. Haz clic en "Guardar"
4. La aplicacion creara automaticamente la base de datos

ARCHIVOS IMPORTANTES:
--------------------
- Alumnos.jar: Aplicacion principal
- alumnos.db: Base de datos (se crea automaticamente)
- plantillas/: Plantillas para informes Word
- Iniciar-Alumnos.bat: Script para iniciar en Windows

FUNCIONALIDADES:
----------------
- Gestion de alumnos y grupos
- Registro de calificaciones
- Calculo automatico de puntos por grupo
- Generacion de informes en Word
- Exportacion a Excel
- Interfaz grafica intuitiva

SOPORTE Y AYUDA:
----------------
Si tienes problemas al ejecutar:

1. Verifica que Java este instalado:
   Abre una terminal y ejecuta: java -version

2. Si el JAR no abre con doble clic:
   Abre una terminal en esta carpeta y ejecuta:
   java -jar Alumnos.jar

   Esto mostrara cualquier error que ocurra.

3. Asegurate de tener permisos de escritura en esta carpeta
   (la aplicacion necesita crear y modificar la base de datos)

RESPALDO DE DATOS:
------------------
Para hacer respaldo de tus datos, copia el archivo:
- alumnos.db

Para restaurar, solo reemplaza ese archivo.

ACTUALIZACIONES:
----------------
Para actualizar a una nueva version:
1. Haz respaldo de alumnos.db
2. Reemplaza Alumnos.jar con la nueva version
3. Ejecuta normalmente

====================================
Desarrollado con JavaFX y Spring Boot
====================================
