


package com.hospital.util;

import com.hospital.model.User;

public class SessionManager {
    private static User currentUser;
    private static String ipAddress;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }

    public static void setIpAddress(String ip) {
        ipAddress = ip;
    }

    public static String getIpAddress() {
        return ipAddress;
    }
}

