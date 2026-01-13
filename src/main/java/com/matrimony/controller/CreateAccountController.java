package com.matrimony.controller;

import com.matrimony.dao.UserDAO;
import com.matrimony.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateAccountController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private Button createAccountButton;

    @FXML
    private Hyperlink backToLoginLink;

    private UserDAO userDAO;

    @FXML
    private void initialize() {
        genderComboBox.getItems().addAll("Male", "Female");
        userDAO = new UserDAO();
        
        System.out.println("CreateAccountController initialized");
    }

    @FXML
    private void handleCreateAccount() {
        System.out.println("Create Account button clicked!");
        
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText().trim();
        String gender = genderComboBox.getValue();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || 
            confirmPassword.isEmpty() || phone.isEmpty() || gender == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 6 characters long.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            return;
        }

        if (userDAO.emailExists(email)) {
            showAlert(Alert.AlertType.WARNING, "Email Already Exists", 
                "This email is already registered.\nPlease use a different email or login.");
            return;
        }

        User newUser = new User(fullName, email, phone, gender, password);
        
        boolean success = userDAO.registerUser(newUser);
        
        if (success) {
            System.out.println("User registered successfully!");
            
            fullNameField.clear();
            emailField.clear();
            phoneField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
            genderComboBox.setValue(null);
            
            navigateToAccountCreatedPage(fullName, email);
            
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", 
                "Failed to create account.\nPlease check:\n" );
        }
    }
    
    private void navigateToAccountCreatedPage(String fullName, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccountCreated.fxml"));
            Parent root = loader.load();
            
            AccountCreatedController controller = loader.getController();
            controller.setUserInfo(fullName, email);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) createAccountButton.getScene().getWindow();
            stage.setTitle(" - Account Created");
            stage.setScene(scene);
            
        } catch (Exception e) {
            System.err.println("Error loading Account Created page: " + e.getMessage());
            e.printStackTrace();
            
            showAlert(Alert.AlertType.INFORMATION, "Account Created Successfully! ", 
                "Your account has been created successfully!\n\n" +
                "Your information has been saved securely\n" +
                "Password has been encrypted\n\n" +
                "You can now login with:\n" +
                "Email: " + email);
            handleBackToLogin();
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) backToLoginLink.getScene().getWindow();
            stage.setTitle(" - Login");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Login page.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
