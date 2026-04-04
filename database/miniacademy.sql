CREATE DATABASE IF NOT EXISTS miniacademy CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE miniacademy;

CREATE TABLE IF NOT EXISTS categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS curso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
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
