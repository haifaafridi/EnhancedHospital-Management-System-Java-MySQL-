package com.hospital.dao;

import com.hospital.model.Ambulance;
import com.hospital.model.AmbulanceBooking;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmbulanceDAO {

    // Get all ambulances
    public List<Ambulance> getAllAmbulances() {
        List<Ambulance> ambulances = new ArrayList<>();
        String sql = "SELECT * FROM Ambulances ORDER BY ambulance_id";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ambulance ambulance = extractAmbulanceFromResultSet(rs);
                ambulances.add(ambulance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ambulances;
    }

    // Get available ambulances
    public List<Ambulance> getAvailableAmbulances() {
        List<Ambulance> ambulances = new ArrayList<>();
        String sql = "SELECT * FROM Ambulances WHERE status = 'Available' ORDER BY ambulance_type";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ambulance ambulance = extractAmbulanceFromResultSet(rs);
                ambulances.add(ambulance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ambulances;
    }

    // Get ambulance by ID
    public Ambulance getAmbulanceById(int ambulanceId) {
        String sql = "SELECT * FROM Ambulances WHERE ambulance_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ambulanceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractAmbulanceFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Search ambulances
    public List<Ambulance> searchAmbulances(String searchText) {
        List<Ambulance> ambulances = new ArrayList<>();
        String sql = "SELECT * FROM Ambulances WHERE " +
                "vehicle_number LIKE ? OR " +
                "ambulance_type LIKE ? OR " +
                "driver_name LIKE ? OR " +
                "status LIKE ? " +
                "ORDER BY ambulance_id";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Ambulance ambulance = extractAmbulanceFromResultSet(rs);
                ambulances.add(ambulance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ambulances;
    }

    // Update ambulance status
    public boolean updateAmbulanceStatus(int ambulanceId, String status) {
        String sql = "UPDATE Ambulances SET status = ? WHERE ambulance_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, ambulanceId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get ambulance count by status
    public int getAmbulanceCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Ambulances WHERE status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get all bookings
    public List<AmbulanceBooking> getAllBookings() {
        List<AmbulanceBooking> bookings = new ArrayList<>();
        String sql = "SELECT ab.*, a.vehicle_number, a.ambulance_type " +
                "FROM Ambulance_Bookings ab " +
                "JOIN Ambulances a ON ab.ambulance_id = a.ambulance_id " +
                "ORDER BY ab.booking_date DESC, ab.booking_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AmbulanceBooking booking = extractBookingFromResultSet(rs);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // Get today's bookings
    public List<AmbulanceBooking> getTodayBookings() {
        List<AmbulanceBooking> bookings = new ArrayList<>();
        String sql = "SELECT ab.*, a.vehicle_number, a.ambulance_type " +
                "FROM Ambulance_Bookings ab " +
                "JOIN Ambulances a ON ab.ambulance_id = a.ambulance_id " +
                "WHERE ab.booking_date = CURDATE() " +
                "ORDER BY ab.booking_time";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AmbulanceBooking booking = extractBookingFromResultSet(rs);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // Create new booking
    public boolean createBooking(AmbulanceBooking booking) {
        String sql = "INSERT INTO Ambulance_Bookings " +
                "(ambulance_id, patient_id, patient_name, patient_phone, " +
                "pickup_location, destination, booking_date, booking_time, " +
                "distance_km, charges, status, emergency_level, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, booking.getAmbulanceId());
            if (booking.getPatientId() != null) {
                pstmt.setInt(2, booking.getPatientId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, booking.getPatientName());
            pstmt.setString(4, booking.getPatientPhone());
            pstmt.setString(5, booking.getPickupLocation());
            pstmt.setString(6, booking.getDestination());
            pstmt.setDate(7, booking.getBookingDate());
            pstmt.setTime(8, booking.getBookingTime());
            pstmt.setBigDecimal(9, booking.getDistanceKm());
            pstmt.setBigDecimal(10, booking.getCharges());
            pstmt.setString(11, booking.getStatus());
            pstmt.setString(12, booking.getEmergencyLevel());
            pstmt.setString(13, booking.getNotes());

            int rowsAffected = pstmt.executeUpdate();

            // Update ambulance status if booking is confirmed or dispatched
            if (rowsAffected > 0 && ("Confirmed".equals(booking.getStatus()) ||
                    "Dispatched".equals(booking.getStatus()))) {
                updateAmbulanceStatus(booking.getAmbulanceId(), "On Call");
            }

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update booking status
    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE Ambulance_Bookings SET status = ? WHERE booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, bookingId);

            int rowsAffected = pstmt.executeUpdate();

            // If booking is completed or cancelled, make ambulance available again
            if (rowsAffected > 0 && ("Completed".equals(status) || "Cancelled".equals(status))) {
                // Get ambulance_id from booking
                String getAmbulanceIdSql = "SELECT ambulance_id FROM Ambulance_Bookings WHERE booking_id = ?";
                try (PreparedStatement getPstmt = conn.prepareStatement(getAmbulanceIdSql)) {
                    getPstmt.setInt(1, bookingId);
                    ResultSet rs = getPstmt.executeQuery();
                    if (rs.next()) {
                        int ambulanceId = rs.getInt("ambulance_id");
                        updateAmbulanceStatus(ambulanceId, "Available");
                    }
                }
            }

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Search bookings
    public List<AmbulanceBooking> searchBookings(String searchText) {
        List<AmbulanceBooking> bookings = new ArrayList<>();
        String sql = "SELECT ab.*, a.vehicle_number, a.ambulance_type " +
                "FROM Ambulance_Bookings ab " +
                "JOIN Ambulances a ON ab.ambulance_id = a.ambulance_id " +
                "WHERE ab.patient_name LIKE ? OR " +
                "ab.patient_phone LIKE ? OR " +
                "a.vehicle_number LIKE ? OR " +
                "ab.status LIKE ? " +
                "ORDER BY ab.booking_date DESC, ab.booking_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                AmbulanceBooking booking = extractBookingFromResultSet(rs);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // Helper method to extract Ambulance from ResultSet
    private Ambulance extractAmbulanceFromResultSet(ResultSet rs) throws SQLException {
        Ambulance ambulance = new Ambulance();
        ambulance.setAmbulanceId(rs.getInt("ambulance_id"));
        ambulance.setVehicleNumber(rs.getString("vehicle_number"));
        ambulance.setAmbulanceType(rs.getString("ambulance_type"));
        ambulance.setModel(rs.getString("model"));
        ambulance.setYear(rs.getInt("year"));
        ambulance.setDriverName(rs.getString("driver_name"));
        ambulance.setDriverPhone(rs.getString("driver_phone"));
        ambulance.setDriverLicense(rs.getString("driver_license"));
        ambulance.setStatus(rs.getString("status"));
        ambulance.setLastMaintenanceDate(rs.getDate("last_maintenance_date"));
        ambulance.setNextMaintenanceDate(rs.getDate("next_maintenance_date"));
        return ambulance;
    }

    // Helper method to extract AmbulanceBooking from ResultSet
    private AmbulanceBooking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        AmbulanceBooking booking = new AmbulanceBooking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setAmbulanceId(rs.getInt("ambulance_id"));

        int patientId = rs.getInt("patient_id");
        if (!rs.wasNull()) {
            booking.setPatientId(patientId);
        }

        booking.setPatientName(rs.getString("patient_name"));
        booking.setPatientPhone(rs.getString("patient_phone"));
        booking.setPickupLocation(rs.getString("pickup_location"));
        booking.setDestination(rs.getString("destination"));
        booking.setBookingDate(rs.getDate("booking_date"));
        booking.setBookingTime(rs.getTime("booking_time"));
        booking.setActualDispatchTime(rs.getTimestamp("actual_dispatch_time"));
        booking.setActualArrivalTime(rs.getTimestamp("actual_arrival_time"));
        booking.setDistanceKm(rs.getBigDecimal("distance_km"));
        booking.setCharges(rs.getBigDecimal("charges"));
        booking.setStatus(rs.getString("status"));
        booking.setEmergencyLevel(rs.getString("emergency_level"));
        booking.setNotes(rs.getString("notes"));
        booking.setVehicleNumber(rs.getString("vehicle_number"));
        booking.setAmbulanceType(rs.getString("ambulance_type"));
        return booking;
    }
}