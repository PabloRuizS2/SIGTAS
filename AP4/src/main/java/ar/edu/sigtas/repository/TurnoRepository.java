package ar.edu.sigtas.repository;
import ar.edu.sigtas.model.Turno;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
/** Contrato DAO. guardar debe validar y reservar atómicamente. */
public interface TurnoRepository {
    int guardar(Turno turno, int usuarioId) throws SQLException;
    void cancelar(int turnoId, int usuarioId, String motivo) throws SQLException;
    Optional<Turno> buscarPorId(int turnoId) throws SQLException;
    List<Turno> agendaDiaria(LocalDate fecha) throws SQLException;
}
