package application;

import databasePart1.DatabaseHelper;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {
	private final DatabaseHelper databaseHelper; //Added databasehelper for use in the future

    public UserHomePage(DatabaseHelper databaseHelper) {
       this.databaseHelper = databaseHelper;
    }

    /**
     * Show the User Home Page
     * @param primaryStage
     */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, User!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    Button logoutButton = new Button("LOGOUT");
	       
        Label spacerLabel = new Label("\n\n\n");
        spacerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        
        
        logoutButton.setOnAction(event -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

	    layout.getChildren().addAll(userLabel, spacerLabel, logoutButton);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
}