package databasePart1;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestGetAllRequests {
	private static DatabaseHelper dbHelper = new DatabaseHelper();
	
	@BeforeAll
	static void setUpBeforeClass() {
		try {
			dbHelper.connectToDatabase();
			dbHelper.addRoleRequest("requester1", "reviewer");
			dbHelper.addRoleRequest("requester2", "reviewer");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@AfterAll
	static void tearDownAfterClass() {
		dbHelper.deleteRoleRequest("requester1");
		dbHelper.deleteRoleRequest("requester2");
	}

	@Test
	void getAllTest1() {
		try {
			if(dbHelper.getAllRequests() == null) {
				fail("**** ERROR **** No results returned, but table is not empty");
			}
		} catch (SQLException e) {
			fail("**** ERROR **** " + e.getMessage());
		}
	}
}
