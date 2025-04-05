package application;

import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Collectors;

/**
 * This class is like a container for all our Question objects.
 * It helps us add, update, delete, and search through questions.
 */
public class Questions {
    // List to store all questions.
    private List<Question> questions;
    
    /**
     * Constructor: Initializes the questions list.
     */
    public Questions() {
        questions = new ArrayList<>();
    }
    
    /**
     * Create: Add a question to the list.
     * Only adds the question if it's not null and the text is valid.
     * @param question
     */
    public void addQuestion(Question question) {
        if (question != null && Question.isValidQuestionText(question.getText())) {
            questions.add(question);
        } else {
            throw new IllegalArgumentException("Invalid question provided.");
        }
    }
    
    /**
     * Read: Retrieve a question by its ID.
     * Loops through the list to find the question with the matching ID.
     * @param id
     * @return
     */
    public Question getQuestionById(int id) {
        for (Question q : questions) {
            if (q.getId() == id) {
                return q;
            }
        }
        return null;
    }
    
    /**
     * Read: Return all questions.
     * Returns a new list containing all current questions.
     * @return
     */
    public List<Question> getAllQuestions() {
        return new ArrayList<>(questions);
    }
    
    /**
     * Update: Update the text of a question.
     * Finds the question by ID and, if found and the new text is valid, updates it.
     * @param id
     * @param newText
     * @return
     */
    public boolean updateQuestion(int id, String newText) {
        Question q = getQuestionById(id);
        if (q != null && Question.isValidQuestionText(newText)) {
            q.setText(newText);
            return true;
        }
        return false;
    }
    
    /**
     * Delete: Remove a question by its ID.
     * Looks for the question and removes it if it's there.
     * @param id
     * @return
     */
    public boolean deleteQuestion(int id) {
        Question q = getQuestionById(id);
        if (q != null) {
            questions.remove(q);
            return true;
        }
        return false;
    }
    
}