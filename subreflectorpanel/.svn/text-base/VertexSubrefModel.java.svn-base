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
import alma.control.gui.antennamount.mount.vertex.MountVertex;
import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.ValueState;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;

public class VertexSubrefModel extends CommonStatusModel {
	
	// The mount
	private MountVertex mount;
	
	/**
	 * The status of the vertex prototype subreflector
	 * Refer to the Vertex ICD GET_SUBREF_STATUS
	 * for further details
	 * 
	 * @author acaproni
	 *
	 */
	private enum VPSStatus {
		TITLE("<HTML><FONT color=\"blue\"><B>Status</B></FONT>"),
		
		// Byte 1
		AMPACT1_OVERTEMP("Amplifier actuator 1 overtemperature"),
		AMPACT2_OVERTEMP("Amplifier actuator 2 overtemperature"),
		AMPACT3_OVERTEMP("Amplifier actuator 3 overtemperature"),
		AMPACT4_OVERTEMP("Amplifier actuator 4 overtemperature"),
		AMPACT5_OVERTEMP("Amplifier actuator 5 overtemperature"),
		AMPACT6_OVERTEMP("Amplifier actuator 6 overtemperature"),
		
		// Byte 2
		ACT1_NOTMOVE("Actuator 1 does not move"),
		ACT2_NOTMOVE("Actuator 2 does not move"),
		ACT3_NOTMOVE("Actuator 3 does not move"),
		ACT4_NOTMOVE("Actuator 4 does not move"),
		ACT5_NOTMOVE("Actuator 5 does not move"),
		ACT6_NOTMOVE("Actuator 6 does not move"),
		
		// Byte 3
		ACT1_NOTINITED("Actuator 1 not initialized"),
		ACT2_NOTINITED("Actuator 2 not initialized"),
		ACT3_NOTINITED("Actuator 3 not initialized"),
		ACT4_NOTINITED("Actuator 4 not initialized"),
		ACT5_NOTINITED("Actuator 5 not initialized"),
		ACT6_NOTINITED("Actuator 6 not initialized"),
		HEXAPOD_NITIALIZING("HEXAPOD in initializing mode"),
		
		// Byte 4
		PS_FAILURE("Subreflector drive PS failure"),
		CAN_FAULT("Communication error ACU-HPC"),
		TEMPERATURE_LOW("Temerature too low");
		
		/**
		 * Constructor
		 */
		private VPSStatus(String title) {
			this.title=title;
			this.value.append(ValueDisplayer.NOT_AVAILABLE);
		}
		
		/**
		 * The tile of the row
		 */
		public final String title;
		
		/**
		 * The value of the row
		 */
		public final StringBuilder value=new StringBuilder();
		
