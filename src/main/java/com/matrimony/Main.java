package com.matrimony;

import com.matrimony.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Initializing database...");
            DatabaseConnection.initializeDatabase();
            
            if (!DatabaseConnection.testConnection()) {
                System.err.println("WARNING: Could not connect to database!");
                System.err.println("Please check your MySQL server and credentials.");
            }
            
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));

            Scene scene = new Scene(root);

            primaryStage.setTitle("Matrimony App - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Login.fxml: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
