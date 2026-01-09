package com.wellington.filewatcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.*;

public class FileInfoDialog extends JDialog {
    private final File file;
    private final JTextField nomeNovoField;
    private final JTextArea descricaoArea;

    public FileInfoDialog(File file) {
        this.file = file;

        setTitle("Novo arquivo detectado");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        //Deixar esta modal sempre visivel
        setModal(true); //bloqueia interação com outras janelas da mesma aplicação
        setAlwaysOnTop(true); //mantém a janela acima de todas as outras (inclusive de outros apps)
              

        // Painel raiz com BorderLayout e padding (margem interna)
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(16, 16, 16, 16)); // top, left, bottom, right

        // Título / informação do arquivo no topo
        JLabel infoLabel = new JLabel("<html><b>Arquivo detectado:</b> " + file.getName() + "</html>");
        infoLabel.setBorder(new EmptyBorder(0, 0, 8, 0)); // pequeno espaço abaixo do label
        root.add(infoLabel, BorderLayout.NORTH);

        // Painel de formulário central com GridBagLayout para controlar espaçamentos
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); // espaço entre componentes (top,left,bottom,right)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Label "Novo nome"
        gbc.anchor = GridBagConstraints.WEST;
        JLabel novoNomeLabel = new JLabel("Novo nome:");
        form.add(novoNomeLabel, gbc);

        // Campo novo nome (ocupando a coluna 1)
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nomeNovoField = new JTextField(30);
        form.add(nomeNovoField, gbc);

        // Próxima linha: descrição label
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel descricaoLabel = new JLabel("Descrição:");
        form.add(descricaoLabel, gbc);

        // Área de descrição (JScrollPane) - ocupa 2 colunas e expande verticalmente
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        descricaoArea = new JTextArea(6, 30);
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(descricaoArea);
        form.add(scroll, gbc);

        // Adiciona o form ao centro do root
        root.add(form, BorderLayout.CENTER);

        // Painel de botões na parte inferior (centralizado)
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton salvarButton = new JButton("Salvar e mover");
        salvarButton.addActionListener(e -> salvarInformacoes());
        buttons.add(salvarButton);
        root.add(buttons, BorderLayout.SOUTH);

        // Define o content pane do diálogo e ajusta tamanho automaticamente
        setContentPane(root);
        pack(); // importante: calcula tamanho levando em conta bordas e componentes
        setLocationRelativeTo(null); // centraliza na tela
    }

    private void salvarInformacoes() {
        try {
            String novoNome = nomeNovoField.getText().trim();
            String descricao = descricaoArea.getText().trim();

            if (novoNome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o novo nome do arquivo!");
                return;
            }

            // Pasta de destino
            Path destinoDir = Paths.get("C:\\FileWatcher\\Destination");
            if (!Files.exists(destinoDir)) {
                Files.createDirectories(destinoDir);
            }

            // Novo caminho do arquivo
            Path novoCaminho = destinoDir.resolve(novoNome);

            // Move o arquivo
            Files.move(file.toPath(), novoCaminho, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Arquivo movido para: " + novoCaminho);

            // Salvar info no banco
            DatabaseHelper.salvarArquivo(file.getName(), novoNome, novoCaminho.toString(), descricao);

            JOptionPane.showMessageDialog(this, "Informações salvas com sucesso!");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao processar arquivo: " + ex.getMessage());
        }
    }
}
