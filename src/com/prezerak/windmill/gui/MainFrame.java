package com.prezerak.windmill.gui;


import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.CardLayout;

import javax.swing.JButton;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.prezerak.windmill.main.WindMill;
import com.prezerak.windmill.model.Anemometer;
import com.prezerak.windmill.model.Gust;
import com.prezerak.windmill.model.High;
import com.prezerak.windmill.model.Higher;
import com.prezerak.windmill.util.Utilities;


import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.awt.Component;

import javax.swing.Box;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import javax.swing.JSpinner;
import javax.swing.JMenuBar;
import java.awt.BorderLayout;
import java.io.BufferedInputStream;

import java.io.BufferedWriter;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.OutputStreamWriter;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Toolkit;

@SuppressWarnings("serial")


public class MainFrame extends JFrame implements ActionListener, Observer {

	private MainFrameModes MODE= MainFrameModes.REAL_TIME_MODE;
	

	private final ButtonGroup buttonGroupUnits = new ButtonGroup();
	protected JRadioButton rdbtnKnots=null;
	protected JRadioButton rdbtnKmHr=null;
	protected JRadioButton rdbtnMSec=null;
	protected JRadioButton rdbtnMilesHr=null;
	protected JRadioButton rdbtnBft=null;

	private JPanel displayPanel;
	private RealTimePanel realTimePanel;
	private JPanel periodPanel;
	private JPanel statsPanel2;
	AveragesPanel avgPanel=null;

	private final static String REALTIMEPANEL = "Realtime Panel";
	private final static String AVGPANEL = "Averages Panel";
	private JLabel lblStartdate;
	private JLabel lblEndDate;
	private JRadioButton rdbtnMins2;
	private JRadioButton rdbtnMins10;
	private JRadioButton rdbtnUserDefined;
	private final ButtonGroup buttonGroupAvgs = new ButtonGroup();

	private JRadioButton rdbtnMins30;
	private JRadioButton rdbtnActual;
	private JPanel alarmsPanel;
	private JPanel gustPanel;
	private JPanel highPanel;
	private JPanel higherPanel;
	private Component verticalStrut;
	private Component verticalStrut_1;
	private Component verticalStrut_2;
	private Component rigidArea;
	private Component rigidArea_1;
	private MyDateChooser dateChooserStart;
	private MyDateChooser dateChooserEnd;
	private JLabel lblStartTime;
	private JLabel lblEndTime;
	private JSpinner spinnerStartHrs;
	private JSpinner spinnerEndHrs;
	private JSpinner spinnerStartMins;
	private JSpinner spinnerEndMins;
	private JLabel lblHrs;
	private JLabel lblHrs_1;
	private JLabel lblMins;
	private JLabel lblMins_1;
	protected JSpinner userSpinner;
	private JButton btnGo;
	private JLabel lblMins_2;
	private JPanel menuPanel;
	private JMenuBar menuBar;
	private JMenu mnMenu;
	private JMenuItem mntmExit;
	private MainWndAdapter wndAdapter;
	private JPanel panelReport;
	private JMenuItem mntmAbout;
	
	//private static seriesState avgPanelState;



