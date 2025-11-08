


package com.hospital.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.hospital.model.Patient;
import com.hospital.util.DatabaseConnection;

public class PatientDAO {

    
    public boolean registerPatient(Patient patient) {
        String sql = "INSERT INTO Patients (id_type, id_number, full_name, date_of_birth, age, " +
                "gender, blood_group, phone, email, emergency_contact_name, " +
                "emergency_contact_phone, address, city, country, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, patient.getIdType());
            pstmt.setString(2, patient.getIdNumber());
            pstmt.setString(3, patient.getFullName());
            pstmt.setDate(4, patient.getDateOfBirth());
            pstmt.setInt(5, patient.getAge());
            pstmt.setString(6, patient.getGender());
            pstmt.setString(7, patient.getBloodGroup());
            pstmt.setString(8, patient.getPhone());
            pstmt.setString(9, patient.getEmail());
            pstmt.setString(10, patient.getEmergencyContactName());
            pstmt.setString(11, patient.getEmergencyContactPhone());
            pstmt.setString(12, patient.getAddress());
            pstmt.setString(13, patient.getCity());
            pstmt.setString(14, patient.getCountry());
            pstmt.setString(15, patient.getStatus());

            int affected = pstmt.executeUpdate();

            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    patient.setPatientId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error registering patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients WHERE status = 'Active' ORDER BY patient_id DESC LIMIT 1000";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all patients: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    
    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM Patients WHERE patient_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractPatientFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting patient by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    
    public List<Patient> searchPatients(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients WHERE " +
                "full_name LIKE ? OR phone LIKE ? OR id_number LIKE ? " +
                "ORDER BY patient_id DESC LIMIT 100";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching patients: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE Patients SET full_name = ?, phone = ?, email = ?, " +
                "address = ?, city = ?, emergency_contact_name = ?, " +
                "emergency_contact_phone = ?, blood_group = ? " +
                "WHERE patient_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getFullName());
            pstmt.setString(2, patient.getPhone());
            pstmt.setString(3, patient.getEmail());
            pstmt.setString(4, patient.getAddress());
            pstmt.setString(5, patient.getCity());
            pstmt.setString(6, patient.getEmergencyContactName());
            pstmt.setString(7, patient.getEmergencyContactPhone());
            pstmt.setString(8, patient.getBloodGroup());
            pstmt.setInt(9, patient.getPatientId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean updatePatientStatus(int patientId, String status) {
        String sql = "UPDATE Patients SET status = ? WHERE patient_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, patientId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating patient status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public Patient getPatientByIdNumber(String idType, String idNumber) {
        String sql = "SELECT * FROM Patients WHERE id_type = ? AND id_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idType);
            pstmt.setString(2, idNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractPatientFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting patient by ID number: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    
    public List<Patient> getPatientsByStatus(String status) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients WHERE status = ? ORDER BY patient_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting patients by status: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    
    public List<Patient> getPatientsByBloodGroup(String bloodGroup) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients WHERE blood_group = ? AND status = 'Active' " +
                "ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bloodGroup);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting patients by blood group: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    
    public int getTotalPatientCount() {
        String sql = "SELECT COUNT(*) FROM Patients";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting total patient count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    
    public int getActivePatientCount() {
        String sql = "SELECT COUNT(*) FROM Patients WHERE status = 'Active'";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting active patient count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    
    public int getPatientCountByGender(String gender) {
        String sql = "SELECT COUNT(*) FROM Patients WHERE gender = ? AND status = 'Active'";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, gender);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting patient count by gender: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    
    public boolean deletePatient(int patientId) {
        
        String checkSql = "SELECT COUNT(*) FROM Appointments WHERE patient_id = ? " +
                "AND status IN ('Scheduled', 'Confirmed') " +
                "UNION ALL " +
                "SELECT COUNT(*) FROM Admissions WHERE patient_id = ? " +
                "AND status = 'Admitted'";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, patientId);
            checkStmt.setInt(2, patientId);
            ResultSet rs = checkStmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                count += rs.getInt(1);
            }

            if (count > 0) {
                System.err.println("Cannot delete patient with active appointments or admissions");
                return false;
            }

            
            return updatePatientStatus(patientId, "Inactive");

        } catch (SQLException e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public List<Patient> getRecentlyRegisteredPatients(int limit) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients WHERE status = 'Active' " +
                "ORDER BY registration_date DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting recently registered patients: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    
    public boolean patientExists(String idType, String idNumber) {
        String sql = "SELECT COUNT(*) FROM Patients WHERE id_type = ? AND id_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idType);
            pstmt.setString(2, idNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking if patient exists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public PatientStatistics getPatientStatistics(int patientId) {
        PatientStatistics stats = new PatientStatistics();

        try (Connection conn = DatabaseConnection.getConnection()) {
            
            String sql1 = "SELECT COUNT(*) FROM Appointments WHERE patient_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                pstmt.setInt(1, patientId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.totalAppointments = rs.getInt(1);
                }
            }

            
            String sql2 = "SELECT COUNT(*) FROM Admissions WHERE patient_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                pstmt.setInt(1, patientId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.totalAdmissions = rs.getInt(1);
                }
            }

            
            String sql3 = "SELECT COUNT(*), SUM(pending_amount) FROM Bills WHERE patient_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql3)) {
                pstmt.setInt(1, patientId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.totalBills = rs.getInt(1);
                    stats.pendingAmount = rs.getBigDecimal(2);
                    if (stats.pendingAmount == null) {
                        stats.pendingAmount = java.math.BigDecimal.ZERO;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting patient statistics: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    
    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setIdType(rs.getString("id_type"));
        patient.setIdNumber(rs.getString("id_number"));
        patient.setFullName(rs.getString("full_name"));
        patient.setDateOfBirth(rs.getDate("date_of_birth"));
        patient.setAge(rs.getInt("age"));
        patient.setGender(rs.getString("gender"));
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setPhone(rs.getString("phone"));
        patient.setEmail(rs.getString("email"));
        patient.setEmergencyContactName(rs.getString("emergency_contact_name"));
        patient.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        patient.setAddress(rs.getString("address"));
        patient.setCity(rs.getString("city"));
        patient.setCountry(rs.getString("country"));
        patient.setRegistrationDate(rs.getTimestamp("registration_date"));
        patient.setStatus(rs.getString("status"));
        return patient;
    }

    
    public static class PatientStatistics {
        public int totalAppointments;
        public int totalAdmissions;
        public int totalBills;
        public java.math.BigDecimal pendingAmount;

        @Override
        public String toString() {
            return String.format("Appointments: %d, Admissions: %d, Bills: %d, Pending: Rs. %s",
                    totalAppointments, totalAdmissions, totalBills, pendingAmount);
        }
    }
}

