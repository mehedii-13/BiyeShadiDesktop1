package com.matrimony.util;

import com.matrimony.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.matrimony.controller.UserDashboardController;

public class TestDashboardLoad extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("=".repeat(60));
            System.out.println("TESTING USER DASHBOARD LOADING");
            System.out.println("=".repeat(60));

            User testUser = new User();
            testUser.setId(1);
            testUser.setFullName("Test User");
            testUser.setEmail("test@example.com");
            testUser.setPhone("1234567890");
            testUser.setGender("Male");
            testUser.setRole("user");

            System.out.println("\n Test user created: " + testUser.getFullName());

            System.out.println("\n Loading UserDashboard.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));

            System.out.println("FXMLLoader created");

            Parent dashboard = loader.load();
            System.out.println("FXML loaded successfully");

            UserDashboardController controller = loader.getController();
            System.out.println("Controller obtained");

            System.out.println("\n Setting user in controller...");
            controller.setUser(testUser);

            Scene scene = new Scene(dashboard);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Dashboard Test - " + testUser.getFullName());
            primaryStage.setWidth(1200);
            primaryStage.setHeight(700);
            primaryStage.show();

            System.out.println("\n" + "=".repeat(60));
            System.out.println("DASHBOARD LOADED SUCCESSFULLY! ");
            System.out.println("=".repeat(60));

        } catch (Exception e) {
            System.err.println("\n" + "=".repeat(60));
            System.err.println("DASHBOARD LOADING FAILED! ");
            System.err.println("=".repeat(60));
            System.err.println("\nError Details:");
            System.err.println("Type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            System.err.println("\nStack Trace:");
            e.printStackTrace();
            System.err.println("=".repeat(60));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

