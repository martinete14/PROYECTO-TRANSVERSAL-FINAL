-- =============================================================
--  Lothar Courses  — Esquema de base de datos
--  Autor : Martín Villagra Tejerina · 1°DAW Ilerna 2026
--  Motor : MySQL 8+ / MariaDB 10.6+
--  Uso   : Ejecutar como root antes de arrancar la aplicación
-- =============================================================

-- ------------------------------------------------------------
-- 1. Base de datos y usuario de aplicación
-- ------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS miniacademy
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE miniacademy;

CREATE USER IF NOT EXISTS 'miniacademy_app'@'localhost'
  IDENTIFIED BY 'miniacademy_app_2026';

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, DROP
  ON miniacademy.*
  TO 'miniacademy_app'@'localhost';

FLUSH PRIVILEGES;

-- ------------------------------------------------------------
-- 2. Tablas (en orden de dependencia)
-- ------------------------------------------------------------

-- 2.1 Categoría
CREATE TABLE IF NOT EXISTS categoria (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100)    NOT NULL,
    descripcion VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.2 Curso
CREATE TABLE IF NOT EXISTS curso (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    titulo           VARCHAR(150) NOT NULL,
    descripcion      TEXT         NOT NULL,
    instructor       VARCHAR(120) NOT NULL,
    imagen_url       VARCHAR(500),
    video_url        VARCHAR(500),
    destacado_semana TINYINT(1)   NOT NULL DEFAULT 0,
    categoria_id     BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_curso_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.3 Usuario
CREATE TABLE IF NOT EXISTS usuario (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    nombre                VARCHAR(120)  NOT NULL,
    email                 VARCHAR(150)  NOT NULL,
    rol                   VARCHAR(24),
    password              VARCHAR(120),
    student_id            VARCHAR(24),
    teacher_id            VARCHAR(24),
    admin_id              VARCHAR(24),
    documento             VARCHAR(30),
    telefono              VARCHAR(30),
    fecha_nacimiento      DATE,
    ciudad_pais           VARCHAR(140),
    institucion_academica VARCHAR(160),
    programa_academico    VARCHAR(160),
    nivel_academico       VARCHAR(80),
    foto_perfil_url       VARCHAR(260),
    biografia             TEXT,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email      UNIQUE (email),
    CONSTRAINT uk_usuario_student_id UNIQUE (student_id),
    CONSTRAINT uk_usuario_teacher_id UNIQUE (teacher_id),
    CONSTRAINT uk_usuario_admin_id   UNIQUE (admin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.4 Inscripción (relación N:M entre Usuario y Curso)
CREATE TABLE IF NOT EXISTS inscripcion (
    id         BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    curso_id   BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_inscripcion_usuario_curso UNIQUE (usuario_id, curso_id),
    CONSTRAINT fk_inscripcion_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_inscripcion_curso
        FOREIGN KEY (curso_id) REFERENCES curso(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.5 Contenido de curso (unidades/lecciones)
CREATE TABLE IF NOT EXISTS curso_contenido (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    curso_id    BIGINT        NOT NULL,
    titulo      VARCHAR(140)  NOT NULL,
    tipo        VARCHAR(32),
    descripcion TEXT,
    recurso_url VARCHAR(260),
    orden       INT,
    creado_en   DATETIME      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_contenido_curso
        FOREIGN KEY (curso_id) REFERENCES curso(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.6 Registro de auditoría de acciones
CREATE TABLE IF NOT EXISTS audit_log (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    occurred_at   DATETIME      NOT NULL,
    actor_user_id BIGINT,
    actor_name    VARCHAR(120),
    actor_role    VARCHAR(24),
    action_type   VARCHAR(80)   NOT NULL,
    target_path   VARCHAR(180),
    details       TEXT,
    ip_address    VARCHAR(45),
    PRIMARY KEY (id),
    INDEX idx_audit_occurred_at  (occurred_at),
    INDEX idx_audit_actor        (actor_user_id),
    INDEX idx_audit_action_type  (action_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Fin del esquema
-- ------------------------------------------------------------
