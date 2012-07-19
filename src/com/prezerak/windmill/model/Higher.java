package com.prezerak.windmill.model;

import com.prezerak.windmill.main.WindMill;

public class Higher {
	private float AVG; // m/sec
	private long TIMEWINDOW; //ms 
	final public short HIGHER=3;
	
	private boolean isOn = false;
	private long startTime=0;
	private long endTime=0;

	private static Higher _instance;

	private Higher() {
		// construct object . . .
		try {
			AVG = Float.parseFloat(WindMill.propertyFile.getProperty("Higher.AVG", "15.0f"));
		} catch (NumberFormatException e) {
			AVG = 15.0f;
		}

		try {
			TIMEWINDOW = 1000*60l*Integer.parseInt(WindMill.propertyFile.getProperty("Higher.TIMEWINDOW", "10"));
		} catch (NumberFormatException e) {
			TIMEWINDOW = 1000*60l*10;
		}

	}

	// For lazy initialization
	public static synchronized Higher getInstance() {
		if (_instance==null) {
			_instance = new Higher();
		} else {
			try {
				_instance.AVG = Float.parseFloat(WindMill.propertyFile.getProperty("Higher.AVG", "15.0f"));
			} catch (NumberFormatException e) {
				_instance.AVG = 15.0f;
			}

			try {
				_instance.TIMEWINDOW = 1000*60l*Integer.parseInt(WindMill.propertyFile.getProperty("Higher.TIMEWINDOW", "10"));
			} catch (NumberFormatException e) {
				_instance.TIMEWINDOW = 1000*60l*10;
			}
		}
		return _instance;
	}
	// Remainder of class definition . . .
	
	public float getAVG() {
		return AVG;
	}
	
	public long getTIMEWINDOW() {
		return TIMEWINDOW;
	}
	public boolean isOn() {
		return isOn;
	}

	public synchronized void setOn(boolean isOn) {
		this.isOn = isOn;
	}

	public synchronized long getStartTime() {
		return startTime;
	}

	public synchronized void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public synchronized long getEndTime() {
		return endTime;
	}

	public synchronized void setEndTime(long endTime) {
		this.endTime = endTime;
	}

} 


//port designer's handbook