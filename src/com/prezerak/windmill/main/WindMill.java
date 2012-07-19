
//Update main menu
//Integrate Log4J


package com.prezerak.windmill.main;

import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.SimpleTimeZone;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger; 
import org.apache.log4j.RollingFileAppender;


import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.prezerak.windmill.derby.WindDB;
import com.prezerak.windmill.gui.MainFrame;
import com.prezerak.windmill.model.Anemometer;
import com.prezerak.windmill.util.MySimpleLayout;
import com.prezerak.windmill.util.Utilities;

public class WindMill {



	//final public static String databaseHome = "C:\\WindMill\\";
	final public static String appHome = System.getProperty("user.home")+System.getProperty("file.separator")+"WindMill_GH_Runnable"+System.getProperty("file.separator");
	final public static String databaseHome = appHome+"WindDB";
	final private static String loggerPath = appHome+"logs"+System.getProperty("file.separator")+"windmill.log";
	final public static String iniPath = appHome+System.getProperty("file.separator")+"windmill.ini";

	public static MainFrame mainFrame;
	public static Anemometer anemometer = null;

	final public static float knotsToMetersConvFactor = 0.51444f;
	final public static float milesPerHrToMetersConvFactor = 0.44704f;
	public static final float KmPerHrToMetersConvFactor = 1000/3600.0f;

	public static long appStartTime;
	public static Properties propertyFile=null;

	public static String version = "1.0";

	public static Logger logger = Logger.getLogger(WindMill.class);

	public static WindDB database = null;

	/**
	 * Launch the application.
	 */


	public static void main(String[] args) {

		//Set ENGLISH locale so that time/date uses this format
		Locale.setDefault(Locale.ENGLISH);

		//Always use GMT
		SimpleTimeZone.setDefault(SimpleTimeZone.getTimeZone("Europe/London"));

		//set the logger level programmatically		

		MySimpleLayout msl = new MySimpleLayout();

		try {
			logger.addAppender(new RollingFileAppender(msl,loggerPath, true));
		} catch (IOException e) {
			logger.addAppender(new ConsoleAppender());
		}
		logger.setLevel(Level.INFO);
		logger.debug(appHome);

		//First check if the app is already running
		if (Utilities.anotherInstanceExists()) {
			JLabel lbl = new JLabel("Another instance of WindMill is already running...");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			logger.warn(lbl.getText());
			JOptionPane.showMessageDialog(null, lbl);
			System.exit(0);
		}

		database = new WindDB();
		//Then check if the database has been installed
		if (Utilities.databaseMissing()) {			
			/*
			JLabel lbl = new JLabel("Database is missing !!!");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			logger.warn(lbl.getText());
			JOptionPane.showMessageDialog(null, lbl);
			System.exit(0);
			 */
			database.createDB();
		}


		//Load the properties file
		//If not found then a message is shown but app continues with default values
		BufferedReader bReader = null;

		try {
			propertyFile = new Properties();
			//Load the properties
			bReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(iniPath)));
			propertyFile.load(bReader);
			bReader.close();
		} catch (FileNotFoundException e) {
			bReader=null;			
			JLabel lbl = new JLabel("Initialization file missing !!!");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			logger.warn(lbl.getText());
			JOptionPane.showMessageDialog(null, lbl);
			initParams();
		} catch (IOException e) {
			JLabel lbl = new JLabel("Problem with initialization !!!");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			logger.warn(lbl.getText());
			JOptionPane.showMessageDialog(null, lbl);
			initParams();
		} 


		//Log the start time
		appStartTime = System.currentTimeMillis();
		logger.info("Application started at:"+new Date(appStartTime).toString());

		//Show the main window
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				mainFrame = new MainFrame();
			}
		});
	}

	/**
	 * 
	 */
	private static void initParams() {
		// TODO Auto-generated method stub
	
		propertyFile.setProperty("SHIP", "Anonymous");
		
		propertyFile.setProperty("MODE", "TIMER_MODE");
		propertyFile.setProperty("PORT", "COM1");
		propertyFile.setProperty("BAUD", "9600");
		propertyFile.setProperty("DATABITS", "8");
		propertyFile.setProperty("STOPBITS", "1");
		propertyFile.setProperty("PARITY", "NONE");
		
		propertyFile.setProperty("Gust.FLOOR", "1");
		propertyFile.setProperty("Gust.CEILING", "12");
		propertyFile.setProperty("Gust.DIFFERENCE", "3.5");
		propertyFile.setProperty("Gust.TIMEWINDOW", "10");
		
		propertyFile.setProperty("High.TIMEWINDOW", "10");				
		propertyFile.setProperty("High.FLOOR", "1");
		propertyFile.setProperty("High.CEILING", "14.5");
		propertyFile.setProperty("High.AVG", "10.0");
		
		propertyFile.setProperty("Higher.TIMEWINDOW", "10");
		propertyFile.setProperty("Higher.FLOOR", "14.5");
		propertyFile.setProperty("Higher.CEILING", "25");
		propertyFile.setProperty("Higher.AVG", "15");
	}
}


