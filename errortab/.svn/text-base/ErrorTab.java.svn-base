/*
 * ALMA - Atacama Large Millimiter Array (c) European Southern Observatory, 2007
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package alma.control.gui.antennamount.errortab;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import alma.control.gui.antennamount.errortab.ErrorInfo.AdditionalInfo;

/**
 * The tab showing errors.
 * <P>
 * It contains two scrollable text areas whose content is an HTML document
 * with the last errors arriving from the GUI or from the components i.e.
 * all the errors that can be of any interest for the user.
 * The upper areas contains the detailed summary of the errors; 
 * the bottom panel contains the detailed description of the errors.
 * <P>
 *  The panel store <code>MAX_ERROR_NUMER</code> errors: when a new error is added the oldest is removed
 *  to avoid exceeding such number of errors in memory.
 *  <P>
 *  The errors are time stamped.
 *  <P>
 *  This panel listens to know when the panel is shown and acknowledges all the error.
 *  New errors (i.e. not seen errors) are marked in the HTML and this works because 
 *  the HTML is written when a new error is added and the errors are acknowledged
 *  when the user selects the error tab.
 *  <P>
 *  The refresh of the content of the HTML windows happens after a specified time
 *  interval elapsed. In fact refreshing every time an error happens causes the
 *  HTML's to flash noisy.
 * 
 * @author acaproni
 *
 */
public class ErrorTab extends JPanel implements HyperlinkListener, ActionListener, ComponentListener {
	
	// The button to save the content text field in a file 
	private JButton saveBtn = new JButton("Save");
	
	// The button to clear the content of the tab
	private JButton clearBtn = new JButton("Clear");
	
	// The text area showing errors is divided in two parts:
	// The upper panel shows the summary and the bottom panel
	// shows the details of the errors
	private JEditorPane summaryHtmlEP=new JEditorPane();
	private JEditorPane detailsHtmlEP=new JEditorPane();
	
	// The component showing the 2 html areas
	private JSplitPane splitP;
	
	// The text with errors to show in the widgets
//	private StringBuffer summaryHtmlText = new StringBuffer();
//	private StringBuffer detailsHtmlText = new StringBuffer();
	
	// Useful HTML strings
	private static final String htmlHeader ="<HTML><BODY>";
	private static final String htmlFooter="</BODY></HTML>";
	
	// The max number of errors shown by the widget
	private static final int MAX_ERROR_NUMBER = 10000;
	
	// The errors to show to the user
	// Newest errors are in position 0
	private Vector<ErrorInfo> errors = new Vector<ErrorInfo>();
	
	/** The refresh of the summary HTML happens only when a new error
	 * arrives but it is limited by time also
	 * 
	 *
	 * <code>newErrors</code> remeber if a new error arrived after refreshing
	 * the summary panel
	 */
	private volatile boolean newErrors=false;
	/**
	 * <code>REFRESH_TIME_INTERVAL</code> is the time interval to refresh
	 * the summary panel
	 */
	private static final int REFRESH_TIME_INTERVAL=2000;
	/**
	 * <code>refreshTimer</code> is the <code>swing</code> timer refreshing the 
	 * content of the summary panel
	 */
	private Timer refreshTimer=new Timer(REFRESH_TIME_INTERVAL,this);
	
