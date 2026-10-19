# Casos de prueba – TP3

| ID | Operación | Entrada | Resultado esperado |
|---|---|---|---|
| CP01 | Agenda | 2026-09-28 | Muestra turnos de ejemplo |
| CP02 | Registrar | Paciente 1, agenda 1, lunes, 09:00–09:30, usuario 2 | Crea turno si no existe conflicto |
| CP03 | Registrar duplicado | Mismos datos de CP02 | Rechaza horario ocupado |
| CP04 | Registrar solapamiento | Franja que se cruza con otra activa | Rechaza superposición |
| CP05 | Registrar | Paciente inexistente | Rechaza paciente inválido |
| CP06 | Registrar | Día que no corresponde a agenda | Rechaza agenda no habilitada |
| CP07 | Registrar | Hora fin anterior a inicio | Rechaza horario inválido |
| CP08 | Cancelar | Turno existente, usuario 2 y motivo | Actualiza estado a CANCELADO |
| CP09 | Cancelar | Turno ya cancelado | Rechaza nueva cancelación |
| CP10 | Modelo POO | Opción 4 | Muestra descripciones diferentes por tipo de persona |

Estado: escenarios especificados; ejecución con MySQL pendiente de realizar en el entorno de destino.
