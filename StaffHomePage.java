package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the staff.
 */

public class StaffHomePage {
	
	private final DatabaseHelper databaseHelper; //Added databasehelper for use in the future

    public StaffHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Show the Staff Home Page.
     * @param primaryStage
     */
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Header label
        Label userLabel = new Label("Hello, Staff!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Logout button
        Button logoutButton = new Button("LOGOUT");
        logoutButton.setOnAction(event -> {
            DatabaseHelper.cur_user = null;
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

        // Go to Conversations button
        Button goToConversationsBtn = new Button("Go to Conversations");
        goToConversationsBtn.setOnAction(e -> {
            ConversationsPage conversationsPage = new ConversationsPage(DatabaseHelper.cur_user.getUserName(), databaseHelper);
            conversationsPage.show(primaryStage);
        });

        // View all questions button
        Button viewQuestionsButton = new Button("View All Questions");
        viewQuestionsButton.setOnAction(e -> {
            ViewAllQuestionsPage page = new ViewAllQuestionsPage(databaseHelper);
            page.show(new Stage());
        });

        // About User button
        Button aboutUserButton = new Button("About User");
        aboutUserButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("View User Info");
            dialog.setHeaderText("Enter the username to view profile:");
            dialog.setContentText("Username:");

            dialog.showAndWait().ifPresent(username -> {
                AboutUserPage userPage = new AboutUserPage(databaseHelper, username);
                userPage.show(new Stage());
            });
        });

        // Spacer for layout formatting
        Label spacerLabel = new Label("\n\n\n");
        spacerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        layout.getChildren().addAll(
            userLabel,
            goToConversationsBtn,
            viewQuestionsButton,
            aboutUserButton,
            spacerLabel,
            logoutButton
        );

        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Staff Page");
    }
}
