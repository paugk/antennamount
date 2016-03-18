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

import java.util.logging.Logger;

import alma.ControlGUIErrType.wrappers.AcsJShutterEx;

public abstract class ShutterCommon  {
	
	/**
	 * The action to apply to the shutter
	 * 
	 * @author acaproni
	 *
	 */
	public enum Action {
		open, // Open the shutter
		close // Close the shutter
	}
	
	/**
	 * Notifies the listeners about the happening of an event.
	 */
	protected final MountListenersNotifier listenersNotifier;
	
	/**
	 * ACS logger
	 */
	protected final Logger logger;
	
	/**
	 * Constructor
	 * 
	 * @param notifier The notifier for errors and command completions
	 */
	protected ShutterCommon(MountListenersNotifier notifier,Logger theLogger) {
		if (notifier==null) {
			throw new IllegalArgumentException("The MountListenersNotifier can't be null");
		}
		if (theLogger==null) {
			throw new IllegalArgumentException("The logger can't be null");
		}
		listenersNotifier=notifier;
		logger=theLogger;
	}
	
	/**
	 * Return a set of strings describing the state of the shutter
	 * Each string represent a bit in the state returned by the mount.
	 * 
	 * @return The description of the state of the shutter
	 */
	public abstract String[] getShutterStatus();
	
	/**
	 * @return true if the shutter is open
	 */
	public abstract boolean isOpen();
	
	/**
	 * @return true if the shutter is closed
	 */
	public abstract boolean isClosed();
	
	/**
	 * The state of the shutter returned by this method does not consider
	 * problems in the value stored in the ValueHolder (like if it is too 
	 * old for example).
	 * It is red from the state of the bits of the shutter.
	 * To know if this state is valid and up to date, ValueHolder methods have
	 * to be called.
	 * 
	 * @return true if there are no errors in the shutter state
	 */
	public abstract boolean isOk();
	
	/**
	 * Apply an action to the shutter
	 * 
	 * @param a The action to apply to the shutter
	 */
	public abstract void setShutter(final Action action);
	
	/**
	 * 
	 * @return The {@link ValueHolder} of the shutter used to format the string
	 */
	public abstract ValueHolder<?> getShutterValueHolder();
}
