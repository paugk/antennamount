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

import java.util.Vector;
import java.util.logging.Logger;

import org.omg.CORBA.OBJECT_NOT_EXIST;

import alma.ACSErr.CompletionHolder;
import alma.Control.Common.Util;
import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.acs.container.ContainerServices;
import alma.acs.exceptions.AcsJCompletion;
import alma.acs.exceptions.AcsJException;
import alma.acs.logging.AcsLogLevel;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorInfo;

/**
 * Base class for the Mount and the MountController
 * 
 * The class defines the thread to update the state of the mount.
 * Such a thread must be started by the final class
 * 
 * @author acaproni
 *
 */
public abstract class MountCommom extends Thread {
	
	/**
	 * The object containing info about the retrieval of values from
	 * the component in one iteration of the thread
	 * 
	 * @author acaproni
	 *
	 */
	public class UpdateError {
		
		/**
		 * <code>true</code> if there was a delay reading values
		 */
		public boolean delay;
		
		/**
		 * The number of detected errors
		 */
		public int errors;
		
		/**
		 * It is <code>true</code> if an exception of type {@link OBJECT_NOT_EXIST}
		 * happened.
		 */
		public boolean componentDown;
		
		/**
		 * The exceptions detected during the last iteration
		 */
		public Vector<AcsJException> exceptions = new Vector<AcsJException>();
		
		/**
		 * Constructor
		 */
		public UpdateError() {
			reset();
		}
		
		/**
		 * Reset the info to a good state (no error, no delay)
		 */
		public void reset() {
			delay=false;
			errors=0;
			componentDown=false;
			exceptions.clear();
		}
		
		/**
		 * Set the status as delay
		 */
		public void addDelay() {
			delay=true;
		}
		
		/**
		 * Add one more error
		 * 
		 * @param t The <code>AcsJException</code> of the error
		 */
		public void addError(AcsJException t) {
			if (t==null) {
				throw new IllegalArgumentException("The error can't be null!");
			}
			errors++;
			exceptions.add(t);
		}
		
		/**
		 * Add one more error. 
		 * <P>
		 * The {@link Throwable} is replaced by a {@link AcsJException} before being
		 * added to the {@link Vector} where the descriptor of the {@link AcsJException} 
		 * is the message of the {@link Throwable};
		 * 
		 * @param t The <code>Throwable</code> of the error
		 */
		public void addError(Throwable t) {
			if (t==null) {
				throw new IllegalArgumentException("The error can't be null!");
			}
			AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
			ex.setContextDescription(t.getMessage());
			errors++;
			exceptions.add(ex);
			if (!componentDown) {
				componentDown=(t instanceof OBJECT_NOT_EXIST);
			}
		}
		
		/**
		 * 
		 * @return True is there are errors
		 */
		public boolean hasErrors() {
			return errors!=0;
		}
		
		/**
		 * 
		 * @return true in case of delay
		 */
		public boolean hasDelay() {
			return delay;
		}
		
		/**
		 * Print on stdout a report of the errors
		 */
		public void dump() {
			if (exceptions.isEmpty()) {
				return;
			}
			ErrorInfo error;
			for (AcsJException t: exceptions) {
				error= new ErrorInfo("Exceptions from MountController","Got "+exceptions.size()+" (unrelated) exceptions while reading values from the MountController",t);
				logger.log(AcsLogLevel.ERROR,"Exception caught",t);
				antennaRootP.addError(error);
			}
		}
	}

	/**
	 * The ACS container services
	 */
	protected ContainerServices acsCS;
	
	/**
	 * ACS logger
	 */
	protected Logger logger;
	
	/**
	 * The rate at which the values are update from the 
	 * mount by the thread
	 */
	private static final int UPDATE_INTERVAL=1500;
	
	/**
	 * The time number of msec to decide if the component is slow
	 * answering
	 *
	 * This only applies to the reading of variables (the thread) because 
	 * the methods are executed asynchronously
	 */
	private static final int SLOW_TIME=2500;
	
	/**
	 * The number of attempts before deciding an error is unrecoverable
	 * This only applies to the reading of variables (the thread)
	 */
	protected static final int NUMBER_OF_ATTEMPTS=5;
	
	/**
	 * Signal the thread to terminate
	 */
	protected volatile boolean terminateThread=false;
	
	/**
	 * Pause/unpause the retrieval of values from the remote component
	 */
	private volatile boolean paused=false;
	
	/**
	 * Notifies the listeners about the happening of an event
	 *This object delegates to this notifier
	 */
	protected MountListenersNotifier listenersNotifier = new MountListenersNotifier();
	
	/**
	 * The identifier for an operation executed in a separate thread
	 */
	private static volatile long opUID=0;
	
	/**
	 * The AntennaRootPane to add errors and status messages
	 */
	protected AntennaRootPane antennaRootP;
		
	/**
	 * Constructor.
	 * 
	 * @param contSvcs The ContainerServices
	 * @param rootP The AntennaRootPane
	 */
	public MountCommom(ContainerServices contSvcs, AntennaRootPane rootP) {
		super("MountCommon");
		if (contSvcs==null) {
			throw new IllegalArgumentException("Invalid null ContainerServices");
		}
		if (rootP==null) {
			throw new IllegalArgumentException("Invalid null AntennaRootPane");
		}
		antennaRootP=rootP;
		acsCS=contSvcs;
		logger=acsCS.getLogger();
		if (logger==null) {
			throw new IllegalStateException("Error getting the logger from ContainerServices");
		}
		setDaemon(true);
	}
	
