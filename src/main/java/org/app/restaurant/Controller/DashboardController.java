package org.app.restaurant.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.app.restaurant.Database;

import java.io.IOException;
import java.sql.*;

public class DashboardController {

    @FXML
    private Button available_bt;

    @FXML
    private Label dashboard_INCOME;

    @FXML
    private AreaChart<String, Double> dashboard_INchart;

    @FXML
    private Label dashboard_TI;

    @FXML
    private Label dashboard_nc;

    @FXML
    private BarChart<String, Double> dashboard_numberchart;

    @FXML
    private Button order_bt;

    @FXML
    private Button signout;

    @FXML
    private void handleOrderButton(ActionEvent event) {
        try {
            // Load the FXML file for the order screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/order.fxml"));
            Parent root = loader.load();

            // Set the scene and show the order window
            Scene scene = new Scene(root);
            Stage orderStage = (Stage) order_bt.getScene().getWindow();
            orderStage.setScene(scene);
            centerStageOnScreen(orderStage);
            orderStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an error message
        }
    }

    @FXML
    private void handleAvailableButton(ActionEvent event) {
        // Add functionality to handle available button click
        try {
            // Load the FXML file for the order screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/available.fxml"));
            Parent root = loader.load();

            // Set the scene and show the order window
            Scene scene = new Scene(root);
            Stage orderStage = (Stage) available_bt.getScene().getWindow();
            orderStage.setScene(scene);
            centerStageOnScreen(orderStage);
            orderStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an error message
        }
    }

    // Sign out method that closes the dashboard and opens the login screen
    @FXML
    private void handleSignOut(ActionEvent event) {
        try {
            // Load the FXML file for the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/login_form.fxml"));
            Parent root = loader.load();

            // Set the scene and show the order window
            Scene scene = new Scene(root);
            Stage loginStage = (Stage) signout.getScene().getWindow();
            loginStage.setScene(scene);
            centerStageOnScreen(loginStage);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Center the stage on the screen
    private void centerStageOnScreen(Stage stage) {
        double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();

        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();

        double x = (screenWidth - stageWidth) / 2;
        double y = (screenHeight - stageHeight) / 2;

        stage.setX(x);
        stage.setY(y);
    }

    public void dashboardNC(){
        String sql = "SELECT COUNT(id) FROM product_info";  // Correct SQL for counting rows

        try (Connection connection = new Database().Link();
             Statement stmnt = connection.createStatement();
             ResultSet rs = stmnt.executeQuery(sql)) {

            int nc = 0;  // Default value for no results
            if (rs.next()) {
                nc = rs.getInt(1);  // Get the count of rows (first column in the result)
            }

            dashboard_nc.setText(String.valueOf(nc));  // Display the count

        } catch (SQLException e) {
            // Log or handle exception properly
            e.printStackTrace();
        }
    }

    public void dashboardTI(){
        java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());

        String sql = "SELECT SUM(total) FROM product_info WHERE date = ?";
        try (Connection connection = new Database().Link();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setDate(1, sqlDate);

            double ti = 0;
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ti = rs.getDouble(1); // You can use column index or name here
                }
            }

            dashboard_TI.setText("$" + String.format("%.2f", ti)); // Properly format the output
        } catch (SQLException e) {
            // Log the exception or show a meaningful message to the user
            e.printStackTrace();
        }
    }

    public void dashboardIncome(){
        String sql = "SELECT SUM(total) FROM product_info";
        try (Connection connection = new Database().Link();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            double ti = 0;

            if (rs.next()) {
                    ti = rs.getDouble("SUM(total)"); // You can use column index or name here
                }


            dashboard_INCOME.setText("$" + String.format("%.2f", ti)); // Properly format the output
        } catch (SQLException e) {
            // Log the exception or show a meaningful message to the user
            e.printStackTrace();
        }
    }

    public void dashboardNumber() {
        // SQL query to get the count of products grouped by date
        String query = "SELECT date, COUNT(id) FROM product_info GROUP BY date ORDER BY date ASC LIMIT 5"; // Corrected to ORDER BY date

        XYChart.Series<String, Double> series = new XYChart.Series<>();

        try (Connection connection = new Database().Link();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String date = rs.getString(1);  // Get the date from the result set
                double count = rs.getDouble(2); // Get the count of items sold on that date

                series.getData().add(new XYChart.Data<>(date, count)); // Add data to the chart series
            }

            // Add the series to the chart
            dashboard_numberchart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception by printing the stack trace
        }
    }

    public void dashboardINC() {
        String query = "SELECT date, SUM(total) FROM product_info GROUP BY date ORDER BY date ASC LIMIT 5"; // Corrected to ORDER BY date
        XYChart.Series<String, Double> series = new XYChart.Series<>();

        try (Connection connection = new Database().Link();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String date = rs.getString(1);  // Get the date from the result set
                double totalIncome = rs.getDouble(2); // Get the sum of total income for the date

                series.getData().add(new XYChart.Data<>(date, totalIncome)); // Add data to the chart series
            }

            // Add the series to the chart
            dashboard_INchart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception by printing the stack trace
        }
    }

    public void initialize(){
        dashboardNC();
        dashboardTI();
        dashboardIncome();
        dashboardNumber();
        dashboardINC();
    }
}