package com.matrimony.controller;

import com.matrimony.dao.MatchDAO;
import com.matrimony.dao.ContactRequestDAO;
import com.matrimony.dao.ShortlistDAO;
import com.matrimony.model.MatchProfile;
import com.matrimony.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.File;
import java.util.List;

public class SearchMatchesController {

    @FXML private ComboBox<Integer> minAgeCombo;
    @FXML private ComboBox<Integer> maxAgeCombo;
    @FXML private ComboBox<String> minHeightCombo;
    @FXML private ComboBox<String> maxHeightCombo;
    @FXML private ComboBox<String> maritalStatusCombo;
    @FXML private ComboBox<String> religionCombo;
    @FXML private ComboBox<String> educationCombo;
    @FXML private ComboBox<String> incomeCombo;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField countryField;
    @FXML private TextField nameSearchField;
    @FXML private VBox resultsContainer;
    @FXML private Label resultsLabel;
    @FXML private Button backButton;
    @FXML private Button searchButton;
    @FXML private Button clearButton;
    @FXML private Button nameSearchButton;
    @FXML private Button clearNameButton;

    private User currentUser;
    private MatchDAO matchDAO;
    private ContactRequestDAO contactRequestDAO;
    private ShortlistDAO shortlistDAO;

    @FXML
    public void initialize() {
        matchDAO = new MatchDAO();
        contactRequestDAO = new ContactRequestDAO();
        shortlistDAO = new ShortlistDAO();
        initializeComboBoxes();
        setupSearchListeners();
        System.out.println("SearchMatchesController initialized");
    }

