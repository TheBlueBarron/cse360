package application;

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
import java.sql.SQLException; 
import java.util.List;
import java.util.Optional;

/******
 *  <p> Title: Discussion Page. </p>
 * 
 *  <p> Description: This class creates a Discussion Forum page where anyone can view and post questions and answers. </p>
 *  
 *  @author Wednesday 44 of CSE 360
 */
public class DiscussionPage {
    
    // Database helper to handle DB operations
    private DatabaseHelper dbHelper;
    // Observable list for questions and its ListView for UI display
    private ObservableList<Question> questionsList;
    private ListView<Question> questionsListView;
    private List<Question> answeredQuestionsList;
    private List<Question> unansweredQuestionsList;
    private List<Question> allQuestionsList;
    // Observable list for answers and its ListView for UI display
    private ObservableList<Answer> answersList;
    private ListView<Answer> answersListView;
    // Observable lists for search results ** EDIT
    private ObservableList<Question> questionsResults;
    private ObservableList<Answer> answersResults;

    /*****
     * Constructor to create a new DiscussionPage.
     * 
     * @param dbHelper	DatabaseHelper object to handle database operations.
     */
    public DiscussionPage(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    
    /******
     * This method sets up and shows the discussion forum page on the given stage.
     * 
     * @param primaryStage	Stage object to display the scene on.
     */
    public void show(Stage primaryStage) {
    	String username = DatabaseHelper.cur_user.getUserName();
        primaryStage.setTitle(username);
        
        // ---------------- Main Layout ----------------
        // Create a vertical box as the main container with some spacing and padding.
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        
        // ---------------- New Question Section ----------------
        // Label and input fields for posting a new question.
        Label newQuestionLabel = new Label("Post a New Question:");
        TextField questionTextField = new TextField();
        questionTextField.setPromptText("Enter your question here");
        CheckBox isAnon = new CheckBox("Anonymous");
        isAnon.setIndeterminate(false);
        CheckBox showAnsweredQuestions = new CheckBox("Show Answered Questions");
        CheckBox showUnansweredQuestions = new CheckBox("Show Unanswered Questions");
        showAnsweredQuestions.setIndeterminate(false);
        showUnansweredQuestions.setIndeterminate(false);    
        Button postQuestionButton = new Button("Post Question");
        
        // HBox to hold the new question input fields and button.
        HBox newQuestionBox = new HBox(10, questionTextField, isAnon, postQuestionButton, showAnsweredQuestions, showUnansweredQuestions);
        newQuestionBox.setPadding(new Insets(5));
        
        // ---------------- Questions List Section ----------------
        // Label and ListView for showing all questions.
        Label questionsLabel = new Label("Questions:");
        questionsListView = new ListView<>();
        questionsListView.setCellFactory(param -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question q, boolean empty) {
                super.updateItem(q, empty);
                if (empty || q == null) {
                    setText(null);
                } else {
                    // Display question ID, text, and author in the list.
                    setText("[" + q.getId() + "] " + q.getText() + " (by " + q.getAuthor() + ")");
                    if(q.getIsResolved() == true) {				//sets the color to green if resolved and black otherwise
                    	setTextFill(Color.DARKSEAGREEN); 
                    }
                    else {
                    	setTextFill(Color.BLACK);
                    }
                }
            }
        });
        
        // ---------------- Question Operations Buttons ----------------
        // Buttons for refreshing, editing, and deleting questions.
        Button refreshQuestionsButton = new Button("Refresh Questions");
        Button editQuestionButton = new Button("Edit Selected Question");
        Button deleteQuestionButton = new Button("Delete Selected Question");
        Button markAsResolvedButton = new Button("Mark Selected Question & Answer As Resolved"); //*Button to mark as resolved
        HBox questionOperationsBox = new HBox(10, refreshQuestionsButton, editQuestionButton, deleteQuestionButton, markAsResolvedButton);
        questionOperationsBox.setPadding(new Insets(5));
        
