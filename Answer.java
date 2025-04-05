
package application;

// This class represents an answer to a specific question in our app.
/**
 * The answer class contains the information and relationships needed for each
 * answer to a question.
 */
public class Answer {
    // Unique ID for this answer.
    private int id;
    // ID of the question this answer belongs to.
    private int questionId; // Foreign key to associate with a Question
    // The content of the answer.
    private String text;
    // Who posted the answer.
    private String author;
    // See if an answer resolved a particular question
    private boolean resolved;
    
    /**
     * Constructor that takes an id, questionId, text, author, and resolved.
     * It throws an error if the answer text is empty.
     * 
     * @param id
     * @param questionId
     * @param text
     * @param author
     * @param resolved
     */
    public Answer(int id, int questionId, String text, String author, boolean resolved) {
        if (!isValidAnswerText(text)) {
            throw new IllegalArgumentException("Answer text cannot be empty.");
        }
        this.id = id;
        this.questionId = questionId;
        this.text = text;
        this.author = author;
        this.resolved = resolved;
    }
    /**
     * Constructor for creating a new answer when the ID isn't known yet.
     * The database will assign the ID later.
     * 
     * @param questionId
     * @param text
     * @param author
     * @param resolved
     */
    public Answer(int questionId, String text, String author, boolean resolved) {
        this(-1, questionId, text, author, resolved);
    }
    /**
     * Checks if the answer text is valid (i.e., not null or empty).
     * @param text
     * @return boolean
     */
    public static boolean isValidAnswerText(String text) {
        return text != null && !text.trim().isEmpty();
    }
    /**
     * Getter for the answer ID.
     * @return id
     */
    public int getId() {
        return id;
    }
    /**
     * Setter for the answer ID.
     * @param id
     */
    public void setId(int id) { 
        this.id = id; 
    }
    /**
     * Getter for the resolved flag
     * @return getResolved
     */
    public boolean getResolved() {
    	return resolved;
    }
    /**
     * Set an answer as resolved
     */
    public void markAsResolver() {
    	resolved = true;
    }
    
    /**
     * Getter for the question ID associated with this answer.
     * @return questionId
     */
    public int getQuestionId() {
        return questionId;
    }
    
    /**
     * Getter for the answer text.
     * @return
     */
    public String getText() {
        return text;
    }
    
    /**
     * Setter for the answer text; checks that the text isn't empty before setting.
     * @param text
     */
    public void setText(String text) {
        if (!isValidAnswerText(text)) {
            throw new IllegalArgumentException("Answer text cannot be empty.");
        }
        this.text = text;
    }
    
    /**
     * Getter for the author of the answer.
     * 
     * @return
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * Overridden toString method to print out answer details in an easy-to-read format.
     */
    @Override
    public String toString() {
        return "Answer [id=" + id + ", questionId=" + questionId + ", text=" + text +
               ", author=" + author + "]";
    }
}