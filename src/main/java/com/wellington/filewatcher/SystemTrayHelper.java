package com.wellington.filewatcher;

import com.wellington.filewatcher.controller.AdminLoginController;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
}
