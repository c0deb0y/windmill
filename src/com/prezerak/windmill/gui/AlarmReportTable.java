package com.prezerak.windmill.gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.jfree.data.time.TimePeriodValues;

import com.prezerak.windmill.util.Utilities;

@SuppressWarnings("serial")
public class AlarmReportTable extends JTable  {

	private MyTableModel m;

	public AlarmReportTable(ResultSet alarmSet, 			
			TimePeriodValues datasetGust,
			TimePeriodValues datasetHigh,
			TimePeriodValues datasetHigher) {

		m=new MyTableModel(alarmSet, datasetGust, datasetHigh, datasetHigher);
		this.setModel(m);
	}



	boolean Next() {
		if (m.currentPage < m.pages-1) {
			//System.out.println((m.currentPage++)+"----"+m.pages);
			m.currentPage++;
			//m.fireTableDataChanged();
		}

		if (m.currentPage >= m.pages-1) {
			m.pageLength = (int) (m.data.length-m.currentPage*60);
			if (m.pageLength==0)
				m.pageLength=60;
			m.fireTableDataChanged();
			return false;
		}
		else {
			m.fireTableDataChanged();
			return true;
		}
	}

	boolean Previous() {
		if (m.currentPage > 0) {
			//System.out.println((m.currentPage++)+"----"+m.pages);
			m.currentPage--;
			m.pageLength=60;
			m.fireTableDataChanged();
		}

		if (m.currentPage == 0)
			return false;
		else
			return true;

	}

	int getTotalPages() {
		return (int) Math.round(Math.ceil(m.pages));
	}

	void isPrintable(boolean b) {
		// TODO Auto-generated method stub
		m.isPrintable=b;
	}

	private class LiveAlarm {
		String type=null;
		String start=null;
		String end=null;
		long startT=0;
		long endT=0;
	}

	private class MyTableModel extends AbstractTableModel {
		private String[] columnNames = {"Alarm type", "Start", "End"};
		private LiveAlarm [] data = null;
		//private ResultSet alarmSet = null;
		private long currentPage;
		private int pageLength;
		private float pages;
		private boolean isPrintable;


		MyTableModel(ResultSet alarmSet,
				TimePeriodValues datasetGust,
				TimePeriodValues datasetHigh,
				TimePeriodValues datasetHigher) {

			int initialItemCount=0;

			isPrintable=false;
			currentPage=0;

			if (alarmSet !=null) {
				//ResultSetMetaData alarmSetMeta = alarmSet.getMetaData()
				try {
					if (alarmSet.isClosed()) return;
				} catch (SQLException e) {
					Utilities.printSQLException(e);
				}

				int itemCount=0;				

				try {
					alarmSet.beforeFirst();
					while (alarmSet.next())
						itemCount++;
				} catch (SQLException e) {
					Utilities.printSQLException(e);
				}

				initialItemCount = itemCount;

				if (datasetGust!=null)
					itemCount+=datasetGust.getItemCount();

				if (datasetHigh!=null)
					itemCount+=datasetHigh.getItemCount();

				if (datasetHigher!=null)			
					itemCount+=datasetHigher.getItemCount();


				pages = itemCount / 60.0f;						
				if (pages < 1) 
					pageLength = itemCount;
				else
					pageLength=60;

				data = new LiveAlarm[itemCount];

				GregorianCalendar calendar = (GregorianCalendar) new GregorianCalendar(SimpleTimeZone.getTimeZone("Europe/London"));


				StringBuffer startBuffer =new StringBuffer();
				Formatter startFormatter = new Formatter(startBuffer);

				StringBuffer endBuffer =new StringBuffer();
				Formatter endFormatter = new Formatter(endBuffer);

				try {
					alarmSet.first();
				} catch (SQLException e) {
					Utilities.printSQLException(e);
				}

				for (int i=0; i<initialItemCount; i++) {
					try {
						int alarmType = alarmSet.getShort(1);//alarm type
						data[i] = new LiveAlarm();
						switch(alarmType) {
						case 1: 
							data[i].type= new String("Gust");
							break;
						case 2: 
							data[i].type= new String("High");
							break;
						case 3: 
							data[i].type= new String("Higher");
							break;
						}

						data[i].startT  = alarmSet.getLong(2);

						calendar.setTimeInMillis(data[i].startT);
						startFormatter.format("%1$tH:%1$tM:%1$tS %1$tb %1$te,%1$tY", calendar);	
						data[i].start= startBuffer.toString();

						data[i].endT = alarmSet.getLong(3);
						
						calendar.setTimeInMillis(data[i].endT);
						endFormatter.format("%1$tH:%1$tM:%1$tS %1$tb %1$te,%1$tY", calendar);	
						data[i].end= endBuffer.toString();

						startBuffer.delete(0, startBuffer.length());
						endBuffer.delete(0, endBuffer.length());
						alarmSet.next();
					} catch (SQLException e) {
						Utilities.printSQLException(e);
						break;
					}

				}
			}
			if (data==null)
				data = new LiveAlarm[datasetGust.getItemCount()+
				                     datasetHigh.getItemCount()+
				                     datasetHigher.getItemCount()];
			int index = addLiveAlarms("Gust", datasetGust, initialItemCount);
			index=addLiveAlarms("High", datasetHigh, index);
			addLiveAlarms("Higher",datasetHigher, index);

			if (data !=null)
				Arrays.sort(data, new startTimeComparator());
		}

