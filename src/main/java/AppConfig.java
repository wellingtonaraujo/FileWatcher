package com.wellington.filewatcher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

public class AppConfig {

    // Diretório de configuração do usuário final (sempre externo)
    private static final Path CONFIG_DIR  = ConfigUtil.getConfigDir();

    // Arquivo principal de configuração
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.properties");

    // Arquivos de credenciais do administrador
    private static final Path UID_APP      = CONFIG_DIR.resolve("uid.app");     // login + dica
    private static final Path UID_PASS     = CONFIG_DIR.resolve("uid.key");     // senha criptografada
    private static final Path AES_KEY_FILE = CONFIG_DIR.resolve("aes.key");     // chave AES em Base64

    private static final int AES_KEY_SIZE   = 256; // bits
    private static final int GCM_IV_LENGTH  = 12;  // bytes
    private static final int GCM_TAG_LENGTH = 128; // bits

    private static final SecureRandom secureRandom = new SecureRandom();

    // ---------- Utilitário: garantir diretório ----------
    private static void ensureConfigDir() throws IOException {
        Files.createDirectories(CONFIG_DIR);
    }

    // ---------- Key generation / load ----------
    private static SecretKey loadOrCreateKey() throws IOException {
        try {
            ensureConfigDir();

            // 1) Se já existe o arquivo da chave AES, usa ele
            if (Files.exists(AES_KEY_FILE)) {
                byte[] encoded = Files.readAllBytes(AES_KEY_FILE);
                byte[] keyBytes = Base64.getDecoder().decode(
                        new String(encoded, StandardCharsets.UTF_8).trim()
                );
                int len = keyBytes.length;
                if (len != 16 && len != 24 && len != 32) {
                    throw new IOException("Chave AES inválida no arquivo aes.key: " + len + " bytes");
                }
                return new SecretKeySpec(keyBytes, "AES");
            }

            // 2) (Opcional) Tentativa de migração de formato antigo:
            //    Se só existe UID_PASS e não AES_KEY_FILE, pode ser que ele contenha a chave antiga em Base64.
            if (Files.exists(UID_PASS)) {
                try {
                    String content = Files.readString(UID_PASS, StandardCharsets.UTF_8).trim();
                    byte[] keyBytes = Base64.getDecoder().decode(content);
                    int len = keyBytes.length;
                    if (len == 16 || len == 24 || len == 32) {
                        // Parece uma chave AES válida -> migra para aes.key
                        Files.write(AES_KEY_FILE,
                                content.getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.CREATE_NEW);
                        System.out.println("[DEBUG] Migrei chave AES antiga de uid.key para aes.key");
                        return new SecretKeySpec(keyBytes, "AES");
                    }
                } catch (Exception ignored) {
                    // Se não deu certo, cai pra geração de nova chave
                }
            }

            // 3) Não existe chave → gera nova
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(AES_KEY_SIZE, secureRandom);
            SecretKey key = kg.generateKey();

            // Salva a chave em base64 em aes.key
            String b64 = Base64.getEncoder().encodeToString(key.getEncoded());
            Files.write(AES_KEY_FILE, b64.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE_NEW);

            try {
                AES_KEY_FILE.toFile().setReadable(false, false);
                AES_KEY_FILE.toFile().setWritable(false, false);
                AES_KEY_FILE.toFile().setExecutable(false, false);
                AES_KEY_FILE.toFile().setReadable(true, true);
                AES_KEY_FILE.toFile().setWritable(true, true);
            } catch (Exception ignored) { }

            return key;

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Erro ao gerar/carregar chave AES: " + e.getMessage(), e);
        }
    }

    // ---------- Encrypt ----------
    private static String encrypt(SecretKey key, String plaintext) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // ---------- Decrypt ----------
    private static String decrypt(SecretKey key, String b64Combined) throws Exception {
        byte[] combined = Base64.getDecoder().decode(b64Combined);
        if (combined.length < GCM_IV_LENGTH) {
            throw new IllegalArgumentException("Dados inválidos para descriptografia");
        }

        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plain = cipher.doFinal(cipherText);
        return new String(plain, StandardCharsets.UTF_8);
    }

