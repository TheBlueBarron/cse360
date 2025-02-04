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
        roleComboBox.getItems().addAll("admin", "user");
        roleComboBox.setPromptText("Select new role");

        // Button to apply the role change
        Button changeRoleButton = new Button("Change Role");
        changeRoleButton.setOnAction(a -> {
            String username = usernameField.getText();
            String newRole = roleComboBox.getValue();
            if (!username.isEmpty() && newRole != null) {
                boolean result = databaseHelper.manageUserRole(username, newRole);
                Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                resultAlert.setContentText(result ? "Role updated successfully." : "Failed to update role.");
                resultAlert.showAndWait();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Username and role must be entered.");
                errorAlert.showAndWait();
            }
        });

        layout.getChildren().addAll(titleLabel, usernameField, roleComboBox, changeRoleButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Role Managment Page");
    }
}
