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

import org.omg.CORBA.LongHolder;

import alma.ControlGUIErrType.wrappers.AcsJSubreflectorEx;
import alma.acs.logging.AcsLogLevel;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorInfo;
import alma.control.gui.antennamount.mount.ACSComponentsManager.AntennaType;
import alma.control.gui.antennamount.mount.ISubreflector.Coordinates;
import alma.control.gui.antennamount.mount.MountCommom.UpdateError;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;

/**
 * A common class for the subreflector implementation of ISubreflector.
 * 
 * Some of the methods/variables defined here might not be implemented
 * in the final class (for example the Alcatel does not have rotation)
 * 
 * @author acaproni
 *
 */
public abstract class SubreflectorCommon implements ISubreflector {
	/**
	 * Absolute position
	 */
	protected ValueHolder<Coordinates> absPosition=new ValueHolder<Coordinates>();
	
	/**
	 * Delta position
	 */
	protected ValueHolder<Coordinates> deltaPosition=new ValueHolder<Coordinates>();
	
	/**
	 * Rotation
	 */
	protected ValueHolder<Coordinates> rotation=new ValueHolder<Coordinates>();
	
	/**
	 * Limits
	 */
	protected ValueHolder<LongArrayBit> limits=new ValueHolder<LongArrayBit>();
	
	/**
	 * State
	 */
	protected ValueHolder<LongArrayBit> state=new ValueHolder<LongArrayBit>();
	
	/**
	 * The AntennaRootPane to add errors and status messages
	 */
	protected AntennaRootPane antennaRootP;
	
	/**
	 * The logger
	 */
	protected Logger logger;
	
	/**
	 * The Mount
	 */
	private final alma.Control.Mount mount;
	
	/**
	 * The type of the antenna for the logs
	 */
	private final AntennaType antennaType;
	
	/**
	 * Constructor
	 * 
	 * @param rootP The AntennaRootPane needed to add errors and messages
	 * @param log The logger
	 */
	public SubreflectorCommon(alma.Control.Mount mount, AntennaType type, AntennaRootPane rootP, Logger log) {
		if (type==null) {
			throw new IllegalArgumentException("AntennaType can't be null");
		}
		if (mount==null) {
			throw new IllegalArgumentException("Mount can't be null");
		}
		if (rootP==null) {
			throw new IllegalArgumentException("AntennaRootPane can't be null");
		}
		if (log==null) {
			throw new IllegalArgumentException("Logger can't be null");
		}
		this.antennaType=type;
		this.mount=mount;
		this.logger=log;
		this.antennaRootP=rootP;		
	}
	
	/**
	 * @see ISubreflector
	 */
	public void setAbsPosition(final Coordinates pos) {
		if (pos==null) {
			throw new IllegalArgumentException("Position can't be null");
		}
		class SetPosition extends Thread {
			public void run() {
				logger.log(AcsLogLevel.DEBUG, "Setting subref pos to ["+pos.x+", "+pos.y+", "+pos.z+"]");
				try {
					mount.setSubreflectorPosition(pos.x,pos.y,pos.z);
					logger.log(AcsLogLevel.DEBUG, "Subref pos set to ["+pos.x+", "+pos.y+", "+pos.z+"]");
				} catch (Throwable t) {
					AcsJSubreflectorEx ex = new AcsJSubreflectorEx(t);
					ex.setAntennatype(antennaType.description);
					ex.setOperation("Error setting subreflector pos to ["+pos.x+", "+pos.y+", "+pos.z+"] in subreflector");
					ErrorInfo error = new ErrorInfo("Error setting a new subreflector position", "Error setting subreflector pos to ["+pos.x+", "+pos.y+", "+pos.z+"] in subreflector",ex);
					antennaRootP.addError(error);
					logger.log(AcsLogLevel.ERROR, "Error setting subreflector position",ex);
				}
			}
		}
		SetPosition thread = new SetPosition();
		thread.setDaemon(true);
		thread.setName("ACASubreflector.setAbsPosition");
		thread.start();
	}

	/**
	 * @see ISubreflector
	 */
	public void setDeltaPosition(final Coordinates delta) {
		if (delta==null) {
			throw new IllegalArgumentException("Delta can't be null");
		}
		class SetDelta extends Thread {
			public void run() {
				logger.log(AcsLogLevel.DEBUG, "Setting subref delta to ["+delta.x+", "+delta.y+", "+delta.z+"]");
				try {
					mount.setSubreflectorPositionOffset(delta.x,delta.y,delta.z);
					logger.log(AcsLogLevel.DEBUG, "Subref delta set to ["+delta.x+", "+delta.y+", "+delta.z+"]");
				} catch (Throwable t) {
					AcsJSubreflectorEx ex = new AcsJSubreflectorEx(t);
					ex.setAntennatype(antennaType.description);
					ex.setOperation("Error setting subreflector delta to ["+delta.x+", "+delta.y+", "+delta.z+"]");
					ErrorInfo error = new ErrorInfo("Error setting a new subreflector delta", "Error setting subreflector delta to ["+delta.x+", "+delta.y+", "+delta.z+"] in subreflector",ex);
					antennaRootP.addError(error);
					logger.log(AcsLogLevel.ERROR, "Error setting subreflector delta",ex);
				}
			}
		}
		SetDelta thread = new SetDelta();
		thread.setDaemon(true);
		thread.setName("ACASubreflector.setDeltaPosition");
		thread.start();
	}

