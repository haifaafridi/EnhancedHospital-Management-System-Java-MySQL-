


package com.hospital.ui;

import javax.swing.*;

import com.hospital.dao.AuditLogDAO;
import com.hospital.dao.UserDAO;
import com.hospital.model.User;
import com.hospital.util.SessionManager;

import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserDAO userDAO;
    private AuditLogDAO auditDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        auditDAO = new AuditLogDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Hospital Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setLocationRelativeTo(null);
        setResizable(true); 

        
        ImageIcon backgroundIcon = new ImageIcon(
                "C:\\Users\\Haifa Afridi\\HospitalManagementSystem\\src\\com\\hospital\\Hospital-Management-System.jpg");

        
        Image scaledImage = backgroundIcon.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH);
        backgroundIcon = new ImageIcon(scaledImage);

        
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new GridBagLayout()); 
        setContentPane(backgroundLabel); 

        
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new GridBagLayout());

        
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(255, 255, 255, 230)); 
        loginPanel.setPreferredSize(new Dimension(350, 280));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        
        JLabel titleLabel = new JLabel("Hospital Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginPanel.add(titleLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        loginPanel.add(subtitleLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        
        
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 13);

        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(usernameField, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(passwordField, gbc);

        
        loginPanel.add(formPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        
        loginButton = new JButton("LOGIN");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performLogin());

        loginPanel.add(loginButton);

        
        mainPanel.add(loginPanel);

        
        backgroundLabel.add(mainPanel);
        getRootPane().setDefaultButton(loginButton);

    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loginButton.setEnabled(false);

        
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return userDAO.authenticate(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();

                    if (user != null) {
                        
                        SessionManager.setCurrentUser(user);
                        SessionManager.setIpAddress("127.0.0.1"); 

                        
                        userDAO.updateLastLogin(user.getUserId());

                        
                        auditDAO.logLogin(user.getUserId(), true);

                        
                        SwingUtilities.invokeLater(() -> {
                            MainDashboard dashboard = new MainDashboard();
                            dashboard.setVisible(true);
                            dispose();
                        });
                    } else {
                        
                        auditDAO.logAction("LOGIN", "Users", null, null, null,
                                "Failed login attempt for username: " + username);

                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Invalid username or password",
                                "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "An error occurred during login",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    loginButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }
}

