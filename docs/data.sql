-- =============================================================
--  Lothar Courses  — Datos de ejemplo (demo)
--  Autor : Martín Villagra Tejerina · 1°DAW Ilerna 2026
--  Requisito previo : haber ejecutado init.sql
--  Contraseñas en texto plano (solo referencia):
--    admin@lotharcourses.local      → admin123
--    instructor@lotharcourses.local → instructor123
--    alumno.demo@lotharcourses.local → cliente123
--  Las columnas `password` almacenan el hash BCrypt generado
--  por Spring Security PasswordEncoder (BCryptPasswordEncoder).
-- =============================================================

USE miniacademy;

-- ------------------------------------------------------------
-- 1. Categorías
-- ------------------------------------------------------------
INSERT INTO categoria (nombre, descripcion) VALUES
  ('Ciencia y Pensamiento',          'Explora ideas cientificas, filosofia y preguntas fundamentales'),
  ('Diseno y Programacion',          'Diseno digital, desarrollo web y programacion orientada a proyectos'),
  ('Historia y Sociedad',            'Procesos historicos, memoria colectiva y comprension del presente'),
  ('Aviacion y Operaciones de Vuelo','Formacion para pilotaje comercial, seguridad y toma de decisiones'),
  ('Salud y Fisioterapia',           'Bienestar fisico, readaptacion funcional y prevencion de lesiones');

-- ------------------------------------------------------------
-- 2. Cursos
-- ------------------------------------------------------------
INSERT INTO curso (titulo, descripcion, instructor, imagen_url, video_url, destacado_semana, categoria_id)
VALUES
  (
    'Grandes preguntas del universo',
    'Ciencia, pensamiento y las grandes preguntas del universo explicadas con claridad.',
    'Juan Martin Maldacena',
    '/uploads/images/img-027.png',
    '/uploads/videos/vid-004.mp4',
    1,
    (SELECT id FROM categoria WHERE nombre = 'Ciencia y Pensamiento')
  ),
  (
    'Desarrollo web con Spring Boot',
    'Construye aplicaciones MVC profesionales con Java, Spring Boot y Thymeleaf.',
    'Admin Lothar Courses',
    '/uploads/images/img-019.jpg',
    '/uploads/videos/vid-009.mp4',
    0,
    (SELECT id FROM categoria WHERE nombre = 'Diseno y Programacion')
  ),
  (
    'El pasado que explica todo',
    'Descubre procesos historicos que construyen el presente desde una mirada critica y bien documentada.',
    'Nils Jacobsen',
    '/uploads/images/img-008.png',
    '/uploads/videos/vid-006.mp4',
    1,
    (SELECT id FROM categoria WHERE nombre = 'Historia y Sociedad')
  ),
  (
    'Instructor de vuelo: de privado a comercial',
    'Entrenamiento integral con enfoque operativo: navegacion, meteorologia, CRM y toma de decisiones.',
    'Alejo Testa',
    '/uploads/images/img-004.jpg',
    '/uploads/videos/vid-002.mp4',
    0,
    (SELECT id FROM categoria WHERE nombre = 'Aviacion y Operaciones de Vuelo')
  ),
  (
    'Fisioterapia funcional para la vida diaria',
    'Movilidad, prevencion de lesiones y ejercicios funcionales para mejorar la calidad de vida.',
    'Tu mejor amigo - Fisioterapeuta',
    '/uploads/images/img-014.png',
    '/uploads/videos/vid-001.mp4',
    0,
    (SELECT id FROM categoria WHERE nombre = 'Salud y Fisioterapia')
  );

-- ------------------------------------------------------------
-- 3. Usuarios de demo (contraseñas hasheadas con BCrypt)
-- ------------------------------------------------------------
INSERT INTO usuario (nombre, email, rol, password, admin_id, teacher_id, student_id)
VALUES
  (
    'Admin Lothar Courses',
    'admin@lotharcourses.local',
    'ADMIN',
    -- admin123
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOhBQ6vgRGJoKi1/CgOwDRbLpRHdB3e6y',
    'ADM-0001',
    NULL,
    NULL
  ),
  (
    'Alejo Testa',
    'instructor@lotharcourses.local',
    'INSTRUCTOR',
    -- instructor123
    '$2a$10$ixAKp7rMtmjhE6AGMznnhOUSa5/S7qmLV1s4Tf2fTYkDYJBGz./3.',
    NULL,
    'TCH-0001',
    NULL
  ),
  (
    'Alumno Demo',
    'alumno.demo@lotharcourses.local',
    -- cliente123
    '$2a$10$Dow/A8Q2FJl2IJm.0bYinO/YKrE0P3GCIV6XMb6J4FmMI4Q1PLqv6',
    NULL,
    NULL,
    'STU-0001'
  );

-- ------------------------------------------------------------
-- 4. Inscripciones del alumno demo
-- ------------------------------------------------------------
INSERT INTO inscripcion (usuario_id, curso_id)
SELECT u.id, c.id
FROM   usuario u, curso c
WHERE  u.email  = 'alumno.demo@lotharcourses.local'
  AND  c.titulo IN (
         'Grandes preguntas del universo',
         'El pasado que explica todo'
       );

-- ------------------------------------------------------------
-- 5. Contenido de ejemplo en el primer curso
-- ------------------------------------------------------------
INSERT INTO curso_contenido (curso_id, titulo, tipo, descripcion, recurso_url, orden, creado_en)
SELECT c.id,
       'Introduccion al curso',
       'VIDEO',
       'Bienvenida y descripcion de los objetivos del curso.',
       '/uploads/videos/vid-004.mp4',
       1,
       NOW()
FROM   curso c
WHERE  c.titulo = 'Grandes preguntas del universo';

INSERT INTO curso_contenido (curso_id, titulo, tipo, descripcion, recurso_url, orden, creado_en)
SELECT c.id,
       'Materia oscura y energia oscura',
       'VIDEO',
       'Explicacion accesible sobre los grandes misterios cosmologicos actuales.',
       '/uploads/videos/vid-004.mp4',
       2,
       NOW()
FROM   curso c
WHERE  c.titulo = 'Grandes preguntas del universo';

-- ------------------------------------------------------------
-- 6. Entradas de auditoría de ejemplo
-- ------------------------------------------------------------
INSERT INTO audit_log (occurred_at, actor_user_id, actor_name, actor_role, action_type, target_path, details, ip_address)
SELECT NOW(), u.id, u.nombre, u.rol, 'LOGIN', '/auth/login', 'Inicio de sesion exitoso', '127.0.0.1'
FROM   usuario u WHERE u.email = 'admin@lotharcourses.local';

INSERT INTO audit_log (occurred_at, actor_user_id, actor_name, actor_role, action_type, target_path, details, ip_address)
SELECT NOW(), u.id, u.nombre, u.rol, 'CURSO_CREAR', '/admin/cursos/crear',
       'Curso creado: Desarrollo web con Spring Boot', '127.0.0.1'
FROM   usuario u WHERE u.email = 'admin@lotharcourses.local';

-- ------------------------------------------------------------
-- Fin de datos de ejemplo
-- ------------------------------------------------------------
