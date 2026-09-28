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
    public boolean existeTurnoActivo(int agendaId, LocalDate fecha, LocalTime horaInicio) throws SQLException {
        String sql = """
                SELECT 1 FROM turno
                WHERE id_agenda = ? AND fecha = ? AND hora_inicio = ?
                  AND estado IN ('ASIGNADO','PRESENTE','ATENDIDO')
                LIMIT 1
                """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, agendaId);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(horaInicio));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public int guardar(Turno turno, int usuarioId) throws SQLException {
        String sql = """
                INSERT INTO turno
                    (id_paciente, id_agenda, fecha, hora_inicio, hora_fin, estado, creado_por)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, turno.pacienteId());
            ps.setInt(2, turno.agendaId());
            ps.setDate(3, Date.valueOf(turno.fecha()));
            ps.setTime(4, Time.valueOf(turno.horaInicio()));
            ps.setTime(5, Time.valueOf(turno.horaFin()));
            ps.setString(6, turno.estado().name());
            ps.setInt(7, usuarioId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo obtener el identificador del turno");
    }

    @Override
    public void cancelar(int turnoId, int usuarioId, String motivo) throws SQLException {
        String sql = """
                UPDATE turno
                SET estado = 'CANCELADO', motivo_cancelacion = ?, modificado_por = ?
                WHERE id_turno = ?
                """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, usuarioId);
            ps.setInt(3, turnoId);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Turno inexistente: " + turnoId);
            }
        }
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
