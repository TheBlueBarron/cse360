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
import databasePart1.GlobalVars;

public class ConversationsPage {
    private String currentUserName;
    private DatabaseHelper dbHelper;

    public ConversationsPage(String userName, DatabaseHelper dbHelper) {
        this.currentUserName = userName;
        this.dbHelper = dbHelper;
    }

    public void show(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label header = new Label("Your Conversations");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        root.getChildren().add(header);

        Button newConvoBtn = new Button("Start New Conversation");
        newConvoBtn.setOnAction(e -> showNewConversationDropdown(primaryStage));
        root.getChildren().add(newConvoBtn);
        
        Button showBackButton = new Button("Back"); ;
        showBackButton.setOnAction(a -> {
        	if (GlobalVars.cur_user.getRole()=="admin") {
        	new AdminHomePage(dbHelper).show(primaryStage);}
        	if (GlobalVars.cur_user.getRole()=="student") {
            new StudentHomePage(dbHelper).show(primaryStage);}
        	if (GlobalVars.cur_user.getRole()=="reviewer") {
            new ReviewerHomePage(dbHelper).show(primaryStage);}
        	if (GlobalVars.cur_user.getRole()=="staff") {
            new StaffHomePage(dbHelper).show(primaryStage);}
        	if (GlobalVars.cur_user.getRole()=="instructor") {
            new InstructorHomePage(dbHelper).show(primaryStage);}
        });
        HBox topBar = new HBox(showBackButton);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(0, topBar); 
        try {
            List<Conversations> conversations = dbHelper.getConversationsForUser(currentUserName);

            for (Conversations conv : conversations) {
                Message latest = dbHelper.getMostRecentMessage(conv.getId());

                String withUser = conv.getParticipent_1_id().equals(currentUserName)
                        ? conv.getParticipent_2_id()
                        : conv.getParticipent_1_id();

                String previewText = (latest != null) ? latest.getText() : "(no messages yet)";
                Button convoButton = new Button("With: " + withUser + "\nLatest: " + previewText);
                convoButton.setMaxWidth(Double.MAX_VALUE);

                convoButton.setOnAction(e -> {
                    MessagePage messagePage = new MessagePage(conv.getId(), currentUserName, dbHelper);
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

        Scene scene = new Scene(scrollPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Conversations");
        primaryStage.show();
    }

    private void showNewConversationDropdown(Stage primaryStage) {
        try {
            List<User> users = dbHelper.getAllUsersExcept(currentUserName);
            if (users.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No other users available.");
                return;
            }

            ComboBox<User> userDropdown = new ComboBox<>();
            userDropdown.getItems().addAll(users);
            userDropdown.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    setText(empty || user == null ? null : user.getUserName());
                }
            });
            userDropdown.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    setText(empty || user == null ? null : user.getUserName());
                }
            });

            userDropdown.getSelectionModel().selectFirst();

            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Start New Conversation");

            ButtonType startButtonType = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(startButtonType, ButtonType.CANCEL);

            VBox content = new VBox(10, new Label("Select a user:"), userDropdown);
            content.setPadding(new Insets(10));
            dialog.getDialogPane().setContent(content);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == startButtonType) {
                    return userDropdown.getValue();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(selectedUser -> {
                try {
                    boolean success = dbHelper.createConversation(currentUserName, selectedUser.getUserName());
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Conversation started!");
                        new ConversationsPage(currentUserName, dbHelper).show(primaryStage);
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Conversation already exists.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Failed to create conversation.");
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unable to load users.");
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
