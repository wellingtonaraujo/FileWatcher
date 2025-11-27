package com.wellington.filewatcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

public class ExamesDialog extends JDialog {

    private JTextField txtExame;
    private JTextField txtSufixo;
    private JButton btnSalvar;
    private JButton btnNovo;
    private JButton btnExcluir;
    private JButton btnFechar;
    private JList<ExameTipo> lstExames;
    private DefaultListModel<ExameTipo> listModel;

    public ExamesDialog(Frame parent) {
        super(parent, "Cadastro de Tipos de Exame", true);
        initComponents();
        carregarListaExames();
        pack();
        setMinimumSize(new Dimension(520, 320));
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        // Painel principal com margem
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        // ========================= LISTA ESQUERDA =========================
        listModel = new DefaultListModel<>();
        lstExames = new JList<>(listModel);
        lstExames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstExames.setVisibleRowCount(10);

        lstExames.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ExameTipo sel = lstExames.getSelectedValue();
                if (sel != null) {
                    txtExame.setText(sel.getExame());
                    txtSufixo.setText(sel.getSufixo());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(lstExames);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new TitledBorder("Exames cadastrados"));
        leftPanel.add(scroll, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(220, 0));

        content.add(leftPanel, BorderLayout.WEST);

        // ========================= FORMULÁRIO DIREITA =====================
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new TitledBorder("Detalhes do exame"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblExame  = new JLabel("Nome do exame:");
        JLabel lblSufixo = new JLabel("Sufixo (abreviação):");

        txtExame  = new JTextField(20);
        txtSufixo = new JTextField(10);

        // Linha 0 - Exame
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        formPanel.add(lblExame, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        formPanel.add(txtExame, gbc);

        // Linha 2 - Sufixo
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1;
        formPanel.add(lblSufixo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1;
        formPanel.add(txtSufixo, gbc);

        // ---------------------- Botões ----------------------
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));

        btnNovo    = new JButton("Novo");
        btnExcluir = new JButton("Excluir");
        btnSalvar  = new JButton("Salvar");
        btnFechar  = new JButton("Fechar");

        buttonsPanel.add(btnNovo);
        buttonsPanel.add(btnExcluir);
        buttonsPanel.add(btnSalvar);
        buttonsPanel.add(btnFechar);

        rightPanel.add(formPanel, BorderLayout.CENTER);
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

        content.add(rightPanel, BorderLayout.CENTER);

        // ========================= AÇÕES =========================
        btnSalvar.addActionListener(e -> onSalvar());
        btnNovo.addActionListener(e -> onNovo());
        btnExcluir.addActionListener(e -> onExcluir());
        btnFechar.addActionListener(e -> dispose());

        // ENTER no campo sufixo chama Salvar
        txtSufixo.addActionListener(e -> onSalvar());

        // ESC fecha a janela
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // ENTER = Salvar
        getRootPane().setDefaultButton(btnSalvar);
    }

    private void carregarListaExames() {
        listModel.clear();
        try {
            List<ExameTipo> exames = ExameCsvRepository.findAll();
            for (ExameTipo e : exames) {
                listModel.addElement(e);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar exames: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onNovo() {
        lstExames.clearSelection();
        txtExame.setText("");
        txtSufixo.setText("");
        txtExame.requestFocus();
    }

    private void onSalvar() {
        String exame  = txtExame.getText()  != null ? txtExame.getText().trim()  : "";
        String sufixo = txtSufixo.getText() != null ? txtSufixo.getText().trim() : "";

        if (exame.isEmpty() || sufixo.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o nome do exame e o sufixo.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        sufixo = sufixo.toUpperCase();
        txtSufixo.setText(sufixo);

        try {
            ExameCsvRepository.saveOrUpdate(exame, sufixo);
            carregarListaExames();

            // Mantém o popup aberto para mais inserções
            txtExame.setText("");
            txtSufixo.setText("");
            txtExame.requestFocus();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao salvar exame: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onExcluir() {
        ExameTipo sel = lstExames.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um exame na lista para excluir.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int op = JOptionPane.showConfirmDialog(
                this,
                "Confirma excluir o exame \"" + sel.getExame() + "\" (" + sel.getSufixo() + ")?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {
            try {
                ExameCsvRepository.deleteBySufixo(sel.getSufixo());
                carregarListaExames();
                onNovo();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Erro ao excluir exame: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
