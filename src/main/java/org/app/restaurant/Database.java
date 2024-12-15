package org.app.restaurant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public Connection Link() throws SQLException {
        try {
            // Replace with your actual database details
            String url = "jdbc:mysql://localhost:3306/restaurant";
            String user = "root";
            String password = "";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // Handle exceptions properly (e.g., log or throw further)
            throw new SQLException("Error connecting to the database", e);
        }
    }
}
