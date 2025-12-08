package com.hospital.ui.room;

import javax.swing.*;
import javax.swing.table.*;
import com.hospital.dao.RoomDAO;
import com.hospital.model.Room;
import java.awt.*;
import java.util.List;

public class RoomManagementPanel extends JPanel {
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private RoomDAO roomDAO;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;

    public RoomManagementPanel() {
        roomDAO = new RoomDAO();
        initComponents();
        loadAllRooms();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Top Section
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(new Color(245, 247, 250));
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Room Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subtitleLabel = new JLabel("Manage and monitor all hospital rooms");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setBackground(new Color(245, 247, 250));
        titleTextPanel.add(titleLabel);
        titleTextPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titleTextPanel.add(subtitleLabel);

        titlePanel.add(titleTextPanel, BorderLayout.WEST);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton newButton = createStyledButton("+ Add Room", new Color(46, 204, 113));
        newButton.addActionListener(e -> showAddRoomDialog());

        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> loadAllRooms());

        JButton statsButton = createStyledButton("Room Statistics", new Color(155, 89, 182));
        statsButton.addActionListener(e -> showRoomStatistics());

        buttonPanel.add(newButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(refreshButton);

        topSection.add(titlePanel, BorderLayout.NORTH);
        topSection.add(buttonPanel, BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        // Search and Filter Panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JPanel searchInnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchInnerPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search Room:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(new Color(52, 73, 94));

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(new Color(52, 73, 94));

        String[] filterOptions = { "All Rooms", "Available", "Occupied", "Under Maintenance", "Reserved" };
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterComboBox.setPreferredSize(new Dimension(180, 35));
        filterComboBox.addActionListener(e -> filterRooms());

        JButton searchButton = createStyledButton("Search", new Color(52, 152, 219));
        searchButton.addActionListener(e -> searchRooms());

        JButton clearButton = createStyledButton("Clear", new Color(149, 165, 166));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            filterComboBox.setSelectedIndex(0);
            loadAllRooms();
        });

        searchInnerPanel.add(searchLabel);
        searchInnerPanel.add(searchField);
        searchInnerPanel.add(filterLabel);
        searchInnerPanel.add(filterComboBox);
        searchInnerPanel.add(searchButton);
        searchInnerPanel.add(clearButton);

