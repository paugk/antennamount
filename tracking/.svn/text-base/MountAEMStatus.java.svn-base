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

package alma.control.gui.antennamount.tracking;

import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.mount.aem.MountAEM;
import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.ValueState;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;
//import java.io.PrintStream;

/**
 * 
 * The class to show details of the detailed status of the AEM production
 *
 */
public class MountAEMStatus extends MountDetails implements MountInterface {
	
	/**
	 * The titles of the rows of the status of the AEM antenna.
	 * @see GET_SYSTEM_STATUS IDL method
	 * 
	 * The order of declaration in this enum reflect the order in the table
	 * 
	 * @author acaproni
	 *
	 */
	private enum SysStatus {
		// Byte 0
		SAFE_SWITCH("Emergency stop"),
		ACU_INTERLOCK("ACU interlock"),
		BASE_DOOR_INTERLOCK("Base door interlock"),
		BASE1_INTERLOCK("Base 1 interlock"),
		BASE2_INTERLOCK("Base 2 interlock"),
		AZ_SKIRT1_INTERLOCK("AZ skirt 1 interlock"),
		AZ_SKIRT2_INTERLOCK("AZ skirt 2 interlock"),
		STAIR1_INTERLOCK("Access stair 1 interlock"),
		
		// Byte 1
		EL_LEFT_INTERLOCK("EL left interlock"),
		EL_RIGHT_INTERLOCK("EL right interlock"),
		PCU1_RC_INTERLOCK("PCU 1 RC interlock"),
		PCU2_D1_INTERLOCK("PCU 2 D1 interlock"),
		PCU3_BASE_INTERLOCK("PCU 3 BASE interlock"),
		PCU4_PLC_INTERLOCK("PCU 4 PLC interlock"),
		RECV_CABIN_INTERLOCK("Receiver cabin interlock"),
		RECV_CABIN_DOOR("Receiver cabin access door interlock"),
		
		// Byte 2
		HANDRAIL_PLATFORM_ILOCK("Handrail RC platform interlock"),
		ACU_BOOT_FAILURE("ACU boot failure"),
		SURVIVAL_STOW_MISSING_CMD("Survival stow due to missing commands after idle time"),
		SURVIVAL_STOW_MISSING_PULSE("Survival stow due to missing timing pulse after idle time"),
		PULSE_MISSING("Timing pulse missing"),
		ACU_TASK_FAILURE("ACU task failure"),
		PULSE_MISSED("Timing pulse missed"),
		
		// Byte 3
		HYDRAULIC_UNIT_ALARM("Hydraulic unit generic alarm"),
		FIRE_SYSTEM("Fire system status"),
		OVERTEMPERATURE_ALARM("Over temperature alarm"),
		VENT_SKIRT_STATUS("Ventilation skirt status"),
		
		// Byte 4: stow status  
		LOCAL_MODE("Antenna local mode"),
		REMOTE_MODE("Antenna remote mode"),
		PCU_MODE("Antenna PCU mode"),
		PCU1_CONN("PCU platform connected"),
		PCU2_CONN("PCU receiver cabin connected"),
		PCU3_CONN("PCU basement connected"),
		PCU4_CONN("PCU PLC connected"),

		// Byte 5: shutter status 
		DRIVES_PWR("Drives power"),
		DRIVES("Drives"),
		HYDRAULIC_PUMP("Hydraulic pump"),
		SKIRT_VENTILATION("Skirt ventilation"),
		SKIRT_VENTILATION_CMD("Skirt ventilation command"),
		
		// Byte 7: 
		CLOSING_MOTOR_SHUTTER("Shutter closing ON"),
		OPENING_MOTOR_SHUTTER("Shutter opening ON"),
		LOCK_ON("Shtter lock on"),
		TIMEOUT("Shutter timeout"),
		BASE_LADDER_INTERLOCK("Base ladder interlock"),
		DRIVES_LOCKOUT_STATUS("Drives lockout status"),
		MISSING_DUMMY_SOCKET("Missing dummy socket / Utility module emergency stop");
		
 
		public final String title;
		
