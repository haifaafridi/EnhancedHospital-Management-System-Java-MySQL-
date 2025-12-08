package com.hospital.ui.medicalhistory;

import javax.swing.*;
import javax.swing.table.*;

import com.hospital.dao.MedicalHistoryDAO;
import com.hospital.model.MedicalHistory;

import java.awt.*;
import java.util.List;

public class MedicalHistoryPanel extends JPanel {
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private MedicalHistoryDAO historyDAO;
    private JTextField searchField;

    public MedicalHistoryPanel() {
        historyDAO = new MedicalHistoryDAO();
        initComponents();
        loadMedicalHistories();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Top Panel with Title and Buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Title Section
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Medical History Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("View and manage patient medical histories");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleSection.add(titleLabel);
        titleSection.add(Box.createRigidArea(new Dimension(0, 5)));
        titleSection.add(subtitleLabel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton addButton = createStyledButton("+ Add Medical History", new Color(46, 204, 113),
                new Color(39, 174, 96));
        addButton.addActionListener(e -> showAddMedicalHistoryDialog());

        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219), new Color(41, 128, 185));
        refreshButton.addActionListener(e -> loadMedicalHistories());

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        topPanel.add(titleSection, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchLabel.setForeground(new Color(52, 73, 94));

        searchField = new JTextField(35);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JButton searchButton = createStyledButton("Search", new Color(52, 152, 219), new Color(41, 128, 185));
        searchButton.addActionListener(e -> searchMedicalHistories());

        JButton clearButton = createStyledButton("Clear", new Color(149, 165, 166), new Color(127, 140, 141));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadMedicalHistories();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchButton);
        searchPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        searchPanel.add(clearButton);

        // Table
        String[] columns = { "ID", "Patient Name", "Allergies", "Chronic Conditions", "Smoking", "Alcohol",
                "Last Updated" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyTable.setRowHeight(35);
        historyTable.setGridColor(new Color(236, 240, 241));
        historyTable.setSelectionBackground(new Color(174, 214, 241));
        historyTable.setSelectionForeground(new Color(44, 62, 80));
        historyTable.setShowVerticalLines(true);
        historyTable.setShowHorizontalLines(true);
        historyTable.setIntercellSpacing(new Dimension(1, 1));
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Table Header
        JTableHeader header = historyTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        // Header Renderer
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setForeground(Color.WHITE);
                label.setBackground(new Color(52, 73, 94));
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                label.setHorizontalAlignment(JLabel.LEFT);
                return label;
            }
        };

        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Cell Renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(249, 250, 251));
                    }
                    c.setForeground(new Color(44, 62, 80));
                } else {
                    c.setBackground(new Color(174, 214, 241));
                    c.setForeground(new Color(44, 62, 80));
                }

                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                // Truncate long text
                if (value != null && value.toString().length() > 30) {
                    setText(value.toString().substring(0, 27) + "...");
                }

                return c;
            }
        };

        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Column Widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Patient Name
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Allergies
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Chronic Conditions
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(80); // Smoking
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Alcohol
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(140); // Last Updated

        // Double-click to view details
        historyTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = historyTable.getSelectedRow();
                    if (row != -1) {
                        int historyId = (Integer) tableModel.getValueAt(row, 0);
                        showMedicalHistoryDetails(historyId);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Info Panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JLabel infoLabel = new JLabel("Tip: Double-click on any row to view detailed medical history");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(127, 140, 141));
        infoPanel.add(infoLabel);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(new Color(245, 247, 250));
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(infoPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadMedicalHistories() {
        tableModel.setRowCount(0);
        List<MedicalHistory> histories = historyDAO.getAllMedicalHistories();

        for (MedicalHistory history : histories) {
            Object[] row = {
                    history.getHistoryId(),
                    history.getPatientName(),
                    history.getAllergies() != null ? history.getAllergies() : "None",
                    history.getChronicConditions() != null ? history.getChronicConditions() : "None",
                    history.getSmokingStatus() != null ? history.getSmokingStatus() : "N/A",
                    history.getAlcoholConsumption() != null ? history.getAlcoholConsumption() : "N/A",
                    history.getUpdatedAt() != null
                            ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(history.getUpdatedAt())
                            : "N/A"
            };
            tableModel.addRow(row);
        }
    }

    private void searchMedicalHistories() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadMedicalHistories();
            return;
        }

        tableModel.setRowCount(0);
        List<MedicalHistory> histories = historyDAO.searchMedicalHistories(searchTerm);

        if (histories.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No medical histories found matching your search criteria.",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (MedicalHistory history : histories) {
            Object[] row = {
                    history.getHistoryId(),
                    history.getPatientName(),
                    history.getAllergies() != null ? history.getAllergies() : "None",
                    history.getChronicConditions() != null ? history.getChronicConditions() : "None",
                    history.getSmokingStatus() != null ? history.getSmokingStatus() : "N/A",
                    history.getAlcoholConsumption() != null ? history.getAlcoholConsumption() : "N/A",
                    history.getUpdatedAt() != null
                            ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(history.getUpdatedAt())
                            : "N/A"
            };
            tableModel.addRow(row);
        }
    }

    private void showAddMedicalHistoryDialog() {
        AddMedicalHistoryDialog dialog = new AddMedicalHistoryDialog(
                (Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadMedicalHistories();
        }
    }

    private void showMedicalHistoryDetails(int historyId) {
        MedicalHistory history = historyDAO.getMedicalHistoryById(historyId);
        if (history != null) {
            MedicalHistoryDetailsDialog dialog = new MedicalHistoryDetailsDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), history);
            dialog.setVisible(true);

            if (dialog.isUpdated()) {
                loadMedicalHistories();
            }
        }
    }
}