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
package alma.control.gui.antennamount.shutter;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.ShutterCommon;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;

/**
 * The panel with the buttons to open/close the shutter.
 * The only active button is that of the possible action to perform on the
 * shutter (for example if the shutter is OPEN, only the close button
 * is enabled).
 * If the state is unknown (or the shutter is in error state) both the buttons
 * are enabled
 *  
 * @author acaproni
 *
 */
public class ShutterPanel extends JPanel implements ActionListener, Runnable {
	/**
	 * The states of the shutter.
	 * 
	 * Any irregular (error) or transitory (like shutter moving) state is 
	 * mapped as UNKNOWN 
	 */
	private enum ShutterState {
		OPEN,
		CLOSED,
		UNKNOWN // Unknown or error state
	}
	
	// The button to open and close the shutter
	private JButton openBtn = new JButton("Open");
	private JButton closeBtn = new JButton("Close");
	
	// Signal the thread to terminate
	private volatile boolean terminateThread;
	
	// The thread to refresh the panel
	private Thread thread;
	
	// The time between 2 refresh of the panel
	private static final int REFRESH_TIME = 1500;
	
	
	// The mount to read and set axis
	private Mount mount=null;
	
	// The shutter
	private ShutterCommon shutter;
	
	/**
	 * The AntennaRootPane
	 */
	private final AntennaRootPane rootP;
	/**
	 * Constructor
	 */
	public ShutterPanel(AntennaRootPane rootP) {
		if (rootP==null) {
			throw new IllegalArgumentException("The AntennaRootPane can't be null");
		}
		this.rootP=rootP;
		initialize();
	}
	
	/**
	 * Init the GUI
	 */
	private void initialize() {
		setBorder(BorderFactory.createTitledBorder("Shutter"));
		setLayout(new FlowLayout());
		add(openBtn);
		add(closeBtn);
		setShutterState(ShutterState.UNKNOWN);
		openBtn.setEnabled(false);
		closeBtn.setEnabled(false);
	}
	
	/**
	 * Changes the appearance of the buttons depending on the
	 * state of the shutter
	 * 
	 * @param newState The state of the shutter
	 */
	private void setShutterState(final ShutterState state) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (state==null || shutter==null) {
					closeBtn.setEnabled(false);
					openBtn.setEnabled(false);
					return;
				}
				switch (state) {
				case OPEN: {
					closeBtn.setEnabled(true);
					openBtn.setEnabled(false);
					return;
				}
				case CLOSED: {
					closeBtn.setEnabled(false);
					openBtn.setEnabled(true);
					return;
				}
				case UNKNOWN: {
					closeBtn.setEnabled(true);
					openBtn.setEnabled(true);
					return;
				}	
				}		
			}
		});
	}
	
	/**
	 * Map the state of the shutter of the Mount in a valid state
	 * for the panel 
	 *  
	 * @param shutter The shutter
	 */
	private void updateShutterState() {
		if (shutter==null || mount==null) {
			setShutterState(ShutterState.UNKNOWN);
			return;
		}
		if (!shutter.isOk()) {
			setShutterState(ShutterState.UNKNOWN);
		}
		if (shutter.isOpen()) {
			setShutterState(ShutterState.OPEN);
		} else if (shutter.isClosed()) {
			setShutterState(ShutterState.CLOSED);
		} else {
			setShutterState(ShutterState.UNKNOWN);
		}
	}
	
	/**
	 * Set the mount and the mount controller
	 * 
	 * @param ctr The mount controller
	 * @param mnt The mount
	 */
	public void setComponents(MountController ctr, Mount mnt) {
		mount=mnt;
		if (mount==null) {
			terminateThread=true;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					openBtn.setEnabled(false);
					closeBtn.setEnabled(false);
					openBtn.removeActionListener(ShutterPanel.this);
					closeBtn.removeActionListener(ShutterPanel.this);
				}
			});
		} else {
			shutter = mount.getShutter();
			terminateThread=false;
			thread = new Thread(this,"ShutterPanel");
			rootP.getHeartbeatChecker().register(thread);
			thread.setDaemon(true);
			thread.start();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					openBtn.addActionListener(ShutterPanel.this);
					closeBtn.addActionListener(ShutterPanel.this);
					openBtn.setEnabled(true);
					closeBtn.setEnabled(true);
				}
			});
		}
	}
	
	/**
	 * @see ActionListener
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		if (mount==null) {
			return;
		}
		if (e.getSource()==openBtn) {
			mount.setShutter(true);
		} else if (e.getSource()==closeBtn) {
			mount.setShutter(false);
		} else {
			throw new UnsupportedOperationException("Action for this source is unsupported: "+e);
		}
	}
	
	/**
	 * @see Runnable
	 */
	public void run() {
		while (!terminateThread) {
			rootP.getHeartbeatChecker().ping(thread);
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException ie) {
				continue;
			}
			updateShutterState();
		}
		rootP.getHeartbeatChecker().unregister(thread);
		shutter=null;
		thread=null;
	}
}
