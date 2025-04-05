package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.application.Platform;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;

public class ConversationPage {
    private String currentUserId;
    private DatabaseHelper dbHelper;

    public ConversationPage(String userId, DatabaseHelper dbHelper) {
        this.currentUserId = userId;
        this.dbHelper = dbHelper;
    }

    public void show(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label header = new Label("Your Conversations");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        root.getChildren().add(header);

        try {
            List<Conversations> conversations = dbHelper.getConversationsForUser(currentUserId);

            for (Conversations conv : conversations) {
                Message latest = dbHelper.getMostRecentMessage(conv.getId());
                String withUser = conv.getParticipent_1_id().equals(currentUserId)
                        ? conv.getParticipent_2_id()
                        : conv.getParticipent_1_id();
                String previewText = (latest != null) ? latest.getText() : "(no messages yet)";

                Button convoButton = new Button("With: " + withUser + "\nLatest: " + previewText);
                convoButton.setMaxWidth(Double.MAX_VALUE);
                convoButton.setOnAction(e -> {
                    MessagePage messagePage = new MessagePage(conv.getId(), currentUserId, dbHelper);
                    messagePage.show(primaryStage);
                });

                root.getChildren().add(convoButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            root.getChildren().add(new Label("Failed to load conversations."));
        }

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Conversations");
        primaryStage.show();
    }
}