    private void setupSearchListeners() {
        // Add listener to name search field for real-time search
        if (nameSearchField != null) {
            nameSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                // Trigger search automatically when user types (after a brief pause)
                if (currentUser != null) {
                    performSearch();
                }
            });
        }

        // Add listeners to filter combo boxes for instant filtering
        if (minAgeCombo != null) {
            minAgeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (currentUser != null) performSearch();
            });
        }

        if (maxAgeCombo != null) {
            maxAgeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (currentUser != null) performSearch();
            });
        }

        if (maritalStatusCombo != null) {
            maritalStatusCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (currentUser != null) performSearch();
            });
        }

        if (religionCombo != null) {
            religionCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (currentUser != null) performSearch();
            });
        }

        if (educationCombo != null) {
            educationCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (currentUser != null) performSearch();
            });
        }

        if (incomeCombo != null) {
            incomeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (currentUser != null) performSearch();
            });
        }
    }

    private void performSearch() {
        if (currentUser == null) return;

        String name = nameSearchField.getText();
        Integer minAge = minAgeCombo.getValue();
        Integer maxAge = maxAgeCombo.getValue();
        String minHeight = minHeightCombo.getValue();
        String maxHeight = maxHeightCombo.getValue();
        String maritalStatus = maritalStatusCombo.getValue();
        String religion = religionCombo.getValue();
        String education = educationCombo.getValue();
        String income = incomeCombo.getValue();
        String city = cityField.getText();
        String state = stateField.getText();
        String country = countryField.getText();

        List<MatchProfile> matches = matchDAO.searchMatches(
            currentUser.getId(), currentUser.getGender(), name, minAge, maxAge, minHeight, maxHeight,
            maritalStatus, religion, education, income, city, state, country
        );

        displayMatches(matches);
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            System.out.println("User set in SearchMatchesController: " + user.getFullName() + " (ID: " + user.getId() + ")");
            loadAllMatches();
        }
    }

    private void initializeComboBoxes() {
        for (int i = 18; i <= 70; i++) {
            minAgeCombo.getItems().add(i);
            maxAgeCombo.getItems().add(i);
        }

        String[] heights = {
            "4'0\"", "4'1\"", "4'2\"", "4'3\"", "4'4\"", "4'5\"", "4'6\"", "4'7\"", "4'8\"", "4'9\"", "4'10\"", "4'11\"",
            "5'0\"", "5'1\"", "5'2\"", "5'3\"", "5'4\"", "5'5\"", "5'6\"", "5'7\"", "5'8\"", "5'9\"", "5'10\"", "5'11\"",
            "6'0\"", "6'1\"", "6'2\"", "6'3\"", "6'4\"", "6'5\"", "6'6\""
        };
        minHeightCombo.setItems(FXCollections.observableArrayList(heights));
        maxHeightCombo.setItems(FXCollections.observableArrayList(heights));

        maritalStatusCombo.setItems(FXCollections.observableArrayList(
            "Any", "Never Married", "Divorced", "Widowed", "Awaiting Divorce"
        ));
        maritalStatusCombo.setValue("Any");

        religionCombo.setItems(FXCollections.observableArrayList(
            "Any", "Islam", "Hinduism", "Buddhism", "Christianity", "Others"
        ));
        religionCombo.setValue("Any");

        educationCombo.setItems(FXCollections.observableArrayList(
            "Any", "High School", "Diploma", "Bachelor's Degree", "Master's Degree", "PhD", "Professional Degree"
        ));
        educationCombo.setValue("Any");

        incomeCombo.setItems(FXCollections.observableArrayList(
            "Any", "Below 2 Lakh", "2-5 Lakh", "5-10 Lakh", "10-20 Lakh", "20-50 Lakh", "50 Lakh - 1 Crore", "Above 1 Crore"
        ));
        incomeCombo.setValue("Any");
    }

    @FXML
    private void handleSearch() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session not found. Please login again.");
            return;
        }

        System.out.println("Searching matches with filters...");
        performSearch();
    }

    @FXML
    private void handleClearFilters() {
        nameSearchField.clear();
        minAgeCombo.setValue(null);
        maxAgeCombo.setValue(null);
        minHeightCombo.setValue(null);
        maxHeightCombo.setValue(null);
        maritalStatusCombo.setValue("Any");
        religionCombo.setValue("Any");
        educationCombo.setValue("Any");
        incomeCombo.setValue("Any");
        cityField.clear();
        stateField.clear();
        countryField.clear();

        System.out.println("Filters cleared");
        loadAllMatches();
    }

    @FXML
    private void handleNameSearch() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session not found. Please login again.");
            return;
        }

        String searchName = nameSearchField.getText();

        if (searchName == null || searchName.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Search", "Please enter a name to search.");
            return;
        }

        System.out.println("Searching by name: " + searchName);
        performSearch();
    }

    @FXML
    private void handleClearName() {
        nameSearchField.clear();
        System.out.println("Name search cleared");
        // The observable listener will automatically trigger search
    }

    private void loadAllMatches() {
        if (currentUser == null) return;

        System.out.println("Loading all matches for " + currentUser.getGender() + " user (opposite gender only)...");
        List<MatchProfile> matches = matchDAO.getAllMatches(currentUser.getId(), currentUser.getGender());
        displayMatches(matches);
    }

    private void displayMatches(List<MatchProfile> matches) {
        resultsContainer.getChildren().clear();

        if (matches.isEmpty()) {
            resultsLabel.setText("No matches found. Try adjusting your filters.");
            Label noResults = new Label("No profiles match your search criteria.");
            noResults.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-padding: 50;");
            resultsContainer.getChildren().add(noResults);
            return;
        }

        resultsLabel.setText("Found " + matches.size() + "match" + (matches.size() > 1 ? "es" : ""));

        for (MatchProfile profile : matches) {
            VBox profileCard = createProfileCard(profile);
            resultsContainer.getChildren().add(profileCard);
        }
    }

    private VBox createProfileCard(MatchProfile profile) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, lightgray, 10, 0, 0, 2);");

        HBox mainContent = new HBox(20);
        mainContent.setAlignment(Pos.CENTER_LEFT);

        // Profile Picture Thumbnail
        ImageView profileImageView = new ImageView();
        profileImageView.setFitWidth(120);
        profileImageView.setFitHeight(120);
        profileImageView.setPreserveRatio(true);

        if (profile.getPhotoPath() != null && !profile.getPhotoPath().isEmpty()) {
            try {
                File imageFile = new File(profile.getPhotoPath());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    profileImageView.setImage(image);
                    profileImageView.setStyle("-fx-background-color: transparent; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1); -fx-background-radius: 40;");
                } else {
                    setDefaultThumbnail(profileImageView);
                }
            } catch (Exception e) {
                setDefaultThumbnail(profileImageView);
            }
        } else {
            setDefaultThumbnail(profileImageView);
        }

        // Profile Details
        VBox detailsBox = new VBox(10);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(profile.getFullName() != null ? profile.getFullName() : "Anonymous");
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label ageLabel = new Label(profile.getAge() > 0 ? profile.getAge() + " years" : "Age N/A");
        ageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");

        header.getChildren().addAll(nameLabel, new Label(" | "), ageLabel);

        VBox details = new VBox(5);

        if (profile.getHeight() != null && !profile.getHeight().isEmpty()) {
            Label heightLabel = new Label("Height: " + profile.getHeight());
            heightLabel.setStyle("-fx-font-size: 14px;");
            details.getChildren().add(heightLabel);
        }

        if (profile.getMaritalStatus() != null && !profile.getMaritalStatus().isEmpty()) {
            Label maritalLabel = new Label("Marital Status: " + profile.getMaritalStatus());
            maritalLabel.setStyle("-fx-font-size: 14px;");
            details.getChildren().add(maritalLabel);
        }

        if (profile.getReligion() != null && !profile.getReligion().isEmpty()) {
            Label religionLabel = new Label("Religion: " + profile.getReligion());
            religionLabel.setStyle("-fx-font-size: 14px;");
            details.getChildren().add(religionLabel);
        }

        if (profile.getEducation() != null && !profile.getEducation().isEmpty()) {
            Label educationLabel = new Label("Education: " + profile.getEducation());
            educationLabel.setStyle("-fx-font-size: 14px;");
            details.getChildren().add(educationLabel);
        }

        if (profile.getOccupation() != null && !profile.getOccupation().isEmpty()) {
            Label occupationLabel = new Label("Occupation: " + profile.getOccupation());
            occupationLabel.setStyle("-fx-font-size: 14px;");
            details.getChildren().add(occupationLabel);
        }

        if (profile.getAnnualIncome() != null && !profile.getAnnualIncome().isEmpty()) {
            Label incomeLabel = new Label("Income: " + profile.getAnnualIncome());
            incomeLabel.setStyle("-fx-font-size: 14px;");
            details.getChildren().add(incomeLabel);
        }

        String location = profile.getLocation();
        if (!location.equals("Not specified")) {
            Label locationLabel = new Label("Location: " + location);
            locationLabel.setStyle("-fx-font-size: 14px;");
            details.getChildren().add(locationLabel);
        }

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        actionButtons.setPadding(new Insets(10, 0, 0, 0));

        Button viewProfileBtn = new Button("View Full Profile");
        viewProfileBtn.setStyle("-fx-background-color: slateblue; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
        viewProfileBtn.setOnAction(e -> handleViewProfile(profile));

        Button shortlistBtn = new Button("Add to Shortlist");
        if (currentUser != null) {
            boolean isShortlisted = shortlistDAO.isInShortlist(currentUser.getId(), profile.getUserId());
            if (isShortlisted) {
                shortlistBtn.setText("Shortlisted");
                shortlistBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5; -fx-opacity: 0.7;");
                shortlistBtn.setDisable(true);
            } else {
                shortlistBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
            }
        } else {
            shortlistBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
        }
        shortlistBtn.setOnAction(e -> handleShortlist(profile));

        Button sendRequestBtn = new Button("Send Request");
        if (currentUser != null) {
            // Check if current user sent a request to this profile
            String requestStatus = contactRequestDAO.getRequestStatus(currentUser.getId(), profile.getUserId());

            // Check if this profile sent a request to current user (reverse direction)
            String reverseRequestStatus = contactRequestDAO.getPendingRequestFromUser(currentUser.getId(), profile.getUserId());

            if (reverseRequestStatus != null && "pending".equals(reverseRequestStatus)) {
                // They sent you a request - show "Respond to Request" button
                sendRequestBtn.setText("Respond to Request");
                sendRequestBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
                sendRequestBtn.setOnAction(e -> handleRespondToRequest(profile));
            } else if (requestStatus != null) {
                // You sent them a request or are already connected
                switch (requestStatus) {
                    case "pending":
                        sendRequestBtn.setText("Request Pending");
                        sendRequestBtn.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
                        break;
                    case "accepted":
                        sendRequestBtn.setText("Connected");
                        sendRequestBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5; -fx-opacity: 0.7;");
                        sendRequestBtn.setDisable(true);
                        break;
                    case "rejected":
                        sendRequestBtn.setText("Request Rejected");
                        sendRequestBtn.setStyle("-fx-background-color: lightcoral; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
                        break;
                    default:
                        sendRequestBtn.setStyle("-fx-background-color: gold; -fx-text-fill: black; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
                }
                sendRequestBtn.setOnAction(e -> handleSendRequest(profile));
            } else {
                // No requests in either direction - can send new request
                sendRequestBtn.setStyle("-fx-background-color: gold; -fx-text-fill: black; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
                sendRequestBtn.setOnAction(e -> handleSendRequest(profile));
            }
        } else {
            sendRequestBtn.setStyle("-fx-background-color: gold; -fx-text-fill: black; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
            sendRequestBtn.setOnAction(e -> handleSendRequest(profile));
        }

        actionButtons.getChildren().addAll(viewProfileBtn, shortlistBtn, sendRequestBtn);

        detailsBox.getChildren().addAll(header, details, actionButtons);
        mainContent.getChildren().addAll(profileImageView, detailsBox);
        card.getChildren().add(mainContent);

        return card;
    }

    private void setDefaultThumbnail(ImageView imageView) {
        imageView.setStyle("-fx-background-color: slateblue; -fx-background-radius: 40; -fx-min-width: 80; -fx-min-height: 80;");
    }

    private void handleViewProfile(MatchProfile profile) {
        try {
            System.out.println("Opening full profile for: " + profile.getFullName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewProfile.fxml"));
            Parent viewProfile = loader.load();

            ViewProfileController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setProfile(profile);

            Scene scene = new Scene(viewProfile);
            Stage stage = (Stage) resultsContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("View Profile - " + profile.getFullName());

            System.out.println("Navigated to full profile view");
        } catch (Exception e) {
            System.err.println("Error opening profile view: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                "Failed to open profile view.\n" + e.getMessage());
        }
    }

    private void handleShortlist(MatchProfile profile) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session not found. Please login again.");
            return;
        }

        try {
            boolean alreadyShortlisted = shortlistDAO.isInShortlist(currentUser.getId(), profile.getUserId());

            if (alreadyShortlisted) {
                showAlert(Alert.AlertType.INFORMATION, "Already Shortlisted",
                    profile.getFullName() + "is already in your shortlist!");
                return;
            }

            boolean added = shortlistDAO.addToShortlist(currentUser.getId(), profile.getUserId());

            if (added) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                    profile.getFullName() + "has been added to your shortlist!");
                System.out.println("Profile added to shortlist: " + profile.getFullName());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to add " + profile.getFullName() + "to shortlist. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Error adding to shortlist: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                "An error occurred while adding to shortlist.\n" + e.getMessage());
        }
    }

    private void handleSendRequest(MatchProfile profile) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session not found. Please login again.");
            return;
        }

        // Check if they sent you a request first (reverse direction)
        String reverseRequestStatus = contactRequestDAO.getPendingRequestFromUser(currentUser.getId(), profile.getUserId());
        if (reverseRequestStatus != null && "pending".equals(reverseRequestStatus)) {
            showAlert(Alert.AlertType.INFORMATION, "Request Already Received",
                profile.getFullName() + " has already sent you a contact request!\n" +
                "Please go to 'Contact Requests' to accept or reject their request.\n\n" +
                "You cannot send a request while theirs is pending.");
            return;
        }

        String existingStatus = contactRequestDAO.getRequestStatus(currentUser.getId(), profile.getUserId());

        if (existingStatus != null) {
            if ("pending".equals(existingStatus)) {
                showAlert(Alert.AlertType.INFORMATION, "Request Already Sent",
                    "You have already sent a contact request to " + profile.getFullName() + ".\nStatus: Pending approval.");
                return;
            } else if ("accepted".equals(existingStatus)) {
                showAlert(Alert.AlertType.INFORMATION, "Already Connected",
                    "You are already connected with " + profile.getFullName() + "!\n" +
                    "You can view their contact details in your shortlist.");
                return;
            }
            else if ("rejected".equals(existingStatus)) {
                System.out.println("Previous request was rejected - allowing re-send");
            } else if ("cancelled".equals(existingStatus)) {
                System.out.println("Previous request was cancelled - allowing re-send");
            }
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Send Contact Request");
        confirmDialog.setHeaderText("Send request to " + profile.getFullName() + "?");

        String contentText = "Once accepted, you will be able to view their contact details.";
        if ("rejected".equals(existingStatus)) {
            contentText = "Your previous request was rejected. Do you want to send a new request?";
        } else if ("cancelled".equals(existingStatus)) {
            contentText = "You previously cancelled your request. Do you want to send a new request?";
        }
        confirmDialog.setContentText(contentText);

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            boolean success = contactRequestDAO.sendContactRequest(
                currentUser.getId(),
                profile.getUserId(),
                "Hi, I would like to connect with you!"
            );

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Contact request sent to " + profile.getFullName() + " successfully!\n" +
                    "You will be notified when they respond.");

                // Reload the matches to update button state
                loadAllMatches();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to send contact request.\nThis could be due to:\n" +
                    "- Network or database error\n" +
                    "- Request already exists\n" +
                    "Please try again later.");
            }
        }
    }

    private void handleRespondToRequest(MatchProfile profile) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session not found. Please login again.");
            return;
        }

        Alert infoDialog = new Alert(Alert.AlertType.INFORMATION);
        infoDialog.setTitle("Respond to Request");
        infoDialog.setHeaderText(profile.getFullName() + " has sent you a contact request!");
        infoDialog.setContentText("Please go to 'Contact Requests' page to accept or reject their request.");

        ButtonType goToRequestsBtn = new ButtonType("Go to Requests");
        ButtonType cancelBtn = new ButtonType("Later", ButtonBar.ButtonData.CANCEL_CLOSE);

        infoDialog.getButtonTypes().setAll(goToRequestsBtn, cancelBtn);

        if (infoDialog.showAndWait().get() == goToRequestsBtn) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ContactRequests.fxml"));
                Parent contactRequests = loader.load();

                com.matrimony.controller.ContactRequestsController controller = loader.getController();
                controller.setUser(currentUser);

                Scene scene = new Scene(contactRequests);
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Contact Requests");

                System.out.println("Navigated to Contact Requests page");
            } catch (Exception e) {
                System.err.println("Error navigating to Contact Requests: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Contact Requests page: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            System.out.println("Navigating back to dashboard...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent dashboard = loader.load();

            UserDashboardController controller = loader.getController();
            if (currentUser != null) {
                controller.setUser(currentUser);
            }

            Scene scene = new Scene(dashboard);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - " + currentUser.getFullName());

            System.out.println("Navigated back to dashboard");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard: " + e.getMessage());
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

