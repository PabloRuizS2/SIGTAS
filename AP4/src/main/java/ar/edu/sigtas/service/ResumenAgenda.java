package ar.edu.sigtas.service;
import ar.edu.sigtas.model.*;
import java.util.List;
public final class ResumenAgenda {
    private ResumenAgenda() {}
    /** Índices en el orden definido por TurnoEstado.values(). */
    public static int[] contar(List<Turno> turnos) {
        int[] cantidades = new int[TurnoEstado.values().length];
        for (Turno turno : turnos) cantidades[turno.estado().ordinal()]++;
        return cantidades;
    }
}
