package com.matrimony.dao;

import com.matrimony.database.DatabaseConnection;
import com.matrimony.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {


    public boolean registerUser(User user) {

        if (emailExists(user.getEmail())) {
            System.err.println("Registration failed: Email already exists");
            return false;
        }

        String sql =
                "INSERT INTO users (full_name, email, phone, gender, password, role) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                System.err.println("Registration failed: Database connection error");
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                String hashedPassword =
                        BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

                pstmt.setString(1, user.getFullName());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPhone());
                pstmt.setString(4, user.getGender());
                pstmt.setString(5, hashedPassword);
                pstmt.setString(6, user.getRole() != null ? user.getRole() : "user");

                return pstmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public boolean emailExists(String email) {

        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, email);

                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            return false;
        }
    }

    public User authenticateUser(String email, String password) {

        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                return null;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, email);

                try (ResultSet rs = pstmt.executeQuery()) {

                    if (rs.next()) {
                        String storedHash = rs.getString("password");

                        if (BCrypt.checkpw(password, storedHash)) {
                            return mapUser(rs);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }

        return null;
    }


    public User getUserByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                return null;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, email);

                try (ResultSet rs = pstmt.executeQuery()) {

                    if (rs.next()) {
                        return mapUser(rs);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }

        return null;
    }


    private User mapUser(ResultSet rs) throws SQLException {

        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setGender(rs.getString("gender"));
        user.setRole(rs.getString("role"));
        user.setBlocked(rs.getBoolean("blocked"));

        java.sql.Date blockedUntilDate = rs.getDate("blocked_until");
        if (blockedUntilDate != null) {
            user.setBlockedUntil(blockedUntilDate.toLocalDate());
        }

        return user;
    }

    public User getUserById(int id) {

        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                return null;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);

                try (ResultSet rs = pstmt.executeQuery()) {

                    if (rs.next()) {
                        return mapUser(rs);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }

        return null;
    }


    public boolean updateUserRole(int userId, String newRole) {

        String sql = "UPDATE users SET role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                System.err.println("Update failed: Database connection error");
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, newRole);
                pstmt.setInt(2, userId);

                return pstmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error updating user role: " + e.getMessage());
            return false;
        }
    }

    public boolean isUserAdmin(String email) {

        User user = getUserByEmail(email);
        return user != null && "admin".equals(user.getRole());
    }

    /**
     * Get all users (for admin dashboard)
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Block a user for a specified number of days
     */
    public boolean blockUser(int userId, java.time.LocalDate blockedUntil) {
        String sql = "UPDATE users SET blocked = TRUE, blocked_until = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(blockedUntil));
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User " + userId + "blocked until " + blockedUntil);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error blocking user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Unblock a user
     */
    public boolean unblockUser(int userId) {
        String sql = "UPDATE users SET blocked = FALSE, blocked_until = NULL WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User " + userId + "unblocked");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error unblocking user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete a user permanently
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User " + userId + "deleted");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}


