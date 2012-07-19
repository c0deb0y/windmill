package com.prezerak.windmill.model;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.prezerak.windmill.main.WindMill;

public class Anemometer extends Observable implements SerialPortEventListener {

	private Timer pollingTimer=null;// = new Timer();
	private int pollingTimerPeriod = 1; //seconds

	public static final int TIMER_MODE=0;
	public static final int REAL_MODE=1;
	public static int mode = TIMER_MODE;

	public SerialPort serialPort = null;
	public SerialPort getSerialPort() {
		return serialPort;
	}

	private InputStream in = null;



	private byte[] buffer = new byte[1024];
	private Wind w = null;

	private ResultSet rsAlarms=null;


	public Anemometer() {
		super();
		w = new Wind();
		String modeParam = WindMill.propertyFile.getProperty("MODE", "REAL_MODE");
		if (modeParam.equals("TIMER_MODE"))
			mode = TIMER_MODE;
		else if (modeParam.equals("REAL_MODE"))
			mode = REAL_MODE;


		if (mode == TIMER_MODE) {
			pollingTimer=new Timer();
			pollingTimer.scheduleAtFixedRate(new WindTimerTask(this), 0, 1000*pollingTimerPeriod);
		} else if (mode == REAL_MODE) {
			//connect
			connect(WindMill.propertyFile.getProperty("PORT", "COM1"));
		}
	}
	/*Below is a simple program that shows how to open a connection to a serial device and then interact with it (receiving data and sending data). One thing to note is that the package gnu.io is used instead of javax.comm, though other than the change in package name the API follows the Java Communication API. To find the names of the available ports, see the Discovering comm ports example.
	 * This varies from the other 'Two Way Communication' example in that this uses an event to trigger 
	 * the reading. One advantage of this approach is that you are not having to poll the device to see if data is available.
	 * 
	 * Note:
	 * Make sure that you call the notifyOnDataAvailable() method of the SerialPort with a boolean true parameter. Based on my experience with RXTX, just registering to the SerialPort as a SerialPortEventListener is not enough -- the event will not be propagated unless you do this.
	 * 
	 * Note2:
	 * When all is done be sure to unregister the listener ( method removeEventListener()). otherwise its possible that your program hangs during exiting causing the serial port to be blocked.
	 */ 


	/**
	 * This version of the TwoWaySerialComm example makes use of the 
	 * SerialPortEventListener to avoid polling.
	 *
	 */

	//Used only in REAL_MODE

