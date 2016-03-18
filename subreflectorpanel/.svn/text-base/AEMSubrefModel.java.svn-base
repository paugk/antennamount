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
package alma.control.gui.antennamount.subreflectorpanel;

import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.mount.aem.MountAEM;
import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.ValueState;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;

public class AEMSubrefModel extends CommonStatusModel {
	
	// The mount
	private MountAEM mount;
	
	/**
	 * The status of the vertex prototype subreflector
	 * Refer to the AEM ICD GET_SUBREF_STATUS
	 * for further details
	 * 
	 * @author acaproni
	 *
	 */
	private enum SubrefStatus {
		TITLE("<HTML><FONT color=\"blue\"><B>Status</B></FONT>"),
		
		// Byte 0
		PWR_MONITOR("Power monitor"),
		OVER_RUN("Over run"),
		INITED("Initialized"),
		INITIALIZING("Is initializing"),
		SERVO_STATE("Servo state"),
		CONN_FAULT("Conenction fault"),
		SM_OVERRIDE("SM override"),
		
		// Byte 1
		STRUT1_MOTION("Strut 1 motion status"),
		STRUT2_MOTION("Strut 2 motion status"),
		STRUT3_MOTION("Strut 3 motion status"),
		STRUT4_MOTION("Strut 4 motion status"),
		STRUT5_MOTION("Strut 5 motion status"),
		STRUT6_MOTION("Strut 6 motion status"),
		
		// Byte 2
		STRUT1_CTR_ERR("Strut 1 controller error"),
		STRUT2_CTR_ERR("Strut 2 controller error"),
		STRUT3_CTR_ERR("Strut 3 controller error"),
		STRUT4_CTR_ERR("Strut 4 controller error"),
		STRUT5_CTR_ERR("Strut 5 controller error"),
		STRUT6_CTR_ERR("Strut 6 controller error"),
		
		// Byte 3
		HEXAPOD_TEMP_MONITOR("Hexapod temperature monitoring"),
		ACT1_OVERTEMP("Actuator 1 over temperature"),
		ACT2_OVERTEMP("Actuator 2 over temperature"),
		ACT3_OVERTEMP("Actuator 3 over temperature"),
		ACT4_OVERTEMP("Actuator 4 over temperature"),
		ACT5_OVERTEMP("Actuator 5 over temperature"),
		ACT6_OVERTEMP("Actuator 6 over temperature");
		
		/**
		 * Constructor
		 */
		private SubrefStatus(String title) {
			this.title=title;
		}
		
		/**
		 * The tile of the row
		 */
		public final String title;
		
		/**
		 * The value of the row
		 */
		public final StringBuilder value = new StringBuilder(ValueDisplayer.NOT_AVAILABLE);
		
		/**
		 * Set all the values as not available
		 */
		public static void setNotAvailable() {
			for (SubrefStatus state: SubrefStatus.values()) {
				if (state.value.length()>0) {
					state.value.delete(0, state.value.length());
				}
				if (state!=TITLE) {
					state.value.append(ValueDisplayer.NOT_AVAILABLE);
				}
			}
		}
	}
	
	/**
	 * The limits of the vertex prototype subreflector
	 * Refer to the AEM ICD GET_SUBREF_LIMITS
	 * for further details
	 * 
	 * @author acaproni
	 *
	 */
	private enum SubrefLimits {
		TITLE("<HTML><FONT color=\"blue\"><B>Limit switches</B></FONT>"),
		
		// Byte 0
		XUP_SW_LIMIT("X upper software position limit"),
		XLOW_SW_LIMIT("X lower software position limit"),
		XUP_SW_ROTLIMIT("X upper software rotational limit"),
		XLOW_SW_ROTLIMIT("X lower software rotational limit"),
		
		// Byte 1
		YUP_SW_LIMIT("Y upper software position limit"),
		YLOW_SW_LIMIT("Y lower software position limit"),
		YUP_SW_ROTLIMIT("Y upper software rotational limit"),
		YLOW_SW_ROTLIMIT("Y lower software rotational limit"),
		
		// Byte 2
		ZUP_SW_LIMIT("Z upper software position limit"),
		ZLOW_SW_LIMIT("Z lower software position limit"),
		ZUP_SW_ROTLIMIT("Z upper software rotational limit"),
		ZLOW_SW_ROTLIMIT("Z lower software rotational limit");
		
		/**
		 * Constructor
		 */
		private SubrefLimits(String title) {
			this.title=title;
		}
		
		/**
		 * The tile of the row
		 */
		public final String title;
		
		/**
		 * The value of the row
		 */
		public final StringBuilder value = new StringBuilder(ValueDisplayer.NOT_AVAILABLE);
		
		/**
		 * Set all the values as not available
		 */
		public static void setNotAvailable() {
			for (SubrefLimits limit: SubrefLimits.values()) {
				if (limit.value.length()>0) {
					limit.value.delete(0, limit.value.length());
				}
				if (limit!=TITLE) {
					limit.value.append(ValueDisplayer.NOT_AVAILABLE);
				}
			}
		}
	}
	
