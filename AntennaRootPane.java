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

package alma.control.gui.antennamount;

import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.ControlGUIErrType.wrappers.AcsJUnsupportedAntennaTypeEx;
import alma.acs.exceptions.AcsJException;
import alma.acs.gui.util.panel.IPanel;
import alma.acs.logging.AcsLogLevel;
import alma.acs.container.ContainerServices;

import alma.common.gui.components.selector.SelectorComponentEvent;
import alma.common.gui.components.selector.SelectorComponentListener;
import alma.control.gui.antennamount.coordtables.CommonCoordsTable;
import alma.control.gui.antennamount.debug.DebugFileLogger;
import alma.control.gui.antennamount.dialogs.SelectAntennaMountDlg;
import alma.control.gui.antennamount.errortab.ErrorInfo;
import alma.control.gui.antennamount.errortab.ErrorTabbedPane;

import alma.control.gui.antennamount.mount.ACSComponentsManager;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountConnectionListener;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.mount.MountOperationListener;
import alma.control.gui.antennamount.mount.vertex.MountVertex;
import alma.control.gui.antennamount.mount.vertexLLama.MountVertexLLama;
import alma.control.gui.antennamount.mount.aca.MountACA;
import alma.control.gui.antennamount.mount.a7m.MountA7M;
import alma.control.gui.antennamount.mount.aem.MountAEM;
import alma.control.gui.antennamount.toolbar.Toolbar;
import alma.control.gui.antennamount.utils.ValueDisplayer;

import alma.exec.extension.subsystemplugin.IPauseResume;
import alma.exec.extension.subsystemplugin.PluginContainerServices;
import alma.exec.extension.subsystemplugin.SubsystemPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * The content of the Antenna mount/tracking GUI
 *
 * @author acaproni
 */
