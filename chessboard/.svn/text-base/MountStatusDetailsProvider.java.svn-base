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
package alma.control.gui.antennamount.chessboard;

import alma.acs.logging.AcsLogLevel;
import alma.common.gui.chessboard.ChessboardDetailsPluginFactory;
import alma.common.gui.chessboard.ChessboardDetailsPlugin;
import alma.common.gui.chessboard.ChessboardDetailsPluginListener;
import alma.control.gui.antennamount.chessboard.plugins.MountStatusPluginHolder;
import alma.exec.extension.subsystemplugin.PluginContainerServices;

public class MountStatusDetailsProvider implements ChessboardDetailsPluginFactory {

    private static final int INSTANCES = 1;

    public ChessboardDetailsPlugin[] instantiateDetailsPlugin(
            String[] names,
	    PluginContainerServices containerServices,
	    ChessboardDetailsPluginListener listener, String pluginTitle,
	    String selectionTitle) {

        ChessboardDetailsPlugin[] plugin = new ChessboardDetailsPlugin[INSTANCES];
        plugin[0] = new MountStatusPluginHolder (
	        names,
		listener,
		containerServices,
		pluginTitle,
		selectionTitle);
	return plugin;
    }
}
