package com.prezerak.windmill.gui;

//import java.awt.BorderLayout;


//NMEA protocol: (K) "Knots," (M) "Miles per Hour," and (N) "Meters per Second

import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;

import java.util.Observable;
import java.util.Observer;


import com.prezerak.windmill.main.WindMill;
import com.prezerak.windmill.model.Anemometer;
import com.prezerak.windmill.util.Utilities;


import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CompassPlot;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import java.awt.FlowLayout;
import java.awt.GridLayout;

@SuppressWarnings("serial")
class RealTimePanel extends JPanel implements Observer{//, ActionListener {
	
	private JLabel lblSpeed;
	private JLabel lblDir;
	private ChartPanel chartPanel = null;
	private DefaultValueDataset compassData = null;
	private JLabel lblRef;
	//private float direction;

	/**
	 * Create the frame.
	 */

	public RealTimePanel() {
		initialize();
	}

	private void initialize() {
		//setTitle("Real time wind");
		//setResizable(false);
		//setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		//setPreferredSize(new Dimension(750, 394));
		
 
		
		
		//setBorder(new EmptyBorder(5, 5, 5, 5));		
		//setLayout(null);
		setLayout(new BorderLayout());
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setLayout(new BorderLayout());
	
	       
		compassData = new DefaultValueDataset(new Double(0.0));
	    final JFreeChart chart = createChart(compassData);
	        
	    // add the chart to a panel...
	    chartPanel = new ChartPanel(chart);
	   // chartPanel.setPreferredSize(new java.awt.Dimension(750, 270));
	    //chartPanel.setBounds(10, 11, 700, 240);
	    chartPanel.setEnforceFileExtensions(false);
	    add(chartPanel, BorderLayout.CENTER);
	    
		JPanel readingsPanel2 = new JPanel();
		readingsPanel2.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(readingsPanel2, BorderLayout.SOUTH);
		readingsPanel2.setLayout(new GridLayout(0, 3, 0, 0));
		//readingsPanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JPanel speedPanel = new JPanel();
		speedPanel.setBorder(new TitledBorder(null, "Speed", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11)));
		//speedPanel.setSize(new Dimension(500, 100));
		readingsPanel2.add(speedPanel);//, FlowLayout.LEFT);
		speedPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		lblSpeed = new JLabel("000,00 miles / hr");
		lblSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpeed.setForeground(Color.BLUE);
		lblSpeed.setFont(new Font("Courier New", Font.PLAIN, 28));
		//lblSpeed.setPreferredSize(new Dimension(270, 35));
		speedPanel.add(lblSpeed);		
		JPanel dirPanel = new JPanel();
		dirPanel.setBorder(new TitledBorder(null, "Direction", TitledBorder.LEADING, TitledBorder.TOP,new Font("Tahoma", Font.BOLD, 11)));
		readingsPanel2.add(dirPanel);//, FlowLayout.CENTER);
		dirPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		lblDir = new JLabel("360,00");
		lblDir.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDir.setForeground(Color.BLUE);
		lblDir.setFont(new Font("Courier New", Font.PLAIN, 28));
		dirPanel.add(lblDir);

		JPanel refPanel = new JPanel();
		refPanel.setBorder(new TitledBorder(null, "Reference", TitledBorder.LEADING, TitledBorder.TOP,new Font("Tahoma", Font.BOLD, 11)));
		readingsPanel2.add(refPanel);//, FlowLayout.RIGHT);
		refPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		lblRef = new JLabel("T");
		lblRef.setHorizontalAlignment(SwingConstants.LEFT);
		lblRef.setForeground(Color.BLUE);
		lblRef.setFont(new Font("Courier New", Font.PLAIN, 28));
		refPanel.add(lblRef);

	}
	
	public synchronized void update(Observable model, Object arg) {
		Anemometer anemometer = (Anemometer) model; 	// the wind model always carries the
		
		// velocity in m/sec
		StringBuffer sbSpeed = null;
		StringBuffer sbDir=null;
		//StringBuffer sbRef=null;
		
		float velocity = anemometer.getW().vel;
		float direction = anemometer.getW().direction;
		String reference = new Character(anemometer.getW().reference).toString();

		if (WindMill.mainFrame.rdbtnMSec.isSelected()) {
			//speed is in m/sec - DO NOT CONVERT
			sbSpeed = new StringBuffer(String.format("%.2f", velocity));
			sbSpeed.append(" m/sec");
		} else if(WindMill.mainFrame.rdbtnKmHr.isSelected()) {
			velocity=velocity/WindMill.KM_PER_HR_TO_METERS_CONV_FACOR;
			sbSpeed = new StringBuffer(String.format("%.2f", velocity));
			sbSpeed.append(" km/hr");			
		}else if (WindMill.mainFrame.rdbtnKnots.isSelected()) {
			//convert from m/sec to knots
			velocity=velocity/WindMill.KNOTS_TO_METERS_CONV_FACTOR;
			sbSpeed = new StringBuffer(String.format("%.2f", velocity));
			sbSpeed.append(" knots");
		} else if (WindMill.mainFrame.rdbtnMilesHr.isSelected()) {
			//convert from m/sec to miles / hr
			velocity=velocity/WindMill.MILES_PER_HR_TO_METERS_CONV_FACTOR;
			sbSpeed = new StringBuffer(String.format("%.2f", velocity));
			sbSpeed.append(" miles / hr");
		} else if (WindMill.mainFrame.rdbtnBft.isSelected()) {
			sbSpeed = new StringBuffer(String.format("%d", Utilities.convertToBeauforts(velocity)));
			sbSpeed.append(" beauforts");

		}		
		lblSpeed.setText(sbSpeed.toString());

		sbDir = new StringBuffer(String.format("%.2f", direction));
		lblDir.setText(sbDir.toString());
		
		lblRef.setText(reference);

		if (isVisible()) {
			compassData.setValue(Double.valueOf(anemometer.getW().direction));
		}
		//direction = anemometer.getW().direction;
	}


	private JFreeChart createChart(final ValueDataset dataset) {
        
        final CompassPlot plot = new CompassPlot(dataset);
        plot.setSeriesNeedle(7);
        plot.setSeriesPaint(0, Color.red);
        plot.setSeriesOutlinePaint(0, Color.red);
        final JFreeChart chart = new JFreeChart(plot);
        return chart;
        
    }
}



