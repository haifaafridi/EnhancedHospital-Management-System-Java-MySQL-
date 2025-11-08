


package com.hospital.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.hospital.model.Appointment;
import com.hospital.util.DatabaseConnection;

public class AppointmentDAO {

    
    public boolean createAppointment(Appointment appointment) {
        String sql = "INSERT INTO Appointments (patient_id, doctor_id, appointment_date, " +
                "appointment_time, appointment_type, status, reason) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setDate(3, appointment.getAppointmentDate());
            pstmt.setTime(4, appointment.getAppointmentTime());
            pstmt.setString(5, appointment.getAppointmentType());
            pstmt.setString(6, appointment.getStatus());
            pstmt.setString(7, appointment.getReason());

            System.out.println("Executing SQL: " + sql);
            System.out.println("Values: PatientID=" + appointment.getPatientId() +
                    ", DoctorID=" + appointment.getDoctorId() +
                    ", Date=" + appointment.getAppointmentDate() +
                    ", Time=" + appointment.getAppointmentTime());

            int affected = pstmt.executeUpdate();

            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    appointment.setAppointmentId(rs.getInt(1));
                    System.out.println("Appointment created successfully with ID: " + appointment.getAppointmentId());
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public List<Appointment> getTodayAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.full_name as patient_name, d.full_name as doctor_name " +
                "FROM Appointments a " +
                "JOIN Patients p ON a.patient_id = p.patient_id " +
                "JOIN Doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE a.appointment_date = CURDATE() " +
                "ORDER BY a.appointment_time";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Loading today's appointments...");
            int count = 0;

            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
                count++;
            }

            System.out.println("Loaded " + count + " appointments for today");

        } catch (SQLException e) {
            System.err.println("Error getting today's appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.full_name as patient_name, d.full_name as doctor_name " +
                "FROM Appointments a " +
                "JOIN Patients p ON a.patient_id = p.patient_id " +
                "JOIN Doctors d ON a.doctor_id = d.doctor_id " +
                "ORDER BY a.appointment_date DESC, a.appointment_time DESC " +
                "LIMIT 500";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    
    public List<Appointment> getAppointmentsByDoctor(int doctorId, Date date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.full_name as patient_name, d.full_name as doctor_name " +
                "FROM Appointments a " +
                "JOIN Patients p ON a.patient_id = p.patient_id " +
                "JOIN Doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE a.doctor_id = ? AND a.appointment_date = ? " +
                "ORDER BY a.appointment_time";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting doctor appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.full_name as patient_name, d.full_name as doctor_name " +
                "FROM Appointments a " +
                "JOIN Patients p ON a.patient_id = p.patient_id " +
                "JOIN Doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE a.patient_id = ? " +
                "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting patient appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        String sql = "UPDATE Appointments SET status = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, appointmentId);

            System.out.println("Updating appointment " + appointmentId + " to status: " + status);
            int affected = pstmt.executeUpdate();

            if (affected > 0) {
                System.out.println("Status updated successfully");
                return true;
            } else {
                System.out.println("No rows affected - appointment might not exist");
            }

        } catch (SQLException e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE Appointments SET appointment_date = ?, appointment_time = ?, " +
                "appointment_type = ?, status = ?, reason = ?, diagnosis = ?, " +
                "prescription = ?, notes = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, appointment.getAppointmentDate());
            pstmt.setTime(2, appointment.getAppointmentTime());
            pstmt.setString(3, appointment.getAppointmentType());
            pstmt.setString(4, appointment.getStatus());
            pstmt.setString(5, appointment.getReason());
            pstmt.setString(6, appointment.getDiagnosis());
            pstmt.setString(7, appointment.getPrescription());
            pstmt.setString(8, appointment.getNotes());
            pstmt.setInt(9, appointment.getAppointmentId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean deleteAppointment(int appointmentId) {
        
        return updateAppointmentStatus(appointmentId, "Cancelled");
    }

    
    public Appointment getAppointmentById(int appointmentId) {
        String sql = "SELECT a.*, p.full_name as patient_name, d.full_name as doctor_name " +
                "FROM Appointments a " +
                "JOIN Patients p ON a.patient_id = p.patient_id " +
                "JOIN Doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE a.appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAppointmentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting appointment by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    
    public List<Appointment> searchAppointments(String searchTerm) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.full_name as patient_name, d.full_name as doctor_name " +
                "FROM Appointments a " +
                "JOIN Patients p ON a.patient_id = p.patient_id " +
                "JOIN Doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE p.full_name LIKE ? OR d.full_name LIKE ? OR a.status LIKE ? " +
                "ORDER BY a.appointment_date DESC, a.appointment_time DESC " +
                "LIMIT 100";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    
    public boolean isSlotAvailable(int doctorId, Date date, Time time) {
        String sql = "SELECT COUNT(*) FROM Appointments " +
                "WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? " +
                "AND status NOT IN ('Cancelled', 'Completed')";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, date);
            pstmt.setTime(3, time);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking slot availability: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public int getAppointmentCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Appointments WHERE status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting appointment count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    
    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        appointment.setAppointmentDate(rs.getDate("appointment_date"));
        appointment.setAppointmentTime(rs.getTime("appointment_time"));
        appointment.setAppointmentType(rs.getString("appointment_type"));
        appointment.setStatus(rs.getString("status"));
        appointment.setReason(rs.getString("reason"));
        appointment.setDiagnosis(rs.getString("diagnosis"));
        appointment.setPrescription(rs.getString("prescription"));
        appointment.setNotes(rs.getString("notes"));
        appointment.setCreatedAt(rs.getTimestamp("created_at"));
        appointment.setUpdatedAt(rs.getTimestamp("updated_at"));

        
        appointment.setPatientName(rs.getString("patient_name"));
        appointment.setDoctorName(rs.getString("doctor_name"));

        return appointment;
    }
}

