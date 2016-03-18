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

import java.awt.Color;

/**
 * A collection of constants to reuse all around the GUI.
 *
 * Constants defined here should be used whenever possible to avoid duplication 
 * and to be able to change look and feel modifying only the constants in this class.
 * 
 * @author acaproni
 *
 */
public class GUIConstants {
	
	/**
	 * The color to show non editable cells of tables
	 */
	public final static Color tableBackgroundColor = new Color(224,224,224);
	
	/**
	 * Color for the background of the headers of the tables
	 */
	public final static Color tableHdrBgColor = Color.LIGHT_GRAY;
	
	/**
	 * Color for the foreground of the headers of the tables
	 */
	public final static Color tableForegroundColor = Color.BLACK;
	
	/**
	 * The folder containing the resources like images
	 */
	public static final String resourceFolder = "/alma/control/gui/antennamount/resources/";
}
