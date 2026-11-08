package ar.edu.sigtas.repository;

import ar.edu.sigtas.config.DatabaseConnection;
import ar.edu.sigtas.model.Turno;
import ar.edu.sigtas.model.TurnoEstado;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTurnoRepository implements TurnoRepository {
    @Override
    public int guardar(Turno turno, int usuarioId) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection()) {
            cn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            cn.setAutoCommit(false);
            try {
                int profesional;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT id_profesional FROM agenda WHERE id_agenda=?")) {
                    ps.setInt(1, turno.agendaId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Agenda inexistente");
                        profesional = rs.getInt(1);
                    }
                }
                // Serializa reservas de un profesional, incluso entre agendas distintas.
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT activo FROM profesional WHERE id_profesional=? FOR UPDATE")) {
                    ps.setInt(1, profesional);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !rs.getBoolean(1))
                            throw new IllegalArgumentException("Profesional inactivo");
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT * FROM agenda WHERE id_agenda=? FOR UPDATE")) {
                    ps.setInt(1, turno.agendaId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !rs.getBoolean("activo"))
                            throw new IllegalArgumentException("Agenda inactiva");
                        if (rs.getInt("id_profesional") != profesional)
                            throw new IllegalArgumentException("La agenda cambió; reintente");
                        LocalTime desde = rs.getTime("hora_inicio").toLocalTime();
                        LocalTime hasta = rs.getTime("hora_fin").toLocalTime();
                        int minutos = rs.getInt("duracion_minutos");
                        long desplazamiento = java.time.Duration.between(desde, turno.horaInicio()).toSeconds();
                        long duracion = java.time.Duration.between(turno.horaInicio(), turno.horaFin()).toSeconds();
                        if (turno.fecha().getDayOfWeek().getValue() != rs.getInt("dia_semana")
                            || turno.horaInicio().isBefore(desde) || turno.horaFin().isAfter(hasta)
                            || duracion != minutos * 60L || desplazamiento % (minutos * 60L) != 0)
                            throw new IllegalArgumentException("Día, rango o duración incompatibles con la agenda");
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT activo FROM paciente WHERE id_paciente=?")) {
                    ps.setInt(1, turno.pacienteId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !rs.getBoolean(1))
                            throw new IllegalArgumentException("Paciente inexistente o inactivo");
                    }
                }
                String solapamiento = "SELECT 1 FROM turno t JOIN agenda a ON a.id_agenda=t.id_agenda "
                    + "WHERE a.id_profesional=? AND t.fecha=? AND t.estado IN ('ASIGNADO','PRESENTE','ATENDIDO') "
                    + "AND t.hora_inicio < ? AND t.hora_fin > ? LIMIT 1";
                try (PreparedStatement ps = cn.prepareStatement(solapamiento)) {
                    ps.setInt(1, profesional); ps.setDate(2, Date.valueOf(turno.fecha()));
                    ps.setTime(3, Time.valueOf(turno.horaFin())); ps.setTime(4, Time.valueOf(turno.horaInicio()));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) throw new IllegalArgumentException("El profesional ya tiene un turno superpuesto");
                    }
                }
                int id;
                try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO turno (id_paciente,id_agenda,fecha,hora_inicio,hora_fin,estado,creado_por) "
                    + "VALUES (?,?,?,?,?,'ASIGNADO',?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, turno.pacienteId()); ps.setInt(2, turno.agendaId());
                    ps.setDate(3, Date.valueOf(turno.fecha())); ps.setTime(4, Time.valueOf(turno.horaInicio()));
                    ps.setTime(5, Time.valueOf(turno.horaFin())); ps.setInt(6, usuarioId); ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("No se obtuvo el ID");
                        id = rs.getInt(1);
                    }
                }
                auditar(cn, usuarioId, id, "ALTA", "Reserva de turno");
                cn.commit(); return id;
            } catch (SQLException | RuntimeException ex) {
                rollback(cn, ex); throw ex;
            }
        }
    }

    @Override
    public void cancelar(int turnoId, int usuarioId, String motivo) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE turno SET estado='CANCELADO',motivo_cancelacion=?,modificado_por=? "
                    + "WHERE id_turno=? AND estado='ASIGNADO'")) {
                    ps.setString(1, motivo); ps.setInt(2, usuarioId); ps.setInt(3, turnoId);
                    if (ps.executeUpdate() != 1)
                        throw new IllegalArgumentException("Turno inexistente o no cancelable (solo ASIGNADO)");
                }
                auditar(cn, usuarioId, turnoId, "CANCELACION", motivo);
                cn.commit();
            } catch (SQLException | RuntimeException ex) {
                rollback(cn, ex); throw ex;
            }
        }
    }
    private void auditar(Connection cn, int usuario, int turno, String operacion, String detalle) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
            "INSERT INTO auditoria (id_usuario,entidad,entidad_id,operacion,detalle) VALUES (?,'turno',?,?,?)")) {
            ps.setInt(1, usuario); ps.setInt(2, turno); ps.setString(3, operacion); ps.setString(4, detalle);
            ps.executeUpdate();
        }
    }
    private void rollback(Connection cn, Exception original) {
        try { cn.rollback(); } catch (SQLException fallo) { original.addSuppressed(fallo); }
    }

    @Override
    public Optional<Turno> buscarPorId(int turnoId) throws SQLException {
        String sql = "SELECT id_turno,id_paciente,id_agenda,fecha,hora_inicio,hora_fin,estado FROM turno WHERE id_turno = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, turnoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        }
    }

    @Override
    public List<Turno> agendaDiaria(LocalDate fecha) throws SQLException {
        String sql = """
                SELECT id_turno,id_paciente,id_agenda,fecha,hora_inicio,hora_fin,estado
                FROM turno WHERE fecha = ? ORDER BY hora_inicio
                """;
        List<Turno> result = new ArrayList<>();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
        }
        return result;
    }

    private Turno map(ResultSet rs) throws SQLException {
        return new Turno(
                rs.getInt("id_turno"),
                rs.getInt("id_paciente"),
                rs.getInt("id_agenda"),
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora_inicio").toLocalTime(),
                rs.getTime("hora_fin").toLocalTime(),
                TurnoEstado.valueOf(rs.getString("estado"))
        );
    }
}
