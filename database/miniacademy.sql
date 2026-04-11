CREATE DATABASE IF NOT EXISTS miniacademy CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE miniacademy;

CREATE USER IF NOT EXISTS 'miniacademy_app'@'localhost' IDENTIFIED BY 'miniacademy_app_2026';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, DROP ON miniacademy.* TO 'miniacademy_app'@'localhost';
FLUSH PRIVILEGES;

CREATE TABLE IF NOT EXISTS categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS curso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    instructor VARCHAR(120) NOT NULL,
    imagen_url VARCHAR(500),
    video_url VARCHAR(500),
    categoria_id BIGINT NOT NULL,
    CONSTRAINT fk_curso_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS inscripcion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    CONSTRAINT fk_inscripcion_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_inscripcion_curso
        FOREIGN KEY (curso_id) REFERENCES curso(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT uk_usuario_curso UNIQUE (usuario_id, curso_id)
);

INSERT INTO categoria (nombre, descripcion) VALUES
('Programacion', 'Cursos de desarrollo de software'),
('Diseno Web', 'Cursos orientados a frontend y UX/UI'),
('Bases de Datos', 'Cursos de modelado y SQL')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO curso (titulo, descripcion, instructor, imagen_url, video_url, categoria_id)
SELECT 'Java Spring Boot desde cero', 'Construye APIs y apps MVC profesionales.', 'Laura Gomez',
       'https://images.unsplash.com/photo-1517694712202-14dd9538aa97',
       'https://cdn.coverr.co/videos/coverr-student-typing-1578/1080p.mp4', c.id
FROM categoria c WHERE c.nombre = 'Programacion' AND NOT EXISTS (
    SELECT 1 FROM curso WHERE titulo = 'Java Spring Boot desde cero'
);

INSERT INTO curso (titulo, descripcion, instructor, imagen_url, video_url, categoria_id)
SELECT 'SQL para proyectos reales', 'Consultas avanzadas y modelado relacional.', 'Carlos Medina',
       'https://images.unsplash.com/photo-1544383835-bda2bc66a55d',
       'https://cdn.coverr.co/videos/coverr-man-working-on-laptop-1574/1080p.mp4', c.id
FROM categoria c WHERE c.nombre = 'Bases de Datos' AND NOT EXISTS (
    SELECT 1 FROM curso WHERE titulo = 'SQL para proyectos reales'
);
