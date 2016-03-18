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
 * @version $Id: CommonCoordsTable.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.coordtables;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.Mount;
import alma.control.gui.antennamount.mount.MountController;
import alma.control.gui.antennamount.utils.GUIConstants;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class CommonCoordsTable extends JTable {
	/**
	 * 
	 * The renderer for the cells of the table
	 *
	 */
	public class CommonCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(
				JTable table,
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row,
				int column) {
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);

			if (row == CommonCoordsTableModel.CoordsRowPos.HEADER.ordinal() || 
					row == CommonCoordsTableModel.CoordsRowPos.OFFSET_HEADER.ordinal() ||
					column==CommonCoordsTableModel.CoordsColPos.HEADER.ordinal()) {
				c.setBackground(GUIConstants.tableHdrBgColor);
				c.setForeground(GUIConstants.tableForegroundColor);
			} else {
				c.setBackground(GUIConstants.tableBackgroundColor);
				c.setForeground(GUIConstants.tableForegroundColor);
			}

			return c;
		}
	}
	
	
	// The table model
	private CommonCoordsTableModel model=null;
	
	public CommonCoordsTable(AntennaRootPane pane) {
		super();
		model = new CommonCoordsTableModel(pane);
		setModel(model);
		setDefaultRenderer(Object.class, new CommonCellRenderer());
		
		//	Get the bounds of the pixel needed to print a string
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		Graphics context = getGraphics();
		Rectangle2D strWidth=fm.getStringBounds("XXXX:XX:XX:XXX",context);
		
		// Set the width of the cols
		TableColumnModel colModel = getColumnModel();
		Enumeration<TableColumn> cols = colModel.getColumns();
		while (cols.hasMoreElements()) {
			TableColumn col=cols.nextElement();
			col.setPreferredWidth((int)strWidth.getWidth());
			col.setMinWidth((int)strWidth.getWidth());
			col.setMaxWidth((int)strWidth.getWidth());
			col.setResizable(false);
		}
		
		setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
        setCellSelectionEnabled(false);
        setOpaque(false);
        
	}
	
	/**
	 * Release the rosource
	 *
	 */
	public void close() {
		((CommonCoordsTableModel)getModel()).close();
	}
	
	/**
	 * Set the mount and the controller
	 * 
	 * @param ctrl The MountController
	 * @param mnt The Mount
	 */
	public void setComponents(MountController ctrl,Mount mnt) {
		((CommonCoordsTableModel)getModel()).setComponents(ctrl, mnt);
	}
	
	/**
	 * Enable disable the table
	 * 
	 * @param b If true enable the table
	 */
	public void enableWidgets(boolean b) {
		super.setEnabled(b);
	}
}
