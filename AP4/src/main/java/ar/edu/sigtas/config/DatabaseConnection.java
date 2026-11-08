package ar.edu.sigtas.config;
import java.sql.*;
public final class DatabaseConnection {
    private DatabaseConnection() {}
    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault("SIGTAS_DB_URL",
            "jdbc:mysql://localhost:3306/sigtas_ap4?serverTimezone=America/Argentina/Buenos_Aires");
        String user = System.getenv("SIGTAS_DB_USER");
        String password = System.getenv("SIGTAS_DB_PASSWORD");
        if (user == null || password == null)
            throw new SQLException("Configure SIGTAS_DB_USER y SIGTAS_DB_PASSWORD");
        return DriverManager.getConnection(url, user, password);
    }
}
