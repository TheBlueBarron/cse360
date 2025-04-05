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

public class NonReviewerViewPage {
	private final DatabaseHelper databaseHelper; //Added databasehelper for use in the future
    private ObservableList<Review> reviewsList;
    private ListView<Review> reviewsListView;
    private List<Review> allReviews; 
    private List<Review> trustedReviews;
	private int ans_id;
    public NonReviewerViewPage(DatabaseHelper databaseHelper, int ans_id) {
       this.databaseHelper = databaseHelper;
       this.ans_id = ans_id;
    }

    @SuppressWarnings("unused")
	public void show(Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, User!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    Button logoutButton = new Button("LOGOUT");
	    Button backButton = new Button("Back");
	    Button likeReviewButton = new Button("Like");
	    CheckBox showTrusted = new CheckBox("Show Trusted Reviews");
	    Button dislikeReviewButton = new Button("Dislike");
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
                	int rID = r.getReviewerId();
                    String author = "Unknown"; // in case getting the author fails
                    try { 
    					 author = databaseHelper.getReviewerById(rID).getName();
    				}catch (SQLException e) {
    					e.printStackTrace();
    				}
                    // Display question ID, text, and author in the list.
                    setText("[" + r.getId() + "] " + r.getText() + " (by " + author + ")");
                }
            }
        });
        
        
        logoutButton.setOnAction(event -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        backButton.setOnAction(event -> {
        	new DiscussionPage(databaseHelper).show(primaryStage);

        });
        
        likeReviewButton.setOnAction(event -> {
    		Review selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
    		if(selectedReview != null) {
    		int reviewerId = selectedReview.getReviewerId();  // Get the reviewer ID from review
    		Reviewer reviewer = null;
    			try {
    				reviewer = databaseHelper.getReviewerById(reviewerId); // Get the Review from its ID
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
    			reviewer.addLike();  // Add a like to the correct reviewer
    			// update database after liking
    			try {
    				databaseHelper.updateReviewer(reviewer);
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
    		}
    		else {
    			Alert alert = new Alert(AlertType.WARNING); // alert for trying to like without selecting a review
    			alert.setTitle("Failure");
    			alert.setContentText("You must select a review to like");
    			alert.showAndWait();
    		}
        });
        
        dislikeReviewButton.setOnAction(event -> {
    		Review selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
    		if(selectedReview != null) {
    		int reviewerId = selectedReview.getReviewerId();  // Get the reviewer ID from review
    		Reviewer reviewer = null;
    			try {
    				reviewer = databaseHelper.getReviewerById(reviewerId); // Get the Review from its ID
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
    			reviewer.addDislike(); // Add a dislike to the correct reviewer
    			// update database after disliking
    			try {
    				databaseHelper.updateReviewer(reviewer);
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
    		}
    		else {
    			Alert alert2 = new Alert(AlertType.WARNING); // alert for trying to dislike without selecting a review
    			alert2.setTitle("Failure");
    			alert2.setContentText("You must select a review to dislike");
    			alert2.showAndWait();
    		}
        });
        // If trusted Reviews is selected
        showTrusted.setOnAction(event -> {
        	if(showTrusted.isSelected()) {	
        		try {
        			trustedReviews = databaseHelper.getTrustedReviewList(ans_id); 	// populate a list of trusted reviews
        			reviewsListView.getItems().setAll(trustedReviews);				// display those
        		} catch(SQLException e) {
        			e.printStackTrace();
        		}
        	} else {
        		try {
					allReviews = databaseHelper.getReviewsForAnswers(ans_id);		// populate the list with all reviews
				} catch (SQLException e) {
					e.printStackTrace();
				}
        		reviewsListView.getItems().setAll(allReviews); 						// if its not selected, show all reviews
        	}
        });
        
        
        
	    layout.getChildren().addAll(userLabel, reviewsListView, backButton, likeReviewButton, dislikeReviewButton, showTrusted);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
	    
	    loadReviews(ans_id);
    	
    }
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

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}