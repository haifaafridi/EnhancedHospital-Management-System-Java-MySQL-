package com.hospital.ui.medicalhistory;

import javax.swing.*;
import com.hospital.dao.MedicalHistoryDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.MedicalHistory;
import com.hospital.model.Patient;
import java.awt.*;
import java.util.List;

public class AddMedicalHistoryDialog extends JDialog {
    private MedicalHistoryDAO historyDAO;
    private PatientDAO patientDAO;
    private boolean success = false;

    private JComboBox<String> patientComboBox;
    private JTextArea allergiesArea;
    private JTextArea chronicConditionsArea;
    private JTextArea previousSurgeriesArea;
    private JTextArea currentMedicationsArea;
    private JTextArea familyHistoryArea;
    private JComboBox<String> smokingComboBox;
    private JComboBox<String> alcoholComboBox;
    private JTextArea notesArea;

    public AddMedicalHistoryDialog(Frame parent) {
        super(parent, "Add Medical History", true);
        historyDAO = new MedicalHistoryDAO();
        patientDAO = new PatientDAO();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(800, 700);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel headerLabel = new JLabel("Add New Medical History");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Patient Selection
        addLabel(formPanel, "Select Patient: *", 0, gbc);
        patientComboBox = new JComboBox<>();
        patientComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loadPatients();
        addField(formPanel, patientComboBox, 0, gbc);

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

        JButton saveButton = createStyledButton("Save Medical History", new Color(46, 204, 113),
                new Color(39, 174, 96));
        saveButton.addActionListener(e -> saveMedicalHistory());

        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166), new Color(127, 140, 141));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

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

    private void loadPatients() {
        List<Patient> patients = patientDAO.getAllPatients();
        patientComboBox.removeAllItems();
        patientComboBox.addItem("-- Select Patient --");

        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getPatientId() + " - " + patient.getFullName());
        }
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
        scrollPane.setPreferredSize(new Dimension(400, 80));
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

    private void saveMedicalHistory() {
        if (patientComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String selectedPatient = (String) patientComboBox.getSelectedItem();
            int patientId = Integer.parseInt(selectedPatient.split(" - ")[0]);

            // Check if medical history already exists for this patient
            MedicalHistory existing = historyDAO.getMedicalHistoryByPatientId(patientId);
            if (existing != null) {
                JOptionPane.showMessageDialog(this,
                        "Medical history already exists for this patient.\nPlease edit the existing record instead.",
                        "Record Exists",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            MedicalHistory history = new MedicalHistory();
            history.setPatientId(patientId);
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

            if (historyDAO.addMedicalHistory(history)) {
                success = true;
                JOptionPane.showMessageDialog(this,
                        "Medical history added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add medical history.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}