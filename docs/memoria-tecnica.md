# Memoria Técnica — Lothar Courses

---

**Autor:** Martín Villagra Tejerina
**Ciclo:** 1.º DAW — Desarrollo de Aplicaciones Web
**Centro:** Ilerna
**Año académico:** 2025 / 2026
**Fecha de entrega:** Abril 2026

---

## 1. Descripción del proyecto

Lothar Courses es una plataforma web para cursos online. Pensada para que cualquiera pueda vender un curso sin saber programar.

Tiene tres tipos de usuario con permisos distintos:
- **Alumnos** que compran cursos
- **Instructores** que crean y venden sus cursos
- **Administradores** que gestionan todo

Cada usuario tiene un perfil, puede ver sus movimientos auditados y accede al sistema por sesión.

## 2. Lo que quería aprender haciendo esto

- Entender realmente cómo funciona MVC separando bien las capas
- Hacer una base de datos relacional de verdad, no juguetes
- Autenticar usuarios sin dejarme llevar por Spring Security (quería aprender cómo va por dentro)
- Que la app sea usable, tanto para clientes como para el panel de admin
- Subir archivos (imágenes, videos) de forma segura
- Auditar qué hace cada usuario (quién crea qué, quién accede a dónde)

---

## 3. Tecnologías utilizadas

| Tecnología | Versión / Rol |
|---|---|
| Java | 21 — lenguaje principal |
| Spring Boot | 4.0.5 — framework base |
| Spring MVC + Thymeleaf | Capa de presentación web |
| Spring Data JPA | Persistencia ORM |
| Spring Validation | Validación de formularios |
| Spring Security Crypto | Hash BCrypt para contraseñas |
| MySQL | Base de datos en entorno local |
| H2 | Base de datos en memoria para tests |
| Maven Wrapper | Gestión de dependencias y build |
| Git / GitHub | Control de versiones |

---

## 4. Proceso de desarrollo — paso a paso

### 4.1 Por dónde empecé

Generé un Spring Boot básico con Spring Initializr: Web, Thymeleaf y JPA.

En la primera versión solo hice un CRUD de cursos muy simple. Sin roles, sin seguridad, nada. Solo crear, leer, actualizar, borrar.

### 4.2 La base de datos

Decidí qué tablas necesitaba:

1. **Categoría** - para agrupar cursos por tema
2. **Curso** - lo central: título, descripción, imagen, video, precio, destacado o no
3. **Usuario** - personas con rol (admin, instructor o cliente)
4. **Inscripción** - quién compró qué curso (un usuario puede tener muchos cursos)
5. **CursoContenido** - el material dentro de cada aula (videos, PDFs, links)
6. **AuditLog** - registro de todo lo que pasó en el sistema (quién hizo qué, cuándo)

El esquema completo se recoge en `docs/init.sql`. Los datos de ejemplo para demostración están en `docs/data.sql`.

### 4.3 Cómo organicé el código

Separé todo en capas clásicas:

