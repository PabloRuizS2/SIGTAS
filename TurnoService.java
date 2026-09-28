package ar.edu.sigtas.service;

import ar.edu.sigtas.model.Turno;
import ar.edu.sigtas.model.TurnoEstado;
import ar.edu.sigtas.repository.TurnoRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public class TurnoService {
    private final TurnoRepository repository;

    public TurnoService(TurnoRepository repository) {
        this.repository = repository;
    }

    public int registrarTurno(int pacienteId, int agendaId, LocalDate fecha,
                              LocalTime horaInicio, LocalTime horaFin, int usuarioId) throws Exception {
        if (pacienteId <= 0 || agendaId <= 0 || usuarioId <= 0) {
            throw new IllegalArgumentException("Paciente, agenda y usuario deben ser válidos");
        }
        if (fecha == null || horaInicio == null || horaFin == null || !horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException("Fecha y horario inválidos");
        }
        if (repository.existeTurnoActivo(agendaId, fecha, horaInicio)) {
            throw new IllegalStateException("El horario seleccionado ya está ocupado");
        }
        Turno turno = new Turno(0, pacienteId, agendaId, fecha, horaInicio, horaFin, TurnoEstado.ASIGNADO);
        return repository.guardar(turno, usuarioId);
    }

    public void cancelarTurno(int turnoId, int usuarioId, String motivo) throws Exception {
        if (turnoId <= 0 || usuarioId <= 0 || motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Turno, usuario y motivo son obligatorios");
        }
        repository.cancelar(turnoId, usuarioId, motivo.trim());
    }
}
