package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // Allow user to pick their role
        ComboBox<String> roleField = new ComboBox<>(); //(Dalton changes) ComboBox for fixed choice dropdown, can add desired choices
        roleField.getItems().addAll("user", "student", "instructor", "staff", "reviewer"); // User can pick between required roles
        roleField.setPromptText("Select role");
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String role = roleField.getValue();
            String code = inviteCodeField.getText();
            
            String usernameErrMessage = UserNameRecognizer.checkForValidUserName(userName);
            String passwordErrMessage = PasswordEvaluator.evaluatePassword(password);
            
            try {
             // Validate username
	         if (usernameErrMessage.length() == 0) {   
	        	// Check if the user already exists
	        	 if(!databaseHelper.doesUserExist(userName)) {
	            		
	            		// Validate the password
	            		if(passwordErrMessage.length() == 0) {
	            			// Validate the invitation code
	            			if (databaseHelper.validateInvitationCode(code)) {
	            				
	            				if(role == "reviewer") {
				            		Reviewer reviewer = new Reviewer(userName);
				            		databaseHelper.saveReviewer(reviewer);
				            	}
	            			
	            				// Create a new user and register them in the database
	            				User user=new User(userName, password, role);
	            				databaseHelper.register(user);
			                
	            				// Navigate to the Welcome Login Page
	            				new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
	            			}
	            			else {
	            				errorLabel.setText("Please enter a valid invitation code");
	            			}
	            		}
	            		else {
	            			errorLabel.setText("Password - " + passwordErrMessage);
	            		}
	            	}
	            	else {
	            		errorLabel.setText("This useruserName is taken!!.. Please use another to setup an account");
	            	}
	         } 
	         else {
	        	 errorLabel.setText("Username - " + usernameErrMessage);
	         }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        
        Button showBackButton = new Button("Back"); ;
        showBackButton.setOnAction(a -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        layout.getChildren().addAll(userNameField, passwordField, inviteCodeField, 
        					  roleField, setupButton, errorLabel, showBackButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}