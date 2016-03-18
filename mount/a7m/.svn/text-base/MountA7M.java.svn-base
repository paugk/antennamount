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
package alma.control.gui.antennamount.mount.a7m;

import org.omg.CORBA.LongHolder;

import alma.acs.container.ContainerServices;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.ACSComponentsManager;
import alma.control.gui.antennamount.mount.IMetrology;
import alma.control.gui.antennamount.mount.ShutterCommon;
import alma.control.gui.antennamount.mount.ISubreflector;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.mount.a7m.Metrology;

/**
 * Objects of this class holds a MountVertex component
 * and execute the methods on the remote object 
 * 
 * @author acaproni
 *
 */
public final class MountA7M extends Mount {
	
	/**
	 * ACU error descriptions stuffs
	 */
	private static final String[] acuStates = {
/*
Error condition indicated as follows: 
byte 0 (uint8): Error code: 
 0x00: No error; 
 0x01: Timeout 
 0x02: Invalid mode change requested 
 0x03: Requested position out of range 
 0x04: Requested velocity out of range 
 0x05: ACU in Local Access Mode 
 0x06: Invalid brake command requested 
 0x10: Illegal command or monitor requested 
           (undefined CAN ID) 
 0x11: Unexpected command or monitor requested 
           (if a command arrives when it is not allowed) 
 0x12: Parameter out of range 
 0x13: Invalid data length of command 
 0x14: Trajectory command delayed 
           (if the trajectory command for TE i+2 arrives after TE i+24ms) 
 0x15: Trajectory command duplicate error 
           (if two trajectory commands arrive within the same TE) 
 0x16: Error stack overflow 
 0x17: Invalid operation requested 
        (eg. Stow-pin insert is commanded but antenna is not in stow position

*/ 

		alma.Control.MountA7M.ACU_ERROR_DESC_00,
		alma.Control.MountA7M.ACU_ERROR_DESC_01,
		alma.Control.MountA7M.ACU_ERROR_DESC_02,
		alma.Control.MountA7M.ACU_ERROR_DESC_03,
		alma.Control.MountA7M.ACU_ERROR_DESC_04,
		alma.Control.MountA7M.ACU_ERROR_DESC_05,
		alma.Control.MountA7M.ACU_ERROR_DESC_06,
		// 0x07..0x0F are not assigned
		" "," "," "," "," "," "," "," "," ",
		alma.Control.MountA7M.ACU_ERROR_DESC_10,
		alma.Control.MountA7M.ACU_ERROR_DESC_11,
		alma.Control.MountA7M.ACU_ERROR_DESC_12,
		alma.Control.MountA7M.ACU_ERROR_DESC_13,
		alma.Control.MountA7M.ACU_ERROR_DESC_14,
		alma.Control.MountA7M.ACU_ERROR_DESC_15,
		alma.Control.MountA7M.ACU_ERROR_DESC_16,
		alma.Control.MountA7M.ACU_ERROR_DESC_17
	};
	
	
	/**
	 * The A7M mount
	 */
	private final alma.Control.MountA7M a7m;
	
	/**
	 * The state of the mount
	 */
	private ValueHolder<int[]> status = new ValueHolder<int[]>();
	private ValueHolder<int[]> status2 = new ValueHolder<int[]>();
	
	/**
	 * The status of AZ
	 */
	private ValueHolder<int[]> azStatus = new ValueHolder<int[]>();
	private ValueHolder<int[]> azStatus2 = new ValueHolder<int[]>();
	
	/**
	 * The status of EL
	 */
	private ValueHolder<int[]> elStatus = new ValueHolder<int[]>();
	private ValueHolder<int[]> elStatus2 = new ValueHolder<int[]>();
	
	/**
	 * The power status
	 */
	private ValueHolder<int[]> powerStatus = new ValueHolder<int[]>();
	
	/**
	 * The shutter
	 */
	private  Shutter shutter = null;
	
	/**
	 * The subreflector
	 */
	private A7MSubreflector subreflector;
	
	/**
	 * The metrology
	 */
	private Metrology metrology;
	
