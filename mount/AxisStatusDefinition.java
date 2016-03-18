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
package alma.control.gui.antennamount.mount;

import javax.swing.ImageIcon;

import alma.Control.MountPackage.AxisMode;

/**
 * The definitions of the possible states of one or two axis.
 * 
 * The definition is composed of constants representing the ste itself together with:
 * - a description of the state
 * - an tootip
 * - an icon
 * 
 * @author acaproni
 *
 */
public enum AxisStatusDefinition {
	SHUTDOWN("Shutdown","/alma/control/gui/antennamount/resources/orange.png"), // Both axis in shutdown
	STANDBY("Standby","/alma/control/gui/antennamount/resources/blue.png"), // Both axis in standby 
	ENCODER("Encoder","/alma/control/gui/antennamount/resources/green.png"), // Both axis in encoder mode
	AUTONOMOUS("Autonomous","/alma/control/gui/antennamount/resources/green.png"), // Both axis in track mode
	SURVIVAL_STOW("Survival stow","/alma/control/gui/antennamount/resources/yellow.png"), // Both axis in survival mode
	MAINTENANCE_STOW("Maintenance stow","Maintenance","/alma/control/gui/antennamount/resources/yellow.png"), // Both axis in maintenance mode
	VELOCITY("Velocity","/alma/control/gui/antennamount/resources/green.png"),  // Both axis in velocity mode
	ERROR("Error","Error getting the state","/alma/control/gui/antennamount/resources/red.png"), // Error received getting the state
	UNKNOWN("Unknown","Unknown state","/alma/control/gui/antennamount/resources/grey.png");
	
	// The description of the state
	public final String description;
	
	// A tooltip fo the state
	public final String tooltip;
	
	// The icon to show in the GUI for the state
	public final ImageIcon icon;
	
	/**
	 * The constructor when description and tooltip are the same string
	 * 
	 * @param desc The description and tooltip of the state
	 * @param iconResource The URI of the icon for this state
	 */
	private AxisStatusDefinition(String desc,String iconResource) {
		description=tooltip=desc;
		icon = new ImageIcon(this.getClass().getResource(iconResource));
	}
	
	/**
	 * The constructor with description and tootip 
	 * 
	 * @param desc The description and tooltip of the state
	 * @param tip The tooltip for this state
	 * @param iconResource The URI of the icon for this state
	 */
	private AxisStatusDefinition(String desc, String tip,String iconResource) {
		description=desc;
		tooltip=tip;
		icon = new ImageIcon(this.getClass().getResource(iconResource));
	}
	
	/**
	 * Translate an AxisMode (i.e. the state of an axis returned by the mount
	 * in an AxisStatusDefinition (i.e. the view of the state of the axis inside 
	 * the GUI).
	 * 
	 * @param actualAxisState The state of an axis returned by the Mount
	 * @return The AxisStatusDefinition for the state read from the mount
	 */
	public static AxisStatusDefinition fromAxisMode(AxisMode actualAxisState) {
		if (actualAxisState==null) {
			return UNKNOWN;
		}
		if (actualAxisState==AxisMode.AUTONOMOUS_MODE) {
			return AUTONOMOUS;
		} if (actualAxisState==AxisMode.ENCODER_MODE) {
			return ENCODER;
		} if (actualAxisState==AxisMode.MAINTENANCE_STOW_MODE) {
			return MAINTENANCE_STOW;
		} if (actualAxisState==AxisMode.SHUTDOWN_MODE) {
			return SHUTDOWN;
		} if (actualAxisState==AxisMode.STANDBY_MODE) {
			return STANDBY;
		} if (actualAxisState==AxisMode.SURVIVAL_STOW_MODE) {
			return SURVIVAL_STOW;
		} if (actualAxisState==AxisMode.VELOCITY_MODE) {
			return VELOCITY;
		} else {
			return UNKNOWN;
		}
	}
}
