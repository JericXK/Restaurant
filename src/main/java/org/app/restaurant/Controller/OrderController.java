package org.app.restaurant.Controller;

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
import org.app.restaurant.Database;
import org.app.restaurant.Model.Product;

import java.io.IOException;
import java.sql.*;

public class OrderController {

    @FXML
    private Button available_bt;

    @FXML
    private Button dashboard_bt;

    @FXML
    private Spinner<Integer> order_Quantity;

    @FXML
    private TextField order_amount;

    @FXML
    private TableColumn<Product, String> order_col_ID;

    @FXML
    private TableColumn<Product, String> order_col_name;

    @FXML
    private TableColumn<Product, String> order_col_price;

    @FXML
    private TableColumn<Product, String> order_col_quantity;

    @FXML
    private TableColumn<Product, String> order_col_type;

    @FXML
    private ComboBox<String> order_productID;

    @FXML
    private ComboBox<String> order_productNAME;

    @FXML
    private TableView<Product> order_tableview;

    @FXML
    private Label order_total;

    @FXML
    private Label order_balance;

    @FXML
    private Button signout;

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

    public void orderProductID() throws SQLException {
        String sql = "SELECT product_id FROM category WHERE status = 'Available'";
        Connection connection = new Database().Link();

        try {
            PreparedStatement pstmnt = connection.prepareStatement(sql);
            ResultSet rs = pstmnt.executeQuery();

            ObservableList listData = FXCollections.observableArrayList();

            while (rs.next()) {
                listData.add(rs.getString("product_id"));
            }

            order_productID.setItems(listData);
            orderProductName();
        } catch (Exception e) {
            // Handle any SQL exceptions
            e.printStackTrace();
        }
    }

    public void orderProductName(){
        // Define the SQL query to fetch product names based on the selected product_id
        String sql = "SELECT product_name FROM category WHERE product_id = ?";  // Use parameterized query

        // Initialize the database connection
        try (Connection connection = new Database().Link();
             PreparedStatement pstmnt = connection.prepareStatement(sql)) {

            // Set the parameter (product_id) in the query
            pstmnt.setString(1, (String) order_productID.getSelectionModel().getSelectedItem());

            // Execute the query and get the result set
            try (ResultSet rs = pstmnt.executeQuery()) {
                // Create a list to hold the product names
                ObservableList<String> listData = FXCollections.observableArrayList();

                // Loop through the result set and add product names to the list
                while (rs.next()) {
                    listData.add(rs.getString("product_name"));
                }
                // Set the list of product names to the ComboBox
                order_productNAME.setItems(listData);
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions and print the stack trace for debugging
            e.printStackTrace();
        }
    }

    private SpinnerValueFactory<Integer> spinner;

    public void orderSpinner() {
        spinner = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 0);
        order_Quantity.setValueFactory(spinner);

    }

    private int qpt;

    public void orderQuantity() {
        qpt = order_Quantity.getValue();

        System.out.println(qpt);
    }

