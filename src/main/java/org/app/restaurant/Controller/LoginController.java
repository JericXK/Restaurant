package org.app.restaurant.Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.app.restaurant.Database;

public class LoginController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Button signin;

    public void login(ActionEvent e) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if fields are empty
        if (username.isEmpty() && password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An Error Occurred");
            alert.setContentText("Username & password cannot be empty");
            alert.showAndWait();
        }
        else if (username.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An Error Occurred");
            alert.setContentText("Username cannot be empty.");
            alert.showAndWait();
            passwordField.clear();
        }
        else if (password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An Error Occurred");
            alert.setContentText("Password cannot be empty.");
            alert.showAndWait();
            usernameField.clear();
        }
        else {// Clear message
            enter(username, password);// Passing credentials to the method
        }
    }

    public void enter(String username, String password) {
        Database connection = new Database();
        try (Connection com = connection.Link()) {
            String verifyLogin = "SELECT * FROM login WHERE username = ? AND password = ?";

            try (PreparedStatement pstmt = com.prepareStatement(verifyLogin)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);

                try (ResultSet query = pstmt.executeQuery()) {
                    if (query.next() && query.getInt(1) == 1) {
                        dashboardPage();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("An Error Occurred");
                        alert.setContentText("Invalid login.");
                        alert.showAndWait();
                        usernameField.clear();
                        passwordField.clear();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An Error Occurred");
            alert.setContentText("An error occurred while logging in.");
            alert.showAndWait();
        }
    }

    public void dashboardPage() {
        // Add functionality to handle dashboard button click
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Access the controller for the dashboard
            DashboardController dashboardController = loader.getController();

            // Call methods to populate the charts with data
            dashboardController.dashboardNumber(); // Populate the BarChart
            dashboardController.dashboardINC(); // Populate the AreaChart

            // Set up the scene and stage for the dashboard
            Scene dashboardScene = new Scene(dashboardRoot);
            Stage stage = (Stage) signin.getScene().getWindow();  // Adjust if needed
            stage.setScene(dashboardScene);
            centerStageOnScreen(stage);  // Optional method to center the stage on screen
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void centerStageOnScreen(Stage stage) {
        // Get the screen width and height
        double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();

        // Get the width and height of the stage (window)
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();

        // Calculate the X and Y position to center the stage
        double x = (screenWidth - stageWidth) / 2;
        double y = (screenHeight - stageHeight) / 2;

        // Set the stage position
        stage.setX(x);
        stage.setY(y);
    }
}