public abstract class AntennaRootPane extends JRootPane  
	implements MenuListener, 
		ActionListener, 
		IPauseResume, 
		SubsystemPlugin, 
		SelectorComponentListener,
		MountOperationListener,
		IPanel {
	
	/**
	 * The menu bar
	 */
    protected JMenuBar antennaMenuBar = new JMenuBar();
    
    /**
     * The plugin container services
     */
    private PluginContainerServices pluginCS=null;
    
    /**
     * The plugin container services
     */
    private ContainerServices acsCS=null;
    
    /**
     * An object to check and report if all the thread are runnign as expected
     */
    protected final HeartbeatChecker hbChecker = new HeartbeatChecker();
    
    /**
     * The window showing this component
     * 
     * It is <code>null</code> when executed as OMC plugin
     */
    private JFrame mountFrame=null;
    
    /**
     * The object getting values out of the Mount component
     */
    protected Mount mountGUI;
    
    /**
     * The object getting values out of the MountController component
     */
    protected MountController controllerGUI;
    
    /**
     * The status line at the bottom of the page
     */
    private StatusLine statusLine=new StatusLine(hbChecker);
    
    /**
     * The tab to show errors
     */
    protected ErrorTabbedPane errorTab = new ErrorTabbedPane();
    
    /**
     * The connection listener
     */
    private MountConnectionListener connectionListener = new VisibleConnectionStatus(statusLine);
    
    /**
     * The File menu
     */
    private JMenu fileMenu = new JMenu("File");
    
    /**
     * The menu item to connect to a mount
     */
	private JMenuItem connectMountMenuItem=new JMenuItem("Select mount...");
	
	/**
     * The menu item to disconnect a mount
     */
	private JMenuItem releaseMountMenuItem=new JMenuItem("Disconnect mount");
	
	/**
	 * The menu item to close the application
	 */
	private JMenuItem exitMenuItem=new JMenuItem("Exit");
    
    /**
     * The table of coordinates at the top of the panel
     */
    protected CommonCoordsTable coordsTbl;
    
    /**
     * The toolbar displayed in the upper side of the main window
     */
    protected Toolbar theToolbar=null;
    
    /**
     * The object managing ACS components
     */
    private ACSComponentsManager manager=null;
    
    /**
     * It is <code>true</code> if the panel runs in debug mode.
     * <P>
     * To run the panel in the debug mode, a JVM property must be explicitly set.
     * @see AntennaRootPane#debubModeOnPropertyName
     */
    public final boolean debugModeOn;
    
    /**
     * The name of the JVM property to run the panel in debug mode
     */
    public static final String debubModeOnPropertyName="alma.control.mountpanel.debug";
    
    /**
     * The object to log debug messages on a file.
     * <P>
     * It is <code>null</code> when the application deoes not run
     * in debug mode
     */
    private final DebugFileLogger debugLogger;
    
	/**
	 * Empty constructor
	 * (called by OMC to run as plugin)
	 *
	 */
	protected AntennaRootPane() {
		super();
		debugModeOn=Boolean.getBoolean(debubModeOnPropertyName);
		if (debugModeOn) {
			debugLogger=new DebugFileLogger();
		} else {
			debugLogger=null;
		}
	}
	
	/**
	 * Constructor
	 * (executed while running stand-alone)
	 * 
	 * @param frame The frame of the application
	 */
	protected AntennaRootPane(JFrame frame) {
		super();
		debugModeOn=Boolean.getBoolean(debubModeOnPropertyName);
		if (debugModeOn) {
			debugLogger=new DebugFileLogger();
		} else {
			debugLogger=null;
		}
		if (frame==null) {
			throw new IllegalArgumentException("Invalid null AntMountFrame in construcotr");
		}
		mountFrame=frame;
	}
	
	/**
	 * Initialize the GUI
	 *
	 */
	protected void initialize() {
		buildMenubar();
		
		// Do not change the BorderLayout because it is used by MountRootPanel to add the 
		// stop button on the left side of the coordinates table
		JPanel pnl = new JPanel(new BorderLayout());
		
		Box upperBox = new Box(BoxLayout.Y_AXIS);
		theToolbar = new Toolbar(this);
		upperBox.add(theToolbar);
		
		upperBox.add(Box.createRigidArea(new Dimension(0,5)));
		upperBox.add(new JSeparator());
		upperBox.add(Box.createRigidArea(new Dimension(0,5)));
		
		// Add the status bar (toolbar) on top
		pnl.add(upperBox,BorderLayout.NORTH);
		
		// Add the coordinate table on top
		JPanel tablePnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
    	coordsTbl = new CommonCoordsTable(this);
    	coordsTbl.setBorder(BorderFactory.createLineBorder(Color.black));
    	tablePnl.add(coordsTbl,BorderLayout.CENTER);
    	pnl.add(tablePnl,BorderLayout.CENTER);
    	
    	getContentPane().add(pnl, BorderLayout.NORTH);
		// Add the status line at the bottom
		getContentPane().add(statusLine, BorderLayout.SOUTH);
		addStatusMessage("Application started",false);

	}
	
	/**
     * Add the menu bar
     */
    private void buildMenubar() {
    	fileMenu.setEnabled(true);
		fileMenu.setVisible(true);
		fileMenu.add(connectMountMenuItem);
		fileMenu.add(releaseMountMenuItem);
		if (!isOMCPlugin()) {
			fileMenu.add(new JSeparator());
			fileMenu.add(exitMenuItem);
		}
		antennaMenuBar.add(fileMenu);
		fileMenu.addMenuListener(this);
    	
    	// connect listeners
    	fileMenu.addMenuListener(this);
    	connectMountMenuItem.addActionListener(this);
    	releaseMountMenuItem.addActionListener(this);
    	exitMenuItem.addActionListener(this);
    	
    	// Add the menubar
    	setJMenuBar(antennaMenuBar);
     }
	
	/**
	 * Set the plugin container services
	 * 
	 * @see alma.exec.extension.subsystemplugin.SubsystemPlugin
	 */
	public void setServices (PluginContainerServices ctrl) {
		// Hides the File menu item, not needed while running inside OMC
		fileMenu.setVisible(false);
		pluginCS=ctrl;
		setACSContainerServices(pluginCS);
	}
	
	/**
	 * Run in restricted mode
	 * 
	 * @see alma.exec.extension.subsystemplugin.SubsystemPlugin
	 */
	public abstract boolean runRestricted (boolean restricted) throws Exception;
	
	/**
	 * Method used by the plugin interface in EXEC.
	 * Start the application 
	 * @see alma.exec.extension.subsystemplugin.IPauseResume
	 * 
	 * @throws Exception
	 */
	public abstract void start() throws Exception;
	
	/**
	 * Method used by the plugin interface in EXEC.
	 * Stop the application 
	 * @see alma.exec.extension.subsystemplugin.IPauseResume
	 * 
	 * @throws Exception
	 */
	public abstract void stop() throws Exception;
	
	/**
	 * Method used by the plugin interface in EXEC.
	 * Pause the application 
	 * @see alma.exec.extension.subsystemplugin.IPauseResume
	 * 
	 * @throws Exception
	 */
	public abstract void pause() throws Exception;
	
	/**
	 * Method used by the plugin interface in EXEC.
	 * Resume the application 
	 * @see alma.exec.extension.subsystemplugin.IPauseResume
	 * 
	 * @throws Exception
	 */
	public abstract void resume() throws Exception;
	
	/**
	 * Set the ACS Container Services
	 * 
	 * @param cs The container services
	 */
	public void setACSContainerServices(ContainerServices cs) {
		if (cs==null) {
			throw new IllegalArgumentException("Invalid null ContainerServices");
		}
		acsCS=cs;
	}
	
	/**
	 * Free all the resources
	 *
	 */
	public synchronized void close() {
		addStatusMessage("Closing",false);
		hbChecker.close();
		coordsTbl.close();
		theToolbar.close();
		disconnectComponents();
		acsCS = null;
		pluginCS = null;
	}
	
	/**
	 * Set the mount and mount controller used by the panels
	 * 
	 * @param ctr The MountController (can be null)
	 * @param mnt The Mount (can be null)
	 */
	public abstract void setComponents(MountController ctr, Mount mnt);
	
	
	/**
	 * Disconnect the mount and the controller
	 *
	 */
	public void disconnectComponents() {
		statusLine.startProgressAnimation();
		setComponents(null,null);
		coordsTbl.setComponents(null,null);
		if (mountGUI!=null) {
			String name=mountGUI.getComponentName();
			if (debugModeOn) {
				debugLogger.log("Closing mountGUI: "+name);
			}
			mountGUI.close();
			connectionListener.componentDisconnected(name);
			mountGUI=null;
		}
		if (controllerGUI!=null) {
			String name=controllerGUI.getComponentName();
			if (debugModeOn) {
				debugLogger.log("Closing controllerGUI: "+name);
			}
			controllerGUI.close();
			connectionListener.componentDisconnected(name);
			controllerGUI=null;
		}
		if (manager!=null) {
			if (debugModeOn) {
				debugLogger.log("Closing manager ");
			}
			manager.close();
			manager=null;
		}
		
		if (!isOMCPlugin()) {
			mountFrame.setTitle("Mount panel");
		}
		statusLine.stopProgressAnimation();
		addStatusMessage("Antenna disconnected",false);
	}
	
	/**
	 * Ask the manager to connect the components and build the mountGUI and
	 * the controllerGUi objects that show values on the panels
	 * 
	 * @param name The name of the MountController to connect to
	 */
	public void connectComponents(String name) throws AcsJException, AcsJMountGUIErrorEx {
		if (name==null || name.length()==0) {
			throw new IllegalArgumentException("Invalid name "+name);
		}
		setComponents(null,null);
		manager = new ACSComponentsManager(acsCS);
		// Connect mount and mount controller
		addStatusMessage("Connecting to "+name,false);
		manager.connectComponents(name);
		addStatusMessage("Connected to "+name,false);
		
		mountGUI=null;
		try {
			if (manager.getMountType()==ACSComponentsManager.AntennaType.VERTEX) {
				mountGUI=new MountVertex(manager.getVertex(),manager.getMountType(),acsCS,this);
			} else if (manager.getMountType()==ACSComponentsManager.AntennaType.VERTEX_LLAMA) {
				mountGUI=new MountVertexLLama(manager.getVertexLLama(),manager.getMountType(),acsCS,this);	
			} else if (manager.getMountType()==ACSComponentsManager.AntennaType.ALCATEL) {
				mountGUI=new MountAEM(manager.getAlcatel(),manager.getMountType(),acsCS,this);	
			} else if (manager.getMountType()==ACSComponentsManager.AntennaType.MELCO) {
				mountGUI=new MountACA(manager.getACA(),manager.getMountType(),acsCS,this);
			} else if (manager.getMountType()==ACSComponentsManager.AntennaType.MELCOA7M) {
				mountGUI=new MountA7M(manager.getA7M(),manager.getMountType(),acsCS,this);
			}
		}  catch (Throwable t) {
			AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
			ex.setContextDescription("Error building the mount");
			ex.setProperty("Mount type", manager.getMountType().description);
			throw ex;
		}
		if (mountGUI==null) {
			throw new AcsJUnsupportedAntennaTypeEx();
		}
		mountGUI.addComponentConnectionListener(connectionListener);
		
		controllerGUI=new MountController(manager.getController(),acsCS, this);
		controllerGUI.addComponentConnectionListener(connectionListener);

		setComponents(controllerGUI,mountGUI);
		
		coordsTbl.setComponents(controllerGUI,mountGUI);
		if (!isOMCPlugin()) {
			if (name.startsWith("CONTROL/")) {
				mountFrame.setTitle(name.substring(8));
			} else {
				mountFrame.setTitle(name);
			}
		}
		mountGUI.addOperationListener(this);
		controllerGUI.addOperationListener(this);
	}
	
	/**
	 * The event generated when the user presses connect in the 
	 * dialog to select an antenna mount
	 */
	public void connectPerformed(SelectorComponentEvent sce) {
		if (sce.getComponentName()==null || sce.getComponentName().length()==0) {
			// It is an error in the selector because it should always return
			// a name of a component
			//
			// We ignore the problem but write a message in stderr
			System.err.println("AntennaRootPane.connectPerformed(...): Wrong component name got from ComponentSelector");
			if (debugModeOn) {
				debugLogger.log("AntennaRootPane.connectPerformed(...): Wrong component name got from ComponentSelector");
			}
			return;
		}
		String componentName=sce.getComponentName();
		if (debugModeOn) {
			debugLogger.log("The user requested to connect to "+componentName);
		}
		statusLine.startProgressAnimation();
		
		class ConnectionRunnable implements Runnable {
			public String compName;
		     public void run() {
		    	 if (debugModeOn) {
		    		 debugLogger.log("Thread to connect to "+compName+" started");
		    	 }
		    	 try {
		    		 connectComponents(compName);
		    		 addStatusMessage("Connection to "+compName+" established",false);
		    	 } catch (Throwable t) {
		    		 String msg = "Error connecting components";
		    		 if (acsCS!=null) {
		    			 if (acsCS.getLogger()!=null) {
		    				 acsCS.getLogger().log(AcsLogLevel.ERROR, msg,t);
		    			 }
		    		 }
		    		 if (t instanceof AcsJException) {
		    			 msg=((AcsJException)t).getShortDescription();
		    			 addError(new ErrorInfo(msg,msg+": "+compName,(AcsJException)t));
		    		 } else {
		    			 AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
		    			 ex.setContextDescription("Error connecting components");
		    			 addError(new ErrorInfo(msg,msg+": "+compName,ex));
		    		 }
		    		 addStatusMessage("Connection to "+compName+" failed!",true);
		    	 } finally {
		    		 statusLine.stopProgressAnimation();
		    	 }
		    	 if (debugModeOn) {
		    		 debugLogger.log("Thread to connect to "+compName+" terminated");
		    	 }
		     }
		 };
		 ConnectionRunnable doConnection = new ConnectionRunnable();
		 doConnection.compName=componentName;
		 Thread connectThread = new Thread(doConnection);
		 connectThread.setName("connectThread");
		 if (debugModeOn) {
			 debugLogger.log("Starting thread to connect to "+componentName);
		 }
		 connectThread.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==connectMountMenuItem){
			connectMount();
		} else if (e.getSource()==exitMenuItem) {
			if (!isOMCPlugin()) {
				WindowEvent wEvt = new WindowEvent(mountFrame,WindowEvent.WINDOW_CLOSING);
				mountFrame.dispatchEvent(wEvt);
			}
		} else if (e.getSource()==releaseMountMenuItem) {
			Thread disconnectThread = new Thread(new Runnable() {
				public void run() {
					if (debugModeOn) {
						debugLogger.log("Started thread to disconnect components");
					}
					disconnectComponents();
					if (debugModeOn) {
						debugLogger.log("Thread to disconnect components terminated");
					}
				}
			});
			disconnectThread.setName("DisconnectMount thread");
			disconnectThread.setDaemon(false);
			if (debugModeOn) {
				debugLogger.log("Starting thread to disconnect components");
			}
			disconnectThread.start();
		} else {
			System.err.println("Unknown source of event: "+e);
		}
	}
	
	/**
	 * @see MenuListener
	 */
	public void menuDeselected(MenuEvent e) {}
	
	/**
	 * @see MenuListener
	 */
	public void menuCanceled(MenuEvent e) {}
	
	/**
	 * @see MenuListener
	 */
	public void menuSelected(MenuEvent e) {
		if (e.getSource()==fileMenu) {
			connectMountMenuItem.setEnabled(acsCS!=null && mountGUI==null);
			releaseMountMenuItem.setEnabled(acsCS!=null && mountGUI!=null);
		} 
	}
	
	/** 
	 * Shows the dialog to select an antenna and connect the components
	 * 
	 * The connection happens in AntennaRootPane that is set as event listener 
	 * by SelectAntennaMountDlg
	 */
	private void connectMount() {
		SelectAntennaMountDlg dlg = new SelectAntennaMountDlg(this,mountFrame,acsCS);
		dlg.setModal(true);
		dlg.setLocationRelativeTo(this);
		dlg.setVisible(true);
	}
	
	/**
	 * This method is executed whenever a command has been executed.
	 * A message is shown if the exception is not <code>null</code> (i.e. there was
	 * an error)
	 * 
	 * @see alma.control.gui.antennamount.Mount.MountOperationListener
	 */
	public void commandExecuted(long id, String cmd, String msg, AcsJException e) {
		String cmdStr = cmd+" (ID="+id+")";
		if (msg!=null) {
			// Error
			ErrorInfo error = new ErrorInfo("Error executing "+cmd, "Error executing "+cmdStr,e);
			error.addAdditionalInfo(error.new AdditionalInfo("Reason",msg));
			addError(error);
			addStatusMessage("Error executing "+cmdStr,true);
		} else {
			// Normal termination
			addStatusMessage(cmdStr+" executed",false);
		}
	}
	
	/**
	 * Notifies that a command has been submitted to a remote component
	 * 
	 * @param id The ID of the command
	 * @param cmd The submitted command
	 */
	public void commandSubmitted(long id, String cmd) {
		addStatusMessage(cmd+" submitted",false);
	}
	
	/**
	 * @see IPanel
	 */
	public boolean isOMCPlugin() {
		return mountFrame==null;
	}
	
