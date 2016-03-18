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
 * @version $Id: MountVA.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */
package alma.control.gui.antennamount.tracking;

import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.mount.vertex.MountVertex;
import alma.control.gui.antennamount.utils.ValueState;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;
import alma.control.gui.antennamount.utils.ValueDisplayer;

/**
 * 
 * The class to show details of the detailed status of the Vertex production
 *
 */
public class MountVA extends MountDetails implements MountInterface {
	
	/**
	 * The titles of the rows of the status of the VA.
	 * @see GET_SYSTEM_STATUS IDL method
	 * 
	 * The order of declaration in this enum reflect the order in the table
	 * 
	 * @author acaproni
	 *
	 */
	private enum VAStatus {
		// Byte 0
		EMERGENCY_STOP("Emergency stop"),
		STAIRWAY_INTERLOCK("Stairway interlock"),
		HANDLING_INTERLOCK("Handling interlock"),
		SMOKE_ALARM("Smoke alarm"),
		ACU_BOOT("ACU booting failure"),
		SURVIVAL_FOR_MISSING_COMMANDS("Survival stow due to missing commands after idle_time"),
		SURVIVAL_FOR_MISSING_TIMING("Survival stow due to missing timing after idle_time"),
		TIMING_PULSE("Timing pulse missing"),
		
		// Byte 1
		SAFE_SWITCH("Safe switch"),
		POWER_FAILURE("Power failure"),
		V24_FAILURE("24V failure"),
		BREAKER_FAILURE("Breaker failure"),
		ACU_FAN("ACU fan failure"),
		ACU_PLC_COMM_ERROR("Comm error (ACU-PLC)"),
		CABINET_OVERTEMPERATURE("Cabinet over-temperature"),
		ALMA_E_STOP("ALMA e-stop"),
		
		// Byte 2
		RAMP_TO_RECEIVER("Ramp to receiver cabin not tilted up"),
		GATE_PLATFORM2("Gate platform 2 open"),
		LADDER_TO_PLATFORM1("Ladder to platform 1 retracted"),
		RECEIVER_DOOR_OPEN("Receiver cabin door open"),
		PEDESTAL_DOOR_OPEN("Pedestal door open"),
		DRIVE_CABINET_OPEN("Drive cabinet open"),
		
		// Byte 3
		CABINET("Cabinet"),
		AZ_DRIVES("Az drives"),
		INSIDE_ANTENNA_BASE("Inside antenna base"),
		PLATFORM2("Platform 2"),
		LADDER_TO_PLATFORM("Ladder to platform 1"),
		RECEIVER_CABIN("Receiver cabin"),
		PORTABLE_CONTROL_UNIT("Portable control unit"),
		OUTSIDE_OF_PEDESTAL("At outside of pedestal"),
		
		// Byte 4
		AUTO_LUBRIFICATION_FAILURE("Auto lubrification system failure"),
		AUTO_LUBRIFICATION_MALFUNCTION("Auto lubrification system malfunction"),
		
		// Byte 5
		ACU_UPS_COMM_ERROR("Comm error (ACU-UPS)");
		
		public final String title;
		public final StringBuilder value = new StringBuilder();
		
		private VAStatus(String title) {
			this.title=title;
		}
	}
	
	/**
	 *  The titles of the rows of the status of the VA elevation.
	 *  
	 *  The order of declaration in this enum reflects the order in the table
	 * 
	 * @author acaproni
	 *
	 */
	private enum ElStatus {
		// Byte 0
		PRELIMIT_UP("EL: Prelimit up"),
		PRELIMIT_DOWN("EL: Prelimit down"),
		LIMIT_UP("EL: Limit up"),
		LIMIT_DOWN("EL: Limit down"),
		EMERGENCY_LIMIT_UP("EL: Emergency limit up"),
		EMERGENCY_LIMIT_DOWN("EL: Emergency limit down"),
		EMERGENCY_LIMIT2_CW("EL: 2nd emergency limit cw"),
		EMERGENCY_LIMIT2_CCW("EL: 2nd emergency limit ccw"),
		
