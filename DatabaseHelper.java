package databasePart1;

import application.User; 
import application.Question; 
import application.Answer;
import application.Conversations;
import application.Message;
import application.Review;
import application.Reviewer;

import java.sql.*; 
import java.util.ArrayList; 
import java.util.List; 
import java.util.UUID;

/**
 * <p> Title: Database Helper </p>
 * 
 * <p> Description:
 * 		The DatabaseHelper class is responsible for managing the connection to the database,
 * 		performing various operations for the application. These include handling all user
 * 		information and associated operations (such as logging in, invitation code requests,
 * 		role operations, etc.), and discussion board information and associated operations 
 * 		(such as storing questions, answers, etc.). </p>
 * 
 * @author Wednesday 44 of CSE 360
 */
public class DatabaseHelper {
	
	public static User cur_user;
	
	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt
	
	/**
	 * This method connects to the database for access to tables and operations.
	 * 
	 * @throws SQLException	upon failure to find JDBC driver
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	/**
	 * This method creates all needed tables used for storage in the database.
	 * 
	 * @throws SQLException upon failure to create at least one table
	 */
	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(100), "
				+ "creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    // Questions table
	    String questionsTable = "CREATE TABLE IF NOT EXISTS Questions ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "text VARCHAR(1024), "
	            + "author VARCHAR(255), "
	            + "isResolved BOOLEAN)"; 
	    statement.execute(questionsTable);
	    
	    // Answers table
	    String answersTable = "CREATE TABLE IF NOT EXISTS Answers ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "question_id INT, "
	            + "text VARCHAR(1024), "
	            + "author VARCHAR(255), "
	            + "resolved BOOLEAN, " 
	            + "FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE)";
	    statement.execute(answersTable);
	    
	    // Reviewer table, stores their rating and name
	    String reviewersTable = "CREATE TABLE IF NOT EXISTS Reviewers ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "name VARCHAR(255), "
	    		+ "xp VARCHAR(1024), "
	    		+ "likes INT DEFAULT 0, "
	    		+ "dislikes INT DEFAULT 0, "
	    		+ "rating DECIMAL(5, 3) DEFAULT 0.0, "
	    		+ "numOfReviews INT DEFAULT 0,"
	    		+ "trusted BOOLEAN DEFAULT FALSE,"
	    		+ "score DOUBLE DEFAULT 0.0)";
	    statement.execute(reviewersTable);
	    
	    // Reviews table (linked to a reviewer)
	    String reviewsTable = "CREATE TABLE IF NOT EXISTS Reviews ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "answer_id INT, "
	            + "text VARCHAR(1024), "
	            + "reviewer_id INT, "
	            + "FOREIGN KEY (reviewer_id) REFERENCES Reviewers(id))";
	    statement.execute(reviewsTable);
	    
	    // Requests table
	    String requestsTable = "CREATE TABLE IF NOT EXISTS Requests ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "userName VARCHAR(255) UNIQUE, "
	    		+ "request VARCHAR(20))";
	    statement.execute(requestsTable);
	    
	    String conversationTable = "CREATE TABLE IF NOT EXISTS Converstations("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "participent_1_id VARCHAR(255), "
	            + "participen_2_id VARCHAR(255))";
	    statement.execute(conversationTable);
	    
