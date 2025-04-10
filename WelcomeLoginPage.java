package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Show the Welcome Login Page.
     * @param primaryStage
     * @param user
     */
    public void show( Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue to your Page");
	    continueButton.setOnAction(a -> {
	    	String role =user.getRole();
	    	System.out.println(role);
	    	
	    	if(role.equals("admin")) {
	    		new AdminHomePage(databaseHelper).show(primaryStage);
	    	}
	    	else if(role.equals("user")) {
	    		new UserHomePage(databaseHelper).show(primaryStage);
	    	}
	    	else if(role.equals("student")) {
	    		new StudentHomePage(databaseHelper).show(primaryStage);  //show homepage for student
	    	}
	    	else if(role.equals("instructor")) {
	    		new InstructorHomePage(databaseHelper).show(primaryStage); //show homepage for instructor
	    	}
	    	else if(role.equals("staff")) {
	    		new StaffHomePage(databaseHelper).show(primaryStage); //show homepage for staff
	    	}
	    	else if(role.equals("reviewer")){
	    		new ReviewerHomePage(databaseHelper).show(primaryStage);  //show homepage for staff
	    	}
 	    	
	    });
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    // "Invite" button for admin to generate invitation codes
	    if ("admin".equals(user.getRole())) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> {
                new InvitationPage().show(databaseHelper, primaryStage);
            });
            layout.getChildren().add(inviteButton);
        }

	    layout.getChildren().addAll(welcomeLabel,continueButton,quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}