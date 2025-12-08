package com.hospital.ui.ambulance;

import javax.swing.*;
import com.hospital.dao.AmbulanceDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.Ambulance;
import com.hospital.model.AmbulanceBooking;
import com.hospital.model.Patient;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

public class NewBookingDialog extends JDialog {
    private AmbulanceDAO ambulanceDAO;
    private PatientDAO patientDAO;
    private boolean success = false;

    private JComboBox<Ambulance> ambulanceCombo;
    private JComboBox<Patient> patientCombo;
    private JTextField patientNameField;
    private JTextField patientPhoneField;
    private JTextArea pickupLocationArea;
    private JTextArea destinationArea;

    private JSpinner timeSpinner;
    private JTextField distanceField;
    private JTextField chargesField;
    private JComboBox<String> emergencyLevelCombo;
    private JTextArea notesArea;
    private JCheckBox existingPatientCheck;

    public NewBookingDialog(Frame parent) {
        super(parent, "New Ambulance Booking", true);
        ambulanceDAO = new AmbulanceDAO();
        patientDAO = new PatientDAO();
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // FIX 1: Dynamic Height Calculation
        // This ensures the window is never taller than the user's screen,
        // so the buttons at the bottom remain visible.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int dialogHeight = Math.min(800, screenSize.height - 50); // Cap at 800 or screen height - 50px
        setSize(700, dialogHeight);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(52, 152, 219));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Create New Ambulance Booking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Ambulance Selection
        JLabel ambulanceLabel = createLabel("Select Ambulance:");
        ambulanceCombo = new JComboBox<>();
        loadAvailableAmbulances();
        ambulanceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ambulanceCombo.setPreferredSize(new Dimension(500, 35));
        ambulanceCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        ambulanceCombo.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Align component left

