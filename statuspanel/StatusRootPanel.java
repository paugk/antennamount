/*
 *    ALMA - Atacama Large Millimiter Array
 *    (c) European Southern Observatory, 2002
 *    Copyright by ESO (in the framework of the ALMA collaboration)
 *    and Cosylab 2002, All rights reserved
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 *    MA 02111-1307  USA
 */
package alma.control.gui.antennamount.statuspanel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;

/**
 * The root panel for the antenna status GUI
 * 
 * @author acaproni
 *
 */
public class StatusRootPanel extends AntennaRootPane {

	// The status panel (default view)
	private AntennaTrackingPanel trackingPnl;
	
	/**
	 * Empty constructor
	 */
	public StatusRootPanel() {
		super();
		initialize();
	}
	
	/**
	 * Constructor
	 * 
	 * @param frame The frame showing this panel
	 */
	public StatusRootPanel(JFrame frame) {
		super(frame);
		initialize();
	}
	
	/**
	 * Init the GUI
	 *
	 */
	protected void initialize() {
		super.initialize();
		trackingPnl = new AntennaTrackingPanel(this);
		getContentPane().add(trackingPnl,BorderLayout.CENTER);
		setPreferredSize(new Dimension(600,350));
	}
	
	/**
	 * Free all the resources
	 *
	 */
	public synchronized void close() {
		trackingPnl.close();
		theToolbar.close();
		super.close();
	}
	
	/**
	 * Method used by the plugin interface in EXEC.
	 * Start the application 
	 * @see alma.exec.extension.subsystemplugin.IPauseResume
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		
	}
	
	/**
	 * Method used by the plugin interface in EXEC.
	 * Stop the application 
	 * @see alma.exec.extension.subsystemplugin.IPauseResume
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		close();
	}

	/**
	 * Method used by the plugin interface in EXEC.
	 * Pause the application 
	 * @see alma.exec.extension.subsystemplugin.IPauseResume
	 * 
	 * @throws Exception
	 */
	public void pause() throws Exception {
		if (mountGUI!=null) {
			mountGUI.setPaused(true);
			addStatusMessage("Paused",false);
		}
	}
	
	/**
	 * Method used by the plugin interface in EXEC.
	 * Resume the application 
	 * @see alma.exec.extension.subsystemplugin.IPauseResume
	 * 
	 * @throws Exception
	 */
	public void resume() throws Exception {
		if (mountGUI!=null) {
			mountGUI.setPaused(false);
			addStatusMessage("Unpaused",false);
		}
	}
	
	/**
	 * Run in restricted mode
	 * 
	 * @see alma.exec.extension.subsystemplugin.SubsystemPlugin
	 */
	public boolean runRestricted (boolean restricted) throws Exception {
		trackingPnl.enableWidgets(!restricted);
		return restricted;
	}
	
	/**
	 * @see AntennaRootPane
	 */
	public void setComponents(MountController ctr, Mount mnt) {
		trackingPnl.setComponents(ctr, mnt);
		theToolbar.setComponents(ctr, mnt);
	}
}
