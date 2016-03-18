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
package alma.control.gui.antennamount.controlspanel;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.axis.AemInitEncoderPanel;
import alma.control.gui.antennamount.axis.AxisPanel;
import alma.control.gui.antennamount.mount.ACSComponentsManager.AntennaType;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.presetpanel.PresetPanel;
import alma.control.gui.antennamount.shutter.ShutterPanel;
import alma.control.gui.antennamount.tolerancepanel.TolerancePanel;

/**
 * The panel with controls like axes, shutter, predefined positions and so on
 * 
 * @author acaproni
 *
 */
public class ControlsPanel extends JPanel {
	
	// The panel with axis
    private AxisPanel axisPanel;
    
    // The shutter panel
    private ShutterPanel shutterPanel;
    
    // The panel to read/set the tolerance
    private TolerancePanel tolerancePanel;
    
    // The panel to movwe antenna to preset positions
    private PresetPanel presetPanel;
    
    /**
     * The panel with the AEM buttons
     */
    private AemInitEncoderPanel aemPanel;

    /**
	 * Constructor
	 * 
	 * @param rootP The AntennaRootPane
	 */
	public ControlsPanel(AntennaRootPane rootP) {
		initialize(rootP);
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initialize(AntennaRootPane rootP) {
		
		Box box = new Box(BoxLayout.Y_AXIS);
		
		// Add the axis panel to the left
		JPanel axisPnl= new JPanel(new FlowLayout(FlowLayout.LEFT));
		axisPnl.setBorder(BorderFactory.createTitledBorder("Axes"));
		axisPanel = new AxisPanel(rootP);
		axisPnl.add(axisPanel);
		box.add(axisPnl);
		
		// Add the panel with the buttons for initializing the axes of the AEM
		JPanel aemPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		aemPnl.setBorder(BorderFactory.createTitledBorder("Encoders Initialization"));
		aemPanel=new AemInitEncoderPanel(rootP);
		aemPnl.add(aemPanel);
		aemPanel.setVisible(false);
		box.add(aemPnl);
		
		// Add the shutter panel to the right
		JPanel shutterPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		shutterPnl.setBorder(BorderFactory.createTitledBorder("Shutter"));
		shutterPanel = new ShutterPanel(rootP);
		shutterPnl.add(shutterPanel);
		box.add(shutterPnl);
		
		// Add the tolerance panel
		JPanel tolerancePnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tolerancePnl.setBorder(BorderFactory.createTitledBorder("Tolerance"));
		tolerancePanel = new TolerancePanel(rootP);
		tolerancePnl.add(tolerancePanel);
		box.add(tolerancePnl);
		
		// Add the preset panel
		JPanel presetPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		presetPnl.setBorder(BorderFactory.createTitledBorder("Preset positions"));
		presetPanel = new PresetPanel();
		presetPnl.add(presetPanel);
		box.add(presetPnl);
		
		box.add(Box.createVerticalGlue());
		
		add(box);
	}
	
	/**
	 * Set the mount and the mount controller
	 * 
	 * @param ctr The mount controller component
	 * @param mnt The mount component
	 */
    public void setComponents(MountController ctr, Mount mnt) {
    	axisPanel.setComponents(ctr, mnt);
    	shutterPanel.setComponents(ctr, mnt);
    	tolerancePanel.setMount(mnt);
    	presetPanel.setComponents(mnt, ctr);
    	if (mnt==null) {
    		aemPanel.setComponents(ctr, mnt);
    		SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					aemPanel.setVisible(false);
				}
			});
    	} else if (mnt.getMountType()==AntennaType.ALCATEL) {
			aemPanel.setComponents(ctr, mnt);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					aemPanel.setVisible(true);
				}
			});
    	}
    }
    

    /** 
     * Enable or disable the widgets in the panel
     */
    public void enableWidgets(boolean b) {
    	axisPanel.enableWidgets(b);
    	tolerancePanel.enableWidgets(b);
    	presetPanel.enableWidgets(b);
    }
    
    /**
     * Release the resources
     */
    public void close() {
    	axisPanel.close();
    	tolerancePanel.close();
    	aemPanel.close();
    }
}
