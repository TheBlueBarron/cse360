package databasePart1;

import static org.junit.jupiter.api.Assertions.*; 

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.*;

// Tests for student filtering, makes sure students can filter for answered and unanswered questions and get the list of reviewers.

class TestStudentFiltersAutomation {
	
	private DatabaseHelper dbHelper = new DatabaseHelper();
    
	@BeforeEach
	void setUp() throws Exception {
		// Sets up in memory database just for testing before each test
		dbHelper.connectToTestDB();
	}

	@AfterEach
	void tearDown() throws Exception {
		// Drops all tables and closes connection after each test
		dbHelper.dropAllTables();
		dbHelper.closeConnection();
	}

	@Test
	// Test ensures students can get a list of answered questions
	void testAnsweredQuestionListValid() {
		List<Question> answeredQuestions = new ArrayList<>();
		// Create questions for testing
		Question question = new Question(1, "q1 text", "q1 author",  false, false);
		Question question2 = new Question(2, "q2 text", "q2 author",  false, false);
		Question question3 = new Question(3, "q3 text", "q3 author",  false, false);
		// create mock answer attached to question2 & question3
		Answer answer = new Answer(1, 2, "a text", "a author", false);
		Answer answer2 = new Answer(2, 3, "a2 text", "a2 author", false);
		
		// Save the questions and answers to DB
		try {
		dbHelper.addQuestion(question);
		dbHelper.addQuestion(question2);
		dbHelper.addQuestion(question3);
		dbHelper.addAnswer(answer);
		dbHelper.addAnswer(answer2);
		answeredQuestions = dbHelper.getAnsweredQuestionsList(); // attempt to create the list of answered questions
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		// Ensure data is preserved and that the query returns the correct question (that is answered)
		assertTrue(answeredQuestions.get(0).getId() == 2);
		assertTrue(answeredQuestions.get(0).getText() == "q2 text");
		assertTrue(answeredQuestions.get(0).getAuthor() == "q2 author");	
		
		assertTrue(answeredQuestions.get(1).getId() == 3);
		assertTrue(answeredQuestions.get(1).getText() == "q3 text");
		assertTrue(answeredQuestions.get(1).getAuthor() == "q3 author");
	}
	
	@Test
	// Test ensures if there are no answered questions, the search for them turns up empty
	void testAnsweredQuestionListInvalid() {
		List<Question> answeredQuestions = new ArrayList<>();
		// Create questions for testing
		Question question = new Question(1, "q1 text", "q1 author",  false, false);
		Question question2 = new Question(2, "q2 text", "q2 author",  false, false);
		
		// Save the questions to DB
		try {
		dbHelper.addQuestion(question);
		dbHelper.addQuestion(question2);
		answeredQuestions = dbHelper.getAnsweredQuestionsList(); // attempt to create the list of answered questions
		} catch(SQLException e) {
			e.printStackTrace();
		}
		// Make sure our list is empty as none of the questions have answers
		assertTrue(answeredQuestions.size() == 0);
	}
	
	@Test
	// Test ensures students can get a list of unanswered questions
	void testUnansweredQuestionListValid() {
		List<Question> unansweredQuestions = new ArrayList<>();
		// Create questions for testing
		Question question = new Question(1, "q1 text", "q1 author",  false, false);
		Question question2 = new Question(2, "q2 text", "q2 author",  false, false);
		Question question3 = new Question(3, "q3 text", "q3 author",  false, false);

		// create mock answer attached to question2
		Answer answer = new Answer(1, 2, "a text", "a author", false);

		
		// Save the questions and answer to DB
		try {
		dbHelper.addQuestion(question);
		dbHelper.addQuestion(question2);
		dbHelper.addQuestion(question3);
		dbHelper.addAnswer(answer);
		unansweredQuestions = dbHelper.getUnansweredQuestionsList(); // attempt to create the list of unanswered questions
		} catch(SQLException e) {
			e.printStackTrace();
		}
		// Make sure our list is the unanswered questions and the data is saved properly
		assertTrue(unansweredQuestions.get(0).getId() == 1);
		assertTrue(unansweredQuestions.get(0).getText() == "q1 text");
		assertTrue(unansweredQuestions.get(0).getAuthor() == "q1 author");
		
		assertTrue(unansweredQuestions.get(1).getId() == 3);
		assertTrue(unansweredQuestions.get(1).getText() == "q3 text");
		assertTrue(unansweredQuestions.get(1).getAuthor() == "q3 author");
	}
	
	@Test
	// Test ensures if there are no unanswered questions, the search for them turns up empty
	void testUnansweredQuestionListInvalid() {
		List<Question> unansweredQuestions = new ArrayList<>();
		// Create questions for testing
		Question question = new Question(1, "q1 text", "q1 author",  false, false);
		Question question2 = new Question(2, "q2 text", "q2 author",  false, false);
		Question question3 = new Question(3, "q3 text", "q3 author",  false, false);

		// create mock answers attached to every question
		Answer answer = new Answer(1, 1, "a text", "a author", false);
		Answer answer2 = new Answer(2, 2, "a2 text", "a2 author", false);
		Answer answer3 = new Answer(3, 3, "a3 text", "a3 author", false);


		
		// Save the questions and answers to DB
		try {
		dbHelper.addQuestion(question);
		dbHelper.addQuestion(question2);
		dbHelper.addQuestion(question3);
		dbHelper.addAnswer(answer);
		dbHelper.addAnswer(answer2);
		dbHelper.addAnswer(answer3);
		unansweredQuestions = dbHelper.getUnansweredQuestionsList(); // attempt to create the list of unanswered questions
		} catch(SQLException e) {
			e.printStackTrace();
		}
		// Make sure our list of unanswered questions is empty, since we added an answer to every question.
		assertTrue(unansweredQuestions.size() == 0);
	}
	
	@Test
	// This method ensures students can get a list of reviewers
	void testReviewerListValid() {
		List<Reviewer> reviewerList = new ArrayList<>();
		// Create reviewers for testing
		Reviewer reviewer = new Reviewer(1, "r1 name", "xp1", 0 , 0, 0.0, false);
		Reviewer reviewer2 = new Reviewer(2, "r2 name", "xp2", 1 , 3, 3.0, false);
		
		// save our reviewers to the database
		try {
			dbHelper.saveReviewer(reviewer);
			dbHelper.saveReviewer(reviewer2);
			reviewerList = dbHelper.getAllReviewers(); 	// attempt to populate a list of all reviewers
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		assertTrue(reviewerList.get(0).getId() == 1);
		assertTrue(reviewerList.get(0).getName() == "r1 name");
		assertTrue(reviewerList.get(0).getRating() == 0.0);
		
		assertTrue(reviewerList.get(1).getId() == 2);
		assertTrue(reviewerList.get(1).getName() == "r2 name");
		assertTrue(reviewerList.get(1).getRating() == 3.0);
	}
	
	@Test
	// This method ensures when the list of reviewers is empty, the search for them returns empty
	void testReviewerListInvalid() {
		List<Reviewer> reviewerList = new ArrayList<>();
		// add and save no reviewers to the DB
		
		try {
			reviewerList = dbHelper.getAllReviewers(); 	// attempt to populate a list of all reviewers
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		// make sure the list is empty after saving no reviewers
		assertTrue(reviewerList.size() == 0);
	}
}
