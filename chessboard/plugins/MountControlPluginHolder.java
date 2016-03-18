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
package alma.control.gui.antennamount.chessboard.plugins;

import java.awt.BorderLayout;

import alma.common.gui.chessboard.ChessboardDetailsPlugin;
import alma.common.gui.chessboard.ChessboardDetailsPluginListener;
import alma.common.gui.components.selector.SelectorComponentEvent;
import alma.exec.extension.subsystemplugin.PluginContainerServices;
import alma.exec.extension.subsystemplugin.SubsystemPlugin;

import alma.control.gui.antennamount.mountpanel.MountRootPanel;
import alma.control.gui.dartboard.DartboardPlugin;

public class MountControlPluginHolder extends PluginHolder {

	private final static String MOUNT_CONTROL_PLUGIN_NAME = "Mount Control";

	public MountControlPluginHolder(
			String antennaNames[], 
			ChessboardDetailsPluginListener owningChessboard,
			PluginContainerServices services, 
			String pluginTitle, 
			String selectionTitle) {
		super(antennaNames,owningChessboard,services,pluginTitle,selectionTitle);
		plugin = new MountRootPanel();
		plugin.setServices(svcs);
		antennas=antennaNames;
		setLayout(new BorderLayout());
		add((MountRootPanel)plugin,BorderLayout.CENTER);
	}


	public void specializedStart() throws Exception {
		plugin.start();
		SelectorComponentEvent sce = new SelectorComponentEvent(this,"CONTROL/"+antennas[0]);
		((MountRootPanel)plugin).connectPerformed(sce);
	}

	public String getPluginName() {
		return MOUNT_CONTROL_PLUGIN_NAME;
	}

}
