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
package alma.control.gui.antennamount.subreflectorpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorInfo;
import alma.control.gui.antennamount.errortab.ErrorTabbedPane;
import alma.control.gui.antennamount.errortab.TabTitleSetter;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.mount.ISubreflector.Coordinates;
import alma.control.gui.antennamount.utils.ValueDisplayer;

/**
 * The panel showing the actual position of the subreflector.
 * <P>
 * Optionally it allows the user to move the subreflector.
 * 
 * @author acaproni
 *
 */
public class SubreflectorPanel extends JPanel implements ActionListener, Runnable {
	
	/**
	 * If it is <code>true</code>, the panel shows the field to change the position of the subreflector
	 */
	private boolean interactive;
	
	// Input text field
	private JTextField xTF = new JTextField(6);
	private JTextField yTF = new JTextField(6);
	private JTextField zTF = new JTextField(6);
	private JTextField xDeltaTF = new JTextField(6);
	private JTextField yDeltaTF = new JTextField(6);
	private JTextField zDeltaTF = new JTextField(6);
	
	// Readback text field
	private JTextField actualX = new JTextField(6);
	private JTextField actualY = new JTextField(6);
	private JTextField actualZ = new JTextField(6);
	private JTextField deltaX = new JTextField(6);
	private JTextField deltaY = new JTextField(6);
	private JTextField deltaZ = new JTextField(6);
	
	// The labels
	private JLabel xLbl = new JLabel("<HTML><BODY><B>X"); 
	private JLabel yLbl = new JLabel("<HTML><BODY><B>Y");
	private JLabel zLbl = new JLabel("<HTML><BODY><B>Z");
	private JLabel tipLbl = new JLabel("<HTML><BODY><B>Tip");
	private JLabel tiltLbl = new JLabel("<HTML><BODY><B>Tilt");
	private JLabel rotLbl = new JLabel("<HTML><BODY><B>Rotation");
	
	/** 
	 * The apply position button
	 */
	private JButton applyPosBtn = new JButton("Apply");
	
	/** 
	 * The apply delta button
	 */
	private JButton applyDeltaBtn = new JButton("Apply");
	
	/**
	 * The button to reset delta
	 */
	private JButton zeroDeltaBtn = new JButton("Zero");
	
	// The label for the rows
	private JLabel actualLbl = new JLabel("<HTML><BODY><B>Actual",SwingConstants.RIGHT);
	private JLabel commandedLbl = new JLabel("<HTML><BODY><B>Commanded",SwingConstants.RIGHT);
	private JLabel actualDeltaLbl = new JLabel("<HTML><BODY><B>Delta",SwingConstants.RIGHT);
	private JLabel commandedDeltaLbl = new JLabel("<HTML><BODY><B>New Delta",SwingConstants.RIGHT);
	
	// tip/tilt/rotation 
	//private JLabel tipLbl = new JLabel("<HTML><BODY><B>New tip",SwingConstants.RIGHT);
	//private JLabel tiltLbl = new JLabel("<HTML><BODY><B>New tilt",SwingConstants.RIGHT);
	private JLabel rotationLbl = new JLabel("<HTML><BODY><B>Commanded",SwingConstants.RIGHT);
	private JTextField tipTF = new JTextField(6);
	private JTextField tiltTF = new JTextField(6);
	private JTextField rotationTF = new JTextField(6);
	
	// Actual tip/tilt/rotation
	//private JLabel actualTipLbl = new JLabel("<HTML><BODY><B>Actual tip",SwingConstants.RIGHT);
	//private JLabel actualTiltLbl = new JLabel("<HTML><BODY><B>Actual tilt",SwingConstants.RIGHT);
	private JLabel actualRotationLbl = new JLabel("<HTML><BODY><B>Actual",SwingConstants.RIGHT);
	private JTextField actualTipTF = new JTextField(6); // X
	private JTextField actualTiltTF = new JTextField(6); // Y
	private JTextField actualRotationTF = new JTextField(6); // Third coords not used in vertex
	
