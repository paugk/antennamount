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

import alma.control.gui.antennamount.utils.ValueDisplayer;

/**
 * A class containing common methods to show values in MountVA, MountAEM, MountACA and MountA7M
 * 
 * @author acaproni
 *
 */
public abstract class MountDetails {
	
	/**
	 * The description of a ACU error
	 * Such a description has to be read from the IDL so this
	 * variable is defined by specializing classes
	 */
	protected String[] ACU_ERROR_DESC=null;
	
	/**
	 * Rows titles for the status
	 * See GET_SYSTEM_STATUS
	 */
	protected String[] statusVals=null; 
	
	/**
	 * Rows titles for the EL status
	 * See GET_EL_STATUS
	 */
	protected String[] elStatusVals=null;
	
	/**
	 * Rows titles for the status
	 * See GET_AZ_STATUS
	 */
	protected String[] azStatusVals=null;
	
	/**
	 * Rows titles for the status
	 * See GET_POWER_STATUS
	 */
	protected String[] powerStatusVals=null; 
	
	// The titles for the sections of the table
	private static String statusTitle = "<HTML><FONT color=\"blue\"><B>System status</B></FONT>";
	private static String elTitle = "<HTML><FONT color=\"blue\"><B>Elevation status</B></FONT>";
	private static String azTitle = "<HTML><FONT color=\"blue\"><B>Azimuth status</B></FONT>";
	
	/**
	 * The title of the power status section
	 */
	private static String powerStatusTitle = "<HTML><FONT color=\"blue\"><B>Power status</B></FONT>";
	
	protected final String unknownAcuError="UNKNOWN ACU ERROR";
	
	/**
	 * @see MountInterface
	 */
	public void updateError() {
		updateStatusError();
		updateAzError();
		updateElError();
		updatePowerError();
	}
	
	/**
	 * Set the power state to error
	 */
	protected void updatePowerError() {
		if (powerStatusVals==null) {
			return;
		}
		for (int t=0; t<powerStatusVals.length; t++) {
			powerStatusVals[t]=ValueDisplayer.RED_NOT_AVAILABLE;
		}
	}
	
	/**
	 * Set the status info on error
	 *
	 */
	protected void updateStatusError() {
		if (statusVals==null) {
			return;
		}
		for (int t=0; t<statusVals.length; t++) {
			statusVals[t]=ValueDisplayer.RED_NOT_AVAILABLE;
		}
	}
	
	/**
	 * Set all the entries for the EL Status to error
	 *
	 */
	protected void updateElError() {
		if (elStatusVals==null) {
			return;
		}
		for (int t=0; t<elStatusVals.length; t++) {
			elStatusVals[t]=ValueDisplayer.RED_NOT_AVAILABLE;
		}
	}
	
	/**
	 * Set all the entries for the AZ Status to error
	 *
	 */
	protected void updateAzError() {
		if (azStatusVals==null) {
			return;
		}
		for (int t=0; t<azStatusVals.length; t++) {
			azStatusVals[t]=ValueDisplayer.RED_NOT_AVAILABLE;
		}
	}
	
	
	
	/**
	 * @see MountInterface
	 */
	public int getRowCount() {
		return statusVals.length+azStatusVals.length+elStatusVals.length+powerStatusVals.length+4;
	}
	
	/**
	 * @see MountInterface
	 */
	public String getValueAt(int row, int col) {
		// Initialize the position of the tiles of the System, AZ and EL sections.
		// (they are different for each type of antenna implementing this abstract class)
		int statusTitlePosition=0;
		int azTitlePosition=statusVals.length+1;
		int elTitlePosition=azTitlePosition+azStatusVals.length+1;
		int powerTitlePosition=elTitlePosition+elStatusVals.length+1;
		if (row>getRowCount()) {
			throw new IllegalArgumentException("Requested a row greater then available rows ("+row+">"+getRowCount()+")");
		}
		if (row==statusTitlePosition) {
			if (col==0) {
				return statusTitle;
			} else {
				return "";
			}
		} else if (row>statusTitlePosition && row<azTitlePosition) {
			// SYSTEM STATUS
			if (col==0) {
				return getStatusRowTitle(row-1);
			} else {
				return statusVals[row-1];
			}
		} else if (row==azTitlePosition) {
			if (col==0) {
				return azTitle;
			} else {
				return "";
			}
		} else if (row>azTitlePosition && row<elTitlePosition) {
			// AZ STATUS
			if (col==0) {
				return getAzRowTitle(row-statusVals.length-2);
			} else {
				return azStatusVals[row-statusVals.length-2];
			}
		} else if (row==elTitlePosition) {
			if (col==0) {
				return elTitle;
			} else {
				return "";
			}
		} else if (row>elTitlePosition && row<elTitlePosition+elStatusVals.length+1) {
			// EL STATUS
			if (col==0) {
				return getElRowTitle(row-statusVals.length-azStatusVals.length-3);
			} else {
				return elStatusVals[row-statusVals.length-azStatusVals.length-3].toString();
			}
		} else if (row==powerTitlePosition) {
			if (col==0) {
				return powerStatusTitle;
			} else {
				return "";
			}
		}
		else if (row>powerTitlePosition && row<powerTitlePosition+powerStatusVals.length+1) {
			// POWER STATUS
			if (col==0) {
				return getPowerStatusRowTitle(row-statusVals.length-azStatusVals.length-elStatusVals.length-4);
			} else {
				return powerStatusVals[row-statusVals.length-azStatusVals.length-elStatusVals.length-4].toString();
			}
		} else {
			return "<HTML><FONT color=\"red\"><B>Wrong col: "+row+" statusPos="+statusTitlePosition+", azPos="+azTitlePosition+", elPos="+elTitlePosition+"</B></FONT>";
		}
	}
	
	/**
	 * @see alma.control.gui.antennamount.tracking.MountInterface
	 */
	public String getAcuErrorDescription(int errorCode, int CANaddress) {
		if (ACU_ERROR_DESC==null) {
			// ACU_ERROR_DESC is defined in specialized classes
			return ValueDisplayer.RED_NOT_AVAILABLE;
		}
		if (errorCode<0 || errorCode>=ACU_ERROR_DESC.length) {
			throw new IllegalArgumentException("Invalid error code for ACU: "+errorCode);
		}
		if (errorCode==0) {
			// OK
			return ACU_ERROR_DESC[errorCode];
		}
		// Error ==> Add the CAN address
		return ACU_ERROR_DESC[errorCode]+", CAN addr: "+Integer.toHexString(CANaddress);
	}
	
	/**
	 * @param row The number of the status row
	 * @return The tile of the status row
	 */
	public abstract String getStatusRowTitle(int row);
	
	/**
	 * @param row The number of the power status row
	 * @return The tile of the status row
	 */
	public abstract String getPowerStatusRowTitle(int row);
	
	/**
	 * @param row The number of the EL status row
	 * @return The tile of the EL status row
	 */
	public abstract String getElRowTitle(int row);
	
	/**
	 * @param row The number of the AZ status row
	 * @return The tile of the AZ status row
	 */
	public abstract String getAzRowTitle(int row);
}
