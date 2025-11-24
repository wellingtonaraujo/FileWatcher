package com.wellington.filewatcher;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.*;

public class SystemTrayHelper {

    public static void initializeTray() {
        // Verifica se o sistema suporta a bandeja
        if (!SystemTray.isSupported()) {
            System.out.println("❌ SystemTray não suportado neste sistema.");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().getImage(
                SystemTrayHelper.class.getResource("/images/icon.png")
        );

        PopupMenu popup = new PopupMenu();

        // -----------------------------------------
        // Abrir pasta monitorada (com login/senha master)
        // -----------------------------------------
        MenuItem openFolderItem = new MenuItem("Abrir pasta monitorada");
        openFolderItem.addActionListener(e ->
                SwingUtilities.invokeLater(() -> {
                    if (!validarAdminLoginESenha()) return;

                    try {
                        Desktop.getDesktop().open(new File("C:\\FileWatcher\\Monitored"));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Erro ao abrir pasta: " + ex.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                })
        );
        popup.add(openFolderItem);

        popup.addSeparator();

        // -----------------------------------------
        // Abrir Configurações (ClienteConfigDialog) – exige login/senha master
        // -----------------------------------------
        MenuItem openConfigItem = new MenuItem("Configurações");
        openConfigItem.addActionListener(e ->
                SwingUtilities.invokeLater(() -> {
                    if (!validarAdminLoginESenha()) return;

                    ClienteConfigDialog dialog = new ClienteConfigDialog((Frame) null);
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                })
        );
        popup.add(openConfigItem);

        // -----------------------------------------
        // Trocar senha do usuário master
        // (login + senha antiga + nova + confirmação)
        // -----------------------------------------
        MenuItem changePasswordItem = new MenuItem("Trocar senha do usuário");
        changePasswordItem.addActionListener(e ->
                SwingUtilities.invokeLater(SystemTrayHelper::mostrarDialogTrocaSenha)
        );
        popup.add(changePasswordItem);

        popup.addSeparator();

        // -----------------------------------------
        // Sair – também exige login/senha master
        // -----------------------------------------
        MenuItem exitItem = new MenuItem("Sair");
        exitItem.addActionListener(e -> {
            //if (!validarAdminLoginESenha()) return;

            TrayIcon[] icons = tray.getTrayIcons();
            if (icons.length > 0) {
                tray.remove(icons[0]);
            }
            System.exit(0);
        });
        popup.add(exitItem);

        // TrayIcon
        TrayIcon trayIcon = new TrayIcon(image, "FileWatcher", popup);
        trayIcon.setImageAutoSize(true);

        // Clique duplo – só exibe mensagem se login/senha master forem válidos
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    SwingUtilities.invokeLater(() -> {
                        if (!validarAdminLoginESenha()) return;

                        JOptionPane.showMessageDialog(
                                null,
                                "FileWatcher está rodando!",
                                "FileWatcher",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    });
                }
            }
        });

        try {
            tray.add(trayIcon);
            System.out.println("✅ Ícone adicionado à bandeja do sistema.");
        } catch (AWTException e) {
            System.out.println("Erro ao adicionar ícone à bandeja: " + e.getMessage());
        }
    }

    // =========================================================
    // Validação simples: login + senha master (para acessar ações)
    // =========================================================
    private static boolean validarAdminLoginESenha() {
        AppConfig.AdminCredentials creds;
        try {
            creds = AppConfig.loadAdminCredentials();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao carregar credenciais do administrador:\n" + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (creds == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Nenhum usuário master configurado.\n" +
                    "Configure o usuário administrador antes de usar o menu da bandeja.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        JTextField txtLogin = new JTextField();
        JPasswordField txtSenha = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Login do administrador:"));
        panel.add(txtLogin);
        panel.add(new JLabel("Senha:"));
        panel.add(txtSenha);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Autenticação do usuário master",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String loginDigitado = txtLogin.getText().trim();
        char[] senhaChars = txtSenha.getPassword();
        String senhaDigitada = new String(senhaChars);
        java.util.Arrays.fill(senhaChars, '\0');

        if (!loginDigitado.equals(creds.getLogin()) ||
            !senhaDigitada.equals(creds.getSenha())) {

            JOptionPane.showMessageDialog(
                    null,
                    "Login ou senha inválidos.",
                    "Autenticação falhou",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }

    // =========================================================
    // Trocar senha: login + senha antiga + nova + confirmação
    // =========================================================
    private static void mostrarDialogTrocaSenha() {
        AppConfig.AdminCredentials creds;
        try {
            creds = AppConfig.loadAdminCredentials();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao carregar credenciais do administrador:\n" + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (creds == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Nenhum usuário master configurado.\n" +
                    "Configure o usuário administrador antes de trocar a senha.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JTextField txtLogin = new JTextField();
        JPasswordField txtSenhaAtual = new JPasswordField();
        JPasswordField txtNovaSenha = new JPasswordField();
        JPasswordField txtConfirmaSenha = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Login do administrador:"));
        panel.add(txtLogin);
        panel.add(new JLabel("Senha atual:"));
        panel.add(txtSenhaAtual);
        panel.add(new JLabel("Nova senha:"));
        panel.add(txtNovaSenha);
        panel.add(new JLabel("Confirmar nova senha:"));
        panel.add(txtConfirmaSenha);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Trocar senha do usuário master",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String loginDigitado = txtLogin.getText().trim();
        char[] senhaAtual = txtSenhaAtual.getPassword();
        char[] novaSenha = txtNovaSenha.getPassword();
        char[] confirmaSenha = txtConfirmaSenha.getPassword();

        try {
            // Valida login
            if (!loginDigitado.equals(creds.getLogin())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Login informado não confere com o usuário master.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String senhaAtualStr = new String(senhaAtual);

            // Valida senha atual
            if (!senhaAtualStr.equals(creds.getSenha())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Senha atual inválida.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (novaSenha.length == 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "A nova senha não pode ser vazia.",
                        "Atenção",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!java.util.Arrays.equals(novaSenha, confirmaSenha)) {
                JOptionPane.showMessageDialog(
                        null,
                        "Nova senha e confirmação não conferem.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String novaSenhaStr = new String(novaSenha);

            // Salva nova senha mantendo login e dica
            AppConfig.saveAdminCredentials(
                    creds.getLogin(),
                    novaSenhaStr,
                    creds.getHint()
            );

            JOptionPane.showMessageDialog(
                    null,
                    "Senha alterada com sucesso.",
                    "Informação",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao alterar a senha: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            java.util.Arrays.fill(senhaAtual, '\0');
            java.util.Arrays.fill(novaSenha, '\0');
            java.util.Arrays.fill(confirmaSenha, '\0');
        }
    }
}
