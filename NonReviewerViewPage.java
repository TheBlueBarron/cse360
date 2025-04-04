package application;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import application.Review;

/**
 * <p> Title: Non-reviewer View Page. </p>
 * 
 * <p> Description: This page displays all reviews for an answer without any of the associated Reviewer operations. </p>
 * 
 * @author Wednesday 44 of CSE 360
 */
@SuppressWarnings("unused")
public class NonReviewerViewPage {
	private final DatabaseHelper databaseHelper; //Added databasehelper for use in the future
    private ObservableList<Review> reviewsList;
    private ListView<Review> reviewsListView;
	private int ans_id;
    
	/**
	 * Constructor of a new NonReviewerViewPage.
	 * 
	 * @param dbHelper	DatabaseHelper object to handle database operations.
	 * @param ans_id	ID of the answer to create a review for.
	 */
	public NonReviewerViewPage(DatabaseHelper databaseHelper, int ans_id) {
       this.databaseHelper = databaseHelper;
       this.ans_id = ans_id;
    }
	
	/**
	 * Shows the NonReviewerViewPage.
	 * 
	 * @param primaryStage	Stage object to display the scene on.
	 */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

	    Button logoutButton = new Button("LOGOUT");
	    Button backButton = new Button("Back");
        Label spacerLabel = new Label("\n\n\n");
        spacerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        
        Label reviewsLabel = new Label("Reviews:");
        reviewsListView = new ListView<>();
        reviewsListView.setCellFactory(param -> new ListCell<Review>() {
            @Override
            protected void updateItem(Review r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) {
                    setText(null);
                } else {
                    // Display question ID, text, and author in the list.
                    setText("[" + r.getId() + "] " + r.getText() + " (by " + r.getAuthor() + ")");
                }
            }
        });
        
        
        logoutButton.setOnAction(event -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        backButton.setOnAction(event -> {
        	new DiscussionPage(databaseHelper).show(primaryStage);

        });
        
	    layout.getChildren().addAll(reviewsListView, backButton);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Reviews");
	    
	    loadReviews(ans_id);
    	
    }
    
    /**
     * Obtains current list of reviews for given answer ID.
     * 
     * @param answer_id		Integer to find the answer to retrieve the reviews of.
     */
    private void loadReviews(int answer_id) {
        try {
            List<Review> rList = databaseHelper.getReviewsForAnswers(answer_id);
            reviewsList = FXCollections.observableArrayList(rList);
            reviewsListView.setItems(reviewsList);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to load answers: " + ex.getMessage());
        }
    }
    
    /******
     * Displays an alert to the UI.
     * 
     * @param title		String of the title to display with the alert.
     * @param message	String of the message to display with the alert.
     */
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}

