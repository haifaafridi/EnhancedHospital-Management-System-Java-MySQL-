package com.hospital.dao;

import com.hospital.model.MedicalHistory;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryDAO {

    // Get all medical histories with patient names
    public List<MedicalHistory> getAllMedicalHistories() {
        List<MedicalHistory> histories = new ArrayList<>();
        String sql = "SELECT mh.*, p.full_name " +
                "FROM Medical_History mh " +
                "JOIN Patients p ON mh.patient_id = p.patient_id " +
                "ORDER BY mh.updated_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MedicalHistory history = new MedicalHistory();
                history.setHistoryId(rs.getInt("history_id"));
                history.setPatientId(rs.getInt("patient_id"));
                history.setPatientName(rs.getString("full_name"));
                history.setAllergies(rs.getString("allergies"));
                history.setChronicConditions(rs.getString("chronic_conditions"));
                history.setPreviousSurgeries(rs.getString("previous_surgeries"));
                history.setCurrentMedications(rs.getString("current_medications"));
                history.setFamilyMedicalHistory(rs.getString("family_medical_history"));
                history.setSmokingStatus(rs.getString("smoking_status"));
                history.setAlcoholConsumption(rs.getString("alcohol_consumption"));
                history.setNotes(rs.getString("notes"));
                history.setCreatedAt(rs.getTimestamp("created_at"));
                history.setUpdatedAt(rs.getTimestamp("updated_at"));
                histories.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return histories;
    }

    // Get medical history by patient ID
    public MedicalHistory getMedicalHistoryByPatientId(int patientId) {
        String sql = "SELECT mh.*, p.full_name " +
                "FROM Medical_History mh " +
                "JOIN Patients p ON mh.patient_id = p.patient_id " +
                "WHERE mh.patient_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MedicalHistory history = new MedicalHistory();
                history.setHistoryId(rs.getInt("history_id"));
                history.setPatientId(rs.getInt("patient_id"));
                history.setPatientName(rs.getString("full_name"));
                history.setAllergies(rs.getString("allergies"));
                history.setChronicConditions(rs.getString("chronic_conditions"));
                history.setPreviousSurgeries(rs.getString("previous_surgeries"));
                history.setCurrentMedications(rs.getString("current_medications"));
                history.setFamilyMedicalHistory(rs.getString("family_medical_history"));
                history.setSmokingStatus(rs.getString("smoking_status"));
                history.setAlcoholConsumption(rs.getString("alcohol_consumption"));
                history.setNotes(rs.getString("notes"));
                history.setCreatedAt(rs.getTimestamp("created_at"));
                history.setUpdatedAt(rs.getTimestamp("updated_at"));
                return history;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get medical history by ID
    public MedicalHistory getMedicalHistoryById(int historyId) {
        String sql = "SELECT mh.*, p.full_name " +
                "FROM Medical_History mh " +
                "JOIN Patients p ON mh.patient_id = p.patient_id " +
                "WHERE mh.history_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, historyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MedicalHistory history = new MedicalHistory();
                history.setHistoryId(rs.getInt("history_id"));
                history.setPatientId(rs.getInt("patient_id"));
                history.setPatientName(rs.getString("full_name"));
                history.setAllergies(rs.getString("allergies"));
                history.setChronicConditions(rs.getString("chronic_conditions"));
                history.setPreviousSurgeries(rs.getString("previous_surgeries"));
                history.setCurrentMedications(rs.getString("current_medications"));
                history.setFamilyMedicalHistory(rs.getString("family_medical_history"));
                history.setSmokingStatus(rs.getString("smoking_status"));
                history.setAlcoholConsumption(rs.getString("alcohol_consumption"));
                history.setNotes(rs.getString("notes"));
                history.setCreatedAt(rs.getTimestamp("created_at"));
                history.setUpdatedAt(rs.getTimestamp("updated_at"));
                return history;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add new medical history
    public boolean addMedicalHistory(MedicalHistory history) {
        String sql = "INSERT INTO Medical_History (patient_id, allergies, chronic_conditions, " +
                "previous_surgeries, current_medications, family_medical_history, " +
                "smoking_status, alcohol_consumption, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, history.getPatientId());
            pstmt.setString(2, history.getAllergies());
            pstmt.setString(3, history.getChronicConditions());
            pstmt.setString(4, history.getPreviousSurgeries());
            pstmt.setString(5, history.getCurrentMedications());
            pstmt.setString(6, history.getFamilyMedicalHistory());
            pstmt.setString(7, history.getSmokingStatus());
            pstmt.setString(8, history.getAlcoholConsumption());
            pstmt.setString(9, history.getNotes());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update medical history
    public boolean updateMedicalHistory(MedicalHistory history) {
        String sql = "UPDATE Medical_History SET allergies = ?, chronic_conditions = ?, " +
                "previous_surgeries = ?, current_medications = ?, family_medical_history = ?, " +
                "smoking_status = ?, alcohol_consumption = ?, notes = ? " +
                "WHERE history_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, history.getAllergies());
            pstmt.setString(2, history.getChronicConditions());
            pstmt.setString(3, history.getPreviousSurgeries());
            pstmt.setString(4, history.getCurrentMedications());
            pstmt.setString(5, history.getFamilyMedicalHistory());
            pstmt.setString(6, history.getSmokingStatus());
            pstmt.setString(7, history.getAlcoholConsumption());
            pstmt.setString(8, history.getNotes());
            pstmt.setInt(9, history.getHistoryId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete medical history
    public boolean deleteMedicalHistory(int historyId) {
        String sql = "DELETE FROM Medical_History WHERE history_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, historyId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Search medical histories
    public List<MedicalHistory> searchMedicalHistories(String searchTerm) {
        List<MedicalHistory> histories = new ArrayList<>();
        String sql = "SELECT mh.*, p.full_name " +
                "FROM Medical_History mh " +
                "JOIN Patients p ON mh.patient_id = p.patient_id " +
                "WHERE p.full_name LIKE ? OR mh.allergies LIKE ? OR mh.chronic_conditions LIKE ? " +
                "ORDER BY mh.updated_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MedicalHistory history = new MedicalHistory();
                history.setHistoryId(rs.getInt("history_id"));
                history.setPatientId(rs.getInt("patient_id"));
                history.setPatientName(rs.getString("full_name"));
                history.setAllergies(rs.getString("allergies"));
                history.setChronicConditions(rs.getString("chronic_conditions"));
                history.setPreviousSurgeries(rs.getString("previous_surgeries"));
                history.setCurrentMedications(rs.getString("current_medications"));
                history.setFamilyMedicalHistory(rs.getString("family_medical_history"));
                history.setSmokingStatus(rs.getString("smoking_status"));
                history.setAlcoholConsumption(rs.getString("alcohol_consumption"));
                history.setNotes(rs.getString("notes"));
                history.setCreatedAt(rs.getTimestamp("created_at"));
                history.setUpdatedAt(rs.getTimestamp("updated_at"));
                histories.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return histories;
    }

    // Get count of medical histories
    public int getMedicalHistoryCount() {
        String sql = "SELECT COUNT(*) FROM Medical_History";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}