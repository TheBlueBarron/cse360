package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * AboutUserPage displays user account information,
 * including roles, account creation date, and recent activity.
 */
public class AboutUserPage {

    private DatabaseHelper dbHelper;
    private String username;

    
    public AboutUserPage(DatabaseHelper dbHelper, String username) {
        this.dbHelper = dbHelper;
        this.username = username;
    }

    
    public void show(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("About User: " + username);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label rolesLabel = new Label("Roles: loading...");
        Label createdLabel = new Label("Account Created: loading...");
        Label recentQuestionsLabel = new Label("Recent Questions:");
        ListView<String> questionList = new ListView<>();

        try {
            // Fetch user roles and creation date from DB
            String roles = dbHelper.getRolesForUser(username); // must exist in DB
            String created = dbHelper.getUserCreationDate(username); // must exist in DB
            rolesLabel.setText("Roles: " + roles);
            createdLabel.setText("Account Created: " + created);

            // Load user's recent questions
            List<Question> questions = dbHelper.getQuestionsByUser(username); // must exist in DB helper
            for (Question q : questions) {
                questionList.getItems().add("[" + q.getId() + "] " + q.getText());
            }

        } catch (Exception e) {
            rolesLabel.setText("Roles: (error)");
            createdLabel.setText("Account Created: (error)");
            e.printStackTrace();
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> primaryStage.close());

        root.getChildren().addAll(title, rolesLabel, createdLabel, recentQuestionsLabel, questionList, closeButton);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("About User");
        primaryStage.show();
    }
} 
