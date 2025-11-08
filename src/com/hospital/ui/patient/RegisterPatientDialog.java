


package com.hospital.ui.patient;

import javax.swing.*;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;

import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;

public class RegisterPatientDialog extends JDialog {
    private boolean success = false;
    private PatientDAO patientDAO;

    private JComboBox<String> idTypeCombo;
    private JTextField idNumberField;
    private JTextField fullNameField;
    private JSpinner dobSpinner;
    private JComboBox<String> genderCombo;
    private JComboBox<String> bloodGroupCombo;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField emergencyNameField;
    private JTextField emergencyPhoneField;
    private JTextArea addressArea;
    private JTextField cityField;

    public RegisterPatientDialog(Frame parent) {
        super(parent, "Register New Patient", true);
        patientDAO = new PatientDAO();
        initComponents();
    }

    private void initComponents() {
        setSize(600, 700);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        
        JLabel titleLabel = new JLabel("Patient Registration Form");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        
        idTypeCombo = new JComboBox<>(new String[] { "CNIC", "Passport", "Driving License" });
        idNumberField = new JTextField();
        fullNameField = new JTextField();

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dobSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dobSpinner, "dd/MM/yyyy");
        dobSpinner.setEditor(dateEditor);

        genderCombo = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        bloodGroupCombo = new JComboBox<>(new String[] { "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-" });
        phoneField = new JTextField();
        emailField = new JTextField();
        emergencyNameField = new JTextField();
        emergencyPhoneField = new JTextField();
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        cityField = new JTextField();

        addFormField(mainPanel, "ID Type:", idTypeCombo);
        addFormField(mainPanel, "ID Number:", idNumberField);
        addFormField(mainPanel, "Full Name:", fullNameField);
        addFormField(mainPanel, "Date of Birth:", dobSpinner);
        addFormField(mainPanel, "Gender:", genderCombo);
        addFormField(mainPanel, "Blood Group:", bloodGroupCombo);
        addFormField(mainPanel, "Phone:", phoneField);
        addFormField(mainPanel, "Email:", emailField);
        addFormField(mainPanel, "Emergency Contact Name:", emergencyNameField);
        addFormField(mainPanel, "Emergency Contact Phone:", emergencyPhoneField);
        addFormField(mainPanel, "Address:", new JScrollPane(addressArea));
        addFormField(mainPanel, "City:", cityField);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton saveButton = new JButton("Register Patient");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> registerPatient());
        saveButton.setOpaque(true);
        saveButton.setContentAreaFilled(true);
        saveButton.setBorderPainted(false);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());
        cancelButton.setOpaque(true);
        cancelButton.setContentAreaFilled(true);
        cancelButton.setBorderPainted(false);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane);
    }

    private void addFormField(JPanel panel, String label, JComponent component) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        component.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                component instanceof JScrollPane ? 60 : 30));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(jLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(component);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void registerPatient() {
        
        if (fullNameField.getText().trim().isEmpty() ||
                idNumberField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Patient patient = new Patient();
            patient.setIdType((String) idTypeCombo.getSelectedItem());
            patient.setIdNumber(idNumberField.getText().trim());
            patient.setFullName(fullNameField.getText().trim());

            java.util.Date dobDate = (java.util.Date) dobSpinner.getValue();
            patient.setDateOfBirth(new Date(dobDate.getTime()));

            
            LocalDate dob = dobDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            int age = Period.between(dob, LocalDate.now()).getYears();
            patient.setAge(age);

            patient.setGender((String) genderCombo.getSelectedItem());
            patient.setBloodGroup((String) bloodGroupCombo.getSelectedItem());
            patient.setPhone(phoneField.getText().trim());
            patient.setEmail(emailField.getText().trim());
            patient.setEmergencyContactName(emergencyNameField.getText().trim());
            patient.setEmergencyContactPhone(emergencyPhoneField.getText().trim());
            patient.setAddress(addressArea.getText().trim());
            patient.setCity(cityField.getText().trim());
            patient.setCountry("Pakistan");
            patient.setStatus("Active");

            if (patientDAO.registerPatient(patient)) {
                success = true;
                JOptionPane.showMessageDialog(this,
                        "Patient registered successfully!\nPatient ID: " + patient.getPatientId(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to register patient",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}

