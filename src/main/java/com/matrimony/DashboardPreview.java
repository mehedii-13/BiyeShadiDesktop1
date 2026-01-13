package com.matrimony;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class DashboardPreview extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Loading UserDashboard.fxml...");

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/UserDashboard.fxml")
            );

            System.out.println("FXML file found, loading content...");

            Parent root = loader.load();

            System.out.println("FXML loaded successfully!");

            Scene scene = new Scene(root, 1200, 700);

            primaryStage.setTitle("Matrimony Dashboard - Preview");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(600);
            primaryStage.show();


        } catch (java.io.IOException e) {
            System.err.println("Error loading FXML file:");
            System.err.println("" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading dashboard:");
            System.err.println("" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("");
        System.out.println("DASHBOARD PREVIEW - STANDALONE      ");
        System.out.println("\n");

        launch(args);
    }
}

