package com.wellington.easyeyeintegrator.controller;

import com.wellington.easyeyeintegrator.service.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHelper {

    public static void salvarArquivo(String nomeOriginal, String novoNome, String caminho, String descricao) {
        String sql = "INSERT INTO arquivos (nome_original, novo_nome, caminho_destino, descricao) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeOriginal);
            stmt.setString(2, novoNome);
            stmt.setString(3, caminho);
            stmt.setString(4, descricao);

            stmt.executeUpdate();
            System.out.println("📦 Dados salvos no banco com sucesso!");

        } catch (SQLException e) {
            System.out.println("❌ Erro ao salvar no banco: " + e.getMessage());
        }
    }
}
