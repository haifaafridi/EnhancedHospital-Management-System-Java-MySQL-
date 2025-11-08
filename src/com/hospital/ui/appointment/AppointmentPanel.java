


package com.hospital.ui.appointment;

import javax.swing.*;
import javax.swing.table.*;

import com.hospital.dao.*;
import com.hospital.model.Appointment;

import java.awt.*;
import java.util.List;

public class AppointmentPanel extends JPanel {
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private AppointmentDAO appointmentDAO;
    private JTextField searchField;

    public AppointmentPanel() {
        appointmentDAO = new AppointmentDAO();
        initComponents();
        loadTodayAppointments();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(new Color(245, 247, 250));
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subtitleLabel = new JLabel("Manage and view all appointment records");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setBackground(new Color(245, 247, 250));
        titleTextPanel.add(titleLabel);
        titleTextPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titleTextPanel.add(subtitleLabel);

        titlePanel.add(titleTextPanel, BorderLayout.WEST);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton newButton = createStyledButton("+ New Appointment", new Color(46, 204, 113));
        newButton.addActionListener(e -> showNewAppointmentDialog());

        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> loadTodayAppointments());

        JButton viewAllButton = createStyledButton("View All", new Color(155, 89, 182));
        viewAllButton.addActionListener(e -> loadAllAppointments());

        buttonPanel.add(newButton);
        buttonPanel.add(viewAllButton);
        buttonPanel.add(refreshButton);

        topSection.add(titlePanel, BorderLayout.NORTH);
        topSection.add(buttonPanel, BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JPanel searchInnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchInnerPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search Appointment:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(new Color(52, 73, 94));

        searchField = new JTextField(30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(400, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        
        searchField.addActionListener(e -> searchAppointments());

        JButton searchButton = createStyledButton("Search", new Color(52, 152, 219));
        searchButton.addActionListener(e -> searchAppointments());

        JButton clearButton = createStyledButton("Clear", new Color(149, 165, 166));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadTodayAppointments();
        });

        searchInnerPanel.add(searchLabel);
        searchInnerPanel.add(searchField);
        searchInnerPanel.add(searchButton);
        searchInnerPanel.add(clearButton);

        searchPanel.add(searchInnerPanel, BorderLayout.WEST);

        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        String[] columns = { "ID", "Patient", "Doctor", "Date", "Time", "Type", "Status", "Reason" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        appointmentTable.setRowHeight(40);
        appointmentTable.setShowGrid(true);
        appointmentTable.setGridColor(new Color(240, 240, 240));
        appointmentTable.setSelectionBackground(new Color(232, 244, 253));
        appointmentTable.setSelectionForeground(new Color(33, 33, 33));
        appointmentTable.setBackground(Color.WHITE);

        
        JTableHeader header = appointmentTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        
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

        for (int i = 0; i < appointmentTable.getColumnCount(); i++) {
            appointmentTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        
        appointmentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        appointmentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        appointmentTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        appointmentTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        appointmentTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        appointmentTable.getColumnModel().getColumn(7).setPreferredWidth(200);

        
        appointmentTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                if (value != null) {
                    String status = value.toString();
                    if (status.equals("Scheduled") || status.equals("Confirmed")) {
                        label.setForeground(new Color(46, 204, 113));
                    } else if (status.equals("Completed")) {
                        label.setForeground(new Color(52, 152, 219));
                    } else if (status.equals("Cancelled")) {
                        label.setForeground(new Color(231, 76, 60));
                    } else if (status.equals("No-Show")) {
                        label.setForeground(new Color(230, 126, 34));
                    } else {
                        label.setForeground(new Color(149, 165, 166));
                    }
                }

                if (!isSelected) {
                    label.setBackground(Color.WHITE);
                }

                return label;
            }
        });

        
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem viewItem = new JMenuItem("View Details");
        JMenuItem confirmItem = new JMenuItem("Confirm Appointment");
        JMenuItem completeItem = new JMenuItem("Mark as Completed");
        JMenuItem cancelItem = new JMenuItem("Cancel Appointment");
        JMenuItem noShowItem = new JMenuItem("Mark as No-Show");

        viewItem.addActionListener(e -> viewAppointmentDetails());
        confirmItem.addActionListener(e -> updateAppointmentStatus("Confirmed"));
        completeItem.addActionListener(e -> updateAppointmentStatus("Completed"));
        cancelItem.addActionListener(e -> updateAppointmentStatus("Cancelled"));
        noShowItem.addActionListener(e -> updateAppointmentStatus("No-Show"));

