package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * ManageUserRolePage class allows the admin to change the role of a user in the db.
 */
public class ManageRolesPage {

    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display the title of the page
        Label titleLabel = new Label("Modify User's Role");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Text field to enter the username of the user whose role is to be modified
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setMaxWidth(200);

        // ComboBox to select the new role
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("user", "admin", "student", "instructor", "reviewer", "staff");
        roleComboBox.setPromptText("Select role");
        
        // ComboBox to select action to take with role
        ComboBox<String> actionComboBox = new ComboBox<>();
        actionComboBox.getItems().addAll("add", "replace", "remove");
        actionComboBox.setPromptText("Select action");
        
        // Button to apply the role change
        Button changeRoleButton = new Button("Change Role");
        changeRoleButton.setOnAction(a -> {
            String username = usernameField.getText();
            String role = roleComboBox.getValue();
            String action = actionComboBox.getValue();
	        // Calls the corresponding function based on which action is selected
            if (action.equals("replace")) {
            	// This will completely replace the user's role in the table
            	if (!username.isEmpty() && role != null) {
	                boolean result = databaseHelper.manageUserRole(username, role);
	                Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
	                resultAlert.setContentText(result ? "Role updated successfully." : "Failed to update role.");
	                resultAlert.showAndWait();
	            } else {
	                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Username and role must be entered.");
	                errorAlert.showAndWait();
	            }
	        } else if (action.equals("add")){
	        	// This will add a role to a user, delimited by a comma
	        	if (!username.isEmpty() && role != null) {
	                boolean result = databaseHelper.addUserRole(username, role);
	                Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
	                resultAlert.setContentText(result ? "Role added successfully." : "Failed to add role.");
	                resultAlert.showAndWait();
	        	} else {
	        		Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Username and role must be entered.");
	                errorAlert.showAndWait();
	        	}
	        } else {
	        	if (!username.isEmpty() && role != null) {
	        		boolean result = databaseHelper.removeUserRole(username, role);
	        		Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
	                resultAlert.setContentText(result ? "Role removed successfully." : "Failed to remove role.");
	                resultAlert.showAndWait();
	        	} else {
	        		Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Username and role must be entered.");
	                errorAlert.showAndWait();
	        	}
	        }
        });

        Button showBackButton = new Button("Back"); ;
        showBackButton.setOnAction(a -> {
        	new AdminHomePage(databaseHelper).show(primaryStage);
        });

        layout.getChildren().addAll(titleLabel, usernameField, roleComboBox, actionComboBox, changeRoleButton, showBackButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Role Management Page");
    }
}
