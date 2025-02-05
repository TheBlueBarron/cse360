package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	
	private final DatabaseHelper databaseHelper;
	public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(5);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin!");
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	 // Button to invite users // edit begin
        Button inviteButton = new Button("Invite User");
        inviteButton.setOnAction(a -> {
            new InvitationPage().show(databaseHelper, primaryStage);
        });
        
        // Button to set one-time password
        Button oneTimePasswordButton = new Button("Set One-Time Password");
        oneTimePasswordButton.setOnAction(event -> {
            new OneTimePasswordPage().show(databaseHelper, primaryStage);
        });
        // Button to delete user
        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.setOnAction(event -> {
        	new DeleteUserPage().show(databaseHelper, primaryStage);
        });
        // Button to list all users
        Button listUsersButton = new Button("List Users");
        listUsersButton.setOnAction(event -> {
        	new ListUsersPage().show(databaseHelper, primaryStage);
        });
        // Button to manage roles
        Button manageRolesButton = new Button("Manage Roles");
        manageRolesButton.setOnAction(event -> {
        	new ManageRolesPage().show(databaseHelper, primaryStage);
        });
        
        // edit ends

	    layout.getChildren().addAll(adminLabel, inviteButton, oneTimePasswordButton, deleteUserButton, listUsersButton, manageRolesButton);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
	    primaryStage.show(); //edited
    }
}