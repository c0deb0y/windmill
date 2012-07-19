package com.prezerak.windmill.gui.optionpanels;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.prezerak.windmill.main.WindMill;


@SuppressWarnings("serial")
public class GeneralPanel extends OptionPanel {
	

	private JTextField textFieldShipName;


	public GeneralPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblShipName = new JLabel("Ship name");
		lblShipName.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblShipName, "2, 2, right, default");
		textFieldShipName = new JTextField(WindMill.propertyFile.getProperty("SHIP", ""));
		textFieldShipName.setFont(new Font("Tahoma", Font.PLAIN, 11));
		add(textFieldShipName, "4, 2, left, default");
		textFieldShipName.setColumns(10);

	}


	@Override
	public void updateProperties() {
		String [] properties = {"SHIP"};
		String [] selections = {textFieldShipName.getText()};

		 
			updateAll(properties, selections);
	}
}
