package ar.edu.sigtas.repository;

import ar.edu.sigtas.model.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository {
    boolean existeTurnoActivo(int agendaId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) throws Exception;
    boolean pacienteActivo(int pacienteId) throws Exception;
    boolean agendaHabilitada(int agendaId, LocalDate fecha, LocalTime inicio, LocalTime fin) throws Exception;
    int guardar(Turno turno, int usuarioId) throws Exception;
    void cancelar(int turnoId, int usuarioId, String motivo) throws Exception;
    Optional<Turno> buscarPorId(int turnoId) throws Exception;
    List<Turno> agendaDiaria(LocalDate fecha) throws Exception;
}