		/**
		 * Set all the values as not available
		 */
		public static void setNotAvailable() {
			for (VPSStatus state: VPSStatus.values()) {
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
	 * Refer to the Vertex ICD GET_SUBREF_LIMITS
	 * for further details
	 * 
	 * @author acaproni
	 *
	 */
	private enum VPSLimits {
		TITLE("<HTML><FONT color=\"blue\"><B>Limit switches</B></FONT>"),
		
		// Byte 0
		ACT1_NEGLIMIT("Actuator 1 negative limit"),
		ACT2_NEGLIMIT("Actuator 2 negative limit"),
		ACT3_NEGLIMIT("Actuator 3 negative limit"),
		ACT4_NEGLIMIT("Actuator 4 negative limit"),
		ACT5_NEGLIMIT("Actuator 5 negative limit"),
		ACT6_NEGLIMIT("Actuator 6 negative limit"),
		
		// Byte 1
		ACT1_POSLIMIT("Actuator 1 positive limit"),
		ACT2_POSLIMIT("Actuator 2 positive limit"),
		ACT3_POSLIMIT("Actuator 3 positive limit"),
		ACT4_POSLIMIT("Actuator 4 positive limit"),
		ACT5_POSLIMIT("Actuator 5 positive limit"),
		ACT6_POSLIMIT("Actuator 6 positive limit"),
		
		// Byte 2
		HEX_X_NEGLIMIT("Hexapod negative x-limit"),
		HEX_X_POSLIMIT("Hexapod positive x-limit"),
		HEX_Y_NEGLIMIT("Hexapod negative y-limit"),
		HEX_Y_POSLIMIT("Hexapod positive y-limit"),
		HEX_Z_NEGLIMIT("Hexapod negative z-limit"),
		HEX_Z_POSLIMIT("Hexapod positive z-limit"),
		
		// Byte 3
		HEX_X_NEGTLIMIT("Hexapod negative Tx-limit"),
		HEX_X_POSTLIMIT("Hexapod positive Tx-limit"),
		HEX_Y_NEGTLIMIT("Hexapod negative Ty-limit"),
		HEX_Y_POSTLIMIT("Hexapod positive Ty-limit"),
		HEX_Z_NEGTLIMIT("Hexapod negative Tz-limit"),
		HEX_Z_POSTLIMIT("Hexapod positive Tz-limit"),
		
		// Byte 4
		HEX_VERT_COLLISION1("Hexapod vert. collision sw. 1"),
		HEX_VERT_COLLISION2("Hexapod vert. collision sw. 2"),
		HEX_VERT_COLLISION3("Hexapod vert. collision sw. 3"),
		HEX_VERT_COLLISION4("Hexapod vert. collision sw. 4"),
		HEX_VERT_COLLISION5("Hexapod vert. collision sw. 5"),
		HEX_VERT_COLLISION6("Hexapod vert. collision sw. 6"),
		HEX_VERT_COLLISION7("Hexapod vert. collision sw. 7"),
		HEX_VERT_COLLISION8("Hexapod vert. collision sw. 8"),
		
		// Byte 5
		HEX_HR_COLLISION1("Hexapod hor. collision sw. 1"),
		HEX_HR_COLLISION2("Hexapod hor. collision sw. 2"),
		HEX_HR_COLLISION3("Hexapod hor. collision sw. 3"),
		HEX_HR_COLLISION4("Hexapod hor. collision sw. 4"),
		HEX_HR_COLLISION5("Hexapod hor. collision sw. 5"),
		HEX_HR_COLLISION6("Hexapod hor. collision sw. 6"),
		HEX_HR_COLLISION7("Hexapod hor. collision sw. 7"),
		HEX_HR_COLLISION8("Hexapod hor. collision sw. 8");
		
		/**
		 * Constructor
		 */
		private VPSLimits(String title) {
			this.title=title;
			this.value.append(ValueDisplayer.NOT_AVAILABLE);
		}
		
		/**
		 * The tile of the row
		 */
		public final String title;
		
		/**
		 * The value of the row
		 */
		public final StringBuilder value=new StringBuilder();
		
		
		
		/**
		 * Set all the values as not available
		 */
		public static void setNotAvailable() {
			for (VPSLimits limit: VPSLimits.values()) {
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
	public VertexSubrefModel(Mount mnt) {
		if (mnt==null) {
			throw new IllegalArgumentException("Mount can't be null");
		}
		if (!(mnt instanceof MountVertex)) {
			throw new IllegalArgumentException("Mount illegal type");
		}
		mount=(MountVertex)mnt;
	}
	
	public int getRowCount() {
		return VPSStatus.values().length+VPSLimits.values().length;
	}

	
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex==0) {
			if (rowIndex<VPSStatus.values().length) {
				return VPSStatus.values()[rowIndex].title;
			} else {
				return VPSLimits.values()[rowIndex-VPSStatus.values().length].title;
			}
		} else {
			if (rowIndex<VPSStatus.values().length) {
				return VPSStatus.values()[rowIndex].value;
			} else {
				return VPSLimits.values()[rowIndex-VPSStatus.values().length].value;
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
			VPSLimits.setNotAvailable();
			return ValueState.NORMAL;
		}
		ValueHolder<LongArrayBit> limits = mount.getSubreflector().getLimits();
		if (limits==null || limits.getValue()==null) {
			VPSLimits.setNotAvailable();
			return ValueState.ERROR;
		}
		
		// The returned value
		ValueState ret = ValueState.NORMAL;
		
		// Byte 0
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,0), ret, VPSLimits.ACT1_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,1), ret, VPSLimits.ACT2_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,2), ret, VPSLimits.ACT3_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,3), ret, VPSLimits.ACT4_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,4), ret, VPSLimits.ACT5_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,5), ret, VPSLimits.ACT6_NEGLIMIT.value , limits);
		
		// Byte 1
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,0), ret, VPSLimits.ACT1_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,1), ret, VPSLimits.ACT2_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,2), ret, VPSLimits.ACT3_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,3), ret, VPSLimits.ACT4_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,4), ret, VPSLimits.ACT5_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,5), ret, VPSLimits.ACT6_POSLIMIT.value , limits);
		
		// Byte2
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,0), ret, VPSLimits.HEX_X_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,1), ret, VPSLimits.HEX_X_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,2), ret, VPSLimits.HEX_Y_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,3), ret, VPSLimits.HEX_Y_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,4), ret, VPSLimits.HEX_Z_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,5), ret, VPSLimits.HEX_Z_POSLIMIT.value , limits);
		
		// Byte 3
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,0), ret, VPSLimits.HEX_X_NEGTLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,1), ret, VPSLimits.HEX_X_POSTLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,2), ret, VPSLimits.HEX_Y_NEGTLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,3), ret, VPSLimits.HEX_Y_POSTLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,4), ret, VPSLimits.HEX_Z_NEGTLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,5), ret, VPSLimits.HEX_Z_POSTLIMIT.value , limits);
		
		// Byte 4
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,0), ret, VPSLimits.HEX_VERT_COLLISION1.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,1), ret, VPSLimits.HEX_VERT_COLLISION2.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,2), ret, VPSLimits.HEX_VERT_COLLISION3.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,3), ret, VPSLimits.HEX_VERT_COLLISION4.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,4), ret, VPSLimits.HEX_VERT_COLLISION5.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,5), ret, VPSLimits.HEX_VERT_COLLISION6.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,6), ret, VPSLimits.HEX_VERT_COLLISION7.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,7), ret, VPSLimits.HEX_VERT_COLLISION8.value , limits);
		
		// Byte 5
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,0), ret, VPSLimits.HEX_HR_COLLISION1.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,1), ret, VPSLimits.HEX_HR_COLLISION2.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,2), ret, VPSLimits.HEX_HR_COLLISION3.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,3), ret, VPSLimits.HEX_HR_COLLISION4.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,4), ret, VPSLimits.HEX_HR_COLLISION5.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,5), ret, VPSLimits.HEX_HR_COLLISION6.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,6), ret, VPSLimits.HEX_HR_COLLISION7.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,7), ret, VPSLimits.HEX_HR_COLLISION8.value , limits);
		
		return ret;
	}
	
	/**
	 * Update the state from the component
	 * 
	 * @see CommonStatusModel
	 */
	protected ValueState refreshState() {
		if (mount==null) {
			VPSStatus.setNotAvailable();
			return ValueState.NORMAL;
		}
		ValueHolder<LongArrayBit> status = mount.getSubreflector().getStatus();
		if (status==null || status.getValue()==null) {
			VPSStatus.setNotAvailable();
			return ValueState.ERROR;
		}
		
		// The returned value
		ValueState ret = ValueState.NORMAL;
		
		// Byte0
		// Byte 1
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,0), ret, VPSStatus.AMPACT1_OVERTEMP.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,1), ret, VPSStatus.AMPACT2_OVERTEMP.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,2), ret, VPSStatus.AMPACT3_OVERTEMP.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,3), ret, VPSStatus.AMPACT4_OVERTEMP.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,4), ret, VPSStatus.AMPACT5_OVERTEMP.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,5), ret, VPSStatus.AMPACT6_OVERTEMP.value , status);

		// Byte 2
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,0), ret, VPSStatus.ACT1_NOTMOVE.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,1), ret, VPSStatus.ACT2_NOTMOVE.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,2), ret, VPSStatus.ACT3_NOTMOVE.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,3), ret, VPSStatus.ACT4_NOTMOVE.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,4), ret, VPSStatus.ACT5_NOTMOVE.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,5), ret, VPSStatus.ACT6_NOTMOVE.value , status);
		
		// Byte 3
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,0), ret, VPSStatus.ACT1_NOTINITED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,1), ret, VPSStatus.ACT2_NOTINITED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,2), ret, VPSStatus.ACT3_NOTINITED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,3), ret, VPSStatus.ACT4_NOTINITED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,4), ret, VPSStatus.ACT5_NOTINITED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,5), ret, VPSStatus.ACT6_NOTINITED.value , status);
		
		// Byte 4
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(4,0), ret, VPSStatus.PS_FAILURE.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(4,1), ret, VPSStatus.CAN_FAULT.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(4,2), ret, VPSStatus.TEMPERATURE_LOW.value , status);
		
		return ret;
	}
}
