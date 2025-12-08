package com.hospital.ui.medicalhistory;

import javax.swing.*;
import com.hospital.dao.MedicalHistoryDAO;
import com.hospital.model.MedicalHistory;
import java.awt.*;

public class MedicalHistoryDetailsDialog extends JDialog {
    private MedicalHistoryDAO historyDAO;
    private MedicalHistory history;
    private boolean updated = false;

    private JTextField patientField;
    private JTextArea allergiesArea;
    private JTextArea chronicConditionsArea;
    private JTextArea previousSurgeriesArea;
    private JTextArea currentMedicationsArea;
    private JTextArea familyHistoryArea;
    private JComboBox<String> smokingComboBox;
    private JComboBox<String> alcoholComboBox;
    private JTextArea notesArea;

    public MedicalHistoryDetailsDialog(Frame parent, MedicalHistory history) {
        super(parent, "Medical History Details", true);
        this.history = history;
        this.historyDAO = new MedicalHistoryDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(850, 750);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(155, 89, 182));
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(155, 89, 182));

        JLabel titleLabel = new JLabel("Medical History Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idLabel = new JLabel("History ID: #" + history.getHistoryId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        idLabel.setForeground(new Color(236, 240, 241));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(idLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);

        // Patient Name (Read-only)
        addLabel(formPanel, "Patient Name:", 0, gbc);
        patientField = new JTextField(30);
        patientField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientField.setEditable(false);
        patientField.setBackground(new Color(236, 240, 241));
        addField(formPanel, patientField, 0, gbc);

        // Allergies
        addLabel(formPanel, "Allergies:", 1, gbc);
        allergiesArea = createTextArea(3);
        addScrollableField(formPanel, allergiesArea, 1, gbc);

        // Chronic Conditions
        addLabel(formPanel, "Chronic Conditions:", 2, gbc);
        chronicConditionsArea = createTextArea(3);
        addScrollableField(formPanel, chronicConditionsArea, 2, gbc);

        // Previous Surgeries
        addLabel(formPanel, "Previous Surgeries:", 3, gbc);
        previousSurgeriesArea = createTextArea(3);
        addScrollableField(formPanel, previousSurgeriesArea, 3, gbc);

        // Current Medications
        addLabel(formPanel, "Current Medications:", 4, gbc);
        currentMedicationsArea = createTextArea(3);
        addScrollableField(formPanel, currentMedicationsArea, 4, gbc);

        // Family Medical History
        addLabel(formPanel, "Family Medical History:", 5, gbc);
        familyHistoryArea = createTextArea(3);
        addScrollableField(formPanel, familyHistoryArea, 5, gbc);

        // Smoking Status
        addLabel(formPanel, "Smoking Status:", 6, gbc);
        smokingComboBox = new JComboBox<>(new String[] { "Never", "Former", "Current" });
        smokingComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addField(formPanel, smokingComboBox, 6, gbc);

        // Alcohol Consumption
        addLabel(formPanel, "Alcohol Consumption:", 7, gbc);
        alcoholComboBox = new JComboBox<>(new String[] { "None", "Occasional", "Regular" });
        alcoholComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addField(formPanel, alcoholComboBox, 7, gbc);

        // Notes
        addLabel(formPanel, "Additional Notes:", 8, gbc);
        notesArea = createTextArea(4);
        addScrollableField(formPanel, notesArea, 8, gbc);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 223, 230)));

        JButton updateButton = createStyledButton("Update", new Color(52, 152, 219), new Color(41, 128, 185));
        updateButton.addActionListener(e -> updateMedicalHistory());

        JButton deleteButton = createStyledButton("Delete", new Color(231, 76, 60), new Color(192, 57, 43));
        deleteButton.addActionListener(e -> deleteMedicalHistory());

        JButton closeButton = createStyledButton("Close", new Color(149, 165, 166), new Color(127, 140, 141));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTextArea createTextArea(int rows) {
        JTextArea textArea = new JTextArea(rows, 30);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return textArea;
    }

    private void addLabel(JPanel panel, String text, int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        panel.add(label, gbc);
    }

    private void addField(JPanel panel, JComponent field, int row, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private void addScrollableField(JPanel panel, JTextArea textArea, int row, GridBagConstraints gbc) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 85));
        addField(panel, scrollPane, row, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

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

    private void loadData() {
        patientField.setText(history.getPatientName());
        allergiesArea.setText(history.getAllergies() != null ? history.getAllergies() : "");
        chronicConditionsArea.setText(history.getChronicConditions() != null ? history.getChronicConditions() : "");
        previousSurgeriesArea.setText(history.getPreviousSurgeries() != null ? history.getPreviousSurgeries() : "");
        currentMedicationsArea
                .setText(history.getCurrentMedications() != null ? history.getCurrentMedications() : "");
        familyHistoryArea.setText(history.getFamilyMedicalHistory() != null ? history.getFamilyMedicalHistory() : "");
        smokingComboBox.setSelectedItem(history.getSmokingStatus() != null ? history.getSmokingStatus() : "Never");
        alcoholComboBox
                .setSelectedItem(history.getAlcoholConsumption() != null ? history.getAlcoholConsumption() : "None");
        notesArea.setText(history.getNotes() != null ? history.getNotes() : "");
    }

    private void updateMedicalHistory() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to update this medical history?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            history.setAllergies(allergiesArea.getText().trim().isEmpty() ? null : allergiesArea.getText().trim());
            history.setChronicConditions(
                    chronicConditionsArea.getText().trim().isEmpty() ? null : chronicConditionsArea.getText().trim());
            history.setPreviousSurgeries(
                    previousSurgeriesArea.getText().trim().isEmpty() ? null : previousSurgeriesArea.getText().trim());
            history.setCurrentMedications(
                    currentMedicationsArea.getText().trim().isEmpty() ? null : currentMedicationsArea.getText().trim());
            history.setFamilyMedicalHistory(
                    familyHistoryArea.getText().trim().isEmpty() ? null : familyHistoryArea.getText().trim());
            history.setSmokingStatus((String) smokingComboBox.getSelectedItem());
            history.setAlcoholConsumption((String) alcoholComboBox.getSelectedItem());
            history.setNotes(notesArea.getText().trim().isEmpty() ? null : notesArea.getText().trim());

            if (historyDAO.updateMedicalHistory(history)) {
                updated = true;
                JOptionPane.showMessageDialog(this,
                        "Medical history updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update medical history.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteMedicalHistory() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this medical history?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (historyDAO.deleteMedicalHistory(history.getHistoryId())) {
                updated = true;
                JOptionPane.showMessageDialog(this,
                        "Medical history deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete medical history.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isUpdated() {
        return updated;
    }
}