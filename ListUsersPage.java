package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * ListUsersPage class displays all users in the db in a text area.
 */
public class ListUsersPage {

    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display the title of the page
        Label listOfUsers = new Label("List of Users");
        listOfUsers.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // TextArea to display users
        TextArea usersList = new TextArea();
        usersList.setEditable(false);  //  TextArea non-editable
        usersList.setPrefHeight(200);  //   height of TBox

        // Button to fetch and display users
        Button displayButton = new Button("Display Users");
        displayButton.setOnAction(a -> {
            String userData = databaseHelper.listUsers(); // get user data from the database
            usersList.setText(userData); // Display the user data in the TextBox
        });
        
        Button showBackButton = new Button("Back"); ;
        showBackButton.setOnAction(a -> {
        	new AdminHomePage(databaseHelper).show(primaryStage);
        });

        layout.getChildren().addAll(listOfUsers, usersList, displayButton, showBackButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("List Users Page");
    }
}
