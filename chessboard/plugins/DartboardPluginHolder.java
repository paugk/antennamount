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
import java.util.logging.Logger;

import alma.common.gui.chessboard.ChessboardDetailsPluginListener;
import alma.common.gui.components.selector.SelectorComponentEvent;
import alma.control.gui.dartboard.DartboardPlugin;
import alma.exec.extension.subsystemplugin.PluginContainerServices;

public class DartboardPluginHolder extends PluginHolder {
	
	private final static String DARTBOARD_PLUGIN_NAME = "Dartboard";
	private Logger logger;
	
	public DartboardPluginHolder(
			String antennaNames[], 
			ChessboardDetailsPluginListener owningChessboard,
			PluginContainerServices services, 
			String pluginTitle, 
			String selectionTitle) {
		super(antennaNames,owningChessboard,services,pluginTitle,selectionTitle);
		logger=services.getLogger();
		plugin = new DartboardPlugin();
		((DartboardPlugin)plugin).setAsOMCPlugin();
		plugin.setServices(svcs);
		antennas=antennaNames;
		setLayout(new BorderLayout());
		add((DartboardPlugin)plugin,BorderLayout.CENTER);
	}

	public void specializedStart() throws Exception {
		plugin.start();
		SelectorComponentEvent sce = new SelectorComponentEvent(this,"CONTROL/"+antennas[0]);
		((DartboardPlugin)plugin).connectPerformed(sce);
	}

	public String getPluginName() {
		return DARTBOARD_PLUGIN_NAME;
	}
}
