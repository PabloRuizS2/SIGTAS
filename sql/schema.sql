DROP DATABASE IF EXISTS sigtas;
CREATE DATABASE sigtas CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE sigtas;

CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

CREATE TABLE especialidad (
    id_especialidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE profesional (
    id_profesional INT AUTO_INCREMENT PRIMARY KEY,
    matricula VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    id_especialidad INT NOT NULL,
    email VARCHAR(120),
    telefono VARCHAR(30),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_especialidad) REFERENCES especialidad(id_especialidad)
);

CREATE TABLE paciente (
    id_paciente INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    fecha_nacimiento DATE,
    telefono VARCHAR(30),
    email VARCHAR(120),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE agenda (
    id_agenda INT AUTO_INCREMENT PRIMARY KEY,
    id_profesional INT NOT NULL,
    dia_semana TINYINT NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    duracion_minutos SMALLINT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_profesional) REFERENCES profesional(id_profesional),
    CONSTRAINT ck_agenda_dia CHECK (dia_semana BETWEEN 1 AND 7),
    CONSTRAINT ck_agenda_horas CHECK (hora_inicio < hora_fin),
    CONSTRAINT ck_agenda_duracion CHECK (duracion_minutos > 0),
    UNIQUE (id_profesional, dia_semana, hora_inicio, hora_fin)
);

CREATE TABLE turno (
    id_turno INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_agenda INT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ASIGNADO',
    motivo_cancelacion VARCHAR(255),
    creado_por INT NOT NULL,
    modificado_por INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    bloquea_horario TINYINT AS (
        CASE WHEN estado IN ('ASIGNADO','PRESENTE','ATENDIDO') THEN 1 ELSE NULL END
    ) STORED,
    FOREIGN KEY (id_paciente) REFERENCES paciente(id_paciente),
    FOREIGN KEY (id_agenda) REFERENCES agenda(id_agenda),
    FOREIGN KEY (creado_por) REFERENCES usuario(id_usuario),
    FOREIGN KEY (modificado_por) REFERENCES usuario(id_usuario),
    CONSTRAINT ck_turno_estado CHECK (estado IN ('ASIGNADO','PRESENTE','ATENDIDO','CANCELADO')),
    CONSTRAINT ck_turno_horas CHECK (hora_inicio < hora_fin),
    UNIQUE (id_agenda, fecha, hora_inicio, bloquea_horario)
);

CREATE TABLE registro_atencion (
    id_atencion INT AUTO_INCREMENT PRIMARY KEY,
    id_turno INT NOT NULL UNIQUE,
    id_profesional INT NOT NULL,
    resumen TEXT NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_turno) REFERENCES turno(id_turno),
    FOREIGN KEY (id_profesional) REFERENCES profesional(id_profesional),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE auditoria (
    id_auditoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    entidad VARCHAR(60) NOT NULL,
    entidad_id INT NOT NULL,
    operacion VARCHAR(30) NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    detalle VARCHAR(500),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE INDEX idx_turno_fecha ON turno(fecha);
CREATE INDEX idx_turno_estado ON turno(estado);
CREATE INDEX idx_paciente_apellido ON paciente(apellido, nombre);
CREATE INDEX idx_auditoria_entidad ON auditoria(entidad, entidad_id);
