package com.prezerak.windmill.model;

import com.prezerak.windmill.main.WindMill;

public final class Gust {
	private float DIFFERENCE; // m/sec
	private long TIMEWINDOW; //ms
	final public short GUST=1;

	private boolean on = false;

	private long startTime=0;
	private long endTime=0;

	private static Gust _instance;

	private Gust() {
		// construct object . . .
		try {
			DIFFERENCE = Float.parseFloat(WindMill.propertyFile.getProperty("Gust.DIFFERENCE", "5.28f"));
		} catch (NumberFormatException e) {
			DIFFERENCE = 5.28f;
		}

		try {
			TIMEWINDOW = 1000*60l*Integer.parseInt(WindMill.propertyFile.getProperty("GUST.TIMEWINDOW", "10"));
		} catch (NumberFormatException e) {
			TIMEWINDOW = 1000*60l*10;
		}

	}

	// For lazy initialization
	public static synchronized Gust getInstance() {
		if (_instance==null) {
			_instance = new Gust();
		} else {
			try {
				_instance.DIFFERENCE = Float.parseFloat(WindMill.propertyFile.getProperty("Gust.DIFFERENCE", "5.28f"));
			} catch (NumberFormatException e) {
				_instance.DIFFERENCE = 5.28f;
			}

			try {
				_instance.TIMEWINDOW = 1000*60l*Integer.parseInt(WindMill.propertyFile.getProperty("Gust.TIMEWINDOW", "10"));
			} catch (NumberFormatException e) {
				_instance.TIMEWINDOW = 1000*60l*10;
			}

		}
		return _instance;
	}
	// Remainder of class definition . . .

	public float getDIFFERENCE() {
		return DIFFERENCE;
	}

	public long getTIMEWINDOW() {
		return TIMEWINDOW;
	}

	public boolean isOn() {
		return on;
	}

	public synchronized void setOn(boolean on) {
		this.on = on;
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

