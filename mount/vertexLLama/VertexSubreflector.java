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
package alma.control.gui.antennamount.mount.vertexLLama;

import java.util.logging.Logger;

import org.omg.CORBA.LongHolder;

import alma.Control.MountVertexLLamaBase;
import alma.Control.MountVertexLLamaBaseOperations;
import alma.Control.MountVertexLLamaOperations;
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
 * The Vertex subreflector
 * 
 * @author acaproni
 *
 */
public class VertexSubreflector extends SubreflectorCommon implements ISubreflector {
	
	// The vertex to get/set the subreflector
	private alma.Control.MountVertexLLama vertex;
	
	/**
	 * Constructor
	 * 
	 * @param vertex The vertex to get/set the subreflector
	 * @param rootP The AntennaRootPane needed to add errors and messages
	 * @param log The logger
	 */
	public VertexSubreflector(alma.Control.MountVertexLLama mount, AntennaRootPane rootP, Logger log) {
		super(mount, AntennaType.VERTEX,rootP,log);
		if (mount==null) {
			throw new IllegalArgumentException("Mount can't be null");
		}
		
		this.vertex=mount;
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
			vals = vertex.GET_SUBREF_LIMITS(time);
			if (vals==null || vals.length!=8) {
				AcsJSubreflectorEx ex = new AcsJSubreflectorEx();
				ex.setAntennatype(AntennaType.VERTEX.description);
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
			vals = vertex.GET_SUBREF_STATUS(time);
			if (vals==null || vals.length!=5) {
				AcsJSubreflectorEx ex = new AcsJSubreflectorEx();
				ex.setAntennatype(AntennaType.VERTEX.description);
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
