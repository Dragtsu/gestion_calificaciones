# 📦 GENERAR EJECUTABLE - GUÍA RÁPIDA

## ✅ Método Más Fácil: Usar IntelliJ IDEA

### Paso 1: Abrir Panel Maven
1. En IntelliJ IDEA, ve a: **View** → **Tool Windows** → **Maven**
2. Se abrirá un panel en el lado derecho

### Paso 2: Generar el Ejecutable
1. En el panel Maven, expande **alumnos**
2. Expande **Lifecycle**
3. Haz doble clic en **clean** (espera a que termine)
4. Haz doble clic en **package** (espera a que termine)

### Paso 3: ¡Listo!
El archivo ejecutable se ha creado en:
```
target\Alumnos-1.0-SNAPSHOT.jar
```

---

## ▶️ Ejecutar la Aplicación

### Opción 1: Doble clic en el archivo BAT
```
start-production.bat
```

### Opción 2: Línea de comandos
```bash
java -jar target\Alumnos-1.0-SNAPSHOT.jar
```

---

## 📤 Distribuir a Otros Equipos

### Crear paquete de distribución:

1. **Crea una carpeta** llamada `Alumnos-App`

2. **Copia estos archivos** a la carpeta:
   ```
   Alumnos-App/
   ├── Alumnos-1.0-SNAPSHOT.jar  ← desde target/
   ├── start-production.bat
   ├── alumnos.db
   └── plantillas/
       └── concentrado_calificaciones.docx
   ```

3. **Comprime la carpeta** (Clic derecho → Comprimir)

4. **Envía el ZIP** a otros usuarios

### Requisitos para usuarios:
- Necesitan tener **Java 17 o superior** instalado
- Descargar desde: https://adoptium.net/

---

## 🎯 Resumen Visual

```
┌─────────────────────────────────────┐
│   IntelliJ IDEA - Panel Maven      │
├─────────────────────────────────────┤
│  📂 alumnos                        │
│    📂 Lifecycle                    │
│       🔄 clean     ← Click aquí   │
│       📦 package   ← Luego aquí   │
└─────────────────────────────────────┘
              ↓
        ✅ Se genera:
   target\Alumnos-1.0-SNAPSHOT.jar
```

---

## 📝 Notas Importantes

- El JAR incluye todas las dependencias necesarias (~80-100 MB)
- No necesitas configurar nada adicional
- La base de datos debe estar en el mismo directorio que el JAR
- Las plantillas deben estar en la carpeta `plantillas/`

---

## ❓ Problemas Comunes

**"Java no está instalado"**
→ Instala Java desde https://adoptium.net/

**"No se encuentra alumnos.db"**
→ Asegúrate de copiar el archivo de base de datos

**El JAR no ejecuta con doble clic**
→ Usa el archivo `start-production.bat`

---

¡Eso es todo! 🎉
