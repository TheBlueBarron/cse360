package application;

// This class represents an answer to a specific question in our app.
public class Review {
    // Unique ID for this answer.
    private int id;
    // ID of the question this answer belongs to.
    private int answerId; // Foreign key to associate with a Question
    // ID of the reviewer who posted the review
    private int reviewer_id;
    // The content of the answer.
    private String text;
    

    
    // Constructor that takes an id, answerId, text, and author
    // It throws an error if the answer text is empty.
    /**
     * Constructor for review 
     * @param id
     * @param answerId
     * @param text
     * @param reviewer_id
     */
    public Review(int id, int answerId, String text, int reviewer_id) {
        if (!isValidReviewText(text)) {
            throw new IllegalArgumentException("review text cannot be empty.");
        }
        this.id = id;
        this.answerId = answerId;
        this.text = text;
        this.reviewer_id = reviewer_id;
    }
    
    // The database will assign the ID later.
    /**Constructor for creating a new review when the ID isn't known yet.
     * 
     * @param answerId
     * @param text
     * @param author
     * @param reviewer_id
     */
    public Review(int answerId, String text, int reviewer_id) {
        this(-1, answerId, text, reviewer_id);
    }
    
    // 
    /**
     * Checks if the review text is valid (i.e., not null or empty).
     * @param text
     * @return boolean True if review is valid
     */
    public static boolean isValidReviewText(String text) {
        return text != null && !text.trim().isEmpty();
    }
    
    // Getter for the review ID.
   /**
    * 
    * @return id of review as integer
    */
    public int getId() {
        return id;
    }
    
    // Setter for the review ID.
    
    /**
     * Setter for the review ID
     * @param id
     */
    public void setId(int id) { 
        this.id = id; 
    }
 
       
    
    // Getter for the review ID associated with this review.
    /**
     * 
     * @return id of answer
     */
    public int getAnswerId() {
        return answerId;
    }
    
    // Getter for the reviewerID associated with this review.
    /**
     * 
     * @return id of reviewer
     */
    public int getReviewerId() {
    	return reviewer_id;
    }
    
    // Getter for the review text.
    /**
     * 	
     * @return review text
     */
    public String getText() {
        return text;
    }
    
    // Setter for the review text; checks that the text isn't empty before setting.
    /**
     * Setter for the review text; checks that the text isn't empty before setting.
     * @param text to set review text to
     */
    public void setText(String text) {
        if (!isValidReviewText(text)) {
            throw new IllegalArgumentException("Answer text cannot be empty.");
        }
        this.text = text;
    }
    

    
    // Overridden toString method to print out answer details in an easy-to-read format.
    /**
     * Overridden toString method to print out answer details in an easy-to-read format.
    /**
     */
    @Override
    public String toString() {
        return "Review [id=" + id + ", answerId=" + answerId + ", text=" + text + "]";
    }
}