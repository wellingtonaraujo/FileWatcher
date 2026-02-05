package com.wellington.easyeyeintegrator;

import com.wellington.easyeyeintegrator.config.ClienteConfigDialog;
import com.wellington.easyeyeintegrator.controller.AdminLoginController;
import com.wellington.easyeyeintegrator.service.ApiServices;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class SystemTrayHelper {

    public void initializeTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray não suportado.");
            return;
        }

        PopupMenu menu = new PopupMenu();

        // -----------------------------------------
        // Menu Pasta Monitorada (antes de Configurações)
        // -----------------------------------------
        MenuItem monitoredFolder = new MenuItem("Pasta padrão");
        monitoredFolder.addActionListener(e -> openMonitoredFolder());
        menu.add(monitoredFolder);
        menu.addSeparator();

        // -----------------------------------------
        // Menu Configurações
        // -----------------------------------------
        MenuItem configItem = new MenuItem("Configurações");        

        configItem.addActionListener(e -> abrirConfiguracoes());
        menu.add(configItem);
        
        // -----------------------------------------
        // Menu trocar senha
        // -----------------------------------------
        MenuItem trocarSenha = new MenuItem("Trocar Senha");        
        trocarSenha.addActionListener(e -> trocarSenha());
        
        menu.add(trocarSenha);
        menu.addSeparator();
        
        // -----------------------------------------
        // Menu teste do correio
        // -----------------------------------------
        
        MenuItem meuEndereco = new MenuItem("Meu endereco");        
        meuEndereco.addActionListener(e -> meuEndereco());
        
        menu.add(meuEndereco);
        menu.addSeparator();
        
        // -----------------------------------------
        // Menu teste de autenticação de API
        // -----------------------------------------
        
        MenuItem testarApi = new MenuItem("Testar API");
        testarApi.addActionListener(e -> ApiServices.authorizeAccess());
        menu.add(testarApi);
        
        // -----------------------------------------
        // Menu Sair do sistema
        // -----------------------------------------
        
        MenuItem exitItem = new MenuItem("Sair");
        exitItem.addActionListener(e -> System.exit(0));
        menu.add(exitItem);

        // -----------------------------------------
        // Ícone do SystemTray
        // -----------------------------------------
        Image image = Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/images/icon.png")
        );
        TrayIcon trayIcon = new TrayIcon(image, "FileWatcher", menu);
        trayIcon.setImageAutoSize(true);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------
    // Método modular para abrir a pasta monitorada
    // -----------------------------------------
    private void openMonitoredFolder() {
        SwingUtilities.invokeLater(() -> {
            File folder = new File("C:\\FileWatcher\\Monitored");

            if (!folder.exists() || !folder.isDirectory()) {
                JOptionPane.showMessageDialog(
                        null,
                        "A pasta não existe: " + folder.getAbsolutePath(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try {
                Desktop.getDesktop().open(folder);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Erro ao abrir a pasta: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    // -----------------------------------------
    // Método para abrir a tela de configurações
    // -----------------------------------------
    private void abrirConfiguracoes() {
        AdminLoginController auth = new AdminLoginController();

        boolean autenticado = auth.exigirAutenticacao();

        if (!autenticado) {
            JOptionPane.showMessageDialog(
                    null,
                    "Acesso negado.",
                    "Autenticação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        SwingUtilities.invokeLater(() -> {
            ClienteConfigDialog dialog = new ClienteConfigDialog((Frame) null);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }
    
    //metodo trocar senha
    private void trocarSenha(){
        JOptionPane.showMessageDialog(
                    null,
                    "Médoto para trocar senha",
                    "Resset Password",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
    }
    
    private void meuEndereco() {
        SwingUtilities.invokeLater(() -> {
            try {
                String cep = "68903740";
                String urlStr = "https://viacep.com.br/ws/" + cep + "/json/";

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8")
                );

                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                JSONObject obj = new JSONObject(json.toString());

                if (obj.has("erro")) {
                    JOptionPane.showMessageDialog(
                            null,
                            "CEP não encontrado.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                String endereco =
                        "CEP: " + obj.getString("cep") + "\n" +
                        "Logradouro: " + obj.getString("logradouro") + "\n" +
                        "Bairro: " + obj.getString("bairro") + "\n" +
                        "Cidade: " + obj.getString("localidade") + "\n" +
                        "UF: " + obj.getString("uf");

                JOptionPane.showMessageDialog(
                        null,
                        endereco,
                        "Meu Endereço",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Erro ao consultar o CEP:\n" + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

}
