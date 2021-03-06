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

/** 
 * @author  caproni   
 * @version $Id: AxisPanel.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.axis;

import alma.Control.MountPackage.AxisMode;
import alma.ControlGUIErrType.wrappers.AcsJMountEx;
import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorInfo;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.mount.AxisStatusDefinition;
import alma.control.gui.antennamount.mount.ValueHolder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 * Shows the panel with the state of the axes and the relative buttons
 *
 */
public class AxisPanel extends JPanel implements Runnable, ActionListener {
	
	/**
	 * The time between 2 refreshes of the values in the table
	 */
	private static final int REFRESH_TIME=1500;
	
	/**
	 * The mount to read and set axis
	 */
	private Mount mount=null;
	
	/**
	 * The MountController.
	 * It is needed to call the stop()
	 */
	private MountController controller;
	
	/**
	 * The thread to refresh the GUI
	 */
	private volatile Thread thread=null;
	
	/**
	 * Signal the thread to terminate
	 */
	private volatile boolean terminateThread=false;
	
	/**
	 * The panel with the status of the axes
	 */
	private AxisStatusPanel axisStatusPanel = new AxisStatusPanel();
	
	/**
	 * The panel with the buttons to control azimuth
	 */
	private AxisButtonPanel azButtonPanel;
	
	/**
	 * The button to shutdown the Azimuth
	 */
	private JToggleButton shutdownAzB;
	
	/**
	 * The button to stand-by the Azimuth
	 */
	private JToggleButton standbyAzB;
	
	/**
	 * The button to drive the Azimuth in tracking mode
	 */
	private JToggleButton trackingAzB;
	
	/**
	 * The AZ status panel
	 */
	private AxisStatusPanel azStatusPanel = new AxisStatusPanel();
	
	/**
	 * The panel with the buttons to control elevation
	 */
	private AxisButtonPanel elButtonPanel;
	
	/**
	 * The buttons to shutdown the Elevation
	 */
	private JToggleButton shutdownElB;
	
	/**
	 * The buttons to standby the Elevation
	 */
	private JToggleButton standbyElB;
	
	/**
	 * The button to drive the Elevation in tracking mode
	 */
	private JToggleButton trackingElB;
	
	/**
	 * The elevation status panel
	 */
	private AxisStatusPanel elStatusPanel = new AxisStatusPanel();
	
	/**
	 * The panel with the axes
	 */
	private JPanel detailedPanel = new JPanel();
	
	/**
	 * The state of EL axis
	 */
	private ValueHolder<AxisMode> elAxisMode;
	
	/**
	 * The state of AZ axis
	 */
	private ValueHolder<AxisMode> azAxisMode;
	
	/**
	 * The AntennaRootPane to add errors and status messages
	 */
	private AntennaRootPane antennaRootP;
	
	/**
	 * Constructor 
	 * 
	 * @param rootP The AntennaRootPane
	 */
	public AxisPanel(AntennaRootPane rootP) {
		super();
		antennaRootP=rootP;
		initialize();
		enableWidgets(false);
	}
	
	/**
	 * Set the mount and the mount controller
	 * 
	 * @param ctr The mount controller
	 * @param mnt The mount
	 */
	public void setComponents(MountController ctr, Mount mnt) {
		mount=mnt;
		controller=ctr;
		if (mount!=null) {
			elAxisMode=mount.getElAxisMode();
			azAxisMode=mount.getAzAxisMode();
			// Start the thread
			terminateThread=false;
			if (thread==null) {
				thread = new Thread(this);
				thread.setName("AxisPanel");
				thread.setDaemon(true);
				antennaRootP.getHeartbeatChecker().register(thread);
				thread.start();
			}
		} else {
			// Stop the thread
			terminateThread=true;
			if (thread!=null) {
				thread.interrupt();
			}
			elAxisMode=azAxisMode=null;
		}
		enableWidgets(mount!=null);
	}
	
