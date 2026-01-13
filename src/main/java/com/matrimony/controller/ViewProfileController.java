package com.matrimony.controller;

import com.matrimony.model.User;
import com.matrimony.model.MatchProfile;
import com.matrimony.model.Biodata;
import com.matrimony.dao.UserDAO;
import com.matrimony.dao.BiodataDAO;
import com.matrimony.dao.ContactRequestDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;

public class ViewProfileController {

    @FXML
    private Button backButton;

    @FXML
    private VBox contentContainer;

    private User currentUser;
    private MatchProfile profile;
    private UserDAO userDAO;
    private BiodataDAO biodataDAO;
    private ContactRequestDAO contactRequestDAO;

    @FXML
    public void initialize() {
        System.out.println("ViewProfileController initialized");
        userDAO = new UserDAO();
        biodataDAO = new BiodataDAO();
        contactRequestDAO = new ContactRequestDAO();
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    public void setProfile(MatchProfile profile) {
        this.profile = profile;
        if (profile != null) {
            loadProfileData();
        }
    }

    private void loadProfileData() {
        contentContainer.getChildren().clear();

        try {
            User profileUser = userDAO.getUserById(profile.getUserId());

            Biodata biodata = biodataDAO.getBiodataByUserId(profile.getUserId());

            boolean isRequestAccepted = contactRequestDAO.isRequestAccepted(currentUser.getId(), profile.getUserId());

            VBox headerSection = createHeaderSection(profileUser, biodata);

            VBox personalSection = createPersonalInfoSection(profileUser, biodata, isRequestAccepted);

            VBox professionalSection = createProfessionalInfoSection(biodata);

            VBox physicalSection = createPhysicalAttributesSection(biodata);

            VBox familySection = createFamilyInfoSection(biodata);

            // Add all sections
            contentContainer.getChildren().addAll(
                headerSection,
                new Separator(),
                personalSection,
                new Separator(),
                professionalSection,
                new Separator(),
                physicalSection
            );

            if (familySection != null) {
                contentContainer.getChildren().add(new Separator());
                contentContainer.getChildren().add(familySection);
            }

            System.out.println("✓ Profile data loaded for: " + profile.getFullName());

        } catch (Exception e) {
            System.err.println(" Error loading profile data: " + e.getMessage());
            e.printStackTrace();

            Label errorLabel = new Label("Error loading profile details.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            contentContainer.getChildren().add(errorLabel);
        }
    }

    private VBox createHeaderSection(User user, Biodata biodata) {
        VBox section = new VBox(15);
        section.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-background-radius: 10;");

        HBox profileRow = new HBox(30);
        profileRow.setAlignment(Pos.CENTER_LEFT);

        // Profile Picture
        ImageView profileImageView = new ImageView();
        profileImageView.setFitWidth(200);
        profileImageView.setFitHeight(200);
        profileImageView.setPreserveRatio(true);

        if (biodata != null && biodata.getPhotoPath() != null && !biodata.getPhotoPath().isEmpty()) {
            try {
                File imageFile = new File(biodata.getPhotoPath());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    profileImageView.setImage(image);
                    profileImageView.setStyle("-fx-background-color: transparent; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2); -fx-background-radius: 75;");
                } else {
                    setDefaultAvatar(profileImageView);
                }
            } catch (Exception e) {
                setDefaultAvatar(profileImageView);
            }
        } else {
            setDefaultAvatar(profileImageView);
        }

        // Profile Info
        VBox infoBox = new VBox(10);

        HBox headerRow = new HBox(20);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(user.getFullName());
        nameLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: slateblue;");

        Button contactRequestBtn = createContactRequestButton(user.getId());

        headerRow.getChildren().addAll(nameLabel, contactRequestBtn);

        HBox subInfo = new HBox(20);
        subInfo.setAlignment(Pos.CENTER_LEFT);

        if (biodata != null && biodata.getAge() > 0) {
            Label ageLabel = new Label(biodata.getAge() + " years");
            ageLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");
            subInfo.getChildren().add(ageLabel);
        }

        Label genderLabel = new Label(capitalizeFirst(user.getGender()));
        genderLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");
        subInfo.getChildren().add(genderLabel);

        infoBox.getChildren().addAll(headerRow, subInfo);

        profileRow.getChildren().addAll(profileImageView, infoBox);
        section.getChildren().add(profileRow);

        return section;
    }

    private void setDefaultAvatar(ImageView imageView) {
        imageView.setStyle("-fx-background-color: slateblue; -fx-background-radius: 75; -fx-min-width: 150; -fx-min-height: 150;");
    }

    private VBox createPersonalInfoSection(User user, Biodata biodata, boolean isRequestAccepted) {
        VBox section = new VBox(15);
        section.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-background-radius: 10;");

        Label sectionTitle = new Label("Personal Information");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(12);
        grid.setPadding(new Insets(10, 0, 0, 0));

        int row = 0;

        if (isRequestAccepted) {
            addDetailRow(grid, row++, "Email:", user.getEmail());
            addDetailRow(grid, row++, "Phone:", user.getPhone());
        } else {
            addDetailRow(grid, row++, "Email:", "Private (Send contact request)");
            addDetailRow(grid, row++, "Phone:", "Private (Send contact request)");
        }

        if (biodata != null) {
            if (biodata.getDateOfBirth() != null) {
                addDetailRow(grid, row++, "Date of Birth:", biodata.getDateOfBirth().toString());
            }
            if (biodata.getReligion() != null) {
                addDetailRow(grid, row++, "Religion:", biodata.getReligion());
            }
            if (biodata.getCaste() != null && !biodata.getCaste().isEmpty()) {
                addDetailRow(grid, row++, "Caste:", biodata.getCaste());
            }
            if (biodata.getMaritalStatus() != null) {
                addDetailRow(grid, row++, "Marital Status:", biodata.getMaritalStatus());
            }
        }

        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private VBox createProfessionalInfoSection(Biodata biodata) {
        VBox section = new VBox(15);
        section.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-background-radius: 10;");

        Label sectionTitle = new Label("Professional Information");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(12);
        grid.setPadding(new Insets(10, 0, 0, 0));

        int row = 0;
        boolean hasData = false;

        if (biodata != null) {
            if (biodata.getEducation() != null) {
                addDetailRow(grid, row++, "Education:", biodata.getEducation());
                hasData = true;
            }
            if (biodata.getOccupation() != null) {
                addDetailRow(grid, row++, "Occupation:", biodata.getOccupation());
                hasData = true;
            }
            if (biodata.getAnnualIncome() != null) {
                addDetailRow(grid, row++, "Annual Income:", biodata.getAnnualIncome());
                hasData = true;
            }
        }

        if (!hasData) {
            Label noData = new Label("No professional information available");
            noData.setStyle("-fx-font-size: 14px; -fx-text-fill: gray; -fx-font-style: italic;");
            section.getChildren().addAll(sectionTitle, noData);
        } else {
            section.getChildren().addAll(sectionTitle, grid);
        }

        return section;
    }

    private VBox createPhysicalAttributesSection(Biodata biodata) {
        VBox section = new VBox(15);
        section.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-background-radius: 10;");

        Label sectionTitle = new Label("Physical Attributes");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(12);
        grid.setPadding(new Insets(10, 0, 0, 0));

        int row = 0;
        boolean hasData = false;

        if (biodata != null) {
            if (biodata.getHeight() != null) {
                addDetailRow(grid, row++, "Height:", biodata.getHeight());
                hasData = true;
            }
            if (biodata.getWeight() != null && !biodata.getWeight().isEmpty()) {
                addDetailRow(grid, row++, "Weight:", biodata.getWeight());
                hasData = true;
            }
            if (biodata.getComplexion() != null && !biodata.getComplexion().isEmpty()) {
                addDetailRow(grid, row++, "Complexion:", biodata.getComplexion());
                hasData = true;
            }
        }

        if (!hasData) {
            Label noData = new Label("No physical attributes information available");
            noData.setStyle("-fx-font-size: 14px; -fx-text-fill: gray; -fx-font-style: italic;");
            section.getChildren().addAll(sectionTitle, noData);
        } else {
            section.getChildren().addAll(sectionTitle, grid);
        }

        return section;
    }

    private VBox createFamilyInfoSection(Biodata biodata) {
        if (biodata == null) return null;

        boolean hasData = (biodata.getFatherName() != null && !biodata.getFatherName().isEmpty()) ||
                         (biodata.getMotherName() != null && !biodata.getMotherName().isEmpty()) ||
                         (biodata.getCity() != null && !biodata.getCity().isEmpty());

        if (!hasData) return null;

        VBox section = new VBox(15);
        section.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-background-radius: 10;");

        Label sectionTitle = new Label("Family & Location");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(12);
        grid.setPadding(new Insets(10, 0, 0, 0));

        int row = 0;

        if (biodata.getFatherName() != null && !biodata.getFatherName().isEmpty()) {
            addDetailRow(grid, row++, "Father's Name:", biodata.getFatherName());
        }
        if (biodata.getMotherName() != null && !biodata.getMotherName().isEmpty()) {
            addDetailRow(grid, row++, "Mother's Name:", biodata.getMotherName());
        }
        if (biodata.getCity() != null && !biodata.getCity().isEmpty()) {
            addDetailRow(grid, row++, "City:", biodata.getCity());
        }

        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");

        Label valueNode = new Label(value != null ? value : "Not specified");
        valueNode.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        valueNode.setWrapText(true);
        valueNode.setMaxWidth(400);

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private Button createContactRequestButton(int profileUserId) {
        Button contactRequestBtn = new Button();

        String existingStatus = contactRequestDAO.getRequestStatus(currentUser.getId(), profileUserId);
        boolean isRequestAccepted = contactRequestDAO.isRequestAccepted(currentUser.getId(), profileUserId);

        // Check if they sent you a request (reverse direction)
        String reverseRequestStatus = contactRequestDAO.getPendingRequestFromUser(currentUser.getId(), profileUserId);

        if (isRequestAccepted || "accepted".equals(existingStatus)) {
            contactRequestBtn.setText("Connected");
            contactRequestBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10 20; " +
                    "-fx-background-radius: 5; -fx-font-size: 14px; -fx-opacity: 0.7;");
            contactRequestBtn.setDisable(true);
        } else if (reverseRequestStatus != null && "pending".equals(reverseRequestStatus)) {
            // They sent you a request - show respond button
            contactRequestBtn.setText("Respond to Request");
            contactRequestBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10 20; " +
                    "-fx-background-radius: 5; -fx-font-size: 14px; -fx-cursor: hand;");
            contactRequestBtn.setOnAction(e -> handleRespondToRequest(profileUserId));
        } else if ("pending".equals(existingStatus)) {
            contactRequestBtn.setText("Request Pending");
            contactRequestBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-padding: 10 20; " +
                    "-fx-background-radius: 5; -fx-font-size: 14px; -fx-opacity: 0.7;");
            contactRequestBtn.setDisable(true);
        } else {
            contactRequestBtn.setText("Send Contact Request");
            contactRequestBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10 20; " +
                    "-fx-background-radius: 5; -fx-font-size: 14px; -fx-cursor: hand;");
            contactRequestBtn.setOnAction(e -> handleSendRequest(profileUserId));
        }

