package com.matrimony.util;

import com.matrimony.database.DatabaseConnection;

import java.sql.*;

public class TestBiodataTable {
    public static void main(String[] args) {
        System.out.println(" Testing Biodata Table \n");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Database connection successful");
                System.out.println("Database: " + conn.getCatalog());
            } else {
                System.err.println("Database connection failed");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("\n Checking if biodata table exists");
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "biodata", null);

            if (tables.next()) {
                System.out.println("biodata table exists");
            } else {
                System.err.println("biodata table does NOT exist!");
                System.out.println("\nAttempting to create biodata table...");
                DatabaseConnection.initializeDatabase();
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error checking table: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("\n Biodata Table Structure ");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet columns = stmt.executeQuery("DESCRIBE biodata");

            int columnCount = 0;
            while (columns.next()) {
                columnCount++;
                String columnName = columns.getString("Field");
                String columnType = columns.getString("Type");
                String nullable = columns.getString("Null");
                String key = columns.getString("Key");

                System.out.printf("  %2d. %-30s %-20s %s %s\n",
                    columnCount, columnName, columnType,
                    "YES".equals(nullable) ? "NULL" : "NOT NULL",
                    key.isEmpty() ? "" : "[" + key + "]");
            }

            System.out.println("\n Total columns: " + columnCount);

            if (columnCount != 48) {
                System.err.println("WARNING: Expected 48 columns, found " + columnCount);
                System.err.println("The table structure might not match the code!");
            }

        } catch (SQLException e) {
            System.err.println("Error checking table structure: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("\n--- Checking Users Table ---");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            if (rs.next()) {
                int userCount = rs.getInt("count");
                System.out.println("Users table has " + userCount + "records");

                if (userCount == 0) {
                    System.err.println("WARNING: No users in database!");
                    System.err.println("You need to create a user account first");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking users: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("\n--- Testing Biodata Insert ---");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT id FROM users LIMIT 1")) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                System.out.println("Found user ID: " + userId);

                String sql = "INSERT INTO biodata (user_id, age, city) VALUES (?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE age = VALUES(age), city = VALUES(city)";

                try (PreparedStatement insertStmt = conn.prepareStatement(sql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, 25);
                    insertStmt.setString(3, "Test City");

                    int rows = insertStmt.executeUpdate();
                    System.out.println("Test insert successful, rows affected: " + rows);

                    try (Statement cleanupStmt = conn.createStatement()) {
                        cleanupStmt.executeUpdate("DELETE FROM biodata WHERE city = 'Test City'");
                        System.out.println("Test data cleaned up");
                    }
                }
            } else {
                System.err.println("No users found to test with");
            }

        } catch (SQLException e) {
            System.err.println("Error during test insert:");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n Test Complete ");
    }
}

