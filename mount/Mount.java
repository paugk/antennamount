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

import org.omg.CORBA.LongHolder;

import alma.Control.HardwareDevicePackage.HwState;
import alma.Control.MountPackage.AxisMode;
import alma.Control.MountPackage.AxisModeHolder;
import alma.Control.MountPackage.BrakesStatus;
import alma.Control.MountPackage.ShutterMode;
import alma.ControlGUIErrType.wrappers.AcsJMountEx;
import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.Control.MountStatusData;
import alma.acs.container.ContainerServices;
import alma.acs.logging.AcsLogLevel;
import alma.control.gui.antennamount.AntennaRootPane;


/**
 * The class that owns a Mount, read all the values,
 * execute all the methods on the remote CORBA object.
 * 
 * The thread continuously read values from the mount
 *
 * The values read from and sent to the component are not translated
 * in human readable format.
 *  
 */
public abstract class Mount extends MountCommom {
	
	/**
	 * The type of this antenna
	 */
	private ACSComponentsManager.AntennaType mountType; 
	
	/**
	 * The CORBA component
	 * It is null if an error arise somewhere and the component is not 
	 * responding/reachable
	 */
	//private volatile alma.Control.Mount mount;
	protected volatile alma.Control.Mount mount;
	
	/**
	 * The mount status data of this mount
	 */
	private final MountStatus mountStatus=new MountStatus();
	
	
	///////////////////////////////////////////////////////
	// Props read from the component 
	
	/**
	 * Elevation axis
	 */
	private final ValueHolder<AxisMode> elAxisMode = new ValueHolder<AxisMode>();
	
	/**
	 * Azimuth axis
	 */
	private final ValueHolder<AxisMode> azAxisMode = new ValueHolder<AxisMode>();
	
	/**
	 * The ABM pointing model
	 */
	private final ValueHolder<Boolean> abmPointingModel= new ValueHolder<Boolean>();
	
	/**
	 * Azimuth brake
	 */
	private final ValueHolder<BrakesStatus> azBrake= new ValueHolder<BrakesStatus>();
	
	/**
	 * Elevation brake
	 */
	private final ValueHolder<BrakesStatus> elBrake= new ValueHolder<BrakesStatus>();
	
	/**
	 * The local access mode (local=true)
	 */
	private final ValueHolder<Boolean> localAccessMode = new ValueHolder<Boolean>();
	
	/**
	 * The ACU error
	 */
	private final ValueHolder<int[]> acuError = new ValueHolder<int[]>();
	
	/**
	 * ACU error descriptions stuffs.  To be replaced when the code generator will be used
	 */
	protected final StatusDescriptorHelper<Integer> acuStateDescriptor = new StatusDescriptorHelper<Integer>();
	
	/**
	 * The tolerance
	 */
	private final ValueHolder<Double>tolerance = new ValueHolder<Double>();
	
	/**
	 * The state of the HW
	 */
	private final ValueHolder<HwState> hwState=new ValueHolder<HwState>();
	
	/**
	 * The mount is moveable
	 */
	private final ValueHolder<Boolean> moveable = new ValueHolder<Boolean>();
	
	/**
	 * The mount is shutdown
	 */
	private final ValueHolder<Boolean> shutdown = new ValueHolder<Boolean>();
	
	/**
	 * The mount is standby
	 */
	private final ValueHolder<Boolean> standby = new ValueHolder<Boolean>();
	
	/**
	 * The name of the component
	 */
	private final String componentName;
	
	//
	///////////////////////////////////////////////////////
	
	/**
	 * Constructor.
	 * 
	 * @param mountComponent The MOUNT component
	 * @param type The string describing the type of this antenna
	 * @param contSvcs The ContainerServices
	 * @param rootP The AntennaRootPane
	 */
	public Mount(alma.Control.Mount mountComponent, ACSComponentsManager.AntennaType type, ContainerServices contSvcs, AntennaRootPane rootP) {
		super(contSvcs,rootP);
		if (mountComponent==null) {
			throw new IllegalArgumentException("Null mount in constructor");
		}
		if (type==null) {
			throw new IllegalArgumentException("Invalid null antenna type in constructor");
		}
		mount=mountComponent;
		mountType=type;
		componentName=mount.name();
	}
	
