package com.wellington.filewatcher;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class ClienteConfigDialog extends JDialog {

    private boolean confirmado = false;
    private final Properties clienteProps = new Properties();

    private JTextField txtNome;
    private JTextField txtId;
    private JTextField txtData;
    private JTextField txtBackup;
    private JButton btnSelecionarBackup;

    public ClienteConfigDialog(Frame parent) {
        super(parent, "Configuração do Cliente", true);

        initComponents();
        carregarConfiguracoes(); // <<< carrega AO ABRIR

        setSize(500, 250);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        // 5 linhas: Nome, ID, Data, Backup, Botões
        setLayout(new GridLayout(5, 2, 10, 10));

        JLabel lblNome    = new JLabel("Nome do Cliente:");
        JLabel lblId      = new JLabel("ID (CNPJ ou CPF):");
        JLabel lblData    = new JLabel("Data do Contrato (AAAA-MM-DD):");
        JLabel lblBackup  = new JLabel("Backup de Exames:");

        txtNome = new JTextField();
        txtId   = new JTextField();
        txtData = new JTextField();

        // Campo para mostrar o caminho do backup
        txtBackup = new JTextField();
        txtBackup.setEditable(false); // usuário escolhe só pelo botão

        btnSelecionarBackup = new JButton("Selecionar...");

        JButton btnSalvar   = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        // Linha 1: Nome
        add(lblNome);
        add(txtNome);

        // Linha 2: ID
        add(lblId);
        add(txtId);

        // Linha 3: Data
        add(lblData);
        add(txtData);

        // Linha 4: Backup (label + painel com campo + botão)
        add(lblBackup);
        JPanel pnlBackup = new JPanel(new BorderLayout(5, 0));
        pnlBackup.add(txtBackup, BorderLayout.CENTER);
        pnlBackup.add(btnSelecionarBackup, BorderLayout.EAST);
        add(pnlBackup);

        // Linha 5: Botões
        add(btnSalvar);
        add(btnCancelar);

        // Ações
        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
        btnSelecionarBackup.addActionListener(e -> selecionarPastaBackup());
    }

    /**
     * Abre um JFileChooser para escolher a pasta de backup e preenche txtBackup.
     */
    private void selecionarPastaBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecione a pasta de backup dos exames");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        // Diretório inicial (opcional)
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        int resultado = chooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File pastaSelecionada = chooser.getSelectedFile();
            if (pastaSelecionada != null) {
                txtBackup.setText(pastaSelecionada.getAbsolutePath());
            }
        }
    }

    private void carregarConfiguracoes() {
        try {
            Properties props = AppConfig.loadConfigProperties();

            System.out.println("[DEBUG] Lendo config.properties em: " + AppConfig.getConfigFilePath());
            System.out.println("[DEBUG] cliente.nome=" + props.getProperty("cliente.nome"));
            System.out.println("[DEBUG] cliente.id=" + props.getProperty("cliente.id"));
            System.out.println("[DEBUG] cliente.data_contrato=" + props.getProperty("cliente.data_contrato"));
            System.out.println("[DEBUG] cliente.backup_exames=" + props.getProperty("cliente.backup_exames"));

            clienteProps.clear();
            clienteProps.putAll(props);

            txtNome.setText(props.getProperty("cliente.nome", ""));
            txtId.setText(props.getProperty("cliente.id", ""));
            txtData.setText(props.getProperty("cliente.data_contrato", ""));
            txtBackup.setText(props.getProperty("cliente.backup_exames", ""));

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

        String nome        = txtNome.getText().trim();
        String id          = txtId.getText().replaceAll("\\D", "");
        String data        = txtData.getText().trim();
        String pastaBackup = txtBackup.getText().trim(); // pode ser vazio se opcional

        clienteProps.setProperty("cliente.nome", nome);
        clienteProps.setProperty("cliente.id", id);
        clienteProps.setProperty("cliente.data_contrato", data);
        clienteProps.setProperty("cliente.backup_exames", pastaBackup);

        try {
            // Mantive a API original para não quebrar o restante do sistema.
            AppConfig.saveClientConfig(nome, id, data, pastaBackup);

            // Se depois você quiser persistir o backup no arquivo também,
            // podemos ajustar AppConfig para salvar "cliente.backup_exames".
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
