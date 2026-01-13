package com.matrimony.util;

import com.matrimony.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class ViewDatabaseData {
    
    public static void main(String[] args) {
        String sql = "SELECT id, full_name, email, phone, gender, created_at FROM users ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            System.out.println(String.format("%-5s %-25s %-30s %-15s %-10s %-20s", 
                "ID", "Full Name", "Email", "Phone", "Gender", "Created At"));
            while (rs.next()) {
                count++;
                System.out.println(String.format("%-5d %-25s %-30s %-15s %-10s %-20s",
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("gender"),
                    rs.getTimestamp("created_at")
                ));
            }

            System.out.println("\nTotal Users: " + count);
            
            if (count == 0) {
                System.out.println("\n  No users found in database.");
                System.out.println("Create an account using the app to see data here!");
            }
            
        } catch (Exception e) {
            System.err.println("\n Error connecting to database!");
            System.err.println("Make sure:");
            System.err.println("1. MySQL is running");
            System.err.println("2. Database 'matrimony_db' exists");
            System.err.println("3. Database credentials are correct in DatabaseConnection.java");
            System.err.println("\nError details: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
