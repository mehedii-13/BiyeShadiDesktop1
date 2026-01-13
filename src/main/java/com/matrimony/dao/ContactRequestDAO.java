package com.matrimony.dao;

import com.matrimony.database.DatabaseConnection;
import com.matrimony.model.ContactRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactRequestDAO {

    /**
     * Send a contact request
     * Validates that users are of opposite gender before sending
     */
    public boolean sendContactRequest(int senderId, int receiverId, String message) {
        System.out.println("=== sendContactRequest() called ===");
        System.out.println("Sender ID: " + senderId);
        System.out.println("Receiver ID: " + receiverId);
        System.out.println("Message: " + message);

        // Check for same gender (prevent same-gender contact requests)
        if (areSameGender(senderId, receiverId)) {
            System.err.println("ERROR: Cannot send contact request to user of same gender!");
            System.err.println("Sender ID: " + senderId + ", Receiver ID: " + receiverId);
            return false;
        }

        System.out.println("Checking for active requests...");
        if (hasActiveRequest(senderId, receiverId)) {
            System.err.println("Active contact request already exists between users " + senderId + " and " + receiverId);
            return false;
        }

        System.out.println("No active request found - proceeding with insert");

        String sql = "INSERT INTO contact_requests (sender_id, receiver_id, status, message) VALUES (?, ?, 'pending', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            System.out.println("Database connection established");

            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            pstmt.setString(3, message);

            System.out.println("Executing INSERT query...");
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                System.out.println("Contact request sent successfully from user " + senderId + " to user " + receiverId);
                return true;
            } else {
                System.err.println("No rows were inserted!");
            }

        } catch (SQLException e) {
            System.err.println("SQL Exception in sendContactRequest:");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected exception in sendContactRequest:");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== sendContactRequest() returning FALSE ===");
        return false;
    }

    /**
     * Check if two users are of the same gender
     * Returns true if same gender, false if opposite gender or if error
     */
    private boolean areSameGender(int userId1, int userId2) {
        String sql = "SELECT u1.gender as gender1, u2.gender as gender2 " +
                    "FROM users u1, users u2 " +
                    "WHERE u1.id = ? AND u2.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId1);
            pstmt.setInt(2, userId2);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String gender1 = rs.getString("gender1");
                    String gender2 = rs.getString("gender2");

                    boolean sameGender = gender1 != null && gender1.equalsIgnoreCase(gender2);
                    System.out.println("Gender check - User " + userId1 + ": " + gender1 +
                                     ", User " + userId2 + ": " + gender2 +
                                     " => Same gender: " + sameGender);
                    return sameGender;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking user genders: " + e.getMessage());
            e.printStackTrace();
        }

        // Default to blocking the request if we can't verify
        return true;
    }

    /**
     * Check if an active contact request exists between two users (in either direction)
     * Facebook-style one-way requests: If A sent to B, B cannot send to A until A's request is resolved
     * (Only checks for pending or accepted requests, allows re-send after cancelled/rejected)
     */
    public boolean hasActiveRequest(int senderId, int receiverId) {
        System.out.println("Checking hasActiveRequest(" + senderId + ", " + receiverId + ")");

        // Check if sender already sent a request to receiver (pending or accepted)
        String sql = "SELECT status FROM contact_requests " +
                    "WHERE ((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)) " +
                    "AND status IN ('pending', 'accepted') " +
                    "ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            pstmt.setInt(3, receiverId);
            pstmt.setInt(4, senderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    System.out.println("Active request found with status: " + status);
                    return true;
                } else {
                    System.out.println("No active request found - OK to send");
                    return false;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking if active contact request exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Check if there's a pending request from the other user (reverse direction)
     * Returns the request status if exists, null otherwise
     */
    public String getPendingRequestFromUser(int currentUserId, int otherUserId) {
        System.out.println("Checking if user " + otherUserId + " has sent a request to user " + currentUserId);

        String sql = "SELECT status FROM contact_requests " +
                    "WHERE sender_id = ? AND receiver_id = ? AND status = 'pending' " +
                    "ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, otherUserId);
            pstmt.setInt(2, currentUserId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    System.out.println("Found pending request from " + otherUserId + " to " + currentUserId);
                    return status;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking pending request from user: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Check if a contact request already exists between two users (any status)
     * @deprecated Use hasActiveRequest() instead to allow re-sending after cancellation
     */
    @Deprecated
    public boolean requestExists(int senderId, int receiverId) {
        String sql = "SELECT 1 FROM contact_requests WHERE sender_id = ? AND receiver_id = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error checking if contact request exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get all contact requests sent by a user
     */
    public List<ContactRequest> getSentRequests(int userId) {
        List<ContactRequest> requests = new ArrayList<>();
        String sql = "SELECT cr.*, u.full_name as receiver_name " +
                    "FROM contact_requests cr " +
                    "JOIN users u ON cr.receiver_id = u.id " +
                    "WHERE cr.sender_id = ? " +
                    "ORDER BY cr.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ContactRequest request = mapResultSetToContactRequest(rs);
                    request.setReceiverName(rs.getString("receiver_name"));
                    requests.add(request);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching sent requests: " + e.getMessage());
            e.printStackTrace();
        }

        return requests;
    }

    /**
     * Get all contact requests received by a user
     */
    public List<ContactRequest> getReceivedRequests(int userId) {
        List<ContactRequest> requests = new ArrayList<>();
        String sql = "SELECT cr.*, u.full_name as sender_name " +
                    "FROM contact_requests cr " +
                    "JOIN users u ON cr.sender_id = u.id " +
                    "WHERE cr.receiver_id = ? " +
                    "ORDER BY cr.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ContactRequest request = mapResultSetToContactRequest(rs);
                    request.setSenderName(rs.getString("sender_name"));
                    requests.add(request);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching received requests: " + e.getMessage());
            e.printStackTrace();
        }

        return requests;
    }

    /**
     * Update the status of a contact request
     */
    public boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE contact_requests SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Contact request " + requestId + "status updated to: " + status);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error updating request status: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get request status between two users (returns the most recent request)
     */
    public String getRequestStatus(int senderId, int receiverId) {
        String sql = "SELECT status FROM contact_requests WHERE sender_id = ? AND receiver_id = ? ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    System.out.println("Latest request status from " + senderId + "to " + receiverId + ": " + status);
                    return status;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting request status: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("No request found from " + senderId + "to " + receiverId);
        return null;
    }

    /**
     * Cancel a contact request
     */
    public boolean cancelRequest(int requestId) {
        return updateRequestStatus(requestId, "cancelled");
    }

    /**
     * Accept a contact request
     */
    public boolean acceptRequest(int requestId) {
        return updateRequestStatus(requestId, "accepted");
    }

    /**
     * Reject a contact request
     */
    public boolean rejectRequest(int requestId) {
        return updateRequestStatus(requestId, "rejected");
    }

    /**
     * Get count of pending requests for a user
     */
    public int getPendingRequestCount(int userId) {
        String sql = "SELECT COUNT(*) FROM contact_requests WHERE receiver_id = ? AND status = 'pending'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting pending request count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Check if contact request has been accepted between two users (in either direction)
     * This is used to determine if contact details should be visible
     */
    public boolean isRequestAccepted(int user1Id, int user2Id) {
        String sql = "SELECT 1 FROM contact_requests " +
                    "WHERE ((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)) " +
                    "AND status = 'accepted' LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user1Id);
            pstmt.setInt(2, user2Id);
            pstmt.setInt(3, user2Id);
            pstmt.setInt(4, user1Id);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean isAccepted = rs.next();
                System.out.println("Checking if request accepted between " + user1Id + "and " + user2Id + ": " + isAccepted);
                return isAccepted;
            }

        } catch (SQLException e) {
            System.err.println("Error checking if request is accepted: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Helper method to map ResultSet to ContactRequest object
     */
    private ContactRequest mapResultSetToContactRequest(ResultSet rs) throws SQLException {
        ContactRequest request = new ContactRequest();
        request.setId(rs.getInt("id"));
        request.setSenderId(rs.getInt("sender_id"));
        request.setReceiverId(rs.getInt("receiver_id"));
        request.setStatus(rs.getString("status"));
        request.setMessage(rs.getString("message"));
        request.setCreatedAt(rs.getTimestamp("created_at"));
        request.setUpdatedAt(rs.getTimestamp("updated_at"));
        return request;
    }
}

