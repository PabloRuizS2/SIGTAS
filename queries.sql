USE sigtas;

-- 1. INSERT: alta de paciente
INSERT INTO paciente (dni, nombre, apellido, fecha_nacimiento, telefono, email)
VALUES ('44777888', 'Pedro', 'López', '2003-02-14', '1155552010', 'pedro.lopez@email.local');

-- 2. SELECT: consultar disponibilidad de una agenda para una fecha
SELECT a.id_agenda, p.apellido, p.nombre, e.nombre AS especialidad,
       a.hora_inicio AS desde, a.hora_fin AS hasta, a.duracion_minutos
FROM agenda a
JOIN profesional p ON p.id_profesional = a.id_profesional
JOIN especialidad e ON e.id_especialidad = p.id_especialidad
WHERE a.activo = TRUE
  AND p.activo = TRUE
  AND e.activa = TRUE
  AND a.id_agenda = 1;

-- 3. SELECT: agenda diaria con estado de cada turno
SELECT t.id_turno, t.fecha, t.hora_inicio, t.hora_fin,
       pa.apellido AS paciente_apellido, pa.nombre AS paciente_nombre,
       t.estado, t.motivo_cancelacion
FROM turno t
JOIN paciente pa ON pa.id_paciente = t.id_paciente
WHERE t.fecha = '2026-09-28'
ORDER BY t.hora_inicio;

-- 4. UPDATE: cancelar un turno sin perder el historial (baja lógica del turno)
UPDATE turno
SET estado = 'CANCELADO',
    motivo_cancelacion = 'Solicitud del paciente',
    modificado_por = 2
WHERE id_turno = 1;

-- 5. SELECT: reportes básicos de turnos por estado
SELECT estado, COUNT(*) AS cantidad
FROM turno
GROUP BY estado
ORDER BY estado;

-- 6. DELETE: borrado físico de un registro de prueba sin referencias
INSERT INTO especialidad (nombre, descripcion) VALUES ('Especialidad temporal', 'Registro de prueba para DELETE');
DELETE FROM especialidad WHERE nombre = 'Especialidad temporal';
