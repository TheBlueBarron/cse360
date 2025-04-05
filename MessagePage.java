package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;

public class MessagePage {
    private int conversationId;
    private String currentUserName;
    private DatabaseHelper dbHelper;

    private VBox messagesBox;

    public MessagePage(int conversationId, String currentUserName, DatabaseHelper dbHelper) {
        this.conversationId = conversationId;
        this.currentUserName = currentUserName;
        this.dbHelper = dbHelper;
    }

    public void show(Stage stage) {
        BorderPane root = new BorderPane();
        messagesBox = new VBox(10);
        messagesBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);

        loadMessages();

        // Input area
        TextField messageField = new TextField();
        messageField.setPromptText("Type your message...");
        Button sendButton = new Button("Send");

        sendButton.setOnAction(e -> {
            String text = messageField.getText().trim();
            if (!text.isEmpty()) {
                try {
                    dbHelper.insertMessage(conversationId, currentUserName, text);
                    messageField.clear();
                    loadMessages();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        HBox inputBox = new HBox(10, messageField, sendButton);
        inputBox.setPadding(new Insets(10));
        HBox.setHgrow(messageField, Priority.ALWAYS);

        // Back button
        Button backButton = new Button("← Back");
        backButton.setOnAction(e -> {
            ConversationsPage conversationsPage = new ConversationsPage(currentUserName, dbHelper);
            conversationsPage.show(stage);
        });

        HBox topBar = new HBox(backButton);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        root.setTop(topBar);
        root.setCenter(scrollPane);
        root.setBottom(inputBox);

        Scene scene = new Scene(root, 400, 600);
        stage.setScene(scene);
        stage.setTitle("Conversation " + conversationId);
        stage.show();
    }

    private void loadMessages() {
        messagesBox.getChildren().clear();
        try {
            List<Message> messages = dbHelper.getMessagesForConversation(conversationId);

            if (messages.isEmpty()) {
                Label emptyLabel = new Label("No messages yet. Start the conversation!");
                emptyLabel.setStyle("-fx-font-style: italic;");
                messagesBox.getChildren().add(emptyLabel);
            } else {
                for (Message msg : messages) {
                    String sender = msg.getSenderId(); // already a username
                    Label msgLabel = new Label(sender + ": " + msg.getText());
                    msgLabel.setWrapText(true);
                    msgLabel.setStyle("-fx-background-color: " +
                        (sender.equals(currentUserName) ? "#d1ffd1" : "#e6e6e6") +
                        "; -fx-padding: 5; -fx-background-radius: 5;");
                    messagesBox.getChildren().add(msgLabel);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            messagesBox.getChildren().add(new Label("Failed to load messages."));
        }
    }
}
