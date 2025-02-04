package databasePart1;

public class AdminTestingAutomation {
    
    static int numPassed = 0;
    static int numFailed = 0;

    public static void main(String[] args) {
        DatabaseHelper databasehelper = new DatabaseHelper();
        try {
            databasehelper.connectToDatabase();
            
            // Test generating an invitation code
            testGenerateInvitationCode(databasehelper);
            
            // Test setting a one-time password
            testSetOneTimePassword(databasehelper, "Radwan1", "Password123", true);
            
            // Test modifying a user's role
            testModifyUserRole(databasehelper, "Radwan1", "admin", true);
            //testModifyUserRole(databasehelper, "Radwan2", "user", true);
            
            // Test deleting a user
            testDeleteUser(databasehelper, "Radwan3", true);
            
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
        
    
    private static void testGenerateInvitationCode(DatabaseHelper db) {
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
        boolean result = db.setOneTimePassword(username, newPassword);
        if (result == expectedSuccess) {
            System.out.println("PASS: One-time password set successfully for " + username);
            numPassed++;
        } else {
            System.out.println("FAIL: Failed to set one-time password for " + username);
            numFailed++;
        }
    }

    private static void testModifyUserRole(DatabaseHelper db, String username, String newRole, boolean expectedSuccess) {
        boolean result = db.manageUserRole(username, newRole);
        if (result == expectedSuccess) {
            System.out.println("PASS: User role modified successfully for " + username);
            numPassed++;
        } else {
            System.out.println("FAIL: User role modification failed for " + username);
            numFailed++;
        }
    }

    private static void testDeleteUser(DatabaseHelper db, String username, boolean expectedSuccess) {
        boolean result = db.deleteUser(username);
        if (result == expectedSuccess) {
            System.out.println("PASS: User deleted successfully.");
            numPassed++;
        } else {
            System.out.println("FAIL: User deletion failed.");
            numFailed++;
        }
    }

    private static void testListUsers(DatabaseHelper db) {
        String users = db.listUsers();
        if (!users.isEmpty()) {
            System.out.println("PASS: Users listed successfully.\n" + users);
            numPassed++;
        } else {
            System.out.println("FAIL: Failed to list users.");
            numFailed++;
        }
    }
}
