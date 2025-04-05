package databasePart1;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestDeleteRoleRequest {
	private static DatabaseHelper dbHelper = new DatabaseHelper();
	
	@BeforeAll
	static void setUp() {
		try {
			dbHelper.connectToDatabase();
			dbHelper.addRoleRequest("requester", "reviewer");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void deleteTest1() {
		if(!dbHelper.deleteRoleRequest("requester")) {
			fail("**** ERROR **** Valid deletion not processed");
		}
	}
	
	@Test
	void deleteTest2() {
		if(dbHelper.deleteRoleRequest("")) {
			fail("**** ERROR **** Invalid deletion processed");
		}
	}

	@Test
	void deleteTest3() {
		if(dbHelper.deleteRoleRequest(null)) {
			fail("**** ERROR **** Invalid deletion processed");
		}
	}
}
