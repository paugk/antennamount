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

import javax.swing.table.AbstractTableModel;

import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.ValueState;

/**
 * Base class for the model of the status table of the subreflector.
 * 
 * @author acaproni
 *
 */
public class CommonStatusModel extends AbstractTableModel {
	
	/**
	 * @see AbstractTableModel
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * The method return the number of the common rows i.e. the rows
	 * shown for every type of antenna.
	 * 
	 * @see AbstractTableModel
	 */
	public int getRowCount() {
		return 0;
	}
	
	public String getColumnName(int columnIndex) {
		if (columnIndex==0) {
			return "<HTML><B>Monitor points";
		} else {
			return "<HTML><B>Status";
		}
	}

	/**
	 * @see AbstractTableModel
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return ValueDisplayer.NOT_AVAILABLE;
	}
	
	/**
	 * Refresh the limits
	 * <P>
	 * This method does nothing and is executed only when no mount is connected.
	 * It will be redefined in the derived class to fill the table with the right 
	 * values depending on the type of the connected antenna
	 * 
	 * @return The state of the component
	 */
	protected ValueState refreshLimits() {
		return ValueState.NORMAL;
	}
	
	/**
	 * Update the state from the component
	 * <P>
	 * This method does nothing and is executed only when no mount is connected.
	 * It will be redefined in the derived class to fill the table with the right 
	 * values depending on the type of the connected antenna
	 * 
	 * @return The state of the component
	 */
	protected ValueState refreshState() {
		return ValueState.NORMAL;
	}
	
	

}
