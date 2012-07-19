package com.prezerak.windmill.gui;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------
 * CompassFormatDemo.java
 * ----------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CompassFormatDemo.java,v 1.4 2004/04/27 14:53:09 mungady Exp $
 *
 * Changes
 * -------
 * 18-Feb-2004 : Version 1 (DG);
 *
 */

import java.awt.Color;

import java.sql.ResultSet;

import java.sql.SQLException;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.time.TimePeriodValue;


import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;

import org.jfree.chart.plot.XYPlot;

import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import com.prezerak.windmill.main.WindMill;
import com.prezerak.windmill.model.Gust;
import com.prezerak.windmill.model.High;
import com.prezerak.windmill.model.Higher;
import com.prezerak.windmill.util.PrintUtilities;
import com.prezerak.windmill.util.Utilities;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import java.awt.BorderLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JRadioButton;
import javax.swing.border.CompoundBorder;

import java.awt.Font;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.UIManager;

/**
 The AveragesPanel class is very memory consuming especially
 in cases where the time interval is long.

 This is a known fact.

 DO NOT TRY TO FURTHER OPTIMIZE IN TERMS OF MEMORY CONSUMPTION!!!!

 TO DO: Eliminate passing rs around. 
 		As a 1st step have a separate ResultSet for reference+ alarms
 */

@SuppressWarnings("serial")
public class AveragesPanel extends JPanel implements ActionListener, ItemListener, Runnable {
	private ChartPanel chartPanel=null;
	private JFreeChart chart=null;

	//private JRadioButton rdbtnMsec = new JRadioButton("m/sec");
	//private JRadioButton rdbtnMileshr = new JRadioButton("miles/hr");
	//private JRadioButton rdbtnKnots = new JRadioButton("knots");

	private float maxY=150;

	/**
	 * Creates a new demo instance.
	 *
	 * @param title  the frame title.
	 */

	private JRadioButton rdbtnVelocity;
	private JRadioButton rdbtnDirection;

	private TimeSeries datasetVel=null;
	private TimeSeries datasetDir=null;
	private TimePeriodValues datasetGust = null;
	private TimePeriodValues datasetHigh = null;
	private TimePeriodValues datasetHigher = null;

	private ResultSet rsVelDir=null;
	private ResultSet alarmSet = null;

	private long startDate=0;
	private long endDate=0;
	private JRadioButton rdbtnGust;
	private JRadioButton rdbtnHigh;
	private JRadioButton rdbtnHigher;
	private JButton btnAlarmReport;
	private WaitDialog waitDlg;


