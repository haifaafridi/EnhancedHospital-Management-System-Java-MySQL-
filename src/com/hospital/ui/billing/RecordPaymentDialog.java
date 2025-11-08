


package com.hospital.ui.billing;

import javax.swing.*;

import com.hospital.dao.BillDAO;
import com.hospital.util.SessionManager;

import java.awt.*;
import java.math.BigDecimal;

public class RecordPaymentDialog extends JDialog {
    private boolean success = false;
    private BillDAO billDAO;
    private int billId;

    private JTextField amountField;
    private JComboBox<String> methodCombo;
    private JTextField referenceField;

    public RecordPaymentDialog(Frame parent, int billId) {
        super(parent, "Record Payment", true);
        this.billId = billId;
        billDAO = new BillDAO();
        initComponents();
    }

    private void initComponents() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Record Payment for Bill #" + billId);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        amountField = new JTextField();
        methodCombo = new JComboBox<>(new String[] { "Cash", "Card", "Bank Transfer",
                "Cheque", "Insurance", "Online" });
        referenceField = new JTextField();

        addFormField(mainPanel, "Amount:", amountField);
        addFormField(mainPanel, "Payment Method:", methodCombo);
        addFormField(mainPanel, "Reference:", referenceField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton recordButton = new JButton("Record Payment");
        recordButton.setBackground(new Color(46, 204, 113));
        recordButton.setForeground(Color.WHITE);
        recordButton.addActionListener(e -> recordPayment());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(recordButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private void addFormField(JPanel panel, String label, JComponent component) {
        JLabel jLabel = new JLabel(label);
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        panel.add(jLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(component);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void recordPayment() {
        try {
            BigDecimal amount = new BigDecimal(amountField.getText());
            String method = (String) methodCombo.getSelectedItem();
            String reference = referenceField.getText().trim();

            Integer userId = SessionManager.isLoggedIn() ? SessionManager.getCurrentUser().getUserId() : null;

            if (billDAO.recordPayment(billId, amount, method, reference, userId)) {
                success = true;
                JOptionPane.showMessageDialog(this, "Payment recorded successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to record payment");
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

