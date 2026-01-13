package com.matrimony.controller;

import com.matrimony.model.ContactRequest;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.matrimony.model.User;
import com.matrimony.model.Biodata;
import com.matrimony.dao.ContactRequestDAO;
import com.matrimony.dao.ShortlistDAO;
import com.matrimony.dao.BiodataDAO;
import javafx.application.Platform;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserDashboardController {

    @FXML
    private Label userNameLabel;

    @FXML
    private ImageView navProfilePicture;

    @FXML
    private Label profileCompletionLabel;

    @FXML
    private ProgressBar profileProgressBar;

    @FXML
    private Label profileCompletionMessage;

    @FXML
    private Label shortlistCount;

    @FXML
    private Label pendingRequestCount;

    @FXML
    private Label contactRequestCount;

    @FXML
    private Label connectedPeopleCount;

    @FXML
    private Label profileViewsCount;

    @FXML
    private javafx.scene.control.Button checkShortlistBtn;

    @FXML
    private javafx.scene.control.Button checkPendingBtn;

    @FXML
    private javafx.scene.control.Button checkContactBtn;

    @FXML
    private javafx.scene.control.Button checkConnectedBtn;

    @FXML
    private Label fullNameInfo;

    @FXML
    private Label emailInfo;

    @FXML
    private Label phoneInfo;

    @FXML
    private Label genderInfo;

    @FXML
    private Label memberSinceInfo;

    @FXML
    private Label accountStatusInfo;

    @FXML
    private VBox activityContainer;

    private User currentUser;
    private ContactRequestDAO contactRequestDAO;
    private ShortlistDAO shortlistDAO;

    @FXML
    public void initialize() {
        System.out.println("UserDashboard initialized");

        shortlistDAO = new ShortlistDAO();
        contactRequestDAO = new ContactRequestDAO();

        System.out.println("DAOs initialized successfully");

        if (shortlistCount != null) {
            updateStatistics(0, 0, 0, 0);
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            try {
                if (userNameLabel != null) {
                    userNameLabel.setText("Welcome, "+ user.getFullName());
                }

                // Load profile picture in navbar
                loadNavProfilePicture(user);

                updateUserInfo(user);
                calculateProfileCompletion(user);

                try {
                    loadShortlistCount();
                } catch (Exception ex) {
                    System.err.println("Warning: Failed to load shortlist count: " + ex.getMessage());
                    if (shortlistCount != null) shortlistCount.setText("0");
                }

                try {
                    loadRequestCounts();
                } catch (Exception ex) {
                    System.err.println("Warning: Failed to load request counts: " + ex.getMessage());
                    if (pendingRequestCount != null) pendingRequestCount.setText("0");
                    if (contactRequestCount != null) contactRequestCount.setText("0");
                }

                try {
                    loadConnectedPeopleCount();
                } catch (Exception ex) {
                    System.err.println("Warning: Failed to load connected people count: " + ex.getMessage());
                    if (connectedPeopleCount != null) connectedPeopleCount.setText("0");
                }

                System.out.println("User dashboard loaded successfully for: " + user.getFullName());
            } catch (Exception e) {
                System.err.println("Error setting user in dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Attempted to set null user in dashboard");
        }
    }

    private void loadNavProfilePicture(User user) {
        if (navProfilePicture == null) {
            System.out.println("navProfilePicture ImageView not found in FXML");
            return;
        }

        try {
            BiodataDAO biodataDAO = new BiodataDAO();
            Biodata biodata = biodataDAO.getBiodataByUserId(user.getId());

            if (biodata != null && biodata.getPhotoPath() != null && !biodata.getPhotoPath().isEmpty()) {
                File imageFile = new File(biodata.getPhotoPath());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    navProfilePicture.setImage(image);
                    // Style the image with circular crop and border
                    navProfilePicture.setStyle(
                        "-fx-background-color: white; " +
                        "-fx-border-color: rgba(102, 126, 234, 0.5); " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 16; " +
                        "-fx-background-radius: 16; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);"
                    );
                    System.out.println("Profile picture loaded in navbar: " + biodata.getPhotoPath());
                } else {
                    setDefaultNavProfilePicture();
                }
            } else {
                setDefaultNavProfilePicture();
            }
        } catch (Exception e) {
            System.err.println("Error loading nav profile picture: " + e.getMessage());
            setDefaultNavProfilePicture();
        }
    }

    private void setDefaultNavProfilePicture() {
        if (navProfilePicture == null) return;

        try {
            // Try to load a default avatar image from resources
            String defaultImagePath = getClass().getResource("/images/default-avatar.png") != null
                ? getClass().getResource("/images/default-avatar.png").toExternalForm()
                : null;

            if (defaultImagePath != null) {
                Image defaultImage = new Image(defaultImagePath);
                navProfilePicture.setImage(defaultImage);
            } else {
                // If no default image available, set a placeholder style
                navProfilePicture.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); " +
                    "-fx-border-color: white; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 16; " +
                    "-fx-background-radius: 16;"
                );
            }
        } catch (Exception ex) {
            System.err.println("Error setting default nav profile picture: " + ex.getMessage());
        }
    }

    private void updateUserInfo(User user) {
        try {
            if (fullNameInfo != null) fullNameInfo.setText(user.getFullName());
            if (emailInfo != null) emailInfo.setText(user.getEmail());
            if (phoneInfo != null) phoneInfo.setText(user.getPhone());
            if (genderInfo != null) genderInfo.setText(capitalizeFirst(user.getGender()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            if (memberSinceInfo != null) memberSinceInfo.setText(LocalDateTime.now().format(formatter));

            if (accountStatusInfo != null) {
                accountStatusInfo.setText("Active ");
                accountStatusInfo.setStyle("-fx-text-fill: #4CAF50;");
            }
        } catch (Exception e) {
            System.err.println("Warning: Some user info fields not found in FXML: " + e.getMessage());
        }
    }

    private void calculateProfileCompletion(User user) {
        try {
            int completionPercentage = 0;
            int totalFields = 5;
            int completedFields = 0;

            if (user.getFullName() != null && !user.getFullName().isEmpty()) completedFields++;
            if (user.getEmail() != null && !user.getEmail().isEmpty()) completedFields++;
            if (user.getPhone() != null && !user.getPhone().isEmpty()) completedFields++;
            if (user.getGender() != null && !user.getGender().isEmpty()) completedFields++;
            if (user.getPassword() != null && !user.getPassword().isEmpty()) completedFields++;

            completionPercentage = (completedFields * 100) / totalFields;

            if (profileCompletionLabel != null) profileCompletionLabel.setText(completionPercentage + "%");
            if (profileProgressBar != null) profileProgressBar.setProgress(completionPercentage / 100.0);

            if (profileCompletionMessage != null) {
                if (completionPercentage == 100) {
                    profileCompletionMessage.setText("Your profile is complete! Great job!");
                    profileCompletionMessage.setStyle("-fx-text-fill: #4CAF50;");
                } else if (completionPercentage >= 60) {
                    profileCompletionMessage.setText("Almost there! Complete your profile to increase your chances.");
                    profileCompletionMessage.setStyle("-fx-text-fill: #FF9800;");
                } else {
                    profileCompletionMessage.setText("Your profile needs more information. Please complete it.");
                    profileCompletionMessage.setStyle("-fx-text-fill: #F44336;");
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Profile completion calculation failed: " + e.getMessage());
        }
    }

    private void updateStatistics(int shortlist, int pending, int contacts, int views) {
        if (shortlistCount != null) shortlistCount.setText(String.valueOf(shortlist));
        if (pendingRequestCount != null) pendingRequestCount.setText(String.valueOf(pending));
        if (contactRequestCount != null) contactRequestCount.setText(String.valueOf(contacts));
        if (profileViewsCount != null) profileViewsCount.setText(String.valueOf(views));
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        try {
            System.out.println("Logout clicked");
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Navigated back to login page");
        } catch (Exception e) {
            System.err.println("Error loading login page: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to logout. Please try again.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleHome() {
        System.out.println("Home clicked");
    }

    @FXML
    private void handleMatches() {
        System.out.println("Matches clicked");
        showAlert("Matches", "Finding your perfect matches... This feature is coming soon!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleMessages() {
        System.out.println("Messages clicked");
        showAlert("Messages", "Messaging feature will be available soon!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleProfile() {
        try {
            System.out.println("My Profile clicked - Navigating to Profile page");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MyProfile.fxml"));
            Parent profilePage = loader.load();

            MyProfileController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(profilePage);
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("My Profile - " + currentUser.getFullName());

            System.out.println("Navigated to My Profile page");
        } catch (Exception e) {
            System.err.println("Error loading profile page: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load profile page.\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSearchMatches() {
        System.out.println("Search Matches clicked - Navigating to Search Matches page");
        navigateToSearchMatchesPage();
    }

    private void navigateToSearchMatchesPage() {
        try {
            System.out.println("Loading Search Matches page...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchMatches.fxml"));
            Parent searchPage = loader.load();
            System.out.println("SearchMatches.fxml loaded");

            SearchMatchesController controller = loader.getController();
            controller.setUser(currentUser);
            System.out.println("User set in SearchMatchesController");

            Scene scene = new Scene(searchPage);
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Search Matches - " + currentUser.getFullName());

            System.out.println("Navigated to Search Matches page!");

        } catch (Exception e) {
            System.err.println("Failed to load Search Matches page:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to open Search Matches page.\n\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUploadBiodata() {
        System.out.println("Upload Biodata clicked - Navigating to Biodata Form page");
        navigateToBiodataPage();
    }

    @FXML
    private void handleEditProfile() {
        System.out.println("Edit Profile clicked");
        loadBiodataForm();
    }

    private void loadBiodataForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BiodataForm.fxml"));
            Parent biodataForm = loader.load();

            BiodataController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(biodataForm);
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Complete Your Biodata - " + currentUser.getFullName());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load biodata form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void navigateToBiodataPage() {
        try {
            System.out.println("Navigating to Biodata Form page...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BiodataForm.fxml"));
            Parent biodataPage = loader.load();
            System.out.println("BiodataForm.fxml loaded");

            BiodataController controller = loader.getController();
            controller.setUser(currentUser);
            System.out.println("User set in BiodataController");

            Scene biodataScene = new Scene(biodataPage);
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(biodataScene);
            stage.setTitle("Upload Biodata - " + currentUser.getFullName());

            System.out.println("Page transition complete!");

        } catch (Exception e) {
            System.err.println("Failed to load biodata form page:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to open biodata form.\n\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUploadPhoto() {
        System.out.println("Upload Photo clicked");
        loadBiodataForm();
    }

    @FXML
    private void handleSettings() {
        System.out.println("Settings clicked");
        showAlert("Settings", "Account settings will be available soon!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleCompleteProfile() {
        System.out.println("Complete Profile clicked");
    }

    @FXML
    private void handleViewShortlist() {
        System.out.println("View Shortlist clicked");
        handleCheckShortlist();
    }

    @FXML
    private void handleViewPendingRequests() {
        System.out.println("View Pending Requests clicked");
        showAlert("Pending Requests", "You have no pending requests at the moment.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleViewContactRequests() {
        System.out.println("View Contact Requests clicked");
        showAlert("Contact Requests", "No contact requests yet. Connect with matches to see requests here!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleViewProfileVisitors() {
        System.out.println("View Profile Visitors clicked");
        showAlert("Profile Visitors", "See who has viewed your profile. This feature is coming soon!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleMatchPreferences() {
        System.out.println("Match Preferences clicked");
        showAlert("Match Preferences", "Set your ideal partner preferences here. Feature coming soon!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handlePrivacySettings() {
        System.out.println("Privacy Settings clicked");
        showAlert("Privacy Settings", "Control who can see your profile and contact you. Feature under development!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleBlockedUsers() {
        System.out.println("Blocked Users clicked");
        showAlert("Blocked Users", "Manage blocked users here. This feature will be available soon!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleHelp() {
        System.out.println("Help clicked");
        showAlert("Help & Support",
            "Need help? Contact us:\n\n" +
            "Email: support@biyeshadi.com\n" +
            "Phone: +880-XXX-XXXXXX\n" +
            "Available: 24/7",
            Alert.AlertType.INFORMATION);
    }


    private void loadShortlistCount() {
        if (currentUser == null || shortlistDAO == null) {
            return;
        }

        try {
            int count = shortlistDAO.getShortlistCount(currentUser.getId());

            if (shortlistCount != null) {
                shortlistCount.setText(String.valueOf(count));
            }

            System.out.println("Shortlist count loaded: " + count);

        } catch (Exception e) {
            System.err.println("Error loading shortlist count: " + e.getMessage());
            e.printStackTrace();
            if (shortlistCount != null) {
                shortlistCount.setText("0");
            }
        }
    }


    private void loadRequestCounts() {
        if (currentUser == null || contactRequestDAO == null) {
            System.out.println("Cannot load request counts - user or DAO is null");
            if (pendingRequestCount != null) pendingRequestCount.setText("0");
            if (contactRequestCount != null) contactRequestCount.setText("0");
            return;
        }

        try {
            System.out.println("Loading request counts for user: " + currentUser.getId());

            List<ContactRequest> sentRequests = contactRequestDAO.getSentRequests(currentUser.getId());
            int pendingCount = 0;
            for (com.matrimony.model.ContactRequest request : sentRequests) {
                if ("pending".equals(request.getStatus())) {
                    pendingCount++;
                }
            }

            List<com.matrimony.model.ContactRequest> receivedRequests = contactRequestDAO.getReceivedRequests(currentUser.getId());
            int contactCount = 0;
            for (com.matrimony.model.ContactRequest request : receivedRequests) {
                if ("pending".equals(request.getStatus())) {
                    contactCount++;
                }
            }

            if (pendingRequestCount != null) {
                pendingRequestCount.setText(String.valueOf(pendingCount));
            }

            if (contactRequestCount != null) {
                contactRequestCount.setText(String.valueOf(contactCount));
            }

            System.out.println("Request counts loaded - Pending: " + pendingCount + ", Contact: " + contactCount);

        } catch (Exception e) {
            System.err.println("Error loading request counts: " + e.getMessage());
            e.printStackTrace();
            if (pendingRequestCount != null) pendingRequestCount.setText("0");
            if (contactRequestCount != null) contactRequestCount.setText("0");
        }
    }


    private void loadConnectedPeopleCount() {
        if (currentUser == null || contactRequestDAO == null) {
            System.out.println("Cannot load connected people count - user or DAO is null");
            if (connectedPeopleCount != null) connectedPeopleCount.setText("0");
            return;
        }

        try {
            System.out.println("Loading connected people count for user: " + currentUser.getId());

            // Get all sent requests with accepted status
            List<ContactRequest> sentRequests = contactRequestDAO.getSentRequests(currentUser.getId());
            int connectedCount = 0;
            for (ContactRequest request : sentRequests) {
                if ("accepted".equals(request.getStatus())) {
                    connectedCount++;
                }
            }

            // Get all received requests with accepted status
            List<ContactRequest> receivedRequests = contactRequestDAO.getReceivedRequests(currentUser.getId());
            for (ContactRequest request : receivedRequests) {
                if ("accepted".equals(request.getStatus())) {
                    connectedCount++;
                }
            }

            if (connectedPeopleCount != null) {
                connectedPeopleCount.setText(String.valueOf(connectedCount));
            }

            System.out.println("✓ Connected people count loaded: " + connectedCount);

        } catch (Exception e) {
            System.err.println(" Error loading connected people count: " + e.getMessage());
            e.printStackTrace();
            if (connectedPeopleCount != null) connectedPeopleCount.setText("0");
        }
    }

    /**
     * Handle click on Shortlist check button
     */
    @FXML
    private void handleCheckShortlist() {
        if (currentUser == null) {
            showAlert("Error", "User session not found. Please login again.", Alert.AlertType.ERROR);
            return;
        }

        try {
            System.out.println("Opening Shortlist view for user: " + currentUser.getFullName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Shortlist.fxml"));
            Parent shortlistPage = loader.load();

            ShortlistController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(shortlistPage);
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("My Shortlist - " + currentUser.getFullName());

            System.out.println("Navigated to Shortlist page");

        } catch (Exception e) {
            System.err.println("Error opening Shortlist page: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to open Shortlist page.\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCheckPendingRequests() {
        if (currentUser == null) {
            showAlert("Error", "User session not found. Please login again.", Alert.AlertType.ERROR);
            return;
        }

        try {
            System.out.println("Opening Pending Requests view for user: " + currentUser.getFullName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PendingRequests.fxml"));
            Parent requestsPage = loader.load();

            PendingRequestsController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(requestsPage);
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Pending Requests - " + currentUser.getFullName());

            System.out.println("Navigated to Pending Requests page");

        } catch (Exception e) {
            System.err.println("Error opening Pending Requests page: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to open Pending Requests page.\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void handleCheckContactRequests() {
        if (currentUser == null) {
            showAlert("Error", "User session not found. Please login again.", Alert.AlertType.ERROR);
            return;
        }

        try {
            System.out.println("Opening Contact Requests view for user: " + currentUser.getFullName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ContactRequests.fxml"));
            Parent requestsPage = loader.load();

            ContactRequestsController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(requestsPage);
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Contact Requests - " + currentUser.getFullName());

            System.out.println("Navigated to Contact Requests page");

        } catch (Exception e) {
            System.err.println("Error opening Contact Requests page: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to open Contact Requests page.\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void handleCheckConnectedPeople() {
        if (currentUser == null) {
            showAlert("Error", "User session not found. Please login again.", Alert.AlertType.ERROR);
            return;
        }

        try {
            System.out.println("✓ Opening Connected People view for user: " + currentUser.getFullName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConnectedPeople.fxml"));
            Parent connectedPage = loader.load();

            ConnectedPeopleController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(connectedPage);
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Connected People - " + currentUser.getFullName());

            System.out.println("✓ Navigated to Connected People page");

        } catch (Exception e) {
            System.err.println(" Error opening Connected People page: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to open Connected People page.\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void refreshRequestCounts() {
        loadRequestCounts();
    }

    public void refreshShortlistCount() {
        loadShortlistCount();
    }


    public void refreshAllCounts() {
        loadShortlistCount();
        loadRequestCounts();
        loadConnectedPeopleCount();
    }
}

