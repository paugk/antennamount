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
import alma.control.gui.antennamount.mount.a7m.MountA7M;
import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.ValueState;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;
//import java.io.PrintStream;

/**
 * 
 * The class to show details of the detailed status of the A7M production
 *
 */
public class MountA7MStatus extends MountDetails implements MountInterface {
	
	/**
	 * The titles of the rows of the status of the Melco 7m antenna.
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
		STAIRWAY_INTERLOCK("Stairway interlock"),
		HANDLING_INTERLOCK("Handling interlock"),
		SMOKE_ALARM("Smoke alarm"),
		ACU_FAULT("ACU fault displayed in GET_SYSTEM_STATUS_2"),
		SURVIVAL_FOR_MISSING_COMMANDS("Survival stow due to missing commands after idle_time"),
		SURVIVAL_FOR_MISSING_TIMING("Survival stow due to missing timing pulse after idle_time"),
		TIMING_PULSE("Timing pulse missing"),
		
		// Byte 1: safety interlock
		RCV_CABIN_SAFETY("Receiver cabin safety"),
		ANT_BASE_SAFETY("Antenna base safety"),
		VERANDA_EQUIP_FREE("Veranda equipment safety"),
		LADDER_SWITCH("Ladder switch"),
		TRANSP_INTLK("Transporter interlock"),
		TRANSP_INTLK_EXCPT_AZ("Transporter interlock except AZ axis"),
			// bit6-7: reserved 

		// Byte 2: door interlock 
		RCV_CABIN_DOOR_OPEN("Receiver cabin door open"),
		CNTR_CABIN_DOOR_OPEN("Control cabin door open"),
		ANT_BASE_DOOR_OPEN("Antenna base door open"),
                        // bit3-5: reserved
                //20090826MT These three bits have been removed for the 7m antenna. 
		//UPS_ROOM_DOOR_OPEN("UPS room door open"),
		//PDB_ROOM_DOOR_OPEN("PDB room door open"),
		//MAIN_REF_HATCH_OPEN("Main reflector hatch open"),
		YOKE_L_HATCH_OPEN("Yoke-L hatch open"),
		YOKE_R_HATCH_OPEN("Yoke-R hatch open"),
		
		// Byte 3: handle interlock  
		AZ_HNDL_INTLK("AZ handle"),
		EL_HNDL_INTLK("EL handle"),
		AZ_STOW_HNDL_INTLK("AZ stow handle"),
		EL_STOW_HNDL_INTLK("EL stow handle"),
		SHUTTER_HNDL_INTLK("Shutter handle"),
			// bit5-7: reserved 
		
		// Byte 4: stow status  
		AZ_STOW_TO("AZ stow timeout"),
		AZ_STOW_THERMAL_TRIP("AZ stow thermal trip (F)"),
		STOW_DPA_CB_OFF("Stow DPA circuit breaker off (F)"),
		STOW_DPA_COND_OFF("Stow DPA contactor off (F)"),
		EL_STOW_TO("EL stow timeout"),
		EL_STOW_THERMAL_TRIP("EL stow thermal trip (F)"),
			// bit6-7: reserved 

		// Byte 5: shutter status 
		SHUTTER_TO("Shutter timeout"),
		SHUTTER_THERMAL_TRIP("Shutter thermal trip"),
		SHUTTER_DPA_CB_OFF("Shutter DPA circuit breaker off (F)"),
		SHUTTER_DPA_COND_OFF("Shutter DPA contactor off (F)"),
			// bit4-6: reserved 
		ZENITH_SHUTTER_OPEN("Zenith shutter open"),
 
		// Byte 6: misc status 
		RD_CONV_FAIL("R/D converter failure (F)"), 
		IF_PANEL_FAIL("Interface panel failure (F)"), 

                ACR2_PS_ALARM("ACR2 PS alarm(F)" ), //20090826MT for 7m antenna.              
 
		NUTATOR_RACK_PS_ALARM("Nutator rack power supply alarm"), //20090930MT (F) is deleted.
		BRAKE_AXIS_HEATER_ALARM("Brake axis heater alarm"), 
		MOTOR_PUMP_FAIL("Motor pump failure"), 
		ARRESTER_BROKEN_ALARM("Arrester broken alarm"),
		DC_POWER_CIRCUIT_ALARM("DC power circuit alarm"), 
 
		// Byte 7: misc status 
		ACU_MAINT("ACU maintenance"), 
		ANT_STATION_POWER_DISCON("Antenna station power disconnected"), 
		ANT_INTLK("Antenna interlock"), 
			// bit3-5: reserved 
		DRIVE_SYSTEM_FAIL("Drive system failure in GET_SYSTEM_STATUS_2"), 
		MORE_PCU_CONNECT("More than one PCU connected (GET_SYSTEM_STATUS_2 have additional information)"),
 		
		/*** GET_SYSTEM_STATUS_2 ***/
		
		// byte 0 (ubyte): ACU fault 
		DC12V_FAIL("DC12V failure (F)"), 
		SYS_FAIL("SYSFAIL (VME-bus) signal asserted"), 
		AC_FAIL("ACFAIL (VME-bus) signal asserted"), 
		DATA_FAULT("Data fault (missing parameter file or file corrupted)"), 
		AD_CONV_FAIL("A/D converter failure (F)"), 
			// bit5: reserved 
		CAN_BOARD_FAIL("CAN board failure (F)"), 
		CLOCK_BOARD_FAIL("Clock board failure (F)"), 
 
		// byte 1 (ubyte): ACU fault 
		DSP_FAIL("DSP failure (F)"), 
		DSP_AD_CONV_FAIL("DSP A/D converter failure (F)"), 
			// bit2-7: reserved 
 
