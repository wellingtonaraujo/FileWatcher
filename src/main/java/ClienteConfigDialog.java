package com.wellington.filewatcher;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;

public class ClienteConfigDialog extends JDialog {

    private boolean confirmado = false;
    private final Properties clienteProps = new Properties();

    private JTextField txtNome;
    private JTextField txtId;
    private JTextField txtData;

    public ClienteConfigDialog(Frame parent) {
        super(parent, "Configuração do Cliente", true);

        initComponents();
        carregarConfiguracoes(); // <<< carrega AO ABRIR

        setSize(400, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel lblNome = new JLabel("Nome do Cliente:");
        JLabel lblId   = new JLabel("ID (CNPJ ou CPF):");
        JLabel lblData = new JLabel("Data do Contrato (AAAA-MM-DD):");

        txtNome = new JTextField();
        txtId   = new JTextField();
        txtData = new JTextField();

        JButton btnSalvar   = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        add(lblNome);
        add(txtNome);
        add(lblId);
        add(txtId);
        add(lblData);
        add(txtData);
        add(btnSalvar);
        add(btnCancelar);

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void carregarConfiguracoes() {
        try {
            Properties props = AppConfig.loadConfigProperties();

            System.out.println("[DEBUG] Lendo config.properties em: " + AppConfig.getConfigFilePath());
            System.out.println("[DEBUG] cliente.nome=" + props.getProperty("cliente.nome"));
            System.out.println("[DEBUG] cliente.id=" + props.getProperty("cliente.id"));
            System.out.println("[DEBUG] cliente.data_contrato=" + props.getProperty("cliente.data_contrato"));

            clienteProps.clear();
            clienteProps.putAll(props);

            txtNome.setText(props.getProperty("cliente.nome", ""));
            txtId.setText(props.getProperty("cliente.id", ""));
            txtData.setText(props.getProperty("cliente.data_contrato", ""));

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível carregar as configurações:\n" + ex.getMessage(),
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void salvar() {
        if (txtNome.getText().isEmpty()
                || txtId.getText().isEmpty()
                || txtData.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos os campos são obrigatórios.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome = txtNome.getText().trim();
        String id   = txtId.getText().replaceAll("\\D", "");
        String data = txtData.getText().trim();

        clienteProps.setProperty("cliente.nome", nome);
        clienteProps.setProperty("cliente.id", id);
        clienteProps.setProperty("cliente.data_contrato", data);

        try {
            AppConfig.saveClientConfig(nome, id, data);

            confirmado = true;
            JOptionPane.showMessageDialog(this,
                    "Configurações salvas com sucesso!",
                    "Informação",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar configurações:\n" + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Properties getClienteProps() {
        return clienteProps;
    }
}
