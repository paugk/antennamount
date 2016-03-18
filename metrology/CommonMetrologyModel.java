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
 * @version $Id: CommonMetrologyModel.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.metrology;

import java.awt.Component;

import alma.ControlGUIErrType.wrappers.AcsJMountGUIErrorEx;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.errortab.ErrorInfo;
import alma.control.gui.antennamount.errortab.ErrorTabbedPane;
import alma.control.gui.antennamount.errortab.TabTitleSetter;
import alma.control.gui.antennamount.mount.IMetrology;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.ValueDisplayer.DisplayStruct;
import alma.control.gui.antennamount.utils.ValueState;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * 
 * The model for the table with the status of the metrology.
 * <P>
 * This model will be specialized for every type of antenna.
 * <P>
 * The table produced by this model is
 * <UL>
 * 	<LI>Deltas (Common)
 * 	<LI>Delta path (Common)
 * 	<LI>Delta temp (vendor specific)
 * 	<LI>Equipment status (vendor specific)
 * 	<LI>. . .
 * 	<LI>Temperatures (Common)
 * </ul>
 *

 * <P>
 * The structure of the table is as folows:
 * <UL>
 * 	<LI>Common status bits
 *  <LI>Vendor specific status bit
 *  <LI>Common status bits
 *  </UL>
 * All the common parts are on top of the table followed by vendor specific fields.
 * The only exceptions are the temperatures that are at the bottom of the table 
 * even if they are in the common part. This is because I suppose that the
 * temperatures are not read very often.
 */
public abstract class CommonMetrologyModel extends AbstractTableModel {
	
	/**
	 * Th eenumerated for the deltas
	 * 
	 * @author acaproni
	 *
	 */
	protected enum Deltas {
		AZ_DELTA("AZ delta correction"),
		EL_DELTA("EL delta correction");
		
		/**
		 * The text of the row
		 */
		private final String title;
		
		/**
		 * The value of the delta
		 */
		private String value=new String(ValueDisplayer.NOT_AVAILABLE);
		
		/**
		 * Constructor
		 * 
		 * @param title The text of the row
		 */
		private Deltas(String title) {
			this.title=title;
		}
		
		/**
		 * refresh the values
		 * 
		 * @param vals The values read from the mount
		 */
		public static ValueState refresh(ValueHolder<int[]> vals) {
			if (vals==null || vals.getValue()==null) {
				AZ_DELTA.value=ValueDisplayer.NOT_AVAILABLE;
				EL_DELTA.value=ValueDisplayer.NOT_AVAILABLE;
				return ValueState.NORMAL;
			}
			DisplayStruct ds=ValueDisplayer.getString(""+vals.getValue()[0], vals);
			AZ_DELTA.value=ds.state.format(ds.str);
			ds=ValueDisplayer.getString(""+vals.getValue()[1], vals);
			EL_DELTA.value=ds.state.format(ds.str);
			return ds.state;
		}
	}
	
	/**
	 * The enumerated for the delta path
	 * 
	 * @author acaproni
	 *
	 */
	protected enum DeltaPath {
		DELTAPATH("Error in path length - Subref. hexapod correction in Z");
		
		/**
		 * The text of the row
		 */
		private final String title;
		
		/**
		 * The value of the delta
		 */
		private String value=new String(ValueDisplayer.NOT_AVAILABLE);
		
		/**
		 * refresh the value of deltaPath
		 */
		public static ValueState refresh(ValueHolder<Integer> val) {
			if (val==null || val.getValue()==null) {
				DELTAPATH.value=ValueDisplayer.NOT_AVAILABLE;
				return ValueState.NORMAL;
			}
			DisplayStruct ds=ValueDisplayer.getString(""+val.getValue(), val);
			DELTAPATH.value=ds.str;
			return ds.state;
		}
		
		/**
		 * Constructor
		 * 
		 * @param title The text of the row
		 */
		private DeltaPath(String title) {
			this.title=title;
		}
		
	}
	
	/**
	 * The class for the temperatures
	 * 
	 * @author acaproni
	 *
	 */
	private class Temps {
		/**
		 * The titles of the temp. sensors
		 */
		public final String[] titles = new String[100];
		
		/**
		 * The values of the temp. sensors
		 */
		public final String[] values = new String[100];
		
		/**
		 * Constructor
		 */
		public Temps() {
			for (int t=0; t<titles.length; t++) {
				titles[t]=String.format("Temperature of sensor #%02d",t);
				values[t]=ValueDisplayer.NOT_AVAILABLE;
			}
		}
		
		/**
		 * 
		 * @return The size of the temperatures
		 */
		public int size() {
			return titles.length;
		}
		
		/**
		 * Refresh the values of the sensors
		 * 
		 * @param values The values to display in the table
		 * @return the state error/ok
		 */
		public ValueState refresh(ValueHolder<int[]> vals) {
			ValueState ret=ValueState.NORMAL;
			for (int t=0; t<titles.length; t++) {
				if (vals==null || vals.getValue()==null) {
					values[t]=ValueDisplayer.NOT_AVAILABLE;
				} else {
					float val = vals.getValue()[t]/100;
					String str=null;
					if (val>=299 || val<=-299) {
						str = "Sensor missing or disabled";
					} else {
						str = String.format("%+03.2f", val);
					}
					DisplayStruct ds=ValueDisplayer.getString(str, vals);
					values[t]=ds.str;
					ret=ValueState.max(ret, ds.state);
				}
			}
			return ret;
		}
	}
	
