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
 * @version $Id: MountInterface.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.tracking;

import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.utils.ValueState;

/**
 * 
 * The interface shared between the two types of antennas
 *
 */
public interface MountInterface {
	
	/**
	 * 
	 * @return The number of rows
	 */
	public int getRowCount(); 
	
	/**
	 * Return the consistent of the cell in the given position
	 *  
	 * @param row The row
	 * @param col The column
	 * @return The content of the cell in the given row and col
	 */
	public String getValueAt(int row, int col);
	
	/**
	 * Refreshes the values to show in the table
	 * i.e. read the values from the Mount
	 * 
	 * @param mnt The mount to read values from
	 * @return The state of the values (to update the title of the error tab)
	 */
	public ValueState refreshValues(Mount mnt);
	
	/** 
	 * Set all the fields in error state (/N/A)
	 *
	 */
	public void updateError();
	
	/**
	 * Return the description of a ACU error. Both antennas return the same type of ACU error 
	 * i.e. a long describing the code and another long with the CAN address.
	 * However, the description of the error is antenna specific.
	 * The string returned by this method is the description of the error
	 * for the specific antenna type. Such a description is in the IDL and stored in ACU_ERROR_DESC (redefined by
	 * the class specializing this one)
	 * 
	 * @param errorCode The error code
	 * @param CANaddress The CAN address (if error!=0)
	 * @return The Description of the specific ACU error
	 */
	public String getAcuErrorDescription(int errorCode, int CANaddress);
}
