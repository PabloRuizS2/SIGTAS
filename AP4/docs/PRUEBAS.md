# Pruebas ejecutadas

Fecha: 07/11/2026. JDK 17.0.20.1; Maven 3.9.11; MySQL Community Server 8.4.13; Connector/J 9.4.0.
Resultado: **BUILD SUCCESS; 24 pruebas, 0 fallos, 0 errores, 0 omitidas.**

## Unitarias: 8 aprobadas
Alta válida, identificadores inválidos, fecha pasada, intervalo invertido, fecha nula, motivo inválido, resumen con ArrayList/arreglo y escritura/lectura de CSV. Evidencia: RESULTADO-UNITARIAS.txt.

## Integración MySQL: 16 aprobadas
Se usó una base exclusiva `sigtas_ap4_it`, diferente de la base de demostración `sigtas_ap4`.

| Caso | Comprobación | Resultado |
|---|---|---|
| I01 | Consulta histórica ordenada y consulta sin resultados | Aprobado |
| I02 | Alta y auditoría persistidas | Aprobado |
| I03 | Rechazo de reserva duplicada sin insertar | Aprobado |
| I04 | Día incompatible con la agenda | Aprobado |
| I05 | Rango y duración inválidos | Aprobado |
| I06 | Paciente inexistente | Aprobado |
| I07 | Error FK de usuario sin persistencia parcial | Aprobado |
| I08 | Cancelación, motivo e historial de auditoría | Aprobado |
| I09 | No repetir cancelación ni cancelar ATENDIDO | Aprobado |
| I10 | Reutilizar horario cancelado | Aprobado |
| I11 | Exportar/recuperar datos reales y resumen por estado | Aprobado |
| I12 | Error de conexión como SQLException | Aprobado |
| I13 | Dos solicitudes simultáneas: solo una reserva | Aprobado |
| I14 | Rechazo de intervalos cruzados entre agendas del mismo profesional | Aprobado |
| I15 | Fallo provocado de auditoría revierte la inserción del turno | Aprobado |
| I16 | Intervalos contiguos permitidos | Aprobado |

La prueba concurrente usa dos conexiones JDBC independientes y una barrera de inicio. No equivale a una prueba de carga masiva. I15 utiliza un trigger exclusivo de pruebas que rechaza la auditoría del usuario ficticio 4. Ese trigger NO se instala en la base normal.

## Repetir las pruebas
1. Preparar una base NUEVA denominada `sigtas_ap4_it`: copiar schema.sql y data.sql y reemplazar solamente el nombre `sigtas_ap4` por `sigtas_ap4_it` antes de ejecutarlos.
2. Ejecutar `sql/integration-fixture.sql` con la cuenta administradora de la instancia de pruebas. La creación del trigger puede requerir privilegios administrativos si el registro binario está activo.
3. La cuenta de pruebas requiere SELECT, INSERT y UPDATE sobre esa base. Configurar las variables SIGTAS_DB_URL, SIGTAS_DB_USER y SIGTAS_DB_PASSWORD para el esquema de pruebas.
4. En PowerShell: `$env:SIGTAS_INTEGRATION='1'` y luego `mvn clean package`.
5. Para repetir otra vez, crear una base de integración nueva con los datos iniciales; la suite exige auditoría vacía y no elimina datos automáticamente. No borrar una base que contenga trabajo personal.

Sin SIGTAS_INTEGRATION=1, la suite de integración se omite y se ejecutan las ocho unitarias. Para volver a usar la consola, quitar esa variable y apuntar SIGTAS_DB_URL a la base normal.

## Demostración real
Se ejecutó Main contra MySQL y se registraron alta, consulta, rechazo, cancelación y CSV. El turno 4 del 09/11/2026 quedó CANCELADO con su motivo y dos entradas de auditoría. Evidencia íntegra: `evidencia/DEMO-CONSOLA.txt`, `evidencia/MYSQL-RESULTADOS.txt`, `evidencia/sesion-console.json`. El video reproduce fragmentos de esa salida real en pantallas legibles, con la narración original.
