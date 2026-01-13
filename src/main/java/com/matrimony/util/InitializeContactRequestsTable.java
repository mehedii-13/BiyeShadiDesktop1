package com.matrimony.util;

import com.matrimony.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InitializeContactRequestsTable {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS contact_requests (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, "+
                    "sender_id INT NOT NULL, "+
                    "receiver_id INT NOT NULL, "+
                    "status VARCHAR(20) DEFAULT 'pending' NOT NULL, "+
                    "message TEXT, "+
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "+
                    "FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE, "+
                    "FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE, "+
                    "UNIQUE KEY unique_request (sender_id, receiver_id), "+
                    "INDEX idx_sender (sender_id), "+
                    "INDEX idx_receiver (receiver_id), "+
                    "INDEX idx_status (status)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

            stmt.executeUpdate(createTableSQL);


        } catch (SQLException e) {
            System.err.println("Error initializing contact_requests table:");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

