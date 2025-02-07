package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the instructor.
 */

public class InstructorHomePage {
	
	private final DatabaseHelper databaseHelper; //Added databasehelper for use in the future
	
    public InstructorHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;  
    }
    
    public void show(Stage primaryStage) {
    	
    	

    	
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello instructor
	    
	    		
	    	    Label userLabel = new Label("Hello, Instructor!"); 
	    
	    	
	    
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    layout.getChildren().add(userLabel);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Instructor Page");
	    
	    }
    	
    }
