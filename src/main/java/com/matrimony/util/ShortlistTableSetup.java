package com.matrimony.util;

import com.matrimony.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;

public class ShortlistTableSetup {

    public static void main(String[] args) {
        createShortlistTable();
    }

    public static void createShortlistTable() {
        String createTableSQL =
            "CREATE TABLE IF NOT EXISTS shortlist (" +
            "id INT PRIMARY KEY AUTO_INCREMENT," +
            "user_id INT NOT NULL," +
            "shortlisted_user_id INT NOT NULL," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
            "FOREIGN KEY (shortlisted_user_id) REFERENCES users(id) ON DELETE CASCADE," +
            "UNIQUE KEY unique_shortlist (user_id, shortlisted_user_id)" +
            ")";

        String createIndex1 = "CREATE INDEX IF NOT EXISTS idx_user_id ON shortlist(user_id)";
        String createIndex2 = "CREATE INDEX IF NOT EXISTS idx_shortlisted_user_id ON shortlist(shortlisted_user_id)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            if (conn == null) {
                return;
            }

            stmt.execute(createTableSQL);
            try {
                stmt.execute(createIndex1);
            } catch (Exception e) {
                System.out.println("  (Index idx_user_id may already exist)");
            }

            try {
                stmt.execute(createIndex2);
                System.out.println("Index idx_shortlisted_user_id created");
            } catch (Exception e) {
                System.out.println("  (Index idx_shortlisted_user_id may already exist)");
            }



        } catch (Exception e) {
            System.err.println();
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println();
        }
    }
}

