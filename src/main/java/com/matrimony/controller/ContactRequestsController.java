package com.matrimony.controller;

import com.matrimony.dao.ContactRequestDAO;
import com.matrimony.dao.UserDAO;
import com.matrimony.dao.BiodataDAO;
import com.matrimony.model.ContactRequest;
import com.matrimony.model.User;
import com.matrimony.model.Biodata;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for viewing requests received BY the current user (Contact Requests)
 */
public class ContactRequestsController {

    @FXML
    private VBox requestsContainer;

    @FXML
    private Button backButton;

    private User currentUser;
    private ContactRequestDAO contactRequestDAO;

    @FXML
    public void initialize() {
        contactRequestDAO = new ContactRequestDAO();
        System.out.println("ContactRequestsController initialized");
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            loadContactRequests();
        }
    }

    private void loadContactRequests() {
        if (currentUser == null) {
            return;
        }

        requestsContainer.getChildren().clear();

        try {
            List<ContactRequest> receivedRequests = contactRequestDAO.getReceivedRequests(currentUser.getId());

            List<ContactRequest> pendingOnly = new ArrayList<>();
            for (ContactRequest request : receivedRequests) {
                if ("pending".equals(request.getStatus())) {
                    pendingOnly.add(request);
                }
            }

            if (pendingOnly.isEmpty()) {
                Label noRequests = new Label("You haven't received any pending contact requests.");
                noRequests.setStyle("-fx-font-size: 16px; -fx-text-fill: gray; -fx-padding: 20;");
                requestsContainer.getChildren().add(noRequests);
                return;
            }

            System.out.println("Loaded " + pendingOnly.size() + "pending requests (filtered from " + receivedRequests.size() + "total)");

            for (ContactRequest request : pendingOnly) {
                VBox requestCard = createRequestCard(request);
                requestsContainer.getChildren().add(requestCard);
            }

        } catch (Exception e) {
            System.err.println("Error loading contact requests: " + e.getMessage());
            e.printStackTrace();

            Label errorLabel = new Label("Error loading requests: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            requestsContainer.getChildren().add(errorLabel);
        }
    }

    private VBox createRequestCard(ContactRequest request) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
        card.setPadding(new Insets(15));

        Label nameLabel = new Label("Request from: " + request.getSenderName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label statusLabel = new Label("Status: " + capitalizeFirst(request.getStatus()));
        String statusColor = getStatusColor(request.getStatus());
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");

        Label messageLabel = new Label("Message: " + (request.getMessage() != null ? request.getMessage() : "No message"));
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        Label dateLabel = new Label("Received on: " + request.getCreatedAt().toString());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: darkgray;");

        HBox actions = new HBox(10);

        Button viewProfileBtn = new Button("View Profile");
        viewProfileBtn.setStyle("-fx-background-color: slateblue; -fx-text-fill: white; -fx-cursor: hand;");
        viewProfileBtn.setOnAction(e -> handleViewProfile(request.getSenderId()));
        actions.getChildren().add(viewProfileBtn);

        if ("pending".equals(request.getStatus())) {
            Button acceptBtn = new Button("Accept");
            acceptBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            acceptBtn.setOnAction(e -> handleAcceptRequest(request));

            Button rejectBtn = new Button("Reject");
            rejectBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            rejectBtn.setOnAction(e -> handleRejectRequest(request));

            actions.getChildren().addAll(acceptBtn, rejectBtn);
        }

        card.getChildren().addAll(nameLabel, statusLabel, messageLabel, dateLabel, actions);
        return card;
    }

    private void handleAcceptRequest(ContactRequest request) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Accept Request");
        confirm.setHeaderText("Accept request from " + request.getSenderName() + "?");
        confirm.setContentText("They will be able to view your contact details.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean success = contactRequestDAO.acceptRequest(request.getId());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Request accepted! " + request.getSenderName() + "can now view your contact details.");
                loadContactRequests();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to accept request.");
            }
        }
    }

    private void handleRejectRequest(ContactRequest request) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reject Request");
        confirm.setHeaderText("Reject request from " + request.getSenderName() + "?");
        confirm.setContentText("This action cannot be undone.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean success = contactRequestDAO.rejectRequest(request.getId());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Request rejected.");
                loadContactRequests();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject request.");
            }
        }
    }

    private String getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "pending": return "orange";
            case "accepted": return "green";
            case "rejected": return "red";
            case "cancelled": return "gray";
            default: return "black";
        }
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private void handleViewProfile(int userId) {
        try {
            System.out.println("Opening profile for user ID: " + userId);

            UserDAO userDAO = new UserDAO();
            BiodataDAO biodataDAO = new BiodataDAO();

            User user = userDAO.getUserById(userId);
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "User not found.");
                return;
            }

            Biodata biodata = biodataDAO.getBiodataByUserId(userId);

            com.matrimony.model.MatchProfile profile = new com.matrimony.model.MatchProfile();
            profile.setUserId(userId);
            profile.setFullName(user.getFullName());
            profile.setGender(user.getGender());

            if (biodata != null) {
                profile.setAge(biodata.getAge());
                profile.setHeight(biodata.getHeight());
                profile.setMaritalStatus(biodata.getMaritalStatus());
                profile.setReligion(biodata.getReligion());
                profile.setEducation(biodata.getEducation());
                profile.setOccupation(biodata.getOccupation());
                profile.setAnnualIncome(biodata.getAnnualIncome());
                profile.setCity(biodata.getCity());
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewProfile.fxml"));
            Parent viewProfile = loader.load();

            ViewProfileController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setProfile(profile);

            Scene scene = new Scene(viewProfile);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("View Profile - " + user.getFullName());

            System.out.println("Navigated to profile view");
        } catch (Exception e) {
            System.err.println("Error opening profile: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open profile.");
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent dashboard = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setUser(currentUser);

            controller.refreshRequestCounts();

            Scene scene = new Scene(dashboard);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - " + currentUser.getFullName());

            System.out.println("Returned to dashboard with updated counts");

        } catch (Exception e) {
            System.err.println("Error navigating back to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

