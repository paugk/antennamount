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

import alma.Control.EquatorialDirection;
import alma.Control.HorizonDirection;
import alma.Control.Offset;
import alma.Control.AntModeControllerPackage.Status;
import alma.Control.MountControllerPackage.PointingData;
import alma.acs.container.ContainerServices;
import alma.acs.logging.AcsLogLevel;
import alma.control.gui.antennamount.AntennaRootPane;

import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.ControlGUIErrType.wrappers.AcsJPointingDataEx;
import alma.ControlGUIErrType.wrappers.AcsJSourceNotVisibleEx;

/**
 * The NewMountController object.
 * It owns a NewMountController, send commands and read
 * properties.
 * 
 * This class can't be extended because the thread is started in the constructor.
 * 
 * @author acaproni
 *
 */
public final class MountController  extends MountCommom {
	
	/**
	 * The MountController
	 */
	private alma.Control.MountController mountController=null;
	
	// AZ/EL
	private ValueHolder<Double> actualAz = new ValueHolder<Double>();
	private ValueHolder<Double> actualEl= new ValueHolder<Double>();
	private ValueHolder<Double> commandAz = new ValueHolder<Double>();
	private ValueHolder<Double> commandEl = new ValueHolder<Double>();
	
	// RA/DEC
	private ValueHolder<Double> actualRA = new ValueHolder<Double>();
	private ValueHolder<Double> actualDec = new ValueHolder<Double>();
	private ValueHolder<Double> commandRA = new ValueHolder<Double>();
	private ValueHolder<Double> commandDec = new ValueHolder<Double>();
	
	
	
	// Offsets
	private ValueHolder<Double> offsetAz = new ValueHolder<Double>();
	private ValueHolder<Double> offsetEl = new ValueHolder<Double>();
	private ValueHolder<Double> offsetRA = new ValueHolder<Double>();
	private ValueHolder<Double> offsetDec = new ValueHolder<Double>();
	
	/**
	 * JEpoch
	 */
	private ValueHolder<Double> epoch = new ValueHolder<Double>();
	
	/**
	 * The status (@see AntModeController)
	 */
	private ValueHolder<Status> status = new ValueHolder<Status>();
	
	/**
	 * isStopped: <code>true</code> if the mount is stopped
	 */
	private ValueHolder<Boolean> isStopped = new ValueHolder<Boolean>();
	
	/**
	 * isOnSource <code>true</code> if the mount is "on source"
	 */
	private ValueHolder<Boolean> isOnSource = new ValueHolder<Boolean>();
	
	/**
	 * The number of seconds before a tracked source sets.
	 * <P>
	 * The value of this variable is based on what is defined in <code>MountController.py</code>.
	 * However <code>MountController.timeToSet()</code> throws an exception if 
	 * invoked when the telescope is not tracking a Planet.
	 * This is not an error but a normal situation if the antenna is not tracking.
	 * To mask this to upper levels of the mount panel, the value of the double is:
	 * <UL>
	 * 	<LI>a positive number containing the number of secs before the source sets
	 * 	<LI>a number greater of 1E30 if the source never sets
	 *  <LI>a negative min number (<code>Double.NEGATIVE_INFINITY</code>) if the telescope is not tracking
	 * </UL> 
	 * @see <code>MountController.py</code> for further information on this method.
	 */
	private ValueHolder<Double>timeToSet = new ValueHolder<Double>();
	
	/**
	 * The name of the component
	 */
	private final String componentName;

	/**
	 * Constructor.
	 * 
	 * @param contSvcs The ContainerServices
	 * rootP The AntennaRootPane
	 */
	public MountController(alma.Control.MountController controllerComponent, ContainerServices contSvcs, AntennaRootPane rootP) {
		super(contSvcs, rootP);
		if (controllerComponent==null) {
			throw new IllegalArgumentException("Null controller in constructor");
		}
		mountController=controllerComponent;
		componentName=mountController.name();
		setName("MountController"); // Set the name of the thread
		rootP.getHeartbeatChecker().register(this);
		start();
	}
	
	/**
	 * Release the resources and close the thread
	 */
	public void close() {
		super.close();
		
		invalidatePointingData();
		status.setValue(null);
		epoch.setValue(null);
		
		mountController=null;
	}
	
