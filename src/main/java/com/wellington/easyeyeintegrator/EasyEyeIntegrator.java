package com.wellington.easyeyeintegrator;

import com.wellington.easyeyeintegrator.view.FileInfoDialog;
import com.wellington.easyeyeintegrator.config.ClienteConfigDialog;
import com.wellington.easyeyeintegrator.config.AppConfig;
import com.wellington.easyeyeintegrator.config.ConfigUtil;
import com.wellington.easyeyeintegrator.controller.AdminLoginController;
import com.wellington.easyeyeintegrator.dto.AuthorizationResult;
import com.wellington.easyeyeintegrator.service.ApiServices;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class EasyEyeIntegrator {

    private static final String MONITORED_FOLDER =
            ConfigUtil.getMonitoredDir().toString();

    public static void main(String[] args) {

        try {
            inicializarDiretorios();

            logAmbiente();

            // 1️⃣ Autorização via API
            AuthorizationResult result = ApiServices.authorizeAccess();

            if (!result.isAuthorized()) {
                encerrarSistema(result.getReason());
                return;
            }

            // 2️⃣ Primeiro acesso
            if (isFirstAccess()) {
                executarPrimeiroAcesso();
            }

            // 3️⃣ Inicializa System Tray
            iniciarSystemTray();

            // 4️⃣ Inicia WatchService
            iniciarWatchService();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= FLUXO =================

    private static boolean isFirstAccess() {
        return !AppConfig.keyExists();
    }

    private static void executarPrimeiroAcesso() {

        System.out.println("🚀 Primeira execução detectada");

        AdminLoginController auth = new AdminLoginController();

        try {
            boolean autenticado = auth.exigirAutenticacao();

            if (!autenticado) {
                encerrarSistema("Autenticação cancelada pelo usuário");
                return;
            }

            ClienteConfigDialog dialog =
                    new ClienteConfigDialog(null);

            dialog.setVisible(true);

            if (!dialog.isConfirmado()) {
                encerrarSistema("Configuração do cliente cancelada");
                return;
            }

            salvarConfigProperties(
                    ConfigUtil.getConfigDir(),
                    dialog.getClienteProps()
            );

            System.out.println("✅ config.properties criado com sucesso");

        } catch (Exception e) {
            encerrarSistema("Erro durante autenticação inicial");
        }
    }

    // ================= INFRA =================

    private static void inicializarDiretorios() throws IOException {
        Files.createDirectories(ConfigUtil.getConfigDir());
    }

    private static void logAmbiente() {
        System.out.println("----------------------------------------------------");
        System.out.println("Monitorando a pasta: " + MONITORED_FOLDER);
        System.out.println("Config dir: " + ConfigUtil.getConfigDir());
        System.out.println("----------------------------------------------------");
    }

    private static void iniciarSystemTray() {
        SwingUtilities.invokeLater(() -> {
            SystemTrayHelper trayHelper = new SystemTrayHelper();
            trayHelper.initializeTray();
        });
    }

    private static void iniciarWatchService()
            throws IOException, InterruptedException {

        Path path = Paths.get(MONITORED_FOLDER);
        WatchService watchService =
                FileSystems.getDefault().newWatchService();

        path.register(watchService, ENTRY_CREATE);

        System.out.println("👀 Monitorando a pasta: " + MONITORED_FOLDER);

        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == ENTRY_CREATE) {
                    Path newFile =
                            path.resolve((Path) event.context());

                    System.out.println("📂 Novo arquivo: " + newFile);

                    SwingUtilities.invokeLater(() -> {
                        FileInfoDialog dialog =
                                new FileInfoDialog(newFile.toFile());
                        dialog.setVisible(true);
                    });
                }
            }

            if (!key.reset()) {
                break;
            }
        }
    }

    private static void encerrarSistema(String motivo) {
        System.out.println("❌ Sistema não iniciado");
        System.out.println("Motivo: " + motivo);
        // 🔹 Janela
        JOptionPane.showMessageDialog(
            null,
            motivo + "\n O sistema não conseguiu conexão com a base de dados web.",
            "Acesso negado",
            JOptionPane.ERROR_MESSAGE
        );
        System.exit(0);
    }

    private static void salvarConfigProperties(
            Path configDir,
            Properties props
    ) throws IOException {

        File file = configDir
                .resolve("config.properties")
                .toFile();

        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, "Configurações do cliente");
        }
    }
}