		// Byte 1
		SERVO_FAILURE("EL: Servo failure"),
		OVERSPEED("EL: Overspeed"),
		IMMOBILE("EL: Immobile"),
		SPEED_ZERO("EL: Speed zero"),
		STOW_POSITION("EL: Stow position"),
		ENCODER_FAILURE("EL: Encoder failure"),
		INSANE_VELOCITY("EL: Insane velocity feedback"),
		
		// Byte 2
		BRAKE1("EL: Brake 1"),
		BRAKE2("EL: Brake 2"),
		BRAKE3("EL: Brake 3"),
		BRAKE4("EL: Brake 4"),
		
		// Byte 3
		AMPLIFIER1("EL: Amplifier 1"),
		AMPLIFIER2("EL: Amplifier 2"),
		AMPLIFIER3("EL: Amplifier 3"),
		AMPLIFIER4("EL: Amplifier 4"),
		CAN_COMMUNICATION("EL: CAN communication error to servo amps"),
		BREAKER("EL: Breaker fault"),
		
		// Byte 4
		MOTOR1_OVERTEMPERATURE("EL: Motor 1 over temperature"),
		MOTOR2_OVERTEMPERATURE("EL: Motor 2 over temperature"),
		MOTOR3_OVERTEMPERATURE("EL: Motor 3 over temperature"),
		MOTOR4_OVERTEMPERATURE("EL: Motor 4 over temperature"),
		REGENERATION_RESISTORE("EL: Regeneration resistor over temp."),
		SERVO_OSCILLATION("EL: Servo oscillation"),
		AUXILIARY_ENC("EL: Auxiliary encoder failure"),
		POS_DEVIATION("EL: Position deviation"),
		
		// Byte 5
		
		// Byte 6
		AUX_MOTOR1_2("EL: AUX motor 1&2 off"),
		AUX_MOTOR3_4("EL: AUX motor 3&4 off"),
		GEAR1_OIL("EL: Gearbox 1 oil level warning"),
		GEAR2_OIL("EL: Gearbox 2 oil level warning"),
		GEAR3_OIL("EL: Gearbox 3 oil level warning"),
		GEAR4_OIL("EL: Gearbox 4 oil level warning"),
		
		// Byte 7
		COMPUTER_DISABLED("EL: Computer disabled"),
		AXIS_DISABLED("EL: Axis disabled"),
		HANDLED_CONTROL_UIT_OP("EL: Handheld cotrol unit operation"),
		AXIS_IN_STOP("EL: Axis in stop");
		
		public final String title;
		public final StringBuilder value = new StringBuilder();
		
		private ElStatus(String title) {
			this.title=title;
		}
	};
	
	/**
	 *  The titles of the rows of the status of the AEC Azimuth.
	 *  
	 *  The order of declaration in this enum reflect the order in the table
	 *  
	 *  @author acaproni
	 */
	private enum AzStatus {
		// byte 0
		PRELIMIT_CW("AZ: Prelimit CW"),
		PRELIMIT_CCW("AZ: Prelimit CCW"),
		LIMIT_CW("AZ: Limit CW"),
		LIMIT_CCW("AZ: Limit CCW"),
		EMERGENCY_LIMIT_CW("AZ: Emergency limit CW"),
		EMERGENCY_LIMIT_CCW("AZ: Emergency limit CCW"),
		EMERGENCY2_LIMIT_CW("AZ: 2nd emergency limit CW"),
		EMERGENCY2_LIMIT_CCW("AZ: 2nd emergency limit CCW"),
		
		// byte 1
		SERVO_FAILURE("AZ: Servo failure"),
		OVERSPEED("AZ: Overspeed"),
		IMMOBILE("AZ: Immobile"),
		SPEED_ZERO("AZ: Speed zero"),
		STOW_POSITION("AZ: Stow position"),
		ENCODER_FAILURE("AZ: Encoder failure"),
		INSANE_VELOCITY("AZ: Insane velocity feedback"),
		
