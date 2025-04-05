package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * DeleteUserPage class allows the admin to delete a user from the system.
 */
public class DeleteUserPage {

    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display the title of the page
        Label deleteUser = new Label("Delete a User");
        deleteUser.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Text field to enter the username of the user to be deleted
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username to delete");
        usernameField.setMaxWidth(250);

        // Button to delete the user
        Button deleteUserButton = new Button("Delete User");
        
        deleteUserButton.setOnAction(a -> {
            String username = usernameField.getText();
            if (!username.isEmpty()) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + username + "?", ButtonType.YES, ButtonType.NO);
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        boolean result = databaseHelper.deleteUser(username);
                        Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                        if (result) {
                            resultAlert.setContentText("User " + username + " was successfully deleted.");
                        } else {
                            resultAlert.setContentText("Failed to delete user " + username + ".");
                        }
                        resultAlert.showAndWait();
                    }
                });
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Username cannot be empty.");
                errorAlert.showAndWait();
            }
        });
        
        Button showBackButton = new Button("Back"); ;
        showBackButton.setOnAction(a -> {
        	new AdminHomePage(databaseHelper).show(primaryStage);
        });

        layout.getChildren().addAll(deleteUser, usernameField, deleteUserButton, showBackButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Delete User Page");
    }
}