	    String messageTable = "CREATE TABLE IF NOT EXISTS Messages("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "converstaion_id INT, "
	            + "sender_id VARCHAR(255), "
	    		+ "text VARCHAR(1025))";
	    statement.execute(messageTable);
	}


	/**
	 * This method checks if the database is empty by checking if the count is zero.
	 * 
	 * @return Returns true if database is empty.
	 * @throws SQLException
	 */
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	/**
	 * This method registers a new user in the database by adding their information to the Users table.
	 * 
	 * @param user				User object to get information of and register into database.
	 * @throws SQLException		upon failure to access database
	 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.executeUpdate();
			// Set current user to given user
			cur_user = user;
		}
	}

	/**
	 * This method validates a user's login credentials by checking 
	 * if their information exists in the Users table.
	 * 
	 * @param user				User object to get information of and check against database.
	 * @return 					Returns true if login of given user was successful.
	 * @throws SQLException		upon failure to access database
	 */
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			// Set current user to given user.
			cur_user = user;
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	/**
	 * This method checks if a user already exists in the database based on their userName.
	 * 
	 * @param userName		String to check against database.
	 * @return				Returns true if userName is found in database.
	 */
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	/**
	 * This method retrieves the role of a user from the database using their userName.
	 * 
	 * @param userName		String to check against database.
	 * @return				Returns a String containing the user's role.
	 */
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	/**
	 * This method generates a new invitation code and inserts it into the database.
	 * 
	 * @return		Returns String of the generated code.
	 */
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	/**
	 * This method validates an invitation code by checking if it is exists and is unused.
	 * 
	 * @param code			Code to check against database.
	 * @return				Returns true if code is unused.
	 */
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	/**
	 * This method marks the invitation code as used in the database.
	 * 
	 * @param code			Code to mark as used.
	 */
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// ---------------- Question Operations ----------------

	/**
	 * This method inserts a new question into the database.
	 * It uses a prepared statement to set the question's text and author,
	 * then it retrieves the auto-generated ID and updates the question object.
	 * 
	 * @param question			{@code Question} object to get information of and insert into database.
	 * @throws SQLException		upon failure to access database
	 */
	public void addQuestion(Question question) throws SQLException {
	    String insertQuestion = "INSERT INTO Questions (text, author, isResolved) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, question.getText());       // set the question text
	        pstmt.setString(2, question.getAuthor());       // set the question author
	        pstmt.setBoolean(3, question.getIsResolved());
	        pstmt.executeUpdate();                          // run the insert
	        ResultSet rs = pstmt.getGeneratedKeys();        // get the new ID from the database
	        if (rs.next()) {
	            question.setId(rs.getInt(1));               // update our question object with its ID
	        }
	    }
	}

	/**
	 * This method fetches a question from the database based on its ID.
	 * 
	 * @param id				Integer ID to find desired question.
	 * @return					Returns {@code Question} object constructed with information from database.
	 * @throws SQLException		upon failure to access database
	 */
	public Question getQuestionById(int id) throws SQLException {
	    String query = "SELECT * FROM Questions WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);                            // set the ID to search for
	        ResultSet rs = pstmt.executeQuery();            // execute the query
	        if (rs.next()) {
	            return new Question(
	                rs.getInt("id"),
	                rs.getString("text"),
	                rs.getString("author"),
	                rs.getBoolean("isResolved") 			
	            );
	        }
	    }
	    return null;  // return null if no question is found
	}
	
	/**
	 * This method retrieves all questions from the database and returns them as a list.
	 * 
	 * @return					Returns list of {@code Question} constructed with information from database.
	 * @throws SQLException		upon failure to access database
	 */
	public List<Question> getAllQuestions() throws SQLException {
	    List<Question> list = new ArrayList<>();
	    String query = "SELECT * FROM Questions";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();            // execute the query to get all questions
	        while (rs.next()) {
	            list.add(new Question(
	                rs.getInt("id"),
	                rs.getString("text"),
	                rs.getString("author"),
	                rs.getBoolean("isResolved") 
	                
	            ));
	        }
	    }
	    return list;  // return the list of questions
	}

	/**
	 * This method updates the text of a question identified by its ID, 
	 * but only if the new text is valid (in other words, non-empty).
	 * 
	 * @param id				Integer ID to find question to edit.
	 * @param newText			String containing edit to perform.
	 * @return					Returns true if the edit was valid and applied in database.
	 * @throws SQLException		upon failure to access database
	 */
	public boolean updateQuestionText(int id, String newText) throws SQLException {
	    if (!Question.isValidQuestionText(newText)) {
	        return false;  // new text is invalid, so we don't update
	    }
	    String updateQuery = "UPDATE Questions SET text = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setString(1, newText);                   // set the new text
	        pstmt.setInt(2, id);                           // specify which question to update
	        return pstmt.executeUpdate() > 0;              // return true if at least one row was updated
	    }
	}
	
	/** 
	 * This method updates a question's resolved flag by taking 
	 * an ID and the Boolean value to change the flag to.
	 * 
	 * @param id				Integer ID of question to change flag of.
	 * @param isResolved		{@code boolean} value to update flag to.
	 * @throws SQLException		upon failure to access database
	 */
	public void updateIsResolvedQuestion(int id, boolean isResolved) throws SQLException {
		String updateQuery = "UPDATE Questions SET isResolved = ? WHERE id = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
			pstmt.setBoolean(1, isResolved);			   // update resolved flag
	        pstmt.setInt(2, id);                           // specify which question to update
	        pstmt.executeUpdate();            
		}
	}

	/**
	 * This method deletes a question from the database after searching for it using an ID.
	 * 
	 * @param id				Integer ID to find question to delete from table.
	 * @return					Returns true if question could be deleted.
	 * @throws SQLException		upon failure to access database.
	 */
	public boolean deleteQuestion(int id) throws SQLException {
	    String deleteQuery = "DELETE FROM Questions WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	        pstmt.setInt(1, id);                           // set the ID of the question to delete
	        return pstmt.executeUpdate() > 0;              // return true if deletion was successful
	    }
	}

	/** 
	 * This method looks for questions whose text OR author 
	 * field includes the given keyword (case-insensitive).
	 * 
	 * @param keyword			String of keyword to check against Questions table.
	 * @return					Returns list of all questions containing the keyword.
	 * @throws SQLException		upon failure to access database.
	 */
	public List<Question> searchQuestions(String keyword) throws SQLException {
	    List<Question> list = new ArrayList<>();
	    String query = "SELECT * FROM Questions WHERE LOWER(text) LIKE ? OR LOWER(author) LIKE ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, "%" + keyword.toLowerCase() + "%");  // prepare keyword for search
	        pstmt.setString(2, "%" + keyword.toLowerCase() + "%");
	        ResultSet rs = pstmt.executeQuery();            // execute the search query
	        while (rs.next()) {
	            list.add(new Question(
	                rs.getInt("id"),
	                rs.getString("text"),
	                rs.getString("author"),
	                rs.getBoolean("isResolved")
	            ));
	        }
	    }
	    return list;  // return the list of matching questions
	}
	
	/**
	 * This method looks for questions whose author field contains the
	 * given keyword (case-insensitive). For use in displaying all of a user's contributions.
	 * 
	 * @param author			String of keyword to check against the Questions table's author field.
	 * @return					Returns list of questions whose author contains the given keyword.
	 * @throws SQLException		upon failure to access database
	 */
	public List<Question> searchQuestionsByAuthor(String author) throws SQLException {
	    List<Question> list = new ArrayList<>();
	    String query = "SELECT * FROM Questions WHERE LOWER(author) LIKE ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, "%" + author.toLowerCase() + "%");  // prepare keyword for search
	        ResultSet rs = pstmt.executeQuery();            // execute the search query
	        while (rs.next()) {
	            list.add(new Question(
	                rs.getInt("id"),
	                rs.getString("text"),
	                rs.getString("author"),
	                rs.getBoolean("isResolved")
	            ));
	        }
	    }
	    return list;  // return the list of matching questions
	}

	// ---------------- Answer Operations ----------------

	/** 
	 * This method inserts a new {@code Answer} into the Answers table, 
	 * then retrieves the auto-generated ID and sets it in the {@code Answer} object.
	 * 
	 * @param answer			{@code Answer} to insert into Answers table.
	 * @throws SQLException		upon failure to access database
	 */
	public void addAnswer(Answer answer) throws SQLException {
	    String insertAnswer = "INSERT INTO Answers (question_id, text, author, resolved) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setInt(1, answer.getQuestionId());      // set the ID of the question this answer belongs to
	        pstmt.setString(2, answer.getText());           // set the answer text
	        pstmt.setString(3, answer.getAuthor());         // set the answer author
	        pstmt.setBoolean(4, answer.getResolved());		// set resolved flag *
	        pstmt.executeUpdate();                          // execute the insert
	        ResultSet rs = pstmt.getGeneratedKeys();        // get the generated ID
	        if (rs.next()) {
	            answer.setId(rs.getInt(1));                 // update the answer with its new ID
	        }
	    }
	}

	/** 
	 * This method retrieves a specific answer from the database using its ID.
	 * 
	 * @param id				Answer ID to search for in database.
	 * @return					Returns {@code Answer} constructed using information from database.
	 * @throws SQLException		upon failure to access database
	 */
	public Answer getAnswerById(int id) throws SQLException {
	    String query = "SELECT * FROM Answers WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);                           // set the answer ID
	        ResultSet rs = pstmt.executeQuery();           // execute the query
	        if (rs.next()) {
	            return new Answer(
	                rs.getInt("id"),
	                rs.getInt("question_id"),
	                rs.getString("text"),
	                rs.getString("author"),
	                rs.getBoolean("resolved") 
	            );
	        }
	    }
	    return null;  // return null if no answer is found
	}

	/** 
	 * This method retrieves all answers from the database and returns them as a list.
	 * 
	 * @return					Returns list of {@code Answer} all answers in the database.
	 * @throws SQLException		upon failure to access database
	 */
	public List<Answer> getAllAnswers() throws SQLException {
	    List<Answer> list = new ArrayList<>();
	    String query = "SELECT * FROM Answers";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();           // execute the query to get all answers
	        while (rs.next()) {
	            list.add(new Answer(
	                rs.getInt("id"),
	                rs.getInt("question_id"),
	                rs.getString("text"),
	                rs.getString("author"),
	                rs.getBoolean("resolved")
	            ));
	        }
	    }
	    return list;  // return the list of answers
	}

	/** 
	 * This method returns all answers linked to a particular question ID.
	 * 
	 * @param questionId			Question ID to search for in database for retrieval of associated answers.
	 * @return						Returns list of {@code Answer} 
	 * @throws SQLException			upon failure to access database
	 */
	public List<Answer> getAnswersForQuestion(int questionId) throws SQLException {
	    List<Answer> list = new ArrayList<>();
	    String query = "SELECT * FROM Answers WHERE question_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, questionId);                  // set the question ID to filter answers
	        ResultSet rs = pstmt.executeQuery();           // execute the query
	        while (rs.next()) {
	            list.add(new Answer(
	                rs.getInt("id"),
	                rs.getInt("question_id"),
	                rs.getString("text"),
	                rs.getString("author"),
	                rs.getBoolean("resolved")
	            ));
	        }
	    }
	    return list;  // return the list of answers for the question
	}

	/** 
	 * This method updates the text of an answer, found with an
	 * ID, if the new text is valid (in other words, non-empty).
	 * 
	 * @param id					Integer ID to find the answer in the database.
	 * @param newText				String to change the answer's text to.
	 * @return						Returns true if the edit was successful.
	 * @throws SQLException			upon failure to access database
	 */
	public boolean updateAnswerText(int id, String newText) throws SQLException {
	    if (!Answer.isValidAnswerText(newText)) {
	        return false;  // new text is invalid, so don't update
	    }
	    String updateQuery = "UPDATE Answers SET text = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setString(1, newText);                  // set the new text
	        pstmt.setInt(2, id);                          // specify which answer to update
	        return pstmt.executeUpdate() > 0;             // return true if update was successful
	    }
	}
	
	/** 
	 * This method updates an answer's resolved flag in the database after
	 * searching for it using an ID.
	 * 
	 * @param id				Integer ID to search for the answer in the database.
	 * @param resolved			Boolean to set the resolved flag to.
	 * @throws SQLException		upon failure to access database
	 */
	public void updateAnswerResolved(int id, boolean resolved) throws SQLException {
		String updateQuery = "UPDATE Answers SET resolved = ? WHERE id = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
			pstmt.setBoolean(1, resolved);			   	   // update resolved flag *
	        pstmt.setInt(2, id);                           // specify which question to update
	        pstmt.executeUpdate();            
		}
	}

	/**
	 * This method deletes an answer from the database based on its ID.
	 * 
	 * @param id				Integer ID to search for the answer in the database.
	 * @return					Returns true if the answer's deletion was successful.
	 * @throws SQLException		upon failure to access database
	 */
	public boolean deleteAnswer(int id) throws SQLException {
	    String deleteQuery = "DELETE FROM Answers WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	        pstmt.setInt(1, id);                          // set the answer ID to delete
	        return pstmt.executeUpdate() > 0;             // return true if deletion succeeded
	    }
	}
	
	/**
	 * This method looks for answers whose text OR author field includes the given keyword (case-insensitive).
	 * 
	 * @param keyword			String of the keyword to search the Answers table for.
	 * @param id				Integer ID of the question ID to search for answers with.
	 * @return					Returns a list of all answers containing the keyword.
	 * @throws SQLException		upon failure to access database
	 */
	public List<Answer> searchAnswers(String keyword, int id) throws SQLException {
		List<Answer> list = new ArrayList<>();
		String query = "SELECT * FROM Answers WHERE question_id = ? AND (LOWER(text) LIKE ? OR LOWER(author) LIKE ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			pstmt.setString(2, "%" + keyword.toLowerCase() + "%");
			pstmt.setString(3, "%" + keyword.toLowerCase() + "%");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				list.add(new Answer(
		                rs.getInt("id"),
		                rs.getInt("question_id"),
		                rs.getString("text"),
		                rs.getString("author"),
		                rs.getBoolean("resolved")
		            ));
			}
			return list;
		} 
	}
	
	/**
	 * This method looks for answers whose author field includes the given keyword (case-insensitive).
	 * For use in displaying all of a user's contributions, not limited to a specific question.
	 * 
	 * @param author			String keyword to search author fields for.
	 * @return					Returns a list of all answers found by the search.
	 * @throws SQLException		upon failure to access database
	 */
	public List<Answer> searchAnswersByAuthor(String author) throws SQLException {
		List<Answer> list = new ArrayList<>();
		String query = "SELECT * FROM Answers WHERE LOWER(author) LIKE ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, author);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				list.add(new Answer(
		                rs.getInt("id"),
		                rs.getInt("question_id"),
		                rs.getString("text"),
		                rs.getString("author"),
		                rs.getBoolean("resolved")
		            ));
			}
			return list;
		}
	}

	// ----------------- Other User Operations -------------------
	
	/** 
	 * This method sets a one-time password for a user based on the given input.
	 * 
	 * @param userName				String username to find whose password to replace.
	 * @param oneTimePassword		String password to change given user's password to.
	 * @return						Returns true if update was successful.
	 */
    public boolean setOneTimePassword(String userName, String oneTimePassword) {
        String updatePassword = "UPDATE cse360users SET password = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updatePassword)) {
            pstmt.setString(1, oneTimePassword);
            pstmt.setString(2, userName);
            return pstmt.executeUpdate() > 0; // Returns true if password was successfully updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * This method deletes a user from the database. If the user is admin, they cannot be deleted.
     * 
     * @param userName			String username of the user to delete.
     * @return					Returns true if the deletion was successful.
     */
    public boolean deleteUser(String userName) {
        String deleteUser = "DELETE FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteUser)) {
            
        	if (getUserRole(userName).contains("admin")) {
            	return false;
            }
        	
        	pstmt.setString(1, userName);
            return pstmt.executeUpdate() > 0; // Returns true if user was successfully deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * This method lists all the users in a database by iterating through the database and
     * retrieving all pertinent information.
     * 
     * @return			Returns a String containing a full, formatted list.
     */
    public String listUsers() {
        StringBuilder userData = new StringBuilder();
        String listUsers = "SELECT userName, role FROM cse360users";
        try (PreparedStatement pstmt = connection.prepareStatement(listUsers);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                userData.append("User: ").append(rs.getString("userName")).append(", Role: ")
                .append(rs.getString("role")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to fetch users."; // Return error message if something goes wrong
        }
        return userData.toString();
    }
    
    /**
     * This method replaces a user's role completely by searching for 
     * them and editing their role field.
     * 
     * @param userName			String username of the user whose role will be replaced.
     * @param newRole			String of the role to fill in the role field with.
     * @return					Returns true if the replacement was successful.
     */
    public boolean manageUserRole(String userName, String newRole) {
        String updateRole = "UPDATE cse360users SET role = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateRole)) {
            pstmt.setString(1, newRole);
            pstmt.setString(2, userName);
            return pstmt.executeUpdate() > 0; // Returns true if the role was successfully updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     * This method adds a role to a user by concatenating it to their existing role with a comma
     * if they do not already have it, allowing them to have multiple roles at a time. 
     * 
     * @param userName			String username of the user whose role will be added to.
     * @param addedRole			String of the role to add to the user's role field.
     * @return					Returns true if the update was successful.
     */
    public boolean addUserRole(String userName, String addedRole) {
    	String addRole = "UPDATE cse360users SET role = ? WHERE userName = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(addRole)) {
    		
    		// Check that user does not already have this role
    		if (getUserRole(userName).contains(addedRole)) {
    			return false;
    		}
    		
    		// If not, add desired role to role attribute
    		pstmt.setString(1, getUserRole(userName) + "," + addedRole);
    		pstmt.setString(2, userName);
    		
    		return pstmt.executeUpdate() > 0; // Returns true if the role was successfully added 
    	} catch (SQLException e) {
    		e.printStackTrace();
            return false;
    	}
    }
    
    /**
     * This method deletes a specific role from a user if they have it. If a user has multiple roles, this
     * function will preserve all other roles. If a user only has one role, the user will be set to a default role.
     * Admins cannot delete their own admin role.
     * 
     * @param userName			String username of the user whose role will be modified.
     * @param removedRole		String of the role to remove from the user.
     * @return					Returns true if the update was successful.
     */
    public boolean removeUserRole(String userName, String removedRole) {
    	String removeRole = "UPDATE cse360users SET role = ? WHERE userName = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(removeRole)) {
    		
    		// Checks that the user has the role to remove
    		if (!getUserRole(userName).contains(removedRole)) {
    			return false;
    		}
    		
    		// Checks if the role being removed is their admin role;
    		// if it is, cancel the operation
    		if (cur_user.getUserName().equals(userName) && removedRole.equals("admin")) {
    			return false;
    		}
    		
    		// If the user only has the one role, restore to default
    		if (!getUserRole(userName).contains(",")) {
    			pstmt.setString(1, "user");
    			pstmt.setString(2, userName);
    			return pstmt.executeUpdate() > 0; // Returns true if the role was successfully added 
    		}
    		
    		/* Out-of-place algorithm to remove the desired role from the user's
    		 * attributes; shouldn't pose too much of a runtime issue as max
    		 * length of user's role can be 5
    		 */
    		String[] roles = getUserRole(userName).split(",");
    		String[] newRoles = new String[roles.length - 1];
    		int newRolesPointer = 0;
    		for (int i = 0; i < roles.length; i++) {
    			if (!roles[i].equals(removedRole)) {
    				newRoles[newRolesPointer] = roles[i];
    				newRolesPointer++;
    			}
    		}
    		
    		if (newRolesPointer > 1) {
    			// If user has multiple roles still, join them into new role value
    			pstmt.setString(1, String.join(",", newRoles));
    			pstmt.setString(2, userName);
    		} else {
    			// If user only has one role after for loop, simply set that as their role
    			pstmt.setString(1, newRoles[0]);
    			pstmt.setString(2, userName);
    		}
    		
    		return pstmt.executeUpdate() > 0; // Returns true if the role was successfully added
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
	// ---------------- Review Operations ----------------
	
	/**
	 * This method adds a review object to the database.
	 *
	 * @param review A Review object created by input to add to database
	 * @return A list of all review objects
	 * @throws SQLException If a database access error occurs.
	 */
	public void addReview(Review review) throws SQLException {
	    String insertReview = "INSERT INTO Reviews (answer_id, text, reviewer_id) VALUES (?, ?, ?)";
	    String updateReviewer = "UPDATE Reviewers SET numOfReviews = numOfReviews + 1 WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertReview, Statement.RETURN_GENERATED_KEYS);
	         PreparedStatement updatePstmt = connection.prepareStatement(updateReviewer)) {
	        pstmt.setInt(1, review.getAnswerId());      // set the ID of the question this answer belongs to
	        pstmt.setString(2, review.getText());           // set the review text
	        pstmt.setInt(3, review.getReviewerId());         // set the review author
	        pstmt.executeUpdate();                          // execute the insert
	        ResultSet rs = pstmt.getGeneratedKeys();        // get the generated ID
	        if (rs.next()) {
	            review.setId(rs.getInt(1));                 // update the answer with its new ID
	        }
	        
	        updatePstmt.setInt(1, review.getReviewerId()); // set the reviewer ID for updating
	        updatePstmt.executeUpdate();                   // update numOfReviews for reviewer
	    }
	}
	
	/**
	 * This method fetches a review from the database based on its ID.
	 *
	 * @param id Id used to find specified review
	 * @return A Review object of found review
	 * @throws SQLException If a database access error occurs.
	 */
	public Review getReviewById(int id) throws SQLException {
	    String query = "SELECT * FROM Reviews WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);                            // set the ID to search for
	        ResultSet rs = pstmt.executeQuery();            // execute the query
	        if (rs.next()) {
	            return new Review(
	                rs.getInt("id"), // gets review id
	                rs.getString("text"), // gets review text
	                rs.getInt("reviewer_id") // gets review author by ID
	            );
	        }
	    }
	    return null;  // return null if no question is found
	}
	
	/**
	 * This method retrieves all reviews from the database and returns them as a list.
	 *
	 * 
	 * @return A list of all review objects
	 * @throws SQLException If a database access error occurs.
	 */
	public List<Review> getAllReviews() throws SQLException {
	    List<Review> list = new ArrayList<>();
	    String query = "SELECT * FROM Reviews";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();           // execute the query to get all reviews
	        while (rs.next()) {
	            list.add(new Review(
	                rs.getInt("id"), // gets review id
	                rs.getInt("answer_id"), // gets answer_id
	                rs.getString("text"), // gets review text
	                rs.getInt("reviewer_id") // gets review author ID
	            ));
	        }
	    }
	    return list;  // return the list of reviews
	}
	
	/**
	 *This method updates the text of an review if the new text is valid.
	 *
	 * @param id The id used to query the review to update
	 * @param newText A string of text to replace review text with
	 * @return A boolean for whether update was successful or not
	 * @throws SQLException If a database access error occurs.
	 */
	public boolean updateReviewText(int id, String newText) throws SQLException {
	    if (!Review.isValidReviewText(newText)) {
	        return false;  // new text is invalid, so don't update
	    }
	    String updateQuery = "UPDATE Reviews SET text = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setString(1, newText);                  // set the new text
	        pstmt.setInt(2, id);                          // specify which review to update
	        return pstmt.executeUpdate() > 0;             // return true if update was successful
	    }
	}
	
	/**
	 *	This method deletes a review from the database based on its ID.
	 *
	 * @param id The id used to query the review to delete
	 * @return A boolean for whether delete was successful or not
	 * @throws SQLException If a database access error occurs.
	 */
	public boolean deleteReview(int id) throws SQLException {
	    String deleteQuery = "DELETE FROM Reviews WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	        pstmt.setInt(1, id);                          // set the review ID to delete
	        return pstmt.executeUpdate() > 0;             // return true if deletion succeeded
	    }
	}
	
	/**
	 * This method looks for reviews whose field includes the given keyword.
	 *
	 * @param keyword The keyword used to find related reviews
	 * @param id The id of all reviews returned from search
	 * @return A list of Review objects corresponding to the specified keyword.
	 * @throws SQLException If a database access error occurs.
	 */
	public List<Review> searchReviews(String keyword, int id) {
		List<Review> list = new ArrayList<>();
		String query = "SELECT * FROM Reviews WHERE answer_id = ? AND (LOWER(text) LIKE ? OR LOWER(author) LIKE ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			pstmt.setString(2, "%" + keyword.toLowerCase() + "%");
			pstmt.setString(3, "%" + keyword.toLowerCase() + "%");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				list.add(new Review(
		                rs.getInt("id"),
		                rs.getInt("answer_id"),
		                rs.getString("text"),
		                rs.getInt("reviewer_id")
		            ));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	/**
	 * This method returns all reviews linked to a particular answer ID.
	 *
	 * @param answerID The ID of the answer for which reviews are to be fetched.
	 * @return A list of {@code Review} objects corresponding to the specified answer ID.
	 * @throws SQLException If a database access error occurs.
	 */
	public List<Review> getReviewsForAnswers(int answerID) throws SQLException {
	    List<Review> list = new ArrayList<>();
	    String query = "SELECT * FROM Reviews WHERE answer_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, answerID);                  // set the answer ID to filter answers
	        ResultSet rs = pstmt.executeQuery();           // execute the query
	        while (rs.next()) {
	            list.add(new Review(
	                rs.getInt("id"),
	                rs.getInt("answer_id"),
	                rs.getString("text"),
	                rs.getInt("reviewer_id")
	            ));
	        }
	    }
	    return list;  // return the list of answers for the question
	}
    
	// ---------- Reviewer Operations ----------
	
	 /**
     * Inserts a new reviewer into the Reviewers table (only used upon creation of a reviewer).
     *
     * @param reviewer The Reviewer object to be saved.
     * @throws SQLException If an error occurs while accessing the database.
     */
	public void saveReviewer(Reviewer reviewer) throws SQLException {
        String addReviewer = "INSERT INTO Reviewers (name, xp, likes, dislikes, rating, trusted, score) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(addReviewer)) {
            pstmt.setString(1, reviewer.getName());  // Set reviewer name
            pstmt.setString(2, reviewer.getXP()); // set reviewer xp
            pstmt.setInt(3, reviewer.getLikeCount());  // Set like count 
            pstmt.setInt(4, reviewer.getDislikeCount());  // Set dislike count 
            pstmt.setDouble(5, reviewer.getRating());  // Set rating 
            pstmt.setBoolean(6, reviewer.isTrusted());  // Set trust status 
            pstmt.setDouble(7, reviewer.getScore()); // Set Score
            
            pstmt.executeUpdate();
        }
    }
	
	 /**
     * Method to update a reviewer after creation.
     *
     * @param reviewer The Reviewer object to be updated.
     * @throws SQLException If an error occurs while accessing the database.
     */
	public void updateReviewer(Reviewer reviewer) throws SQLException {
        String updateReviewer = "UPDATE Reviewers SET name = ?, xp = ?, likes = ?, dislikes = ?, rating = ?, trusted = ?, score = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateReviewer)) {
            pstmt.setString(1, reviewer.getName());  // update reviewer name
            pstmt.setString(2, reviewer.getXP()); // set reviewer xp
            pstmt.setInt(3, reviewer.getLikeCount());  // update like count 
            pstmt.setInt(4, reviewer.getDislikeCount());  // update dislike count 
            pstmt.setDouble(5, reviewer.getRating());  // update rating 
            pstmt.setBoolean(6, reviewer.isTrusted());  // update trust status 
            pstmt.setDouble(7, reviewer.getScore()); // update score
            pstmt.setInt(8, reviewer.getId());
            
            pstmt.executeUpdate();
        }
    }
	
	/**
	 * This method fetches a review from the database based on its ID.
	 *
	 * @param id Id used to find specified reviewer.
	 * @return A Reviewer object of the specified Id.
	 * @throws SQLException If a database access error occurs.
	 */
	public Reviewer getReviewerById(int id) throws SQLException {
	    String query = "SELECT * FROM Reviewers WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);                            // set the ID to search for
	        ResultSet rs = pstmt.executeQuery();            // execute the query
	        if (rs.next()) {
	            return new Reviewer (
	                rs.getInt("id"), // gets reviewer id
	                rs.getString("name"), // gets reviewer name
	                rs.getString("xp"),
	                rs.getInt("likes"), // gets reviewer likes
	                rs.getInt("dislikes"), // gets reviewers dislikes
	                rs.getDouble("rating"), // rating
	                rs.getBoolean("trusted"), // trusted status
	                rs.getDouble("score") // score
	            );
	        }
	    }
	    return null;  // return null if no reviewer is found
	}
	

	/**
	 * This method fetches a retrieves a list of reviews based on answer ID and reviewer trust 
	 * 
	 * This method uses a joined reviewer and reviews table to return the specific list of reviews based on Reviewer metrics
	 *
	 * @param answer_id Id used to find specified reviews for the answer
	 * @return A list of reviews for the answer but only with trusted reviewers in order
	 * @throws SQLException If a database access error occurs.
	 */
	public List<Review> getTrustedReviewList(int answer_id) throws SQLException {
		List<Review> trustedReviews = new ArrayList<>();
	    String query = "SELECT review.id AS review_id, review.text AS review_text, review.answer_id, " 
	    		+ "reviewer.id AS reviewer_id, reviewer.name AS reviewer_name, reviewer.rating AS reviewer_rating "
	    		+ "FROM Reviews review "
	    		+ "JOIN Reviewers reviewer ON review.reviewer_id = reviewer.id "  // Join the reviews and reviewer table by connected reviewerID
	    		+ "WHERE review.answer_id = ? AND reviewer.trusted = true "     // Only display for certain answer and if trusted
	    		+ "ORDER BY reviewer.rating DESC"; 								  // Order by trust level
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, answer_id);                            // set the ID to search for
	        ResultSet rs = pstmt.executeQuery();            // execute the query
	        while (rs.next()) {
	           Review review = new Review(			// create a review object
	        		   rs.getInt("review_id"),
	        		   rs.getInt("answer_id"), 
	        		   rs.getString("review_text"),
	        		   rs.getInt("reviewer_id")
	        		   );
	           trustedReviews.add(review); 			// put the object in a trusted list
	        }
	    }
	    return trustedReviews;  // return the list of trusted reviews
	}
	
	/**
	 *
	 * @param username the posters username
	 * @return the reviewer's ID
	 * @throws SQLException
	 */
	public int getReviewerIDByUsername(String username) throws SQLException {
	    String query = "SELECT id FROM Reviewers WHERE name = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);                            // set the ID to search for
	        ResultSet rs = pstmt.executeQuery();            // execute the query
	        if (rs.next()) {
	                return rs.getInt("id"); // returns id
	        }
	    }
	    return -1; 							// if no reviewer found return -1
	}
	
	/**
	 * Returns reviewer with matching reviewID
	 * @param reviewerId
	 * @return
	 * @throws SQLException
	 */
	public List<Review> getReviewsByReviewerId(int reviewerId) throws SQLException {
	    List<Review> reviews = new ArrayList<>();
	    String query = "SELECT * FROM Reviews WHERE reviewer_id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, reviewerId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Review review = new Review(
	                rs.getInt("id"),
	                rs.getInt("answer_id"),
	                rs.getString("text"),
	                rs.getInt("reviewer_id")
	            );
	            reviews.add(review);
	        }
	    }
	    return reviews;
	}

	/**
	 * Returns list of all registered reviewers
	 * @return
	 * @throws SQLException
	 */
	public List<Reviewer> getAllReviewers() throws SQLException {
	    List<Reviewer> reviewers = new ArrayList<>();
	    String query = "SELECT * FROM Reviewers";
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            Reviewer reviewer = new Reviewer(
	                rs.getInt("id"),
	                rs.getString("name"),
	                rs.getString("xp"),
	                rs.getInt("likes"),
	                rs.getInt("dislikes"),
	                rs.getDouble("rating"),
	                rs.getBoolean("trusted"),
	                rs.getDouble("score")
	            );
	            reviewers.add(reviewer);
	        }
	    }
	    return reviewers;
	}
	
	/**
	 * Return list of reviewers
	 * 
	 * @return String of all reviewers
	 */
	public String listReviewers(String role) {
        StringBuilder userData = new StringBuilder();
        String listUsers = "SELECT userName, role FROM cse360users WHERE role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(listUsers)) {
            pstmt.setString(1, role); 
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userData.append("Reviewer - ").append(rs.getString("userName")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to fetch users."; // Return error message if something goes wrong
        }
        return userData.toString();
    }

	
    // ---------- Role Request Operations ----------
	
	/**
	 * This method returns all the existing role requests in the database.
	 * 
	 * @return					Returns a list of all the usernames of users that are requesting a role.
	 * @throws SQLException		upon failure to access database
	 */
    public List<String> getAllRequests() throws SQLException {
    	String query = "SELECT username FROM Requests";
    	PreparedStatement pstmt = connection.prepareStatement(query);
    	ResultSet rs = pstmt.executeQuery();
    	List<String> list = new ArrayList<>();
    	
    	while (rs.next()) {
    		list.add(rs.getString("username"));
    	}
    	return list;
    }
    
    /**
     * This method returns a particular role request by 
     * searching the database for the given username.
     * 
     * @param userName			String of the username to search the database for.
     * @return					Returns a String of the role the user is requesting.
     */
    public String getRoleRequest(String userName) {
    	String query = "SELECT request FROM Requests WHERE username = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
    		pstmt.setString(1, userName);
    		ResultSet rs = pstmt.executeQuery();
    		
    		if (rs.next()) {
    			return rs.getString("request");
    		}
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return null; // If no such request exists or an error occurs
    }
    
    /**
     * This method adds a user's request to the database, taking their username and the
     * role that they are requesting and inserting it into the Requests table.
     * 
     * @param userName			String username of the user making a request.
     * @param requestedRole		String of the role that the user is requesting.
     * @return					Returns true if the update was successful.
     */
    public boolean addRoleRequest(String userName, String requestedRole) {
    	String requestRole = "INSERT INTO Requests (username, request) VALUES (?, ?)";
    	try (PreparedStatement pstmt = connection.prepareStatement(requestRole)) {
    		String existingRequest = getRoleRequest(userName);
    		
    		// Check that params are valid before proceeding
    		if (userName == null || userName.isEmpty()) {
    			return false;
    		}
    		
    		if (requestedRole == null || requestedRole.isEmpty()) {
    			return false;
    		}
    		// Check if user already has an existing request; if not, proceed
    		if (existingRequest == null || existingRequest.isEmpty()) {
    			pstmt.setString(1, userName);
        		pstmt.setString(2, requestedRole);
        		return pstmt.executeUpdate() > 0;
    		}
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return false;
    }
    
    /**
     * This method deletes a role request for the database by searching the database
     * for the username and deleting the row from the table.
     * 
     * @param userName		String username of the user to delete from the table.
     * @return				Returns true if the update was successful.
     */
    public boolean deleteRoleRequest(String userName) {
    	String deleteRole = "DELETE FROM Requests WHERE username = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(deleteRole)) {
    		String existingRequest = getRoleRequest(userName);
    		
    		// Check for valid params before executing
    		if (userName == null || userName.isEmpty()) {
    			return false;
    		}
    		
    		if (!(existingRequest == null) && !existingRequest.isEmpty()) {
    			pstmt.setString(1, userName);
    			return pstmt.executeUpdate() > 0;
    		}
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return false;
    }
    
    // ---------- Private Message Operations ----------
    
	//pulls all conversation the user is involved with
	public List<Conversations> getConversationsForUser(String userId) throws SQLException {
	    List<Conversations> conversations = new ArrayList<>();
	    String sql = "SELECT * FROM Converstations WHERE participent_1_id = ? OR participen_2_id = ?";
	    PreparedStatement ps = connection.prepareStatement(sql);
	    ps.setString(1, userId);
	    ps.setString(2, userId);
	    ResultSet rs = ps.executeQuery();

	    while (rs.next()) {
	        int id = rs.getInt("id");
	        String p1 = rs.getString("participent_1_id");
	        String p2 = rs.getString("participen_2_id");
	        conversations.add(new Conversations(id, p1, p2));
	    }

	    return conversations;
	}
	//starts a new conversation between 2 existing users
	public boolean createConversation(String user1, String user2) throws SQLException {
	    String checkSql = "SELECT * FROM Converstations WHERE " +
	            "(participent_1_id = ? AND participen_2_id = ?) OR (participent_1_id = ? AND participen_2_id = ?)";
	    PreparedStatement checkStmt = connection.prepareStatement(checkSql);
	    checkStmt.setString(1, user1);
	    checkStmt.setString(2, user2);
	    checkStmt.setString(3, user2);
	    checkStmt.setString(4, user1);

	    ResultSet rs = checkStmt.executeQuery();
	    if (rs.next()) {
	        return false; // Already exists
	    }

	    String insertSql = "INSERT INTO Converstations (participent_1_id, participen_2_id) VALUES (?, ?)";
	    PreparedStatement insertStmt = connection.prepareStatement(insertSql);
	    insertStmt.setString(1, user1);
	    insertStmt.setString(2, user2);
	    insertStmt.executeUpdate();
	    return true;
	}
	//used to as display in conversation
	public Message getMostRecentMessage(int conversationId) throws SQLException {
	    String sql = "SELECT * FROM Messages WHERE converstaion_id = ? ORDER BY id DESC LIMIT 1";
	    PreparedStatement ps = connection.prepareStatement(sql);
	    ps.setInt(1, conversationId);
	    ResultSet rs = ps.executeQuery();

	    if (rs.next()) {
	        int id = rs.getInt("id");
	        String sender = rs.getString("sender_id");
	        String text = rs.getString("text");
	        return new Message(id, conversationId, sender, text);
	    }
	    return null;
	}
	//pushes conversation to database
	public void insertMessage(int conversationId, String senderId, String text) throws SQLException {
	    String sql = "INSERT INTO Messages (converstaion_id, sender_id, text) VALUES (?, ?, ?)";
	    PreparedStatement ps = connection.prepareStatement(sql);
	    ps.setInt(1, conversationId);
	    ps.setString(2, senderId);
	    ps.setString(3, text);
	    ps.executeUpdate();
	}
	
	// queres all messages that share a conversation id
	public List<Message> getMessagesForConversation(int conversationId) throws SQLException {
	    List<Message> messages = new ArrayList<>();
	    String sql = "SELECT * FROM Messages WHERE converstaion_id = ? ORDER BY id ASC";
	    PreparedStatement ps = connection.prepareStatement(sql);
	    ps.setInt(1, conversationId);
	    ResultSet rs = ps.executeQuery();

	    while (rs.next()) {
	        int id = rs.getInt("id");
	        String sender = rs.getString("sender_id");
	        String text = rs.getString("text");
	        messages.add(new Message(id, conversationId, sender, text));
	    }

	    return messages;
	}
	// gets all users but the passed user name, usered in conversations to give the list of people with whom you can start a conversation
	public List<User> getAllUsersExcept(String currentUserName) throws SQLException {
	    List<User> users = new ArrayList<>();
	    String sql = "SELECT userName, password, role FROM cse360users WHERE userName != ?";
	    PreparedStatement ps = connection.prepareStatement(sql);
	    ps.setString(1, currentUserName);
	    ResultSet rs = ps.executeQuery();

	    while (rs.next()) {
	        String username = rs.getString("userName");
	        String password = rs.getString("password");
	        String role = rs.getString("role");
	        users.add(new User(username, password, role));
	    }

	    return users;
	}
	/**
	 * Returns a comma-separated string of roles assigned to a user.
	 *
	 * @param username the username to look up
	 * @return roles as a string
	 */
	public String getRolesForUser(String username) {
	    String roles = "";
	    try {
	        connectToDatabase();
	        String query = "SELECT role FROM cse360users WHERE userName = ?";
	        PreparedStatement pstmt = connection.prepareStatement(query);
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            roles = rs.getString("role");
	        }
	        rs.close();
	        pstmt.close();
	        closeConnection();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return roles;
	}

	/**
	 * Gets the account creation date of a user.
	 *
	 * @param username the user to look up
	 * @return the account creation date as a string
	 */
	public String getUserCreationDate(String username) {
	    String date = "";
	    try {
	        connectToDatabase();
	        String query = "SELECT creation_date FROM cse360users WHERE userName = ?";
	        PreparedStatement pstmt = connection.prepareStatement(query);
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	Timestamp timestamp = rs.getTimestamp("creation_date");
	        	//LocalDateTime ldt = timestamp.toLocalDateTime();
	        	//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	        	//date = ldt.format(formatter); // 2025-04-22 21:48

	        }
	        rs.close();
	        pstmt.close();
	        closeConnection();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return date;
	}

	/**
	 * Returns a list of questions asked by the given user.
	 *
	 * @param username the user whose questions to retrieve
	 * @return a list of Question objects
	 */
	public List<Question> getQuestionsByUser(String username) {
	    List<Question> questions = new ArrayList<>();
	    try {
	        connectToDatabase();
	        String sql = "SELECT * FROM Questions WHERE author = ?";
	        PreparedStatement pstmt = connection.prepareStatement(sql);
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String text = rs.getString("text");
	            boolean resolved = rs.getBoolean("isResolved");
	            boolean flagged = rs.getBoolean("flagged");  // added new for flagging
	            questions.add(new Question(id, text, username, resolved));
	        }

	        rs.close();
	        pstmt.close();
	        closeConnection();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return questions;
	}
	
	/**
	 * Retrieves a list of all flagged questions from the database.
	 * A flagged question is one that has been marked by staff for review due to inappropriate or suspicious content.
	 *
	 * @return a list of {@link Question} objects that are flagged for review
	 * @throws SQLException if a database access error occurs
	 */	
	public List<Question> getFlaggedQuestions() throws SQLException {
	    connectToDatabase();
	    List<Question> list = new ArrayList<>();
	    String sql = "SELECT * FROM Questions WHERE flagged = true";
	    PreparedStatement pstmt = connection.prepareStatement(sql);
	    ResultSet rs = pstmt.executeQuery();

	    while (rs.next()) {
	    	list.add(new Question(
	    		    rs.getInt("id"),
	    		    rs.getString("text"),
	    		    rs.getString("author"),
	    		    rs.getBoolean("isResolved")
	    		));
	    }

	    rs.close();
	    pstmt.close();
	    closeConnection();
	    return list;
	}
	
	/**
	 * Sets the flagged status of a specific question in the database.
	 * This is typically used by staff to mark a question for review due to
	 * inappropriate or suspicious content.
	 *
	 * @param id the unique ID of the question to update
	 * @param flagged true to mark the question as flagged, false to unflag it
	 * @return true if the update was successful (i.e., one or more rows were affected), false otherwise
	 * @throws SQLException if a database access error occurs
	 */
	public boolean setQuestionFlagged(int id, boolean flagged) throws SQLException {
		connectToDatabase();
		String query = "UPDATE Questions SET flagged = ? WHERE id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setBoolean(1, true);
		pstmt.setInt(2, id);  // Make sure this matches an existing row
		int rowsAffected = pstmt.executeUpdate();
		 pstmt.close();           // clean up
		    closeConnection();       // optional: close right away
		    
		return rowsAffected > 0;

	}
    
    // ---------------------- Score Card Operations ----------------------
	
	/**
	 * Will calculate the reviewers score and update all reviewers in database
	 * based on instructors input.
	 * 
	 * @param useLikesDislikes
	 * @param useNumReviews
	 * @throws SQLException
	 */
	public void calculateAndUpdateScores(boolean useLikesDislikes, boolean useNumReviews) throws SQLException {
	    List<Reviewer> reviewers = getAllReviewers(); 

	    for (Reviewer reviewer : reviewers) {
	        // Calculate score based on current parameters
	        double score = calculateReviewerScore(reviewer.getId(), useLikesDislikes, useNumReviews);

	        // Determine trust based on score
	        boolean isTrusted = score >= 70;
	        reviewer.setTrust(isTrusted);
	        reviewer.setScore(score);

	        // Update the database with the new trust status and score
	        updateReviewer(reviewer);
	        
	        System.out.println("-- In Loop --\nScore: " + reviewer.getScore() + " | Trust: " + reviewer.isTrusted());
	    }
	}

	/**
	 * Calculates reviewers score
	 * 
	 * @param reviewerId
	 * @param useReviews
	 * @param useLikes
	 * @return
	 * @throws SQLException 
	 */
	public double calculateReviewerScore(int reviewerId, boolean useReviews, boolean useLikes) throws SQLException {
		
		// if neither are true, return 0
		if (!useReviews && !useLikes) {
			return 0;
		}
		
		double score = 0;
		double likeRatio = 0.0;
		double reviewRatio = 0.0;
		
		// The weight for calc at end
		double likeWeight = 0.6;
		double reviewWeight = 0.4;
		
		// get reviewer likes/dislikes and numOfReviews
		Reviewer r = null;
		try {
			r = getReviewerById(reviewerId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		int numLikes = r.getLikeCount();
		int numDislikes = r.getDislikeCount();
		int numReviews = r.getNumOfReviews();
		
		// Calculate
		if (useLikes) {
		    if (numLikes + numDislikes > 0) {
		        likeRatio = (double) numLikes / (numLikes + numDislikes);
		    }
		} else {
			likeWeight = 0;
			reviewWeight = 1;
		}
		
		if (useReviews) {
			System.out.println("Num reviews in class: " + numReviews);
			if (numReviews == 0) {
				reviewRatio = 0;
			} else {
				reviewRatio = (1-((1/numReviews)*0.4))*0.4;
			}
		} else {
			reviewWeight = 0;
			likeWeight = 1;
		}
		
		score = (likeRatio * likeWeight) + (reviewRatio * reviewWeight);
		return (int) Math.round(score * 100);
	}
	
	public void setNumReviews(Reviewer r) {
		
	}
	
    // ------------------------------------------------
    
	/**
	 * Closes the database connection and statement.
	 */
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}



}
