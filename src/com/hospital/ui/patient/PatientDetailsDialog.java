


package com.hospital.ui.patient;

import javax.swing.*;

import com.hospital.dao.AuditLogDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.util.SessionManager;

import java.awt.*;
import java.awt.event.*;

public class PatientDetailsDialog extends JDialog {
    private boolean updated = false;
    private PatientDAO patientDAO;
    private AuditLogDAO auditDAO;
    private Patient patient;

    
    private JTextField fullNameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField emergencyNameField;
    private JTextField emergencyPhoneField;
    private JTextArea addressArea;
    private JTextField cityField;
    private JComboBox<String> bloodGroupCombo;

    
    private JLabel patientIdLabel;
    private JLabel idTypeLabel;
    private JLabel idNumberLabel;
    private JLabel dobLabel;
    private JLabel ageLabel;
    private JLabel genderLabel;
    private JLabel statusLabel;
    private JLabel registrationDateLabel;

    public PatientDetailsDialog(Frame parent, Patient patient) {
        super(parent, "Patient Details - ID: " + patient.getPatientId(), true);
        this.patient = patient;
        this.patientDAO = new PatientDAO();
        this.auditDAO = new AuditLogDAO();
        initComponents();
        loadPatientData();
    }

    private void initComponents() {
        setSize(700, 800);
        setLocationRelativeTo(getParent());
        setResizable(false);

        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        
        JPanel titlePanel = createTitlePanel();
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        
        JPanel readOnlySection = createReadOnlySection();
        readOnlySection.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(readOnlySection);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(separator);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        
        JPanel editableSection = createEditableSection();
        editableSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(editableSection);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        
        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(buttonPanel);

        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleClose();
            }
        });
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel titleLabel = new JLabel("Patient Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));

        JLabel subtitleLabel = new JLabel("View and update patient information");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReadOnlySection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Patient Information (Read-Only)",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        
        patientIdLabel = new JLabel();
        idTypeLabel = new JLabel();
        idNumberLabel = new JLabel();
        dobLabel = new JLabel();
        ageLabel = new JLabel();
        genderLabel = new JLabel();
        statusLabel = new JLabel();
        registrationDateLabel = new JLabel();

        
        JPanel gridPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addReadOnlyField(gridPanel, "Patient ID:", patientIdLabel);
        addReadOnlyField(gridPanel, "ID Type:", idTypeLabel);
        addReadOnlyField(gridPanel, "ID Number:", idNumberLabel);
        addReadOnlyField(gridPanel, "Date of Birth:", dobLabel);
        addReadOnlyField(gridPanel, "Age:", ageLabel);
        addReadOnlyField(gridPanel, "Gender:", genderLabel);
        addReadOnlyField(gridPanel, "Status:", statusLabel);
        addReadOnlyField(gridPanel, "Registration Date:", registrationDateLabel);

        panel.add(gridPanel);

        return panel;
    }

    private void addReadOnlyField(JPanel panel, String labelText, JLabel valueLabel) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(52, 73, 94));

        valueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        valueLabel.setForeground(Color.BLACK);

        panel.add(label);
        panel.add(valueLabel);
    }

    private JPanel createEditableSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Editable Information",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        fullNameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        emergencyNameField = new JTextField();
        emergencyPhoneField = new JTextField();
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        cityField = new JTextField();
        bloodGroupCombo = new JComboBox<>(new String[] {
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        });

        
        addFormField(formPanel, "Full Name:", fullNameField, true);
        addFormField(formPanel, "Phone:", phoneField, true);
        addFormField(formPanel, "Email:", emailField, false);
        addFormField(formPanel, "Blood Group:", bloodGroupCombo, false);
        addFormField(formPanel, "Emergency Contact Name:", emergencyNameField, false);
        addFormField(formPanel, "Emergency Contact Phone:", emergencyPhoneField, false);
        addFormField(formPanel, "Address:", new JScrollPane(addressArea), false);
        addFormField(formPanel, "City:", cityField, false);

        panel.add(formPanel);

        return panel;
    }

    private void addFormField(JPanel panel, String labelText, JComponent component, boolean required) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                component instanceof JScrollPane ? 90 : 60));

        JLabel label = new JLabel(labelText + (required ? " *" : ""));
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (required) {
            label.setForeground(new Color(192, 57, 43));
        }

        component.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                component instanceof JScrollPane ? 70 : 30));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (component instanceof JTextField) {
            ((JTextField) component).setFont(new Font("Arial", Font.PLAIN, 12));
        }

        fieldPanel.add(label);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        fieldPanel.add(component);

        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        
        JButton updateButton = new JButton("Update Information");
        updateButton.setFont(new Font("Arial", Font.BOLD, 12));
        updateButton.setBackground(new Color(52, 152, 219));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setBorderPainted(false);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateButton.setPreferredSize(new Dimension(160, 35));
        updateButton.addActionListener(e -> updatePatient());

        
        JButton historyButton = new JButton("View History");
        historyButton.setFont(new Font("Arial", Font.BOLD, 12));
        historyButton.setBackground(new Color(155, 89, 182));
        historyButton.setForeground(Color.WHITE);
        historyButton.setFocusPainted(false);
        historyButton.setBorderPainted(false);
        historyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        historyButton.setPreferredSize(new Dimension(130, 35));
        historyButton.addActionListener(e -> viewHistory());

        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setBackground(new Color(149, 165, 166));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> handleClose());

        panel.add(historyButton);
        panel.add(updateButton);
        panel.add(closeButton);

        return panel;
    }

    private void loadPatientData() {
        
        patientIdLabel.setText(String.valueOf(patient.getPatientId()));
        idTypeLabel.setText(patient.getIdType());
        idNumberLabel.setText(patient.getIdNumber());
        dobLabel.setText(patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "N/A");
        ageLabel.setText(String.valueOf(patient.getAge()) + " years");
        genderLabel.setText(patient.getGender());
        statusLabel.setText(patient.getStatus());
        registrationDateLabel
                .setText(patient.getRegistrationDate() != null ? patient.getRegistrationDate().toString() : "N/A");

        
        if ("Active".equals(patient.getStatus())) {
            statusLabel.setForeground(new Color(39, 174, 96));
        } else if ("Discharged".equals(patient.getStatus())) {
            statusLabel.setForeground(new Color(52, 152, 219));
        } else {
            statusLabel.setForeground(new Color(192, 57, 43));
        }

        
        fullNameField.setText(patient.getFullName());
        phoneField.setText(patient.getPhone());
        emailField.setText(patient.getEmail() != null ? patient.getEmail() : "");
        bloodGroupCombo.setSelectedItem(patient.getBloodGroup());
        emergencyNameField.setText(patient.getEmergencyContactName() != null ? patient.getEmergencyContactName() : "");
        emergencyPhoneField
                .setText(patient.getEmergencyContactPhone() != null ? patient.getEmergencyContactPhone() : "");
        addressArea.setText(patient.getAddress() != null ? patient.getAddress() : "");
        cityField.setText(patient.getCity() != null ? patient.getCity() : "");
    }

    private void updatePatient() {
        
        if (fullNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Full Name is required",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            fullNameField.requestFocus();
            return;
        }

        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Phone number is required",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return;
        }

        
        String phone = phoneField.getText().trim();
        if (!phone.matches("^[0-9\\-+() ]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid phone number",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return;
        }

        
        String oldValues = String.format("Name: %s, Phone: %s, Email: %s",
                patient.getFullName(), patient.getPhone(), patient.getEmail());

        
        patient.setFullName(fullNameField.getText().trim());
        patient.setPhone(phoneField.getText().trim());
        patient.setEmail(emailField.getText().trim());
        patient.setBloodGroup((String) bloodGroupCombo.getSelectedItem());
        patient.setEmergencyContactName(emergencyNameField.getText().trim());
        patient.setEmergencyContactPhone(emergencyPhoneField.getText().trim());
        patient.setAddress(addressArea.getText().trim());
        patient.setCity(cityField.getText().trim());

        
        if (patientDAO.updatePatient(patient)) {
            
            String newValues = String.format("Name: %s, Phone: %s, Email: %s",
                    patient.getFullName(), patient.getPhone(), patient.getEmail());

            auditDAO.logUpdate("Patients", patient.getPatientId(), oldValues, newValues,
                    "Patient information updated by " +
                            (SessionManager.isLoggedIn() ? SessionManager.getCurrentUser().getUsername() : "System"));

            updated = true;

            JOptionPane.showMessageDialog(this,
                    "Patient information updated successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update patient information.\nPlease try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewHistory() {
        
        StringBuilder history = new StringBuilder();
        history.append("Medical History for: ").append(patient.getFullName()).append("\n");
        history.append("Patient ID: ").append(patient.getPatientId()).append("\n\n");
        history.append("Blood Group: ").append(patient.getBloodGroup()).append("\n");
        history.append("Registration Date: ").append(patient.getRegistrationDate()).append("\n");
        history.append("Current Status: ").append(patient.getStatus()).append("\n\n");
        history.append("Emergency Contact:\n");
        history.append("  Name: ").append(patient.getEmergencyContactName()).append("\n");
        history.append("  Phone: ").append(patient.getEmergencyContactPhone()).append("\n");

        JTextArea historyArea = new JTextArea(history.toString());
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "Patient History",
                JOptionPane.INFORMATION_MESSAGE);

        
        auditDAO.logView("Patients", patient.getPatientId(),
                "Patient history viewed by " +
                        (SessionManager.isLoggedIn() ? SessionManager.getCurrentUser().getUsername() : "System"));
    }

    private void handleClose() {
        
        boolean hasChanges = !fullNameField.getText().trim().equals(patient.getFullName()) ||
                !phoneField.getText().trim().equals(patient.getPhone()) ||
                !emailField.getText().trim().equals(patient.getEmail() != null ? patient.getEmail() : "");

        if (hasChanges) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes. Do you want to close without saving?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    
    public boolean isUpdated() {
        return updated;
    }

    
    public Patient getPatient() {
        return patient;
    }
}

