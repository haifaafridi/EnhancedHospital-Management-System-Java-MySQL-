
package com.hospital.ui.report;

import com.hospital.util.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReportPanel extends JPanel {

        public ReportPanel() {
                initComponents();
        }

        private void initComponents() {
                setLayout(new BorderLayout());
                setBackground(new Color(245, 247, 250));
                setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setBackground(new Color(245, 247, 250));
                headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

                JPanel titlePanel = new JPanel(new BorderLayout());
                titlePanel.setBackground(new Color(245, 247, 250));

                JLabel titleLabel = new JLabel("Reports & Analytics");
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
                titleLabel.setForeground(new Color(44, 62, 80));

                JLabel subtitleLabel = new JLabel("View comprehensive reports and statistics");
                subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                subtitleLabel.setForeground(new Color(127, 140, 141));

                titlePanel.add(titleLabel, BorderLayout.NORTH);
                titlePanel.add(subtitleLabel, BorderLayout.CENTER);

                headerPanel.add(titlePanel, BorderLayout.WEST);
                add(headerPanel, BorderLayout.NORTH);

                JPanel mainContentPanel = new JPanel(new BorderLayout());
                mainContentPanel.setBackground(Color.WHITE);
                mainContentPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230), 1));

                JPanel reportPanel = new JPanel(new GridLayout(3, 2, 20, 20));
                reportPanel.setBackground(Color.WHITE);
                reportPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

                reportPanel.add(createReportCard("Patient Statistics",
                                "View patient demographics and status",
                                new Color(52, 152, 219),
                                () -> showPatientStats()));

                reportPanel.add(createReportCard("Revenue Report",
                                "Monthly billing and payment summary",
                                new Color(46, 204, 113),
                                () -> showRevenueReport()));

                reportPanel.add(createReportCard("Appointment Report",
                                "Track appointments and schedules",
                                new Color(155, 89, 182),
                                () -> showAppointmentReport()));

                reportPanel.add(createReportCard("Pending Payments",
                                "Outstanding bills and dues",
                                new Color(230, 126, 34),
                                () -> showPendingPayments()));

                reportPanel.add(createReportCard("Department Statistics",
                                "Staff and department overview",
                                new Color(52, 73, 94),
                                () -> showDepartmentStats()));

                reportPanel.add(createReportCard("Room Availability",
                                "Current room occupancy status",
                                new Color(26, 188, 156),
                                () -> showRoomAvailability()));

                mainContentPanel.add(reportPanel, BorderLayout.CENTER);
                add(mainContentPanel, BorderLayout.CENTER);
        }

        private JPanel createReportCard(String title, String description, Color accentColor, Runnable action) {
                JPanel card = new JPanel();
                card.setLayout(new BorderLayout(10, 10));
                card.setBackground(new Color(248, 249, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
                                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));

                JPanel colorBar = new JPanel();
                colorBar.setBackground(accentColor);
                colorBar.setPreferredSize(new Dimension(4, 0));

                JPanel contentPanel = new JPanel();
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                contentPanel.setBackground(new Color(248, 249, 250));

                JLabel titleLabel = new JLabel(title);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                titleLabel.setForeground(new Color(44, 62, 80));
                titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel descLabel = new JLabel(description);
                descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                descLabel.setForeground(new Color(127, 140, 141));
                descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                contentPanel.add(titleLabel);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                contentPanel.add(descLabel);

                card.add(colorBar, BorderLayout.WEST);
                card.add(contentPanel, BorderLayout.CENTER);

                card.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                                card.setBackground(Color.WHITE);
                                contentPanel.setBackground(Color.WHITE);
                                card.setBorder(BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(accentColor, 1),
                                                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
                        }

                        public void mouseExited(java.awt.event.MouseEvent evt) {
                                card.setBackground(new Color(248, 249, 250));
                                contentPanel.setBackground(new Color(248, 249, 250));
                                card.setBorder(BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(new Color(220, 223, 230), 1),
                                                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
                        }

                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                action.run();
                        }
                });

                return card;
        }

        private void showPatientStats() {
                try (Connection conn = DatabaseConnection.getConnection()) {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(
                                        "SELECT COUNT(*) as total, " +
                                                        "SUM(CASE WHEN status = 'Active' THEN 1 ELSE 0 END) as active, "
                                                        +
                                                        "SUM(CASE WHEN gender = 'Male' THEN 1 ELSE 0 END) as male, " +
                                                        "SUM(CASE WHEN gender = 'Female' THEN 1 ELSE 0 END) as female "
                                                        +
                                                        "FROM Patients");

                        if (rs.next()) {
                                String message = String.format(
                                                "Total Patients: %d\nActive Patients: %d\nMale: %d\nFemale: %d",
                                                rs.getInt("total"), rs.getInt("active"),
                                                rs.getInt("male"), rs.getInt("female"));

                                JOptionPane.showMessageDialog(this, message,
                                                "Patient Statistics", JOptionPane.INFORMATION_MESSAGE);
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error loading statistics",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                }
        }

        private void showRevenueReport() {
                try (Connection conn = DatabaseConnection.getConnection()) {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(
                                        "SELECT SUM(final_amount) as total_revenue, " +
                                                        "SUM(paid_amount) as total_paid, " +
                                                        "SUM(pending_amount) as total_pending " +
                                                        "FROM Bills WHERE MONTH(bill_date) = MONTH(CURDATE())");

                        if (rs.next()) {
                                String message = String.format(
                                                "This Month's Revenue:\n\n" +
                                                                "Total Billed: Rs. %.2f\n" +
                                                                "Total Collected: Rs. %.2f\n" +
                                                                "Pending: Rs. %.2f",
                                                rs.getDouble("total_revenue"),
                                                rs.getDouble("total_paid"),
                                                rs.getDouble("total_pending"));

                                JOptionPane.showMessageDialog(this, message,
                                                "Revenue Report", JOptionPane.INFORMATION_MESSAGE);
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error loading revenue report",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                }
        }

        private void showAppointmentReport() {
                try (Connection conn = DatabaseConnection.getConnection()) {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(
                                        "SELECT " +
                                                        "SUM(CASE WHEN appointment_date = CURDATE() THEN 1 ELSE 0 END) as today, "
                                                        +
                                                        "SUM(CASE WHEN status = 'Scheduled' THEN 1 ELSE 0 END) as scheduled, "
                                                        +
                                                        "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) as completed, "
                                                        +
                                                        "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) as cancelled "
                                                        +
                                                        "FROM Appointments WHERE appointment_date >= CURDATE() - INTERVAL 30 DAY");

                        if (rs.next()) {
                                String message = String.format(
                                                "Appointment Statistics (Last 30 Days):\n\n" +
                                                                "Today's Appointments: %d\n" +
                                                                "Scheduled: %d\n" +
                                                                "Completed: %d\n" +
                                                                "Cancelled: %d",
                                                rs.getInt("today"), rs.getInt("scheduled"),
                                                rs.getInt("completed"), rs.getInt("cancelled"));

                                JOptionPane.showMessageDialog(this, message,
                                                "Appointment Report", JOptionPane.INFORMATION_MESSAGE);
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error loading appointment report",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                }
        }

        private void showPendingPayments() {
                ReportDialog dialog = new ReportDialog(
                                (Frame) SwingUtilities.getWindowAncestor(this), "Pending Payments");

                String[] columns = { "Bill ID", "Patient", "Amount", "Pending", "Days" };
                String query = "SELECT b.bill_id, p.full_name, b.final_amount, " +
                                "b.pending_amount, DATEDIFF(CURDATE(), b.bill_date) as days " +
                                "FROM Bills b JOIN Patients p ON b.patient_id = p.patient_id " +
                                "WHERE b.payment_status IN ('Pending', 'Partially Paid') " +
                                "ORDER BY days DESC LIMIT 50";

                dialog.loadReport(columns, query);
                dialog.setVisible(true);
        }

        private void showDepartmentStats() {
                ReportDialog dialog = new ReportDialog(
                                (Frame) SwingUtilities.getWindowAncestor(this), "Department Statistics");

                String[] columns = { "Department", "Total Staff", "Phone" };
                String query = "SELECT d.department_name, COUNT(DISTINCT s.staff_id) as staff_count, d.phone " +
                                "FROM Departments d " +
                                "LEFT JOIN Staff s ON d.department_id = s.department_id AND s.status = 'Active' " +
                                "GROUP BY d.department_id";

                dialog.loadReport(columns, query);
                dialog.setVisible(true);
        }

        private void showRoomAvailability() {
                try (Connection conn = DatabaseConnection.getConnection()) {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(
                                        "SELECT room_type, " +
                                                        "SUM(CASE WHEN availability_status = 'Available' THEN 1 ELSE 0 END) as available, "
                                                        +
                                                        "SUM(CASE WHEN availability_status = 'Occupied' THEN 1 ELSE 0 END) as occupied, "
                                                        +
                                                        "COUNT(*) as total " +
                                                        "FROM Rooms GROUP BY room_type");

                        StringBuilder message = new StringBuilder("Room Availability:\n\n");
                        while (rs.next()) {
                                message.append(String.format("%s:\n  Available: %d | Occupied: %d | Total: %d\n\n",
                                                rs.getString("room_type"),
                                                rs.getInt("available"),
                                                rs.getInt("occupied"),
                                                rs.getInt("total")));
                        }

                        JOptionPane.showMessageDialog(this, message.toString(),
                                        "Room Availability", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error loading room availability",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                }
        }
}