	@SuppressWarnings("unchecked")
	public Vector<String> getPorts() {
		Enumeration<CommPortIdentifier> portList;
		Vector<String> portVect = new Vector<String>();
		portList =  CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId;

		while (portList.hasMoreElements()) {
			portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				portVect.add(portId.getName());
			}
		}
		return portVect;
	}


	public void connect (String portName ) {

		if (portName == null) return;

		try {


			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
			if ( portIdentifier.isCurrentlyOwned() )
			{
				JLabel lbl = new JLabel("Error: Port is currently in use");
				lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
				WindMill.LOGGER.warn(lbl.getText());
				JOptionPane.showMessageDialog(WindMill.mainFrame, lbl);
			}
			else
			{
				CommPort commPort = portIdentifier.open(this.getClass().getName(),5000);


				if ( commPort instanceof SerialPort )
				{
					serialPort = (SerialPort) commPort;

					int baud;
					try {
						baud=Integer.parseInt(WindMill.propertyFile.getProperty("BAUD", "9600"));
					} catch (NumberFormatException e) {
						baud=9600;
					}

					int databits;
					try {
						databits=Integer.parseInt(WindMill.propertyFile.getProperty("DATABITS", "8"));
						switch (databits) {
						case 5:
							databits=SerialPort.DATABITS_5;
							break;
						case 6:
							databits=SerialPort.DATABITS_6;
							break;
						case 7:
							databits=SerialPort.DATABITS_7;
							break;
						case 8:
							databits=SerialPort.DATABITS_8;
							break;
						default:
							databits=SerialPort.DATABITS_8;
						}
					} catch (NumberFormatException e) {
						databits=SerialPort.DATABITS_8;
					}

					int stopbits;
					String stopBitsString = WindMill.propertyFile.getProperty("STOPBITS", "1");

					if (stopBitsString.equals("1"))
						stopbits = SerialPort.STOPBITS_1;
					else if (stopBitsString.equals("1.5"))
						stopbits = SerialPort.STOPBITS_1_5;
					else if (stopBitsString.equals("2"))
						stopbits = SerialPort.STOPBITS_2;
					else
						stopbits = SerialPort.STOPBITS_1;

					int parity;
					String parityString = WindMill.propertyFile.getProperty("PARITY", "NONE");

					if (parityString.equals("EVEN"))
						parity = SerialPort.PARITY_EVEN;
					else if (parityString.equals("ODD"))
						parity = SerialPort.PARITY_ODD;
					else if (parityString.equals("MARK"))
						parity = SerialPort.PARITY_MARK;
					else if (parityString.equals("NONE"))
						parity = SerialPort.PARITY_NONE;
					else if (parityString.equals("SPACE"))
						parity = SerialPort.PARITY_SPACE;
					else
						parity = SerialPort.PARITY_NONE;

					//create invalid ini state for one second so that the PLC can sync

					serialPort.setRTS(false);
					serialPort.setDTR(true);

					long initTime = System.currentTimeMillis();

					while(true)
						if (System.currentTimeMillis() - initTime >= 1000) break;



					serialPort.setDTR(false);
					serialPort.setRTS(false);
					serialPort.setSerialPortParams(baud,databits,stopbits,parity);
					serialPort.setDTR(false);
					serialPort.setRTS(false);					
					in = serialPort.getInputStream();
					serialPort.addEventListener(this);
					serialPort.notifyOnDataAvailable(true);

					//We never write anything to the anemometer
					//OutputStream out = serialPort.getOutputStream();
					//(new Thread(new SerialWriter(out))).start();

					WindMill.LOGGER.info(portName+" connected");
				}
				else
				{
					WindMill.LOGGER.warn("Error: Only serial ports are handled by this app.");
				}
			}   //otherwise shallow some exceptions
		} catch (NoSuchPortException e) {
			JLabel lbl = new JLabel(portName+" does not exist");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			WindMill.LOGGER.warn(lbl.getText());
			JOptionPane.showMessageDialog(WindMill.mainFrame, lbl);
		} catch (PortInUseException e) {			
			JLabel lbl = new JLabel("Error: Port is currently in use");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			WindMill.LOGGER.warn(lbl.getText());
			JOptionPane.showMessageDialog(WindMill.mainFrame, lbl);
			//disconnect();
		} catch(UnsupportedCommOperationException e) {			
			JLabel lbl = new JLabel("Unsupported "+portName+" parameter");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			WindMill.LOGGER.warn(lbl.getText());
			JOptionPane.showMessageDialog(WindMill.mainFrame, lbl);
		} catch (TooManyListenersException e) {
			JLabel lbl = new JLabel("Too many serial listeners");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			WindMill.LOGGER.warn(lbl.getText());
			JOptionPane.showMessageDialog(WindMill.mainFrame, lbl);
		} catch (IOException e) {
			JLabel lbl = new JLabel("IO Error");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			WindMill.LOGGER.warn(lbl.getText());
			JOptionPane.showMessageDialog(WindMill.mainFrame, lbl);
		} catch (Exception e) {
			JLabel lbl = new JLabel("General Exception");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			WindMill.LOGGER.warn(lbl.getText());
			JOptionPane.showMessageDialog(WindMill.mainFrame, lbl);
		}
	}

	public void disconnect () {
		if (mode == TIMER_MODE) 
			pollingTimer.cancel();
		else if (mode == REAL_MODE) {
			if (serialPort==null) return;
			try {
				if (in !=null) in.close();
				serialPort.removeEventListener();
			} catch (IOException e) {

			}

			serialPort.close();
		}	
		WindMill.LOGGER.info("serial port disconnected");
	}


	//used only in TIMER_MODE
	public synchronized void poll() {


		w.direction = (float) Math.random()*360.0f;
		w.reference = 'R';
		w.vel = (float) Math.random()*30.0f;
		/*
		 * For debugging purposes only
		if (periods <= 60)
			w.vel = 10.0f;
		else if (periods >60 && periods <=120)
			w.vel = 10.0f;
		else if (periods >120 && periods <=180)
			w.vel = 1.0f;
		else if (periods >180 && periods <=240)
			w.vel = 10.0f;
		else
			w.vel = 1.0f;*/
		w.units = 'M';
		w.timeMills = System.currentTimeMillis();

		switch(w.units) {//allowed values (K) "Knots," (M) "Miles per Hour," and (N) "Meters per Second
		case 'M':
			//m/sec - do nothing
			break;
		case 'N':
			//convert knots to m / sec
			w.vel = WindMill.KNOTS_TO_METERS*w.vel;
		case 'K':
			//convert km /hr to m/sec
			w.vel = WindMill.KM_TO_METERS*w.vel;
		}

		//This way, wind always carries velocity in m/sec
		checkForAlarms();
		WindMill.database.writeWind(this);
		markChange();					
		notifyObservers();
	}

	//User only in REAL_MODE
	public synchronized void serialEvent(SerialPortEvent arg0) {
		int data;

		try
		{

			int len = 0;
			while ( ( data = in.read()) > -1 )
			{
				if ( data == '\n' ) {
					break;
				}
				buffer[len++] = (byte) data;

			}
			//$IIMWV,000,R,0.0,M,V*37
			String s = new String(buffer,0,len);
			if (populateWind(s)) {
				checkForAlarms();
				WindMill.database.writeWind(this);
				markChange();
				notifyObservers();				
			}
		}
		catch ( IOException e )
		{
			//e.printStackTrace();
			//System.exit(-1);
			WindMill.LOGGER.warn("IO exception while reading serial port");
		}             
	}

	private boolean populateWind(String s) {

		String [] tokens = s.split(",");
		/*
		if (!tokens[0].equals(WindMill.propertyFile.getProperty("EMEA_PHRASE", "$IIMWV"))) {
			System.out.println("Invalid phrase !!!"+tokens[0]);
			w.direction = 0.0f;
			w.reference = 'R';
			w.vel = 0.0f;
			w.units = 'N';
			w.timeMills = System.currentTimeMillis();
			return false;
		} else {
		*/
			//$IIMWV,000,R,0.0,M,V*37
			w.direction = Float.parseFloat(tokens[1]);
			if (w.direction > 360.0)
				w.direction -= 360.0;
			else if (w.direction < 0)
				w.direction+=360.0;
			w.reference = tokens[2].charAt(0);
			w.vel = Float.parseFloat(tokens[3]);
			w.units = tokens[4].charAt(0);
			w.timeMills = System.currentTimeMillis();
			//always convert velocity to meters
			switch (w.units) {
			case 'M': //meters per second - TESTED
				//do nothing
				break;

			case 'N': //knots - TESTED
				w.vel = w.vel*WindMill.KNOTS_TO_METERS;
				break;
			case 'K': //km/hr ?
				w.vel = w.vel*WindMill.KM_TO_METERS;
				break;
			}

		//}
		return true;
	}

	public synchronized Wind getW() {
		return w;
	}


	private void markChange() {
		setChanged();
	}

	private void checkForAlarms() {

		//check for gust
		long endTime = System.currentTimeMillis();		
		try {				

			//1. Check for for gust
			if (endTime-Gust.getInstance().getTIMEWINDOW()>= WindMill.appStartTime) {
				rsAlarms = WindMill.database.queryMinimumVelInTimePeriod(endTime-Gust.getInstance().getTIMEWINDOW(), endTime);
				//rs is never null because there is always wind data at this point
				rsAlarms.beforeFirst();
				if (rsAlarms.next()) {
					if (getW().vel-rsAlarms.getFloat(1) > Gust.getInstance().getDIFFERENCE()) {//if we have gust
						if (!Gust.getInstance().isOn()) {//if we didn't have gust during the prev. tperiod
							Gust.getInstance().setOn(true); //we must turn gust on
							Gust.getInstance().setStartTime(endTime); // and mark the start time
						} 
					} else {//we don't have gust
						if (Gust.getInstance().isOn()) {//if we had gust 						
							Gust.getInstance().setOn(false);//then we must turn it off
							Gust.getInstance().setEndTime(endTime); // mark the endTime
							WindMill.database.writeAlarm(Gust.getInstance().GUST, Gust.getInstance().getStartTime(), 
									Gust.getInstance().getEndTime());//and write to DB
						}
					}
				} 
				rsAlarms.close();
			}

			//2. Check for Higher
			if (endTime-Higher.getInstance().getTIMEWINDOW()>= WindMill.appStartTime) {
				rsAlarms = WindMill.database.queryAVGRecordsInTimePeriod(endTime-Higher.getInstance().getTIMEWINDOW(), endTime);
				//rs is never null because there is always wind data at this point
				rsAlarms.beforeFirst();
				if (rsAlarms.next()) {
					if (rsAlarms.getFloat(1) >= Higher.getInstance().getAVG()) {
						//we got Higher
						if (!Higher.getInstance().isOn()) {
							Higher.getInstance().setOn(true);
							Higher.getInstance().setStartTime(endTime);
							if (serialPort != null)
								serialPort.setDTR(true);
						}
					} else {//we don't have higher
						if (Higher.getInstance().isOn()) {
							Higher.getInstance().setOn(false);
							Higher.getInstance().setEndTime(endTime);
							WindMill.database.writeAlarm(Higher.getInstance().HIGHER, Higher.getInstance().getStartTime(), 
									Higher.getInstance().getEndTime());//and write to DB
							if (serialPort != null)
								serialPort.setDTR(false);
						}
					}
				}
				rsAlarms.close();
			}

			//3. Check for high
			if (endTime-High.getInstance().getTIMEWINDOW()>= WindMill.appStartTime) {
				rsAlarms = WindMill.database.queryAVGRecordsInTimePeriod(endTime-High.getInstance().getTIMEWINDOW(), endTime);
				//if (rs == null) return;
				rsAlarms.beforeFirst();
				if (rsAlarms.next()) {
					if (rsAlarms.getFloat(1) >= High.getInstance().getAVG()) {
						if (!High.getInstance().isOn()) {						
							High.getInstance().setOn(true);
							High.getInstance().setStartTime(endTime);
							if (serialPort != null)
								serialPort.setRTS(true);
						}
					} else {
						if (High.getInstance().isOn()) {
							High.getInstance().setOn(false);						
							High.getInstance().setEndTime(endTime);
							WindMill.database.writeAlarm(High.getInstance().HIGH, High.getInstance().getStartTime(), 
									High.getInstance().getEndTime());//and write to DB
							if (serialPort != null)
								serialPort.setRTS(false);
						}
					}
				}
				rsAlarms.close();
			}			
		} catch (SQLException e) {
			com.prezerak.windmill.util.Utilities.printSQLException(e);
		}
	}

	private class WindTimerTask extends TimerTask {

		private final Anemometer anemometer;

		public WindTimerTask(final Anemometer anemometer) {
			super();
			this.anemometer = anemometer;
		}

		//User only in TIMER_MODE
		public void run() {
			anemometer.poll();
		}
	}

}

