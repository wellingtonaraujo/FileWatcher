package com.wellington.filewatcher;

import java.util.Properties;
import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

import javax.swing.SwingUtilities;

import com.wellington.filewatcher.controller.AdminLoginController;

import java.io.File;
import java.io.FileWriter;

public class FileWatcher {

    private static final String MONITORED_FOLDER =
            ConfigUtil.getMonitoredDir() + "";

    public static void main(String[] args) {

        try {
            // 1) Diretório de configuração
            Path configDir = ConfigUtil.getConfigDir();
            Files.createDirectories(configDir);

            Path configPath = configDir.resolve("config.properties");
            File configFile = configPath.toFile();

            System.out.println("----------------------------------------------------");
            System.out.println("Monitorando a pasta: " + MONITORED_FOLDER);
            System.out.println("Caminho do Config: " + configPath);
            System.out.println("Config existe? " + configFile.exists());
            System.out.println("----------------------------------------------------");

            boolean firstAccess = !AppConfig.keyExists();
            System.out.println("Primeiro acesso: " + firstAccess);

            // 2) Primeiro acesso → exige autenticação + configura cliente
            if (firstAccess) {
                System.out.println("Primeira execução detectada");

                AdminLoginController auth = new AdminLoginController();

                try {
                    auth.exigirAutenticacao(); // 🔐 sempre exige login
                } catch (Exception e) {
                    System.out.println("Autenticação cancelada");
                    System.exit(0);
                }

                ClienteConfigDialog clienteDialog =
                        new ClienteConfigDialog(null);

                clienteDialog.setVisible(true);

                if (clienteDialog.isConfirmado()) {
                    salvarConfigProperties(
                            configDir,
                            clienteDialog.getClienteProps()
                    );
                    System.out.println("✅ config.properties criado com sucesso");
                } else {
                    System.out.println("⚠️ Operação cancelada");
                    System.exit(0);
                }
            }

            // 3) Inicializa SystemTray
            SwingUtilities.invokeLater(() -> {
                SystemTrayHelper trayHelper = new SystemTrayHelper();
                trayHelper.initializeTray();
            });

            // 4) WatchService
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

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void salvarConfigProperties(
            Path configDir,
            Properties props
    ) throws IOException {

        Files.createDirectories(configDir);

        File file = configDir
                .resolve("config.properties")
                .toFile();

        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, "Configurações do cliente");
        }
    }
}
