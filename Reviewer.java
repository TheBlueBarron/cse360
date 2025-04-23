package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;

/**
 * <p> Title: Reviewer class. </p>
 * 
 * <p> Description: This is a class to store Reviewer objects for student rating. </p>
 * 
 * @author Wednesday 44 of CSE 360
 */
@SuppressWarnings("unused")
public class Reviewer {
	// Reviewer's name and Id
	private int Id;
	private String name;
	private String xp;
	
	// Stores the students likes, dislikes, and a calculated rating
	private int likes;
	private int dislikes;
	private double rating;
	
	private boolean isTrusted; // Boolean calculated by student's ratings
	
    private DatabaseHelper dbHelper;
    
    public Reviewer(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
	
	/** Constructor
	 * 
	 * @param name The name for the reviewer
	 */
	public Reviewer(String name, String xp) {
		this.name = name;
		this.xp = xp;
		this.likes = 0;
		this.dislikes = 0;
		this.rating = 0.0;
		this.isTrusted = false;
	}
	
	public Reviewer(String name) {
		this.name = name;
		this.xp = "";
		this.likes = 0;
		this.dislikes = 0;
		this.rating = 0.0;
		this.isTrusted = false;
	}
	
	/** Constructor (allows us to return an updated reviewer object if needed)
	 * @param Id The unique identifier for a reviewer
	 * @param name The name for the reviewer
	 * @param likes reviewers like count
	 * @param dislikes reviewers dislike count
	 * @param rating reviewer's rating
	 * @param isTrusted the reviewer's trusted status
	 */
	public Reviewer(int Id, String name, String xp, int likes, int dislikes, double rating, boolean isTrusted ) {
		this.Id = Id;
		this.name = name;
		this.xp = xp;
		this.likes = likes;
		this.dislikes = dislikes;
		this.rating = rating;
		this.isTrusted = isTrusted;
	}
	

    // Getters and Setters
    
	/**
     * Gets the reviewer's unique identifier.
     *
     * @return The reviewer's ID.
     */
    public int getId() { return Id; }

    /**
     * Gets the reviewer's name.
     *
     * @return The reviewer's name.
     */
    public String getName() { return name; }

    /**
     * Gets the total like count for this reviewer	
     * @return The total like count for the reviewer.
     */
    
    
    /**
     * Gets reviewers experience
     * @return The reviewers experience they listed
     */
    public String getXP() { return xp; }
    
    
    public int getLikeCount() { return likes; }

    /**
     * Gets the total dislike count for this reviewer.
     *
     * @return The total dislike count for the reviewer.
     */
    public int getDislikeCount() { return dislikes; }

    /**
     * Gets the current rating of this reviewer, calculated as 
     * likeCount / dislikeCount.
     *
     * @return The reviewer's rating.
     */
    public double getRating() { return rating; }

    /**
     * Checks if this reviewer is trusted which is based on their rating.
     *
     * @return true if the reviewer is trusted, false otherwise.
     */
    public boolean isTrusted() { return isTrusted; }

    /**
     * Increments the like count for this reviewer and updates their rating.
     */
    public void addLike() {
        this.likes += 1; // increment likes
        updateRating();  // update rating with new values
        }

    /**
     * Increments the dislike count for this reviewer and updates their rating.
     */
    public void addDislike() {
        this.dislikes += 1; // increment dislikes
        updateRating(); 	// update rating with new values
    }

    /**
     * Recalculates the reviewer's rating based on their like and dislike counts, and updates the trusted status
     * 
     */
    private void updateRating() {
        if (dislikes > 0) {
            this.rating = (double) likes / dislikes;
        } else {
            this.rating = (double)likes;
        }
        updateTrustStatus();
    }

    /**
     * Updates the trust status of the reviewer based on their current rating.
     * A reviewer is considered trusted if their rating is greater than or equal to 1.5.
     */
    private void updateTrustStatus() {
        this.isTrusted = rating >= 1.5;  // Trusted if rating >= 1.5
    }
    
    /**
     * setter for user experience
     * @param xp
     */
    public void setXp(String xp) {
    	this.xp = xp;
    }
	
}
