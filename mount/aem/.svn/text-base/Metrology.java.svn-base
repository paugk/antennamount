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
package alma.control.gui.antennamount.mount.aem;

import java.util.logging.Logger;

import org.omg.CORBA.LongHolder;

import alma.acs.logging.AcsLogLevel;
import alma.control.gui.antennamount.mount.IMetrology;
import alma.control.gui.antennamount.mount.MetrologyCommon;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.mount.ACSComponentsManager.AntennaType;
import alma.control.gui.antennamount.mount.MountCommom.UpdateError;
import alma.control.gui.antennamount.mount.MountListenersNotifier;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;

import alma.ControlGUIErrType.wrappers.AcsJMetrologyEx;

/**
 * The metrology for the ACA 12m
 * 
 * @author acaproni
 *
 */
public class Metrology extends MetrologyCommon implements IMetrology {

	/**
	 * The mount
	 */
	private final alma.Control.MountAEM mount;
	
	/**
	 * The Logger
	 */
	private final Logger logger;
	
	/**
     * Metrology mode
     */
    protected final ValueHolder<LongArrayBit> metrMode = new ValueHolder<LongArrayBit>();
	
	/**
	 * Metrology equipment status
	 */
	protected ValueHolder<LongArrayBit> equipStatus = new ValueHolder<LongArrayBit>();
	
	/**
	 * Constructor
	 * 
	 * @param The mount component
	 * @param rootP The AntennaRootPane needed to add errors and messages
	 * @param logger The logger
	 * @param notifier The notifier for errors and commands completions
	 */
	public Metrology(alma.Control.MountAEM mount, Logger logger, MountListenersNotifier notifier) {
		super(mount, AntennaType.ALCATEL, logger, notifier);
		this.mount=mount;
		this.logger=logger;
	}
	
	/**
	 * Set the metrology mode
	 * 
	 * @param mode The new mode (4 bytes)
	 */
	public void setMetrMode(final int[] mode) {
		if (mode==null || mode.length!=4) {
			throw new IllegalArgumentException("Invalide metrology mode");
		}
		Thread t = new Thread(new Runnable() {
			public void run() {
				String modeStr="["+mode[0]+", "+mode[1]+", "+mode[2]+", "+mode[3]+"]";
				try {
					logger.log(AcsLogLevel.DEBUG,"Setting metrology mode to "+modeStr);
					mount.SET_METR_MODE(mode);
					logger.log(AcsLogLevel.DEBUG,"Metrology mode set to "+modeStr);
				} catch (Throwable t) {
					AcsJMetrologyEx ex = new AcsJMetrologyEx(t);
					ex.setAntennatype(AntennaType.VERTEX.description);
					ex.setOperation("Invalid metrology mode");
					notifier.commandExecuted(0,"Set Vertex metr mode to "+modeStr,"Error from remote component while setting metrology mode to "+modeStr,ex);
				}
			}
		});
		t.setDaemon(true);
		t.setName("Vertex:setMetrMode");
		t.start();
	}
	
	/**
	 * refresh the values by reading from the Mount
	 * @param errState
	 * 
	 * @see IMetrology
	 */
	public void refresh(UpdateError errState) {
		super.refresh(errState);
		LongHolder time = new LongHolder();
		int[] vals;
		
		// EQUIP_STATUS
		try {
			vals=mount.GET_METR_EQUIP_STATUS(time);
			if (vals!=null && vals.length==4) {
				Long[] longs = new Long[vals.length];
				for (int t=0; t<vals.length; t++) {
					longs[t]=Long.valueOf(vals[t]);
				}
				equipStatus.setValue(new LongArrayBit(longs),time.value);
			} else {
				AcsJMetrologyEx ex = new AcsJMetrologyEx();
				ex.setAntennatype(antennaType.description);
				ex.setOperation("Invalid metrology equipment status from component");
				throw ex;
			}
		} catch (Throwable t) {
			metrMode.setValue(null);
			errState.addError(t);
		}
		
		// METROLOGY MODE
		try {
			vals=mount.GET_METR_MODE(time);
			if (vals!=null && vals.length==4) {
				Long[] longs = new Long[vals.length];
				for (int t=0; t<vals.length; t++) {
					longs[t]=Long.valueOf(vals[t]);
				}
				metrMode.setValue(new LongArrayBit(longs),time.value);
			} else {
				AcsJMetrologyEx ex = new AcsJMetrologyEx();
				ex.setAntennatype(antennaType.description);
				ex.setOperation("Invalid metrology mode from component");
				throw ex;
			}
		} catch (Throwable t) {
			metrMode.setValue(null);
			errState.addError(t);
		}
	}
	
	/**
	 * @return The metrology equipment status
	 * 
	 * @see IMetrology
	 */
	public ValueHolder<LongArrayBit> getEquipStatus() {
		return equipStatus;
	}
	
	/**
     * @see IMetrology
     */
    public ValueHolder<LongArrayBit> getMode() {
            return metrMode;
    }

}
