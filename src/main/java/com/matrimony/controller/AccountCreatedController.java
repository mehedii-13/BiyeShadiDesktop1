package com.matrimony.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AccountCreatedController {

    @FXML
    private Label emailLabel;
    
    @FXML
    private Label fullNameLabel;
    
    @FXML
    private Button goToLoginButton;
    
    private String userEmail;
    private String userName;
    
    public void setUserInfo(String name, String email) {
        this.userName = name;
        this.userEmail = email;
        if (fullNameLabel != null) {
            fullNameLabel.setText(name);
        }
        if (emailLabel != null) {
            emailLabel.setText(email);
        }
    }
    
    @FXML
    private void initialize() {
    }
    
    @FXML
    private void handleGoToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) goToLoginButton.getScene().getWindow();
            stage.setTitle(" - Login");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
