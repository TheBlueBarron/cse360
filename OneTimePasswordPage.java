package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * OneTimePasswordPage class allows the admin to set a one-time password for a user.
 */
public class OneTimePasswordPage {

    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display the title of the page
        Label setPassword = new Label("Set One-Time Password");
        setPassword.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Text field to enter the username
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setMaxWidth(250);

        // Text field to enter the new password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter new one-time password");
        passwordField.setMaxWidth(250);

        // Button to set the one-time password
        Button setPasswordButton = new Button("Set Password");
        setPasswordButton.setOnAction(a -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            boolean result = databaseHelper.setOneTimePassword(username, password);
            Alert alert = new Alert(Alert.AlertType.INFORMATION); //Alert window for confirmation
            if (result) {
                alert.setContentText("Password set successfully for " + username);
            } else {
                alert.setContentText("Failed to set password for " + username + ", User doesnn't exist");
            }
            alert.showAndWait();
        });
        
        Button showBackButton = new Button("Back"); ;
        showBackButton.setOnAction(a -> {
        	new AdminHomePage(databaseHelper).show(primaryStage);
        });

        layout.getChildren().addAll(setPassword, usernameField, passwordField, setPasswordButton, showBackButton);
        Scene scene = new Scene(layout, 800, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Set One-Time Password Page");
    }
}
