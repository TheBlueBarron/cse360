package databasePart1;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestGetRoleRequest {
	private static DatabaseHelper dbHelper = new DatabaseHelper();
	
	@BeforeAll
	static void setUp() {
		try {
			dbHelper.connectToDatabase();
			dbHelper.addRoleRequest("requester1", "reviewer");
			dbHelper.addRoleRequest("requester2", "reviewer");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getTest1() {
		if (dbHelper.getRoleRequest("requester1") == null) {
			fail("**** ERROR **** Valid fetch didn't retrieve any results");
		}
	}
	
	@Test
	void getTest2() {
		if (dbHelper.getRoleRequest("requester2") == null) {
			fail("**** ERROR **** Valid fetch didn't retrieve any results");
		}
	}

	@Test
	void getTest3() {
		if (!(dbHelper.getRoleRequest("") == null)) {
			fail("**** ERROR **** Invalid fetch retrieved some result");
		}
	}
	
	@Test
	void getTest4() {
		if (!(dbHelper.getRoleRequest(null) == null)) {
			fail("**** ERROR **** Invalid fetch retrieved some result");
		}
	}
	
	@AfterAll
	static void post() {
		dbHelper.deleteRoleRequest("requester1");
		dbHelper.deleteRoleRequest("requester2");
	}
}
