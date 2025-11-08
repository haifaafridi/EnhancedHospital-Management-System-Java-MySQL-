


package com.hospital.ui.appointment;

import javax.swing.*;

import com.hospital.dao.*;
import com.hospital.model.*;

import java.awt.*;
import java.sql.*;
import java.util.Calendar;
import java.util.List;

public class NewAppointmentDialog extends JDialog {
    private boolean success = false;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;

    private JComboBox<PatientItem> patientCombo;
    private JComboBox<DoctorItem> doctorCombo;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JComboBox<String> typeCombo;
    private JTextArea reasonArea;
    private JButton checkAvailabilityButton;

    private List<Patient> patients;
    private List<Doctor> doctors;

    public NewAppointmentDialog(Frame parent) {
        super(parent, "Schedule New Appointment", true);
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
        doctorDAO = new DoctorDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setSize(600, 650);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        
        JLabel titleLabel = new JLabel("Schedule New Appointment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel subtitleLabel = new JLabel("Fill in the details below to create an appointment");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        
        patientCombo = new JComboBox<>();
        patientCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientCombo.setMaximumRowCount(10);

        
        doctorCombo = new JComboBox<>();
        doctorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        doctorCombo.setMaximumRowCount(10);

        
        Calendar calendar = Calendar.getInstance();
        SpinnerDateModel dateModel = new SpinnerDateModel(calendar.getTime(), null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        SpinnerDateModel timeModel = new SpinnerDateModel(calendar.getTime(), null, null, Calendar.MINUTE);
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        
        typeCombo = new JComboBox<>(new String[] { "Consultation", "Follow-up", "Emergency", "Check-up" });
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        
        reasonArea = new JTextArea(4, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reasonArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        
        checkAvailabilityButton = createStyledButton("Check Availability", new Color(52, 152, 219));
        checkAvailabilityButton.addActionListener(e -> checkAvailability());

        
        addFormField(mainPanel, "Patient:", patientCombo);
        addFormField(mainPanel, "Doctor:", doctorCombo);
        addFormField(mainPanel, "Date:", dateSpinner);
        addFormField(mainPanel, "Time:", timeSpinner);
        addFormField(mainPanel, "Type:", typeCombo);

        
        JPanel availPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        availPanel.setBackground(Color.WHITE);
        availPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        availPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        availPanel.add(checkAvailabilityButton);
        mainPanel.add(availPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        addFormField(mainPanel, "Reason:", new JScrollPane(reasonArea));

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton saveButton = createStyledButton("Schedule Appointment", new Color(46, 204, 113));
        saveButton.addActionListener(e -> scheduleAppointment());

        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buttonPanel);

        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void addFormField(JPanel panel, String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        component.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                component instanceof JScrollPane ? 100 : 35));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(component);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void loadData() {
        System.out.println("Loading patients and doctors...");

        patients = patientDAO.getAllPatients();
        doctors = doctorDAO.getAvailableDoctors();

        System.out.println("Found " + patients.size() + " patients");
        System.out.println("Found " + doctors.size() + " doctors");

        patientCombo.removeAllItems();
        for (Patient p : patients) {
            patientCombo.addItem(new PatientItem(p));
        }

        doctorCombo.removeAllItems();
        for (Doctor d : doctors) {
            doctorCombo.addItem(new DoctorItem(d));
        }

        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No patients found in the system. Please register patients first.",
                    "No Patients",
                    JOptionPane.WARNING_MESSAGE);
        }

        if (doctors.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No available doctors found in the system.",
                    "No Doctors",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void checkAvailability() {
        DoctorItem selectedDoctor = (DoctorItem) doctorCombo.getSelectedItem();
        if (selectedDoctor == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a doctor first",
                    "No Doctor Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            java.util.Date appointmentDate = (java.util.Date) dateSpinner.getValue();
            java.util.Date appointmentTime = (java.util.Date) timeSpinner.getValue();

            Date sqlDate = new Date(appointmentDate.getTime());
            Time sqlTime = new Time(appointmentTime.getTime());

            boolean available = appointmentDAO.isSlotAvailable(
                    selectedDoctor.doctor.getDoctorId(),
                    sqlDate,
                    sqlTime);

            if (available) {
                JOptionPane.showMessageDialog(this,
                        "✓ Time slot is available!",
                        "Available",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "✗ This time slot is already booked. Please choose another time.",
                        "Not Available",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error checking availability: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void scheduleAppointment() {
        try {
            
            PatientItem selectedPatient = (PatientItem) patientCombo.getSelectedItem();
            DoctorItem selectedDoctor = (DoctorItem) doctorCombo.getSelectedItem();

            if (selectedPatient == null || selectedDoctor == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select both patient and doctor",
                        "Incomplete Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (reasonArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a reason for the appointment",
                        "Reason Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            
            java.util.Date appointmentDate = (java.util.Date) dateSpinner.getValue();
            java.util.Date appointmentTime = (java.util.Date) timeSpinner.getValue();

            
            Date sqlDate = new Date(appointmentDate.getTime());
            Time sqlTime = new Time(appointmentTime.getTime());

            
            Calendar now = Calendar.getInstance();
            Calendar appointmentCal = Calendar.getInstance();
            appointmentCal.setTime(appointmentDate);
            appointmentCal.set(Calendar.HOUR_OF_DAY, appointmentTime.getHours());
            appointmentCal.set(Calendar.MINUTE, appointmentTime.getMinutes());

            if (appointmentCal.before(now)) {
                JOptionPane.showMessageDialog(this,
                        "Cannot schedule appointment in the past",
                        "Invalid Date/Time",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            
            boolean available = appointmentDAO.isSlotAvailable(
                    selectedDoctor.doctor.getDoctorId(),
                    sqlDate,
                    sqlTime);

            if (!available) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "This slot appears to be taken. Schedule anyway?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            
            Appointment appointment = new Appointment();
            appointment.setPatientId(selectedPatient.patient.getPatientId());
            appointment.setDoctorId(selectedDoctor.doctor.getDoctorId());
            appointment.setAppointmentDate(sqlDate);
            appointment.setAppointmentTime(sqlTime);
            appointment.setAppointmentType((String) typeCombo.getSelectedItem());
            appointment.setStatus("Scheduled");
            appointment.setReason(reasonArea.getText().trim());

            
            System.out.println("Creating appointment:");
            System.out.println("  Patient ID: " + appointment.getPatientId());
            System.out.println("  Doctor ID: " + appointment.getDoctorId());
            System.out.println("  Date: " + appointment.getAppointmentDate());
            System.out.println("  Time: " + appointment.getAppointmentTime());
            System.out.println("  Type: " + appointment.getAppointmentType());
            System.out.println("  Reason: " + appointment.getReason());

            
            if (appointmentDAO.createAppointment(appointment)) {
                success = true;
                JOptionPane.showMessageDialog(this,
                        "Appointment scheduled successfully!\n" +
                                "Appointment ID: " + appointment.getAppointmentId(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to schedule appointment. Please check the console for errors.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error scheduling appointment:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }

    
    private static class PatientItem {
        Patient patient;

        PatientItem(Patient patient) {
            this.patient = patient;
        }

        @Override
        public String toString() {
            return patient.getPatientId() + " - " + patient.getFullName() +
                    " (" + patient.getPhone() + ")";
        }
    }

    private static class DoctorItem {
        Doctor doctor;

        DoctorItem(Doctor doctor) {
            this.doctor = doctor;
        }

        @Override
        public String toString() {
            return doctor.getDoctorId() + " - " + doctor.getFullName() +
                    " (" + doctor.getSpecialization() + ")";
        }
    }
}