- **model/** - Las tablas de la BD como clases Java (entidades JPA)
- **repository/** - Clases que hablan con la base de datos
- **service/** - La lógica real: validaciones, inscripciones, archivos
- **controller/** - Los endpoints web que reciben peticiones
- **config/** - Configuración global, permisos, datos iniciales

### 4.4 Login y seguridad

No quería usar todo Spring Security porque es complicado. Hice lo mío:

- **Sesión HTTP** - El usuario inicia sesión y su rol se guarda en la sesión
- **BCrypt** - Las contraseñas se hashean, no se guardan en texto plano
- **Migración de contraseñas viejas** - Si alguien tiene una contraseña antigua, se convierte a BCrypt automáticamente al loguear
- **RoutePermissionPolicy** - Una clase que dice qué ruta necesita qué rol
- **RoleAuthorizationInterceptor** - Intercepta cada petición y verifica si tienes permiso. Si no, te manda a "acceso denegado"

### 4.5 Tres paneles, tres formas de usar

Cada rol ve algo diferente:

- **Alumnos** - Ven el catálogo, pueden filtrar, comprar cursos, ver los suyos y entrar al aula
- **Instructores** - Panel para ver sus cursos, crear nuevos, editar, gestionar el contenido de cada aula
- **Admin** - Ve todos los cursos de todos, puede crear/editar/borrar cualquiera, marcar destacados, y ver toda la auditoría

### 4.6 Subir archivos

Uno de los puntos más difíciles fue hacer que funcionen bien las subidas:

- Los archivos van a una carpeta `uploads/` fuera del código
- Valido el tipo y el tamaño antes de guardar
- Si eliminas un curso, se borra el archivo viejo también
- En `ResourceHandler` configuré que `/uploads/**` sirva archivos desde esa carpeta
- Pero también puedes meter una URL externa si prefieres

### 4.7 Perfil del usuario

Cada usuario tiene un ID único generado al registrarse, con prefijo según su rol:

- Alumnos: `MA-EST-XXXX`
- Instructores: `MA-PROF-XXXX`
- Admins: `MA-ADM-XXXX`

Desde su perfil pueden editar nombre, biografía y foto.

### 4.8 Auditoría - Quién hizo qué

Quería saber qué pasaba en el sistema, así que implementé un AuditLog que registra:

- Logins, logouts, accesos denegados
- Cuándo se crea, edita o borra un curso
- Quién agregó contenido a un aula

En el panel de admin puedes filtrar logs por usuario o tipo de acción.

### 4.9 Datos iniciales

Cuando arranca la app, una clase `DataSeeder` se ejecuta una sola vez y crea:

- Los tres usuarios de demo (admin, instructor, alumno)
- Las categorías de cursos
- Cursos de ejemplo con destacados configurados

Usuarios de acceso rápido para evaluación:

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin@LotharCourses.local` | `admin123` | ADMIN |
| `instructor@LotharCourses.local` | `instructor123` | INSTRUCTOR |
| `alumno.demo@LotharCourses.local` | `cliente123` | CLIENTE |

### 4.10 Tests

Escribí pruebas automatizadas con JUnit 5:

| Clase de test | Cobertura |
|---|---|
| `DemoApplicationTests` | Carga del contexto Spring |
| `AuthControllerTest` | Login, logout y acceso denegado |
| `WebControllerMvcCrudTest` | CRUD de cursos vía MVC |
| `CursoServiceImplTest` | Lógica de negocio de cursos |
| `InscripcionServiceImplTest` | Lógica de inscripciones |
| `RoutePermissionPolicyTest` | Política de permisos por ruta |
| `RoleAuthorizationInterceptorTest` | Interceptor de autorización |

Comando para ejecutar los tests en Windows:

```
mvnw.cmd -q test
```

---

## 5. Arquitectura de la aplicación

Aplicación monolítica Spring Boot con separación en capas:

```
src/main/
├── java/com/example/demo/
│   ├── controller/    ← Controladores MVC (/web/**)
│   ├── service/       ← Lógica de negocio
│   ├── repository/    ← Acceso a datos (Spring Data JPA)
│   ├── model/         ← Entidades JPA + DTOs
│   └── config/        ← Seed, interceptor, política de permisos, recursos estáticos
└── resources/
    ├── templates/     ← Plantillas Thymeleaf
    ├── static/        ← CSS, JS, imágenes estáticas
    └── application.properties
```

---

## 6. Modelo de datos

Entidades principales y sus relaciones:

- `Categoria` **1:N** `Curso`
- `Usuario` **1:N** `Inscripcion`
- `Curso` **1:N** `Inscripcion`
- `Curso` **1:N** `CursoContenido`
- `AuditLog` — entidad independiente vinculada al actor por nombre de usuario

Ficheros de base de datos incluidos en la entrega:

- `docs/init.sql` — esquema completo listo para importar
- `docs/data.sql` — datos de ejemplo para demo
- `docs/diagrama-er.mmd` — diagrama entidad-relación

---

## 7. Páginas principales

Las plantillas Thymeleaf que ves:

| Plantilla | Función |
|---|---|
| `login.html` | Acceso al sistema |
| `cursos.html` | Catálogo con filtros |
| `curso-detalle.html` | Ficha completa del curso |
| `aula-curso.html` | Aula con contenido ordenado |
| `admin-cursos.html` | Panel ADMIN de gestión |
| `admin-logs.html` | Panel de auditoría |
| `instructor-cursos.html` | Panel INSTRUCTOR |
| `crear-curso.html` / `editar-curso.html` | Formularios de curso |
| `curso-contenido-admin.html` | Gestión de clases |
| `perfil.html` | Perfil académico editable |

Aspectos de UX implementados:

- Interfaz responsive adaptada a escritorio y móvil.
- Paleta visual unificada (variables CSS).
- Mensajería de éxito/error por flash attributes.
- Navegación condicionada por rol (menús y acciones visibles solo si el usuario tiene permiso).

---

## 8. Cómo arrancar en tu PC

1. Importa el esquema en MySQL: ejecuta `docs/init.sql`
2. (Opcional) Carga datos de ejemplo: ejecuta `docs/data.sql`
3. Revisa las credenciales de BD en `src/main/resources/application.properties`
4. Arranca la app:
   - Windows: `mvnw.cmd spring-boot:run`
   - Linux/Mac: `./mvnw spring-boot:run`
5. Abre en el navegador:
   - http://localhost:8081/web/auth/login
   - http://localhost:8081/web/cursos

---

## 9. Limitaciones y futuro

- Corre completamente en local: Spring Boot + MySQL + archivos en disco
- Sin Docker ni despliegue a nube (está fuera del alcance de este curso)
- Las "compras" no son reales; el sistema simula inscripciones pero no procesa pagos

Si quisiera hacerlo en serio, habría que:
- Añadir una pasarela de pago (Stripe, PayPal)
- Mover archivos a un CDN (AWS S3, Azure Blob)
- Dockerizar y desplegar en la nube
- Añadir más escalabilidad (caché, base de datos en clusters)

---

## 10. Documentación complementaria

| Fichero | Contenido |
|---|---|
| `docs/diagrama-clases.mmd` | Diagrama de clases UML |
| `docs/diagrama-er.mmd` | Diagrama entidad-relación |
| `docs/ANALISIS_PROYECTO_MINIACADEMIA.md` | Análisis estratégico y empresarial |
| `README.md` | Guía rápida de arranque y funcionalidades |

---

## 11. Reflexión final

Este proyecto partió de un CRUD simple y evolucionó hasta ser una plataforma de cursos funcional:

- **Seguridad:** Cada rol ve solo lo que puede hacer
- **Confiabilidad:** El sistema sabe qué paso con cada acción (auditoría)
- **Escalabilidad:** La estructura por capas permite añadir features sin romper nada
- **Tests:** Hay pruebas automatizadas para lo crítico

Cubre programación, bases de datos, entornos de desarrollo e interfaces web. Es un proyecto de verdad, no algo hecho solo para la nota.
