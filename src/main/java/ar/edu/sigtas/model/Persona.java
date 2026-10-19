package ar.edu.sigtas.model;

/** Abstracción compartida por pacientes y profesionales. */
public abstract class Persona {
    private final int id;
    private final String nombre;
    private final String apellido;

    protected Persona(int id, String nombre, String apellido) {
        if (id <= 0 || nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("Identificación y nombre de persona inválidos");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
    }

    public final int getId() { return id; }
    public final String getNombre() { return nombre; }
    public final String getApellido() { return apellido; }
    public String descripcion() { return nombre + " " + apellido; }
    public abstract String tipo();
}