		public final StringBuilder value = new StringBuilder(); 
		
		private SysStatus(String title) {
			this.title=title;
		}
	}

	/**
	 *  The titles of the rows of the status of the AEM elevation.
	 *  
	 *  The order of declaration in this enum reflects the order in the table
	 * 
	 * @author acaproni
	 *
	 */
	private enum ElStatus {
 
		// byte 0 (ubyte): limit switches 
		SW_UP("EL: SW UP prelimit"), 
		HW_UP("EL: HW UP prelimit"), 
		HW_UP_FINAL("EL: HW UP final limit"), 
		UP_SHUTDOWN("EL: UP shutdown due to limit"), 
		SW_DOWN("EL: SW DOWN prelimit"), 
		HW_DOWN("EL: HW DOWN prelimit"), 
		HW_DOWN_FINAL("EL: HW DOWN final limit"), 
		DOWN_SHUTDOWN("EL: DOWN shutdown due to limit"),
 
		// byte 1 (ubyte): Interlocks 
		ROT_FINAL_LIMIT("EL: Rotation final limit"), 
		AXIS_HW_INTERLOCK("EL: Axis hardware interlock"),
		OVERRIDE_CDM("EL: Override command"),
		
		// byte 2 (ubyte): Motors 
		MOTOR_OVERSPEED("EL: Motor overspeed"),
		MOTOR_1HALF_OVRCURRENT("EL: Motor 1st half over current"),
		MOTOR_1HALF_OVRHEATING("EL: Motor 1st half over heating"),
		MOTOR_2HALF_OVRCURRENT("EL: Motor 2nd half over current"),
		MOTOR_2HALF_OVRHEATING("EL: Motor 2nd half over heating"),
		DRIVE_PWR_ON("EL: Drive power on"),
		DC_BUS1("EL: DC bus 1"),
		DC_BUS2("EL: DC bus 2"),
 
		// byte 3 (ubyte): Motors 
		MOTORS_PWR_ON_SWITCH_FAIL("EL: Motors power-on / switch failure"), 
		MOTORS_ENABLE_TMOUT("EL: Motors enable timeout"),
		MOTORS_1HALF_FAULT("EL: Motors 1st half fault"),
		MOTORS_2HALF_FAULT("EL: Motors 2nd half fault"),
		MOTORS_DRIVER_RDY("EL: Motor drivers ready"),
		SENSORS_INCONSISTENCY("EL: Encoder/Hall sensors inconsistency"),
		FOLLOWING("EL: Following error"),
		
		// byte 5 (ubyte): encoder
		ENC_VALUE_FAULT("EL: Encoder value fault"),
		ABSENC_ABS_POS_NA("EL: Absolute encoder position not avilable"),
		ENC_VAL_VALIDATION("EL: Encoder value validation"),
		SERVO_OSC("EL: Servo oscillation"),
		INTERPOL_BOARD1("EL: Interpolation board #1 status"),
 
		// byte 6 (ubyte): encoder
		ENC_HEAD1_STATUS("EL: Encoder head #1 status"), 
		ENC_HEAD2_STATUS("EL: Encoder head #2 status"),
		ENC_HEAD3_STATUS("EL: Encoder head #3 status"),
		ENC_HEAD4_STATUS("EL: Encoder head #4 status"),
		
		// Byte 7
		BRAKE_POS_ERR("EL: Brake position error"),
		BRAKE_WEAR("EL: Brake wear"),
		BRAKE_LOCAL_MODE("EL: Brake local mode"),
		BRAKE_OUT("EL: Brake out"),
		BRAKE_DISENGAGE_TO("EL: Brake disengage timeout"),
		BRAKE_ENGAGE_TO("EL: Brake engage timeout");

		public final String title;
		
		public final StringBuilder value = new StringBuilder(); 
		
