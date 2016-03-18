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
package alma.control.gui.antennamount.mount;

import alma.Control.MountStatusData;

/**
 * The status of the mount as returned by Mount.getMountStatusData.
 * 
 * There is no error associated with the values hold by this data structure.
 * They can be available without error or unavailable.
 * On the other handm they can be available but not up to date.
 * 
 * @author acaproni
 *
 */
public class MountStatus {
	
	// The mount status as read from the MountController
	private MountStatusData mountData;
	
	// True if the mount is on target
	private ValueHolder<Boolean> onTarget = new ValueHolder<Boolean>();
	
	// True if the ACU pointing model has been applied
	private ValueHolder<Boolean> acuPointingModel = new ValueHolder<Boolean>();
	
	// True if the ACU Aux PM has been applied
	private ValueHolder<Boolean> acuAuxPointingModel = new ValueHolder<Boolean>();
	
	// AZ/EL
	private ValueHolder<Double> commandAz = new ValueHolder<Double>();
	private ValueHolder<Double> commandEl = new ValueHolder<Double>();
	private ValueHolder<Double> azPos = new ValueHolder<Double>();
	private ValueHolder<Double> elPos = new ValueHolder<Double>();
	
	// AZ/EL encoders
	private ValueHolder<Double> azEncoder = new ValueHolder<Double>();
	private ValueHolder<Double> elEncoder = new ValueHolder<Double>();
	
	/**
	 * Getter method
	 * 
	 * @return The name of the antenna returning this 
	 *         data struct
	 */
	public String getAntennaName() {
		if (mountData==null) {
			return null;
		}
		return mountData.antennaName;
	}
	
	/**
	 * Return the time when the MountStatusData has been produced
	 * by the Mount
	 * 
	 * @return The timestamp of the MountStatusData
	 *         null if there is no data available
	 */
	public Long getProducedTime() {
		if (mountData==null) {
			return null;
		}
		return mountData.timestamp;
	}
	
	/**
	 * Update the data structure with the value passed as parameter.
	 * 
	 * 
	 * @param newData The new data to set as current value 
	 *                Can be null if no data is available
	 */
	public void update(MountStatusData newData) {
		if (newData==null) {
			azEncoder.setValue(null);
			elEncoder.setValue(null);
			commandAz.setValue(null);
			commandEl.setValue(null);
			azPos.setValue(null);
			elPos.setValue(null);
			acuPointingModel.setValue(null);
			acuAuxPointingModel.setValue(null);
			onTarget.setValue(null);
			return;
		}
		mountData=newData;
		if (newData.azEncoderValid) {
			azEncoder.setValue(newData.azEncoder,newData.timestamp);
		} else {
			azEncoder.setValue(null);
		}
		if (newData.elEncoderValid) {
			elEncoder.setValue(newData.elEncoder,newData.timestamp);
		} else {
			elEncoder.setValue(null);
		}
		if (newData.azCommandedValid) {
			commandAz.setValue(newData.azCommanded,newData.timestamp);
		} else {
			commandAz.setValue(null);
		}
		if (newData.elCommandedValid) {
			commandEl.setValue(newData.elCommanded,newData.timestamp);
		} else {
			commandEl.setValue(null);
		}
		if (newData.azPositionsValid) {
			azPos.setValue(newData.azPosition,newData.timestamp);
		} else {
			azPos.setValue(null);
		}
		if (newData.elPositionsValid) {
			elPos.setValue(newData.elPosition,newData.timestamp);
		} else {
			elPos.setValue(null);
		}
		acuPointingModel.setValue(newData.pointingModel, newData.timestamp);
		acuAuxPointingModel.setValue(newData.auxPointingModel, newData.timestamp);
		onTarget.setValue(newData.onSource, newData.timestamp);
	}

	public ValueHolder<Boolean> getAcuPointingModel() {
		return acuPointingModel;
	}
	
	public ValueHolder<Double> getAzEncoder() {
		return azEncoder;
	}

	public ValueHolder<Double> getAzPos() {
		return azPos;
	}

	public ValueHolder<Double> getCommandAz() {
		return commandAz;
	}

	public ValueHolder<Double> getCommandEl() {
		return commandEl;
	}

	public ValueHolder<Double> getElEncoder() {
		return elEncoder;
	}

	public ValueHolder<Double> getElPos() {
		return elPos;
	}

	public ValueHolder<Boolean> getOnTarget() {
		return onTarget;
	}

	public ValueHolder<Boolean> getAcuAuxPointingModel() {
		return acuAuxPointingModel;
	}
	
}
