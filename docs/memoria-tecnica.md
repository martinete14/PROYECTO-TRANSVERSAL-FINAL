# Memoria tecnica - Mini Academia

## 1. Descripcion del proyecto

Mini Academia es una aplicacion web monolitica orientada a la gestion y venta de cursos online.
El sistema permite organizar cursos por categorias, inscribir usuarios y consultar el catalogo academico.

Tematica asignada: Plataforma de cursos online (mini academia).

## 2. Objetivos

- Aplicar patron MVC en un proyecto real.
- Implementar persistencia relacional con entidades relacionadas.
- Construir interfaz web con Thymeleaf y Bootstrap.
- Mantener buenas practicas de validacion, manejo de errores y organizacion de capas.

## 3. Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate Validator (Bean Validation)
- Thymeleaf
- Bootstrap 5
- MySQL
- Maven
- Git/GitHub

## 4. Arquitectura de la aplicacion

Aplicacion monolitica con separacion por capas:

- Capa de presentacion:
  - Controladores MVC para vistas web
  - Controladores REST para endpoints de datos
- Capa de negocio:
  - Servicios con reglas de negocio
- Capa de persistencia:
  - Repositorios Spring Data JPA
- Capa de dominio:
  - Entidades JPA

## 5. Modelo de datos

Entidades principales:

- Categoria
- Curso
- Usuario
- Inscripcion

Relaciones:

- Categoria 1:N Curso
- Usuario 1:N Inscripcion
- Curso 1:N Inscripcion

Entregables BD:

- Script SQL: `database/miniacademy.sql`
- Modelo ER: `docs/diagrama-er.mmd`
- Modelo relacional: definido en el script SQL con PK/FK y restricciones

## 6. Funcionalidades implementadas

- CRUD completo de Cursos
- Listado de cursos por categoria
- Inscripcion de usuarios en cursos
- Consulta de cursos de un usuario
- Validaciones de datos en entidades (`@NotBlank`, `@NotNull`)
- Manejo basico de errores con `GlobalExceptionHandler`

## 7. Frontend y SEO

Plantillas Thymeleaf:

- `cursos.html`
- `crear-curso.html`
- `editar-curso.html`

Requisitos cubiertos:

- HTML semantico (`header`, `main`, `section`, `article`, `footer`)
- Responsive design con Bootstrap 5
- SEO basico con `title`, `meta description` y encabezados jerarquicos

## 8. Instrucciones de despliegue local

1. Crear la base de datos en MySQL ejecutando `database/miniacademy.sql`.
2. Revisar credenciales en `src/main/resources/application.properties`.
3. Ejecutar la aplicacion:
   - Windows: `mvnw.cmd spring-boot:run`
   - Linux/Mac: `./mvnw spring-boot:run`
4. Acceder en navegador a `http://localhost:8081/web/cursos`.

## 9. Diagramas

- Diagrama de clases: `docs/diagrama-clases.mmd`
- Diagrama ER: `docs/diagrama-er.mmd`
- Diagrama de secuencia: opcional (pendiente para bonus)

## 10. Conclusiones

El proyecto cumple con los requisitos principales del modulo transversal para Programacion, BD, Lenguaje de Marcas/SEO y uso de Git.
La fase de contenedores Docker y despliegue en Linux queda planificada para la etapa final del proyecto, segun el ritmo de clase.
