package com.wellington.easyeyeintegrator.service;

import com.wellington.easyeyeintegrator.dto.AuthorizationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellington.easyeyeintegrator.config.ApiConstants;
import com.wellington.easyeyeintegrator.dto.AuthorizationResult;
import java.util.Map;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class ApiServices {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static AuthorizationResult authorizeAccess() {
        try {
            configureUnirest();

            HttpResponse<String> response = sendAuthorizationRequest();

            logResponse(response);

            return buildResultFromResponse(response);

        } catch (Exception e) {
            return new AuthorizationResult(
                false,
                "Erro ao autenticar na API: " + e.getMessage()
            );
        } finally {
            Unirest.shutDown();
        }
    }

    // ================= MÉTODOS AUXILIARES =================

    private static void configureUnirest() {
        Unirest.config()
               .connectTimeout(0)
               .socketTimeout(0);
    }
    
    private static HttpResponse<String> sendAuthorizationRequest() throws Exception {
        String url = ApiConstants.BASE_URL + "/integrators/auth";
        
        String body = MAPPER.writeValueAsString(
            Map.of("token", ApiConstants.TOKEN)
        );
        
        System.out.println(url);
        System.out.println(body);
        
        return Unirest.post(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(body)
                .asString();
    }

    private static AuthorizationResult buildResultFromResponse(HttpResponse<String> response) {

        int status = response.getStatus();

        if (status >= 200 && status < 300) {
            return new AuthorizationResult(
                true,
                "Acesso autorizado pela API"
            );
        }

        if (status == 401) {
            return new AuthorizationResult(
                false,
                "Token inválido ou não autorizado"
            );
        }

        return new AuthorizationResult(
            false,
            "Erro HTTP " + status
        );
    }

    private static void logResponse(HttpResponse<String> response) {
        System.out.println("=== API RESPONSE ===");
        System.out.println("Status HTTP: " + response.getStatus());
        System.out.println("Body: " + response.getBody());
        System.out.println("====================");
    }
}
