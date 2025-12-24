
package com.hospital.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.hospital.model.*;
import com.hospital.util.DatabaseConnection;

public class RoomDAO {

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms WHERE availability_status = 'Available' " +
                "ORDER BY room_type, room_number";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting available rooms: " + e.getMessage());
            e.printStackTrace();
        }
        return rooms;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms ORDER BY room_number";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all rooms: " + e.getMessage());
            e.printStackTrace();
        }
        return rooms;
    }

    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM Rooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRoomFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting room: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Room> getRoomsByType(String roomType) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms WHERE room_type = ? ORDER BY room_number";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roomType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting rooms by type: " + e.getMessage());
            e.printStackTrace();
        }
        return rooms;
    }

    public boolean addRoom(Room room) {
        String sql = "INSERT INTO Rooms (room_number, room_type, floor, capacity, " +
                "price_per_day, availability_status, amenities) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());

            if (room.getFloor() != null) {
                pstmt.setInt(3, room.getFloor());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            if (room.getCapacity() != null) {
                pstmt.setInt(4, room.getCapacity());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setBigDecimal(5, room.getPricePerDay());
            pstmt.setString(6, room.getAvailabilityStatus());
            pstmt.setString(7, room.getAmenities());

            int affected = pstmt.executeUpdate();

            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    room.setRoomId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRoom(Room room) {
        String sql = "UPDATE Rooms SET room_number = ?, room_type = ?, floor = ?, " +
                "capacity = ?, price_per_day = ?, availability_status = ?, " +
                "amenities = ? WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());

            if (room.getFloor() != null) {
                pstmt.setInt(3, room.getFloor());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            if (room.getCapacity() != null) {
                pstmt.setInt(4, room.getCapacity());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setBigDecimal(5, room.getPricePerDay());
            pstmt.setString(6, room.getAvailabilityStatus());
            pstmt.setString(7, room.getAmenities());
            pstmt.setInt(8, room.getRoomId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating room: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRoomStatus(int roomId, String status) {
        String sql = "UPDATE Rooms SET availability_status = ? WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, roomId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating room status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public int getRoomCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Rooms WHERE availability_status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting room count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM Rooms WHERE room_id = ? AND availability_status = 'Available'";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setFloor((Integer) rs.getObject("floor"));
        room.setCapacity((Integer) rs.getObject("capacity"));
        room.setPricePerDay(rs.getBigDecimal("price_per_day"));
        room.setAvailabilityStatus(rs.getString("availability_status"));
        room.setAmenities(rs.getString("amenities"));
        room.setLastCleaned(rs.getTimestamp("last_cleaned"));
        room.setCreatedAt(rs.getTimestamp("created_at"));
        room.setUpdatedAt(rs.getTimestamp("updated_at"));
        return room;
    }
}
