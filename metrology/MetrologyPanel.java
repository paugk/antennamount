/*
 * ALMA - Atacama Large Millimiter Array (c) European Southern Observatory, 2010
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
package alma.control.gui.antennamount.metrology;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorInfo;
import alma.control.gui.antennamount.mount.ACSComponentsManager.AntennaType;
import alma.control.gui.antennamount.mount.IMetrology;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.utils.ValueState;

/**
 * The panel of the metrology tab.
 * <P>
 * The panel is composed of 
 * <ul>
 * 	<LI>the control widgets on top
 * 	<LI>the status table at the bottom
 * </UL>
 * 
 * @author acaproni
 *
 */
public class MetrologyPanel extends JPanel implements Runnable {
	/**
	 * The label shown when the mount is disconnected
	 * <P>
	 * This label is shown only at the beginning when no mount has been connected yet.
	 * <BR>
	 * When a connected mount is disconnected, then the last used widget is visible but disabled.
	 */
	private final JLabel disconnectedLbl = new JLabel("Metrology is unavailble: mount disconnected");
	
	/**
	 * The table with the status bits
	 */
	private MetrologyTable table;
	
	/**
	 * The widget to set and read the state of the metrology
	 */
	private MetrologyModePnl metrologyModeControl=null;
	
	/**
	 * The connected antenna type
	 */
	private AntennaType aType=null;
	
	/**
	 * The metrology
	 */
	private IMetrology metrology=null;
	
	/**
	 * If it is <code>true</code>, the panel shows the field to change the metrology
	 */
	private boolean interactive;
	
	/**
	 * The time between 2 refreshes of the table
	 */
	private static final int REFRESH_TIME=5000;
	
	/**
	 * Signal the thread to terminate
	 */
	private volatile boolean terminateThread;
	
	/**
	 * The container for the widgets
	 */
	private final JPanel modePnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
	
	/**
	 * The thread to refresh the table
	 */
	private Thread thread=null;
	
	/**
	 * The 	{@link AntennaRootPane} to add errors to the error tab
	 */
	private AntennaRootPane antennaRootPane;

	/**
	 * Constructor
	 */
	public MetrologyPanel(AntennaRootPane antennaRootPane, boolean interactive) {
		if (antennaRootPane==null) {
			throw new IllegalArgumentException("The root pane can't be null");
		}
		this.antennaRootPane=antennaRootPane;
		this.interactive=interactive;
		initialize(antennaRootPane);
		setComponents(null);
	}

	/**
	 * Initialize the GUI
	 * 
	 * @param rootPane The root pane
	 */
	private void initialize(AntennaRootPane rootPane) {
		setLayout(new BorderLayout());
		table = new MetrologyTable(rootPane);
		setModeWidget();
		add(modePnl, BorderLayout.NORTH);
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tablePanel.add(table, BorderLayout.CENTER);

		add(tablePanel, BorderLayout.CENTER);
	}
	
	/**
	 * Set the mount for reading/setting the metrology
	 * <P>
	 * This method displays the widgets needed by the type of the connected mount
	 *   
	 * @param mnt The mount
	 */
	public void setComponents(Mount mnt) {
		table.setComponents(mnt);
		if (mnt!=null) {
			metrology=mnt.getMetrology();
			aType=mnt.getMountType();
			setModeWidget();
			// Start the thread
			terminateThread=false;
			thread = new Thread(this);
			thread.setDaemon(true);
			thread.setName(this.getClass().getName());
			antennaRootPane.getHeartbeatChecker().register(thread);
			thread.start();
		} else {
			metrology=null;
			aType=null;
			metrologyModeControl=null;
			// Stop the thread
			terminateThread=true;
			if (thread!=null) {
				thread.interrupt();
			}
			setModeWidget();
		}
	}
	
	/**
	 * Set the mode widget
	 */
	private void setModeWidget() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Empty the container
				modePnl.removeAll();
				modePnl.validate();
				// Add the mode panel
				if (aType!=null) {
					metrologyModeControl = new MetrologyModePnl(interactive,metrology,aType);
					modePnl.add(metrologyModeControl);
				} else {
					modePnl.add(disconnectedLbl);
				}
				modePnl.validate();
				validate();
			}
		});
	}
	
	/**
	 * Close the thread and release all the resources
	 */
	public void close() {
		setComponents(null);
	}
	
	/**
	 * The thread to update the values in the table
	 */
	@Override
	public void run() {

	while (!terminateThread) {
			antennaRootPane.getHeartbeatChecker().ping(thread);
			
			if (metrologyModeControl!=null && metrology!=null) {
				metrologyModeControl.refresh(metrology.getMode());
			} else {
				if (metrologyModeControl!=null) {
					metrologyModeControl.refresh(null);
				}
			}
			ValueState st=table.refresh();
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException ie) {
				continue;
			}
			

//			titleSetter.tabTitleState(st,"Metrology",errorTabComponent,false);
//			fireTableDataChanged();
		}
//		updateError();
		antennaRootPane.getHeartbeatChecker().unregister(thread);
		thread=null;
	}
}