	/**
	 * Release the resources and stop the thread
	 *
	 */
	public void close() {
		
		// Wait until the thread terminates before exiting
		logger.log(AcsLogLevel.DEBUG,"Terminating thread "+getName());
		terminateThread=true;
		this.interrupt();
		boolean terminated=false;
		while (terminated==false) {
			try {
				this.join();
				terminated=true;
			} catch (InterruptedException ie) {
				continue;
			}
		}
		antennaRootP.getHeartbeatChecker().unregister(this);
		logger.log(AcsLogLevel.DEBUG,getName()+" thread terminated");
		listenersNotifier.notifyConnectionClosed(getName());
		// Release the listeners for events generated by this object
		listenersNotifier.clearListeners();
	}
	
	/**
	 * 
	 * @return A unique identifier for an async operation
	 */
	protected synchronized static long getOpUID() {
		return opUID++;
	}
	
	/**
	 * Pause/Unpause the object. 
	 * When the object is paused, it stops reading values from
	 * the remote component
	 *  
	 * @param pause If true the object is paused
	 */
	public void setPaused(boolean pause) {
		paused=pause;
	}
	
	/**
	 * Check if the completion contains an error and eventually
	 * log a message
	 * 
	 * @param completion The completion to check
	 */
	protected void checkError(CompletionHolder completion) {
		if (completion==null) {
			throw new IllegalArgumentException("Illegal null CompletionHolder");
		}
		AcsJCompletion acsCompletion = AcsJCompletion.fromCorbaCompletion(completion.value);
		if (!acsCompletion.isError()) {
			// No error
			return;
		}
		AcsJException acsEx = acsCompletion.getAcsJException();
		acsEx.log(logger);
	}
	
	/**
	 * Add a listener for the operations 
	 * 
	 * @param l The listener
	 */
	public void addOperationListener(MountOperationListener listener) {
		if (listener==null) {
			throw new IllegalArgumentException("Invalid null listener");
		}
		listenersNotifier.addOperationListener(listener);
	}
	
	/**
	 * Remove a listener for the operations 
	 * 
	 * @param l The listener
	 */
	public void removeOperationListener(MountOperationListener l) {
		if (l==null) {
			throw new IllegalArgumentException("Invalid null listener");
		}
		listenersNotifier.removeOperationListener(l);
	}
	
	/**
	 * Add a listener for the connection events
	 * 
	 * @param l The listener
	 */
	public void addComponentConnectionListener(MountConnectionListener l) {
		if (l==null) {
			throw new IllegalArgumentException("Invalid null listener");
		}
		listenersNotifier.addMountListener(l);
	}
	
	/**
	 * Check if the components is too slow returning the value of a variable
	 * 
	 * @param pre The instant when the request was issued
	 * @return true if the answer was slow
	 */
	protected boolean checkDelay(long pre) {
		long now=System.currentTimeMillis();
		if (now-pre>SLOW_TIME) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * The thread to update the status of the component
	 */
	public void run() {
		listenersNotifier.notifyConnectionEstablished(getName());
		int currentAttempt=0;
		UpdateError errState = new UpdateError();
		while (!terminateThread) {
			antennaRootP.getHeartbeatChecker().ping(this);
			errState.reset();
			try {
				updateComponentStatus(errState);
			} catch (OBJECT_NOT_EXIST one) {
				// The component is down!!!
				AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(one);
				ex.setContextDescription("Error updating component status");
				ErrorInfo ei = new ErrorInfo("Error updating component status", "Erron in the thread updating the state of the component",ex);
				antennaRootP.addError(ei);
				componentDown();
				break;
			} catch (Throwable t) {
				AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
				ex.setContextDescription("Error updating component status");
				ErrorInfo ei = new ErrorInfo("Error updating component status", "Erron in the thread updating the state of the component",ex);
				antennaRootP.addError(ei);
			}
			if (errState.componentDown) {
				componentDown();
				break;
			}
			if (errState.hasDelay()) {
				listenersNotifier.notifyResponseTimeSlow();
			} else {
				listenersNotifier.notifyResponseTimeOk();
			}
			if (errState.hasErrors()) {
				errState.dump();
				currentAttempt++;
			} else {
				if (currentAttempt!=0) {
					listenersNotifier.notifyConnectionEstablished(getName());
					currentAttempt=0;
				}
			}
			
			// Check the number of failures
			if (currentAttempt < NUMBER_OF_ATTEMPTS && currentAttempt!=0) {
				listenersNotifier.notifyTransientError();
			} else if (currentAttempt>=NUMBER_OF_ATTEMPTS) {
				// Do nothing: I don't want to stop in any case even if full of errors
				currentAttempt=Integer.MIN_VALUE;
			} 
			do {
				try {
					Thread.sleep(UPDATE_INTERVAL);
					antennaRootP.getHeartbeatChecker().ping(this);
				} catch (InterruptedException ie) {}	
			} while (paused && !terminateThread);
		}
	}
	
	/**
	 * The component went down.
	 * <P>
	 * The event is detect by checking if an exception of type
	 * {@link OBJECT_NOT_EXIST} occurred while updating the status
	 */
	private void componentDown() {
		antennaRootP.addStatusMessage("Connection lost with "+getName()+", disconnecting", true);
		listenersNotifier.notifyConnectionLost(getName());
		// Start the thread to disconnect the components
		Thread disconnectThread = new Thread(new Runnable() {
			public void run() {
				antennaRootP.disconnectComponents();
			}
		});
		disconnectThread.setName("MountCommon.DisconnectMount thread");
		disconnectThread.setDaemon(false);
		disconnectThread.start();
	}
	
	/**
	 * Update the status of the component.
	 * 
	 * The method is overridden by specialized classes
	 *
	 * @param errState The error state of execution
	 */
	protected abstract void updateComponentStatus(UpdateError errState) throws AcsJMountGUIErrorEx;
	
}
