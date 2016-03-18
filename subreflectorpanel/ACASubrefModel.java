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
import alma.control.gui.antennamount.mount.aca.MountACA;
import alma.control.gui.antennamount.mount.a7m.MountA7M;
import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.ValueState;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;

public class ACASubrefModel extends CommonStatusModel {
	
	// The mount
	private Mount mount; // MountACA and MountA7M
	
	/**
	 * The status of the ACA prototype subreflector
	 * Refer to the ACA ICD GET_SUBREF_STATUS
	 * for further details
	 * 
	 * @author acaproni
	 *
	 */
	private enum VPSStatus {
		TITLE("<HTML><FONT color=\"blue\"><B>Status</B></FONT>"),
		
		// Byte 0
		LINK1_BRAKE_FASTEN("Link-1 brake engaged"),
		LINK2_BRAKE_FASTEN("Link-2 brake engaged"),
		LINK3_BRAKE_FASTEN("Link-3 brake engaged"),
		LINK4_BRAKE_FASTEN("Link-4 brake engaged"),
		LINK5_BRAKE_FASTEN("Link-5 brake engaged"),
		LINK6_BRAKE_FASTEN("Link-6 brake engaged"),
		
		// Byte 1
		LINK1_DPA_ALARM("Link-1 DPA alarm"),
		LINK2_DPA_ALARM("Link-2 DPA alarm"),
		LINK3_DPA_ALARM("Link-3 DPA alarm"),
		LINK4_DPA_ALARM("Link-4 DPA alarm"),
		LINK5_DPA_ALARM("Link-5 DPA alarm"),
		LINK6_DPA_ALARM("Link-6 DPA alarm"),
		
		// Byte 2
		BRAKE_POWER_FAIL("Brake power failure"),
		DPA_CB_OFF("DPA circuit breaker off"),
		DPA_COND_OFF("DPA contactor off"),
		
		// Byte 3
		ACU2SUBREF_LINE_DISCON("ACU to subreflector controller line disconnected"),
		SUBREF2ACU_LINE_DISCON("Subreflector controller to ACU line disconnected"),
		SUBREF_CONT_FAULT("Subreflector controller fault"),
		SUBREF_AD_CONV_FAIL("Subreflector controller A/D converter failure"),
		SUBREF_CLOCK_FAULT("Subreflector controller misses timing pulse"),
		
		// Byte 4
		LINK1_ENCODER_DISABLED("Link-1 encoder disabled"),
		LINK2_ENCODER_DISABLED("Link-2 encoder disabled"),
		LINK3_ENCODER_DISABLED("Link-3 encoder disabled"),
		LINK4_ENCODER_DISABLED("Link-4 encoder disabled"),
		LINK5_ENCODER_DISABLED("Link-5 encoder disabled"),
		LINK6_ENCODER_DISABLED("Link-6 encoder disabled"),
		
		//Byte 5
		LINK1_DRIVE_ON_TO("Link-1 drive on timeout"),
		LINK2_DRIVE_ON_TO("Link-2 drive on timeout"),
		LINK3_DRIVE_ON_TO("Link-3 drive on timeout"),
		LINK4_DRIVE_ON_TO("Link-4 drive on timeout"),
		LINK5_DRIVE_ON_TO("Link-5 drive on timeout"),
		LINK6_DRIVE_ON_TO("Link-6 drive on timeout"),
		// Byte 5 bit 6 is reserved
		LINK_RESET_TO("LinkReset timeout");
		
