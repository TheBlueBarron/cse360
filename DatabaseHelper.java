package databasePart1;

import application.User; 
import application.Question; 
import application.Answer; 
import java.sql.*; 
import java.util.ArrayList; 
import java.util.List; 
import java.util.UUID;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(20))";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    // New for HW2 (Questions and Answers table)
	    //Questions table
	    String questionsTable = "CREATE TABLE IF NOT EXISTS Questions ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "text VARCHAR(1024), "
	            + "author VARCHAR(255), "
	            + "isResolved BOOLEAN)"; 
	    statement.execute(questionsTable);
	    
	    //Answers table
	    String answersTable = "CREATE TABLE IF NOT EXISTS Answers ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "question_id INT, "
	            + "text VARCHAR(1024), "
	            + "author VARCHAR(255), "
	            + "resolved BOOLEAN, " 
	            + "FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE)";
	    statement.execute(answersTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
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
	
	// Retrieves the role of a user from the database using their UserName.
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
	
	// Generates a new invitation code and inserts it into the database.
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
	
	// Validates an invitation code to check if it is unused.
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
	
	// Marks the invitation code as used in the database.
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

	// Create: Add a new question
	// This method inserts a new question into the database.
	// It uses a prepared statement to set the question's text and author,
	// then it retrieves the auto-generated ID and updates the question object.
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

	// Read: Get a question by its ID
	// This method fetches a question from the database based on its ID.
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

	// Read: Get all questions
	// This method retrieves all questions from the database and returns them as a list.
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

	// Update: Update a question's text
	// This method updates the text of a question identified by its ID,
	// but only if the new text is valid.
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
	
	//This method updates the questions resolved flag
	public void updateIsResolvedQuestion(int id, boolean isResolved) throws SQLException {
		String updateQuery = "UPDATE Questions SET isResolved = ? WHERE id = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
			pstmt.setBoolean(1, isResolved);			   // update resolved flag
	        pstmt.setInt(2, id);                           // specify which question to update
	        pstmt.executeUpdate();            
		}
	}

	// Delete: Remove a question
	// This method deletes a question from the database using its ID.
	public boolean deleteQuestion(int id) throws SQLException {
	    String deleteQuery = "DELETE FROM Questions WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	        pstmt.setInt(1, id);                           // set the ID of the question to delete
	        return pstmt.executeUpdate() > 0;              // return true if deletion was successful
	    }
	}

	// Search: Find questions containing a keyword (case-insensitive)
	// This method looks for questions whose text OR author field includes the given keyword.
	// **** EDIT: updated to search author field as well
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

	// ---------------- Answer Operations ----------------

	// Create: Add a new answer
	// This method inserts a new answer into the Answers table,
	// then retrieves the auto-generated ID and sets it in the answer object.
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

	// Read: Get an answer by its ID
	// This method retrieves a specific answer from the database using its ID.
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

	// Read: Get all answers
	// This method retrieves all answers from the database and returns them as a list.
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

	// Read: Get all answers for a specific question
	// This method returns all answers linked to a particular question ID.
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

	// Update: Update an answer's text
	// This method updates the text of an answer if the new text is valid.
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
	
	//This method updates the answers resolved flag
	public void updateAnswerResolved(int id, boolean resolved) throws SQLException {
		String updateQuery = "UPDATE Answers SET resolved = ? WHERE id = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
			pstmt.setBoolean(1, resolved);			   	   // update resolved flag *
	        pstmt.setInt(2, id);                           // specify which question to update
	        pstmt.executeUpdate();            
		}
	}

	// Delete: Remove an answer
	// This method deletes an answer from the database based on its ID.
	public boolean deleteAnswer(int id) throws SQLException {
	    String deleteQuery = "DELETE FROM Answers WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	        pstmt.setInt(1, id);                          // set the answer ID to delete
	        return pstmt.executeUpdate() > 0;             // return true if deletion succeeded
	    }
	}
	
	// Search: Find answers containing a keyword (case-insensitive) **** EDIT
	// This method looks for answers whose field includes the given keyword.
	public List<Answer> searchAnswers(String keyword, int id) {
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
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// ----------------- Other User Opeations -------------------
	
	// Set a one-time password for a user // Radwan edit begins!!------------------------------------------
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
    
    // Modify a user's role
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
    } // Radwan edit ends!! ------------------------------------------------------------------
    
    // Jaari edit ----------------------------------------------------------------------------
    // Adds a role to a user to allow multiple options.
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
    public boolean removeUserRole(String userName, String removedRole) {
    	String removeRole = "UPDATE cse360users SET role = ? WHERE userName = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(removeRole)) {
    		
    		// Checks that the user has the role to remove
    		if (!getUserRole(userName).contains(removedRole)) {
    			return false;
    		}
    		
    		// Checks if the role being removed is their admin role;
    		// if it is, cancel the operation
    		if (removedRole.equals("admin")) {
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
    
    // end of Jaari edit ----------------------------------------------------------------



	// Closes the database connection and statement.
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
