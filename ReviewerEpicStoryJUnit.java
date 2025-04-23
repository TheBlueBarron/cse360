package databasePart1;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestReviewerQueries {
	private static DatabaseHelper databaseHelper = new DatabaseHelper();

	@BeforeAll
	static void setUp() {
		try {
			databaseHelper.connectToDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Test
	void test1() {
		try {
			databaseHelper.getReviewsByReviewerId(1);
		} catch (SQLException e) {
			fail("**** ERROR **** Method threw SQLException on valid ID");
		}
	}

	@Test
	void test2() {
		try {
			databaseHelper.getReviewsByReviewerId(-1);
		} catch (SQLException e) {
			fail("**** ERROR **** Method threw SQLException on invalid ID");
		}
	}
}
