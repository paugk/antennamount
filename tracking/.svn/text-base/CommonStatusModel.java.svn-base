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

import java.awt.Component;

import alma.Control.AntModeControllerPackage.Status;
import alma.Control.HardwareDevicePackage.HwState;
import alma.Control.MountPackage.BrakesStatus;
import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorInfo;
import alma.control.gui.antennamount.errortab.ErrorTabbedPane;
import alma.control.gui.antennamount.errortab.TabTitleSetter;
import alma.control.gui.antennamount.mount.ShutterCommon;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.tolerancepanel.TolerancePanel;
import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.ValueState;
import alma.control.gui.antennamount.utils.ValueDisplayer.DisplayStruct;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * 
 * The model for the table with the status of the antenna
 * 
 * @author  acaproni
 */
public class CommonStatusModel extends AbstractTableModel implements Runnable {
	
	/**
	 * The titles of the rows.
	 * Usage of enum avoid to use indexes of arrays enhancing readability/maintainability of the code.
	 * 
	 * The order of the entries in this enum reflects the order the rows are displayed in the table
	 * 
	 * 
	 * @author acaproni
	 *
	 */
	private enum StatusTableRow {
		ANTENNA_TYPE("Antenna type"),
		STATUS("Status"),
		HWSTATUS("Hardware status"),
		AZ_BRAKE("Az brake"),
		EL_BRAKE("El brake"),
		IN_POSITION("In position"),
		ACCESS_MODE("Access mode"),
		ACU_ERROR("ACU Error"),
		SHUTTER("Shutter"),
		TOLERANCE("Tolerance"),
		ABM_POINTING_MODEL("ABM pointing model"),
		ACU_POINTING_MODEL("ACU pointing model"),
		AUX_ACU_POINTING_MODEL("Aux ACU pointing model");
		
		public final String title;
		public final StringBuilder value = new StringBuilder();
		
		/**
		 * Constructor
		 * 
		 * @param rowTitle The tile of the Row
		 */
		private StatusTableRow(String rowTitle) {
			title=rowTitle;
		}
		
		/**
		 * Shortcut to set a string the value field
		 *  
		 * @param val The string to set in the value property
		 */
		public void setValue(String val) {
			if (val==null) {
				val="";
			}
			if (value.length()>0) {
				value.delete(0, value.length());
			}
			value.append(val);
		}
	}
	
	// The detailed status view for the connected antenna
	private MountInterface detailedStatusView=null;
	
	/**
	 * The thread to update the state of the antenna
	 */
	private Thread thread;
	
	/**
	 * Signal the thread to terminate
	 */
	private volatile boolean terminateThread=false;
    
    
    /**
     * The interval for the refresh (msecs)
     */
    private static final int REFRESH_TIME = 1500;
    
    /**
     * The Az brakes
     */
    private ValueHolder<BrakesStatus> azBrake;
    
    /**
     * The El brakes
     */
    private ValueHolder<BrakesStatus> elBrake;
    
    /**
     * The tolerance
     */
    private ValueHolder<Double> tolerance;
    
    /**
     * The ACU error
     */
    private ValueHolder<int[]> acuError;
    
    /**
     * The local/remote access model
     */
    private ValueHolder<Boolean> localAccessMode;
    
    /**
     * The boolean to know if the mount is on target
     */
    private ValueHolder<Boolean> onTarget = null;
    
    /**
     * The boolean for the ACS pointing model
     */
    private ValueHolder<Boolean> acuPointingModel = null;
    
    /**
     * The boolean for the ACS pointing model
     */
    private ValueHolder<Boolean> auxAcuPointingModel = null;
    
    /**
     * The ABM pointing model
     */
    private ValueHolder<Boolean> abmPointingModel= null;
    
    /**
     * The status
     */
    private ValueHolder<Status> status = null;
    
    /**
	 * The state of the HW
	 */
	private ValueHolder<HwState> hwState=new ValueHolder<HwState>();
    
