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

/** 
 * @author  acaproni   
 * @version $Id: CommonStatusTable.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.tracking;

import java.awt.Component;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorTabbedPane;
import alma.control.gui.antennamount.errortab.TabTitleSetter;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.utils.GUIConstants;

import javax.swing.JTable;

/**
 * 
 * The table with the detailed status of the antenna.
 * It contains the common part between AEC and VA mount.
 *
 */
public class CommonStatusTable extends JTable {
	
	/**
	 * The model for the table
	 */
	private CommonStatusModel model;
	
	/**
	 * Constructor
	 *
	 */
	public CommonStatusTable(AntennaRootPane rootPane) {
		super();
		 model= new CommonStatusModel(rootPane);
		setModel(model);
		setBackground(GUIConstants.tableBackgroundColor);
		setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
        setCellSelectionEnabled(false);
        setOpaque(false);
	}
	
	/**
	 * Set the mount and the controller to read values from
	 * 
	 * @param mnt The mount (can be null)
	 */
	public void setComponents(MountController ctr, Mount mnt) {
		model.setComponents(ctr,mnt);
	}
	
	/**
	 * Stop the thread and release the resources
	 *
	 */
	public void close() {
		model.close();
	}
	
	/**
	 * Set the tab title setter in the model 
	 * 
	 * @param titleSetter The title setter
	 * @param tabComponent The component
	 * 
	 * @see ErrorTabbedPane
	 */
	public void setTabTitleSetter(TabTitleSetter titleSetter, Component tabComponent) {
		model.setTabTitleSetter(titleSetter, tabComponent);
	}
}
