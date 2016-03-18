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

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import alma.control.gui.antennamount.AntennaRootPane;
import alma.control.gui.antennamount.mount.Mount;

/**
 * The widget showing the state of the axes read from the Mount component.
 * 
 * The state is retrieved by the mount using the following methods:
 *   - inShutdown,
 *   - inStandby,
 *   - isMoveable
 * 
 * The state of each axis is shown in the Control tab together with the buttons to
 * change their state.
 * 
 * @author acaproni
 *
 */
public class AxesWidget extends ToolbarWidget {
	
	// The mount to read the state of its axis
	private Mount mount;
	
	/** 
	 * Constructor
	 * 
	 * @param description The description
	 * @param hasIcon true if an icon appears in the widget
	 * @param rootP The AntennaRootPane
	 */
	public AxesWidget(String description, boolean hasIcon, AntennaRootPane rootP) {
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
		if (mount==null) {
			setLabelAndIcon(description+"-", WidgetIcon.UNKNOWN.icon);
			return;
		}
		if (mount.inShutdown()==Boolean.TRUE) {
			// Shutdown
			setLabelAndIcon(description+"Shutdown", WidgetIcon.WARNING.icon);
		} else if (mount.inStandby()==Boolean.TRUE) {
			// Standby
			setLabelAndIcon(description+"Standby", WidgetIcon.NORMAL.icon);
		} else if (mount.isMoveable()==Boolean.TRUE) {
			// Encoder/Autonomous
			setLabelAndIcon(description+"Auto/Enc", WidgetIcon.OK.icon);
		} else {
			// Unknown state
			setLabelAndIcon(description+"Unknown", WidgetIcon.ERROR.icon);
		}
	}
	
	/**
	 * Set the icon and the text of the label as well as the tooltip.
	 * 
	 * @param lbl The string to set in the label
	 * @param icon The icon of the label
	 */
	private void setLabelAndIcon(final String lbl, final ImageIcon icon) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				label.setText(lbl);
				label.setIcon(icon);
				label.setToolTipText(lbl);
			}
		});
	}

	/**
	 * This method is a stub in fact the icon is refreshed in refresh()
	 * to avoid calling the same CORBA methods too often
	 * 
	 * @see ToolbarWidget
	 */
	public void refreshIcon() {
		if (!hasIcon) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (mount==null) {
					label.setIcon(WidgetIcon.UNKNOWN.icon);
				}
			}
		});
	}
	
	/**
	 * Set the mount used to read the state of the axis
	 * 
	 * @param mnt
	 */
	public void setMount(Mount mnt) {
		mount=mnt;
	}

}
