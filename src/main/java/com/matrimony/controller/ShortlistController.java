package com.matrimony.controller;

import com.matrimony.dao.ShortlistDAO;
import com.matrimony.dao.BiodataDAO;
import com.matrimony.dao.UserDAO;
import com.matrimony.dao.ContactRequestDAO;
import com.matrimony.model.User;
import com.matrimony.model.Biodata;
import com.matrimony.model.MatchProfile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ShortlistController {

    @FXML private VBox shortlistContainer;
    @FXML private VBox noDataMessage;

    private User currentUser;
    private ShortlistDAO shortlistDAO;
    private BiodataDAO biodataDAO;
    private UserDAO userDAO;
    private ContactRequestDAO contactRequestDAO;

    @FXML
    public void initialize() {
        System.out.println("ShortlistController initialized");
        shortlistDAO = new ShortlistDAO();
        biodataDAO = new BiodataDAO();
        userDAO = new UserDAO();
        contactRequestDAO = new ContactRequestDAO();
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            loadShortlist();
        }
    }

    private void loadShortlist() {
        try {
            System.out.println("Current User ID: " + currentUser.getId());

            List<Integer> shortlistedUserIds = shortlistDAO.getShortlistedUserIds(currentUser.getId());

            System.out.println("Shortlisted User IDs retrieved: " + shortlistedUserIds.size());

            if (shortlistedUserIds.isEmpty()) {
                System.out.println("No shortlisted profiles found - showing empty message");
                showNoDataMessage();
            } else {
                System.out.println("Found " + shortlistedUserIds.size() + "shortlisted profiles");
                hideNoDataMessage();
                displayShortlistedProfiles(shortlistedUserIds);
            }

        } catch (Exception e) {
            System.err.println("Error loading shortlist: " + e.getMessage());
            e.printStackTrace();
            showNoDataMessage();
        }
    }

    private void displayShortlistedProfiles(List<Integer> userIds) {
        System.out.println("=== Displaying Shortlisted Profiles ===");
        System.out.println("Clearing container...");
        shortlistContainer.getChildren().clear();

        System.out.println("Processing " + userIds.size() + "user IDs...");

        for (Integer userId : userIds) {
            try {
                System.out.println("Loading user with ID: " + userId);
                User user = userDAO.getUserById(userId);

                if (user != null) {
                    System.out.println("User found: " + user.getFullName());
                    Biodata biodata = biodataDAO.getBiodataByUserId(userId);
                    System.out.println("Biodata loaded: " + (biodata != null ? "Yes" : "No"));

                    VBox profileCard = createProfileCard(user, biodata);
                    shortlistContainer.getChildren().add(profileCard);
                    System.out.println("Profile card added to container");
                } else {
                    System.err.println("User with ID " + userId + "not found!");
                }
            } catch (Exception e) {
                System.err.println("Error loading profile for user " + userId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Total cards displayed: " + shortlistContainer.getChildren().size());
    }

    private VBox createProfileCard(User user, Biodata biodata) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        HBox header = new HBox(15);
        header.setStyle("-fx-alignment: center-left;");

        Label nameLabel = new Label(user.getFullName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #E91E63;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewProfileBtn = new Button("View Profile");
        viewProfileBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15;");
        viewProfileBtn.setOnAction(e -> handleViewProfile(user.getId()));

        Button removeBtn = new Button("Remove from Shortlist");
        removeBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15;");
        removeBtn.setOnAction(e -> handleRemoveFromShortlist(user.getId()));

        header.getChildren().addAll(nameLabel, spacer, viewProfileBtn, removeBtn);

        GridPane details = new GridPane();
        details.setHgap(30);
        details.setVgap(10);
        details.setPadding(new Insets(10, 0, 0, 0));

        int row = 0;

        addDetailRow(details, row++, "Gender:", capitalizeFirst(user.getGender()));

        boolean isRequestAccepted = contactRequestDAO.isRequestAccepted(currentUser.getId(), user.getId());

        if (isRequestAccepted) {
            addDetailRow(details, row++, "Email:", user.getEmail());
            addDetailRow(details, row++, "Phone:", user.getPhone());
        } else {
            addDetailRow(details, row++, "Email:", "Private (Send contact request)");
            addDetailRow(details, row++, "Phone:", "Private (Send contact request)");
        }

        if (biodata != null) {
            if (biodata.getAge() > 0) {
                addDetailRow(details, row++, "Age:", biodata.getAge() + " years");
            }
            if (biodata.getHeight() != null) {
                addDetailRow(details, row++, "Height:", biodata.getHeight());
            }
            if (biodata.getEducation() != null) {
                addDetailRow(details, row++, "Education:", biodata.getEducation());
            }
            if (biodata.getOccupation() != null) {
                addDetailRow(details, row++, "Occupation:", biodata.getOccupation());
            }
            if (biodata.getCity() != null) {
                addDetailRow(details, row++, "City:", biodata.getCity());
            }
        }

        card.getChildren().addAll(header, new Separator(), details);
        return card;
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-text-fill: #666;");

        Label valueNode = new Label(value != null ? value : "N/A");
        valueNode.setStyle("-fx-text-fill: #333;");

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private void handleViewProfile(int userId) {
        try {
            System.out.println("Opening full profile for user ID: " + userId);

            User user = userDAO.getUserById(userId);
            if (user == null) {
                showAlert("Error", "User not found.", Alert.AlertType.ERROR);
                return;
            }

            Biodata biodata = biodataDAO.getBiodataByUserId(userId);

            MatchProfile profile = new MatchProfile();
            profile.setUserId(user.getId());
            profile.setFullName(user.getFullName());
            profile.setGender(user.getGender());

            if (biodata != null) {
                profile.setAge(biodata.getAge());
                profile.setHeight(biodata.getHeight());
                profile.setEducation(biodata.getEducation());
                profile.setOccupation(biodata.getOccupation());
                profile.setCity(biodata.getCity());
                profile.setMaritalStatus(biodata.getMaritalStatus());
                profile.setReligion(biodata.getReligion());
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewProfile.fxml"));
            Parent viewProfile = loader.load();

            ViewProfileController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setProfile(profile);

            Scene scene = new Scene(viewProfile);
            Stage stage = (Stage) shortlistContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("View Profile - " + user.getFullName());

            System.out.println("Navigated to full profile view");
        } catch (Exception e) {
            System.err.println("Error opening profile view: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to open profile view.\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleRemoveFromShortlist(int shortlistedUserId) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Remove from Shortlist");
        confirmation.setHeaderText("Are you sure?");
        confirmation.setContentText("Do you want to remove this profile from your shortlist?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean removed = shortlistDAO.removeFromShortlist(currentUser.getId(), shortlistedUserId);
                if (removed) {
                    showAlert("Success", "Profile removed from shortlist.", Alert.AlertType.INFORMATION);
                    loadShortlist();
                } else {
                    showAlert("Error", "Failed to remove profile from shortlist.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showNoDataMessage() {
        if (shortlistContainer != null) {
            shortlistContainer.setVisible(false);
            shortlistContainer.setManaged(false);
        }
        if (noDataMessage != null) {
            noDataMessage.setVisible(true);
            noDataMessage.setManaged(true);
        }
    }

    private void hideNoDataMessage() {
        if (shortlistContainer != null) {
            shortlistContainer.setVisible(true);
            shortlistContainer.setManaged(true);
        }
        if (noDataMessage != null) {
            noDataMessage.setVisible(false);
            noDataMessage.setManaged(false);
        }
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent dashboard = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setUser(currentUser);

            controller.refreshShortlistCount();

            Scene scene = new Scene(dashboard);
            Stage stage = (Stage) shortlistContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("User Dashboard");

            System.out.println("Navigated back to dashboard with updated shortlist count");
        } catch (Exception e) {
            System.err.println("Error navigating to dashboard: " + e.getMessage());
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
            Stage stage = (Stage) shortlistContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Search Matches");

            System.out.println("Navigated to Search Matches");
        } catch (Exception e) {
            System.err.println("Error navigating to Search Matches: " + e.getMessage());
            e.printStackTrace();
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

