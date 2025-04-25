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
import javafx.scene.control.Alert.AlertType;
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

/**
 * <p> Title: Reviewer Rating Page. </p>
 * 
 * <p> Description: This page displays students a list of reviewers for them to curate. </p>
 * 
 * @author Wednesday 44 of CSE 360
 */

@SuppressWarnings("unused")
class ReviewerRatingPage {

		private final DatabaseHelper databaseHelper; 
	    private ObservableList<Reviewer> reviewersList;
	    private ListView<Reviewer> reviewersListView;
		/**
		 * Constructor of a new ReviewerRatingPage.
		 * 
		 * @param dbHelper	DatabaseHelper object to handle database operations.
		 * @param ans_id	ID of the answer to create a review for.
		 */
		public ReviewerRatingPage(DatabaseHelper databaseHelper) {
	       this.databaseHelper = databaseHelper;
	    }
	    
		/**
		 * Shows the reviewer rating page.
		 * 
		 * @param primaryStage	Stage object to display the scene on.
		 */
		public void show(Stage primaryStage) {
	    	VBox layout = new VBox();
		    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		    Button backButton = new Button("Back");
		    Button likeReviewerButton = new Button("Like");
		    Button dislikeReviewerButton = new Button("Dislike");
		    Button viewProfileButton = new Button("View Profile");
		    Label spacerLabel = new Label("\n\n\n");
	        Label spacerLabel2 = new Label("\n\n");
	        spacerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	        
	        
	        Label reviewersLabel = new Label("Reviewers:");
	        reviewersListView = new ListView<>();
	        reviewersListView.setCellFactory(param -> new ListCell<Reviewer>() {
	            @Override
	            protected void updateItem(Reviewer r, boolean empty) {
	                super.updateItem(r, empty);
	                if (empty || r == null) {
	                    setText(null);
	                } else {    	
	                	String trusted = "";
	                	if(r.isTrusted() == true){
	                	    trusted = "trusted.";
	                	}
	                	else {
	                		trusted = "untrusted.";
	                	}
	                    // Display the reviewer's name and rating.
	                    setText("[Reviewer: " + r.getName() + "] Rating: " + r.getRating() + " || reviewer is " + trusted);
	                }
	            }
	        });
	        
	        viewProfileButton.setOnAction(e -> {
	            Reviewer selectedReviewer = reviewersListView.getSelectionModel().getSelectedItem();
	            if (selectedReviewer != null) {
	                new ReviewerProfilePage(databaseHelper, selectedReviewer.getName(), true).show(primaryStage, "discussion");
	            } else {
	                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a reviewer first.");
	                alert.showAndWait();
	            }
	        });
	        
	        
	       
	        backButton.setOnAction(event -> {
	        	new StudentHomePage(databaseHelper).show(primaryStage);

	        });
	        
	        // Button for students to like a reviewer
	        likeReviewerButton.setOnAction(event -> {
	    		Reviewer selectedReviewer = reviewersListView.getSelectionModel().getSelectedItem();
	    		if(selectedReviewer != null) {
		    			selectedReviewer.addLike();  // Add a like to the correct reviewer
		    			// update database after liking
		    			try {
		    				databaseHelper.updateReviewer(selectedReviewer);
		    			} catch (SQLException e) {
		    				e.printStackTrace();
		    				showAlert("Error", "Failed to get reviewer to like." + e);
		    			}
		    	// Load to see change
			    loadReviewers();
	    		}
	
	    		else {
	    			Alert alert = new Alert(AlertType.WARNING); // alert for trying to like without selecting a review
	    			alert.setTitle("Failure");
	    			alert.setContentText("You must select a reviewer to like");
	    			alert.showAndWait();
	    		}
	        });
	        
	        // Button for students to dislike reviewers
	        dislikeReviewerButton.setOnAction(event -> {
	    		Reviewer selectedReviewer = reviewersListView.getSelectionModel().getSelectedItem();
	    		if(selectedReviewer != null) {
	    			selectedReviewer.addDislike(); // Add a dislike to the correct reviewer
	    			// update database after disliking
	    			try {
	    				databaseHelper.updateReviewer(selectedReviewer);
	    			} catch (SQLException e) {
	    				e.printStackTrace();
	    				showAlert("Error", "Failed to get reviewer to dislike." + e);
	    			}
	    		// Load to see change
	    		loadReviewers();
	    		}
	    		else {
	    			Alert alert2 = new Alert(AlertType.WARNING); // alert for trying to dislike without selecting a review
	    			alert2.setTitle("Failure");
	    			alert2.setContentText("You must select a reviewer to dislike");
	    			alert2.showAndWait();
	    		}
	        });

	        layout.setSpacing(10);
		    layout.getChildren().addAll(reviewersListView, viewProfileButton, likeReviewerButton, dislikeReviewerButton, spacerLabel2, backButton);
		    Scene userScene = new Scene(layout, 800, 400);

		    // Set the scene to primary stage
		    primaryStage.setScene(userScene);
		    primaryStage.setTitle("Reviewer Rating Page");
		    
		    loadReviewers();
	    	
	    }
		/**
	     * Obtains current list of reviewers with updated information.
	     * 
	     * @param answer_id		Integer to find the answer to retrieve the reviews of.
	     */
	    private void loadReviewers() {
	        try {
	            List<Reviewer> rList = databaseHelper.getAllReviewers();
	            reviewersList = FXCollections.observableArrayList(rList);
	            reviewersListView.setItems(reviewersList);
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