	/** 
	 * Constructor
	 * 
	 * @param mount The remote component
	 * @param type The type of this mount component
	 * @param contSvcs The ContainerServices
	 * @param rootP The AntennaRootPane
	 */
	public MountA7M(
			alma.Control.MountA7M mnt, 
			ACSComponentsManager.AntennaType type, 
			ContainerServices contSvcs,
			AntennaRootPane rootP) {
		super(mnt,type,contSvcs,rootP);
		if (type!=ACSComponentsManager.AntennaType.MELCOA7M) {
			throw new IllegalStateException("The component is MountA7M but the passed type is "+type);
		}
		for (Integer t=0; t<acuStates.length; t++) {
			acuStateDescriptor.put(t, acuStates[t]);
		}
		a7m=mnt;
		shutter = new Shutter(a7m,listenersNotifier,logger);
		subreflector = new A7MSubreflector(mnt,antennaRootP,logger);
		metrology = new Metrology(a7m, logger,listenersNotifier);
		
		// Start the thread to update the state of the mount
		setName("MountA7M");
		rootP.getHeartbeatChecker().register(this);
		start();
	}
		
	/**
	 * Update the status of the component. Called by the thread
	 * 
	 * @param errState The error state of execution
	 */
	public void update(UpdateError errState) throws Exception {
		antennaRootP.getHeartbeatChecker().ping(this);
		
		// Used to check if the component is slow answering
		long preReadTime;
		
		// System status
		LongHolder time=new LongHolder();
		
		preReadTime = System.currentTimeMillis();
		try {
			int vals[]=a7m.GET_SYSTEM_STATUS(time);
			status.setValue(vals, time.value);
		} catch (Throwable t) {
			status.setValue(null);
			errState.addError(t);
		}
		try {
			int vals[]=a7m.GET_SYSTEM_STATUS_2(time);
			status2.setValue(vals, time.value);
		} catch (Throwable t) {
			status2.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// EL status
		preReadTime = System.currentTimeMillis();
		try {
			int vals[]=a7m.GET_EL_STATUS(time);
			elStatus.setValue(vals, time.value);
		} catch (Throwable t) {
			elStatus.setValue(null);
			errState.addError(t);
		}
		try {
			int vals[]=a7m.GET_EL_STATUS_2(time);
			elStatus2.setValue(vals, time.value);
		} catch (Throwable t) {
			elStatus2.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// AZ status
		preReadTime = System.currentTimeMillis();
		try {
			int vals[]=a7m.GET_AZ_STATUS(time);
			azStatus.setValue(vals, time.value);
		} catch (Throwable t) {
			azStatus.setValue(null);
			errState.addError(t);
		}
		try {
			int vals[]=a7m.GET_AZ_STATUS_2(time);
			azStatus2.setValue(vals, time.value);
		} catch (Throwable t) {
			azStatus2.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// Shutter
		preReadTime = System.currentTimeMillis();
		try {
			Integer status=a7m.GET_SHUTTER(time);
			shutter.updateStatus(status, time.value);
		} catch (Throwable t) {
			shutter.updateStatus(null,0L);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// Power status
		preReadTime = System.currentTimeMillis();
		try {
			int vals[]=a7m.GET_POWER_STATUS(time);
			powerStatus.setValue(vals, time.value);
		} catch (Throwable t) {
			powerStatus.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// Subreflector
		subreflector.refresh(errState);
		
		// Metrology
		metrology.refresh(errState);
	}
	
	public ValueHolder<int[]> getStatus() {
		return status;
	}
	public ValueHolder<int[]> getStatus2() {
		return status2;
	}
	
	public ValueHolder<int[]> getAzStatus() {
		return azStatus;
	}
	public ValueHolder<int[]> getAzStatus2() {
		return azStatus2;
	}

	public ValueHolder<int[]> getElStatus() {
		return elStatus;
	}
	public ValueHolder<int[]> getElStatus2() {
		return elStatus2;
	}
	
	public ShutterCommon getShutter() {
		return shutter;
	}

	public ISubreflector getSubreflector() {
		return subreflector;
	}
	
	public IMetrology getMetrology() {
		return metrology;
	}
	
	/**
	 * @see Mount#getPowerStatus()
	 */
	public ValueHolder<int[]> getPowerStatus() {
		return powerStatus;
	}
}
