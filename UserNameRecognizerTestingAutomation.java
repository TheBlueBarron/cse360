package userNameRecognizerTestbed;

public class UserNameRecognizerTestingAutomation {

	static int numPassed = 0;	// Counter of number of passed tests
	static int numFailed = 0;	// Counter of number of failed tests
	
	public static void main(String[] args) {
		// TODO Insert test cases
		performTestCase(1, "1username", false);
			
		performTestCase(2, ".username", false);
			
		performTestCase(3, "_username", false);
			
		performTestCase(4, "-username", false);
			
		performTestCase(5, "user..name", false);
			
		performTestCase(6, "user__name", false);
			
		performTestCase(7, "user--name", false);
			
		performTestCase(8, "user.name", true);
			
		performTestCase(9, "username", true);
		
		performTestCase(10, "username12", true);
		
		performTestCase(11, "UserName34", true);
		
		
		
		System.out.println("Number passed: " + numPassed);
		System.out.println("Number failed: " + numFailed);
		
	}
	
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
