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

import java.util.logging.Logger;

import org.omg.CORBA.LongHolder;

import alma.Control.MountAEMBase;
import alma.Control.MountAEMBaseOperations;
import alma.Control.MountAEMOperations;
import alma.ControlGUIErrType.wrappers.AcsJSubreflectorEx;
import alma.acs.logging.AcsLogLevel;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorInfo;
import alma.control.gui.antennamount.mount.ISubreflector;
import alma.control.gui.antennamount.mount.SubreflectorCommon;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.mount.ACSComponentsManager.AntennaType;
import alma.control.gui.antennamount.mount.ISubreflector.Coordinates;
import alma.control.gui.antennamount.mount.MountCommom.UpdateError;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;

/**
 * The AEM prototype subreflector
 * 
 * @author acaproni
 *
 */
public class AEMSubreflector extends SubreflectorCommon implements ISubreflector {
	
	// The aem to get/set the subreflector
	private alma.Control.MountAEM aem;
	
	/**
	 * Constructor
	 * 
	 * @param aem The aem aem prototype to get/set the subreflector
	 * @param rootP The AntennaRootPane needed to add errors and messages
	 * @param log The logger
	 */
	public AEMSubreflector(alma.Control.MountAEM mount, AntennaRootPane rootP, Logger log) {
		super(mount,AntennaType.ALCATEL,rootP,log);
		if (mount==null) {
			throw new IllegalArgumentException("Mount can't be null");
		}
		
		this.aem=mount;
	}
	
	/**
	 * Initialize the subreflector
	 */
	public void init() {
	}

	/**
	 * @see ISubreflector
	 */
	public void refresh(UpdateError errState) {
		super.refresh(errState);
		LongHolder time = new LongHolder();
		int[] vals;
		
		// LIMITS
		try {
			vals = aem.GET_SUBREF_LIMITS(time);
			if (vals==null || vals.length!=3) {
				AcsJSubreflectorEx ex = new AcsJSubreflectorEx();
				ex.setAntennatype(AntennaType.ALCATEL.description);
				ex.setOperation("Wrong number of bytes received from component while getting subreflector limits");
				throw ex;
			}
			Long[] longs = new Long[vals.length];
			for (int t=0; t<vals.length; t++) {
				longs[t]=Long.valueOf(vals[t]);
			}
			limits.setValue(new LongArrayBit(longs), time.value);
		}  catch (Throwable t) {
			limits.setValue(null);
			errState.addError(t);
		}
		
		// STATUS
		try {
			vals = aem.GET_SUBREF_STATUS(time);
			if (vals==null || vals.length!=4) {
				AcsJSubreflectorEx ex = new AcsJSubreflectorEx();
				ex.setAntennatype(AntennaType.ALCATEL.description);
				ex.setOperation("Wrong number of bytes received from component while getting subreflector limits");
				throw ex;
			}
			Long[] longs = new Long[vals.length];
			for (int t=0; t<vals.length; t++) {
				longs[t]=Long.valueOf(vals[t]);
			}
			state.setValue(new LongArrayBit(longs), time.value);
		}  catch (Throwable t) {
			state.setValue(null);
			errState.addError(t);
		}
	}
	
}
