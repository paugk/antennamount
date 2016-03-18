package alma.control.gui.antennamount.chessboard.plugins;

import alma.acs.logging.AcsLogLevel;
import alma.common.gui.chessboard.ChessboardDetailsPlugin;
import alma.common.gui.chessboard.ChessboardDetailsPluginListener;
import alma.exec.extension.subsystemplugin.PluginContainerServices;
import alma.exec.extension.subsystemplugin.SubsystemPlugin;

public abstract class PluginHolder extends ChessboardDetailsPlugin {
	
	// Container services
	protected PluginContainerServices svcs;
	
	// The antennas to connect to
	// The array contains only on antenna name
	protected String[] antennas;
	
	// The plugin
	protected SubsystemPlugin plugin;
	
	public PluginHolder(
			String antennaNames[], 
			ChessboardDetailsPluginListener owningChessboard,
			PluginContainerServices services, 
			String pluginTitle, 
			String selectionTitle) {
		super(antennaNames,owningChessboard,services,pluginTitle,selectionTitle);
		svcs=services;
		antennas=antennaNames;
	}

	@Override
	public void replaceContents(String[] newAntennaNames) {
	}

	@Override
	public void specializedStop() {
		try {
			plugin.stop();
		} catch (Throwable t) {}
	}

	public boolean runRestricted(boolean restricted) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