		// byte 2
		BRAKE1("AZ: Brake 1"),
		BRAKE2("AZ: Brake 2"),
		
		// byte 3
		AMPLIFIER1("AZ: Amplifier 1"),
		AMPLIFIER2("AZ: Amplifier 2"),
		CAN_COMMUNICATION("AZ: CAN communication error to servo amps"),
		BREAKER_FAULT("AZ: Breaker fault"),
		
		// byte 4
		MOTOR1_OVERTEMPERATURE("AZ: Motor 1 over temperature"),
		MOTOR2_OVERTEMPERATURE("AZ: Motor 2 over temperature"),
		REGENERATION_RESISTORE("AZ: Regeneration resistor over temp."),
		SERVO_OSCILLATION("AZ: Servo oscillation"),
		AUXILIARY_ENC("AZ: Auxiliary encoder failure"),
		POS_DEVIATION("AZ: Position deviation"),
		
		// byte 5
		
		// byte 6
		AUX_MOTOR1_OFF("AZ: AUX motor 1 off"),
		AUX_MOTOR2_OFF("AZ: AUX motor 2 off"),
		GEAR1_OIL("AZ: Gearbox 1 oil level warning"),
		GEAR2_OIL("AZ: Gearbox 2 oil level warning"),
		
		// byte 7
		COMPUTER_DISABLED("AZ: Computer disabled"),
		AXIS_DISABLED("AZ: Axis disabled"),
		HANDLED_CONTROL_UIT_OP("AZ: Handled cotrol unit operation"),
		AXIS_IN_STOP("AZ: Axis in stop"),
		FLIP_FLOP_INCORRECT_POS("AZ: Flip-flop buffer pos. incorrectly"),
		HAND_CRANCK("AZ: Hand cranck inserted");
		
		public final String title;
		public final StringBuilder value = new StringBuilder();
		
		private AzStatus(String title) {
			this.title=title;
		}
	};
	
	/**
	 * The titles of the rows of the power status.
	 * @see GET_POWER_STATUS monitor point
	 * 
	 * The order of declaration in this enum reflect the order in the table
	 * 
	 * @author acaproni
	 *
	 */
	private enum PowerStatus {
		
		// Byte 0
		MAIN_SWITCH_TRANS("Main switch: transportable position"),
		MAIN_SWITCH_ANT_BASE("Main switch: antenna base position"),
		MAIN_CIRCUIT_BRK("Main circuit breaker"),
		LIGHTNING_ARREST("Lightning arrestor tripped"),
		
		// Byte 1
		SINGLE_PHASE_ILOCK("Single phase interlock"),
		REVERSE_PHASE("Reverse phase protection released"),
		CIRCUIT_BRK_ELECTRONIC("Circuit breaker Critical Electronic Bus on"),
		CIRCUIT_BRK_CRYO("Circuit breaker Critical Cryogenic Bus on"),
		CIRCUIT_BRK_NON_CRITICAL("Circuit breaker Non-Critical Bus on");
		
		public final String title;
		
		public final StringBuilder value = new StringBuilder(); 
		
