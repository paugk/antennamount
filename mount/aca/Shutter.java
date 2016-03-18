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
package alma.control.gui.antennamount.mount.aca;

import java.util.Vector;
import java.util.logging.Logger;

import alma.Control.MountPackage.ShutterMode;
import alma.ControlGUIErrType.wrappers.AcsJShutterEx;
import alma.Control.MountACA;
import alma.acs.logging.AcsLogLevel;
import alma.control.gui.antennamount.mount.MountListenersNotifier;
import alma.control.gui.antennamount.mount.ShutterCommon;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.mount.ACSComponentsManager.AntennaType;
import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.bit.LongBit;

public class Shutter extends ShutterCommon {
	
	/**
	 * The state of the shutter is a bit mask:
	 * each string in this array is the description of the bit
	 * in the same position
	 * i.e. bit[0]==1 ==> "Open"
	 * 
	 * The state could have more bits set to 1
	 */
	private static final String[] stateDescriptors = {
		"Open",
		"Closed",
		"Shutter motor ON",
		"Local sys error"
	};
	
	/**
	 * The mount
	 */
	private final MountACA mount;
	
	/**
	 * The state of the shutter
	 */
	private final ValueHolder<Integer> shutterStatus = new ValueHolder<Integer>();
	
	/**
	 * Constructor 
	 * 
	 * @param aca The mount that owns this shutter
	 * @param notifier The notifier for errors and commands completions
	 * @param logger The logger
	 */
	public Shutter(MountACA aca, MountListenersNotifier notifier,Logger logger) {
		super(notifier,logger);
		if (aca==null) {
			throw new IllegalArgumentException("Invalid null MountVertex");
		}
		this.mount=aca;
	}

	/**
	 * @see alma.control.gui.antennamount.mount.ShutterCommon
	 */
	public String[] getShutterStatus() {
		Integer status = shutterStatus.getValue();
		if (status==null) {
			return new String[] {
					ValueDisplayer.NOT_AVAILABLE
			};
		}
		LongBit bits = new LongBit(Long.valueOf(status.longValue()));
		Vector<String> ret = new Vector<String>();
		for (int t=0; t<4; t++) {
			if (bits.getBit(t)) {
				ret.add(stateDescriptors[t]);
			}
		}
		String[] val = new String[ret.size()];
		ret.toArray(val);
		return val;
	}

	/**
	 * @see alma.control.gui.antennamount.mount.ShutterCommon
	 */
	public boolean isClosed() {
		Integer status = shutterStatus.getValue();
		if (status==null) {
			return false;
		}
		LongBit bits = new LongBit(Long.valueOf(status.longValue()));
		return bits.getBit(1);
	}

	/**
	 * @see alma.control.gui.antennamount.mount.ShutterCommon
	 */
	public boolean isOk() {
		Integer status = shutterStatus.getValue();
		if (status==null) {
			return false;
		}
		LongBit bits = new LongBit(Long.valueOf(status.longValue()));
		return !bits.getBit(3);
	}

	/**
	 * @see alma.control.gui.antennamount.mount.ShutterCommon
	 */
	public boolean isOpen() {
		Integer status = shutterStatus.getValue();
		if (status==null) {
			return false;
		}
		LongBit bits = new LongBit(Long.valueOf(status.longValue()));
		return bits.getBit(0);
	}

	/**
	 * @see alma.control.gui.antennamount.mount.ShutterCommon
	 */
	public void setShutter(final Action action) {
		if (action==null) {
			throw new IllegalArgumentException("Invalid null action");
		}
		if (mount==null) {
			throw new IllegalStateException("Setting the shutter of a NULL Mount!");
		}
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				ShutterMode newMode = null;
				switch (action) {
				case open: {
					newMode = ShutterMode.SHUTTER_OPEN;
					break;
				} 
				case close: {
					newMode = ShutterMode.SHUTTER_CLOSED;
					break;
				}
				default: {
					newMode=null;
				}
				}
				if (newMode==null) {
					AcsJShutterEx ex = new AcsJShutterEx();
					ex.setOperation("Unsupported shutter mode "+action.toString());
					ex.setAntennatype(AntennaType.MELCO.description);
					listenersNotifier.commandExecuted(0,"Unsupported ACA shutter to,"+action.toString(),"Unsupported ACA shutter to,"+action.toString(),ex);
					return;
				}
				try {
					logger.log(AcsLogLevel.DEBUG,"Setting ACA shutter to "+newMode);
					mount.SET_SHUTTER(newMode);
					logger.log(AcsLogLevel.DEBUG,"Setting ACA shutter to "+newMode+" done");
				} catch (Throwable t) {
					AcsJShutterEx ex = new AcsJShutterEx(t);
					ex.setOperation("Error setting the shutter to "+action.toString());
					ex.setAntennatype(AntennaType.MELCO.description);
					listenersNotifier.commandExecuted(0,"Set ACA shutter to,"+newMode,"Error from remote component while setting shutter to "+action.toString(),ex);
				}		
			}
		});
		t.setDaemon(true);
		t.setName("ACA:setShutter");
		t.start();
	}

	/**
	 * Update the status of the shutter with the value read from the component.
	 * 
	 * @param status The status of the shutter
	 * @param time The tie of the update
	 */
	public void updateStatus(Integer status, long time) {
		shutterStatus.setValue(status, time);
	}
	
	/**
	 * 
	 * @return The {@link ValueHolder} of the shutter used to format the string
	 */
	public ValueHolder<?> getShutterValueHolder() {
		return shutterStatus;
	}
}
