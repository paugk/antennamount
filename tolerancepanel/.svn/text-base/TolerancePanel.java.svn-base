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
package alma.control.gui.antennamount.tolerancepanel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.utils.ValueDisplayer;

/**
 * The panel to read/set the tolerance
 * 
 * @author acaproni
 *
 */
public class TolerancePanel extends JPanel implements Runnable, ActionListener {
	
	/**
	 * To convert arcsec to radian:
	 * the arcsecond is 1/1296000 of a circle, or (Math.PI/648000) radians
	 */
	public static final double CONVERSION_FACTOR = Math.PI/(double)648000.0;
	
	// The widgets
	private JLabel actualLbl = new JLabel("Actual: ");
	private JLabel commandLbl = new JLabel("Commanded: ");
	private JTextField actualToleranceTF = new JTextField(ValueDisplayer.NOT_AVAILABLE,5);
	private JTextField newToleranceTF = new JTextField("0.0",5);
	private JButton applyB = new JButton("Apply");
	
	/**
	 * Actual tolerance read from Mount
	 */
	private ValueHolder<Double>tolerance=null;
	
	/**
	 * The mount component to read/set tolerance
	 */
	private Mount mount=null;
	
	/**
	 * The AntennaRootPane to add errors and status messages
	 */
	private AntennaRootPane antennaRootP;

	/**
	 * Constructor
	 * 
	 * @param rootP The AntennaRootPane
	 */
	public TolerancePanel(AntennaRootPane rootP) {
		if (rootP==null) {
			throw new IllegalArgumentException("Invalid null AntennaRootPane");
		}
		antennaRootP=rootP;
		initialize();
		enableWidgets(false);
	}
	
	/**
	 * The thread to update the value of the actual tolerance
	 */
	private Thread thread=null;
	
	/**
	 * Signal the thread to terminate
	 */
	private volatile boolean terminateThread=false;
	
	/**
	 * The interval between 2 refreshes
	 */
	private static final int UPDATE_INTERVAL=1000;
	
	/**
	 * Initialize the panel
	 */
	private void initialize() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(actualLbl);
		add(actualToleranceTF);
		add(commandLbl);
		add(newToleranceTF);
		add(applyB);
		
		actualToleranceTF.setEditable(false);
		
		applyB.addActionListener(this);
	}
	
	/**
	 * Enable/disable the widgets
	 * 
	 * @param enable If true enable the widgets
	 */
	public void enableWidgets(final boolean enable) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				actualLbl.setEnabled(enable);
				commandLbl.setEnabled(enable);
				actualToleranceTF.setEnabled(enable);
				newToleranceTF.setEnabled(enable);
				applyB.setEnabled(enable);
			}
		});
	}
	
	/**
	 * Release the resources
	 */
	public void close() {
		setMount(null);
	}
	
	/**
	 * Set the mount component
	 * 
	 * @param mnt
	 */
	public void setMount(Mount mnt) {
		mount=mnt;
		enableWidgets(mnt!=null);
		if (mount!=null) {
			tolerance=mount.getTolerance();
			// Start the thread
			terminateThread=false;
			thread=new Thread(this);
			thread.setDaemon(true);
			thread.setName("ToleranceThread");
			thread.start();
			antennaRootP.getHeartbeatChecker().register(thread);
		} else {
			
			// Stop the thread
			terminateThread=true;
			
			tolerance=null;
		}
	}
	
	/**
	 * The thread to refresh the actual tolerance.
	 * The text in the text field is updated only if changed
	 */
	public void run() {
		while (!terminateThread) {
			antennaRootP.getHeartbeatChecker().ping(thread);
			if (tolerance==null ||tolerance.getValue()==null ) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						actualToleranceTF.setText(ValueDisplayer.NOT_AVAILABLE);		
					}
				});
			} else {
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						String text = actualToleranceTF.getText();
						String newActVal=String.format("%03.3f", radiansToArcsec(tolerance.getValue()));
						if (!text.equals(newActVal)) {
							actualToleranceTF.setText(newActVal);
						}
					}
				});
			}
			try {
				Thread.sleep(UPDATE_INTERVAL);
			} catch (Exception e) {
				continue;
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				actualToleranceTF.setText(ValueDisplayer.NOT_AVAILABLE);		
			}
		});
		antennaRootP.getHeartbeatChecker().unregister(thread);
		tolerance=null;
		thread=null;
	}
	
	/**
	 * @see ActionListener
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==applyB && mount!=null) {
			double d;
			try {
				d=Double.parseDouble(newToleranceTF.getText());
				newToleranceTF.setForeground(Color.BLACK);
			} catch (Throwable t) {
				// Malformed double ==> nothing to do
				antennaRootP.addStatusMessage("Malformed tolerance value: "+newToleranceTF.getText(),true);
				newToleranceTF.setForeground(Color.RED);
				return;
			}
			if (d<0 || d>=60) {
				antennaRootP.addStatusMessage("Tolerance in arcsec (0=&lt;tolerance&lt;60) ",true);
				newToleranceTF.setForeground(Color.RED);
				return;
			}
			// Convert tolerance from arcsec to radians
			double rad = arcsecToRadians(d);
			mount.setTolerance(rad);
		}
	}
	
	/**
	 * Convert an arcsec to radians
	 * 
	 * @param arcsec The arcsec to convert to radians
	 * @return The radians 
	 */
	private double arcsecToRadians(double arcsec) {
		return arcsec*CONVERSION_FACTOR;
	}
	
	/**
	 * Convert a radians in arcsec
	 * 
	 * @param rad The radians to convert to arcsec
	 * @return The arcsec
	 */
	private double radiansToArcsec(double rad) {
		return (double)(((double)1/CONVERSION_FACTOR))*rad;
	}
	
}
