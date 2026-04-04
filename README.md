# Mini Academia - Proyecto Transversal DAW/DAM

Aplicacion web monolitica para la gestion de cursos online. Permite administrar cursos y categorias, consultar cursos por usuario e inscribir usuarios en cursos.

## Stack tecnologico

- Java 21
- Spring Boot (MVC + Spring Data JPA + Validation)
- Thymeleaf
- Bootstrap 5
- MySQL
- Maven

## Arquitectura

Se aplica arquitectura MVC en capas:

- Controladores: exponen endpoints web y REST
- Servicios: implementan logica de negocio
- Repositorios: acceso a datos con Spring Data JPA
- Modelos: entidades persistentes

Estructura principal:

- src/main/java/com/example/demo/controller
- src/main/java/com/example/demo/service
- src/main/java/com/example/demo/repository
- src/main/java/com/example/demo/model
- src/main/resources/templates

## Funcionalidades implementadas

- CRUD de cursos (crear, listar, editar, eliminar)
- Gestion de categorias asociadas a cursos
- Inscripcion de usuarios a cursos
- Consulta de cursos por categoria
- Consulta de cursos por usuario
- Carga multimedia por curso (imagen y video por URL o por archivo)
- Validacion de tipo y tamano de archivos multimedia
- Validaciones con Bean Validation
- Manejo basico de errores con GlobalExceptionHandler

## Base de datos

Motor: MySQL

Tablas relacionadas:

- categoria
- curso
- usuario
- inscripcion

Script SQL incluido en:

- database/miniacademy.sql

## Vistas y SEO basico

Plantillas con Thymeleaf y Bootstrap:

- cursos.html
- crear-curso.html
- editar-curso.html

Incluyen:

- Etiquetas semanticas (header, main, section, article, footer)
- title y meta description
- Jerarquia de encabezados
- Diseno responsive

## Calidad tecnica y pruebas

- Tests unitarios de servicios para reglas de negocio:
	- `CursoServiceImplTest`
	- `InscripcionServiceImplTest`
- Test de contexto de aplicacion (`DemoApplicationTests`)
- Configuracion de subida de archivos por propiedades:
	- `app.upload.base-dir`
	- `app.upload.images-dir`
	- `app.upload.videos-dir`
- Publicacion de multimedia mediante ruta `/uploads/**`

## Como ejecutar en local

1. Crear base de datos e inicializar esquema:

	- Ejecutar `database/miniacademy.sql` en MySQL

2. Configurar credenciales en `src/main/resources/application.properties`

3. Ejecutar la aplicacion:

	- En Windows: `mvnw.cmd spring-boot:run`
	- En Linux/Mac: `./mvnw spring-boot:run`

4. Abrir en navegador:

	- http://localhost:8081/web/cursos

## Documentacion y diagramas

- Memoria tecnica: docs/memoria-tecnica.md
- Diagrama ER: docs/diagrama-er.mmd
- Diagrama de clases: docs/diagrama-clases.mmd

## Estado actual

Proyecto preparado para la parte de Programacion, Base de Datos, Lenguaje de Marcas/SEO y Entornos de Desarrollo.

Alcance acordado para esta entrega:

- Se incluye ejecucion local completa (Spring Boot + MySQL).
- No se incluye Docker ni docker-compose porque ese contenido no fue impartido en clase en esta fase.
