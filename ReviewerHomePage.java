package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the reviewer.
 */

public class ReviewerHomePage {
	
	private final DatabaseHelper databaseHelper; //Added databasehelper for use in the future
	
    public ReviewerHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;  
    }
    
    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello reviewer
	    Label userLabel = new Label("Hello, Reviewer!"); 
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to view private messages
	    Button pmButton = new Button("View private messages");
	    
	    // Button to go to discussion page
	    Button discussionPageButton = new Button("Discussion Board");
	    
	    // Logout button
	    Button logoutButton = new Button("LOGOUT");
          
        Label spacerLabel = new Label("\n\n\n");
        spacerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        logoutButton.setOnAction(event -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        discussionPageButton.setOnAction(event -> {
        	new DiscussionPage(databaseHelper).show(primaryStage);
        });

	    layout.getChildren().addAll(userLabel, pmButton, discussionPageButton, spacerLabel, logoutButton); 
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Reviewer Page");
	    }
}
