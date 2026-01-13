package com.matrimony.controller;

import com.matrimony.dao.ContactRequestDAO;
import com.matrimony.dao.UserDAO;
import com.matrimony.dao.BiodataDAO;
import com.matrimony.model.ContactRequest;
import com.matrimony.model.User;
import com.matrimony.model.Biodata;
import com.matrimony.model.MatchProfile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectedPeopleController {

    @FXML
    private VBox connectedPeopleContainer;

    @FXML
    private VBox noDataMessage;

    @FXML
    private Button backButton;

    @FXML
    private Label userNameLabel;

    private User currentUser;
    private ContactRequestDAO contactRequestDAO;
    private UserDAO userDAO;
    private BiodataDAO biodataDAO;

    @FXML
    public void initialize() {
        System.out.println("✓ ConnectedPeopleController initialized");
        contactRequestDAO = new ContactRequestDAO();
        userDAO = new UserDAO();
        biodataDAO = new BiodataDAO();
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            if (userNameLabel != null) {
                userNameLabel.setText(user.getFullName());
            }
            loadConnectedPeople();
        }
    }

    private void loadConnectedPeople() {
        try {
            System.out.println("=== Loading Connected People ===");
            System.out.println("Current User ID: " + currentUser.getId());

            // Get all sent requests with accepted status
            List<ContactRequest> sentRequests = contactRequestDAO.getSentRequests(currentUser.getId());

            // Get all received requests with accepted status
            List<ContactRequest> receivedRequests = contactRequestDAO.getReceivedRequests(currentUser.getId());

            // Use a set to avoid duplicates
            Set<Integer> connectedUserIds = new HashSet<>();

            // Add users from sent accepted requests
            for (ContactRequest request : sentRequests) {
                if ("accepted".equals(request.getStatus())) {
                    connectedUserIds.add(request.getReceiverId());
                }
            }

            // Add users from received accepted requests
            for (ContactRequest request : receivedRequests) {
                if ("accepted".equals(request.getStatus())) {
                    connectedUserIds.add(request.getSenderId());
                }
            }

            System.out.println("✓ Found " + connectedUserIds.size() + " connected people");

            if (connectedUserIds.isEmpty()) {
                showNoDataMessage();
                System.out.println(" No connected people found");
            } else {
                hideNoDataMessage();
                displayConnectedPeople(new ArrayList<>(connectedUserIds));
            }

        } catch (Exception e) {
            System.err.println(" Error loading connected people: " + e.getMessage());
            e.printStackTrace();
            showNoDataMessage();
        }
    }

    private void displayConnectedPeople(List<Integer> userIds) {
        System.out.println("Clearing container...");
        connectedPeopleContainer.getChildren().clear();

        System.out.println("Processing " + userIds.size() + " connected user IDs...");

        for (int userId : userIds) {
            try {
                System.out.println("Loading data for user ID: " + userId);

                User user = userDAO.getUserById(userId);

                if (user != null) {
                    Biodata biodata = biodataDAO.getBiodataByUserId(userId);
                    VBox profileCard = createProfileCard(user, biodata);
                    connectedPeopleContainer.getChildren().add(profileCard);
                    System.out.println("✓ Profile card added to container");
                } else {
                    System.err.println(" User with ID " + userId + " not found!");
                }

            } catch (Exception e) {
                System.err.println(" Error loading data for user " + userId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("✓ Total cards displayed: " + connectedPeopleContainer.getChildren().size());
    }

    private VBox createProfileCard(User user, Biodata biodata) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Header with name and action buttons
        HBox header = new HBox(15);
        header.setStyle("-fx-alignment: center-left;");

        Label nameLabel = new Label(user.getFullName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #E91E63;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // View Profile button
        Button viewProfileBtn = new Button("View Full Profile");
        viewProfileBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15;");
        viewProfileBtn.setOnAction(e -> handleViewProfile(user.getId()));

        // Message button
        Button messageBtn = new Button("Message");
        messageBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15;");
        messageBtn.setOnAction(e -> handleMessage(user));

        header.getChildren().addAll(nameLabel, spacer, viewProfileBtn, messageBtn);

        // Details grid
        GridPane details = new GridPane();
        details.setHgap(30);
        details.setVgap(10);
        details.setPadding(new Insets(10, 0, 0, 0));

        int row = 0;

        // Add basic details
        addDetailRow(details, row++, "Gender:", capitalizeFirst(user.getGender()));

        // Email and Phone are now visible since they're connected
        addDetailRow(details, row++, "Email:", user.getEmail());
        addDetailRow(details, row++, "Phone:", user.getPhone());

        if (biodata != null) {
            if (biodata.getAge() > 0) {
                addDetailRow(details, row++, "Age:", biodata.getAge() + " years");
            }
            if (biodata.getEducation() != null && !biodata.getEducation().isEmpty()) {
                addDetailRow(details, row++, "Education:", biodata.getEducation());
            }
            if (biodata.getOccupation() != null && !biodata.getOccupation().isEmpty()) {
                addDetailRow(details, row++, "Occupation:", biodata.getOccupation());
            }
            if (biodata.getCity() != null && !biodata.getCity().isEmpty()) {
                addDetailRow(details, row++, "City:", biodata.getCity());
            }
        }

        // Connection status label
        Label connectedLabel = new Label("Connected");
        connectedLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        card.getChildren().addAll(header, details, connectedLabel);
        return card;
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

        Label valueNode = new Label(value != null ? value : "Not specified");
        valueNode.setStyle("-fx-text-fill: #333;");

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private void handleViewProfile(int userId) {
        try {
            System.out.println("✓ Opening full profile for user ID: " + userId);

            User user = userDAO.getUserById(userId);
            if (user == null) {
                showAlert("Error", "User not found.", Alert.AlertType.ERROR);
                return;
            }

            Biodata biodata = biodataDAO.getBiodataByUserId(userId);

            // Create MatchProfile object
            MatchProfile profile = new MatchProfile(
                userId,
                user.getFullName(),
                user.getGender(),
                biodata != null ? biodata.getAge() : 0,
                biodata != null ? biodata.getHeight() : "",
                biodata != null ? biodata.getMaritalStatus() : "",
                biodata != null ? biodata.getReligion() : "",
                biodata != null ? biodata.getEducation() : "",
                biodata != null ? biodata.getOccupation() : "",
                biodata != null ? biodata.getCity() : "",
                biodata != null ? biodata.getState() : "",
                biodata != null ? biodata.getCountry() : "",
                biodata != null ? biodata.getAnnualIncome() : "",
                biodata != null ? biodata.getPhotoPath() : ""
            );

            // Navigate to ViewProfile page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewProfile.fxml"));
            Parent viewProfilePage = loader.load();

            ViewProfileController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setProfile(profile);

            Scene scene = new Scene(viewProfilePage);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Profile - " + user.getFullName());

            System.out.println("✓ Navigated to ViewProfile page");

        } catch (Exception e) {
            System.err.println(" Error opening profile: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load profile.\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleMessage(User user) {
        System.out.println("💬 Message button clicked for: " + user.getFullName());
        showAlert("Messages", "Messaging feature coming soon!\nYou can contact " + user.getFullName() +
                  " at:\nEmail: " + user.getEmail() + "\nPhone: " + user.getPhone(), Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent dashboard = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(dashboard);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - " + currentUser.getFullName());

            System.out.println("✓ Navigated back to Dashboard");
        } catch (Exception e) {
            System.err.println(" Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchMatches() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchMatches.fxml"));
            Parent searchPage = loader.load();

            SearchMatchesController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(searchPage);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Search Matches");

            System.out.println(" Navigated to Search Matches");
        } catch (Exception e) {
            System.err.println(" Error navigating to search: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            System.out.println("✓ Navigating back to User Dashboard");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent dashboard = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(dashboard);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - " + currentUser.getFullName());

            System.out.println(" Navigated back to dashboard");
        } catch (Exception e) {
            System.err.println(" Error navigating back to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCheckContactRequests() {
        try {
            System.out.println("✓ Navigating to Contact Requests from Connected People page");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ContactRequests.fxml"));
            Parent requestsPage = loader.load();

            ContactRequestsController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(requestsPage);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Contact Requests");

            System.out.println("✓ Navigated to Contact Requests");
        } catch (Exception e) {
            System.err.println(" Error navigating to contact requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNoDataMessage() {
        if (connectedPeopleContainer != null) {
            connectedPeopleContainer.setVisible(false);
        }
        if (noDataMessage != null) {
            noDataMessage.setVisible(true);
        }
    }

    private void hideNoDataMessage() {
        if (connectedPeopleContainer != null) {
            connectedPeopleContainer.setVisible(true);
        }
        if (noDataMessage != null) {
            noDataMessage.setVisible(false);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