    /**
     * The shutter
     */
    private ShutterCommon shutter;
    
    /**
     * The Mount 
     */
    private Mount mount=null;
    
    /**
     * The MountController 
     */
    private MountController controller;
    
    /**
	 * <code>titleSetter</code> is used to set the title of the tab, depending on the state
	 * of the bits of the subreflector
	 */
	private TabTitleSetter titleSetter;
	
	/**
	 * The component shown in the error tab (to set the title of the tab with the
	 * right color and icon)
	 */
	private Component errorTabComponent;
	
	/**
	 * The 	{@link AntennaRootPane} to add errors to the error tab
	 */
	private AntennaRootPane antennaRootPane;
	
	public CommonStatusModel(AntennaRootPane antennaRootPane) {
		super();
		if (antennaRootPane==null) {
			throw new IllegalArgumentException("The AntennaRootPane can't be null");
		}
		this.antennaRootPane=antennaRootPane;
		for (StatusTableRow str: StatusTableRow.values()) {
			str.setValue(ValueDisplayer.NOT_AVAILABLE);
		}
	}

	public int getRowCount() {
		if (detailedStatusView==null) {
			return StatusTableRow.values().length;
		} else {
			return StatusTableRow.values().length+detailedStatusView.getRowCount();
		}
	}

	public int getColumnCount() {
		return 2;
	}