	/**
	 * The button to apply tip/tilt
	 */
	private JButton applyTipTiltBtn = new JButton("Apply");
	
	/**
	 * The button to init the subreflector
	 */
	private JButton initBtn = new JButton("Init subreflector");
	
	/**
	 * The position from the component
	 */
	private ValueHolder<Coordinates> actual=null; 
	
	/**
	 * The position from the component
	 */
	private ValueHolder<Coordinates> delta=null;
	
	/**
	 * The rotation from the component
	 */
	private ValueHolder<Coordinates> rotation=null;
	
	/**
	 * The mount controller
	 */
	private MountController controller;
	
	/**
	 * The mount
	 */
	private Mount mount;
	
	/**
	 * The panel with the status table
	 */
	private Box statusTablePanel;
	
	/**
	 * The table with the state of the subreflector
	 */
	private final SubrefStatusTable statusTable=new SubrefStatusTable();
	
	/**
	 * The thread to refresh the values shown in the panel
	 */
	private Thread thread=null;
	
	/**
	 * The boolean to signal the thread to terminate
	 */
	private volatile boolean terminateThread;
	
	/**
	 * The interval between 2 refreshes (msec)
	 */
	private static final int REFRESH_INTERVAL=1500;
	
	/**
	 * The content of the panel
	 */
	private final AntennaRootPane antennaRootP;
	
	/**
	 * <code>titleSetter</code> is used to set the title of the tab, depending on the state
	 * of the bits of the subreflector
	 */
	private TabTitleSetter titleSetter;
	
	/**
	 * The component shown in the error tab (to set the title of the tab with the
	 * right color and icon)
	 */
	private Component errorTabComponent;
	
	/**
	 * Constructor.
	 * 
	 * @param interactive If <code>true</code> the panel shows the field to move
	 *                    the subreflector
	 * @param rootP The AntennaRootPane to write messages in the status line
	 */
	public SubreflectorPanel(AntennaRootPane rootP, boolean interactive) {
		super();
		if (rootP==null) {
			throw new IllegalArgumentException("AntennaRootPane can't be null");
		}
		
		this.antennaRootP=rootP;
		this.interactive=interactive;
		
		initialize();
		setComponents(null, null);
	}
	
	/**
	 * Set the tab title setter (it is informed about the situation of the items
	 * to display warnings and errs in the tab title).
	 * 
	 * @param titleSetter The title setter
	 * @param tabComponent The component
	 * 
	 * @see ErrorTabbedPane
	 */
	public void setTabTitleSetter(TabTitleSetter titleSetter, Component tabComponent) {
		if (titleSetter==null) {
			throw new IllegalArgumentException("TabTitleSetter can't be null");
		}
		if (tabComponent==null) {
			throw new IllegalArgumentException("Tab component can't be null");
		}
		this.titleSetter=titleSetter;
		this.errorTabComponent=tabComponent;
	}
	
