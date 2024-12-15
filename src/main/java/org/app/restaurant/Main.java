package org.app.restaurant;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/login_form.fxml"));
        Parent root = loader.load();
        Stage secondaryStage = new Stage();
        secondaryStage.setScene(new Scene(root));
        secondaryStage.setTitle("Login");
        secondaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}