		// byte 2 (ubyte): ACU fault 
		IMP1_FAIL("IMP1 failure (F)"), 
		IMP2_FAIL("IMP2 failure (F)"), 
		IMP3_FAIL("IMP3 failure (F)"), 
		IMP4_FAIL("IMP4 failure (F)"), 
		IMP5_FAIL("IMP5 failure (F)"), 
		IMP6_FAIL("IMP6 failure (F)"), 
		IMP7_FAIL("IMP7 failure (F)"), 
			// bit7: reserved 
 
		// byte 3 (ubyte): PCU 
		PCU1_CONNECTED("PCU-1 connected"), 
		PCU2_CONNECTED("PCU-2 connected"), 
		PCU3_CONNECTED("PCU-3 connected"), 
			// bit3-7: reserved 
 
		// byte 4 (ubyte): drive system failure (DIFC : DPA Interface Card) 
			// bit0-5: reserved 
		DIFC_FAULT("DIFC fault (F)"), 
		DIFC_POWER_FAIL("DIFC power failure (F)"), 
 
		// byte 5 (ubyte): drive system failure 
		ACU2DIFC_LINE1_DISCON("ACU to DIFC line #1 disconnected (F)"), 
		ACU2DIFC_LINE2_DISCON("ACU to DIFC line #2 disconnected (F)"), 
		DIFC2ACU_LINE1_DISCON("DIFC to ACU line #1 disconnected (F)"), 
		DIFC2ACU_LINE2_DISCON("DIFC to ACU line #2 disconnected (F)"), 
		ACU2DIFC_LINE3_DISCON("ACU to DIFC line #3 disconnected (F)");
			// bit5-7: reserved 
 
		// bytes 6-7: reserved 
 
		public final String title;
		
		public final StringBuilder value = new StringBuilder(); 
		
