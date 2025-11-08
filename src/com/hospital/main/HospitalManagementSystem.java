


package com.hospital.main;

import javax.swing.*;

import com.hospital.ui.LoginFrame;
import com.hospital.util.DatabaseConnection;

public class HospitalManagementSystem {

    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        if (DatabaseConnection.testConnection()) {
            System.out.println("Database connected successfully!");

            
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to database. Please check your configuration.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}

