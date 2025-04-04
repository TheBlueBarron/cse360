package application;

import databasePart1.DatabaseHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

/******
 * <p> Title: Role Request Page. </p>
 * 
 * <p> Description: Java class to display the Role Request page and the associated operations. </p>
 * 
 * @author Wednesday 44 of CSE 360
 * 
*/
public class RoleRequestPage {
	
	// DatabaseHelper object to perform database operations
	private DatabaseHelper databaseHelper;
	// Observable list for users requesting a role and its ListView for UI display
	private ObservableList<String> usersList;
	private ListView<String> usersListView;
	// Observable list for questions and its ListView for UI display
    private ObservableList<Question> questionsList;
    private ListView<Question> questionsListView;
    // Observable list for answers and its ListView for UI display
    private ObservableList<Answer> answersList;
    private ListView<Answer> answersListView;
    
    /******
     * Constructor for the RoleRequestPage to be called based on
     * user interaction with the application.
     * 
     * @param databaseHelper	DatabaseHelper object to handle database operations.
     * 
    */
	public RoleRequestPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
	/******
	 * Operation to display an existing RoleRequestPage.
	 * 
	 * @param primaryStage	Stage object to display the scene on.	
	 */
	public void show(Stage primaryStage) {
		primaryStage.setTitle("Role Requests");
		
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(10));
		
		// ---------- Users List Section ----------
		// Label and ListView for displaying a list of users requesting a role.
		Label usersListLabel = new Label("Users: ");
		usersListView = new ListView<>();
		usersListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) {
                    setText(null);
                } else {
                    // Display question ID, text, and author in the list.
                    setText(s);
                }
            }
        });
		
		// ---------- Approval & Denial Section ----------
		// UI features used to handle approval and denial of requests.
		Button approveButton = new Button("Approve");
		Button denyButton = new Button("Deny");
		HBox requestHandlingBox = new HBox(approveButton, denyButton);
		
		// ----------- Questions & Answers Section ----------
		// Labels, ListViews & HBox to display contributions to the forum.
		Label contributionsLabel = new Label("Contributions:");
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
		HBox qAndABox = new HBox(10, questionsListView, answersListView);
		
		// ----------- Back Button ----------
		Button backButton = new Button("Back");
		
		layout.getChildren().addAll(usersListLabel, usersListView, 
				requestHandlingBox, contributionsLabel, qAndABox, 
				backButton);
		Scene requestScene = new Scene(layout, 800, 400);
		
		primaryStage.setScene(requestScene);
		
		// ---------- Load Data ----------
		loadUsers();
		
		// When a user is selected, retrieve their questions and answers using
		// search functions in database helper
		usersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadQuestions(newVal);
            	loadAnswers(newVal);
            } else {
                // If no question is selected, clear the answers list.
            	questionsListView.setItems(FXCollections.observableArrayList());
                answersListView.setItems(FXCollections.observableArrayList());
            }
        });
        
	    // ---------- Button Handlers -----------

		approveButton.setOnAction(e -> {
			String selectedUser = usersListView.getSelectionModel().getSelectedItem();
			
			if (selectedUser == null || selectedUser.isEmpty()) {
				showAlert("Error", "Please select a user.");
				return;
			}
			
			boolean success = databaseHelper.addUserRole(selectedUser, "reviewer") && databaseHelper.deleteRoleRequest(selectedUser);
			if (success) {
				showAlert("Success", "Role has been added!");
			} else {
				showAlert("Error", "Role could not be added.");
			}
			loadUsers(); // Refresh list
		});
		
		denyButton.setOnAction(e -> {
			String selectedUser = usersListView.getSelectionModel().getSelectedItem();
			
			if (selectedUser == null || selectedUser.isEmpty()) {
				showAlert("Error", "Please select a user.");
				return;
			}
			
			boolean success = databaseHelper.deleteRoleRequest(selectedUser);
			
			if (success) {
				showAlert("Success", "Role request has been denied.");
			} else {
				showAlert("Error", "Role request could not be denied.");
			}
			loadUsers();
		});
	    backButton.setOnAction(e -> {
	    	new InstructorHomePage(databaseHelper).show(primaryStage);
	    });
	}
	
	// ---------- Helper Methods ----------
	
	/******
	 * Operation to load the list of users currently requesting
	 * a role from the database.
	 */
	private void loadUsers() {
		try {
			List<String> uList = databaseHelper.getAllRequests();
			usersList = FXCollections.observableArrayList(uList);
	        usersListView.setItems(usersList);
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "Failed to retrieve users: " + e.getMessage());
		}
		
	}
	
	/******
	 * Operation to load the list of questions a particular user
	 * has asked from the database and display in the ListView.
	 * 
	 * @param username	String of the username to use to retrieve corresponding questions.
	 */
    private void loadQuestions(String username) {
        try {
            List<Question> qList = databaseHelper.searchQuestionsByAuthor(username);
            questionsList = FXCollections.observableArrayList(qList);
            questionsListView.setItems(questionsList);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to load questions: " + ex.getMessage());
        }
    }

    /******
     * Operation to load the list of answers a particular question
     * has contributed to the discussion board from the database
     * and display in the ListView.
     * 
     * @param username	String of the username to use to retrieve corresponding answers.
     */
    private void loadAnswers(String username) {
        try {
            List<Answer> aList = databaseHelper.searchAnswersByAuthor(username);
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
    		Alert prompt = new Alert(Alert.AlertType.INFORMATION);
    		prompt.setTitle(title);
    		prompt.setContentText(message);
    		prompt.showAndWait();
    	});
    }
	
	/* Helper method to test functionality; uncomment if needed
	private void printList(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		} 
	}
	*/
}