	/**
	 * Create the application.
	 */
	public MainFrame() {


		//String key = Utilities.getKey();

		byte [] keyByte =  Utilities.getKey();
		byte [] digestByte = new byte[keyByte.length];
		BufferedInputStream in = null;

		try {
			in = new BufferedInputStream(new FileInputStream("license.key"));
			in.read(digestByte);
			in.close();
		} catch (Exception e) {
			JLabel lbl = new JLabel("Possible license violation !!!");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			JOptionPane.showMessageDialog(null, lbl);
			WindMill.database.terminateDB();	
			System.exit(0);			
		} 

		try {
			if (!Arrays.equals(keyByte, digestByte)) {
				JLabel lbl = new JLabel("Possible license violation !!!");
				lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
				JOptionPane.showMessageDialog(null, lbl);
				System.exit(0);
			}} catch (Exception e) {
				
			}

			WindMill.database.initDB();

			setFont(new Font("Tahoma", Font.PLAIN, 11));
			initialize();	
			pack();
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setVisible(true);


			WindMill.anemometer = new Anemometer();		
			WindMill.anemometer.addObserver(this);
			WindMill.anemometer.addObserver(realTimePanel);

			wndAdapter = new MainWndAdapter();
			addWindowListener(wndAdapter);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setTitle("WindMill "+WindMill.version);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/com/prezerak/windmill/gui/resources/rotor_icon.gif")));
		//setSize(new Dimension(1280,800));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 258, 331, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 79, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		JPanel controlsPanel = new JPanel();
		controlsPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_controlsPanel = new GridBagConstraints();
		gbc_controlsPanel.anchor = GridBagConstraints.NORTHEAST;
		gbc_controlsPanel.gridheight = 23;
		gbc_controlsPanel.gridx = 26;
		gbc_controlsPanel.gridy = 0;
		getContentPane().add(controlsPanel, gbc_controlsPanel);
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));


		menuPanel = new JPanel();
		menuPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		menuPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		//Component verticalStrut20 = Box.createVerticalStrut(20);
		//panel.add(verticalStrut20, BorderLayout.NORTH);

		menuBar = new JMenuBar();
		menuBar.setFont(new Font("Tahoma", Font.BOLD, 12));


		menuBar.setLayout(new BorderLayout());

		mnMenu = new JMenu("Menu");
		mnMenu.setFont(new Font("Tahoma", Font.BOLD, 11));

		mnMenu.setHorizontalAlignment(SwingConstants.CENTER);


		JMenuItem mntmOptions_1 = new JMenuItem("Options");
		mntmOptions_1.setActionCommand("Options");
		mntmOptions_1.addActionListener(this);
		menuPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("307px:grow"),},
				new RowSpec[] {
				RowSpec.decode("fill:30px"),}));
		mntmOptions_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		mnMenu.add(mntmOptions_1);

		mntmExit = new JMenuItem("Exit");
		mntmExit.setActionCommand("Exit");
		mntmExit.addActionListener(this);

		mntmAbout = new JMenuItem("About");
		mntmAbout.setFont(new Font("Tahoma", Font.PLAIN, 11));
		mntmAbout.setActionCommand("About");
		mntmAbout.addActionListener(this);
		mnMenu.add(mntmAbout);
		mntmExit.setFont(new Font("Tahoma", Font.PLAIN, 11));
		mnMenu.add(mntmExit);
		menuBar.add(mnMenu, BorderLayout.CENTER);
		menuPanel.add(menuBar, "1, 1, fill, fill");

		controlsPanel.add(menuPanel);




		JPanel unitsPanel = new JPanel();
		unitsPanel.setBorder(new TitledBorder(null, "Units", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11)));
		unitsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		unitsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		rdbtnMSec = new JRadioButton("m/sec");
		rdbtnMSec.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnMSec.setSelected(true);
		rdbtnMSec.setActionCommand("m/sec");
		rdbtnMSec.addActionListener(this);
		buttonGroupUnits.add(rdbtnMSec);
		unitsPanel.add(rdbtnMSec);

		rdbtnKmHr = new JRadioButton("km/hr");
		rdbtnKmHr.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnKmHr.setActionCommand("km/hr");
		rdbtnKmHr.addActionListener(this);
		buttonGroupUnits.add(rdbtnKmHr);
		unitsPanel.add(rdbtnKmHr);

		rdbtnKnots = new JRadioButton("knots");
		rdbtnKnots.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnKnots.setActionCommand("knots");
		rdbtnKnots.addActionListener(this);
		buttonGroupUnits.add(rdbtnKnots);
		unitsPanel.add(rdbtnKnots);

		rdbtnMilesHr = new JRadioButton("miles/hr");
		rdbtnMilesHr.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnMilesHr.setActionCommand("miles/hr");
		rdbtnMilesHr.addActionListener(this);		
		buttonGroupUnits.add(rdbtnMilesHr);
		unitsPanel.add(rdbtnMilesHr);

		rdbtnBft = new JRadioButton("Bft");
		rdbtnBft.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnBft.addActionListener(this);
		rdbtnBft.setActionCommand("Bft");
		buttonGroupUnits.add(rdbtnBft);
		unitsPanel.add(rdbtnBft);

		controlsPanel.add(unitsPanel);

		//displayPanel = new RealTimePanel();
		displayPanel = new JPanel();
		displayPanel.setLayout(new CardLayout());
		realTimePanel = new RealTimePanel();
		displayPanel.add(realTimePanel, REALTIMEPANEL);
		MODE = MainFrameModes.REAL_TIME_MODE;

		GridBagConstraints gbc_displayPanel = new GridBagConstraints();
		gbc_displayPanel.fill = GridBagConstraints.BOTH;
		gbc_displayPanel.gridheight = 23;
		gbc_displayPanel.gridwidth = 26;
		gbc_displayPanel.insets = new Insets(0, 0, 0, 5);
		gbc_displayPanel.gridx = 0;
		gbc_displayPanel.gridy = 0;
		getContentPane().add(displayPanel, gbc_displayPanel);





		periodPanel = new JPanel();
		periodPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		periodPanel.setBorder(new TitledBorder(null, "Time period", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11)));
		controlsPanel.add(periodPanel);
		periodPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(5dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:max(56dlu;default)"),
				ColumnSpec.decode("left:18dlu"),
				ColumnSpec.decode("left:max(12dlu;default)"),
				ColumnSpec.decode("left:max(21dlu;default)"),
				ColumnSpec.decode("left:max(17dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
				new RowSpec[] {
				RowSpec.decode("25px"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:23px"),}));

		lblStartdate = new JLabel("Start Date");
		lblStartdate.setFont(new Font("Tahoma", Font.PLAIN, 11));
		periodPanel.add(lblStartdate, "3, 1, left, default");

		lblStartTime = new JLabel("Start Time (GMT)");
		lblStartTime.setFont(new Font("Tahoma", Font.PLAIN, 11));
		periodPanel.add(lblStartTime, "5, 1, 3, 1, left, default");

		SpinnerListener sListener = new SpinnerListener();

		dateChooserStart = new MyDateChooser("dd/MM/yy", "##/##/##", '_');
		dateChooserStart.setLocale(Locale.ENGLISH);
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		dateChooserStart.setDate(gc.getTime());
		((JTextFieldDateEditor)dateChooserStart.getDateEditor()).addActionListener(sListener);
		//System.out.println(dateChooserStart.getDate());
		periodPanel.add(dateChooserStart, "3, 2, left, fill");


		spinnerStartMins = new JSpinner(new RolloverSpinnerNumberModel(0, 0, 59,1));
		spinnerStartMins.setEditor(new JSpinner.NumberEditor(spinnerStartMins,"00"));
		spinnerStartMins.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinnerStartMins.addChangeListener(sListener);


		spinnerStartHrs = new JSpinner(new RolloverSpinnerNumberModel(12, 0, 23,1));
		spinnerStartHrs.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinnerStartHrs.addChangeListener(sListener);
		periodPanel.add(spinnerStartHrs, "5, 2, left, default");

		lblHrs = new JLabel("hrs");
		lblHrs.setFont(new Font("Tahoma", Font.PLAIN, 11));
		periodPanel.add(lblHrs, "6, 2, center, default");
		periodPanel.add(spinnerStartMins, "7, 2, right, default");

		lblMins = new JLabel("mins");
		lblMins.setFont(new Font("Tahoma", Font.PLAIN, 11));
		periodPanel.add(lblMins, "9, 2, center, default");

		lblEndDate = new JLabel("End Date");
		lblEndDate.setFont(new Font("Tahoma", Font.PLAIN, 11));
		periodPanel.add(lblEndDate, "3, 4, left, default");

		lblEndTime = new JLabel("End Time (GMT)");
		lblEndTime.setFont(new Font("Tahoma", Font.PLAIN, 11));
		periodPanel.add(lblEndTime, "5, 4, 3, 1, left, default");

		dateChooserEnd = new MyDateChooser("dd/MM/yy", "##/##/##", '_');
		dateChooserEnd.setLocale(Locale.ENGLISH);
		gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		dateChooserEnd.setDate(gc.getTime());
		((JTextFieldDateEditor)dateChooserEnd.getDateEditor()).addActionListener(sListener);
		periodPanel.add(dateChooserEnd, "3, 6, left, fill");
		//tf3.setEditable(false);
		//tf3.setBackground(Color.white);

		spinnerEndMins = new JSpinner(new RolloverSpinnerNumberModel(1, 0, 59,1));
		spinnerEndMins.setEditor(new JSpinner.NumberEditor(spinnerEndMins,"00"));
		spinnerEndMins.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinnerEndMins.addChangeListener(sListener);

		spinnerEndHrs = new JSpinner(new RolloverSpinnerNumberModel(12, 0, 23,1));
		spinnerEndHrs.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinnerEndHrs.addChangeListener(sListener);
		periodPanel.add(spinnerEndHrs, "5, 6, left, default");


		lblHrs_1 = new JLabel("hrs");
		lblHrs_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		periodPanel.add(lblHrs_1, "6, 6, center, center");
		periodPanel.add(spinnerEndMins, "7, 6, right, default");

		lblMins_1 = new JLabel("mins");
		lblMins_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblMins_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		periodPanel.add(lblMins_1, "9, 6, center, center");

		statsPanel2 = new JPanel();
		statsPanel2.setBorder(new TitledBorder(null, "Wind stats", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11)));
		controlsPanel.add(statsPanel2);
		statsPanel2.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("right:default"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:default"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:default"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:default"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		rdbtnActual = new JRadioButton("Actual");
		rdbtnActual.setSelected(true);
		statsPanel2.add(rdbtnActual, "2, 2, left, default");
		rdbtnActual.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnActual.setActionCommand("0");
		rdbtnActual.addActionListener(this);
		buttonGroupAvgs.add(rdbtnActual);

		rdbtnMins2 = new JRadioButton("2 mins");
		statsPanel2.add(rdbtnMins2, "4, 2");
		rdbtnMins2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnMins2.setActionCommand("120");
		rdbtnMins2.addActionListener(this);
		buttonGroupAvgs.add(rdbtnMins2);


		userSpinner = new JSpinner(new RolloverSpinnerNumberModel(1, 0, 60, 1));
		statsPanel2.add(userSpinner, "8, 2");
		userSpinner.setEnabled(false);
		userSpinner.addChangeListener(sListener);
		Double d = (Double) userSpinner.getModel().getValue();

		rdbtnUserDefined = new JRadioButton("user defined");
		statsPanel2.add(rdbtnUserDefined, "6, 2");
		rdbtnUserDefined.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnUserDefined.setActionCommand(String.valueOf(d.intValue()));
		rdbtnUserDefined.addChangeListener(new UserDefinedRdbtnListener());
		buttonGroupAvgs.add(rdbtnUserDefined);

		lblMins_2 = new JLabel("mins");
		lblMins_2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		statsPanel2.add(lblMins_2, "10, 2");

		rdbtnMins10 = new JRadioButton("10 mins");
		statsPanel2.add(rdbtnMins10, "2, 4, left, default");
		rdbtnMins10.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnMins10.setActionCommand("600");
		rdbtnMins10.addActionListener(this);
		buttonGroupAvgs.add(rdbtnMins10);

		rdbtnMins30 = new JRadioButton("30 mins");
		statsPanel2.add(rdbtnMins30, "4, 4");
		rdbtnMins30.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnMins30.setActionCommand("1800");
		rdbtnMins30.addActionListener(this);
		buttonGroupAvgs.add(rdbtnMins30);

		btnGo = new JButton("Graph");
		btnGo.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnGo.setActionCommand("Go");
		btnGo.addActionListener(this);
		statsPanel2.add(btnGo, "6, 4, 5, 1");

		alarmsPanel = new JPanel();
		controlsPanel.add(alarmsPanel);
		alarmsPanel.setBorder(new TitledBorder(null, "Alarms", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11)));
		alarmsPanel.setLayout(new BoxLayout(alarmsPanel, BoxLayout.Y_AXIS));

		gustPanel = new JPanel();
		gustPanel.setBorder(new TitledBorder(null, "Gust", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.PLAIN, 11)));
		gustPanel.setBackground(Color.GREEN);
		alarmsPanel.add(gustPanel);

		verticalStrut = Box.createVerticalStrut(20);
		gustPanel.add(verticalStrut);

		rigidArea = Box.createRigidArea(new Dimension(20, 20));
		alarmsPanel.add(rigidArea);

		highPanel = new JPanel();
		highPanel.setBorder(new TitledBorder(null, "High", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.PLAIN, 11)));
		highPanel.setBackground(Color.GREEN);
		alarmsPanel.add(highPanel);

		verticalStrut_1 = Box.createVerticalStrut(20);
		highPanel.add(verticalStrut_1);

		rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		alarmsPanel.add(rigidArea_1);

		higherPanel = new JPanel();
		higherPanel.setBorder(new TitledBorder(null, "Higher", TitledBorder.LEADING, TitledBorder.TOP, new Font("Tahoma", Font.PLAIN, 11)));
		higherPanel.setBackground(Color.GREEN);
		alarmsPanel.add(higherPanel);

		verticalStrut_2 = Box.createVerticalStrut(20);
		higherPanel.add(verticalStrut_2);

		panelReport = new JPanel();
		controlsPanel.add(panelReport);
		panelReport.setLayout(new BorderLayout(0, 0));
	}


	public void showRealTimeFrame() {
		CardLayout cl = (CardLayout)(displayPanel.getLayout());
		cl.show(displayPanel, REALTIMEPANEL);
		MODE = MainFrameModes.REAL_TIME_MODE;
		enableTimeControls(true);
		this.btnGo.setEnabled(true);
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getActionCommand().equals("Exit")) {
			wndAdapter.windowClosing(null);
		} if (evt.getActionCommand().equals("About")) {
			new AboutDlg(this, true);
		}
		else	if (evt.getActionCommand().equals("Options")) {
			new OptionsDialog();//blocks due to setVisible(true);
		} else if (evt.getActionCommand().equals("Go")) {

			this.btnGo.setEnabled(false);
			RolloverSpinnerNumberModel n=null;
			long startHrsOffset = 0;
			long startMinsOffset = 0;
			long endHrsOffset = 0;
			long endMinsOffset = 0;
			long startDate = dateChooserStart.getDate().getTime();
			long endDate =  dateChooserEnd.getDate().getTime();



			n = (RolloverSpinnerNumberModel) spinnerStartHrs.getModel();
			startHrsOffset = ((Double) n.getValue()).intValue()*60*60*1000l;
			n = (RolloverSpinnerNumberModel) spinnerStartMins.getModel();
			startMinsOffset = ((Double)n.getValue()).intValue()*60*1000l;
			startDate+=startHrsOffset+startMinsOffset;//+TimeZone.getDefault().getRawOffset();


			n = (RolloverSpinnerNumberModel) spinnerEndHrs.getModel();
			endHrsOffset = ((Double)n.getValue()).intValue()*60*60*1000l;
			n = (RolloverSpinnerNumberModel) spinnerEndMins.getModel();
			endMinsOffset = ((Double)n.getValue()).intValue()*60*1000l;
			endDate+=endHrsOffset+endMinsOffset;//+TimeZone.getDefault().getRawOffset();

			Double d = (Double) userSpinner.getValue();
			int pollInterval = d.intValue()*60;
			this.rdbtnUserDefined.setActionCommand(Integer.valueOf(pollInterval).toString());
			if (endDate <= startDate) {
				JLabel lbl = new JLabel("End date/time is earlier than Start date/time");
				lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
				JOptionPane.showMessageDialog(this, lbl);
				btnGo.setEnabled(true);
				return;
			}
			
			if (endDate - startDate > 1000l*60*60*24*7) {
				JLabel lbl = new JLabel("Time period is more than a week");
				lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
				JOptionPane.showMessageDialog(this, lbl);
				btnGo.setEnabled(true);
				return;			
			}

			enableTimeControls(false);
			if (avgPanel==null) 
				avgPanel = new AveragesPanel(startDate, endDate); 	
			else
				avgPanel.setDates(startDate, endDate);	
			avgPanel.update(); //this means we have a whole new chart with new dates			
		} else {//it's a unit radio button or averages radio button 

			JRadioButton btn = (JRadioButton) evt.getSource();
			if (btn == this.rdbtnKmHr ||
				btn == this.rdbtnMSec ||
				btn == this.rdbtnBft ||
				btn == this.rdbtnKnots ||
				btn == this.rdbtnMilesHr) {
				if (MODE==MainFrameModes.REAL_TIME_MODE)//just call update
					realTimePanel.update(WindMill.anemometer, null);
				else if (MODE==MainFrameModes.AVG_MODE) 	
					avgPanel.update(); //this means that just the velocity dataset gets updated					
			} else if (MODE==MainFrameModes.AVG_MODE){ //it's an averages button
				avgPanel.update(); //this means a whole new chart based on averages but with same dates				
			}
			
		}
	}


	private void enableTimeControls(boolean enabled) {
		// TODO Auto-generated method stub
		
		dateChooserStart.setEnabled(enabled);		
		dateChooserEnd.setEnabled(enabled);	
		spinnerStartHrs.setEnabled(enabled);
		spinnerEndHrs.setEnabled(enabled);
		spinnerStartMins.setEnabled(enabled);
		spinnerEndMins.setEnabled(enabled);
		
	}

	ButtonGroup getAvgButtonGroup() {
		// TODO Auto-generated method stub
		return buttonGroupAvgs;
	}
	
	ButtonGroup getUnitsButtonGroup() {
		// TODO Auto-generated method stub
		return this.buttonGroupUnits;
	}


	public synchronized void update(Observable model, Object arg) {

		if (Gust.getInstance().isOn()) {
			gustPanel.setBackground(Color.RED);
		} else {
			gustPanel.setBackground(Color.GREEN);
		}

		if (Higher.getInstance().isOn()) {
			higherPanel.setBackground(Color.RED);
		} else {
			higherPanel.setBackground(Color.GREEN);
		}

		if (High.getInstance().isOn()) {
			highPanel.setBackground(Color.RED);
		} else {
			highPanel.setBackground(Color.GREEN);
		}

	}

	MainFrameModes getMode() {
		return MODE;
	}

	JButton btnGo() {
		return btnGo;
	}

	ButtonGroup getButtonGroupUnits() {
		// TODO Auto-generated method stub
		return buttonGroupUnits;
	}

	
	void addToDisplayPanel() {
		// TODO Auto-generated method stub
		displayPanel.add(avgPanel, AVGPANEL);
		CardLayout cl = (CardLayout)(displayPanel.getLayout());
		cl.show(displayPanel, AVGPANEL);
		MODE = MainFrameModes.AVG_MODE;

	}
	
}

