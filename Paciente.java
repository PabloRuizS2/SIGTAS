package ar.edu.sigtas.model;

import java.time.LocalDate;

public record Paciente(
        int id,
        String dni,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        String telefono,
        String email,
        boolean activo
) {}