		private ElStatus(String title) {
			this.title=title;
		}
	}

	
	/**
	 *  The titles of the rows of the status of the AEM Azimuth.
	 *  
	 *  The order of declaration in this enum reflect the order in the table
	 *  
	 *  @author acaproni
	 */
	private enum AzStatus {

		// byte 0 (ubyte): limit switches 
		SW_CW("AZ: SW CW prelimit"), 
		HW_CW("AZ: HW CW prelimit"), 
		HW_CW_FINAL("AZ: HW CW final limit"), 
		CW_SHUTDOWN("AZ: CW shutdown due to limit"), 
		SW_CCW("AZ: SW CCW prelimit"), 
		HW_CCW("AZ: HW CCW prelimit"), 
		HW_CCW_FINAL("AZ: HW CCW final limit"), 
		CCW_SHUTDOWN("AZ: CCW shutdown due to limit"),
 
		// byte 1 (ubyte): Interlocks 
		ROT_FINAL_LIMIT("AZ: Rotation final limit"), 
		AXIS_HW_INTERLOCK("AZ: Axis hardware interlock"),
		OVERRIDE_CDM("AZ: Override command"),
		HARDSTOP_PLUS("AZ: Hard stop sense +"),
		HARDSTOP_MINUS("AZ: Hard stop sense -"),
		SENSE_PLUS("AZ: Sense +"),
		SENSE_MINUS("AZ: Sense -"),
		
		// byte 2 (ubyte): Motors 
		MOTOR_OVERSPEED("AZ: Motor over speed"),
		MOTOR_1HALF_OVRCURRENT("AZ: Motor 1st half over current"),
		MOTOR_1HALF_OVRHEATING("AZ: Motor 1st half over heating"),
		MOTOR_2HALF_OVRCURRENT("AZ: Motor 2nd half over current"),
		MOTOR_2HALF_OVRHEATING("AZ: Motor 2nd half over heating"),
		DRIVE_PWR_ON("AZ: Drive power on"),
		DC_BUS1("AZ: DC bus 1"),
		DC_BUS2("AZ: DC bus 2"),
 
		// byte 3 (ubyte): Motors 
		MOTORS_PWR_ON_SWITCH_FAIL("AZ: Motors power-on / switch failure"), 
		MOTORS_ENABLE_TMOUT("AZ: Motors enable timeout"),
		MOTORS_1HALF_FAULT("AZ: Motors 1st half fault"),
		MOTORS_2HALF_FAULT("AZ: Motors 2nd half fault"),
		MOTORS_DRIVER_RDY("AZ: Motor drivers ready"),
		SENSORS_INCONSISTENCY("AZ: Encoder/Hall sensors inconsistency"),
		FOLLOWING_ERROR("AZ: Following error"),
 
		// byte 4 (ubyte): encoder
		ENC_VALUE_FAULT("AZ: Encoder value fault"),
		ABSENC_ABS_POS_NA("AZ: Absolute encoder position not avilable"),
		ENC_VAL_VALIDATION("AZ: Encoder value validation"),
		SERVO_OSC("AZ: Servo oscillation"),
		INTERPOL_BOARD1("AZ: Interpolation board #1"),
		INTERPOL_BOARD2("AZ: Interpolation board #2"),
 
		// byte 5 (ubyte): encoder
		ENC_HEAD1_STATUS("AZ: Encoder head #1 status"), 
		ENC_HEAD2_STATUS("AZ: Encoder head #2 status"),
		ENC_HEAD3_STATUS("AZ: Encoder head #3 status"),
		ENC_HEAD4_STATUS("AZ: Encoder head #4 status"),
		ENC_HEAD5_STATUS("AZ: Encoder head #5 status"),
		ENC_HEAD6_STATUS("AZ: Encoder head #6 status"),
		ENC_HEAD7_STATUS("AZ: Encoder head #7 status"),
		ENC_HEAD8_STATUS("AZ: Encoder head #8 status"),
		
