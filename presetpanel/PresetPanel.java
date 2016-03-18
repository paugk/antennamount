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
package alma.control.gui.antennamount.presetpanel;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;

/**
 * The panel with the buttons to move the antenna into a preset position
 * @author acaproni
 *
 */
public class PresetPanel extends JPanel implements ActionListener {
	
	// The mount
	private Mount mount;
	
	// The mount controller
	private MountController mountController;
	
	// The button with the preset positions
	private JButton maintenanceStowB = new JButton("Maintenance stow");
	private JButton survivalStowB = new JButton("Survival stow");

	/**
	 * Constructor
	 */
	public PresetPanel() {
		initialize();
		enableWidgets(false);
	}
	
	/**
	 * Initialize the panel
	 */
	private void initialize() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(maintenanceStowB);
		add(survivalStowB);
		
		// Connect the listeners
		maintenanceStowB.addActionListener(this);
		survivalStowB.addActionListener(this);
	}

	/**
	 * Set the mount and mount controller components
	 * 
	 * @param mnt The mount
	 * @param ctrl The mount controller
	 */
	public void setComponents(Mount mnt, MountController ctrl) {
		mount=mnt;
		mountController=ctrl;
		enableWidgets(mountController!=null);
	}
	
	/**
	 * Enable/disable the widgets
	 * 
	 * @param enable If true enable the widgets
	 */
	public void enableWidgets(final boolean enable) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				survivalStowB.setEnabled(enable);
				maintenanceStowB.setEnabled(enable);
			}
		});
	}
	
	/**
	 * @see ActionListener
	 */
	public void actionPerformed(ActionEvent e) {
		if (mountController==null) {
			return;
		}
		if (e.getSource()==maintenanceStowB) {
			mountController.maintenanceStow();
		} else if (e.getSource()==survivalStowB) {
			mountController.survivalStow();
		}
	}
}
