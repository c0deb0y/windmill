package com.prezerak.windmill.gui.optionpanels;

import java.awt.Font;


import javax.swing.JLabel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.prezerak.windmill.main.WindMill;

import java.awt.Choice;
import java.util.Iterator;
import java.util.Vector;

@SuppressWarnings("serial")
public class SerialPanel extends OptionPanel {

	private Choice choiceParity;
	private Choice choiceStopBits;
	private Choice choiceDataBits;
	private Choice choiceBaud;
	private Choice choiceComPorts;

	/**
	 * Create the panel.
	 */
	public SerialPanel() {
		Font fn = new Font("Tahoma", Font.BOLD, 11);
		Font fnPlain = new Font("Tahoma", Font.PLAIN, 11);

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(13dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:max(15dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:max(15dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblComPort = new JLabel("COM port");
		lblComPort.setFont(fn);
		add(lblComPort, "2, 2, default, fill");
		
		choiceComPorts = new Choice();
		Vector <String> comPorts = WindMill.anemometer.getPorts();
		Iterator <String> i = comPorts.iterator();
		String preferredComPort = WindMill.propertyFile.getProperty("PORT", "COM1");
		boolean preferredComPortExists = false;
		
		while(i.hasNext()) {
			String portComFound = i.next();
			choiceComPorts.add(portComFound);
			if (portComFound.equals(preferredComPort))
				preferredComPortExists = true;
		}
			
		choiceComPorts.setFont(fnPlain);
		if (preferredComPortExists)
			choiceComPorts.select(preferredComPort);
		else if (choiceComPorts.getItemCount() > 0) {
			choiceComPorts.select(0);
		}
		add(choiceComPorts, "4, 2, left, top");	
		

		JLabel lblBaudRate = new JLabel("Baud rate");
		lblBaudRate.setFont(fn);
		add(lblBaudRate, "2, 4, left, center");

	


		choiceBaud = new Choice();
		choiceBaud.add("2400");
		choiceBaud.add("4800");
		choiceBaud.add("9600");
		choiceBaud.add("19200");
		choiceBaud.add("38400");
		choiceBaud.add("57600");
		choiceBaud.add("115200");
		choiceBaud.select(WindMill.propertyFile.getProperty("BAUD", "9600"));
		choiceBaud.setFont(fnPlain);	
		add(choiceBaud, "4, 4, left, top");
		

		JLabel lblDataBits = new JLabel("Data bits");
		lblDataBits.setFont(fn);
		add(lblDataBits, "2, 6, left, fill");
		
		choiceDataBits = new Choice();
		choiceDataBits.add("5");
		choiceDataBits.add("6");
		choiceDataBits.add("7");
		choiceDataBits.add("8");
		choiceDataBits.select(WindMill.propertyFile.getProperty("DATABITS", "8"));
		choiceDataBits.setFont(fnPlain);	
		add(choiceDataBits, "4, 6, left, top");
		
		
		

		JLabel lblStopBits = new JLabel("Stop bits");
		lblStopBits.setFont(fn);
		add(lblStopBits, "2, 8, left, center");
		
		choiceStopBits = new Choice();
		choiceStopBits.add("1");
		choiceStopBits.add("1.5");
		choiceStopBits.add("2");
		choiceStopBits.select(WindMill.propertyFile.getProperty("STOPBITS", "1"));
		choiceStopBits.setFont(fnPlain);	
		add(choiceStopBits, "4, 8, left, top");
		
		JLabel lblParity = new JLabel("Parity");
		lblParity.setFont(fn);
		add(lblParity, "2, 10, left, center");
		
		choiceParity = new Choice();
		choiceParity.add("EVEN");
		choiceParity.add("ODD");
		choiceParity.add("MARK");
		choiceParity.add("NONE");
		choiceParity.add("SPACE");
		choiceParity.select(WindMill.propertyFile.getProperty("PARITY", "NONE"));
		choiceStopBits.setFont(fnPlain);
		add(choiceParity, "4, 10");


	}
	
	@Override
	public void updateProperties() {
		String [] properties = {"PORT", "BAUD", "DATABITS", "STOPBITS", "PARITY"};
		String [] selections = {choiceComPorts.getSelectedItem(),
								choiceBaud.getSelectedItem(),
								choiceDataBits.getSelectedItem(),
								choiceStopBits.getSelectedItem(),
								choiceParity.getSelectedItem()};
				 
		 
		boolean hasChanged = updateAll(properties, selections);
		
		if (hasChanged && WindMill.propertyFile.getProperty("MODE").equals("REAL_MODE")) {
			WindMill.anemometer.disconnect();
			WindMill.anemometer.connect(WindMill.propertyFile.getProperty("PORT"));
		}
	}
}


