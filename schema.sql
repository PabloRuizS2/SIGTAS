CREATE DATABASE IF NOT EXISTS sigtas;
USE sigtas;

CREATE TABLE especialidad (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  activa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE profesional (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  matricula VARCHAR(40) NOT NULL UNIQUE,
  especialidad_id INT NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT fk_prof_especialidad FOREIGN KEY (especialidad_id) REFERENCES especialidad(id)
);

CREATE TABLE paciente (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  documento VARCHAR(30) NOT NULL UNIQUE,
  telefono VARCHAR(40),
  email VARCHAR(120),
  activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE turno (
  id INT AUTO_INCREMENT PRIMARY KEY,
  paciente_id INT NOT NULL,
  profesional_id INT NOT NULL,
  fecha_hora DATETIME NOT NULL,
  estado ENUM('RESERVADO','CANCELADO','PRESENTE','ATENDIDO') NOT NULL DEFAULT 'RESERVADO',
  motivo_cancelacion VARCHAR(255),
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_turno_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id),
  CONSTRAINT fk_turno_profesional FOREIGN KEY (profesional_id) REFERENCES profesional(id),
  CONSTRAINT uq_turno_profesional_hora UNIQUE (profesional_id, fecha_hora)
);

INSERT INTO especialidad(nombre) VALUES
('Clínica Médica'),('Pediatría'),('Cardiología'),('Traumatología'),('Dermatología');
