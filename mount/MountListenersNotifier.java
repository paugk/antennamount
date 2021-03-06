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
 * @author  caproni   
 * @version $Id: MountListenersNotifier.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.mount;

import java.util.Vector;

import alma.acs.exceptions.AcsJException;

/**
 * Notify the listeners of the happening of an event
 *
 */
public class MountListenersNotifier {
	
	// The vector of listeners
	private Vector<MountConnectionListener> listeners = new Vector<MountConnectionListener>();
	
	// The listeners for the execution of a command
	private Vector<MountOperationListener>opsListeners=new Vector<MountOperationListener>();
	
	/**
	 * Add a new listener
	 * 
	 * @param l The listener to add
	 */
	public synchronized void addMountListener(MountConnectionListener l) {
		if (l==null) {
			throw new IllegalArgumentException("Invalid null listener");
		}
		listeners.add(l);
	}
	
	/**
	 * Remove a listener 
	 * 
	 * @param l The listener to remove
	 * @return true if the listener has been removed
	 */
	public synchronized boolean removeListener(MountConnectionListener l) {
		if (l==null) {
			throw new IllegalArgumentException("Invalid null listener");
		}
		return listeners.remove(l);
	}
	
	/**
	 * Remove all the listeners
	 *
	 */
	public synchronized void clearListeners() {
		listeners.clear();
		opsListeners.clear();
	}
	
	/**
	 * @see MountConnectionListener
	 *
	 */
	public synchronized void notifyConnectionEstablished(String name) {
		for (MountConnectionListener l: listeners) {
			l.componentConnected(name);
		}
		
	}
	
	/**
	 * @see MountConnectionListener
	 *
	 */
	public synchronized void notifyConnectionClosed(String name) {
		for (MountConnectionListener l: listeners) {
			l.componentDisconnected(name);
		}
		
	}
	
	/**
	 * @see MountConnectionListener
	 *
	 */
	public synchronized void notifyConnectionLost(String name) {
		for (MountConnectionListener l: listeners) {
			l.componentUnreliable(name);
		}
	}
	
	/**
	 * @see MountConnectionListener
	 *
	 */
	public synchronized void notifyTransientError(){
		for (MountConnectionListener l: listeners) {
			l.componentTransientError();
		}
	}
	
	/**
	 * @see MountConnectionListener
	 *
	 */
	public synchronized void notifyResponseTimeSlow() {
		for (MountConnectionListener l: listeners) {
			l.componentResponseTimeSlow();
		}
	}
	
	/**
	 * @see MountConnectionListener
	 *
	 */
	public synchronized void notifyResponseTimeOk() {
		for (MountConnectionListener l: listeners) {
			l.componentResponseTimeOk();
		}
	}
	
	/**
	 * Notifies the registered listeners that the command with the given ID has teminated
	 *  
	 * @param id The id of the command
	 * @param cmd The command
	 * @param If not null describes an error occurred while executing the command
	 * @param me The exception (can be null)
	 */
	public synchronized void commandExecuted(long id, String cmd, String msg, AcsJException e) {
		if (id<0) {
			throw new IllegalArgumentException("The id "+id+" is out of range");
		}
		for (MountOperationListener opL: opsListeners) {
			opL.commandExecuted(id,cmd,msg,e);
		}
	}
	
	/**
	 * Notifies that a command has been submitted to a remote component
	 * 
	 * @param id The ID of the command
	 * @param cmd The submitted command
	 */
	public synchronized void commandSubmitted(long id, String cmd) {
		if (id<0) {
			throw new IllegalArgumentException("The id "+id+" is out of range");
		}
		for (MountOperationListener opL: opsListeners) {
			opL.commandSubmitted(id, cmd);
		}
	}
	
	public synchronized void addOperationListener(MountOperationListener listener) {
		if (listener==null) {
			throw new IllegalArgumentException("Invalid null listener");
		}
		opsListeners.add(listener);
	}
	
	/**
	 * Remove a listener for the operations 
	 * 
	 * @param l The listener
	 */
	public synchronized void removeOperationListener(MountOperationListener l) {
		if (l==null) {
			throw new IllegalArgumentException("Invalid null listener");
		}
		opsListeners.remove(l);
	}
}