	/**
	 * Init the GUI
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		
		JPanel initPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		initPnl.add(initBtn);
		add(initPnl,BorderLayout.NORTH);
		
		// Top panel with control widgets
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		GridLayout layout = new GridLayout(10,5,10,5);
		JPanel pnl = new JPanel(layout);
		
		// First row
		pnl.add(new JLabel());
		JPanel xPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		xPnl.add(xLbl);
		pnl.add("xLbl", xPnl);
		JPanel yPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		yPnl.add(yLbl);
		pnl.add("yLbl", yPnl);
		JPanel zPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		zPnl.add(zLbl);
		pnl.add("xLbl", zPnl);
		pnl.add(new JLabel());
		
		// Second row
		actualX.setEditable(false);
		actualY.setEditable(false);
		actualZ.setEditable(false);
		pnl.add("actual", actualLbl);
		pnl.add("acutualX", actualX);
		pnl.add("acutualY", actualY);
		pnl.add("acutualZ", actualZ);
		pnl.add(new JLabel());
		
		// Third row
		pnl.add("commanded", commandedLbl);
		pnl.add("commandedX", xTF);
		pnl.add("commandedY", yTF);
		pnl.add("commandedZ", zTF);
		pnl.add("apply", applyPosBtn);
		
		// Separator
		pnl.add(new JLabel());
		pnl.add(new JLabel());
		pnl.add(new JLabel());
		pnl.add(new JLabel());
		pnl.add(new JLabel());
		
		// Forth row
		deltaX.setEditable(false);
		deltaY.setEditable(false);
		deltaZ.setEditable(false);
		pnl.add("actualD", actualDeltaLbl);
		pnl.add("acutualDX", deltaX);
		pnl.add("acutualDY", deltaY);
		pnl.add("acutualDZ", deltaZ);
		pnl.add(zeroDeltaBtn);
		
		// Fifth row
		pnl.add("commandedD", commandedDeltaLbl);
		pnl.add("commandedDX", xDeltaTF);
		pnl.add("commandedDY", yDeltaTF);
		pnl.add("commandedDZ", zDeltaTF);
		pnl.add("applyD", applyDeltaBtn);
		
		// Separator
		pnl.add(new JLabel());
		pnl.add(new JLabel());
		pnl.add(new JLabel());
		pnl.add(new JLabel());
		pnl.add(new JLabel());
		
		// Sixth row
		pnl.add(new JLabel());
		JPanel tipPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		tipPnl.add(tipLbl);
		pnl.add("tipLbl", tipPnl);
		JPanel tiltPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		tiltPnl.add(tiltLbl);
		pnl.add("tiltLbl", tiltPnl);
		JPanel rotPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		rotPnl.add(rotLbl);
		pnl.add("rotLbl", rotPnl);
		pnl.add(new JLabel());

		// Seventh row
		actualTipTF.setEditable(false);
		actualTiltTF.setEditable(false);
		actualRotationTF.setEditable(false);
		pnl.add("actualRotationLbl", actualRotationLbl);
		pnl.add("actualtip", actualTipTF);
		pnl.add("actualtilt", actualTiltTF);
		pnl.add("actualrotation", actualRotationTF);
		pnl.add(new JLabel());
/*
		actualTipTF.setEditable(false);
		actualTiltTF.setEditable(false);
		pnl.add("actualTipLbl", actualTipLbl);
		pnl.add("actualtip", actualTipTF);
		pnl.add("actualtiltLbl", actualTiltLbl);
		pnl.add("actualtilt", actualTiltTF);
		pnl.add("actualEmpty", new JLabel());
*/
		// Eighth row
		pnl.add("rotationLbl", rotationLbl);
		pnl.add("tip", tipTF);
		pnl.add("tilt", tiltTF);
		pnl.add("rotation", rotationTF);
		pnl.add("applyTT", applyTipTiltBtn);
/*
		pnl.add("tipLbl", tipLbl);
		pnl.add("tip", tipTF);
		pnl.add("tiltLbl", tiltLbl);
		pnl.add("tilt", tiltTF);
		pnl.add("applyTT", applyTipTiltBtn);
*/
		
		topPnl.add(pnl);
		add(topPnl,BorderLayout.CENTER);
		
		// Add the status table at the bottom
		statusTablePanel = new Box(BoxLayout.Y_AXIS);
		statusTablePanel.add(Box.createRigidArea(new Dimension(0,5)));
		statusTablePanel.add(new JSeparator());
		statusTablePanel.add(Box.createRigidArea(new Dimension(0,5)));
		// The header must be explicitly displayed because the tables is not inside a JScrollPane
		// infact it is inside statusTablePanel
		statusTablePanel.add(statusTable.getTableHeader());
		statusTablePanel.add(statusTable);
		statusTablePanel.add(Box.createVerticalGlue());
		
		add(statusTablePanel,BorderLayout.SOUTH);
		
