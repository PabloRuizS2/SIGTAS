# Guion de video - aproximadamente 3 minutos

Preparación: ejecutar las pruebas de integración antes de grabar. Abrir el proyecto en una IDE Java, el programa y MySQL Workbench. Usar solo los datos ficticios. Elegir un lunes futuro y reservar 09:00-09:30 en agenda 1. Ensayar con cronómetro; los tiempos incluyen las acciones en pantalla.

## 0:00-0:25 | Presentación y problema
Mostrar portada y menú.

“Soy Pablo Ruiz y presento la versión integradora de SIGTAS, el Sistema de Gestión de Turnos y Atención del Centro Médico Salud Integral. El proyecto continúa el trabajo anterior. Busca resolver la dispersión de información en planillas, las reservas superpuestas y la falta de un historial uniforme de cancelaciones. El alcance de este prototipo comprende consultar, registrar y cancelar turnos.”

## 0:25-0:55 | Arquitectura y patrón
Mostrar paquetes y la interfaz TurnoRepository.

“La aplicación está desarrollada en Java y organizada en presentación, servicios y persistencia. Seleccioné el patrón DAO para encapsular el acceso a datos. TurnoRepository define el contrato y JdbcTurnoRepository lo implementa con JDBC. El servicio recibe la interfaz por constructor. Así, las reglas no dependen de una implementación concreta y puedo probarlas con un repositorio en memoria. La implementación real utiliza MySQL.”

## 0:55-1:35 | Demostración operacional
Registrar un turno, consultar la fecha y repetir la reserva.

“Ingreso un paciente, una agenda y una fecha futura. El sistema valida el día, el rango horario y la duración configurada. Al guardar, registra el turno y su auditoría en una misma transacción. Ahora consulto la agenda y veo el registro persistido. Si intento reservar nuevamente la franja, se rechaza la operación. El control contempla intervalos superpuestos y bloquea al profesional durante la reserva para coordinar solicitudes concurrentes.”

## 1:35-2:05 | Cancelación y excepciones
Cancelar el ID registrado y mostrar turno y auditoría en MySQL.

“La cancelación cambia el estado y conserva el motivo, sin borrar el historial. Solo se permite para turnos asignados. Si falla una operación SQL, se intenta revertir la transacción. También se cierran automáticamente las conexiones y las consultas. La consola distingue errores de formato, validaciones, errores de base de datos y problemas de archivos, para que el usuario pueda corregir la entrada.”

## 2:05-2:35 | Estructuras y archivos
Mostrar ResumenAgenda, contadores, exportación y lectura.

“Los resultados de la consulta se almacenan en un ArrayList porque la cantidad de turnos varía. Un arreglo de enteros cuenta los turnos por estado. Ambas estructuras se complementan: la lista conserva el detalle y el arreglo resume la agenda. Además, el programa exporta los datos a un CSV y permite recuperar su contenido desde el menú. El archivo funciona como reporte y no reemplaza a MySQL.”

## 2:35-3:00 | PUD, pruebas y cierre
Mostrar resultado real de pruebas y concluir.

“El desarrollo sigue el Proceso Unificado: definición del problema, refinamiento de arquitectura, construcción y validación. La compilación y las ocho pruebas unitarias fueron exitosas. Las pruebas de integración se documentan por separado y deben respaldarse con su ejecución. Como evolución quedan la autenticación, la gestión de catálogos y una interfaz gráfica. El resultado es una base integradora que puede ampliarse por iteraciones.”

Estado final: video producido con la voz original del estudiante. La pantalla de cierre incorpora las 24 pruebas aprobadas, y las operaciones muestran salida de la sesión real registrada el 07/11/2026.
