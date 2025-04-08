package application;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import databasePart1.DatabaseHelper;

/** <p> Title: ReviewerTestingAutomation Class. </p>
* 
* <p> Description: Automated tests that verify the ability of students to weight reviewers and see a list of trusted reviews </p>
* 
* Author: Wednesday Group 44
* 
* Version: 1.00 Created 4/2/2025, set of automated tests.
* 
* IMPORTANT NOTE: DON'T RUN THESE TESTS WITH A USED DATABASE, it will break the DB and the tests.
* 
*/
class ReviewerTestingAutomation {
	
	private static DatabaseHelper dbHelper = new DatabaseHelper();

	
	@BeforeEach
	public void setUp() {
		try {
			dbHelper.connectToDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	/** <p> This test ensure that a student is able to update a reviewer's amount of likes <p>
	 * 
	 *  <p> It creates a reviewer with initialized values, and ensures the used function updates reviewer like count
	 */
	@Test
	public void testStudentLike() {
		Reviewer reviewer = new Reviewer(1, "epic name", 0, 0, 0.0, false ); // Initialize dummy reviewer for testing
		reviewer.addLike();  												 // Add a like to the reviewer
		
		assertTrue(reviewer.getLikeCount() == 1); 							 // Make sure we now have one like on the reviewer
		
		reviewer.addLike();													 // Add another like and
		assertTrue(reviewer.getLikeCount() == 2); 							 // Make sure we now have two likes on the reviewer, just in case

	}
	
	/** <p> This test ensure that a student is able to update a reviewer's amount of dislikes <p>
	 * 
	 *  <p> It creates a reviewer with initialized values, and ensures the used function updates reviewer dislike count <p>
	 */
	@Test
	public void testStudentDislike() {
		Reviewer reviewer = new Reviewer(1, "epic name", 0, 0, 0.0, false ); // Initialize dummy reviewer for testing
		reviewer.addDislike();  												 // Add a dislike to the reviewer
		
		assertTrue(reviewer.getDislikeCount() == 1); 							 // Make sure we now have one dislike on the reviewer
		
		reviewer.addDislike();													 // Add another dislike to the reviewer
		assertTrue(reviewer.getDislikeCount() == 2); 							 // Make sure we now have two dislikes on the reviewer, just in case

	}
	/** <p> This test ensures the rating system is working properly <p>
	 * 
	 *  <p> The rating is based on the like and dislike count and should update every time a like or dislike is added <p>
	 */
	@Test
	public void testStudentRatingUpdate() {
		Reviewer reviewer = new Reviewer(1, "epic name", 0, 0, 0.0, false ); // Initialize dummy reviewer for testing
		
		reviewer.addLike();
		assertTrue(reviewer.getRating() == 1.0); 							 // Handle when dislikes = 0, ensure rating is accurate
		assertTrue(reviewer.getLikeCount() == 1);						     // Check the like count just in case
		
		reviewer.addLike();
		assertTrue(reviewer.getRating() == 2.0); 							 // Check another like and ensure rating and count are updated
		assertTrue(reviewer.getLikeCount() == 2);
		
		reviewer.addDislike();
		assertTrue(reviewer.getRating() == 2.0); 							 // Now add a dislike and ensure rating is properly calculated
		assertTrue(reviewer.getDislikeCount() == 1);
		
		reviewer.addDislike();
		assertTrue(reviewer.getRating() == 1.0); 							 // Another dislike to be sure
		assertTrue(reviewer.getDislikeCount() == 2);
		}
	
	/** <p> This test ensures the trusted flag is working properly in the case it should be true <p>
	 * 
	 *  <p> Trusted is calculated by the reviewer's like and dislike count and must be either true or false, this is testing the case
	 *  in which a Reviewer has >= 1.5 rating and should therefore be trusted. Like and dislike automatically update rating and isTrusted.<p>
	 */
	@Test
	public void testReviewerTrustedUpdateTrue() {
		Reviewer reviewer = new Reviewer(1, "epic name", 0, 0, 0.0, false ); // Initialize dummy reviewer for testing
		
		assertFalse(reviewer.isTrusted()); 									 // Make sure when a reviewer is created and has 0 likes, 
																			 // they're not trusted.
		reviewer.addLike();												     // then add a like
		assertFalse(reviewer.isTrusted()); 								     // reviewer still shouldn't be trusted as rating < 1.5
		
		reviewer.addLike();
		assertTrue(reviewer.isTrusted());							    	 // now with a proper rating of >= 1.5, reviewer should be 
																			 // trusted.
	}
	
	/** <p> This test ensures the trusted flag is working properly in the case it should be false <p>
	 * 
	 *  <p> Trusted is calculated by the reviewer's like and dislike count and must be either true or false, this is testing the case
	 *  in which a Reviewer has < 1.5 rating and should therefore NOT be trusted. Like and dislike automatically update rating and 
	 *  isTrusted. <p>
	 *  
	 */
	@Test
	public void testReviewerTrustedUpdateFalse() {
		Reviewer reviewer = new Reviewer(1, "epic name", 0, 0, 0.0, false ); // Initialize dummy reviewer for testing
		
		assertFalse(reviewer.isTrusted()); 									 // Make sure when a reviewer is created and has 0 likes, 
																			 // they're not trusted.
		reviewer.addLike();												     // then add a like
		assertFalse(reviewer.isTrusted()); 								     // reviewer still shouldn't be trusted as rating < 1.5
		
		reviewer.addLike();
		assertTrue(reviewer.isTrusted());							    	 // now with a proper rating of >= 1.5, reviewer should be 
																			 // trusted.
		// Now we can ensure the flag will update back to false if the ratio goes < 1.5
		reviewer.addDislike();
		assertTrue(reviewer.isTrusted());									 // Ratio is still > 1.5 (2/1)
		reviewer.addDislike(); 												 // another dislike < 1.5 (2/2)
		assertFalse(reviewer.isTrusted()); 									 // now they shouldn't be trusted anymore
	}
	
	/**
	 * 
	 * IMPORTANT: THE NEXT 2 TESTS WILL BREAK DB AND TES IF THE DB IS NOT FRESH. RUN WITH CAUTION!!
	 * 
	 */
	
	
	/** <p> This test ensures the like but mostly rating work properly for larger values. <p>
	 * 
	 *  <p> Rating is a ratio of the likes to dislikes. Previously, I had trouble entering the rating into the database due to my double
	 *  variable in the database not allowing enough digits, so I wanted to ensure this behavior is safe now. This test will break if not run on a fresh DB
	 *  and will likely make the DB useless.. <p>
	 *  
	 */
	@Test
	public void testStudentLikeCountHigh() {
		try {
			dbHelper.connectToDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Reviewer reviewer = new Reviewer(1, "epic name", 99, 0, 99.0, true);	// Initialize reviewer with dummy likes for testing
		
		try {
			dbHelper.saveReviewer(reviewer);									// Save reviewer with likes to DB
		} catch (SQLException e) {
			e.printStackTrace();

		}
		
		Reviewer tReviewer = null;
		try {
			 tReviewer = dbHelper.getReviewerById(1);					// Get the reviewer by his ID from DB to ensure no lost data
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertTrue(tReviewer.getRating() == 99.0);						// Make sure he still has the right likes 
	}
	
	/** <p> This test ensures the trusted reviewers list is populated properly. ONLY WORKS ON FRESH DB... <p>
	 * 
	 *  <p> The trusted list is populated by a database function that collects all of the reviews that have the correct answer id
	 *  as well as the trusted status, then orders by the rating descending. It collects from a joined review and reviewer table
	 *  (review now has a foreign key, reviewer_id, that references the reviewers table). This test will break if not run on a fresh DB
	 *  and will likely make the DB useless. <p>
	 *  
	 */
	@Test
	public void testTrustedReviewList() {
		// Create 2 dummy reviewers, one that's trusted and one that isn't (based on rating but just set here for testing)
		Reviewer reviewer = new Reviewer(2, "epic name", 5, 0, 5.0, true);		// trusted
		Reviewer reviewer1 = new Reviewer(3, "epic name2", 0, 0, 0.0, false);	// not trusted
		// Also create 2 dummy reviews attached to each reviewer so we can verify their trusted status is being 
		Review review = new Review(1, "my review1", 2);						// attached to trusted
		Review review1 = new Review(1, "my review2", 3);						// attached to untrusted
		
		
		try {
			dbHelper.saveReviewer(reviewer);									// Save reviewer with trusted status to DB
		} catch (SQLException e) {
			e.printStackTrace();

		}
		try {
			dbHelper.saveReviewer(reviewer1);									// Save reviewer1 with untrusted status to DB
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		try {
			dbHelper.addReview(review);											// Save review to DB
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			dbHelper.addReview(review1);										// Save review1 to DB
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		List<Review> trustedReviewList = new ArrayList<>();						// Initialize empty trustedList
		try {
			 trustedReviewList = dbHelper.getTrustedReviewList(1);				// Run the search on reviews for our answer
		} catch (SQLException e) {
						e.printStackTrace();
		}
		assertTrue(trustedReviewList.size() == 1);								// List should only have the one trusted review
		
		Review testReview = trustedReviewList.get(0);							// get the only element on our list
		
		assertTrue(testReview.getText() == "my review1");						// make sure the review was saved and returned correctly
	}
}