	public AveragesPanel (long startDate, long endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout(0, 0));
		JPanel controlsPanel = new JPanel();
		controlsPanel.setBackground(UIManager.getColor("Button.background"));
		controlsPanel.setBorder(null);
		add(controlsPanel, BorderLayout.SOUTH);
		controlsPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("100px"),
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("101px"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("69px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:max(46dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(25dlu;default)"),},
				new RowSpec[] {
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				RowSpec.decode("23px"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("25px"),}));


		JButton btnReport = new JButton("Wind Report");
		controlsPanel.add(btnReport, "2, 2, fill, fill");
		btnReport.addActionListener(this);
		btnReport.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnReport.setActionCommand("Report");

		btnAlarmReport = new JButton("Alarm Report");
		btnAlarmReport.setActionCommand("Alarm Report");
		btnAlarmReport.addActionListener(this);
		btnAlarmReport.setFont(new Font("Tahoma", Font.PLAIN, 11));


		controlsPanel.add(btnAlarmReport, "4, 2");
		rdbtnVelocity = new JRadioButton("velocity");
		controlsPanel.add(rdbtnVelocity, "10, 2, left, default");
		rdbtnVelocity.setFont(new Font("Tahoma", Font.PLAIN, 11));

		rdbtnVelocity.setSelected(true);
		rdbtnVelocity.addItemListener(this);

		rdbtnDirection = new JRadioButton("direction");
		controlsPanel.add(rdbtnDirection, "12, 2, left, default");
		rdbtnDirection.setFont(new Font("Tahoma", Font.PLAIN, 11));

		rdbtnDirection.setSelected(true);
		rdbtnDirection.addItemListener(this);

		JButton btnPrintGraph = new JButton("Print Graph");
		controlsPanel.add(btnPrintGraph, "2, 4, fill, top");
		btnPrintGraph.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnPrintGraph.setActionCommand("Print Graph");
		btnPrintGraph.addActionListener(this);

		JButton btnBackToReal = new JButton("Back to Real Time ");
		controlsPanel.add(btnBackToReal, "4, 4, fill, top");
		btnBackToReal.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBackToReal.setActionCommand("Back to Real");
		btnBackToReal.addActionListener(this);

		rdbtnGust = new JRadioButton("Gust");
		rdbtnGust.setSelected(true);
		rdbtnGust.addItemListener(this);
		rdbtnGust.setFont(new Font("Tahoma", Font.PLAIN, 11));
		controlsPanel.add(rdbtnGust, "10, 4, left, default");

		rdbtnHigh = new JRadioButton("High");
		rdbtnHigh.setSelected(true);
		rdbtnHigh.addItemListener(this);
		rdbtnHigh.setFont(new Font("Tahoma", Font.PLAIN, 11));
		controlsPanel.add(rdbtnHigh, "12, 4, left, default");

		rdbtnHigher = new JRadioButton("Higher");
		rdbtnHigher.setSelected(true);
		rdbtnHigher.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnHigher.addItemListener(this);
		controlsPanel.add(rdbtnHigher, "14, 4, left, default");
	}

	private void createChart() {

		ButtonModel btnModelAvg = WindMill.mainFrame.getAvgButtonGroup().getSelection();
		long pollInterval = Long.parseLong(btnModelAvg.getActionCommand())*1000;
		String pollString=null;

		if (pollInterval == 0)
			pollString=" (actual)";
		else
			pollString = "("+String.valueOf(pollInterval/60000)+" mins)";

		ButtonModel btnModelUnits = WindMill.mainFrame.getUnitsButtonGroup().getSelection();
		String unitsString = btnModelUnits.getActionCommand();

		chart = ChartFactory.createTimeSeriesChart(
				WindMill.propertyFile.getProperty("SHIP", "SHIP")+" wind velocity/ direction"+pollString, 
				Utilities.createDateString(datasetVel.getDataItem(0).getPeriod().getLastMillisecond(),
						datasetVel.getDataItem(datasetVel.getItemCount()-1).getPeriod().getLastMillisecond()), 
						"Velocity ("+unitsString+")",
						null,
						true,
						true,
						true);
	}

	private void createChartPanel() {
		// TODO Auto-generated method stub
		if (chartPanel==null) {
			chartPanel=new ChartPanel(chart);
			chartPanel.setBorder(new CompoundBorder());
			add(chartPanel, BorderLayout.CENTER);
		}

		if (chart !=null) 
			chartPanel.setChart(chart);
	}

	private void createSeries() {

		ButtonModel btnModel = WindMill.mainFrame.getAvgButtonGroup().getSelection();
		long pollInterval = Long.parseLong(btnModel.getActionCommand())*1000;
		float conversionFactor = getWindConversionFactor();

		datasetVel = new TimeSeries("Velocity");
		datasetDir = new TimeSeries("Direction");

		Velocity v = null;

		if (conversionFactor == 0)
			v = new BeaufortVelocity(conversionFactor);
		else
			v = new RegularVelocity(conversionFactor);

		rsVelDir = WindMill.database.queryRecordsInTimePeriod(startDate, endDate);
		//In case of no values
		try {
			rsVelDir.beforeFirst();
			if (!rsVelDir.next()) {
				datasetVel.add(new FixedMillisecond(startDate), null);
				datasetDir.add(new FixedMillisecond(startDate), null);
				datasetVel.add(new FixedMillisecond(endDate), null);
				datasetDir.add(new FixedMillisecond(endDate), null);
				maxY=150;
				rsVelDir.close();

				if (datasetGust!=null)
					datasetGust.delete(0, datasetGust.getItemCount()-1);

				if (datasetHigh!=null)
					datasetHigh.delete(0, datasetHigh.getItemCount()-1);

				if (datasetHigher!=null)
					datasetHigher.delete(0, datasetHigher.getItemCount()-1);

				return;
			} 
		} catch(SQLException e) {
			Utilities.printSQLException(e);
		}

		//Graph display with actual values
		if (pollInterval==0) {
			try {
				rsVelDir.beforeFirst();
				while (rsVelDir.next()) {
					long currentTime = rsVelDir.getLong(rsVelDir.findColumn("timeMills"));
					FixedMillisecond timePeriod = new FixedMillisecond(currentTime);						 
					v.setValueInMeters(rsVelDir.getFloat(rsVelDir.findColumn("vel")));					
					datasetVel.add(timePeriod, v.getValue());
					datasetDir.add(timePeriod, rsVelDir.getFloat(rsVelDir.findColumn("dir")));
				}
			} catch(SQLException e) {
				Utilities.printSQLException(e);
			} 
		} else {

			//Obviously pollInterval !=0 so we aim for average
			try {
				rsVelDir.first();
				long startT = startDate+(rsVelDir.getLong(rsVelDir.findColumn("timeMills"))-startDate)/pollInterval*pollInterval;

				rsVelDir.last();
				long endT = startT+pollInterval;
				long endTime=rsVelDir.getLong(rsVelDir.findColumn("timeMills"))/pollInterval*pollInterval+pollInterval;

				float sumVel=0;
				float sumDir=0;
				long counter=0;

				rsVelDir.first();
				while(endT < endTime) {	
					long time=0;//set to UNIX epoch;
					if (!rsVelDir.isAfterLast()) 
						time = rsVelDir.getLong(rsVelDir.findColumn("timeMills")); 
					if (time >= startT && time < endT) {
						sumVel+= rsVelDir.getFloat(rsVelDir.findColumn("vel"));
						sumDir+= rsVelDir.getFloat(rsVelDir.findColumn("dir"));
						rsVelDir.next();
						counter++;
					} else {
						FixedMillisecond timePeriod = new FixedMillisecond(endT);
						if (counter !=0)
							v.setValueInMeters(sumVel/counter);	
						else
							v.setValueInMeters(0.0f);
						datasetVel.add(timePeriod, v.getValue());
						if (counter !=0)
							datasetDir.add(timePeriod, sumDir/counter);
						else
							datasetDir.add(timePeriod, 0.0f);
						startT=endT;
						endT+=pollInterval;
						sumVel=0;
						sumDir=0;
						counter=0;
					}
				}
				rsVelDir.close();
			} catch (SQLException e) {
				Utilities.printSQLException(e);
			}
		}

		datasetGust = queryAlarm(Gust.getInstance().GUST, "Gust");
		if (Gust.getInstance().isOn() && Gust.getInstance().getStartTime() >=startDate && Gust.getInstance().getStartTime() < endDate) {
			long t=0;
			long currentT =  System.currentTimeMillis();
			if (endDate < currentT)
				t=endDate;
			else
				t=currentT;
			SimpleTimePeriod timePeriod = new 
			SimpleTimePeriod(Gust.getInstance().getStartTime(), t);	
			TimePeriodValue timePeriodValue = new TimePeriodValue(timePeriod, 1);
			datasetGust.add(timePeriodValue);
		}

		datasetHigh = queryAlarm(High.getInstance().HIGH, "High");
		if (High.getInstance().isOn() && High.getInstance().getStartTime() >=startDate && High.getInstance().getStartTime() < endDate) {
			long t=0;
			long currentT =  System.currentTimeMillis();
			if (endDate < currentT)
				t=endDate;
			else
				t=currentT;

			SimpleTimePeriod timePeriod = new 
			SimpleTimePeriod(High.getInstance().getStartTime(), t);	
			TimePeriodValue timePeriodValue = new TimePeriodValue(timePeriod, 1);
			datasetHigh.add(timePeriodValue);
		}


		datasetHigher = queryAlarm(Higher.getInstance().HIGHER, "Higher");
		if (Higher.getInstance().isOn() && Higher.getInstance().getStartTime() >=startDate && Higher.getInstance().getStartTime() < endDate) {
			long t=0;
			long currentT =  System.currentTimeMillis();
			if (endDate < currentT)
				t=endDate;
			else
				t=currentT;

			SimpleTimePeriod timePeriod = new 
			SimpleTimePeriod(Higher.getInstance().getStartTime(), t);	
			TimePeriodValue timePeriodValue = new TimePeriodValue(timePeriod, 1);
			datasetHigher.add(timePeriodValue);
		}
	}

	private void cleanUp(boolean resultSetsOnly) {


		if (rsVelDir != null) {
			try {

				if (!rsVelDir.isClosed()){
					rsVelDir.close();
				}

			} catch (SQLException e) {
				Utilities.printSQLException(e);
			} finally {
				rsVelDir = null;
			}
		}
		if (alarmSet !=null) {
			try {
				if (!alarmSet.isClosed()){
					alarmSet.close();
				}
			} catch (SQLException e) {
				Utilities.printSQLException(e);
			} finally {
				alarmSet = null;
			}
		}

		if (resultSetsOnly) return;

		if (datasetVel !=null) {
			datasetVel.clear();
			datasetVel = null;
		}
		if (datasetDir !=null) {
			datasetDir.clear();
			datasetDir = null;
		}

		if (datasetGust !=null) datasetGust.delete(0, datasetGust.getItemCount()-1);
		if (datasetHigh !=null) datasetHigh.delete(0, datasetHigh.getItemCount()-1);
		if (datasetHigher !=null) datasetHigher.delete(0, datasetHigh.getItemCount()-1);

		datasetGust=null;
		datasetHigher=null;
		datasetHigh = null;

	}

	private void backToRealTime() {
		// TODO Auto-generated method stub
		cleanUp(false);
		WindMill.mainFrame.showRealTimeFrame();


	}

	private void plotEverything() {
		try {
			final XYPlot plot = chart.getXYPlot();

			ValueAxis domainAxis = plot.getDomainAxis();
			plot.getDomainAxis().setLowerMargin(0);
			plot.getDomainAxis().setUpperMargin(0);			
			plot.getDomainAxis().setAutoRange(true);

			if (domainAxis instanceof DateAxis) {
				DateAxis axis = (DateAxis) domainAxis;
				// customise axis here...
				//axis.setRange(new Date(startDate), new Date(endDate));
				long startT=datasetVel.getDataItem(0).getPeriod().getLastMillisecond();
				long endT=datasetVel.getDataItem(datasetVel.getItemCount()-1).getPeriod().getLastMillisecond();;
				DateFormat formatter;
				long duration = endT-startT;
				long _24hrs = 1000*60*60*24;
				long _3mins = 1000*60*3;

				if (duration > _24hrs) {
					formatter = new SimpleDateFormat("HH:mm dd-MMM");
				}
				else if (endDate-startDate > _3mins && endDate-startDate <= _24hrs)
					formatter = new SimpleDateFormat("HH:mm");
				else //smaller than 3mins
					formatter = new SimpleDateFormat("HH:mm:ss");
				axis.setDateFormatOverride(formatter);
			}


			TimeSeriesCollection seriesVel = new TimeSeriesCollection();
			seriesVel.addSeries(datasetVel);
			plot.setDataset(0, seriesVel);
			final NumberAxis velRangeAxis = (NumberAxis) plot.getRangeAxis();
			velRangeAxis.setRange(0.0, maxY);
			plot.setRangeAxis(velRangeAxis);
			plot.mapDatasetToRangeAxis(0, 0);
			XYLineAndShapeRenderer velocityRenderer = (XYLineAndShapeRenderer)plot.getRenderer(0);
			velocityRenderer.setBaseShapesVisible(true);
			velocityRenderer.setBaseShapesFilled(false);
			velocityRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
					new SimpleDateFormat("dd-MM-yy, hh:mm:ss a"), new DecimalFormat("00.0")));
			velocityRenderer.setSeriesPaint(0, Color.BLACK);

			if (!rdbtnVelocity.isSelected()) {		
				velocityRenderer.setSeriesVisible(0, false);			
			} else {
				velocityRenderer.setSeriesVisible(0, true);
			}



			TimeSeriesCollection seriesDir = new TimeSeriesCollection();
			seriesDir.addSeries(datasetDir);
			plot.setDataset(1,seriesDir);
			final ValueAxis dirRangeAxis = new NumberAxis("Direction");			
			dirRangeAxis.setRange(0.0, 370.0);
			plot.setRangeAxis(1, dirRangeAxis);
			plot.mapDatasetToRangeAxis(1, 1);


			XYLineAndShapeRenderer dirRenderer = (XYLineAndShapeRenderer) plot.getRenderer(1);
			if (dirRenderer == null)
				dirRenderer = new XYLineAndShapeRenderer();
			dirRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
					new SimpleDateFormat("dd-MM-yy, hh:mm:ss a"), new DecimalFormat("00.0")));

			plot.setRenderer(1,dirRenderer);
			dirRenderer.setSeriesPaint(0, Color.BLUE);

			if (!rdbtnDirection.isSelected()) {
				dirRenderer.setSeriesVisible(0, false);
			} else {
				dirRenderer.setSeriesVisible(0, true);
			}


			final ValueAxis alarmsRangeAxis = new NumberAxis("Alarms");			
			alarmsRangeAxis.setRange(0.0, 1);
			alarmsRangeAxis.setVisible(false);

			XYBarRenderer gustRenderer = null;

			TimePeriodValuesCollection seriesGust = new TimePeriodValuesCollection(datasetGust);
			plot.setDataset(2, seriesGust);


			plot.setRangeAxis(2, alarmsRangeAxis);
			plot.mapDatasetToRangeAxis(2, 2);

			gustRenderer= (XYBarRenderer) plot.getRenderer(2);
			if (gustRenderer==null)
				gustRenderer = new XYBarRenderer();
			plot.setRenderer(2,gustRenderer);
			gustRenderer.setSeriesPaint(0, Color.PINK);

			if ((rdbtnVelocity.isSelected() || rdbtnDirection.isSelected()) && rdbtnGust.isSelected()) 
				gustRenderer.setSeriesVisible(0, true);
			else 
				gustRenderer.setSeriesVisible(0, false);


			XYBarRenderer higherRenderer=null;
			TimePeriodValuesCollection seriesHigher = new TimePeriodValuesCollection(datasetHigher);
			plot.setDataset(3, seriesHigher);

			plot.setRangeAxis(3, alarmsRangeAxis);
			plot.mapDatasetToRangeAxis(3, 2);

			higherRenderer= (XYBarRenderer) plot.getRenderer(3);
			if (higherRenderer==null)
				higherRenderer = new XYBarRenderer();
			plot.setRenderer(3,higherRenderer);
			higherRenderer.setSeriesPaint(0, Color.RED);
			if ((rdbtnVelocity.isSelected() || rdbtnDirection.isSelected()) && rdbtnHigher.isSelected()) 

				higherRenderer.setSeriesVisible(0, true);

			else 
				higherRenderer.setSeriesVisible(0, false);



			TimePeriodValuesCollection seriesHigh = new TimePeriodValuesCollection(datasetHigh);
			plot.setDataset(4, seriesHigh);

			plot.setRangeAxis(4, alarmsRangeAxis);
			plot.mapDatasetToRangeAxis(4, 2);

			XYBarRenderer highRenderer= (XYBarRenderer) plot.getRenderer(4);
			if (highRenderer==null)
				highRenderer = new XYBarRenderer();
			plot.setRenderer(4,highRenderer);
			highRenderer.setSeriesPaint(0, new Color(206,33,85));
			if ((rdbtnVelocity.isSelected() || rdbtnDirection.isSelected()) && rdbtnHigh.isSelected()) 

				highRenderer.setSeriesVisible(0, true);
			else 
				highRenderer.setSeriesVisible(0, false);

		} catch (OutOfMemoryError e) {
			WindMill.logger.warn("Out of Memory in plotEverything");
		}

	}

	private float getWindConversionFactor() {

		float conversionFactor=0;

		if (WindMill.mainFrame.rdbtnMSec.isSelected()){		
			conversionFactor=1.0f;
			maxY=50.0f;
		} else if (WindMill.mainFrame.rdbtnKmHr.isSelected()) {
			conversionFactor=1/WindMill.KM_PER_HR_TO_METERS_CONV_FACOR;
			maxY=150.0f;
		}
		else if (WindMill.mainFrame.rdbtnMilesHr.isSelected()) {
			conversionFactor= 1/WindMill.MILES_PER_HR_TO_METERS_CONV_FACTOR;
			maxY=150.0f;
		}
		else if (WindMill.mainFrame.rdbtnKnots.isSelected()) {
			conversionFactor=1/WindMill.KNOTS_TO_METERS_CONV_FACTOR;;
			maxY=150.0f;
		}
		else if (WindMill.mainFrame.rdbtnBft.isSelected()) {
			maxY=12.0f;
			conversionFactor=0.0f; // magic value that denotes beauforts
		}

		return conversionFactor;
	}


	/**
	 * Creates a sample chart.
	 * 
	 * @return a sample chart.
	 */



	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getActionCommand().equals("Print Graph")) {
			PrintUtilities.printComponent(chartPanel);
		}  else	if (evt.getActionCommand().equals("Back to Real")) {		
			backToRealTime();
		} else if (evt.getActionCommand().equals("Report")) {
			ButtonModel btnModel = WindMill.mainFrame.getAvgButtonGroup().getSelection();
			long pollInterval = Long.parseLong(btnModel.getActionCommand())/60;

			String titlePad;
			if (pollInterval ==0)
				titlePad = " (actual)";
			else
				titlePad = " ("+Long.toString(pollInterval)+" mins)";
			new ReportDialog(WindMill.mainFrame, "Wind Report"+titlePad, 
					datasetVel, 
					datasetDir, 
					" "+WindMill.mainFrame.getButtonGroupUnits().getSelection().getActionCommand(),
					pollInterval,
					rsVelDir);
		} else if (evt.getActionCommand().equals("Alarm Report")) {
			new AlarmReportDialog(WindMill.mainFrame, "Alarm Report", true, 
					WindMill.database.queryAllAlarms(startDate, endDate), datasetGust, datasetHigh, datasetHigher);			
		}
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		plotEverything();
	}


	public void update() {
		waitDlg = new WaitDialog();
		Thread t = new Thread(this);
		t.start();
		RefineryUtilities.centerDialogInParent(waitDlg);
		waitDlg.setVisible(true);
	}

	private void runSequence() {

		createSeries();
		createChart();
		plotEverything();
		createChartPanel();
	}

	private TimePeriodValues queryAlarm(short alarmcode, String title) {
		alarmSet = WindMill.database.queryAlarm(alarmcode, startDate, endDate);
		TimePeriodValues dataset = new TimePeriodValues(title);
		try {
			alarmSet.beforeFirst();
			while (alarmSet.next()) {				
				SimpleTimePeriod timePeriod = new SimpleTimePeriod(
						alarmSet.getLong(alarmSet.findColumn("startTime")), 
						alarmSet.getLong(alarmSet.findColumn("endTime")));	
				TimePeriodValue timePeriodValue = new TimePeriodValue(timePeriod, 1);
				dataset.add(timePeriodValue);
			}

		} catch (SQLException e) {
			com.prezerak.windmill.util.Utilities.printSQLException(e);
		} finally {
			if (alarmSet !=null) {
				try {
					alarmSet.close();
				} catch (SQLException e) {
					Utilities.printSQLException(e);
				}
			}
			alarmSet=null;
		}
		return dataset;
	}

	private class Velocity {
		protected float conversionFactor = 0.0f;
		protected float valueInMeters = 0.0f;

		public Velocity(float cf) {
			this.conversionFactor = cf;
		}

		protected void setValueInMeters(float v) {
			this.valueInMeters = v;
		}

		protected float getValue() {
			return 0.0f;
		}
	}

	private class RegularVelocity extends Velocity {

		public RegularVelocity(float cf) {
			super(cf);
		}

		@Override
		protected float getValue() {
			return conversionFactor*valueInMeters;
		}
	}

	private class BeaufortVelocity extends Velocity {

		public BeaufortVelocity(float cf) {
			super(cf);
		}

		@Override
		protected float getValue() {
			return Utilities.convertToBeauforts(valueInMeters);
		}

	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		ButtonModel btnModelAvg = WindMill.mainFrame.getAvgButtonGroup().getSelection();
		long pollInterval = Long.parseLong(btnModelAvg.getActionCommand())*1000;

		if (pollInterval !=0 && endDate - startDate < pollInterval) {

			waitDlg.setVisible(false);
			waitDlg.removeAll();
			waitDlg.dispose();

			JLabel lbl = new JLabel("Choose either a longer duration or a smaller average period !!!");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			JOptionPane.showMessageDialog(null, lbl);
			if (WindMill.mainFrame.getMode() == MainFrameModes.REAL_TIME_MODE)				
				WindMill.mainFrame.showRealTimeFrame();

		} else {

			cleanUp(true);	
			runSequence();
			WindMill.mainFrame.addToDisplayPanel();
			waitDlg.setVisible(false);
			waitDlg.removeAll();
			waitDlg.dispose();				
		}

	}

	public void setDates(long startDate, long endDate) {
		// TODO Auto-generated method stub
		this.startDate = startDate;
		this.endDate = endDate;
	}


}

