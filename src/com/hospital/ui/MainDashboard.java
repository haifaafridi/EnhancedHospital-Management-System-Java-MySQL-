


package com.hospital.ui;

import javax.swing.*;

import com.hospital.dao.AuditLogDAO;
import com.hospital.ui.appointment.*;
import com.hospital.ui.billing.*;
import com.hospital.ui.patient.*;
import com.hospital.ui.report.*;
import com.hospital.util.SessionManager;

import java.awt.*;

public class MainDashboard extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private AuditLogDAO auditDAO;

    public MainDashboard() {
        auditDAO = new AuditLogDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Hospital Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 700));
        setLayout(new BorderLayout());

        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(52, 152, 219)); 
        topPanel.setPreferredSize(new Dimension(0, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        
        ImageIcon hospitalIcon = new ImageIcon(getClass().getResource("/com/hospital/main/hospital.png"));
        Image scaledHospitalIcon = hospitalIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        hospitalIcon = new ImageIcon(scaledHospitalIcon);

        JLabel titleLabel = new JLabel(" Hospital Management System", hospitalIcon, JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        userPanel.setOpaque(false);

        String userName = SessionManager.getCurrentUser().getUsername();
        String userRole = SessionManager.getCurrentUser().getRole();

        JLabel userLabel = new JLabel("Welcome, " + userName + " (" + userRole + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.addActionListener(e -> handleLogout());

        userPanel.add(userLabel);
        userPanel.add(logoutButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(userPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80)); 
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JLabel menuLabel = new JLabel("   MAIN MENU");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        menuLabel.setForeground(new Color(189, 195, 199));
        menuLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 0));
        sidebar.add(menuLabel);

        
        ImageIcon dashboardIcon = scaleIcon("/com/hospital/main/dashboard.png", 30, 30);
        ImageIcon patientIcon = scaleIcon("/com/hospital/main/patient.png", 30, 30);
        ImageIcon appointmentIcon = scaleIcon("/com/hospital/main/appointment.png", 30, 30);
        ImageIcon billingIcon = scaleIcon("/com/hospital/main/bill.png", 30, 30);
        ImageIcon reportIcon = scaleIcon("/com/hospital/main/report.png", 30, 30);

        
        addSidebarButton(sidebar, dashboardIcon, "Dashboard", "home");
        addSidebarButton(sidebar, patientIcon, "Patients", "patients");
        addSidebarButton(sidebar, appointmentIcon, "Appointments", "appointments");
        addSidebarButton(sidebar, billingIcon, "Billing", "billing");
        addSidebarButton(sidebar, reportIcon, "Reports", "reports");

        sidebar.add(Box.createVerticalGlue());
        add(sidebar, BorderLayout.WEST);

        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 250)); 

        contentPanel.add(new DashboardHomePanel(), "home");
        contentPanel.add(new PatientManagementPanel(), "patients");
        contentPanel.add(new AppointmentPanel(), "appointments");
        contentPanel.add(new BillingPanel(), "billing");
        contentPanel.add(new ReportPanel(), "reports");

        add(contentPanel, BorderLayout.CENTER);

        
        createMenuBar();

        
        cardLayout.show(contentPanel, "home");
    }

    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> handleExit());
        fileMenu.add(exitItem);

        JMenu patientsMenu = new JMenu("Patients");
        JMenuItem newPatientItem = new JMenuItem("Register New Patient");
        JMenuItem viewPatientsItem = new JMenuItem("View All Patients");
        newPatientItem.addActionListener(e -> cardLayout.show(contentPanel, "patients"));
        viewPatientsItem.addActionListener(e -> cardLayout.show(contentPanel, "patients"));
        patientsMenu.add(newPatientItem);
        patientsMenu.add(viewPatientsItem);

        JMenu appointmentsMenu = new JMenu("Appointments");
        JMenuItem newAppointmentItem = new JMenuItem("New Appointment");
        JMenuItem viewAppointmentsItem = new JMenuItem("View Appointments");
        newAppointmentItem.addActionListener(e -> cardLayout.show(contentPanel, "appointments"));
        viewAppointmentsItem.addActionListener(e -> cardLayout.show(contentPanel, "appointments"));
        appointmentsMenu.add(newAppointmentItem);
        appointmentsMenu.add(viewAppointmentsItem);

        JMenu billingMenu = new JMenu("Billing");
        JMenuItem generateBillItem = new JMenuItem("Generate Bill");
        JMenuItem viewBillsItem = new JMenuItem("View Bills");
        generateBillItem.addActionListener(e -> cardLayout.show(contentPanel, "billing"));
        viewBillsItem.addActionListener(e -> cardLayout.show(contentPanel, "billing"));
        billingMenu.add(generateBillItem);
        billingMenu.add(viewBillsItem);

        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        JMenuItem revenueReportItem = new JMenuItem("Revenue Report");
        dashboardItem.addActionListener(e -> cardLayout.show(contentPanel, "home"));
        revenueReportItem.addActionListener(e -> cardLayout.show(contentPanel, "reports"));
        reportsMenu.add(dashboardItem);
        reportsMenu.add(revenueReportItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(patientsMenu);
        menuBar.add(appointmentsMenu);
        menuBar.add(billingMenu);
        menuBar.add(reportsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    
    private void addSidebarButton(JPanel sidebar, ImageIcon icon, String text, String panelName) {
        JButton button = new JButton(text, icon);
        button.setMaximumSize(new Dimension(220, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(44, 62, 80));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        button.setIconTextGap(10);

        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 73, 94));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 62, 80));
            }
        });

        button.addActionListener(e -> cardLayout.show(contentPanel, panelName));

        sidebar.add(button);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    
    private ImageIcon scaleIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            auditDAO.logLogout(SessionManager.getCurrentUser().getUserId());
            SessionManager.logout();

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        }
    }

    private void handleExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (SessionManager.isLoggedIn()) {
                auditDAO.logLogout(SessionManager.getCurrentUser().getUserId());
            }
            System.exit(0);
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Hospital Management System v1.0\n" +
                        "Developed using Java Swing and MySQL\n" +
                        "© 2025 All Rights Reserved",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

