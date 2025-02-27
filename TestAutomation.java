package databasePart1;

import java.sql.SQLException;
import java.util.List;
import application.Answer;
import application.Question;

/**
 * TestAutomation tests CRUD operations on questions and answers,
 * as well as search functionality and updates to the isResolved flag.
 */
public class TestAutomation {

    static int numPassed = 0;
    static int numFailed = 0;

    public static void main(String[] args) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        try {
            // Connect to the database
            databaseHelper.connectToDatabase();

            // ------------------------------
            // Run CRUD tests
            // ------------------------------
            System.out.println("=== Running CRUD Tests ===");
            // QUESTION tests
            testCreateQuestionValid(databaseHelper);
            testCreateQuestionInvalid(databaseHelper);
            testUpdateQuestionValid(databaseHelper);
            testUpdateQuestionInvalid(databaseHelper);
            testDeleteQuestionValid(databaseHelper);
            testDeleteQuestionInvalid(databaseHelper);
            
            // ANSWER tests
            testCreateAnswerValid(databaseHelper);
            testCreateAnswerInvalid(databaseHelper);
            testUpdateAnswerValid(databaseHelper);
            testUpdateAnswerInvalid(databaseHelper);
            testDeleteAnswerValid(databaseHelper);
            testDeleteAnswerInvalid(databaseHelper);

            // ------------------------------
            // Run Search and isResolved
            // ------------------------------
            System.out.println("\n=== Running Search and isResolved Tests ===");
            // Create new questions for search and isResolved tests
            Question q  = new Question("Is this working?", "Xander", true);
            Question q1 = new Question("Is the sky blue?", "Mr.Blue Sky", true);
            Question q2 = new Question("Why?", "Jesus", false);
            databaseHelper.addQuestion(q);
            databaseHelper.addQuestion(q1);
            databaseHelper.addQuestion(q2);

            // Add answers to these questions
            Answer a  = new Answer(q.getId(),  "Sure is!",          "Anonymous", true);
            Answer a1 = new Answer(q1.getId(), "I hope so!",        "Xander",    true);
            Answer a2 = new Answer(q1.getId(), "Depends on the time.", "Big Ben", false);
            Answer a3 = new Answer(q2.getId(), "Because.",          "Anonymous", false);
            databaseHelper.addAnswer(a);
            databaseHelper.addAnswer(a1);
            databaseHelper.addAnswer(a2);
            databaseHelper.addAnswer(a3);

            // Test Question search functionality
            System.out.println("-------------- TEST: Question Search --------------");
            testQuestionSearch(databaseHelper, "Is", true);
            testQuestionSearch(databaseHelper, "blue", true);
            testQuestionSearch(databaseHelper, "Dog", false);
            System.out.println();

            // Test Answer search functionality
            System.out.println("-------------- TEST: Answer Search --------------");
            testAnswersSearch(databaseHelper, "Depends", q1.getId(), true);
            testAnswersSearch(databaseHelper, "Depends", q.getId(), false);
            System.out.println();

            // Test updating isResolved for Questions
            System.out.println("-------------- TEST: Question isResolved --------------");
            testIsResolvedQuestion(databaseHelper, q.getId(), true, false);
            testIsResolvedQuestion(databaseHelper, q2.getId(), true, true);
            testIsResolvedQuestion(databaseHelper, q1.getId(), false, true);
            testIsResolvedQuestion(databaseHelper, q.getId(), false, true);
            System.out.println();

            // Test updating isResolved for Answers
            System.out.println("-------------- TEST: Answer isResolved --------------");
            testIsResolvedAnswer(databaseHelper, a.getId(), true, false);
            testIsResolvedAnswer(databaseHelper, a.getId(), false, true);
            testIsResolvedAnswer(databaseHelper, a1.getId(), false, false);
            testIsResolvedAnswer(databaseHelper, a3.getId(), true, true);

            // Print overall summary of tests
            System.out.println("\nTotal Tests Passed: " + numPassed);
            System.out.println("Total Tests Failed: " + numFailed);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            databaseHelper.closeConnection();
        }
    }
    
    // ===============================
    // Methods (CRUD tests)
    // ===============================
    
    // QUESTION TESTS
    
    private static void testCreateQuestionValid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: Create a VALID question");
        Question q = new Question("What is Java?", "Alice", false);
        try {
            db.addQuestion(q);
            if (q.getId() > 0) {
                System.out.println("PASS: Valid question created with ID " + q.getId());
                numPassed++;
            } else {
                System.out.println("FAIL: Valid question did not receive a valid ID");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("FAIL: Exception while adding valid question: " + e.getMessage());
            numFailed++;
        }
    }
    
    private static void testCreateQuestionInvalid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: Create an INVALID question (empty text)");
        try {
            // This should throw an IllegalArgumentException
            Question q = new Question("", "Bob", false);
            db.addQuestion(q);
            System.out.println("FAIL: Invalid question was created without error.");
            numFailed++;
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Caught expected IllegalArgumentException for empty question text.");
            numPassed++;
        } catch (SQLException e) {
            System.out.println("FAIL: Caught SQLException instead of IllegalArgumentException: " + e.getMessage());
            numFailed++;
        }
    }
    
    private static void testUpdateQuestionValid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: UPDATE a question with valid text");
        Question q = new Question("Old Text", "Carol", false);
        try {
            db.addQuestion(q);
            boolean result = db.updateQuestionText(q.getId(), "Updated Text");
            if (result) {
                System.out.println("PASS: Question text successfully updated.");
                numPassed++;
            } else {
                System.out.println("FAIL: updateQuestionText returned false.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("FAIL: Exception while updating question: " + e.getMessage());
            numFailed++;
        }
    }
    
    private static void testUpdateQuestionInvalid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: UPDATE a question with INVALID text (empty)");
        Question q = new Question("Some Text", "Dave", false);
        try {
            db.addQuestion(q);
            boolean result = db.updateQuestionText(q.getId(), "");
            if (!result) {
                System.out.println("PASS: updateQuestionText failed as expected for empty text.");
                numPassed++;
            } else {
                System.out.println("FAIL: updateQuestionText succeeded when it should fail for empty text.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("FAIL: Exception while updating question with invalid text: " + e.getMessage());
            numFailed++;
        }
    }
    
    private static void testDeleteQuestionValid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: DELETE a valid existing question");
        Question q = new Question("Question to Delete", "Eve", false);
        try {
            db.addQuestion(q);
            boolean result = db.deleteQuestion(q.getId());
            if (result) {
                System.out.println("PASS: Successfully deleted existing question.");
                numPassed++;
            } else {
                System.out.println("FAIL: Could not delete existing question.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("FAIL: Exception while deleting question: " + e.getMessage());
            numFailed++;
        }
    }
    
    private static void testDeleteQuestionInvalid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: DELETE a question with invalid ID (does not exist)");
        try {
            boolean result = db.deleteQuestion(-999);
            if (!result) {
                System.out.println("PASS: Attempting to delete a non-existent question returned false.");
                numPassed++;
            } else {
                System.out.println("FAIL: Non-existent question deletion returned true.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("FAIL: Exception while deleting non-existent question: " + e.getMessage());
            numFailed++;
        }
    }
    
    // ANSWER TESTS
    
    private static void testCreateAnswerValid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: Create a VALID answer");
        // Create a question first to associate with the answer
        Question q = new Question("Why test answers?", "Frank", false);
        try {
            db.addQuestion(q);
            Answer a = new Answer(q.getId(), "Because quality matters!", "Gina", false);
            db.addAnswer(a);
            if (a.getId() > 0) {
                System.out.println("PASS: Valid answer created with ID " + a.getId());
                numPassed++;
            } else {
                System.out.println("FAIL: Valid answer did not receive a valid ID.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("FAIL: Exception while creating a valid answer: " + e.getMessage());
            numFailed++;
        } catch (IllegalArgumentException e) {
            System.out.println("FAIL: Unexpected IllegalArgumentException for a valid answer text.");
            numFailed++;
        }
    }
    
    private static void testCreateAnswerInvalid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: Create an INVALID answer (empty text)");
        // Create a question to associate with the answer
        Question q = new Question("Question for invalid answer", "Hank", false);
        try {
            db.addQuestion(q);
            Answer a = new Answer(q.getId(), "", "Ian", false);
            db.addAnswer(a);
            System.out.println("FAIL: Invalid answer was created without error.");
            numFailed++;
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Caught expected IllegalArgumentException for empty answer text.");
            numPassed++;
        } catch (SQLException e) {
            System.out.println("FAIL: Caught SQLException instead of IllegalArgumentException: " + e.getMessage());
            numFailed++;
        }
    }
    
    private static void testUpdateAnswerValid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: UPDATE a valid answer text");
        Question q = new Question("Question for updating an answer", "Ken", false);
        try {
            db.addQuestion(q);
            Answer a = new Answer(q.getId(), "Original Answer", "Leo", false);
            db.addAnswer(a);
            boolean result = db.updateAnswerText(a.getId(), "Updated Answer");
            if (result) {
                System.out.println("PASS: Answer text successfully updated.");
                numPassed++;
            } else {
                System.out.println("FAIL: updateAnswerText returned false for a valid update.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("FAIL: Exception while updating answer: " + e.getMessage());
            numFailed++;
        }
    }
    
    private static void testUpdateAnswerInvalid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: UPDATE an answer with INVALID text (empty)");
        Question q = new Question("Question for invalid update", "Mike", false);
        try {
            db.addQuestion(q);
            Answer a = new Answer(q.getId(), "Some valid text", "Nina", false);
            db.addAnswer(a);
            boolean result = db.updateAnswerText(a.getId(), "");
            if (!result) {
                System.out.println("PASS: updateAnswerText failed as expected for empty text.");
                numPassed++;
            } else {
                System.out.println("FAIL: updateAnswerText succeeded when it should fail for empty text.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("FAIL: Exception while updating answer with invalid text: " + e.getMessage());
            numFailed++;
        }
    }
    
    private static void testDeleteAnswerValid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: DELETE a valid existing answer");
        Question q = new Question("Question for answer deletion", "Olivia", false);
        try {
            db.addQuestion(q);
            Answer a = new Answer(q.getId(), "Answer to be deleted", "Pete", false);
            db.addAnswer(a);
            boolean result = db.deleteAnswer(a.getId());
            if (result) {
                System.out.println("PASS: Successfully deleted existing answer.");
                numPassed++;
            } else {
                System.out.println("FAIL: Could not delete existing answer.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("FAIL: Exception while deleting answer: " + e.getMessage());
            numFailed++;
        }
    }
    
    private static void testDeleteAnswerInvalid(DatabaseHelper db) {
        System.out.println("___________________________________________________________________");
        System.out.println("Test: DELETE an answer with invalid ID (does not exist)");
        try {
            boolean result = db.deleteAnswer(-999);
            if (!result) {
                System.out.println("PASS: Attempting to delete a non-existent answer returned false.");
                numPassed++;
            } else {
                System.out.println("FAIL: Non-existent answer deletion returned true.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("FAIL: Exception while deleting non-existent answer: " + e.getMessage());
            numFailed++;
        }
    }
    
    // ===============================
    // Methods (Search and isResolved tests)
    // ===============================
    
    // Tests searching for questions with a given keyword.
    private static void testQuestionSearch(DatabaseHelper db, String keyword, boolean expectedAns) throws SQLException {
        List<Question> searchResults = db.searchQuestions(keyword);
        if (!searchResults.isEmpty()) {
            System.out.println("\n*** " + (expectedAns ? "PASS" : "FAIL") + " ***");
            System.out.println("Search results for: \"" + keyword + "\"");
            for (Question q : searchResults) {
                System.out.println(q.getText());
            }
        } else {
            System.out.println("\n*** " + (!expectedAns ? "PASS" : "FAIL") + " ***");
            System.out.println("No questions containing keyword \"" + keyword + "\"");
        }
    }
    
    // Tests searching for answers with a given keyword for a specific question.
    private static void testAnswersSearch(DatabaseHelper db, String keyword, int questionId, boolean expectedAns) throws SQLException {
        List<Answer> searchResults = db.searchAnswers(keyword, questionId);
        if (!searchResults.isEmpty()) {
            System.out.println("\n*** " + (expectedAns ? "PASS" : "FAIL") + " ***");
            System.out.println("Search results for: \"" + keyword + "\"");
            for (Answer a : searchResults) {
                System.out.println(a.getText());
            }
        } else {
            System.out.println("\n*** " + (!expectedAns ? "PASS" : "FAIL") + " ***");
            System.out.println("Question doesn't contain answer with keyword \"" + keyword + "\"");
        }
    }
    
    // Tests updating the isResolved flag for a question.
    private static void testIsResolvedQuestion(DatabaseHelper db, int id, boolean resolved, boolean expectedAns) throws SQLException {
        Question q = db.getQuestionById(id);
        if (q == null) {
            System.out.println("\n*** " + (!expectedAns ? "PASS" : "FAIL") + " ***");
            System.out.println("Question ID does not exist");
            return;
        } else if (q.getIsResolved() == resolved) {
            System.out.println("\n*** " + (!expectedAns ? "PASS" : "FAIL") + " ***");
            System.out.println("isResolved is already \"" + resolved + "\"");
            return;
        } else {
            db.updateIsResolvedQuestion(id, resolved);
            Question q1 = db.getQuestionById(id);
            System.out.println("\n*** PASS ***");
            System.out.println("Question \"" + q.getText() + "\"");
            System.out.println("isResolved changed from \"" + q.getIsResolved() + "\" to \"" + q1.getIsResolved() + "\"");
        }
    }
    
    // Tests updating the isResolved flag for an answer.
    private static void testIsResolvedAnswer(DatabaseHelper db, int id, boolean resolved, boolean expectedAns) throws SQLException {
        Answer a = db.getAnswerById(id);
        if (a == null) {
            System.out.println("\n*** " + (!expectedAns ? "PASS" : "FAIL") + " ***");
            System.out.println("Answer ID does not exist");
            return;
        } else if (a.getResolved() == resolved) {
            System.out.println("\n*** " + (!expectedAns ? "PASS" : "FAIL") + " ***");
            System.out.println("isResolved is already \"" + resolved + "\"");
            return;
        } else {
            db.updateAnswerResolved(id, resolved);
            Answer a1 = db.getAnswerById(id);
            System.out.println("\n*** PASS ***");
            System.out.println("Answer \"" + a.getText() + "\"");
            System.out.println("isResolved changed from \"" + a.getResolved() + "\" to \"" + a1.getResolved() + "\"");
        }
    }
}
