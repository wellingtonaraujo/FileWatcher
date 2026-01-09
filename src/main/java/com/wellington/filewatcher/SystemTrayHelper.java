package com.wellington.filewatcher;

import com.wellington.filewatcher.controller.AdminLoginController;

import javax.swing.*;
import java.awt.*;

public class SystemTrayHelper {

    public void initializeTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray não suportado.");
            return;
        }

        PopupMenu menu = new PopupMenu();

        MenuItem configItem = new MenuItem("Configurações");
        MenuItem exitItem = new MenuItem("Sair");

        configItem.addActionListener(e -> abrirConfiguracoes());
        exitItem.addActionListener(e -> System.exit(0));

        menu.add(configItem);
        menu.addSeparator();
        menu.add(exitItem);

        // Carrega o ícone do resources
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

        // 👉 aqui você abre a tela real de configurações
        JOptionPane.showMessageDialog(
                null,
                "Administrador autenticado com sucesso!",
                "Configurações",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
