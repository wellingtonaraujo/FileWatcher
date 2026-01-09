package com.wellington.filewatcher.view;

import com.wellington.filewatcher.controller.AdminLoginController;

import javax.swing.*;
import java.awt.*;

public class AdminLoginDialog extends JDialog {

    private final JTextField txtUser = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();
    private final JPasswordField txtConfirmPass = new JPasswordField();
    private final JTextField txtHint = new JTextField();
    private final JTextField txtEmail = new JTextField();

    private boolean autenticado = false;

    private final AdminLoginController controller;

    public AdminLoginDialog(Frame parent, AdminLoginController controller) {
        super(parent, "Autenticação do Administrador", true);
        this.controller = controller;

        boolean firstAccess = controller.isFirstAccess();

        setLayout(new GridLayout(firstAccess ? 6 : 3, 2, 10, 10));

        add(label("Login:"));
        add(txtUser);

        add(label("Senha:"));
        add(txtPass);

        if (firstAccess) {
            add(label("Confirmar Senha:"));
            add(txtConfirmPass);

            add(label("Dica da Senha:"));
            add(txtHint);
            
            add(label("E-mail de recuperação:"));
            add(txtEmail);
        }        

        JButton btnOk = new JButton(firstAccess ? "Criar Administrador" : "Entrar");
        JButton btnCancel = new JButton("Cancelar");

        btnOk.addActionListener(e -> controller.handleLogin(this));
        btnCancel.addActionListener(e -> dispose());

        add(btnOk);
        add(btnCancel);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(360, firstAccess ? 260 : 170);
        setLocationRelativeTo(parent);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }

    // ===== getters usados pelo controller =====

    public String getUser() {
        return txtUser.getText().trim();
    }

    public String getPassword() {
        return new String(txtPass.getPassword());
    }

    public String getConfirmPassword() {
        return new String(txtConfirmPass.getPassword());
    }

    public String getHint() {
        return txtHint.getText().trim();
    }

    // ===== controle de autenticação =====

    public void autenticarComSucesso() {
        this.autenticado = true;
        dispose();
    }

    public boolean isAutenticado() {
        return autenticado;
    }
}