	/**
	 * @see ISubreflector
	 */
	public void setRotation(final int tip, final int tilt, final int rotation) {
		class SetRotation extends Thread {
			public void run() {
				logger.log(AcsLogLevel.DEBUG, "Setting subref rotation to ["+tip+", "+tilt+", "+rotation+"]");
				try {
					int[] ints = new int[3];
					ints[0]=tip;
					ints[1]=tilt;
					ints[2]=rotation;
					mount.SET_SUBREF_ROTATION(ints);
					logger.log(AcsLogLevel.DEBUG, "Subref rotation set to ["+tip+", "+tilt+", "+rotation+"]");
				} catch (Throwable t) {
					AcsJSubreflectorEx ex = new AcsJSubreflectorEx(t);
					ex.setAntennatype(antennaType.description);
					ex.setOperation("Error setting subreflector rotation to ["+tip+", "+tilt+", "+rotation+"] in subreflector");
					ErrorInfo error = new ErrorInfo("Error setting a new subreflector rotation", "Error setting subreflector rotation to ["+tip+", "+tilt+", "+rotation+"] in subreflector",ex);
					antennaRootP.addError(error);
					logger.log(AcsLogLevel.ERROR, "Error setting subreflector rotation",ex);
				}
			}
		}
		SetRotation thread = new SetRotation();
		thread.setDaemon(true);
		thread.setName("ACASubreflector.setRotation");
		thread.start();
	}
	
	/**
	 * Reset the subreflector: set the subreflector to the absolute position
	 * and clear the delta
	 */
	public void zeroDelta() {
		class ZeroDelta extends Thread {
			public void run() {
				logger.log(AcsLogLevel.DEBUG, "Resetting delta");
				try {
					mount.SET_SUBREF_DELTA_ZERO_CMD();
					logger.log(AcsLogLevel.DEBUG, "Delta resetted");
				} catch (Throwable t) {
					AcsJSubreflectorEx ex = new AcsJSubreflectorEx(t);
					ex.setAntennatype(antennaType.description);
					ex.setOperation("Error resetting delta");
					ErrorInfo error = new ErrorInfo("Error resetting the subreflector delta", "Error resetting delta in the subreflector",ex);
					antennaRootP.addError(error);
					logger.log(AcsLogLevel.ERROR, "Error resetting subreflector delta",ex);
				}
			}
		}
		Thread thread = new ZeroDelta();
		thread.setDaemon(true);
		thread.setName("ACASubreflector.ZeroDelta");
		thread.start();
	}
	
	/**
	 * @see ISubreflector
	 */
	public void refresh(UpdateError errState) {
		LongHolder time = new LongHolder();
		int[] vals;
		
		// ABS POSITION
		try {
			vals=mount.GET_SUBREF_ABS_POSN(time);
			if (vals!=null && vals.length==3) {
				absPosition.setValue(new Coordinates(vals[0],vals[1],vals[2]),time.value);
			} else {
				AcsJSubreflectorEx ex = new AcsJSubreflectorEx();
				ex.setAntennatype(antennaType.description);
				ex.setOperation("Invalid position from component");
				throw ex;
			}
		} catch (Throwable t) {
			absPosition.setValue(null);
			errState.addError(t);
		}
		
		// DELTA
		try {
			vals=mount.GET_SUBREF_DELTA_POSN(time);
			if (vals!=null && vals.length==3) {
				deltaPosition.setValue(new Coordinates(vals[0],vals[1],vals[2]),time.value);
			} else {
				AcsJSubreflectorEx ex = new AcsJSubreflectorEx();
				ex.setAntennatype(antennaType.description);
				ex.setOperation("Invalid delta position from component");
				throw ex;
			}
		} catch (Throwable t) {
			deltaPosition.setValue(null);
			errState.addError(t);
		}
		
		// ROTATION
		try {
			vals=mount.GET_SUBREF_ROTATION(time);
			if (vals!=null && vals.length==3) {
				rotation.setValue(new Coordinates(vals[0],vals[1],vals[2]),time.value);
			} else {
				AcsJSubreflectorEx ex = new AcsJSubreflectorEx();
				ex.setAntennatype(antennaType.description);
				ex.setOperation("Invalid rotation from component");
				throw ex;
			}
		} catch (Throwable t) {
			deltaPosition.setValue(null);
			errState.addError(t);
		}
	}
	
	/**
	 * @see ISubreflector
	 */
	public ValueHolder<Coordinates> getAbsPosition() {
		return absPosition;
	}

	/**
	 * @see ISubreflector
	 */
	public ValueHolder<Coordinates> getDeltaPosition() {
		return deltaPosition;
	}

	/**
	 * @see ISubreflector
	 */
	public ValueHolder<LongArrayBit> getLimits() {
		return limits;
	}

	/**
	 * AEC prototype does not support rotation
	 * 
	 * @return null
	 * 
	 * @see ISubreflector
	 */
	public ValueHolder<Coordinates> getRotation() {
		return rotation;
	}

	/**
	 * @see ISubreflector
	 */
	public ValueHolder<LongArrayBit> getStatus() {
		return state;
	}
	
	
}
