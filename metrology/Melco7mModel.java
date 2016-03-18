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
 * The metrology model for the MELCO with vendor specific status bits
 * 
 * @author acaproni
 * @since ALMA 7.1.1
 */
public class Melco7mModel extends CommonMetrologyModel {
	
	enum EquipStatus {
		DATA_LOG_1("Data logger 1 diconnected",0,0),
		DATA_LOG_2("Data logger 2 diconnected",0,1),
		DATA_LOG_3("Data logger 3 diconnected",0,2),
		DATA_LOG_4("Data logger 4 diconnected",0,3),
		BOX_1("Metrology box 1 disconnected ",0,4),
		BOX_2("Metrology box 2-1 disconnected ",0,5),
		BOX_3("Metrology box 2-2 disconnected ",0,6),
		BOX_4("Metrology box 3 disconnected ",0,7),
		
		GAP_SENSOR1("Gap sensor 1 alarm",1,0),
		GAP_SENSOR2("Gap sensor 1 alarm",1,1),
		GAP_SENSOR3("Gap sensor 1 alarm",1,2),
		LINEAR_SCALE6("Linear scale 6 alarm",1,5),
		LINEAR_SCALE7("Linear scale 7 alarm",1,6),
		LINEAR_SCALE8("Linear scale 8 alarm",1,7),
		
		LINEAR_SCALE9("Linear scale 9 alarm",2,0),
		LINEAR_SCALE10("Linear scale 10 alarm",2,1),
		LINEAR_SCALE11("Linear scale 11 alarm",2,2),
		LINEAR_SCALE12("Linear scale 12 alarm",2,3),
		LINEAR_SCALE13("Linear scale 13 alarm",2,4),
		
		TILT1_DISC("Tiltmeter 1 disconnected",3,1),
		TILT2_DISC("Tiltmeter 2 disconnected",3,2),
		TILT3_DISC("Tiltmeter 3 disconnected",3,3);
		
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
	public Melco7mModel(AntennaRootPane antennaRootPane, IMetrology metrology) {
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