		private PowerStatus(String title) {
			this.title=title;
		}
	}
		
	
	/**
	 * Constructor
	 *
	 */
	public MountVA() {
		ACU_ERROR_DESC = new String[] {
			alma.Control.MountACA.ACU_ERROR_DESC_00, // 0x00
			alma.Control.MountACA.ACU_ERROR_DESC_01, // 0x01
			alma.Control.MountACA.ACU_ERROR_DESC_02, //0x02
			alma.Control.MountACA.ACU_ERROR_DESC_03,//0x03
			alma.Control.MountACA.ACU_ERROR_DESC_04,//0x04
			alma.Control.MountACA.ACU_ERROR_DESC_05, // 0x05
			alma.Control.MountACA.ACU_ERROR_DESC_06, //0x06
			unknownAcuError, // 0x07
			unknownAcuError, // 0x08
			unknownAcuError, // 0x09
			unknownAcuError, // 0x0A
			unknownAcuError, // 0x0B
			unknownAcuError, // 0x0C
			unknownAcuError, // 0x0D
			unknownAcuError, // 0x0E
			unknownAcuError, // 0x0F
			alma.Control.MountACA.ACU_ERROR_DESC_10, //0x10
			alma.Control.MountACA.ACU_ERROR_DESC_11,//0x11
			alma.Control.MountACA.ACU_ERROR_DESC_12,//0x12
			alma.Control.MountACA.ACU_ERROR_DESC_13,//0x13
			alma.Control.MountACA.ACU_ERROR_DESC_14,//0x14
			alma.Control.MountACA.ACU_ERROR_DESC_15,//0x15
			alma.Control.MountACA.ACU_ERROR_DESC_16//0x16
		};
		statusVals = new String[VAStatus.values().length];
		elStatusVals = new String[ElStatus.values().length];
		azStatusVals = new String[AzStatus.values().length];
		powerStatusVals=new String[PowerStatus.values().length];
		updateError();
	}
	
	/**
	 * Update the status of the system (GET_SYSTEM_STATUS of the ICD)
	 *
	 * @param mnt The mount
	 */
	private ValueState updateSystemStatus(MountVertex mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.NORMAL;
		}
		ValueHolder<int[]> statusBits = mnt.getStatus();
		if (statusBits!=null && statusBits.getValue()!=null) {
			Long[] longBits = new Long[statusBits.getValue().length];
			if (longBits.length!=6) {
				updateStatusError();
				return ValueState.ERROR;
			}
			for (int t=0; t<longBits.length; t++) {
				longBits[t]=(long)statusBits.getValue()[t];
			}
			LongArrayBit bits = new LongArrayBit(longBits);
			ValueState ret = ValueState.NORMAL;
			// Byte 0
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,VAStatus.EMERGENCY_STOP.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,VAStatus.STAIRWAY_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,VAStatus.HANDLING_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,VAStatus.SMOKE_ALARM.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,VAStatus.ACU_BOOT.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,VAStatus.SURVIVAL_FOR_MISSING_COMMANDS.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,VAStatus.SURVIVAL_FOR_MISSING_TIMING.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,VAStatus.TIMING_PULSE.value,statusBits);
			
			// Byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,VAStatus.SAFE_SWITCH.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,VAStatus.POWER_FAILURE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,2),ret,VAStatus.V24_FAILURE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,3),ret,VAStatus.BREAKER_FAILURE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,4),ret,VAStatus.ACU_FAN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,5),ret,VAStatus.ACU_PLC_COMM_ERROR.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,6),ret,VAStatus.CABINET_OVERTEMPERATURE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,7),ret,VAStatus.ALMA_E_STOP.value,statusBits);
			
