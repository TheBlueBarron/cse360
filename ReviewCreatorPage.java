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
import javafx.scene.control.CheckBox;
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

public class ReviewCreatorPage {
	private final DatabaseHelper dbHelper; //Added databasehelper for use in the future
	
    private ObservableList<Review> reviewsList;
    private ListView<Review> reviewsListView;
	private int ans_id;

    public ReviewCreatorPage(DatabaseHelper dbHelper, int ans_id) {
       this.dbHelper = dbHelper;
       this.ans_id = ans_id;
    }

    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Create Reviews HERE!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    
	    
	    Button addReview = new Button("Add Review");
	    
	    Button editReviewButton = new Button("Edit Selected Review");
	    Button removeReviewButton = new Button("Remove Selected Review");
	    Button refreshReviewsButton = new Button("Refresh Reviews");
	    
	    Button backButton = new Button("Back");
	       
        Label spacerLabel = new Label("\n\n\n");
        spacerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        HBox reviewOperationsBox;
        reviewOperationsBox = new HBox(10, addReview, editReviewButton, removeReviewButton, refreshReviewsButton, backButton);

        
        Label newReviewLabel = new Label("Post a New Review:");
        TextField reviewTextField = new TextField();
        reviewTextField.setPromptText("Enter your review here");

        // HBox to hold the new question input fields and button.
        HBox newReviewBox = new HBox(10, reviewTextField);
        newReviewBox.setPadding(new Insets(5));
        

  
        //    public Review(int id, int answerId, String text, String author) {

        // Displays all reviews for selected answer
        Label reviewsLabel = new Label("Reviews:");
        reviewsListView = new ListView<>();
        reviewsListView.setCellFactory(param -> new ListCell<Review>() {
            @Override
            protected void updateItem(Review r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) {
                    setText(null);
                } else { // Have to get reviewer name by ID
                    int rID = r.getReviewerId();
                    String author = "Unknown"; // in case getting the author fails
                    try { 
    					 author = dbHelper.getReviewerById(rID).getName();
    				}catch (SQLException e) {
    					e.printStackTrace();
    				}
                    // Display question ID, text, and author in the list.
                    setText("[" + r.getId() + "] " + r.getText() + " (by " + author + ")");

                }
            }
        });
        
        
        // Adds review using .addReview() function in DBHelper
        addReview.setOnAction(e -> {
            // Get the currently selected question to answer.
            String rText = reviewTextField.getText().trim();
            String rAuthor = DatabaseHelper.cur_user.getUserName();
            int reviewer_id = -1;
			try {
				reviewer_id = dbHelper.getReviewerIDByUsername(rAuthor);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
            
            // Create a new answer for the selected question.
            Review newReview = new Review(ans_id, rText, reviewer_id);
            try {
                // Add the answer to the database.
                dbHelper.addReview(newReview);
                System.out.println(newReview.getId());
                //showAlert("Success", "Answer posted successfully!");
                // Clear input fields and reload answers for the selected question.
                loadReviews(ans_id);
            } catch (SQLException ex) {
                ex.printStackTrace();
                //showAlert("Error", "Failed to post answer: " + ex.getMessage());
            }
        });
        
        
        // Edits review and auto-refreshes review based on selected answer
        editReviewButton.setOnAction(e -> {
            Review selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
            if (selectedReview == null) {
                showAlert("Error", "Please select an answer to edit.");
                return;
            }
            // Open a dialog with the current review text for editing.
            TextInputDialog dialog = new TextInputDialog(selectedReview.getText());
            dialog.setTitle("Edit Review");
            dialog.setHeaderText("Edit the Review text");
            dialog.setContentText("New Review text:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newText -> {
                if (newText.trim().isEmpty()) {
                    showAlert("Error", "Answer text cannot be empty.");
                    return;
                }
                try {
                	// updates the list to the current database data
                    if (dbHelper.updateReviewText(selectedReview.getId(), newText.trim())) {
                        showAlert("Success", "Answer updated successfully!");
                        loadReviews(ans_id);
                        //Question selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Database error: " + ex.getMessage());
                }
            });
        });
        
        // goes back to discussion board as a reviewer
        backButton.setOnAction(event -> {
        	new DiscussionPage(dbHelper).show(primaryStage);
        });
        
        
        // Removes review based on selected answer
        removeReviewButton.setOnAction(e -> {
            Review selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
            if (selectedReview == null) {
                showAlert("Error", "Please select an answer to delete.");
                return;
            }
            // Confirm deletion of the answer.
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setContentText("Are you sure you want to delete this review?");
            Optional<ButtonType> response = confirm.showAndWait();
            if (response.isPresent() && response.get() == ButtonType.OK) {
                try {
                    if (dbHelper.deleteReview(selectedReview.getId())) {
                        showAlert("Success", "Review deleted successfully!");
                        loadReviews(ans_id);
                    } else {
                        showAlert("Failure", "Review deleted unsuccessfully!");

                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Database error: " + ex.getMessage());
                }
            }
        });
	    layout.getChildren().addAll(reviewsLabel, spacerLabel, backButton, reviewsListView, newReviewBox, reviewOperationsBox);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
	    
        // ---------------- Load Data ----------------
        // Load existing reviews from the database.
        loadReviews(ans_id);
        
        
    }
    
    
  // Obtains current list of reviews for given answer_id
    
    private void loadReviews(int answer_id) {
        try {
            List<Review> rList = dbHelper.getReviewsForAnswers(answer_id);
            reviewsList = FXCollections.observableArrayList(rList);
            reviewsListView.setItems(reviewsList);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to load answers: " + ex.getMessage());
        }
    }
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}


