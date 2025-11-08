


package com.hospital.ui.billing;

import javax.swing.*;
import javax.swing.table.*;

import com.hospital.dao.*;
import com.hospital.model.*;

import java.awt.*;

import java.util.List;

public class BillingPanel extends JPanel {
    private JTable billTable;
    private DefaultTableModel tableModel;
    private BillDAO billDAO;

    private JComboBox<String> filterCombo;

    public BillingPanel() {
        billDAO = new BillDAO();

        initComponents();
        loadAllBills();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Billing Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage and view all billing records");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleSection.add(titleLabel);
        titleSection.add(Box.createRigidArea(new Dimension(0, 5)));
        titleSection.add(subtitleLabel);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton newBillButton = createStyledButton("+ Generate Bill", new Color(46, 204, 113));
        newBillButton.addActionListener(e -> showGenerateBillDialog());

        JButton paymentButton = createStyledButton(" Record Payment", new Color(52, 152, 219));
        paymentButton.addActionListener(e -> showRecordPaymentDialog());

        JButton refreshButton = createStyledButton("Refresh", new Color(149, 165, 166));
        refreshButton.addActionListener(e -> loadAllBills());

        buttonPanel.add(newBillButton);
        buttonPanel.add(paymentButton);
        buttonPanel.add(refreshButton);

        headerPanel.add(titleSection, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JPanel filterSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterSection.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Filter Bills:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(new Color(44, 62, 80));

        filterCombo = new JComboBox<>(new String[] { "All Bills", "Pending", "Paid", "Overdue" });
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(150, 35));
        filterCombo.setBackground(Color.WHITE);
        filterCombo.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230), 1));
        filterCombo.addActionListener(e -> applyFilter());

        filterSection.add(filterLabel);
        filterSection.add(filterCombo);

        searchPanel.add(filterSection, BorderLayout.WEST);

        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        
        String[] columns = { "ID", "Patient ID", "Date", "Type", "Total Amount",
                "Paid", "Pending", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billTable = new JTable(tableModel);
        billTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        billTable.setRowHeight(40);
        billTable.setShowGrid(true);
        billTable.setGridColor(new Color(240, 243, 245));
        billTable.setSelectionBackground(new Color(232, 245, 253));
        billTable.setSelectionForeground(new Color(44, 62, 80));
        billTable.setIntercellSpacing(new Dimension(10, 0));

        
        JTableHeader header = billTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setReorderingAllowed(false);

        
        header.setOpaque(true);

        
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

        for (int i = 0; i < billTable.getColumnCount(); i++) {
            billTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        
        billTable.getColumnModel().getColumn(0).setPreferredWidth(60); 
        billTable.getColumnModel().getColumn(1).setPreferredWidth(100); 
        billTable.getColumnModel().getColumn(2).setPreferredWidth(120); 
        billTable.getColumnModel().getColumn(3).setPreferredWidth(120); 
        billTable.getColumnModel().getColumn(4).setPreferredWidth(120); 
        billTable.getColumnModel().getColumn(5).setPreferredWidth(120); 
        billTable.getColumnModel().getColumn(6).setPreferredWidth(120); 
        billTable.getColumnModel().getColumn(7).setPreferredWidth(120); 

        
        billTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                String status = value.toString();
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                if ("Fully Paid".equals(status)) {
                    label.setForeground(new Color(46, 204, 113));
                } else if ("Pending".equals(status) || "Partially Paid".equals(status)) {
                    label.setForeground(new Color(241, 196, 15));
                } else if ("Overdue".equals(status)) {
                    label.setForeground(new Color(231, 76, 60));
                } else {
                    label.setForeground(new Color(44, 62, 80));
                }

                if (!isSelected) {
                    label.setBackground(Color.WHITE);
                }

                return label;
            }
        });

        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.LEFT);
        for (int i = 0; i < billTable.getColumnCount(); i++) {
            billTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        billTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                String status = value.toString();
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                if ("Fully Paid".equals(status)) {
                    label.setForeground(new Color(46, 204, 113));
                } else if ("Pending".equals(status) || "Partially Paid".equals(status)) {
                    label.setForeground(new Color(241, 196, 15));
                } else if ("Overdue".equals(status)) {
                    label.setForeground(new Color(231, 76, 60));
                } else {
                    label.setForeground(new Color(44, 62, 80));
                }

                if (!isSelected) {
                    label.setBackground(Color.WHITE);
                }

                return label;
            }
        });

        
        billTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = billTable.getSelectedRow();
                    if (row != -1) {
                        int billId = (Integer) tableModel.getValueAt(row, 0);
                        showBillDetails(billId);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        
        JPanel tipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        tipPanel.setBackground(Color.WHITE);
        tipPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JLabel tipLabel = new JLabel("Tip: Double-click on any row to view bill details");
        tipLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        tipLabel.setForeground(new Color(149, 165, 166));
        tipPanel.add(tipLabel);

        tablePanel.add(tipPanel, BorderLayout.SOUTH);

        
        JPanel contentWrapper = new JPanel(new BorderLayout(0, 15));
        contentWrapper.setBackground(new Color(245, 247, 250));
        contentWrapper.add(searchPanel, BorderLayout.NORTH);
        contentWrapper.add(tablePanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 38));

        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = bgColor;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void loadAllBills() {
        tableModel.setRowCount(0);
        List<Bill> bills = billDAO.getAllBills();

        for (Bill bill : bills) {
            Object[] row = {
                    bill.getBillId(),
                    bill.getPatientId(),
                    bill.getBillDate(),
                    bill.getBillType(),
                    "Rs. " + bill.getFinalAmount(),
                    "Rs. " + bill.getPaidAmount(),
                    "Rs. " + bill.getPendingAmount(),
                    bill.getPaymentStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void applyFilter() {
        String filter = (String) filterCombo.getSelectedItem();
        tableModel.setRowCount(0);

        List<Bill> bills;
        if ("Pending".equals(filter)) {
            bills = billDAO.getPendingBills();
        } else {
            bills = billDAO.getAllBills();
        }

        for (Bill bill : bills) {
            if ("All Bills".equals(filter) ||
                    (filter.equals("Paid") && "Fully Paid".equals(bill.getPaymentStatus())) ||
                    (filter.equals("Pending") && ("Pending".equals(bill.getPaymentStatus()) ||
                            "Partially Paid".equals(bill.getPaymentStatus())))
                    ||
                    (filter.equals("Overdue") && "Overdue".equals(bill.getPaymentStatus()))) {

                Object[] row = {
                        bill.getBillId(),
                        bill.getPatientId(),
                        bill.getBillDate(),
                        bill.getBillType(),
                        "Rs. " + bill.getFinalAmount(),
                        "Rs. " + bill.getPaidAmount(),
                        "Rs. " + bill.getPendingAmount(),
                        bill.getPaymentStatus()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void showGenerateBillDialog() {
        GenerateBillDialog dialog = new GenerateBillDialog(
                (Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadAllBills();
        }
    }

    private void showRecordPaymentDialog() {
        int row = billTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a bill from the table",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId = (Integer) tableModel.getValueAt(row, 0);

        RecordPaymentDialog dialog = new RecordPaymentDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), billId);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadAllBills();
        }
    }

    private void showBillDetails(int billId) {
        JOptionPane.showMessageDialog(this,
                "Bill details for Bill ID: " + billId,
                "Bill Details",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

