package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class ReviewerProfilePage {

    private final DatabaseHelper databaseHelper;
    private final String username;
    private final boolean isSet;

    public ReviewerProfilePage(DatabaseHelper databaseHelper, String user, boolean isSet) {
        this.databaseHelper = databaseHelper;
        this.username = user;
        this.isSet = isSet;
    }

    public void show(Stage primaryStage, String location) {        
        VBox layout = new VBox(10);
        HBox backButtons = new HBox(560);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        try {
            int reviewerId = databaseHelper.getReviewerIDByUsername(username);
            Reviewer reviewer = databaseHelper.getReviewerById(reviewerId);

            Label nameLabel = new Label("Reviewer Name: " + reviewer.getName());
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"); // took mhow e too long to figure out how to bold text
            layout.getChildren().add(nameLabel);

            if (!isSet) {
                Label promptLabel = new Label("Please add your experiences here before continuing.");
                TextField xpField = new TextField();
                xpField.setPromptText("Enter experience here: ");

                Button submitXP = new Button("Submit");
                submitXP.setOnAction(e -> {
                    String xpInput = xpField.getText();
                    if (!xpInput.isEmpty()) {
                        reviewer.setXp(xpInput);
                        try {
                            databaseHelper.updateReviewer(reviewer);
                            new ReviewerProfilePage(databaseHelper, username, true).show(primaryStage, location);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                layout.getChildren().addAll(promptLabel, xpField, submitXP);
            } else {
                Label xpLabel = new Label("Experience: " + reviewer.getXP());
                xpLabel.setStyle("-fx-font-size: 14px;");
                layout.getChildren().add(xpLabel);
            }

           
            ListView<Review> reviewsListView = new ListView<>();

            List<Review> reviews = databaseHelper.getReviewsByReviewerId(reviewerId);
            reviewsListView.getItems().addAll(reviews);
 // all of users reviews here
            reviewsListView.setCellFactory(param -> new ListCell<Review>() {
                @Override
                protected void updateItem(Review review, boolean empty) {
                    super.updateItem(review, empty);
                    if (empty || review == null) {
                        setText(null);
                    } else {
                        setText("[" + review.getId() + "] " + review.getText() +
                                " | Rating: " + reviewer.getRating() +
                                " | Likes: " + reviewer.getLikeCount() +
                                " | Dislikes: " + reviewer.getDislikeCount());
                    }
                }
            });

            Label reviewListLabel = new Label("Reviews:");
            reviewListLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
            layout.getChildren().addAll(reviewListLabel, reviewsListView, backButtons);

            // --- Back Buttons ---
            Button backButton = new Button("Back");
            Button homePageButton = new Button("Home Page");
            Button discussionPageButton = new Button("Disscussion Board");
            backButtons.getChildren().addAll(discussionPageButton, homePageButton);
            discussionPageButton.setAlignment(Pos.CENTER_RIGHT);
            homePageButton.setAlignment(Pos.CENTER_RIGHT);
            /*
             * Voodoo magic to differentiate between a discussion user viewing profiles, and a reviewer viewing it
             */
            backButton.setOnAction(e -> {
                if ("discussion".equals(location)) {
                    new FindReviewerPage(databaseHelper).show(primaryStage);  
                } else if ("reviewerpage".equals(location)) {
                    new ReviewerHomePage(databaseHelper).show(primaryStage); 
                }
            });
            
            homePageButton.setOnAction(e -> {
            	String role = DatabaseHelper.cur_user.getRole();
              	if(role.equals("admin")) {
    	    		new AdminHomePage(databaseHelper).show(primaryStage);
    	    	}
    	    	else if(role.equals("user")) {
    	    		new UserHomePage(databaseHelper).show(primaryStage);
    	    	}
    	    	else if(role.equals("student")) {
    	    		new StudentHomePage(databaseHelper).show(primaryStage); 
    	    	}
    	    	else if(role.equals("instructor")) {
    	    		new InstructorHomePage(databaseHelper).show(primaryStage); 
    	    	}
    	    	else if(role.equals("staff")) {
    	    		new StaffHomePage(databaseHelper).show(primaryStage); 
    	    	}
    	    	else if(role.equals("reviewer")){
    	    		new ReviewerHomePage(databaseHelper).show(primaryStage);  
    	    	}
            });
            
            discussionPageButton.setOnAction(e -> {
            	new DiscussionPage(databaseHelper).show(primaryStage);
            });
            

            //layout.getChildren().add(backButton);

        } catch (Exception e) {
            e.printStackTrace();
                    }

        Scene profileScene = new Scene(layout, 800, 500);
        primaryStage.setScene(profileScene);
        primaryStage.setTitle("Reviewer Profile");
        primaryStage.show();
    }
}