	/**
	 * Invalidate all the pointing data variables by setting
	 * a <code>null</code> value
	 */
	private void invalidatePointingData() {
		commandAz.setValue(null);
		commandEl.setValue(null);
		actualAz.setValue(null);
		actualEl.setValue(null);
		commandRA.setValue(null);
		commandDec.setValue(null);
		actualRA.setValue(null);
		actualDec.setValue(null);
		offsetAz.setValue(null);
		offsetEl.setValue(null);
		offsetRA.setValue(null);
		offsetDec.setValue(null);
		isOnSource.setValue(null);
		isStopped.setValue(null);
	}
	
	/**
	 * Set the offsets in Az and El
	 * 
	 * @param offAz The new azimuth offset
	 * @param offEl The new elevation offset
	 * 
	 * @return A unique identifier for this operation
	 */
	public synchronized long offsetAzEl(double offAz, double offEl) {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the offsetAzEl
		class OffsetAzELClass implements Runnable {
			public long uid;
			double azimOffset,elevOffset;
			public void run() {
				String cmd = String.format("Setting AZ/EL offsets to [%.2f,%.2f]",azimOffset,elevOffset);
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid);
				try {
					mountController.setHorizonOffsetAsync(azimOffset,elevOffset);
					logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,cmd,null,null);
				} catch (Throwable t) {
					String msg="Error "+cmd+" with ID="+uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,cmd, "Error from remote component while setting AZ/EL offsets",ex);
				}
			}
		};
		// Setup and start the thread
		OffsetAzELClass thread =new OffsetAzELClass();
		thread.uid=id;
		thread.azimOffset=offAz;
		thread.elevOffset=offEl;
		Thread t = new Thread(thread,"MountController.offsetAzEl");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Increment the horizontal offset longitude
	 * 
	 * @param inc the increment to apply to the offset
	 * 
	 * @return A unique identifier for this operation
	 */
	public synchronized long incrementHorOffsetLong(final double inc) {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the offsetAzEl
		class IncHorOffsetLong implements Runnable {
			public long uid;
			public void run() {
				String cmd = String.format("Increment horizontal offsets longitude to [%.2f]",inc);
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid);
				try {
					mountController.incrementHorizonOffsetLongAsync(inc);
					logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,cmd,null,null);
				} catch (Throwable t) {
					String msg="Error "+cmd+" with ID="+uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,cmd, "Error from remote component while incrementing horizontal longitude offset",ex);
				}
			}
		};
		// Setup and start the thread
		IncHorOffsetLong thread =new IncHorOffsetLong();
		thread.uid=id;
		Thread t = new Thread(thread,"MountController.incHorOffsetLong");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Increment the horizontal offset latitude
	 * 
	 * @param inc the increment to apply to the offset
	 * 
	 * @return A unique identifier for this operation
	 */
	public synchronized long incrementHorOffsetLat(final double inc) {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the offsetAzEl
		class IncHorOffsetLat implements Runnable {
			public long uid;
			public void run() {
				String cmd = String.format("Increment horizontal offsets latitude to [%.2f]",inc);
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid);
				try {
					mountController.incrementHorizonOffsetLatAsync(inc);
					logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,cmd,null,null);
				} catch (Throwable t) {
					String msg="Error "+cmd+" with ID="+uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,cmd, "Error from remote component while incrementing horizontal latitude offset",ex);
				}
			}
		};
		// Setup and start the thread
		IncHorOffsetLat thread =new IncHorOffsetLat();
		thread.uid=id;
		Thread t = new Thread(thread,"MountController.incHorOffsetLat");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Increment the equatorial offset longitude
	 * 
	 * @param inc the increment to apply to the offset
	 * 
	 * @return A unique identifier for this operation
	 */
	public synchronized long incrementEqOffsetLong(final double inc) {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the offsetAzEl
		class IncEqOffsetLong implements Runnable {
			public long uid;
			public void run() {
				String cmd = String.format("Increment equatorial offsets longitude to [%.2f]",inc);
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid);
				try {
					mountController.incrementEquatorialOffsetLongAsync(inc);
					logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,cmd,null,null);
				} catch (Throwable t) {
					String msg="Error "+cmd+" with ID="+uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,cmd, "Error from remote component while incrementing equatorial longitude offset",ex);
				}
			}
		};
		// Setup and start the thread
		IncEqOffsetLong thread =new IncEqOffsetLong();
		thread.uid=id;
		Thread t = new Thread(thread,"MountController.incEqOffsetLong");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Increment the equatorial offset latitude
	 * 
	 * @param inc the increment to apply to the offset
	 * 
	 * @return A unique identifier for this operation
	 */
	public synchronized long incrementEqOffsetLat(final double inc) {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the offsetAzEl
		class IncEqOffsetLat implements Runnable {
			public long uid;
			public void run() {
				String cmd = String.format("Increment equatorial offsets latitude to [%.2f]",inc);
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid);
				try {
					mountController.incrementEquatorialOffsetLatAsync(inc);
					logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,cmd,null,null);
				} catch (Throwable t) {
					String msg="Error "+cmd+" with ID="+uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,cmd, "Error from remote component while incrementing equatorial latitude offset",ex);
				}
			}
		};
		// Setup and start the thread
		IncEqOffsetLat thread =new IncEqOffsetLat();
		thread.uid=id;
		Thread t = new Thread(thread,"MountController.incEqOffsetLat");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Set the offsets in RA and Dec
	 * 
	 * @param offRA The new right ascension offset
	 * @param offDec The new declination offset
	 * 
	 * @return A unique identifier for this operation
	 */
	public synchronized long offsetEquatorial(double offRA, double offDec) {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the offsetEquatorial
		class OffsetEqClass implements Runnable {
			public long uid;
			double raOffset,decOffset;
			public void run() {
				String cmd = "Setting RA/DEC offsets to ["+raOffset+", "+decOffset+"]";
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid);
				try {
					mountController.setEquatorialOffsetAsync(raOffset,decOffset);
					logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,cmd,null,null);
				} catch (Throwable t) {
					String msg=String.format("Error setting offset to [%.2f,%.2f]  with ID=%d",raOffset,decOffset,uid);
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,cmd, "Error from remote component while setting RA/DEC offsets",ex);
				}
			}
		};
		// Setup and start the thread
		OffsetEqClass thread =new OffsetEqClass();
		thread.uid=id;
		thread.raOffset=offRA;
		thread.decOffset=offDec;
		Thread t = new Thread(thread,"MountController.offsetEquatorial");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Execute the objstar to the controller.
	 * The operation is performed in a separate thread and the listener
	 * will be notified.
	 * 
	 * This operation is don in 2 steps:
	 *   1. set the epoch
	 *   2. call setDirection
	 * 
	 * @param ra The right ascension
	 * @param el The declination
	 * @param epoch jEpoch
	 * @param pmRA The pmRA
	 * @param pmDec The pmDec
	 * @param parallax The parallax
	 * @return A unique identifier for this operation
	 * 
	 */
	public synchronized long objstar(double ra, double dec, double epoch, double pmRA, double pmDec, double parallax) {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the objstar
		class ObjstarClass implements Runnable {
			public long uid;
			double rightA,declination,jepoch, pm_RA, pm_DEC, px;
			public void run() {
				String cmd = String.format("RA/Dec pointing to [%.2f,%.2f] pmRA=%.2f, pmDEC=%.2f parallax=%.2f", rightA,declination,pm_RA,pm_DEC,px);
				//	Launch the command in a separate thread
				try {
					// Set the epoch
					logger.log(AcsLogLevel.DEBUG,"Setting JEpoch to "+jepoch);
					mountController.setEpoch(jepoch);
					// Check if the object is observable
					logger.log(AcsLogLevel.DEBUG,"Checking if object is observable");
					boolean observable=mountController.isObservableEquatorial(rightA,declination, 0.0, pm_RA,pm_DEC,px);
					if (!observable) {
						throw new AcsJSourceNotVisibleEx();
					}
					// Set the direction
					logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid);
					mountController.track();
					mountController.setDirection(rightA, declination, pm_RA, pm_DEC, px);
					logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,cmd, null,null);
				} catch (Throwable t) {
					String msg="Error in "+cmd+" with ID="+uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,cmd,"Error from component while poinintg in RA/DEC",ex);
				}
			}
		};
		// Setup and start the thread
		ObjstarClass thread =new ObjstarClass();
		thread.uid=id;
		thread.rightA=ra;
		thread.declination=dec;
		thread.jepoch=epoch;
		thread.pm_RA=pmRA;
		thread.pm_DEC=pmDec;
		thread.px=parallax;
		Thread t = new Thread(thread,"MountController.objstar");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Delegate the objfix to the controller.
	 * The operation is performed in a separate thread and the listener
	 * will be notified.
	 * 
	 * @param az The azimuth (radians)
	 * @param el The elevation (radians)
	 * @return A unique identifier for this operation
	 * 
	 */
	public synchronized long objfix(double az, double el) {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the objfix
		class ObjfixClass implements Runnable {
			public long uid;
			double azim,elev;
			public void run() {
				String cmd = "AZ/EL pointing to ["+azim+", "+elev+"]";
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,cmd+" with ID="+uid);
				try {
				        mountController.track();
					mountController.setAzEl(azim,elev);
					logger.log(AcsLogLevel.DEBUG,cmd+ " with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,cmd,null,null);
				} catch (Throwable t) {
					String msg="Error pointing to ["+azim+", "+elev+"] with ID="+uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,cmd,"Error from component while poinintg in AZ/EL",ex);
				}
			}
		};
		// Setup and start the thread
		ObjfixClass thread =new ObjfixClass();
		thread.uid=id;
		thread.azim=az;
		thread.elev=el;
		Thread t = new Thread(thread,"MountController.objfix");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	public ValueHolder<Double> getCommandDec() {
		return commandDec;
	}
	
	public ValueHolder<Double> getCommandRA() {
		return commandRA;
	}
	
	public ValueHolder<Double> getActualRA() {
		return actualRA;
	}
	
	public ValueHolder<Double> getActualDec() {
		return actualDec;
	}
	
	public ValueHolder<Double> getOffsetAz() {
		return offsetAz;
	}
	
	public ValueHolder<Double> getOffsetEl() {
		return offsetEl;
	}
	
	/**
	 * Update the status of the component. Called by the thread
	 * 
	 * @param errState The error state of execution
	 */
	public void updateComponentStatus(UpdateError errState) {
		
		//	Used to check if the component is slow answering
		long preReadTime;
		
		try {
			updatePointingData();
		} catch (Throwable t) {
			errState.addError(t);
		}
		
		// Status
		preReadTime = System.currentTimeMillis();
		try {
			Status newStatus = mountController.getStatus();
			status.setValue(newStatus);
		} catch (Throwable t) {
			status.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		
		// Epoch
		preReadTime = System.currentTimeMillis();
		try {
			double ep = mountController.getEpoch();
			epoch.setValue(Double.valueOf(ep));
		} catch (Throwable t) {
			epoch.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
		
		// timeToSet
		preReadTime = System.currentTimeMillis();
		try {
			double newStatus = mountController.timeToSet();
			/////////////////////////////////////////////
			//// Check if the value is less the 0 ==> ERROR!
			/////////////////////////////////////////////
			timeToSet.setValue(newStatus);
		} catch (Exception e) {
			// This exception is thrown when the component
			// is not tracking a planet.
			// It is NOT an error
			timeToSet.setValue(Double.NEGATIVE_INFINITY);
		} catch (Throwable t) {
			timeToSet.setValue(null);
			errState.addError(t);
		}
		if (checkDelay(preReadTime)) {
			errState.addDelay();
		}
	}
	
	/**
	 * Update pointing data.
	 * <P>
	 * <code>MountController.getPointingData</code> returns all the pointing data
	 * synchronized.
	 * <P>
	 * This method gets the pointing data from the <code>MountController</code> and fills
	 * all the internal variables
	 * @throws AcsJPointingDataEx In case of error getting pointing data from the mount controller
	 */
	private void updatePointingData() throws AcsJPointingDataEx {
		PointingData pData;
		try {
			pData= mountController.getPointingData();
		} catch (Throwable t) {
			invalidatePointingData();
			throw new AcsJPointingDataEx(t);
		}
		
		// Commanded AZ/EL
                HorizonDirection pointing = pData.pointing;
		if (!pData.stopped) {
			HorizonDirection commanded = pData.commanded;
			commandAz.setValue(commanded.az+pointing.az , pData.timestamp);
			commandEl.setValue(commanded.el+pointing.el, pData.timestamp);
		} else {
			commandAz.setValue(null);
			commandEl.setValue(null);
		}
		
		// Actual AZ/EL
		HorizonDirection measured = pData.measured;
		actualAz.setValue(measured.az + pointing.az, pData.timestamp);
		actualEl.setValue(measured.el + pointing.el, pData.timestamp);
		
		// Commanded RA/DEC
		if (!pData.stopped) {
			EquatorialDirection target = pData.target;
			commandRA.setValue(target.ra, pData.timestamp);
			commandDec.setValue(target.dec, pData.timestamp);
		} else {
			commandRA.setValue(null);
			commandDec.setValue(null);
		}
		
		// Actual RA/DEC
		EquatorialDirection measuredTarget = pData.measuredTarget;
		actualRA.setValue(measuredTarget.ra, pData.timestamp);
		actualDec.setValue(measuredTarget.dec, pData.timestamp);
		
		// Horizontal offsets
		Offset hrOffsets = pData.horizon;
		offsetAz.setValue(hrOffsets.lng, pData.timestamp);
		offsetEl.setValue(hrOffsets.lat, pData.timestamp);
		
		// Equatorial offsets
		Offset eqOffsets = pData.equatorial;
		offsetRA.setValue(eqOffsets.lng, pData.timestamp);
		offsetDec.setValue(eqOffsets.lat, pData.timestamp);
		
		isStopped.setValue(pData.stopped, pData.timestamp);
		isOnSource.setValue(pData.onSource, pData.timestamp);
	}

	public ValueHolder<Double> getOffsetDec() {
		return offsetDec;
	}

	public ValueHolder<Double> getOffsetRA() {
		return offsetRA;
	}
	
	public ValueHolder<Status> getStatus() {
		return status;
	}
	
	/**
	 * Exit from the trajectory mode
	 * 
	 * @return A unique identifier for this operation
	 * 
	 */
	public synchronized long stopTrajectory() {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the stop
		class StopTrajectory implements Runnable {
			public long uid;
			public void run() {
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,"Exiting from trajectory mode with ID="+uid);
				try {
					mountController.stop();
					logger.log(AcsLogLevel.DEBUG,"Exited from trajectory mode with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,"Stop trajectory",null,null);
				} catch (Throwable t) {
					String msg="Error exiting from trajectory mode with ID="+uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,"Stop trajectory", "Error from remote component while stopping trajectory",ex);
				}
			}
		};
		// Setup and start the thread
		StopTrajectory thread =new StopTrajectory();
		thread.uid=id;
		Thread t = new Thread(thread,"MountController.stopTrajectory");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Point to a planet
	 * 
	 * @param planet The planet to point to
	 */
	public synchronized long setPlanet(String planet) {
		if (planet==null || planet.length()==0) {
			throw new IllegalArgumentException("Invalid null/empty planet name");
		}
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the stop
		class PointToPlanet implements Runnable {
			public long uid;
			String thePlanet;
			public void run() {
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,"Pointing to "+thePlanet+" with ID="+uid);
				try {
					// Check if the planet is observable
					boolean observable=mountController.isObservablePlanet(thePlanet,0.0);
					if (!observable) {
						AcsJSourceNotVisibleEx ex= new AcsJSourceNotVisibleEx();
						ex.setSource(thePlanet);
						throw ex;
					}
					// Point to the planet
                                        mountController.track();
					mountController.setPlanet(thePlanet);
					logger.log(AcsLogLevel.DEBUG,"Planet set to "+thePlanet+" with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,"Pointing to "+thePlanet,null,null);
				} catch (Throwable t) {
					String msg="Error pointing to "+thePlanet+" with ID="+uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,"Pointing to "+thePlanet, "Error from remote component while pointing to "+thePlanet,ex);
				}
			}
		};
		// Setup and start the thread
		PointToPlanet thread =new PointToPlanet();
		thread.uid=id;
		thread.thePlanet=planet;
		Thread t = new Thread(thread,"MountController.setPlanet");
		t.setDaemon(true);
		t.start();
		return id;
	}
	
	/**
	 * Start tracking
	 * 
	 * @return A unique identifier for this operation
	 * 
	 */
	public synchronized long track() {
		// Get a unique a ID for this command
		long id=getOpUID();
		// Define the thread to execute the stop
		class StopAxis implements Runnable {
			public long uid;
			public void run() {
				//	Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG,"Start tracking with ID="+uid);
				try {
					mountController.track();
					logger.log(AcsLogLevel.DEBUG,"Started tracking with ID="+uid+" done");
					listenersNotifier.commandExecuted(uid,"Go to trak mode",null,null);
				} catch (Throwable t) {
					String msg="Error start tracking with ID="+uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR,msg);
					listenersNotifier.commandExecuted(uid,"Go to trak mode","Error setting the track mode",ex);
				}
			}
		};
		// Setup and start the thread
		StopAxis thread =new StopAxis();
		thread.uid=id;
		Thread t = new Thread(thread,"MountController.track");
		t.setDaemon(true);
		t.start();
		return id;
	}

	public ValueHolder<Boolean> getIsStopped() {
		return isStopped;
	}

	public ValueHolder<Double> getEpoch() {
		return epoch;
	}

	/**
	 * Move the mount to maintenance stow
	 */
	public synchronized long maintenanceStow() {
		// Get a unique a ID for this command
		long id = getOpUID();
		// Define the thread to set the shutter
		class MaintenanceStow implements Runnable {
			public long uid;

			public void run() {
				// Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG, "Moving to maintenance stow with ID=" + uid);
				try {
					mountController.maintenanceStow();
					logger.log(AcsLogLevel.DEBUG, "Moved to maintenance stow  ID=" + uid + " done");
					listenersNotifier.commandExecuted(uid, "Move to maintenance stow",null,null);
				} catch (Throwable t) {
					String msg = "Error moving to maintenance stow with ID=" + uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR, msg);
					listenersNotifier.commandExecuted(uid, "Move to maintenance stow","Error from the component while moving to maintenance stow",ex);
				}
			}
		}
		
		// Setup and start the thread
		MaintenanceStow thread = new MaintenanceStow();
		thread.uid = id;
		Thread t = new Thread(thread,"MountController.maintenanceStow");
		t.setDaemon(true);
		t.start();
		return id;		
	}
	
	/**
	 * Move the mount to survival stow
	 */
	public synchronized long survivalStow() {
		// Get a unique a ID for this command
		long id = getOpUID();
		// Define the thread to set the shutter
		class SurvivalStow implements Runnable {
			public long uid;

			public void run() {
				// Launch the command in a separate thread
				logger.log(AcsLogLevel.DEBUG, "Moving to survival stow with ID=" + uid);
				try {
					mountController.survivalStow();
					logger.log(AcsLogLevel.DEBUG, "Moved to survival stow  ID=" + uid + " done");
					listenersNotifier.commandExecuted(uid, "Move to survival stow",null, null);
				} catch (Throwable t) {
					String msg = "Error moving to survival stow with ID=" + uid;
					AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
					ex.setContextDescription(msg);
					logger.log(AcsLogLevel.ERROR, msg);
					listenersNotifier.commandExecuted(uid, "Move to survival stow","Error from the component while moving to survival stow", ex);
				}
			}
		}
		
		// Setup and start the thread
		SurvivalStow thread = new SurvivalStow();
		thread.uid = id;
		Thread t = new Thread(thread,"MountController.survivalStow");
		t.setDaemon(true);
		t.start();
		return id;		
	}

	public ValueHolder<Boolean> getIsOnSource() {
		return isOnSource;
	}

	public ValueHolder<Double> getCommandAz() {
		return commandAz;
	}

	public ValueHolder<Double> getCommandEl() {
		return commandEl;
	}

	public ValueHolder<Double> getActualAz() {
		return actualAz;
	}

	public ValueHolder<Double> getActualEl() {
		return actualEl;
	}

	public ValueHolder<Double> getTimeToSet() {
		return timeToSet;
	}
	
	public String getComponentName() {
		return componentName;
	}
	
}
