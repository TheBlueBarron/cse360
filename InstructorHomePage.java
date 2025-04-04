package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * <p> Title: Instructor Home Page. </p>
 * 
 * <p> Description: This page displays a welcome message and the operations an instructor can perform. </p>
 * 
 * @author Wednesday 44 of CSE 360
 */
public class InstructorHomePage {
	
	private final DatabaseHelper databaseHelper;
	
	/**
	 * Constructor to create a new InstructorHomePage.
	 * 
	 * @param databaseHelper	DatabaseHelper object to handle database operations.
	 */
    public InstructorHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;  
    }
    
    /**
     * Shows the Instructor Home page.
     * 
     * @param primaryStage	Stage object to display the scene on.
     */
    public void show(Stage primaryStage) {
    	
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello instructor
	    Label userLabel = new Label("Hello, Instructor!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to see role requests page
	    Button roleRequestPageButton = new Button("See Role Requests");
	    
	    Button logoutButton = new Button("LOGOUT");
	 	       
	    Label spacerLabel = new Label("\n\n\n");
	    spacerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // ---------- Button Handlers ---------- //
	    logoutButton.setOnAction(event -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	    });	
	    
	    roleRequestPageButton.setOnAction(event -> {
	    	new RoleRequestPage(databaseHelper).show(primaryStage);
	    });
	    
	    layout.getChildren().addAll(userLabel, roleRequestPageButton, spacerLabel, logoutButton); 
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Instructor Page");
	    
	    }
    	
    }
