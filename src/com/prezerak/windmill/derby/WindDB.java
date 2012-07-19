package com.prezerak.windmill.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


import com.prezerak.windmill.main.WindMill;
import com.prezerak.windmill.model.Gust;
import com.prezerak.windmill.model.High;
import com.prezerak.windmill.model.Higher;
import com.prezerak.windmill.model.Wind;
import com.prezerak.windmill.model.Anemometer;
import com.prezerak.windmill.util.Utilities;


public class WindDB  {

	//final private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
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


	public ResultSet QueryRecordsInTimePeriod(long startTime, long endTime) {
		
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


	public ResultSet QueryAVGRecordsInTimePeriod(long startTime, long endTime) {

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

	public ResultSet QueryMinimumVelInTimePeriod(long startTime, long endTime) {

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


	public void initDB() {
		String protocol = "jdbc:derby:";

		final String userName="pr3z3rak";
		final String passwd = "mar31sa";
		final String bootpasswd="1itt131amb";
		final String dbName = "WindDB";

		Properties p = System.getProperties();
    	p.put("derby.system.home", WindMill.databaseHome);
		
		//loadDriver();

		Properties props = new Properties(); // connection properties
		// providing a user name and password is optional in the embedded
		// and derbyclient frameworks
		props.put("user", userName);
		props.put("password", passwd);

		StringBuffer connectionProperties = new StringBuffer(protocol);
		connectionProperties.append(dbName);
		connectionProperties.append(";bootPassword=");
		connectionProperties.append(bootpasswd);
		connectionProperties.append(";");
		try {
			conn = DriverManager.getConnection(connectionProperties.toString(), props);
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
				if (psInsert != null)
					psInsert.close();
				if (  psInsertAlarm != null)
					psInsertAlarm.close();
				if (  psSelectAlarm != null)
					psSelectAlarm.close();
				if (  psSelectAllAlarms != null)
					psSelectAllAlarms.close();
				if (  psSelectAll != null)
					psSelectAll.close();
				if (  psSelect != null)
					psSelect.close();
				if (  psSelectAvg != null)
					psSelectAvg.close();
				if (  psSelectMinVel != null)
					psSelectMinVel.close();

			} catch (SQLException sqle) {

				Utilities.printSQLException(sqle);
			}
			//ResultSet
			/*
			try {
				if (rs !=null) {
					if (!rs.isClosed()) {
						conn.close();
						rs = null;
					}
				}
			} catch (SQLException sqle) {
				Utilities.printSQLException(sqle);
			}
			 */

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
}
