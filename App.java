package com.sigtas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Prototipo mínimo de conexión a MySQL y consulta de especialidades.
 * Completar URL, usuario y contraseña antes de ejecutar.
 */
public class App {
    private static final String URL = "jdbc:mysql://localhost:3306/sigtas";
    private static final String USER = "root";
    private static final String PASSWORD = "CAMBIAR_PASSWORD";

    public static void main(String[] args) {
        String sql = "SELECT id, nombre FROM especialidad WHERE activa = TRUE ORDER BY nombre";
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("SIGTAS - Especialidades disponibles");
            while (rs.next()) {
                System.out.printf("%d - %s%n", rs.getInt("id"), rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("No fue posible conectarse a MySQL: " + e.getMessage());
        }
    }
}