		// Byte 6: brakes
		BRAKE_POS_ERR("AZ: Brake position error"),
		BRAKE_WEAR("AZ: Brake wear"),
		BRAKE_LOCAL_MODE("AZ: Brake local mode"),
		BRAKE_OUT("AZ: Brake out"),
		BRAKE_DISENGAGE_TO("AZ: Brake disengage timeout"),
		BRAKE_ENGAGE_TO("AZ: Brake engage timeout");
 
		public final String title;
		
		public final StringBuilder value = new StringBuilder(); 
		
		private AzStatus(String title) {
			this.title=title;
		}
	}
	
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
		PWR_SOURCE("Antenna power source (ALMA/transporter)"),
		UPS_LINE_FAIL("UPS line failure"),
		UPS_LOW_BAT("UPS low battery"),
		UPS_ALARM("UPS alarm"),
		UPS_LOAD_BYPASS("UPS load on bypass"),
		UPS_LOAD_INVERTER("UPS load on inverter"),
		
		// Byte 1
		AUX_READY("24VDC aux ready"),
		INTERFACE("24VDC interface"),
		AZIMUTH("24VDC azimuth"),
		ELEVATION("24VDC elevation"),
		INTERLOCKS("24VDC interlocks");
		
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
	public MountAEMStatus() {

		ACU_ERROR_DESC = new String[] {
				"OK", //0x00
				"TIMEOUT", //0x01
				"INVALID MODE CHANGE REQUEST", //0x02
				"REQUESTED POSITION OUT OF RANGE", //0x03
				"REQUESTED VELOCITY OUT OF RANGE", //0x04
				"ACU IN LOCAL ACCESS MODE", //0x05
				"INVALID BRAKE COMMAND", //0x06
				unknownAcuError, // 0x07
				unknownAcuError, // 0x08
				unknownAcuError, // 0x09
				unknownAcuError, // 0x0A
				unknownAcuError, // 0x0B
				unknownAcuError, // 0x0C
				unknownAcuError, // 0x0D
				unknownAcuError, // 0x0E
				unknownAcuError, // 0x0F
				"ILLEGAL COMMAND/MONITOR POINT", //0x10
				"UNEXPECTED COMMAND/MONITOR POINT", //0x11
				"PARAMETER OUT OF RANGE", //0x12
				"INVALID DATA LENGTH", //0x13
				"TRAJECTORY COMMAND DELAYED", // 0x14
				"TRAJECTORY COMMAND DUPLICATE", //0x15
				"ERROR STACK OVERFLOW", //0x16
				"INVALID OPERATION REQUESTED" // 0x17
		};

		statusVals = new String[SysStatus.values().length];
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
	private ValueState updateSystemStatus(MountAEM mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.NORMAL;
		}
		ValueHolder<int[]> statusBits = mnt.getStatus();
		if (statusBits!=null && statusBits.getValue()!=null) {
			Long[] longBits = new Long[statusBits.getValue().length];
			if (longBits.length!=7) {
				System.err.printf("updateSystemStatus longBits.length= %d\n", longBits.length);
				updateStatusError();
				return ValueState.ERROR;
			}
			for (int t=0; t<longBits.length; t++) {
				longBits[t]=(long)statusBits.getValue()[t];
			}
			LongArrayBit bits = new LongArrayBit(longBits);
			ValueState ret = ValueState.NORMAL;
			// Byte 0
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,SysStatus.SAFE_SWITCH.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,SysStatus.ACU_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,SysStatus.BASE_DOOR_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,SysStatus.BASE1_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,SysStatus.BASE2_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,SysStatus.AZ_SKIRT1_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,SysStatus.AZ_SKIRT2_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,SysStatus.STAIR1_INTERLOCK.value,statusBits);
			
			// Byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,SysStatus.EL_LEFT_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,SysStatus.EL_RIGHT_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,2),ret,SysStatus.PCU1_RC_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,3),ret,SysStatus.PCU2_D1_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,4),ret,SysStatus.PCU3_BASE_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,5),ret,SysStatus.PCU4_PLC_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,6),ret,SysStatus.RECV_CABIN_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,7),ret,SysStatus.RECV_CABIN_DOOR.value,statusBits);
			
			// Byte 2
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,SysStatus.HANDRAIL_PLATFORM_ILOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,SysStatus.ACU_BOOT_FAILURE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,2),ret,SysStatus.SURVIVAL_STOW_MISSING_CMD.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,3),ret,SysStatus.SURVIVAL_STOW_MISSING_PULSE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,4),ret,SysStatus.PULSE_MISSING.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,5),ret,SysStatus.ACU_TASK_FAILURE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,6),ret,SysStatus.PULSE_MISSED.value,statusBits);
			
			// Byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,SysStatus.HYDRAULIC_UNIT_ALARM.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,SysStatus.FIRE_SYSTEM.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,2),ret,SysStatus.OVERTEMPERATURE_ALARM.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,3),ret,SysStatus.VENT_SKIRT_STATUS.value,statusBits);
			
			// Byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,SysStatus.LOCAL_MODE.value,statusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(4,1),ret,SysStatus.REMOTE_MODE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,2),ret,SysStatus.PCU_MODE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,3),ret,SysStatus.PCU1_CONN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,4),ret,SysStatus.PCU2_CONN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,5),ret,SysStatus.PCU3_CONN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,6),ret,SysStatus.PCU4_CONN.value,statusBits);
			
			// Byte 5
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,0),ret,SysStatus.DRIVES_PWR.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,1),ret,SysStatus.DRIVES.value,statusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(5,2),ret,SysStatus.HYDRAULIC_PUMP.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,3),ret,SysStatus.SKIRT_VENTILATION.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,4),ret,SysStatus.SKIRT_VENTILATION_CMD.value,statusBits);
			
			// Byte 6
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,0),ret,SysStatus.CLOSING_MOTOR_SHUTTER.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,1),ret,SysStatus.OPENING_MOTOR_SHUTTER.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,2),ret,SysStatus.LOCK_ON.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,3),ret,SysStatus.TIMEOUT.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,4),ret,SysStatus.BASE_LADDER_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,5),ret,SysStatus.DRIVES_LOCKOUT_STATUS.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,6),ret,SysStatus.MISSING_DUMMY_SOCKET.value,statusBits);
			

			for (int t=SysStatus.SAFE_SWITCH.ordinal(); t<=SysStatus.MISSING_DUMMY_SOCKET.ordinal(); t++) {
				statusVals[t]=SysStatus.values()[t].value.toString();
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
		if (!(mnt instanceof MountAEM)) {
			throw new IllegalArgumentException("The mount is not MountAEM");
		}
		ValueState st=updateSystemStatus((MountAEM)mnt);
		st=ValueState.max(st,updateAzStatus((MountAEM)mnt));
		st=ValueState.max(st,updateElStatus((MountAEM)mnt));
		st=ValueState.max(st,updatePowerStatus((MountAEM)mnt));
		return st;
	}
	
	/**
	 * Refresh the values of the status of the azimuth
	 * 
	 * @param mnt The mount
	 */
	private ValueState updateAzStatus(MountAEM mnt) {
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
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,AzStatus.SW_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,AzStatus.HW_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,AzStatus.HW_CW_FINAL.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,AzStatus.CW_SHUTDOWN.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,AzStatus.SW_CCW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,AzStatus.HW_CCW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,AzStatus.HW_CCW_FINAL.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,AzStatus.CCW_SHUTDOWN.value,azStatusBits);
			
			// byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,AzStatus.ROT_FINAL_LIMIT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,AzStatus.AXIS_HW_INTERLOCK.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,2),ret,AzStatus.OVERRIDE_CDM.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,3),ret,AzStatus.HARDSTOP_PLUS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,4),ret,AzStatus.HARDSTOP_MINUS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,5),ret,AzStatus.SENSE_PLUS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,6),ret,AzStatus.SENSE_MINUS.value,azStatusBits);
			
			// byte 2 
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,AzStatus.MOTOR_OVERSPEED.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,AzStatus.MOTOR_1HALF_OVRCURRENT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,2),ret,AzStatus.MOTOR_1HALF_OVRHEATING.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,3),ret,AzStatus.MOTOR_2HALF_OVRCURRENT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,4),ret,AzStatus.MOTOR_2HALF_OVRHEATING.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(2,5),ret,AzStatus.DRIVE_PWR_ON.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(2,6),ret,AzStatus.DC_BUS1.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(2,7),ret,AzStatus.DC_BUS2.value,azStatusBits);
			
			// byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,AzStatus.MOTORS_PWR_ON_SWITCH_FAIL.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,AzStatus.MOTORS_ENABLE_TMOUT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,2),ret,AzStatus.MOTORS_1HALF_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,3),ret,AzStatus.MOTORS_2HALF_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(3,4),ret,AzStatus.MOTORS_DRIVER_RDY.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,5),ret,AzStatus.SENSORS_INCONSISTENCY.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,6),ret,AzStatus.FOLLOWING_ERROR.value,azStatusBits);
			
			// byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,AzStatus.ENC_VALUE_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,1),ret,AzStatus.ABSENC_ABS_POS_NA.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,2),ret,AzStatus.ENC_VAL_VALIDATION.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,3),ret,AzStatus.SERVO_OSC.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(4,4),ret,AzStatus.INTERPOL_BOARD1.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(4,5),ret,AzStatus.INTERPOL_BOARD2.value,azStatusBits);
			
			// byte 5
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,0),ret,AzStatus.ENC_HEAD1_STATUS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,1),ret,AzStatus.ENC_HEAD2_STATUS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,2),ret,AzStatus.ENC_HEAD3_STATUS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,3),ret,AzStatus.ENC_HEAD4_STATUS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,4),ret,AzStatus.ENC_HEAD5_STATUS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,5),ret,AzStatus.ENC_HEAD6_STATUS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,6),ret,AzStatus.ENC_HEAD7_STATUS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,7),ret,AzStatus.ENC_HEAD8_STATUS.value,azStatusBits);
			
			// byte 6
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,0),ret,AzStatus.BRAKE_POS_ERR.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,1),ret,AzStatus.BRAKE_WEAR.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,2),ret,AzStatus.BRAKE_LOCAL_MODE.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(6,3),ret,AzStatus.BRAKE_OUT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,4),ret,AzStatus.BRAKE_DISENGAGE_TO.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,5),ret,AzStatus.BRAKE_ENGAGE_TO.value,azStatusBits);
			
			
			for (int t=AzStatus.SW_CW.ordinal(); t<=AzStatus.BRAKE_ENGAGE_TO.ordinal(); t++) {
				azStatusVals[t]=AzStatus.values()[t].value.toString();
			}
			return ret;
		} else {
			updateAzError();
			return ValueState.ERROR;
		}
	}
	
	/**
	 * Refresh the values of the status of the elevation
	 * 
	 * @param mnt The mount
	 */
	private ValueState updateElStatus(MountAEM mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.NORMAL;
		}
		ValueHolder<int[]> elStatusBits = mnt.getElStatus();
		if (elStatusBits!=null && elStatusBits.getValue()!=null) {
			Long[] longBits = new Long[elStatusBits.getValue().length];
			if (longBits.length!=8) {
				updateElError();
				return ValueState.ERROR;
			}
			for (int t=0; t<longBits.length; t++) {
				longBits[t]=(long)elStatusBits.getValue()[t];
			}
			LongArrayBit bits = new LongArrayBit(longBits);
			ValueState ret = ValueState.NORMAL;
			// byte 0
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,ElStatus.SW_UP.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,ElStatus.HW_UP.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,ElStatus.HW_UP_FINAL.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,ElStatus.UP_SHUTDOWN.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,ElStatus.SW_DOWN.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,ElStatus.HW_DOWN.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,ElStatus.HW_DOWN_FINAL.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,ElStatus.DOWN_SHUTDOWN.value,elStatusBits);
			
			
			// byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,ElStatus.ROT_FINAL_LIMIT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,ElStatus.AXIS_HW_INTERLOCK.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,ElStatus.OVERRIDE_CDM.value,elStatusBits);
			
			// byte 2 
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,ElStatus.MOTOR_OVERSPEED.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,ElStatus.MOTOR_1HALF_OVRCURRENT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,2),ret,ElStatus.MOTOR_1HALF_OVRHEATING.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,3),ret,ElStatus.MOTOR_2HALF_OVRCURRENT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,4),ret,ElStatus.MOTOR_2HALF_OVRHEATING.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(2,5),ret,ElStatus.DRIVE_PWR_ON.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(2,6),ret,ElStatus.DC_BUS1.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(2,7),ret,ElStatus.DC_BUS2.value,elStatusBits);
			
			// byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,ElStatus.MOTORS_PWR_ON_SWITCH_FAIL.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,ElStatus.MOTORS_ENABLE_TMOUT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,2),ret,ElStatus.MOTORS_1HALF_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,3),ret,ElStatus.MOTORS_2HALF_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(3,4),ret,ElStatus.MOTORS_DRIVER_RDY.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,5),ret,ElStatus.SENSORS_INCONSISTENCY.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,6),ret,ElStatus.FOLLOWING.value,elStatusBits);
			
			// byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,ElStatus.ENC_VALUE_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,1),ret,ElStatus.ABSENC_ABS_POS_NA.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,2),ret,ElStatus.ENC_VAL_VALIDATION.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,3),ret,ElStatus.SERVO_OSC.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(4,4),ret,ElStatus.INTERPOL_BOARD1.value,elStatusBits);

			// byte 5
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,0),ret,ElStatus.ENC_HEAD1_STATUS.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,1),ret,ElStatus.ENC_HEAD2_STATUS.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,2),ret,ElStatus.ENC_HEAD3_STATUS.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,3),ret,ElStatus.ENC_HEAD4_STATUS.value,elStatusBits);
			
			// byte 6
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,0),ret,ElStatus.BRAKE_POS_ERR.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,1),ret,ElStatus.BRAKE_WEAR.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,2),ret,ElStatus.BRAKE_LOCAL_MODE.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(6,3),ret,ElStatus.BRAKE_OUT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,4),ret,ElStatus.BRAKE_DISENGAGE_TO.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,5),ret,ElStatus.BRAKE_ENGAGE_TO.value,elStatusBits);

			for (int t=ElStatus.SW_UP.ordinal(); t<=ElStatus.BRAKE_ENGAGE_TO.ordinal(); t++) {
				elStatusVals[t]=ElStatus.values()[t].value.toString();
			}
			return ret;
		} else {
			updateElError();
			return ValueState.ERROR;
		}
	}
	
	/**
	 * Refresh the power status
	 * 
	 * @param mnt The mount
	 */
	private ValueState updatePowerStatus(MountAEM mnt) {
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
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,PowerStatus.PWR_SOURCE.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,PowerStatus.UPS_LINE_FAIL.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,PowerStatus.UPS_LOW_BAT.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,PowerStatus.UPS_ALARM.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,PowerStatus.UPS_LOAD_BYPASS.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,PowerStatus.UPS_LOAD_INVERTER.value,powerStatusBits);
			
			// byte 1
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(1,0),ret,PowerStatus.AUX_READY.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(1,1),ret,PowerStatus.INTERFACE.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(1,2),ret,PowerStatus.AZIMUTH.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(1,3),ret,PowerStatus.ELEVATION.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(1,4),ret,PowerStatus.INTERLOCKS.value,powerStatusBits);
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
	 * @param row The number of the status row
	 * @return The tile of the status row
	 */
	public String getStatusRowTitle(int row) {
		if (row<0 || row>=SysStatus.values().length) {
			throw new IllegalArgumentException("Row "+row+" is out of range [0,"+SysStatus.values().length+"]");
		}
		return SysStatus.values()[row].title;
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
