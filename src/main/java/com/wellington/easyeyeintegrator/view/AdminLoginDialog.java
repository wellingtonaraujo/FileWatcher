package com.wellington.easyeyeintegrator.view;

import com.wellington.easyeyeintegrator.controller.AdminLoginController;

import javax.swing.*;
import java.awt.*;

public class AdminLoginDialog extends JDialog {

    private final JTextField txtUser = new JTextField();
    private final JTextField txtCode = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();
    private final JPasswordField txtConfirmPass = new JPasswordField();
    private final JTextField txtHint = new JTextField();
    private final JTextField txtEmail = new JTextField();

    private boolean autenticado = false;

    private final AdminLoginController controller;

    public AdminLoginDialog(Frame parent, AdminLoginController controller) {
        super(parent, "Autenticação do Administrador", true);
        this.controller = controller;

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        row = addRow(form, gbc, row, "E-mail:", txtUser);
        row = addRow(form, gbc, row, "Senha:", txtPass);
        row = addRow(form, gbc, row, "Código:", txtCode);

        JButton btnOk = new JButton(
            controller.isFirstAccess() ? "Criar Administrador" : "Entrar"
        );
        JButton btnCancel = new JButton("Cancelar");

        btnOk.addActionListener(e -> controller.handleLogin(this));
        btnCancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(btnOk);
        buttons.add(btnCancel);

        content.add(form, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);

        setContentPane(content);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    // Helper para adicionar uma linha "Label + Campo"
    private int addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1; // campo ocupa o resto
        panel.add(field, gbc);

        return row + 1;
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