	/**
	 * Constructor
	 */
	public ErrorTab() {
		setName("ErrorTab");
		initialize();
		
		updateSummaryHTML();
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		
		// Add the summary panel at the top
		summaryHtmlEP.setContentType("text/html");
		summaryHtmlEP.setEditable(false);
		summaryHtmlEP.addHyperlinkListener(this);
		JScrollPane summarySP = new JScrollPane(summaryHtmlEP,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// Add the details panel at the bottom
		detailsHtmlEP.setContentType("text/html");
		detailsHtmlEP.setEditable(false);
		JScrollPane detailsSP = new JScrollPane(detailsHtmlEP,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// Create the split pane with the two html text areas inside
		splitP = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				false,
				summarySP,
				detailsSP);
		add(splitP,BorderLayout.CENTER);
		
		// Add the buttons
		JPanel btnPnl = new JPanel(new BorderLayout());
		btnPnl.add(clearBtn,BorderLayout.WEST);
		btnPnl.add(saveBtn,BorderLayout.EAST);
		clearBtn.addActionListener(this);
		saveBtn.addActionListener(this);
		
		add(btnPnl,BorderLayout.SOUTH);
		
		addComponentListener(this);
		
		refreshTimer.addActionListener(this);
		refreshTimer.start();
	}
	
	/**
	 * Add a new error on the text area
	 * 
	 * @param error
	 */
	public synchronized void addError(ErrorInfo error) {
		if (error==null) {
			throw new IllegalArgumentException("Invalid null error");
		}
		newErrors=true;
		while (errors.size()>=MAX_ERROR_NUMBER) {
			errors.remove(errors.size()-1);
		}
		if (errors.contains(error)) {
			int pos = errors.indexOf(error);
			if (pos==-1) {
				throw new IllegalStateException("The list contains an error equal to the error to add but can't find its position");
			}
			error = errors.remove(pos);
			error.addInstance();
		}
		errors.insertElementAt(error,0);
		if (isShowing()) {
			error.acknowledge();
		}
	}
	
	/**
	 * Regenerate the content of the widget with the summary 
	 * of the errors
	 */
	private synchronized void updateSummaryHTML( ) {
		StringBuilder summaryHtmlText=new StringBuilder(htmlHeader);
		summaryHtmlText.append(htmlHeader);
		if (errors.size()==0) {
			summaryHtmlText.append("<H1 align=\"center\">No errors</H1>\n");
			summaryHtmlText.append(htmlFooter);
			clearBtn.setEnabled(false);
			saveBtn.setEnabled(false);
			summaryHtmlEP.setText(summaryHtmlText.toString());
			return;
		}
		clearBtn.setEnabled(true);
		saveBtn.setEnabled(true);
		
		// Summary
		summaryHtmlText.append("<H2 align=\"left\">Summary</H2>\n");
		summaryHtmlText.append("<OL>");
		int anchor=0; // The internal anchors have a progressive number
		for (ErrorInfo error: errors) {
			summaryHtmlText.append("<LI>");
			if (!error.hasBeenAcknowledged()) {
				summaryHtmlText.append("<FONT color=\"red\">New </FONT>");
			}
			if (error.getInstances()>0) {
				summaryHtmlText.append("<B>");
				summaryHtmlText.append(error.getInstances());
				summaryHtmlText.append("</B> instances of ");
			}
			summaryHtmlText.append("<A href=\"#Error");
			summaryHtmlText.append(anchor++);
			summaryHtmlText.append("\">");
			summaryHtmlText.append(error.shortDescription);
			summaryHtmlText.append("</A> (");
			summaryHtmlText.append(error.getTimestampStr());
			summaryHtmlText.append(')');
			summaryHtmlText.append("</LI>\n");
		}
		summaryHtmlText.append("<OL>\n");
		
		summaryHtmlText.append(htmlFooter);
		
		HTMLDocument doc = (HTMLDocument)summaryHtmlEP.getEditorKit().createDefaultDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit)summaryHtmlEP.getEditorKit();
		StringReader reader = new StringReader(summaryHtmlText.toString());
		try {
			editorKit.read(reader,doc,0);
		} catch (Exception e) {
			System.out.println("Ooooops: "+e.getMessage());
			e.printStackTrace();
		}
		summaryHtmlEP.setDocument(doc);
	}
	
