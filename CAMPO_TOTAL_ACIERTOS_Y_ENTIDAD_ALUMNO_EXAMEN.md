# ✅ CAMPO TOTAL ACIERTOS Y ENTIDAD ALUMNO_EXAMEN AGREGADOS

## 📋 Resumen

Se ha agregado el campo "Total de aciertos de examen" al formulario de exámenes y se ha creado una nueva entidad `AlumnoExamen` para vincular explícitamente alumnos con exámenes.

---

## 🗂️ Archivos Modificados

### 1. Modelo de Dominio - Examen.java
**Ruta**: `src/main/java/com/alumnos/domain/model/Examen.java`

**Cambio realizado**:
- Agregado campo `totalAciertos` para almacenar el total de aciertos del examen

```java
private Integer totalAciertos;  // Total de aciertos del examen (máximo de aciertos posibles)
```

### 2. Entidad JPA - ExamenEntity.java
**Ruta**: `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/entity/ExamenEntity.java`

**Cambio realizado**:
- Agregada columna `total_aciertos` en la tabla `examenes`

```java
@Column(name = "total_aciertos")
private Integer totalAciertos;
```

### 3. Adaptador del Repositorio - ExamenRepositoryAdapter.java
**Ruta**: `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/repository/ExamenRepositoryAdapter.java`

**Cambios realizados**:
- Actualizado método `toEntity()` para incluir el campo `totalAciertos`
- Actualizado método `toDomain()` para incluir el campo `totalAciertos`

### 4. HomeController.java
**Ruta**: `src/main/java/com/alumnos/infrastructure/adapter/in/ui/controller/HomeController.java`

**Cambios realizados**:

#### A. Campo de Input para Total de Aciertos
- Agregado un `TextField` con el label "Total de aciertos de examen"
- Posicionado sobre la tabla de alumnos
- Limitado a máximo 2 dígitos
- Validación obligatoria antes de guardar

```java
// Campo para Total de Aciertos del Examen
javafx.scene.layout.HBox totalAciertosBox = new javafx.scene.layout.HBox(10);
totalAciertosBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

Label lblTotalAciertos = new Label("Total de aciertos de examen:");
lblTotalAciertos.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");

TextField txtTotalAciertos = new TextField();
txtTotalAciertos.setPromptText("00");
txtTotalAciertos.setPrefWidth(60);
txtTotalAciertos.setStyle("-fx-alignment: CENTER;");

// Limitar a máximo 2 dígitos
txtTotalAciertos.textProperty().addListener((obs, oldVal, newVal) -> {
    if (newVal != null && !newVal.matches("\\d{0,2}")) {
        txtTotalAciertos.setText(oldVal);
    }
});

totalAciertosBox.getChildren().addAll(lblTotalAciertos, txtTotalAciertos);
```

#### B. Validación al Guardar
- Validación de que el campo totalAciertos no esté vacío
- El valor se guarda en cada registro de examen

```java
// Validar que se haya ingresado el total de aciertos
String totalAciertosStr = txtTotalAciertos.getText();
if (totalAciertosStr == null || totalAciertosStr.trim().isEmpty()) {
    mostrarAlerta("Validación", "Debe ingresar el total de aciertos del examen", Alert.AlertType.WARNING);
    return;
}
```

#### C. Carga Automática del Total de Aciertos
- Al buscar exámenes guardados, se carga el totalAciertos en el campo de texto

```java
// Establecer el totalAciertos en el campo de texto
if (totalAciertosGuardado != null) {
    txtTotalAciertos.setText(String.valueOf(totalAciertosGuardado));
} else {
    txtTotalAciertos.setText("");
}
```

#### D. Guardado del Total de Aciertos
- Se incluye el totalAciertos al crear o actualizar exámenes

```java
Examen examen = Examen.builder()
    .alumnoId(alumno.getId())
    .grupoId(grupo.getId())
    .materiaId(materia.getId())
    .parcial(parcial)
    .aciertos(aciertos)
    .totalAciertos(totalAciertosExamen)
    .build();
```

