package com.wellington.filewatcher;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

public class SystemTrayHelper {

    public static void initializeTray() {
        // Verifica se o sistema suporta a bandeja
        if (!SystemTray.isSupported()) {
            System.out.println("❌ SystemTray não suportado neste sistema.");
            return;
        }

        // Obtém a bandeja do sistema
        SystemTray tray = SystemTray.getSystemTray();

        // Cria o ícone (pode ser substituído por um ícone seu)
        Image image = Toolkit.getDefaultToolkit().getImage(SystemTrayHelper.class.getResource("/images/icon.png"));

        // Cria menu popup
        PopupMenu popup = new PopupMenu();

        // Opção: abrir pasta monitorada
        MenuItem openFolderItem = new MenuItem("Abrir pasta monitorada");
        openFolderItem.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File("C:\\FileWatcher\\Monitored"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro ao abrir pasta: " + ex.getMessage());
            }
        });
        popup.add(openFolderItem);

        // Separador
        popup.addSeparator();

        // Opção: sair
        MenuItem exitItem = new MenuItem("Sair");
        exitItem.addActionListener(e -> {
            tray.remove(tray.getTrayIcons()[0]);
            System.exit(0);
        });
        popup.add(exitItem);

        // Cria o TrayIcon com o menu
        TrayIcon trayIcon = new TrayIcon(image, "FileWatcher", popup);
        trayIcon.setImageAutoSize(true);

        // Ação ao clicar duas vezes
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "FileWatcher está rodando!");
                    });
                }
            }
        });

        // Adiciona o ícone à bandeja
        try {
            tray.add(trayIcon);
            System.out.println("✅ Ícone adicionado à bandeja do sistema.");
        } catch (AWTException e) {
            System.out.println("Erro ao adicionar ícone à bandeja: " + e.getMessage());
        }
    }
}
