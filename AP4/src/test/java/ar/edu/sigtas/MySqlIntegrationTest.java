package ar.edu.sigtas;

import ar.edu.sigtas.config.DatabaseConnection;
import ar.edu.sigtas.model.*;
import ar.edu.sigtas.repository.*;
import ar.edu.sigtas.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.sql.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** Requiere un esquema NUEVO sigtas_ap4_it cargado con schema.sql y data.sql. */
@EnabledIfEnvironmentVariable(named="SIGTAS_INTEGRATION", matches="1")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySqlIntegrationTest {
    static final TurnoRepository repo = new JdbcTurnoRepository();
    static final TurnoService service = new TurnoService(repo);
    static final LocalDate fecha = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusWeeks(1);
    static int turnoId;
    static LocalTime h(String value) { return LocalTime.parse(value); }
    static int alta(int paciente, int agenda, LocalDate dia, String desde, String hasta, int usuario) throws SQLException {
        return service.registrarTurno(paciente,agenda,dia,h(desde),h(hasta),usuario);
    }
    static int scalar(String sql) throws SQLException {
        try (var cn=DatabaseConnection.getConnection();var st=cn.createStatement();var rs=st.executeQuery(sql)) {
            assertTrue(rs.next());return rs.getInt(1);
        }
    }
    static void execute(String sql) throws SQLException {
        try(var cn=DatabaseConnection.getConnection();var st=cn.createStatement()) { st.execute(sql); }
    }
    @BeforeAll static void baseAislada() throws SQLException {
        try(var cn=DatabaseConnection.getConnection();var st=cn.createStatement();var rs=st.executeQuery("SELECT DATABASE(),VERSION()")) {
            assertTrue(rs.next());assertEquals("sigtas_ap4_it",rs.getString(1),"Solo usar la base aislada de integración");
            System.out.println("MySQL real: "+rs.getString(2)+"; fecha de pruebas: "+fecha);
        }
        assertEquals(0,scalar("SELECT COUNT(*) FROM auditoria"),"Cargar una base de pruebas nueva");
    }
    @Test @Order(1) void I01_consultaHistoricaOrdenada() throws Exception {
        var turnos=repo.agendaDiaria(LocalDate.of(2026,9,28));
        assertEquals(2,turnos.size());assertTrue(turnos.get(0).horaInicio().isBefore(turnos.get(1).horaInicio()));
        assertTrue(repo.agendaDiaria(fecha).isEmpty());
    }
    @Test @Order(2) void I02_altaConAuditoria() throws Exception {
        turnoId=alta(1,1,fecha,"09:00","09:30",2);
        assertEquals(TurnoEstado.ASIGNADO,repo.buscarPorId(turnoId).orElseThrow().estado());
        assertEquals(1,scalar("SELECT COUNT(*) FROM auditoria WHERE entidad_id="+turnoId+" AND operacion='ALTA'"));
    }
    @Test @Order(3) void I03_rechazaReservaDuplicada() throws Exception {
        int antes=scalar("SELECT COUNT(*) FROM turno");
        assertThrows(IllegalArgumentException.class,()->alta(1,1,fecha,"09:00","09:30",2));
        assertEquals(antes,scalar("SELECT COUNT(*) FROM turno"));
    }
    @Test @Order(4) void I04_rechazaDiaIncompatible() {
        assertThrows(IllegalArgumentException.class,()->alta(1,1,fecha.plusDays(1),"09:00","09:30",2));
    }
    @Test @Order(5) void I05_rechazaRangoYDuracion() {
        assertThrows(IllegalArgumentException.class,()->alta(1,1,fecha,"07:30","08:00",2));
        assertThrows(IllegalArgumentException.class,()->alta(1,1,fecha,"09:00","09:45",2));
    }
    @Test @Order(6) void I06_rechazaPacienteInexistente() {
        assertThrows(IllegalArgumentException.class,()->alta(999999,1,fecha,"10:00","10:30",2));
    }
    @Test @Order(7) void I07_errorFKNoPersisteTurno() throws Exception {
        int antes=scalar("SELECT COUNT(*) FROM turno");
        assertThrows(SQLException.class,()->alta(1,1,fecha,"10:00","10:30",999999));
        assertEquals(antes,scalar("SELECT COUNT(*) FROM turno"));
    }
    @Test @Order(8) void I08_cancelacionConMotivoYAuditoria() throws Exception {
        service.cancelarTurno(turnoId,2,"Prueba de cancelación");
        assertEquals(TurnoEstado.CANCELADO,repo.buscarPorId(turnoId).orElseThrow().estado());
        assertEquals(1,scalar("SELECT COUNT(*) FROM turno WHERE id_turno="+turnoId+" AND motivo_cancelacion='Prueba de cancelación'"));
        assertEquals(1,scalar("SELECT COUNT(*) FROM auditoria WHERE entidad_id="+turnoId+" AND operacion='CANCELACION'"));
    }
    @Test @Order(9) void I09_noRepiteCancelacionNiCancelaAtendido() {
        assertThrows(IllegalArgumentException.class,()->service.cancelarTurno(turnoId,2,"Repetir"));
        assertThrows(IllegalArgumentException.class,()->service.cancelarTurno(3,2,"Atendido"));
    }
    @Test @Order(10) void I10_reutilizaFranjaCancelada() throws Exception {
        int nuevo=alta(2,1,fecha,"09:00","09:30",2);assertNotEquals(turnoId,nuevo);
        assertEquals(TurnoEstado.ASIGNADO,repo.buscarPorId(nuevo).orElseThrow().estado());
    }
    @Test @Order(11) void I11_exportaYRecuperaDatosReales(@TempDir Path tmp) throws Exception {
        var turnos=repo.agendaDiaria(fecha);var csv=tmp.resolve("agenda.csv");
        ArchivoAgenda.guardar(csv,turnos);var texto=ArchivoAgenda.leer(csv);
        assertTrue(texto.contains("CANCELADO"));assertTrue(texto.contains("ASIGNADO"));
        assertArrayEquals(new int[]{1,0,0,1},ResumenAgenda.contar(turnos));
    }
    @Test @Order(12) void I12_errorConexionEsSQLException() {
        assertThrows(SQLException.class,()->DriverManager.getConnection("jdbc:mysql://127.0.0.1:1/sigtas_ap4_it?connectTimeout=1000","inexistente",""));
    }
    @Test @Order(13) void I13_concurrenciaSoloUnaReserva() throws Exception {
        var pool=Executors.newFixedThreadPool(2);var barrera=new CountDownLatch(1);
        Callable<Boolean> intento=()->{barrera.await();try{alta(1,1,fecha,"10:00","10:30",2);return true;}catch(IllegalArgumentException ex){return false;}};
        try {
            var a=pool.submit(intento);var b=pool.submit(intento);barrera.countDown();
            assertNotEquals(a.get(15,TimeUnit.SECONDS),b.get(15,TimeUnit.SECONDS));
            assertEquals(1,scalar("SELECT COUNT(*) FROM turno WHERE fecha='"+fecha+"' AND hora_inicio='10:00:00' AND estado='ASIGNADO'"));
        } finally { pool.shutdownNow(); }
    }
    @Test @Order(14) void I14_solapamientoEntreAgendas() throws Exception {
        execute("INSERT INTO agenda(id_profesional,dia_semana,hora_inicio,hora_fin,duracion_minutos) VALUES(1,1,'08:15','12:15',30)");
        int agenda=scalar("SELECT MAX(id_agenda) FROM agenda");
        assertThrows(IllegalArgumentException.class,()->alta(1,agenda,fecha,"09:15","09:45",2));
    }
    @Test @Order(15) void I15_falloAuditoriaRevierteInsercion() throws Exception {
        int antes=scalar("SELECT COUNT(*) FROM turno");
        // El fixture de integración rechaza auditoría del usuario ficticio 4.
        assertThrows(SQLException.class,()->alta(1,1,fecha,"11:00","11:30",4));
        assertEquals(antes,scalar("SELECT COUNT(*) FROM turno"));
    }
    @Test @Order(16) void I16_turnosContiguosPermitidos() throws Exception {
        assertTrue(alta(1,1,fecha,"10:30","11:00",2)>0);
    }
}
