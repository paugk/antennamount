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
package alma.control.gui.antennamount.toolbar;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JPanel;

import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorInfo;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;

/**
 * The status bar in the upper side of the window.
 * <P>
 * The panel shows several status indicators that can be useful to have always
 * visible.
 * 
 * @author acaproni
 *
 */
public class Toolbar extends JPanel implements Runnable {
	
	/**
	 * The Mount Controller to read values from
	 */
	private Mount mount;
	
	/**
	 * The MountController to read values from
	 */
	private MountController controller;
	
	/**
	 * The list of widgets shown inside the toolbar
	 */
	private final Vector<IToolbarWidget> widgets = new Vector<IToolbarWidget>(); // A vector to perform common ops on widgets
	
	
	private BooleanWidget inPosition=null;
	private AxesWidget axesStatus=null;
	private BrakeWidget azBrake=null;
	private BrakeWidget elBrake=null;
	private BooleanWidget abmPM=null;
	private ShutterWidget shutter;
	private TimeWidget timeWidget = new TimeWidget();
	
	/**
	 * The AntennaRootPane
	 */
	private final AntennaRootPane rootPane;
	
	/**
	 * The widget showing the time to set when the antenna is tracking
	 * a source
	 */
	private TimeToSetWidget timeToSetWidget;
	
	/**
	 * The thread to update the widgets in the toolbar
	 */
	private Thread thread=null;
	
	/**
	 * Signal the thread to terminate
	 */
	private volatile boolean terminateThread;
	
	/**
	 * msecs between two updates of the widgets
	 */
	private static final int REFRESH_INTERVAL = 1000;
	
	/**
	 * Constructor
	 * 
	 * @param items The items to show in the toolbar
	 * @param rootP The AntennaRootPane
	 */
	public Toolbar(AntennaRootPane rootP) {
		if (rootP==null) {
			throw new IllegalArgumentException("Invalid null AntennaRootPane");
		}
		rootPane=rootP;
		// Initialize the GUI
		initialize(rootP);
		
		terminateThread=false;
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.setName("Toolbar");
		thread.start();
		
	}
	
	/**
	 * Enable/Disable the toolbar.
	 * 
	 * Enable/disable all the widgets displayed inside the toolbar.
	 * 
	 * @param enabled If true enable the widget
	 */
	public void enableWidget(boolean enabled) {
		for (IToolbarWidget widget: widgets) {
			widget.enableWidget(enabled);
		}
	}
	
	/**
	 * Initialize the toolbar
	 */
	private void initialize(AntennaRootPane rootP) {
		setLayout(new BorderLayout());
		
		// Build all the widgets
		inPosition=new BooleanWidget(null, "On source", true, rootP);
		axesStatus=new AxesWidget("Axes", true, rootP);
		shutter = new ShutterWidget("Shutter",true, rootP);
		azBrake=new BrakeWidget(null, "AZ brake", true, rootP);
		elBrake=new BrakeWidget(null, "EL brake", true, rootP);
		abmPM=new BooleanWidget(null, "ABM PM", true, rootP);
		timeToSetWidget = new TimeToSetWidget("Time to set",false,rootP);
		
		// The widgets at the left side
		JPanel leftPnl = new JPanel(new GridLayout(2,3));
		leftPnl.add(inPosition);
		leftPnl.add(axesStatus);
		leftPnl.add(shutter);
		leftPnl.add(azBrake);
		leftPnl.add(elBrake);
		leftPnl.add(abmPM);
		add(leftPnl,BorderLayout.WEST);
		
		// The widgets at the right side
		JPanel rightPnl = new JPanel(new BorderLayout());
		rightPnl.add(timeWidget,BorderLayout.NORTH);
		rightPnl.add(timeToSetWidget,BorderLayout.SOUTH);
		add(rightPnl,BorderLayout.EAST);
		
		
		// Add the widgets to the vector
		widgets.add(inPosition);
		widgets.add(axesStatus);
		widgets.add(azBrake);
		widgets.add(elBrake);
		widgets.add(abmPM);
		widgets.add(shutter);
		widgets.add(timeWidget);
		widgets.add(timeToSetWidget);
	}
	
	/**
     * @see AntennaRootPane
     */
    public void setComponents(MountController ctr, Mount mnt) {
    	mount=mnt;
    	controller=ctr;
    	
    	// Connect the widgets of the mount
    	if (mount!=null) {
    		azBrake.setValue(mount.getAzBrake());
    		elBrake.setValue(mount.getElBrake());
    		inPosition.setValue(controller.getIsOnSource());
    		abmPM.setValue(mount.getAbmPointingModel());
    		shutter.setShutter(mount.getShutter());
    		timeToSetWidget.setValues(controller.getTimeToSet(),controller.getIsOnSource());
    	} else {
    		azBrake.setValue(null);
    		elBrake.setValue(null);
    		inPosition.setValue(null);
    		abmPM.setValue(null);
    		shutter.setShutter(null);
    		timeToSetWidget.setValues(null,null);
    	}
    	axesStatus.setMount(mount);
    	
    	enableWidget(ctr!=null && mount!=null);
    }
    
    /**
     * The thread to update the values in the widgets
     */
    public void run() {
    	rootPane.getHeartbeatChecker().register(thread);
    	while (!terminateThread) {
    		rootPane.getHeartbeatChecker().ping(thread);
    		try {
    			refreshWidgets();
    		} catch (Throwable t) {
    			AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
				ex.setContextDescription("Error refreshing widgets");
				ErrorInfo ei = new ErrorInfo("Toolbar error","Error refreshing widgets",ex);
				rootPane.addError(ei);
    		}
    		
    		// sleep between 2 iterations
    		if (terminateThread) {
    			return;
    		}
    		try {
    			Thread.sleep(REFRESH_INTERVAL);
    		} catch (Exception e) {}
    	}
    	rootPane.getHeartbeatChecker().unregister(thread);
    }
    
    /**
     * Refresh all the widgets displayed by the toolbar
     */
    private void refreshWidgets() {
    	// Refresh the widgets
		for (IToolbarWidget item: widgets) {
			item.refresh();
			item.refreshIcon();
		}
    }
    
    /**
	 * Free all the resources
	 *
	 */
    public void close() {
   		setComponents(null, null);
   		terminateThread=true;
    }
}
