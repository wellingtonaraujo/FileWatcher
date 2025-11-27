package com.wellington.filewatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ExameCsvRepository {

    // Arquivo CSV de exames, no diretório de resources do usuário
    private static final Path EXAMES_CSV =
            ConfigUtil.getResourcesDir().resolve("exames.csv");

    private static void ensureResourcesDir() throws IOException {
        Path dir = EXAMES_CSV.getParent();
        if (dir != null) {
            Files.createDirectories(dir);
        }
    }

    /**
     * Lê todos os exames do arquivo exames.csv.
     * Formato de cada linha: exame;sufixo
     */
    public static List<ExameTipo> findAll() throws IOException {
        List<ExameTipo> lista = new ArrayList<>();

        if (!Files.exists(EXAMES_CSV)) {
            return lista; // vazio se ainda não existe
        }

        List<String> linhas = Files.readAllLines(EXAMES_CSV, StandardCharsets.UTF_8);
        for (String linha : linhas) {
            linha = linha.trim();
            if (linha.isEmpty() || linha.startsWith("#")) continue;

            String[] parts = linha.split(";", 2);
            if (parts.length == 2) {
                String exame  = parts[0].trim();
                String sufixo = parts[1].trim();

                if (!exame.isEmpty() && !sufixo.isEmpty()) {
                    lista.add(new ExameTipo(exame, sufixo));
                }
            }
        }

        return lista;
    }

    /**
     * Salva a lista completa em exames.csv (sobrescreve).
     */
    public static void saveAll(List<ExameTipo> exames) throws IOException {
        ensureResourcesDir();

        List<String> linhas = new ArrayList<>();
        for (ExameTipo e : exames) {
            // Formato CSV simples: exame;sufixo
            // (CSV no estilo BR normalmente usa ; mesmo)
            linhas.add(e.getExame() + ";" + e.getSufixo());
        }

        Files.write(
                EXAMES_CSV,
                linhas,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /**
     * Insere novo ou atualiza se o sufixo já existir (sufixo = chave).
     */
    public static void saveOrUpdate(String exame, String sufixo) throws IOException {
        if (exame == null)  exame  = "";
        if (sufixo == null) sufixo = "";

        exame  = exame.trim();
        sufixo = sufixo.trim();

        if (exame.isEmpty() || sufixo.isEmpty()) {
            throw new IOException("Exame e sufixo não podem ser vazios.");
        }

        List<ExameTipo> lista = findAll();
        boolean updated = false;

        for (ExameTipo e : lista) {
            if (e.getSufixo().equalsIgnoreCase(sufixo)) {
                e.setExame(exame);
                updated = true;
                break;
            }
        }

        if (!updated) {
            lista.add(new ExameTipo(exame, sufixo));
        }

        saveAll(lista);
    }

    /**
     * Deleta um exame pelo sufixo (case-insensitive).
     */
    public static void deleteBySufixo(String sufixo) throws IOException {
        if (sufixo == null || sufixo.trim().isEmpty()) return;

        String alvo = sufixo.trim();

        List<ExameTipo> lista = findAll();
        lista.removeIf(e -> e.getSufixo().equalsIgnoreCase(alvo));

        saveAll(lista);
    }
}
