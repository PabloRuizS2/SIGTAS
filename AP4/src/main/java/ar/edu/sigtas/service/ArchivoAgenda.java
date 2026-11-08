package ar.edu.sigtas.service;
import ar.edu.sigtas.model.Turno;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
public final class ArchivoAgenda {
    private ArchivoAgenda() {}
    public static void guardar(Path archivo, List<Turno> turnos) throws IOException {
        StringBuilder texto = new StringBuilder("ID;PACIENTE;AGENDA;FECHA;INICIO;FIN;ESTADO\n");
        for (Turno t : turnos) texto.append(t.id()).append(';').append(t.pacienteId()).append(';')
            .append(t.agendaId()).append(';').append(t.fecha()).append(';').append(t.horaInicio())
            .append(';').append(t.horaFin()).append(';').append(t.estado()).append('\n');
        Files.createDirectories(archivo.toAbsolutePath().getParent());
        Files.writeString(archivo, texto, StandardCharsets.UTF_8);
    }
    /** Recuperación para consulta, no importación a MySQL. */
    public static String leer(Path archivo) throws IOException {
        return Files.readString(archivo, StandardCharsets.UTF_8);
    }
}