	/**
	 * Initialize the GUI
	 *
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		initializePanel();
		add(detailedPanel,BorderLayout.NORTH);
		
		// Connect buttons listeners
		shutdownAzB.addActionListener(this);
		shutdownElB.addActionListener(this);
		standbyAzB.addActionListener(this);
		standbyElB.addActionListener(this);
		trackingAzB.addActionListener(this);
		trackingElB.addActionListener(this);
		
	}
	
	/**
	 * Initialize the detailed panel i.e. the panel
	 * that allows to control both axis
	 *
	 * This panel is created only when the user switches to
	 * detailed mode
	 */
	private void initializePanel() {
		detailedPanel=new JPanel();
		detailedPanel.setLayout(new BoxLayout(detailedPanel,BoxLayout.Y_AXIS));
		
		JPanel azPanel=new JPanel();
		azPanel.setBorder(new TitledBorder("Azimuth"));
		azPanel.setLayout(new BoxLayout(azPanel,BoxLayout.X_AXIS));
		shutdownAzB=new JToggleButton("Shutdown");
		standbyAzB=new JToggleButton("Standby");
		trackingAzB=new JToggleButton("Autonomous");
		azButtonPanel=new AxisButtonPanel(shutdownAzB,standbyAzB,trackingAzB);
		azPanel.add(azButtonPanel);
		azPanel.add(azStatusPanel);
		detailedPanel.add(azPanel);
		
		JPanel elPanel=new JPanel();
		elPanel.setBorder(new TitledBorder("Elevation"));
		elPanel.setLayout(new BoxLayout(elPanel,BoxLayout.X_AXIS));
		shutdownElB=new JToggleButton("Shutdown");
		standbyElB=new JToggleButton("Standby");
		trackingElB=new JToggleButton("Autonomous");
		elButtonPanel=new AxisButtonPanel(shutdownElB,standbyElB,trackingElB);
		elPanel.add(elButtonPanel);
		elPanel.add(elStatusPanel);
		detailedPanel.add(elPanel);
	}
	
	/**
	 * Set the widgets to enabled/disable
	 * 
	 * @param enable if true enable the widgets 
	 */
	public void enableWidgets(final boolean enable) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				azButtonPanel.enableWidgets(enable);
				azStatusPanel.enableWidgets(enable);
				elButtonPanel.enableWidgets(enable);
				elStatusPanel.enableWidgets(enable);
				axisStatusPanel.enableWidgets(enable);		
			}
		});
		
	}
	
	/**
	 * Release all the resources and terminate the thread
	 *
	 */
	public void close() {
		setComponents(null,null);
	}
	
	// The thread to update the state of the axis in the GUI
	public void run() {
		while (!terminateThread) {
			antennaRootP.getHeartbeatChecker().ping(thread);
			try {
				// DO NOT move this sleep at the end
				// otherwise the continue in the case mount==null
				// will cause troubles
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException ie) {
				continue;
			}
		
			try {
				refresh();
			} catch (Throwable t) {
				AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
				ex.setContextDescription("Error refreshing the axis panel");
				ErrorInfo ei = new ErrorInfo("AxisPanel error","Error refreshing the axis panel",ex);
				antennaRootP.addError(ei);
			}
			
		}
		antennaRootP.getHeartbeatChecker().unregister(thread);
		thread=null;
	}
	
	private void refresh() {
		elStatusPanel.refreshPanel(elAxisMode);
		azStatusPanel.refreshPanel(azAxisMode);
		if (mount==null) {
			elButtonPanel.refreshButtonState(AxisStatusDefinition.UNKNOWN);
			azButtonPanel.refreshButtonState(AxisStatusDefinition.UNKNOWN);
			return;
		}
		
		// AZ
		if (azAxisMode==null || azAxisMode.getValue()==null) {
			azButtonPanel.refreshButtonState(AxisStatusDefinition.UNKNOWN);
		} else {
			azButtonPanel.refreshButtonState(AxisStatusDefinition.fromAxisMode(azAxisMode.getValue()));
		}
		
		// EL
		if (elAxisMode==null || elAxisMode.getValue()==null) {
			elButtonPanel.refreshButtonState(AxisStatusDefinition.UNKNOWN);
		} else {
			elButtonPanel.refreshButtonState(AxisStatusDefinition.fromAxisMode(elAxisMode.getValue()));
		}	
	}
	
	/**
	 * @see java.awt.event.ActionListener
	 * 
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==shutdownAzB) {
			// SHUTDOWN AZ
			setAzStatus(AxisMode.SHUTDOWN_MODE);
		} else if (e.getSource()==shutdownElB) {
			// SHUTDOWN EL
			setElStatus(AxisMode.SHUTDOWN_MODE);
		} else if (e.getSource()==standbyAzB) {
			// STANDBY AZ
			setAzStatus(AxisMode.STANDBY_MODE);
		} else if (e.getSource()==standbyElB) {
			// STANDBY EL
			setElStatus(AxisMode.STANDBY_MODE);
		} else if (e.getSource()==trackingAzB) {
			// TRACK AZ
			setAzStatus(AxisMode.AUTONOMOUS_MODE);
		} else if (e.getSource()==trackingElB) {
			// TRACK EL
			setElStatus(AxisMode.AUTONOMOUS_MODE);
		} else {
			System.err.println("Unknown event from "+e);
		}
	}
	
	/**
	 * Set the mode of the azimuth
	 * @param mode The new mode
	 */
	private void setAzStatus(AxisMode mode) {
		mount.setAzStatus(mode);
	}
	
	/**
	 * Set the mode of the azimuth
	 * @param mode The new mode
	 */
	private void setElStatus(AxisMode mode) {
		mount.setElStatus(mode);
	}
}
