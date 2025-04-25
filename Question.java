package application;

/**
 * This class represents a single question in our app
 */
public class Question {
    // Unique ID for the question
    private int id;
    // The actual text of the question
    private String text;
    // Who asked the question
    private String author;
    // You can see if the question is resolved
    private boolean isResolved;
    // If the question has been flagged
    private boolean flagged;

    /**
     * Constructor that takes an id, text, author, isResolved.
     * It throws an error if the question text is empty.
     * @param id
     * @param text
     * @param author
     * @param isResolved
     */
    public Question(int id, String text, String author, boolean isResolved, boolean flagged) {
        if (!isValidQuestionText(text)) {
            throw new IllegalArgumentException("Question text cannot be empty.");
        }
        this.id = id;
        this.text = text;
        this.author = author;
        this.isResolved = isResolved;
        this.flagged = flagged;
    }
    
    /**
     * Constructor for creating a new question when we don't have an ID yet.
     * The database will assign the ID later.
     * @param text
     * @param author
     * @param isResolved
     */
    public Question(String text, String author, boolean isResolved, boolean flagged) {
        this(-1, text, author, isResolved, flagged);
    }

    /**
     * Checks if the question text is valid (not null or blank)
     * @param text
     * @return boolean, true if valid question, false otherwise
     */
    public static boolean isValidQuestionText(String text) {
        return text != null && !text.trim().isEmpty();
    }
    
    /**
     * Getter for the question ID
     * @return The questions id
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the question ID
     * @param id
     */
    public void setId(int id) { 
        this.id = id; 
    }
    
    /**
     * Getter for the question text
     * @return The question's text
     */
    public String getText() {
        return text;
    }
    
    /**
     * Getter for resolved flag
     * @return boolean, true if is resolved, false otherwise
     */
    public boolean getIsResolved() {
    	return isResolved;
    }
    /**
     * Getter for resolved flag
     */
    public void markAsResolved() {
    	isResolved = true;
    }
    
    /**
     * Setter for the question text; validates before setting
     * @param text
     */
    public void setText(String text) {
        if (!isValidQuestionText(text)) {
            throw new IllegalArgumentException("Question text cannot be empty.");
        }
        this.text = text;
    }
    
    /**
     * Getter for the author of the question
     * @return author
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * Overridden toString method to print out question details easily
     */
    @Override
    public String toString() {
        return "Question [id=" + id + ", text=" + text + ", author=" + author + "]";
    }
    public boolean getFlagged() {
    	return flagged;
    }
}