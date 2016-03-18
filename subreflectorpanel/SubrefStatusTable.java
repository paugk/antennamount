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

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.utils.GUIConstants;
import alma.control.gui.antennamount.utils.ValueState;

/**
 * The table with the status of the shutter
 * 
 * @author acaproni
 *
 */
public class SubrefStatusTable extends JTable {
	
	/**
	 *  The table model.
	 *  <P>
	 *  The model depends on the type of the connected mount and set in setMount() 
	 */
	private CommonStatusModel model;
	
	/**
	 * Constructor
	 *
	 */
	public SubrefStatusTable() {
		super();

		setModel(new CommonStatusModel());
		
		setBackground(GUIConstants.tableBackgroundColor);
		setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
        setCellSelectionEnabled(false);
        setOpaque(false);
	}
	
	public void setMount(Mount mnt) {
		if (mnt==null) {
			setModel(new CommonStatusModel());
			return;
		}
		switch (mnt.getMountType()) {
		case VERTEX: {
			model = new VertexSubrefModel(mnt);
			break;
		}
		case VERTEX_LLAMA: {
			model = new VertexLLamaSubrefModel(mnt);
			break;
		}
		case ALCATEL: {
                        model = new AEMSubrefModel(mnt);
                        break;
                }
		case MELCO: { 
                        model = new ACASubrefModel(mnt);
                        break;
                }    
		case MELCOA7M: {
                        /*20090826MT Subref status of Melco 7m antenna is same as that of Melco12m.*/
                        model = new ACASubrefModel(mnt);
                        break;
                }    
		default: {
			throw new IllegalArgumentException("Unsopported antenna type "+mnt.getMountType());
		}
		}
		setModel(model);
	}
	
	/**
	 * Refresh the rows of the table
	 */
	public ValueState refresh() {
		ValueState a=model.refreshLimits();
		ValueState b=model.refreshState();
		model.fireTableDataChanged();
		return ValueState.max(a, b);
	}
}
