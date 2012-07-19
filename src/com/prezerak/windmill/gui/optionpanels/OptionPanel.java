package com.prezerak.windmill.gui.optionpanels;

import javax.swing.JPanel;

import com.prezerak.windmill.main.WindMill;

@SuppressWarnings("serial")
public class OptionPanel extends JPanel {
	public OptionPanel() {
	}



	public void  updateProperties() {}

	protected boolean  updateAll(String [] properties, String [] selections) {

		//TO-DO: Check for resetting the alarms
		boolean hasChanged = false;
		if (selections != null) {
			for (int i=0; i < properties.length; i++) {
				if (selections[i]==null) continue;
				if 	(!selections[i].equals(
						WindMill.propertyFile.getProperty(properties[i]))) {
					WindMill.propertyFile.setProperty(properties[i], selections[i]);
					hasChanged=true;
				}
			}
		}

		return hasChanged;
	}
}
