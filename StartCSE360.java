package application;

import databasePart1.DatabaseHelper; 
import javafx.application.Application; 
import javafx.stage.Stage; 
import java.sql.SQLException;

public class StartCSE360 extends Application {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	
	public static void main( String[] args )
	{
		 launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
//        try {
//            databaseHelper.connectToDatabase(); // Connect to the database
//            if (databaseHelper.isDatabaseEmpty()) {
//            	
//            	new FirstPage(databaseHelper).show(primaryStage);
//            } else {
//            	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
//                
//            }
//        } catch (SQLException e) {
//        	System.out.println(e.getMessage());
//        }
		 try {
		        databaseHelper.connectToDatabase(); // Connect to the database
		        if (databaseHelper.isDatabaseEmpty()) {
		        // Displaying the Discussion page instead of FirstPage for HW2
		        new DiscussionPage(databaseHelper).show(primaryStage);
		        } else {
		        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	                
	            }
		    } catch (SQLException e) {
		        System.out.println(e.getMessage());
		    }
    }
	

}
