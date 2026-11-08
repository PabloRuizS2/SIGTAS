# Matriz de trazabilidad
Las rutas de clases son relativas a src/main/java/ar/edu/sigtas/.

| Requisito | Implementación | Verificación |
|---|---|---|
| Patrón coherente con capas | repository/TurnoRepository y JdbcTurnoRepository; service/TurnoService | Inyección por constructor; sustitución en PrototipoTest |
| Conexión MySQL | config/DatabaseConnection.getConnection | I01-I16 aprobados; I12 verifica fallo de conexión |
| Consulta e interfaz | JdbcTurnoRepository.agendaDiaria; ui/Main.mostrarAgenda | I01 aprobado |
| Alta y actualización | guardar; cancelar | I02-I10 aprobados |
| Excepciones y atomicidad | SQLException, rollback, try-with-resources | Unitarias e I07/I12/I15 aprobados |
| Interfaces | TurnoRepository | Compilación y doble Memoria |
| ArrayList | agendaDiaria agrega cada fila con add | I01; resumen unitario |
| Arreglos | service/ResumenAgenda.contar; Main.mostrarAgenda | arregloCuentaListaVariable |
| Archivos | service/ArchivoAgenda | archivoGuardaYRecupera; I11 aprobado |
| Negocio y PUD | Informe, secciones 1 y 2 | Revisión documental |
| Entrega incremental | README y código derivado del repositorio anterior | Informe, sección 1 |
| Video de 3 minutos | docs/GUION-VIDEO.md | Video final con voz original y sesión real |
| Participación | Evidencia personal de foros/encuentros | Debe aportarla el estudiante |
