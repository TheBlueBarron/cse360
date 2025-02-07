package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 * Class written by Jaari Moreno for
 * Wednesday 44 group of CSE360
 *
 * This page allows the user to select which role they would like to enter.
 * If the user does not have multiple roles, they will be directed elsewhere.
 * 
 * This should also replace the welcome page that shows by default for users
 * with only one role.
 */
public class MultiRoleUserPage {
	
	private final DatabaseHelper databaseHelper;

    public MultiRoleUserPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
	public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Label to welcome user
        Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Label to display the title of the page
        Label titleLabel = new Label("Select your role:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        /*
         * If a user is directed here, it is assumed they have multiple roles delimited by
         * a comma; this tokenizes each role to allow for use in the ComboBox
         */
        String[] options = user.getRole().split(",");
        
        // ComboBox to select the new role
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(options);
        roleComboBox.setPromptText("Select");

        // Button to apply the role change
        Button selectButton = new Button("Continue to your Page");
        selectButton.setOnAction(a -> {
            String role = roleComboBox.getValue();
            if (role != null) {
            	// Cascading ifs to determine where to direct the user based on their selection
            	if(role.equals("admin")) {
    	    		new AdminHomePage(databaseHelper).show(primaryStage);
    	    	}
    	    	else if(role.equals("user")) {
    	    		new UserHomePage().show(primaryStage);
    	    	}
    	    	else if(role.equals("staff")) {
    	    		new StaffHomePage(databaseHelper).show(primaryStage);
    	    	}
    	    	else if(role.equals("instructor")) {
    	    		new InstructorHomePage(databaseHelper).show(primaryStage);
    	    	}
    	    	else if(role.equals("reviewer")) {
    	    		new ReviewerHomePage(databaseHelper).show(primaryStage);
    	    	}
    	    	else {
    	    		new UserHomePage().show(primaryStage);
    	    	}
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "No selection made!");
                errorAlert.showAndWait();
            }
        });
        
        Button showBackButton = new Button("Back"); ;
        showBackButton.setOnAction(a -> {
        	new AdminHomePage(databaseHelper).show(primaryStage);
        });

        layout.getChildren().addAll(welcomeLabel, titleLabel, roleComboBox, selectButton, showBackButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Role Selection Page");
    }
}
