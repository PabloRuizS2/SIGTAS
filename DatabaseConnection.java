package ar.edu.sigtas.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault(
                "SIGTAS_DB_URL",
                "jdbc:mysql://localhost:3306/sigtas?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires"
        );
        String user = System.getenv().getOrDefault("SIGTAS_DB_USER", "root");
        String password = System.getenv().getOrDefault("SIGTAS_DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }
}
