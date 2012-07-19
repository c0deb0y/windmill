package com.prezerak.windmill.gui;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;


import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.jfree.data.time.TimeSeries;


import com.prezerak.windmill.util.Utilities;

@SuppressWarnings("serial")
public class ReportTable extends JTable  {

	private MyTableModel m;

	public ReportTable(TimeSeries datasetVel, TimeSeries datasetDir,  String units, long pollInterval, ResultSet rs) {
		m=new MyTableModel(datasetVel, datasetDir, units, pollInterval, rs);
		this.setModel(m);
	}



	boolean next() {
		if (m.currentPage < m.pages-1) {
			m.currentPage++;
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

	boolean previous() {
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



	private class MyTableModel extends AbstractTableModel {
		private String[] columnNames = {"Speed", "Direction", "Time", "Relevance", "Alarms"};
		private Object[][] data = null;
		//private TimeSeries datasetVel = null;
		//private TimeSeries datasetDir = null;
		private long currentPage;
		private int pageLength;
		private float pages;
		private boolean isPrintable;
		//private String units = null;
		private short columnCount;

		MyTableModel(TimeSeries datasetVel, TimeSeries datasetDir, String units, 
				long pollInterval, ResultSet rs) {

			isPrintable=false;
			currentPage=0;
			int itemCount;

			
			if (datasetVel == null) 
				itemCount=0;
			else if (datasetVel.getValue(0)==null)
				itemCount=0;
			else
				itemCount = datasetVel.getItemCount();//assuming vel and dir have identical number of points
			
			pages = itemCount / 60.0f;						
			if (pages < 1) 
				pageLength = itemCount;
			else
				pageLength=60;

			if (pollInterval==0)
				columnCount=5;
			else
				columnCount=3;

			data = new Object[itemCount][columnCount];
			GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Europe/London"));

			StringBuffer sb =new StringBuffer();
			Formatter formatter = new Formatter(sb);

			StringBuffer sb1 =new StringBuffer();
			Formatter formatter1 = new Formatter(sb1);

			StringBuffer sb2 =new StringBuffer();
			Formatter formatter2 = new Formatter(sb2);


			for (int i=0; i<data.length; i++) {


				Number num = datasetVel.getDataItem(i).getValue();

				if (num != null) {
					formatter1.format("%.2f", num.floatValue());
					sb1.append(units);
					data[i][0]= sb1.toString();//vel

					formatter2.format("%.2f",  datasetDir.getDataItem(i).getValue().floatValue());
					sb2.append(" ");
					sb2.append('\u00B0');					
					data[i][1]=sb2.toString();//dir

					calendar.setTimeInMillis(datasetVel.getTimePeriod(i).getLastMillisecond());
					formatter.format("%1$tH:%1$tM:%1$tS %1$tb %1$te,%1$tY", calendar);	
					data[i][2]= sb.toString();

					//data[i][3]= "test";

				}

				sb1.delete(0, sb1.length());
				sb2.delete(0, sb2.length());
				sb.delete(0, sb.length());

			}
			
			try {
				if (pollInterval==0 && !rs.isClosed()) {
					StringBuffer alarms=new StringBuffer(64);

					rs.beforeFirst();
					if (rs.next()) {
						rs.first();
						for (int i=0; i<data.length; i++) {
							data[i][3]=rs.getString(rs.findColumn("reference"));
							if (rs.getBoolean(rs.findColumn("gust")))
								alarms.append("GUST ");
							if (rs.getBoolean(rs.findColumn("high")))
								alarms.append("HIGH ");
							if (rs.getBoolean(rs.findColumn("higher")))
								alarms.append("HIGHER ");
							data[i][4]=alarms.toString();
							alarms.delete(0, alarms.length());
							rs.next();
						}
					}
				} 
			} catch (SQLException e) {
				Utilities.printSQLException(e);
			}

		}

		@Override
		public int getColumnCount() {
			//return columnNames.length;
			return columnCount;
		}

		@Override
		public int getRowCount() {
			if (isPrintable) {
				//System.out.println("data:"+data.length);
				return data.length;
			}
			else
				return pageLength;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (data !=null) {
				if (isPrintable) {								
					return data[row][col];			
				}
				else
					return data[(int)(currentPage*60)+row][col];				
			}
			else 
				return null;
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			return data[0][c].getClass();
		}
	}
}
