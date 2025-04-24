package databasePart1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.Answer;
import application.Question;

import java.util.ArrayList;
import java.util.List;


public class StaffRoleTest {

    
    private DatabaseHelper db;

    
    @BeforeEach
    public void setUp() throws Exception {
        db = new DatabaseHelper();
        db.connectToDatabase();
    }

    
    @Test
    public void testGetAllQuestions() throws Exception {
        List<Question> questions = db.getAllQuestions();
        assertNotNull(questions);
    }

    
    @Test
    public void testGetAnswersForQuestionValid() throws Exception {
        List<Question> questions = db.getAllQuestions();
        if (!questions.isEmpty()) {
            int qid = questions.get(0).getId();
            assertNotNull(db.getAnswersForQuestion(qid));
        }
    }

    
    @Test
    public void testGetRolesForUser() {
        String role = db.getRolesForUser("Student");  // Existing username on my db, change it to one of your existing username
        assertNotNull(role);
        assertTrue(role.length() > 0);
    }

    
    @Test
    public void testGetUserCreationDate() {
        String date = db.getUserCreationDate("Student"); // Existing username on my db
        assertNotNull(date);
        assertTrue(date.length() > 5);
    }

    
    @Test
    public void testGetQuestionsByUser() {
        List<Question> userQs = db.getQuestionsByUser("Student"); // Existing username on my db
        assertNotNull(userQs);
    }

    
    @Test
    public void testUnknownUserReturnsSafeDefaults() {
        String fakeUser = "FakeUser";

        String role = db.getRolesForUser(fakeUser);
        assertNotNull(role);
        assertTrue(role.isEmpty() || role.equals("user"));

        String date = db.getUserCreationDate(fakeUser);
        assertTrue(date == null || date.isEmpty());

        List<Question> userQs = db.getQuestionsByUser(fakeUser);
        assertNotNull(userQs);
        assertEquals(0, userQs.size());
    }

    
    @Test
    public void testUnansweredQuestionsFilter() throws Exception {
        List<Question> all = db.getAllQuestions();
        List<Question> unanswered = new ArrayList<>();

        for (Question q : all) {
            List<Answer> answers = db.getAnswersForQuestion(q.getId());
            if (answers == null || answers.isEmpty()) {
                unanswered.add(q);
            }
        }

        for (Question q : unanswered) {
            assertTrue(db.getAnswersForQuestion(q.getId()).isEmpty());
        }
    }
}