    private ObservableList<Product> orderListData(){
        ObservableList<Product> productList = FXCollections.observableArrayList();
        String sql = "SELECT product_id, product_name, type, price, quantity FROM product WHERE customer_id = ?";

        try (Connection connection = new Database().Link();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerID);  // Set the correct customer ID
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getString("type"),
                        rs.getBigDecimal("price"),
                        rs.getInt("quantity")
                );
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Fetched products: " + productList.size());
        return productList;
    }

    private int customerID;

    public void orderCustomer() throws SQLException {
        String sql = "SELECT customer_id FROM product";
        Connection connection = new Database().Link();
        try {
            PreparedStatement pstmnt = connection.prepareStatement(sql);
            ResultSet rs = pstmnt.executeQuery();

            while(rs.next()) {
                customerID = rs.getInt("customer_id");
            }

            String checkData = "SELECT customer_id FROM product_info";
            Database database = new Database();
            Statement statement = database.Link().createStatement();
            rs = statement.executeQuery(checkData);

            int customerInfold = 0;

            while(rs.next()){
                customerInfold = rs.getInt("customer_id");
            }

            if(customerID == 0){
                customerID += 1;
            }
            else if(customerID ==customerInfold){
                customerID += 1;
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions and print the stack trace for debugging
            e.printStackTrace();
        }
    }

    public void orderAdd() throws SQLException {
        // Add product and update the database
        orderCustomer();

        String Addsql = "INSERT INTO product (customer_id, product_id, product_name, type, price, quantity, date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String checkData = "SELECT * FROM category WHERE product_id = ?";
        String insert = "INSERT INTO product_id (customer_id, total, date) VALUES (?, ?, ?)";

        try (Connection connection = new Database().Link();
             PreparedStatement preparedStatement = connection.prepareStatement(Addsql);
             PreparedStatement checkStmt = connection.prepareStatement(checkData);
             PreparedStatement insertStmt = connection.prepareStatement(insert)) {

            // Set values for check query and retrieve data
            checkStmt.setString(1, (String) order_productID.getSelectionModel().getSelectedItem());
            ResultSet rs = checkStmt.executeQuery();

            String orderType = "";
            double orderPrice = 0;

            if (rs.next()) {
                orderType = rs.getString("type");
                orderPrice = rs.getDouble("price");
            }

            double totalPrice = orderPrice * qpt;  // Calculate total price for this order

            // Set values for the main order insertion
            preparedStatement.setInt(1, customerID);
            preparedStatement.setString(2, (String) order_productID.getSelectionModel().getSelectedItem());
            preparedStatement.setString(3, (String) order_productNAME.getSelectionModel().getSelectedItem());
            preparedStatement.setString(4, orderType);
            preparedStatement.setDouble(5, totalPrice);  // Use the totalPrice here
            preparedStatement.setInt(6, qpt);

            // Set date
            java.sql.Date SQLDate = new java.sql.Date(System.currentTimeMillis());
            preparedStatement.setDate(7, SQLDate);

            // Execute the first insert
            preparedStatement.executeUpdate();

            // Update the total price after adding the product
            orderTotal();
            orderDisplayTotal(); // Ensure this is called to update the UI

            // Update total order in the second table
            insertStmt.setInt(1, customerID);
            insertStmt.setDouble(2, totalP);
            insertStmt.setDate(3, SQLDate);

            // Execute the second insert
            insertStmt.executeUpdate();

            // After adding the product, update the display table
            orderDisplayTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double totalP = 0;

    public void orderTotal() throws SQLException {
        orderCustomer();  // Ensure the correct customer is set
        String sql = "SELECT SUM(price * quantity) FROM product WHERE customer_id = ?";

        try (Connection connection = new Database().Link();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerID);  // Ensure the customer ID is set correctly
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalP = rs.getDouble(1);  // Retrieve the correct sum
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void orderDisplayTotal() throws SQLException {
        orderTotal();  // Recalculate total after adding products
        order_total.setText("$" + String.format("%.2f", totalP));  // Display total with 2 decimal places
    }

    private ObservableList<Product> orderData = FXCollections.observableArrayList();

    public void orderDisplayTable() throws SQLException {
        orderData = orderListData(); // Fetch latest list of products

        if (orderData == null || orderData.isEmpty()) {
            System.out.println("No data available for the table.");
        }

        // Bind the columns to the properties of the Product object
        order_col_ID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        order_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        order_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        order_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        order_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Set items in TableView
        order_tableview.setItems(orderData);
    }

    private double amount;
    private double balance;

    public void orderAmount() throws SQLException {
        orderTotal();  // Ensure the total is updated

        Alert alert;
        String orderAmountText = order_amount.getText().trim();

        // Check if the text field is empty
        if (orderAmountText.isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please type the amount");
            alert.showAndWait();
            return;
        }

        // Try to parse the entered amount
        try {
            amount = Double.parseDouble(orderAmountText);

            // Check if the amount is less than the total price
            if (amount < totalP) {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Amount is less than the total price. Please enter a valid amount.");
                alert.showAndWait();
                order_amount.setText("");  // Reset the input field
                return;
            }

            // Calculate balance
            balance = (amount - totalP);
            order_balance.setText(String.format("$%.2f", balance));  // Display balance

        } catch (NumberFormatException e) {
            // Catch invalid number formats (non-numeric input)
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Invalid amount entered. Please enter a valid number.");
            alert.showAndWait();
            order_amount.setText("");  // Reset the input field
        }
    }

    public void orderPay() throws SQLException {
        orderCustomer(); // Ensure the customer is set
        orderTotal(); // Ensure the total is updated

        String pay = "INSERT INTO product_info (customer_id, total, date) VALUES (?, ?, ?);";

        try (Connection connection = new Database().Link(); // Use try-with-resources to automatically close the connection
             PreparedStatement pstmnt = connection.prepareStatement(pay)) {

            Alert alert;

            // Check if the balance or total is 0
            if (balance == 0 || totalP == 0) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Invalid payment details.");
                alert.showAndWait();
            } else {
                // Set the values for the payment insertion
                pstmnt.setInt(1, customerID); // Assuming customerID is an integer
                pstmnt.setDouble(2, totalP);  // Total payment amount
                pstmnt.setDate(3, new java.sql.Date(System.currentTimeMillis()));  // Set current date

                // Execute the payment insert
                pstmnt.executeUpdate();

                // Reset order information
                order_total.setText("$0.00");
                order_balance.setText("$0.00");
                order_amount.setText("");  // Clear the entered amount field

                orderDisplayTable();

                // Optionally, show a success alert here
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Payment Successful");
                alert.setHeaderText(null);
                alert.setContentText("Your payment has been successfully processed.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Database Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("An error occurred while processing the payment. Please try again.");
            errorAlert.showAndWait();
        }
    }

    private int item;

    public void orderSelect() {
        // Get the selected product from the TableView
        Product product = order_tableview.getSelectionModel().getSelectedItem();

        // Get the index of the selected row
        int num = order_tableview.getSelectionModel().getSelectedIndex();

        // Check if a valid item is selected (index is not -1, which means no item selected)
        if (num < 0) {
            return; // Exit the method if no product is selected
        }

        // Retrieve the ID of the selected product
        item = product.getId();
    }

    public void orderRemove() {
        // SQL query to delete a product by its ID
        String delete = "DELETE FROM product_info WHERE id = ?";

        try (Connection connection = new Database().Link(); // Use try-with-resources to automatically close the connection
             PreparedStatement pstmnt = connection.prepareStatement(delete)) {

            // Check if an item has been selected
            if (item <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No product selected for removal.");
                alert.showAndWait();
                return; // Exit if no valid product is selected
            }

            // Confirm before deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to remove this item?");
            alert.setContentText("This action cannot be undone.");

            if (alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent()) {
                // Set the product ID in the prepared statement
                pstmnt.setInt(1, item);

                // Execute the deletion
                int rowsAffected = pstmnt.executeUpdate();

                if (rowsAffected > 0) {
                    // If deletion is successful, show confirmation
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Product removed successfully.");
                    successAlert.showAndWait();

                    // Update the UI or table view (if necessary)
                    orderDisplayTable(); // Call this method to update the TableView
                    orderTotal(); // Recalculate the total after removing an item
                    orderDisplayTotal(); // Update the total display in the UI

                    order_amount.setText("");
                    order_total.setText("$0.0");
                } else {
                    // Handle the case where the deletion failed
                    Alert failureAlert = new Alert(Alert.AlertType.ERROR);
                    failureAlert.setTitle("Error");
                    failureAlert.setHeaderText(null);
                    failureAlert.setContentText("Failed to remove the product. Please try again.");
                    failureAlert.showAndWait();
                }
            }

        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Database Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("An error occurred while trying to remove the product.");
            errorAlert.showAndWait();
        }
    }

        public void initialize() throws SQLException {
        // Set default value for ComboBoxes (before loading actual data)
        ObservableList<String> defaultOptions = FXCollections.observableArrayList("Choose...");
        order_productID.setItems(defaultOptions);
        order_productNAME.setItems(defaultOptions);

        // Set default selection
        order_productID.getSelectionModel().select("Choose...");
        order_productNAME.getSelectionModel().select("Choose...");

            // Ensure ComboBoxes are populated first
            orderProductID();
            orderSpinner();

            // Fetch and display the product data
            orderListData();
            orderDisplayTable();  // Display the table

            // Initialize total
            orderDisplayTotal();
    }
}