---

## 🆕 Archivos Creados - Entidad AlumnoExamen

### 1. Modelo de Dominio - AlumnoExamen.java
**Ruta**: `src/main/java/com/alumnos/domain/model/AlumnoExamen.java`

**Descripción**:
- Modelo que representa la relación entre un alumno y un examen
- Campos: id, alumnoId, examenId, aciertos

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoExamen {
    private Long id;
    private Long alumnoId;
    private Long examenId;
    private Integer aciertos;  // Aciertos obtenidos por el alumno en este examen (0-99)

    // Campos opcionales para mostrar información
    private String nombreAlumno;
    private Integer numeroLista;
}
```

### 2. Entidad JPA - AlumnoExamenEntity.java
**Ruta**: `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/entity/AlumnoExamenEntity.java`

**Descripción**:
- Entidad JPA para persistencia en SQLite
- Tabla: `alumno_examen`
- Constraint único: (alumno_id, examen_id)

```java
@Entity
@Table(name = "alumno_examen",
    uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "examen_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoExamenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_id", nullable = false)
    private Long alumnoId;

    @Column(name = "examen_id", nullable = false)
    private Long examenId;

    @Column(nullable = false)
    private Integer aciertos;
}
```

### 3. Puerto de Entrada - AlumnoExamenServicePort.java
**Ruta**: `src/main/java/com/alumnos/domain/port/in/AlumnoExamenServicePort.java`

**Descripción**:
- Define contratos para el servicio de AlumnoExamen
- Métodos CRUD + consultas especiales

```java
public interface AlumnoExamenServicePort {
    AlumnoExamen crearAlumnoExamen(AlumnoExamen alumnoExamen);
    Optional<AlumnoExamen> obtenerAlumnoExamenPorId(Long id);
    List<AlumnoExamen> obtenerTodosLosAlumnoExamen();
    AlumnoExamen actualizarAlumnoExamen(AlumnoExamen alumnoExamen);
    void eliminarAlumnoExamen(Long id);
    List<AlumnoExamen> obtenerAlumnoExamenPorAlumno(Long alumnoId);
    List<AlumnoExamen> obtenerAlumnoExamenPorExamen(Long examenId);
    Optional<AlumnoExamen> obtenerAlumnoExamenPorAlumnoYExamen(Long alumnoId, Long examenId);
}
```

### 4. Puerto de Salida - AlumnoExamenRepositoryPort.java
**Ruta**: `src/main/java/com/alumnos/domain/port/out/AlumnoExamenRepositoryPort.java`

**Descripción**:
- Define contratos para el repositorio de AlumnoExamen
- Métodos de persistencia y consultas

```java
public interface AlumnoExamenRepositoryPort {
    AlumnoExamen save(AlumnoExamen alumnoExamen);
    Optional<AlumnoExamen> findById(Long id);
    List<AlumnoExamen> findAll();
    void deleteById(Long id);
    List<AlumnoExamen> findByAlumnoId(Long alumnoId);
    List<AlumnoExamen> findByExamenId(Long examenId);
    Optional<AlumnoExamen> findByAlumnoIdAndExamenId(Long alumnoId, Long examenId);
}
```

### 5. Servicio de Aplicación - AlumnoExamenService.java
**Ruta**: `src/main/java/com/alumnos/application/service/AlumnoExamenService.java`

**Descripción**:
- Implementa la lógica de negocio
- Servicio transaccional con Spring

```java
@Service
@Transactional
public class AlumnoExamenService implements AlumnoExamenServicePort {
    private final AlumnoExamenRepositoryPort alumnoExamenRepositoryPort;

    public AlumnoExamenService(AlumnoExamenRepositoryPort alumnoExamenRepositoryPort) {
        this.alumnoExamenRepositoryPort = alumnoExamenRepositoryPort;
    }

