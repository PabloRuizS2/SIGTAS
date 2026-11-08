package ar.edu.sigtas;
import ar.edu.sigtas.model.*;
import ar.edu.sigtas.repository.TurnoRepository;
import ar.edu.sigtas.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class PrototipoTest {
    static class Memoria implements TurnoRepository {
        ArrayList<Turno> datos = new ArrayList<>();
        public int guardar(Turno t, int u) { datos.add(t); return datos.size(); }
        public void cancelar(int t,int u,String m) {}
        public Optional<Turno> buscarPorId(int id) { return Optional.empty(); }
        public List<Turno> agendaDiaria(LocalDate d) { return datos; }
    }
    private final Memoria repo = new Memoria();
    private final TurnoService service = new TurnoService(repo);
    private final LocalDate futuro = LocalDate.now().plusDays(7);
    private final LocalTime inicio = LocalTime.of(8,0), fin = LocalTime.of(8,30);
    @Test void altaValidaDelegaPersistencia() throws Exception {
        assertEquals(1,service.registrarTurno(1,1,futuro,inicio,fin,1));
        assertEquals(TurnoEstado.ASIGNADO,repo.datos.get(0).estado());
    }
    @Test void rechazaIdentificadores() {
        assertThrows(IllegalArgumentException.class,()->service.registrarTurno(0,1,futuro,inicio,fin,1));
        assertTrue(repo.datos.isEmpty());
    }
    @Test void rechazaPasado() {
        assertThrows(IllegalArgumentException.class,()->service.registrarTurno(1,1,LocalDate.now().minusDays(1),inicio,fin,1));
    }
    @Test void rechazaIntervaloInvertido() {
        assertThrows(IllegalArgumentException.class,()->service.registrarTurno(1,1,futuro,fin,inicio,1));
    }
    @Test void rechazaFechaNula() {
        assertThrows(IllegalArgumentException.class,()->service.registrarTurno(1,1,null,inicio,fin,1));
    }
    @Test void rechazaMotivoVacioOLargo() {
        assertThrows(IllegalArgumentException.class,()->service.cancelarTurno(1,1," "));
        assertThrows(IllegalArgumentException.class,()->service.cancelarTurno(1,1,"x".repeat(256)));
    }
    @Test void arregloCuentaListaVariable() {
        var lista = new ArrayList<Turno>();
        lista.add(new Turno(1,1,1,futuro,inicio,fin,TurnoEstado.ASIGNADO));
        lista.add(new Turno(2,1,1,futuro,inicio,fin,TurnoEstado.CANCELADO));
        lista.add(new Turno(3,1,1,futuro,inicio,fin,TurnoEstado.ASIGNADO));
        assertArrayEquals(new int[]{2,0,0,1},ResumenAgenda.contar(lista));
        assertArrayEquals(new int[4],ResumenAgenda.contar(List.of()));
    }
    @Test void archivoGuardaYRecupera(@TempDir Path tmp) throws Exception {
        Path p=tmp.resolve("agenda.csv");
        ArchivoAgenda.guardar(p,List.of(new Turno(1,1,1,futuro,inicio,fin,TurnoEstado.ASIGNADO)));
        String lectura=ArchivoAgenda.leer(p);
        assertTrue(lectura.startsWith("ID;PACIENTE;AGENDA;FECHA;INICIO;FIN;ESTADO"));
        assertTrue(lectura.contains("1;1;1;"+futuro+";08:00;08:30;ASIGNADO"));
        assertThrows(java.io.IOException.class,()->ArchivoAgenda.leer(tmp.resolve("no.csv")));
    }
}
