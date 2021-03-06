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
 * @version $Id: MountOperationListener.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.mount;

import alma.acs.exceptions.AcsJException;

/**
 * 
 * The interface to listen to the termination of operation run in parallel
 *
 */
public interface MountOperationListener {
	/**
	 * Notifies about the termination of a command.
	 * 
	 * @param id The ID of the command
	 * @param cmd The command that terminated
	 * @param msg A message explaining the failure (null in case of success)
	 * @param e The exception explains the error (can be null even in case of error
	 *          if msg is enough to explain the problem)
	 */
	public void commandExecuted(long id, String cmd, String msg, AcsJException e);
	
	/**
	 * Notifies that a command has been submitted to a remote component
	 * 
	 * @param id The ID of the command
	 * @param cmd The submitted command
	 */
	public void commandSubmitted(long id, String cmd);
}
