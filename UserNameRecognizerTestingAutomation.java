package application;

public class UserNameRecognizerTestingAutomation {

	static int numPassed = 0;	// Counter of number of passed tests
	static int numFailed = 0;	// Counter of number of failed tests
	
	public static void main(String[] args) {
		/************** Test cases semi-automation report header **************/
		System.out.println("______________________________________");
		System.out.println("\nTesting Automation");
		
		// Test cases to ensure invalidity
		performTestCase(1, "1username", false);
			
		performTestCase(2, ".username", false);
			
		performTestCase(3, "_username", false);
			
		performTestCase(4, "-username", false);
			
		performTestCase(5, "user..name", false);
			
		performTestCase(6, "user__name", false);
			
		performTestCase(7, "user--name", false);
		
		performTestCase(8, "usernameUserName1234", false);
		
		performTestCase(9, "username?", false);
		
		// Test cases to ensure validity	
		performTestCase(10, "username", true);
		
		performTestCase(11, "user.name", true);
			
		performTestCase(12, "user_name", true);
		
		performTestCase(13, "user-name", true);
		
		performTestCase(14, "username12", true);
		
		performTestCase(15, "UserName34", true);
		
		/************** End of the test cases **************/
		
		/************** Test cases semi-automation report footer **************/
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number passed: " + numPassed);
		System.out.println("Number failed: " + numFailed);
		
	}
	/* This method sets up the input value for the tests from the parameters,
	*  invokes the corresponding function from the UserNameRecognizer class,
	*  displays the pass/fail of the test with the given boolean parameter,
	*  and increments the total passed/failed
	*/
	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {
		// Display individual test case headers
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nEvaluation start:");
		
		// Call the recognizer to process the input
		
		String result = UserNameRecognizer.checkForValidUserName(inputText);
		System.out.println();
		
		// Begin evaluation of test cases
		
		// If an error was thrown:
		if (result.length() > 0) {
			System.out.println("The given input <" + inputText + "> was evaluated as invalid.");
			System.out.println("The error sent:" + result); // Display error to ensure correct error was thrown
			if (!expectedPass) {
				System.out.println("PASSED: expected invalidity");
				numPassed++;
			} else {
				System.out.println("FAILED: expected validity");
				numFailed++;
			}
		} else {
			System.out.println("The given input <" + inputText + "> was evaluated as valid.");
			if (expectedPass) {
				System.out.println("PASSED: expected validity");
				numPassed++;
			} else {
				System.out.println("FAILED: expected invalidity");
				numFailed++;
			}
		}
	}
	
	

}
