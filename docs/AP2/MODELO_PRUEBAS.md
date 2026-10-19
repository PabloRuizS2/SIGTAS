# Casos de prueba del prototipo

| ID | Objetivo | Entrada / condición | Resultado esperado |
|---|---|---|---|
| CP01 | Registrar turno libre | Paciente existente + agenda activa + horario libre | Se crea un turno ASIGNADO |
| CP02 | Evitar superposición | Misma agenda, fecha y hora de CP01 | Operación rechazada |
| CP03 | Cancelar turno | Turno existente + motivo | Estado CANCELADO y horario liberado |
| CP04 | Consultar agenda | Fecha con turnos | Turnos ordenados por hora |
| CP05 | Registrar atención | Turno PRESENTE + profesional correspondiente | Atención almacenada y turno ATENDIDO |
| CP06 | Control de autenticación | Usuario inactivo / rol insuficiente | Operación denegada |
| CP07 | Integridad referencial | FK inexistente | INSERT/UPDATE rechazado |
| CP08 | Datos obligatorios | DNI o nombre vacío | Validación rechaza la operación |
