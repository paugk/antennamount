/*
 * ALMA - Atacama Large Millimiter Array (c) European Southern Observatory, 2010
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
package alma.control.gui.antennamount.mount;

import alma.control.gui.antennamount.mount.MountCommom.UpdateError;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;

import alma.ControlGUIErrType.wrappers.AcsJMetrologyEx;

public interface IMetrology {

	/**
	 * @return The metrology mode
	 */
	public ValueHolder<LongArrayBit> getMode();
	
	/**
	 * @return The metrology equipment status
	 */
	public ValueHolder<LongArrayBit> getEquipStatus();
	
	/**
	 * @return The metrology delta path
	 */
	public ValueHolder<Integer> getDeltaPath();
	
	/**
	 * @return The metrology deltas temperature
	 */
	public ValueHolder<int[]> getDeltas();
	
	/**
	 * @return The temperatures
	 * 
	 * @see MetrologyCommon.temps for a detailed description of the value
	 *      returned by this method. 
	 */
	public ValueHolder<int[]> getTemps();
	
	/**
	 * Set the metrology mode
	 * 
	 * @param mode The new mode (4 bytes)
	 * @throws AcsJMetrologyEx In case of error
	 */
	public void setMetrMode(int[] mode);
	
	/**
	 * refresh the values by reading from the Mount
	 * @param errState
	 */
	public void refresh(UpdateError errState);
	
}