			// Byte 2
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,VAStatus.RAMP_TO_RECEIVER.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,3),ret,VAStatus.GATE_PLATFORM2.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,4),ret,VAStatus.LADDER_TO_PLATFORM1.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,5),ret,VAStatus.RECEIVER_DOOR_OPEN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,6),ret,VAStatus.PEDESTAL_DOOR_OPEN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,7),ret,VAStatus.DRIVE_CABINET_OPEN.value,statusBits);
			
			// Byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,VAStatus.CABINET.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,VAStatus.AZ_DRIVES.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,2),ret,VAStatus.INSIDE_ANTENNA_BASE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,3),ret,VAStatus.PLATFORM2.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,4),ret,VAStatus.LADDER_TO_PLATFORM.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,5),ret,VAStatus.RECEIVER_CABIN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,6),ret,VAStatus.PORTABLE_CONTROL_UNIT.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,7),ret,VAStatus.OUTSIDE_OF_PEDESTAL.value,statusBits);
			
			// Byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,VAStatus.AUTO_LUBRIFICATION_FAILURE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,1),ret,VAStatus.AUTO_LUBRIFICATION_MALFUNCTION.value,statusBits);
			
			// Byte 5
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,0),ret,VAStatus.ACU_UPS_COMM_ERROR.value,statusBits);
			for (int t=0; t<VAStatus.values().length; t++) {
				statusVals[t]=VAStatus.values()[t].value.toString();
			}
			return ret;
		} else {
			updateStatusError();
			return ValueState.ERROR;
		}
	}
	
	/**
	 * @see MountInterface
	 */
	public ValueState refreshValues(Mount mnt) {
		if (!(mnt instanceof MountVertex)) {
			throw new IllegalArgumentException("The mount is not MountVertex");
		}
		ValueState st=updateSystemStatus((MountVertex)mnt);
		st=ValueState.max(st, updateAzStatus((MountVertex)mnt));
		st=ValueState.max(st, updateElStatus((MountVertex)mnt));
		st=ValueState.max(st, updatePowerStatus((MountVertex)mnt));
		return st;
	}
	
	/**
	 * Refresh the values of the status of the azimuth
	 * 
	 * @param mnt The mount
	 */
	private ValueState updateAzStatus(MountVertex mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.NORMAL;
		}
		ValueHolder<int[]> azStatusBits = mnt.getAzStatus();
		if (azStatusBits!=null && azStatusBits.getValue()!=null) {
			Long[] longBits = new Long[azStatusBits.getValue().length];
			if (longBits.length!=8) {
				updateAzError();
				return ValueState.ERROR;
			}
			for (int t=0; t<longBits.length; t++) {
				longBits[t]=(long)azStatusBits.getValue()[t];
			}
			LongArrayBit bits = new LongArrayBit(longBits);
			ValueState ret = ValueState.NORMAL;
			// byte 0
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,AzStatus.PRELIMIT_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,AzStatus.PRELIMIT_CCW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,AzStatus.LIMIT_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,AzStatus.LIMIT_CCW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,AzStatus.EMERGENCY_LIMIT_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,AzStatus.EMERGENCY_LIMIT_CCW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,AzStatus.EMERGENCY2_LIMIT_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,AzStatus.EMERGENCY2_LIMIT_CCW.value,azStatusBits);
			
			// byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,AzStatus.SERVO_FAILURE.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,AzStatus.OVERSPEED.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,2),ret,AzStatus.IMMOBILE.value,azStatusBits);
			ret=ValueDisplayer.formatBit(bits.getBit(1,3),ret,AzStatus.SPEED_ZERO.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,4),ret,AzStatus.STOW_POSITION.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,5),ret,AzStatus.ENCODER_FAILURE.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,6),ret,AzStatus.INSANE_VELOCITY.value,azStatusBits);
			
			// byte 2 
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,AzStatus.BRAKE1.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,AzStatus.BRAKE2.value,azStatusBits);
			
			// byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,AzStatus.AMPLIFIER1.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,AzStatus.AMPLIFIER2.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,4),ret,AzStatus.CAN_COMMUNICATION.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,5),ret,AzStatus.BREAKER_FAULT.value,azStatusBits);
			
			// byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,AzStatus.MOTOR1_OVERTEMPERATURE.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,1),ret,AzStatus.MOTOR2_OVERTEMPERATURE.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,4),ret,AzStatus.REGENERATION_RESISTORE.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,5),ret,AzStatus.SERVO_OSCILLATION.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,6),ret,AzStatus.AUXILIARY_ENC.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,7),ret,AzStatus.POS_DEVIATION.value,azStatusBits);
			
			// byte 5
			
			// byte 6
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,0),ret,AzStatus.AUX_MOTOR1_OFF.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,1),ret,AzStatus.AUX_MOTOR2_OFF.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,4),ret,AzStatus.GEAR1_OIL.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,5),ret,AzStatus.GEAR2_OIL.value,azStatusBits);
			
			// byte 7
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,0),ret,AzStatus.COMPUTER_DISABLED.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,1),ret,AzStatus.AXIS_DISABLED.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,2),ret,AzStatus.HANDLED_CONTROL_UIT_OP.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,3),ret,AzStatus.AXIS_IN_STOP.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,4),ret,AzStatus.FLIP_FLOP_INCORRECT_POS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,5),ret,AzStatus.HAND_CRANCK.value,azStatusBits);
			for (int t=0; t<AzStatus.values().length; t++) {
				azStatusVals[t]=AzStatus.values()[t].value.toString();
			}
			return ret;
		} else {
			updateAzError();
			return ValueState.ERROR;
		}
	}
	
	/**
	 * Refresh the power status
	 * 
	 * @param mnt The mount
	 */
	private ValueState updatePowerStatus(MountVertex mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.NORMAL;
		}
		ValueHolder<int[]> powerStatusBits = mnt.getPowerStatus();
		if (powerStatusBits!=null && powerStatusBits.getValue()!=null) {
			Long[] longBits = new Long[powerStatusBits.getValue().length];
			if (longBits.length!=2) {
				updatePowerError();
				return ValueState.ERROR;
			}
			for (int t=0; t<longBits.length; t++) {
				longBits[t]=(long)powerStatusBits.getValue()[t];
			}
			LongArrayBit bits = new LongArrayBit(longBits);
			ValueState ret = ValueState.NORMAL;
			// byte 0
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,PowerStatus.MAIN_SWITCH_TRANS.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(0,1),ret,PowerStatus.MAIN_SWITCH_ANT_BASE.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(0,2),ret,PowerStatus.MAIN_CIRCUIT_BRK.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,PowerStatus.LIGHTNING_ARREST.value,powerStatusBits);
			
			// byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,PowerStatus.SINGLE_PHASE_ILOCK.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,PowerStatus.REVERSE_PHASE.value,powerStatusBits);
			ret=ValueDisplayer.formatBit(bits.getBit(1,2),ret,PowerStatus.CIRCUIT_BRK_ELECTRONIC.value,powerStatusBits);
			ret=ValueDisplayer.formatBit(bits.getBit(1,3),ret,PowerStatus.CIRCUIT_BRK_CRYO.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,PowerStatus.CIRCUIT_BRK_NON_CRITICAL.value,powerStatusBits);
			for (int t=0; t<PowerStatus.values().length; t++) {
				powerStatusVals[t]=PowerStatus.values()[t].value.toString();
			}
			return ret;
		} else {
			updatePowerError();
			return ValueState.ERROR;
		}
	}
	
	/**
	 * Refresh the values of the status of the azimuth
	 * 
	 * @param mnt The mount
	 */
	private ValueState updateElStatus(MountVertex mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.ERROR;
		}
		ValueHolder<int[]> elStatusBits = mnt.getElStatus();
		if (elStatusBits!=null && elStatusBits.getValue()!=null) {
			Long[] longBits = new Long[elStatusBits.getValue().length];
			if (longBits.length!=8) {
				updateElError();
				return ValueState.NORMAL;
			}
			for (int t=0; t<longBits.length; t++) {
				longBits[t]=(long)elStatusBits.getValue()[t];
			}
			LongArrayBit bits = new LongArrayBit(longBits);
			ValueState ret = ValueState.NORMAL;
			// byte 0
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,ElStatus.PRELIMIT_UP.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,ElStatus.PRELIMIT_DOWN.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,ElStatus.LIMIT_UP.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,ElStatus.LIMIT_DOWN.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,ElStatus.EMERGENCY_LIMIT_UP.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,ElStatus.EMERGENCY_LIMIT_DOWN.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,ElStatus.EMERGENCY_LIMIT2_CW.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,ElStatus.EMERGENCY_LIMIT2_CCW.value,elStatusBits);
			
			
			// byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,ElStatus.SERVO_FAILURE.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,ElStatus.OVERSPEED.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,2),ret,ElStatus.IMMOBILE.value,elStatusBits);
			ret=ValueDisplayer.formatBit(bits.getBit(1,3),ret,ElStatus.SPEED_ZERO.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,4),ret,ElStatus.STOW_POSITION.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,5),ret,ElStatus.ENCODER_FAILURE.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,6),ret,ElStatus.INSANE_VELOCITY.value,elStatusBits);
			
			// byte 2 
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,ElStatus.BRAKE1.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,ElStatus.BRAKE2.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,2),ret,ElStatus.BRAKE3.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,3),ret,ElStatus.BRAKE4.value,elStatusBits);
			
			// byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,ElStatus.AMPLIFIER1.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,ElStatus.AMPLIFIER2.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,2),ret,ElStatus.AMPLIFIER3.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,3),ret,ElStatus.AMPLIFIER4.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,4),ret,ElStatus.CAN_COMMUNICATION.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,5),ret,ElStatus.BREAKER.value,elStatusBits);
			
			// byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,ElStatus.MOTOR1_OVERTEMPERATURE.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,1),ret,ElStatus.MOTOR2_OVERTEMPERATURE.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,2),ret,ElStatus.MOTOR3_OVERTEMPERATURE.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,3),ret,ElStatus.MOTOR4_OVERTEMPERATURE.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,4),ret,ElStatus.REGENERATION_RESISTORE.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,5),ret,ElStatus.SERVO_OSCILLATION.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,6),ret,ElStatus.AUXILIARY_ENC.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,7),ret,ElStatus.POS_DEVIATION.value,elStatusBits);
			
			// byte 5
			// byte 6
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,0),ret,ElStatus.AUX_MOTOR1_2.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,2),ret,ElStatus.AUX_MOTOR3_4.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,4),ret,ElStatus.GEAR1_OIL.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,5),ret,ElStatus.GEAR2_OIL.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,6),ret,ElStatus.GEAR3_OIL.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,7),ret,ElStatus.GEAR4_OIL.value,elStatusBits);
			
			// byte 7
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,0),ret,ElStatus.COMPUTER_DISABLED.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,1),ret,ElStatus.AXIS_DISABLED.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,2),ret,ElStatus.HANDLED_CONTROL_UIT_OP.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,3),ret,ElStatus.AXIS_IN_STOP.value,elStatusBits);
			for (int t=0; t<ElStatus.values().length; t++) {
				elStatusVals[t]=ElStatus.values()[t].value.toString();
			}
			return ret;
		} else {
			updateElError();
			return ValueState.ERROR;
		}
	}
	
	/**
	 * @param row The number of the status row
	 * @return The tile of the status row
	 */
	public String getStatusRowTitle(int row) {
		if (row<0 || row>=VAStatus.values().length) {
			throw new IllegalArgumentException("Row "+row+" is out of range [0,"+VAStatus.values().length+"]");
		}
		return VAStatus.values()[row].title;
	}
	
	/**
	 * @param row The number of the power status row
	 * @return The tile of the status row
	 */
	public String getPowerStatusRowTitle(int row) {
		if (row<0 || row>=PowerStatus.values().length) {
			throw new IllegalArgumentException("Row "+row+" is out of range [0,"+PowerStatus.values().length+"]");
		}
		return PowerStatus.values()[row].title;
	}
	
	/**
	 * @param row The number of the EL status row
	 * @return The tile of the EL status row
	 */
	public String getElRowTitle(int row) {
		if (row<0 || row>=ElStatus.values().length) {
			throw new IllegalArgumentException("Row "+row+" is out of range [0,"+ElStatus.values().length+"]");
		}
		return ElStatus.values()[row].title;
	}
	
	/**
	 * @param row The number of the AZ status row
	 * @return The tile of the AZ status row
	 */
	public String getAzRowTitle(int row) {
		if (row<0 || row>=AzStatus.values().length) {
			throw new IllegalArgumentException("Row "+row+" is out of range [0,"+AzStatus.values().length+"]");
		}
		return AzStatus.values()[row].title;
	}
}
