package com.hospital.ui.ambulance;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import com.hospital.dao.AmbulanceDAO;
import com.hospital.model.Ambulance;
import com.hospital.model.AmbulanceBooking;
import java.awt.*;
import java.util.List;

public class AmbulancePanel extends JPanel {
    private JTable ambulanceTable;
    private JTable bookingTable;
    private DefaultTableModel ambulanceTableModel;
    private DefaultTableModel bookingTableModel;
    private AmbulanceDAO ambulanceDAO;
    private JTextField searchField;
    private JTabbedPane tabbedPane;
    private JLabel availableCountLabel;
    private JLabel onCallCountLabel;
    private JLabel maintenanceCountLabel;

    // colors matching your AppointmentPanel
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color HEADER_BG = new Color(52, 73, 94);
    private final Color BG_COLOR = new Color(245, 247, 250);

    public AmbulancePanel() {
        ambulanceDAO = new AmbulanceDAO();
        initComponents();
        loadAmbulances();
        loadTodayBookings();
        updateStatistics();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // --- Top Section (Title & Buttons) ---
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(BG_COLOR);
        topSection.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BG_COLOR);

        JLabel titleLabel = new JLabel("Ambulance Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subtitleLabel = new JLabel("Manage fleet status and dispatch bookings");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        // Top Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BG_COLOR);

        JButton newBookingButton = createStyledButton("+ New Booking", new Color(46, 204, 113));
        newBookingButton.addActionListener(e -> showNewBookingDialog());

        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> refreshAll());

        buttonPanel.add(newBookingButton);
        buttonPanel.add(refreshButton);

