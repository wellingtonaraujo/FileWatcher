package com.wellington.filewatcher;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AdminLoginDialog extends JDialog {

    private boolean autenticado = false;

    public AdminLoginDialog(Frame parent) {
        super(parent, "Autenticação do Administrador", true);

        boolean firstAccess = !AppConfig.keyExists();

        // Layout adaptável
        setLayout(new GridLayout(firstAccess ? 5 : 3, 2, 10, 10));

        JLabel lblUser = new JLabel("Login:");
        JTextField txtUser = new JTextField();

        JLabel lblPass = new JLabel("Senha:");
        JPasswordField txtPass = new JPasswordField();

        JLabel lblConfirmPass = new JLabel("Confirmar Senha:");
        JPasswordField txtConfirmPass = new JPasswordField();

        JLabel lblHint = new JLabel("Dica da Senha:");
        JTextField txtHint = new JTextField();

        JButton btnLogin = new JButton(firstAccess ? "Criar Administrador" : "Entrar");
        JButton btnCancel = new JButton("Cancelar");

        // Adiciona campos conforme o modo
        add(lblUser);
        add(txtUser);
        add(lblPass);
        add(txtPass);

        if (firstAccess) {
            add(lblConfirmPass);
            add(txtConfirmPass);
            add(lblHint);
            add(txtHint);
        }

        add(btnLogin);
        add(btnCancel);

        // --- Ações ---
        btnLogin.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Primeiro acesso: criar credenciais
                if (firstAccess) {
                    String confirm = new String(txtConfirmPass.getPassword());
                    String hint = txtHint.getText().trim();

                    if (!pass.equals(confirm)) {
                        JOptionPane.showMessageDialog(this, "As senhas não coincidem!", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (hint.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Informe uma dica para a senha!", "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    AppConfig.saveAdminCredentials(user, pass, hint);
                    JOptionPane.showMessageDialog(this, "Administrador criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    autenticado = true;
                    dispose();
                    return;
                }

                // Caso já exista o arquivo → autenticação normal
                AppConfig.AdminCredentials creds = AppConfig.loadAdminCredentials();
                if (creds == null) {
                    JOptionPane.showMessageDialog(this, "Credenciais não encontradas!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                
                

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao acessar credenciais: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dispose());

        setSize(350, firstAccess ? 250 : 150);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public boolean isAutenticado() {
        return autenticado;
    }
}