    // Implementación de todos los métodos del puerto
}
```

### 6. Repositorio JPA - AlumnoExamenJpaRepository.java
**Ruta**: `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/repository/AlumnoExamenJpaRepository.java`

**Descripción**:
- Interfaz Spring Data JPA
- Métodos de consulta personalizados

```java
@Repository
public interface AlumnoExamenJpaRepository extends JpaRepository<AlumnoExamenEntity, Long> {
    List<AlumnoExamenEntity> findByAlumnoId(Long alumnoId);
    List<AlumnoExamenEntity> findByExamenId(Long examenId);
    Optional<AlumnoExamenEntity> findByAlumnoIdAndExamenId(Long alumnoId, Long examenId);
}
```

### 7. Adaptador del Repositorio - AlumnoExamenRepositoryAdapter.java
**Ruta**: `src/main/java/com/alumnos/infrastructure/adapter/out/persistence/repository/AlumnoExamenRepositoryAdapter.java`

**Descripción**:
- Implementa el puerto de salida
- Convierte entre Entity y Domain Model

```java
@Component
public class AlumnoExamenRepositoryAdapter implements AlumnoExamenRepositoryPort {
    private final AlumnoExamenJpaRepository alumnoExamenJpaRepository;

    // Implementación de métodos con mapeo entre Entity y Domain
    private AlumnoExamenEntity toEntity(AlumnoExamen alumnoExamen) { ... }
    private AlumnoExamen toDomain(AlumnoExamenEntity entity) { ... }
}
```

---

## 🗄️ Estructura de la Base de Datos

### Tabla: `examenes` (Modificada)

| Columna        | Tipo    | Restricciones           |
|----------------|---------|-------------------------|
| id             | BIGINT  | PRIMARY KEY, AUTO_INCREMENT |
| alumno_id      | BIGINT  | NOT NULL                |
| grupo_id       | BIGINT  | NOT NULL                |
| materia_id     | BIGINT  | NOT NULL                |
| parcial        | INTEGER | NOT NULL                |
| aciertos       | INTEGER | NOT NULL                |
| **total_aciertos** | **INTEGER** | **NULL** |

**Constraint Único**: `(alumno_id, grupo_id, materia_id, parcial)`

### Tabla: `alumno_examen` (Nueva)

| Columna    | Tipo    | Restricciones           |
|------------|---------|-------------------------|
| id         | BIGINT  | PRIMARY KEY, AUTO_INCREMENT |
| alumno_id  | BIGINT  | NOT NULL                |
| examen_id  | BIGINT  | NOT NULL                |
| aciertos   | INTEGER | NOT NULL                |

**Constraint Único**: `(alumno_id, examen_id)`
- Garantiza que solo haya una relación por alumno/examen

---

## 🏗️ Arquitectura Hexagonal

```
┌─────────────────────────────────────┐
│   CAPA DE PRESENTACIÓN              │
│   HomeController                    │
│   - crearVistaExamenesCompleta()    │
│   - Campo txtTotalAciertos          │
│   - Validación de totalAciertos     │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   CAPA DE APLICACIÓN                │
│   ExamenService                     │
│   AlumnoExamenService               │
│   - crearAlumnoExamen()             │
│   - actualizarAlumnoExamen()        │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   CAPA DE DOMINIO                   │
│   Examen (Domain Model)             │
│   - totalAciertos (NUEVO)           │
│   AlumnoExamen (Domain Model - NUEVO)|
│   - alumnoId, examenId, aciertos    │
│   ExamenServicePort (Input Port)    │
│   AlumnoExamenServicePort (NUEVO)   │
│   ExamenRepositoryPort (Output Port)│
│   AlumnoExamenRepositoryPort (NUEVO)│
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   CAPA DE INFRAESTRUCTURA           │
│   ExamenEntity (JPA)                │
│   - totalAciertos (NUEVO)           │
│   AlumnoExamenEntity (JPA - NUEVO)  │
│   AlumnoExamenJpaRepository (NUEVO) │
│   AlumnoExamenRepositoryAdapter (NUEVO)|
└─────────────────────────────────────┘
```

---

## 🔗 Vínculos de la Entidad AlumnoExamen

### AlumnoExamen → Alumno
```java
private Long alumnoId;
```

### AlumnoExamen → Examen
```java
private Long examenId;
```

### AlumnoExamen → Aciertos
```java
private Integer aciertos;  // Aciertos obtenidos por el alumno en este examen
```

---

## 📝 Funcionalidad de la Interfaz

### Campo "Total de aciertos de examen"

1. **Ubicación**: Sobre la tabla de alumnos en la vista de exámenes
2. **Tipo**: Campo de texto (TextField) limitado a 2 dígitos
3. **Validación**: 
   - Solo acepta números (0-99)
   - Es obligatorio antes de guardar
4. **Comportamiento**:
   - Se carga automáticamente cuando se selecciona un examen guardado
   - Se guarda en cada registro de examen
   - Representa el total de aciertos posibles del examen

### Flujo de Uso

1. Usuario selecciona Grupo, Materia y Parcial
2. Presiona "Buscar" para generar la tabla
3. **Ingresa el total de aciertos del examen** en el campo nuevo
4. Ingresa los aciertos de cada alumno en la tabla
5. Presiona "Guardar Exámenes"
6. El sistema valida que el total de aciertos esté ingresado
7. Guarda los aciertos de cada alumno junto con el total de aciertos del examen

---

## 🚀 Estado de Implementación

| Componente | Estado | Descripción |
|------------|--------|-------------|
| Campo totalAciertos en Examen | ✅ Completo | Agregado a modelo de dominio |
| Campo totalAciertos en ExamenEntity | ✅ Completo | Agregado a entidad JPA |
| Mapeo en ExamenRepositoryAdapter | ✅ Completo | toEntity() y toDomain() actualizados |
| Campo de input en UI | ✅ Completo | TextField con validación |
| Validación al guardar | ✅ Completo | Verifica campo obligatorio |
| Carga automática | ✅ Completo | Carga valor al buscar |
| Modelo AlumnoExamen | ✅ Completo | Creado en dominio |
| AlumnoExamenEntity | ✅ Completo | Entidad JPA creada |
| AlumnoExamenServicePort | ✅ Completo | Puerto de entrada |
| AlumnoExamenRepositoryPort | ✅ Completo | Puerto de salida |
| AlumnoExamenService | ✅ Completo | Servicio implementado |
| AlumnoExamenJpaRepository | ✅ Completo | Repositorio JPA |
| AlumnoExamenRepositoryAdapter | ✅ Completo | Adaptador implementado |

---

## 📌 Notas Importantes

1. **Compatibilidad**: La columna `total_aciertos` en la tabla `examenes` acepta NULL para mantener compatibilidad con registros existentes
2. **Validación**: El campo es obligatorio solo al crear nuevos registros desde la interfaz
3. **Persistencia**: El total de aciertos se guarda en cada registro de examen, permitiendo que cada combinación de grupo/materia/parcial tenga su propio total
4. **Entidad AlumnoExamen**: Proporciona una relación explícita entre alumnos y exámenes, útil para consultas más complejas en el futuro
5. **Arquitectura**: Se sigue la arquitectura hexagonal del proyecto con separación de capas

---

## ✅ Verificación

Para verificar que los cambios funcionan correctamente:

1. Ejecutar la aplicación
2. Navegar a "Concentrado" → "Exámenes"
3. Seleccionar Grupo, Materia y Parcial
4. Verificar que aparece el campo "Total de aciertos de examen" sobre la tabla
5. Ingresar un valor (ej: 50) en el campo
6. Ingresar aciertos para los alumnos
7. Guardar y verificar que se guarda correctamente
8. Buscar el mismo examen y verificar que se carga el total de aciertos

---

**Fecha de implementación**: 2026-01-29
**Versión**: 1.0
**Estado**: ✅ Completado
