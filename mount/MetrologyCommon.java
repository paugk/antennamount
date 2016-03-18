/*
 * ALMA - Atacama Large Millimiter Array (c) European Southern Observatory, 2010
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

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.omg.CORBA.LongHolder;

import alma.acs.logging.AcsLogLevel;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.ACSComponentsManager.AntennaType;
import alma.control.gui.antennamount.mount.MountCommom.UpdateError;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;
import alma.control.gui.antennamount.mount.MountListenersNotifier;

import alma.ControlGUIErrType.wrappers.AcsJMetrologyEx;

/**
 * Comon class for the metrology
 * 
 * @author acaproni
 *
 */
public class MetrologyCommon {

	/**
	 * Metrology deltas
	 */
	protected final ValueHolder<int[]> deltas = new ValueHolder<int[]>();
	
	/**
	 * Metrology delta path
	 */
	protected final ValueHolder<Integer> deltaPath = new ValueHolder<Integer>();
	
	/**
	 * Metrology temperatures
	 * 
	 * Values of this array are returned by GET_METR_TEMPS_N with n in [0x0,0x19].
	 * Each GET_METR_TEMPS_N returns 4 integer of 16 bits each (i.e. int[4]).
	 * <code>temps</code> concatenates in a single <code>ValueHolder<int[]></code> 
	 * the values returned by all the GET_METR_TEMPS_N (in total there 25x4=100 int.
	 */
	protected final ValueHolder<int[]> temps = new ValueHolder<int[]>();
	
	/**
	 * The Mount
	 */
	private final alma.Control.Mount mount;
	
	/**
	 * The logger
	 */
	private final Logger logger;
	
	/**
	 * The type of the antenna for the logs
	 */
	protected final AntennaType antennaType;
	
	/**
	 * The notifier for errors and commands completions
	 */
	protected final MountListenersNotifier notifier;
	
	/**
	 * Constructor
	 * 
	 * @param The mount component
	 * @param rootP The AntennaRootPane needed to add errors and messages
	 * @param log The logger
	 * @param notifier The notifier for errors and commands completions
	 */
	public MetrologyCommon(alma.Control.Mount mount, AntennaType type, Logger log, MountListenersNotifier notifier) {
		if (type==null) {
			throw new IllegalArgumentException("AntennaType can't be null");
		}
		if (mount==null) {
			throw new IllegalArgumentException("Mount can't be null");
		}
		if (log==null) {
			throw new IllegalArgumentException("Logger can't be null");
		}
		if (notifier==null) {
			throw new IllegalArgumentException("Notifier can't be null");
		}
		this.antennaType=type;
		this.mount=mount;
		this.logger=log;
		this.notifier=notifier;
	}
	
	/**
	 * refresh the values by reading from the Mount
	 * @param errState
	 * 
	 * @see IMetrology
	 */
	public void refresh(UpdateError errState) {
		LongHolder time = new LongHolder();
		int[] vals;

		// DELTA PATH
		try {
			Integer val=Integer.valueOf(mount.GET_METR_DELTAPATH(time));
			deltaPath.setValue(val,time.value);
		}  catch (Throwable t) {
			deltaPath.setValue(null);
			errState.addError(t);
		}
		// DELTAS
		try {
			vals = mount.GET_METR_DELTAS(time);
			if (vals!=null && vals.length==2) {
				deltas.setValue(vals,time.value);
			} else {
				AcsJMetrologyEx ex = new AcsJMetrologyEx();
				ex.setAntennatype(antennaType.description);
				ex.setOperation("Invalid metrology delta path from component");
				throw ex;
			}
		}  catch (Throwable t) {
			deltas.setValue(null);
			errState.addError(t);
		}
		// TEMPS
		int[] allTemps = new int[100]; // All the values
		ArrayList<int[]> tempVals = new ArrayList<int[]>(); // The values returned by each CORBA call
		try {
			tempVals.add(mount.GET_METR_TEMPS_00(time));
			tempVals.add(mount.GET_METR_TEMPS_01(time));
			tempVals.add(mount.GET_METR_TEMPS_02(time));
			tempVals.add(mount.GET_METR_TEMPS_03(time));
			tempVals.add(mount.GET_METR_TEMPS_04(time));
			tempVals.add(mount.GET_METR_TEMPS_05(time));
			tempVals.add(mount.GET_METR_TEMPS_06(time));
			tempVals.add(mount.GET_METR_TEMPS_07(time));
			tempVals.add(mount.GET_METR_TEMPS_08(time));
			tempVals.add(mount.GET_METR_TEMPS_09(time));
			tempVals.add(mount.GET_METR_TEMPS_0A(time));
			tempVals.add(mount.GET_METR_TEMPS_0B(time));
			tempVals.add(mount.GET_METR_TEMPS_0C(time));
			tempVals.add(mount.GET_METR_TEMPS_0D(time));
			tempVals.add(mount.GET_METR_TEMPS_0E(time));
			tempVals.add(mount.GET_METR_TEMPS_0F(time));
			tempVals.add(mount.GET_METR_TEMPS_10(time));
			tempVals.add(mount.GET_METR_TEMPS_11(time));
			tempVals.add(mount.GET_METR_TEMPS_12(time));
			tempVals.add(mount.GET_METR_TEMPS_13(time));
			tempVals.add(mount.GET_METR_TEMPS_14(time));
			tempVals.add(mount.GET_METR_TEMPS_15(time));
			tempVals.add(mount.GET_METR_TEMPS_16(time));
			tempVals.add(mount.GET_METR_TEMPS_17(time));
			tempVals.add(mount.GET_METR_TEMPS_18(time));
			// Check the quality of the returned value and flush
			// into the global array allTemps
			for (int t=0; t<25; t++) {
				if (tempVals.get(t)!=null && tempVals.get(t).length==4) {
					// The value is good
					for (int j=0; j<4; j++) {
						allTemps[t*4+j]=tempVals.get(t)[j];
					}
				} else {
					// the returned value is bad!!!
					AcsJMetrologyEx ex = new AcsJMetrologyEx();
					ex.setAntennatype(antennaType.description);
					ex.setOperation("Invalid temperatures from component");
					throw ex;
				}
			}
			temps.setValue(allTemps);
		} catch (Throwable t) {
			temps.setValue(null);
			errState.addError(t);
		}
	}
	
	/**
	 * @see IMetrology
	 */
	public ValueHolder<Integer> getDeltaPath() {
		return deltaPath;
	}
	
	/**
	 * @see IMetrology
	 */
	public ValueHolder<int[]> getDeltas() {
		return deltas;
	}
	
	/**
	 * @see IMetrology
	 */
	public ValueHolder<int[]> getTemps() {
		return temps;
	}
}
