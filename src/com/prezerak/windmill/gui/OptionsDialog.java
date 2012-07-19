package com.prezerak.windmill.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;


import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.prezerak.windmill.gui.optionpanels.AlarmPanel;
import com.prezerak.windmill.gui.optionpanels.OptionPanel;
import com.prezerak.windmill.gui.optionpanels.SerialPanel;
import com.prezerak.windmill.gui.optionpanels.GeneralPanel;
import com.prezerak.windmill.main.WindMill;


import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.jfree.ui.RefineryUtilities;

@SuppressWarnings("serial")
public class OptionsDialog extends JDialog implements ActionListener {

	private TabbedPanePanel tabbedPanePanel;
	private JButton okButton;
	private JButton discardButton;


	/**
	 * Create the dialog.
	 */
	public OptionsDialog()  {
		super(WindMill.mainFrame, "Options", true);
		tabbedPanePanel = new TabbedPanePanel();
		GridLayout gridLayout = (GridLayout) tabbedPanePanel.getLayout();
		gridLayout.setRows(0);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabbedPanePanel, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		okButton = new JButton("OK");
		okButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		okButton.addActionListener(this);
		discardButton = new JButton("Discard");
		discardButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		discardButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(discardButton);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		setPreferredSize(new Dimension(390, 330));
		pack();
		RefineryUtilities.centerDialogInParent(this);
		setVisible(true);
		removeAll();
		dispose();
	}


	public TabbedPanePanel getTabbedPanePanel() {
		return tabbedPanePanel;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JButton btn = (JButton) e.getSource();
		if (btn.getText().equals("OK")) {
			TabbedPanePanel tpPanel = getTabbedPanePanel();
			JTabbedPane tp = (JTabbedPane) tpPanel.getComponent(0);
			OptionPanel [] panels = new OptionPanel[tp.getTabCount()];

			for (int i=0; i<3; i++)
				panels[i] = (OptionPanel) tp.getComponent(i);



			for (int i=0; i<panels.length; i++) {				
				panels[i].updateProperties();
			}
		}
		setVisible(false);
	}


	class TabbedPanePanel extends JPanel {

		private GeneralPanel genPanel;
		private SerialPanel serialPanel;
		private AlarmPanel alarmPanel;

		public TabbedPanePanel() {
			super(new GridLayout(1, 1));

			Font fn = new Font("Tahoma", Font.BOLD, 11);

			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.setFont(fn);
			tabbedPane.setTabPlacement(SwingConstants.TOP);


			genPanel = new GeneralPanel();
			tabbedPane.addTab("General", genPanel);


			serialPanel = new SerialPanel();
			tabbedPane.addTab("Serial Port", serialPanel);


			alarmPanel = new AlarmPanel();
			tabbedPane.addTab("Alarms", alarmPanel);




			add(tabbedPane);
			//The following line enables to use scrolling tabs.
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			//Add the tabbed pane to this panel.


		}

		protected JComponent makeTextPanel(String text) {
			JPanel panel = new JPanel(false);
			JLabel filler = new JLabel(text);
			filler.setHorizontalAlignment(SwingConstants.CENTER);
			panel.setLayout(new GridLayout(1, 1));
			panel.add(filler);
			return panel;
		}
	}
}

