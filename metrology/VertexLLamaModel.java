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
package alma.control.gui.antennamount.metrology;

import javax.swing.table.TableModel;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.IMetrology;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.ValueState;
import alma.control.gui.antennamount.utils.bit.LongArrayBit;


/**
 * The metrology model for the Vertex with vendor specific status bits
 * 
 * @author acaproni
 * @since ALMA 7.1.1
 */
public class VertexLLamaModel extends CommonMetrologyModel {
	
	enum EquipStatus {
		LOCAL_BUS_ERROR("Local bus error",0,0),
		TILT1_RDOUT("Titlmeter 1 readout error",0,1),
		TILT2_RDOUT("Titlmeter 2 readout error",0,2),
		TILT3_RDOUT("Titlmeter 3 readout error",0,3),
		TEMP_RDOUT("Temperature sensor readout",0,6),
		DISPL_RDOUT("Displacement sensor readout",0,7),
		COMM_ERROR_ACU_MMC("Communication error ACU-MMC",3,6),
		DATA_INVALID("Metrology data is not valid",3,7);
		
		/**
		 * The title of the entry
		 */
		private final String title;
		
		/**
		 * The byte of this entry
		 */
		private final int byteNum;
		
		/**
		 * The byte of this entry
		 */
		private final int bitNum;
		
		/**
		 * The actual value of the entry
		 */
		private final StringBuilder value = new StringBuilder(ValueDisplayer.NOT_AVAILABLE);
		
		/**
		 * Constructor
		 * 
		 * @param title The title shown in the table
		 * @param byteNum The byte of this entry
		 * @param bitNum The bit of this entry
		 */
		private EquipStatus(String title, int byteNum, int bitNum) {
			this.title=title;
			this.byteNum=byteNum;
			this.bitNum=bitNum;
		}
		
		/**
		 * Refresh the values of the bits
		 * 
		 * @param bits 
		 * @param state The ValueState
		 */
		public static ValueState refresh(ValueHolder<LongArrayBit> bits) {
			if (bits==null || bits.getValue()==null) {
				for (EquipStatus es: EquipStatus.values()) {
					es.value.delete(0, es.value.length());
					es.value.append(ValueDisplayer.NOT_AVAILABLE);
				}
				return ValueState.NORMAL;
			}
			LongArrayBit lab = bits.getValue();
			ValueState ret= ValueState.NORMAL;
			for (EquipStatus es: EquipStatus.values()) {
				ret=ValueDisplayer.formatBit(lab.getBit(es.byteNum, es.bitNum), ret, es.value, bits);
			}
			return ret;
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param antennaRootPane The antenna root pane
	 * @param metrology The metrology
	 */
	public VertexLLamaModel(AntennaRootPane antennaRootPane, IMetrology metrology) {
		super(antennaRootPane,metrology);
	}

	@Override
	public int getRowCount() {
		return super.getRowCount()+getVertexModelRowCount();
	}
	
	/**
	 * Return the number of rows for the vertex.
	 * This numebr has to be added to the line in the common part to get
	 * the number of the rows of the table.
	 * 
	 * @return the number of rows for the vertex specific
	 */
	private int getVertexModelRowCount() {
		return 1+EquipStatus.values().length;
	}
	
	/**
	 * @see TableModel
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex<=lastCommonRow) {
			return super.getBottomValue(rowIndex, columnIndex);
		}
		if (rowIndex>=getVertexModelRowCount()+lastCommonRow) {
			return super.getTopValue(rowIndex-getVertexModelRowCount()-lastCommonRow, columnIndex);
		}
		
		// index is zero based in this class
		int index= rowIndex-lastCommonRow-1;
		if (index==0) {
			if (columnIndex==0) {
				return equipStatusTitle;
			} else {
				return null;
			}
		}
		if (index>0 && index<=EquipStatus.values().length) {
			if (columnIndex==0) {
				return EquipStatus.values()[index-1].title;
			} else {
				return EquipStatus.values()[index-1].value.toString();
			}
		}
		return null;
	}

	@Override
	protected ValueState refresh() {
		if (metrology==null) {
			EquipStatus.refresh(null);
			fireTableDataChanged();
			return ValueState.NORMAL;
		}
		ValueState ret = ValueState.NORMAL;
		ret=ValueState.max(ret, EquipStatus.refresh(metrology.getEquipStatus()));
		ret=ValueState.max(ret, super.refresh());
		fireTableDataChanged();
		return ret;
	}

}
