# Memoria tecnica - Lothar Courses

## 1. Descripcion del proyecto

Lothar Courses es una aplicacion web monolitica para catalogo, compra e imparticion de cursos online.
El sistema contempla flujos para tres roles (`ADMIN`, `INSTRUCTOR`, `CLIENTE`), con autenticacion por sesion, gestion de perfil academico, aula por curso y trazabilidad de acciones mediante auditoria.

Tematica asignada: plataforma de cursos online (Lothar Courses).

## 2. Objetivos del desarrollo

- Aplicar patron MVC y separacion por capas en un caso real.
- Consolidar persistencia relacional con reglas de integridad.
- Implementar autenticacion y autorizacion por rol.
- Construir experiencia web usable para usuario final y paneles de gestion.
- Incorporar robustez en carga multimedia y gestion de archivos.
- Mantener trazabilidad de acciones criticas con auditoria.

## 3. Tecnologias utilizadas

- Java 21
- Spring Boot 4.0.5
- Spring MVC + Thymeleaf
- Spring Data JPA
- Spring Validation
- Spring Security Crypto (BCrypt)
- MySQL (entorno principal)
- H2 (entorno de pruebas)
- Maven Wrapper
- Git/GitHub

## 4. Arquitectura de la aplicacion

Aplicacion monolitica por capas:

- Capa de presentacion:
  - Controladores MVC (`/web/**`)
  - Plantillas Thymeleaf y recursos estaticos
- Capa de negocio:
  - Servicios para reglas de cursos, inscripciones y auditoria
- Capa de persistencia:
  - Repositorios Spring Data JPA
- Capa de dominio:
  - Entidades JPA + DTOs de entrada/salida
- Capa de configuracion:
  - Seed inicial
  - Interceptor de autorizacion por rutas
  - Politica centralizada de permisos
  - Publicacion de recursos en `/uploads/**`

## 5. Modelo de datos actual

Entidades principales:

- `Categoria`
- `Curso`
- `Usuario`
- `Inscripcion`
- `CursoContenido`
- `AuditLog`

Relaciones clave:

- `Categoria` 1:N `Curso`
- `Usuario` 1:N `Inscripcion`
- `Curso` 1:N `Inscripcion`
- `Curso` 1:N `CursoContenido`

Entregables de BD:

- `docs/init.sql` (esquema completo)
- `docs/data.sql` (datos de ejemplo)
- `database/miniacademy.sql` (script historico)
- Modelo ER: `docs/diagrama-er.mmd`

## 6. Funcionalidades implementadas

### 6.1 Catalogo, compra y consumo

- Portada de cursos con destacados de la semana
- Filtro por categoria
- Vista de detalle de curso
- Compra/inscripcion de curso para clientes
- Vista de cursos adquiridos
- Aula por curso con contenido ordenado

### 6.2 Gestion por roles

- Panel ADMIN con filtros y gestion completa de cursos
- Panel INSTRUCTOR con gestion de sus cursos
- Marcado de cursos destacados
- Gestion de contenido de clase por curso (alta/baja)

### 6.3 Autenticacion, perfil y seguridad

- Login/logout con sesion HTTP
- Password con hash BCrypt
- Migracion transparente de password legacy al iniciar sesion
- Perfil editable con datos academicos y foto
- Identificador academico automatico por rol (`MA-EST-*`, `MA-PROF-*`, `MA-ADM-*`)
- Interceptor de autorizacion por rutas protegidas
- Vista de acceso denegado segun rol requerido

### 6.4 Auditoria y trazabilidad

- Registro de eventos de autenticacion y autorizacion
- Registro de acciones de gestion (cursos/contenidos)
- Panel de logs administrativo con filtros por actor/accion/limite

### 6.5 Multimedia y archivos

- Subida de imagen/video de curso por archivo o URL
- Subida de recursos de clase por archivo o URL
- Validacion de tipo y tamano (imagenes, videos y recursos de clase)
- Limpieza de archivos administrados al eliminar/actualizar
- Publicacion de ficheros locales via `/uploads/**`

## 7. Seed de datos para demo

`DataSeeder` garantiza usuarios base y sincroniza catalogo semilla.

Usuarios demo:

- `admin@LotharCourses.local` / `admin123`
- `instructor@LotharCourses.local` / `instructor123`
- `alumno.demo@LotharCourses.local` / `cliente123`

Comportamientos relevantes del seed:

- Fusion de categorias legacy a categorias objetivo
- Limpieza de duplicados por titulo normalizado
- Aplicacion de destacados semanales fijos

## 8. Testing y calidad tecnica

Suite de pruebas actual:

- `DemoApplicationTests`
- `controller/AuthControllerTest`
- `controller/WebControllerMvcCrudTest`
- `service/CursoServiceImplTest`
- `service/InscripcionServiceImplTest`
- `config/RoutePermissionPolicyTest`
- `config/RoleAuthorizationInterceptorTest`

Comando recomendado en Windows:

- `mvnw.cmd -q test`

## 9. Frontend y experiencia de usuario

Plantillas principales:

- `login.html`
- `cursos.html`
- `curso-detalle.html`
- `aula-curso.html`
- `admin-cursos.html`
- `admin-logs.html`
- `instructor-cursos.html`
- `crear-curso.html`
- `editar-curso.html`
- `curso-contenido-admin.html`
- `perfil.html`

Aspectos UX implementados:

- Interfaz responsive
- Paleta visual unificada
- Mensajeria de exito/error
- Navegacion condicionada por rol

## 10. Despliegue local

1. Ejecutar `docs/init.sql` en MySQL.
2. (Opcional demo) ejecutar `docs/data.sql`.
3. Revisar credenciales en `src/main/resources/application.properties`.
4. Ejecutar la aplicacion:
   - Windows: `mvnw.cmd spring-boot:run`
   - Linux/Mac: `./mvnw spring-boot:run`
5. Abrir:
   - `http://localhost:8081/web/auth/login`
   - `http://localhost:8081/web/cursos`

## 11. Alcance y limites de esta fase

- Incluye ejecucion local completa (Spring Boot + MySQL + carga de archivos).
- No incluye Docker ni docker-compose por alcance academico de la fase actual.

## 12. Diagramas y documentacion complementaria

- `docs/diagrama-clases.mmd`
- `docs/diagrama-er.mmd`
- `docs/ANALISIS_PROYECTO_LotharCourses.md`

## 13. Conclusiones

El proyecto evoluciono desde un CRUD basico hacia una plataforma academica con:

- seguridad por roles,
- paneles diferenciados,
- gestion de contenido de aula,
- perfil academico,
- y auditoria de operaciones.

Con esto se cubren requisitos de Programacion, Base de Datos, Entornos y presentacion de producto, manteniendo una base tecnica escalable para iteraciones futuras.
