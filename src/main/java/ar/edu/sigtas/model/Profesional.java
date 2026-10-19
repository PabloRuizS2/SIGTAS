package ar.edu.sigtas.model;

public final class Profesional extends Persona {
    private final String matricula;
    private final int especialidadId;

    public Profesional(int id, String matricula, String nombre, String apellido, int especialidadId) {
        super(id, nombre, apellido);
        if (matricula == null || matricula.isBlank() || especialidadId <= 0) {
            throw new IllegalArgumentException("Matrícula o especialidad inválida");
        }
        this.matricula = matricula.trim();
        this.especialidadId = especialidadId;
    }

    public String getMatricula() { return matricula; }
    public int getEspecialidadId() { return especialidadId; }
    @Override public String tipo() { return "Profesional"; }
    @Override public String descripcion() { return tipo() + ": " + super.descripcion() + " (MP " + matricula + ")"; }
}
