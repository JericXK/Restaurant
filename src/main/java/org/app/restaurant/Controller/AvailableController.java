package org.app.restaurant.Controller;
import org.app.restaurant.Database;
import org.app.restaurant.Model.Category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

public class AvailableController{

    @FXML
    private TextField availableFD_ProductID;

    @FXML
    private TextField availableFD_ProductName;

    @FXML
    private TableView<Category> table_viewFD;

    @FXML
    private TableColumn<Category, String> availableFD_col_ID;

    @FXML
    private TableColumn<Category, String> availableFD_col_name;

    @FXML
    private TableColumn<Category, BigDecimal> availableFD_col_price;

    @FXML
    private TableColumn<Category, String> availableFD_col_status;

    @FXML
    private TableColumn<Category, String> availableFD_col_type;

    @FXML
    private TextField availableFD_price;

    @FXML
    private TextField availableFD_search;

    @FXML
    private ComboBox<String> availableFD_status;

    @FXML
    private ComboBox<String> availableFD_type;

    @FXML
    private Button dashboard_bt;

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
    private void handleDashboardButton(ActionEvent event) {
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
            Stage stage = (Stage) dashboard_bt.getScene().getWindow();  // Adjust if needed
            stage.setScene(dashboardScene);
            centerStageOnScreen(stage);  // Optional method to center the stage on screen
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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



    @FXML // Action handler for adding data to the database
    private void availableFDAdd(ActionEvent event){
        String productID = availableFD_ProductID.getText();
        String productName = availableFD_ProductName.getText();
        String productPrice = availableFD_price.getText();
        String productType = availableFD_type.getSelectionModel().getSelectedItem();
        String productStatus = availableFD_status.getSelectionModel().getSelectedItem();

        // Validate the inputs
        if (productID.isEmpty() || productName.isEmpty() || productPrice.isEmpty() ||
                productType == null || productStatus == null) {
            // Show an alert if any field is empty
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Validation");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.show();
            return; // Stop execution if any field is empty
        }

        // Define the SQL query to insert data
        String insertSQL = "INSERT INTO category (product_id, product_name, type, price, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = new Database().Link();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            // Set the values of the PreparedStatement
            preparedStatement.setString(1, productID);
            preparedStatement.setString(2, productName);
            preparedStatement.setString(3, productType);
            preparedStatement.setString(4, productPrice);
            preparedStatement.setString(5, productStatus);

            // Execute the insert operation
            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the insert was successful
            if (rowsAffected > 0) {
                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Product successfully added!");
                alert.show();

                // Clear input fields after successful insert
                availableFDClear();

                // Show Category Table by loading data from database
                loadTableData();
            } else {
                // Show error message if no rows were affected
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while adding the product.");
                alert.show();
            }

        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error inserting data");
            alert.setContentText("An error occurred while adding the product. Please try again.");
            alert.show();
        }
    }

    private ObservableList<Category> categorieslist = FXCollections.observableArrayList();
    // Method to load data from the database into the TableView
    public void loadTableData() throws SQLException {
        categorieslist.clear(); // Clear any old data

        String sql = "SELECT * FROM category";
        Database database = new Database();
        Statement statement = database.Link().createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            Category category = new Category(
                    rs.getString("product_id"),
                    rs.getString("product_name"),
                    rs.getString("type"),
                    rs.getBigDecimal("price"),
                    rs.getString("status")
            );
            categorieslist.add(category);
        }

        // Check if the data was loaded
        if (categorieslist.isEmpty()) {
            System.out.println("No categories found.");
        }

