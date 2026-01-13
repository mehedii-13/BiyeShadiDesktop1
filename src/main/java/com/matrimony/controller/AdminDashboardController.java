package com.matrimony.controller;

import com.matrimony.dao.UserDAO;
import com.matrimony.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminDashboardController {

    @FXML private Label adminNameLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label blockedUsersLabel;
    @FXML private TextField searchField;
    @FXML private VBox usersContainer;
    @FXML private Button logoutButton;

    private User currentAdmin;
    private UserDAO userDAO;
    private List<User> allUsers;

    @FXML
    public void initialize() {
        System.out.println("AdminDashboardController initialized");
        userDAO = new UserDAO();
    }

    public void setUser(User admin) {
        this.currentAdmin = admin;
        if (admin != null) {
            adminNameLabel.setText("Admin: " + admin.getFullName());
            loadUsers();
            updateStatistics();
        }
    }

    @FXML
    private void loadUsers() {
        try {
            allUsers = userDAO.getAllUsers();
            displayUsers(allUsers);
            updateStatistics();
            System.out.println("Loaded " + allUsers.size() + "users");
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users.");
        }
    }

    private void displayUsers(List<User> users) {
        usersContainer.getChildren().clear();

        if (users.isEmpty()) {
            Label noUsers = new Label("No users found.");
            noUsers.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            usersContainer.getChildren().add(noUsers);
            return;
        }

        for (User user : users) {
            if ("admin".equals(user.getRole())) {
                continue;
            }

            VBox userCard = createUserCard(user);
            usersContainer.getChildren().add(userCard);
        }
    }

    private VBox createUserCard(User user) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, lightgray, 10, 0, 0, 2);");

        HBox infoSection = new HBox(20);
        infoSection.setAlignment(Pos.CENTER_LEFT);

        VBox userInfo = new VBox(5);
        Label nameLabel = new Label(user.getFullName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label emailLabel = new Label("Email: " + user.getEmail());
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        Label phoneLabel = new Label("Phone: " + user.getPhone());
        phoneLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        Label genderLabel = new Label("Gender: " + capitalizeFirst(user.getGender()));
        genderLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        userInfo.getChildren().addAll(nameLabel, emailLabel, phoneLabel, genderLabel);

        VBox statusSection = new VBox(5);
        statusSection.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(statusSection, javafx.scene.layout.Priority.ALWAYS);

        Label statusLabel = new Label();
        if (user.isBlocked()) {
            statusLabel.setText("BLOCKED");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f44336; " +
                               "-fx-background-color: #ffebee; -fx-padding: 5 10; -fx-background-radius: 5;");

            if (user.getBlockedUntil() != null) {
                Label blockedUntilLabel = new Label("Until: " + user.getBlockedUntil().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                blockedUntilLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #f44336;");
                statusSection.getChildren().add(blockedUntilLabel);
            }
        } else {
            statusLabel.setText("ACTIVE");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4CAF50; " +
                               "-fx-background-color: #e8f5e9; -fx-padding: 5 10; -fx-background-radius: 5;");
        }
        statusSection.getChildren().add(0, statusLabel);

        infoSection.getChildren().addAll(userInfo, statusSection);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(10, 0, 0, 0));

        if (user.isBlocked()) {
            Button unblockBtn = new Button("Unblock User");
            unblockBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
            unblockBtn.setOnAction(e -> handleUnblockUser(user));
            actions.getChildren().add(unblockBtn);
        } else {
            Button blockBtn = new Button("Block User");
            blockBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
            blockBtn.setOnAction(e -> handleBlockUser(user));
            actions.getChildren().add(blockBtn);
        }

        Button deleteBtn = new Button("Delete User");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteUser(user));
        actions.getChildren().add(deleteBtn);

        card.getChildren().addAll(infoSection, actions);
        return card;
    }

    private void handleBlockUser(User user) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Block User");
        dialog.setHeaderText("Block " + user.getFullName());

        ButtonType blockButtonType = new ButtonType("Block", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(blockButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label label = new Label("Block for how many days?");
        TextField daysField = new TextField("7");
        daysField.setPromptText("Enter number of days");

        content.getChildren().addAll(label, daysField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == blockButtonType) {
                try {
                    return Integer.parseInt(daysField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(days -> {
            if (days != null && days > 0) {
                LocalDate blockedUntil = LocalDate.now().plusDays(days);
                boolean success = userDAO.blockUser(user.getId(), blockedUntil);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                        user.getFullName() + "has been blocked for " + days + "days.");
                    loadUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to block user.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid number of days.");
            }
        });
    }

    private void handleUnblockUser(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Unblock User");
        confirm.setHeaderText("Unblock " + user.getFullName() + "?");
        confirm.setContentText("This user will be able to access the system again.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean success = userDAO.unblockUser(user.getId());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                    user.getFullName() + "has been unblocked.");
                loadUsers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to unblock user.");
            }
        }
    }

    private void handleDeleteUser(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Delete " + user.getFullName() + "?");
        confirm.setContentText("This action cannot be undone. All user data will be permanently deleted.");

        ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(deleteButton, cancelButton);

        confirm.showAndWait().ifPresent(response -> {
            if (response == deleteButton) {
                boolean success = userDAO.deleteUser(user.getId());

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                        user.getFullName() + "has been deleted.");
                    loadUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
                }
            }
        });
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            displayUsers(allUsers);
            return;
        }

        List<User> filteredUsers = allUsers.stream()
            .filter(user -> user.getFullName().toLowerCase().contains(searchText) ||
                           user.getEmail().toLowerCase().contains(searchText))
            .collect(Collectors.toList());

        displayUsers(filteredUsers);
    }

    private void updateStatistics() {
        if (allUsers == null) return;

        int total = 0;
        int active = 0;
        int blocked = 0;

        for (User user : allUsers) {
            if ("admin".equals(user.getRole())) continue;

            total++;
            if (user.isBlocked()) {
                blocked++;
            } else {
                active++;
            }
        }

        totalUsersLabel.setText(String.valueOf(total));
        activeUsersLabel.setText(String.valueOf(active));
        blockedUsersLabel.setText(String.valueOf(blocked));
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent login = loader.load();

            Scene scene = new Scene(login);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login - Matrimony System");

            System.out.println("Admin logged out");
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