        formPanel.add(ambulanceLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(ambulanceCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Existing Patient Checkbox
        existingPatientCheck = new JCheckBox("Existing Patient");
        existingPatientCheck.setFont(new Font("Segoe UI", Font.BOLD, 14));
        existingPatientCheck.setBackground(Color.WHITE);
        existingPatientCheck.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Align component left
        existingPatientCheck.addActionListener(e -> togglePatientFields());

        formPanel.add(existingPatientCheck);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Patient Selection (for existing patients)
        JLabel patientLabel = createLabel("Select Patient:");
        patientCombo = new JComboBox<>();
        patientCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        patientCombo.setPreferredSize(new Dimension(500, 35));
        patientCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        patientCombo.setEnabled(false);
        patientCombo.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Align component left
        patientCombo.addActionListener(e -> fillPatientDetails());

        formPanel.add(patientLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(patientCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Patient Name
        JLabel nameLabel = createLabel("Patient Name:");
        patientNameField = createTextField();

        formPanel.add(nameLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(patientNameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Patient Phone
        JLabel phoneLabel = createLabel("Patient Phone:");
        patientPhoneField = createTextField();

        formPanel.add(phoneLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(patientPhoneField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Pickup Location
        JLabel pickupLabel = createLabel("Pickup Location:");
        pickupLocationArea = new JTextArea(3, 20);
        pickupLocationArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pickupLocationArea.setLineWrap(true);
        pickupLocationArea.setWrapStyleWord(true);
        JScrollPane pickupScroll = new JScrollPane(pickupLocationArea);
        pickupScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        pickupScroll.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Align component left

        formPanel.add(pickupLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(pickupScroll);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Destination
        JLabel destinationLabel = createLabel("Destination:");
        destinationArea = new JTextArea(3, 20);
        destinationArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        destinationArea.setLineWrap(true);
        destinationArea.setWrapStyleWord(true);
        JScrollPane destinationScroll = new JScrollPane(destinationArea);
        destinationScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        destinationScroll.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Align component left

        formPanel.add(destinationLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(destinationScroll);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Date and Time Panel
        JPanel dateTimePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        dateTimePanel.setBackground(Color.WHITE);
        dateTimePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        dateTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Align Panel left

        // Date
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
        datePanel.setBackground(Color.WHITE);

        JLabel dateLabel = createLabel("Booking Date:");

        datePanel.add(dateLabel);
        datePanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Time
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setBackground(Color.WHITE);

        JLabel timeLabel = createLabel("Booking Time:");
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeSpinner.setPreferredSize(new Dimension(250, 35));
        timeSpinner.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Align component left

        timePanel.add(timeLabel);
        timePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        timePanel.add(timeSpinner);

        dateTimePanel.add(datePanel);
        dateTimePanel.add(timePanel);

        formPanel.add(dateTimePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Distance and Charges Panel
        JPanel distanceChargesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        distanceChargesPanel.setBackground(Color.WHITE);
        distanceChargesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        distanceChargesPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Align Panel left

        // Distance
        JPanel distancePanel = new JPanel();
        distancePanel.setLayout(new BoxLayout(distancePanel, BoxLayout.Y_AXIS));
        distancePanel.setBackground(Color.WHITE);

        JLabel distanceLabel = createLabel("Distance (km):");
        distanceField = createTextField();
        distanceField.setPreferredSize(new Dimension(250, 35));

        distancePanel.add(distanceLabel);
        distancePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        distancePanel.add(distanceField);

        // Charges
        JPanel chargesPanel = new JPanel();
        chargesPanel.setLayout(new BoxLayout(chargesPanel, BoxLayout.Y_AXIS));
        chargesPanel.setBackground(Color.WHITE);

        JLabel chargesLabel = createLabel("Charges (PKR):");
        chargesField = createTextField();
        chargesField.setPreferredSize(new Dimension(250, 35));

        chargesPanel.add(chargesLabel);
        chargesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        chargesPanel.add(chargesField);

        distanceChargesPanel.add(distancePanel);
        distanceChargesPanel.add(chargesPanel);

        formPanel.add(distanceChargesPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Emergency Level
        JLabel emergencyLabel = createLabel("Emergency Level:");
        String[] emergencyLevels = { "Low", "Medium", "High", "Critical" };
        emergencyLevelCombo = new JComboBox<>(emergencyLevels);
        emergencyLevelCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emergencyLevelCombo.setPreferredSize(new Dimension(500, 35));
        emergencyLevelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        emergencyLevelCombo.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Align component left

        formPanel.add(emergencyLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(emergencyLevelCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Notes
        JLabel notesLabel = createLabel("Notes:");
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        notesScroll.setAlignmentX(Component.LEFT_ALIGNMENT); // FIX: Align component left

        formPanel.add(notesLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(notesScroll);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton saveButton = createStyledButton("Create Booking", new Color(46, 204, 113));
        saveButton.addActionListener(e -> saveBooking());

        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAvailableAmbulances() {
        List<Ambulance> ambulances = ambulanceDAO.getAvailableAmbulances();
        for (Ambulance ambulance : ambulances) {
            ambulanceCombo.addItem(ambulance);
        }
    }

    private void togglePatientFields() {
        boolean isExistingPatient = existingPatientCheck.isSelected();

        patientCombo.setEnabled(isExistingPatient);
        patientNameField.setEnabled(!isExistingPatient);
        patientPhoneField.setEnabled(!isExistingPatient);

        if (isExistingPatient) {
            loadPatients();
        } else {
            patientNameField.setText("");
            patientPhoneField.setText("");
        }
    }

    private void loadPatients() {
        patientCombo.removeAllItems();
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient patient : patients) {
            patientCombo.addItem(patient);
        }
    }

    private void fillPatientDetails() {
        if (!existingPatientCheck.isSelected())
            return;

        Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
        if (selectedPatient != null) {
            patientNameField.setText(selectedPatient.getFullName());
            patientPhoneField.setText(selectedPatient.getPhone());
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(52, 73, 94));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(500, 35));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // FIX 2: Text and Component Alignment
        // Ensure text starts at the left inside the box
        field.setHorizontalAlignment(JTextField.LEFT);
        // Ensure the box itself aligns left within the panel
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private void saveBooking() {
        try {
            // Validation
            if (ambulanceCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select an ambulance", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (patientNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter patient name", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (patientPhoneField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter patient phone", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create booking object
            AmbulanceBooking booking = new AmbulanceBooking();

            Ambulance selectedAmbulance = (Ambulance) ambulanceCombo.getSelectedItem();
            booking.setAmbulanceId(selectedAmbulance.getAmbulanceId());

            if (existingPatientCheck.isSelected() && patientCombo.getSelectedItem() != null) {
                Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
                booking.setPatientId(selectedPatient.getPatientId());
            }

            booking.setPatientName(patientNameField.getText().trim());
            booking.setPatientPhone(patientPhoneField.getText().trim());
            booking.setPickupLocation(pickupLocationArea.getText().trim());
            booking.setDestination(destinationArea.getText().trim());

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timeString = sdf.format(timeSpinner.getValue());
            booking.setBookingTime(Time.valueOf(timeString));

            if (!distanceField.getText().trim().isEmpty()) {
                booking.setDistanceKm(new BigDecimal(distanceField.getText().trim()));
            }

            if (!chargesField.getText().trim().isEmpty()) {
                booking.setCharges(new BigDecimal(chargesField.getText().trim()));
            }

            booking.setStatus("Confirmed");
            booking.setEmergencyLevel((String) emergencyLevelCombo.getSelectedItem());
            booking.setNotes(notesArea.getText().trim());

            // Save booking
            if (ambulanceDAO.createBooking(booking)) {
                success = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to create booking",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isSuccess() {
        return success;
    }
}