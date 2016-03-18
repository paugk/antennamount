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
 * @author  caproni   
 * @version $Id: AxisStatusPanel.java 199134 2013-12-19 00:07:05Z rmarson $
 * @since    
 */

package alma.control.gui.antennamount.axis;

import alma.Control.MountPackage.AxisMode;
import alma.control.gui.antennamount.mount.AxisStatusDefinition;
import alma.control.gui.antennamount.mount.ValueHolder;
import alma.control.gui.antennamount.utils.ValueDisplayer;
import alma.control.gui.antennamount.utils.ValueState;
import alma.control.gui.antennamount.utils.ValueDisplayer.DisplayStruct;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 * The panel that shows the status of the axis.
 * 
 *
 */
public class AxisStatusPanel extends JPanel {
	
	/**
	 * The label showing the actual state
	 */
	private JLabel statusLabel=new JLabel(ValueDisplayer.NOT_AVAILABLE);
	
	/**
	 * The label showing the icon
	 */
	private JLabel iconLbl = new JLabel();
	
	/**
	 * The last set status to avoid refreshing
	 */
	private AxisStatusDefinition oldState = null; // Force the first refresh
	
	/**
	 * Constructor
	 * 
	 * @see AxisStatus
	 *
	 */
	public AxisStatusPanel() {
		super();
		initialize();
	}
	
	/** 
	 * Init the GUI
	 *
	 */
	private void initialize() {
		setBorder(new TitledBorder("Actual state"));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		iconLbl.setIcon(AxisStatusDefinition.UNKNOWN.icon);
		add(iconLbl);
		add(statusLabel);
		// Set the size of the label big enough to contain the
		// longest string
		refreshPanel(null);
		Dimension c = statusLabel.getPreferredSize();
		FontMetrics fm = statusLabel.getFontMetrics(statusLabel.getFont());
		Rectangle2D sz = fm.getStringBounds(AxisStatusDefinition.AUTONOMOUS.description,statusLabel.getGraphics());
		Rectangle r = sz.getBounds();
		c.width=r.width+20;
		statusLabel.setPreferredSize(c);
		statusLabel.setMinimumSize(c);
	}
	
	/**
	 * Refresh the content of the panel with the current state
	 *  
	 * @param state The axis state
	 * 
	 */
	public void refreshPanel(final ValueHolder<AxisMode> axisMode) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (axisMode==null) {
					// No component connected
					statusLabel.setText(AxisStatusDefinition.UNKNOWN.description);
					statusLabel.setToolTipText(AxisStatusDefinition.UNKNOWN.tooltip);
					iconLbl.setIcon(AxisStatusDefinition.UNKNOWN.icon);
					iconLbl.setToolTipText(AxisStatusDefinition.UNKNOWN.tooltip);
					return;
				}
				if (axisMode.getValue()==null) {
					// Something not working in the component
					statusLabel.setText(ValueDisplayer.errorColor+AxisStatusDefinition.ERROR.description);
					statusLabel.setToolTipText(AxisStatusDefinition.ERROR.tooltip);
					iconLbl.setIcon(AxisStatusDefinition.ERROR.icon);
					iconLbl.setToolTipText(AxisStatusDefinition.ERROR.tooltip);
					return;
				}
				AxisStatusDefinition statusDef = AxisStatusDefinition.fromAxisMode(axisMode.getValue()); 
				DisplayStruct dStruct = ValueDisplayer.getString(statusDef.description,axisMode);
				statusLabel.setText(dStruct.state.htmlHeader+dStruct.str+ValueState.htmlCloser);
				statusLabel.setToolTipText(statusDef.tooltip);
				iconLbl.setIcon(statusDef.icon);
				iconLbl.setToolTipText(statusDef.tooltip);
				oldState=statusDef;		
			}
		});
		
	}
	
	/**
	 * Enable/disable the widgets in the panel
	 * 
	 * @param enable If true enable the widgets
	 */
	public void enableWidgets(final boolean enable) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				iconLbl.setEnabled(enable);
				statusLabel.setEnabled(enable);
			}
		});
	}
	
}
