package com.matrimony.dao;

import com.matrimony.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShortlistDAO {

    /**
     * Add a user to shortlist
     */
    public boolean addToShortlist(int userId, int shortlistedUserId) {
        String sql = "INSERT INTO shortlist (user_id, shortlisted_user_id) VALUES (?, ?)";

        System.out.println("=== Attempting to add to shortlist ===");
        System.out.println("User ID: " + userId);
        System.out.println("Shortlisted User ID: " + shortlistedUserId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Database connection is NULL!");
                return false;
            }

            System.out.println("Database connection established");

            stmt.setInt(1, userId);
            stmt.setInt(2, shortlistedUserId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            System.out.println("User " + shortlistedUserId + "added to shortlist for user " + userId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error adding to shortlist:");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error adding to shortlist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a user from shortlist
     */
    public boolean removeFromShortlist(int userId, int shortlistedUserId) {
        String sql = "DELETE FROM shortlist WHERE user_id = ? AND shortlisted_user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, shortlistedUserId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("User " + shortlistedUserId + "removed from shortlist for user " + userId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error removing from shortlist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if a user is in shortlist
     */
    public boolean isInShortlist(int userId, int shortlistedUserId) {
        String sql = "SELECT COUNT(*) FROM shortlist WHERE user_id = ? AND shortlisted_user_id = ?";

        System.out.println("Checking if user " + shortlistedUserId + "is in shortlist for user " + userId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Database connection is NULL in isInShortlist!");
                return false;
            }

            stmt.setInt(1, userId);
            stmt.setInt(2, shortlistedUserId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Shortlist check result: " + (count > 0 ? "Already shortlisted" : "Not shortlisted"));
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("SQL Error checking shortlist:");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error checking shortlist: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all shortlisted user IDs for a user
     */
    public List<Integer> getShortlistedUserIds(int userId) {
        List<Integer> shortlistedIds = new ArrayList<>();
        String sql = "SELECT shortlisted_user_id FROM shortlist WHERE user_id = ? ORDER BY created_at DESC";

        System.out.println("=== Getting Shortlisted User IDs ===");
        System.out.println("User ID: " + userId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Database connection is NULL in getShortlistedUserIds!");
                return shortlistedIds;
            }

            System.out.println("Database connection established");

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int shortlistedId = rs.getInt("shortlisted_user_id");
                shortlistedIds.add(shortlistedId);
                System.out.println("Found shortlisted user ID: " + shortlistedId);
            }

            System.out.println("Found " + shortlistedIds.size() + "shortlisted users for user " + userId);

        } catch (SQLException e) {
            System.err.println("SQL Error getting shortlist:");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error getting shortlist: " + e.getMessage());
            e.printStackTrace();
        }

        return shortlistedIds;
    }

    /**
     * Get count of shortlisted users
     */
    public int getShortlistCount(int userId) {
        String sql = "SELECT COUNT(*) FROM shortlist WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting shortlist count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}

