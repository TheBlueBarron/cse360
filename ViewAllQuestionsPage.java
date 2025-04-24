package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides the staff user interface for viewing, filtering,
 * and managing all student-submitted questions.
 * <p>
 * Staff can:
 * <ul>
 *   <li>View all questions</li>
 *   <li>Filter to see only unanswered questions</li>
 *   <li>Flag questions for admin/instructor review</li>
 * </ul>
 */
public class ViewAllQuestionsPage {

    
    private DatabaseHelper dbHelper;

    
    public ViewAllQuestionsPage(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    
    public void show(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("All Student Questions");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        CheckBox filterUnanswered = new CheckBox("Show only unanswered questions");
        ListView<String> questionList = new ListView<>();

        
        Runnable updateList = () -> {
            questionList.getItems().clear();
            try {
                List<Question> all = dbHelper.getAllQuestions();

                if (filterUnanswered.isSelected()) {
                    all = all.stream()
                            .filter(q -> {
                                try {
                                    List<Answer> relatedAnswers = dbHelper.getAnswersForQuestion(q.getId());
                                    return relatedAnswers == null || relatedAnswers.isEmpty();
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    return false;
                                }
                            })
                            .collect(Collectors.toList());
                }

                for (Question q : all) {
                    questionList.getItems().add("[" + q.getId() + "] " + q.getText());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        filterUnanswered.setOnAction(e -> updateList.run());

        Button flagButton = new Button("Flag Selected Question");

        
        flagButton.setOnAction(e -> {
            String selectedItem = questionList.getSelectionModel().getSelectedItem();

            if (selectedItem == null) {
                showAlert("No question selected.", Alert.AlertType.WARNING);
                return;
            }

            try {
                // Extract question ID from ID Question text ** fix (parsing + db issue) later
                int questionId = Integer.parseInt(selectedItem.split("]")[0].substring(1));

                DatabaseHelper dbHelper = new DatabaseHelper();
                dbHelper.setQuestionFlagged(questionId, true);

                showAlert("Question has been flagged for review.", Alert.AlertType.INFORMATION);

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Failed to flag question.", Alert.AlertType.ERROR);
            }
        });

        updateList.run();

        root.getChildren().addAll(title, filterUnanswered, questionList, flagButton);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("View All Questions - Staff");
        primaryStage.show();
    }

    
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Flagged");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
