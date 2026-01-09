package com.wellington.filewatcher.controller;

import java.io.IOException;
import com.wellington.filewatcher.AppConfig;
import com.wellington.filewatcher.view.AdminLoginDialog;

import javax.swing.*;
import java.awt.*;

public class AdminLoginController {

    public boolean exigirAutenticacao() {
        Frame parent = null; // ou sua JFrame principal
        AdminLoginDialog dialog = new AdminLoginDialog(parent, this);
        dialog.setVisible(true);
        return dialog.isAutenticado();
    }

    public void handleLogin(AdminLoginDialog dialog) {

        boolean firstAccess = isFirstAccess();

        String login = dialog.getUser();
        String senha = dialog.getPassword();

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                    "Informe login e senha",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (firstAccess) {

            if (!senha.equals(dialog.getConfirmPassword())) {
                JOptionPane.showMessageDialog(dialog,
                        "As senhas não conferem",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                AppConfig.saveAdminCredentials(login, senha, dialog.getHint());

                JOptionPane.showMessageDialog(dialog,
                        "Administrador criado com sucesso!");

                dialog.autenticarComSucesso();
                return;

            } catch (IOException e) {
                JOptionPane.showMessageDialog(dialog,
                        "Erro ao salvar as credenciais do administrador.\n" +
                        "Verifique permissões do sistema.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);

                e.printStackTrace(); // útil em desenvolvimento
            }
        }


        if (AppConfig.validateAdmin(login, senha)) {
            dialog.autenticarComSucesso();
        } else {
            JOptionPane.showMessageDialog(dialog,
                    "Login ou senha inválidos",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isFirstAccess() {
        return !AppConfig.keyExists();
    }
}
