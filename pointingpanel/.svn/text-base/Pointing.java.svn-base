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
package alma.control.gui.antennamount.pointingpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.offset.OffsetPanel;

/**
 * The pointing panel with the widgets to point to objects.
 * 
 * The panel shows:
 * <UL>
 * 	<LI>the pointing panel that has 2 operating mode, expert and standard;
 * 	<LI>the offset panel;
 * </UL>
 * 
 * Initially the standard panel is shown but the user can select the
 * expert panel by pressing a toggle switch.
 * The difference between the two panels consists in the greater
 * number of controls shown by the expert panel.
 * 
 * @author acaproni
 *
 */
public class Pointing extends JPanel implements ActionListener {
    
    /**
     * The button to show hide some control widgets
     */
    private JToggleButton expertPointing = new JToggleButton("Details",false);
    
    /**
     * A button to show/hide the offset panel
     */
    private JToggleButton viewOffsetpanel = new JToggleButton("Offsets",false);
    
    /**
     * The panel with the buttons shown in the upper side of the tab
     */
    private JPanel buttonBar;
    
    /**
     * The offset panel
     */
    private OffsetPanel offsetPanel;
    
    /**
     * The panel showing the expert/standard pointing panels inside
     */
    private JPanel pointingPanel = new JPanel();
    
    /**
     * The mount 
     */
    private Mount mount=null;
    
    /**
     * The MountController
     */
    private MountController controller=null;
    
    /**
     * The expert pointing panels
     */
    private ExpertPanel expertPanel;
    
	/**
	 * Constructor
	 */
	public Pointing(AntennaRootPane rootP) {
		initialize(rootP);
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initialize(AntennaRootPane rootP) {
		setLayout(new BorderLayout());
		
		// Add the buttons bar at the top of the panel
		buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonBar.add(expertPointing);
		buttonBar.add(viewOffsetpanel);
		add(buttonBar,BorderLayout.NORTH);
		
		Box box = new Box(BoxLayout.Y_AXIS);
		
		// Add the expert/standard panel in the center
		expertPanel = new ExpertPanel(rootP,false);
		pointingPanel.add(expertPanel);
		JPanel centerPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		centerPnl.add(pointingPanel);
		pointingPanel.setBorder(new TitledBorder("Pointing"));
		box.add(centerPnl);
		
		box.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the offset panel
		JPanel offsetPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		offsetPanel=new OffsetPanel();
		offsetPanel.setBorder(new TitledBorder("Sky offsets"));
		offsetPnl.add(offsetPanel);
		box.add(offsetPnl);
		offsetPanel.setVisible(viewOffsetpanel.isSelected());
		
		
		add(box,BorderLayout.CENTER);
		
		// Connect the listeners
		expertPointing.addActionListener(this);
		viewOffsetpanel.addActionListener(this);
	}
	
	/**
	 * @see java.awt.event.ActionListener
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==expertPointing) {
			expertPointing.setEnabled(false);
			expertPanel.setExpertMode(expertPointing.isSelected(),expertPointing);
		} else if (e.getSource()==viewOffsetpanel) {
			offsetPanel.setVisible(viewOffsetpanel.isSelected());
		} else {
			System.out.println("Unknown event received: "+e);
		}
	}
	
	/**
	 * Set the mount and mount controller components
	 * 
     * @param ctr The mount controller
     * @param mnt The mount
     */
    public void setComponents(MountController ctr, Mount mnt) {
    	mount=mnt;
    	controller=ctr;
        offsetPanel.setController(ctr);
        expertPanel.setComponents(ctr, mnt);
        enableWidgets(controller!=null);
    }
    
    /** 
     * Enable or disable the widgets
     * 
     *  @param b If <code>true</code> enable the widgets
     */
    public void enableWidgets(final boolean b) {
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				offsetPanel.enableWidgets(b);
		        expertPanel.enableWidgets(b);
			}
		});
    }
    
    /**
	 * Release all the resources and close the panel
	 *
	 */
	public void close() {
		mount=null;
		offsetPanel.close();
		enableWidgets(false);
	}
}
