package databasePart1;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import application.Answer;
import application.Question;
import application.Review;
import databasePart1.DatabaseHelper;


class DatabaseHelperTest {
	
	private static DatabaseHelper databasehelper = new DatabaseHelper();
    static Question q;
    static Answer a;
    static Review r1;
    static Review r2;
    static Review r3;
    static Review newReview;
    
	@BeforeAll
	static void setup ( ) throws SQLException {
		try {
			databasehelper.connectToDatabase();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		// Create Question, Answer, and Reviews once and reuse them across tests
        q = new Question("Is this working?", "Xander", true);
        databasehelper.addQuestion(q);
        a = new Answer(q.getId(), "Sure is!", "Anonymous", true);
        databasehelper.addAnswer(a);
        r1 = new Review(a.getId(), "Very epic review", "Mr.Review");
        r2 = new Review(a.getId(), "I like trains", "Steve");
        r3 = new Review(a.getId(), "I like apples", "Adam");
        newReview = new Review(a.getId(), "New Review", "Mr.Review");
        databasehelper.addReview(r1);
        databasehelper.addReview(r2);
        databasehelper.addReview(r3);
	dbHelper = new DatabaseHelper();
        dbHelper.clearAllTables();

        // Register test users
        dbHelper.register(new User("alice", "passsss1!", "student"));
        dbHelper.register(new User("bob", "passsss1", "student"));
        dbHelper.register(new User("charlie", "passsss1", "student"));
        dbHelper.register(new User("dave", "passsss1", "student"));
        dbHelper.register(new User("eve", "passsss1", "student"));
        dbHelper.register(new User("frank", "passsss1", "student"));
	}
	
	@Test
	void testAddReview() throws SQLException {
		databasehelper.addReview(newReview);
		assertEquals("New Review", databasehelper.getReviewById(newReview.getId()).getText(), "The Text should be the same");
	}
	
	@Test
	void testGetReviewById() throws SQLException {
		assertEquals("Very epic review", databasehelper.getReviewById(r1.getId()).getText(), "The Text should be the same");
	}

	@Test
        void testGetReviewByIdNotFound() throws SQLException {
            Review result = databasehelper.getReviewById(9999);
        
            assertNull(result, "The review should not be found, so the result should be null");
        }
	
	@Test
	void testGetAllReviews() throws SQLException {
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(r1);
		reviewList.add(r2);
		reviewList.add(r3);
		
        assertEquals(reviewList.get(0).getText(), databasehelper.getAllReviews().get(0).getText(), "Review 1 should be in the database");
        assertEquals(reviewList.get(1).getText(), databasehelper.getAllReviews().get(1).getText(), "Review 2 should be in the database");
        assertEquals(reviewList.get(2).getText(), databasehelper.getAllReviews().get(2).getText(), "Review 3 should be in the database");
	}
	
	@Test
	void testUpdateReviewText() throws SQLException {
		databasehelper.updateReviewText(r1.getId(), "New Text");
		assertEquals("New Text", databasehelper.getReviewById(r1.getId()).getText(), "The text should be the same.");
		
		databasehelper.updateReviewText(r1.getId(), "Very epic review");
		assertEquals("Very epic review", databasehelper.getReviewById(r1.getId()).getText(), "The text should be the same.");
	}
	
	@Test
	void testDeleteReview() throws SQLException {
		assertTrue(databasehelper.deleteReview(r1.getId()));
	}
	
	@Test
	void testSearchReviews() throws SQLException {
		
		List<Review> expectedResults = new ArrayList<>();
		expectedResults.add(r2);
		expectedResults.add(r3);
		
		List<Review> actualList = databasehelper.searchReviews("like", q.getId());
		
		assertEquals(expectedResults.get(0).getText(), actualList.get(0).getText(), "Review 2 should be in the database");
        assertEquals(expectedResults.get(1).getText(), actualList.get(1).getText(), "Review 3 should be in the database");
	}

	@Test
	void testCantSearchReviews() throws SQLException {
		assertTrue(databasehelper.searchReviews("OOGABOOGA", q.getId()).isEmpty());
	}
	
	@Test
	void testGetReviewsForAnswers() throws SQLException {
		
		List<Review> expectedResults = new ArrayList<>();
		expectedResults.add(r1);
		expectedResults.add(r2);
		expectedResults.add(r3);
		
		List<Review> actualList = databasehelper.getReviewsForAnswers(a.getId());
		
		assertEquals(expectedResults.get(0).getText(), actualList.get(0).getText(), "Review 1 should be in the database");
        assertEquals(expectedResults.get(1).getText(), actualList.get(1).getText(), "Review 2 should be in the database");
        assertEquals(expectedResults.get(2).getText(), actualList.get(2).getText(), "Review 3 should be in the database");
	}
	// JUnit Test 21
    @Test
    public void testCreateConversationBetweenTwoUsers() throws SQLException {
        boolean created = dbHelper.createConversation("alice", "bob");
        assertTrue(created);
    }

    // JUnit Test 22
    @Test
    public void testDuplicateConversationIsNotCreated() throws SQLException {
        dbHelper.createConversation("alice", "bob");
        boolean result = dbHelper.createConversation("alice", "bob");
        assertFalse(result);
    }

    // JUnit Test 23
    @Test
    public void testSelfConversationNotAllowed() throws SQLException {
        boolean result = dbHelper.createConversation("alice", "alice");
        assertFalse(result);
    }

    // JUnit Test 24
    @Test
    public void testGetConversationsForUserReturnsCorrectList() throws SQLException {
        dbHelper.createConversation("alice", "charlie");
        List<Conversations> convos = dbHelper.getConversationsForUser("alice");
        assertFalse(convos.isEmpty());
        for (Conversations convo : convos) {
            assertTrue(convo.getParticipent_1_id().equals("alice") || convo.getParticipent_2_id().equals("alice"));
        }
    }

    // JUnit Test 25
    @Test
    public void testInsertMessageIntoConversation() throws SQLException {
        Conversations convo = dbHelper.getConversationsForUser("alice").get(0);
        dbHelper.insertMessage(convo.getId(), "alice", "Hello, Bob!");
        List<Message> messages = dbHelper.getMessagesForConversation(convo.getId());
        assertTrue(messages.stream().anyMatch(m -> m.getText().equals("Hello, Bob!")));
    }

    // JUnit Test 26
    @Test
    public void testMessageSenderAndTextStoredCorrectly() throws SQLException {
        Conversations convo = dbHelper.getConversationsForUser("alice").get(0);
        dbHelper.insertMessage(convo.getId(), "bob", "Hey Alice!");
        List<Message> messages = dbHelper.getMessagesForConversation(convo.getId());
        Message last = messages.get(messages.size() - 1);
        assertEquals("bob", last.getSenderId());
        assertEquals("Hey Alice!", last.getText());
    }

    // JUnit Test 27
    @Test
    public void testEmptyMessageListOnNewConversation() throws SQLException {
        dbHelper.createConversation("alice", "dave");
        List<Conversations> convos = dbHelper.getConversationsForUser("dave");
        Conversations convo = convos.get(0);
        List<Message> messages = dbHelper.getMessagesForConversation(convo.getId());
        assertEquals(0, messages.size());
    }

    // JUnit Test 28
    @Test
    public void testMessageTextCannotBeBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Message(0, 1, "alice", "   ");
        });
    }

    // JUnit Test 29
    @Test
    public void testGetMostRecentMessageReturnsLastInserted() throws SQLException {
        dbHelper.createConversation("alice", "eve");
        Conversations convo = dbHelper.getConversationsForUser("eve").get(0);

        dbHelper.insertMessage(convo.getId(), "alice", "First");
        dbHelper.insertMessage(convo.getId(), "eve", "Second");
        dbHelper.insertMessage(convo.getId(), "alice", "MOST RECENT");

        Message mostRecent = dbHelper.getMostRecentMessage(convo.getId());

        assertNotNull(mostRecent);
        assertEquals("MOST RECENT", mostRecent.getText());
    }

    // JUnit Test 30
    @Test
    public void testMultipleConversationsPerUser() throws SQLException {
        dbHelper.createConversation("alice", "frank");
        dbHelper.createConversation("alice", "charlie");

        List<Conversations> convos = dbHelper.getConversationsForUser("alice");
        assertTrue(convos.size() >= 2);
    }

    @AfterAll
    public static void teardown() throws SQLException {
        dbHelper.clearAllTables();
    }

}