		// Add the listeners
		applyPosBtn.addActionListener(this);
		applyDeltaBtn.addActionListener(this);
		applyTipTiltBtn.addActionListener(this);
		zeroDeltaBtn.addActionListener(this);
		initBtn.addActionListener(this);
	}
	
	/**
	 * Initialize the widgets hiding/showing and enabling/disabling 
	 * them depending on
	 *  - the value of interactive
	 *  - the value of mount
	 *  
	 *  The widgets
	 */
	private void initWidgets() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Show the widgets depending on the value of interactive
				xTF.setVisible(interactive);
				yTF.setVisible(interactive);
				zTF.setVisible(interactive);
				xDeltaTF.setVisible(interactive);
				yDeltaTF.setVisible(interactive);
				zDeltaTF.setVisible(interactive);
				commandedLbl.setVisible(interactive);
				applyPosBtn.setVisible(interactive);
				applyDeltaBtn.setVisible(interactive);
				zeroDeltaBtn.setVisible(interactive);
				initBtn.setVisible(interactive);
				tiltTF.setVisible(interactive);
				tiltLbl.setVisible(interactive);
				tipTF.setVisible(interactive);
				tipLbl.setVisible(interactive);
				rotationTF.setVisible(interactive);
				rotationLbl.setVisible(interactive);
				applyTipTiltBtn.setVisible(interactive);
				
				// enable/disable the widgets depending on the value of mount
				actualX.setEnabled(mount!=null);
				actualY.setEnabled(mount!=null);
				actualZ.setEnabled(mount!=null);
				deltaX.setEnabled(mount!=null);
				deltaY.setEnabled(mount!=null);
				deltaZ.setEnabled(mount!=null);
				xTF.setEnabled(mount!=null);
				yTF.setEnabled(mount!=null);
				zTF.setEnabled(mount!=null);
				xDeltaTF.setEnabled(mount!=null);
				yDeltaTF.setEnabled(mount!=null);
				zDeltaTF.setEnabled(mount!=null);
				applyPosBtn.setEnabled(mount!=null);
				applyDeltaBtn.setEnabled(mount!=null);
				statusTablePanel.setEnabled(mount!=null);
				zeroDeltaBtn.setEnabled(mount!=null);
				initBtn.setEnabled(mount!=null);
				actualTipTF.setEnabled(mount!=null);
				actualTiltTF.setEnabled(mount!=null);
				actualRotationTF.setEnabled(mount!=null);
				tiltTF.setEnabled(mount!=null);
				tipTF.setEnabled(mount!=null);
				rotationTF.setEnabled(mount!=null);
				applyTipTiltBtn.setEnabled(mount!=null);
				
				// Display/hide the widgets depending on the type of the mount
				ratioWidgets();
			}
		});
		
		
	}
	
	/**
	 * Display/hide the widgets depending on the type of the mount.
	 * <P>
	 * Some of the widgets needs to be displayed or hidden depending
	 * on the type of the connected mount. In fact, even if all the
	 * subreflectors implements ISubreflector, not all the methods and values
	 * are available in the different mount types. 
	 * For example, the alcatel prototype does not have rotation.
	 * 
	 * <b>Note</b>: this method must be executed by the Swing thread.
	 */
	private void ratioWidgets() {
		if (mount==null) {
			// Hide all optional fields
			tipLbl.setVisible(false);
			tiltLbl.setVisible(false);
			rotLbl.setVisible(false);
			rotationLbl.setVisible(false);
			tipTF.setVisible(false);
			tiltTF.setVisible(false);
			rotationTF.setVisible(false);
			actualRotationLbl.setVisible(false);
			actualTipTF.setVisible(false);
			actualTiltTF.setVisible(false);
			actualRotationTF.setVisible(false);
			applyTipTiltBtn.setVisible(false);
			initBtn.setVisible(false);
			return;
		}
		tipLbl.setVisible(true);
		tiltLbl.setVisible(true);
		rotLbl.setVisible(true);
		rotationLbl.setVisible(true);
		tipTF.setVisible(true);
		tiltTF.setVisible(true);
		rotationTF.setVisible(true);
		actualRotationLbl.setVisible(true);
		actualTipTF.setVisible(true);
		actualTiltTF.setVisible(true);
		actualRotationTF.setVisible(true);
		applyTipTiltBtn.setVisible(true);
		initBtn.setVisible(false);
	}
	
	/**
	 * Set the mount and the mount controller.
	 * <P>
	 * <code>setComponents</code> starts or stop the thread
	 * depending on the value of the passed mount. 
	 * 
	 * @param ctr The mount controller
	 * @param mnt The mount
	 */
	public void setComponents(MountController ctr, Mount mnt) {
		mount=mnt;
		controller=ctr;
		statusTable.setMount(mnt);
		initWidgets();
		if (mnt!=null) {
			actual = mount.getSubreflector().getAbsPosition();
			delta= mount.getSubreflector().getDeltaPosition();
			rotation=mount.getSubreflector().getRotation();
			terminateThread=false;
			thread = new Thread(this,"Subreflector panel thread");
			thread.setDaemon(true);
			antennaRootP.getHeartbeatChecker().register(thread);
			thread.start();
		} else {
			actual=null;
			delta=null;
			terminateThread=true;
			if (thread!=null) {
				thread.interrupt();
			}
		}
	}
	
	/**
	 * @see java.awt.event.ActionListener
	 */
	public void actionPerformed(ActionEvent e) {
		if (mount==null) {
			return;
		}
		if (e.getSource()==applyPosBtn) {
			double x=0;
			double y=0;
			double z=0;
			try {
				x= Double.parseDouble(xTF.getText());
				xTF.setForeground(Color.BLACK);
			} catch (Throwable t) {
				xTF.setForeground(Color.RED);
				antennaRootP.addStatusMessage("Wrong X= "+xTF.getText(),true);
				return;
			}
			try { 
				y= Double.parseDouble(yTF.getText());
				yTF.setForeground(Color.BLACK);
			} catch (Throwable t) {
				yTF.setForeground(Color.RED);
				antennaRootP.addStatusMessage("Wrong Y= "+yTF.getText(),true);
				return;
			}
			try {
				z= Double.parseDouble(zTF.getText());
				zTF.setForeground(Color.BLACK);
			} catch (Throwable t) {
				zTF.setForeground(Color.RED);
				antennaRootP.addStatusMessage("Wrong Z= "+zTF.getText(),true);
				return;
			}
			Coordinates coords = null;
			try {
				coords = new Coordinates(x/1000.0,y/1000.0,z/1000.0);
			} catch (Throwable t) {
				antennaRootP.addStatusMessage(String.format("Invalid coordinates [%d,%d,%d]",x,y,z),true);
				return;
			}
			// Move the subreflector
			mount.getSubreflector().setAbsPosition(coords);
		} else if (e.getSource()==applyDeltaBtn) {
			// Set a new delta
			double x=0;
			double y=0;
			double z=0;
			try {
				x= Double.parseDouble(xDeltaTF.getText());
				xDeltaTF.setForeground(Color.BLACK);
			} catch (Throwable t) {
				xDeltaTF.setForeground(Color.RED);
				antennaRootP.addStatusMessage("Wrong Delta X= "+xDeltaTF.getText(),true);
				return;
			}
			try { 
				y= Double.parseDouble(yDeltaTF.getText());
				yDeltaTF.setForeground(Color.BLACK);
			} catch (Throwable t) {
				yDeltaTF.setForeground(Color.RED);
				antennaRootP.addStatusMessage("Wrong Delta Y= "+yDeltaTF.getText(),true);
				return;
			}
			try {
				z= Double.parseDouble(zDeltaTF.getText());
				zDeltaTF.setForeground(Color.BLACK);
			} catch (Throwable t) {
				zDeltaTF.setForeground(Color.RED);
				antennaRootP.addStatusMessage("Wrong Delta Z= "+zDeltaTF.getText(),true);
				return;
			}
			Coordinates coords = null;
			try {
				coords = new Coordinates(x/1000.0,y/1000.0,z/1000.0);
			} catch (Throwable t) {
				antennaRootP.addStatusMessage(String.format("Invalid delta coordinates [%d,%d,%d]",x,y,z),true);
				return;
			}
			mount.getSubreflector().setDeltaPosition(coords);
		} else if (e.getSource()==applyTipTiltBtn) {
			// Set a tip/tilt
			int tip=0;
			int tilt=0;
			int rotation=0;
			try {
				tip= Integer.parseInt(tipTF.getText());
				tipTF.setForeground(Color.BLACK);
			} catch (Throwable t) {
				tipTF.setForeground(Color.RED);
				antennaRootP.addStatusMessage("Wrong tip= "+tipTF.getText(),true);
				return;
			}
			try { 
				tilt= Integer.parseInt(tiltTF.getText());
				tiltTF.setForeground(Color.BLACK);
			} catch (Throwable t) {
				tiltTF.setForeground(Color.RED);
				antennaRootP.addStatusMessage("Wrong tilt= "+tiltTF.getText(),true);
				return;
			}
			try { 
				rotation= Integer.parseInt(rotationTF.getText());
				rotationTF.setForeground(Color.BLACK);
			} catch (Throwable t) {
				rotationTF.setForeground(Color.RED);
				antennaRootP.addStatusMessage("Wrong rotation= "+rotationTF.getText(),true);
				return;
			}
			mount.getSubreflector().setRotation(tip,tilt,rotation);
		} else if (e.getSource()==zeroDeltaBtn) {
			// Reset the delta
			mount.getSubreflector().zeroDelta();
		} else if (e.getSource()==initBtn) {
			mount.getSubreflector().init();
		}
	}
	
	/**
	 * Refresh the content of the X, Y, Z widget with the content
	 * of the coordinates
	 * 
	 * @param coords The coordinates read from the component
	 * @param x The widget showing X
	 * @param y The widget showing Y
	 * @param z The widget showing Z
	 * @param factor To factor to divide values in the text field
	 */
	private void refreshContent(ValueHolder<Coordinates> coords, JTextField x, JTextField y, JTextField z, double factor) {
		if (x==null || y==null || z==null) {
			throw new IllegalArgumentException("Widgets can't be null");
		}
		if (coords==null) {
			x.setText("");
			y.setText("");
			z.setText("");
			return;
		}
		if (coords.getValue()==null) {
			x.setText(ValueDisplayer.NOT_AVAILABLE);
			y.setText(ValueDisplayer.NOT_AVAILABLE);
			z.setText(ValueDisplayer.NOT_AVAILABLE);
			return;
		}
		x.setText(""+coords.getValue().x/factor);
		y.setText(""+coords.getValue().y/factor);
		z.setText(""+coords.getValue().z/factor);
	}
	
	/**
	 * Refresh all the values shown in the panel.
	 * 
	 * The thread is started/stopped by <code>setComponents</code>.
	 */
	public void run() {
		while (!terminateThread) {
			antennaRootP.getHeartbeatChecker().ping(thread);
			titleSetter.tabTitleState(statusTable.refresh(), "Subreflector",errorTabComponent , false);
			// Actual position
			try {
				refreshContent(actual, actualX, actualY, actualZ,1000.0);
				refreshContent(delta, deltaX, deltaY, deltaZ,1000.0);
				refreshContent(rotation, actualTipTF, actualTiltTF, actualRotationTF,1.0);
			} catch (Throwable t) {
				AcsJMountGUIErrorEx ex=new AcsJMountGUIErrorEx(t);
				ex.setContextDescription("Error refreshing the Subreflector panel");
				ErrorInfo ei = new ErrorInfo("Error in subreflector panel","Error refreshing the contento of the subreflector panel",ex);
				antennaRootP.addError(ei);
			}
			try {
				Thread.sleep(REFRESH_INTERVAL);
			} catch (Exception e) {
				continue;
			}
		}
		antennaRootP.getHeartbeatChecker().unregister(thread);
		thread=null;
	}
}
