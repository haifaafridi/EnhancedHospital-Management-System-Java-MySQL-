


package com.hospital.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.hospital.model.*;
import com.hospital.util.DatabaseConnection;

public class AdmissionDAO {

    
    public boolean admitPatient(Admission admission) {
        String sql = "{CALL sp_admit_patient(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, admission.getPatientId());
            cstmt.setInt(2, admission.getRoomId());
            cstmt.setInt(3, admission.getAdmittingDoctorId());
            cstmt.setString(4, admission.getDiagnosis());
            cstmt.setString(5, admission.getAdmissionType());
            cstmt.setBigDecimal(6, admission.getInitialDeposit());

            cstmt.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error admitting patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public List<Admission> getCurrentAdmissions() {
        List<Admission> admissions = new ArrayList<>();
        String sql = "SELECT * FROM Admissions WHERE status = 'Admitted' ORDER BY admission_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                admissions.add(extractAdmissionFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting current admissions: " + e.getMessage());
            e.printStackTrace();
        }
        return admissions;
    }

    
    public List<Admission> getAdmissionsByPatient(int patientId) {
        List<Admission> admissions = new ArrayList<>();
        String sql = "SELECT * FROM Admissions WHERE patient_id = ? ORDER BY admission_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                admissions.add(extractAdmissionFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting patient admissions: " + e.getMessage());
            e.printStackTrace();
        }
        return admissions;
    }

    
    public Admission getAdmissionById(int admissionId) {
        String sql = "SELECT * FROM Admissions WHERE admission_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, admissionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAdmissionFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting admission: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    
    public boolean dischargePatient(int admissionId, String dischargeSummary,
            String dischargeInstructions) {
        String sql = "{CALL sp_discharge_patient(?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, admissionId);
            cstmt.setString(2, dischargeSummary);
            cstmt.setString(3, dischargeInstructions);

            cstmt.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error discharging patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean updateAdmission(Admission admission) {
        String sql = "UPDATE Admissions SET expected_discharge_date = ?, diagnosis = ?, " +
                "discharge_summary = ?, discharge_instructions = ? WHERE admission_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, admission.getExpectedDischargeDate());
            pstmt.setString(2, admission.getDiagnosis());
            pstmt.setString(3, admission.getDischargeSummary());
            pstmt.setString(4, admission.getDischargeInstructions());
            pstmt.setInt(5, admission.getAdmissionId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating admission: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public int getTotalAdmissions() {
        String sql = "SELECT COUNT(*) FROM Admissions";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting admission count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    
    public int getCurrentAdmissionCount() {
        String sql = "SELECT COUNT(*) FROM Admissions WHERE status = 'Admitted'";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting current admission count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    
    private Admission extractAdmissionFromResultSet(ResultSet rs) throws SQLException {
        Admission admission = new Admission();
        admission.setAdmissionId(rs.getInt("admission_id"));
        admission.setPatientId(rs.getInt("patient_id"));
        admission.setRoomId(rs.getInt("room_id"));
        admission.setAdmittingDoctorId(rs.getInt("admitting_doctor_id"));
        admission.setAdmissionDate(rs.getTimestamp("admission_date"));
        admission.setExpectedDischargeDate(rs.getDate("expected_discharge_date"));
        admission.setActualDischargeDate(rs.getTimestamp("actual_discharge_date"));
        admission.setDiagnosis(rs.getString("diagnosis"));
        admission.setAdmissionType(rs.getString("admission_type"));
        admission.setInitialDeposit(rs.getBigDecimal("initial_deposit"));
        admission.setStatus(rs.getString("status"));
        admission.setDischargeSummary(rs.getString("discharge_summary"));
        admission.setDischargeInstructions(rs.getString("discharge_instructions"));
        admission.setCreatedAt(rs.getTimestamp("created_at"));
        admission.setUpdatedAt(rs.getTimestamp("updated_at"));
        return admission;
    }
}