        return contactRequestBtn;
    }

    private void handleSendRequest(int profileUserId) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session not found. Please login again.");
            return;
        }

        User profileUser = userDAO.getUserById(profileUserId);
        String profileName = profileUser != null ? profileUser.getFullName() : "this user";

        // Check if they sent you a request first (reverse direction)
        String reverseRequestStatus = contactRequestDAO.getPendingRequestFromUser(currentUser.getId(), profileUserId);
        if (reverseRequestStatus != null && "pending".equals(reverseRequestStatus)) {
            showAlert(Alert.AlertType.INFORMATION, "Request Already Received",
                profileName + " has already sent you a contact request!\n" +
                "Please go to 'Contact Requests' to accept or reject their request.\n\n" +
                "You cannot send a request while theirs is pending.");
            return;
        }

        String existingStatus = contactRequestDAO.getRequestStatus(currentUser.getId(), profileUserId);

        if (existingStatus != null) {
            if ("pending".equals(existingStatus)) {
                showAlert(Alert.AlertType.INFORMATION, "Request Already Sent",
                    "You have already sent a contact request to " + profileName + ".\nStatus: Pending approval.");
                return;
            } else if ("accepted".equals(existingStatus)) {
                showAlert(Alert.AlertType.INFORMATION, "Already Connected",
                    "You are already connected with " + profileName + "!\n" +
                    "You can view their contact details now.");
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
        confirmDialog.setHeaderText("Send request to " + profileName + "?");

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
                profileUserId,
                "Hi, I would like to connect with you!"
            );

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Contact request sent to " + profileName + " successfully!\n" +
                    "You will be notified when they respond.");

                loadProfileData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to send contact request.\nThis could be due to:\n" +
                    "- Network or database error\n" +
                    "- Request already exists\n" +
                    "Please try again.");
            }
        }
    }

    private void handleRespondToRequest(int profileUserId) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session not found. Please login again.");
            return;
        }

        User profileUser = userDAO.getUserById(profileUserId);
        String profileName = profileUser != null ? profileUser.getFullName() : "this user";

        Alert infoDialog = new Alert(Alert.AlertType.INFORMATION);
        infoDialog.setTitle("Respond to Request");
        infoDialog.setHeaderText(profileName + " has sent you a contact request!");
        infoDialog.setContentText("Please go to 'Contact Requests' page to accept or reject their request.");

        ButtonType goToRequestsBtn = new ButtonType("Go to Requests");
        ButtonType cancelBtn = new ButtonType("Later", ButtonBar.ButtonData.CANCEL_CLOSE);

        infoDialog.getButtonTypes().setAll(goToRequestsBtn, cancelBtn);

        if (infoDialog.showAndWait().get() == goToRequestsBtn) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ContactRequests.fxml"));
                Parent contactRequests = loader.load();

                ContactRequestsController controller = loader.getController();
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchMatches.fxml"));
            Parent searchMatches = loader.load();

            SearchMatchesController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(searchMatches);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Search Matches");

            System.out.println(" Navigated back to Search Matches");
        } catch (Exception e) {
            System.err.println(" Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

