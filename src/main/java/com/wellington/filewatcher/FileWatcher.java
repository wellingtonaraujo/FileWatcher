package com.wellington.filewatcher;

import java.util.Properties;
import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import javax.swing.SwingUtilities;

import com.wellington.filewatcher.AppConfig;
import com.wellington.filewatcher.ClienteConfigDialog;
import com.wellington.filewatcher.AdminLoginDialog;

import java.io.File;
import java.io.FileWriter;

public class FileWatcher {

    private static final String MONITORED_FOLDER = "C:\\FileWatcher\\Monitored"; // Caminho da pasta a ser monitorada    

    public static void main(String[] args) {        
        
        try {
            
            String basePath  = System.getProperty("user.dir") + "/src/main/resources/config/";
            File configFile = new File(basePath + "config.properties");
            //Caso não exista o arquivo de configurações
            if(!configFile.exists()){
                System.out.println("Primeira execução detectada - iniciando a configuração...");
                
                //Autenticação do administrador
                AdminLoginDialog loginDialog = new AdminLoginDialog(null);
                loginDialog.setVisible(true);
                
                if (!loginDialog.isAutenticado()) {
                    System.out.println("❌ Autenticação falhou. Encerrando.");
                    System.exit(0);
                }
                
                // 2️⃣ Formulário de dados do cliente
                ClienteConfigDialog clienteDialog = new ClienteConfigDialog(null);
                clienteDialog.setVisible(true);

                if (clienteDialog.isConfirmado()) {
                    salvarConfigProperties(basePath, clienteDialog.getClienteProps());
                    System.out.println("✅ Arquivo config.properties criado com sucesso!");
                } else {
                    System.out.println("⚠️ Operação cancelada pelo administrador.");
                    System.exit(0);
                }
            }
            
            //Executa a aplicação
            
            SystemTrayHelper.initializeTray();
            Path path = Paths.get(MONITORED_FOLDER);
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // Registrar eventos de criação, modificação e deleção
            path.register(watchService, ENTRY_CREATE);

            System.out.println("👀 Monitorando a pasta: " + MONITORED_FOLDER);

            // Loop infinito que escuta mudanças
            while (true) {
                WatchKey key = watchService.take(); // Espera evento

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == ENTRY_CREATE) {
                        Path newFile = path.resolve((Path) event.context());
                        System.out.println("📂 Novo arquivo detectado: " + newFile);

                        // Exibe popup em thread de interface
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
            
    private static void salvarConfigProperties(String basePath, Properties props) throws IOException {
        File dir = new File(basePath);
        if (!dir.exists()) dir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(dir, "config.properties"))) {
            props.store(writer, "Configurações do cliente");
        }
    }
}