	/**
	 * Regenerate the content of the widget with the details 
	 * of the errors
	 */
	private synchronized void updateDetailsHTML( ) {
		StringBuilder detailsHtmlText=new StringBuilder(htmlHeader);
		detailsHtmlText.append(htmlHeader);
		if (errors.size()==0) {
			// No errors ==> blank page
			detailsHtmlText.append(htmlFooter);
			detailsHtmlEP.setText(detailsHtmlText.toString());
			return;
		}
		detailsHtmlText.append("<H1 align=\"center\"><A name=\"BeginningOfDoc\"></A>Errors</H1>\n");
		
		// Details
		int anchor=0;
		detailsHtmlText.append("<H2 align=\"left\">Details</H2>\n");
		for (ErrorInfo error: errors) {
			detailsHtmlText.append(formatError(error,Integer.valueOf(anchor).toString()));
		}
		detailsHtmlText.append(htmlFooter);
		//detailsHtmlEP.setText(detailsHtmlText.toString());
		
		HTMLDocument doc = (HTMLDocument)detailsHtmlEP.getEditorKit().createDefaultDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit)detailsHtmlEP.getEditorKit();
		StringReader reader = new StringReader(detailsHtmlText.toString());
		try {
			editorKit.read(reader,doc,0);
		} catch (Exception e) {
			System.out.println("Ooooops: "+e.getMessage());
			e.printStackTrace();
		}
		detailsHtmlEP.setDocument(doc);
	}
	
	/**
	 * Create the HTML representing an error
	 * 
	 * @param error The error to format
	 * @param anchor The anchor to set in the title
	 * @return The HTML string generated from the passed error
	 */
	private StringBuffer formatError(ErrorInfo error, String anchor) {
		if (error==null) {
			throw new IllegalArgumentException("Invalid null error");
		}
		StringBuffer ret = new StringBuffer("<H3>");
		ret.append(error.getTimestampStr());
		ret.append(" ");
		ret.append("<A name=\"Error");
		ret.append(anchor);
		ret.append("\">");
		ret.append(error.shortDescription);
		ret.append("</A></H3><P>");
		ret.append(error.detailedDescription);
		if (error.getInstances()>0) {
			ret.append("Received <B>");
			ret.append(error.getInstances());
			ret.append("</B> instances of the same error");
		}
		if (error.hasInfos()) {
			Collection<AdditionalInfo> addInfos = error.getAddInfos();
			ret.append("<DL>");
			for (AdditionalInfo info: addInfos) {
				ret.append("<DT><I>");
				ret.append(info.title);
				ret.append("</I></DT>");
				ret.append("<DD>");
				ret.append(info.description);
				ret.append("</DD");
			}
		}
		ret.append("<BR><BR>");
		return ret;
	}
	
	/**
	 * Receive the event generated by hyperlinks
	 * 
	 * @see javax.swing.event.HyperlinkListener
	 */
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType()!=HyperlinkEvent.EventType.ACTIVATED) {
			return;
		}
		// Refresh the details page
		updateDetailsHTML();
		detailsHtmlEP.scrollToReference(e.getDescription().substring(1));
	}
	
	/**
	 * @see java.awt.event.ActionListener
	 */
	public synchronized void actionPerformed(ActionEvent e) {
		if (e.getSource()==refreshTimer) {
			if (newErrors) {
				updateSummaryHTML();
				newErrors=false;
			}
		}
		if (e.getSource()==clearBtn) {
			synchronized(this) {
				errors.clear();
				updateSummaryHTML();
				updateDetailsHTML();
			}
		} else if (e.getSource()==saveBtn) {
			String dir=System.getProperty("user.dir");
			JFileChooser chooser;
			if (dir!=null) {
				chooser= new JFileChooser(dir);
			} else {
				chooser= new JFileChooser();
			}
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "HTML", "htm", "html");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showSaveDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       String fileName=chooser.getSelectedFile().getName();
		       if (!fileName.toLowerCase().endsWith(".htm") && !fileName.toLowerCase().endsWith(".html")) {
		    	   fileName+=".html";
		       }
		       FileOutputStream outF;
		       try {
		    	   outF= new FileOutputStream(fileName);
		    	   BufferedOutputStream outStream = new BufferedOutputStream(outF);
		    	   synchronized(this) {
		    		   outStream.write(summaryHtmlEP.getText().getBytes());
		    		   outStream.write(detailsHtmlEP.getText().getBytes());
		    		   outStream.flush();
		    	   }
		    	   outStream.close();
		       } catch (Throwable t) {
		    	   JOptionPane.showInternalMessageDialog(this, "Error creating "+fileName+"\n"+t.getMessage(), "Error saving file", JOptionPane.ERROR_MESSAGE);
		    	   t.printStackTrace(System.err);
		       }
		       
		    }
		}
	}
	
	/**
	 * Mark all the errors as seen
	 * 
	 * @see ComponentListener
	 */
	public synchronized void componentShown(ComponentEvent e) {
		updateSummaryHTML();
		splitP.setDividerLocation(splitP.getMaximumDividerLocation()*2/3);
		
		// ACK all the errors
		for (ErrorInfo error: errors) {
			error.acknowledge();
		}
    }

	/**
	 * @see ComponentListener
	 */
	public void componentResized(ComponentEvent e) {}
	
	/**
	 * @see ComponentListener
	 */
    public void componentMoved(ComponentEvent e){}
    
    /**
	 * @see ComponentListener
	 */
    public void componentHidden(ComponentEvent e)	{  }
}