        searchPanel.add(searchInnerPanel, BorderLayout.WEST);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        String[] columns = { "Room ID", "Room Number", "Type", "Floor", "Capacity", "Price/Day", "Status",
                "Amenities" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roomTable.setRowHeight(40);
        roomTable.setShowGrid(true);
        roomTable.setGridColor(new Color(240, 240, 240));
        roomTable.setSelectionBackground(new Color(232, 244, 253));
        roomTable.setSelectionForeground(new Color(33, 33, 33));
        roomTable.setBackground(Color.WHITE);

        // Table Header Styling
        JTableHeader header = roomTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        // Custom Header Renderer
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

        for (int i = 0; i < roomTable.getColumnCount(); i++) {
            roomTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Column Widths
        roomTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        roomTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        roomTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        roomTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        roomTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        roomTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        roomTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        roomTable.getColumnModel().getColumn(7).setPreferredWidth(250);

        // Status Column Renderer
        roomTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                if (value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "Available":
                            label.setForeground(new Color(46, 204, 113));
                            break;
                        case "Occupied":
                            label.setForeground(new Color(231, 76, 60));
                            break;
                        case "Under Maintenance":
                            label.setForeground(new Color(230, 126, 34));
                            break;
                        case "Reserved":
                            label.setForeground(new Color(52, 152, 219));
                            break;
                        default:
                            label.setForeground(new Color(149, 165, 166));
                    }
                }

                if (!isSelected) {
                    label.setBackground(Color.WHITE);
                }
                return label;
            }
        });

        // Right-click Context Menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem viewItem = new JMenuItem("View Details");
        JMenuItem editItem = new JMenuItem("Edit Room");
        JMenuItem updateStatusItem = new JMenuItem("Update Status");
        JMenuItem deleteItem = new JMenuItem("Delete Room");

        viewItem.addActionListener(e -> viewRoomDetails());
        editItem.addActionListener(e -> editRoom());
        updateStatusItem.addActionListener(e -> updateRoomStatus());
        deleteItem.addActionListener(e -> deleteRoom());

        popupMenu.add(viewItem);
        popupMenu.add(editItem);
        popupMenu.addSeparator();
        popupMenu.add(updateStatusItem);
        popupMenu.addSeparator();
        popupMenu.add(deleteItem);

        roomTable.setComponentPopupMenu(popupMenu);

        // Double-click to view details
        roomTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewRoomDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Tip Label
        JLabel tipLabel = new JLabel(
                "Tip: Right-click on any row for options | Double-click to view details");
        tipLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        tipLabel.setForeground(new Color(149, 165, 166));
        tipLabel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        tipLabel.setBackground(Color.WHITE);
        tipLabel.setOpaque(true);

        tablePanel.add(tipLabel, BorderLayout.SOUTH);

        // Content Panel Assembly
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
        int buttonWidth = textWidth + 50;
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

    private void loadAllRooms() {
        tableModel.setRowCount(0);
        List<Room> rooms = roomDAO.getAllRooms();

        for (Room room : rooms) {
            Object[] row = {
                    room.getRoomId(),
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getFloor() != null ? room.getFloor() : "N/A",
                    room.getCapacity() != null ? room.getCapacity() : "N/A",
                    "Rs. " + String.format("%.2f", room.getPricePerDay()),
                    room.getAvailabilityStatus(),
                    room.getAmenities() != null ? room.getAmenities() : "None"
            };
            tableModel.addRow(row);
        }
    }

    private void searchRooms() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadAllRooms();
            return;
        }

        tableModel.setRowCount(0);
        List<Room> rooms = roomDAO.getAllRooms();

        for (Room room : rooms) {
            if (room.getRoomNumber().toLowerCase().contains(searchText.toLowerCase()) ||
                    room.getRoomType().toLowerCase().contains(searchText.toLowerCase())) {
                Object[] row = {
                        room.getRoomId(),
                        room.getRoomNumber(),
                        room.getRoomType(),
                        room.getFloor() != null ? room.getFloor() : "N/A",
                        room.getCapacity() != null ? room.getCapacity() : "N/A",
                        "Rs. " + String.format("%.2f", room.getPricePerDay()),
                        room.getAvailabilityStatus(),
                        room.getAmenities() != null ? room.getAmenities() : "None"
                };
                tableModel.addRow(row);
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No rooms found matching: " + searchText,
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterRooms() {
        String selectedFilter = (String) filterComboBox.getSelectedItem();

        if (selectedFilter.equals("All Rooms")) {
            loadAllRooms();
            return;
        }

        tableModel.setRowCount(0);
        List<Room> rooms = roomDAO.getAllRooms();

        for (Room room : rooms) {
            if (room.getAvailabilityStatus().equals(selectedFilter)) {
                Object[] row = {
                        room.getRoomId(),
                        room.getRoomNumber(),
                        room.getRoomType(),
                        room.getFloor() != null ? room.getFloor() : "N/A",
                        room.getCapacity() != null ? room.getCapacity() : "N/A",
                        "Rs. " + String.format("%.2f", room.getPricePerDay()),
                        room.getAvailabilityStatus(),
                        room.getAmenities() != null ? room.getAmenities() : "None"
                };
                tableModel.addRow(row);
            }
        }
    }

    private void showAddRoomDialog() {
        AddRoomDialog dialog = new AddRoomDialog(
                (Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadAllRooms();
            JOptionPane.showMessageDialog(this,
                    "Room added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewRoomDetails() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a room",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (Integer) tableModel.getValueAt(row, 0);
        Room room = roomDAO.getRoomById(roomId);

        if (room != null) {
            StringBuilder details = new StringBuilder();
            details.append("Room ID: ").append(room.getRoomId()).append("\n");
            details.append("Room Number: ").append(room.getRoomNumber()).append("\n");
            details.append("Type: ").append(room.getRoomType()).append("\n");
            details.append("Floor: ").append(room.getFloor() != null ? room.getFloor() : "N/A").append("\n");
            details.append("Capacity: ").append(room.getCapacity() != null ? room.getCapacity() : "N/A").append("\n");
            details.append("Price per Day: Rs. ").append(String.format("%.2f", room.getPricePerDay())).append("\n");
            details.append("Status: ").append(room.getAvailabilityStatus()).append("\n");
            details.append("Amenities: ").append(room.getAmenities() != null ? room.getAmenities() : "None")
                    .append("\n");
            if (room.getLastCleaned() != null) {
                details.append("Last Cleaned: ").append(room.getLastCleaned()).append("\n");
            }

            JOptionPane.showMessageDialog(this,
                    details.toString(),
                    "Room Details",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a room to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (Integer) tableModel.getValueAt(row, 0);
        Room room = roomDAO.getRoomById(roomId);

        if (room != null) {
            EditRoomDialog dialog = new EditRoomDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), room);
            dialog.setVisible(true);

            if (dialog.isSuccess()) {
                loadAllRooms();
                JOptionPane.showMessageDialog(this,
                        "Room updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void updateRoomStatus() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a room",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (Integer) tableModel.getValueAt(row, 0);
        String[] statuses = { "Available", "Occupied", "Under Maintenance", "Reserved" };

        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Select new status:",
                "Update Room Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                statuses[0]);

        if (newStatus != null) {
            if (roomDAO.updateRoomStatus(roomId, newStatus)) {
                JOptionPane.showMessageDialog(this,
                        "Room status updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadAllRooms();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update room status",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a room to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int roomId = (Integer) tableModel.getValueAt(row, 0);
        String roomNumber = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Room " + roomNumber + "?\n" +
                        "This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (roomDAO.deleteRoom(roomId)) {
                JOptionPane.showMessageDialog(this,
                        "Room deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadAllRooms();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete room. Room may be currently in use.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRoomStatistics() {
        int available = roomDAO.getRoomCountByStatus("Available");
        int occupied = roomDAO.getRoomCountByStatus("Occupied");
        int maintenance = roomDAO.getRoomCountByStatus("Under Maintenance");
        int reserved = roomDAO.getRoomCountByStatus("Reserved");
        int total = available + occupied + maintenance + reserved;

        StringBuilder stats = new StringBuilder();
        stats.append("Room Statistics\n\n");
        stats.append("Total Rooms: ").append(total).append("\n\n");
        stats.append("Available: ").append(available).append("\n");
        stats.append("Occupied: ").append(occupied).append("\n");
        stats.append("Under Maintenance: ").append(maintenance).append("\n");
        stats.append("Reserved: ").append(reserved).append("\n\n");

        if (total > 0) {
            stats.append("Occupancy Rate: ")
                    .append(String.format("%.1f%%", (occupied * 100.0 / total)));
        }

        JOptionPane.showMessageDialog(this,
                stats.toString(),
                "Room Statistics",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshTable() {
        loadAllRooms();
    }
}