        topSection.add(titlePanel, BorderLayout.WEST);
        topSection.add(buttonPanel, BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        // --- Center Content (Stats + Tabs) ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_COLOR);

        // 1. Statistics Panel
        JPanel statsPanel = createStatisticsPanel();

        // Fix: Stop stats panel from stretching too big.
        // We wrap it in a container that respects preferred size.
        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setBackground(BG_COLOR);
        statsWrapper.add(statsPanel, BorderLayout.WEST);
        statsWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110)); // Limit height

        // 2. Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        // Create Tabs
        tabbedPane.addTab("Fleet Overview", createAmbulancesTab());
        tabbedPane.addTab("Booking History", createBookingsTab());

        contentPanel.add(statsWrapper);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing
        contentPanel.add(tabbedPane);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createStatisticsPanel() {
        // Grid layout with gaps
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(BG_COLOR);
        // Explicit preferred size to prevent them from looking "Too Large"
        statsPanel.setPreferredSize(new Dimension(800, 100));

        availableCountLabel = new JLabel("0", SwingConstants.CENTER);
        JPanel availableCard = createStatCard("Available", availableCountLabel, new Color(46, 204, 113));

        onCallCountLabel = new JLabel("0", SwingConstants.CENTER);
        JPanel onCallCard = createStatCard("On Call", onCallCountLabel, new Color(241, 196, 15));

        maintenanceCountLabel = new JLabel("0", SwingConstants.CENTER);
        JPanel maintenanceCard = createStatCard("Maintenance", maintenanceCountLabel, new Color(231, 76, 60));

        statsPanel.add(availableCard);
        statsPanel.add(onCallCard);
        statsPanel.add(maintenanceCard);

        return statsPanel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        // Add a subtle border
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(127, 140, 141));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32)); // Slightly smaller than before
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createAmbulancesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding inside tab

        // Search Bar Area
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel searchLabel = new JLabel("Search Fleet:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.addActionListener(e -> searchAmbulances());

        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> searchAmbulances());

        JButton clearButton = createStyledButton("Clear", new Color(149, 165, 166));
        clearButton.setPreferredSize(new Dimension(80, 35));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadAmbulances();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = { "ID", "Vehicle No.", "Type", "Model", "Year", "Driver", "Phone", "Status", "Next Maint." };
        ambulanceTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ambulanceTable = new JTable(ambulanceTableModel);
        styleTable(ambulanceTable); // APPLY THE FIX

        // Custom renderer for Status column colors
        ambulanceTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                String status = (value != null) ? value.toString() : "";
                if (status.equals("Available"))
                    label.setForeground(new Color(46, 204, 113));
                else if (status.equals("On Call"))
                    label.setForeground(new Color(241, 196, 15));
                else if (status.equals("Under Maintenance"))
                    label.setForeground(new Color(231, 76, 60));
                else
                    label.setForeground(Color.BLACK);

                if (!isSelected)
                    label.setBackground(Color.WHITE);
                return label;
            }
        });

        // Context Menu
        setupAmbulanceContextMenu();

        JScrollPane scrollPane = new JScrollPane(ambulanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE); // Fixes grey background issue
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Filter Controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JButton todayButton = createStyledButton("Today's Bookings", PRIMARY_COLOR);
        todayButton.setPreferredSize(new Dimension(160, 35));
        todayButton.addActionListener(e -> loadTodayBookings());

        JButton allButton = createStyledButton("All History", new Color(155, 89, 182));
        allButton.setPreferredSize(new Dimension(120, 35));
        allButton.addActionListener(e -> loadAllBookings());

        filterPanel.add(todayButton);
        filterPanel.add(allButton);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = { "ID", "Vehicle", "Patient", "Phone", "Pickup", "Destination", "Date", "Time", "Emergency",
                "Status" };
        bookingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingTable = new JTable(bookingTableModel);
        styleTable(bookingTable); // APPLY THE FIX

        // Custom renderer for Status column
        bookingTable.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                String status = (value != null) ? value.toString() : "";
                if (status.equals("Confirmed") || status.equals("Dispatched"))
                    label.setForeground(new Color(241, 196, 15));
                else if (status.equals("Completed"))
                    label.setForeground(new Color(46, 204, 113));
                else if (status.equals("Cancelled"))
                    label.setForeground(new Color(231, 76, 60));

                if (!isSelected)
                    label.setBackground(Color.WHITE);
                return label;
            }
        });

        setupBookingContextMenu();

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // --- KEY FIX: UNIFIED TABLE STYLING METHOD ---
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40); // Matches AppointmentPanel
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(232, 244, 253));
        table.setSelectionForeground(new Color(33, 33, 33));
        table.setBackground(Color.WHITE);

        // Header Styling - FIXES "Invisible Headers"
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setReorderingAllowed(false);

        // Custom Header Renderer to force Dark Blue Background
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setForeground(Color.WHITE); // White Text
                label.setBackground(HEADER_BG); // Dark Blue Background
                label.setOpaque(true); // Force opaque to show color
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                label.setHorizontalAlignment(JLabel.LEFT);
                return label;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    private void setupAmbulanceContextMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem viewDetailsItem = new JMenuItem("View Details");
        JMenuItem markAvailableItem = new JMenuItem("Mark Available");
        JMenuItem markOnCallItem = new JMenuItem("Mark On Call");
        JMenuItem markMaintenanceItem = new JMenuItem("Mark Under Maintenance");

        viewDetailsItem.addActionListener(e -> viewAmbulanceDetails());
        markAvailableItem.addActionListener(e -> updateAmbulanceStatus("Available"));
        markOnCallItem.addActionListener(e -> updateAmbulanceStatus("On Call"));
        markMaintenanceItem.addActionListener(e -> updateAmbulanceStatus("Under Maintenance"));

        popupMenu.add(viewDetailsItem);
        popupMenu.addSeparator();
        popupMenu.add(markAvailableItem);
        popupMenu.add(markOnCallItem);
        popupMenu.add(markMaintenanceItem);

        ambulanceTable.setComponentPopupMenu(popupMenu);
    }

    private void setupBookingContextMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem viewDetailsItem = new JMenuItem("View Details");
        JMenuItem dispatchItem = new JMenuItem("Dispatch");
        JMenuItem completeItem = new JMenuItem("Mark Completed");
        JMenuItem cancelItem = new JMenuItem("Cancel Booking");

        viewDetailsItem.addActionListener(e -> viewBookingDetails());
        dispatchItem.addActionListener(e -> updateBookingStatus("Dispatched"));
        completeItem.addActionListener(e -> updateBookingStatus("Completed"));
        cancelItem.addActionListener(e -> updateBookingStatus("Cancelled"));

        popupMenu.add(viewDetailsItem);
        popupMenu.addSeparator();
        popupMenu.add(dispatchItem);
        popupMenu.add(completeItem);
        popupMenu.add(cancelItem);

        bookingTable.setComponentPopupMenu(popupMenu);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        return button;
    }

    // --- DATA LOADING & ACTIONS (Kept mostly same, just ensured refreshing works)
    // ---

    private void loadAmbulances() {
        ambulanceTableModel.setRowCount(0);
        List<Ambulance> ambulances = ambulanceDAO.getAllAmbulances();
        for (Ambulance a : ambulances) {
            ambulanceTableModel.addRow(new Object[] {
                    a.getAmbulanceId(), a.getVehicleNumber(), a.getAmbulanceType(),
                    a.getModel(), a.getYear(), a.getDriverName(), a.getDriverPhone(),
                    a.getStatus(), a.getNextMaintenanceDate()
            });
        }
    }

    private void loadTodayBookings() {
        bookingTableModel.setRowCount(0);
        List<AmbulanceBooking> bookings = ambulanceDAO.getTodayBookings();
        populateBookingTable(bookings);
    }

    private void loadAllBookings() {
        bookingTableModel.setRowCount(0);
        List<AmbulanceBooking> bookings = ambulanceDAO.getAllBookings();
        populateBookingTable(bookings);
    }

    private void populateBookingTable(List<AmbulanceBooking> bookings) {
        for (AmbulanceBooking b : bookings) {
            bookingTableModel.addRow(new Object[] {
                    b.getBookingId(), b.getVehicleNumber(), b.getPatientName(),
                    b.getPatientPhone(), b.getPickupLocation(), b.getDestination(),
                    b.getBookingDate(), b.getBookingTime(), b.getEmergencyLevel(), b.getStatus()
            });
        }
    }

    private void searchAmbulances() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadAmbulances();
            return;
        }
        ambulanceTableModel.setRowCount(0);
        List<Ambulance> ambulances = ambulanceDAO.searchAmbulances(searchText);
        if (ambulances.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No ambulances found matching: " + searchText);
        }
        for (Ambulance a : ambulances) {
            ambulanceTableModel.addRow(new Object[] {
                    a.getAmbulanceId(), a.getVehicleNumber(), a.getAmbulanceType(),
                    a.getModel(), a.getYear(), a.getDriverName(), a.getDriverPhone(),
                    a.getStatus(), a.getNextMaintenanceDate()
            });
        }
    }

    private void updateStatistics() {
        int available = ambulanceDAO.getAmbulanceCountByStatus("Available");
        int onCall = ambulanceDAO.getAmbulanceCountByStatus("On Call");
        int maintenance = ambulanceDAO.getAmbulanceCountByStatus("Under Maintenance");

        availableCountLabel.setText(String.valueOf(available));
        onCallCountLabel.setText(String.valueOf(onCall));
        maintenanceCountLabel.setText(String.valueOf(maintenance));
    }

    private void showNewBookingDialog() {
        NewBookingDialog dialog = new NewBookingDialog(
                (Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadTodayBookings();
            updateStatistics();
            JOptionPane.showMessageDialog(this, "Booking created successfully!");
        }
    }

    private void viewAmbulanceDetails() {
        int row = ambulanceTable.getSelectedRow();
        if (row == -1)
            return;
        int id = (Integer) ambulanceTableModel.getValueAt(row, 0);
        Ambulance a = ambulanceDAO.getAmbulanceById(id);
        if (a != null) {
            JOptionPane.showMessageDialog(this,
                    "Vehicle: " + a.getVehicleNumber() + "\nDriver: " + a.getDriverName() + "\nStatus: "
                            + a.getStatus(),
                    "Ambulance Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewBookingDetails() {
        int row = bookingTable.getSelectedRow();
        if (row == -1)
            return;
        JOptionPane.showMessageDialog(this, "Details for Booking ID: " + bookingTableModel.getValueAt(row, 0));
    }

    private void updateAmbulanceStatus(String status) {
        int row = ambulanceTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an ambulance.");
            return;
        }
        int id = (Integer) ambulanceTableModel.getValueAt(row, 0);
        if (ambulanceDAO.updateAmbulanceStatus(id, status)) {
            loadAmbulances();
            updateStatistics();
        }
    }

    private void updateBookingStatus(String status) {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking.");
            return;
        }
        int id = (Integer) bookingTableModel.getValueAt(row, 0);
        if (ambulanceDAO.updateBookingStatus(id, status)) {
            loadTodayBookings();
            updateStatistics();
        }
    }

    private void refreshAll() {
        loadAmbulances();
        loadTodayBookings();
        updateStatistics();
        JOptionPane.showMessageDialog(this, "Data refreshed!");
    }
}