        popupMenu.add(viewItem);
        popupMenu.addSeparator();
        popupMenu.add(confirmItem);
        popupMenu.add(completeItem);
        popupMenu.add(cancelItem);
        popupMenu.add(noShowItem);

        appointmentTable.setComponentPopupMenu(popupMenu);

        
        appointmentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewAppointmentDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        
        JLabel tipLabel = new JLabel(
                "Tip: Right-click on any row to update appointment status | Double-click to view details");
        tipLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        tipLabel.setForeground(new Color(149, 165, 166));
        tipLabel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        tipLabel.setBackground(Color.WHITE);
        tipLabel.setOpaque(true);

        tablePanel.add(tipLabel, BorderLayout.SOUTH);

        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 247, 250));

        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(searchPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(tablePanel);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        FontMetrics metrics = button.getFontMetrics(button.getFont());
        int textWidth = metrics.stringWidth(text);
        int buttonWidth = textWidth + 100;
        button.setPreferredSize(new Dimension(buttonWidth, 40));
        button.setMinimumSize(new Dimension(buttonWidth, 40));

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

    

    private void loadTodayAppointments() {
        tableModel.setRowCount(0);

        System.out.println("Loading today's appointments...");
        List<Appointment> appointments = appointmentDAO.getTodayAppointments();

        System.out.println("Found " + appointments.size() + " appointments");

        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No appointments found for today.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        for (Appointment a : appointments) {
            Object[] row = {
                    a.getAppointmentId(),
                    a.getPatientName(),
                    a.getDoctorName(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getAppointmentType(),
                    a.getStatus(),
                    a.getReason()
            };
            tableModel.addRow(row);
        }
    }

    private void loadAllAppointments() {
        tableModel.setRowCount(0);

        List<Appointment> appointments = appointmentDAO.getAllAppointments();

        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No appointments found in the system.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Appointment a : appointments) {
            Object[] row = {
                    a.getAppointmentId(),
                    a.getPatientName(),
                    a.getDoctorName(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getAppointmentType(),
                    a.getStatus(),
                    a.getReason()
            };
            tableModel.addRow(row);
        }

        JOptionPane.showMessageDialog(this,
                "Loaded " + appointments.size() + " appointments.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchAppointments() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            loadTodayAppointments();
            return;
        }

        tableModel.setRowCount(0);
        List<Appointment> appointments = appointmentDAO.searchAppointments(searchText);

        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No appointments found matching: " + searchText,
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Appointment a : appointments) {
            Object[] row = {
                    a.getAppointmentId(),
                    a.getPatientName(),
                    a.getDoctorName(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getAppointmentType(),
                    a.getStatus(),
                    a.getReason()
            };
            tableModel.addRow(row);
        }
    }

    private void showNewAppointmentDialog() {
        NewAppointmentDialog dialog = new NewAppointmentDialog(
                (Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadTodayAppointments();
            JOptionPane.showMessageDialog(this,
                    "Appointment created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewAppointmentDetails() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (Integer) tableModel.getValueAt(row, 0);
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);

        if (appointment != null) {
            StringBuilder details = new StringBuilder();
            details.append("Appointment ID: ").append(appointment.getAppointmentId()).append("\n");
            details.append("Patient: ").append(appointment.getPatientName()).append("\n");
            details.append("Doctor: ").append(appointment.getDoctorName()).append("\n");
            details.append("Date: ").append(appointment.getAppointmentDate()).append("\n");
            details.append("Time: ").append(appointment.getAppointmentTime()).append("\n");
            details.append("Type: ").append(appointment.getAppointmentType()).append("\n");
            details.append("Status: ").append(appointment.getStatus()).append("\n");
            details.append("Reason: ").append(appointment.getReason()).append("\n");
            if (appointment.getDiagnosis() != null) {
                details.append("Diagnosis: ").append(appointment.getDiagnosis()).append("\n");
            }
            if (appointment.getNotes() != null) {
                details.append("Notes: ").append(appointment.getNotes()).append("\n");
            }

            JOptionPane.showMessageDialog(this,
                    details.toString(),
                    "Appointment Details",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateAppointmentStatus(String status) {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (Integer) tableModel.getValueAt(row, 0);
        String currentStatus = (String) tableModel.getValueAt(row, 6);

        
        if (currentStatus.equals("Completed") || currentStatus.equals("Cancelled")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "This appointment is already " + currentStatus + ". Update anyway?",
                    "Confirm Update",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        if (appointmentDAO.updateAppointmentStatus(appointmentId, status)) {
            JOptionPane.showMessageDialog(this,
                    "Appointment status updated to: " + status,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTodayAppointments();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update appointment status",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public void refreshTable() {
        loadTodayAppointments();
    }
}

