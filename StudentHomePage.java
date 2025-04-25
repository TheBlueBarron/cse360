package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the student.
 */

public class StudentHomePage {
	
	private final DatabaseHelper databaseHelper; //Added databasehelper for use in the future

    public StudentHomePage(DatabaseHelper databaseHelper) {
       this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage) {
    	
    	

    	
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello Student
	    
	    		
	    Label userLabel = new Label("Hello, Student!"); 
	    Button discussionBoardButton = new Button("Go to Discussion Board");
	    Button goToConversationsBtn = new Button("Go to Conversations");
	    Button reviewerRatingButton = new Button("Curate Your Trusted Reveiwers");
        goToConversationsBtn.setOnAction(e -> {
            ConversationsPage conversationsPage = new ConversationsPage(DatabaseHelper.cur_user.getUserName(), databaseHelper);
            conversationsPage.show(primaryStage);
        });
	    discussionBoardButton.setOnAction(e -> {
	    	new DiscussionPage(databaseHelper).show(primaryStage);
	    });
	    reviewerRatingButton.setOnAction(e -> {
	    	new ReviewerRatingPage(databaseHelper).show(primaryStage);
	    });

	    	
	    
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    Button logoutButton = new Button("LOGOUT");
        
	       
        Label spacerLabel = new Label("\n\n");
        Label spacerLabel2 = new Label("\n\n");

        spacerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        
        
        logoutButton.setOnAction(event -> {
		DatabaseHelper.cur_user = null;
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
	    layout.setSpacing(10);
	    layout.getChildren().addAll(userLabel, spacerLabel, discussionBoardButton, goToConversationsBtn, reviewerRatingButton, spacerLabel2, logoutButton);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Student Page");
	    
	    }
    	
    }
