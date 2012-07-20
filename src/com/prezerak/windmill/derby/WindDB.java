package com.prezerak.windmill.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;


import com.prezerak.windmill.main.WindMill;
import com.prezerak.windmill.model.Gust;
import com.prezerak.windmill.model.High;
import com.prezerak.windmill.model.Higher;
import com.prezerak.windmill.model.Wind;
import com.prezerak.windmill.model.Anemometer;
import com.prezerak.windmill.util.Utilities;


public class WindDB  {


	final String protocol = "jdbc:derby:";

    
	final String userName="c0deb0y";
	final String passwd = "!@#$%^&*";
	final String bootpasswd="1itt131amb";
	final String dbName = WindMill.DBPATH;


	final private String windTable = "wind_data";
	final private String alarmTable = "alarms";
	private Connection conn=null;


	private PreparedStatement psInsert = null;
	private PreparedStatement psInsertAlarm = null;
	private PreparedStatement psSelectAlarm = null;
	private PreparedStatement psSelectAllAlarms = null;
	private PreparedStatement psSelectAll = null;
	private PreparedStatement psSelect= null;
	private PreparedStatement psSelectAvg= null;
	private PreparedStatement psSelectMinVel= null;
	private PreparedStatement psEraseAllWindData= null;
	private PreparedStatement psEraseAllAlarmData= null;