		private SysStatus(String title) {
			this.title=title;
		}
	}

	/**
	 *  The titles of the rows of the status of the A7M elevation.
	 *  
	 *  The order of declaration in this enum reflects the order in the table
	 * 
	 * @author acaproni
	 *
	 */
	private enum ElStatus {
 
		// byte 0 (ubyte): limit status 
		LIMIT2_UP("EL: 2nd limit UP"), 
 		LIMIT2_DOWN("EL: 2nd limit DOWN"), 
		LIMIT1_UP("EL: 1st limit UP"), 
		LIMIT1_DOWN("EL: 1st limit DOWN"), 
		PRE_LIMIT_UP("EL: Pre-limit UP"), 
		PRE_LIMIT_DOWN("EL: Pre-limit DOWN"), 
		LIMIT_WARN_UP("EL: Limit warning UP"), 
		LIMIT_WARN_DOWN("EL: Limit warning DOWN"), 
 
		// byte 1 (ubyte): 1st limit, override 
		INVALID_DIR("EL: Invalid direction"), 
		OVERRIDE_SW_ENABLED("EL: Override switch enabled"), 
			// bit2-7: reserved 
 
		// byte 2 (ubyte): drive system failure 
		EL_L_DPA_FAULT("EL: EL-L DPA fault (F)"), 
		EL_R_DPA_FAULT("EL: EL-R DPA fault (F)"), 
			// bit2-3: reserved 
		EL_L_MOTOR_OH("EL: EL-L motor overheat (F)"), 
		EL_R_MOTOR_OH("EL: EL-R motor overheat (F)"), 
			// bit6-7: reserved 
 
		// byte 3 (ubyte): capacitor bank status 
		EL_L_CBANK_FAULT("EL: EL-L capacitor bank fault (F)"), 
		EL_R_CBANK_FAULT("EL: EL-R capacitor bank fault (F)"), 
			// bit2-3: reserved 
		EL_L_CBANK_FULL("EL: EL-L capacitor bank full"), 
		EL_R_CBANK_FULL("EL: EL-R capacitor bank full"), 
			// bit6-7: reserved 
 
		// byte 4 (ubyte): servo condition 
		EL_L_EXCSV_CURR("EL: EL-L excessive current (F)"), 
		EL_R_EXCSV_CURR("EL: EL-R excessive current (F)"), 
			// bit2-3: reserved 
		SERVO_OSCILLATION("EL: Servo oscillation (F)"), 
		RUNAWAY("EL: Runaway (F)"), 
		OVERSPEED("EL: Overspeed (F)"), 
		ANGLE_INPUT_FAULT("EL: Angle input fault (F)"), 
 
		// byte 5 (ubyte): drive system failure 
		DCPA_CB_OFF("EL: DCPA rack circuit breaker off (F)"), 
			// bit1-2: reserved 
		EL_ALL_ENCDR_ALARM("EL: EL-all encoder alarm (F)"), 
		EL_F_ENCDR_ALARM("EL: EL-F encoder alarm (F)"), 
		EL_R_ENCDR_ALARM("EL: EL-R encoder alarm (F)"), 
			// bit6-7: reserved 
 
		// byte 6 (ubyte): brake condition 
			// bit0-3: reserved 
		BRAKE_POWER_FAIL("EL: Brake power failure (F)"), 
		BRAKE_PS_FUSE_BLOW("EL: Brake power supply fuse blow (F)"), 
			// bit6-7: reserved 
 
		// byte 7 (ubyte): misc status 
		SURVIVAL_STOW_POS("EL: Survival stow position"), 
		MAINT_STOW_POS("EL: Maintenance stow position"), 
		ZENITH_POS("EL: Zenith position"), 
			// bit3-6: reserved 
		OTHER_FAULT("EL: Other EL fault (set = GET_EL_STATUS_2 have some fault bit set)"), 

		/*** GET_EL_STATUS_2 ***/

		// byte 0 (ubyte): drive system failure 
		EL_F_ENCDR_DISCON("EL: EL-F encoder disconnected (F)"), 
		EL_R_ENCDR_DISCON("EL: EL-R encoder disconnected (F)"), 
			// bit2-3: reserved 
		EL_F_ENCDR_NOT_INIT("EL: EL-F encoder not initialized (F)"), 
		EL_R_ENCDR_NOT_INIT("EL: EL-R encoder not initialized (F)"), 
			// bit6-7: reserved 
 
		// byte 1 (ubyte): drive system failure 
		EL_L_DPA_DISCON("EL: EL-L DPA disconnected (F)"), 
		EL_R_DPA_DISCON("EL: EL-R DPA disconnected (F)"), 
			// bit2-7: reserved 
 
		// byte 2 (ubyte): drive system failure 
		EL_L_DPA_COND_OFF("EL: EL-L DPA contactor off (F)"), 
		EL_R_DPA_COND_OFF("EL: EL-R DPA contactor off (F)"), 
			// bit2-3: reserved 
		EL_L_DPA_DISCHARGE("EL: EL-L DPA discharge"), 
		EL_R_DPA_DISCHARGE("EL: EL-R DPA discharge"), 
			// bit6-7: reserved 
 
		// byte 3 (ubyte): servo condition 
		POSN_FILTER_FAULT("EL: Position filter fault (F)"), 
		MAJOR_FILTER_FAULT("EL: Major filter fault (F)"), 
		MINOR_FILTER_FAULT("EL: Minor filter fault (F)"), 
		FDBK_FILTER_FAULT("EL: Feedback filter fault (F)"), 
			// bit4-7: reserved 
 
		// byte 4 (ubyte): drive on timeout 
		EL_L_DRIVE_ON_TO("EL: EL-L drive on timeout"), 
		EL_R_DRIVE_ON_TO("EL: EL-R drive on timeout"), 
			// bit2-3: reserved 
		EL_L_POWER_ON_TO("EL: EL-L power on timeout"), 
		EL_R_POWER_ON_TO("EL: EL-R power on timeout"), 
			// bit6-7: reserved 
 
		// byte 5 (ubyte): brake axis failure 
		BRAKE_AXIS_DPA_FAULT("EL: Brake axis DPA fault (F)"), 
		BRAKE_AXIS_DPA_CB_OFF("EL: Brake axis DPA circuit breaker off (F)"), 
		BRAKE_AXIS_DPA_COND_OFF("EL: Brake axis DPA contactor off (F)"), 
		BRAKE_AXIS_RSLV_DISCON("EL: Brake axis resolver disconnected (F)"), 
		BRAKE_AXIS_ASYNC("EL: Brake axis async"), 
			// bit5-6: reserved 
		BRAKE_AXIS_DRIVE_ON_TO("EL: Brake axis drive on timeout"); 
 
		// bytes 6-7: reserved 

		public final String title;
		
		public final StringBuilder value = new StringBuilder(); 
		
		private ElStatus(String title) {
			this.title=title;
		}
	}

	
	/**
	 *  The titles of the rows of the status of the A7M Azimuth.
	 *  
	 *  The order of declaration in this enum reflect the order in the table
	 *  
	 *  @author acaproni
	 */
	private enum AzStatus {

		// byte 0 (ubyte): limit status 
		LIMIT2_CW("AZ: 2nd limit CW"), 
		LIMIT2_CCW("AZ: 2nd limit CCW"), 
		LIMIT1_CW("AZ: 1st limit CW"), 
		LIMIT1_CCW("AZ: 1st limit CCW"), 
		PRE_LIMIT_CW("AZ: Pre-limit CW"), 
		PRE_LIMIT_CCW("AZ: Pre-limit CCW"), 
		LIMIT_WARN_CW("AZ: Limit warning CW"), 
		LIMIT_WARN_CCW("AZ: Limit warning CCW"), 
 
		// byte 1 (ubyte): 1st limit, override switch 
		INVALID_DIR("AZ: Invalid direction"), 
		OVERRIDE_SW_ENABLED("AZ: Override switch enabled"), 
			// bit2-7: reserved 
 
		// byte 2 (ubyte): drive system failure 
		AZ_L1_DPA_FAULT("AZ: AZ-L1 DPA fault (F)"), 
		AZ_L2_DPA_FAULT("AZ: AZ-L2 DPA fault (F)"), 
		AZ_R1_DPA_FAULT("AZ: AZ-R1 DPA fault (F)"), 
		AZ_R2_DPA_FAULT("AZ: AZ-R2 DPA fault (F)"), 
		AZ_L1_MOTOR_OH("AZ: AZ-L1 motor overheat (F)"), 
		AZ_L2_MOTOR_OH("AZ: AZ-L2 motor overheat (F)"), 
		AZ_R1_MOTOR_OH("AZ: AZ-R1 motor overheat (F)"), 
		AZ_R2_MOTOR_OH("AZ: AZ-R2 motor overheat (F)"), 
 
		// byte 3 (ubyte): capacitor bank status 
		AZ_L1_CBANK_FAULT("AZ: AZ-L1 capacitor bank fault (F)"), 
		AZ_L2_CBANK_FAULT("AZ: AZ-L2 capacitor bank fault (F)"), 
		AZ_R1_CBANK_FAULT("AZ: AZ-R1 capacitor bank fault (F)"), 
		AZ_R2_CBANK_FAULT("AZ: AZ-R2 capacitor bank fault (F)"), 
		AZ_L1_CBANK_FULL("AZ: AZ-L1 capacitor bank full"), 
		AZ_L2_CBANK_FULL("AZ: AZ-L2 capacitor bank full"), 
		AZ_R1_CBANK_FULL("AZ: AZ-R1 capacitor bank full"), 
		AZ_R2_CBANK_FULL("AZ: AZ-R2 capacitor bank full"), 
 
		// byte 4 (ubyte): servo condition 
		AZ_L1_EXCSV_CURR("AZ: AZ-L1 excessive current (F)"), 
		AZ_L2_EXCSV_CURR("AZ: AZ-L2 excessive current (F)"), 
		AZ_R1_EXCSV_CURR("AZ: AZ-R1 excessive current (F)"), 
		AZ_R2_EXCSV_CURR("AZ: AZ-R2 excessive current (F)"), 
		SERVO_OSCILLATION("AZ: Servo oscillation (F)"), 
		RUNAWAY("AZ: Runaway (F)"), 
		OVERSPEED("AZ: Over speed (F)"), 
		ANGLE_INPUT_FAULT("AZ: Angle input fault (F)"), 
 
		// byte 5 (ubyte): drive system failure 
		AZ_L_DCPA_CB_OFF("AZ: AZ-L DCPA rack circuit breaker off (F)"), 
		AZ_R_DCPA_CB_OFF("AZ: AZ-R DCPA rack circuit breaker off (F)"), 
			// bit2: reserved 
		AZ_ALL_ENCODER_ALARM("AZ: AZ-All encoder alarm (F)"), 
		AZ_LF_ENCODER_ALARM("AZ: AZ-LF encoder alarm (F)"), 
		AZ_LR_ENCODER_ALARM("AZ: AZ-LR encoder alarm (F)"), 
		AZ_RF_ENCODER_ALARM("AZ: AZ-RF encoder alarm (F)"), 
		AZ_RR_ENCODER_ALARM("AZ: AZ-RR encoder alarm (F)"), 
 
		// byte 6 (ubyte): brake condition 
			// bit0-3: reserved 
		BRAKE_POWER_FAIL("AZ: Brake power failure (F)"), 
		BRAKE_PS_FUSE_BLOW("AZ: Brake power supply fuse blow (F)"), 
			// bit6-7: reserved 
 
		// byte 7 (ubyte): misc status 
		SURVIVAL_STOW_POS("AZ: Survival stow position"), 
		MAINT_STOW_POS("AZ: Maintenance stow position"), 
			// bit2-3: reserved 
		CABLE_OVERLAP_CW("AZ: Cable overlap CW"), 
		CABLE_OVERLAP_CCW("AZ: Cable overlap CCW"), 
			// bit6: reserved 
		OTHER_FAULT("AZ: other AZ fault (set = GET_AZ_STATUS_2 have some fault bit set)"), 
		
		/*** GET_AZ_STATUS_2 ***/

		// byte 0 (ubyte): drive system failure 
		AZ_LF_ENCDR_DISCON("AZ: AZ-LF encoder disconnected (F)"), 
		AZ_LR_ENCDR_DISCON("AZ: AZ-LR encoder disconnected (F)"), 
		AZ_RF_ENCDR_DISCON("AZ: AZ-RF encoder disconnected (F)"), 
		AZ_RR_ENCDR_DISCON("AZ: AZ-RR encoder disconnected (F)"), 
		AZ_LF_ENCDR_NOT_INIT("AZ: AZ-LF encoder not initialized (F)"), 
		AZ_LR_ENCDR_NOT_INIT("AZ: AZ-LR encoder not initialized (F)"), 
		AZ_RF_ENCDR_NOT_INIT("AZ: AZ-RF encoder not initialized (F)"), 
		AZ_RR_ENCDR_NOT_INIT("AZ: AZ-RR encoder not initialized (F)"), 

		// byte 1 (ubyte): drive system failure 
		AZ_L1_DPA_DISCON("AZ: AZ-L1 DPA disconnected (F)"), 
		AZ_L2_DPA_DISCON("AZ: AZ-L2 DPA disconnected (F)"), 
		AZ_R1_DPA_DISCON("AZ: AZ-R1 DPA disconnected (F)"), 
		AZ_R2_DPA_DISCON("AZ: AZ-R2 DPA disconnected (F)"), 
			// bit4-7: reserved 
 
		// byte 2 (ubyte): drive system failure 
		AZ_L1_DPA_COND_OFF("AZ: AZ-L1 DPA contactor off (F)"), 
		AZ_L2_DPA_COND_OFF("AZ: AZ-L2 DPA contactor off (F)"), 
		AZ_R1_DPA_COND_OFF("AZ: AZ-R1 DPA contactor off (F)"), 
		AZ_R2_DPA_COND_OFF("AZ: AZ-R2 DPA contactor off (F)"), 
		AZ_L1_DPA_DISCHARGE("AZ: AZ-L1 DPA discharge (F)"), 
		AZ_L2_DPA_DISCHARGE("AZ: AZ-L2 DPA discharge (F)"), 
		AZ_R1_DPA_DISCHARGE("AZ: AZ-R1 DPA discharge (F)"), 
		AZ_R2_DPA_DISCHARGE("AZ: AZ-R2 DPA discharge (F)"), 
 
		// byte 3 (ubyte): servo condition 
		POSN_FILTER_FAULT("AZ: Position filter fault (F)"), 
		MAJOR_FILTER_FAULT("AZ: Major filter fault (F)"), 
		MINOR_FILTER_FAULT("AZ: Minor filter fault (F)"),
		FDBK_FILTER_FAULT("AZ: Feedback filter fault (F)"), 
			// bit4-7: reserved 
 
		// byte 4 (ubyte): drive on timeout 
		AZ_L1_DRIVE_ON_TO("AZ: AZ-L1 drive on timeout"), 
		AZ_L2_DRIVE_ON_TO("AZ: AZ-L2 drive on timeout"), 
		AZ_R1_DRIVE_ON_TO("AZ: AZ-R1 drive on timeout"), 
		AZ_R2_DRIVE_ON_TO("AZ: AZ-R2 drive on timeout"), 
		AZ_L1_POWER_ON_TO("AZ: AZ-L1 power on timeout"), 
		AZ_L2_POWER_ON_TO("AZ: AZ-L2 power on timeout"), 
		AZ_R1_POWER_ON_TO("AZ: AZ-R1 power on timeout"), 
		AZ_R2_POWER_ON_TO("AZ: AZ-R2 power on timeout"), 
 
		// byte 5 (ubyte): brake axis failure 
		BRAKE_AXIS_DPA_FAULT("AZ: Brake axis DPA fault (F)"), 
		BRAKE_AXIS_DPA_CB_OFF("AZ: Brake axis DPA circuit breaker off (F)"), 
		BRAKE_AXIS_DPA_COND_OFF("AZ: Brake axis DPA contactor off (F)"), 
		BRAKE_AXIS_RSLV_DISCON("AZ: Brake axis resolver disconnected (F)"), 
		BRAKE_AXIS_ASYNC("AZ: Brake axis async"), 
			// bit5-6: reserved 
		BRAKE_AXIS_DRIVE_ON_TO("AZ: Brake axis drive on timeout"); 
 
		// bytes 6-7: reserved 
 
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
		BASE_PWR("Base power"),
		TRANS_PWR("Transporter power"),
		AC_PWR_FAILURE("AC power failure"),
		AC_ABNORMAL_PHASE("AC abnormal phase"),
		
		// Byte 1
		UPS1_DISC("UPS-1 disconnected"),
		UPS1_ALARM("UPS-1 alarm"),
		UPS1_INPUT_FAILURE("UPS-1 input failure"),
		UPS1_BAT_LOW("UPS-1 battery low"),
		
		// Byte 2
		UPS2_DISC("UPS-2 disconnected"),
		UPS2_ALARM("UPS-2 alarm"),
		UPS2_INPUT_FAILURE("UPS-2 input failure"),
		UPS2_BAT_LOW("UPS-2 battery low");
		
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
	public MountA7MStatus() {

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
	private ValueState updateSystemStatus(MountA7M mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.NORMAL;
		}
		ValueHolder<int[]> statusBits = mnt.getStatus();
		if (statusBits!=null && statusBits.getValue()!=null) {
			Long[] longBits = new Long[statusBits.getValue().length];
			if (longBits.length!=8) {
				//System.out.printf("updateSystemStatus longBits.length= %d\n", longBits.length);
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
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,SysStatus.STAIRWAY_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,SysStatus.HANDLING_INTERLOCK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,SysStatus.SMOKE_ALARM.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,SysStatus.ACU_FAULT.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,SysStatus.SURVIVAL_FOR_MISSING_COMMANDS.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,SysStatus.SURVIVAL_FOR_MISSING_TIMING.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,SysStatus.TIMING_PULSE.value,statusBits);
			
			// Byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,SysStatus.RCV_CABIN_SAFETY.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,SysStatus.ANT_BASE_SAFETY.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,2),ret,SysStatus.VERANDA_EQUIP_FREE.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,3),ret,SysStatus.LADDER_SWITCH.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,4),ret,SysStatus.TRANSP_INTLK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,5),ret,SysStatus.TRANSP_INTLK_EXCPT_AZ.value,statusBits);
			
			// Byte 2
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,SysStatus.RCV_CABIN_DOOR_OPEN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,SysStatus.CNTR_CABIN_DOOR_OPEN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,2),ret,SysStatus.ANT_BASE_DOOR_OPEN.value,statusBits);
			//20091002MT  The following three bits have been removed.
			//ret=ValueDisplayer.formatStatusBit(bits.getBit(2,3),ret,SysStatus.UPS_ROOM_DOOR_OPEN.value,statusBits);
			//ret=ValueDisplayer.formatStatusBit(bits.getBit(2,4),ret,SysStatus.PDB_ROOM_DOOR_OPEN.value,statusBits);
			//ret=ValueDisplayer.formatStatusBit(bits.getBit(2,5),ret,SysStatus.MAIN_REF_HATCH_OPEN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,6),ret,SysStatus.YOKE_L_HATCH_OPEN.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,7),ret,SysStatus.YOKE_R_HATCH_OPEN.value,statusBits);
			
			// Byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,SysStatus.AZ_HNDL_INTLK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,SysStatus.EL_HNDL_INTLK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,2),ret,SysStatus.AZ_STOW_HNDL_INTLK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,3),ret,SysStatus.EL_STOW_HNDL_INTLK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,4),ret,SysStatus.SHUTTER_HNDL_INTLK.value,statusBits);
			
			// Byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,SysStatus.AZ_STOW_TO.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,1),ret,SysStatus.AZ_STOW_THERMAL_TRIP.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,2),ret,SysStatus.STOW_DPA_CB_OFF.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,3),ret,SysStatus.STOW_DPA_COND_OFF.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,4),ret,SysStatus.EL_STOW_TO.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,5),ret,SysStatus.EL_STOW_THERMAL_TRIP.value,statusBits);
			
			// Byte 5
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,0),ret,SysStatus.SHUTTER_TO.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,1),ret,SysStatus.SHUTTER_THERMAL_TRIP.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,2),ret,SysStatus.SHUTTER_DPA_CB_OFF.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,3),ret,SysStatus.SHUTTER_DPA_COND_OFF.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,7),ret,SysStatus.ZENITH_SHUTTER_OPEN.value,statusBits);
			
			// Byte 6
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,0),ret,SysStatus.RD_CONV_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,1),ret,SysStatus.IF_PANEL_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,3),ret,SysStatus.NUTATOR_RACK_PS_ALARM.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,4),ret,SysStatus.BRAKE_AXIS_HEATER_ALARM.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,5),ret,SysStatus.MOTOR_PUMP_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,6),ret,SysStatus.ARRESTER_BROKEN_ALARM.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,7),ret,SysStatus.DC_POWER_CIRCUIT_ALARM.value,statusBits);
			
			// Byte 7
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,0),ret,SysStatus.ACU_MAINT.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,1),ret,SysStatus.ANT_STATION_POWER_DISCON.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,2),ret,SysStatus.ANT_INTLK.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,6),ret,SysStatus.DRIVE_SYSTEM_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,7),ret,SysStatus.MORE_PCU_CONNECT.value,statusBits);
			for (int t=SysStatus.SAFE_SWITCH.ordinal(); t<=SysStatus.MORE_PCU_CONNECT.ordinal(); t++) {
				statusVals[t]=SysStatus.values()[t].value.toString();
			}
			return ret;
		} else {
			updateStatusError();
			return ValueState.ERROR;
		}
	}

	private ValueState updateSystemStatus2(MountA7M mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.NORMAL;
		}
		ValueHolder<int[]> statusBits = mnt.getStatus2();
		if (statusBits!=null && statusBits.getValue()!=null) {
			Long[] longBits = new Long[statusBits.getValue().length];
			if (longBits.length!=8) {
				updateStatusError();
				return ValueState.ERROR;
			}
			for (int t=0; t<longBits.length; t++) {
				longBits[t]=(long)statusBits.getValue()[t];
			}
			LongArrayBit bits = new LongArrayBit(longBits);
			ValueState ret = ValueState.NORMAL;
			// Byte 0
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,SysStatus.DC12V_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,SysStatus.SYS_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,SysStatus.AC_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,SysStatus.DATA_FAULT.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,SysStatus.AD_CONV_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,SysStatus.CAN_BOARD_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,SysStatus.CLOCK_BOARD_FAIL.value,statusBits);

			// Byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,SysStatus.DSP_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,SysStatus.DSP_AD_CONV_FAIL.value,statusBits);

			// Byte 2
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,SysStatus.IMP1_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,SysStatus.IMP2_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,2),ret,SysStatus.IMP3_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,3),ret,SysStatus.IMP4_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,4),ret,SysStatus.IMP5_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,5),ret,SysStatus.IMP6_FAIL.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,6),ret,SysStatus.IMP7_FAIL.value,statusBits);
			
			// Byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,SysStatus.PCU1_CONNECTED.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,SysStatus.PCU2_CONNECTED.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,2),ret,SysStatus.PCU3_CONNECTED.value,statusBits);

			// Byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,6),ret,SysStatus.DIFC_FAULT.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,7),ret,SysStatus.DIFC_POWER_FAIL.value,statusBits);

			// Byte 5
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,0),ret,SysStatus.ACU2DIFC_LINE1_DISCON.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,1),ret,SysStatus.ACU2DIFC_LINE2_DISCON.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,2),ret,SysStatus.DIFC2ACU_LINE1_DISCON.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,3),ret,SysStatus.DIFC2ACU_LINE2_DISCON.value,statusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,4),ret,SysStatus.ACU2DIFC_LINE3_DISCON.value,statusBits);
			for (int t=SysStatus.DC12V_FAIL.ordinal(); t<=SysStatus.ACU2DIFC_LINE3_DISCON.ordinal(); t++) {
				statusVals[t]=SysStatus.values()[t].value.toString();
			}
			return ret;
		} else {
			updateStatusError();
			return ValueState.ERROR;
		}
	}
	
	/**
	 * Refresh the power status
	 * 
	 * @param mnt The mount
	 */
	private ValueState updatePowerStatus(MountA7M mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.NORMAL;
		}
		ValueHolder<int[]> powerStatusBits = mnt.getPowerStatus();
		if (powerStatusBits!=null && powerStatusBits.getValue()!=null) {
			Long[] longBits = new Long[powerStatusBits.getValue().length];
			if (longBits.length!=3) {
				updatePowerError();
				return ValueState.ERROR;
			}
			for (int t=0; t<longBits.length; t++) {
				longBits[t]=(long)powerStatusBits.getValue()[t];
			}
			LongArrayBit bits = new LongArrayBit(longBits);
			ValueState ret = ValueState.NORMAL;
			// byte 0
			ret=ValueDisplayer.formatStatusBitInverse(bits.getBit(0,0),ret,PowerStatus.BASE_PWR.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,PowerStatus.TRANS_PWR.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,PowerStatus.AC_PWR_FAILURE.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,PowerStatus.AC_ABNORMAL_PHASE.value,powerStatusBits);
			
			// byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,PowerStatus.UPS1_DISC.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,PowerStatus.UPS1_ALARM.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,2),ret,PowerStatus.UPS1_INPUT_FAILURE.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,3),ret,PowerStatus.UPS1_BAT_LOW.value,powerStatusBits);
			
			// byte 2
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,PowerStatus.UPS2_DISC.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,PowerStatus.UPS2_ALARM.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,2),ret,PowerStatus.UPS2_INPUT_FAILURE.value,powerStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,3),ret,PowerStatus.UPS2_BAT_LOW.value,powerStatusBits);
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
	 * @see MountInterface
	 */
	public ValueState refreshValues(Mount mnt) {
		if (!(mnt instanceof MountA7M)) {
			throw new IllegalArgumentException("The mount is not MountA7M");
		}
		ValueState st=updateSystemStatus((MountA7M)mnt);
		st=ValueState.max(st,updateSystemStatus2((MountA7M)mnt));
		st=ValueState.max(st,updateAzStatus((MountA7M)mnt));
		st=ValueState.max(st,updateAzStatus2((MountA7M)mnt));
		st=ValueState.max(st,updateElStatus((MountA7M)mnt));
		st=ValueState.max(st,updateElStatus2((MountA7M)mnt));
		st=ValueState.max(st,updatePowerStatus((MountA7M)mnt));
		return st;
	}
	
	/**
	 * Refresh the values of the status of the azimuth
	 * 
	 * @param mnt The mount
	 */
	private ValueState updateAzStatus(MountA7M mnt) {
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
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,AzStatus.LIMIT2_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,AzStatus.LIMIT2_CCW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,AzStatus.LIMIT1_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,AzStatus.LIMIT1_CCW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,AzStatus.PRE_LIMIT_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,AzStatus.PRE_LIMIT_CCW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,AzStatus.LIMIT_WARN_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,AzStatus.LIMIT_WARN_CCW.value,azStatusBits);
			
			// byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,AzStatus.INVALID_DIR.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,AzStatus.OVERRIDE_SW_ENABLED.value,azStatusBits);
			
			// byte 2 
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,AzStatus.AZ_L1_DPA_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,AzStatus.AZ_L2_DPA_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,2),ret,AzStatus.AZ_R1_DPA_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,3),ret,AzStatus.AZ_R2_DPA_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,4),ret,AzStatus.AZ_L1_MOTOR_OH.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,5),ret,AzStatus.AZ_L2_MOTOR_OH.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,6),ret,AzStatus.AZ_R1_MOTOR_OH.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,7),ret,AzStatus.AZ_R2_MOTOR_OH.value,azStatusBits);
			
			// byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,AzStatus.AZ_L1_CBANK_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,AzStatus.AZ_L2_CBANK_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,2),ret,AzStatus.AZ_R1_CBANK_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,3),ret,AzStatus.AZ_R2_CBANK_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,4),ret,AzStatus.AZ_L1_CBANK_FULL.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,5),ret,AzStatus.AZ_L2_CBANK_FULL.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,6),ret,AzStatus.AZ_R1_CBANK_FULL.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,7),ret,AzStatus.AZ_R2_CBANK_FULL.value,azStatusBits);
			
			// byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,AzStatus.AZ_L1_EXCSV_CURR.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,1),ret,AzStatus.AZ_L2_EXCSV_CURR.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,2),ret,AzStatus.AZ_R1_EXCSV_CURR.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,3),ret,AzStatus.AZ_R2_EXCSV_CURR.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,4),ret,AzStatus.SERVO_OSCILLATION.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,5),ret,AzStatus.RUNAWAY.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,6),ret,AzStatus.OVERSPEED.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,7),ret,AzStatus.ANGLE_INPUT_FAULT.value,azStatusBits);
			
			// byte 5
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,0),ret,AzStatus.AZ_L_DCPA_CB_OFF.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,1),ret,AzStatus.AZ_R_DCPA_CB_OFF.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,3),ret,AzStatus.AZ_ALL_ENCODER_ALARM.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,4),ret,AzStatus.AZ_LF_ENCODER_ALARM.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,5),ret,AzStatus.AZ_LR_ENCODER_ALARM.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,6),ret,AzStatus.AZ_RF_ENCODER_ALARM.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,7),ret,AzStatus.AZ_RR_ENCODER_ALARM.value,azStatusBits);
			
			// byte 6
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,4),ret,AzStatus.BRAKE_POWER_FAIL.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,5),ret,AzStatus.BRAKE_PS_FUSE_BLOW.value,azStatusBits);
			
			// byte 7
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,0),ret,AzStatus.SURVIVAL_STOW_POS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,1),ret,AzStatus.MAINT_STOW_POS.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,4),ret,AzStatus.CABLE_OVERLAP_CW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,5),ret,AzStatus.CABLE_OVERLAP_CCW.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,7),ret,AzStatus.OTHER_FAULT.value,azStatusBits);
			for (int t=AzStatus.LIMIT2_CW.ordinal(); t<=AzStatus.OTHER_FAULT.ordinal(); t++) {
				azStatusVals[t]=AzStatus.values()[t].value.toString();
			}
			return ret;
		} else {
			updateAzError();
			return ValueState.ERROR;
		}
	}
	
	private ValueState updateAzStatus2(MountA7M mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.NORMAL;
		}
		ValueHolder<int[]> azStatusBits = mnt.getAzStatus2();
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
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,AzStatus.AZ_LF_ENCDR_DISCON.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,AzStatus.AZ_LR_ENCDR_DISCON.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,AzStatus.AZ_RF_ENCDR_DISCON.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,AzStatus.AZ_RR_ENCDR_DISCON.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,AzStatus.AZ_LF_ENCDR_NOT_INIT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,AzStatus.AZ_LR_ENCDR_NOT_INIT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,AzStatus.AZ_RF_ENCDR_NOT_INIT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,AzStatus.AZ_RR_ENCDR_NOT_INIT.value,azStatusBits);
	
			// byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,AzStatus.AZ_L1_DPA_DISCON.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,AzStatus.AZ_L2_DPA_DISCON.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,2),ret,AzStatus.AZ_R1_DPA_DISCON.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,3),ret,AzStatus.AZ_R2_DPA_DISCON.value,azStatusBits);
	
			// byte 2
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,AzStatus.AZ_L1_DPA_COND_OFF.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,AzStatus.AZ_L2_DPA_COND_OFF.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,2),ret,AzStatus.AZ_R1_DPA_COND_OFF.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,3),ret,AzStatus.AZ_R2_DPA_COND_OFF.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,4),ret,AzStatus.AZ_L1_DPA_DISCHARGE.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,5),ret,AzStatus.AZ_L2_DPA_DISCHARGE.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,6),ret,AzStatus.AZ_R1_DPA_DISCHARGE.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,7),ret,AzStatus.AZ_R2_DPA_DISCHARGE.value,azStatusBits);
	
			// byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,AzStatus.POSN_FILTER_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,AzStatus.MAJOR_FILTER_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,2),ret,AzStatus.MINOR_FILTER_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,3),ret,AzStatus.FDBK_FILTER_FAULT.value,azStatusBits);
	
			// byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,AzStatus.AZ_L1_DRIVE_ON_TO.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,1),ret,AzStatus.AZ_L2_DRIVE_ON_TO.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,2),ret,AzStatus.AZ_R1_DRIVE_ON_TO.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,3),ret,AzStatus.AZ_R2_DRIVE_ON_TO.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,4),ret,AzStatus.AZ_L1_POWER_ON_TO.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,5),ret,AzStatus.AZ_L2_POWER_ON_TO.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,6),ret,AzStatus.AZ_R1_POWER_ON_TO.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,7),ret,AzStatus.AZ_R2_POWER_ON_TO.value,azStatusBits);
	
			// byte 5
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,0),ret,AzStatus.BRAKE_AXIS_DPA_FAULT.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,1),ret,AzStatus.BRAKE_AXIS_DPA_CB_OFF.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,2),ret,AzStatus.BRAKE_AXIS_DPA_COND_OFF.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,3),ret,AzStatus.BRAKE_AXIS_RSLV_DISCON.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,4),ret,AzStatus.BRAKE_AXIS_ASYNC.value,azStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,7),ret,AzStatus.BRAKE_AXIS_DRIVE_ON_TO.value,azStatusBits);
			for (int t=AzStatus.AZ_LF_ENCDR_DISCON.ordinal(); t<=AzStatus.BRAKE_AXIS_DRIVE_ON_TO.ordinal(); t++) {
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
	private ValueState updateElStatus(MountA7M mnt) {
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
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,ElStatus.LIMIT2_UP.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,ElStatus.LIMIT2_DOWN.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,2),ret,ElStatus.LIMIT1_UP.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,3),ret,ElStatus.LIMIT1_DOWN.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,ElStatus.PRE_LIMIT_UP.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,ElStatus.PRE_LIMIT_DOWN.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,6),ret,ElStatus.LIMIT_WARN_UP.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,7),ret,ElStatus.LIMIT_WARN_DOWN.value,elStatusBits);
			
			
			// byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,ElStatus.INVALID_DIR.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,ElStatus.OVERRIDE_SW_ENABLED.value,elStatusBits);
			
			// byte 2 
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,ElStatus.EL_L_DPA_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,ElStatus.EL_R_DPA_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,4),ret,ElStatus.EL_L_MOTOR_OH.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,5),ret,ElStatus.EL_R_MOTOR_OH.value,elStatusBits);
			
			// byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,ElStatus.EL_L_CBANK_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,ElStatus.EL_R_CBANK_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,4),ret,ElStatus.EL_L_CBANK_FULL.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,5),ret,ElStatus.EL_R_CBANK_FULL.value,elStatusBits);
			
			// byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,ElStatus.EL_L_EXCSV_CURR.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,1),ret,ElStatus.EL_R_EXCSV_CURR.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,4),ret,ElStatus.SERVO_OSCILLATION.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,5),ret,ElStatus.RUNAWAY.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,6),ret,ElStatus.OVERSPEED.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,7),ret,ElStatus.ANGLE_INPUT_FAULT.value,elStatusBits);
			
			// byte 5
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,0),ret,ElStatus.DCPA_CB_OFF.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,3),ret,ElStatus.EL_ALL_ENCDR_ALARM.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,4),ret,ElStatus.EL_F_ENCDR_ALARM.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,5),ret,ElStatus.EL_R_ENCDR_ALARM.value,elStatusBits);

			// byte 6
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,4),ret,ElStatus.BRAKE_POWER_FAIL.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(6,5),ret,ElStatus.BRAKE_PS_FUSE_BLOW.value,elStatusBits);
			
			// byte 7
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,0),ret,ElStatus.SURVIVAL_STOW_POS.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,1),ret,ElStatus.MAINT_STOW_POS.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,2),ret,ElStatus.ZENITH_POS.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(7,7),ret,ElStatus.OTHER_FAULT.value,elStatusBits);
			for (int t=ElStatus.LIMIT2_UP.ordinal(); t<=ElStatus.OTHER_FAULT.ordinal(); t++) {
				elStatusVals[t]=ElStatus.values()[t].value.toString();
			}
			return ret;
		} else {
			updateElError();
			return ValueState.ERROR;
		}
	}

	private ValueState updateElStatus2(MountA7M mnt) {
		if (mnt==null) {
			updateError();
			return ValueState.NORMAL;
		}
		ValueHolder<int[]> elStatusBits = mnt.getElStatus2();
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
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,0),ret,ElStatus.EL_F_ENCDR_DISCON.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,1),ret,ElStatus.EL_R_ENCDR_DISCON.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,4),ret,ElStatus.EL_F_ENCDR_NOT_INIT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(0,5),ret,ElStatus.EL_R_ENCDR_NOT_INIT.value,elStatusBits);

			// byte 1
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,0),ret,ElStatus.EL_L_DPA_DISCON.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(1,1),ret,ElStatus.EL_R_DPA_DISCON.value,elStatusBits);

			// byte 2
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,0),ret,ElStatus.EL_L_DPA_COND_OFF.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,1),ret,ElStatus.EL_R_DPA_COND_OFF.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,4),ret,ElStatus.EL_L_DPA_DISCHARGE.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(2,5),ret,ElStatus.EL_R_DPA_DISCHARGE.value,elStatusBits);

			// byte 3
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,0),ret,ElStatus.POSN_FILTER_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,1),ret,ElStatus.MAJOR_FILTER_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,2),ret,ElStatus.MINOR_FILTER_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(3,3),ret,ElStatus.FDBK_FILTER_FAULT.value,elStatusBits);

			// byte 4
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,0),ret,ElStatus.EL_L_DRIVE_ON_TO.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,1),ret,ElStatus.EL_R_DRIVE_ON_TO.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,4),ret,ElStatus.EL_L_POWER_ON_TO.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(4,5),ret,ElStatus.EL_R_POWER_ON_TO.value,elStatusBits);

			// byte 5
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,0),ret,ElStatus.BRAKE_AXIS_DPA_FAULT.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,1),ret,ElStatus.BRAKE_AXIS_DPA_CB_OFF.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,2),ret,ElStatus.BRAKE_AXIS_DPA_COND_OFF.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,3),ret,ElStatus.BRAKE_AXIS_RSLV_DISCON.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,4),ret,ElStatus.BRAKE_AXIS_ASYNC.value,elStatusBits);
			ret=ValueDisplayer.formatStatusBit(bits.getBit(5,7),ret,ElStatus.BRAKE_AXIS_DRIVE_ON_TO.value,elStatusBits);
			for (int t=ElStatus.EL_F_ENCDR_DISCON.ordinal(); t<=ElStatus.BRAKE_AXIS_DRIVE_ON_TO.ordinal(); t++) {
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
		if (row<0 || row>=SysStatus.values().length) {
			throw new IllegalArgumentException("Row "+row+" is out of range [0,"+SysStatus.values().length+"]");
		}
		return SysStatus.values()[row].title;
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
