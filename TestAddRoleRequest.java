package databasePart1;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestAddRoleRequest {
	private static DatabaseHelper databaseHelper = new DatabaseHelper();
	
	
	@BeforeAll
	static void setUp() {
		try { 
			databaseHelper.connectToDatabase(); 
			databaseHelper.deleteRoleRequest("requester");
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
	@Test
	void addTest1() {
		if (!databaseHelper.addRoleRequest("requester", "reviewer")) {
			fail("**** ERROR **** Valid request was not added");
		}
	}
	
	@Test
	void addTest2() {
		if (databaseHelper.addRoleRequest(null, null)) {
			fail("**** ERROR **** Invalid request was added");
		}
	}
	
	@Test
	void addTest3() {
		if (databaseHelper.addRoleRequest("", "reviewer")) {
			fail("**** ERROR **** Invalid request was added");
		}
	}
	
	@Test
	void addTest4() {
		if (databaseHelper.addRoleRequest("requester", "")) {
			fail("**** ERROR **** Invalid request was added");
		}
	}
	
	@Test
	void addTest5() {
		if (databaseHelper.addRoleRequest(null, "reviewer")) {
			fail("**** ERROR **** Invalid request was added");
		}
	}
	
	@Test
	void addTest6() {
		if (databaseHelper.addRoleRequest("requester", null)) {
			fail("**** ERROR **** Invalid request was added");
		}
	}
}