        // ---------------- Search Operation Fields & Buttons ---------------- **** EDIT
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        
        // ---------------- Answers Section ----------------
        // Label and ListView for showing answers.
        Label answersLabel = new Label("Answers:");
        answersListView = new ListView<>();
        answersListView.setCellFactory(param -> new ListCell<Answer>() {
            @Override
            protected void updateItem(Answer a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) {
                    setText(null);
                } else {
                    // Display answer ID, text, and author in the list.
                    setText("[" + a.getId() + "] " + a.getText() + " (by " + a.getAuthor() + ")");
                    if(a.getResolved() == true) {				//sets the color to green if resolved and black otherwise
                    	setTextFill(Color.DARKSEAGREEN); 
                    }
                    else {
                    	setTextFill(Color.BLACK);
                    }
                }
            }
        });
        
        // ---------------- Answer Operations Buttons ----------------
        // Buttons for refreshing, editing, and deleting answers.
        Button refreshAnswersButton = new Button("Refresh Answers");
        Button editAnswerButton = new Button("Edit Selected Answer");
        Button deleteAnswerButton = new Button("Delete Selected Answer");
        Button reviewAnswerButton = new Button("Review Selected Answer");
        Button viewReviewButton = new Button("View Selected Answer Reviews");
        HBox answerOperationsBox;
        answerOperationsBox = new HBox(10, refreshAnswersButton, editAnswerButton, deleteAnswerButton, reviewAnswerButton, viewReviewButton);

        if (!DatabaseHelper.cur_user.getRole().equals("reviewer")) {
            answerOperationsBox = new HBox(10, refreshAnswersButton, editAnswerButton, deleteAnswerButton, viewReviewButton);

        }
        answerOperationsBox.setPadding(new Insets(5));

        
        // ---------------- New Answer Section ----------------
        // Label and input fields for posting a new answer.
        
        
        Label newAnswerLabel = new Label("Post a New Answer:");
        TextField answerTextField = new TextField();
        answerTextField.setPromptText("Enter your answer here");
        //TextField answerAuthorField = new TextField();
        CheckBox isAnon1 = new CheckBox("Anonymous");
        isAnon.setIndeterminate(false);
        
        
        Button postAnswerButton = new Button("Post Answer");
        Label spacerLabel = new Label("                       ");
        Button viewReviewers = new Button("View Reviewers");
        
        
        viewReviewers.setOnAction(event ->  {
        	new FindReviewerPage(dbHelper).show(primaryStage);
        });
        HBox newAnswerBox = new HBox(10, answerTextField, isAnon1, postAnswerButton, spacerLabel, viewReviewers);
        newAnswerBox.setPadding(new Insets(5));

        // ---------------- Back Button --------------------
        // Button to handle return to student page.
        Button backButton = new Button("Back");
        
        // ---------------- Assemble Layout ----------------
        // Add all sections to the main layout in order.
        mainLayout.getChildren().addAll(
        		searchField, newQuestionLabel, newQuestionBox, 
            questionsLabel, questionsListView, questionOperationsBox, 
            answersLabel, answersListView, answerOperationsBox, 
            newAnswerLabel, newAnswerBox, backButton
        );
        
        // Create a scene with the main layout and set it on the stage.
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // ---------------- Load Data ----------------
        // Load existing questions from the database.
        loadQuestions();
        
        // When a question is selected, load its associated answers.
        questionsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadAnswers(newVal.getId());
            } else {
                // If no question is selected, clear the answers list.
                answersListView.setItems(FXCollections.observableArrayList());
            }
        });
        
        // ---------------- Button Handlers ----------------
        
        // Handle posting a new question.
        
        // Handle search of questions. **** EDIT
        searchField.setOnAction(e -> {
        	String sText = searchField.getText();
        	if (sText.isEmpty()) {
        		showAlert("Notice", "No text entered!");
        		return;
        	}
        	try {
        		Question selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
        		if (selectedQuestion == null) {
        			// Gather the results of keyword from Questions database
            		List<Question> qResults = dbHelper.searchQuestions(sText);
            		if (qResults.isEmpty()) {
            			showAlert("Notice", "No results found!");
            			return;
            		}
            		questionsResults = FXCollections.observableArrayList(qResults);
                    questionsListView.setItems(questionsResults);
        		} else {
        			// Gather the results of keyword from Answers database based on question selected
        			List<Answer> aResults = dbHelper.searchAnswers(sText, selectedQuestion.getId());
        			if (aResults.isEmpty()) {
        				showAlert("Notice", "No results found!");
            			return;
        			}
        			answersResults = FXCollections.observableArrayList(aResults);
        			answersListView.setItems(answersResults);
        		}
        		return;
        	} catch (SQLException ex) {
        		ex.printStackTrace();
        		showAlert("Error", "Failed to search.");
        	}
        });
        
        
        postQuestionButton.setOnAction(e -> {
            String qText = questionTextField.getText().trim();
            String qAuthor = DatabaseHelper.cur_user.getUserName();
            
            if (isAnon.isSelected()) {
            	qAuthor = "Anonymous";
            }
            boolean isResolved = false;
            boolean flagged = false;
            if (qText.isEmpty()) {
                showAlert("Error", "Question text cannot be empty.");
                return;
            }
          
            // Create a new question object.
            Question newQuestion = new Question(qText, qAuthor, isResolved, flagged);
            try {
                // Add it to the database.
                dbHelper.addQuestion(newQuestion);
                showAlert("Success", "Question posted successfully!");
                // Clear input fields and reload questions.
                questionTextField.clear();
                isAnon1.setIndeterminate(false);
                loadQuestions();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to post question: " + ex.getMessage());
            }
        });
        
        // Handles showing answered questions list
        showAnsweredQuestions.setOnAction(e -> {
        	if(showAnsweredQuestions.isSelected()) {  // if selected
	        	try {
					answeredQuestionsList = dbHelper.getAnsweredQuestionsList(); // populate list of answered questions with DB query
					questionsListView.getItems().setAll(answeredQuestionsList);  // set the ListView for display to the answered questions
				} catch (SQLException e1) {
					e1.printStackTrace();
					showAlert("Error", "failed to get answered questions: " + e1.getMessage());
				}
        	}
        	else {
        		try {
        			allQuestionsList = dbHelper.getAllQuestions();				// if show answered question is unselected, populate list with all questions
        			questionsListView.getItems().setAll(allQuestionsList);		// set ListView to all questions
        		} catch(SQLException e2) {
        			e2.printStackTrace();
        			showAlert("Error", "Failed to get all questions: " + e2.getMessage());
        		}
        	}
        });
        
        // Handles showing unanswered questions list
        showUnansweredQuestions.setOnAction(e -> {
        	if(showUnansweredQuestions.isSelected()) {  // if selected
	        	try {
					unansweredQuestionsList = dbHelper.getUnansweredQuestionsList(); // populate list of unanswered questions with DB query
					questionsListView.getItems().setAll(unansweredQuestionsList);  // set the ListView for display to the unanswered questions
				} catch (SQLException e1) {
					e1.printStackTrace();
					showAlert("Error", "failed to get unanswered questions: " + e1.getMessage());
				}
        	}
        	else {
        		try {
        			allQuestionsList = dbHelper.getAllQuestions();				// if show unanswered question is unselected, populate list with all questions
        			questionsListView.getItems().setAll(allQuestionsList);		// set ListView to all questions
        		} catch(SQLException e2) {
        			e2.printStackTrace();
        			showAlert("Error", "Failed to get all questions: " + e2.getMessage());
        		}
        	}
        });
        
        // Handle posting a new answer.
        postAnswerButton.setOnAction(e -> {
            // Get the currently selected question to answer.
            Question selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion == null) {
                showAlert("Error", "Please select a question to answer.");
                return;
            }
            String aText = answerTextField.getText().trim();
            String aAuthor = DatabaseHelper.cur_user.getUserName();
            if (isAnon1.isSelected()) {
            	aAuthor = "Anonymous";
            }
            boolean resolved = false;
            if (aText.isEmpty()) {
                showAlert("Error", "Answer text cannot be empty.");
                return;
            }
            if (aAuthor.isEmpty()) {
                aAuthor = "Anonymous";
            }
            // Create a new answer for the selected question.
            Answer newAnswer = new Answer(selectedQuestion.getId(), aText, aAuthor, resolved);
            try {
                // Add the answer to the database.
                dbHelper.addAnswer(newAnswer);
                System.out.println(newAnswer.getId());
                showAlert("Success", "Answer posted successfully!");
                // Clear input fields and reload answers for the selected question.
                answerTextField.clear();
                isAnon1.setIndeterminate(false);
                loadAnswers(selectedQuestion.getId());
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to post answer: " + ex.getMessage());
            }
        });
        
        reviewAnswerButton.setOnAction(e -> {
            Answer selectedAnswer = answersListView.getSelectionModel().getSelectedItem();
            if (selectedAnswer == null) {
            	showAlert("Error", "Please select an answer to review.");
                return;
            }
            //Review newReview = new Review(selectedAnswer.getId(), null, null);
            
        	new ReviewCreatorPage(dbHelper, selectedAnswer.getId()).show(primaryStage);
        });
        
        // Refresh buttons for questions and answers.
        refreshQuestionsButton.setOnAction(e -> {loadQuestions(); searchField.clear();});
        refreshAnswersButton.setOnAction(e -> {
            Question selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                loadAnswers(selectedQuestion.getId());
                searchField.clear();
            }
        });
        
        // ---------------- Edit and Delete Operations for Questions ----------------
        
        // Edit a selected question.
        editQuestionButton.setOnAction(e -> {
            Question selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion == null) {
                showAlert("Error", "Please select a question to edit.");
                return;
            }
            // Open a dialog with the current text for editing.
            TextInputDialog dialog = new TextInputDialog(selectedQuestion.getText());
            dialog.setTitle("Edit Question");
            dialog.setHeaderText("Edit the question text");
            dialog.setContentText("New question text:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newText -> {
                if (newText.trim().isEmpty()) {
                    showAlert("Error", "Question text cannot be empty.");
                    return;
                }
                try {
                    if (dbHelper.updateQuestionText(selectedQuestion.getId(), newText.trim())) {
                        showAlert("Success", "Question updated successfully!");
                        loadQuestions();
                    } else {
                        showAlert("Error", "Failed to update question.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Database error: " + ex.getMessage());
                }
            });
        });
        
        // Delete a selected question.
        deleteQuestionButton.setOnAction(e -> {
            Question selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion == null) {
                showAlert("Error", "Please select a question to delete.");
                return;
            }
            // Confirm deletion with the user.
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setContentText("Are you sure you want to delete this question? It will also delete all associated answers.");
            Optional<ButtonType> response = confirm.showAndWait();
            if (response.isPresent() && response.get() == ButtonType.OK) {
                try {
                    if (dbHelper.deleteQuestion(selectedQuestion.getId())) {
                        showAlert("Success", "Question deleted successfully!");
                        loadQuestions();
                        // Clear answers if the question was deleted.
                        answersListView.setItems(FXCollections.observableArrayList());
                    } else {
                        showAlert("Error", "Failed to delete question.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Database error: " + ex.getMessage());
                }
            }
        });
        
        // Mark Question + Answer as resolved.
        markAsResolvedButton.setOnAction(e -> { 						
            Question selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();	// *get selected question
            Answer selectedAnswer = answersListView.getSelectionModel().getSelectedItem();			// and selected answer
            if(selectedQuestion != null && selectedAnswer != null) {	
            	selectedQuestion.markAsResolved();													// mark question resolved
            	selectedAnswer.markAsResolver();													// and answer
            }
            try {
            dbHelper.updateIsResolvedQuestion(selectedQuestion.getId(), true);						// update Question in DB
            dbHelper.updateAnswerResolved(selectedAnswer.getId(), true);							// and answer*
            } catch(SQLException ex){
            	ex.printStackTrace();
            	showAlert("Error", "Database error: " + ex.getMessage());
            }
        	loadQuestions(); 																		// *refresh to see changes
        	loadAnswers(selectedAnswer.getQuestionId());
        });

        backButton.setOnAction(e -> {
        	if (DatabaseHelper.cur_user.getRole().equals("reviewer")) {
        		new ReviewerHomePage(dbHelper).show(primaryStage);
        	} else {
        		new StudentHomePage(dbHelper).show(primaryStage);
        	}
        });
        
        // ---------------- Edit and Delete Operations for Answers ----------------
        
        // Edit a selected answer.
        editAnswerButton.setOnAction(e -> {
            Answer selectedAnswer = answersListView.getSelectionModel().getSelectedItem();
            if (selectedAnswer == null) {
                showAlert("Error", "Please select an answer to edit.");
                return;
            }
            // Open a dialog with the current answer text for editing.
            TextInputDialog dialog = new TextInputDialog(selectedAnswer.getText());
            dialog.setTitle("Edit Answer");
            dialog.setHeaderText("Edit the answer text");
            dialog.setContentText("New answer text:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newText -> {
                if (newText.trim().isEmpty()) {
                    showAlert("Error", "Answer text cannot be empty.");
                    return;
                }
                try {
                    if (dbHelper.updateAnswerText(selectedAnswer.getId(), newText.trim())) {
                        showAlert("Success", "Answer updated successfully!");
                        Question selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
                        if (selectedQuestion != null) {
                            loadAnswers(selectedQuestion.getId());
                        }
                    } else {
                        showAlert("Error", "Failed to update answer.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Database error: " + ex.getMessage());
                }
            });
        });
        
        // Delete a selected answer.
        deleteAnswerButton.setOnAction(e -> {
            Answer selectedAnswer = answersListView.getSelectionModel().getSelectedItem();
            if (selectedAnswer == null) {
                showAlert("Error", "Please select an answer to delete.");
                return;
            }
            // Confirm deletion of the answer.
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setContentText("Are you sure you want to delete this answer?");
            Optional<ButtonType> response = confirm.showAndWait();
            if (response.isPresent() && response.get() == ButtonType.OK) {
                try {
                    if (dbHelper.deleteAnswer(selectedAnswer.getId())) {
                        showAlert("Success", "Answer deleted successfully!");
                        Question selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
                        if (selectedQuestion != null) {
                            loadAnswers(selectedQuestion.getId());
                        }
                    } else {
                        showAlert("Error", "Failed to delete answer.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Database error: " + ex.getMessage());
                }
            }
        });
        
        viewReviewButton.setOnAction(e -> {
        	Answer selectedAnswer = answersListView.getSelectionModel().getSelectedItem();
            if (selectedAnswer == null) {
                showAlert("Error", "Please select an answer to access reviews.");
                return;
            }
            
        	new NonReviewerViewPage(dbHelper, selectedAnswer.getId()).show(primaryStage);
        });
        
        
    }

    // ---------------- Helper Methods ----------------

    /******
     * Loads all questions from the database into the ListView.
     */
    private void loadQuestions() {
        try {
            List<Question> qList = dbHelper.getAllQuestions();
            questionsList = FXCollections.observableArrayList(qList);
            questionsListView.setItems(questionsList);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to load questions: " + ex.getMessage());
        }
    }

    /******
     * Loads all answers for a specific question into the ListView.
     * 
     * @param questionId Integer of ID of question to retrieve answers of.
     */
    private void loadAnswers(int questionId) {
        try {
            List<Answer> aList = dbHelper.getAnswersForQuestion(questionId);
            answersList = FXCollections.observableArrayList(aList);
            answersListView.setItems(answersList);
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