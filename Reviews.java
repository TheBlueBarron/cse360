package application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// This class is like a container for all our Answer objects.
// It lets us add, update, delete, and search through answers.
public class Reviews {
    // List to store all the answers.
    private List<Review> reviews;
    
    /**
     	Constructor initialized review list
     */
    public Reviews() {
        reviews = new ArrayList<>();
    }
    
    // 
    // It only adds the review if it's not null and the text is valid.
    /**
     * Create: Add a review to the list.
     * @param review 
     */
    public void addReview(Review review) {
        if (review != null && Answer.isValidAnswerText(review.getText())) {
            reviews.add(review);
        } else {
            throw new IllegalArgumentException("Invalid answer provided.");
        }
    }
    
    // 
    // Loops through the list and returns the review if the ID matches.
    /**
     * Read: Retrieve a review by its ID.
     * @param id
     * @return Review object by id
     */
    public Review getReviewById(int id) {
        for (Review a : reviews) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }
    
    //
    // Returns a new list containing all the reviews we have.
    /**
     *  Read: Return all reviews.
     * @return List of all review objects
     */
    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews);
    }
    
    // 
    // Finds the review by ID and, if found and the new text is valid, updates it.
    /**
     * Update: Update the text of an review.
     * @param id
     * @param newText
     * @return boolean, True if update is successful
     */
    public boolean updateReview(int id, String newText) {
        Review a = getReviewById(id);
        if (a != null && Review.isValidReviewText(newText)) {
            a.setText(newText);
            return true;
        }
        return false;
    }
    
    // 
    // Searches for the review and removes it if found.
    /**
     * Delete: Remove an review by its ID.
     * @param id
     * @return boolean True if deletion is successful
     */
    public boolean deleteReview(int id) {
        Review a = getReviewById(id);
        if (a != null) {
            reviews.remove(a);
            return true;
        }
        return false;
    }
    
    // 
    //
    /**
     * Return review for a specific question.
     *  Filters the reviews list to include only reviews with the given answerId.
     * @param answerId
     * @return List of reviews given answer id
     */
    public List<Review> getReviewsForAnswers(int answerId) {
        return reviews.stream()
                      .filter(a -> a.getAnswerId() == answerId)
                      .collect(Collectors.toList());
    }
    
}