        // Set the items for the TableView to display the latest data
        table_viewFD.setItems(categorieslist);
    }


    @FXML
    public void availableFDClear(){
        availableFD_ProductID.clear();
        availableFD_ProductName.clear();
        availableFD_price.clear();

        // Reset ComboBox selections
        if (availableFD_type.getItems().contains("Select Type...")) {
            availableFD_type.getSelectionModel().select("Select Type...");
        } else {
            availableFD_type.getSelectionModel().clearSelection();  // Alternatively, clear selection if not present
        }

        if (availableFD_status.getItems().contains("Select Status...")) {
            availableFD_status.getSelectionModel().select("Select Status...");
        } else {
            availableFD_status.getSelectionModel().clearSelection();  // Alternatively, clear selection if not present
        }
    }

    @FXML // Method to update a record when the Update button is clicked
    public void availableFDUpdate() {
        String productID = availableFD_ProductID.getText();
        String productName = availableFD_ProductName.getText();
        String productPrice = availableFD_price.getText();
        String productType = availableFD_type.getSelectionModel().getSelectedItem();
        String productStatus = availableFD_status.getSelectionModel().getSelectedItem();

        if (productID.isEmpty() || productName.isEmpty() || productPrice.isEmpty() || productType == null || productStatus == null) {
            // Show alert if any field is empty
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Validation");
            alert.setHeaderText(null);
            alert.setContentText("All fields must be filled out.");
            alert.show();
            return; // Stop execution if any field is empty
        }

        String updateSQL = "UPDATE category SET product_name = ?, type = ?, price = ?, status = ? WHERE product_id = ?";
        try (Connection connection = new Database().Link();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, productName);
            preparedStatement.setString(2, productType);
            preparedStatement.setString(3, productPrice);
            preparedStatement.setString(4, productStatus);
            preparedStatement.setString(5, productID);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Update Successful");
                alert.setHeaderText(null);
                alert.setContentText("The product has been updated successfully.");
                alert.show();

                // Clear input fields after successful insert
                availableFDClear();

                // Show Category Table by loading data from database
                loadTableData();
            } else {
                // Error message if no rows were updated
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update Failed");
                alert.setHeaderText(null);
                alert.setContentText("No product found with the provided ID.");
                alert.show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error updating product");
            alert.setContentText("An error occurred while updating the product. Please try again.");
            alert.show();
        }
    }

    @FXML
    public void availableFDDelete() {
        // Get the selected record from the TableView
        Category selectedCategory = table_viewFD.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            // If no row is selected, show a warning message
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a product to delete.");
            alert.show();
            return;
        }

        // SQL query to delete the selected product
        String deleteSQL = "DELETE FROM category WHERE product_id = ?";

        try (Connection connection = new Database().Link();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            // Set the product_id of the selected category
            preparedStatement.setString(1, selectedCategory.getProductID());

            // Execute the delete operation
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // If the deletion is successful, remove the item from the TableView
                table_viewFD.getItems().remove(selectedCategory);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Product successfully deleted!");
                alert.show();
            } else {
                // If no rows were affected, show an error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while deleting the product.");
                alert.show();
            }

            // Show Category Table by loading data from database
            loadTableData();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error deleting data");
            alert.setContentText("An error occurred while deleting the product. Please try again.");
            alert.show();
        }
    }

    @FXML
    public void availableFDChoiceSelection() {
        Category selectedCategory = table_viewFD.getSelectionModel().getSelectedItem();

        if (selectedCategory != null) {
            // Populate the fields with the selected category data
            availableFD_ProductID.setText(String.valueOf(selectedCategory.getProductID()));  // Ensure the ID is converted to a string
            availableFD_ProductName.setText(selectedCategory.getName());  // Assuming Name is a String
            availableFD_price.setText(selectedCategory.getPrice().toString());  // Ensure BigDecimal is converted to a string
            availableFD_type.getSelectionModel().select(selectedCategory.getType());  // Select the type
            availableFD_status.getSelectionModel().select(selectedCategory.getStatus());  // Select the status
        }
    }

    @FXML
    public void availableFDSearch() {
        // Create a FilteredList that initially allows all items to be shown
        FilteredList<Category> filter = new FilteredList<>(categorieslist, e -> true);

        // Bind the text input from the search field to the filter
        availableFD_search.textProperty().addListener((observableValue, newValue, oldValue) -> {
            // Predicate to filter items based on search text
            filter.setPredicate(predicateCategory -> {
                // If the search field is empty, show all items
                if (newValue == null || newValue.isEmpty()) {
                    return true;  // Show all items when search is empty
                }

                // Convert the input to lowercase for case-insensitive comparison
                String searchKey = newValue.toLowerCase();

                // Check if any of the category properties match the search key
                boolean matches = false;
                if (predicateCategory.getProductID() != null && predicateCategory.getProductID().toLowerCase().contains(searchKey)) {
                    matches = true;
                } else if (predicateCategory.getName() != null && predicateCategory.getName().toLowerCase().contains(searchKey)) {
                    matches = true;
                } else if (predicateCategory.getPrice() != null && String.valueOf(predicateCategory.getPrice()).contains(searchKey)) {
                    matches = true;
                } else if (predicateCategory.getType() != null && predicateCategory.getType().toLowerCase().contains(searchKey)) {
                    matches = true;
                } else if (predicateCategory.getStatus() != null && predicateCategory.getStatus().toLowerCase().contains(searchKey)) {
                    matches = true;
                }

                return matches;  // Return true if a match is found
            });
        });

        // Create a SortedList to sort the filtered results (optional)
        SortedList<Category> sortedList = new SortedList<>(filter);
        sortedList.comparatorProperty().bind(table_viewFD.comparatorProperty()); // Bind to the TableView's comparator

        // Set the SortedList to be the data source for the TableView
        table_viewFD.setItems(sortedList);
    }


    // Initialize method to set up ComboBoxes and TableView
    public void initialize() throws SQLException{
        // Set up ComboBoxes with default options
        ObservableList<String> typeOptions = FXCollections.observableArrayList("Select Type...", "Meal", "Drink", "Dessert");
        availableFD_type.setItems(typeOptions);
        availableFD_type.getSelectionModel().select("Select Type...");

        ObservableList<String> statusOptions = FXCollections.observableArrayList("Select Status...", "Available", "Unavailable");
        availableFD_status.setItems(statusOptions);
        availableFD_status.getSelectionModel().select("Select Status...");

        // Show Category Table by loading data from database
        loadTableData();

        // Set up columns for TableView
        availableFD_col_ID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        availableFD_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        availableFD_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        availableFD_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        availableFD_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Call the availableFDSearch method to bind the search functionality
        availableFDSearch();

    }
}