//	/**
//	 * {@link ComponentConnectionLost}
//	 */
//	public void componentConnLost(String name) {
//		class CompDownThread extends Thread {
//			public void run() {
//				disconnectComponents();
//			}
//		}
//		Thread t = new CompDownThread();
//		t.setDaemon(true);
//		t.setName("CompDownThread");
//		t.start();
//		addError(new ErrorInfo(name+" is down", "Component "+name+" is down/not responding"));
//	}
	
	/**
	 * Add a message to the status line
	 * 
	 * Add a message to the status line at the bottom of the
	 * main window.
	 * The message must be not null and not empty.
	 * If the message is an error, it is written in red
	 * 
	 * @param msg The message to show in the status line
	 * @param errorMsg True if the message represents an error
	 */
	public void addStatusMessage(String msg, boolean errorMsg) {
		if (msg==null || msg.length()==0) {
			throw new IllegalArgumentException("Invalid message");
		}
		if (errorMsg) {
			statusLine.setMessage(ValueDisplayer.errorColor+msg);
		} else {
			statusLine.setMessage(ValueDisplayer.normalColor+msg);
		}
		if (debugModeOn) {
			debugLogger.log(msg);
		}
	}
	
	/**
	 * Add an error to the error panel
	 * 
	 * @param newError The not null new error to add
	 */
	public void addError(ErrorInfo newError) {
		if (newError==null) {
			throw new IllegalArgumentException("Invalid null error");
		}
		errorTab.addError(newError);
		if (debugModeOn) {
			debugLogger.log(newError);
		}
	}

	/**
	 * 
	 * @return The error tab
	 */
	public ErrorTabbedPane getErrorTabbedPane() {
		return errorTab;
	}
	
	/**
	 * 
	 * @return The heartbeat checker
	 */
	public HeartbeatChecker getHeartbeatChecker() {
		return hbChecker;
	}
}