    // ---------- Salvar credenciais ----------
    public static void saveAdminCredentials(String login, String senha, String dicaSenha) throws IOException {
        try {
            ensureConfigDir();

            // Garante que a chave AES exista
            SecretKey key = loadOrCreateKey();
            String senhaEnc = encrypt(key, senha);

            // Salva login e dica no uid.app
            Properties p = new Properties();
            p.setProperty("admin.login", login);
            p.setProperty("admin.dica.senha", dicaSenha);

            try (OutputStream out = Files.newOutputStream(
                    UID_APP,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {
                p.store(out, "Credenciais do administrador (somente login e dica da senha)");
            }

            // Salva a senha criptografada separadamente no uid.key (substituindo o conteúdo)
            Files.write(UID_PASS,
                    senhaEnc.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            try {
                UID_APP.toFile().setReadable(false, false);
                UID_APP.toFile().setWritable(false, false);
                UID_APP.toFile().setReadable(true, true);
                UID_APP.toFile().setWritable(true, true);

                UID_PASS.toFile().setReadable(false, false);
                UID_PASS.toFile().setWritable(false, false);
                UID_PASS.toFile().setReadable(true, true);
                UID_PASS.toFile().setWritable(true, true);
            } catch (Exception ignored) { }

        } catch (Exception e) {
            throw new IOException("Não foi possível salvar credenciais: " + e.getMessage(), e);
        }
    }

    // ---------- Ler credenciais ----------
    public static AdminCredentials loadAdminCredentials() throws IOException {
        if (!Files.exists(UID_APP) || !Files.exists(UID_PASS)) return null;

        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(UID_APP, StandardOpenOption.READ)) {
            p.load(in);
        }

        String login = p.getProperty("admin.login", "");
        String dica  = p.getProperty("admin.dica.senha", "");

        if (login.isEmpty()) return null;

        try {
            SecretKey key = loadOrCreateKey();
            String senhaEnc = Files.readString(UID_PASS, StandardCharsets.UTF_8);
            String senha = decrypt(key, senhaEnc);

            return new AdminCredentials(login, senha, dica);
        } catch (Exception e) {
            throw new IOException("Erro ao processar credenciais: " + e.getMessage(), e);
        }
    }

    public static class AdminCredentials {
        private final String login;
        private final String senha;
        private final String hint;

        public AdminCredentials(String login, String senha, String hint) {
            this.login = login;
            this.senha = senha;
            this.hint  = hint;
        }

        public String getLogin() { return login; }
        public String getSenha() { return senha; }
        public String getHint()  { return hint; }
    }

    // =========================================================
    // ================ CONFIGURAÇÃO DO CLIENTE ================
    // =========================================================

    /**
     * Carrega TODAS as propriedades do config.properties.
     * Se o arquivo não existir, retorna um Properties vazio.
     */
    public static Properties loadConfigProperties() throws IOException {
        Properties props = new Properties();

        if (Files.exists(CONFIG_FILE)) {
            try (InputStream in = Files.newInputStream(CONFIG_FILE, StandardOpenOption.READ)) {
                props.load(in);
            }
            System.out.println("[DEBUG] Lendo config.properties em: " + CONFIG_FILE);
        } else {
            System.out.println("[DEBUG] config.properties ainda não existe em: " + CONFIG_FILE);
        }

        return props;
    }

    /**
     * Salva TODAS as propriedades no config.properties.
     * Sobrescreve o arquivo inteiro.
     */
    public static void saveConfigProperties(Properties props) throws IOException {
        try {
            ensureConfigDir();

            try (OutputStream out = Files.newOutputStream(
                    CONFIG_FILE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {
                props.store(out, "Configurações do FileWatcher");
            }

            System.out.println("[DEBUG] Arquivo config.properties salvo em: " + CONFIG_FILE);
        } catch (Exception e) {
            throw new IOException("Erro ao salvar config.properties: " + e.getMessage(), e);
        }
    }

    /**
     * Helper específico para atualizar apenas os dados do cliente
     * sem perder outras propriedades que possam existir.
     */
    public static void saveClientConfig(String nome, String id, String dataContrato) throws IOException {
        Properties props = loadConfigProperties(); // carrega o que já existir

        props.setProperty("cliente.nome", nome);
        props.setProperty("cliente.id", id);
        props.setProperty("cliente.data_contrato", dataContrato);

        saveConfigProperties(props);
    }

    public static boolean configFileExists() {
        return Files.exists(CONFIG_FILE);
    }

    // Expor o caminho do arquivo para debug / UI
    public static Path getConfigFilePath() {
        return CONFIG_FILE;
    }

    // Verifica se as credenciais já foram criadas (primeiro acesso ou não)
    public static boolean keyExists() {
        // Considera que só existe admin configurado se tiver login/dica e senha criptografada
        
        System.out.println("----------------------------------------------------");
        System.out.println("UID_APP: " + UID_APP + " " + Files.exists(UID_APP));
        System.out.println("UID_PASS: " + UID_PASS + " " + Files.exists(UID_PASS));
        System.out.println("----------------------------------------------------");
        
        return Files.exists(UID_APP) && Files.exists(UID_PASS);
    }
}
