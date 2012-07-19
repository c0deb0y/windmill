package com.prezerak.windmill.gui;

import java.awt.BorderLayout;


import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.prezerak.windmill.main.WindMill;

import java.awt.Font;

@SuppressWarnings("serial")
public class WaitDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	



	/**
	 * Create the dialog.
	 */
	public WaitDialog() {
		super(WindMill.mainFrame, "Info", true);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		setBounds(100, 100, 299, 204);
		getContentPane().setLayout(new BorderLayout(0, 0));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		{
			JLabel lblPleaseWait = new JLabel("Please wait...");
			lblPleaseWait.setBounds(64, 52, 170, 47);
			lblPleaseWait.setFont(new Font("Tahoma", Font.BOLD, 12));
			lblPleaseWait.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblPleaseWait);
		}
	}
}
