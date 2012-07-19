package com.prezerak.windmill.gui.optionpanels;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.prezerak.windmill.main.WindMill;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.Font;
import javax.swing.border.TitledBorder;


@SuppressWarnings("serial")
public class AlarmPanel extends OptionPanel {

	private JSpinner spinnerGustSpeed;
	private JSpinner spinnerGustTime;
	private JSpinner spinnerHighTime;
	private JSpinner spinnerHighSpeed;
	private JSpinner spinnerHigherTime;
	private JSpinner spinnerHigherSpeed;
	/**
	 * Create the panel.
	 */
	public AlarmPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:max(221dlu;default)"),},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("4dlu:grow"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JPanel panelGust = new JPanel();
		panelGust.setBorder(new TitledBorder(null, "GUST", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11)));
		add(panelGust, "2, 2, fill, fill");
		panelGust.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(72dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(13dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:max(13dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(76dlu;default)"),},
				new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		float floor, ceiling, value;

		JLabel lblCurrentWindSpeed = new JLabel("Current wind speed is higher by");
		lblCurrentWindSpeed.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelGust.add(lblCurrentWindSpeed, "1, 1");
		try {
			floor = Float.parseFloat(WindMill.propertyFile.getProperty("Gust.FLOOR", "1"));
			ceiling = Float.parseFloat(WindMill.propertyFile.getProperty("Gust.CEILING", "12"));
			value = Float.parseFloat(WindMill.propertyFile.getProperty("Gust.DIFFERENCE", "3.5"));
		} catch (Exception e) {
			floor = 1;
			ceiling =12;
			value=3.5f;
		}
		
		if (value < floor || value > ceiling) {
			floor = 1;
			ceiling =12;
			value=3.5f;
		}
		
		spinnerGustSpeed = new JSpinner(new SpinnerNumberModel(
				value, floor, ceiling, 0.5));
		spinnerGustSpeed.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelGust.add(spinnerGustSpeed, "3, 1, 3, 1");

		JLabel lblMsec = new JLabel("m/sec from the lowest");
		lblMsec.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelGust.add(lblMsec, "7, 1, left, center");

		JLabel lblMSec1 = new JLabel("wind speed observed in the last");
		lblMSec1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelGust.add(lblMSec1, "1, 3, left, default");

		spinnerGustTime = new JSpinner(new SpinnerNumberModel(
				Integer.parseInt(WindMill.propertyFile.getProperty("Gust.TIMEWINDOW", "10")), 1, 20, 1));
		spinnerGustTime.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelGust.add(spinnerGustTime, "3, 3, 3, 1");

		JLabel lblMinutes = new JLabel("minutes");
		lblMinutes.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelGust.add(lblMinutes, "7, 3");

		JPanel panelHigh = new JPanel();
		panelHigh.setBorder(new TitledBorder(null, "HIGH", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11)));
		add(panelHigh, "2, 4, fill, fill");
		panelHigh.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(72dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(14dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:max(13dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(76dlu;default)"),},
				new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblCurrentWindSpeed2 = new JLabel("Average wind speed in the last");
		lblCurrentWindSpeed2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigh.add(lblCurrentWindSpeed2, "1, 1");


		spinnerHighTime = new JSpinner(new SpinnerNumberModel(
				Integer.parseInt(WindMill.propertyFile.getProperty("High.TIMEWINDOW", "10")), 1, 20, 1));
		spinnerHighTime.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigh.add(spinnerHighTime, "3, 1, 3, 1");

		JLabel lblMsec2 = new JLabel("minutes");
		lblMsec2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigh.add(lblMsec2, "7, 1, left, center");

		JLabel lblMSec3 = new JLabel("is higher than");
		lblMSec3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigh.add(lblMSec3, "1, 3, right, default");

		try {
			floor = Float.parseFloat(WindMill.propertyFile.getProperty("High.FLOOR", "1"));
			ceiling = Float.parseFloat(WindMill.propertyFile.getProperty("High.CEILING", "14.5"));
			value = Float.parseFloat(WindMill.propertyFile.getProperty("High.AVG", "10.0"));
		} catch (Exception e) {
			floor = 1;
			ceiling =14.5f;
			value=10.0f;
		}
		
		if (value < floor || value > ceiling) {
			floor = 1;
			ceiling =14.5f;
			value=10.0f;			
		}

		spinnerHighSpeed = new JSpinner(new SpinnerNumberModel(
				value, floor, ceiling, 0.5));
		spinnerHighSpeed.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigh.add(spinnerHighSpeed, "3, 3, 3, 1");

		JLabel lblMinutes2 = new JLabel("m/sec");
		lblMinutes2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblMinutes.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigh.add(lblMinutes2, "7, 3");


		JPanel panelHigher = new JPanel();
		panelHigher.setBorder(new TitledBorder(null, "HIGHER", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11)));
		add(panelHigher, "2, 6, fill, fill");
		panelHigher.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(72dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(13dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:max(13dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(76dlu;default)"),},
				new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblCurrentWindSpeed3 = new JLabel("Average wind speed in the last");
		lblCurrentWindSpeed3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigher.add(lblCurrentWindSpeed3, "1, 1");

		spinnerHigherTime = new JSpinner(new SpinnerNumberModel(
				Integer.parseInt(WindMill.propertyFile.getProperty("Higher.TIMEWINDOW", "10")), 1, 20, 1));
		spinnerHigherTime.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigher.add(spinnerHigherTime, "3, 1, 3, 1");

		JLabel lblMsec3 = new JLabel("minutes");
		lblMsec3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblMsec3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigher.add(lblMsec3, "7, 1, left, top");

		JLabel lblMSec4 = new JLabel("is higher than");
		lblMSec4.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigher.add(lblMSec4, "1, 3, right, default");
		
		try {
			floor = Float.parseFloat(WindMill.propertyFile.getProperty("Higher.FLOOR", "14.5"));
			ceiling = Float.parseFloat(WindMill.propertyFile.getProperty("Higher.CEILING", "25"));
			value = Float.parseFloat(WindMill.propertyFile.getProperty("Higher.AVG", "15"));
		} catch (Exception e) {
			floor = 14.5f;
			ceiling =25;
			value =15;
		}
		
		if (value < floor || value > ceiling) {

			floor = 14.5f;
			ceiling =25;
			value =15;
		}
		
		spinnerHigherSpeed = new JSpinner(new SpinnerNumberModel(
				value, floor, ceiling, 0.5));
		spinnerHigherSpeed.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigher.add(spinnerHigherSpeed, "3, 3, 3, 1");

		JLabel lblMinutes3 = new JLabel("m/sec");
		lblMinutes3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelHigher.add(lblMinutes3, "7, 3");


	}
	public void updateProperties() {		
		String [] properties = {"Gust.DIFFERENCE", "Gust.TIMEWINDOW", 
								"High.AVG", "High.TIMEWINDOW", 
								"Higher.AVG", "Higher.TIMEWINDOW"};
		
		String [] selections = {((Double) spinnerGustSpeed.getValue()).toString(), 
								((Integer) spinnerGustTime.getValue()).toString(),
								((Double) spinnerHighSpeed.getValue()).toString(), 
								((Integer) spinnerHighTime.getValue()).toString(),
								((Double) spinnerHigherSpeed.getValue()).toString(), 
								((Integer) spinnerHigherTime.getValue()).toString()};
		
		
		updateAll(properties, selections);
	}
}
