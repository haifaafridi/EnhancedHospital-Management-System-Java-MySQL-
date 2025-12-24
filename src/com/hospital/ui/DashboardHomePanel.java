package com.hospital.ui;

import javax.swing.*;
import com.hospital.util.DatabaseConnection;
import java.awt.*;
import java.sql.*;

public class DashboardHomePanel extends JPanel {

    public DashboardHomePanel() {
        initComponents();
        loadStatistics();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subtitleLabel = new JLabel("Real-time hospital statistics and metrics");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setBackground(new Color(245, 247, 250));
        titleContainer.add(titleLabel);
        titleContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 25, 25));
        statsPanel.setBackground(new Color(245, 247, 250));
        add(statsPanel, BorderLayout.CENTER);
    }

    private void loadStatistics() {
        JPanel statsPanel = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        statsPanel.removeAll();

        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql1 = "SELECT COUNT(*) FROM Patients WHERE status = 'Active'";
            addStatCard(statsPanel, "Total Patients", "Active patient records",
                    executeCountQuery(conn, sql1), new Color(52, 152, 219));

            String sql2 = "SELECT COUNT(*) FROM Appointments WHERE appointment_date = CURDATE() " +
                    "AND status IN ('Scheduled', 'Confirmed')";
            addStatCard(statsPanel, "Today's Appointments", "Scheduled for today",
                    executeCountQuery(conn, sql2), new Color(46, 204, 113));

            String sql3 = "SELECT COUNT(*) FROM Admissions WHERE status = 'Admitted'";
            addStatCard(statsPanel, "Current Admissions", "Patients currently admitted",
                    executeCountQuery(conn, sql3), new Color(155, 89, 182));

            String sql4 = "SELECT COUNT(*) FROM Doctors WHERE status = 'Available'";
            addStatCard(statsPanel, "Available Doctors", "On duty and available",
                    executeCountQuery(conn, sql4), new Color(52, 73, 94));

            String sql5 = "SELECT COUNT(*) FROM Bills WHERE payment_status IN ('Pending', 'Partially Paid')";
            addStatCard(statsPanel, "Pending Bills", "Outstanding payments",
                    executeCountQuery(conn, sql5), new Color(231, 76, 60));

            String sql6 = "SELECT COUNT(*) FROM Rooms WHERE availability_status = 'Available'";
            addStatCard(statsPanel, "Available Rooms", "Ready for occupancy",
                    executeCountQuery(conn, sql6), new Color(26, 188, 156));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private int executeCountQuery(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private void addStatCard(JPanel panel, String title, String description, int count, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
                BorderFactory.createEmptyBorder(25, 20, 25, 20)));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(220, 225, 230)),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
                        BorderFactory.createEmptyBorder(25, 20, 25, 20))));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(4, 0));
        card.add(accentBar, BorderLayout.WEST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(127, 140, 141));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        countLabel.setForeground(accentColor);
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(countLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(248, 250, 252));
                contentPanel.setBackground(new Color(248, 250, 252));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                contentPanel.setBackground(Color.WHITE);
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        panel.add(card);
    }
}
