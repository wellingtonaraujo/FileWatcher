package com.wellington.filewatcher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/filewatcher_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    // Método para obter conexão
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Carrega o driver do MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Conexão com o banco de dados estabelecida com sucesso!");
            } catch (ClassNotFoundException e) {
                System.out.println("❌ Driver MySQL não encontrado: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("❌ Erro ao conectar ao banco: " + e.getMessage());
            }
        }
        return connection;
    }

    // Método para fechar a conexão (opcional)
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("🔌 Conexão encerrada.");
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
