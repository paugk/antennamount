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

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.ValueHolder;

/**
 * The widget for boolean values like the in position or the ABM PM
 * @author acaproni
 *
 */
public class BooleanWidget extends ValueWidget<Boolean> {
	
	/**
	 * Constructor
	 * 
	 * @param value The ValueHolder<BrakeStatus>
	 * @param description The description
	 * @param hasIcon true if the widget shows an icon
	 * @param rootP The AntennaRootPane
	 */
	public BooleanWidget(ValueHolder<Boolean> value, String description, boolean hasIcon, AntennaRootPane rootP) {
		super(value, description, hasIcon, rootP);
	}

	/**
	 * Refresh the icon depending on the content of the value
	 * 
	 * @see ToolbarWidget
	 */
	public void refreshIcon() {
		final ValueHolder<Boolean> temp;
		synchronized (this) { 
			temp= value;
		}
		if (temp==null || temp.getValue()==null) {
			super.refreshIcon();
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (temp.getValue()==Boolean.TRUE) {
					label.setIcon(WidgetIcon.OK.icon);
				} else {
					label.setIcon(WidgetIcon.ERROR.icon);
				}
			}
		});
	}
	
	/**
	 * Refresh the message of the widget depending on the content of the value
	 * 
	 * @see ToolbarWidget
	 */
	public void refresh() {
		final ValueHolder<Boolean> temp;
		synchronized (this) { 
			temp= value;
		}
		if (temp==null || temp.getValue()==null) {
			super.refresh();
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (temp.getValue()==Boolean.TRUE) {
					label.setText(description+"Yes");
				} else {
					label.setText("<HTML>"+description+"<FONT color=\"red\"><B>NO");
				}
				label.setToolTipText(label.getText());
			}
		});
	}
}
