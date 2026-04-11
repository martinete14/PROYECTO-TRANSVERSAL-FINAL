# MINI ACADEMIA - ANÁLISIS TÉCNICO Y ESTRATÉGICO DEL PROYECTO
## Documento de Defensa Académica y Análisis Empresarial

**Autor:** Martín Villagra Tejerina  
**Curso:** 1º DAW (Desarrollo de Aplicaciones Web)  
**Año:** 2026  
**Institución:** Ilerna  
**Tipo de Documento:** Análisis Arquitectónico + Justificación Empresarial + Perspectiva de Marketing

---

## ÍNDICE
1. [Introducción Ejecutiva](#introducción-ejecutiva)
2. [Visión Empresarial y Contexto](#visión-empresarial-y-contexto)
3. [Análisis Estructural del Proyecto](#análisis-estructural-del-proyecto)
4. [Carpetas y Componentes - Análisis Profundo](#carpetas-y-componentes---análisis-profundo)
5. [Decisiones Arquitectónicas Clave](#decisiones-arquitectónicas-clave)
6. [Debilidades del Proyecto Actual](#debilidades-del-proyecto-actual)
7. [Fortalezas y Decisiones Acertadas](#fortalezas-y-decisiones-acertadas)
8. [Mejoras Futuras - Roadmap Empresarial](#mejoras-futuras---roadmap-empresarial)
9. [Perspectiva de Marketing](#perspectiva-de-marketing)
10. [Conclusión y Reflexión Final](#conclusión-y-reflexión-final)

---

## INTRODUCCIÓN EJECUTIVA

Mini Academia es una plataforma web de gestión y distribución de contenido educativo en línea. En términos técnicos, es una aplicación Spring Boot que permite:
- Crear, editar y eliminar cursos
- Organizar cursos en categorías temáticas
- Visualizar contenido con multimedia (imágenes y videos)
- Gestionar inscripciones de usuarios

Desde una perspectiva empresarial, la plataforma intenta resolver el problema de **centralización y democratización del acceso a contenido educativo de calidad**, permitiendo que expertos y profesionales puedan compartir su conocimiento sin necesidad de infraestructura técnica propia.

---

## VISIÓN EMPRESARIAL Y CONTEXTO

### Problema que Resuelve
En el mercado actual (2026), existen tres segmentos dispuestos a pagar por educación online:
1. **Usuarios individuales** que buscan formación específica en tiempo real
2. **Empresas** que necesitan entrenar equipos internos
3. **Instructores independientes** que quieren monetizar su experiencia sin crear su propia plataforma

Mini Academia se posiciona como **solución de punto de entrada bajo** para este último grupo, permitiendo que un instructor con cero conocimiento técnico pueda publicar cursos.

### Modelo de Negocio Implícito
- **B2C:** Usuarios pagan por acceso a cursos (curre en la app actual con inscripciones)
- **B2B2C:** Instructores pagan comisión por participación en ingresos
- **Freemium:** Cursos gratuitos para adquirir masa crítica

**Estado Actual:** El modelo está presente en código (tabla Inscripción) pero NO monetizado aún.

---

## ANÁLISIS ESTRUCTURAL DEL PROYECTO

### Árbol de Directorios y Responsabilidades

```
src/main/
├── java/com/example/demo/
│   ├── DemoApplication.java           [CORE] Punto de entrada
│   ├── config/                        [CONFIGURACIÓN] Lógica de arranque
│   ├── controller/                    [PRESENTACIÓN] Interacción web
│   ├── model/                         [DOMINIO] Estructuras de datos
│   ├── repository/                    [ACCESO A DATOS] ORM/JPA
│   ├── service/                       [LÓGICA EMPRESARIAL] Orquestación
│   └── exception/                     [MANEJO DE ERRORES] Excepciones custom
└── resources/
    ├── application.properties         [CONFIGU EXTERNA] Variables de entorno
    ├── static/                        [CONTENIDO ESTÁTICO] CSS, JS
    └── templates/                     [VISTAS] HTML con Thymeleaf

docs/                                 [DOCUMENTACIÓN] Diagramas y análisis
target/                               [COMPILADOS] Generados por Maven (ignorar)
uploads/                              [ALMACENAMIENTO LOCAL] Archivos del usuario
```

### Patrón Arquitectónico: Modelo de Capas

La app sigue el patrón clásico de **3 capas (con extensión)**:

```
┌─────────────────────────────────────────────┐
│        CAPA DE PRESENTACIÓN (VIEW)          │
│   Templates HTML + CSS (Thymeleaf + BS5)   │
│        Controllers Web MVC                  │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│        CAPA DE LÓGICA EMPRESARIAL           │
│   Services → Reglas de negocio             │
│   Validaciones → Coherencia de datos       │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│        CAPA DE ACCESO A DATOS (DAO)         │
│   Repositories (JPA) → BD                  │
│   Entidades del Dominio (Models)           │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│          CAPA DE PERSISTENCIA               │
│        Base de Datos (MySQL)               │
└─────────────────────────────────────────────┘
```

**Ventaja:** Separación de responsabilidades clara.  
**Desventaja:** Más verboso para un proyecto pequeño.

---

## CARPETAS Y COMPONENTES - ANÁLISIS PROFUNDO

### 1. CONFIG/

#### 1.1 DataSeeder.java

**¿Qué hace?**
Implementa `CommandLineRunner`, una interfaz de Spring que ejecuta código **al arrancar la aplicación**, una sola vez.
Carga automáticamente:
- 10 categorías temáticas (Ciencia, Humor, Educación, Aviación, etc.)
- 10 cursos semilla con datos de 10 personajes conocidos (Maldacena, Rosa Montero, etc.)

**Código Clave:**
```java
@Component
public class DataSeeder implements CommandLineRunner {
    @Override
    public void run(String... args) {
        // Carga inicial de datos
    }
}
```

**¿Es Necesario?**

| Perspectiva | Respuesta | Justificación |
|---|---|---|
| **Técnica** | No obligatorio | La app funciona sin datos; se crean por formulario |
| **Académica** | Sí recomendado | Mejora demostración y experiencia de usuario |
| **Empresarial** | Depende | En producción: NO. En desarrollo/demo: SÍ |

**Importancia: ⭐⭐⭐⭐ (ALTA para presentación)**

**Función Secundaria - Limpieza Inteligente:**
Detecta y elimina cursos "legacy" (versiones antiguas sin acentos) para evitar duplicados.
```java
Set<String> titulosLegacy = new HashSet<>(Arrays.asList(...));
cursoRepository.deleteAll(cursosLegacy);
```

**Alternativas:**
1. **Base SQL poblada de inicio** (scripts .sql en carpeta database/)
2. **Fixtures en sistema de tests** (menos visible para usuario)
3. **Panel manual de "Cargar Demo Data"** (requiere más UI)

**Por qué se eligió DataSeeder:**
- Automático: sin intervención manual
- Visible en código: transparencia académica
- Fácil de desactivar (if statement por propiedad)
- Patrón Spring estándar

**Crítica y Mejora Futura:**
- **Debilidad:** No es configurable sin modificar código
- **Mejora:** Leer datos de archivo JSON/YAML en resources
- **Empresarial:** En producción, desactivar completamente y alimentar via API

---

#### 1.2 StaticResourceConfig.java

**¿Qué hace?**
Configura Spring para que archivos en `uploads/` (imágenes, videos subidos) sean accesibles por URL web.

**Problema que Resuelve:**
Sin esta clase, cuando subes un video a `uploads/videos/mi-video.mp4`, el navegador da 404.

**Código:**
```java
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absoluteUploadPath + "/");
    }
}
```

**Traducción Conceptual:**
"Spring, cuando alguien pida /uploads/algo.mp4, ve a la carpeta `uploads/algo.mp4` en disco y sírvelo."

**¿Es Necesario?**

| Caso | Necesario | Razón |
|---|---|---|
| Archivos dentro de `src/main/resources/static/` | NO | Spring los sirve automáticamente |
| Archivos subidos a `uploads/` (carpeta fuera de código) | **SÍ** | Necesita configuración explícita |
| Imagen de video en la BD | SÍ | Solo guarda ruta, no el archivo |

**Importancia: ⭐⭐⭐⭐⭐ (CRÍTICA si tienes uploads)**

**Alternativas:**
1. **Guardar archivos en `src/main/resources/static/`** → Recompilación necesaria, no escalable
2. **Servir desde CDN externo** → Sin uploads locales, más costo
3. **Almacenamiento en nube (AWS S3, Azure Blob)** → Escalable, más complejo (empresa)

**Por qué se eligió esta approach:**
- Simplicidad: desarrollo local sin dependencias externas
- Educativo: aprendes cómo Spring maneja archivos
- Flexible: cambias ruta en `application.properties` sin tocar código

**Crítica:**
- **Debilidad:** No valida MIME type ni seguridad de archivos
- **Riesgo:** Sin sanitización, podrías servir archivos peligrosos
- **Mejora:** Validar tipo de archivo en upload

**Empresarial - Producción:**
```properties
# Cambiar a:
app.upload.storage-type=s3
aws.s3.bucket=miniacademia-videos
```

---

### 2. CONTROLLER/

#### WebController.java

**Responsabilidad:** Gestionar rutas HTTP, recibir datos del usuario y devolver vistas.

**Métodos Principales:**
- `GET /web/cursos` → Lista todos los cursos
- `GET /web/cursos/nuevo` → Formulario crear
- `POST /web/cursos/crear` → Procesa creación
- `POST /web/cursos/eliminar/{id}` → Borra curso
- `GET /web/cursos/editar/{id}` → Formulario editar
- `POST /web/cursos/actualizar/{id}` → Procesa edición

**Aspectos Interesantes:**

1. **Upload Seguro de Archivos:**
```java
validateImageFile(imagenFile);
validateVideoFile(videoFile);
```
Valida tamaño (5 MB imagen, 120 MB video) y tipo MIME.

2. **UUID para Nombres de Archivo:**
```java
String fileName = UUID.randomUUID() + extension;
```
Evita que dos usuarios suban archivos con mismo nombre.

3. **Fallback de URL:**
```java
String imagenFinal = resolveMediaUrl(imagenUrl, imagenFile, imageUploadDir, "/uploads/images/");
```
Acepta URL externa O archivo local: flexible.

**Crítica:**
- **Falta:** Sin autenticación, cualquiera puede crear cursos
- **Falta:** Sin autorización, puedes editar cursos ajenos
- **Mejora:** Agregar `@PreAuthorize` + roles de usuario

#### CursoController.java

**Responsabilidad:** API REST para operaciones CRUD en cursos (usado por JS, móviles, etc.).

**Endpoints:**
- `GET /api/cursos` → JSON de todos
- `GET /api/cursos/{id}` → JSON curso específico
- `GET /api/cursos/categoria/{id}` → JSON por categoría
- `POST /api/cursos` → Crear
- `PUT /api/cursos/{id}` → Actualizar
- `DELETE /api/cursos/{id}` → Eliminar

**Importante:** Esta clase es **agnóstica de la presentación** → los datos XML/JSON se generan automáticamente por Spring.

---

### 3. SERVICE/

**Patrón:** Una clase `*Service` por entidad principal.

#### CursoServiceImpl.java

**¿Por qué no está directamente en el Controller?**

**MÁS MALO (sin service):**
```java
@PostMapping("/crear")
public String crear(@RequestBody Curso curso) {
    if (curso.getCategoria() == null) 
        throw new RuntimeException("Categoría obligatoria");
    // ... 50 líneas más de lógica de negocio...
    cursoRepository.save(curso);
    return "redirect:/web/cursos";
}
```

**MEJOR (con service):**
```java
@PostMapping("/crear")
public String crear(@RequestBody Curso curso) {
    cursoService.crearCurso(curso);  // Delega
    return "redirect:/web/cursos";
}

// En CursoServiceImpl:
public Curso crearCurso(Curso curso) {
    validateCurso(curso);           // Validación
    Categoria cat = mapearCategoria(curso);  // Transformación
    return cursoRepository.save(curso);      // Persistencia
}
```

**Ventajas:**
1. **Testeable:** Puedes testear lógica sin HTTP
2. **Reutilizable:** Usas el mismo servicio desde Controller y API REST
3. **Mantenible:** Cambios lógica en un lugar

---

### 4. MODEL/

**Definición:** Entidades del dominio (Curso, Usuario, Inscripción, Categoría).

Cada una es una tabla en BD con validaciones JPA:
```java
@NotBlank(message = "El título es obligatorio")
private String titulo;
```

**Nota de Diseño:**
Hay un DTO `CursoDTO` separado de la entidad `Curso`.

**¿Por qué?**
- **Entidad:** Lo que está en BD (con relaciones complejas)
- **DTO:** Lo que envías al cliente (solo campos necesarios)

```java
// Entidad (BD):
Curso {
    Long id
    String titulo
    Categoria categoria  ← Relación, carga la categoría entera
}

// DTO (API):
CursoDTO {
    Long id
    String titulo
    Long categoriaId     ← Solo ID, más ligero
    String categoriaNombre
}
```

**Importancia:** ⭐⭐⭐⭐ (buena práctica, no obligatorio)

---

### 5. REPOSITORY/

Interfaces que extienden `JpaRepository<Entidad, ID>`.

```java
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByCategoriaId(Long categoriaId);
}
```

**¿Magia?** Spring genera automáticamente:
- `findAll()`
- `findById()`
- `save()`
- `delete()`
- Consultas personalizadas como `findByCategoriaId()`

**Alternativa:** Escribir SQL puro en clases DAO (más control, menos automatización).

---

### 6. EXCEPTION/

**GlobalExceptionHandler.java**

Atrapa excepciones no controladas y devuelve mensajes amables:

```java
@ExceptionHandler(RuntimeException.class)
public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
    return ResponseEntity.status(500)
        .body(new ErrorResponse(ex.getMessage()));
}
```

**Importancia:** ⭐⭐⭐ (mejora UX, no crítico)

---

### 7. RESOURCES/

#### application.properties

**Qué es:** Variables de entorno de la app.

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/miniacademy
app.upload.base-dir=uploads
spring.servlet.multipart.max-file-size=120MB
```

**Por qué separado del código:**
- Cambias BD sin recompilar
- Valores distintos por entorno (dev vs. producción)
- Seguridad: no hardcodear credenciales

#### static/ y templates/

- **static/:** Archivos que nunca cambian (CSS, JS, imágenes de bootstrap)
- **templates/:** HTML dinámico (Thymeleaf procesa `${variables}`)

---

## DECISIONES ARQUITECTÓNICAS CLAVE

### 1. Uso de Spring Boot (Framework Completo)

**Pro:**
- Todo incluido (web, BD, validación, etc.)
- Comunidad enorme
- Curva de aprendizaje bien documentada

**Contra:**
- "Overkill" para un proyecto pequeño
- Consume recursos (memoria)
- Asume estructura específica

**Alternativa:** Flask (Python), Express (Node.js) → más livianos, menos convención.

**Decisión:** **ACERTADA** para proyecto académico (industria estándar).

---

### 2. Uso de JPA/Hibernate (ORM)

**Qué es:** Mapeo automático BD ↔ Objetos Java.

**Pro:**
- Cambias BD sin reescribir consultas (portabilidad)
- Menos SQL memorizado
- Validaciones automáticas

**Contra:**
- Queries N+1 (carga relaciones innecesariamente)
- Menos control fino sobre BD
- Debug más difícil

**Alternativa:** JDBC puro → más control, más código.

**Decisión:** **PARCIALMENTE ACERTADA** → JPA es estándar, pero el DataSeeder con normalizadores es over-engineered.

---

### 3. Patrón MVC con Thymeleaf

**Qué es:** Renderizar HTML en servidor, enviar HTML completo al cliente.

**Pro:**
- SEO nativo (HTML en respuesta HTTP)
- Menos JS necesario
- Más rápido para conexiones lentas

**Contra:**
- No es SPA (Single Page Application)
- Experiencia menos fluida
- Escalabilidad limitada

**Alternativa:** React/Vue.js + API REST pura.

**Decisión:** **ACERTADA para 1º DAW** → enseña fundamentos web, no abstrae complejidad en JS.

---

## DEBILIDADES DEL PROYECTO ACTUAL

### Crítica Sincera y Constructiva

#### 1. **Seguridad: Falta de Autenticación**
- Cualquiera accede a `/web/cursos/crear` y puede crear cursos.
- Cualquiera puede eliminar cursos de otros.
- **Impacto:** Alto (proyecto demo, baja para producción).
- **Solución:** Agregar Spring Security con roles de usuario.

#### 2. **Modelo de Inscripción Incompleto**
- Tabla `Inscripcion` existe pero no se usa en vistas ni API.
- No hay forma de inscribirse desde UI.
- **Impacto:** Medio (funcionalidad fantasma).
- **Solución:** Implementar formulario de inscripción y dashboard de usuario.

#### 3. **Normalización de Categorías Over-Engineered**
```java
private String normalizarTexto(String valor) {
    // 15 líneas para evitar duplicados "Educacion" vs "Educación"
}
```
- Soluciona problema real (acentos) pero es complejo.
- Alternativa más simple: constraint UNIQUE en BD con collation case-insensitive.
- **Impacto:** Bajo (funciona bien, pero code smell).

#### 4. **Sin Paginación**
- Si hay 1.000 cursos, carga todos en memoria.
- **Impacto:** Bajo hoy, crítico en escala.
- **Solución:** `Pageable` de Spring Data.

#### 5. **Validación de Video/Imagen Solo en Controller**
- Si modificas BD directamente, no valida.
- **Solución:** Validadores en modelo (anotaciones JPA).

#### 6. **Test Coverage Bajo**
- Solo test básico que app arranca.
- **Impacto:** Riesgo en refactorización.

#### 7. **Sin Caché**
- Cada solicitud lee categorías de BD.
- **Solución:** `@Cacheable` de Spring.

#### 8. **Manejo de Errores Genérico**
- Excepciones técnicas muestran al usuario.
- **Mejora:** Mensajes amigables específicos.

---

## FORTALEZAS Y DECISIONES ACERTADAS

### Aspectos Positivos

#### 1. **Separación de Capas Clara**
- Controller, Service, Repository, Model muy bien diferenciados.
- Fácil de mantener y entender.
- ⭐ **Patrón profesional.**

#### 2. **Almacenamiento de Archivos Inteligente**
- UUID para evitar colisiones.
- Soporte para URL externa O archivo local.
- Validación de tamaño y MIME type.
- ⭐ **Práctico y escalable.**

#### 3. **Data Seeding Automático**
- Demo lista para usar sin datos manuales.
- Mejora experiencia académica.
- ⭐ **Buena decisión para presentación.**

#### 4. **DTO vs Entidad**
- Separa modelo interno de modelo de presentación.
- Evita exposición innecesaria de datos.
- ⭐ **Buena práctica.**

#### 5. **Configuración Externa**
- `application.properties` permite cambios sin recompilación.
- Fácil adaptar por entorno.
- ⭐ **Escalable.**

#### 6. **Multimedia: Preload None**
```html
<video preload="none">  ← No carga video hasta hover
```
- Optimiza carga inicial.
- Buena UX.
- ⭐ **Performance consciente.**

#### 7. **Acentos Correctos**
- Todas las descripciones con tildes (Juan Martín, Educación, etc.).
- Genera confianza en calidad.
- ⭐ **Detalle que importa.**

---

## MEJORAS FUTURAS - ROADMAP EMPRESARIAL

### Fase 1: Seguridad y Control (Trimestre 1)

```
OBJETIVO: Hacer app segura y multiusuario

1. Spring Security + Roles
   - Usuario: puede ver cursos, inscribirse
   - Instructor: puede crear/editar sus cursos
   - Admin: control total
   Esfuerzo: 1-2 semanas

2. Autenticación OAuth2
   - Login con Google/GitHub
   - No guardar contraseñas en BD
   Esfuerzo: 1 semana

3. Auditoría
   - Logs de quién hizo qué y cuándo
   - Cumplimiento legal
   Esfuerzo: 3 días
```

### Fase 2: Experiencia de Usuario (Trimestre 2)

```
1. Dashboard de Alumno
   - Ver mis cursos inscritos
   - Progreso: qué videos he visto
   - Certificados

2. Dashboard de Instructor
   - Analytics: cuántos alumnos, cuántos vieron cada video
   - Ingresos por curso

3. Search + Filtros Avanzados
   - Buscar por título, categoría, nivel
   - Ratings de cursos
```

### Fase 3: Escalabilidad (Trimestre 3-4)

```
1. Backend Async
   - Conversión de videos a background job
   - Env emails sin bloquear request

2. BD Distribuida
   - Replicación para redundancia
   - Read replicas para analytics

3. CDN para Multimedia
   - Videos en Cloudflare/Akamai
   - Imágenes optimizadas automáticamente
   Impacto: 10x más rápido globally

4. Microservicios (largo plazo)
   - Videos → servicio dedicado
   - Pagos → Stripe API service
   - Emails → SendGrid service
```

### Fase 4: Monetización (Continuo)

```
1. Integración Stripe/PayPal
   - Pago al crear inscripción
   - Comisión automática a instructor

2. Suscripción Mensual
   - Acceso ilimitado a cursos
   - Costo recurrente bajo

3. Certificados Verificables
   - Blockchain (verificabilidad futura)
   - Validación por universidades

4. API Pública
   - Otros sitios integran cursos de Mini Academia
   - Modelo SaaS B2B2C
```

---

## PERSPECTIVA DE MARKETING

### Propuesta de Valor

**Para Usuario Final:**
> "Aprende de los mejores sin salir de casa. Desde Juan Martín Maldacena explicando el universo hasta Rosa Montero desglosando literatura. 10 cursos, 0 esperas."

**Para Instructor:**
> "Publica tus cursos en 5 minutos. Sin código, sin hosting, sin gestión. Gana dinero mientras duermes."

**Para Empresa:**
> "Plataforma de capacitación interna. Cero fricción: subes video, genera enlace, equipo accede. Tracking de progreso incluido."

### Diferencial vs. Competencia

| Aspecto | Mini Academia | Udemy | MiCurso Propio | Teachable |
|---|---|---|---|---|
| **Setup Time** | 5 min | 30 min | 2-3 días | 1 día |
| **Precio** | Gratuito hoy | Comisión 50% | Licencia $300/mes | Desde $29/mes |
| **Contenido Especializado** | Sí (personajes famosos) | Masivo genérico | Limitado | Neutral |
| **Curva Aprendizaje** | Nula | Media | Alta | Media |

**Ventaja:** Posicionamiento como **plataforma de especialistas colaborativa.**

### Estrategia Go-to-Market

**MVP (Producto Mínimo Viable) - Ya existe:**
- Cursos, categorías, inscripciones
- Multimedia embebida
- Interfaz limpia

**Fase 1 - Adquirir Usuarios Iniciales:**
1. Convencer a 10 expertos conocidos para publicar cursos (ej: personajes del dataset)
2. Social proof: "Juan Martín Maldacena enseña física en Mini Academia"
3. Press release en medios educativos
4. Estrategia viral: cada instructor invita 5 personas

**Fase 2 - Monetizar:**
1. Comisión 30% por suscripciones (70% para instructor)
2. Plan Pro para instructores ($9/mes) → analytics avanzados
3. B2B: acceso empresarial por equipos

**KPIs a Seguir:**
- Users activos mensuales (MAU)
- Curso completion rate
- Revenue per course
- Instructor lifetime value

### Mensajería

**Hero Message:**
"La academia está en tu bolsillo. Del espacio a la cocina, aprende con pasión."

**Para Press:**
"Mini Academia democratiza educación premium: Maldacena, Montero, Villagra comparten expertise en plataforma de acceso libre."

---

## CONCLUSIÓN Y REFLEXIÓN FINAL

### Evaluación Global

Mini Academia es un **prototipo funcional y bien arquitecturado** para un proyecto de 1º DAW. Demuestra conocimieto de:

✅ **Técnico:**
- Arquitectura en capas
- ORM (Hibernate/JPA)
- Framework moderno (Spring Boot)
- CSS/HTML responsivo
- Gestión de archivos
- Validaciones

✅ **Académico:**
- Separación de responsabilidades
- Patrones de diseño reales
- Convenciones de la industria

⚠️ **Mejorables:**
- Falta seguridad (auth, autorización)
- Modelo incompleto (Inscripción sin usar)
- Test coverage bajo
- Over-engineering en algunos puntos

### Defensa en Tribunal

**Framing Recomendado:**

> "Mini Academia es una plataforma educativa que demuestra la arquitectura moderna de aplicaciones web. Implementé una separación clara de responsabilidades usando Spring Boot, JPA y Thymeleaf. El proyecto es escalable por diseño: soporta agregación de usuarios, gesión de multimedia, y está pensado para monetización futura. Las decisiones de arquitectura prioritizan mantenibilidad sobre simplicidad, porque un sistema educativo requiere robustez. Soy consciente de las limitaciones actuales de seguridad y experiencia, que son roadmap claro para la siguiente fase."

**Preguntas Posibles y Respuestas:**

**P: ¿Por qué tanto código si podrías hacerlo más simple?**  
R: "Porque la complejidad está contenida por arquitectura. Agregar usuarios, pagos, o reportes es ahora cuestión de agregar una capa, no reescribir todo."

**P: ¿DataSeeder no es over-engineering?**  
R: "Es un trade-off. Complica 50 líneas de código para mejorar 100% la experiencia de demostración. Es decisión deliberada."

**P: ¿Por qué Spring Boot y no algo más ligero?**  
R: "Porque Spring es industria estándar. Mi objetivo es aprender lo que usa el mercado laboral, no inventar."

**P: ¿Seguridad real?**  
R: "Es la debilidad conocida. Un MVP educativo necesita seguridad antes de producción. Está en roadmap de Fase 1."

---

## APÉNDICES

### A. Stack Tecnológico Detallado

| Capa | Tecnología | Versión | Justificación |
|---|---|---|---|
| **Backend** | Spring Boot | 4.0.5 | Framework estándar |
| **ORM** | Hibernate/JPA | Integrado | Abstracción BD |
| **BD** | MySQL | 8.0+ | Relacional, industria |
| **Frontend** | Thymeleaf | Spring estándar | Server-side rendering |
| **CSS** | Bootstrap 5 | 5.3.3 | Responsive rápido |
| **Build** | Maven | 3.8+ | Gestión dependencias |
| **Java** | OpenJDK | 21 | LTS vigente |

### B. Comandos Clave para Desarrollador

```bash
# Compilar y testear
mvnw.cmd -q test

# Ejecutar proyecto
mvnw.cmd spring-boot:run

# Compilar + crear JAR
mvnw.cmd clean package

# Conectar a BD MySQL
mysql -u root -p miniacademy
```

### C. Estructura de BD (Diagrama)

```
┌─────────────────┐         ┌──────────────────┐
│   CATEGORIA     │         │      CURSO       │
├─────────────────┤         ├──────────────────┤
│ id (PK)         │◄────────│ categoria_id (FK)│
│ nombre          │1      N │ id (PK)          │
│ descripcion     │         │ titulo           │
│                 │         │ descripcion      │
└─────────────────┘         │ instructor       │
                            │ imagenUrl        │
                            │ videoUrl         │
                            └──────────────────┘
                                    ▲
                                    │ 1
                                    │
                                    │ N
                            ┌──────────────────┐
                            │   INSCRIPCION    │
                            ├──────────────────┤
                            │ id (PK)          │
                            │ usuario_id (FK)  │
                            │ curso_id (FK)    │
                            │ fecha_inscr      │
                            │ estado           │
                            └──────────────────┘
                                    ▲
                                    │ 1
                                    │
                                    │ N
                            ┌──────────────────┐
                            │      USUARIO     │
                            ├──────────────────┤
                            │ id (PK)          │
                            │ email            │
                            │ nombre           │
                            │ password_hash    │
                            └──────────────────┘
```

### D. Cálculo de Complejidad Algorítmica

| Operación | Complejidad | Notas |
|---|---|---|
| Listar todos cursos | O(n) | Sin índices, carga lineal |
| Buscar por ID | O(1) | BD indexada por PK |
| Crear curso | O(1) | Insert simple |
| Listar por categoría | O(n) | Índice en categoria_id mejora a O(log n) |
| Buscar por full-text | O(n) | Sin búsqueda Elasticsearch |

**Mejora futura:** Agregar índice full-text para búsqueda rápida.

### E. Glosario Técnico

- **JPA:** Java Persistence API → Estándar para mapear objetos a BD
- **DTO:** Data Transfer Object → Versión simplificada de entidad para cliente
- **ORM:** Object-Relational Mapping → Traducción automática SQL ↔ Objetos
- **CommandLineRunner:** Interfaz que ejecuta código al arrancar Spring
- **Repository:** Interfaz para operaciones CRUD en BD
- **Service:** Lógica de negocio, orquestación
- **Controller:** Maneja HTTP, delega a Service
- **Thymeleaf:** Template engine para generar HTML dinámico
- **UUID:** Identificador único universal (evita colisiones)

---

## REFLEXIÓN FINAL PARA TU DEFENSA

Cuando presentes, no esperes que el tribunal entienda todo. **Tu objetivo es demostrar:**

1. **Que conoces qué hiciste y por qué**
2. **Que eres consciente de limitaciones y mejoras**
3. **Que pensaste en escalabilidad, no solo en hacer funcionar**

Un proyecto "simple pero bien hecho" vale más que uno "complejo y frágil".

Mini Academia está en la primera categoría.

**Buena suerte en tu defensa. 🚀**

---

**Documento Preparado por:** Sistema de Análisis de Proyectos académicos  
**Fecha:** 8 de abril de 2026  
**Para:** Defensa de Proyecto Final - 1º DAW
