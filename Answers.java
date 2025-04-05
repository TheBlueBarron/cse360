package application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** This class is like a container for all our Answer objects.
 *  It lets us add, update, delete, and search through answers.
 */
public class Answers {
    // List to store all the answers.
    private List<Answer> answers;
    
    /**
     * Constructor: Initializes the answers list.
     */
    public Answers() {
        answers = new ArrayList<>();
    }
    
    /**
     * Create: Add an answer to the list.
     * It only adds the answer if it's not null and the text is valid.
     * @param answer
     */
    public void addAnswer(Answer answer) {
        if (answer != null && Answer.isValidAnswerText(answer.getText())) {
            answers.add(answer);
        } else {
            throw new IllegalArgumentException("Invalid answer provided.");
        }
    }
    
    /**
     * Read: Retrieve an answer by its ID.
     * Loops through the list and returns the answer if the ID matches.
     * @param id
     * @return a - Returns the answer with the matching id
     */
    public Answer getAnswerById(int id) {
        for (Answer a : answers) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }
    
    /**
     * Read: Return all answers.
     * Returns a new list containing all the answers we have.
     * @return List of all answers
     */
    public List<Answer> getAllAnswers() {
        return new ArrayList<>(answers);
    }
    
    /**
     * Update: Update the text of an answer.
     * Finds the answer by ID and, if found and the new text is valid, updates it.
     * @param id
     * @param newText
     * @return boolean, true if updated successfully false otherwise
     */
    public boolean updateAnswer(int id, String newText) {
        Answer a = getAnswerById(id);
        if (a != null && Answer.isValidAnswerText(newText)) {
            a.setText(newText);
            return true;
        }
        return false;
    }
    
    /**
     * Delete: Remove an answer by its ID.
     * Searches for the answer and removes it if found.
     * @param id
     * @return boolean, true if deleted successfully false otherwise
     */
    public boolean deleteAnswer(int id) {
        Answer a = getAnswerById(id);
        if (a != null) {
            answers.remove(a);
            return true;
        }
        return false;
    }
    
    /**
     * Return answers for a specific question.
     * Filters the answers list to include only answers with the given questionId.
     * @param questionId
     * @return list of answer tied to a questions id
     */
    public List<Answer> getAnswersForQuestion(int questionId) {
        return answers.stream()
                      .filter(a -> a.getQuestionId() == questionId)
                      .collect(Collectors.toList());
    }
    
}