@SuppressWarnings("serial")
class RolloverSpinnerNumberModel extends SpinnerNumberModel {

	public RolloverSpinnerNumberModel ( double value, double minimum, double maximum, double stepSize ) {
		super (  value,  minimum,  maximum,  stepSize );
	}

	public Object getNextValue() {
		Object nv = super.getNextValue();
		if (nv != null) {
			return nv;
		}
		return getMinimum();
	}

	public Object getPreviousValue() {
		Object pv = super.getPreviousValue();
		if (pv != null) {
			return pv;
		}

		return getMaximum();
	}


}

class SpinnerListener implements ChangeListener, ActionListener {
	public void stateChanged(ChangeEvent evt) {
		activateGoBtn();
	}

	public void actionPerformed(ActionEvent evt) {
		activateGoBtn();
	}

	private void activateGoBtn() {
		//System.out.println("foo");
		if ( WindMill.mainFrame.getMode() == MainFrameModes.AVG_MODE)
			WindMill.mainFrame.btnGo().setEnabled(true); 	
	}


}

class UserDefinedRdbtnListener implements ChangeListener {
	public void stateChanged(ChangeEvent evt) {
		JRadioButton rdbtn = (JRadioButton) evt.getSource();
		if (rdbtn.isSelected()) {
			WindMill.mainFrame.userSpinner.setEnabled(true);
			WindMill.mainFrame.btnGo().setEnabled(true);
		}
		else
			WindMill.mainFrame.userSpinner.setEnabled(false);
	}
}

