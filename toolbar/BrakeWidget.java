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
package alma.control.gui.antennamount.toolbar;

import javax.swing.SwingUtilities;

import alma.Control.MountPackage.BrakesStatus;
import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.ValueHolder;

/**
 * The widget showing the value of AZ/EL brakes
 * @author acaproni
 *
 */
public class BrakeWidget extends ValueWidget<BrakesStatus> {

	/**
	 * Constructor
	 * 
	 * @param value The ValueHolder<BrakeStatus>
	 * @param description The description
	 * @param hasIcon true if the widget shows an icon
	 * @param rootP The AntennaRootPane
	 */
	public BrakeWidget(ValueHolder<BrakesStatus> value, String description, boolean hasIcon, AntennaRootPane rootP) {
		super(value,description,hasIcon, rootP);
	}
	
	/**
	 * Refresh the message of the widget depending on the content of the value
	 * 
	 * @see ToolbarWidget
	 */
	public void refresh() {
		final ValueHolder<BrakesStatus> temp;
		synchronized (this) { 
			temp= value;
		}
		if (temp==null || temp.getValue()==null) {
			super.refresh();
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (temp.getValue()==BrakesStatus.BRAKE_ENGAGED) {
					label.setText("<HTML>"+description+"<FONT color=\"red\"><B>ON");
				} else {
					label.setText(description+"OFF");
				}
				label.setToolTipText(label.getText());
			}
		});
	}
	
	/**
	 * Refresh the icon depending on the state of the brake
	 * 
	 * @see ToolbarWidget
	 */
	public void refreshIcon() {
		final ValueHolder<BrakesStatus> temp;
		synchronized (this) { 
			temp= value;
		}
		if (temp==null || temp.getValue()==null) {
			super.refreshIcon();
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (temp.getValue()==BrakesStatus.BRAKE_ENGAGED) {
					label.setIcon(WidgetIcon.WARNING.icon);
				} else {
					label.setIcon(WidgetIcon.NORMAL.icon);
				}
			}
		});
	}
}
