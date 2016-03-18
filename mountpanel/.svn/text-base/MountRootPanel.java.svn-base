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
package alma.control.gui.antennamount.mountpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.utils.ValueDisplayer;

/**
 * The RootPane for the mount panel
 * 
 * @author acaproni
 *
 */
public class MountRootPanel extends AntennaRootPane implements ActionListener {
	
	/**
	 * The mount panel (initially not shown)
	 */
	private AntennaMountPanel mountPnl;
	
	/**
	 * The button to stop axes
	 */
	private JButton stopB = new JButton("Stop");
	
	/**
	 * The name of the connected antenna
	 */
	private JLabel antennaNameLbl = new JLabel("",SwingConstants.LEFT);
	
	/**
	 * The type of the connected antenna
	 */
	private JLabel antennaTypeLbl = new JLabel("",SwingConstants.LEFT);
	
	/**
	 * The mount controller
	 */
	private MountController controller=null; 
	
	/**
	 * Empty constructor
	 */
	public MountRootPanel() {
		super();
		initialize();
	}
	
	/**
	 * Constructor
	 * 
	 * @param frame The frame showing this panel
	 */
	public MountRootPanel(JFrame frame) {
		super(frame);
		initialize();
		enableWidgets(controller!=null);
		stopB.addActionListener(this);
	}
	
	/**
	 * Init the GUI
	 *
	 */
	protected void initialize() {
		super.initialize();
		mountPnl=new AntennaMountPanel(this);
		
		// Add the stop button on the left side of the table of coordinates
		
		BorderLayout layout = (BorderLayout)getContentPane().getLayout();
		Component comp = layout.getLayoutComponent(BorderLayout.NORTH);

		JPanel tempPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tempPnl.add(stopB);
		stopB.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.red));
		stopB.setMinimumSize(new Dimension(55,30));
		stopB.setMinimumSize(new Dimension(75,50));
		stopB.setPreferredSize(stopB.getMinimumSize());
		
		
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createVerticalGlue());
		box.add(tempPnl);
		
		JPanel namePnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		namePnl.add(antennaNameLbl);
		box.add(namePnl);
		
		// Reduce the size of the font of the antenna type
		Font fnt = antennaTypeLbl.getFont();
		antennaTypeLbl.setFont(fnt.deriveFont(fnt.getSize()-2));
		JPanel typePnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		typePnl.add(antennaTypeLbl);
		box.add(typePnl);
		box.add(Box.createVerticalGlue());

		((JPanel)comp).add(box,BorderLayout.WEST);
		
		getContentPane().add(mountPnl,BorderLayout.CENTER);
		setPreferredSize(new Dimension(600,350));
		
	}
	
	/**
	 * Free all the resources
	 *
	 */
	public synchronized void close() {
		mountPnl.close();
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
			controllerGUI.setPaused(true);
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
			controllerGUI.setPaused(false);
			addStatusMessage("Unpaused",false);
		}
	}
	
	/**
	 * Run in restricted mode
	 * 
	 * @see alma.exec.extension.subsystemplugin.SubsystemPlugin
	 */
	public boolean runRestricted (boolean restricted) throws Exception {
		mountPnl.enableWidgets(!restricted);
		return restricted;
	}
	
	/**
	 * Enable/disable the widgets
	 * 
	 * @param enable if <code>true</code> enable the widgets
	 */
	public void enableWidgets(final boolean enable) {
		SwingUtilities.invokeLater(new Runnable() {
        	public void run() {
        		stopB.setEnabled(enable);
        	}
        });
	}
	
	/**
	 * @see AntennaRootPane
	 */
	public void setComponents(MountController ctr, final Mount mnt) {
		controller=ctr;
		mountPnl.setComponents(ctr,mnt);
		theToolbar.setComponents(ctr, mnt);
		enableWidgets(controller!=null);
		
		if (mnt==null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					antennaNameLbl.setText("");
					antennaTypeLbl.setText("");
				}
			});
			return;
		} 
		
		String compName = mnt.getComponentName();
		final String antName;
		
		if (compName==null || compName.isEmpty()) {
			antName=ValueDisplayer.RED_NOT_AVAILABLE;
		} else {
			// The name is something like CONTROL/DA41/Mount
			// We split to get the central part
			String[] nameParts = compName.split("/");
			if (nameParts==null || nameParts.length!=3) {
				antName=ValueDisplayer.RED_NOT_AVAILABLE;
			} else {
				antName="<HTML><FONT color=\"blue\" size=\"+1\">"+nameParts[1].trim();
			}
			
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				antennaTypeLbl.setText(mnt.getMountType().toString().trim());
				antennaNameLbl.setText(antName);
			}
		});
		
	}
	
	/**
	 * @see ActionListener
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==stopB) {
			if (controller!=null) {
				controller.stopTrajectory();
			}
		} else {
			super.actionPerformed(e);
		}
	}
	
}
