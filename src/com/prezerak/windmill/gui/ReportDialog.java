package com.prezerak.windmill.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.ScrollPaneConstants;

import org.jfree.data.time.TimeSeries;
import org.jfree.ui.RefineryUtilities;

import com.prezerak.windmill.main.WindMill;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.ResultSet;



@SuppressWarnings("serial")
public class ReportDialog extends JDialog implements ActionListener , Printable, Runnable {
	private ReportTable table;
	private TimeSeries datasetVel=null, datasetDir=null;
	private JButton btnNext;
	private JButton btnPrevious;
	private String units;
	private long pollInterval;
	private WaitDialog waitDlg=null;
	private ResultSet rs=null;




	/**
	 * Create the dialog.
	 */
	public ReportDialog(JFrame owner, String title, TimeSeries datasetVel, 
			TimeSeries datasetDir, String units, long pollInterval, ResultSet rs) {
		super(owner, title, true);
		this.datasetVel = datasetVel;
		this.datasetDir = datasetDir;
		this.rs = rs;
		this.units = units;
		this.pollInterval = pollInterval;
		waitDlg = new WaitDialog();
		Thread t = new Thread(this);
		t.start();
		RefineryUtilities.centerDialogInParent(waitDlg);
		waitDlg.setVisible(true);
	}


	private void initialize() {
		setBounds(100, 100, 727, 562);
		getContentPane().setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		table = new ReportTable(datasetVel, datasetDir, units, pollInterval, rs);
		table.setBounds(104, 10, 225, 16);
		table.setFont(new Font("Tahoma", Font.PLAIN, 11));

		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(SwingConstants.RIGHT);		
		for (int i=0; i < table.getColumnCount();i++)
			table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);

		table.setFillsViewportHeight(true);		
		scrollPane.setViewportView(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton btnPrint = new JButton("Print");
				btnPrint.setFont(new Font("Tahoma", Font.PLAIN, 11));
				btnPrint.setActionCommand("Print");
				btnPrint.addActionListener(this);
				buttonPane.add(btnPrint);
			}
			{
				btnPrevious = new JButton("Previous");
				btnPrevious.setFont(new Font("Tahoma", Font.PLAIN, 11));
				btnPrevious.setActionCommand("Previous");
				btnPrevious.addActionListener(this);
				btnPrevious.setEnabled(false);
				buttonPane.add(btnPrevious);
			}
			{
				btnNext = new JButton("Next");
				btnNext.setFont(new Font("Tahoma", Font.PLAIN, 11));
				btnNext.setActionCommand("Next");
				btnNext.addActionListener(this);
				if (table.getTotalPages()==1)
					btnNext.setEnabled(false);
				buttonPane.add(btnNext);
			}
		}
		waitDlg.setVisible(false);
		waitDlg.removeAll();
		waitDlg.dispose();
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		RefineryUtilities.centerDialogInParent(this);
		setVisible(true);
		table.removeAll();
		removeAll();
		dispose();
		table=null;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		JButton btn = (JButton) arg0.getSource();
		String command = btn.getActionCommand();
		if (command.equals("OK")) {
			this.setVisible(false);
		} else if (command.equals("Print")) {
			table.isPrintable(true);
			//print();
			PrinterJob pj=PrinterJob.getPrinterJob();
			pj.setPrintable(this);
			table.isPrintable(true);
			pj.printDialog();
			try{ 
				pj.print();
			}catch (Exception PrintException) {}

			table.isPrintable(false);		
		} else if (command.equals("Next")) {
			boolean enabled = table.next();
			btnNext.setEnabled(enabled);
			btnPrevious.setEnabled(true);

		} else if (command.equals("Previous")) {
			boolean enabled =table.previous();
			btnPrevious.setEnabled(enabled);
			btnNext.setEnabled(true);
		}
	}



	@Override
	public int print(Graphics pg, PageFormat pageFormat,
			int pageIndex) throws PrinterException {

		pg.translate((int)pageFormat.getImageableX(),
				(int)pageFormat.getImageableY());
		int wPage = 0;
		int hPage = 0;
		if (pageFormat.getOrientation() == PageFormat.LANDSCAPE) {
			wPage = (int)pageFormat.getImageableWidth();
			hPage = (int)pageFormat.getImageableHeight();
		}
		else {
			wPage = (int)pageFormat.getImageableWidth();
			wPage += wPage/2;
			hPage = (int)pageFormat.getImageableHeight();
			pg.setClip(0,0,wPage,hPage);
		}

		int y = 20;
		pg.setFont(new java.awt.Font("Tahoma", Font.BOLD, 15));
		pg.setColor(Color.black);

		FontMetrics fm = pg.getFontMetrics();
		y += fm.getAscent();
		Graphics2D pg2 = (Graphics2D) pg;
		FontRenderContext frc = pg2.getFontRenderContext();
		StringBuffer sb = new StringBuffer(WindMill.propertyFile.getProperty("SHIP"));
		//sb.append(" wind data");
		sb.append(" ");
		sb.append(this.getTitle());
		String bigTitle = sb.toString();
		Rectangle2D bounds = pg2.getFont().getStringBounds(bigTitle, frc);
		int tWidth = (int) bounds.getWidth();

		pg.drawString(bigTitle, wPage/3 -tWidth/2, y);
		y += 20; // space between title and table headers

		Font headerFont = new Font("Tahoma", Font.PLAIN, 11);
		pg.setFont(headerFont);
		fm = pg.getFontMetrics();

		TableColumnModel colModel = table.getColumnModel();
		int nColumns = colModel.getColumnCount();
		int x[] = new int[nColumns];
		x[0] = 50;

		int h = fm.getAscent();
		y += h; // add ascent of header font because of baseline
		// positioning (see figure 2.10)

		int nRow, nCol;
		for (nCol=0; nCol<nColumns; nCol++) {
			TableColumn tk = colModel.getColumn(nCol);
			int width = 100;//tk.getWidth();
			if (x[nCol] + width > wPage) {
				nColumns = nCol;
				break;
			}
			if (nCol+1<nColumns)
				x[nCol+1] = x[nCol] + width;
			String title = (String)tk.getIdentifier();
			pg.drawString(title, x[nCol], y);
		}

		pg.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 8));
		fm = pg.getFontMetrics();

		int header = y;
		h = fm.getHeight();
		int rowH = Math.max((int)(h*1.5), 10);
		int rowPerPage = (hPage-header-3*rowH)/rowH; // 2*rowH is one blank line plus footer plus one blank
		int m_maxNumPage = Math.max((int)Math.ceil(table.getRowCount()/
				(double)rowPerPage), 1);
		if (pageIndex >= m_maxNumPage)
			return NO_SUCH_PAGE;

		int iniRow = pageIndex*rowPerPage;
		int endRow = Math.min(table.getRowCount(),
				iniRow+rowPerPage);

		for (nRow=iniRow; nRow<endRow; nRow++) {
			//y += h;
			y+=rowH;
			for (nCol=0; nCol<nColumns; nCol++) {
				int col = table.getColumnModel().getColumn(nCol).getModelIndex();
				Object obj = table.getValueAt(nRow, col);
				if (obj != null) {
					String str = obj.toString();
					pg.setColor(Color.black);
					pg.drawString(str, x[nCol], y);
				}
			}
		}
		pg.drawString("Page "+ (pageIndex+1), 300, hPage - rowH);
		//System.gc();
		return PAGE_EXISTS;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		initialize();
	}
}