		/**
		 * Constructor
		 */
		private VPSStatus(String title) {
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
	 * The limits of the ACA prototype subreflector
	 * Refer to the ACA ICD GET_SUBREF_LIMITS
	 * for further details
	 * 
	 * @author acaproni
	 *
	 */
	private enum VPSLimits {
		TITLE("<HTML><FONT color=\"blue\"><B>Limit switches</B></FONT>"),
		
		// Byte 0
		XAXIS_POS_SOFT_LIMIT("X axis positive software limit"), 
		XAXIS_NEG_SOFT_LIMIT("X axis negative software limit"), 
			// bit2-3: reserved 
		LINK1_POSLIMIT("Link-1 positive limit"), 
		LINK1_NEGLIMIT("Link-1 negative limit"), 
		LINK1_OUTOFRANGE("Link-1 out of range"), 
			// bit7: reserved 
		
		// Byte 1
		YAXIS_POS_SOFT_LIMIT("Y axis positive software limit"), 
		YAXIS_NEG_SOFT_LIMIT("Y axis negative software limit"), 
			// bit2-3: reserved 
		LINK2_POSLIMIT("Link-2 positive limit"), 
		LINK2_NEGLIMIT("Link-2 negative limit"), 
		LINK2_OUTOFRANGE("Link-2 out of range"), 
			// bit7: reserved 
		
		// Byte 2
		ZAXIS_POS_SOFT_LIMIT("Z axis positive software limit"), 
		ZAXIS_NEG_SOFT_LIMIT("Z axis negative software limit"), 
			// bit2-3: reserved 
		LINK3_POSLIMIT("Link-3 positive limit"), 
		LINK3_NEGLIMIT("Link-3 negative limit"), 
		LINK3_OUTOFRANGE("Link-3 out of range"), 
			// bit7: reserved 
		
		// Byte 3
		THETAX_POS_SOFT_LIMIT("Theta-x positive software limit"),
		THETAX_NEG_SOFT_LIMIT("Theta-x negative software limit"),
			// bit2-3: reserved 
		LINK4_POSLIMIT("Link-4 positive limit"), 
		LINK4_NEGLIMIT("Link-4 negative limit"), 
		LINK4_OUTOFRANGE("Link-4 out of range"), 
			// bit7: reserved 
		
		// Byte 4
		THETAY_POS_SOFT_LIMIT("Theta-y positive software limit"),
		THETAY_NEG_SOFT_LIMIT("Theta-y negative software limit"),
			// bit2-3: reserved 
		LINK5_POSLIMIT("Link-5 positive limit"), 
		LINK5_NEGLIMIT("Link-5 negative limit"), 
		LINK5_OUTOFRANGE("Link-5 out of range"), 
			// bit7: reserved 
		
		// Byte 5
		THETAZ_POS_SOFT_LIMIT("Theta-z positive software limit"),
		THETAZ_NEG_SOFT_LIMIT("Theta-z negative software limit"),
			// bit2-3: reserved 
		LINK6_POSLIMIT("Link-6 positive limit"), 
		LINK6_NEGLIMIT("Link-6 negative limit"), 
		LINK6_OUTOFRANGE("Link-6 out of range"), 
			// bit7: reserved 
		
		// Byte 6
		COLLISION_INSIDE("Collision limit inside"), 
		COLLISION_OUTSIDE("Collision limit outside"), 
		COLLISION_DISABLED("Collision limit disabled"); 
			// bit3-7: reserved 
 
		
		/**
		 * Constructor
		 */
		private VPSLimits(String title) {
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
	 * @param mnt The ACA to get/set the shutter
	 */
	public ACASubrefModel(Mount mnt) {
		if (mnt==null) {
			throw new IllegalArgumentException("Mount can't be null");
		}
		if (mnt instanceof MountACA) {
			//mount=(MountACA)mnt;
			mount=mnt;
		} else if (mnt instanceof MountA7M) {
			//mount=(MountA7M)mnt;
			mount=mnt;
		}
		else {
			throw new IllegalArgumentException("Mount illegal type");
		}
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
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,0), ret, VPSLimits.XAXIS_POS_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,1), ret, VPSLimits.XAXIS_NEG_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,4), ret, VPSLimits.LINK1_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,5), ret, VPSLimits.LINK1_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(0,6), ret, VPSLimits.LINK1_OUTOFRANGE.value , limits);
		
