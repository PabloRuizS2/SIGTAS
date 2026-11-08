# SIGTAS - AP4

Proyecto integrador de Pablo Ruiz. Continúa el alcance del TP3 y adapta el código del repositorio https://github.com/PabloRuizS2/SIGTAS.

## Requisitos
JDK 17+, Maven 3.9+, MySQL 8.0.16+ (o 8.4) con InnoDB. Abrir `pom.xml` en IntelliJ IDEA, Eclipse o NetBeans como proyecto Maven. Configurar SDK 17 y codificación UTF-8. La compilación y la sesión de validación se ejecutaron desde terminal. Se incluye configuración para abrir y ejecutar en VS Code con sus extensiones Java.

## Base de datos
En MySQL Workbench, ejecutar primero `sql/schema.sql` y después `sql/data.sql`. También se puede usar el cliente mysql:
```
mysql -u root -p
SOURCE C:/ruta/SIGTAS-AP4/sql/schema.sql;
SOURCE C:/ruta/SIGTAS-AP4/sql/data.sql;
```
Se crea una base nueva `sigtas_ap4`; no se modifica la base anterior `sigtas`. Los scripts se ejecutan una sola vez: fallan si la base o los datos ya existen. No contienen DROP DATABASE. Los datos son ficticios. Los usuarios y hashes de demostración NO implementan autenticación.

Configurar en la misma terminal PowerShell (sustituir los valores de ejemplo):
```powershell
$env:SIGTAS_DB_URL='jdbc:mysql://localhost:3306/sigtas_ap4?serverTimezone=America/Argentina/Buenos_Aires'
$env:SIGTAS_DB_USER='usuario_local'
$env:SIGTAS_DB_PASSWORD='clave_local'
mvn clean package
mvn exec:java
```
La cuenta de MySQL debe tener acceso al esquema y permisos SELECT, INSERT y UPDATE. No subir credenciales al repositorio. El `ID usuario` solicitado por la consola es el actor registrado en la tabla `usuario`, no la cuenta JDBC.

## Demostración
1. Opción 1: consultar `2026-09-28` para ver los registros históricos del seed.
2. Opción 2: paciente 1, agenda 1, una fecha de lunes futura, 09:00 a 09:30, usuario 2. Anotar el ID devuelto.
3. Repetir la reserva: debe rechazarse. En la agenda 1 solo se admiten lunes de 08:00 a 12:00, con turnos de 30 minutos alineados a las 08:00.
4. Opción 3: cancelar el ID creado con usuario 2 y motivo. Solo se cancela un turno ASIGNADO.
5. Opción 1: consultar la fecha elegida. Se conserva CANCELADO y se actualizan los contadores.
6. Opción 4: exportar esa fecha. Opción 5: recuperar el CSV. Los archivos están en `exportaciones/` y se sobrescriben al exportar de nuevo la misma fecha. Son reportes, no copias completas de la base ni importaciones.

## Organización
- `config`: conexiones JDBC mediante variables de entorno.
- `model`: Turno, Paciente y TurnoEstado.
- `repository`: interfaz DAO y acceso MySQL.
- `service`: validaciones, resumen en arreglo y archivos.
- `ui`: menú de consola.
- `sql`: esquema relacional y datos de demostración.
- `src/test`: pruebas unitarias JUnit y pruebas de integración con MySQL.
- `docs`: pruebas, trazabilidad, guion y evidencia.

## Estado verificado
El 07/11/2026 se ejecutó `mvn clean package` con MySQL 8.4.13: 24 pruebas aprobadas (8 unitarias + 16 de integración), sin fallos, errores ni pruebas omitidas. Se grabó la salida de una sesión real de la consola y se contrastó con consultas MySQL. Ver `docs/PRUEBAS.md` y `docs/evidencia/`.

La suite de integración se activa con SIGTAS_INTEGRATION=1 y requiere una base nueva sigtas_ap4_it más el fixture de pruebas. Nunca ejecutar ese fixture sobre la base normal. Sin esa variable se ejecutan las unitarias y se omite integración.

## Versión final y archivos
Esta entrega está en https://github.com/PabloRuizS2/SIGTAS/tree/main/AP4. Las entregas anteriores se conservan fuera de AP4.
El informe está en `docs/RUIZ-PABLO-AP4.pdf`. El video final se entrega como archivo MP4 separado, con voz original y reproducción de la salida real de consola. Las credenciales, el audio original y las herramientas portátiles no forman parte del repositorio público.

## VS Code
Instalar Language Support for Java (Red Hat) y Debugger for Java (Microsoft). Abrir esta carpeta, configurar un JDK 17 y ejecutar `mvn package` para resolver dependencias. Copiar `.env.example` a `.env`, completar credenciales locales y usar la configuración **SIGTAS - Consola** en Ejecutar y depurar. `.env` se excluye del repositorio.

## Límites
Sin autenticación real, altas de catálogos desde consola, facturación ni gestión clínica. El bloqueo de reservas es por profesional, reduce el paralelismo y protege operaciones que siguen este DAO; escrituras SQL externas deben respetar el mismo protocolo. No se evita que un paciente reserve con profesionales diferentes a la misma hora. No se implementan reintentos automáticos tras un deadlock. La incertidumbre de un corte de conexión durante COMMIT debe resolverse consultando la agenda y la auditoría antes de reintentar.