		private class startTimeComparator implements Comparator <LiveAlarm> {

			@Override
			public int compare(LiveAlarm o1, LiveAlarm o2) {
				// TODO Auto-generated method stub
				long start1 = o1.startT;
				long start2 = o2.startT;
				
				if (start1 < start2) return -1;
				if (start1 == start2) return 0;
				if (start1 > start2) return 1;
				
				return 0;//to suppress the compiler error;
			}
		}
		private int addLiveAlarms(String label, TimePeriodValues datasetAlarm, int start) {
			// TODO Auto-generated method stub

			if (datasetAlarm == null) 
				return start;

			int end = start+datasetAlarm.getItemCount();
			int index = 0;

			GregorianCalendar calendar = (GregorianCalendar) new GregorianCalendar(SimpleTimeZone.getTimeZone("Europe/London"));


			StringBuffer startBuffer =new StringBuffer();
			Formatter startFormatter = new Formatter(startBuffer);

			StringBuffer endBuffer =new StringBuffer();
			Formatter endFormatter = new Formatter(endBuffer);


			for (int i=start; i<end; i++) {

				data[i] = new LiveAlarm();
				
				data[i].type= new String(label);


				calendar.setTimeInMillis(datasetAlarm.getTimePeriod(index).getStart().getTime());
				startFormatter.format("%1$tH:%1$tM:%1$tS %1$tb %1$te,%1$tY", calendar);	
				data[i].start= startBuffer.toString();

				calendar.setTimeInMillis(datasetAlarm.getTimePeriod(index++).getEnd().getTime());
				endFormatter.format("%1$tH:%1$tM:%1$tS %1$tb %1$te,%1$tY", calendar);	
				data[i].end= endBuffer.toString();

				startBuffer.delete(0, startBuffer.length());
				endBuffer.delete(0, endBuffer.length());
			}
			return end;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			if (isPrintable) {
				//System.out.println("data:"+data.length);
				return data.length;
			}
			else
				return pageLength;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {

			Object o = null;

			if (data !=null) {
				if (!isPrintable) row = (int)(currentPage*60)+row;								
				switch(col) {
				case 0:
					o = (Object) data[row].type;
					break;
				case 1:
					o = (Object) data[row].start;
					break;
				case 2:
					o = (Object) data[row].end;
					break;
				}			
			}

			return o;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			return data[0].type.getClass();
		}
	}
}
