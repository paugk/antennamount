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
import alma.control.gui.antennamount.mount.ShutterCommon;
import alma.control.gui.antennamount.utils.ValueDisplayer;

/**
 * The toolbar widget showing the state of the shutter
 * @author acaproni
 *
 */
public class ShutterWidget extends ToolbarWidget {
	
	// The shutter from the mount
	private ShutterCommon shutter=null;
	
	/** 
	 * Constructor
	 * 
	 * @param description The description
	 * @param hasIcon true if an icon appears in the widget
	 * @param rootP The AntennaRootPane
	 */
	public ShutterWidget(String description, boolean hasIcon, AntennaRootPane rootP) {
		super(description,hasIcon, rootP);
		refresh();
		refreshIcon();
	}

	/**
	 * Update the state (text and icon) of the widget depending of the value
	 * read from the component
	 * 
	 * @see ToolbarWidget
	 */
	public void refresh() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (shutter==null) {
					label.setText(description+ValueDisplayer.NOT_AVAILABLE);
				} else {
					if (shutter.isOpen()) {
						label.setText(description+"open");
					} else if (shutter.isClosed()) {
						label.setText(description+"closed");
					} else {
						label.setText(description+"-");
					}
					label.setToolTipText(label.getText());
				}
			}
		});
	}

	/**
	 * Refresh the icon in the label (if any)
	 * 
	 * @see ToolbarWidget
	 */
	public void refreshIcon() {
		if (!hasIcon) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (shutter==null) {
					label.setIcon(WidgetIcon.UNKNOWN.icon);
				} else {
					if (shutter.isOpen()) {
						label.setIcon(WidgetIcon.OK.icon);
					} else if (shutter.isClosed()) {
						label.setIcon(WidgetIcon.NORMAL.icon);
					} else {
						label.setIcon(WidgetIcon.WARNING.icon);
					}
				}
			}
		});
	}
	
	/**
	 * Set the shutter whose state is shown in the widget
	 * 
	 * @param shutter The shutter 9can be null)
	 */
	public void setShutter(ShutterCommon shutter) {
		this.shutter=shutter;
	}

}
