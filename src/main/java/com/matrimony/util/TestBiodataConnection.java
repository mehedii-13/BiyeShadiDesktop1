package com.matrimony.util;

import com.matrimony.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestBiodataConnection {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("TESTING BIODATA TABLE AND DATA");
        System.out.println("=".repeat(60));

        // Test 1: Check if biodata table exists
        System.out.println("\n Test 1: Checking if biodata table exists...");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SHOW TABLES LIKE 'biodata'";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                System.out.println("✓ Biodata table EXISTS");
            } else {
                System.out.println("Biodata table DOES NOT EXIST");
                System.out.println("Please run: initialize_biodata_table.sql");
                return;
            }

        } catch (Exception e) {
            System.err.println(" Error checking table: " + e.getMessage());
            return;
        }

        // Test 2: Check biodata table structure
        System.out.println("\n✓ Test 2: Checking biodata table structure...");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "DESCRIBE biodata";
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("Columns in biodata table:");
            int count = 0;
            while (rs.next()) {
                count++;
                String field = rs.getString("Field");
                String type = rs.getString("Type");
                System.out.println("  " + count + ". " + field + " (" + type + ")");
            }
            System.out.println("✓ Found " + count + " columns");

        } catch (Exception e) {
            System.err.println(" Error checking structure: " + e.getMessage());
        }

        // Test 3: Count biodata records
        System.out.println("\n✓ Test 3: Counting biodata records...");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT COUNT(*) as total FROM biodata";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("Total biodata records: " + total);

                if (total == 0) {
                    System.out.println(" No biodata records found. This is normal for new users.");
                }
            }

        } catch (Exception e) {
            System.err.println(" Error counting records: " + e.getMessage());
        }

        // Test 4: List all biodata records
        System.out.println("\n Test 4: Listing all biodata records...");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = """
                SELECT 
                    b.id as biodata_id,
                    b.user_id,
                    u.full_name,
                    u.email,
                    b.age,
                    b.education,
                    b.occupation,
                    b.city,
                    b.profile_completed
                FROM biodata b
                JOIN users u ON b.user_id = u.id
                ORDER BY b.id DESC
                LIMIT 10
                """;
            ResultSet rs = stmt.executeQuery(sql);

            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                System.out.println("   ---");
                System.out.println("    Biodata ID: " + rs.getInt("biodata_id"));
                System.out.println("    User ID: " + rs.getInt("user_id"));
                System.out.println("    Name: " + rs.getString("full_name"));
                System.out.println("    Email: " + rs.getString("email"));
                System.out.println("    Age: " + rs.getInt("age"));
                System.out.println("    Education: " + rs.getString("education"));
                System.out.println("    Occupation: " + rs.getString("occupation"));
                System.out.println("    City: " + rs.getString("city"));
                System.out.println("    Completed: " + rs.getBoolean("profile_completed"));
            }

            if (!hasRecords) {
                System.out.println(" No biodata records to display");
            }

        } catch (Exception e) {
            System.err.println(" Error listing records: " + e.getMessage());
            e.printStackTrace();
        }

        // Test 5: Test fetch by user_id
        System.out.println("\n✓ Test 5: Testing fetch by user_id...");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String getUserSql = "SELECT id, full_name, email FROM users LIMIT 1";
            ResultSet userRs = stmt.executeQuery(getUserSql);

            if (userRs.next()) {
                int userId = userRs.getInt("id");
                String userName = userRs.getString("full_name");
                String userEmail = userRs.getString("email");

                System.out.println("Testing with user:");
                System.out.println("   - ID: " + userId);
                System.out.println("   - Name: " + userName);
                System.out.println("   - Email: " + userEmail);

                String getBiodataSql = "SELECT * FROM biodata WHERE user_id = " + userId;
                ResultSet biodataRs = stmt.executeQuery(getBiodataSql);

                if (biodataRs.next()) {
                    System.out.println("✓ Biodata FOUND for this user!");
                    System.out.println("   - Education: " + biodataRs.getString("education"));
                    System.out.println("   - Occupation: " + biodataRs.getString("occupation"));
                    System.out.println("   - City: " + biodataRs.getString("city"));
                } else {
                    System.out.println(" No biodata found for this user (expected for new users)");
                }
            } else {
                System.out.println(" No users found in users table");
            }

        } catch (Exception e) {
            System.err.println(" Error in fetch test: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println(" TEST COMPLETE");
        System.out.println("=".repeat(60));
    }
}


