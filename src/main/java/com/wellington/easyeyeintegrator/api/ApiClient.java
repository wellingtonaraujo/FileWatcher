package com.wellington.easyeyeintegrator.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellington.easyeyeintegrator.api.dto.SignInRequest;
import com.wellington.easyeyeintegrator.api.dto.SignInResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public SignInResponse signIn(String email, String password, String code) throws Exception {
        URL url = new URL(baseUrl + "/integrators/signin");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        conn.setRequestProperty("Accept", "application/json");
        // IMPORTANTE: alguns servidores recusam "application/json; charset=UTF-8"
        conn.setRequestProperty("Content-Type", "application/json");

        // Body
        SignInRequest request = new SignInRequest(email, password, code);
        String jsonBody = mapper.writeValueAsString(request);

        byte[] payload = jsonBody.getBytes(StandardCharsets.UTF_8);

        // ajuda alguns servidores/proxies a não reclamarem do body
        conn.setFixedLengthStreamingMode(payload.length);

        // (opcional) User-Agent pode ajudar em alguns gateways
        conn.setRequestProperty("User-Agent", "EasyEyeIntegrator/1.0");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload);
        }

        int status = conn.getResponseCode();

        InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        String responseBody = readStream(is);

        if (status < 200 || status >= 300) {
            throw new RuntimeException("Erro ao autenticar (HTTP " + status + "): " + responseBody);
        }

        return mapper.readValue(responseBody, SignInResponse.class);
    }



    private String readStream(InputStream is) throws Exception {
        if (is == null) return "";

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
