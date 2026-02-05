package com.wellington.easyeyeintegrator.service;
import com.wellington.easyeyeintegrator.config.ApiConstants;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import javax.swing.*;

public class ApiTestHelper {

    public static void testarAutenticacao() {
        try {
            Unirest.config()
                   .connectTimeout(0)
                   .socketTimeout(0);

            String url = ApiConstants.BASE_URL + "/integrators/auth";
            HttpResponse<String> response = Unirest.post(url)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body("""
                        {
                            "token": "53b7262c0e68f302d824f07a8acd20ae6cf73268904a286e9df362cee921db31"
                        }
                    """)
                    .asString();

            // 🔹 Log no console (sempre)
            System.out.println("=== API RESPONSE ===");
            System.out.println("Status HTTP: " + response.getStatus());
            System.out.println("Body: " + response.getBody());
            System.out.println("Chamando API: " + url);
            System.out.println("====================");

            JOptionPane.showMessageDialog(
                    null,
                    "Status HTTP: " + response.getStatus(),
                    "Teste da API",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            // 🔴 Log detalhado no console
            System.err.println("=== ERRO NA CONEXÃO COM A API ===");
            System.err.println("Mensagem: " + e.getMessage());
            e.printStackTrace(); // stacktrace completo
            System.err.println("================================");

            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao conectar na API.\n\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            Unirest.shutDown();
        }
    }

}
