package com.hospital.ui.room;

import javax.swing.*;
import com.hospital.dao.RoomDAO;
import com.hospital.model.Room;
import java.awt.*;
import java.math.BigDecimal;

public class EditRoomDialog extends JDialog {
    private Room room;
    private JTextField roomNumberField;
    private JComboBox<String> roomTypeCombo;
    private JSpinner floorSpinner;
    private JSpinner capacitySpinner;
    private JTextField priceField;
    private JComboBox<String> statusCombo;
    private JTextArea amenitiesArea;
    private RoomDAO roomDAO;
    private boolean success = false;

    public EditRoomDialog(Frame parent, Room room) {
        super(parent, "Edit Room", true);
        this.room = room;
        roomDAO = new RoomDAO();
        initComponents();
        loadRoomData();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(600, 700);
        setResizable(false);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel headerLabel = new JLabel("Edit Room - " + room.getRoomNumber());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Room Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel roomNumberLabel = new JLabel("Room Number: *");
        roomNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(roomNumberLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        roomNumberField = new JTextField();
        roomNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomNumberField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(roomNumberField, gbc);

        // Room Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel roomTypeLabel = new JLabel("Room Type: *");
        roomTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(roomTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] roomTypes = { "General", "Private", "ICU", "NICU", "Emergency", "Operation Theater" };
        roomTypeCombo = new JComboBox<>(roomTypes);
        roomTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomTypeCombo.setPreferredSize(new Dimension(250, 35));
        formPanel.add(roomTypeCombo, gbc);

        // Floor
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel floorLabel = new JLabel("Floor:");
        floorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(floorLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        floorSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        floorSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        floorSpinner.setPreferredSize(new Dimension(250, 35));
        formPanel.add(floorSpinner, gbc);

        // Capacity
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel capacityLabel = new JLabel("Capacity:");
        capacityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(capacityLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        capacitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        capacitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        capacitySpinner.setPreferredSize(new Dimension(250, 35));
        formPanel.add(capacitySpinner, gbc);

        // Price per Day
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel priceLabel = new JLabel("Price per Day (Rs.): *");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        priceField = new JTextField();
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(priceField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        JLabel statusLabel = new JLabel("Status: *");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] statuses = { "Available", "Occupied", "Under Maintenance", "Reserved" };
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusCombo.setPreferredSize(new Dimension(250, 35));
        formPanel.add(statusCombo, gbc);

        // Amenities
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.3;
        JLabel amenitiesLabel = new JLabel("Amenities:");
        amenitiesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amenitiesLabel.setVerticalAlignment(JLabel.TOP);
        formPanel.add(amenitiesLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        amenitiesArea = new JTextArea(4, 20);
        amenitiesArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        amenitiesArea.setLineWrap(true);
        amenitiesArea.setWrapStyleWord(true);
        amenitiesArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        JScrollPane amenitiesScroll = new JScrollPane(amenitiesArea);
        amenitiesScroll.setPreferredSize(new Dimension(250, 80));
        formPanel.add(amenitiesScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Update Room");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(140, 40));
        saveButton.addActionListener(e -> updateRoom());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(140, 40));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadRoomData() {
        roomNumberField.setText(room.getRoomNumber());
        roomTypeCombo.setSelectedItem(room.getRoomType());

        if (room.getFloor() != null) {
            floorSpinner.setValue(room.getFloor());
        }

        if (room.getCapacity() != null) {
            capacitySpinner.setValue(room.getCapacity());
        }

        priceField.setText(room.getPricePerDay().toString());
        statusCombo.setSelectedItem(room.getAvailabilityStatus());

        if (room.getAmenities() != null) {
            amenitiesArea.setText(room.getAmenities());
        }
    }

    private void updateRoom() {
        // Validation
        if (roomNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Room number is required",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            roomNumberField.requestFocus();
            return;
        }

        if (priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Price per day is required",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }

            // Update Room object
            room.setRoomNumber(roomNumberField.getText().trim());
            room.setRoomType((String) roomTypeCombo.getSelectedItem());
            room.setFloor((Integer) floorSpinner.getValue());
            room.setCapacity((Integer) capacitySpinner.getValue());
            room.setPricePerDay(price);
            room.setAvailabilityStatus((String) statusCombo.getSelectedItem());
            room.setAmenities(amenitiesArea.getText().trim());

            // Update in database
            if (roomDAO.updateRoom(room)) {
                success = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update room",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid price (positive number)",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
        }
    }

    public boolean isSuccess() {
        return success;
    }
}