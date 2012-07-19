package com.prezerak.windmill.model;

import com.prezerak.windmill.main.WindMill;

public class High {
	private float AVG; // m/sec
	private long TIMEWINDOW; //ms
	final public short HIGH=2;
	
	private boolean isOn = false;
	private long startTime=0;
	private long endTime=0;

	private static High _instance;

	private High() {
		// construct object . . .
		try {
			AVG = Float.parseFloat(WindMill.propertyFile.getProperty("High.AVG", "10.0f"));
		} catch (NumberFormatException e) {
			AVG = 10.0f;
		}

		try {
			TIMEWINDOW = 1000*60l*Integer.parseInt(WindMill.propertyFile.getProperty("High.TIMEWINDOW", "10"));
		} catch (NumberFormatException e) {
			TIMEWINDOW = 1000*60l*10;
		}

	}

	// For lazy initialization
	public static synchronized High getInstance() {
		if (_instance==null) {
			_instance = new High();
		} else {
			try {
				_instance.AVG = Float.parseFloat(WindMill.propertyFile.getProperty("High.AVG", "10.0f"));
			} catch (NumberFormatException e) {
				_instance.AVG = 10.0f;
			}

			try {
				_instance.TIMEWINDOW = 1000*60l*Integer.parseInt(WindMill.propertyFile.getProperty("High.TIMEWINDOW", "10"));
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