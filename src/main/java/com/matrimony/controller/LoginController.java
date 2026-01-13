package com.matrimony.controller;

import com.matrimony.dao.UserDAO;
import com.matrimony.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink createAccountLink;

    private UserDAO userDAO;

    @FXML
    private void initialize() {
        userDAO = new UserDAO();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter email and password.");
            return;
        }

        User user = userDAO.authenticateUser(email, password);
        
        if (user != null) {
            if (user.isBlocked()) {
                String message = "Your account has been blocked.";
                if (user.getBlockedUntil() != null) {
                    message += "\nBlocked until: " + user.getBlockedUntil().toString();
                }
                showAlert(Alert.AlertType.ERROR, "Account Blocked", message);
                return;
            }

            if ("admin".equalsIgnoreCase(user.getRole())) {
                try {
                    System.out.println("Loading AdminDashboard for: " + user.getFullName());

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                    Parent adminDashboard = loader.load();

                    AdminDashboardController controller = loader.getController();
                    controller.setUser(user);

                    Scene scene = new Scene(adminDashboard);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(scene);
                    stage.setTitle("Admin Dashboard - " + user.getFullName());

                    System.out.println("Admin Dashboard loaded successfully!");

                } catch (Exception e) {
                    System.err.println("Failed to load admin dashboard:");
                    System.err.println("Error: " + e.getClass().getName());
                    System.err.println("Message: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load admin dashboard.\n\nDetails: " + e.getMessage());
                }
            } else if ("user".equalsIgnoreCase(user.getRole())) {
                try {
                    System.out.println("STARTING DASHBOARD LOAD");
                    System.out.println("Loading UserDashboard for: " + user.getFullName());
                    System.out.println("User ID: " + user.getId());
                    System.out.println("User Role: " + user.getRole());

                    System.out.println("Step 1: Getting FXML resource...");
                    java.net.URL fxmlUrl = getClass().getResource("/fxml/UserDashboard.fxml");
                    if (fxmlUrl == null) {
                        throw new java.io.IOException("UserDashboard.fxml not found in /fxml/");
                    }
                    System.out.println("✓ FXML URL: " + fxmlUrl);

                    System.out.println("Step 2: Creating FXMLLoader...");
                    FXMLLoader loader = new FXMLLoader(fxmlUrl);

                    System.out.println("Step 3: Loading FXML...");
                    Parent dashboard = loader.load();
                    System.out.println("✓ FXML loaded successfully");

                    System.out.println("Step 4: Getting controller...");
                    UserDashboardController controller = loader.getController();
                    if (controller == null) {
                        throw new RuntimeException("Controller is null after FXML load");
                    }
                    System.out.println("✓ Controller retrieved: " + controller.getClass().getName());

                    System.out.println("Step 5: Setting user in controller...");
                    controller.setUser(user);
                    System.out.println("✓ User set in controller");

                    System.out.println("Step 6: Creating scene...");
                    Scene dashboardScene = new Scene(dashboard);
                    System.out.println("✓ Scene created");

                    System.out.println("Step 7: Getting stage and setting scene...");
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(dashboardScene);
                    stage.setTitle("Dashboard - " + user.getFullName());
                    System.out.println("✓ Scene set on stage");

                    System.out.println("=== DASHBOARD LOADED SUCCESSFULLY ===");

                } catch (Exception e) {
                    System.err.println("Exception Type: " + e.getClass().getName());
                    System.err.println("Error Message: " + e.getMessage());
                    System.err.println("\n--- FULL STACK TRACE ---");
                    e.printStackTrace();
                    System.err.println("--- END STACK TRACE ---\n");

                    if (e.getCause() != null) {
                        System.err.println("--- CAUSED BY ---");
                        System.err.println("Cause Type: " + e.getCause().getClass().getName());
                        System.err.println("Cause Message: " + e.getCause().getMessage());
                        e.getCause().printStackTrace();
                        System.err.println("--- END CAUSE ---\n");
                    }

                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard.\n\nDetails: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Unknown Role",
                    "Your account role is not recognized. Please contact administrator.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", 
                "Invalid email or password.\nPlease try again or create a new account.");
        }
    }

    @FXML
    private void handleCreateAccount() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/CreateAccount.fxml"));
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) createAccountLink.getScene().getWindow();
            stage.setTitle(" - Create Account");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Create Account page.");
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
