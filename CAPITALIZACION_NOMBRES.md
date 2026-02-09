# Capitalización de Nombres de Alumnos

## Descripción del Cambio

Se implementó la funcionalidad para que al guardar un alumno desde el formulario, los campos de nombre, apellido paterno y apellido materno sean almacenados con la primera letra en mayúscula y las demás en minúsculas.

## Funcionalidad

- **Primera letra en mayúscula**: Cada campo se almacena con su primera letra capitalizada
- **Resto en minúsculas**: Las demás letras se convierten a minúsculas
- **Soporte para acentos**: Los caracteres con acento se preservan correctamente

## Ejemplos de Transformación

| Entrada | Salida |
|---------|--------|
| JOSÉ | José |
| maría | María |
| LÓPEZ | López |
| garcía | García |
| MARTÍNEZ | Martínez |
| ángel | Ángel |

## Implementación Técnica

### Ubicación del Código

- **Archivo**: `src/main/java/com/alumnos/application/service/AlumnoService.java`

### Métodos Implementados

1. **`normalizarNombres(Alumno alumno)`**: Método privado que aplica la capitalización a todos los campos de texto del alumno (nombre, apellido paterno, apellido materno)

2. **`capitalizarPrimeraLetra(String texto)`**: Método privado que realiza la capitalización de un texto individual

### Integración

La normalización se aplica automáticamente en:
- `crearAlumno()`: Al crear un nuevo alumno
- `actualizarAlumno()`: Al actualizar un alumno existente

Esto garantiza que todos los nombres se almacenen en el formato correcto independientemente de cómo sean ingresados en el formulario.

## Ventajas

1. **Consistencia**: Todos los nombres se almacenan en el mismo formato
2. **Mejora en la presentación**: Los nombres se ven profesionales y correctamente formateados
3. **Centralizado**: La lógica está en el servicio, garantizando que se aplique sin importar desde dónde se llame
4. **Soporte Unicode**: Maneja correctamente caracteres acentuados del español

## Fecha de Implementación

- **Fecha**: 2026-02-06
