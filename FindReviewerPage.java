package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class FindReviewerPage {

    private final DatabaseHelper databaseHelper;

    public FindReviewerPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label heading = new Label("Select a Reviewer to View Profile");
        ListView<Reviewer> reviewerListView = new ListView<>();

        try {
            List<Reviewer> reviewers = databaseHelper.getAllReviewers(); 
            reviewerListView.getItems().addAll(reviewers);

            reviewerListView.setCellFactory(param -> new ListCell<Reviewer>() {
                @Override
                protected void updateItem(Reviewer reviewer, boolean empty) {
                    super.updateItem(reviewer, empty);
                    if (empty || reviewer == null) {
                        setText(null);
                    } else {
                        setText(reviewer.getName() + " | Rating: " + reviewer.getRating());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            
        }

        Button viewProfileButton = new Button("View Profile");
        viewProfileButton.setOnAction(e -> {
            Reviewer selectedReviewer = reviewerListView.getSelectionModel().getSelectedItem();
            if (selectedReviewer != null) {
                new ReviewerProfilePage(databaseHelper, selectedReviewer.getName(), true).show(primaryStage, "discussion");
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a reviewer first.");
                alert.showAndWait();
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new DiscussionPage(databaseHelper).show(primaryStage); 
        });

        layout.getChildren().addAll(heading, reviewerListView, viewProfileButton, backButton);

        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Select Reviewer");
        primaryStage.show();
    }
}
