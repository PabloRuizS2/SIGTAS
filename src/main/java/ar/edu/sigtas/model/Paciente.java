package ar.edu.sigtas.model;

import java.time.LocalDate;

public final class Paciente extends Persona {
    private final String dni;
    private final LocalDate fechaNacimiento;
    private final String telefono;
    private final String email;
    private final boolean activo;

    public Paciente(int id, String dni, String nombre, String apellido, LocalDate fechaNacimiento,
                    String telefono, String email, boolean activo) {
        super(id, nombre, apellido);
        if (dni == null || dni.isBlank()) throw new IllegalArgumentException("DNI obligatorio");
        this.dni = dni.trim();
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.email = email;
        this.activo = activo;
    }

    public String getDni() { return dni; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public boolean isActivo() { return activo; }
    @Override public String tipo() { return "Paciente"; }
    @Override public String descripcion() { return tipo() + ": " + super.descripcion() + " (DNI " + dni + ")"; }
}
