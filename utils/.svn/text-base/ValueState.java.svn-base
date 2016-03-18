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
package alma.control.gui.antennamount.utils;

import javax.swing.ImageIcon;

/**
 * The state of a value.
 * <P>
 * Each value is composed of a HTML string plus a icon: this enumerated groups common
 * attributes of the titles.
 * <P>
 * The state is also used to report errors in the tabbed pane.
 * 
 * @author acaproni
 *
 */
public enum ValueState {
	NORMAL(null,ValueDisplayer.normalColor),
	WARNING("warning.gif",ValueDisplayer.warningColor),
	ERROR("error.gif",ValueDisplayer.errorColor);
	
	/**
	 * The icon for each title
	 */
	public final ImageIcon icon;
	
	/**
	 * The HTML header to build the title
	 */
	public final String htmlHeader;
	
	/**
	 * The HTML closing string to build the title
	 */
	public static final String htmlCloser=ValueDisplayer.closeHTML;
	
	/**
	 * Constructor
	 * 
	 * @param iconUrl The location of the icon
	 * @param header The HTML header to build the title string
	 */
	private ValueState(String iconUrl, String header) {
		if (iconUrl==null) {
			icon=null;
		} else {
			icon = new ImageIcon(this.getClass().getResource(GUIConstants.resourceFolder+iconUrl));
		}
		htmlHeader=header;
	}
	
	/**
	 * 
	 * @param a The first state
	 * @param b The second state
	 * @return The max between <code>a</code> and <code>b</code> 
	 * 			or <code>null</code> if both <code>a</code> and <code>b</code>
	 * 			are <code>null</code>
	 */
	public static ValueState max(ValueState a, ValueState b) {
		if (a==null) {
			return b;
		}
		if (b==null) {
			return a;
		}
		return ValueState.values()[Math.max(a.ordinal(), b.ordinal())];
	}
	
	/**
	 * Color the given string depending on the state
	 * 
	 * @param str The string to format
	 * @return The colored version of <code>str</code>
	 */
	public String format(String str) {
		return htmlHeader+str+ValueState.htmlCloser;
	}
}
