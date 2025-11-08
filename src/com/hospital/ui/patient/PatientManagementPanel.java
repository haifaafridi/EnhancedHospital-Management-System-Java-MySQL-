


package com.hospital.ui.patient;

import javax.swing.*;
import javax.swing.table.*;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;

import java.awt.*;
import java.util.List;

public class PatientManagementPanel extends JPanel {
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private PatientDAO patientDAO;
    private JTextField searchField;

    public PatientManagementPanel() {
        patientDAO = new PatientDAO();
        initComponents();
        loadPatients();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage and view all patient records");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleSection.add(titleLabel);
        titleSection.add(Box.createRigidArea(new Dimension(0, 5)));
        titleSection.add(subtitleLabel);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton addButton = createStyledButton("+ Register New Patient", new Color(46, 204, 113),
                new Color(39, 174, 96));
        addButton.addActionListener(e -> showRegisterPatientDialog());

        JButton refreshButton = createStyledButton(" Refresh", new Color(52, 152, 219), new Color(41, 128, 185));
        refreshButton.addActionListener(e -> loadPatients());

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        topPanel.add(titleSection, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel searchLabel = new JLabel("Search Patient:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchLabel.setForeground(new Color(52, 73, 94));

        searchField = new JTextField(35);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JButton searchButton = createStyledButton("Search", new Color(52, 152, 219), new Color(41, 128, 185));
        searchButton.addActionListener(e -> searchPatients());

        JButton clearButton = createStyledButton("Clear", new Color(149, 165, 166), new Color(127, 140, 141));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadPatients();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchButton);
        searchPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        searchPanel.add(clearButton);

        
        String[] columns = { "ID", "Name", "Age", "Gender", "Blood Group", "Phone", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientTable.setRowHeight(35);
        patientTable.setGridColor(new Color(236, 240, 241));
        patientTable.setSelectionBackground(new Color(174, 214, 241));
        patientTable.setSelectionForeground(new Color(44, 62, 80));
        patientTable.setShowVerticalLines(true);
        patientTable.setShowHorizontalLines(true);
        patientTable.setIntercellSpacing(new Dimension(1, 1));
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        
        JTableHeader header = patientTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        
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

        for (int i = 0; i < patientTable.getColumnCount(); i++) {
            patientTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        
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

                
                if (column == 6 && value != null) {
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    String status = value.toString();
                    if ("Active".equals(status)) {
                        setForeground(new Color(39, 174, 96));
                    } else if ("Discharged".equals(status)) {
                        setForeground(new Color(52, 152, 219));
                    } else {
                        setForeground(new Color(192, 57, 43));
                    }
                }

                return c;
            }
        };

        for (int i = 0; i < patientTable.getColumnCount(); i++) {
            patientTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(60); 
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(200); 
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(60); 
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(80); 
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(100); 
        patientTable.getColumnModel().getColumn(5).setPreferredWidth(130); 
        patientTable.getColumnModel().getColumn(6).setPreferredWidth(100); 

        
        patientTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = patientTable.getSelectedRow();
                    if (row != -1) {
                        int patientId = (Integer) tableModel.getValueAt(row, 0);
                        showPatientDetails(patientId);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JLabel infoLabel = new JLabel("Tip: Double-click on any row to view patient details");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(127, 140, 141));
        infoPanel.add(infoLabel);

        
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

    private void loadPatients() {
        tableModel.setRowCount(0);
        List<Patient> patients = patientDAO.getAllPatients();

        for (Patient patient : patients) {
            Object[] row = {
                    patient.getPatientId(),
                    patient.getFullName(),
                    patient.getAge(),
                    patient.getGender(),
                    patient.getBloodGroup(),
                    patient.getPhone(),
                    patient.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void searchPatients() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadPatients();
            return;
        }

        tableModel.setRowCount(0);
        List<Patient> patients = patientDAO.searchPatients(searchTerm);

        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No patients found matching your search criteria.",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Patient patient : patients) {
            Object[] row = {
                    patient.getPatientId(),
                    patient.getFullName(),
                    patient.getAge(),
                    patient.getGender(),
                    patient.getBloodGroup(),
                    patient.getPhone(),
                    patient.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void showRegisterPatientDialog() {
        RegisterPatientDialog dialog = new RegisterPatientDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadPatients();
        }
    }

    private void showPatientDetails(int patientId) {
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient != null) {
            PatientDetailsDialog dialog = new PatientDetailsDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), patient);
            dialog.setVisible(true);

            if (dialog.isUpdated()) {
                loadPatients();
            }
        }
    }
}

