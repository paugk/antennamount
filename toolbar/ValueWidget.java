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
import alma.control.gui.antennamount.utils.ValueDisplayer;

/**
 * An widget displayed in the toolbar having a ValueHolder to show.
 * <P>
 * It maps a ValueHolder object into a decorated items to show in the toolbar.
 * The item is composed of a text and optionally an icon.
 * The text explains what the indicator shows and its state.
 * The icon is optional and add informations about the state of the value.
 * <P>
 * The layout is: <I>[icon] Text: status</I><BR>
 * where
 * <ul>
 * 	<LI>Text explains the object displayed (for example "ABM Pointing model")
 *  <LI>status explains the status (in previous example might be "Applied")
 * </UL>
 * 
 * @author acaproni
 *
 */
public class ValueWidget<T> extends ToolbarWidget {
	
	// The value mapped in the widget
	protected ValueHolder<T> value;
	
	/**
	 * Constructor.
	 * 
	 * @param value The value (read from the component) to show in the widget 
	 * @param description The description of the info shown by the widget
	 * @param hasIcon true if an icon must be shown on the left side of the widget
	 * @param rootP The AntennaRootPane
	 */
	public ValueWidget(ValueHolder<T> value, String description, boolean hasIcon, AntennaRootPane rootP) {
		super(description,hasIcon, rootP);
		this.value=value;
		refresh();
		refreshIcon();
	}
	
	/**
	 * Set the value used to display data in the widget
	 * 
	 * @param value
	 */
	public void setValue(ValueHolder<T> value) {
		synchronized (this) {
			this.value=value;
		}
	}
	
	/**
	 * Update the state (text and icon) of the widget depending of the value
	 * read from the component
	 * 
	 * @see ToolbarWidget
	 */
	public void refresh() {
		final ValueHolder<T> temp;
		synchronized (this) { 
			temp= value;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (temp==null || temp.getValue()==null) {
					label.setText(description+ValueDisplayer.NOT_AVAILABLE);
				} else {
					label.setText(description+temp.getValue());
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
		final ValueHolder<T> temp;
		synchronized (this) { 
			temp= value;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (temp==null || temp.getValue()==null) {
					label.setIcon(WidgetIcon.UNKNOWN.icon);
				}
			}
		});
	}
}
