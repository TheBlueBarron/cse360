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
import javafx.scene.control.Label; 
import javafx.scene.control.ListCell; 
import javafx.scene.control.ListView; 
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox; 
import javafx.scene.layout.VBox; 
import javafx.stage.Stage; 
import java.sql.SQLException; 
import java.util.List;
import java.util.Optional;

// This class creates a Discussion Forum page where anyone can view and post questions and answers.
public class DiscussionPage {
    
    // Database helper to handle DB operations
    private DatabaseHelper dbHelper;
    // Observable list for questions and its ListView for UI display
    private ObservableList<Question> questionsList;
    private ListView<Question> questionsListView;
    // Observable list for answers and its ListView for UI display
    private ObservableList<Answer> answersList;
    private ListView<Answer> answersListView;
    // Observable lists for search results **** EDIT
    private ObservableList<Question> questionsResults;
    private ObservableList<Answer> answersResults;

    // Constructor that takes the DatabaseHelper as a parameter
    public DiscussionPage(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // This method sets up and shows the discussion forum page on the given stage.
    public void show(Stage primaryStage) {
        primaryStage.setTitle("Discussion Forum");
        
        // ---------------- Main Layout ----------------
        // Create a vertical box as the main container with some spacing and padding.
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        
        // ---------------- New Question Section ----------------
        // Label and input fields for posting a new question.
        Label newQuestionLabel = new Label("Post a New Question:");
        TextField questionTextField = new TextField();
        questionTextField.setPromptText("Enter your question here");
        TextField questionAuthorField = new TextField();
        questionAuthorField.setPromptText("Your name (optional, default: Anonymous)");
        Button postQuestionButton = new Button("Post Question");
        
        // HBox to hold the new question input fields and button.
        HBox newQuestionBox = new HBox(10, questionTextField, questionAuthorField, postQuestionButton);
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
                }
            }
        });
        
        // ---------------- Question Operations Buttons ----------------
        // Buttons for refreshing, editing, and deleting questions.
        Button refreshQuestionsButton = new Button("Refresh Questions");
        Button editQuestionButton = new Button("Edit Selected Question");
        Button deleteQuestionButton = new Button("Delete Selected Question");
        HBox questionOperationsBox = new HBox(10, refreshQuestionsButton, editQuestionButton, deleteQuestionButton);
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
                }
            }
        });
        
        // ---------------- Answer Operations Buttons ----------------
        // Buttons for refreshing, editing, and deleting answers.
        Button refreshAnswersButton = new Button("Refresh Answers");
        Button editAnswerButton = new Button("Edit Selected Answer");
        Button deleteAnswerButton = new Button("Delete Selected Answer");
        HBox answerOperationsBox = new HBox(10, refreshAnswersButton, editAnswerButton, deleteAnswerButton);
        answerOperationsBox.setPadding(new Insets(5));
        
        // ---------------- New Answer Section ----------------
        // Label and input fields for posting a new answer.
        Label newAnswerLabel = new Label("Post a New Answer:");
        TextField answerTextField = new TextField();
        answerTextField.setPromptText("Enter your answer here");
        TextField answerAuthorField = new TextField();
        answerAuthorField.setPromptText("Your name (optional, default: Anonymous)");
        Button postAnswerButton = new Button("Post Answer");
        HBox newAnswerBox = new HBox(10, answerTextField, answerAuthorField, postAnswerButton);
        newAnswerBox.setPadding(new Insets(5));
        
        // ---------------- Assemble Layout ----------------
        // Add all sections to the main layout in order.
        mainLayout.getChildren().addAll(
            newQuestionLabel, newQuestionBox, 
            questionsLabel, questionsListView, questionOperationsBox, searchField, 
            answersLabel, answersListView, answerOperationsBox, 
            newAnswerLabel, newAnswerBox
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
        
        // Handle posting a new question.
        postQuestionButton.setOnAction(e -> {
            String qText = questionTextField.getText().trim();
            String qAuthor = questionAuthorField.getText().trim();
            if (qText.isEmpty()) {
                showAlert("Error", "Question text cannot be empty.");
                return;
            }
            if (qAuthor.isEmpty()) {
                qAuthor = "Anonymous";
            }
            // Create a new question object.
            Question newQuestion = new Question(qText, qAuthor);
            try {
                // Add it to the database.
                dbHelper.addQuestion(newQuestion);
                showAlert("Success", "Question posted successfully!");
                // Clear input fields and reload questions.
                questionTextField.clear();
                questionAuthorField.clear();
                loadQuestions();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to post question: " + ex.getMessage());
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
            String aAuthor = answerAuthorField.getText().trim();
            if (aText.isEmpty()) {
                showAlert("Error", "Answer text cannot be empty.");
                return;
            }
            if (aAuthor.isEmpty()) {
                aAuthor = "Anonymous";
            }
            // Create a new answer for the selected question.
            Answer newAnswer = new Answer(selectedQuestion.getId(), aText, aAuthor);
            try {
                // Add the answer to the database.
                dbHelper.addAnswer(newAnswer);
                showAlert("Success", "Answer posted successfully!");
                // Clear input fields and reload answers for the selected question.
                answerTextField.clear();
                answerAuthorField.clear();
                loadAnswers(selectedQuestion.getId());
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to post answer: " + ex.getMessage());
            }
        });
        
        // Refresh buttons for questions and answers.
        // **** EDIT: Added a clear to the search field upon pressing
        refreshQuestionsButton.setOnAction(e -> {
        	loadQuestions();
        	searchField.clear();
        });
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
    }
    // ---------------- Helper Methods ----------------

    // Loads all questions from the database into the ListView.
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

    // Loads all answers for a specific question into the ListView.
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

    // Shows an alert with the given title and message on the UI.
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