	/**
	 * Signal the thread to terminate
	 */
	private boolean terminateThread=false;
    
    /**
     * The Mount 
     */
    protected IMetrology metrology=null;
    
    /**
	 * <code>titleSetter</code> is used to set the title of the tab, depending on the state
	 * of the bits of the metrology
	 */
	private TabTitleSetter titleSetter;
	
	/**
	 * The component shown in the error tab (to set the title of the tab with the
	 * right color and icon)
	 */
	private Component errorTabComponent;
	
	/**
	 *  The title of the equipment status section
	 */
	protected static String equipStatusTitle = "<HTML><FONT color=\"blue\"><B>Equipment status</B></FONT>";
	
	/**
	 *  The title of the equipment status section
	 */
	protected static String deltasTitle = "<HTML><FONT color=\"blue\"><B>Delta correction</B></FONT>";
	
	/**
	 *  The title of the equipment status section
	 */
	protected static String deltaPathTitle = "<HTML><FONT color=\"blue\"><B>Error in path length</B></FONT>";
	
	/**
	 *  The title of the temperatures section
	 */
	protected static String tempsTitle = "<HTML><FONT color=\"blue\"><B>Temperatures</B></FONT>";
	
	/**
	 * The last row (zero-based) of the common part.
	 * 
	 * <E>Note</E>: this does not include the temps at the bottom of the table
	 */
	protected final int lastCommonRow=1+Deltas.values().length+DeltaPath.values().length;
	
	/**
	 * The temperature of th esensor to display in the table
	 */
	private final Temps temps = new Temps();
	
	/**
	 * The 	{@link AntennaRootPane} to add errors to the error tab
	 */
	private AntennaRootPane antennaRootPane;
	
	public CommonMetrologyModel(AntennaRootPane antennaRootPane, IMetrology metrology) {
		if (antennaRootPane==null) {
			throw new IllegalArgumentException("The AntennaRootPane can't be null");
		}
		if (metrology==null) {
			throw new IllegalArgumentException("The IMetrology can't be null");
		}
		this.antennaRootPane=antennaRootPane;
		this.metrology=metrology;
	}

	/**
	 * The number of rows depends on the type of the antenna.
	 * <P>
	 * <EM><B>Note</B>: this methos return the number of rows in the common part only</EM>
	 * 
	 * @see AbstractTableModel
	 */
	@Override
	public int getRowCount() {
		return lastCommonRow+1+temps.size();
	}

	/**
	 * @see AbstractTableModel
	 */
	@Override
	public int getColumnCount() {
		return 2;
	}

	/**
	 * Return the value of the cells at the bottom of the table
	 * @see TableModel
	 */
	public Object getBottomValue(int rowIndex, int columnIndex) {
		if (rowIndex>lastCommonRow) {
			throw new IllegalStateException("Invalid row requested "+rowIndex);
		}
		//DELTAS
		if (rowIndex==0) {
			if (columnIndex==0) {
				return deltasTitle;
			} else {
				return "";
			}
		}
		if (rowIndex>0 && rowIndex<3) {
			if (columnIndex==0) {
				return Deltas.values()[rowIndex-1].title;
			} else {
				return Deltas.values()[rowIndex-1].value;
			}
		}
		// DELTAPATH
		if (rowIndex==3) {
			if (columnIndex==0) {
				return deltaPathTitle;
			} else {
				return "";
			}
		}
		if (rowIndex==4) {
			if (columnIndex==0) {
				return DeltaPath.DELTAPATH.title;
			} else {
				return DeltaPath.DELTAPATH.value;
			}
		}
		return null;
	}
	
	/**
	 * Return the value of the cell at the bottom of the table.
	 * <P>
	 * <B>Note</B>: <EM>the rowIndex here is 0 based<EM>
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	public Object getTopValue(int rowIndex, int columnIndex) {
		// Temps title
		if (rowIndex==0) {
			if (columnIndex==0) {
				return tempsTitle;
			} else {
				return "";
			}
		}
		// Temperatures
		if (rowIndex-1>=0 && rowIndex-1<temps.size()) {
			if (columnIndex==0) {
				return temps.titles[rowIndex-1];
			} else {
				return temps.values[rowIndex-1];
			}
		}
		System.out.println("Row out of range "+rowIndex);
		return "?";
	}
	
	/**
	 * Refresh the values shown in the antenna
	 *
	 */
	protected ValueState refresh() {
		ValueState ret=ValueState.NORMAL;
		ret=ValueState.max(ret, Deltas.refresh(metrology.getDeltas()));
		ret=ValueState.max(ret, DeltaPath.refresh(metrology.getDeltaPath()));
		ret=ValueState.max(ret, temps.refresh(metrology.getTemps()));
		return ret;
	}
	
	/**
	 * @see AbstractTableModel
	 */
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex==0) {
			return "<HTML><B>Monitor points";
		} else {
			return "<HTML><B>Status";
		}
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
