


package com.hospital.ui.billing;

import javax.swing.*;

import com.hospital.dao.*;
import com.hospital.model.*;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class GenerateBillDialog extends JDialog {
    private boolean success = false;
    private BillDAO billDAO;
    private PatientDAO patientDAO;

    private JComboBox<String> patientCombo;
    private JComboBox<String> billTypeCombo;
    private JTextField amountField;
    private JTextField discountField;
    private JTextField taxField;
    private JTextArea notesArea;

    private List<Patient> patients;

    public GenerateBillDialog(Frame parent) {
        super(parent, "Generate Bill", true);
        billDAO = new BillDAO();
        patientDAO = new PatientDAO();
        initComponents();
        loadPatients();
    }

    private void initComponents() {
        setSize(450, 500);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Generate New Bill");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        patientCombo = new JComboBox<>();
        patientCombo.setFocusable(false);
        billTypeCombo = new JComboBox<>(new String[] { "Consultation", "Admission", "Emergency", "Other" });
        amountField = new JTextField();
        discountField = new JTextField("0");
        taxField = new JTextField("0");
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);

        addFormField(mainPanel, "Patient:", patientCombo);
        addFormField(mainPanel, "Bill Type:", billTypeCombo);
        addFormField(mainPanel, "Amount:", amountField);
        addFormField(mainPanel, "Discount:", discountField);
        addFormField(mainPanel, "Tax:", taxField);
        addFormField(mainPanel, "Notes:", new JScrollPane(notesArea));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton generateButton = new JButton("Generate Bill");
        generateButton.setBackground(new Color(46, 204, 113));
        generateButton.setForeground(Color.WHITE);
        generateButton.addActionListener(e -> generateBill());
        generateButton.setOpaque(true);
        generateButton.setContentAreaFilled(true);
        generateButton.setBorderPainted(false);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dispose());
        cancelButton.setOpaque(true);
        cancelButton.setContentAreaFilled(true);
        cancelButton.setBorderPainted(false);

        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private void addFormField(JPanel panel, String label, JComponent component) {
        JLabel jLabel = new JLabel(label);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                component instanceof JScrollPane ? 60 : 30));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(jLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(component);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void loadPatients() {

        patients = patientDAO.getAllPatients();
        patientCombo.removeAllItems();
        for (Patient p : patients) {
            patientCombo.addItem(p.getPatientId() + " - " + p.getFullName());
        }
    }

    private void generateBill() {
        try {
            int patientIndex = patientCombo.getSelectedIndex();
            if (patientIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a patient");
                return;
            }

            BigDecimal amount = new BigDecimal(amountField.getText());
            BigDecimal discount = new BigDecimal(discountField.getText());
            BigDecimal tax = new BigDecimal(taxField.getText());
            BigDecimal finalAmount = amount.subtract(discount).add(tax);

            Bill bill = new Bill();
            bill.setPatientId(patients.get(patientIndex).getPatientId());
            bill.setBillType((String) billTypeCombo.getSelectedItem());
            bill.setTotalAmount(amount);
            bill.setDiscountAmount(discount);
            bill.setTaxAmount(tax);
            bill.setFinalAmount(finalAmount);
            bill.setPaidAmount(BigDecimal.ZERO);
            bill.setPendingAmount(finalAmount);
            bill.setPaymentStatus("Pending");
            bill.setNotes(notesArea.getText());

            if (billDAO.createBill(bill)) {
                success = true;
                JOptionPane.showMessageDialog(this,
                        "Bill generated successfully!\nBill ID: " + bill.getBillId());
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to generate bill");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public boolean isSuccess() {
        return success;
    }
}

