# SIGTAS - Sistema de Gestión de Turnos y Atención

Prototipo académico correspondiente al AP2 de Licenciatura en Informática.

## Tecnologías
- Java 17+
- MySQL 8+
- JDBC
- Maven

## Estructura
- `src/main/java/ar/edu/sigtas/config`: conexión a MySQL.
- `src/main/java/ar/edu/sigtas/model`: entidades del dominio.
- `src/main/java/ar/edu/sigtas/repository`: contratos de persistencia.
- `src/main/java/ar/edu/sigtas/service`: reglas de negocio.
- `src/main/java/ar/edu/sigtas/ui`: prototipo de consola.
- `sql/schema.sql`: creación de la base y tablas.
- `sql/data.sql`: datos de prueba.
- `sql/queries.sql`: inserción, consulta, modificación y borrado de registros.

## Preparación de MySQL
1. Crear/ejecutar `sql/schema.sql`.
2. Ejecutar `sql/data.sql`.
3. Configurar las variables de entorno:
   - `SIGTAS_DB_URL` (por defecto `jdbc:mysql://localhost:3306/sigtas?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires`)
   - `SIGTAS_DB_USER`
   - `SIGTAS_DB_PASSWORD`
4. Compilar con `mvn clean package`.
5. Ejecutar `ar.edu.sigtas.ui.Main` desde el IDE.

## Prototipo
El menú permite consultar disponibilidad, registrar turnos, cancelar turnos y consultar la agenda. La lógica de negocio valida que el paciente exista, que la agenda esté activa y que la franja no se encuentre ocupada.

## Repositorio de referencia
https://github.com/PabloRuizS2/SIGTAS