		// Byte 1
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,0), ret, VPSLimits.YAXIS_POS_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,1), ret, VPSLimits.YAXIS_NEG_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,4), ret, VPSLimits.LINK2_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,5), ret, VPSLimits.LINK2_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(1,6), ret, VPSLimits.LINK2_OUTOFRANGE.value , limits);
		
		// Byte2
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,0), ret, VPSLimits.ZAXIS_POS_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,1), ret, VPSLimits.ZAXIS_NEG_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,4), ret, VPSLimits.LINK3_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,5), ret, VPSLimits.LINK3_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(2,6), ret, VPSLimits.LINK3_OUTOFRANGE.value , limits);
		
		// Byte 3
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,0), ret, VPSLimits.THETAX_POS_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,1), ret, VPSLimits.THETAX_NEG_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,4), ret, VPSLimits.LINK4_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,5), ret, VPSLimits.LINK4_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(3,6), ret, VPSLimits.LINK4_OUTOFRANGE.value , limits);
		
		// Byte 4
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,0), ret, VPSLimits.THETAY_POS_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,1), ret, VPSLimits.THETAY_NEG_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,4), ret, VPSLimits.LINK5_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,5), ret, VPSLimits.LINK5_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(4,6), ret, VPSLimits.LINK5_OUTOFRANGE.value , limits);
		
		// Byte 5
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,0), ret, VPSLimits.THETAZ_POS_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,1), ret, VPSLimits.THETAZ_NEG_SOFT_LIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,4), ret, VPSLimits.LINK6_POSLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,5), ret, VPSLimits.LINK6_NEGLIMIT.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(5,6), ret, VPSLimits.LINK6_OUTOFRANGE.value , limits);
		
		// Byte 6
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(6,0), ret, VPSLimits.COLLISION_INSIDE.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(6,1), ret, VPSLimits.COLLISION_OUTSIDE.value , limits);
		ret=ValueDisplayer.formatStatusBit(limits.getValue().getBit(6,2), ret, VPSLimits.COLLISION_DISABLED.value , limits);
		
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
		
		// Byte 0
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(0,0), ret, VPSStatus.LINK1_BRAKE_FASTEN.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(0,1), ret, VPSStatus.LINK2_BRAKE_FASTEN.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(0,2), ret, VPSStatus.LINK3_BRAKE_FASTEN.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(0,3), ret, VPSStatus.LINK4_BRAKE_FASTEN.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(0,4), ret, VPSStatus.LINK5_BRAKE_FASTEN.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(0,5), ret, VPSStatus.LINK6_BRAKE_FASTEN.value , status);
		
		// Byte 1
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,0), ret, VPSStatus.LINK1_DPA_ALARM.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,1), ret, VPSStatus.LINK2_DPA_ALARM.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,2), ret, VPSStatus.LINK3_DPA_ALARM.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,3), ret, VPSStatus.LINK4_DPA_ALARM.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,4), ret, VPSStatus.LINK5_DPA_ALARM.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(1,5), ret, VPSStatus.LINK6_DPA_ALARM.value , status);
		
		// Byte 2
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,0), ret, VPSStatus.BRAKE_POWER_FAIL.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,1), ret, VPSStatus.DPA_CB_OFF.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(2,2), ret, VPSStatus.DPA_COND_OFF.value , status);
		
		// Byte 3
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,0), ret, VPSStatus.ACU2SUBREF_LINE_DISCON.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,1), ret, VPSStatus.SUBREF2ACU_LINE_DISCON.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,2), ret, VPSStatus.SUBREF_CONT_FAULT.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,3), ret, VPSStatus.SUBREF_AD_CONV_FAIL.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(3,4), ret, VPSStatus.SUBREF_CLOCK_FAULT.value , status);
		
		// Byte 4
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(4,0), ret, VPSStatus.LINK1_ENCODER_DISABLED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(4,1), ret, VPSStatus.LINK2_ENCODER_DISABLED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(4,2), ret, VPSStatus.LINK3_ENCODER_DISABLED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(4,3), ret, VPSStatus.LINK4_ENCODER_DISABLED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(4,4), ret, VPSStatus.LINK5_ENCODER_DISABLED.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(4,5), ret, VPSStatus.LINK6_ENCODER_DISABLED.value , status);

		// Byte 5
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(5,0), ret, VPSStatus.LINK1_DRIVE_ON_TO.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(5,1), ret, VPSStatus.LINK2_DRIVE_ON_TO.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(5,2), ret, VPSStatus.LINK3_DRIVE_ON_TO.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(5,3), ret, VPSStatus.LINK4_DRIVE_ON_TO.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(5,4), ret, VPSStatus.LINK5_DRIVE_ON_TO.value , status);
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(5,5), ret, VPSStatus.LINK6_DRIVE_ON_TO.value , status);
		
 		// Byte 5 bit 6 is reserved
		ret=ValueDisplayer.formatStatusBit(status.getValue().getBit(5,7), ret, VPSStatus.LINK_RESET_TO.value , status);
		
		return ret;
	}
}
