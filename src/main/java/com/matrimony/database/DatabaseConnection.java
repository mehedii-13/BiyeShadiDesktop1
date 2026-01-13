package com.matrimony.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_NAME = "matrimony_db";
    private static final String SERVER_URL =
            "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "batin";

    private static boolean driverLoaded = false;
    private static boolean connectionTested = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
            System.out.println(" MySQL JDBC Driver loaded successfully");
            System.out.println("  Database URL: " + DB_URL);
            System.out.println("  Database User: " + USER);
            System.out.println();
        } catch (ClassNotFoundException e) {
            System.err.println(" ERROR: MySQL JDBC Driver not found");
            System.err.println(" " + e.getMessage());
        }
    }

    private static void createDatabaseIfNotExists() throws SQLException {
        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Create database if it doesn't exist
            stmt.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS " + DB_NAME +
                            " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
            );
        }
    }

    public static Connection getConnection() {
        if (!driverLoaded) {
            System.err.println("JDBC Driver not loaded");
            return null;
        }

        try {
            createDatabaseIfNotExists();
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            if (!connectionTested) {
                System.out.println("✓ Database connected successfully");
                connectionTested = true;
            }
            return conn;

        } catch (SQLException e) {
            System.err.println("\n DATABASE CONNECTION FAILED");
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Message: " + e.getMessage());

            if (e.getErrorCode() == 1045) {
                System.err.println("  Invalid username or password");
            } else if (e.getErrorCode() == 0) {
                System.err.println("  MySQL server not running");
            }
            return null;
        }
    }

    public static void initializeDatabase() {


        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                full_name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                phone VARCHAR(20) NOT NULL,
                gender VARCHAR(10) NOT NULL,
                password VARCHAR(255) NOT NULL,
                role VARCHAR(20) DEFAULT 'user' NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_email (email),
                INDEX idx_role (role)
            ) ENGINE=InnoDB
            DEFAULT CHARSET=utf8mb4
            COLLATE=utf8mb4_unicode_ci;
            """;

        String createBiodataTable = """
            CREATE TABLE IF NOT EXISTS biodata (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                
                -- Personal Information
                date_of_birth DATE,
                age INT,
                height VARCHAR(20),
                weight VARCHAR(20),
                marital_status VARCHAR(30),
                religion VARCHAR(50),
                caste VARCHAR(50),
                mother_tongue VARCHAR(50),
                complexion VARCHAR(30),
                blood_group VARCHAR(10),
                
                -- Professional Information
                education VARCHAR(100),
                occupation VARCHAR(100),
                annual_income VARCHAR(50),
                company_name VARCHAR(100),
                
                -- Family Information
                father_name VARCHAR(100),
                father_occupation VARCHAR(100),
                mother_name VARCHAR(100),
                mother_occupation VARCHAR(100),
                siblings VARCHAR(200),
                family_type VARCHAR(30),
                family_status VARCHAR(30),
                
                -- Location Information
                address TEXT,
                city VARCHAR(100),
                state VARCHAR(100),
                country VARCHAR(100) DEFAULT 'Bangladesh',
                
                -- About
                about_me TEXT,
                hobbies TEXT,
                
                -- Partner Preferences
                partner_age_from INT,
                partner_age_to INT,
                partner_height_from VARCHAR(20),
                partner_height_to VARCHAR(20),
                partner_religion VARCHAR(50),
                partner_education VARCHAR(100),
                partner_occupation VARCHAR(100),
                partner_income VARCHAR(50),
                partner_marital_status VARCHAR(30),
                partner_expectations TEXT,
                
                -- Photo
                photo_path VARCHAR(255),
                
                -- Status
                profile_completed BOOLEAN DEFAULT FALSE,
                is_verified BOOLEAN DEFAULT FALSE,
                
                -- Timestamps
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                UNIQUE KEY unique_user_biodata (user_id),
                INDEX idx_user_id (user_id),
                INDEX idx_age (age),
                INDEX idx_city (city)
            ) ENGINE=InnoDB
            DEFAULT CHARSET=utf8mb4
            COLLATE=utf8mb4_unicode_ci;
            """;

        String createContactRequestsTable = """
            CREATE TABLE IF NOT EXISTS contact_requests (
                id INT AUTO_INCREMENT PRIMARY KEY,
                sender_id INT NOT NULL,
                receiver_id INT NOT NULL,
                status ENUM('pending', 'accepted', 'rejected') DEFAULT 'pending',
                message TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                
                FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
                INDEX idx_sender (sender_id),
                INDEX idx_receiver (receiver_id),
                INDEX idx_status (status)
            ) ENGINE=InnoDB
            DEFAULT CHARSET=utf8mb4
            COLLATE=utf8mb4_unicode_ci;
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            if (conn == null) {
                System.err.println(" ERROR: Failed to get database connection");
                return;
            }

            System.out.println("✓ Database connected successfully!");
            System.out.println("✓ Connection established");

            stmt.execute(createUsersTable);
            System.out.println("✓ Table 'users' created/verified");

            stmt.execute(createBiodataTable);
            System.out.println("✓ Table 'biodata' created/verified");

            stmt.execute(createContactRequestsTable);
            System.out.println("✓ Table 'contact_requests' created/verified");

            System.out.println(" Database initialization completed");

        } catch (SQLException e) {
            System.err.println("\n✗ ERROR during database initialization:");
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Message: " + e.getMessage());
        }
    }

    public static boolean testConnection() {


        try (Connection conn = getConnection()) {
            var meta = conn.getMetaData();

            System.out.println(" Connection test PASSED");
            System.out.println("\nDatabase Information:");
            System.out.println("  Product: " + meta.getDatabaseProductName());
            System.out.println("  Version: " + meta.getDatabaseProductVersion());
            System.out.println("  Driver: " + meta.getDriverName());
            System.out.println("  Driver Version: " + meta.getDriverVersion());
            return true;
        } catch (SQLException e) {
            System.err.println("✗ Connection test FAILED");
            System.err.println(" " + e.getMessage());
            return false;
        }
    }
}
