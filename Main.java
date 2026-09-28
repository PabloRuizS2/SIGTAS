package ar.edu.sigtas.ui;

import ar.edu.sigtas.model.Turno;
import ar.edu.sigtas.repository.JdbcTurnoRepository;
import ar.edu.sigtas.repository.TurnoRepository;
import ar.edu.sigtas.service.TurnoService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final TurnoRepository repository = new JdbcTurnoRepository();
    private final TurnoService service = new TurnoService(repository);

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("=== SIGTAS - Prototipo de consola ===");
        while (true) {
            System.out.println("\n1. Consultar agenda diaria\n2. Registrar turno\n3. Cancelar turno\n0. Salir");
            System.out.print("Opción: ");
            String option = scanner.nextLine().trim();
            try {
                switch (option) {
                    case "1" -> mostrarAgenda();
                    case "2" -> registrar();
                    case "3" -> cancelar();
                    case "0" -> { System.out.println("Fin del prototipo."); return; }
                    default -> System.out.println("Opción inválida");
                }
            } catch (Exception ex) {
                System.out.println("Operación no realizada: " + ex.getMessage());
            }
        }
    }

    private void mostrarAgenda() throws Exception {
        System.out.print("Fecha (AAAA-MM-DD): ");
        LocalDate fecha = LocalDate.parse(scanner.nextLine().trim());
        List<Turno> turnos = repository.agendaDiaria(fecha);
        if (turnos.isEmpty()) {
            System.out.println("No hay turnos registrados para la fecha.");
            return;
        }
        for (Turno t : turnos) {
            System.out.printf("#%d | agenda=%d | paciente=%d | %s-%s | %s%n",
                    t.id(), t.agendaId(), t.pacienteId(), t.horaInicio(), t.horaFin(), t.estado());
        }
    }

    private void registrar() throws Exception {
        System.out.print("ID paciente: "); int pacienteId = Integer.parseInt(scanner.nextLine());
        System.out.print("ID agenda: "); int agendaId = Integer.parseInt(scanner.nextLine());
        System.out.print("Fecha (AAAA-MM-DD): "); LocalDate fecha = LocalDate.parse(scanner.nextLine());
        System.out.print("Hora inicio (HH:MM): "); LocalTime inicio = LocalTime.parse(scanner.nextLine());
        System.out.print("Hora fin (HH:MM): "); LocalTime fin = LocalTime.parse(scanner.nextLine());
        System.out.print("ID usuario: "); int usuarioId = Integer.parseInt(scanner.nextLine());
        int id = service.registrarTurno(pacienteId, agendaId, fecha, inicio, fin, usuarioId);
        System.out.println("Turno registrado correctamente. ID=" + id);
    }

    private void cancelar() throws Exception {
        System.out.print("ID turno: "); int turnoId = Integer.parseInt(scanner.nextLine());
        System.out.print("ID usuario: "); int usuarioId = Integer.parseInt(scanner.nextLine());
        System.out.print("Motivo: "); String motivo = scanner.nextLine();
        service.cancelarTurno(turnoId, usuarioId, motivo);
        System.out.println("Turno cancelado y horario liberado.");
    }
}
