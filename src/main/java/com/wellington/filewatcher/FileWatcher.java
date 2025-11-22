package com.wellington.filewatcher;

import java.util.Properties;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import javax.swing.SwingUtilities;

import com.wellington.filewatcher.AppConfig;
import com.wellington.filewatcher.ClienteConfigDialog;
import com.wellington.filewatcher.AdminLoginDialog;
import com.wellington.filewatcher.ConfigUtil;

import java.io.File;
import java.io.FileWriter;

public class FileWatcher {

    private static final String MONITORED_FOLDER = "C:\\FileWatcher\\Monitored"; // Caminho da pasta a ser monitorada    

    public static void main(String[] args) {

        try {
            // 1) Descobrir diretório de configuração do usuário final
            Path configDir = ConfigUtil.getConfigDir();
            
            Files.createDirectories(configDir);

            Path configPath = configDir.resolve("config.properties");
            File configFile = configPath.toFile();   // ✅ agora compila

            System.out.println("Config em: " + configPath);
            System.out.println("Existe? " + configFile.exists());

            // 2) Primeira execução: não existe config.properties
            if (!configFile.exists()) {
                System.out.println("Primeira execução detectada - iniciando a configuração...");

                // 2.1) Autenticação do administrador (cria usuário master)
                AdminLoginDialog loginDialog = new AdminLoginDialog(null);
                loginDialog.setVisible(true);

                if (!loginDialog.isAutenticado()) {
                    System.out.println("❌ Autenticação falhou. Encerrando.");
                    System.exit(0);
                }

                // 2.2) Formulário de dados do cliente (gera as propriedades)
                ClienteConfigDialog clienteDialog = new ClienteConfigDialog(null);
                clienteDialog.setVisible(true);

                if (clienteDialog.isConfirmado()) {
                    salvarConfigProperties(configDir, clienteDialog.getClienteProps());
                    System.out.println("✅ Arquivo config.properties criado com sucesso em: " + configPath);
                } else {
                    System.out.println("⚠️ Operação cancelada pelo administrador.");
                    System.exit(0);
                }
            }

            // 3) Daqui pra frente, o sistema já tem config.properties garantido
            //    Aqui você pode carregar AppConfig, iniciar SystemTrayHelper, monitor etc.

            SystemTrayHelper.initializeTray();

            Path path = Paths.get(MONITORED_FOLDER);
            WatchService watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, ENTRY_CREATE);

            System.out.println("👀 Monitorando a pasta: " + MONITORED_FOLDER);

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == ENTRY_CREATE) {
                        Path newFile = path.resolve((Path) event.context());
                        System.out.println("📂 Novo arquivo detectado: " + newFile);

                        SwingUtilities.invokeLater(() -> {
                            FileInfoDialog dialog = new FileInfoDialog(newFile.toFile());
                            dialog.setVisible(true);
                        });
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

            
    private static void salvarConfigProperties(Path configDir, Properties props) throws IOException {
        Files.createDirectories(configDir);

        File file = configDir.resolve("config.properties").toFile();  // ✅ converte Path → File
        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, "Configurações do cliente");
        }
    }
}
