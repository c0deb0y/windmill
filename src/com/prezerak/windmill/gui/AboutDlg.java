package com.prezerak.windmill.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jfree.ui.RefineryUtilities;

import com.prezerak.windmill.main.WindMill;

@SuppressWarnings("serial")
public class AboutDlg extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */


	/**
	 * Create the dialog.
	 */
	public AboutDlg(JFrame owner, boolean modal) {
		super(owner, modal);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("About");
		setBounds(100, 100, 306, 265);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{63, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{27, 14, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{		
			Image wp = Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/com/prezerak/windmill/gui/resources/rotor_logo.gif"));

			JLabel canvas = new JLabel(new ImageIcon(wp));
			GridBagConstraints gbc_canvas = new GridBagConstraints();
			gbc_canvas.gridheight = 2;
			gbc_canvas.insets = new Insets(0, 0, 5, 5);
			gbc_canvas.gridx = 0;
			gbc_canvas.gridy = 0;
			contentPanel.add(canvas, gbc_canvas);
		}
		{
			JLabel lblWindmillV = new JLabel("WindMill version "+WindMill.VERSION);
			lblWindmillV.setFont(new Font("Tahoma", Font.BOLD, 11));
			lblWindmillV.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbc_lblWindmillV = new GridBagConstraints();
			gbc_lblWindmillV.insets = new Insets(0, 0, 5, 0);
			gbc_lblWindmillV.gridx = 1;
			gbc_lblWindmillV.gridy = 0;
			contentPanel.add(lblWindmillV, gbc_lblWindmillV);
		}
		{
			JLabel lblFrom = new JLabel("From:");
			lblFrom.setHorizontalAlignment(SwingConstants.LEFT);
			lblFrom.setFont(new Font("Tahoma", Font.BOLD, 11));
			GridBagConstraints gbc_lblFrom = new GridBagConstraints();
			gbc_lblFrom.anchor = GridBagConstraints.WEST;
			gbc_lblFrom.insets = new Insets(0, 0, 5, 5);
			gbc_lblFrom.gridx = 0;
			gbc_lblFrom.gridy = 2;
			contentPanel.add(lblFrom, gbc_lblFrom);
		}
		{
			JLabel lblMarelSa = new JLabel("c0deb0y");
			lblMarelSa.setFont(new Font("Tahoma", Font.PLAIN, 11));
			GridBagConstraints gbc_lblMarelSa = new GridBagConstraints();
			gbc_lblMarelSa.anchor = GridBagConstraints.WEST;
			gbc_lblMarelSa.insets = new Insets(0, 0, 5, 0);
			gbc_lblMarelSa.gridx = 1;
			gbc_lblMarelSa.gridy = 2;
			contentPanel.add(lblMarelSa, gbc_lblMarelSa);
		}
		{
			JLabel lblBasedOn = new JLabel("Based on:");
			lblBasedOn.setFont(new Font("Tahoma", Font.BOLD, 11));
			GridBagConstraints gbc_lblBasedOn = new GridBagConstraints();
			gbc_lblBasedOn.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
			gbc_lblBasedOn.insets = new Insets(0, 0, 5, 5);
			gbc_lblBasedOn.gridx = 0;
			gbc_lblBasedOn.gridy = 3;
			contentPanel.add(lblBasedOn, gbc_lblBasedOn);
		}
		{
			JLabel lblApacheDerby = new JLabel("Apache Derby");
			lblApacheDerby.setFont(new Font("Tahoma", Font.PLAIN, 11));
			GridBagConstraints gbc_lblApacheDerby = new GridBagConstraints();
			gbc_lblApacheDerby.insets = new Insets(0, 0, 5, 0);
			gbc_lblApacheDerby.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
			gbc_lblApacheDerby.gridx = 1;
			gbc_lblApacheDerby.gridy = 3;
			contentPanel.add(lblApacheDerby, gbc_lblApacheDerby);
		}
		{
			JLabel lblJfreechart = new JLabel("JFreeChart");
			lblJfreechart.setFont(new Font("Tahoma", Font.PLAIN, 11));
			GridBagConstraints gbc_lblJfreechart = new GridBagConstraints();
			gbc_lblJfreechart.anchor = GridBagConstraints.WEST;
			gbc_lblJfreechart.insets = new Insets(0, 0, 5, 0);
			gbc_lblJfreechart.gridx = 1;
			gbc_lblJfreechart.gridy = 4;
			contentPanel.add(lblJfreechart, gbc_lblJfreechart);
		}
		{
			JLabel lblJcalendar = new JLabel("JCalendar");
			lblJcalendar.setFont(new Font("Tahoma", Font.PLAIN, 11));
			lblJcalendar.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_lblJcalendar = new GridBagConstraints();
			gbc_lblJcalendar.anchor = GridBagConstraints.WEST;
			gbc_lblJcalendar.insets = new Insets(0, 0, 5, 0);
			gbc_lblJcalendar.gridx = 1;
			gbc_lblJcalendar.gridy = 5;
			contentPanel.add(lblJcalendar, gbc_lblJcalendar);
		}
		{
			JLabel lblRxtxcloudscape = new JLabel("RxTx (as a courtesy of Cloudhopper, Inc.");
			lblRxtxcloudscape.setHorizontalAlignment(SwingConstants.LEFT);
			lblRxtxcloudscape.setFont(new Font("Tahoma", Font.PLAIN, 11));
			GridBagConstraints gbc_lblRxtxcloudscape = new GridBagConstraints();
			gbc_lblRxtxcloudscape.insets = new Insets(0, 0, 5, 0);
			gbc_lblRxtxcloudscape.anchor = GridBagConstraints.WEST;
			gbc_lblRxtxcloudscape.gridx = 1;
			gbc_lblRxtxcloudscape.gridy = 6;
			contentPanel.add(lblRxtxcloudscape, gbc_lblRxtxcloudscape);
		}
		{
			JLabel lblRxtxBinaryBuilds = new JLabel("");
			lblRxtxBinaryBuilds.setFont(new Font("Tahoma", Font.PLAIN, 11));
			GridBagConstraints gbc_lblRxtxBinaryBuilds = new GridBagConstraints();
			gbc_lblRxtxBinaryBuilds.insets = new Insets(0, 0, 5, 0);
			gbc_lblRxtxBinaryBuilds.gridx = 1;
			gbc_lblRxtxBinaryBuilds.gridy = 7;
			contentPanel.add(lblRxtxBinaryBuilds, gbc_lblRxtxBinaryBuilds);
		}
		{
			JLabel lblHttprxtxcloudhoppernet = new JLabel("http://rxtx.cloudhopper.net/)");
			lblHttprxtxcloudhoppernet.setHorizontalAlignment(SwingConstants.LEFT);
			lblHttprxtxcloudhoppernet.setFont(new Font("Tahoma", Font.PLAIN, 11));
			GridBagConstraints gbc_lblHttprxtxcloudhoppernet = new GridBagConstraints();
			gbc_lblHttprxtxcloudhoppernet.anchor = GridBagConstraints.WEST;
			gbc_lblHttprxtxcloudhoppernet.gridx = 1;
			gbc_lblHttprxtxcloudhoppernet.gridy = 8;
			contentPanel.add(lblHttprxtxcloudhoppernet, gbc_lblHttprxtxcloudhoppernet);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		RefineryUtilities.centerDialogInParent(this);
		setVisible(true);
		removeAll();
		dispose();
	}

	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		setVisible(false);

	}

}
