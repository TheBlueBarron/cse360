package databasePart1;

import application.*;

public class AdminTestingAutomation {
    
    static int numPassed = 0;
    static int numFailed = 0;

    public static void main(String[] args) {
        DatabaseHelper databasehelper = new DatabaseHelper();
        try {
            databasehelper.connectToDatabase();
            
            // Create a new user dedicated to testing; will be removed
            User tester = new User("tester", "Pass123!", "user");
            databasehelper.register(tester);
            
            // Test generating an invitation code
            testGenerateInvitationCode(databasehelper);
            
            // Test setting a one-time password
            testSetOneTimePassword(databasehelper, "tester", "Password123", true);
            
            // Test modifying a user's role
            testModifyUserRole(databasehelper, "tester", "staff", true);
            // System.out.println(databasehelper.getUserRole("tester"));
            
            // Test adding a role to a user's role
            testAddUserRole(databasehelper, "tester", "staff", false);
            testAddUserRole(databasehelper, "tester", "instructor", true);
            // System.out.println(databasehelper.getUserRole("tester"));
            
            // Test removing a role from a user
            testRemoveUserRole(databasehelper, "tester", "staff", true);
            testRemoveUserRole(databasehelper, "tester", "instructor", true);
            
            testRemoveUserRole(databasehelper, "admin", "admin", false);
            // System.out.println(databasehelper.getUserRole("tester"));
            
            // Test deleting a user
            testDeleteUser(databasehelper, "tester", true);
            
            // Test listing all users
            testListUsers(databasehelper);
            
            // Summary of test results
            System.out.println("\nTotal Tests Passed: " + numPassed);
            System.out.println("Total Tests Failed: " + numFailed);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            databasehelper.closeConnection();
        }
    }
        
    // Methods created by Radwan
    private static void testGenerateInvitationCode(DatabaseHelper db) {
    	// Testing header
    	System.out.println("___________________________________________________________________");
    	String code = db.generateInvitationCode();
        if (code != null && !code.isEmpty() && code.length() == 4) {
            System.out.println("PASS: Generated invitation code successfully. Code: " + code);
            numPassed++;
            // ToDo later! add a checker to verify the code is stored in the db for enhancement
        } else {
            System.out.println("FAIL: Failed to generate a valid invitation code.");
            numFailed++;
        }
    }
    
    private static void testSetOneTimePassword(DatabaseHelper db, String username, String newPassword, boolean expectedSuccess) {
    	// Testing header
    	System.out.println("___________________________________________________________________");
    	boolean result = db.setOneTimePassword(username, newPassword);
        if (result == expectedSuccess) {
            System.out.println("PASS: One-time password set successfully for <" + username + ">");
            numPassed++;
        } else {
            System.out.println("FAIL: Failed to set one-time password for <" + username + ">");
            numFailed++;
        }
    }

    private static void testModifyUserRole(DatabaseHelper db, String username, String newRole, boolean expectedSuccess) {
    	// Testing header
    	System.out.println("___________________________________________________________________");
    	boolean result = db.manageUserRole(username, newRole);
        if (result == expectedSuccess) {
            System.out.println("PASS: User role modified successfully for <" + username + ">");
            numPassed++;
        } else {
            System.out.println("FAIL: User role modification failed for <" + username + ">");
            numFailed++;
        }
    }

    private static void testDeleteUser(DatabaseHelper db, String username, boolean expectedSuccess) {
    	// Testing header
    	System.out.println("___________________________________________________________________");
    	boolean result = db.deleteUser(username);
        if (result == expectedSuccess) {
            System.out.println("PASS: User <" + username + "> deleted successfully.");
            numPassed++;
        } else {
            System.out.println("FAIL: User deletion failed.");
            numFailed++;
        }
    }

    private static void testListUsers(DatabaseHelper db) {
    	// Testing header
    	System.out.println("___________________________________________________________________");
    	String users = db.listUsers();
        if (!users.isEmpty()) {
            System.out.println("PASS: Users listed successfully.\n" + users);
            numPassed++;
        } else {
            System.out.println("FAIL: Failed to list users.");
            numFailed++;
        }
    }
    
    // Methods created by Jaari
    private static void testAddUserRole(DatabaseHelper db, String username, String role, boolean expectedSuccess) {
    	// Testing header
    	System.out.println("___________________________________________________________________");
    	boolean result = db.addUserRole(username, role);
    	if (expectedSuccess) {
    		if (result) {
    			System.out.println("PASS: Role added successfully to <" + username + ">, expected success.");
    			System.out.println("Roles: " + db.getUserRole(username));
    			numPassed++;
    		} else {
    			System.out.println("FAIL: Role was not added to <" + username + ">, expected success.");
    			System.out.println("Roles: " + db.getUserRole(username));
    			numFailed++;
    		}
    	} else {
    		if (result) {
    			System.out.println("FAIL: Role added successfully to <" + username + ">, expected failure.");
    			System.out.println("Roles: " + db.getUserRole(username));
    			numFailed++;
    		} else {
    			System.out.println("PASS: Role was not added to <" + username + ">, expected failure.");
    			System.out.println("Roles: " + db.getUserRole(username));
    			numPassed++;
    		}
    	}
    }
    
    private static void testRemoveUserRole(DatabaseHelper db, String username, String role, boolean expectedSuccess) {
    	// Testing header
    	System.out.println("___________________________________________________________________");
    	boolean result = db.removeUserRole(username, role);
    	if (expectedSuccess) {
    		if (result) {
    			System.out.println("PASS: Role removed successfully from <" + username + ">, expected success.");
    			System.out.println("Roles: " + db.getUserRole(username));
    			numPassed++;
    		} else {
    			System.out.println("FAIL: Role was not removed from <" + username + ">, expected success.");
    			System.out.println("Roles: " + db.getUserRole(username));
    			numFailed++;
    		}
    	} else {
    		if (result) {
    			System.out.println("FAIL: Role removed successfully from <" + username + ">, expected failure.");
    			System.out.println("Roles: " + db.getUserRole(username));
    			numFailed++;
    		} else {
    			System.out.println("PASS: Role was not removed from <" + username + ">, expected failure.");
    			System.out.println("Roles: " + db.getUserRole(username));
    			numPassed++;
    		}
    	}
    }
    
}