	public void createDB() {

		/* We will be using Statement and PreparedStatement objects for
		 * executing SQL. These objects, as well as Connections and ResultSets,
		 * are resources that should be released explicitly after use, hence
		 * the try-catch-finally pattern used below.
		 * We are storing the Statement and Prepared statement object references
		 * in an array list for convenience.
		 */
		Connection conn = null;




		/* This ArrayList usage may cause a warning when compiling this class
		 * with a compiler for J2SE 5.0 or newer. We are not using generics
		 * because we want the source to support J2SE 1.4.2 environments. */
		ArrayList <Statement> statements =  new ArrayList <Statement>();
		// list of Statements, PreparedStatements
		Statement s = null;
		ResultSet rs = null;
		try
		{
			//loadDriver();

			Properties props = new Properties(); // connection properties
			// providing a user name and password is optional in the embedded
			// and derbyclient frameworks            
			props.put("user", userName);
			props.put("password", passwd);

			/* By default, the schema APP will be used when no username is
			 * provided.
			 * Otherwise, the schema name is the same as the user name (in this
			 * case "user1" or USER1.)
			 *
			 * Note that user authentication is off by default, meaning that any
			 * user can connect to your database using any password. To enable
			 * authentication, see the Derby Developer's Guide.
			 */

			/*
			 * This connection specifies create=true in the connection URL to
			 * cause the database to be created when connecting for the first
			 * time. To remove the database, remove the directory derbyDB (the
			 * same as the database name) and its contents.
			 *
			 * The directory derbyDB will be created under the directory that
			 * the system property derby.system.home points to, or the current
			 * directory (user.dir) if derby.system.home is not set.
			 */
			conn = DriverManager.getConnection(protocol + dbName
					+ ";create=true;dataEncryption=true;bootPassword="+bootpasswd, props);

			//System.out.println("Connected to and created database " + dbName);
			WindMill.logger.debug("Connected to and created database " + dbName);

			// We want to control transactions manually. Autocommit is on by
			// default in JDBC.
			conn.setAutoCommit(false);
			/* Creating a statement object that we can use for running various
			 * SQL statements commands against the database.*/
			s = conn.createStatement();
			statements.add(s);
			final String setProperty = 
				"CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(";
			final String requireAuth = "'derby.connection.requireAuthentication'";
			final String sqlAuthorization = "'derby.database.sqlAuthorization'";
			final String defaultConnMode =
				"'derby.database.defaultConnectionMode'";
			final String fullAccessUsers = "'derby.database.fullAccessUsers'";
			final String provider = "'derby.authentication.provider'";
			final String propertiesOnly = "'derby.database.propertiesOnly'";
			final String home = "'derby.system.home'";



			s.executeUpdate(setProperty + home + ", '"+WindMill.DBPATH+"')");
			// Set requireAuthentication
			s.executeUpdate(setProperty + requireAuth + ", 'true')");
			// Set sqlAuthorization
			s.executeUpdate(setProperty + sqlAuthorization + ", 'true')");
			// Set authentication scheme to Derby builtin
			s.executeUpdate(setProperty + provider + ", 'BUILTIN')");
			// Create some sample users
			s.executeUpdate(
					setProperty + "'derby.user."+userName+"'"+", '"+passwd+"')");
			// Define noAccess as default connection mode
			s.executeUpdate(
					setProperty + defaultConnMode + ", 'noAccess')");
			// Define read-write users
			s.executeUpdate(
					setProperty + fullAccessUsers + ", '"+userName+"')");
			s.executeUpdate(setProperty + propertiesOnly + ", 'true')");




			// We create a table...
			s.execute("create table wind_data(vel FLOAT, dir FLOAT, timeMills BIGINT, reference CHAR, gust BOOLEAN, high BOOLEAN, higher BOOLEAN)");            
			s.execute("CREATE UNIQUE INDEX time ON wind_data (timeMills ASC)");
			//System.out.println("Created table wind_data");
			WindMill.logger.info("Created table wind_data");
			conn.commit();
			WindMill.logger.info("Committed the transaction");
			s.execute("create table alarms(type SMALLINT, startTime BIGINT, endTime BIGINT)");
			s.execute("CREATE INDEX time2 ON alarms(startTime, endTime ASC)");
			//System.out.println("Created table alarms");
			WindMill.logger.info("Created table alarms");
			conn.commit();
			//System.out.println("Committed the transaction");
			WindMill.logger.info("Committed the transaction");

			/*
			 * In embedded mode, an application should shut down the database.
			 * If the application fails to shut down the database,
			 * Derby will not perform a checkpoint when the JVM shuts down.
			 * This means that it will take longer to boot (connect to) the
			 * database the next time, because Derby needs to perform a recovery
			 * operation.
			 *
			 * It is also possible to shut down the Derby system/engine, which
			 * automatically shuts down all booted databases.
			 *
			 * Explicitly shutting down the database or the Derby engine with
			 * the connection URL is preferred. This style of shutdown will
			 * always throw an SQLException.
			 *
			 * Not shutting down when in a client environment, see method
			 * Javadoc.
			 */

			try
			{
				// the shutdown=true attribute shuts down Derby
				DriverManager.getConnection("jdbc:derby:;shutdown=true");

				// To shut down a specific database only, but keep the
				// engine running (for example for connecting to other
				// databases), specify a database in the connection URL:
				//DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
			}
			catch (SQLException se)
			{
				if (( (se.getErrorCode() == 50000)
						&& ("XJ015".equals(se.getSQLState()) ))) {
					// we got the expected exception
					WindMill.logger.info("Derby shut down normally");
					// Note that for single database shutdown, the expected
					// SQL state is "08006", and the error code is 45000.
				} else {
					// if the error code or SQLState is different, we have
					// an unexpected exception (shutdown failed)
					WindMill.logger.info("Derby did not shut down normally");
					Utilities.printSQLException(se);
				}
			}

		}
		catch (SQLException sqle)
		{
			Utilities.printSQLException(sqle);
		} finally {
			// release all open resources to avoid unnecessary memory usage

			// ResultSet
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException sqle) {
				Utilities.printSQLException(sqle);
			}

			// Statements and PreparedStatements
			int stmnt_index = 0;
			while (!statements.isEmpty()) {
				// PreparedStatement extend Statement
				Statement st = statements.remove(stmnt_index);
				try {
					if (st != null) {
						st.close();
						st = null;
					}
				} catch (SQLException sqle) {
					Utilities.printSQLException(sqle);
				}
			}

			//Connection
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException sqle) {
				Utilities.printSQLException(sqle);
			}
		}
	}

 public void initDB() {
	System.getProperties().put("derby.system.home", WindMill.DBPATH);

	//loadDriver();

	Properties props = new Properties(); // connection properties
	// providing a user name and password is optional in the embedded
	// and derbyclient frameworks
	props.put("user", userName);
	props.put("password", passwd);

	StringBuffer connPriorities = new StringBuffer(protocol);
	connPriorities.append(dbName);
	connPriorities.append(";bootPassword=");
	connPriorities.append(bootpasswd);
	connPriorities.append(";");
	try {
		//loadDriver();
		conn = DriverManager.getConnection(connPriorities.toString(), props);
		//here we might get an exception because the db does not exist
		//we should handle this otherwise the program freezes
		WindMill.logger.info("Connected to database " + dbName);

		// We want to control transactions manually. Autocommit is on by
		// default in JDBC.
		conn.setAutoCommit(false);
		psInsert = conn.prepareStatement("insert into "+windTable+" values (?, ?, ?, ?, ?, ?, ?)");
		psInsertAlarm = conn.prepareStatement("insert into "+alarmTable+" values (?, ?, ?)");
		psSelectAll = conn.prepareStatement("SELECT * FROM WIND_DATA ORDER BY timeMills ASC", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		psSelect = conn.prepareStatement("SELECT * FROM WIND_DATA WHERE timeMills >= ? AND timeMills <= ? ORDER BY timeMills ASC", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		psSelectAvg = conn.prepareStatement("SELECT AVG(vel), AVG(dir) FROM WIND_DATA WHERE timeMills >= ? AND timeMills <= ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		psSelectMinVel = conn.prepareStatement("SELECT MIN(vel) FROM WIND_DATA WHERE timeMills >= ? AND timeMills <= ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		psSelectAlarm = conn.prepareStatement("SELECT startTime, endTime FROM alarms WHERE type = ? AND (endTime >= ? AND endTime <= ?) ORDER BY startTime ASC", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		psSelectAllAlarms = conn.prepareStatement("SELECT * FROM alarms WHERE endTime >= ? AND endTime <= ? ORDER BY startTime ASC", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		psEraseAllWindData = conn.prepareStatement("DELETE FROM WIND_DATA");
		psEraseAllAlarmData = conn.prepareStatement("DELETE FROM alarms");
	} catch (SQLException e) {
		Utilities.printSQLException(e);
	}
}

public void terminateDB() {
	try
	{
		DriverManager.getConnection("jdbc:derby:;shutdown=true");

	}
	catch (SQLException se)
	{
		if (( (se.getErrorCode() == 50000)
				&& ("XJ015".equals(se.getSQLState()) ))) {
			// we got the expected exception
			WindMill.logger.info("Derby shut down normally");
			// Note that for single database shutdown, the expected
			// SQL state is "08006", and the error code is 45000.
		} else {
			// if the error code or SQLState is different, we have
			// an unexpected exception (shutdown failed)
			WindMill.logger.warn("Derby did not shut down normally");
			Utilities.printSQLException(se);
		}
	} finally {
		// release all open resources to avoid unnecessary memory usage


		// Statements and PreparedStatements
		try {	
			if (psInsert != null) {
				psInsert.close();
			}
			
			if (psInsertAlarm != null){
				psInsertAlarm.close();
			}
			
			if (psSelectAlarm != null){
				psSelectAlarm.close();
			}
			if (psSelectAllAlarms != null) {
				psSelectAllAlarms.close();
			}
			
			if (psSelectAll != null) {
				psSelectAll.close();
			}
			
			if (psSelect != null) {
				psSelect.close();
			}
			
			if (psSelectAvg != null) {
				psSelectAvg.close();
			}
			
			if (psSelectMinVel != null) {
				psSelectMinVel.close();
			}

		} catch (SQLException sqle) {
			Utilities.printSQLException(sqle);
		}

		//Connection
		try {
			if (conn !=null) {
				if (!conn.isClosed()) {
					conn.close();
					conn = null;
				}
			}
		} catch (SQLException sqle) {
			Utilities.printSQLException(sqle);
		}
	}
	WindMill.logger.info("Database connection terminated");
}


public void writeWind(Anemometer anemometer) {

	Wind w =  anemometer.getW();

	try {  

		psInsert.setFloat(1, w.vel);
		psInsert.setFloat(2, w.direction);    
		psInsert.setLong(3, w.timeMills);
		psInsert.setString(4, Character.toString(w.reference));
		psInsert.setBoolean(5, Gust.getInstance().isOn());
		psInsert.setBoolean(6, High.getInstance().isOn());
		psInsert.setBoolean(7, Higher.getInstance().isOn());

		psInsert.executeUpdate();			
		conn.commit();			
	}
	catch (SQLException e) {
		Utilities.printSQLException(e);
	}
}

public ResultSet queryAllAlarms(long startTime, long endTime) {

	ResultSet rs = null;

	try {

		psSelectAllAlarms.setLong(1, startTime);
		psSelectAllAlarms.setLong(2, endTime);
		rs = psSelectAllAlarms.executeQuery();
	} catch (SQLException e) {
		com.prezerak.windmill.util.Utilities.printSQLException(e);
	}
	return rs;
}


public ResultSet queryAlarm(short alarmCode, long startTime, long endTime) {

	ResultSet rs = null;

	try {
		psSelectAlarm.setShort(1, alarmCode);
		psSelectAlarm.setLong(2, startTime);
		psSelectAlarm.setLong(3, endTime);
		rs = psSelectAlarm.executeQuery();
	} catch (SQLException e) {
		com.prezerak.windmill.util.Utilities.printSQLException(e);
	}
	return rs;
}

public void writeAlarm(short alarmCode, long startTime, long endTime) {

	try {  
		psInsertAlarm.setShort(1, alarmCode);
		psInsertAlarm.setLong(2, startTime);
		psInsertAlarm.setLong(3, endTime);
		psInsertAlarm.executeUpdate();
		conn.commit();			
	}
	catch (SQLException e) {
		Utilities.printSQLException(e);
	}
}


public ResultSet queryRecordsInTimePeriod(long startTime, long endTime) {

	ResultSet rs = null;

	try {
		psSelect.setLong(1, startTime);
		psSelect.setLong(2, endTime);
		rs = psSelect.executeQuery();
	} catch (SQLException e) {
		com.prezerak.windmill.util.Utilities.printSQLException(e);
	}
	return rs;
}


public ResultSet queryAVGRecordsInTimePeriod(long startTime, long endTime) {

	ResultSet rs = null;

	try {
		psSelectAvg.setLong(1, startTime);
		psSelectAvg.setLong(2, endTime);
		rs = psSelectAvg.executeQuery();
	} 		catch (SQLException e) {
		com.prezerak.windmill.util.Utilities.printSQLException(e);
	}
	return rs;
}

public ResultSet queryMinimumVelInTimePeriod(long startTime, long endTime) {

	ResultSet rs = null;

	try {
		psSelectMinVel.setLong(1, startTime);
		psSelectMinVel.setLong(2, endTime);
		rs = psSelectMinVel.executeQuery();

	} 		catch (SQLException e) {
		com.prezerak.windmill.util.Utilities.printSQLException(e);
	}
	return rs;
}

/**
 * 
 */
public void eraseDB() {
	// TODO Auto-generated method stub
	try {
		psEraseAllWindData.execute();
		psEraseAllAlarmData.execute();
	} 		catch (SQLException e) {
		com.prezerak.windmill.util.Utilities.printSQLException(e);
	}
	
}

}
