package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import application.User;


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
				+ "role VARCHAR(50))"; // Edited this from 20
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
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
	
	// Jaari edit -------------------------------------------------------------
	// Retrieves the password of a user from the database using their UserName.
	public String getUserPassword(String userName) {
		String query = "SELECT password FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getString("password");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	} // end of edit ----------------------------------------------------------
	
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
    
    // Delete a user from the database
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

    // List all users in the database
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
    
    // end of Jaari edit --------------------------------------------------------------------
    
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
