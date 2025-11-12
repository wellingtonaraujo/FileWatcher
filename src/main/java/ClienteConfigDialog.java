package com.wellington.filewatcher;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class ClienteConfigDialog extends JDialog {

    private boolean confirmado = false;
    private final Properties clienteProps = new Properties();

    public ClienteConfigDialog(Frame parent) {
        super(parent, "Configuração do Cliente", true);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel lblNome = new JLabel("Nome do Cliente:");
        JLabel lblId = new JLabel("ID (CNPJ ou CPF):");
        JLabel lblData = new JLabel("Data do Contrato (AAAA-MM-DD):");

        JTextField txtNome = new JTextField();
        JTextField txtId = new JTextField();
        JTextField txtData = new JTextField();

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        add(lblNome);
        add(txtNome);
        add(lblId);
        add(txtId);
        add(lblData);
        add(txtData);
        add(btnSalvar);
        add(btnCancelar);

        btnSalvar.addActionListener(e -> {
            if (txtNome.getText().isEmpty() || txtId.getText().isEmpty() || txtData.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            clienteProps.setProperty("cliente.nome", txtNome.getText().trim());
            clienteProps.setProperty("cliente.id", txtId.getText().replaceAll("\\D", "")); // somente números
            clienteProps.setProperty("cliente.data_contrato", txtData.getText().trim());

            confirmado = true;
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());

        setSize(400, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Properties getClienteProps() {
        return clienteProps;
    }
}