	/**
	 * Constructor 
	 * 
	 * @param mnt The vertex prototype to get/set the shutter
	 */
	public AEMSubrefModel(Mount mnt) {
		if (mnt==null) {
			throw new IllegalArgumentException("Mount can't be null");
		}
		if (!(mnt instanceof MountAEM)) {
			throw new IllegalArgumentException("Mount illegal type");
		}
		mount=(MountAEM)mnt;
	}
	
	public int getRowCount() {
		return SubrefStatus.values().length+SubrefLimits.values().length;
	}

	
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex==0) {
			if (rowIndex<SubrefStatus.values().length) {
				return SubrefStatus.values()[rowIndex].title;
			} else {
				return SubrefLimits.values()[rowIndex-SubrefStatus.values().length].title;
			}
		} else {
			if (rowIndex<SubrefStatus.values().length) {
				return SubrefStatus.values()[rowIndex].value;
			} else {
				return SubrefLimits.values()[rowIndex-SubrefStatus.values().length].value;
			}
		}
	}
	
	/**
	 * Update the limits from the component
	 * 
	 * @see CommonStatusModel
	 */
	protected ValueState refreshLimits() {
		if (mount==null) {
			SubrefLimits.setNotAvailable();
			return ValueState.NORMAL;
		}
		ValueHolder<LongArrayBit> limits = mount.getSubreflector().getLimits();
		if (limits==null || limits.getValue()==null) {
			SubrefLimits.setNotAvailable();
			return ValueState.ERROR;
		}
		
		// The returned value
		ValueState ret = ValueState.NORMAL;
		
		// Byte 0
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,0), ret, SubrefLimits.XUP_SW_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,1), ret, SubrefLimits.XLOW_SW_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,2), ret, SubrefLimits.XUP_SW_ROTLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,3), ret, SubrefLimits.XLOW_SW_ROTLIMIT.value , limits);
		
		// Byte 1
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,0), ret, SubrefLimits.YUP_SW_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,1), ret, SubrefLimits.YLOW_SW_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,2), ret, SubrefLimits.YUP_SW_ROTLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,3), ret, SubrefLimits.YLOW_SW_ROTLIMIT.value , limits);
		
		// Byte2
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,0), ret, SubrefLimits.ZUP_SW_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,1), ret, SubrefLimits.ZLOW_SW_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,2), ret, SubrefLimits.ZUP_SW_ROTLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,3), ret, SubrefLimits.ZLOW_SW_ROTLIMIT.value , limits);
		
		return ret;
	}
	
	/**
	 * Update the state from the component
	 * 
	 * @see CommonStatusModel
	 */
	protected ValueState refreshState() {
		if (mount==null) {
			SubrefStatus.setNotAvailable();
			return ValueState.NORMAL;
		}
		ValueHolder<LongArrayBit> status = mount.getSubreflector().getStatus();
		if (status==null || status.getValue()==null) {
			SubrefStatus.setNotAvailable();
			return ValueState.ERROR;
		}
		
		// The returned value
		ValueState ret = ValueState.NORMAL;
		
		// Byte 0
		ret=ValueDisplayer.formatStatusBitInverse(status.getValue().getBit(0,0), ret, SubrefStatus.PWR_MONITOR.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(0,1), ret, SubrefStatus.OVER_RUN.value , status);
		ret=ValueDisplayer.formatStatusBitInverse(status.getValue().getBit(0,2), ret, SubrefStatus.INITED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(0,3), ret, SubrefStatus.INITIALIZING.value , status);
		ret=ValueDisplayer.formatStatusBitInverse(status.getValue().getBit(0,4), ret, SubrefStatus.SERVO_STATE.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(0,5), ret, SubrefStatus.CONN_FAULT.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(0,6), ret, SubrefStatus.SM_OVERRIDE.value , status);
	
		// Byte 1
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,0), ret, SubrefStatus.STRUT1_MOTION.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,1), ret, SubrefStatus.STRUT2_MOTION.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,2), ret, SubrefStatus.STRUT3_MOTION.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,3), ret, SubrefStatus.STRUT4_MOTION.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,4), ret, SubrefStatus.STRUT5_MOTION.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,5), ret, SubrefStatus.STRUT6_MOTION.value , status);
		
		// Byte 2
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,0), ret, SubrefStatus.STRUT1_CTR_ERR.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,1), ret, SubrefStatus.STRUT2_CTR_ERR.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,2), ret, SubrefStatus.STRUT3_CTR_ERR.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,3), ret, SubrefStatus.STRUT4_CTR_ERR.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,4), ret, SubrefStatus.STRUT5_CTR_ERR.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,5), ret, SubrefStatus.STRUT6_CTR_ERR.value , status);
		
		// Byte 3
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,0), ret, SubrefStatus.HEXAPOD_TEMP_MONITOR.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,1), ret, SubrefStatus.ACT1_OVERTEMP.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,2), ret, SubrefStatus.ACT2_OVERTEMP.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,3), ret, SubrefStatus.ACT3_OVERTEMP.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,4), ret, SubrefStatus.ACT4_OVERTEMP.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,5), ret, SubrefStatus.ACT5_OVERTEMP.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,5), ret, SubrefStatus.ACT6_OVERTEMP.value , status);
		
		return ret;
	}
}
