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
 * <p> Title: Multi-role User Page </p>
 *
 * <p> Description: 
 * 		This page allows the user to select which role they would like to enter.
 * 		If the user does not have multiple roles, they will be directed elsewhere.
 * 
 * 		This should also replace the welcome page that shows by default for users
 * 		with only one role.
 * </p>
 *
 * @author Wednesday 44 of CSE 360
 */
public class MultiRoleUserPage {
	
	private final DatabaseHelper databaseHelper;
	
	/**
	 * Constructor of a new MultiRoleUserPage.
	 * 
	 * @param databaseHelper	DatabaseHelper object to handle database operations.
	 */
    public MultiRoleUserPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
    /**
     * Shows the MultiRoleUserPage.
     * 
     * @param primaryStage	Stage object to display the scene on.
     * @param user			User object to retrieve the roles of.
     */
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
            	// Set current user's role to selected role for use in other operations
            	DatabaseHelper.cur_user.setRole(role);
            	// Cascading ifs to determine where to direct the user based on their selection
            	if(role.equals("admin")) {
    	    		new AdminHomePage(databaseHelper).show(primaryStage);
    	    	}
    	    	else if(role.equals("user")) {
    	    		new UserHomePage(databaseHelper).show(primaryStage);
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
    	    	else if(role.equals("student")) {
    	    		new StudentHomePage(databaseHelper).show(primaryStage);
    	    	}
    	    	else {
    	    		// By default, display the user home page
    	    		new UserHomePage(databaseHelper).show(primaryStage);
    	    	}
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "No selection made!");
                errorAlert.showAndWait();
            }
        });
        
        layout.getChildren().addAll(welcomeLabel, titleLabel, roleComboBox, selectButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Role Selection Page");
    }
}
