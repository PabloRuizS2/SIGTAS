package ar.edu.sigtas.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Turno(
        int id,
        int pacienteId,
        int agendaId,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        TurnoEstado estado
) {}