	/**
	 * Release the resources and close the thread
	 */
	public void close() {
		super.close();
		mount=null;
	}
	
	/**
	 * 
	 * @return A reference to the CORBA mount component
	 */
	public alma.Control.Mount getMount() {
		return mount;
	}
	
	/**
	 * Update the component status.
	 * It must be defined by each specialized type in order to reads the state.
	 * 
	 * @param errState
	 */
	protected abstract void update(UpdateError errState) throws Exception;
	
	/**
	 * Update the status of the properties by polling the component. 
	 * It is called by the thread.
	 * 
	 * @param errState The error state of execution
	 */
	public void updateComponentStatus(UpdateError errState) throws AcsJMountGUIErrorEx {
		antennaRootP.getHeartbeatChecker().ping(this);
		//	Used to check if the component is slow answering
		long preReadTime;
		
		// Mount hardware status
		boolean isStopped=false;
		preReadTime = System.currentTimeMillis();
		try {
			HwState state = mount.getHwState();
			hwState.setValue(state);
			isStopped=(state==HwState.Stop);
		} catch (Throwable t) {
			hwState.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		if (isStopped) {
			// The hardware is stopped then there is no point in updating the values
			return;
		}
	
		// Mount status
		preReadTime = System.currentTimeMillis();
		try {
			MountStatusData mountData=mount.getMountStatusData();
			mountStatus.update(mountData);
		} catch (Throwable t) {
			mountStatus.update(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// SHUTDOWN
		try {
			shutdown.setValue(Boolean.valueOf(mount.inShutdownMode()));
		}  catch (Throwable t) {
			shutdown.setValue(null);
			errState.addError(t);
		}
		
		// STANDBY
		try {
			standby.setValue(Boolean.valueOf(mount.inStandbyMode()));
		}  catch (Throwable t) {
			standby.setValue(null);
			errState.addError(t);
		}
		
		// MOVEABLE
		try {
			moveable.setValue(Boolean.valueOf(mount.isMoveable()));
		}  catch (Throwable t) {
			moveable.setValue(null);
			errState.addError(t);
		}
		
		// Axis
		preReadTime = System.currentTimeMillis();
		AxisModeHolder elH=new AxisModeHolder();
		AxisModeHolder azH=new AxisModeHolder();
		try {
			mount.getAxisMode(azH,elH);
			elAxisMode.setValue(elH.value);
			azAxisMode.setValue(azH.value);
		} catch (Throwable t) {
			elAxisMode.setValue(null);
			azAxisMode.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		LongHolder val=new LongHolder();
		
		// AZ BRAKE
		preReadTime = System.currentTimeMillis();
		try {
			BrakesStatus azBrakeState=mount.GET_AZ_BRAKE(val);
			azBrake.setValue(azBrakeState);
		} catch (Throwable t) {
			azBrake.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// EL BRAKE
		preReadTime = System.currentTimeMillis();
		try {
			BrakesStatus elBrakeState= mount.GET_EL_BRAKE(val);
			elBrake.setValue(elBrakeState);
		} catch (Throwable t) {
			elBrake.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}

		// Local access mode
		preReadTime = System.currentTimeMillis();
		boolean localAccess;
		try {
			localAccess=mount.inLocalMode();
			localAccessMode.setValue(localAccess);
		}	catch (Throwable t) {
				// Set the value as unavailable
				localAccessMode.setValue(null);
				errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// ABM pointing model
		preReadTime = System.currentTimeMillis();
		boolean pointingModel;
		try {
			pointingModel=mount.isPointingModelEnabled();
			abmPointingModel.setValue(pointingModel);
		}	catch (Throwable t) {
			// Set the value as unavailable
			abmPointingModel.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// ACU error
		preReadTime = System.currentTimeMillis();
		try {
			LongHolder timestamp=new LongHolder();
			int[] acuErr=mount.GET_ACU_ERROR(timestamp);
			acuError.setValue(acuErr, timestamp.value);
		} catch (Throwable t) {
			acuError.setValue(null);
			AcsJMountEx ex = new AcsJMountEx(t);
			ex.setOperation("Error getting ACU_ERROR");
			errState.addError(ex);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// Tolerance
		preReadTime = System.currentTimeMillis();
		try {
			tolerance.setValue(mount.getTolerance());
		}	catch (Throwable t) {
			// Set the value as unavailable
			tolerance.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		if (terminateThread) {
			return;
		}
		
		// Update other component state properties
		try {
			update(errState);
		} catch (Throwable t) {
			AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
			ex.setContextDescription("Error updating mount state");
			ex.setProperty("Antenna type", mountType.description);
		}
	}

	public ValueHolder<AxisMode> getAzAxisMode() {
		return azAxisMode;
	}

	public ValueHolder<AxisMode> getElAxisMode() {
		return elAxisMode;
	}
	
	/**
	 * Set the azimuth to the given state
	 *
	 * @param status The new status for the azimuth
	 */
	public synchronized long setAzStatus(final AxisMode mode) {
		if (	mode!=AxisMode.SHUTDOWN_MODE &&
				mode!=AxisMode.STANDBY_MODE &&
				mode!=AxisMode.AUTONOMOUS_MODE) {
			throw new IllegalArgumentException("Invalid status for azimuth: "+mode);
		}
		// Get a unique a ID for this command
		long uid = getOpUID();
		class SetAzStatus implements Runnable {
			public long uid;
			public void run() {
				// Get a unique a ID for this command
				logger.log(AcsLogLevel.DEBUG,"Setting azimuth axis mode to "+mode + " with ID=" + uid);
				try {
					mount.setAzAxisMode(mode);
				} catch (Throwable t) {
					String msg = "Error setting azimuth mode to "+mode + " with ID=" + uid;
					logger.log(AcsLogLevel.ERROR,msg);
					AcsJMountEx ex = new AcsJMountEx(t);
					ex.setOperation(msg);
					logger.log(AcsLogLevel.ERROR, msg);
					listenersNotifier.commandExecuted(uid, "Azimuth status set to "+mode, "Error from remote component while setting the azimuth state to "+mode,ex);
				}
				logger.log(AcsLogLevel.DEBUG,"Azimuth axis mode set to "+mode + " with ID=" + uid);
			}
		};
		SetAzStatus setAz = new SetAzStatus();
		setAz.uid=uid;
		Thread t = new Thread(setAz,"Mount:setAzStatus");
		t.setDaemon(true);
		t.start();
		return uid;
	}
	
	/**
	 * Set the elevation to the given state
	 * 
	 * @param status The new status for the elevation
	 */
	public synchronized long setElStatus(final AxisMode mode) {
		if (	mode!=AxisMode.SHUTDOWN_MODE &&
				mode!=AxisMode.STANDBY_MODE &&
				mode!=AxisMode.AUTONOMOUS_MODE) {
			throw new IllegalArgumentException("Invalid status for elevation: "+mode);
		}
		// Get a unique a ID for this command
		long uid = getOpUID();
		class SetElStatus implements Runnable {
			public long uid;
			public void run() {
				logger.log(AcsLogLevel.DEBUG,"Setting elevaton axis to "+mode);
				try {
					mount.setElAxisMode(mode);
				} catch (Throwable t) {
					String msg = "Error setting elevation mode to "+mode + " with ID=" + uid;
					logger.log(AcsLogLevel.ERROR,msg);
					AcsJMountEx ex = new AcsJMountEx(t);
					ex.setOperation(msg);
					logger.log(AcsLogLevel.ERROR, msg);
					listenersNotifier.commandExecuted(uid, "Elevation status set to "+mode, "Error from remote component while setting the elevation state to "+mode,ex);
				}
				logger.log(AcsLogLevel.DEBUG,"Elevation axis mode set to "+mode);		
			}
		}
		SetElStatus setEl = new SetElStatus();
		setEl.uid=uid;
		Thread t = new Thread(setEl,"Mount:setElStatus");
		t.setDaemon(true);
		t.start();
		return uid;
	}
	
	/**
	 * <code>true</code> if the axes are in shutdown.
	 * <P>
	 * <b>Note</b>: the value returned by this method is read
	 * 			from a {@link ValueHolder} refreshed by the thread.
	 * 			It means that it reflects the state of the axis
	 * 			at the time of the last iteration of the thread
	 * 
	 * @return <code>true</code> if the axes are in shutdown
	 */
	public Boolean inShutdown() {
		return shutdown.getValue();
	}
	
	/**
	 * <code>true</code> if the axes are in standby
	 * <P>
	 * <b>Note</b>: the value returned by this method is read
	 * 			from a {@link ValueHolder} refreshed by the thread.
	 * 			It means that it reflects the state of the axis
	 * 			at the time of the last iteration of the thread
 
	 * 
	 * @return <code>true</code> if the axes are in standby
	 */
	public Boolean inStandby() {
		return standby.getValue();
	}
	
	/**
	 * Check if the antenna is moveable i.e.;
	 *   - both axes in encoder (without ACU PM loaded)
	 *   - both axes in autonomous (with ACU PM enabled)
	 * <P>
	 * <b>Note</b>: the value returned by this method is read
	 * 			from a {@link ValueHolder} refreshed by the thread.
	 * 			It means that it reflects the state of the axis
	 * 			at the time of the last iteration of the thread
  
	 * 
	 * @return <code>true</code> if the axes are in encoder/autonomous mode
	 */
	public Boolean isMoveable() {
		return moveable.getValue();
	}
	
	/**
	 * Open/close the shutter
	 * 
	 * @param open The mode (open or close) to set 
	 */
	public long setShutter(boolean open) {
		// Get a unique a ID for this command
		long id = getOpUID();
		// Define the thread to set the shutter
		class SetShutter implements Runnable {
			public long uid;
			public boolean openAction;

			public void run() {
				ShutterMode newMode = (openAction)?ShutterMode.SHUTTER_OPEN: ShutterMode.SHUTTER_CLOSED;
				String actionDesc = (openAction)?"Open": "Close";
				// Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG, "Setting the shutter to "	+ actionDesc + " with ID=" + uid);
				try {
					mount.SET_SHUTTER(newMode);
					logger.log(AcsLogLevel.DEBUG, "Shutter set to " + actionDesc	+ " ID=" + uid + " done");
					listenersNotifier.commandExecuted(uid, "Shutter set to "+actionDesc,null,null);
				} catch (Throwable t) {
					String msg = "Error setting the shutter to " + actionDesc + " with ID=" + uid;
					AcsJMountEx ex = new AcsJMountEx(t);
					ex.setOperation(msg);
					logger.log(AcsLogLevel.ERROR, msg);
					listenersNotifier.commandExecuted(uid, "Shutter set to "+actionDesc, "Error from remote component while setting the shutter to "+actionDesc,ex);
				}
			}
		}
		
		// Setup and start the thread
		SetShutter thread = new SetShutter();
		thread.uid = id;
		thread.openAction = open;
		Thread t = new Thread(thread,"Mount:setShutter");
		t.setDaemon(true);
		t.start();
		return id;		
	}
	
	/**
	 * Exit from the trajectory mode
	 * 
	 * @return A unique identifier for this operation
	 * 
	 */
	public synchronized long clearFault() {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to clear the fault
		class ClearFault implements Runnable {
			public long uid;
			public void run() {
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,"Clearing the fault with ID="+uid);
				try {
					mount.SET_CLEAR_FAULT_CMD();
					logger.log(AcsLogLevel.DEBUG,"Fault cleared with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,"Clear fault",null,null);
				} catch (Throwable t) {
					String msg="Error clearing the fault with ID="+uid;
					AcsJMountEx ex = new AcsJMountEx(t);
					ex.setOperation(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,"Clear fault", "Error from remote component",ex);
				}
			}
		};
		// Setup and start the thread
		ClearFault thread =new ClearFault();
		thread.uid=id;
		Thread t = new Thread(thread,"Mount:clearFault");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Set a new tolerance
	 * 
	 * @param d The new tolerance
	 * @return A unique identifier for this operation
	 * 
	 */
	public synchronized long setTolerance(double d) {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to clear the fault
		class SetTolerance implements Runnable {
			public long uid;
			public double theTolerance;
			public void run() {
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,"Setting tolerance ID="+uid);
				try {
					mount.setTolerance(theTolerance);
					logger.log(AcsLogLevel.DEBUG,"Tolerance set to "+theTolerance+" with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,"SetTolerance",null,null);
				} catch (Throwable t) {
					String msg="Error setting the tolerance to "+theTolerance+" with ID="+uid;
					AcsJMountEx ex = new AcsJMountEx(t);
					ex.setOperation(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,"SetTolerance","Error from the component while setting the tolerance to "+theTolerance,ex);
				}
			}
		};
		// Setup and start the thread
		SetTolerance thread =new SetTolerance();
		thread.theTolerance=d;
		thread.uid=id;
		Thread t = new Thread(thread,"Mount:setTolerance");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	public ValueHolder<Boolean> getAcuPointingModel() {
		return mountStatus.getAcuPointingModel();
	}
	
	public ValueHolder<Boolean> getAuxAcuPointingModel() {
		return mountStatus.getAcuAuxPointingModel();
	}

	public ValueHolder<int[]> getAcuError() {
		return acuError;
	}

	public ValueHolder<Boolean> getLocalAccessMode() {
		return localAccessMode;
	}

	public ValueHolder<BrakesStatus> getAzBrake() {
		return azBrake;
	}

	public ValueHolder<BrakesStatus> getElBrake() {
		return elBrake;
	}

	public ACSComponentsManager.AntennaType getMountType() {
		return mountType;
	}

	public ValueHolder<Boolean> getAbmPointingModel() {
		return abmPointingModel;
	}
	
	public ValueHolder<Double> getTolerance() {
		return tolerance;
	}
	
	/**
	 * @return the hwState
	 */
	public ValueHolder<HwState> getHwState() {
		return hwState;
	}
	
	/**
	 * Return the description of the ACU state with the given code.
	 * 
	 * @param actAcuState The ACU state
	 * @return The description of the state
	 */
	public String getAcuStateDescr(int actualAcuState) {
		return acuStateDescriptor.getDescription(actualAcuState);
	}
	
	/**
	 * Return the name of the component 
	 * 
	 *  @return The name of the connected component
	 */
	public String getComponentName() {
		return componentName;
	}
	
	/**
	 * Return the shutter
	 * 
	 * @return The shutter
	 */
	public abstract ShutterCommon getShutter();
	
	/**
	 * Return the metrology
	 */
	public abstract IMetrology getMetrology();
	
	/**
	 * Return the subreflector
	 * 
	 * @return The subreflector
	 */
	public abstract ISubreflector getSubreflector();
	
	public abstract ValueHolder<int[]> getStatus();
	
	public abstract ValueHolder<int[]> getAzStatus();

	public abstract ValueHolder<int[]> getElStatus();
	
	/**
	 * @return the POWER_STATUS monitor point
	 * @return
	 */
	public abstract ValueHolder<int[]> getPowerStatus();

}