@SuppressWarnings("serial")
class MyDateChooser extends JDateChooser {
	public MyDateChooser(String datePattern, String maskPattern, char placeholder) {
		super(datePattern,maskPattern, placeholder);
	}

	public void setDate(java.util.Date date) {
		super.setDate(date);
		if (WindMill.mainFrame==null) return; //useful only for the 1st initialization
		if ( WindMill.mainFrame.getMode() == MainFrameModes.AVG_MODE)
			WindMill.mainFrame.btnGo().setEnabled(true); 	
	}

}

class MainWndAdapter extends WindowAdapter {
	public void windowClosing(WindowEvent evt) {
		
		WindMill.anemometer.disconnect();
		
		if (WindMill.database!=null) {
			if (Gust.getInstance().isOn()) 
				WindMill.database.writeAlarm(Gust.getInstance().GUST, Gust.getInstance().getStartTime(), 
						System.currentTimeMillis());

			if (High.getInstance().isOn()) 		
				WindMill.database.writeAlarm(High.getInstance().HIGH, High.getInstance().getStartTime(), 
						System.currentTimeMillis());

			if (Higher.getInstance().isOn()) 				
				WindMill.database.writeAlarm(Higher.getInstance().HIGHER, Higher.getInstance().getStartTime(), 
						System.currentTimeMillis());

			WindMill.database.terminateDB();	
		}
		try {
			BufferedWriter bWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(WindMill.databaseHome+"windmill.ini")));
			WindMill.propertyFile.store(bWriter, ".ini file");
			bWriter.close();
		} catch (Exception e) {
			JLabel lbl = new JLabel("Failed to save initialization file !!!");
			lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
			WindMill.logger.warn(lbl.getText());
			JOptionPane.showMessageDialog(null, lbl);
		}
		WindMill.logger.info("Application ended at:"+new Date(System.currentTimeMillis()).toString());
		System.exit(0);
	}
}
