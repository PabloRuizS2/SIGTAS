USE sigtas;

INSERT INTO rol (nombre) VALUES ('ADMINISTRADOR'), ('RECEPCIONISTA'), ('PROFESIONAL'), ('DIRECCION');

-- Hashes de ejemplo para fines académicos. Deben reemplazarse por hashes seguros en un entorno real.
INSERT INTO usuario (username, password_hash, id_rol) VALUES
('admin', 'demo_hash_admin', 1),
('recepcion', 'demo_hash_recepcion', 2),
('medico', 'demo_hash_medico', 3),
('direccion', 'demo_hash_direccion', 4);

INSERT INTO especialidad (nombre, descripcion) VALUES
('Clínica médica', 'Atención clínica general'),
('Pediatría', 'Atención pediátrica'),
('Cardiología', 'Atención cardiovascular'),
('Traumatología', 'Atención traumatológica'),
('Dermatología', 'Atención de piel y anexos');

INSERT INTO profesional (matricula, nombre, apellido, id_especialidad, email, telefono) VALUES
('MP-1001', 'Ana', 'Pérez', 1, 'ana.perez@saludintegral.local', '1155551001'),
('MP-1002', 'Juan', 'Gómez', 3, 'juan.gomez@saludintegral.local', '1155551002');

INSERT INTO paciente (dni, nombre, apellido, fecha_nacimiento, telefono, email) VALUES
('30111222', 'María', 'Sosa', '1984-03-12', '1155552001', 'maria.sosa@email.local'),
('35123456', 'Lucas', 'Romero', '1990-08-22', '1155552002', 'lucas.romero@email.local'),
('42111222', 'Sofía', 'Díaz', '2001-11-10', '1155552003', 'sofia.diaz@email.local');

INSERT INTO agenda (id_profesional, dia_semana, hora_inicio, hora_fin, duracion_minutos) VALUES
(1, 1, '08:00:00', '12:00:00', 30),
(1, 3, '08:00:00', '12:00:00', 30),
(2, 2, '14:00:00', '18:00:00', 30),
(2, 4, '14:00:00', '18:00:00', 30);

-- Turnos de prueba; ajustar la fecha si se desea ejecutar en un día distinto.
INSERT INTO turno (id_paciente, id_agenda, fecha, hora_inicio, hora_fin, estado, creado_por) VALUES
(1, 1, '2026-09-28', '08:00:00', '08:30:00', 'ASIGNADO', 2),
(2, 1, '2026-09-28', '08:30:00', '09:00:00', 'PRESENTE', 2),
(3, 2, '2026-09-30', '08:00:00', '08:30:00', 'ATENDIDO', 2);

INSERT INTO registro_atencion (id_turno, id_profesional, resumen, id_usuario)
VALUES (3, 1, 'Consulta ambulatoria registrada como ejemplo académico.', 3);
