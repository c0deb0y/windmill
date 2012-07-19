package com.prezerak.windmill.util;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;

import com.prezerak.windmill.main.WindMill;

public class Utilities {

	public static void printSQLException(SQLException e)
	{
		// Unwraps the entire exception chain to unveil the real cause of the
		// Exception.
		while (e != null)
		{
			WindMill.logger.warn("\n----- SQLException -----" + "\n"+
								"  SQL State:  " + e.getSQLState()+"\n"+
								"  Error Code: " + e.getErrorCode()+"\n"+
								"  Message:    " + e.getMessage());
			// for stack traces, refer to derby.log or uncomment this:
			WindMill.logger.warn(System.err);
			e = e.getNextException();
		}
	}

	public static void reportFailure(String message) {
		WindMill.logger.warn("\nData verification failed:\n\t" + message);
	}


	public static boolean anotherInstanceExists() {
		File f = new File(WindMill.appHome+"windmill.lock"); //create a .lock file instance 
		
		if (f.exists())
			return true; //terminate if .lock exists
		else //else create .lock
			try {
				f.createNewFile();//else create it and
				f.deleteOnExit(); //mark for deletion when the JVM terminates
			} catch (IOException e) {
				WindMill.logger.warn("Problem creating windmill.lock file");
				return true; //if we get an Exception at this point
							 //we behave as if another instance is running
			} 
			return false;			
	}


	public static int convertToBeauforts(float velocity) {
		// TODO Auto-generated method stub
		velocity = velocity/WindMill.KmPerHrToMetersConvFactor; //convert to km/hr
		if (velocity < 2)
			return 0;
		if (velocity < 6)
			return 1;
		if (velocity < 13)
			return 2;
		if (velocity < 21)
			return 3;
		if (velocity < 31)
			return 4;
		if (velocity < 41)
			return 5;
		if (velocity < 51)
			return 6;
		if (velocity < 62)
			return 7;
		if (velocity < 75)
			return 8;
		if (velocity < 90)
			return 9;
		if (velocity < 104)
			return 10;
		if (velocity < 120)
			return 11;

		return 12;
	}

	
	public static String createDateString(long startDate, long endDate) {
		GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance(SimpleTimeZone.getDefault());
		
		StringBuffer dateLine = new StringBuffer(50);
		Formatter formatter = new Formatter(dateLine, Locale.ENGLISH);

				
		cal.setTimeInMillis(startDate);
		
		dateLine.append("From ");
		formatter.format("%1$tH:%1$tM (GMT) %1$tb %1$te,%1$tY", cal);
		
		
		cal.setTimeInMillis(endDate);
		
		dateLine.append(" to ");
		formatter.format("%1$tH:%1$tM (GMT) %1$tb %1$te,%1$tY", cal);
		
		
		return dateLine.toString();
	}
	

	public static boolean databaseMissing() {
		// TODO Auto-generated method stub
		File f = new File(WindMill.databaseHome); 
		
		if (f.exists())
			return false; 	

		return true;
	}

	
}
