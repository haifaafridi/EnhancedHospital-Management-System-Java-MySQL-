


package com.hospital.dao;

import java.sql.*;

import com.hospital.util.DatabaseConnection;
import com.hospital.util.SessionManager;

public class AuditLogDAO {

    
    public void logAction(String actionType, String tableName, Integer recordId,
            String oldValue, String newValue, String description) {
        String sql = "INSERT INTO Audit_Log (user_id, action_type, table_name, record_id, " +
                "old_value, new_value, ip_address, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            
            if (SessionManager.isLoggedIn()) {
                pstmt.setInt(1, SessionManager.getCurrentUser().getUserId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, actionType);
            pstmt.setString(3, tableName);

            if (recordId != null) {
                pstmt.setInt(4, recordId);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setString(5, oldValue);
            pstmt.setString(6, newValue);
            pstmt.setString(7, SessionManager.getIpAddress());
            pstmt.setString(8, description);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error logging action: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public void logLogin(int userId, boolean successful) {
        String description = successful ? "Successful login" : "Failed login attempt";
        logAction("LOGIN", "Users", userId, null, null, description);
    }

    
    public void logLogout(int userId) {
        logAction("LOGOUT", "Users", userId, null, null, "User logged out");
    }

    
    public void logInsert(String tableName, Integer recordId, String newValue, String description) {
        logAction("INSERT", tableName, recordId, null, newValue, description);
    }

    
    public void logUpdate(String tableName, Integer recordId, String oldValue,
            String newValue, String description) {
        logAction("UPDATE", tableName, recordId, oldValue, newValue, description);
    }

    
    public void logDelete(String tableName, Integer recordId, String oldValue, String description) {
        logAction("DELETE", tableName, recordId, oldValue, null, description);
    }

    
    public void logView(String tableName, Integer recordId, String description) {
        logAction("VIEW", tableName, recordId, null, null, description);
    }

    
    public java.util.List<String> getRecentLogs(int limit) {
        java.util.List<String> logs = new java.util.ArrayList<>();
        String sql = "SELECT al.*, u.username FROM Audit_Log al " +
                "LEFT JOIN Users u ON al.user_id = u.user_id " +
                "ORDER BY al.timestamp DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String log = String.format("[%s] %s - %s: %s on %s (Record ID: %s)",
                        rs.getTimestamp("timestamp"),
                        rs.getString("username") != null ? rs.getString("username") : "System",
                        rs.getString("action_type"),
                        rs.getString("description"),
                        rs.getString("table_name"),
                        rs.getObject("record_id") != null ? rs.getInt("record_id") : "N/A");
                logs.add(log);
            }

        } catch (SQLException e) {
            System.err.println("Error getting audit logs: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    
    public java.util.List<String> getUserLogs(int userId, int limit) {
        java.util.List<String> logs = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Audit_Log WHERE user_id = ? " +
                "ORDER BY timestamp DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String log = String.format("[%s] %s: %s",
                        rs.getTimestamp("timestamp"),
                        rs.getString("action_type"),
                        rs.getString("description"));
                logs.add(log);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user logs: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }
}