	/**
	 * @see TableModel
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex<StatusTableRow.values().length) {
			if (columnIndex==0) {
				return StatusTableRow.values()[rowIndex].title;
			} else {
				return StatusTableRow.values()[rowIndex].value.toString();
			}
		} else {
			return detailedStatusView.getValueAt(rowIndex-StatusTableRow.values().length,columnIndex);
		}
	}
	
	/**
	 * Refresh the values shown in the antenna
	 *
	 */
	public ValueState refresh() {
		if (mount==null) {
			for (StatusTableRow str: StatusTableRow.values()) {
				str.setValue(ValueDisplayer.NOT_AVAILABLE);
			}
			fireTableDataChanged();
			return ValueState.NORMAL;
		}
		
		ValueState ret = ValueState.NORMAL;
		DisplayStruct dStruct;
		
		// HW state
		if (hwState!=null && hwState.getValue()!=null) {
			dStruct=ValueDisplayer.getString(hwState.getValue().toString(), hwState);
			if (hwState.getValue()==HwState.Stop) {
				// The hardware is stopped
				StatusTableRow.HWSTATUS.setValue(ValueDisplayer.errorColor+dStruct.str+ValueState.htmlCloser);
				ret=ValueState.ERROR;
			} else {
				StatusTableRow.HWSTATUS.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
				ret = ValueState.max(ret, dStruct.state);
			}
		} else {
			ret=ValueState.ERROR;
			StatusTableRow.HWSTATUS.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		
		// AZ BRAKE
		if (azBrake!=null && azBrake.getValue()!=null) {
			if (azBrake.getValue()==BrakesStatus.BRAKE_ENGAGED) {
				dStruct=ValueDisplayer.getString("Engaged", azBrake);
			} else {
				dStruct=ValueDisplayer.getString("Disengaged", azBrake);
			}
			StatusTableRow.AZ_BRAKE.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
			ret = ValueState.max(ret, dStruct.state);
		} else {
			ret=ValueState.ERROR;
			StatusTableRow.AZ_BRAKE.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		
		if (terminateThread) {
			return ret;
		}
		
		// EL BRAKE
		if (elBrake!=null && elBrake.getValue()!=null) {
			if (elBrake.getValue()==BrakesStatus.BRAKE_ENGAGED) {
				dStruct=ValueDisplayer.getString("Engaged",elBrake);
			} else {
				dStruct=ValueDisplayer.getString("Disengaged",elBrake);
			}
			StatusTableRow.EL_BRAKE.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
			ret = ValueState.max(ret, dStruct.state);
		} else {
			ret=ValueState.ERROR;
			StatusTableRow.EL_BRAKE.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		
		if (terminateThread) {
			return ret;
		}
		
		// LOCAL ACCESS MODE
		if (localAccessMode!=null && localAccessMode.getValue()!=null) {
			if (localAccessMode.getValue()==Boolean.TRUE) {
				dStruct=ValueDisplayer.getString("Local",localAccessMode);
			} else {
				dStruct=ValueDisplayer.getString("Remote",localAccessMode);
			}
			StatusTableRow.ACCESS_MODE.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
			ret = ValueState.max(ret, dStruct.state);
		} else {
			ret=ValueState.ERROR;
			StatusTableRow.ACCESS_MODE.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		
		if (terminateThread) {
			return ret;
		}
		
		// ACU ERROR
		dStruct=updateACUError(detailedStatusView);
		StatusTableRow.ACU_ERROR.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
		ret=ValueState.max(ret, dStruct.state);
		
		
		// On target
		if (onTarget!=null) {
			if (onTarget.getValue()==Boolean.TRUE) {
				dStruct=ValueDisplayer.getString("On target",onTarget);
			} else {
				dStruct=ValueDisplayer.getString("NOT on target",onTarget);
			}
			StatusTableRow.IN_POSITION.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
			ret = ValueState.max(ret, dStruct.state);
		} else {
			ret=ValueState.ERROR;
			StatusTableRow.IN_POSITION.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		
		if (terminateThread) {
			return ret;
		}
		
		// The ACU pointing model
		if (acuPointingModel!=null) {
			if (acuPointingModel.getValue()==Boolean.TRUE) {
				dStruct=ValueDisplayer.getString("Applied",acuPointingModel);
			} else {
				dStruct=ValueDisplayer.getString("NOT applied",acuPointingModel);
			}
			StatusTableRow.ACU_POINTING_MODEL.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
			ret = ValueState.max(ret, dStruct.state);
		} else {
			ret=ValueState.ERROR;
			StatusTableRow.ACU_POINTING_MODEL.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		
		if (terminateThread) {
			return ret;
		}
		
		// The AUX ACU pointing model
		if (auxAcuPointingModel!=null) {
			if (acuPointingModel.getValue()==Boolean.TRUE) {
				dStruct=ValueDisplayer.getString("Applied",acuPointingModel);
			} else {
				dStruct=ValueDisplayer.getString("NOT applied",acuPointingModel);
			}
			StatusTableRow.AUX_ACU_POINTING_MODEL.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
			ret = ValueState.max(ret, dStruct.state);
		} else {
			ret=ValueState.ERROR;
			StatusTableRow.AUX_ACU_POINTING_MODEL.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		
		if (terminateThread) {
			return ret;
		}
		
		// The ABM pointing model
		if (abmPointingModel!=null) {
			if (abmPointingModel.getValue()==Boolean.TRUE) {
				dStruct=ValueDisplayer.getString("Applied",abmPointingModel);
			} else {
				dStruct=ValueDisplayer.getString("NOT applied",abmPointingModel);
			}
			StatusTableRow.ABM_POINTING_MODEL.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
			ret = ValueState.max(ret, dStruct.state);
		} else {
			ret=ValueState.ERROR;
			StatusTableRow.ABM_POINTING_MODEL.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		
		if (terminateThread) {
			return ret;
		}
		
		// The tolerance
		if (tolerance==null || tolerance.getValue()==null) {
			ret=ValueState.ERROR;
			StatusTableRow.TOLERANCE.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		} else {
			String newActVal=String.format("%02.2f", (double)(((double)1/TolerancePanel.CONVERSION_FACTOR))*tolerance.getValue());
			dStruct=ValueDisplayer.getString(newActVal, tolerance);
			StatusTableRow.TOLERANCE.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
			ret = ValueState.max(ret, dStruct.state);
		}
		
		// The shutter
		// The line contains all the string returned by getShutterStatus()
		if (shutter!=null){
			StringBuilder str = new StringBuilder();
			String[] props;
			try {
				props= shutter.getShutterStatus();
				for (int t=0; props!=null && t<props.length; t++) {
					if (t>0) {
						str.append(", ");
					}
					str.append(props[t]);
				}
			} catch (Throwable t) {
				// This exception can happen when the value read from the mount is incorrect
				str.append("Wrong value from mount component");
			}
			dStruct=ValueDisplayer.getString(str.toString(),(ValueHolder<?>)shutter.getShutterValueHolder());
			StatusTableRow.SHUTTER.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
			ret = ValueState.max(ret, dStruct.state);
		} else {
			ret=ValueState.ERROR;
			StatusTableRow.SHUTTER.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		
		// The status
		if (status!=null && status.getValue()!=null) {
			dStruct=ValueDisplayer.getString(status.getValue().toString(), status);
			if (
					status.getValue()==Status.ERROR ||
					status.getValue()==Status.UNINITIALIZED) {
				StatusTableRow.STATUS.setValue(ValueDisplayer.errorColor+dStruct.str+ValueState.htmlCloser);
				ret=ValueState.ERROR;
			} else {
				StatusTableRow.STATUS.setValue(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
				ret = ValueState.max(ret, dStruct.state);
			}
		} else {
			ret=ValueState.ERROR;
			StatusTableRow.STATUS.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		
		// Refresh the antenna specific status
		if (detailedStatusView!=null) {
			ret=ValueState.max(ret,detailedStatusView.refreshValues(mount));
		}
		
		return ret;
		
	}
	
	/**
	 * Return a struct with the error state of the ACU
	 * 
	 * @param mnt The mount
	 */
	private DisplayStruct updateACUError(MountInterface mnt) {
		DisplayStruct ret = new DisplayStruct();
		if (mnt==null) {
			ret.state=ValueState.NORMAL;
			ret.str=ValueDisplayer.NOT_AVAILABLE;
			return ret;
		}
		if (acuError==null || acuError.getValue()==null) {
			ret.state=ValueState.ERROR;
			ret.str=ValueDisplayer.NOT_AVAILABLE;
			return ret;
		}
		int[] vals= acuError.getValue();
		if (vals.length==0 ||vals[0]==0) {
			// No error!
			ret.str=mnt.getAcuErrorDescription(0,0);
			ret.state=ValueState.NORMAL;
			return ret;
		}
		if (vals.length!=2) {
			ret.state=ValueState.ERROR;
			ret.str="Invalid ACU error description from mount";
			return ret;
		}
		
		ret.state=ValueState.ERROR;
		ret.str=mnt.getAcuErrorDescription(vals[0],vals[1]);
		return ret;
		
	}
	
	/**
	 * Set all the fields to error
	 *
	 */
	private void updateError() {
		for (StatusTableRow stw: StatusTableRow.values()) {
			stw.setValue(ValueDisplayer.RED_NOT_AVAILABLE);
		}
		if (detailedStatusView!=null) {
			detailedStatusView.updateError();
		}
		fireTableDataChanged();
	}
	
	public String getColumnName(int columnIndex) {
		if (columnIndex==0) {
			return "<HTML><B>Monitor points";
		} else {
			return "<HTML><B>Status";
		}
	}
	
	/**
	 * Close the thread and release all the resources
	 */
	public void close() {
		terminateThread=true;
		thread.interrupt();
		setComponents(null,null);
	}
	
	/**
	 * Set the description of the antenna in rowValues[0]
	 */
	private ValueState setAntennaType() {
		StatusTableRow.ANTENNA_TYPE.value.delete(0, StatusTableRow.ANTENNA_TYPE.value.length());
		if (mount==null) {
			StatusTableRow.ANTENNA_TYPE.value.append(ValueDisplayer.NOT_AVAILABLE);
			return ValueState.NORMAL;
		}
		StatusTableRow.ANTENNA_TYPE.value.append(mount.getMountType().toString());
		
		switch (mount.getMountType()) {
		case VERTEX: {
			detailedStatusView= new MountVA();
			break;
		}
		case MELCO: {
			detailedStatusView= new MountACAStatus();
			break;
		}
		case ALCATEL: {
			detailedStatusView= new MountAEMStatus();
			break;
		}
		case MELCOA7M: {
			detailedStatusView= new MountA7MStatus();
			break;
		}
		default: {
			detailedStatusView=null;
			return ValueState.ERROR;
		}
		}
		return ValueState.NORMAL;
	}
	
	/**
	 * Set the mount and the mount controller to get values from
	 * 
	 * @param mnt The mount (can be null)
	 */
	public void setComponents(MountController ctr, Mount mnt) {
		controller=ctr;
		mount=mnt;
		
		if (controller!=null) {
			status = controller.getStatus();
		} else {
			status=null;
		}
		if (mnt!=null) {
			azBrake= mount.getAzBrake();
			elBrake= mount.getElBrake();
			localAccessMode =mount.getLocalAccessMode();
			acuError=mount.getAcuError();
			acuPointingModel=mount.getAcuPointingModel();
			auxAcuPointingModel=mount.getAuxAcuPointingModel();
			onTarget=controller.getIsOnSource();
			abmPointingModel=mnt.getAbmPointingModel();
			shutter=mount.getShutter();
			tolerance=mount.getTolerance();
			hwState=mount.getHwState();
			
			// Start the thread
			terminateThread=false;
			thread = new Thread(this);
			thread.setDaemon(true);
			thread.setName("CommonStatusModel");
			antennaRootPane.getHeartbeatChecker().register(thread);
			thread.start();
		} else {
			// Stop the thread
			terminateThread=true;
			if (thread!=null) {
				thread.interrupt();
			}
			azBrake=null;
			elBrake=null;
			localAccessMode=null;
			detailedStatusView=null;
			acuError=null;
			onTarget=null;
			abmPointingModel=null;
			auxAcuPointingModel=null;
			acuPointingModel=null;
			shutter=null;
			tolerance=null;
		}
		setAntennaType();
	}
	
	/**
	 * The thread to update the values in the table
	 */
	public void run() {
		while (!terminateThread) {
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException ie) {}
			antennaRootPane.getHeartbeatChecker().ping(thread);
			ValueState st;
			try {
				st=refresh();
			} catch (Throwable t) {
				// This should never happen but it is better to catch
				// the exception and log a message to avoid the exception
				// blocks the thread avoid refreshing the panel
				fireTableDataChanged();
				AcsJMountGUIErrorEx ex = new AcsJMountGUIErrorEx(t);
				ex.setContextDescription("Error refreshing antenna status");
				ErrorInfo error = new ErrorInfo("Error refreshing antenna status", "Error refreshing the antenna status",ex);
				antennaRootPane.addError(error);
				continue;
			}
			titleSetter.tabTitleState(st,"Status",errorTabComponent,false);
			fireTableDataChanged();
		}
		updateError();
		antennaRootPane.getHeartbeatChecker().unregister(thread);
		thread=null;
	}
	
	/**
	 * Set the tab title setter (it is informed about the situation of the items
	 * to display warnings and errs in the tab title).
	 * 
	 * @param titleSetter The title setter
	 * @param tabComponent The component
	 * 
	 * @see ErrorTabbedPane
	 */
	public void setTabTitleSetter(TabTitleSetter titleSetter, Component tabComponent) {
		if (titleSetter==null) {
			throw new IllegalArgumentException("TabTitleSetter can't be null");
		}
		if (tabComponent==null) {
			throw new IllegalArgumentException("Tab component can't be null");
		}
		this.titleSetter=titleSetter;
		this.errorTabComponent=tabComponent;
	}
}
