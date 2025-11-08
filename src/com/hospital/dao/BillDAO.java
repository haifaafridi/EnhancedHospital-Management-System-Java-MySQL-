


package com.hospital.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.hospital.model.*;
import com.hospital.util.DatabaseConnection;

import java.math.BigDecimal;

public class BillDAO {

    
    public boolean createBill(Bill bill) {
        String sql = "INSERT INTO Bills (patient_id, admission_id, bill_type, total_amount, " +
                "discount_amount, tax_amount, final_amount, paid_amount, pending_amount, " +
                "payment_status, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, bill.getPatientId());

            if (bill.getAdmissionId() != null) {
                pstmt.setInt(2, bill.getAdmissionId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, bill.getBillType());
            pstmt.setBigDecimal(4, bill.getTotalAmount());
            pstmt.setBigDecimal(5, bill.getDiscountAmount());
            pstmt.setBigDecimal(6, bill.getTaxAmount());
            pstmt.setBigDecimal(7, bill.getFinalAmount());
            pstmt.setBigDecimal(8, bill.getPaidAmount());
            pstmt.setBigDecimal(9, bill.getPendingAmount());
            pstmt.setString(10, bill.getPaymentStatus());
            pstmt.setString(11, bill.getNotes());

            int affected = pstmt.executeUpdate();

            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    bill.setBillId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error creating bill: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM Bills ORDER BY bill_date DESC LIMIT 1000";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all bills: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    
    public Bill getBillById(int billId) {
        String sql = "SELECT * FROM Bills WHERE bill_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, billId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractBillFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting bill: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    
    public List<Bill> getBillsByPatient(int patientId) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM Bills WHERE patient_id = ? ORDER BY bill_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting patient bills: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    
    public List<Bill> getPendingBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM Bills WHERE payment_status IN ('Pending', 'Partially Paid') " +
                "ORDER BY bill_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting pending bills: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    
    public boolean recordPayment(int billId, BigDecimal amount, String paymentMethod,
            String transactionRef, Integer receivedBy) {
        String sql = "{CALL sp_record_payment(?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, billId);
            cstmt.setBigDecimal(2, amount);
            cstmt.setString(3, paymentMethod);
            cstmt.setString(4, transactionRef);

            if (receivedBy != null) {
                cstmt.setInt(5, receivedBy);
            } else {
                cstmt.setNull(5, Types.INTEGER);
            }

            cstmt.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error recording payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean updateBill(Bill bill) {
        String sql = "UPDATE Bills SET total_amount = ?, discount_amount = ?, " +
                "tax_amount = ?, final_amount = ?, notes = ? WHERE bill_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, bill.getTotalAmount());
            pstmt.setBigDecimal(2, bill.getDiscountAmount());
            pstmt.setBigDecimal(3, bill.getTaxAmount());
            pstmt.setBigDecimal(4, bill.getFinalAmount());
            pstmt.setString(5, bill.getNotes());
            pstmt.setInt(6, bill.getBillId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating bill: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(paid_amount) FROM Bills";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                BigDecimal revenue = rs.getBigDecimal(1);
                return revenue != null ? revenue : BigDecimal.ZERO;
            }

        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    
    public BigDecimal getTotalPending() {
        String sql = "SELECT SUM(pending_amount) FROM Bills WHERE payment_status != 'Fully Paid'";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                BigDecimal pending = rs.getBigDecimal(1);
                return pending != null ? pending : BigDecimal.ZERO;
            }

        } catch (SQLException e) {
            System.err.println("Error getting total pending: " + e.getMessage());
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    
    private Bill extractBillFromResultSet(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setPatientId(rs.getInt("patient_id"));
        bill.setAdmissionId((Integer) rs.getObject("admission_id"));
        bill.setBillDate(rs.getTimestamp("bill_date"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        bill.setTaxAmount(rs.getBigDecimal("tax_amount"));
        bill.setFinalAmount(rs.getBigDecimal("final_amount"));
        bill.setPaidAmount(rs.getBigDecimal("paid_amount"));
        bill.setPendingAmount(rs.getBigDecimal("pending_amount"));
        bill.setPaymentStatus(rs.getString("payment_status"));
        bill.setBillType(rs.getString("bill_type"));
        bill.setNotes(rs.getString("notes"));
        return bill;
    }
}

