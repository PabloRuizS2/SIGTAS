# SIGTAS – Sistema Integral de Gestión de Turnos y Atención Sanitaria

Proyecto académico: Seminario de Práctica de Informática, TP3. Continuación del AP2.

## Requisitos
Java 17 o superior, Maven 3.8 o superior y MySQL 8 o superior.

## Estructura
- `src/main/java/ar/edu/sigtas`: aplicación Java (modelo, servicio, repositorio, configuración e interfaz).
- `sql`: esquema, datos de ejemplo y consultas de AP2.
- `docs/AP2`: diagramas y documentos originales del análisis y diseño.
- `docs/PRUEBAS_TP3.md`: escenarios de verificación.

## Base de datos
**Atención:** `sql/schema.sql` elimina y recrea la base `sigtas`. Usar solo en una base de pruebas.

1. Ejecutar `sql/schema.sql` y después `sql/data.sql` en MySQL.
2. Configurar las variables de entorno `SIGTAS_DB_URL`, `SIGTAS_DB_USER` y `SIGTAS_DB_PASSWORD`.
3. Compilar con `mvn clean package`.
4. Ejecutar desde el IDE `ar.edu.sigtas.ui.Main`, o bien:
   `mvn exec:java -Dexec.mainClass=ar.edu.sigtas.ui.Main`

## Operaciones
1. Consultar agenda por fecha.
2. Registrar un turno (paciente y agenda activos, día y franja válidos, sin superposición).
3. Cancelar turno.
4. Mostrar un ejemplo de polimorfismo con `Persona`, `Paciente` y `Profesional`.
0. Salir.

## Ejemplo con los datos cargados
La agenda 1 corresponde a lunes, 08:00–12:00, intervalos de 30 minutos; paciente 1 y usuario 2 son datos de prueba. Elegir un lunes posterior a las fechas de ejemplo para registrar un turno a las 09:00–09:30.

## Alcance
Prototipo de consola con JDBC. No implementa autenticación, interfaz gráfica ni administración integral de historias clínicas. La ejecución completa requiere una instancia MySQL configurada.

Repositorio: https://github.com/PabloRuizS2/SIGTAS
