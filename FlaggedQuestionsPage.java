package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

/**
 * A page for Admins/Instructors to view all flagged questions in the system.
 */
public class FlaggedQuestionsPage {

    private DatabaseHelper dbHelper;

    public FlaggedQuestionsPage(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    
    public void show(Stage stage) {
        stage.setTitle("Flagged Questions");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label header = new Label("Flagged Questions for Review");
        ListView<String> listView = new ListView<>();

        try {
            List<Question> flagged = dbHelper.getFlaggedQuestions();
            for (Question q : flagged) {
                listView.getItems().add("[" + q.getId() + "] " + q.getText());
            }
        } catch (SQLException e) {
            listView.getItems().add("Error loading flagged questions.");
            e.printStackTrace();
        }

        layout.getChildren().addAll(header, listView);
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}