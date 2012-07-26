1. INSTALLATION
----------------
The WindMill app has been stored as an Eclipse Java project (v. INDIGO).
One shoud be able to import it in Eclipse with no problems.
It has been tested with JSE v6 but since it doesn't do something version specific, higher JRE versions should be OK.



2. SCOPE
----------------
The app records wind velocity and direction measurements from any NMEA compatible anemometer via the serial report. Measurements can be observed at real time. Moreover they are stored in a database and the app displays diagrams from stored measurements over any time interval. Several measurement units (knots, km/hr etc) are supported.

The app was initially targeted for the tanker market since oil companies have enforced strict rules regarding wind conditions during loading/unloading of fuel. Although it seemed a promising market, the app managed to sell 0 copies :-) in the end. Ship owners preferred more turn-key solutions (usually hw-based) to save the crew from the hassle of messing with a PC.

In any case, it can be used by anyone interested in recording wind measurements (e.g. amateur meteorologists). With a little tweaking, one can record and plot any measurements coming from the serial port.

3. NMEA
---------------

WindMill assumes an NMEA phrase in the following format:

$IIMWV,000,R,0.0,M,V*37

that is: NMEA phrase, wind direction, relative (R) or absolute (A), speed, speed units (M for m/sec, N for knots, K for km.sec).

If your anemometer uses a different format, pls modify the code in the populateWind(...) method of the Anemometer.java file.


4. TIME / DATE
---------------
Please take into account that the app uses the system clock in Windows. Thus it does not synchronize with a GPS clock or any other measurement device on board. Also, time/date is recorded in GMT therefore DO NOT use your local time when retrieving stats, ALWAYS USE the GMT equivalent.

I am aware of the issues pertaining date/time in software. The app doesn't take any special precautions, it relies solely on the system clock and converts date/time to GMT (via the java.util.TimeZone package). 

5. INITIALIZATION
------------------
Windmill creates a working directory named 'WindMill_GH_Runnable' under the user home dir (e.g C:\users\~username in Windows). This where the database resides and logs are kept.

The user may config the app by filling in values in the windmill.ini file under the same dir. If no .ini file is present, the app uses defaults values and creates an initilization file with these default values. The parameters can also be set from within the app in the Options menu.

The parameters in question are:

SHIP=<The ship's name>

MODE=<REAL_MODE to work with a real anemometer or TIMER_MODE to fool around with random values>

Various parameters for initializing the COM port.
BAUD=
DATABITS=
PARITY=
STOPBITS=
PORT=


To detect gusts, one can set a timewindow in minutes. The app measures the diff between the lowest observed wind speed and the current wind in this time window. If the difference is greater that a specific speed (in m/sec) the a gust alarm is set. The user can set a lowest (floor) and a highest(ceiling) limit regarding gust values.

Gust.TIMEWINDOW=1
Gust.FLOOR = 1
Gust.CEILING = 12
Gust.DIFFERENCE=3.5


There are also two type of alarms, high and higher. The app averages the wind speed (in m/sec) over a time period (in minutes). If the average is over a certain value then the respective alarm is set. Note that the limit for the higher alarm should be :-) higher than the high alarm. Again, the user can set a lowest (floor) and a highest(ceiling) limit regarding high/higher values.



High.TIMEWINDOW=1
High.FLOOR = 1
High.CEILING = 14
High.AVG=10.0

Higher.TIMEWINDOW=1
Higher.FLOOR = 14.5
Higher.CEILING = 25
Higher.AVG=15.0


4. DOCUMENTATION
------------------
No Javadoc yet (maybe in a next commit)
Sparse comments here & there
From an end-user's point of view, it is a relatively easy app to use.

5. LIBRARIES
-------------

Windmill uses the following freely available libraries:

Apache Derby (some old version back from 2010)
RxTx (CloudHopper version)
JGoodies Forms framework (1.3.0)
JCalendar Java date chooser bean (1.3.3) by Kai Toedter 
JFreeChart (1.0.13) by David Gilbert
Log4j(1.2.16)

My thanks to all who contributed to the code above.

5. CODE QUALITY
----------------

WindMill was developed in about 3 months, working approx. 80% of a full time equivalent. There was an urgency to ship, this explains the absence of detailed comments as well as the absence of Unit tests and the like.

Given the pressure to ship, it is not the best code I've written but not the worse either. Certainly the app would benefit from some code clean-up, more thorough static analysis and testing as well as improvements in the UI.

Enjoy // c0deb0y
Comments / proposals are welcome at albertshuffle@gmail.com



