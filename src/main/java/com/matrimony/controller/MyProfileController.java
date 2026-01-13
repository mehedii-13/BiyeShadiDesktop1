package com.matrimony.controller;

import com.matrimony.dao.BiodataDAO;
import com.matrimony.model.Biodata;
import com.matrimony.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MyProfileController {

    @FXML private ImageView profileImageView;
    @FXML private Button uploadPhotoButton;
    @FXML private Button removePhotoButton;
    @FXML private Label photoStatusLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label genderLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private VBox noDataMessage;

    @FXML private VBox biodataSection;
    @FXML private VBox educationSection;
    @FXML private VBox locationSection;
    @FXML private Label ageLabel;
    @FXML private Label heightLabel;
    @FXML private Label religionLabel;
    @FXML private Label maritalStatusLabel;
    @FXML private Label educationLabel;
    @FXML private Label occupationLabel;
    @FXML private Label incomeLabel;
    @FXML private Label cityLabel;
    @FXML private Label stateLabel;
    @FXML private Label countryLabel;

    private User currentUser;
    private BiodataDAO biodataDAO;

    @FXML
    public void initialize() {
        System.out.println("MyProfile initialized");
        biodataDAO = new BiodataDAO();

        // Set default profile image
        setDefaultProfileImage();
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            loadProfileData();
        }
    }

    private void loadProfileData() {
        try {
            if (fullNameLabel != null) fullNameLabel.setText(currentUser.getFullName());
            if (genderLabel != null) genderLabel.setText(capitalizeFirst(currentUser.getGender()));
            if (emailLabel != null) emailLabel.setText(currentUser.getEmail());
            if (phoneLabel != null) phoneLabel.setText(currentUser.getPhone());

            System.out.println("Basic profile data loaded for: " + currentUser.getFullName());

            Biodata biodata = biodataDAO.getBiodataByUserId(currentUser.getId());

            if (biodata != null) {
                System.out.println("Biodata found, displaying information...");
                displayBiodataInfo(biodata);
                showBiodataSections();

                // Load profile picture
                loadProfilePicture(biodata);
            } else {
                System.out.println("No biodata found for user");
                hideBiodataSections();
                showNoDataMessage();
                setDefaultProfileImage();
            }

        } catch (Exception e) {
            System.err.println("Error loading profile data: " + e.getMessage());
            e.printStackTrace();
            hideBiodataSections();
        }
    }

    private void displayBiodataInfo(Biodata biodata) {
        try {
            if (ageLabel != null) {
                ageLabel.setText(biodata.getAge() > 0 ? biodata.getAge() + "years" : "N/A");
            }
            if (heightLabel != null) {
                heightLabel.setText(biodata.getHeight() != null ? biodata.getHeight() : "N/A");
            }
            if (religionLabel != null) {
                religionLabel.setText(biodata.getReligion() != null ? biodata.getReligion() : "N/A");
            }
            if (maritalStatusLabel != null) {
                maritalStatusLabel.setText(biodata.getMaritalStatus() != null ? biodata.getMaritalStatus() : "N/A");
            }

            if (educationLabel != null) {
                educationLabel.setText(biodata.getEducation() != null ? biodata.getEducation() : "N/A");
            }
            if (occupationLabel != null) {
                occupationLabel.setText(biodata.getOccupation() != null ? biodata.getOccupation() : "N/A");
            }
            if (incomeLabel != null) {
                incomeLabel.setText(biodata.getAnnualIncome() != null ? biodata.getAnnualIncome() : "N/A");
            }

            if (cityLabel != null) {
                cityLabel.setText(biodata.getCity() != null ? biodata.getCity() : "N/A");
            }
            if (stateLabel != null) {
                stateLabel.setText(biodata.getState() != null ? biodata.getState() : "N/A");
            }
            if (countryLabel != null) {
                countryLabel.setText(biodata.getCountry() != null ? biodata.getCountry() : "N/A");
            }

            System.out.println("Biodata information displayed successfully");

        } catch (Exception e) {
            System.err.println("Error displaying biodata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showBiodataSections() {
        if (biodataSection != null) {
            biodataSection.setManaged(true);
            biodataSection.setVisible(true);
        }
        if (educationSection != null) {
            educationSection.setManaged(true);
            educationSection.setVisible(true);
        }
        if (locationSection != null) {
            locationSection.setManaged(true);
            locationSection.setVisible(true);
        }
        if (noDataMessage != null) {
            noDataMessage.setManaged(false);
            noDataMessage.setVisible(false);
        }
    }

    private void hideBiodataSections() {
        if (biodataSection != null) {
            biodataSection.setManaged(false);
            biodataSection.setVisible(false);
        }
        if (educationSection != null) {
            educationSection.setManaged(false);
            educationSection.setVisible(false);
        }
        if (locationSection != null) {
            locationSection.setManaged(false);
            locationSection.setVisible(false);
        }
    }

    private void showNoDataMessage() {
        if (noDataMessage != null) {
            noDataMessage.setManaged(true);
            noDataMessage.setVisible(true);
        }
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private void setDefaultProfileImage() {
        try {
            // Try to use a default avatar image if available
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-avatar.png"));
            if (profileImageView != null) {
                profileImageView.setImage(defaultImage);
                profileImageView.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 100;");
            }
        } catch (Exception e) {
            // If default image not found, use a placeholder
            if (profileImageView != null) {
                try {
                    // Create a simple colored circle as placeholder
                    String placeholderUrl = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSIxMDAiIGN5PSIxMDAiIHI9IjEwMCIgZmlsbD0iI0U5MUU2MyIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjgwIiBmaWxsPSJ3aGl0ZSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPjwvdGV4dD48L3N2Zz4=";
                    profileImageView.setStyle("-fx-background-color: #E91E63; -fx-background-radius: 100; -fx-min-width: 200; -fx-min-height: 200;");
                } catch (Exception ex) {
                    System.err.println("Error setting placeholder image: " + ex.getMessage());
                }
            }
        }
    }

    private void loadProfilePicture(Biodata biodata) {
        if (biodata.getPhotoPath() != null && !biodata.getPhotoPath().isEmpty()) {
            try {
                File imageFile = new File(biodata.getPhotoPath());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    profileImageView.setImage(image);
                    profileImageView.setStyle("-fx-background-color: transparent; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");
                    photoStatusLabel.setText("Profile picture uploaded");
                    removePhotoButton.setVisible(true);
                    System.out.println("Profile picture loaded: " + biodata.getPhotoPath());
                } else {
                    System.out.println("Photo file not found: " + biodata.getPhotoPath());
                    setDefaultProfileImage();
                    photoStatusLabel.setText("Click 'Upload Photo' to add your profile picture");
                    removePhotoButton.setVisible(false);
                }
            } catch (Exception e) {
                System.err.println("Error loading profile picture: " + e.getMessage());
                setDefaultProfileImage();
                photoStatusLabel.setText("Click 'Upload Photo' to add your profile picture");
                removePhotoButton.setVisible(false);
            }
        } else {
            setDefaultProfileImage();
            photoStatusLabel.setText("Click 'Upload Photo' to add your profile picture");
            removePhotoButton.setVisible(false);
        }
    }

    @FXML
    private void handleUploadPhoto() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Picture");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );

            Stage stage = (Stage) uploadPhotoButton.getScene().getWindow();
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                // Create uploads directory if it doesn't exist
                Path uploadsDir = Paths.get("uploads/profile-pictures");
                Files.createDirectories(uploadsDir);

                // Generate unique filename
                String fileName = "user_" + currentUser.getId() + "_" + System.currentTimeMillis()
                                + selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                Path targetPath = uploadsDir.resolve(fileName);

                // Copy file to uploads directory
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Update database
                Biodata biodata = biodataDAO.getBiodataByUserId(currentUser.getId());
                if (biodata != null) {
                    biodata.setPhotoPath(targetPath.toString());
                    biodataDAO.updateBiodata(biodata);
                } else {
                    // Create new biodata if doesn't exist
                    biodata = new Biodata();
                    biodata.setUserId(currentUser.getId());
                    biodata.setPhotoPath(targetPath.toString());
                    biodataDAO.saveBiodata(biodata);
                }

                // Display the uploaded image
                Image image = new Image(targetPath.toUri().toString());
                profileImageView.setImage(image);
                profileImageView.setStyle("-fx-background-color: transparent; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");
                photoStatusLabel.setText("Profile picture uploaded successfully!");
                photoStatusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                removePhotoButton.setVisible(true);

                System.out.println("Profile picture uploaded: " + targetPath);

                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile picture uploaded successfully!");

            }
        } catch (Exception e) {
            System.err.println("Error uploading photo: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to upload photo: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemovePhoto() {
        try {
            Biodata biodata = biodataDAO.getBiodataByUserId(currentUser.getId());
            if (biodata != null && biodata.getPhotoPath() != null) {
                // Delete the file
                File photoFile = new File(biodata.getPhotoPath());
                if (photoFile.exists()) {
                    photoFile.delete();
                }

                // Update database
                biodata.setPhotoPath(null);
                biodataDAO.updateBiodata(biodata);

                // Reset to default image
                setDefaultProfileImage();
                photoStatusLabel.setText("Profile picture removed");
                photoStatusLabel.setStyle("-fx-text-fill: #E91E63; -fx-font-size: 12px;");
                removePhotoButton.setVisible(false);

                System.out.println("Profile picture removed");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile picture removed successfully!");
            }
        } catch (Exception e) {
            System.err.println("Error removing photo: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove photo: " + e.getMessage());
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
    private void handleBackToDashboard() {
        try {
            System.out.println("Navigating back to dashboard...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent dashboard = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(dashboard);
            Stage stage = (Stage) fullNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("User Dashboard");

            System.out.println("Navigated back to dashboard");
        } catch (Exception e) {
            System.err.println("Error navigating back to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCompleteProfile() {
        try {
            System.out.println("Navigating to biodata form...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BiodataForm.fxml"));
            Parent biodataForm = loader.load();

            BiodataController controller = loader.getController();
            controller.setUser(currentUser);

            Scene scene = new Scene(biodataForm);
            Stage stage = (Stage) fullNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Complete Your Biodata");

            System.out.println("Navigated to biodata form");
        } catch (Exception e) {
            System.err.println("Error loading biodata form: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

