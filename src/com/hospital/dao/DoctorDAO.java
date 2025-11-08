


package com.hospital.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.hospital.model.Doctor;
import com.hospital.util.DatabaseConnection;

public class DoctorDAO {

    
    public List<Doctor> getAvailableDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctors WHERE status = 'Available' ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting available doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctors ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    
    public Doctor getDoctorById(int doctorId) {
        String sql = "SELECT * FROM Doctors WHERE doctor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractDoctorFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting doctor by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctors WHERE specialization = ? AND status = 'Available' " +
                "ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, specialization);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting doctors by specialization: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    
    public List<String> getAllSpecializations() {
        List<String> specializations = new ArrayList<>();
        String sql = "SELECT DISTINCT specialization FROM Doctors ORDER BY specialization";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                specializations.add(rs.getString("specialization"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting specializations: " + e.getMessage());
            e.printStackTrace();
        }
        return specializations;
    }

    
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO Doctors (user_id, staff_id, full_name, specialization, " +
                "qualification, license_number, experience_years, phone, email, " +
                "consultation_fee, available_days, available_from, available_to, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (doctor.getUserId() != null) {
                pstmt.setInt(1, doctor.getUserId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (doctor.getStaffId() != null) {
                pstmt.setInt(2, doctor.getStaffId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, doctor.getFullName());
            pstmt.setString(4, doctor.getSpecialization());
            pstmt.setString(5, doctor.getQualification());
            pstmt.setString(6, doctor.getLicenseNumber());

            if (doctor.getExperienceYears() != null) {
                pstmt.setInt(7, doctor.getExperienceYears());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            pstmt.setString(8, doctor.getPhone());
            pstmt.setString(9, doctor.getEmail());
            pstmt.setBigDecimal(10, doctor.getConsultationFee());
            pstmt.setString(11, doctor.getAvailableDays());
            pstmt.setTime(12, doctor.getAvailableFrom());
            pstmt.setTime(13, doctor.getAvailableTo());
            pstmt.setString(14, doctor.getStatus());

            int affected = pstmt.executeUpdate();

            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    doctor.setDoctorId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error adding doctor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE Doctors SET full_name = ?, specialization = ?, qualification = ?, " +
                "phone = ?, email = ?, consultation_fee = ?, available_days = ?, " +
                "available_from = ?, available_to = ?, status = ?, experience_years = ? " +
                "WHERE doctor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doctor.getFullName());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getQualification());
            pstmt.setString(4, doctor.getPhone());
            pstmt.setString(5, doctor.getEmail());
            pstmt.setBigDecimal(6, doctor.getConsultationFee());
            pstmt.setString(7, doctor.getAvailableDays());
            pstmt.setTime(8, doctor.getAvailableFrom());
            pstmt.setTime(9, doctor.getAvailableTo());
            pstmt.setString(10, doctor.getStatus());

            if (doctor.getExperienceYears() != null) {
                pstmt.setInt(11, doctor.getExperienceYears());
            } else {
                pstmt.setNull(11, Types.INTEGER);
            }

            pstmt.setInt(12, doctor.getDoctorId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean updateDoctorStatus(int doctorId, String status) {
        String sql = "UPDATE Doctors SET status = ? WHERE doctor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, doctorId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating doctor status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public List<Doctor> searchDoctorsByName(String searchTerm) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctors WHERE full_name LIKE ? ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    
    public List<String> getDoctorSchedule(int doctorId, Date date) {
        List<String> schedule = new ArrayList<>();
        String sql = "SELECT appointment_time, status FROM Appointments " +
                "WHERE doctor_id = ? AND appointment_date = ? " +
                "ORDER BY appointment_time";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String slot = rs.getTime("appointment_time") + " - " + rs.getString("status");
                schedule.add(slot);
            }

        } catch (SQLException e) {
            System.err.println("Error getting doctor schedule: " + e.getMessage());
            e.printStackTrace();
        }
        return schedule;
    }

    
    public boolean isDoctorAvailable(int doctorId, Date date, Time time) {
        String sql = "SELECT COUNT(*) FROM Appointments WHERE doctor_id = ? " +
                "AND appointment_date = ? AND appointment_time = ? " +
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
            System.err.println("Error checking doctor availability: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public DoctorStatistics getDoctorStatistics(int doctorId) {
        DoctorStatistics stats = new DoctorStatistics();

        
        String sql1 = "SELECT COUNT(*) FROM Appointments WHERE doctor_id = ?";

        
        String sql2 = "SELECT COUNT(*) FROM Appointments WHERE doctor_id = ? AND status = 'Completed'";

        
        String sql3 = "SELECT COUNT(*) FROM Appointments WHERE doctor_id = ? " +
                "AND appointment_date = CURDATE() AND status IN ('Scheduled', 'Confirmed')";

        try (Connection conn = DatabaseConnection.getConnection()) {
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                pstmt.setInt(1, doctorId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.totalAppointments = rs.getInt(1);
                }
            }

            
            try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                pstmt.setInt(1, doctorId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.completedAppointments = rs.getInt(1);
                }
            }

            
            try (PreparedStatement pstmt = conn.prepareStatement(sql3)) {
                pstmt.setInt(1, doctorId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.todayAppointments = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting doctor statistics: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    
    public int getDoctorCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Doctors WHERE status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting doctor count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    
    public int getTotalDoctorCount() {
        String sql = "SELECT COUNT(*) FROM Doctors";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting total doctor count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    
    public boolean deleteDoctor(int doctorId) {
        
        String checkSql = "SELECT COUNT(*) FROM Appointments WHERE doctor_id = ? " +
                "AND status IN ('Scheduled', 'Confirmed')";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, doctorId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.err.println("Cannot delete doctor with scheduled appointments");
                return false;
            }

            
            return updateDoctorStatus(doctorId, "Unavailable");

        } catch (SQLException e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    
    public List<Doctor> getDoctorsAvailableOnDay(String day) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctors WHERE status = 'Available' " +
                "AND (available_days LIKE ? OR available_days = 'All') " +
                "ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + day + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting doctors by day: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    
    private Doctor extractDoctorFromResultSet(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(rs.getInt("doctor_id"));
        doctor.setUserId((Integer) rs.getObject("user_id"));
        doctor.setStaffId((Integer) rs.getObject("staff_id"));
        doctor.setFullName(rs.getString("full_name"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setQualification(rs.getString("qualification"));
        doctor.setLicenseNumber(rs.getString("license_number"));
        doctor.setExperienceYears((Integer) rs.getObject("experience_years"));
        doctor.setPhone(rs.getString("phone"));
        doctor.setEmail(rs.getString("email"));
        doctor.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        doctor.setAvailableDays(rs.getString("available_days"));
        doctor.setAvailableFrom(rs.getTime("available_from"));
        doctor.setAvailableTo(rs.getTime("available_to"));
        doctor.setStatus(rs.getString("status"));
        doctor.setCreatedAt(rs.getTimestamp("created_at"));
        doctor.setUpdatedAt(rs.getTimestamp("updated_at"));
        return doctor;
    }

    
    public static class DoctorStatistics {
        public int totalAppointments;
        public int completedAppointments;
        public int todayAppointments;

        @Override
        public String toString() {
            return String.format("Total: %d, Completed: %d, Today: %d",
                    totalAppointments, completedAppointments, todayAppointments);
        }
    }
}

