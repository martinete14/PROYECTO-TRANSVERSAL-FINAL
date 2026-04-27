# Lothar Courses

Una plataforma donde instructores pueden vender cursos y alumnos pueden aprenderlos. Sin complicaciones técnicas.

Tiene tres tipos de usuario: alumnos (compran y aprenden), instructores (crean y venden), y admins (controlan todo).

## Tecnología

**Lenguaje:** Java 21  
**Framework:** Spring Boot 4.0.5  
**Base de datos:** MySQL (y H2 para tests)  
**Interfaz:** Thymeleaf + Bootstrap 5  
**Autenticación:** Sesión HTTP + BCrypt  
**Herramienta de build:** Maven

## Cómo está organizado

Separación clásica por capas:

- **controller/** - Las rutas y páginas web
- **service/** - La lógica real
- **repository/** - Acceso a base de datos
- **model/** - Las entidades
- **config/** - Configuración global
- **resources/templates/** - Las páginas HTML
- **resources/static/** - CSS, JS, imágenes

## Funcionalidades implementadas

### Catalogo y compra

- Catalogo de cursos con portada destacada semanal
- Filtro por categoria en catalogo
- Vista detalle de curso
- Compra/inscripcion de curso para clientes
- Vista de cursos adquiridos por el usuario autenticado

### Gestion academica por rol

- Panel ADMIN: listado, filtros, CRUD de cursos y marcado de destacados
- Panel INSTRUCTOR: gestion de sus cursos
- Aula por curso (`/web/cursos/aula/{cursoId}`)
- Gestion de contenido de clase por curso (crear/eliminar recursos)

### Perfil y autenticacion

- Login/logout con sesion HTTP
- Passwords almacenadas con BCrypt
- Migracion automatica de password legacy en texto plano al hacer login
- Perfil editable con datos academicos y foto de perfil
- Identificador academico por rol (`MA-EST-*`, `MA-PROF-*`, `MA-ADM-*`)

### Seguridad y auditoria

- Interceptor de autorizacion por rutas protegidas
- Politica de permisos centralizada por prefijo de URL
- Pantalla de acceso denegado con contexto de ruta y rol requerido
- Registro de auditoria para login, logout, accesos denegados y acciones de gestion
- Panel de logs para ADMIN con filtros

### Multimedia y robustez

- Carga de imagen/video de curso por archivo o URL
- Carga de recursos de clase por archivo o URL
- Validacion de tipo y tamano de archivos
- Limpieza de archivos gestionados al actualizar/eliminar contenido
- Publicacion de archivos en `/uploads/**`

## Base de datos

Motor principal: MySQL

Tablas actuales:

- `categoria`
- `curso`
- `usuario`
- `inscripcion`
- `curso_contenido`
- `audit_log`

Scripts SQL:

- `docs/init.sql` -> creacion de esquema completo
- `docs/data.sql` -> datos de ejemplo para demo
- `database/miniacademy.sql` -> script historico de base

## Seed de datos demo

Al arrancar, `DataSeeder` asegura usuarios base y carga catalogo semilla si corresponde.

Usuarios demo:

- `admin@LotharCourses.local` / `admin123`
- `instructor@LotharCourses.local` / `instructor123`
- `alumno.demo@LotharCourses.local` / `cliente123`

## Tests y calidad

Suite actual (7 clases):

- `DemoApplicationTests`
- `controller/AuthControllerTest`
- `controller/WebControllerMvcCrudTest`
- `service/CursoServiceImplTest`
- `service/InscripcionServiceImplTest`
- `config/RoutePermissionPolicyTest`
- `config/RoleAuthorizationInterceptorTest`

Ejecucion rapida en Windows:

- `mvnw.cmd -q test`

## Configuracion principal

Archivo: `src/main/resources/application.properties`

Puntos relevantes:

- MySQL en `localhost:3307`
- `spring.jpa.hibernate.ddl-auto=update`
- Puerto app: `8081`
- Directorios de upload configurables:
  - `app.upload.images-dir`
  - `app.upload.videos-dir`
  - `app.upload.profile-images-dir`
  - `app.upload.course-content-dir`

## Ejecucion local

1. Inicializar BD:
	- Ejecutar `docs/init.sql`
	- (Opcional demo) ejecutar `docs/data.sql`

2. Revisar credenciales en `src/main/resources/application.properties`

3. Ejecutar la app:
	- Windows: `mvnw.cmd spring-boot:run`
	- Linux/Mac: `./mvnw spring-boot:run`

4. Acceder:
	- `http://localhost:8081/web/auth/login`
	- `http://localhost:8081/web/cursos`

## Documentacion

- Memoria tecnica: `docs/memoria-tecnica.md`
- Analisis estrategico: `docs/ANALISIS_PROYECTO_LotharCourses.md`
- Diagrama ER: `docs/diagrama-er.mmd`
- Diagrama de clases: `docs/diagrama-clases.mmd`

## Alcance de la entrega

- Incluye despliegue local completo con Spring Boot + MySQL.
- No incluye Docker ni docker-compose en esta fase academica.
