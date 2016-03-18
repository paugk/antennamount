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
package alma.control.gui.antennamount.mount.aem;

import org.omg.CORBA.LongHolder;

import alma.acs.container.ContainerServices;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.ACSComponentsManager;
import alma.ControlGUIErrType.wrappers.AcsJMountEx;
import alma.control.gui.antennamount.mount.IMetrology;
import alma.control.gui.antennamount.mount.ShutterCommon;
import alma.control.gui.antennamount.mount.ISubreflector;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.acs.logging.AcsLogLevel;

/**
 * Objects of this class holds a MountAEM component
 * and execute the methods on the remote object 
 * 
 * @author acaproni
 *
 */
public final class MountAEM extends Mount {
	
	/**
	 * ACU error descriptions stuffs
	 */
	private static final String[] acuStates = {
/*		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_00,
		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_01,
		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_02,
		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_03,
		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_04,
		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_05,
		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_06,
		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_07,
		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_08,
		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_09,
		alma.Control.MountAEMPrototype.ACU_ERROR_DESC_0A
*/
	};
	
	
	/**
	 * The AEM prototype mount
	 */
	private final alma.Control.MountAEM aem;
	
	/**
	 * The state of the mount
	 */
	private ValueHolder<int[]> status = new ValueHolder<int[]>();
	
	/**
	 * The status of EL
	 */
	private ValueHolder<int[]> azStatus = new ValueHolder<int[]>();
	
	/**
	 * The status of EL
	 */
	private ValueHolder<int[]> elStatus = new ValueHolder<int[]>();
	
	/**
	 * The power status
	 */
	private ValueHolder<int[]> powerStatus = new ValueHolder<int[]>();
	
	/**
	 * The shutter
	 */
	private  final Shutter shutter;
	
	/**
	 * The subreflector
	 */
	private AEMSubreflector subreflector;
	
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
	public MountAEM(
			alma.Control.MountAEM mnt, 
			ACSComponentsManager.AntennaType type, 
			ContainerServices contSvcs,
			AntennaRootPane rootP) {
		super(mnt,type,contSvcs,rootP);
		if (type!=ACSComponentsManager.AntennaType.ALCATEL) {
			throw new IllegalStateException("The component is a AEM but the passed type is "+type);
		}
		for (Integer t=0; t<acuStates.length; t++) {
			acuStateDescriptor.put(t, acuStates[t]);
		}
		aem=mnt;
		shutter = new Shutter(aem,listenersNotifier,logger);
		subreflector = new AEMSubreflector(mnt,antennaRootP,logger);
		metrology = new Metrology(aem, logger,listenersNotifier);
		
		// Start the thread to update the state of the mount
		setName("MountAEM");
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
			int vals[]=aem.GET_SYSTEM_STATUS(time);
			status.setValue(vals, time.value);
		} catch (Throwable t) {
			status.setValue(null);
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
			int vals[]=aem.GET_EL_STATUS(time);
			elStatus.setValue(vals, time.value);
		} catch (Throwable t) {
			elStatus.setValue(null);
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
			int vals[]=aem.GET_AZ_STATUS(time);
			azStatus.setValue(vals, time.value);
		} catch (Throwable t) {
			azStatus.setValue(null);
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
			Integer status=aem.GET_SHUTTER(time);
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
			int vals[]=aem.GET_POWER_STATUS(time);
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
	
	public ValueHolder<int[]> getAzStatus() {
		return azStatus;
	}

	public ValueHolder<int[]> getElStatus() {
		return elStatus;
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
	
	/**
	 * Int AZ encoder
	 * 
	 * @return A unique identifier for this operation
	 * 
	 */
	public synchronized long initAzEncoder() {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to clear the init
		class InitEncoderThread implements Runnable {
			public long uid;
			public void run() {
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,"Initing AZ encoder with ID="+uid);
				try {
					aem.SET_INIT_AZ_ENC_ABS_POS();
					logger.log(AcsLogLevel.DEBUG,"AZ encoder inited with ID="+uid);
					listenersNotifier.commandExecuted(uid,"Init AZ Encoder",null,null);
				} catch (Throwable t) {
					String msg="Error initing AZ encoder with ID="+uid;
					AcsJMountEx ex = new AcsJMountEx(t);
					ex.setOperation(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,"Init AZ encoder","Error from the component initing AZ encoder",ex);
				}
			}
		};
		// Setup and start the thread
		InitEncoderThread thread =new InitEncoderThread();
		thread.uid=id;
		Thread t = new Thread(thread,"Mount:initAzEncoder");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Int EL encoder
	 * 
	 * @return A unique identifier for this operation
	 * 
	 */
	public synchronized long initElEncoder() {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to clear the init
		class InitEncoderThread implements Runnable {
			public long uid;
			public void run() {
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,"Initing EL encoder with ID="+uid);
				try {
					aem.SET_INIT_EL_ENC_ABS_POS();
					logger.log(AcsLogLevel.DEBUG,"EL encoder inited with ID="+uid);
					listenersNotifier.commandExecuted(uid,"Init EL encoder",null,null);
				} catch (Throwable t) {
					String msg="Error initing EL encoder with ID="+uid;
					AcsJMountEx ex = new AcsJMountEx(t);
					ex.setOperation(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,"Init EL encoder","Error from the component initing EL encoder",ex);
				}
			}
		};
		// Setup and start the thread
		InitEncoderThread thread =new InitEncoderThread();
		thread.uid=id;
		Thread t = new Thread(thread,"Mount:initELEncoder");
		t.setDaemon(true);
		t.start();
		return id;
	}
}
