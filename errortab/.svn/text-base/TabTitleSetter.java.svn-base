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
package alma.control.gui.antennamount.errortab;

import java.awt.Component;

import alma.control.gui.antennamount.utils.ValueState;

/**
 * The interface setting the title of a tab depending of its state:
 * - NORMAL: the title is black with no icon
 * - WARING: the title is orange with a warning icon
 * - ERROR:  the title is red with an error icon
 * 
 * @author acaproni
 *
 */
public interface TabTitleSetter {
	/**
	 * Set the title of a tab
	 * 
	 * @param state The state of the title (can't be <code>null</code>)
	 * @param title The title of the tab (can be <code>null</code> or empty)
	 * @param component The component shown in the tab (can't be <code>null</code>)
	 * @param flash If <code>true</code> the title must flash
	 */
	public void tabTitleState(ValueState state, String title, Component component, boolean flash);
}
